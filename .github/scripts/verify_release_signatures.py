#!/usr/bin/env python3
"""Verify every release APK/JAR signer against the repository certificate."""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path


APK_DIGEST = re.compile(r"certificate SHA-256 digest:\\s*([0-9a-f:]+)", re.IGNORECASE)
JAR_DIGEST = re.compile(r"^\\s*SHA256:\\s*([0-9a-f:]+)\\s*$", re.IGNORECASE | re.MULTILINE)
FINGERPRINT = re.compile(r"^[0-9a-f]{64}$")


def _extract(pattern: re.Pattern[str], output: str, artifact: str) -> list[str]:
    values = []
    for match in pattern.finditer(output):
        value = match.group(1).replace(":", "").lower()
        if FINGERPRINT.fullmatch(value):
            values.append(value)
    if not values:
        raise ValueError(f"No valid {artifact} SHA-256 signer fingerprint found")
    return values


def extract_apk_fingerprints(output: str) -> list[str]:
    return _extract(APK_DIGEST, output, "APK")


def extract_jar_fingerprints(output: str) -> list[str]:
    return _extract(JAR_DIGEST, output, "JAR")


def verify_fingerprints(expected: str, apk: list[str], jar: list[str]) -> None:
    expected = expected.lower()
    if not FINGERPRINT.fullmatch(expected):
        raise ValueError("The expected signing fingerprint must be 64 lowercase hexadecimal characters")
    if not apk or any(value != expected for value in apk):
        raise ValueError(f"APK signer certificate mismatch: {', '.join(apk) or 'none found'}")
    if not jar or any(value != expected for value in jar):
        raise ValueError(f"JAR signer certificate mismatch: {', '.join(jar) or 'none found'}")


def _run(command: list[str], description: str) -> str:
    result = subprocess.run(command, text=True, capture_output=True, check=False)
    if result.returncode:
        detail = result.stderr.strip() or result.stdout.strip() or "no diagnostic output"
        raise RuntimeError(f"{description} failed (exit {result.returncode}): {detail}")
    return result.stdout


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--expected", required=True)
    parser.add_argument("--apk", type=Path, nargs="+", required=True)
    parser.add_argument("--jar", type=Path, nargs="+", required=True)
    parser.add_argument("--apksigner", type=Path, required=True)
    args = parser.parse_args()

    try:
        if len(args.apk) != len(args.jar):
            raise ValueError("The number of APKs and JARs must match")
        for path in (*args.apk, *args.jar, args.apksigner):
            if not path.is_file():
                raise FileNotFoundError(f"Required release-signing input is missing: {path}")

        apk_fingerprints: list[str] = []
        jar_fingerprints: list[str] = []
        for apk_path in args.apk:
            output = _run(
                [str(args.apksigner), "verify", "--verbose", "--print-certs", str(apk_path)],
                f"APK signature verification ({apk_path.name})",
            )
            apk_fingerprints.extend(extract_apk_fingerprints(output))
        for jar_path in args.jar:
            output = _run(
                ["keytool", "-printcert", "-jarfile", str(jar_path)],
                f"JAR signature inspection ({jar_path.name})",
            )
            jar_fingerprints.extend(extract_jar_fingerprints(output))

        print(f"Expected signing certificate: {args.expected.lower()}")
        print(f"Verified APK files: {len(args.apk)}")
        print(f"Verified JAR files: {len(args.jar)}")
        verify_fingerprints(args.expected, apk_fingerprints, jar_fingerprints)
    except (FileNotFoundError, RuntimeError, ValueError) as error:
        print(f"::error::{error}", file=sys.stderr)
        return 1

    print("Every APK and JAR signature matches the repository certificate.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
