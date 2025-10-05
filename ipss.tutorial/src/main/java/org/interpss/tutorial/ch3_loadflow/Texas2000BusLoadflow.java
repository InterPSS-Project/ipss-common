package org.interpss.tutorial.ch3_loadflow;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.raw.PSSERawAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.odm.mapper.ODMAclfParserMapper;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.LoadflowAlgoObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Texas2000BusLoadflow {
	
	public static void main(String[] args) throws InterpssException, IOException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();

		Logger logger = IpssLogger.getLogger();
		logger.setLevel(Level.ALL);

		// Configure file logging with appropriate exception handling
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler("ipss-common/ipss.tutorial/output/mylog.log", false); // true = append mode
			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setLevel(Level.ALL);
			
			// Add handler to the logger
			logger.setUseParentHandlers(false); // Prevent logging to console
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			// Fall back to console logging if file logging fails
			logger.setUseParentHandlers(true);
			System.err.println("Warning: Could not set up file logging: " + e.getMessage());
			e.printStackTrace();
		}
	
		PSSEAdapter adapter = new PSSERawAdapter(PsseVersion.PSSE_35);
		adapter.parseInputFile("ipss-common/ipss.tutorial/testData/psse/Texas2k/Texas2k_series24_case1_2016summerPeak_v35.RAW");
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
	  	
	  		
		//output load flow summary result
		//System.out.println(AclfOutFunc.loadFlowSummary(net));
		
		//BusStyle output provides bus generation and load, as well as branch power flow info
		//System.out.println(AclfOut_BusStyle.lfResultsBusStyle(net, BusIdStyle.BusId_No));
	}

}
