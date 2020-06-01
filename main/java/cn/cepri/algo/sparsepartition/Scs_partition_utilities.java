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

import cn.cepri.algo.sparsepartition.Dcs_entry;
import cn.cepri.algo.sparsepartition.Dcs_spsolve;
import cn.cepri.algo.sparsepartition.Dcs_util;
import cn.cepri.algo.sparsepartition.Dcs_common.Dcs;
import cn.cepri.algo.sparsepartition.Dcs_common.Dcsn;

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
	public  ArrayList<HashMap<Integer, Double>> mat=new ArrayList<HashMap<Integer, Double>>(); //temporary container for i.j.x of an element in the matrix

	public static class branch
	{
		int fnd; //row
		int tnd; //column
		double x;
	}

//	
	public int putElement(int i, int j, double x, int rowsum)
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

	
	public int allocate_entries(ArrayList<Scs_partition_utilities.branch> A,
										HashMap<Integer,Integer>H,int r,Integer rnd )
	//adds the entries needed  to designated location from different 
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
	
	
	public static void old_tnew_index(ArrayList<Scs_partition_utilities.branch> L, HashMap<Integer,Integer> otn,Integer rnd){
		//L:arraylist contains all branches from the old list; otn:  
		//list of hashmap for recording old to new index; rnd: reference node
		//find all the different nodes in the net and then re-arranging them,
	    //put the old and new nodes in a new structure
		ArrayList<Integer> old_ind = new ArrayList<Integer>(); // list as temporary containers
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
	
	public static void dcs_to_hashmat(ArrayList<HashMap<Integer,Double>>Amat,Dcs T) {
		//convert a triplet formed matrix into a hash_mat
		
	}
	
	public static void hash_mat_to_dcs(ArrayList<HashMap<Integer,Double>>Amat, Dcs T) {
		// Convert a hash_mat to a triplet formed matrix
		//collect all keys/ column numbers in Amat 
		// T is in triplet form
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
	//returns the lower/upper tirangular decomp. of any sparse mat. 
	public static Dcsn ludev(Dcs A,int intSize) {//intSize is inner size
			int tol=0;
	        Dcs L, U;
	        Dcsn N;
	        double pivot, Lx[], Ux[], x[], a, t;
	        int Lp[], Li[], Up[], Ui[], pinv[], xi[], q[], n, ipiv, k, top, p, i, col, lnz, unz;
	        if (!Dcs_util.CS_CSC(A))//|| S == null
	            return (null); /* check inputs */
	        n = A.n;
	       // q = S.q;
	        q=null;
	       // lnz = S.lnz;
	      //  unz = S.unz;
	        lnz= 4 * (A.p[n]) + n;
	        unz= lnz;
	        
	        x = new double[n]; /* get float workspace */
	        xi = new int[2 * n]; /* get int workspace */
	        N = new Dcsn(); /* allocate result */
	        N.L = L = Dcs_util.cs_spalloc(n, n, lnz, true, false); /* allocate result L */
	        N.U = U = Dcs_util.cs_spalloc(n, n, unz, true, false); /* allocate result U */
	        N.pinv = pinv = new int[n]; /* allocate result pinv */
	        Lp = L.p;
	        Up = U.p;
	        for (i = 0; i < n; i++)
	            x[i] = 0; /* clear workspace */
	        for (i = 0; i < n; i++)
	            pinv[i] = -1; /* no rows pivotal yet */
	        for (k = 0; k <= n; k++)
	            Lp[k] = 0; /* no cols of L yet */
	        lnz = unz = 0;
	       // for (k = 0; k < n; k++) /* compute L(:,k) and U(:,k) */
	        for (k = 0; k < intSize; k++)
	        {
	            /* --- Triangular solve --------------------------------------------- */
	            Lp[k] = lnz; /* L(:,k) starts here */
	            Up[k] = unz; /* U(:,k) starts here */
	            if (lnz + n > L.nzmax) {
	                Dcs_util.cs_sprealloc(L, 2 * L.nzmax + n);
	            }
	            if (unz + n > U.nzmax) {
	                Dcs_util.cs_sprealloc(U, 2 * U.nzmax + n);
	            }
	            Li = L.i;
	            Lx = L.x;
	            Ui = U.i;
	            Ux = U.x;
	            col = q != null ? (q[k]) : k; 
	            top = Dcs_spsolve.cs_spsolve(L, A, col, xi, x, pinv, true); /* x = L\A(:,col) */
	            /* --- Find pivot --------------------------------------------------- */
	            ipiv = -1;
	            a = -1;
	            for (p = top; p < n; p++) {
	                i = xi[p]; /* x(i) is nonzero */
	                if (pinv[i] < 0) /* row i is not yet pivotal */
	                {
	                    if ((t = Math.abs(x[i])) > a) {
	                        a = t; /* largest pivot candidate so far */
	                        ipiv = i;
	                    }
	                } else /* x(i) is the entry U(pinv[i],k) */
	                {
	                    Ui[unz] = pinv[i];
	                    Ux[unz++] = x[i];
	                }
	            }
	            if (ipiv == -1 || a <= 0)
	                return (null);
	            if (pinv[col] < 0 && Math.abs(x[col]) >= a * tol)
	                ipiv = col;
	            /* --- ivide by pivot ---------------------------------------------- */
	            pivot = x[ipiv]; /* the chosen pivot */
	            Ui[unz] = k; /* last entry in U(:,k) is U(k,k) */
	            Ux[unz++] = pivot;
	            pinv[ipiv] = k; /* ipiv is the kth pivot row */
	            Li[lnz] = ipiv; /* first entry in L(:,k) is L(k,k) = 1 */
	            Lx[lnz++] = 1;
	            for (p = top; p < n; p++) /* L(k+1:n,k) = x / pivot */
	            {
	                i = xi[p];
	                if (pinv[i] < 0) /* x(i) is an entry in L(:,k) */
	                {
	                    Li[lnz] = i; /* save unpermuted row in L */
	                    Lx[lnz++] = x[i] / pivot; /* scale pivot column */
	                }
	                x[i] = 0; /* x [0.f.n-1] = 0 for next k */
	            }
	        }
	        /* --- Finalize L and U ------------------------------------------------- */
	        Lp[n] = lnz;
	        Up[n] = unz;
	        Li = L.i; /* fix row indices of L for final pinv */
	        for (p = 0; p < lnz; p++)
	            Li[p] = pinv[Li[p]];
	        Dcs_util.cs_sprealloc(L, 0); /* remove extra space from L and U */
	        Dcs_util.cs_sprealloc(U, 0);
	        return N;
	    }
		
	
	public static int load (String fileName,ArrayList<branch> intList,
			ArrayList<branch> bordList,HashMap<Integer, Double> injMap,
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
	        ArrayList<branch> alist=null;
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
		
}

