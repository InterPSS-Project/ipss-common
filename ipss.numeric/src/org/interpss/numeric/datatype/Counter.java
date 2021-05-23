package org.interpss.numeric.datatype;

public class Counter {
    private int count = 0;         // current value

    /**
     * Initializes a new counter starting at 0
     *
     */
    public Counter() {
    } 

    /**
     * Increments the counter by 1.
     */
    public void increment() {
        count++;
    } 

    /**
     * Returns the current value of this counter.
     *
     * @return the current value of this counter
     */
    public int getCount() {
        return count;
    } 

    /**
     * Returns a string representation of this counter.
     *
     * @return a string representation of this counter
     */
    public String toString() {
        return "count = " + count;
    }
}