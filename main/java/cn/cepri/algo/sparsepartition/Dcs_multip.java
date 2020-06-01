package cn.cepri.algo.sparsepartition;

import java.util.Arrays;

import cn.cepri.algo.sparsepartition.Dcs_common.Dcs;


public class Dcs_multip {
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
	}
	
}


