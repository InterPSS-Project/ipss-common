package Double;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import Double.Scs_coordinate.branch;
import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;
import edu.emory.mathcs.csparsej.tdouble.Dcs_entry;

/**
 * @author ajian
 *
 */
public class Scs_partition_utilities {
	//serve for the Scs_partitioned; not essential for the partitioned operation to be realized  
//	public void getMat(String s){
//		
//	}
//	public void setRhs(String s){
//		
//	}
//	
//	public int [] setExt_nodes() {
//		
//	}
//	public int [] setInt_nodes() {
//		
//	}
//	
	public static int putElement(int i, int j, double x, int rowsum)
	//i == row indices
	{
		HashMap<Integer, Double> oneRow;				if(i+1>rowsum)
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
	public static ArrayList<HashMap<Integer, Double>> mat=new ArrayList<HashMap<Integer, Double>>(); //temporary container for i.j.x of an element in the matrix
	
	
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
	
	//print any Dcs matrix
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
	
	public static int load(String fileName, 
			ArrayList<Scs_coordinate.branch> intList,
			ArrayList<Scs_coordinate.branch> bordList,
			Integer snd,
			HashMap<Integer,Double>injMap)
	
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
	        int i, j;
	        double x;
	        //Scs T;
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
	        System.out.println(fileName+" readed success");
	        return snd;
	    }
		
	
	public static void getSubBlock(Dcs A, int i1, int i2, int j1,int j2, Dcs subB)
	// get the subblock of a matrix 
	{
	}
	}
