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
     * Initializes a new counter starting at seed
     *
     */
    public Counter(int seed) {
    	this.count = seed;
    }  
    
    /**
     * Reset counter to 0.
     */
    public void reset() {
        count = 0;
    }
    
    /**
     * Increments the counter by 1.
     */
    public void increment() {
        count++;
    } 
    
    /**
     * Increments the counter by cnt.
     */
    public void increment(int cnt) {
        count += cnt;
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
     * Returns the current value of this counter and then increase the count.
     *
     * @return the current value of this counter
     */
    public int getCountThenIncrement() {
        return count++;
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