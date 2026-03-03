package org.interpss.tutorial.ch7_saa;

import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.DclfAlgoObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.dclf.ContingencyAnalysisAlgorithm;
import com.interpss.core.algo.dclf.DclfMethod;
import com.interpss.core.contingency.ContingencyBranchOutageType;
import com.interpss.core.contingency.dclf.DclfOutageBranch;

public class IEEE9BusSaa {
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		// import IEEE CDF format data to create a network object
		AclfNetwork net = CorePluginFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("ipss.tutorial/testData/ieee/009ieee.dat")
				.getAclfNet();	
	    
		// create a load flow algorithm object
		ContingencyAnalysisAlgorithm algo = DclfAlgoObjectFactory.createContingencyAnalysisAlgorithm(net);
	  	
		algo.calculateDclf(DclfMethod.INC_LOSS);
		
		algo.getDclfAlgoBranchList().forEach(outBranch -> {
			DclfOutageBranch caOutBranch = DclfAlgoObjectFactory.createCaOutageBranch(outBranch, ContingencyBranchOutageType.OPEN);
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
