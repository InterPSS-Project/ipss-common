package org.interpss.tutorial.ch3_loadflow;
/*
*
* Copyright (C) 2006-2013 www.interpss.org
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
* @Author Tony Huang
* @Version 1.0
* @Date 12/23/2013
* 
*   Revision History
*   ================
*
*/


import org.apache.commons.math3.complex.Complex;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.sparse.ISparseEqnMatrix2x2;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.impl.DefaultNrSolver;
import com.interpss.simu.util.sample.SampleCases;

public class CustomLoadFlowExample {

			
        /**
         * Define a custom NR solver 
         *
         */
        static class CustomNrSolver extends DefaultNrSolver {
                public CustomNrSolver(AclfNetwork net) {
                        super(net);
                }

                /**
                 * formJMatrix method is called at the beginning of each NR iteration
                 */
                @Override
                public ISparseEqnMatrix2x2 formJMatrix() {
                        // create network J-matrix with one extra-dimension
                	    // such that upto two addtional equations can be considered 
                	    // and included in the augmented Jacobian equations.
                        ISparseEqnMatrix2x2 lfEqn = aclfNet.formJMatrix(1);
                        
                        // create a 2x2 matrix element
                        Matrix_xy m = new Matrix_xy();
                        m.xx = 2.0;
                        m.xy = 0.0;
                        m.yx = 0.0;
                        m.yy = 2.0;
                        
                        // set the matrix element to J-matrix
                        int n = aclfNet.getNoBus();
                        
                        // index is 0-based, which means the index originally is 0,1...n-1, now the last element index is n
                        lfEqn.setA(m, n, n); 
                        
                        return lfEqn;
                }

                // this is dummy variable for setting the extra mismatch field
                private double mis = 1.0;
                
                /**
                 * setPowerMismatch method is called at the beginning of each NR iteration
                 */
                @Override
                public void setPowerMismatch(ISparseEqnMatrix2x2 lfEqn) {
                        // calculate bus power mismatch. The mismatch stored on 
                        // the right-hand side of the sparse eqn
                        super.setPowerMismatch(lfEqn);
                        
                        // define a 2x1 vector
                        Complex b = new Complex(1.0, 1.0);
                
                        // set the vector to the right-hand side of the sparse eqn
                        int n = aclfNet.getNoBus();
                        //Again, index is 0-based, which means the index originally is 0,1...n-1, and now the last element index is n
                        
                        lfEqn.setB(b, n);
                }
                
                /**
                 * updateBusVoltage method is called at at the end of each NR iteration, after the 
                 * sparse eqn has been solved. The results of the sparse eqn solution is stored in the
                 * sparse eqn.
                 */
                @Override
                public void updateBusVoltage(ISparseEqnMatrix2x2 lfEqn) {
                        // update the bus voltage using the solution results store in the sparse eqn
                        super.updateBusVoltage(lfEqn);
                        
                        // the solution result of the extra variable defined is stored at B(n)  
                        int n = aclfNet.getNoBus();
                        System.out.println("mis: " + this.mis + "  ---> " + lfEqn.getX(n));
                        
                        // reduce the dummy variable so that the loadflow can converge  
                        this.mis *= 0.1;
                }
        }
		        
        public static void main(String args[]) {
        	  //Initialize logger and Spring config
			  IpssCorePlugin.init();
           
                
                // create a sample 5-bus system for Loadflow 
                AclfNetwork net = CoreObjectFactory.createAclfNetwork();
                SampleCases.load_LF_5BusSystem(net);
                //System.out.println(net.net2String());

                // create a Loadflow algo object
                LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();

                // set algo NR solver to the CustomNrSolver
                algo.setNrSolver(new CustomNrSolver(net));

                // run Loadflow
                try {
					net.accept(algo);
				} catch (InterpssException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                // output loadflow calculation results
                System.out.println(AclfOutFunc.loadFlowSummary(net));
        }       
}



