package Double;

import java.util.ArrayList;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;


public class Scs_coordinate {

	
	ArrayList<Scs_subnet> list_Yii=new ArrayList<Scs_subnet>();
	ArrayList<branch> YTTBranch=new ArrayList<branch>();
	Dcs YTT;
	ArrayList<UValue> UTT=new ArrayList<UValue>();
	
	ArrayList<UValue> UAll=new ArrayList<UValue>();
	
	Scs_coordinate()
	{
		
	}
	
	public static class UValue
	{
		int oldNode;
		double value;
	}
	public static class branch
	{
		int fnd;
		int tnd;
		double x;
	}

	ArrayList<UValue> getResult()
	// Include all of UT, Ui
	{
		return UAll;
	}
	
	public void setBranch(int a,int b,double x)
	{
		branch br=new branch();
		br.fnd=a; //front node
		br.tnd=b; //terminal node 
		br.x=x;
		YTTBranch.add(br);
	}

	public void setYii(Scs_subnet s)
	// set Yii from all its internal nodes
	{
		list_Yii.add(s);
	}
	
	public void setYtt(Dcs a)
	//set YTT formed with all the border nodes 
	{
		YTT=a;
	}
	
	public void parallelForward()
	// get delta It, delta Ytt 
	{
		int i;
		int size=list_Yii.size();
		
		//TODO: will be replaced by parallel class;
		for(i=0;i<size;i++)
		{
			Scs_subnet s=list_Yii.get(i);
			s.forward();
		}
		
	}
	

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
		//work out delta Ytt, delta It 
		parallelForward();
		//get A and b (Ytt~ & It ~) form Ax=b; workout UT
		workoutUT();
		//backward, getUi, 
		parallelBackward();
		//Finalize the result set UALL of UTT, Ui (U for all nodes)
		getResult();
		
		
	}
	public void workoutUT()
	{
		Sum_delta_Ytt();
		//Ax=b ,A already ok.
		
		
	}
	
	public void Sum_delta_Ytt() {
		//calculate the sum of delta Ytt
		int i;
		int size;
		size=list_Yii.size();
		for(i=0;i<size;i++)
		{
			Scs_subnet s=list_Yii.get(i);
			Dcs deltaYtt=s.getDeltaYtt();
			add_delta_Ytt(deltaYtt);
		}
		
//	Scs Ytt = cs_Ytt();
//	ArrayList<Scs> set_Yii = Scs_subnet.cs_List_Yii();
//	ArrayList<Scs> set_delta_Ytt 6= new ArrayList<>();
//	int size=set_Yii.size();
//		for(int i=0; i<size; i++) {
//		Scs delta_Ytt_i =Scs_subnet.cs_delta_Ytt_i();
//		set_delta_Ytt.add(delta_Ytt_i);
//		add_deladd_delta_Yttta_Ytt(Ytt,set_delta_Ytt.get(i));
//		}
	}

	public void add_delta_Ytt(Dcs delta_Ytt) {
		//renews Ytt by adding a delta_Ytt_i to the corresponding nodes
		
	}
	
	
	

}
