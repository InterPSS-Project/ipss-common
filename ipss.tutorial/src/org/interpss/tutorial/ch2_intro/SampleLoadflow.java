 /*
  * @(#)SampleLoadflow.java   
  *
  * Copyright (C) 2006 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.tutorial.ch2_intro;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import org.apache.commons.math3.complex.Complex;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.PerformanceTimer;
import org.interpss.pssl.simu.IpssAclf;
import org.interpss.pssl.simu.net.IpssAclfNet;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adpter.AclfLine;
import com.interpss.core.aclf.adpter.AclfLoadBusAdapter;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.algo.LoadflowAlgorithm;


public class SampleLoadflow {
	public static void main(String args[]) throws InterpssException {
		IpssCorePlugin.init();
		
		// set session message to Warning level
		IPSSMsgHub msg = IpssAclf.psslMsg;
		
		//use interpss basic functions to create network
		simpleLoadflow(msg);

		// use PSSL to create network
		simpleLoadflowPSSL(msg);

	}	

	public static void simpleLoadflow(IPSSMsgHub msg) throws InterpssException {
		// Create an AclfNetwork object
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();

		double baseKva = 100000.0;
		
		// set system basekva for loadflow calculation
		net.setBaseKva(baseKva);
	  	
		// create a AclfBus object
  		AclfBus bus1 = CoreObjectFactory.createAclfBus("Bus1", net);
  		// set bus name and description attributes
  		bus1.setAttributes("Bus 1", "");
  		// set bus base voltage 
  		bus1.setBaseVoltage(4000.0);
  		// set bus to be a swing bus
  		bus1.setGenCode(AclfGenCode.SWING);
  		// adapt the bus object to a swing bus object
  		AclfSwingBus swingBus = bus1.toSwingBus();
  		// set swing bus attributes
  		swingBus.setDesiredVoltMag(1.0, UnitType.PU);
  		swingBus.setDesiredVoltAng(0.0, UnitType.Deg);
  		// add the bus into the network
  		//net.addBus(bus1);
  		
  		AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net);
  		bus2.setAttributes("Bus 2", "");
  		bus2.setBaseVoltage(4000.0);
  		// set the bus to a non-generator bus
  		bus2.setGenCode(AclfGenCode.NON_GEN);
  		// set the bus to a constant power load bus
  		bus2.setLoadCode(AclfLoadCode.CONST_P);
  		// adapt the bus object to a Load bus object
  		AclfLoadBusAdapter loadBus = bus2.toLoadBus();
  		// set load to the bus
  		loadBus.setLoad(new Complex(1.0, 0.8), UnitType.PU);
  		//net.addBus(bus2);
  		
  		// create an AclfBranch object
  		AclfBranch branch = CoreObjectFactory.createAclfBranch();
  		net.addBranch(branch, "Bus1", "Bus2");
  		// set branch name, description and circuit number
  		branch.setAttributes("Branch 1", "", "1");
  		// set branch to a Line branch
  		branch.setBranchCode(AclfBranchCode.LINE);
  		// adapt the branch object to a line branch object
		AclfLine lineBranch = branch.toLine();
		// set branch parameters
  		lineBranch.setZ(new Complex(0.05, 0.1), UnitType.PU, 4000.0);
  		// add the branch from Bus1 to Bus2
	  	
	  	// create the default loadflow algorithm
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);

	  	// use the loadflow algorithm to perform loadflow calculation
	  	algo.loadflow();
	  	
	  	// output loadflow calculation results
	  	System.out.println(AclfOutFunc.loadFlowSummary(net));
    }	

	public static void simpleLoadflowPSSL(IPSSMsgHub msg) throws InterpssException {
		// Create an AclfNetwork object
		AclfNetwork net = IpssAclfNet.createAclfNetwork("Net")
				.setBaseKva(100000.0)
				.getAclfNet();

		// set the network data
		setSimpleLoadflowDataByPSSL(net, msg);
	  	
	  	// create the default loadflow algorithm
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);

	  	// run power flow using default setting
		algo.loadflow();
		
	  	
	  	// output loadflow calculation results
	  	System.out.println(AclfOutFunc.loadFlowSummary(net));
    }	

	public static void setSimpleLoadflowDataByPSSL(AclfNetwork net, IPSSMsgHub msg) throws InterpssException {
		IpssAclfNet.addAclfBus("Bus1", "Bus 1", net)
				.setBaseVoltage(4000.0)
				.setGenCode(AclfGenCode.SWING)
				.setVoltageSpec(1.0, UnitType.PU, 0.0, UnitType.Deg)
				.setLoadCode(AclfLoadCode.NON_LOAD);
  		
		IpssAclfNet.addAclfBus("Bus2", "Bus 2", net)
  				.setBaseVoltage(4000.0)
  				.setGenCode(AclfGenCode.NON_GEN)
  				.setLoadCode(AclfLoadCode.CONST_P)
  				.setLoad(new Complex(1.0, 0.8), UnitType.PU);
  		
		IpssAclfNet.addAclfBranch("Bus1", "Bus2", "Branch 1", net)
				.setBranchCode(AclfBranchCode.LINE)
				.setZ(new Complex(0.05, 0.1), UnitType.PU);
	}	

	
}
