import unittest

from verify_release_signatures import (
    extract_apk_fingerprints,
    extract_jar_fingerprints,
    verify_fingerprints,
)


EXPECTED = "0123456789abcdef" * 4
OTHER = "fedcba9876543210" * 4


class ReleaseSignatureTests(unittest.TestCase):
    def test_extracts_all_apk_signer_sha256_digests(self):
        output = f"""Signer #1 certificate SHA-256 digest: {EXPECTED[:2]}:{EXPECTED[2:]}
Signer #1 certificate SHA-1 digest: 00:11
Signer #2 certificate SHA-256 digest: {EXPECTED}
"""
        self.assertEqual(extract_apk_fingerprints(output), [EXPECTED, EXPECTED])

    def test_extracts_jar_sha256_digest_case_insensitively(self):
        output = f"""Owner: CN=Release
SHA256: {':'.join(EXPECTED[i:i + 2] for i in range(0, 64, 2)).upper()}
SHA1: 00:11
"""
        self.assertEqual(extract_jar_fingerprints(output), [EXPECTED])

    def test_rejects_missing_or_malformed_apk_digest(self):
        with self.assertRaisesRegex(ValueError, "No valid APK SHA-256"):
            extract_apk_fingerprints("Signer #1 certificate SHA-1 digest: 00:11")
        with self.assertRaisesRegex(ValueError, "No valid APK SHA-256"):
            extract_apk_fingerprints("Signer #1 certificate SHA-256 digest: not-a-digest")

    def test_rejects_any_signer_that_does_not_match_expected_key(self):
        with self.assertRaisesRegex(ValueError, "APK"):
            verify_fingerprints(EXPECTED, [EXPECTED, OTHER], [EXPECTED])
        with self.assertRaisesRegex(ValueError, "JAR"):
            verify_fingerprints(EXPECTED, [EXPECTED], [OTHER])

    def test_rejects_missing_jar_digest(self):
        with self.assertRaisesRegex(ValueError, "No valid JAR SHA-256"):
            extract_jar_fingerprints("Owner: CN=Release\\nSHA1: 00:11")

    def test_rejects_malformed_expected_fingerprint(self):
        with self.assertRaisesRegex(ValueError, "expected signing fingerprint"):
            verify_fingerprints("invalid", [EXPECTED], [EXPECTED])


if __name__ == "__main__":
    unittest.main()
