import jpype
import jpype.imports
from jpype.types import *
import time
import os
from pathlib import Path

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

# Import Java classes
from org.interpss.numeric.datatype.Unit import UnitType
from org.interpss import IpssCorePlugin
from com.interpss.common.util import IpssLogger
from org.ieee.odm.adapter.psse.PSSEAdapter import PsseVersion
from org.ieee.odm.adapter.psse.raw import PSSERawAdapter
from org.interpss.odm.mapper import ODMAclfParserMapper
from com.interpss.core import CoreObjectFactory
from com.interpss.common.util import IpssLogger
from org.ieee.odm.common import ODMLogger
from java.util.logging import Level

# Initialize InterPSS
IpssCorePlugin.init()
IpssLogger.getLogger().setLevel(Level.INFO)
ODMLogger.getLogger().setLevel(Level.INFO)

# Load PSSE RAW file - use Path for cross-platform path handling
adapter = PSSERawAdapter(PsseVersion.PSSE_33)
raw_path = str(parent_folder / "testData" / "psse" / "ACTIVSg25k.RAW")
print(f"Loading file: {raw_path}")

# Check if the raw file exists
if not Path(raw_path).exists():
    raise FileNotFoundError(f"RAW file not found: {raw_path}")

adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

algo = CoreObjectFactory.createLoadflowAlgorithm(net)
# Configure data check settings
algo.getDataCheckConfig().setTurnOffIslandBus(True)
algo.getDataCheckConfig().setAutoTurnLine2Xfr(True)
algo.getLfAdjAlgo().setApplyAdjustAlgo(False)
algo.setNonDivergent(True)
algo.setTolerance(0.001)

# Serial contingency analysis
starting_idx = 0
total_con = 10
i = 0
start_time = time.time()
contResultDict = {}
for bra in net.getBranchList():
    # take one branch out for contingency analysis
    if (starting_idx >100 and bra.isActive()):
        bra.setStatus(False)
    else:
        starting_idx += 1
        continue
    
    # run AC power flow
    algo.loadflow()
    print("Contingency Analysis: Branch", bra.getId(), "out of service, power flow convergence:", net.isLfConverged())
    contResultDict[bra.getId()] = net.isLfConverged()
    # reset the branch status to True for the next iteration
    bra.setStatus(True)
    i+=1
    
    if i >= total_con:
        break

end_time = time.time()
print("total contingency cases:", total_con)
print("Contingency results:")
for bra_id, is_converged in contResultDict.items():
    print(f"Branch {bra_id}: {'Converged' if is_converged else 'Not Converged'}")

print("\nContingency Analysis completed in", round(end_time - start_time, 2), "seconds\n")

# Shutdown JVM
jpype.shutdownJVM()
