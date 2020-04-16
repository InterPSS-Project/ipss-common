package demo;

import java.util.ArrayList;
import java.util.HashMap;

import cn.cepri.algo.sparsepartition.Scs_coordinate;
import cn.cepri.algo.sparsepartition.Scs_partition_utilities;
import cn.cepri.algo.sparsepartition.Scs_subnet;

public class Dcs_partition_Ut_demo {
	// gets UT

	public static void main(String[] args) {
		 Scs_coordinate scsMain=new Scs_coordinate();
		 int s = -1;
		 HashMap<Integer,Double>inj = new HashMap<Integer, Double>();
		 for(int i=0;i<args.length;i++)
		 {
			 ArrayList<Scs_coordinate.branch> intBrlist;
			 ArrayList<Scs_coordinate.branch> bordBrlist;
			 
			 if(i==0)
			 {
				 intBrlist=scsMain.YTTBranch;
				 bordBrlist=null;
			 }
			 else
			 {
				 Scs_subnet sub=new Scs_subnet();
				 intBrlist=sub.intbranch;
				 bordBrlist=sub.bordbranch;
			 }
			 
			 Scs_partition_utilities.load(args[i],intBrlist,bordBrlist,s,inj); //read the file
		 }   
		//	setYtt();

//	workoutUt;
	  }
}
