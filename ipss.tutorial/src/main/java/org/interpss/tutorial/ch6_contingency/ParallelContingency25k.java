package org.interpss.tutorial.ch6_contingency;

import java.util.logging.Level;

import org.interpss.IpssCorePlugin;
import org.interpss.plugin.contingency.ParallelContingencyAnalyzer;
import org.interpss.plugin.pssl.plugin.IpssAdapter;
import static org.interpss.plugin.pssl.plugin.IpssAdapter.FileFormat.PSSE;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

/**
 * Extended Parallel Contingency Analysis Test with ACTIVSg25k bus system.
 * This test demonstrates large-scale contingency analysis using parallel processing.
 */
public class ParallelContingency25k {
    
    public static void main(String[] args) {
        try {
            // Initialize InterPSS
            IpssCorePlugin.init();
            IpssLogger.getLogger().setLevel(Level.INFO);
            
            System.out.println("=== Large-Scale Parallel Contingency Analysis Test ===");
            System.out.println("Testing with ACTIVSg25k bus system (25,000+ buses)");
            System.out.println("============================================================");
            
            // Load the ACTIVSg25k network
            long loadStartTime = System.currentTimeMillis();
            System.out.println("Loading ACTIVSg25k network...");

            AclfNetwork net = IpssAdapter.importAclfNet("ipss-common/ipss.tutorial/testData/psse/ACTIVSg25k.RAW")
                    .setFormat(PSSE)
                    .setPsseVersion(IpssAdapter.PsseVersion.PSSE_33) 
                    .load()
                    .getImportedObj();
            
            long loadEndTime = System.currentTimeMillis();
            System.out.println("Network loaded successfully in " + (loadEndTime - loadStartTime) + " ms");
            System.out.println("Network statistics:");
            System.out.println("  Total buses: " + net.getNoBus());
            System.out.println("  Total branches: " + net.getNoBranch());
            System.out.println("  Active buses: " + net.getNoActiveBus());
            System.out.println("  Active branches: " + net.getNoActiveBranch());
            
            // Verify base case load flow convergence
            System.out.println("\nVerifying base case load flow...");
            LoadflowAlgorithm baseAlgo = CoreObjectFactory.createLoadflowAlgorithm(net);
            baseAlgo.getDataCheckConfig().setTurnOffIslandBus(true);
            baseAlgo.getDataCheckConfig().setAutoTurnLine2Xfr(true);
            baseAlgo.getLfAdjAlgo().setApplyAdjustAlgo(false);
            baseAlgo.setTolerance(1.0E-6);
            
            long baseLoadflowStart = System.currentTimeMillis();
            boolean baseConverged = baseAlgo.loadflow();
            long baseLoadflowEnd = System.currentTimeMillis();
            
            System.out.println("Base case converged: " + baseConverged + " in " + 
                             (baseLoadflowEnd - baseLoadflowStart) + " ms");
            
            if (!baseConverged) {
                System.err.println("Base case did not converge! Aborting contingency analysis.");
                return;
            }
            
            // Configure contingency analysis
            ParallelContingencyAnalyzer.ContingencyConfig config = 
                ParallelContingencyAnalyzer.createDefaultConfig();
            
            // Use more relaxed settings for large system
            config.setMaxIterations(50);
            config.setTolerance(0.001);  // Slightly relaxed tolerance
            config.setTurnOffIslandBus(true);
            config.setAutoTurnLine2Xfr(true);
            config.setApplyAdjustAlgo(false);
            config.setNonDivergent(true);
            
            System.out.println("\nContingency analysis configuration:");
            System.out.println("  Max Iterations: " + config.getMaxIterations());
            System.out.println("  Tolerance: " + config.getTolerance());
            System.out.println("  Load Flow Method: " + config.getLfMethod());
            System.out.println("  Non-divergent: " + config.isNonDivergent());
            
            // Test different contingency sizes
            int[] contingencySizes = {20};
            
            for (int contingencyCount : contingencySizes) {
                if (contingencyCount > net.getNoActiveBranch()) {
                    System.out.println("\nSkipping " + contingencyCount + 
                                     " contingencies (exceeds branch count)");
                    continue;
                }
                
                System.out.println("\n==================================================");
                System.out.println("TESTING " + contingencyCount + " CONTINGENCIES");
                System.out.println("==================================================");
                
                // Sequential analysis
                System.out.println("\n--- Sequential Analysis ---");
                ParallelContingencyAnalyzer.ContingencyResult sequentialResult = 
                    ParallelContingencyAnalyzer.analyzeContingencies(
                        net, contingencyCount, config, false);
                
                // Parallel analysis
                System.out.println("\n--- Parallel Analysis ---");
                ParallelContingencyAnalyzer.ContingencyResult parallelResult = 
                    ParallelContingencyAnalyzer.analyzeContingencies(
                        net, contingencyCount, config, true);
                
                // Compare results
                printComparison(contingencyCount, sequentialResult, parallelResult);
                
                // Verify consistency
                verifyResultConsistency(sequentialResult, parallelResult);
            }
        
            
        } catch (Exception e) {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Print comparison between sequential and parallel results
     */
    private static void printComparison(int contingencyCount, 
                                      ParallelContingencyAnalyzer.ContingencyResult sequential,
                                      ParallelContingencyAnalyzer.ContingencyResult parallel) {
        
        System.out.println("\n--- PERFORMANCE COMPARISON ---");
        System.out.println(String.format("Contingencies analyzed: %d", contingencyCount));
        System.out.println(String.format("Sequential time: %.3f seconds", 
                                        sequential.getExecutionTimeSeconds()));
        System.out.println(String.format("Parallel time: %.3f seconds", 
                                        parallel.getExecutionTimeSeconds()));
        
        double speedup = sequential.getExecutionTimeSeconds() / parallel.getExecutionTimeSeconds();
        System.out.println(String.format("Speedup: %.2fx", speedup));
        
        System.out.println(String.format("Sequential success rate: %.2f%% (%d/%d)", 
                                        sequential.getSuccessRate() * 100,
                                        sequential.getTotalSuccessCount(),
                                        sequential.getTotalCases()));
        System.out.println(String.format("Parallel success rate: %.2f%% (%d/%d)", 
                                        parallel.getSuccessRate() * 100,
                                        parallel.getTotalSuccessCount(),
                                        parallel.getTotalCases()));
        
        // Performance per contingency
        double seqPerContingency = sequential.getExecutionTimeMs() / (double)contingencyCount;
        double parPerContingency = parallel.getExecutionTimeMs() / (double)contingencyCount;
        System.out.println(String.format("Time per contingency - Sequential: %.2f ms, Parallel: %.2f ms", 
                                        seqPerContingency, parPerContingency));
    }
    
    /**
     * Verify that sequential and parallel results are consistent
     */
    private static void verifyResultConsistency(ParallelContingencyAnalyzer.ContingencyResult sequential,
                                              ParallelContingencyAnalyzer.ContingencyResult parallel) {
        
        int mismatches = 0;
        java.util.Map<String, Boolean> seqResults = sequential.getConvergenceResults();
        java.util.Map<String, Boolean> parResults = parallel.getConvergenceResults();
        
        for (String branchId : seqResults.keySet()) {
            if (!seqResults.get(branchId).equals(parResults.get(branchId))) {
                mismatches++;
                if (mismatches <= 5) { // Show first 5 mismatches
                    System.out.println("  Mismatch - Branch " + branchId + 
                                     ": Sequential=" + seqResults.get(branchId) + 
                                     ", Parallel=" + parResults.get(branchId));
                }
            }
        }
        
        if (mismatches == 0) {
            System.out.println("✓ Results consistency: PASS (all results match)");
        } else {
            System.out.println("⚠ Results consistency: " + mismatches + " mismatches found");
            if (mismatches > 5) {
                System.out.println("  ... and " + (mismatches - 5) + " more mismatches");
            }
        }
    }
}
