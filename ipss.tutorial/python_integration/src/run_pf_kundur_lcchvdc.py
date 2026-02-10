import jpype
import jpype.imports
from jpype.types import *
from pathlib import Path
import os

parent_folder = Path.cwd().parent.parent
print(parent_folder)

jvm_path = jpype.getDefaultJVMPath()

# Use Path object for jar path to ensure cross-platform compatibility
jar_path = str(Path("..") / "lib" / "ipss_runnable.jar")

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


# create instances of the classes we are going to used
IpssCorePlugin.init()
IpssLogger.getLogger().setLevel(Level.INFO)
ODMLogger.getLogger().setLevel(Level.INFO)
adapter = PSSERawAdapter(PsseVersion.PSSE_33)

# Use Path object for raw_path to ensure cross-platform compatibility
raw_path = str(parent_folder / "testData" / "psse" / "Kundur_2area_LCC_HVDC_PsetOnInv.raw")
adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

algo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(net)
algo.getLfAdjAlgo().setApplyAdjustAlgo(False)
algo.loadflow()

from com.interpss.core.aclf.hvdc import HvdcLine2TLCC
for lccHVDC in net.getSpecialBranchList():
    if lccHVDC.isActive() and isinstance(lccHVDC, HvdcLine2TLCC):
        print(f"Branch {lccHVDC.getId()} is a LCC HVDC branch.")
        print(f"  From bus: {lccHVDC.getFromBus().getId()}")
        print(f"  To bus: {lccHVDC.getToBus().getId()}")
        print(f"  Power flow at Rec: {lccHVDC.getRectifier().powerIntoConverter().getReal()} + j{lccHVDC.getRectifier().powerIntoConverter().getImaginary()} pu")
        print(f"  Power flow at Inv: {lccHVDC.getInverter().powerIntoConverter().getReal()} + j{lccHVDC.getInverter().powerIntoConverter().getImaginary()} pu")

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
# print(AclfOutFunc.loadFlowSummary(net))

# uncomment the line below to print out more detailed power flow results in PSS/E style

# print(AclfOut_PSSE.lfResults(net,PSSEOutFormat.GUI))

# Create results directory if it doesn't exist
results_dir = Path("..") / "results"
results_dir.mkdir(exist_ok=True)

# Use Path object for results file to ensure cross-platform compatibility
results_filename = str(results_dir / "kundur_hvdc_results.txt")
output_file = open(results_filename, "w")

output_file.write(str(AclfOut_PSSE.lfResults(net, PSSEOutFormat.GUI).toString()))
output_file.close()

print(f"Detailed results saved to {results_filename}")

# Shutdown JVM
jpype.shutdownJVM()
