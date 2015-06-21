package org.interpss.tutorial.ch2_intro;

import org.apache.commons.math3.complex.Complex;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

public class ThreeBasicObjects {
	
	
	public static void main(String[] args) throws InterpssException {
		//Initialize logger and Spring config
		IpssCorePlugin.init();
		
		// import IEEE CDF format data to create a network object
		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee/009ieee.dat")
				.getAclfNet();	
		
		/*
		 * This part shows how to access and change the load data at a bus
		 */
		
		//get the total load PQ, return as a complex
		
		Complex load_bus5 = net.getBus("Bus5").getLoadPQ();
		System.out.println(" load@Bus5 = "+load_bus5);
		
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
		

	}

}
