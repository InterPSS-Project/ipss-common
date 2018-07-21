package org.interpss.numeric.datatype;

public class Triplet {
	

	private double v1 = 0.0,v2 = 0.0,v3 = 0.0;
	
	
	public Triplet(double value1,double value2,double value3){
		this.v1 = value1;
		this.v2 = value2;
		this.v3 = value3;
	}
	
	public double getValue1(){
		return this.v1;
	}
	
	public double getValue2(){
		return this.v2;
	}
	
	public double getValue3(){
		return this.v3;
	}
	
	public double[] toArray(){
		return new double[]{this.v1,this.v2,this.v3};
	}
	

}
