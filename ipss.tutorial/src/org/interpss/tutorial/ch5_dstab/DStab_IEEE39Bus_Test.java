package org.interpss.tutorial.ch5_dstab;

import static com.interpss.dstab.cache.StateVariableRecorder.StateVarRecType.MachineState;
import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.mapper.odm.ODMDStabParserMapper;
import org.interpss.numeric.NumericConstant;
import org.junit.Test;

import com.interpss.CoreCommonFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.BaseDStabBus;
import com.interpss.dstab.BaseDStabNetwork;
import com.interpss.dstab.DStabObjectFactory;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.cache.StateVariableRecorder;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.dstab.devent.DynamicSimuEvent;
import com.interpss.dstab.devent.DynamicSimuEventType;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DStab_IEEE39Bus_Test  {
	    IPSSMsgHub msg = CoreCommonFactory.getIpssMsgHub();
		
		@Test
		public void IEEE39_Dstab_benchMark(){
			IpssCorePlugin.init();
			PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
			assertTrue(adapter.parseInputFile(NetType.DStabNet, new String[]{
					"testData/psse/IEEE39Bus/IEEE39bus_v30.raw",
					"testData/psse/IEEE39Bus/IEEE39bus.dyr"
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(AclfOutFunc.loadFlowSummary(dsNet));
			
			dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
			dstabAlgo.setSimuStepSec(0.005);
			dstabAlgo.setTotalSimuTimeSec(1);
			dstabAlgo.setRefMachine(dsNet.getMachine("Bus31-mach1"));
			double[] timePoints   = {0.0,    0.04,    0.3,    0.5},
		      			 //machPePoints = {0.8330, 0.83333,   0.83333,   0.83333},
		      			 machPePoints = {0.83309, 0.83321, 0.83378, 0.83356 },
		      			 //machAngPoints  = {-8.58308, -8.58308,  -8.58308,   -8.58308},
		      			 machAngPoints  = {-8.58308, -8.58350, -8.57127, -8.56934},
		      			 machEfdPoints  = {3.06708, 3.06708,   3.06708,   3.06708};
			
			StateVariableRecorder stateTestRecorder = new StateVariableRecorder(0.0001);
			stateTestRecorder.addTestRecords("Bus30-mach1", MachineState, 
						DStabOutSymbol.OUT_SYMBOL_MACH_PE, timePoints, machPePoints);
			stateTestRecorder.addTestRecords("Bus30-mach1", MachineState, 
						DStabOutSymbol.OUT_SYMBOL_MACH_ANG, timePoints, machAngPoints);
			stateTestRecorder.addTestRecords("Bus30-mach1", MachineState, 
					DStabOutSymbol.OUT_SYMBOL_MACH_Efd, timePoints, machEfdPoints);
			dstabAlgo.setSimuOutputHandler(stateTestRecorder);
				

			if (dstabAlgo.initialization()) {
				//System.out.println(simuCtx.getDStabilityNet().net2String());

				System.out.println("Running DStab simulation ...");
				assertTrue(dstabAlgo.performSimulation());
			}
				
			assertTrue(stateTestRecorder.diffTotal("Bus30-mach1", MachineState, 
						DStabOutSymbol.OUT_SYMBOL_MACH_PE) < 0.002);
			assertTrue(stateTestRecorder.diffTotal("Bus30-mach1", MachineState, 
						DStabOutSymbol.OUT_SYMBOL_MACH_ANG) < 0.01);
			assertTrue(stateTestRecorder.diffTotal("Bus30-mach1", MachineState, 
					DStabOutSymbol.OUT_SYMBOL_MACH_Efd) < 0.001);
		}
		
		private DynamicSimuEvent create3PhaseFaultEvent(String faultBusId, DStabilityNetwork net,double startTime, double durationTime){
		       // define an event, set the event id and event type.
				DynamicSimuEvent event1 = DStabObjectFactory.createDEvent("BusFault3P@"+faultBusId, "Bus Fault 3P@"+faultBusId, 
						DynamicSimuEventType.BUS_FAULT, net);
				event1.setStartTimeSec(startTime);
				event1.setDurationSec(durationTime);
				
		      // define a bus fault
				BaseDStabBus<?,?> faultBus = net.getDStabBus(faultBusId);
				AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus Fault 3P@"+faultBusId, net, false);
		  		fault.setBus(faultBus);
				fault.setFaultCode(SimpleFaultCode.GROUND_3P);
				fault.setZLGFault(NumericConstant.SmallScZ);

		      // add this fault to the event, must be consist with event type definition before.
				event1.setBusFault(fault); 
				return event1;
		}

}
