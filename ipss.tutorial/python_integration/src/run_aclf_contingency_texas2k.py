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
adapter = PSSERawAdapter(PsseVersion.PSSE_35)
raw_path = str(parent_folder / "testData" / "psse" / "Texas2k" / "Texas2k_series24_case1_2016summerPeak_v35.RAW")
print(f"Loading file: {raw_path}")

# Check if the raw file exists
if not Path(raw_path).exists():
    raise FileNotFoundError(f"RAW file not found: {raw_path}")

adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

algo = CoreObjectFactory.createLoadflowAlgorithm(net)
algo.getLfAdjAlgo().setApplyAdjustAlgo(False)

# Serial contingency analysis
total_con = 50
i = 0
start_time = time.time()
for bra in net.getBranchList():
    # take one branch out for contingency analysis
    bra.setStatus(False)
    # run AC power flow
    algo.loadflow()
    print("Contingency Analysis: Branch", bra.getId(), "Bus1001 voltage:", net.getBus("Bus1001").getVoltageMag(),"Branch Bus1004->Bus3133(1) flow: ", net.getBranch("Bus1004->Bus3133(1)").powerFrom2To(UnitType.mVar))
    # reset the branch status to True for the next iteration
    bra.setStatus(True)
    i+=1
    
    if i >= total_con:
        break
end_time = time.time()
print("total contingency cases:", total_con)
print("\nContingency Analysis completed in", round(end_time - start_time, 2), "seconds\n")

# Shutdown JVM
jpype.shutdownJVM()
