#!/usr/bin/env bash
set -uo pipefail

: "${RUNNER_TEMP:?RUNNER_TEMP is required}"
: "${GITHUB_WORKSPACE:?GITHUB_WORKSPACE is required}"
REPORT_DIR="${RUNNER_TEMP}/mangon-rea-audit"
CDP_ENDPOINT="http://127.0.0.1:9222"
CHROME="${REA_BROWSER_EXECUTABLE:-/usr/bin/google-chrome}"
REA_BIN="$(command -v rea || true)"

mkdir -p "${REPORT_DIR}/pages"
manifest="${REPORT_DIR}/manifest.tsv"
printf 'target\turl\tpage_capture\tbundle_analysis\tbrowser_target_id\n' > "${manifest}"

for command in curl jq timeout; do
  command -v "${command}" >/dev/null 2>&1 || {
    echo "::error::Required command missing: ${command}"
    exit 2
  }
done

if [[ -z "${REA_BIN}" ]]; then
  echo "::error::REA CLI is not installed or is not on PATH"
  exit 2
fi
if [[ ! -x "${CHROME}" ]]; then
  echo "::error::Chrome executable is unavailable: ${CHROME}"
  exit 2
fi

rea --version --json > "${REPORT_DIR}/rea-version.json" 2> "${REPORT_DIR}/rea-version.stderr" || true
rea capabilities --json > "${REPORT_DIR}/rea-capabilities.json" 2> "${REPORT_DIR}/rea-capabilities.stderr" || true

profile="${RUNNER_TEMP}/mangon-rea-chrome-profile"
mkdir -p "${profile}"
"${CHROME}" \
  --headless=new \
  --no-sandbox \
  --disable-dev-shm-usage \
  --disable-background-networking \
  --disable-extensions \
  --disable-sync \
  --no-first-run \
  --no-default-browser-check \
  --remote-debugging-address=127.0.0.1 \
  --remote-debugging-port=9222 \
  --user-data-dir="${profile}" \
  about:blank \
  > "${REPORT_DIR}/chrome.stdout.log" \
  2> "${REPORT_DIR}/chrome.stderr.log" &
chrome_pid=$!

cleanup() {
  kill "${chrome_pid}" >/dev/null 2>&1 || true
  wait "${chrome_pid}" >/dev/null 2>&1 || true
}
trap cleanup EXIT

ready=false
for _ in $(seq 1 40); do
  if curl --silent --fail "${CDP_ENDPOINT}/json/version" >/dev/null 2>&1; then
    ready=true
    break
  fi
  sleep 1
done
if [[ "${ready}" != true ]]; then
  echo "::error::Chrome DevTools endpoint did not become ready"
  exit 2
fi

# Only public sample pages are visited. No login, account state, cookies,
# paid access, CAPTCHA, geographic restriction or anti-bot controls are bypassed.
targets=(
  "globalcomix-catalog|https://globalcomix.com/browse/fa/manga"
  "globalcomix-release|https://globalcomix.com/c/god-book/chapters/fa/1/"
  "globalcomix-reader|https://globalcomix.com/read/748c2c55-dba8-4730-936b-bde19a6c11e1/13"
  "manhwachi-catalog|https://manhwachi.ir/library-page.html"
  "manhwachi-details|https://manhwachi.ir/comic/error-d-echo"
  "manhwachi-reader|https://manhwachi.ir/reader.html?slug=error-d-echo&ch=1"
  "mangabox-catalog|https://mangabox.ir/archive"
  "mangabox-details|https://mangabox.ir/work/the-forgotten-field"
  "mangabox-reader|https://mangabox.ir/read/the-forgotten-field/1"
  "meast-catalog|https://meast.ir/mangas"
  "meast-details|https://meast.ir/manga/4/"
  "meast-reader|https://meast.ir/chapter/165?manga_id=3"
  "dango-catalog|https://dangomanga.ir/manga/"
  "dango-search|https://dangomanga.ir/manga/?s=solo"
  "mangaup-reader|https://mangaup.ir/manga/133/reader/7053"
  "maneast-details|https://maneast.fun/manhwa/1382/my-slain-dragon-bride"
  "mangate-reader|https://mangate.ir/manhwa/201/reader/6551"
  "mangariom-details|https://mangariom.top/all-books/the-fragrant-flower-blooms-with-dignity/"
  "mangamic-details|https://mangamic.com/picbook/the-horizon/"
  "mangato-details|https://mangato.ir/work/20th-century-boys"
  "minato-details|https://www.minatomanhwa.com/manga/invincible/"
  "twomanga-chapters|https://twomanga.top/Manga/Blue-Box-9823/chapters"
  "manhwatower-details|https://mt98.ir/Manhwa/Omniscient_Reader/"
)

