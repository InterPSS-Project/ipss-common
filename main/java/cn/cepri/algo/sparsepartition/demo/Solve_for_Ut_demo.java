package cn.cepri.algo.sparsepartition.demo;

import java.util.ArrayList;
import java.util.HashMap;

import cn.cepri.algo.sparsepartition.Scs_coordinate.Scs_coord;
import cn.cepri.algo.sparsepartition.Scs_coordinate.UValue;
import cn.cepri.algo.sparsepartition.Scs_partition_utilities;
import cn.cepri.algo.sparsepartition.Scs_subnet;
public class Solve_for_Ut_demo {
	//solves Ut, the coordinate 
	 public static void main(String[] args) {	
	 int s = -1;
	 HashMap<Integer,Double> inj = new HashMap<Integer, Double>();
	 for(int i=0;i<args.length;i++)
		 
	 {
		 ArrayList<Scs_partition_utilities.branch> intBrlist;
		 ArrayList<Scs_partition_utilities.branch> bordBrlist;
		 if(i==0)
		 {
			 // note that the first file is going to be a YTT system;
			 Scs_coordinate scsMain=new Scs_coordinate().new Scs_coordinate();
			 intBrlist=scsMain.YTTBranch;
			 bordBrlist=null; 
		 }
		 else
		 {
			 // for any 
			 Scs_subnet sub=new Scs_subnet();
			 intBrlist=sub.intbranch;
			 bordBrlist=sub.bordbranch;
		 	}
		 Scs_partition_utilities.load(args[i],intBrlist,bordBrlist,s,inj);
	 	}   
	 	csMain.solve();
	 	ArrayList<UValue> resultU=scsMain.getResult(); //get UT

	 }
}
