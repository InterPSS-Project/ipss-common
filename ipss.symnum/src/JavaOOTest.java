import org.apache.commons.math3.complex.Complex;

import java.math.*;
import java.util.*;
import org.apache.commons.math3.complex.*;

public class JavaOOTest {
    public static void main(String[] args) {
        BigInteger a = BigInteger.valueOf(1), // without OO
                b = 2, // with OO

                c1 = a.negate().add(b.multiply(b)).add(b.divide(a)), // without OO
                c2 = -a + b*b + b/a; // with OO

        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("expression: -a + b*b + b/a = " + c2);

        if (c1.compareTo(c2)<0 || c1.compareTo(c2)>0) // without OO
            System.out.println("impossible");
        if (c1<c2 || c1>c2) // with OO
            System.out.println("impossible");

        HashMap<String, String> map = new HashMap<>();
        if (!map.containsKey("qwe"))
            map.put("qwe", map.get("asd")); // without OO
        if (map["qwe"]==null)
            map["qwe"] = map["asd"]; // with OO

        Complex x = new Complex(1.0, 1.0);
        Complex y = new Complex(2.0, 2.0);

        Complex z = x + y;
        System.out.println("x+y: " + z.getReal() + " + j" + z.getImaginary());

        z = x - y;
        System.out.println("x-y: " + z.getReal() + " + j" + z.getImaginary());

        z = x * y;
        System.out.println("x*y: " + z.getReal() + " + j" + z.getImaginary());

        z = x / y;
        System.out.println("x/y: " + z.getReal() + " + j" + z.getImaginary());

    }
}