capture_count=0
success_count=0

for item in "${targets[@]}"; do
  name="${item%%|*}"
  url="${item#*|}"
  page_dir="${REPORT_DIR}/pages/${name}"
  mkdir -p "${page_dir}"

  echo "::group::REA audit ${name}"
  printf '%s\n' "${url}" > "${page_dir}/target-url.txt"

  tab_json="$(curl --silent --show-error --fail --max-time 20 \
    --request PUT --globoff "${CDP_ENDPOINT}/json/new?${url}" 2> "${page_dir}/open.stderr")"
  open_status=$?
  target_id=""
  if [[ "${open_status}" -eq 0 ]]; then
    target_id="$(printf '%s' "${tab_json}" | jq -r '.id // empty' 2>/dev/null || true)"
  fi

  if [[ -z "${target_id}" ]]; then
    printf 'open_failed\t%s\n' "${open_status}" > "${page_dir}/status.txt"
    printf '%s\t%s\topen_failed\topen_failed\t\n' "${name}" "${url}" >> "${manifest}"
    echo "::warning::Could not open public page ${url}"
    echo "::endgroup::"
    continue
  fi

  capture_count=$((capture_count + 1))
  printf '%s\n' "${target_id}" > "${page_dir}/browser-target-id.txt"
  sleep 6

  if timeout 90s "${REA_BIN}" list-browser-targets "${CDP_ENDPOINT}" --json \
      > "${page_dir}/rea-targets.json" 2> "${page_dir}/rea-targets.stderr"; then
    target_status="listed"
  else
    target_status="list_failed"
  fi

  if timeout 120s "${REA_BIN}" inspect-web-page "${CDP_ENDPOINT}" "${target_id}" \
      --observation-ms 2500 \
      --include-accessibility-text \
      --include-console-text \
      --include-json-body-shapes \
      --include-script-sources \
      --json \
      > "${page_dir}/inspection.json" 2> "${page_dir}/inspection.stderr"; then
    inspect_status="success"
    success_count=$((success_count + 1))
  else
    inspect_status="failed"
  fi

  if timeout 150s "${REA_BIN}" analyze-web-bundle "${CDP_ENDPOINT}" "${target_id}" \
      --observation-ms 1000 \
      --fetch-source-maps \
      --json \
      > "${page_dir}/bundle.json" 2> "${page_dir}/bundle.stderr"; then
    bundle_status="success"
  else
    bundle_status="failed"
  fi

  printf 'target_list=%s\ninspection=%s\nbundle=%s\n' \
    "${target_status}" "${inspect_status}" "${bundle_status}" > "${page_dir}/status.txt"
  printf '%s\t%s\t%s\t%s\t%s\n' \
    "${name}" "${url}" "${inspect_status}" "${bundle_status}" "${target_id}" >> "${manifest}"

  curl --silent --fail --max-time 10 --request PUT \
    "${CDP_ENDPOINT}/json/close/${target_id}" >/dev/null 2>&1 || true
  echo "::endgroup::"
done

printf 'pages_opened=%s\ninspections_succeeded=%s\n' \
  "${capture_count}" "${success_count}" > "${REPORT_DIR}/summary.txt"

if [[ "${capture_count}" -eq 0 ]]; then
  echo "::error::REA could not open any candidate page"
  exit 1
fi
if [[ "${success_count}" -eq 0 ]]; then
  echo "::error::REA did not produce any successful page inspection"
  exit 1
fi

echo "REA site audit completed. Inspect per-page status and Evidence before accepting any source."
