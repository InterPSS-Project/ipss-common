##
# Acknowledgment:
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
from concurrent.futures import ThreadPoolExecutor, as_completed
import threading


# Use platform-neutral path operations
parent_folder = Path.cwd().parent.parent
print(f"Parent folder: {parent_folder}")

# Get JVM path
jvm_path = jpype.getDefaultJVMPath()
print(f"JVM Path: {jvm_path}")

# Convert relative path to absolute using Path for cross-platform compatibility
script_dir = Path(__file__).parent.absolute()
jar_path = str(script_dir.parent / "lib" / "ipss_runnable.jar")
print(f"JAR path: {jar_path}")

# Check if jar exists
if not Path(jar_path).exists():
    raise FileNotFoundError(f"JAR file not found: {jar_path}")

# Start JVM with platform-neutral path
jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")

# Import Java classes for Stream operations
from com.interpss.core import CoreObjectFactory
from com.interpss.core import LoadflowAlgoObjectFactory
from com.interpss.core.algo import AclfMethodType
from com.interpss.core.funcImpl import AclfAdjCtrlFunction
# Import Java classes
from org.interpss.numeric.datatype.Unit import UnitType
from org.ieee.odm.adapter.psse.PSSEAdapter import PsseVersion
from org.ieee.odm.adapter.psse.raw import PSSERawAdapter
from org.interpss.odm.mapper import ODMAclfParserMapper

def analyze_contingencies_threadpool(network, total_cases, use_parallel=True):
    """
    Python implementation that leverages threadpool for contingency analysis.
    This mimics the Java Stream API behavior from the ParallelContingencyAnalyzer.
    """

    
    print(f"Starting {'parallel' if use_parallel else 'sequential'} contingency analysis with {total_cases} cases...")
    print(f"Active bus size: {network.getNoActiveBus()}")
    print(f"Active branch size: {network.getNoActiveBranch()}")
    
    start_time = time.time()
    
    # Configuration (equivalent to ContingencyConfig)
    config = {
        'turn_off_island_bus': True,
        'auto_turn_line2_xfr': True,
        'lf_method': AclfMethodType.NR,
        'apply_adjust_algo': False,
        'non_divergent': True,
        'max_iterations': 50,
        'tolerance': 0.005
    }
    
    # Thread-safe storage for results
    convergence_results = {}
    results_lock = threading.Lock()
    
    def process_contingency(i):
        """
        Process a single contingency case.
        This is equivalent to the Java Stream mapToObj lambda function.
        """
        try:
            # Create a copy of the network for each contingency
            copy_net = network.jsonCopy()
            
            # Remove the i-th branch
            if i < copy_net.getBranchList().size():
                index = jpype.JInt(i)
                branch = copy_net.getBranchList().get(index)
                branch.setStatus(False)
                branch_id = str(branch.getId())
                
                # Create a new algorithm instance for each thread to avoid conflicts
                parallel_algo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(copy_net)

                #parallel_algo.getLfAdjAlgo().getVoltAdjConfig().setCheckGenQLimitImmediate(False)
                
                # Configure algorithm (equivalent to configureAlgorithm method)
                parallel_algo.setLfMethod(config['lf_method'])
                if not config['apply_adjust_algo']:
                    AclfAdjCtrlFunction.disableAllAdjControls.accept(parallel_algo)
                parallel_algo.setMaxIterations(config['max_iterations'])
                parallel_algo.setTolerance(config['tolerance'])
                
                is_converged = bool(parallel_algo.loadflow())
                
                # Store result in thread-safe map (equivalent to Java ConcurrentHashMap.put)
                with results_lock:
                    convergence_results[branch_id] = is_converged
                
                return is_converged
            else:
                print(f"Warning: Contingency index {i} exceeds branch list size {copy_net.getBranchList().size()}")
                return False
                
        except Exception as e:
            print(f"Error processing contingency {i}: {str(e)}")
            return False
    
    # Equivalent to Java Stream processing
    total_success_count = 0
    
    if use_parallel:
        # Parallel processing using ThreadPoolExecutor (mimics Java parallel stream)
        max_workers = min(32, (os.cpu_count() or 1) + 4)  # Similar to ForkJoinPool default
        
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            # Submit all tasks (equivalent to IntStream.range(0, totalCases).parallel())
            future_to_index = {executor.submit(process_contingency, i): i 
                             for i in range(total_cases)}
            
            # Collect results as they complete (equivalent to .mapToLong(converged -> converged ? 1 : 0).sum())
            for future in as_completed(future_to_index):
                try:
                    result = future.result()
                    if result:
                        total_success_count += 1
                except Exception as exc:
                    index = future_to_index[future]
                    print(f'Contingency {index} generated an exception: {exc}')
    else:
        # Sequential processing
        for i in range(total_cases):
            result = process_contingency(i)
            if result:
                total_success_count += 1
    
    end_time = time.time()
    execution_time = end_time - start_time
    
    print("Contingency analysis completed!")
    print(f"Total time: {execution_time:.3f} seconds")
    print(f"Total successful contingencies: {total_success_count} out of {total_cases}")
    success_rate = (total_success_count / total_cases) * 100 if total_cases > 0 else 0
    print(f"Success rate: {success_rate:.2f}%")
    
    return {
        'convergence_results': convergence_results,
        'total_success_count': total_success_count,
        'total_cases': total_cases,
        'execution_time_seconds': execution_time,
        'success_rate': success_rate
    }





