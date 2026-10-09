"""Prepare a Java-only Android build from the verified v0.5.4 release.

No Xbox executable or native compiler is needed. Native libraries and shader
assets are copied without modification; SDL Java comes from the SDK's pinned
SDL revision. All downloaded/extracted material lives in ignored out/.
"""
import argparse
import hashlib
import io
import json
from pathlib import Path
import shutil
import urllib.request
import zipfile

APK_SHA256 = "f2be67907cc69427e051fa34387d54af67f54cad8930f3154fce26bfc38124b6"
SDL_COMMIT = "8bf3b7215ad9fc3deb583c6a3a37c6c67f2e24e4"
SDL_URL = f"https://codeload.github.com/libsdl-org/SDL/zip/{SDL_COMMIT}"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", required=True, type=Path)
    parser.add_argument("--sdl-zip", type=Path, help="Optional offline cache of the pinned SDL archive")
    args = parser.parse_args()
    data = args.apk.read_bytes()
    if hashlib.sha256(data).hexdigest() != APK_SHA256:
        parser.error("Expected the unmodified upstream v0.5.4 ARM64 APK; SHA-256 differs")
    root = Path(__file__).resolve().parents[1]
    target = root / "out/android-prebuilt"
    staging = target.with_name("android-prebuilt.preparing")
    if staging.exists():
        shutil.rmtree(staging)
    staging.mkdir(parents=True)
    hashes = {}
    try:
        with zipfile.ZipFile(io.BytesIO(data)) as apk:
            for entry in apk.infolist():
                if entry.is_dir():
                    continue
                if entry.filename.startswith("lib/arm64-v8a/"):
                    relative = "jniLibs/" + entry.filename[len("lib/"):]
                    hashes[entry.filename] = hashlib.sha256(apk.read(entry)).hexdigest()
                elif entry.filename.startswith("assets/") and not entry.filename.startswith("assets/dexopt/"):
                    # AGP regenerates the Java baseline profile for the new DEX.
                    relative = entry.filename
                else:
                    continue
                path = staging / relative
                if not path.resolve().is_relative_to(staging.resolve()):
                    raise ValueError(f"Invalid archive path: {entry.filename}")
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_bytes(apk.read(entry))
        if args.sdl_zip:
            sdl_data = args.sdl_zip.read_bytes()
        else:
            request = urllib.request.Request(SDL_URL, headers={"User-Agent": "NFSMW-HU-EN-build"})
            with urllib.request.urlopen(request, timeout=60) as response:
                sdl_data = response.read()
        prefix = f"SDL-{SDL_COMMIT}/android-project/app/src/main/java/"
        with zipfile.ZipFile(io.BytesIO(sdl_data)) as archive:
            for entry in archive.infolist():
                if entry.is_dir() or not entry.filename.startswith(prefix):
                    continue
                path = staging / "sdl-java" / entry.filename[len(prefix):]
                if not path.resolve().is_relative_to(staging.resolve()):
                    raise ValueError(f"Invalid SDL archive path: {entry.filename}")
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_bytes(archive.read(entry))
        if not (staging / "sdl-java/org/libsdl/app/SDLActivity.java").is_file():
            raise ValueError("The pinned SDL archive contains no Android Java sources")
        provenance = {"upstream_apk_sha256": APK_SHA256, "sdl_commit": SDL_COMMIT,
                      "native_libraries": hashes}
        (staging / "provenance.json").write_text(json.dumps(provenance, indent=2) + "\n")
        if target.exists():
            shutil.rmtree(target)
        staging.rename(target)
        print(f"Prepared {len(hashes)} unchanged native libraries in {target}")
    finally:
        if staging.exists():
            shutil.rmtree(staging)


if __name__ == "__main__":
    main()
