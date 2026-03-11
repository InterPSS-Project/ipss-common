#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if ! command -v conda >/dev/null 2>&1; then
	echo "conda command not found" >&2
	exit 1
fi

eval "$(conda shell.bash hook)"
conda activate ipss_py
python "$SCRIPT_DIR/run_all_tests.py"