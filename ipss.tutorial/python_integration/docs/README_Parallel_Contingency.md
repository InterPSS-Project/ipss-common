# Parallel Contingency Analysis for Python

This directory contains the Python translation of the `parallel_aclfContingency` function with a Java wrapper for parallel operations.

## Files Overview

### Java Components
- **`ParallelContingencyAnalyzer.java`** - Java wrapper class that provides thread-safe parallel contingency analysis


### Python Components
- **`run_parallelContingencyAnalyzer_ACTIVSg25k.py`** - Complete Python translation of the original Java test
- **`run_ThreadPool_contingency_ACTIVSg25kk.py`** - Use Python threadpool to run contingency in parallel
- **`run_aclf_contingency_ACTIVSg25k.py`** - Existing sequential contingency analysis example

## Key Features

### ParallelContingencyAnalyzer Java Class

1. **Thread-Safe Parallel Processing**: Uses Java 8 parallel streams for concurrent contingency analysis
2. **Configurable Parameters**: 
   - Load flow method (Newton-Raphson, PQ, etc.)
   - Maximum iterations
   - Tolerance
   - Island bus handling
   - Auto line-to-transformer conversion

3. **Result Management**:
   - Detailed convergence results per branch
   - Success rate calculation
   - Execution time tracking
   - Comparison between sequential and parallel execution

4. **Python-Friendly API**:
   - Static methods for easy JPype access
   - Default configuration builder
   - Detailed result printing utilities

### Python Integration

The Python scripts demonstrate how to:
- Initialize the JVM and InterPSS framework
- Load PSSE RAW files
- Configure load flow algorithms
- Run both sequential and parallel contingency analysis
- Compare performance and results

## Usage Example

```python
# Import after JVM startup
from org.interpss.tutorial.ch6_contingency import ParallelContingencyAnalyzer

# Create configuration
config = ParallelContingencyAnalyzer.createDefaultConfig()
config.setMaxIterations(50)
config.setTolerance(0.005)

# Run parallel analysis
result = ParallelContingencyAnalyzer.analyzeContingencies(
    network,        # AclfNetwork object
    50,            # Number of contingency cases
    config,        # Configuration object
    True           # Use parallel processing
)

# Get results
print(f"Success rate: {result.getSuccessRate() * 100:.2f}%")
print(f"Execution time: {result.getExecutionTimeSeconds():.3f} seconds")
```

## Performance Benefits

The parallel implementation provides significant speedup for large-scale contingency analysis:
- **Parallel Processing**: Utilizes multiple CPU cores
- **Memory Efficient**: Each thread works on independent network copies
- **Scalable**: Performance scales with available CPU cores
- **Safe**: Thread-safe implementation prevents race conditions

## File Requirements

To run the tests, you need:
1. **ipss_runnable.jar** - InterPSS runnable JAR file in `../lib/` directory
2. **PSSE RAW file** - Network data file
3. **JPype** - Python-Java bridge library
4. **Java 8+** - For parallel streams support

## Original Java Method Translation

1. **Network Loading**: PSSE v34 RAW file parsing
2. **Algorithm Configuration**: Newton-Raphson method with specified tolerance
3. **Contingency Analysis**: Remove each branch and test load flow convergence
4. **Performance Comparison**: Sequential vs parallel execution timing
5. **Result Validation**: Convergence rate and consistency checking

## Running the Tests

### Complete Test:
```bash
cd python_integration/src
python run_parallelContingencyAnalyzer_ACTIVSg25k.py
```


This implementation provides a robust, scalable solution for large-scale power system contingency analysis accessible from Python while leveraging Java's parallel processing capabilities.
