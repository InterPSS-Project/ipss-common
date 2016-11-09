package org.interpss.tutorial.ch5_dstab;

import static com.interpss.dstab.cache.StateVariableRecorder.StateVarRecType.MachineState;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Level;

import org.apache.commons.math3.complex.Complex;
import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.mapper.odm.ODMDStabParserMapper;
import org.interpss.numeric.NumericConstant;
import org.interpss.numeric.util.Number2String;
import org.interpss.numeric.util.NumericUtil;
import org.junit.Test;

import com.interpss.CoreCommonFactory;
import com.interpss.CoreObjectFactory;
import com.interpss.DStabObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabGen;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.cache.StateVariableRecorder;
import com.interpss.dstab.cache.StateVariableRecorder.StateRecord;
import com.interpss.dstab.cache.StateVariableRecorder.StateVarRecType;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.dstab.devent.DynamicEvent;
import com.interpss.dstab.devent.DynamicEventType;
import com.interpss.dstab.mach.SalientPoleMachine;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DStab_IEEE9Bus_Test {
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
		
		
	    DStabilityNetwork dsNet =simuCtx.getDStabilityNet();
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
		dstabAlgo.setTotalSimuTimeSec(5.0);
		dstabAlgo.setRefMachine(dsNet.getMachine("Bus1-mach1"));
		
		//create a three phase fault @Bus4, start at 1.0s, last for 0.08s
		dsNet.addDynamicEvent(create3PhaseFaultEvent("Bus4",dsNet,1.0,0.08),"3phaseFault@Bus4");
		
		StateVariableRecorder ssRecorder = new StateVariableRecorder(0.0001);
		ssRecorder.addCacheRecords("Bus2-mach1",      // mach id 
				MachineState,    // record type
				DStabOutSymbol.OUT_SYMBOL_MACH_ANG,       // state variable name
				0.005,                                      // time steps for recording 
				1000);                                      // total points to record 
		
		ssRecorder.addCacheRecords("Bus2-mach1",      // mach id 
				MachineState,    // record type
				DStabOutSymbol.OUT_SYMBOL_MACH_PE,       // state variable name
				0.005,                                      // time steps for recording 
				1000);                                      // total points to record
		ssRecorder.addCacheRecords("Bus2-mach1",      // mach id 
				StateVarRecType.MachineState,    // record type
				DStabOutSymbol.OUT_SYMBOL_MACH_Efd,       // state variable name
				0.005,                                      // time steps for recording 
				1000);
		
		ssRecorder.addCacheRecords("Bus2-mach1",      // mach id 
				StateVarRecType.MachineState,    // record type
				DStabOutSymbol.OUT_SYMBOL_MACH_PM,       // state variable name
				0.005,                                      // time steps for recording 
				1000);
		// set the output handler
		dstabAlgo.setSimuOutputHandler(ssRecorder);
		
		IpssLogger.getLogger().setLevel(Level.FINE);
		if (dstabAlgo.initialization()) {

			System.out.println("Running DStab simulation ...");
			
			//NOTE: There are two ways to perform the simulation
			
			// 1. standard one--run till the end
			dstabAlgo.performSimulation();
			
			// 2. run step by step, this provides users more flexibility to make changes to the network 
			/*
			for (double t =0;t<dstabAlgo.getTotalSimuTimeSec();t+=dstabAlgo.getSimuStepSec()){
			     dstabAlgo.solveDEqnStep(true);
			     //Here users can make some changes to the system, or the set point of a dynamic model
			}
			*/
		}
		/*
		 * Machine Initial angle 
		 * bus1-mach1: 0.06257886961702232 Rad
		 * bus2-mach2: 1.0672402573811874  Rad  
		 *    -> relative angle = 57.5628 deg
		 */
		// output recorded simulation results
		List<StateRecord> list = ssRecorder.getMachineRecords(
				"Bus2-mach1", MachineState, DStabOutSymbol.OUT_SYMBOL_MACH_ANG);
		System.out.println("\n\n Bus2 Machine Anagle");
		for (StateRecord rec : list) {
			System.out.println(Number2String.toStr(rec.t) + ", " + Number2String.toStr(rec.variableValue));
		}

		
		list = ssRecorder.getMachineRecords(
				"Bus2-mach1", MachineState, DStabOutSymbol.OUT_SYMBOL_MACH_PE);
		System.out.println("\n\n Bus2 Machine PE");
		for (StateRecord rec : list) {
			System.out.println(Number2String.toStr(rec.t) + ", " + Number2String.toStr(rec.variableValue));
		}
		list = ssRecorder.getMachineRecords(
				"Bus2-mach1", StateVarRecType.MachineState, DStabOutSymbol.OUT_SYMBOL_MACH_Efd);
		System.out.println("\n\n Bus2 Machine Efd");
		for (StateRecord rec : list) {
			System.out.println(Number2String.toStr(rec.t) + ", " + Number2String.toStr(rec.variableValue));
		}
		
		list = ssRecorder.getMachineRecords(
				"Bus2-mach1", StateVarRecType.MachineState, DStabOutSymbol.OUT_SYMBOL_MACH_PM);
		System.out.println("\n\n Bus2 Machine PM");
		for (StateRecord rec : list) {
			System.out.println(Number2String.toStr(rec.t) + ", " + Number2String.toStr(rec.variableValue));
		}
		
		
		/*
		 *  Bus2 Machine Anagle
				0.0000, 57.56288
				0.0010, 57.56288
				0.0020, 57.56288
				0.0030, 57.56288
				0.0040, 57.56288
				0.0050, 57.56288
				0.0060, 57.56288
				0.0070, 57.56288
				0.0080, 57.56288
				0.0090, 57.56288
		 */
		
		/*
		 *  Bus2 Machine PM
			0.0000, 1.6300
			0.0010, 1.6300
			0.0020, 1.6300
			0.0030, 1.6300
			0.0040, 1.6300
			0.0050, 1.6300
			0.0060, 1.6300
			0.0070, 1.6300
			0.0080, 1.6300
			0.0090, 1.6300
		 */
		
		/*
		 *  Bus2 Machine Efd
			0.0000, 1.78898
			0.0010, 1.78898
			0.0020, 1.78898
			0.0030, 1.78898
			0.0040, 1.78898
			0.0050, 1.78898
			0.0060, 1.78898
			0.0070, 1.78898
			0.0080, 1.78898
			0.0090, 1.78898
		 */
	}
	
	//@Test
	public void Dstab_data_check(){
		IpssCorePlugin.init();
		IPSSMsgHub msg = CoreCommonFactory.getIpssMsgHub();
		IpssLogger.getLogger().setLevel(Level.WARNING);
		
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
		
		
	    DStabilityNetwork dsNet =simuCtx.getDStabilityNet();
	    
	    /*
	     * check generator data
	     * --------------------------------------
	     * 
	     * <dstabBus scCode="Contributing" id="Bus1" areaNumber="1" zoneNumber="1" number="1" offLine="false" name="BUS-1       ">
                <ownerList id="1">
                    <ownership unit="PU" value="1.0"/>
                </ownerList>
                <baseVoltage unit="KV" value="16.5"/>
                <voltage unit="PU" value="1.04"/>
                <angle unit="DEG" value="0.0"/>
                <genData>
                    <dstabEquivGen code="Swing">
                        <power unit="MVA" re="0.0" im="0.0"/>
                        <desiredVoltage unit="PU" value="1.04"/>
                        <desiredAngle unit="DEG" value="0.0"/>
                        <potiveZ unit="PU" re="0.0" im="0.04"/>
                        <negativeZ unit="PU" re="0.0" im="0.04"/>
                        <zeroZ unit="PU" re="0.0" im="0.04"/>
                    </dstabEquivGen>
                    <dstabContributeGen id="1" offLine="false" name="Gen:1(1)">
                        <desc>PSSE Generator 1 at Bus 1</desc>
                        <power unit="MVA" re="71.64" im="27.05"/>
                        <desiredVoltage unit="PU" value="1.04"/>
                        <qLimit unit="MVAR" max="9999.0" min="-9999.0"/>
                        <pLimit unit="MW" max="9999.0" min="-9999.0"/>
                        <mvaBase unit="MVA" value="100.0"/>
                        <sourceZ unit="PU" re="0.0" im="0.04"/>
                        <mvarVControlParticipateFactor>1.0</mvarVControlParticipateFactor>
                        <potiveZ unit="PU" re="0.0" im="0.04"/>
                        <negativeZ unit="PU" re="0.0" im="0.04"/>
                        <zeroZ unit="PU" re="0.0" im="0.04"/>
                        <eq11MachModel>
                            <desc>GENSAL</desc>
                            <H>23.64</H>
                            <D>0.0</D>
                            <xl>0.0336</xl>
                            <ra>0.0</ra>
                            <xd>0.146</xd>
                            <xq>0.0969</xq>
                            <xd1>0.0608</xd1>
                            <Td01 unit="Sec" value="8.96"/>
                            <seFmt1>
								<se100>0.0</se100>
								<se120>0.0</se120>
								<sliner>1.0</sliner>
                            </seFmt1>
                            <xq11>0.04</xq11>
                            <Tq011 unit="Sec" value="0.06"/>
                            <xd11>0.04</xd11>
                            <Td011 unit="Sec" value="0.04"/>
                        </eq11MachModel>
                    </dstabContributeGen>
                </genData>
                <loadData>
                    <dstabEquivLoad/>
                </loadData>
                <shuntYData>
                    <equivY im="0.0"/>
                </shuntYData>
            </dstabBus>
	     */
	    
	    DStabBus bus1 = dsNet.getDStabBus("Bus1");
	    assertTrue(bus1.getContributeGenList().size()==1);
	    
	    assertTrue(bus1.getContributeGenList().get(0) instanceof DStabGen);
	    DStabGen gen1 =(DStabGen) bus1.getContributeGenList().get(0);
	    assertTrue(bus1.getGenCode()==AclfGenCode.SWING);
	    
	    /*
	     * <power unit="MVA" re="71.64" im="27.05"/>
           <desiredVoltage unit="PU" value="1.04"/>
	     */
	    assertTrue(gen1.getGen().getReal()==0.7164);
	    assertTrue(gen1.getGen().getImaginary()==0.2705);
	    assertTrue(gen1.getDesiredVoltMag()==1.04);
	    
	    /*
	     * <mvaBase unit="MVA" value="100.0"/>
           <sourceZ unit="PU" re="0.0" im="0.04"/>
	     */
	    assertTrue(gen1.getMvaBase()==100);
	    assertTrue(NumericUtil.equals(gen1.getSourceZ(), new Complex(0, 0.04),1.0E-4));
	    assertTrue(NumericUtil.equals(gen1.getPosGenZ(), new Complex(0, 0.04),1.0E-4));
	    
	    /*
	     * <eq11MachModel>
                            <desc>GENSAL</desc>
                            <H>23.64</H>
                            <D>0.0</D>
                            <xl>0.0336</xl>
                            <ra>0.0</ra>
                            <xd>0.146</xd>
                            <xq>0.0969</xq>
                            <xd1>0.0608</xd1>
                            <Td01 unit="Sec" value="8.96"/>
                            <seFmt1>
								<se100>0.0</se100>
								<se120>0.0</se120>
								<sliner>1.0</sliner>
                            </seFmt1>
                            <xq11>0.04</xq11>
                            <Tq011 unit="Sec" value="0.06"/>
                            <xd11>0.04</xd11>
                            <Td011 unit="Sec" value="0.04"/>
                        </eq11MachModel>
	     */
	    SalientPoleMachine mach = (SalientPoleMachine) gen1.getMach();
	    assertTrue(mach.getH()==23.64);
	    assertTrue(mach.getXl()==0.0336);
	    assertTrue(mach.getTd01()==8.96);
	    assertTrue(mach.getTd011()==0.04);
	    assertTrue(mach.getTq011()==0.06);
	    
	    /*
	     * check sequence network data
	     */
	}
    
	private DynamicEvent create3PhaseFaultEvent(String faultBusId, DStabilityNetwork net,double startTime, double durationTime){
	       // define an event, set the event id and event type.
			DynamicEvent event1 = DStabObjectFactory.createDEvent("BusFault3P@"+faultBusId, "Bus Fault 3P@"+faultBusId, 
					DynamicEventType.BUS_FAULT, net);
			event1.setStartTimeSec(startTime);
			event1.setDurationSec(durationTime);
			
	      // define a bus fault
			DStabBus faultBus = net.getDStabBus(faultBusId);
			AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus Fault 3P@"+faultBusId, net);
	  		fault.setBus(faultBus);
			fault.setFaultCode(SimpleFaultCode.GROUND_3P);
			fault.setZLGFault(NumericConstant.SmallScZ);

	      // add this fault to the event, must be consist with event type definition before.
			event1.setBusFault(fault); 
			return event1;
	}
}
