"""
Python translation of test_parallel_aclfContingency_24HSP1ap() function
using the ParallelContingencyAnalyzer Java wrapper for parallel operations.

This script demonstrates how to perform parallel contingency analysis
on the 24HSP1ap PSSE case using InterPSS through JPype.
"""

##
# Acknowledgement:
# The synthetic 25k-bus electric grid test case used in this example is provided by Texas A&M University’s energy and power group researchers.
# https://electricgrids.engr.tamu.edu/
#  
# For details of the synthetic grid, please visit the website above and/or refer to the following references:
#  [1] A. B. Birchfield; T. Xu; K. M. Gegner; K. S. Shetye; T. J. Overbye, “Grid Structural Characteristics as Validation Criteria for Synthetic Networks,”  in IEEE Transactions on Power Systems, vol. 32, no. 4, pp. 3258-3265, July 2017.
#  [2] A. B. Birchfield; K. M. Gegner; T. Xu; K. S. Shetye; T. J. Overbye, “Statistical Considerations in the Creation of Realistic Synthetic PowerGrids for Geomagnetic Disturbance Studies,” in IEEE Transactions on Power Systems, vol. 32, no. 2, pp. 1502-1510, March 2017.
#  [3] K. M. Gegner; A. B. Birchfield; T. Xu; K. S. Shetye; T. J. Overbye, “A methodology for the creation of geographically realistic synthetic powerflow models,” 2016 IEEE Power and Energy Conference at Illinois (PECI), Urbana, IL, 2016, pp. 1-6.
#

import jpype
import jpype.imports
from jpype.types import *
import time
import os
from pathlib import Path

