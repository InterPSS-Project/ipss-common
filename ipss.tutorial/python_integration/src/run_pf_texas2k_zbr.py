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
CoreObjectFactory = jpype.JClass("com.interpss.core.CoreObjectFactory")
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

def is_small_z_branch_connected(bus_id, net, small_z):
        """
        Check if a bus is connected to a branch with small impedance
        
        Args:
            bus_id: Bus ID string
            net: AclfNetwork object
            small_z: Threshold for small impedance
            
        Returns:
            bool: True if connected to small Z branch
        """
        has_small_z_branch = False
        b = net.getBus(bus_id)
        for bra in b.getBranchList():
            if bra.isActive() and isinstance(bra, AclfBranch):
                if bra.getZ().abs() <= small_z:
                    has_small_z_branch = True
                    print(f"Branch: {bra.getId()}, isXfr? {bra.isXfr()}, original Z: {bra.getZ().abs()} pu")
        return has_small_z_branch

def find_small_z_branch(self, net, small_z, fix_z=False):
        """
        Find branches with small impedance and optionally fix them
        
        Args:
            net: AclfNetwork object
            small_z: Threshold for small impedance
            fix_z: Boolean to control whether to fix small Z branches
            
        Returns:
            bool: True if small Z branches were found
        """
        small_z_bra = False
        from org.apache.commons.math3.complex import Complex
        
        for bra in net.getBranchList():
            if bra.isActive():
                if bra.getZ().abs() < small_z:
                    small_z_bra = True
                    print(f"Branch: {bra.getId()}  Z: {bra.getZ().abs()} pu")
                    if fix_z:
                        print(f"Branch: {bra.getId()} original Z: {bra.getZ().abs()} pu, setting to {small_z} pu")
                        bra.setZ(Complex(0, small_z))
        return small_z_bra

# Create instances of the classes we are going to use
IpssCorePlugin.init()
IpssLogger.getLogger().setLevel(Level.INFO)
ODMLogger.getLogger().setLevel(Level.INFO)
adapter = PSSERawAdapter(PsseVersion.PSSE_35)

# Use platform-independent path handling for test data
raw_path = str(parent_folder / "testData" / "psse" / "Texas2k" / "Texas2k_series24_case1_2016summerPeak_zbr_v35.RAW")
adapter.parseInputFile(raw_path)
net = ODMAclfParserMapper().map2Model(adapter.getModel()).getAclfNet()

algo = CoreObjectFactory.createLoadflowAlgorithm(net)
# the following two settings are false by default, but they are critical for some real-world networks due to data quality issues
algo.getDataCheckConfig().setTurnOffIslandBus(True)
algo.getDataCheckConfig().setAutoTurnLine2Xfr(True)

#branch_with_errors = [(1001,1064,"1")]
branch_with_errors = [(301018,304864,"1"), (255204,255205,"3"), (270678,274656,"1W"), (208014,208054,"1"),(113347,113384,"1")]
for i,j, idx in branch_with_errors:
    # Get the branch between buses using net.getBranch()
    branch = net.getBranch(f"Bus{i}", f"Bus{j}", idx)

    # Check if branch exists and print properties
    if branch is not None:
        print(f"Branch ID: {branch.getId()} Z: {branch.getZ().abs()} pu, "
            f"fromShuntY: {branch.getFromShuntY().abs()} pu, "
            f"halfShuntY: {branch.getHShuntY().abs()} pu")
    else:
        print(f"Branch not found between Bus{i} and Bus{j} with ID: {idx}")
# check the power flow mismatch with the imported data to find potential data issues
# if any bus has a mismatch larger than mismatch_threshold, check if it is connected to a branch with small impedance

zero_branch_threshold = 0.0002
mismatch_threshold = 10.0
# Check bus mismatches, if any bus has a mismatch larger than mismatch_threshold, check if it is connected to a branch with small impedance
mismatch_table = {}
for bus in net.getBusList():
    mis = bus.mismatch(AclfMethodType.NR)
   
    if (bus.isActive() and mis.abs() > mismatch_threshold): 
        print(f"{bus.getId()}, mismatch = {mis}")
        mismatch_table[bus.getId()] = mis.abs()
        if not is_small_z_branch_connected(bus.getId(), net, zero_branch_threshold):
            print(f"{bus.getId()} does not connect with zero impedance branches \n{BusLfResultBusStyle.f(net, bus)}\n")
   
sorted_mismatch_table = dict(sorted(mismatch_table.items(), key=lambda item: item[1], reverse=True))
print("Sorted Mismatch Table:", sorted_mismatch_table)

# Run power flow
algo.getLfAdjAlgo().setApplyAdjustAlgo(False)
algo.setNonDivergent(True)
algo.setTolerance(0.005)
algo.loadflow()

# basic load flow results summary, showing the bus type, voltage magnitude and angle and bus net power  	
# print(AclfOutFunc.loadFlowSummary(net))

# uncomment the line below to print out more detailed power flow results in PSS/E style

# print(AclfOut_PSSE.lfResults(net,PSSEOutFormat.GUI))

# Create results directory if it doesn't exist
results_dir = Path(__file__).parent.parent / "results"
results_dir.mkdir(exist_ok=True)

results_filename = str(results_dir / "Texas2k_zbr_lf_results.txt")
output_file = open(results_filename, "w")

output_file.write(str(AclfOut_PSSE.lfResults(net, PSSEOutFormat.GUI).toString()))
output_file.close()

print(f"Detailed results saved to {results_filename}")

# Shutdown JVM
jpype.shutdownJVM()
