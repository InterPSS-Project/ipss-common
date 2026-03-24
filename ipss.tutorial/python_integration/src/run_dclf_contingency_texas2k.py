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

# Import necessary Java classes
IpssAdapter = jpype.JClass("org.interpss.plugin.pssl.plugin.IpssAdapter")
PsseVersion = jpype.JClass("org.interpss.plugin.pssl.plugin.IpssAdapter$PsseVersion")
FileFormat = jpype.JClass("org.interpss.plugin.pssl.plugin.IpssAdapter$FileFormat")

ContingencyFileUtil = jpype.JClass("org.interpss.plugin.contingency.util.ContingencyFileUtil")
DclfContingencyConfig = jpype.JClass("org.interpss.plugin.contingency.DclfContingencyConfig")
ParallelDclfContingencyAnalyzer = jpype.JClass("org.interpss.plugin.contingency.ParallelDclfContingencyAnalyzer")

DclfAlgoObjectFactory = jpype.JClass("com.interpss.core.DclfAlgoObjectFactory")
ContBranchOutageType = jpype.JClass("com.interpss.core.contingency.ContingencyBranchOutageType")
DclfMethod = jpype.JClass("com.interpss.core.algo.dclf.DclfMethod")

File = jpype.JClass("java.io.File")
ArrayList = jpype.JClass("java.util.ArrayList")
HashSet = jpype.JClass("java.util.HashSet")


# Use platform-independent path for the test data
raw_path = str(parent_folder  / "testData" / "psse" / "Texas2k" /  "Texas2k_series24_case1_2016summerPeak_v36.RAW")
print(f"Loading network from: {raw_path}")

# Import PSSE network using IpssAdapter
net = IpssAdapter.importAclfNet(raw_path) \
    .setFormat(FileFormat.PSSE) \
    .psseVersion(PsseVersion.PSSE_36) \
    .load() \
    .getImportedObj()

print(f"Network loaded: {net.getBusList().size()} buses")

# Run DCLF
algo = DclfAlgoObjectFactory.createContingencyAnalysisAlgorithm(net)
algo.calculateDclf(DclfMethod.INC_LOSS)
print("DCLF calculation completed")

# Import contingency definitions from JSON file
cont_file_path = str(parent_folder/ "testData" / "psse" / "Texas2k" / "2k_contingencies_115kVAbove.json")
cont_file = File(cont_file_path)
contingencies = ContingencyFileUtil.importContingenciesFromJson(cont_file)
print(f"Loaded {contingencies.size()} contingencies from JSON")

# Create contingency list
cont_list = ArrayList()

for record in contingencies:
    try:
        # Build branch ID
        branch_id = f"{record.fromBus}->{record.toBus}({record.ckt})"
        
        if net.getBranch(branch_id) is not None:
            cont = DclfAlgoObjectFactory.createContingency(record.name)
            
            # Determine outage type based on action type
            action_type = str(record.actionType).lower()
            if action_type == "open":
                outage_type = ContBranchOutageType.OPEN
            elif action_type == "close":
                outage_type = ContBranchOutageType.CLOSE
            else:
                outage_type = ContBranchOutageType.OPEN  # Default to open
            
            outage = DclfAlgoObjectFactory.createCaOutageBranch(
                algo.getDclfAlgoBranch(branch_id), 
                outage_type
            )
            cont.setOutageBranch(outage)
            cont_list.add(cont)
    except Exception as ex:
        print(f"Warning: Could not create contingency for {record.name}: {ex}")

print(f"Created {cont_list.size()} contingencies")

# Import monitored branches from JSON file
mon_file_path = str(parent_folder / "testData" / "psse" / "Texas2k" / "2k_monitored_branches.json")
mon_file = File(mon_file_path)
monitored_branches = ContingencyFileUtil.importMonitoredBranchRecordsFromJson(mon_file)
print(f"Loaded {monitored_branches.size()} monitored branches from JSON")

# Create set of monitored branch IDs
monitored_branch_ids = HashSet()
for record in monitored_branches:
    monitored_branch_ids.add(record.getBranchId())

# Define contingency analysis configuration
config = DclfContingencyConfig()
config.setDclfInclLoss(True)
config.setOverloadThreshold(100)  # in percentage

print("Starting parallel contingency analysis...")

# Execute parallel contingency analysis
results = ParallelDclfContingencyAnalyzer.executeContingencyAnalysis(
    net, 
    cont_list, 
    monitored_branch_ids, 
    config, 
    4  # parallelism level
)

print(f"\nContingency analysis completed. Found {results.size()} results:\n")

# Print results
violation_count = 0
for rec in results:
    branch_id = rec.getBranchId()
    contingency_name = rec.getContingencyName()
    post_flow_mw = rec.getPostFlowMW()
    line_rating_mw = rec.getLineRatingMW()
    loading_percent = rec.getLoadingPercent()
    
    print(f"{{\n  \"branch_id\": \"{branch_id}\",")
    print(f"  \"contingency_name\": \"{contingency_name}\",")
    print(f"  \"post_flow_mw\": {post_flow_mw:.2f},")
    print(f"  \"line_rating_mw\": {line_rating_mw:.2f},")
    print(f"  \"loading_percent\": {loading_percent:.2f}")
    print(f"}}")
    violation_count += 1

print(f"\nTotal violations: {violation_count}")

# Run GSF analysis for violated branches
print("\nRunning GSF analysis for violated branches...")
gsf_threshold = 0.05  # only print GSF values above this threshold

for result_rec in results:
    monitored_branch = net.getBranch(result_rec.getBranchId())
    print(f"\nGSF Analysis for {result_rec.getBranchId()} under contingency {result_rec.getContingencyName()}:")
    
    gsf_count = 0
    for bus in net.getBusList():
        if bus.isActive() and (bus.isGenPV() or bus.isGenPQ()):
            gsf = algo.calGenShiftFactor(bus.getId(), monitored_branch)
            if abs(gsf) > gsf_threshold:
                print(f"   GSF Gen@{bus.getId()} on Branch {result_rec.getBranchId()}: {gsf:.4f}")
                gsf_count += 1
    
    if gsf_count == 0:
        print(f"   No significant GSFs (threshold: {gsf_threshold})")
    
    # Limit GSF analysis output for large result sets
    if violation_count > 10:
        print(f"\n(Showing GSF for first result only due to large result set)")
        break

print("\nAnalysis completed successfully")

# Shutdown JVM
jpype.shutdownJVM()
