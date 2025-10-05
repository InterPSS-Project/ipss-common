package org.interpss.tutorial.ch6_contingency;


import java.util.Arrays;

import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.raw.PSSERawAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.odm.mapper.ODMAclfParserMapper;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.LoadflowAlgoObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Texas2000BusContingency {
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		IpssLogger.getLogger().setLevel(java.util.logging.Level.OFF); // set logger level to WARNING to avoid too much log output
	
		PSSEAdapter adapter = new PSSERawAdapter(PsseVersion.PSSE_30);
		adapter.parseInputFile("ipss.tutorial/testData/psse/Texas2k/Texas2k_series24_case1_2016summerPeak_v30.RAW");
		AclfModelParser parser =(AclfModelParser) adapter.getModel();
		
		//System.out.println(parser.toXmlDoc());

		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		
		if (!new ODMAclfParserMapper().map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
		}
	    
		AclfNetwork net =simuCtx.getAclfNet();
		//create a load flow algorithm object
	  	LoadflowAlgorithm algo = LoadflowAlgoObjectFactory.createLoadflowAlgorithm(net);
	  	
	  	/*
	  	 * users can also customize the configurations of the load flow algorithm
	  	 *
	  	  algo.setLfMethod(AclfMethod.PQ);
	  	  algo.setMaxIterations(20);
	  	  algo.setInitBusVoltage(false);
	  	  algo.setTurnOffIslandBus(true);
	  	 */ 
	  	 
	  	
	  	//run load flow using default setting
	  	algo.getLfAdjAlgo().setApplyAdjustAlgo(false);
	  	algo.loadflow();

		// run aclf contingency analysis by iterating through all branches
		
		// time the run time for contingency analysis
		
		long startTime = System.currentTimeMillis();
		int topK = 50;
		int i = 0;

		for(AclfBranch bra : net.getBranchList()) {
			bra.setStatus(false);
			algo.loadflow();
			// get the load flow summary result after the branch outage
			System.out.println("\n\nContingency Analysis: Branch " + bra.getId() + " Bus 1001 voltage:\n" + net.getBus("Bus1001").getVoltageMag());
			System.out.println("\n\nContingency Analysis: Branch " + bra.getId() + " Branch flow:\n" + net.getBranch("Bus1004->Bus3133(1)").powerFrom2To(UnitType.mVar));
			bra.setStatus(true);

			i++;
			if (i > topK) {
				break;
			}

		}
		long endTime = System.currentTimeMillis();
		System.out.println("\n\nContingency Analysis completed. Total branches analyzed: " + topK);
		System.out.println("\nContingency Analysis completed in " + (endTime - startTime)/1000 + " seconds\n\n");
	}

}
