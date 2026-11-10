#!/usr/bin/env python3
"""Build a Mihon-compatible, gzip-compressed protobuf extension repository index."""
from __future__ import annotations

import argparse
import gzip
import json
import re
from pathlib import Path
from urllib.parse import quote

from google.protobuf import json_format
import index_pb2


FINGERPRINT_RE = re.compile(r"^[0-9a-f]{64}$")
WARNING_VALUES = {
    0: index_pb2.CONTENT_WARNING_UNSPECIFIED,
    1: index_pb2.CONTENT_WARNING_SAFE,
    2: index_pb2.CONTENT_WARNING_MIXED,
    3: index_pb2.CONTENT_WARNING_NSFW,
}


def _load_metadata(path: Path) -> dict:
    info = json.loads(path.read_text(encoding="utf-8"))
    required = ("name", "packageName", "versionCode", "versionName", "extensionLib", "sources")
    missing = [key for key in required if key not in info]
    if missing:
        raise ValueError(f"{path}: source metadata missing required fields: {', '.join(missing)}")
    if not isinstance(info["sources"], list) or not info["sources"]:
        raise ValueError(f"{path}: at least one source is required")
    if not isinstance(info["packageName"], str) or not info["packageName"].strip():
        raise ValueError(f"{path}: packageName must be a non-empty string")
    return info


def _copy_release_assets(info_path: Path, output_dir: Path) -> tuple[str, str]:
    apk_files = sorted(info_path.parent.rglob("*.apk"))
    jar_files = sorted(info_path.parent.rglob("*.jar"))
    if len(apk_files) != 1 or len(jar_files) != 1:
        raise ValueError(
            f"{info_path.parent}: expected one release APK and one signed JAR; "
            f"found {len(apk_files)} APK(s), {len(jar_files)} JAR(s)"
        )

    apk_dir, jar_dir = output_dir / "apk", output_dir / "jar"
    apk_dir.mkdir(parents=True, exist_ok=True)
    jar_dir.mkdir(parents=True, exist_ok=True)
    apk_name, jar_name = apk_files[0].name, jar_files[0].name
    for directory, filename, source in (
        (apk_dir, apk_name, apk_files[0]),
        (jar_dir, jar_name, jar_files[0]),
    ):
        target = directory / filename
        if target.exists():
            raise ValueError(f"Duplicate release asset filename would overwrite another module: {filename}")
        target.write_bytes(source.read_bytes())
    return apk_name, jar_name


def build_index(
    artifacts_dir: Path,
    output_dir: Path,
    signing_key: str,
    base_url: str,
    icon_url: str,
) -> index_pb2.Index:
    if not FINGERPRINT_RE.fullmatch(signing_key):
        raise ValueError("signing_key must be a 64-character lowercase SHA-256 certificate fingerprint")
    if not base_url.startswith("https://") or (icon_url and not icon_url.startswith("https://")):
        raise ValueError("Repository and icon URLs must use HTTPS")

    metadata_files = sorted(artifacts_dir.rglob("keiyoushi-source-info.json"))
    if not metadata_files:
        raise ValueError("No keiyoushi-source-info.json files were found")

    output_dir.mkdir(parents=True, exist_ok=True)
    upstream_icon = "https://raw.githubusercontent.com/keiyoushi/extensions-source/ee661f5233b346559fbdd53993d447c318c5aef0/core/src/main/res/mipmap-xhdpi/ic_launcher.png"
    resolved_icon = icon_url or upstream_icon
    index = index_pb2.Index(
        name="MangOn",
        badgeLabel="MANGON",
        signingKey=signing_key,
        contact=index_pb2.Contact(website="https://github.com/studio1111/MangOn"),
    )

    seen_packages: set[str] = set()
    for info_path in metadata_files:
        info = _load_metadata(info_path)
        package_name = info["packageName"]
        if package_name in seen_packages:
            raise ValueError(f"Duplicate extension packageName in build metadata: {package_name}")
        seen_packages.add(package_name)

        apk_name, _ = _copy_release_assets(info_path, output_dir)
        extension = index_pb2.Extension(
            name=info["name"],
            packageName=package_name,
            extensionLib=info["extensionLib"],
            versionCode=int(info["versionCode"]),
            versionName=info["versionName"],
            contentWarning=WARNING_VALUES.get(
                int(info.get("contentWarning", 0)),
                index_pb2.CONTENT_WARNING_UNSPECIFIED,
            ),
            # Mihon's official NetworkExtensionStore.Resources schema contains only
            # apkUrl (field 1) and iconUrl (field 2). Keep signed JARs published
            # as repository assets, but do not inject non-standard fields.
            resources=index_pb2.Resources(
                apkUrl=f"{base_url.rstrip('/')}/apk/{quote(apk_name)}",
                iconUrl=resolved_icon,
            ),
        )
        for source in info["sources"]:
            required_source = ("id", "name", "lang", "baseUrl")
            missing_source = [key for key in required_source if key not in source]
            if missing_source:
                raise ValueError(
                    f"{info_path}: source metadata missing required fields: {', '.join(missing_source)}"
                )
            extension.sources.add(
                id=int(source["id"]),
                name=source["name"],
                language=source["lang"],
                homeUrl=source["baseUrl"],
                mirrorUrls=source.get("mirrorUrls", []),
            )
        index.extensionList.extensions.add().CopyFrom(extension)

    (output_dir / "index.json").write_text(
        json_format.MessageToJson(
            index,
            preserving_proto_field_name=True,
            always_print_fields_with_no_presence=False,
        ) + "\n",
        encoding="utf-8",
    )
    (output_dir / "index.pb").write_bytes(
        gzip.compress(index.SerializeToString(deterministic=True), compresslevel=9, mtime=0)
    )
    repo_descriptor = {
        "index_v2": "https://raw.githubusercontent.com/studio1111/MangOn/repo/index.pb",
        "meta": {
            "name": "MangOn",
            "website": "https://github.com/studio1111/MangOn",
            "signingKeyFingerprint": signing_key,
        },
    }
    (output_dir / "repo.json").write_text(
        json.dumps(repo_descriptor, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    (output_dir / "README.txt").write_text(
        "MangOn Mihon extension repository\n"
        "Add this repository: https://raw.githubusercontent.com/studio1111/MangOn/repo/repo.json\n",
        encoding="utf-8",
    )
    return index


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--artifacts-dir", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--signing-key", required=True)
    parser.add_argument("--base-url", default="https://raw.githubusercontent.com/studio1111/MangOn/repo")
    parser.add_argument("--icon-url", default="")
    args = parser.parse_args()
    build_index(args.artifacts_dir, args.output_dir, args.signing_key, args.base_url, args.icon_url)


if __name__ == "__main__":
    main()
