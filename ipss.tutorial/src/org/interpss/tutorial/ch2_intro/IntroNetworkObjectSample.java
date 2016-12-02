package org.interpss.tutorial.ch2_intro;

import static org.interpss.CorePluginFunction.formatKVStr;

import org.apache.commons.math3.complex.Complex;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.BaseAclfBus;
import com.interpss.core.aclf.adpter.AclfGenBusAdapter;
import com.interpss.core.aclf.adpter.AclfLine;
import com.interpss.core.aclf.adpter.AclfLoadBusAdapter;
import com.interpss.core.aclf.adpter.AclfPSXformer;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Branch;

public class IntroNetworkObjectSample {
	
	public static void main(String[] args) throws InterpssException{
		IpssCorePlugin.init();
		//create a sample system consisting of two buses
		AclfNetwork net = createSampleNetwork();
		
		// create the default loadflow algorithm
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);

	  	// use the loadflow algorithm to perform loadflow calculation
	  	algo.loadflow();
	  	
	  	// output loadflow calculation results
	  	System.out.println(AclfOutFunc.loadFlowSummary(net));
	  	
		//output the power flow result related to Bus1
		System.out.println(busOrientedOutPut(net,net.getBus("Bus1")));
	}
	/**
	 * output the bus voltage, generation, load and connected branch information
	 * @param net
	 * @param bus
	 * @return
	 */
	private static String busOrientedOutPut(AclfNetwork net, AclfBus bus){
		StringBuffer str = new StringBuffer("");
		str.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
		str.append(" Bus ID            Bus Voltage          Generation           Load             To             Branch P+jQ          Xfr Ratio   PS-Xfr Ang\n");
		str.append("             baseKV  Mag/pu  Ang/deg   (mW)    (mVar)    (mW)    (mVar)      Bus ID      (mW)    (mVar)   (kA)   (From)  (To) (from)   (to)\n");
		str.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
		
		
		double baseKVA = net.getBaseKva();

		AclfGenBusAdapter genBus = bus.toGenBus();
		//get the generation and load 
		Complex busGen = genBus.getGenResults(UnitType.mVA);
		Complex busLoad = genBus.getLoadResults(UnitType.mVA);

		String id = bus.getId();
		//output the bus data with specific format
		str.append(Number2String.toStr(-12, id) + " ");
		str.append(String.format(" %s ", formatKVStr.apply(bus.getBaseVoltage()*0.001)));
		str.append(Number2String.toStr("0.0000", bus.getVoltageMag(UnitType.PU)) + " ");
		str.append(Number2String.toStr("##0.00", bus.getVoltageAng(UnitType.Deg)) + " ");
		str.append(Number2String.toStr("####0.00", busGen.getReal()) + " ");
		str.append(Number2String.toStr("####0.00", busGen.getImaginary()) + " ");
		str.append(Number2String.toStr("####0.00", busLoad.getReal()) + " ");
		str.append(Number2String.toStr("####0.00", busLoad.getImaginary()) + " ");

        //output the data of branches connected to the bus
		int cnt = 0;
		//iterate over all the branches connected to the bus, both in-service and off-line
		for (Branch br : bus.getBranchList()) {
			if (br.isActive() && br instanceof AclfBranch) {
				AclfBranch bra = (AclfBranch) br;

				Complex pq = new Complex(0.0, 0.0);
				double amp = 0.0, fromRatio = 1.0, toRatio = 1.0, fromAng = 0.0, toAng = 0.0;
				BaseAclfBus<?,?> toBus = null;
				if (bra.isActive()) {
					// to determine whether the bus is at the from- or to-end of the branch.
					if (bus.getId().equals(bra.getFromAclfBus().getId())) {
						toBus = bra.getToAclfBus();
						// power transfer from from-end to to-end of the branch
						pq = bra.powerFrom2To(UnitType.mVA);
						//brnach current measured at Ampere. 
						amp = UnitHelper.iConversion(bra.current(UnitType.PU), bra.getFromAclfBus().getBaseVoltage(),
								baseKVA, UnitType.PU, UnitType.Amp);
						//if the branch is a transformer, then output the tap ratio and phase-shifting angle, if any
						if (bra.isXfr() || bra.isPSXfr()) {
							fromRatio = bra.getFromTurnRatio();
							toRatio = bra.getToTurnRatio();
							if (bra.isPSXfr()) {
								AclfPSXformer psXfr = bra.toPSXfr();
								fromAng = psXfr.getFromAngle(UnitType.Deg);
								toAng = psXfr.getToAngle(UnitType.Deg);
							}
						}
					} else {
						toBus = bra.getFromAclfBus();
						pq = bra.powerTo2From(UnitType.mVA);
						amp = UnitHelper.iConversion(bra.current(UnitType.PU), bra.getToAclfBus().getBaseVoltage(),
								baseKVA, UnitType.PU, UnitType.Amp);
						if (bra.isXfr() || bra.isPSXfr()) {
							toRatio = bra.getFromTurnRatio();
							fromRatio = bra.getToTurnRatio();
							if (bra.isPSXfr()) {
								AclfPSXformer psXfr = bra.toPSXfr();
								toAng = psXfr.getFromAngle(UnitType.Deg);
								fromAng = psXfr.getToAngle(UnitType.Deg);
							}
						}
					}
				}
				// if more than one branch connected to the bus, output the branch information in a new line
				if (cnt++ > 0)
					str.append(Number2String.toStr(67, " ")	+ "     ");
				id = toBus.getId();
				str.append(" " + Number2String.toStr(-12, id) + " ");
				str.append(Number2String.toStr("####0.00", pq.getReal()) + " ");
				str.append(Number2String.toStr("####0.00", pq.getImaginary()) + " ");
				str.append(Number2String.toStr("##0.0##", 0.001 * amp) + " ");
				if (bra.isXfr() || bra.isPSXfr()) {
					if (fromRatio != 1.0)
						str.append(Number2String.toStr("0.0###", fromRatio) + " ");
					else
						str.append("       ");

					if (toRatio != 1.0)
						str.append(Number2String.toStr("0.0###", toRatio));
					else
						str.append("      ");

					if (bra.isPSXfr()) {
						if (fromAng != 0.0)
							str.append("   " + Number2String .toStr("##0.0", fromAng));
						else
							str.append("        ");

						if (toAng != 0.0)
							str.append(" " + Number2String.toStr("##0.0", toAng));
						else
							str.append("      ");
					}
					str.append("\n");
				} else {
					str.append("\n");
				}
			}
		}
	
		return str.toString();
	
	}
	/**
	 * create a two-bus network and run the power flow
	 * @return an aclf network with converged power flow state.
	 * @throws InterpssException 
	 */
	private static AclfNetwork createSampleNetwork() throws InterpssException{
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
			  	
			  	
			  	
			  	return net;
	}

}
