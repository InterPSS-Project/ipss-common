package cn.cepri.algo.sparsepartition;

import java.util.ArrayList;

import cn.cepri.algo.sparsepartition.Dcs_common.Dcs;
import cn.cepri.algo.sparsepartition.Scs_partition_utilities.branch;


public class Scs_coordinate {
	// Works out Ut and It 

	
	ArrayList<Scs_subnet> list_Yii=new ArrayList<Scs_subnet>();
	ArrayList<branch> YTTBranch=new ArrayList<branch>();
	Dcs YTT;
	ArrayList<UValue> UT=new ArrayList<UValue>();
	
	ArrayList<UValue> UAll=new ArrayList<UValue>();
	
	
	Scs_coordinate()
	{
		
	}
	
	public static class UValue
	{
		int oldNode;
		double value;
	}

	ArrayList<UValue> getResult()
	// Include all of UT, Ui
	{
		return UAll;
	}
	public void sum_delta_Ytt() {
		
	}
	public void setYtt(Dcs a)
	//set YTT formed with all the border nodes 
	{
		YTT=a;
	}
	
	
	public void setBranch(int a,int b,double x)
	{
		branch br=new branch();
		br.fnd=a; //front node
		br.tnd=b; //terminal node 
		br.x=x;
		YTTBranch.add(br);
	}
//
//	public void parallelForward()
//	// get delta It, delta Ytt 
//	{
//		int i;
//		int size=list_Yii.size();
//		
//		//TODO: will be replaced by parallel class;
//		for(i=0;i<size;i++)
//		{
//			Scs_subnet s=list_Yii.get(i);
//			s.forward();
//		}
//		
//	}
	

	public void parallelBackward()
	{
		int i;
		int size=list_Yii.size();
		//TODO: will be replaced by parallel class;
		for(i=0;i<size;i++)
		{
			Scs_subnet s=list_Yii.get(i);
			s.backward(UTT);
		}
		
	}

	//work out UT
	public void solve()
	{
//		work out delta Ytt, delta It 
//		parallelForward();
//		//get A and b (Ytt~ & It ~) form Ax=b; workout UT
//		workoutUT();
//		//backward, getUi, 
//		parallelBackward();
//		//Finalize the result set UALL of UTT, Ui (U for all nodes)
//		getResult();
		
		
		
		
		
		
		workoutUT();
		
	}
	public void workoutUT()
	{
		Sum_delta_Ytt();
		Sum_delta_It();
		//Ax=b ,A already ok.
		
	}
	
	public void Sum_delta_Ytt() {
		//calculate the sum of delta Ytt
		int i;
		int size;

	public void Sum_delta_It() {
		
	}
	
	
	

}
