package org.interpss.tutorial.ch4_shortCircuit;

import org.apache.commons.math3.complex.Complex;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AcscOutFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.TestUtilFunc;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.XfrConnectCode;
import com.interpss.core.acsc.adpter.AcscXformer;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.util.input.AcscInputUtilFunc;

import static com.interpss.common.util.IpssLogger.ipssLogger;
import static com.interpss.core.funcImpl.AcscFunction.acscXfrAptr;

public class Acsc5BusExample {

	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		AcscNetwork faultNet = CoreObjectFactory.createAcscNetwork();
		load_SC_5BusSystem(faultNet);
		System.out.println(faultNet.net2String());

  		
	  	SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);
	  	
  		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus2", algo);
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
	  	algo.calculateBusFault(fault);
 
		
	    // 0.0000 + j0.0000  -0.7531 + j29.05407  0.0000 + j0.0000
		
	  	System.out.println(fault.getFaultResult().getSCCurrent_012());
		System.out.println(AcscOutFunc.faultResult2String(faultNet,algo));
    
	}
	
	public static void load_SC_5BusSystem( AcscNetwork net) throws InterpssException {
		net.setId( "ACSC 5-bus test system" );
		net.setBaseKva(100000.0);
        String IdPrefix = "Bus";
    	AcscInputUtilFunc.addScNonContributeBusTo(net, IdPrefix+"1", "Bus-1", 13800, 1, 1);
    	AcscInputUtilFunc.addScNonContributeBusTo(net, IdPrefix+"2", "Bus-2", 13800, 1, 1);
    	AcscInputUtilFunc.addScNonContributeBusTo(net, IdPrefix+"3", "Bus-3", 13800, 1, 1);
    	AcscInputUtilFunc.addScContributeBusTo(net, IdPrefix+"4", "Bus-4", 13800, 1, 1,
  	         0.0, .02, 0.0, .02, 0.0, 1.0e10, UnitType.PU, "SolidGrounded", 0.0, 0.0, UnitType.PU);
    	AcscInputUtilFunc.addScContributeBusTo(net, IdPrefix+"5", "Bus-5", 13800, 1, 1,
  	         0.0, .02, 0.0, .02, 0.0, 1.0e10, UnitType.PU, "SolidGrounded", 0.0, 0.0, UnitType.PU);

		AcscBranch bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.LINE);
		bra.setZ( new Complex( 0.0, 0.25 ));
		bra.setZ0( new Complex(0.0,0.7));
		net.addBranch(bra, IdPrefix+"1", IdPrefix+"2");

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.LINE);
		net.addBranch(bra, IdPrefix+"1", IdPrefix+"3");
		bra.setZ( new Complex( 0.0, 0.35 ));
		bra.setZ0( new Complex(0.0,1.0));

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.LINE);
		net.addBranch(bra, IdPrefix+"2", IdPrefix+"3");
		bra.setZ( new Complex( 0.0, 0.3 ));
		bra.setZ0( new Complex(0.0,0.75));

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.XFORMER);
		net.addBranch(bra, IdPrefix+"4", IdPrefix+"2");
		bra.setZ( new Complex( 0.0, 0.015 ));
		bra.setZ0( new Complex(0.0, 0.03 ));
		AcscXformer xfr = acscXfrAptr.apply(bra);
		xfr.setFromConnectGroundZ(XfrConnectCode.WYE_UNGROUNDED, new Complex(0.0,0.0), UnitType.PU);
		xfr.setToConnectGroundZ(XfrConnectCode.DELTA, new Complex(0.0,0.0), UnitType.PU);

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.XFORMER);
		net.addBranch(bra, IdPrefix+"5", IdPrefix+"3");
		bra.setZ( new Complex( 0.0, 0.03 ));
		bra.setZ0( new Complex(0.0, 0.03 ));
		xfr = acscXfrAptr.apply(bra);
		xfr.setFromConnectGroundZ(XfrConnectCode.WYE_UNGROUNDED, new Complex(0.0,0.0), UnitType.PU);
		xfr.setToConnectGroundZ(XfrConnectCode.DELTA, new Complex(0.0,0.0), UnitType.PU);

		net.setScDataLoaded(true);
		ipssLogger.info( "ACSC 5-bus test system loaded" );
	}
}
