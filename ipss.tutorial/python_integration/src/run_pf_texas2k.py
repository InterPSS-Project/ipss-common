import jpype
from pathlib import Path

parent_folder = Path.cwd().parent.parent
print(parent_folder)


jvm_path = jpype.getDefaultJVMPath()


jar_path = "../lib/ipss_runnable.jar"


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
IpssCorePlugin.init();
adapter = PSSERawAdapter(PsseVersion.PSSE_35);
raw_path = str(parent_folder/"testData/psse/Texas2k/Texas2k_series24_case1_2016summerPeak_v35.RAW")
adapter.parseInputFile(raw_path);
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet();

algo = CoreObjectFactory.createLoadflowAlgorithm(net)
algo.getLfAdjAlgo().setApplyAdjustAlgo(False);
algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
print(AclfOutFunc.loadFlowSummary(net))

# uncomment the line below to print out more detailed power flow results in PSS/E style

# print(AclfOut_PSSE.lfResults(net,PSSEOutFormat.GUI))

results_filename = "../results/Texas2k_lf_results.txt"
output_file = open(results_filename, "w")

output_file.write(str(AclfOut_PSSE.lfResults(net, PSSEOutFormat.GUI).toString()))
output_file.close()

print(f"Detailed results saved to {results_filename}")

# Shutdown JVM
jpype.shutdownJVM()
