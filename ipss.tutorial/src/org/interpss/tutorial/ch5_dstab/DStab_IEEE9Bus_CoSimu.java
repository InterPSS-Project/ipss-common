package org.interpss.tutorial.ch5_dstab;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.mapper.odm.ODMDStabParserMapper;
import org.junit.Test;

import com.interpss.common.CoreCommonFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.BaseDStabNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DStab_IEEE9Bus_CoSimu {
	IPSSMsgHub msg = CoreCommonFactory.getIpssMsgHub();
	@Test
	public void test_IEEE9Bus_Dstab(){
		IpssCorePlugin.init();
		PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
		assertTrue(adapter.parseInputFile(NetType.DStabNet, new String[]{
				"testData/psse/IEEE9Bus/ieee9.raw",
				"testData/psse/IEEE9Bus/ieee9.seq",
				"testData/psse/IEEE9Bus/ieee9_dyn_onlyGen.dyr"
		}));
		DStabModelParser parser =(DStabModelParser) adapter.getModel();
		
		//System.out.println(parser.toXmlDoc());

		
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET);
		if (!new ODMDStabParserMapper(msg)
					.map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			return;
		}
		
		
	    BaseDStabNetwork<?,?> dsNet =simuCtx.getDStabilityNet();
	    //System.out.println(dsNet.net2String());
	    
		DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
		LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
		try {
			assertTrue(aclfAlgo.loadflow());
		} catch (InterpssException e) {
			e.printStackTrace();
		}
		
		System.out.println(AclfOutFunc.loadFlowSummary(dsNet));
		
		dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
		dstabAlgo.setSimuStepSec(0.005);
		dstabAlgo.setTotalSimuTimeSec(0.05);
		dstabAlgo.setRefMachine(dsNet.getMachine("Bus1-mach1"));
		
		IpssLogger.getLogger().setLevel(Level.INFO);
		
		MySimulator mySimu = new MySimulator();
		
		mySimu.initialization();
		
		if (dstabAlgo.initialization()) {

			System.out.println("Running DStab simulation ...");
			
			for (double t =0; t<dstabAlgo.getTotalSimuTimeSec(); t+=dstabAlgo.getSimuStepSec()){
				System.out.println("\nSimu Time: " + t);
				
				/*
				 * performance simulation of the MySimulator.
				 */
				mySimu.performSimulation(t);
				
				/*
				 * update the DStabilityNetwork object based on the MySimulator simulation
				 * results
				 */
				this.updateDStabilityNet(dsNet, mySimu);
				
				/*
				 * performance simulation of the DStabilityNetwork
				 */
				System.out.println("Perform simulaiton of InterPSS");
				dstabAlgo.solveDEqnStep(true);

				/*
				 * update the MySimulator object based on the InterPSS simulation
				 * results
				 */
			    this.updateMySimu(mySimu, dsNet);
			}
		}
	}
	
	private void updateDStabilityNet(BaseDStabNetwork<?,?> dsNet, MySimulator mySimu) {
		System.out.println("Update DStabilityNetwork object, if necessory");
	}

	private void updateMySimu(MySimulator mySimu, BaseDStabNetwork<?,?> dsNet) {
		System.out.println("Update MySimulator object, if necessory");
	}
}

class MySimulator {
	public void initialization() {
		System.out.println("Initialize MySimulator object");
	}
	
	public void performSimulation(double t) {
		System.out.println("Perform simulaiton of MySimulator");
	}
}