def parallel_aclf_contingency():
    """
    Performs parallel contingency analysis using Java backend.
    """
    
    # Use platform-neutral path operations
    script_dir = Path(__file__).parent.absolute()
    
    # Get JVM path
    jvm_path = jpype.getDefaultJVMPath()
    print(f"JVM Path: {jvm_path}")
    
    # Convert relative path to absolute using Path for cross-platform compatibility
    jar_path = str(script_dir.parent / "lib" / "ipss_runnable.jar")
    print(f"JAR path: {jar_path}")
    
    # Check if jar exists
    if not Path(jar_path).exists():
        raise FileNotFoundError(f"JAR file not found: {jar_path}")
    
    # Start JVM with platform-neutral path
    jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")
    
    try:
        # Import Java classes
        from org.interpss import IpssCorePlugin
        from com.interpss.common.util import IpssLogger
        from org.ieee.odm.common import ODMLogger
        from org.ieee.odm.adapter.psse.PSSEAdapter import PsseVersion
        from org.ieee.odm.adapter.psse.raw import PSSERawAdapter
        from org.ieee.odm.adapter.psse import PSSEAdapter
        from org.ieee.odm.model.aclf import AclfModelParser
        from org.interpss.odm.mapper import ODMAclfParserMapper
        from com.interpss.simu import SimuObjectFactory, SimuCtxType
        from com.interpss.core import CoreObjectFactory
        from com.interpss.core.algo import AclfMethodType
        from java.util.logging import Level
        
        # Import our custom parallel contingency analyzer
        from org.interpss.plugin.contingency import ParallelContingencyAnalyzer
        
        # Initialize InterPSS core plugin
        IpssCorePlugin.init()
        
        # Set logging levels
        IpssLogger.getLogger().setLevel(Level.INFO)
        ODMLogger.getLogger().setLevel(Level.FINE)
        
        print("Starting parallel contingency analysis test...")
        start_time = time.time()
        
        # Load PSSE RAW file - use Path for cross-platform path handling
        adapter = PSSERawAdapter(PsseVersion.PSSE_33)
        raw_path = str(script_dir.parent.parent / "testData" / "psse" / "ACTIVSg25k.RAW")
        print(f"Loading file: {raw_path}")

        # Check if the raw file exists
        if not Path(raw_path).exists():
            raise FileNotFoundError(f"RAW file not found: {raw_path}")

        adapter.parseInputFile(raw_path)
        net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

        
        print(f"Active bus size: {net.getNoActiveBus()}")
        print(f"Active branch size: {net.getNoActiveBranch()}")
        
        # Configuration for contingency analysis
        config = ParallelContingencyAnalyzer.createDefaultConfig()
        config.setMaxIterations(50)
        config.setTolerance(0.005)
        config.setLfMethod(AclfMethodType.NR)
        config.setNonDivergent(True)
        config.setApplyAdjustAlgo(False)

        # Number of contingency cases to analyze
        total_cases = 50
        
        print(f"\n=== Sequential Contingency Analysis ===")
        sequential_start = time.time()
        sequential_result = ParallelContingencyAnalyzer.analyzeContingencies(
            net, total_cases, config, False  # useParallel = False
        )
        sequential_time = time.time() - sequential_start
        
        print(f"\n=== Parallel Contingency Analysis ===")
        parallel_start = time.time()
        parallel_result = ParallelContingencyAnalyzer.analyzeContingencies(
            net, total_cases, config, True  # useParallel = True
        )
        parallel_time = time.time() - parallel_start
        
        # Print detailed results
        print(f"\n=== COMPARISON RESULTS ===")
        print(f"Sequential Analysis:")
        print(f"  - Execution Time: {sequential_result.getExecutionTimeSeconds():.3f} seconds")
        print(f"  - Success Rate: {sequential_result.getSuccessRate() * 100:.2f}%")
        print(f"  - Successful Cases: {sequential_result.getTotalSuccessCount()}/{sequential_result.getTotalCases()}")
        
        print(f"\nParallel Analysis:")
        print(f"  - Execution Time: {parallel_result.getExecutionTimeSeconds():.3f} seconds")
        print(f"  - Success Rate: {parallel_result.getSuccessRate() * 100:.2f}%")
        print(f"  - Successful Cases: {parallel_result.getTotalSuccessCount()}/{parallel_result.getTotalCases()}")
        
        speedup = sequential_result.getExecutionTimeSeconds() / parallel_result.getExecutionTimeSeconds()
        print(f"\nSpeedup: {speedup:.2f}x")
        
        # Optionally print detailed results for debugging
        print_detailed = False  # Set to True if you want detailed branch-by-branch results
        if print_detailed:
            print("\n=== DETAILED SEQUENTIAL RESULTS ===")
            ParallelContingencyAnalyzer.printDetailedResults(sequential_result)
            
            print("\n=== DETAILED PARALLEL RESULTS ===")
            ParallelContingencyAnalyzer.printDetailedResults(parallel_result)
        
        # Verify results consistency
        seq_results = sequential_result.getConvergenceResults()
        par_results = parallel_result.getConvergenceResults()
        
        mismatches = []
        for branch_id in seq_results.keySet():
            if seq_results.get(branch_id) != par_results.get(branch_id):
                mismatches.append(branch_id)
        
        if mismatches:
            print(f"\nWARNING: Found {len(mismatches)} result mismatches between sequential and parallel:")
            for branch_id in mismatches[:10]:  # Show first 10 mismatches
                print(f"  Branch {branch_id}: Sequential={seq_results.get(branch_id)}, Parallel={par_results.get(branch_id)}")
        else:
            print(f"\n✓ All results match between sequential and parallel analysis!")
        
        total_time = time.time() - start_time
        print(f"\nTotal test execution time: {total_time:.3f} seconds")
        
        return {
            'sequential_result': sequential_result,
            'parallel_result': parallel_result,
            'speedup': speedup,
            'mismatches': len(mismatches)
        }
        
    finally:
        # Shutdown JVM
        if jpype.isJVMStarted():
            jpype.shutdownJVM()

if __name__ == "__main__":
    """
    Main execution block - run the parallel contingency analysis test.
    """
    print("=== InterPSS Parallel Contingency Analysis Test ===")
    print("=" * 60)
    
    try:
        # Run the main test
        results = parallel_aclf_contingency()
        
    except Exception as e:
        print(f"Error during test execution: {e}")
        import traceback
        traceback.print_exc()
