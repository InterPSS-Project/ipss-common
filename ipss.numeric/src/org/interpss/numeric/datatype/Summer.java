package org.interpss.numeric.datatype;

public class Summer {
    private double sum = 0.0;         // current value

    /**
     * Initializes a new Summer starting at 0.0.
     *
     */
    public Summer() {
    } 

    /**
     * add x to the sum.
     */
    public void add(double x) {
        sum += x;
    } 

    /**
     * Returns the current value of sum.
     *
     * @return the current value of sum
     */
    public double getSum() {
        return sum;
    } 

    /**
     * Returns a string representation of this summer.
     *
     * @return a string representation of this summer
     */
    public String toString() {
        return "sum = " + sum;
    }
}