package org.interpss.tutorial.ch7_saa;

import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.display.AclfOutFunc.BusIdStyle;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.DclfAlgoObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.dclf.CaBranchOutageType;
import com.interpss.core.algo.dclf.CaOutageBranch;
import com.interpss.core.algo.dclf.ContingencyAnalysisAlgorithm;
import com.interpss.core.algo.dclf.DclfMethod;

public class IEEE9BusSaa {
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		// import IEEE CDF format data to create a network object
		AclfNetwork net = CorePluginFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee/009ieee.dat")
				.getAclfNet();	
	    
		// create a load flow algorithm object
		ContingencyAnalysisAlgorithm algo = DclfAlgoObjectFactory.createContingencyAnalysisAlgorithm(net);
	  	
		algo.calculateDclf(DclfMethod.INC_LOSS);
		
		algo.getDclfAlgoBranchList().forEach(outBranch -> {
			CaOutageBranch caOutBranch = DclfAlgoObjectFactory.createCaOutageBranch(outBranch, CaBranchOutageType.OPEN);
			algo.getDclfAlgoBranchList().forEach(branch -> {
				if (!branch.getId().equals(outBranch.getId()))
					try {
						double postFlow = algo.calPostOutageFlow(caOutBranch, branch);
						System.out.println("caOutBranch :" + caOutBranch.getBranch().getId() + ", branch :" + branch.getId()
								+ ", flow(pu) :" + branch.getDclfFlow() + " , postFlow(pu) :" + postFlow);
					} catch (InterpssException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			});
		});
	}

}
