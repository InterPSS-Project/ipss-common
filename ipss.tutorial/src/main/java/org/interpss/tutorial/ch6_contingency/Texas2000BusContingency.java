package org.interpss.tutorial.ch6_contingency;


import java.util.stream.IntStream;

import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.raw.PSSERawAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.odm.mapper.ODMAclfParserMapper;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.LoadflowAlgoObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethodType;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.NrMethodConfig;
import com.interpss.core.algo.NrOptimizeAlgoType;
import com.interpss.core.funcImpl.AclfAdjCtrlFunction;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Texas2000BusContingency {
	
	public static void main(String[] args) throws InterpssException {
	
		PSSEAdapter adapter = new PSSERawAdapter(PsseVersion.PSSE_30);
		adapter.parseInputFile("ipss-common/ipss.tutorial/testData/psse/Texas2k/Texas2k_series24_case1_2016summerPeak_v30.RAW");
		AclfModelParser parser =(AclfModelParser) adapter.getModel();
		
		//System.out.println(parser.toXmlDoc());

		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		
		if (!new ODMAclfParserMapper().map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
		}
	    
		AclfNetwork net =simuCtx.getAclfNet();
		//create a load flow algorithm object
	  	LoadflowAlgorithm algo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(net);
	  	 
	  	// disable all the controls
		AclfAdjCtrlFunction.disableAllAdjControls.accept(algo);
		
		//algo.getNrMethodConfig().setNonDivergent(true);
		NrMethodConfig config = algo.getNrMethodConfig();
	  	//config.setNonDivergent(true);
	  	config.setOptAlgo(NrOptimizeAlgoType.CUBIC_EQN_STEP_SIZE);
	  	// re-configure the Nr solver with the updated config
	  	algo.getLfCalculator().getNrSolver().reConfigSolver(config);

	  	algo.loadflow();

		// run aclf contingency analysis by iterating through all branches
		
		//time the following code block
		long startTime1 = System.currentTimeMillis();

		// AclfNetwork copyNet = net.jsonCopy();

		// AclfNetObjectComparator comparator = new AclfNetObjectComparator(net, copyNet);
		// comparator.compareNetwork();
		// System.out.println("Network comparison result: " + comparator.getDiffMsgList());
        
		//System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
	
		int totalCnt = 50;

		
		// turn off the parallel processing
		long totalSuccessCount = IntStream.range(0, totalCnt)
			//.parallel()
			.mapToObj(i -> {
				try {
					AclfNetwork copyNet = net.jsonCopy();
					copyNet.getBranchList().get(i).setStatus(false);

					// Create a new algorithm instance for each thread to avoid conflicts
					LoadflowAlgorithm parallelAlgo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(copyNet);
					parallelAlgo.getDataCheckConfig().setTurnOffIslandBus(true);
					parallelAlgo.getDataCheckConfig().setAutoTurnLine2Xfr(true);
					parallelAlgo.setLfMethod(AclfMethodType.NR);

					AclfAdjCtrlFunction.disableAllAdjControls.accept(parallelAlgo);

					parallelAlgo.getNrMethodConfig().setNonDivergent(true);
					parallelAlgo.setMaxIterations(50);
					parallelAlgo.setTolerance(0.005);

					boolean isConverged = parallelAlgo.loadflow();
					
					// Thread-safe printing with synchronization
					// synchronized(System.out) {
					// 	System.out.println("index: " + i + ", Load flow converged? " + isConverged);
					// }
					
					return isConverged;
				} catch (Exception e) {
					synchronized(System.err) {
						System.err.println("Error processing contingency " + i + ": " + e.getMessage());
					}
					return false;
				}
			})
			.mapToLong(converged -> converged ? 1 : 0)
			.sum();
	  	
	  	// // end time
		long endTime1 = System.currentTimeMillis();
		System.out.println("End time: " + endTime1);
		System.out.println("Total time: " + (endTime1 - startTime1)/1000.0 + " seconds");
		System.out.println("Total successful contingencies: " + totalSuccessCount +" out of "+ totalCnt);
	}

}
