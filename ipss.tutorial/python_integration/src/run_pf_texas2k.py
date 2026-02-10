##
# Acknowledgement:
# The synthetic Texas electric grid test case used in this example is provided by Texas A&M University’s energy and power group researchers.
# https://electricgrids.engr.tamu.edu/
#  
# For details of the Texas 2000-bus synthetic grid, please refer to the following references:
#  [1] A. B. Birchfield; T. Xu; K. M. Gegner; K. S. Shetye; T. J. Overbye, “Grid Structural Characteristics as Validation Criteria for Synthetic Networks,”  in IEEE Transactions on Power Systems, vol. 32, no. 4, pp. 3258-3265, July 2017.
#  [2] A. B. Birchfield; K. M. Gegner; T. Xu; K. S. Shetye; T. J. Overbye, “Statistical Considerations in the Creation of Realistic Synthetic PowerGrids for Geomagnetic Disturbance Studies,” in IEEE Transactions on Power Systems, vol. 32, no. 2, pp. 1502-1510, March 2017.
#  [3] K. M. Gegner; A. B. Birchfield; T. Xu; K. S. Shetye; T. J. Overbye, “A methodology for the creation of geographically realistic synthetic powerflow models,” 2016 IEEE Power and Energy Conference at Illinois (PECI), Urbana, IL, 2016, pp. 1-6.
#

import jpype
import jpype.imports
from jpype.types import *
from pathlib import Path
import os

# Get parent folder in a platform-independent way
parent_folder =  Path.cwd().parent.parent
print(f"Parent folder: {parent_folder}")

# Let jpype find the JVM automatically
jvm_path = jpype.getDefaultJVMPath()

# Use platform-independent path joining
jar_path = str(Path(__file__).parent.parent / "lib" / "ipss_runnable.jar")
print(f"JAR path: {jar_path}")

# Start JVM with proper path separators
jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")

IpssCorePlugin = jpype.JClass("org.interpss.IpssCorePlugin")
LoadflowAlgoObjectFactory = jpype.JClass("com.interpss.core.LoadflowAlgoObjectFactory")
AclfOutFunc = jpype.JClass("org.interpss.display.AclfOutFunc")
AclfOut_PSSE = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE")
PSSEOutFormat = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE.Format")
PSSERawAdapter = jpype.JClass("org.ieee.odm.adapter.psse.raw.PSSERawAdapter")
ODMAclfParserMapper = jpype.JClass("org.interpss.odm.mapper.ODMAclfParserMapper")
NetType = jpype.JClass("org.ieee.odm.adapter.IODMAdapter.NetType")
PsseVersion = jpype.JClass("org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion")

# another way to import the necessary classes
from com.interpss.common.exp import InterpssException
from com.interpss.core.aclf import AclfBranch
from com.interpss.core.aclf import AclfNetwork
from com.interpss.core.aclf import AclfBus
from com.interpss.common.util import IpssLogger
from org.ieee.odm.common import ODMLogger
from java.util.logging import Level
from com.interpss.core.algo import AclfMethodType
from org.interpss.CorePluginFunction import BusLfResultBusStyle
from org.interpss.IpssCorePlugin import init as ipss_init


# Create instances of the classes we are going to use
IpssCorePlugin.init()
IpssLogger.getLogger().setLevel(Level.INFO)
ODMLogger.getLogger().setLevel(Level.INFO)
adapter = PSSERawAdapter(PsseVersion.PSSE_35)

# Use platform-independent path handling for test data
raw_path = str(parent_folder / "testData" / "psse" / "Texas2k" / "Texas2k_series24_case1_2016summerPeak_v35.RAW")
adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

algo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(net)
# the following two settings are false by default, but they are critical for some real-world networks due to data quality issues
algo.getDataCheckConfig().setTurnOffIslandBus(True)
algo.getDataCheckConfig().setAutoTurnLine2Xfr(True)

# Run power flow
algo.getLfAdjAlgo().setApplyAdjustAlgo(False)
algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
# print(AclfOutFunc.loadFlowSummary(net))

# uncomment the line below to print out more detailed power flow results in PSS/E style

# print(AclfOut_PSSE.lfResults(net,PSSEOutFormat.GUI))

# Create results directory if it doesn't exist
results_dir = Path(__file__).parent.parent / "results"
results_dir.mkdir(exist_ok=True)

results_filename = str(results_dir / "Texas2k_lf_results.txt")
output_file = open(results_filename, "w")

output_file.write(str(AclfOut_PSSE.lfResults(net, PSSEOutFormat.GUI).toString()))
output_file.close()

print(f"Detailed results saved to {results_filename}")

# Shutdown JVM
jpype.shutdownJVM()
