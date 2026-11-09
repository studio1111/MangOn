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
    if len(metadata_files) != 1:
        raise ValueError(f"Expected exactly one source-info JSON, found {len(metadata_files)}")

    info_path = metadata_files[0]
    info = json.loads(info_path.read_text(encoding="utf-8"))
    required = ("name", "packageName", "versionCode", "versionName", "extensionLib", "sources")
    missing = [key for key in required if key not in info]
    if missing:
        raise ValueError(f"Source metadata missing required fields: {', '.join(missing)}")
    if not info["sources"]:
        raise ValueError("At least one source is required")

    apk_files = sorted(info_path.parent.rglob("*.apk"))
    jar_files = sorted(info_path.parent.rglob("*.jar"))
    if len(apk_files) != 1 or len(jar_files) != 1:
        raise ValueError(f"Expected one release APK and one signed JAR; found {len(apk_files)} APK(s), {len(jar_files)} JAR(s)")

    apk_name, jar_name = apk_files[0].name, jar_files[0].name
    output_dir.mkdir(parents=True, exist_ok=True)
    apk_dir, jar_dir = output_dir / "apk", output_dir / "jar"
    apk_dir.mkdir(exist_ok=True)
    jar_dir.mkdir(exist_ok=True)
    (apk_dir / apk_name).write_bytes(apk_files[0].read_bytes())
    (jar_dir / jar_name).write_bytes(jar_files[0].read_bytes())

    upstream_icon = "https://raw.githubusercontent.com/keiyoushi/extensions-source/ee661f5233b346559fbdd53993d447c318c5aef0/core/src/main/res/mipmap-xhdpi/ic_launcher.png"
    resolved_icon = icon_url or upstream_icon

    extension = index_pb2.Extension(
        name=info["name"],
        packageName=info["packageName"],
        extensionLib=info["extensionLib"],
        versionCode=int(info["versionCode"]),
        versionName=info["versionName"],
        contentWarning=WARNING_VALUES.get(int(info.get("contentWarning", 0)), index_pb2.CONTENT_WARNING_UNSPECIFIED),
        # Mihon's official NetworkExtensionStore.Resources schema contains only
        # apkUrl (field 1) and iconUrl (field 2). Keep the signed JAR published
        # as a repository asset, but do not inject the non-standard field 501
        # into index.pb; strict protobuf consumers may reject unknown fields.
        resources=index_pb2.Resources(
            apkUrl=f"{base_url.rstrip('/')}/apk/{quote(apk_name)}",
            iconUrl=resolved_icon,
        ),
    )
    for source in info["sources"]:
        extension.sources.add(
            id=int(source["id"]),
            name=source["name"],
            language=source["lang"],
            homeUrl=source["baseUrl"],
            mirrorUrls=source.get("mirrorUrls", []),
        )

    index = index_pb2.Index(
        name="MangOn",
        badgeLabel="MANGON",
        signingKey=signing_key,
        contact=index_pb2.Contact(website="https://github.com/studio1111/MangOn"),
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
