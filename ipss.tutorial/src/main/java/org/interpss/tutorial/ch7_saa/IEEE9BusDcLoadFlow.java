package org.interpss.tutorial.ch7_saa;

import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.display.AclfOutFunc.BusIdStyle;
import org.interpss.display.DclfOutFunc;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.DclfAlgoObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.dclf.DclfAlgorithm;
import com.interpss.core.algo.dclf.DclfMethod;

public class IEEE9BusDcLoadFlow {
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		// import IEEE CDF format data to create a network object
		AclfNetwork net = CorePluginFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee/009ieee.dat")
				.getAclfNet();	
		
	    net.formB11Matrix();
	    
	    
		//output load flow summary result
		System.out.println(AclfOutFunc.loadFlowSummary(net));
		
		//create a load flow algorithm object
	  	DclfAlgorithm algo = DclfAlgoObjectFactory.createDclfAlgorithm(net);
	  	
	  	algo.calculateDclf(DclfMethod.INC_LOSS);
		
		//BusStyle output provides bus generation and load, as well as branch power flow info
		System.out.println(DclfOutFunc.dclfResults(algo, false));
	}

}
