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
LoadflowAlgoObjectFactory = jpype.JClass("com.interpss.core.LoadflowAlgoObjectFactory")
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
