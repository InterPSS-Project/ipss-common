package cn.cepri.algo.sparsepartition.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)

public class UtilitiesTest {

	private int i;
	private int j;
	private int r;
	private double x; 

	
	public void putElementTest(int i,int j,int x, int r) {
		this.i = i;
		this.j=j;
		this.r=r;
		this.x=x;

	}
	@Parameterized.Parameters
	public static Collection Elements() {
		return Arrays.asList(new int[][][][] {
		});
		
	}

}
