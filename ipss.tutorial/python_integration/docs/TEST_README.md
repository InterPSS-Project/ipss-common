# InterPSS Python Integration Test Suite

## Quick Start

Run all standard tests:
```bash
python run_all_tests.py
```

## Usage Options

### Run all tests (including large systems):
```bash
python run_all_tests.py --all
```

### Run only large system tests:
```bash
python run_all_tests.py --large
```

### Run a specific test:
```bash
python run_all_tests.py sample_psse.py
```

### Show detailed error messages:
```bash
python run_all_tests.py --verbose
```

### Combine options:
```bash
python run_all_tests.py --all --verbose
```

## Test Categories

### Basic Tests (Fast - ~1-2 minutes each)
- `sample_psse.py` - IEEE 9-bus basic example
- `run_pf_kundur_vschvdc.py` - Kundur 2-area VSC HVDC
- `run_pf_kundur_lcchvdc.py` - Kundur 2-area LCC HVDC

### Medium Tests (~2-3 minutes each)
- `run_pf_texas2k.py` - Texas 2000-bus power flow
- `run_pf_texas2k_zbr.py` - Texas 2000-bus with ZBR model

### Contingency Analysis Tests (~5-10 minutes each)
- `run_aclf_contingency_texas2k.py` - AC contingency analysis
- `run_dclf_contingency_texas2k.py` - DC contingency analysis

### Large System Tests (Slow - skipped by default)
- `run_pf_ACTIVSg25k.py` - 25k-bus power flow
- `run_aclf_contingency_ACTIVSg25k.py` - 25k-bus contingency analysis

## Exit Codes

- `0` - All tests passed
- `1` - One or more tests failed

## Example Output

```
======================================================================
InterPSS Python Integration Test Suite
======================================================================

[BASIC] Basic PSSE IEEE9Bus example
Running: sample_psse.py
✓ PASSED (1.23s)

[MEDIUM] Texas 2000-bus power flow
Running: run_pf_texas2k.py
✓ PASSED (45.67s)

======================================================================
Test Results Summary
======================================================================

Total tests run: 7
Passed: 7
Failed: 0
Total time: 123.45s
```

## Tips

1. **First time setup**: Make sure all required data files are in place in the `testData` directory
2. **Memory**: Large system tests may require significant memory (8GB+ recommended)
3. **Parallel execution**: Tests run sequentially to avoid JVM conflicts
4. **Timeout**: Each test has a timeout (60-600s depending on complexity)
5. **Results**: Individual test results are saved in the `results` directory

## Troubleshooting

If a test fails:
1. Run it with `--verbose` to see full error messages
2. Run the specific test file directly: `python <test_name>.py`
3. Check that the required data files exist
4. Verify the JAR file is in the correct location: `../lib/ipss_runnable.jar`
