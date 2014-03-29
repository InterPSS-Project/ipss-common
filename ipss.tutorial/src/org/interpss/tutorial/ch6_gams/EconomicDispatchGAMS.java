package org.interpss.tutorial.ch6_gams;


	import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.DclfOutFunc;
import org.interpss.fadapter.IpssFileAdapter;

import com.gams.api.GAMSDatabase;
import com.gams.api.GAMSJob;
import com.gams.api.GAMSOptions;
import com.gams.api.GAMSParameter;
import com.gams.api.GAMSSet;
import com.gams.api.GAMSVariableRecord;
import com.gams.api.GAMSWorkspace;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.DclfObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.core.net.Bus;


	public class EconomicDispatchGAMS {	        
	        public static void main(String[] args) throws InterpssException {
	        
	        IpssCorePlugin.init();
	               
	        
	        //Step-1. Read in IEEE 14 Bus data
	        AclfNetwork net = CorePluginObjFactory
	                                .getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
	                                .loadDebug("testData/ieee/IEEE14Bus.dat")
	                                .getAclfNet();

	                
	        // Step-2. set the data for GAMS model  through GAMSDatabase
	                
	        //create new GAMS workspace
	        GAMSWorkspace ws = new GAMSWorkspace();
	        
	        // Create the GAMSDatabase and fill with the network data
	        GAMSDatabase db = ws.addDatabase();
	        
	        //define two sets as the keys for storing and accessing the data in the following Parameters 
	        GAMSSet genUnit = db.addSet("i", 1, "generating units");
	        GAMSSet loadBus = db.addSet("j", 1, "load buses");
	        
	        GAMSParameter genPLow = db.addParameter("genPLow", 1,"lower bound of each generating unit");
	        GAMSParameter genPUp  = db.addParameter("genPUp",1, "upper bound of each generating unit");
	        GAMSParameter loadP   = db.addParameter("loadP", 1,"demand at load bus j");
	        
	        //Generation cost in quadratic function: cost = A(i)+B(i)*p(i)+C(i)*p(i)*p(i)
	        
	        GAMSParameter FactorA = db.addParameter("FactorA", 1,"fixed part of generation cost function");
	        GAMSParameter FactorB = db.addParameter("FactorB", 1,"linear weighting factor b for generation cost function");
	        GAMSParameter FactorC = db.addParameter("FactorC", 1,"quadratic weighting factor c for generation cost function");
	        
	        //2.1 set gen and load busId
	        for (Bus b : net.getBusList()) {
	                AclfBus bus = (AclfBus)b;
	                //gen busId
	                if (bus.isActive() && bus.isGen() )
	                        genUnit.addRecord(bus.getId());
	                
	                //load busId
	                if (bus.isActive() && bus.isLoad() ){
	                        //set the loadBus id
	                        loadBus.addRecord(bus.getId());
	                        //set loadP
	                        loadP.addRecord(bus.getId()).setValue(bus.getLoadP());
	                }
	        }
	        
	        //NOTE: IEEE CDF format does NOT define gen lower and upper bound, therefore, we set them here manually,
	        //This can also be loaded by reading  a XLS or CSV file which stores these info.
	        //2.2 set genP Lower bound

	        genPLow.addRecord("Bus1").setValue(0.02);
	        genPLow.addRecord("Bus2").setValue(0.03);
	        genPLow.addRecord("Bus3").setValue(0.05);
	        genPLow.addRecord("Bus6").setValue(0.06);
	        genPLow.addRecord("Bus8").setValue(0.07);
	        
	        
	        //2.3 set genP upper bound
	 
	        genPUp.addRecord("Bus1").setValue(2.7);
	        genPUp.addRecord("Bus2").setValue(0.5);
	        genPUp.addRecord("Bus3").setValue(0.5);
	        genPUp.addRecord("Bus6").setValue(0.5);
	        genPUp.addRecord("Bus8").setValue(0.5);  
	        
	        //2.4 set FactorA

	        FactorA.addRecord("Bus1").setValue(500.0);
	        FactorA.addRecord("Bus2").setValue(300.0);
	        FactorA.addRecord("Bus3").setValue(400.0);
	        FactorA.addRecord("Bus6").setValue(100.0);
	        FactorA.addRecord("Bus8").setValue(50.0);
	        
	        
	        //2.5 set FactorB

	        FactorB.addRecord("Bus1").setValue(30.0);
	        FactorB.addRecord("Bus2").setValue(10.0);
	        FactorB.addRecord("Bus3").setValue(70.0);
	        FactorB.addRecord("Bus6").setValue(50.0);
	        FactorB.addRecord("Bus8").setValue(40.0);
	        
	        //2.6 set FactorC

	        FactorC.addRecord("Bus1").setValue(0.01);
	        FactorC.addRecord("Bus2").setValue(0.005);
	        FactorC.addRecord("Bus3").setValue(0.03);
	        FactorC.addRecord("Bus6").setValue(0.06);
	        FactorC.addRecord("Bus8").setValue(0.09);
	        
	        // Create and run the GAMSJob
	        GAMSOptions opt = ws.addOptions();
	        GAMSJob ieee14ED = ws.addJobFromString(modelStr);
	        
	        // load the database data into the GAMS engine by GDXIN 
	        opt.defines("gdxincname", db.getName());
	        
	        //run GAMSJob with the option and input data
	        ieee14ED.run(opt, db);
	        
	        //store the genP of all the active buses.
	        double[] genP=new double[net.getNoActiveBus()];
	        int idx=0;
	        //iterate the output of "genp" 
	        for (GAMSVariableRecord rec : ieee14ED.OutDB().getVariable("genp")){
	            System.out.println("genP @ Bus-" + rec.getKeys()[0]+"  : level=" + rec.getLevel() + " ,  marginal=" + rec.getMarginal());
	            genP[idx++] = rec.getLevel();
	        }
	        
	        //update the genP of gen buses in the  network
	        idx=0;
	        for(AclfBus b: net.getBusList()){
	                if(b.isGen()){
	                        b.setGenP(genP[idx]);
	                }
	                idx++;
	        }
	        
	        //perform dc load flow to check whether there is any line rating violation
	        DclfAlgorithm algo = DclfObjectFactory.createDclfAlgorithm(net);
	        
	        algo.calculateDclf();
	        
	        //NOTE: the branch mva rating data is not defined in input IEEE14 Bus system 
	        System.out.println(DclfOutFunc.dclfResults(algo, true));
	        
	        
	        
	        
	    }
	    static String modelStr=
	        "$Title ED model                                                 \n"+ 
	        "Sets                                                            \n"+
	        "         i   generating unit                                    \n"+
	        "         j   load bus;                                          \n"+
	        "                                                                \n"+
	        "Parameters                                                      \n"+
	        "        genPLow(i) lower  bound of each generating unit         \n"+
	        "        genPUp(i) upper bound of each generating unit           \n"+
	        "        loadP(j) demand at load bus j;                          \n"+
	        "                                                                \n"+
	        "Parameter FactorA(i) weighting factor A for generation cost function; \n"+
	        "Parameter FactorB(i) weighting factor B for generation cost function; \n"+
	        "Parameter FactorC(i) weighting factor C for generation cost function; \n"+
	        "                                                                \n"+
	        "$gdxin %gdxincname%                                             \n"+
	        "$LOAD  i j genPLow genPUp  loadP FactorA FactorB FactorC        \n"+
	        "$GDXIN                                                          \n"+
	        "                                                                \n"+
	        "Variables                                                       \n"+
	        "         genp(i)     generation output for each generating unit  \n"+
	        "         ft       total generation cost ;                       \n"+

	        "Equations                                                       \n"+
	        "        cost            define objective function               \n"+
	        "        powerBalance    real power balance of the whole network; \n"+
	        "                                                                \n"+
	        "cost.. ft =e= sum(i,  FactorA(i)+FactorB(i)*genp(i)+FactorC(i)*genp(i)*genp(i));\n"+
	        "powerBalance ..      sum(i, genp(i)) =g= sum(j,loadP(j)) ;         \n"+
	        "  genp.lo(i)  =  genPLow(i);                                       \n"+
	        "  genp.up(i)  =  genPUp(i);                                        \n"+
	        "                                                                \n"+
	        "Model transport /all/ ;                                         \n"+
	        "                                                                \n"+
	        "solve transport using nlp minimizing ft ;                       \n"+
	        "                                                                \n"+
	        "display genp.l, genp.m ;                                        \n";

}
