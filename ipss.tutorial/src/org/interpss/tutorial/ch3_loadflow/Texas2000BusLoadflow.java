package org.interpss.tutorial.ch3_loadflow;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.display.AclfOutFunc.BusIdStyle;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.mapper.odm.ODMAclfParserMapper;

import com.interpss.common.CoreCommonFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.BaseDStabNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Texas2000BusLoadflow {
	
	
	 
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
	
		PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
		adapter.parseInputFile("testData/psse/ACTIVSg2000/ACTIVSg2000.raw");
		AclfModelParser parser =(AclfModelParser) adapter.getModel();
		
		//System.out.println(parser.toXmlDoc());

		
		
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		
		if (!new ODMAclfParserMapper().map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			return;
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
	  	algo.loadflow();
	  	
	  		
		//output load flow summary result
		System.out.println(AclfOutFunc.loadFlowSummary(net));
		
		//BusStyle output provides bus generation and load, as well as branch power flow info
		System.out.println(AclfOut_BusStyle.lfResultsBusStyle(net, BusIdStyle.BusId_No));
	}

}
