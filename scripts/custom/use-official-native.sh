#!/usr/bin/env bash
# Personal Linux build: reuse native engine, engine assets and models from the
# official release APK instead of compiling Swift/Rust natively (Windows-only).
# Only valid when HEAD is based on the same release tag as the APK.
# Usage: scripts/custom/use-official-native.sh v0.8.0
set -euo pipefail

tag="${1:?release tag, e.g. v0.8.0}"
repo_root="$(cd "$(dirname "$0")/../.." && pwd)"
work_dir="$(mktemp -d)"
trap 'rm -rf "$work_dir"' EXIT

cd "$repo_root"
git merge-base --is-ancestor "$tag" HEAD || { echo "HEAD is not based on $tag" >&2; exit 1; }

apk_name="ZeroConvert-IME-${tag}-universal-debug.apk"
gh release download "$tag" -R g0083/ZeroConvert-IME -p "$apk_name" -p SHA256SUMS.txt -D "$work_dir"
(cd "$work_dir" && tr -d '\r' < SHA256SUMS.txt | grep " $apk_name\$" | sha256sum -c -)

unzip -q "$work_dir/$apk_name" 'lib/*' 'assets/shared-engine/*' 'assets/models/*' -d "$work_dir/x"
rm -rf engine/build/jniLibs engine/build/assets/shared-engine
mkdir -p engine/build/jniLibs engine/build/assets android/app/src/main/assets/models
cp -r "$work_dir/x/lib/." engine/build/jniLibs/
cp -r "$work_dir/x/assets/shared-engine" engine/build/assets/
cp "$work_dir/x/assets/models/"*.gguf android/app/src/main/assets/models/

# The engine lock in the APK must match the committed one; otherwise the
# native binaries were built from different shared-engine sources.
diff <(tr -d '\r' < engine/build/assets/shared-engine/lock.json) <(tr -d '\r' < engine/shared-engine.lock.json)

# Same metadata the official build script writes next to the native libraries.
for abi in arm64-v8a x86_64; do
    out="engine/build/jniLibs/$abi"
    cp engine/build/assets/shared-engine/sources.json "$out/build-inputs.json"
    node -e '
const fs = require("fs"), crypto = require("crypto"), path = require("path");
const dir = process.argv[1];
const libs = fs.readdirSync(dir).filter(f => f.endsWith(".so")).map(name => ({
  name, sha256: crypto.createHash("sha256").update(fs.readFileSync(path.join(dir, name))).digest("hex"),
}));
console.log(JSON.stringify(libs, null, 2));' "$out" > "$out/libraries.json"
done

node windows/scripts/generate-reverse-dictionary.mjs \
    --source android/app/src/main/assets/dictionary/louds \
    --output android/app/src/main/assets/dictionary/reverse-v2.bin

echo "Ready. Build in android/ with: ./gradlew :rebuilt:assembleDebug -x verifySharedEngine"
