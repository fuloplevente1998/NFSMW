"""Verify a UI-only APK retains upstream native code and runtime assets."""
import argparse
import hashlib
import json
from pathlib import Path
import zipfile

from prepare_android_prebuilt import APK_SHA256


def sha256(data):
    return hashlib.sha256(data).hexdigest()


def runtime_paths(archive):
    return {path for path in archive.namelist() if not path.endswith("/") and
            (path.startswith("lib/") or
             (path.startswith("assets/") and not path.startswith("assets/dexopt/")))}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--upstream-apk", type=Path, required=True)
    parser.add_argument("--test-apk", type=Path, required=True)
    parser.add_argument("--report", type=Path)
    args = parser.parse_args()
    if sha256(args.upstream_apk.read_bytes()) != APK_SHA256:
        parser.error("Upstream APK differs from the pinned v0.5.4 release")
    with zipfile.ZipFile(args.upstream_apk) as original, zipfile.ZipFile(args.test_apk) as rebuilt:
        paths = runtime_paths(original)
        if paths != runtime_paths(rebuilt):
            parser.error("Runtime file inventory differs: " + str(paths ^ runtime_paths(rebuilt)))
        hashes = {}
        for path in sorted(paths):
            original_hash = sha256(original.read(path))
            if sha256(rebuilt.read(path)) != original_hash:
                parser.error("Changed runtime file: " + path)
            hashes[path] = original_hash
    report = {
        "upstream_apk_sha256": APK_SHA256,
        "test_apk_sha256": sha256(args.test_apk.read_bytes()),
        "native_libraries_unchanged": sum(path.startswith("lib/") for path in hashes),
        "runtime_assets_unchanged": sum(path.startswith("assets/") for path in hashes),
        "files": hashes,
        "excluded": "assets/dexopt/: regenerated Java baseline profiles",
    }
    if args.report:
        args.report.write_text(json.dumps(report, indent=2) + "\n")
    print(f"PASS: {report['native_libraries_unchanged']} native libraries and "
          f"{report['runtime_assets_unchanged']} runtime assets are byte-identical to v0.5.4")
    print("Test APK SHA-256: " + report["test_apk_sha256"])


if __name__ == "__main__":
    main()
