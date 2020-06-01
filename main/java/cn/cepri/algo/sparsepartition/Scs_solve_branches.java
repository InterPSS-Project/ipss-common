package cn.cepri.algo.sparsepartition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import cn.cepri.algo.sparsepartition.Dcs_compress;
import cn.cepri.algo.sparsepartition.Dcs_entry;
import cn.cepri.algo.sparsepartition.Dcs_ipvec;
import cn.cepri.algo.sparsepartition.Dcs_lsolve;
import cn.cepri.algo.sparsepartition.Dcs_lu;
import cn.cepri.algo.sparsepartition.Dcs_sqr;
import cn.cepri.algo.sparsepartition.Dcs_usolve;
import cn.cepri.algo.sparsepartition.Dcs_util;
import cn.cepri.algo.sparsepartition.Dcs_common.Dcs;
import cn.cepri.algo.sparsepartition.Dcs_common.Dcsn;
import cn.cepri.algo.sparsepartition.Dcs_common.Dcss;
import cn.cepri.algo.sparsepartition.Scs_coordinate.branch;

public class Scs_solve_branches {
	// For verification only
	//solves Ax=b as a whole by loading the coordinate file and subnet files 
	
	public static ArrayList<HashMap<Integer, Double>> mat=new ArrayList<HashMap<Integer, Double>>(); //temporary container for i.j.x of an element in the matrix
	
