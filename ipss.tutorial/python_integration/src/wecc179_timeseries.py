import jpype
from pathlib import Path
import os

# Get script directory for reliable path resolution
script_dir = Path(__file__).resolve().parent
# Navigate to parent of parent directory (project root)
parent_folder = script_dir.parent.parent
print(f"Parent folder: {parent_folder}")

# Let jpype find the JVM automatically
jvm_path = jpype.getDefaultJVMPath()

# Use platform-independent path joining
jar_path = str(script_dir.parent / "lib" / "ipss_runnable.jar")
print(f"JAR path: {jar_path}")

# Start JVM with proper path separators
jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")

IpssCorePlugin = jpype.JClass("org.interpss.IpssCorePlugin")
CoreObjectFactory = jpype.JClass("com.interpss.core.CoreObjectFactory")
AclfOutFunc = jpype.JClass("org.interpss.display.AclfOutFunc")
AclfOut_PSSE = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE")
PSSEOutFormat = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE.Format")
PSSERawAdapter = jpype.JClass("org.ieee.odm.adapter.psse.raw.PSSERawAdapter")
ODMAclfParserMapper = jpype.JClass("org.interpss.odm.mapper.ODMAclfParserMapper")
NetType = jpype.JClass("org.ieee.odm.adapter.IODMAdapter.NetType")
PsseVersion = jpype.JClass("org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion")

# create instances of the classes we are going to used
IpssCorePlugin.init()
adapter = PSSERawAdapter(PsseVersion.PSSE_32)

# Use platform-independent path handling for test data
raw_path = str(parent_folder / "testData" / "psse" / "WECC179" / "wecc_179_v32.raw")
adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

algo = CoreObjectFactory.createLoadflowAlgorithm(net)

algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
print(AclfOutFunc.loadFlowSummary(net))

# print out more detailed power flow results in PSS/E style
# print(AclfOut_PSSE.lfResults(net, PSSEOutFormat.GUI))


## ---------------update load and generator data----------------
print("---------------update load and generator data----------------")


load_factor = 0.9
gen_factor = 0.9

for bus in net.getBusList():
    for load in bus.getContributeLoadList():
        load.setLoadCP(load.getLoadCP().multiply(load_factor)) # note: here cp means constant power load, and loadcp returns the complex (loadP, loadQ)

    for gen in bus.getContributeGenList():
        gen.setGen(gen.getGen().multiply(gen_factor)) # note:  for PV bus, the genQ is calculated by the power flow solver, so it is uncessary to set it.

algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
print(AclfOutFunc.loadFlowSummary(net))

# ------------------OUTPUT Y matrix as a parse matrix-------------------
#from org.interpss.numeric.util import MatrixOutputUtil
MatrixOutputUtil = jpype.JClass("org.interpss.numeric.util.MatrixOutputUtil")

# note the output matrix is a sparse matrix in matlab sparse matrix format, so the index starts from 1, thus index 1 means the first row/column
print(MatrixOutputUtil.matrixToString(net.formYMatrix()))

# the Y matrix is organized by the bus sort number
for bus in net.getBusList():
    print(f"Bus {bus.getId()} index: {bus.getSortNumber()}")


# Shutdown JVM
jpype.shutdownJVM()
