package org.interpss.tutorial.ch3_loadflow;

import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.display.AclfOutFunc.BusIdStyle;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;

public class IEEE9BusLoadFlow {
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		// import IEEE CDF format data to create a network object
		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee/009ieee.dat")
				.getAclfNet();	
	    
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
