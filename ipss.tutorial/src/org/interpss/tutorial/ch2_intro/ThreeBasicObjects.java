package org.interpss.tutorial.ch2_intro;

import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.interpss.CorePluginFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;

public class ThreeBasicObjects {
	
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		// import IEEE CDF format data to create a network object
		AclfNetwork net = CorePluginFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee/009ieee.dat")
				.getAclfNet();	
		
		/*
		 * This part shows how to access and change the load data at a bus
		 */
		
		//get the total load PQ, return as a complex
		
		Complex load_bus5 = net.getBus("Bus5").getLoadPQ();
		System.out.println("\n\nload@Bus5 = "+load_bus5);
		
		// internally, interpss supports multiple loads connected to the same bus, thus
		//it uses a list to store the load objects, users need to first access the load List to
		// to the specific load object to change the load setting
		
		// One can get the load object by list index or load Id
		net.getBus("Bus5").getContributeLoadList().get(0).setLoadCP(new Complex( 1.5,0.8));
		Complex load1_bus5 = net.getBus("Bus5").getLoadPQ();
		System.out.println("after change to 1.5+j0.8, load@Bus5 = "+load1_bus5);
		
		
		// or via load Id, if there is an id for this load object
		//net.getBus("Bus5").getContributeLoad(LoadId).setLoadCP(new Complex( 2.0,1.0));
		
		
		/*
		 * This part shows how to access and change the Generation data at a bus
		 */
		double bus2_genP =net.getBus("Bus2").getGenP();
		
		//NOTE: genQ is not accurate until you run the power flow
		System.out.println("genp@Bus2 = "+bus2_genP);
		net.getBus("Bus2").setGenP(bus2_genP*1.2);
		System.out.println("after scaling up by 1.2 times, genp@Bus2 = "+net.getBus("Bus2").getGenP()+"\n\n");

		
		
		/*
		 * This part shows how to access the connected branches of a bus
		 * 
		 * Such info is important for knowing the topology of system
		 * 
		 */
		
		List<Branch> branchList =net.getBus("Bus7").getBranchList();
		
		for(Branch br:branchList ){
			System.out.print("connected branch of bus7: "+br.getId());
			System.out.print(", from bus: "+br.getFromBus().getId());
			System.out.print(", to bus: "+br.getToBus().getId());
			System.out.print(", status: "+br.isActive()+"\n");
		}
		
		
		/*
		 * access basic information of a network, such as total number of buses, in-service buses, off-line buses
		 */
		
		System.out.println("\n\nTotal number of buses:"+net.getNoBus());
		System.out.println("Total number of off-line buses:"+net.getNoActiveBus());
		
		
		
	}

}
