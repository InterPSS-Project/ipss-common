import jpype
import os
from pathlib import Path

# Get parent folder in a platform-independent way
parent_folder = Path.cwd().parent.parent
print(f"Parent folder: {parent_folder}")

# Get default JVM path
jvm_path = jpype.getDefaultJVMPath()
print(f"JVM path: {jvm_path}")

# Use platform-independent path for the JAR file
jar_path = str(Path("../lib/ipss_runnable.jar").resolve())
print(f"JAR path: {jar_path}")

# Start JVM with platform-independent classpath
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

ContingencyAnalysisAlgorithmFactory = jpype.JClass("com.interpss.core.DclfAlgoObjectFactory")
ContingencyAnalysisMonad = jpype.JClass("com.interpss.algo.parallel.ContingencyAnalysisMonad")
CaBranchOutageType = jpype.JClass("com.interpss.core.algo.dclf.CaBranchOutageType")

# create instances of the classes we are going to used
IpssCorePlugin.init()
adapter = PSSERawAdapter(PsseVersion.PSSE_35)

# Use platform-independent path for the test data
raw_path = str(parent_folder / "testData" / "psse" / "Texas2k" / "Texas2k_series24_case1_2016summerPeak_v35.RAW")
print(f"Loading data from: {raw_path}")
adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

#algo = CoreObjectFactory.createLoadflowAlgorithm(net)
#algo.getLfAdjAlgo().setApplyAdjustAlgo(False)
#algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
# print(AclfOutFunc.loadFlowSummary(net))

# Create algorithm
algo = ContingencyAnalysisAlgorithmFactory.createContingencyAnalysisAlgorithm(net)
algo.calculateDclf()

# Define contingencies
for i in range(10):
    cont = ContingencyAnalysisAlgorithmFactory.createContingency(f"contId{i}")
    branch = algo.getDclfAlgoBranch("Bus1001->Bus1064(1)")
    cont.setOutageBranch(ContingencyAnalysisAlgorithmFactory.createCaOutageBranch(branch, 
                    CaBranchOutageType.OPEN))
    
    def ca_callback(resultRec):
        branch_id = resultRec.aclfBranch.getId()
        postFlow = resultRec.getPostFlowMW()
        if branch_id == "Bus1001->Bus1064(2)":
            print(f"{cont.getId()} - {branch_id} postContFlow: {postFlow} MW")
            
        elif branch_id == "Bus1001->Bus1071(1)":
            print(f"{cont.getId()} - {branch_id} postContFlow: {postFlow} MW")
            
    
    ContingencyAnalysisMonad.of(algo, cont).ca(ca_callback)

# Shutdown JVM
jpype.shutdownJVM()