# Load PSSE RAW file - use Path for cross-platform path handling
adapter = PSSERawAdapter(PsseVersion.PSSE_33)
raw_path = str(parent_folder / "testData" / "psse" / "ACTIVSg25k.RAW")
print(f"Loading file: {raw_path}")

# Check if the raw file exists
if not Path(raw_path).exists():
    raise FileNotFoundError(f"RAW file not found: {raw_path}")

adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

# Verify base case convergence
algo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(net)
AclfAdjCtrlFunction.disableAllAdjControls.accept(algo)
algo.getLfAdjAlgo().getVoltAdjConfig().setCheckGenQLimitImmediate(False)
algo.setTolerance(0.001)
base_converged = algo.loadflow()

print(f"Network loaded successfully!")
print(f"Buses: {net.getNoBus()}, Branches: {net.getNoBranch()}")
print(f"Active buses: {net.getNoActiveBus()}, Active branches: {net.getNoActiveBranch()}")
print(f"Base case converged: {base_converged}")

if not base_converged:
    print("Base case did not converge! Aborting contingency analysis.")
    jpype.shutdownJVM()
    exit(1)

# Test parameters
total_contingencies = 2

print("\n" + "="*70)
print("CONTINGENCY ANALYSIS COMPARISON: Java Streams vs Python Threading")
print("="*70)

# # Method 1: Use Java ParallelContingencyAnalyzer (most efficient)
# print("\n--- Method 1: Java ParallelContingencyAnalyzer (Optimized) ---")

# print("Sequential processing...")
# result1_seq = analyze_contingencies_java_wrapper(net, total_contingencies, use_parallel=False)

# print("Parallel processing...")
# result1_par = analyze_contingencies_java_wrapper(net, total_contingencies, use_parallel=True)

# print(f"\nJava Results:")
# print(f"  Sequential: {result1_seq['execution_time_seconds']:.3f}s, Success: {result1_seq['success_rate']:.2f}%")
# print(f"  Parallel: {result1_par['execution_time_seconds']:.3f}s, Success: {result1_par['success_rate']:.2f}%")
# speedup1 = result1_seq['execution_time_seconds'] / result1_par['execution_time_seconds']
# print(f"  Speedup: {speedup1:.2f}x")

# Method 2: Python implementation mimicking Java Streams
print("\n--- Method 2: Python with ThreadPoolExecutor (Java Stream-like) ---")

print("Sequential processing...")
result2_seq = analyze_contingencies_threadpool(net, total_contingencies, use_parallel=False)

print("Parallel processing...")
result2_par = analyze_contingencies_threadpool(net, total_contingencies, use_parallel=True)

print(f"\nPython Results:")
print(f"  Sequential: {result2_seq['execution_time_seconds']:.3f}s, Success: {result2_seq['success_rate']:.2f}%")
print(f"  Parallel: {result2_par['execution_time_seconds']:.3f}s, Success: {result2_par['success_rate']:.2f}%")
speedup2 = result2_seq['execution_time_seconds'] / result2_par['execution_time_seconds']
print(f"  Speedup: {speedup2:.2f}x")



# Shutdown JVM
jpype.shutdownJVM()
