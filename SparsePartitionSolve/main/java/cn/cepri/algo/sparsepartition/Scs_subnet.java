package cn.cepri.algo.sparsepartition;


import java.util.ArrayList;

import cn.cepri.algo.sparsepartition.Scs_coordinate.UValue;
import cn.cepri.algo.sparsepartition.Scs_coordinate.branch;
import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;
import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcsn;
import edu.emory.mathcs.csparsej.tdouble.Dcs_spsolve;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;

public class Scs_subnet {
	// process the subnets 
	
	
	public ArrayList<branch> intbranch=new ArrayList<branch>();
	public ArrayList<branch> bordbranch=new ArrayList<branch>();
	
	Dcs Yiidev;
	Dcsn YiiLU;
	
	Dcs deltaYtt;
	Dcs LDii;
	Dcs UiT;
	Dcs Uii;
	
	int intSize; //size of inner network
	
	ArrayList<Integer> intNd=new ArrayList<Integer>();
	ArrayList<Integer> bordNd=new ArrayList<Integer>();
	
	ArrayList<UValue> Ui=new ArrayList<UValue>();
	
	public Scs_subnet()
	{
	
	}
	
	public ArrayList<UValue> getUi()
	// only returns U of subnets
	{
		return Ui;
	}
	
	
	public Dcs getDeltaYtt()
	{
		return deltaYtt;
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

	
	public void formmat() {
		//generate Yiidev for Yii, Yit, Yti
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
	public void sortnode() {
	// sort old nodes and replace them with new nodes 
	}
	
	public Dcsn ludev(Dcs A,int intSize) {//intSize is inner size
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
	            /* --- Sivide by pivot ---------------------------------------------- */
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
		
	
	public void forward() {
		// 
		formmat();
		
		Dcs A=Yiidev;
		YiiLU = ludev(A, intSize);
		getSubBlock();
	}
	
	//getSubBlock(): take LDii, Uii, UiT, deltaYtt from Yiidev
	public void getSubBlock()
	{
		int i1,i2,j1,j2;
		i1=0;
		i2=intSize;
		j1=0;
		j2=intSize;
		Scs_partition_utilities.getSubBlock(YiiLU.L, i1, i2, j1, j2, LDii);

		i1=0;
		i2=intSize;
		j1=intSize;
		j2=Yiidev.n;
		Scs_partition_utilities.getSubBlock(YiiLU.U, i1, i2, j1, j2, UiT);

		i1=0;
		i2=intSize;
		j1=0;
		j2=intSize;
		Scs_partition_utilities.getSubBlock(YiiLU.U, i1, i2, j1, j2, Uii);

		i1=intSize;
		i2=Yiidev.n;
		j1=intSize;
		j2=Yiidev.n;
		Scs_partition_utilities.getSubBlock(YiiLU.L, i1, i2, j1, j2, deltaYtt);
	}
	



}