	public static void showMatrix(Dcs A)
	{
		int m,n,Ai[],Ap[];
		double Ax[];
		m=A.m;
		n=A.n;
		Ai=A.i;
		Ap=A.p;
		Ax=A.x;
		double R[][]=new double[m][n];
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				for(int p=Ap[j];p<Ap[j+1];p++)
				{
					if(Ai[p]>i)
						break;
					else if(Ai[p]==i)
					{
						R[i][j]=Ax[p];
						break;
					}
				}
			}
			System.out.println(Arrays.toString(R[i]));
		
		}
		System.out.println("p"+Arrays.toString(Ap));
		System.out.println("i"+Arrays.toString(Ai));
		System.out.println("x"+Arrays.toString(Ax));
		}
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
	
    public static double[] cs_gaxpy(Dcs A, double[] x, double[] y) {
        int p, j, n, Ap[], Ai[];
        double Ax[];
        if (!Dcs_util.CS_CSC(A) || x == null || y == null)
            return (null); /* check inputs */
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        for (j = 0; j < n; j++) {
            for (p = Ap[j]; p < Ap[j + 1]; p++) {
                y[Ai[p]] += Ax[p] * x[j];
            }
        }
        return (y);
    }

	
	public static void hash_mat_to_dcs(ArrayList<HashMap<Integer,Double>>Amat, Dcs T) {
		//collect all keys/ column numbers in Amat 
		for(int i = 0; i<Amat.size();i++) 
		{
			HashMap<Integer,Double>one_row=new HashMap<Integer, Double>();
			one_row=Amat.get(i);
			Set<Integer> keys=one_row.keySet();
		    Iterator iterator1=keys.iterator();
			while (iterator1.hasNext()){
				 	int j =(int)iterator1.next();
				 	double x =one_row.get(j);
				 	Dcs_entry.cs_entry(T,i,j,x);
			 		}	
	    }
		System.out.println(Arrays.toString(T.i));
		System.out.println(Arrays.toString(T.p));
		System.out.println(Arrays.toString(T.x));

	}

	public static int allocate_entries(ArrayList<Scs_coordinate.branch> A,HashMap<Integer,Integer>H,int r,Integer rnd )
	{
		for(int i =0; i<A.size(); i++) {
			int new_i=0;
			int new_j=0;
			branch Lbr =A.get(i);
			int old_i= Lbr.fnd;//sort the indexes by removing the snd list and then record the old, new positions of the nodes respectively 
			int old_j= Lbr.tnd;
			double x = Lbr.x;
			if (old_i==rnd) {
			new_j=H.get(old_j);
			r=putElement(new_j,new_j,x,r);
			}
			else if (old_j==rnd) {
			new_i=H.get(old_i);
			r=putElement(new_i,new_i,x,r);
			}
			else if(old_j==old_i) {
			new_i=H.get(old_i);
			r=putElement(new_i,new_i,-x,r);
			}
			else {
			new_i=H.get(old_i);
			new_j=H.get(old_j);
			r=putElement(new_i,new_i,x,r);
			r=putElement(new_i,new_j,-x,r);
			r=putElement(new_j,new_i,-x,r);
			r=putElement(new_j,new_j,x,r);
			}
		}
		return(r);
	}
	public static void printMatrix(String remark,Dcs A)
	{
		int m,n,Ai[],Ap[];
		double Ax[];
		m=A.m;
		n=A.n;
		Ai=A.i;
		Ap=A.p;
		Ax=A.x;
		double R[][]=new double[m][n];
		System.out.println(remark);
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				for(int p=Ap[j];p<Ap[j+1];p++)
				{
					if(Ai[p]>i)
						break;
					else if(Ai[p]==i)
					{
						R[i][j]=Ax[p];
						break;
					}
				}
			}
			System.out.println(Arrays.toString(R[i]));
		}
	}
    public static void cs_lusol(int order, Dcs A, double[] b, double tol) {
        double[] x;
        Dcss S;
        Dcsn N;
        int n;
        boolean ok;
        if (!Dcs_util.CS_CSC(A) || b == null)
            return ; /* check inputs */
        n = A.n;
        S = Dcs_sqr.cs_sqr(order, A, false); /* ordering and symbolic analysis */
        N = Dcs_lu.cs_lu(A, S, tol); /* numeric LU factorization */
        x = new double[n]; /* get workspace */
        ok = (S != null && N != null);
        if (ok) {
            Dcs_ipvec.cs_ipvec(N.pinv, b, x, n); /* x = b(p) */
            Dcs_lsolve.cs_lsolve(N.L, x); /* x = L\x */
            Dcs_usolve.cs_usolve(N.U, x); /* x = U\x */
            Dcs_ipvec.cs_ipvec(S.q, x, b, n); /* b(q) = x */
        }
        return;
    }
    
			public static void old_tnew_index(ArrayList<Scs_coordinate.branch> L, HashMap<Integer,Integer> otn,Integer rnd){
				//L:arraylist contains all branches from the old list; otn:  list of hashmap for recording old to new index; rnd: reference node
				//find all the different nodes in the net and then re-arranging them, put the old and new nodes in a new structure
				ArrayList<Integer> old_ind = new ArrayList<Integer>(); //temporary list for temporary containers
				for (int k=0;k<L.size();k++) {
					branch Lbr =L.get(k);
					int i = Lbr.fnd;
					int j = Lbr.tnd;
					if (!old_ind.contains(i)  &&  i != rnd) {
						old_ind.add(i);
					}
					if (!old_ind.contains(j) && j != rnd) {
						old_ind.add(j);
					}
				}
				for(int m=0; m<old_ind.size(); m++) {
					int xin=m;
					int old=old_ind.get(m);
					otn.put(old,xin);
					System.out.println("Old and new:"+old+"="+xin);
				}
			}
			
			
			//scs.sc_entry don't support value addition, so first put element in mat , then convert it to scs.
			public static int putElement(int i, int j, double x, int rowsum)
			//i == row indices
			{
				HashMap<Integer, Double> oneRow;				
				if(i+1>rowsum)
				{
					int k=rowsum;
					rowsum=i+1;
					for(;k<rowsum;k++)
					{
						oneRow=new HashMap<Integer, Double>();
						mat.add(oneRow);
					}
				}
				oneRow=mat.get(i);
				Double oldElement=oneRow.get(j);
				if(oldElement!=null) {
					x+=oldElement;
				}
				oneRow.put(j,x);
			
		//	System.out.println(j+";"+x+";"+rowsum+";"+i);
			return rowsum;
				
			}
			public static int load (String fileName,ArrayList<Scs_coordinate.branch> intList,
					ArrayList<Scs_coordinate.branch> bordList,HashMap<Integer, Double> injMap,
					int snd)
			
			{
				//for corordinate b=null
				//put i j x -> branch , then a.add(branch)
				//file format:
				//  intbranch
				//  i j x
				//  ...
				//  bordbranch
				//  i j x 
				//  ...
			  	        //Scs T;
					int i, j;
					double x;
			        BufferedReader in;
			        try {
			            in = new BufferedReader(new FileReader(fileName));
			        } catch (FileNotFoundException e1) {
			            return snd ;
			        }
			    //    T = Scs_util.cs_spalloc(0, 0, 1, true, true); /* allocate result */
			        String line;
			        int fieldnum=3;
			        ArrayList<Scs_coordinate.branch> alist=null;
			        HashMap<Integer,Double> amap=null;
			        
			        try {
			            while ((line = in.readLine()) != null) {
			            	if(0==line.compareTo("intbranch")) 
			            	{
			            		alist=intList;
			            		continue;
			            	}
			            		
			            	else if (0==line.compareTo("bordbranch"))
			            	{
			            		alist=bordList;
			            		continue;
			            	}
			            	else if (0==line.compareTo("injection"))
			            	{
			            		amap=injMap;
			            		fieldnum=2;
			            		continue;
			            	}
			            	else if (0==line.compareTo("referencenode"))
			            	{
			            		fieldnum=1;
			            		continue;
			            		
			            	}
			                String[] tokens = line.trim().split("\\s+");
			                
			                if (tokens.length != fieldnum) {
			                    continue;
			                }
			                if(3==fieldnum)
			                {
				                i = Integer.parseInt(tokens[0]);
				                j = Integer.parseInt(tokens[1]);
				                x = Double.parseDouble(tokens[2]);
				              //  if (!Scs_entry.cs_entry(T, i, j, x))
				              //      return (null);
				                branch ab=new branch();
				                ab.fnd=i;
				                ab.tnd=j;
				                ab.x=x;
				                if(alist==null)
				                	return snd;
				                alist.add(ab);
			                }
			                else if(2==fieldnum)
			                {
				                i = Integer.parseInt(tokens[0]);
				                x = Double.parseDouble(tokens[1]);
				                amap.put(i,x);
			                }
			                else if(1==fieldnum)
			                {
				                i = Integer.parseInt(tokens[0]);
				                snd = i;
			                }
			            }
			        } catch (IOException e) {
			            return snd;
			        }
			        System.out.println(fileName+": read success");
			        return snd;
			    }
			
	 public static void main(String[] args) {		
		 
		 		int s=-1; //initialize the reference node
				ArrayList<Scs_coordinate.branch> intlist=new ArrayList<Scs_coordinate.branch>(); 
				ArrayList<Scs_coordinate.branch> bordlist=new ArrayList<Scs_coordinate.branch>();
				HashMap<Integer, Double> inj=new HashMap<Integer, Double>(); // for rhs 
				
				for (int i=0; i<args.length; i++) {

					 s=load(args[i],intlist,bordlist,inj,s);
				 }
				
				ArrayList<Scs_coordinate.branch>alllist;// = new ArrayList<Scs_coordinate.branch>();
				intlist.addAll(bordlist);
				alllist = intlist;
				HashMap<Integer, Integer>otnmap = new HashMap<Integer, Integer>();
				old_tnew_index(alllist,otnmap,s);// get the old list to new list;
				Dcs T = Dcs_util.cs_spalloc(0, 0, 1, true, true);
				int rowsum=0;
				rowsum =allocate_entries(alllist,otnmap,rowsum,s);
				//mat to Dcs
				hash_mat_to_dcs(mat, T);
				Dcs C = Dcs_compress.cs_compress(T);
				double [] b = cs_set_rhs(inj, otnmap, rowsum);
				int order =2;
				double tol=0.00001;
				System.out.println("Your rhs is: "+Arrays.toString(b));
				cs_lusol(order,C,b,tol);
				double [] y=new double[9];
				y = cs_gaxpy(C,b,y);
				System.out.println("b:"+Arrays.toString(b));
				System.out.println("y:"+Arrays.toString(y));

			}

		 
		 
	 }
