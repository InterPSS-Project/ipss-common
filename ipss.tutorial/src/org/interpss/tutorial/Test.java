package org.interpss.tutorial;

import org.apache.commons.math3.complex.Complex;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfLineAdapter;
import com.interpss.core.aclf.adpter.AclfLoadBusAdapter;
import com.interpss.core.aclf.adpter.AclfSwingBusAdapter;
import com.interpss.core.algo.LoadflowAlgorithm;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		IpssCorePlugin.init();
		
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		
		AclfBus bus5 = CoreObjectFactory.createAclfBus("Bus5", net).get();
		//bus5.setStatus(true);
		// set bus name and description attributes
		bus5.setAttributes("Bus 5", "");
		// set bus base voltage 
		bus5.setBaseVoltage(100000.0);
		// set bus to be a swing bus
		bus5.setGenCode(AclfGenCode.SWING);
		
		// adapt the bus object to a swing bus object
		AclfSwingBusAdapter swingBus = bus5.toSwingBus();
		// set swing bus attributes
		swingBus.setDesiredVoltMag(1.05, UnitType.PU);
		swingBus.setDesiredVoltAng(0.0, UnitType.Deg);
		// add the bus into the network
		//net.addBus(bus1);
		
		AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net).get();
		bus2.setAttributes("Bus 2", "");
		bus2.setBaseVoltage(100000.0);
		// set the bus to a non-generator bus
		bus2.setGenCode(AclfGenCode.GEN_PQ);
		// set the bus to a constant power load bus
		bus2.setLoadCode(AclfLoadCode.CONST_P);
		// adapt the bus object to a Load bus object
		AclfLoadBusAdapter loadBus = bus2.toLoadBus();
		// set load to the bus
		loadBus.setLoad(new Complex(1.45,-67), UnitType.PU);
		// net.addBus(bus2);
		bus2.setGenP(2);
		bus2.setGenQ(1);

	    AclfBus bus3 = CoreObjectFactory.createAclfBus("Bus3",net).get();
	    System.out.println(bus3.isStatus());
	    bus3.setAttributes("Bus 3","");
	    bus3.setBaseVoltage(100000.0);
	    bus3.setGenCode(AclfGenCode.NON_GEN);
	    bus3.setLoadCode(AclfLoadCode.CONST_P);
	    AclfLoadBusAdapter loadBus1 = bus3.toLoadBus();
	    loadBus1.setLoad(new Complex(1.58,-35.7),UnitType.PU);
	    bus3.setGenP(3.7);
	    bus3.setGenQ(1.3);
	    
	    AclfBus bus4 = CoreObjectFactory.createAclfBus("Bus4", net).get();
		bus4.setAttributes("Bus 4", "");
		bus4.setBaseVoltage(100000.0);
		bus4.setGenCode(AclfGenCode.GEN_PV);
		bus4.setLoadCode(AclfLoadCode.CONST_P);
		AclfLoadBusAdapter loadBus2 = bus4.toLoadBus();
		loadBus2.setLoad(new Complex(0,-66.7), UnitType.PU);
		
		
		AclfBus bus1 = CoreObjectFactory.createAclfBus("Bus1", net).get();
		bus1.setAttributes("Bus 1", "");
		bus1.setBaseVoltage(100000.0);
		bus1.setGenCode(AclfGenCode.GEN_PQ);
		bus1.setLoadCode(AclfLoadCode.CONST_P);
		AclfLoadBusAdapter loadBus3 = bus1.toLoadBus();
		loadBus3.setLoad(new Complex(1.74,0.85), UnitType.PU);
		bus1.setGenP(1.6);
		bus1.setGenQ(0.8);
		
	    
	    
		
		// create an AclfBranch object
		AclfBranch branch = CoreObjectFactory.createAclfBranch();
		net.addBranch(branch, "Bus1", "Bus2");
		// set branch name, description and circuit number
		branch.setAttributes("Branch 1", "", "1");
		// set branch to a Line branch
		branch.setBranchCode(AclfBranchCode.LINE);
		// adapte the branch object to a line branch object
		AclfLineAdapter lineBranch = branch.toLine();
		// set branch parameters
		lineBranch.setZ(new Complex(0.04, 0.25), UnitType.PU, 13800.0);
		// add the branch from Bus1 to Bus2
	  	
	  	AclfBranch branch1 = CoreObjectFactory.createAclfBranch();
		net.addBranch(branch1, "Bus1", "Bus3");
		branch1.setAttributes("Branch 2", "", "2");
		branch1.setBranchCode(AclfBranchCode.LINE);
		AclfLineAdapter lineBranch1 = branch1.toLine();
		lineBranch1.setZ(new Complex(0.1, 0.35), UnitType.PU, 13800.0);
	  	
		AclfBranch branch2 = CoreObjectFactory.createAclfBranch();
		net.addBranch(branch2, "Bus2", "Bus3");
		branch2.setAttributes("Branch 3", "", "3");
		branch2.setBranchCode(AclfBranchCode.LINE);
		AclfLineAdapter lineBranch2 = branch2.toLine();
		lineBranch2.setZ(new Complex(0.08, 0.3), UnitType.PU, 1050.0);
		
		AclfBranch branch3 = CoreObjectFactory.createAclfBranch();
		net.addBranch(branch3, "Bus2", "Bus4");
		branch3.setAttributes("Branch 4", "", "4");
		branch3.setBranchCode(AclfBranchCode.LINE);
		AclfLineAdapter lineBranch3 = branch3.toLine();
		lineBranch3.setZ(new Complex(0,63.5), UnitType.PU, 1050.0);
		
		AclfBranch branch4 = CoreObjectFactory.createAclfBranch();
		net.addBranch(branch4, "Bus3", "Bus5");
		branch4.setAttributes("Branch 4", "", "4");
		branch4.setBranchCode(AclfBranchCode.LINE);
		AclfLineAdapter lineBranch4 = branch4.toLine();
		lineBranch4.setZ(new Complex(0, 31.75), UnitType.PU, 4200.0);
		
	  	System.out.println(net.net2String());

	}

}
