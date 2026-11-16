#!/usr/bin/env bash
set -uo pipefail

cd "$(dirname "$0")" || exit 1

java_home="${JAVA_HOME_25:-/usr/lib/jvm/java-25-openjdk}"
bukkit_sources="platforms/bukkit"
errors=0
warnings=0

error() {
    echo "error: $*"
    errors=$((errors + 1))
}

warning() {
    echo "warning: $*"
    warnings=$((warnings + 1))
}

build_log="$(mktemp)"
trap 'rm -f "$build_log"' EXIT

echo "==> Building"
if [[ ! -x "$java_home/bin/java" ]]; then
    error "JDK 25 not found at $java_home (set JAVA_HOME_25)"
elif ! JAVA_HOME="$java_home" ./gradlew :platforms:bukkit:build -x test --rerun-tasks --console=plain >"$build_log" 2>&1; then
    error "gradle build failed: $(grep -m1 -E 'error:|What went wrong' -A1 "$build_log" | tail -n1 || echo 'see gradle output')"
fi
while IFS= read -r line; do
    warning "javac: $line"
done < <(grep -E "/$bukkit_sources/.*\.java:[0-9]+: warning: " "$build_log")

echo "==> Checking jar"
if ! compgen -G "$bukkit_sources/build/libs/*.jar" >/dev/null; then
    error "no Bukkit jar in $bukkit_sources/build/libs"
fi

echo "==> Checking plugin.yml"
plugin_yml="$bukkit_sources/common/src/main/resources/plugin.yml"
if [[ ! -f "$plugin_yml" ]]; then
    error "$plugin_yml is missing"
elif ! grep -qE '^folia-supported: true' "$plugin_yml"; then
    error "$plugin_yml does not declare folia-supported: true"
fi

echo "==> Checking Bukkit sources"
while IFS= read -r line; do
    error "BukkitScheduler use: $line"
done < <(grep -rnE 'getScheduler\(\)|BukkitRunnable|BukkitScheduler' --include=*.java "$bukkit_sources")
while IFS= read -r line; do
    warning "unfinished: $line"
done < <(grep -rnE 'TODO|FIXME|XXX' --include=*.java "$bukkit_sources")

echo "==> $errors error(s), $warnings warning(s)"
[[ $errors -eq 0 && $warnings -eq 0 ]]
