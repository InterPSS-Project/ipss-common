package org.interpss.tutorial.ch3_loadflow;


import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.raw.PSSERawAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.display.AclfOutFunc.BusIdStyle;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.odm.mapper.ODMAclfParserMapper;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Texas2000BusLoadflow {
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
	
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
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
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
		System.out.println(AclfOutFunc.loadFlowSummary(net));
		
		//BusStyle output provides bus generation and load, as well as branch power flow info
		System.out.println(AclfOut_BusStyle.lfResultsBusStyle(net, BusIdStyle.BusId_No));
	}

}
