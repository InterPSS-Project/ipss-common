import jpype
from pathlib import Path

parent_folder = Path.cwd().parent.parent
print(parent_folder)


jvm_path = jpype.getDefaultJVMPath()


jar_path = "lib/ipss_runnable.jar"


jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")


IpssCorePlugin = jpype.JClass("org.interpss.IpssCorePlugin")
CoreObjectFactory = jpype.JClass("com.interpss.core.CoreObjectFactory")
AclfOutFunc = jpype.JClass("org.interpss.display.AclfOutFunc")
AclfOut_PSSE = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE")
PSSEOutFormat = jpype.JClass("org.interpss.display.impl.AclfOut_PSSE.Format")
PSSEAdapter = jpype.JClass("org.ieee.odm.adapter.psse.PSSEAdapter")
ODMAclfParserMapper = jpype.JClass("org.interpss.mapper.odm.ODMAclfParserMapper")
NetType = jpype.JClass("org.ieee.odm.adapter.IODMAdapter.NetType")
PsseVersion = jpype.JClass("org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion")

# create instances of the classes we are going to used
IpssCorePlugin.init();
adapter = PSSEAdapter(PsseVersion.PSSE_30);
raw_path = str(parent_folder/"testData/psse/IEEE9Bus/ieee9.raw")
adapter.parseInputFile(raw_path);
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet();

algo = CoreObjectFactory.createLoadflowAlgorithm(net)

algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
print(AclfOutFunc.loadFlowSummary(net))

# print out more detailed power flow results in PSS/E style
print(AclfOut_PSSE.lfResults(net,PSSEOutFormat.GUI))

# Shutdown JVM
jpype.shutdownJVM()
