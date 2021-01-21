package org.interpss.tutorial.ch4_shortCircuit;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.complex.Complex;
import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AcscOutFunc;
import org.interpss.mapper.odm.ODMAcscParserMapper;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.numeric.util.TestUtilFunc;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.SequenceCode;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.sc.ScBusVoltageType;
import com.interpss.core.algo.sc.SimpleFaultAlgorithm;


public class IEEE9Bus_Acsc_test {
	
	@Test
	public void testIeee9SeqY() throws InterpssException, IpssNumericException{
			IpssCorePlugin.init();
			PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
			assertTrue(adapter.parseInputFile(NetType.AcscNet, new String[]{
					"testData/psse/IEEE9Bus/ieee9.raw",
					"testData/psse/IEEE9Bus/ieee9.seq"
			}));
			AcscModelParser acscParser =(AcscModelParser) adapter.getModel();
			acscParser.stdout();
			
	
			AcscNetwork net = new ODMAcscParserMapper().map2Model(acscParser).getAcscNet();
			
			//set the order in original sequence for better testing
			for(int i=1;i<=net.getNoBus();i++){
				net.getBus("Bus"+i).setSortNumber(i-1);
			}
			net.setBusNumberArranged(true);
			
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		  	algo.setLfMethod(AclfMethod.PQ);
		  	algo.getLfAdjAlgo().setApplyAdjustAlgo(false);
		  	algo.loadflow();
	  	
	  		assertTrue( net.isLfConverged());
	  		//System.out.println(AclfOutFunc.loadFlowSummary(net));
	  		//System.out.println(net.net2String());
	  		/*
	  		 * ***********************************
	  		 *       Positive sequence
	  		 * ***********************************      
	  		 * 
	  		 */
	  		
	        ISparseEqnComplex posYMatrix = net.formScYMatrix(SequenceCode.POSITIVE, false);
	        System.out.println(posYMatrix.getA(0, 0).toString());
	        //Gen Bus: Bus 1
	        //Yii: 0.0 + (-42.63668430335097i)
	        assertTrue(posYMatrix.getA(0, 0).getReal()==0);
	        assertTrue(Math.abs(posYMatrix.getA(0, 0).getImaginary()+42.6366)<1.0E-4);
	        //Yij (bus1->bus4): -0.0 + (17.636684303350968i)
	        assertTrue(posYMatrix.getA(0, 3).getReal()==0);
	        assertTrue(Math.abs(posYMatrix.getA(0, 3).getImaginary()-17.6366)<1.0E-4);
	        
	        //Load Bus: Bus 5
	        //Yii: 3.81 - j17.84
	        assertTrue(Math.abs(posYMatrix.getA(4, 4).getReal()-3.81)<1.0E-2);
	        assertTrue(Math.abs(posYMatrix.getA(4, 4).getImaginary()+17.84)<1.0E-2);
	        
	        //Y54:-1.37 + j11.60
	        assertTrue(Math.abs(posYMatrix.getA(4, 3).getReal()+1.37)<1.0E-2);
	        assertTrue(Math.abs(posYMatrix.getA(4, 3).getImaginary()-11.60)<1.0E-2);
	        
	        //Non-Gen, Non-Load: Bus7
	        //Yii 2.80 - j35.45
	        assertTrue(Math.abs(posYMatrix.getA(6, 6).getReal()-2.80)<1.0E-2);
	        assertTrue(Math.abs(posYMatrix.getA(6, 6).getImaginary()+35.45)<1.0E-2);
	       
			
	        /*
	  		 * ***********************************
	  		 *       Zero sequence
	  		 * ***********************************
	  		 * Gen Bus 1,2 and 3 is open from the sequence network
	  		 * 
	  		 */
	        
	        ISparseEqnComplex zeroYMatrix = net.formScYMatrix(SequenceCode.ZERO, false);
	      //Load Bus: Bus 5
	        //Yii: 1.0211168370406916 + (-6.79069203867941i)
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 4).getReal()-1.02)<1.0E-2);
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 4).getImaginary()+6.79)<1.0E-2);
	        
	        //Y54: -0.5460750853242321 + (4.641638225255973i)
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 3).getReal()+0.54)<1.0E-2);
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 3).getImaginary()-4.64)<1.0E-2);
	        
	        //Non-Gen, Non-Load: Bus7
	        //Yii 1.1218907410149137 + (-23.641745252186816i)
	        assertTrue(Math.abs(zeroYMatrix.getA(6, 6).getReal()-1.12)<1.0E-2);
	        assertTrue(Math.abs(zeroYMatrix.getA(6, 6).getImaginary()+23.64)<1.0E-2);
	        
		}

	@Test
	public void testFaultCalc() throws InterpssException{
		IpssCorePlugin.init();
		PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
		assertTrue(adapter.parseInputFile(NetType.AcscNet, new String[]{
				"testData/psse/IEEE9Bus/ieee9.raw",
				"testData/psse/IEEE9Bus/ieee9.seq"
		}));
		AcscModelParser acscParser =(AcscModelParser) adapter.getModel();
		acscParser.stdout();
		
		AcscNetwork net = new ODMAcscParserMapper().map2Model(acscParser).getAcscNet();
		
		//set the order in original sequence for better testing
		for(int i=1;i<=net.getNoBus();i++){
			net.getBus("Bus"+i).setSortNumber(i-1);
		}
		net.setBusNumberArranged(true);
		
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.PQ);
	  	algo.getLfAdjAlgo().setApplyAdjustAlgo(false);
	  	algo.loadflow();
  	
  		assertTrue( net.isLfConverged());
		
  	  	//*********************************************
	  	//             Bus4 3P Fault
	  	//********************************************
	  	
  		
	  	SimpleFaultAlgorithm acscAlgo = CoreObjectFactory.createSimpleFaultAlgorithm(net);
  		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus4", acscAlgo );
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
		//pre fault profile : solved power flow
		acscAlgo.setScBusVoltage(ScBusVoltageType.LOADFLOW_VOLT);
		
		acscAlgo.calculateBusFault(fault);
	  	System.out.println(fault.getFaultResult().getSCCurrent_012());
	  	System.out.println(fault.getFaultResult().getBusVoltage_012(net.getBus("Bus1")));
	  	
	  	//3p fault @Bus4
	  	//fault current
	  	//0.0000 + j0.0000  -1.4243 + j15.62133  0.0000 + j0.0000
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_012(), 
	  			0.0, 0.0, -1.4243, 15.62133, 0.0, 0.0) );
	  	//voltage @Bus1
	  	//0.0000 + j0.0000  0.61592 + j0.01616  0.0000 + j0.0000
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBusVoltage_012(net.getBus("Bus1")), 
	  			0.0, 0.0, 0.61592, 0.01616, 0.0, 0.0) );
	  	
	  	
	  	//*********************************************
	  	//             Bus4 LG Fault
	  	//********************************************
	  	fault = CoreObjectFactory.createAcscBusFault("Bus4", acscAlgo );
		fault.setFaultCode(SimpleFaultCode.GROUND_LG);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
		//pre fault profile : solved power flow
		acscAlgo.setScBusVoltage(ScBusVoltageType.LOADFLOW_VOLT);
		
		acscAlgo.calculateBusFault(fault);
	  	System.out.println(fault.getFaultResult().getSCCurrent_012());
	  	System.out.println(fault.getFaultResult().getBusVoltage_012(net.getBus("Bus1")));
	  	
	  	
	    //seq voltage @Bus1
	  	//0.0000 + j0.0000  0.88659 + j0.01024  -0.15334 + j0.01034
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBusVoltage_012(net.getBus("Bus1")), 
	  			0.0, 0.0, 0.88659, 0.01024, -0.15334, 0.01034) );
	  	
	  	
	  	//*********************************************
	  	//             Bus1 L-L Fault
	  	//********************************************
	  	
	  	fault = CoreObjectFactory.createAcscBusFault("Bus1", acscAlgo );
		fault.setFaultCode(SimpleFaultCode.GROUND_LL);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
		//pre fault profile : solved power flow
		acscAlgo.setScBusVoltage(ScBusVoltageType.LOADFLOW_VOLT);
		
		acscAlgo.calculateBusFault(fault);
	  	System.out.println(fault.getFaultResult().getSCCurrent_012());
	  	System.out.println(fault.getFaultResult().getBusVoltage_012(net.getBus("Bus4")));
	  	
	    //seq voltage @Bus4
	  	//0.0000 + j0.0000  0.61996 + j-0.00357  0.40527 + j-0.0355
	  	
	  	/*PWD: Fault Data - Buses
	  	 Seq. Volt +	 Seq. Volt -
	  	      0.61997	      0.40682
         */
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBusVoltage_012(net.getBus("Bus4")), 
	  			0.0, 0.0, 0.61996, -0.00357, 0.40527, -0.0355) );
	  	
	  	// Output fault analysis result
	  	System.out.println(AcscOutFunc.faultResult2String(net,acscAlgo));
	}
	

}
