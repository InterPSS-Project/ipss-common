package cn.cepri.algo.sparsepartition;


import java.util.ArrayList;
import java.util.HashMap;

import cn.cepri.algo.sparsepartition.Dcs_common.Dcs;
import cn.cepri.algo.sparsepartition.Dcs_common.Dcsn;
import cn.cepri.algo.sparsepartition.Scs_coordinate.UValue;
import cn.cepri.algo.sparsepartition.Scs_partition_utilities.branch;

public class Scs_subnet {
	// process the subnets 
	
	int rnd; //reference node (not taking account into the subnet) 
	ArrayList<branch> intbranch=new ArrayList<branch>();
	ArrayList<branch> bordbranch=new ArrayList<branch>();
	
	Dcs Yiidev;
	Dcsn YiiLU;
	
	Dcs deltaYtt;//
	Dcs LDii;
	Dcs UiT;
	Dcs Uii;
	
	int intSize; //size of inner network
	
	ArrayList<Integer> intNd=new ArrayList<Integer>();
	ArrayList<Integer> bordNd=new ArrayList<Integer>();
	HashMap<Integer,Integer> oldtNew= new HashMap<Integer,Integer>();
	ArrayList<UValue> Ui=new ArrayList<UValue>();

	 Scs_subnet()
	{
		
	}
	
	 //temporary container for i.j.x of an element in the matrix
	public static ArrayList<HashMap<Integer, Double>> mat=new ArrayList<HashMap<Integer, Double>>();

	//temporary container for i.j.x of an element in the matrix Yii, Yit, Yti; 
	
	public ArrayList<UValue> getUi()
	// only returns U of subnets
	{
		return Ui;
	}
	
	
	public void setIntbranch(int a,int b,double x)
	//set internal branches 
	{		
		branch br=new branch();
		br.fnd=a; //front node
		br.tnd=b; //terminal node 
		br.x=x;
		intbranch.add(br);
	}
	
	public void setBordbranch(int a,int b,double x)
	//set border branches
	{
		branch br=new branch();
		br.fnd=a; //front node 
		br.tnd=b; // terminal node 
		br.x=x;
		bordbranch.add(br);
	}
	
	public void backward(ArrayList<UValue> UTT)
	// get U for each branch in a subnet
	{
		
	}
	
	public void formMat() {
		//generate Yiidev for Yii, Yit, Yti
		Dcs T = Dcs_util.cs_spalloc(0, 0, 1, true, true);
		ArrayList<branch>InL =	intbranch;	
		ArrayList<branch>ExL =	bordbranch;
		//same method of adding the elements used for demo solve_branches
		sortnode();
		InL.addAll(ExL);  
		intSize=ExL.size();
		old_tnew_index();// get the old list to new list;
		int rowsm=0;
		rowsm =Scs_partition_utilities.allocate_entries(InL,oldtNew,rowsm,rnd);
		//here we end up with a complete matrix; mat obtained from the structure; 
		
		
	}
	public void old_tnew_index(){
		//L:arraylist contains all branches from the old list; otn:  
		//list of hashmap for recording old to new index; rnd: reference node
		//find all the different nodes in the net and then re-arranging them,
	    //put the old and new nodes in a new structure
		ArrayList<Integer> old_ind = new ArrayList<Integer>(); // list as temporary containers
		int s =intNd.size();
		for (int w=0;w<s;w++) {
			int i = intNd.get(w);
			if (!old_ind.contains(i) && i != rnd) {
				old_ind.add(i);
			}
		}
		for (int v=0; v<bordNd.size();v++) {
			int j =bordNd.get(v);
			if (!old_ind.contains(j)  &&  j != rnd) {
				old_ind.add(j);
			}
		
		}
		for(int m=0; m<old_ind.size(); m++) {
			int xin=m;
			int old=old_ind.get(m);
			oldtNew.put(old,xin);
			System.out.println("Old and new:"+old+"="+xin);
		}
		
	}
	public void sortnode() {
	// sort old nodes and replace them with new nodes 
		int i;
		for(i=0;i<intbranch.size();i++)
		{
			int nd;
			branch br=intbranch.get(i);
			nd=br.fnd;
			if(-1==intNd.indexOf(nd))
				intNd.add(nd);
			nd=br.tnd;
			if(-1==intNd.indexOf(nd))
				intNd.add(nd);
		}
		for(i=0;i<bordbranch.size();i++)
		{
			int nd;
			branch br=bordbranch.get(i);
			nd=br.fnd;
			if(-1!=intNd.indexOf(nd))
				nd=br.tnd;
	
			if(-1==bordNd.indexOf(nd))
				bordNd.add(nd);
		}
		//create Yiidev
	}

	public void setYii(Scs_subnet s)
	// set Yii from all its internal nodes
	{
		list_Yii.add(s);
	}
	
	

//	
	public void forward() {
		formMat();
		Dcs T = Dcs_util.cs_spalloc(0,0,1,true,false);
		Scs_partition_utilities.hash_mat_to_dcs(mat,T);
		Dcs C =Dcs_compress.cs_compress(T);
		YiiLU = Scs_partition_utilities.ludev(C, intSize);
		getSubBlock();
		
	}
	
	public void getSubBlock()
	{
//		getSubBlock(): take LDii, Uii, UiT, deltaYtt from Yiidev
		int i1,i2,j1,j2;
		i1=0;
		i2=intSize;
		j1=0;
		j2=intSize;
		LDii = getSubBlock(YiiLU.L, i1, i2, j1, j2);

		i1=0;
		i2=intSize;
		j1=intSize;
		j2=Yiidev.n;
		UiT = getSubBlock(YiiLU.U, i1, i2, j1, j2);

		i1=0;
		i2=intSize;
		j1=0;
		j2=intSize;
		Uii = getSubBlock(YiiLU.U, i1, i2, j1, j2);

		i1=intSize;
		i2=Yiidev.n;
		j1=intSize;
		j2=Yiidev.n;
		deltaYtt = getSubBlock(YiiLU.L, i1, i2, j1, j2);
	}
	
	public static Dcs getSubBlock(Dcs A, int i1, int i2, int j1,int j2) {
	// get the subblock from a block matrix  (get delta Ytt i), from i1 to i2 ; j1 to j2 
	Dcs subB = Dcs_util.cs_spalloc(0,0,1,true,false);
	int [] Ai, Ap;
	double [] Ax;
	Ai=A.i;
	Ap=A.p;
	Ax=A.x;
	for(int i= i1; i<i2+1;i++) 
	{
		for(int j= j1; j<j2+1; j++) 
		{
			for(int p=Ap[j];p<Ap[j+1];p++)
			{
				if(Ai[p]>i) 
				break;
				else if(Ai[p]==i)
				{
					if (!Dcs_entry.cs_entry(subB, i, j, Ax[p]))
						return (null);
				}
				
				}
			}
		
		}
			return subB;
	}
	

	
//	public Dcs Verify(Dcs A, Dcs B) {
//		// verifies the answer by multiplying: A:Lti 	
//		//get B: Uit 
//		//multiply:
//	int a=A.nzmax;		//intsize of a, b
//	int b=B.nzmax;
//	Dcsn NA = Scs_utilities.ludev(A,a);
//	Dcsn NB = Scs_utilities.ludev(B,b);
//	Dcs LA= NA.L;
//	Dcs UB= NB.U;
//	Dcs deltaYtt = Dcs_multiply.cs_multiply(LA, UB); 
//	return deltaYtt;
//	}
	
}
