import jpype
import os
from pathlib import Path

# Get parent folder in a cross-platform way
parent_folder = Path(__file__).resolve().parent.parent.parent
print(f"Parent folder: {parent_folder}")

# Get default JVM path
jvm_path = jpype.getDefaultJVMPath()

# Construct jar path relative to script location
jar_path = str(Path(__file__).resolve().parent.parent / "lib" / "ipss_runnable.jar")
print(f"JAR path: {jar_path}")

# Start JVM with properly formatted classpath
jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")

# Import Java classes
IpssCorePlugin = jpype.JClass("org.interpss.IpssCorePlugin")
CoreObjectFactory = jpype.JClass("com.interpss.core.CoreObjectFactory")
AclfOutFunc = jpype.JClass("org.interpss.display.AclfOutFunc")
AclfOut_PSSE = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE")
PSSEOutFormat = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE.Format")
PSSERawAdapter = jpype.JClass("org.ieee.odm.adapter.psse.raw.PSSERawAdapter")
ODMAclfParserMapper = jpype.JClass("org.interpss.odm.mapper.ODMAclfParserMapper")
NetType = jpype.JClass("org.ieee.odm.adapter.IODMAdapter.NetType")
PsseVersion = jpype.JClass("org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion")

ContingencyAnalysisAlgorithmFactory = jpype.JClass("com.interpss.core.DclfAlgoObjectFactory")
DclfAlgoObjectFactory = jpype.JClass("com.interpss.core.DclfAlgoObjectFactory")
CaBranchOutageType = jpype.JClass("com.interpss.core.algo.dclf.CaBranchOutageType")
InterpssException = jpype.JClass("com.interpss.common.exp.InterpssException")

# Initialize plugin
IpssCorePlugin.init()
adapter = PSSERawAdapter(PsseVersion.PSSE_35)

# Construct test data path in platform-independent way
raw_path = str(parent_folder / "testData" / "psse" / "Texas2k" / "Texas2k_series24_case1_2016summerPeak_v35.RAW")
print(f"RAW file path: {raw_path}")

adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

# Create algorithm
algo = ContingencyAnalysisAlgorithmFactory.createContingencyAnalysisAlgorithm(net)
algo.calculateDclf()

# Define contingencies
# Loop over contingency and compute post-outage flows
topk = 2
for outBranch in algo.getDclfAlgoBranchList():
    caOutBranch = DclfAlgoObjectFactory.createCaOutageBranch(outBranch, CaBranchOutageType.OPEN)
    i = 0
    for branch in algo.getDclfAlgoBranchList():
        if branch.getId() != outBranch.getId():
            try:
                postFlow = algo.calPostOutageFlow(caOutBranch, branch)
                print(f"caOutBranch: {caOutBranch.getBranch().getId()}, branch: {branch.getId()}, flow(pu): {branch.getDclfFlow()}, postFlow(pu): {postFlow}")
                i += 1
                if i >= topk:
                    break
            except InterpssException as e:
                print("Error during post-outage flow calculation:", e)

# Shutdown JVM
jpype.shutdownJVM()
