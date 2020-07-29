import java.awt.geom.Arc2D;

enum KeySet1 {KS1_1, KS1_2}

enum KeySet2 {KS2_1, KS2_2}

public class CustomClass {
   static class CusClass {
        private double x;

        public CusClass(double x) {
            this.x = x;
        }

        public double getX() {
            return this.x;
        }

        public CusClass add(CusClass y) {
           double z = this.x + y.getX();
           return new CusClass(z);
        }
        public CusClass add(double y) {
            double z = this.x + y;
           return new CusClass(z);
         }

         public static CusClass valueOf(double x) {
            return new CusClass(x);
         }

       public CusClass get(KeySet1 key) {
            return this;
       }

       public void set(KeySet2 key, double x) {
           this.x = x;
       }
       public String toString() {
            return new Double(this.x).toString();
        }
    }

    public static void main(String[] args) {
        CusClass a = new CusClass(1.0);
        CusClass c = a + 2.0;
        System.out.println("a + b = " + c);

        CusClass b = new CusClass(2.0);
        CusClass d = a + b;
        System.out.println("a + b = " + d);

        CusClass e = 3.0;
        System.out.println("e = " + e);

        CusClass f = e[KeySet1.KS1_1];
        System.out.println("f = " + f);

        f[KeySet2.KS2_1] = 6.0;
        System.out.println("f = " + f);
    }
}
