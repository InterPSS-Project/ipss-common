#!/usr/bin/env bash

if [ -z "${BASH_VERSION:-}" ]; then
	if command -v bash >/dev/null 2>&1; then
		exec bash "$0" "$@"
	fi
	echo "bash is required to run this script" >&2
	exit 1
	fi

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if ! command -v conda >/dev/null 2>&1; then
	echo "conda command not found" >&2
	exit 1
fi

eval "$(conda shell.bash hook)"
conda activate ipss_py
python "$SCRIPT_DIR/run_all_tests.py"