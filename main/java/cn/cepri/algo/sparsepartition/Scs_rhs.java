package cn.cepri.algo.sparsepartition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Scs_rhs {
	// creates a sparse right hand side vector for linear system Ax=b
	
	public static double[] cs_set_rhs(HashMap<Integer, Double> injmap,HashMap<Integer,Integer> otn,int rowsm) {
		//get rhs for Ax=b from an injection map
		double [] b=new double [rowsm];
		Set<Integer>keys=injmap.keySet();
		Iterator iterator1=keys.iterator();
		while (iterator1.hasNext()){
			  int old_j = (int)iterator1.next();
			  double x=injmap.get(old_j);
			  int new_j=otn.get(old_j);
			  b[new_j]=x;
		}
		return b;
	}
	

}
