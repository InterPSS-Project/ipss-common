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

import com.interpss.CoreCommonFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.BaseDStabNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DStab_IEEE9Bus_Pause {
	IPSSMsgHub msg = CoreCommonFactory.getIpssMsgHub();
	
	@Test
	public void test_IEEE9Bus_Dstab() throws InterpssException {
		IpssCorePlugin.init();
		PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
		assertTrue(adapter.parseInputFile(NetType.DStabNet, new String[]{
				"testData/psse/IEEE9Bus/ieee9.raw",
				"testData/psse/IEEE9Bus/ieee9.seq",
				"testData/psse/IEEE9Bus/ieee9_dyn_onlyGen.dyr"
		}));
		DStabModelParser parser =(DStabModelParser) adapter.getModel();
		
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
		dstabAlgo.setTotalSimuTimeSec(1.0);
		dstabAlgo.setRefMachine(dsNet.getMachine("Bus1-mach1"));
		
		IpssLogger.getLogger().setLevel(Level.INFO);
		
		double pauseT1 = 0.2,
			   pauseT2 = 0.5;	
		
		if (dstabAlgo.initialization()) {

			System.out.println("Running DStab simulation ...");
			
			performSimuTo(dstabAlgo, pauseT1);
			
			/*
			 * Pause and update the DStabilityNetwork object
			 */
			this.updateDStabilityNet(dsNet, dstabAlgo.getSimuTime());
			
			performSimuTo(dstabAlgo, pauseT2);
			
			/*
			 * Pause and update the DStabilityNetwork object
			 */
			this.updateDStabilityNet(dsNet, dstabAlgo.getSimuTime());
			
			performSimuTo(dstabAlgo, dstabAlgo.getTotalSimuTimeSec());

			System.out.println("Completed DStab simulation, total simu time: " + dstabAlgo.getSimuTime());
		}
	}
	
	private void updateDStabilityNet(BaseDStabNetwork<?,?> dsNet, double t) {
		System.out.println("Update DStabilityNetwork object, if necessory, time:" + t);
	}
	
	private void performSimuTo(DynamicSimuAlgorithm dstabAlgo, double toTime) throws InterpssException {
		double time = dstabAlgo.getSimuTime();
		if (toTime <= time )
				throw new InterpssException("performSimuTo().toTime <= dstabAlgo.getSimuTime()");
		if (toTime > dstabAlgo.getTotalSimuTimeSec())
			throw new InterpssException("\"performSimuTo().toTime > dstabAlgo.getTotalSimuTimeSec()\"");
		for (double t = time; t< toTime; t+=dstabAlgo.getSimuStepSec()){
			dstabAlgo.solveDEqnStep(true);
		}		
	}
}
