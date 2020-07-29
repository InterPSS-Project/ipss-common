package test;

import test.KeySet1;
import test.KeySet2;

public class CustomClass {
        private double x;

        public CustomClass(double x) {
            this.x = x;
        }

        public double getX() {
            return this.x;
        }

        public CustomClass add(CustomClass y) {
           double z = this.x + y.getX();
           return new CustomClass(z);
        }
        public CustomClass add(double y) {
            double z = this.x + y;
           return new CustomClass(z);
         }

         public static CustomClass valueOf(double x) {
            return new CustomClass(x);
         }

       public CustomClass get(KeySet1 key) {
            return this;
       }

       public void set(KeySet2 key, double x) {
           this.x = x;
       }
       public String toString() {
            return new Double(this.x).toString();
        }
}
