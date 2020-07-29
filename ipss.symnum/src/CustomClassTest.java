
import static test.KeySet1.*;
import static test.KeySet2.*;
import test.CustomClass;

public class CustomClassTest {
    public static void main(String[] args) {
        CustomClass a = new CustomClass(1.0);
        CustomClass c = a + 2.0;
        System.out.println("a + b = " + c);

        CustomClass b = new CustomClass(2.0);
        CustomClass d = a + b;
        System.out.println("a + b = " + d);

        CustomClass e = 3.0;
        System.out.println("e = " + e);

        CustomClass f = e[KS1_1];
        System.out.println("f = " + f);

        f[KS2_1] = 6.0;
        System.out.println("f = " + f);
    }
}
