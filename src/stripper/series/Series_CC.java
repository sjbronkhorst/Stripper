/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

import java.math.BigDecimal;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author SJ
 */
public class Series_CC extends Series {

    public Series_CC(double a) {
        super(a);
    }

    public strictfp double getFunctionValue(double y, int m, int n) {

        double Pi = Math.PI;

        double um = Pi * (2 * m + 1) / 2.0;
        double un = Pi * (2 * n + 1) / 2.0;
        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));
        double km = um / a;
        double kn = un / a;

        BigDecimal Ym = new BigDecimal("0.0");
        BigDecimal Yn = new BigDecimal("0.0");

       // System.out.println("stupid som ="+ (-alphaM * -cosh(km * y) -sinh(km * y)));
//        if ((-alphaM * -cosh(km * y)  -sinh(km * y)) == 0.0) {
//             Ym = sin(km * y) - alphaM * (cos(km * y));
//             Yn = sin(kn * y) - alphaN * (cos(kn * y));
//        } else {
//             Ym = sin(km * y) - sinh(km * y) - alphaM * (cos(km * y) - cosh(km * y));
//             Yn = sin(kn * y) - sinh(kn * y) - alphaN * (cos(kn * y) - cosh(kn * y));
//        }
//        System.out.println(Pi);
//        System.out.println(um);
//        System.out.println(un);
//        System.out.println(alphaM);
//        System.out.println(alphaN);
//        System.out.println(km);
//        System.out.println(kn);
//        System.out.println(Ym);
//        System.out.println(Yn);
//        System.out.println("sin " + sin(km * y));
//        System.out.println("sinh " + -sinh(km * y));
//
//        // System.out.println("km*y = " + km*y);
//        System.out.println("cos " + -alphaM * cos(km * y));
//        System.out.println("cosh " + -alphaM * -cosh(km * y));
       
        BigDecimal cosh = new BigDecimal(-alphaM * -cosh(km * y));
        BigDecimal sinh = new BigDecimal(-sinh(km * y));
        BigDecimal sin = new BigDecimal(sin(km * y));
        BigDecimal cos = new BigDecimal(-alphaM * cos(km * y));

        Ym = (cos.add(sin).add(sinh).add(cosh));

        BigDecimal coshN = new BigDecimal(-alphaN * -cosh(kn * y));
        BigDecimal sinhN = new BigDecimal(-sinh(kn * y));
        BigDecimal sinN = new BigDecimal(sin(kn * y));
        BigDecimal cosN = new BigDecimal(-alphaN * cos(kn * y));

        Yn = cosN.add(sinN).add(sinhN).add(coshN);
        
        BigDecimal ans = Ym.multiply(Yn);

        return ans.doubleValue();

        //return Ym * Yn;
    }

    public double getYmIntegral(double m, double a) {

        return (a - a * Math.cos(Math.PI * m)) / (Math.PI * m);
    }

    public UnivariateFunction getFunction(int m, int n) {

        UnivariateFunction f = new UnivariateFunction() {

            @Override
            public double value(double x) {
                return getFunctionValue(x, m, n);
            }

        };

        return f;
    }

//    public UnivariateFunction getFirstDerivativeFunction(int m) 
//    {
//        UnivariateFunction f = new UnivariateFunction() {
//
//            @Override
//            public double value(double x) {
//                return getFirstDerivativeValue(x, m);
//            }
//           
//        };
//        
//         return f;
//    }
    @Override
    public double getFirstDerivativeValue(double y, int m) {
        return 0.0;
    }

//
//    @Override
//    public double getSecondDerivativeValue(double y, int m) {
//        return -Math.sin(m * Math.PI * y / a) * (m * Math.PI / a) * (m * Math.PI/ a);
//    }
    @Override
    public double getMu_m(int m) {
        return m * Math.PI;
    }

    public double getI1(int m, int n) {

        if(m!= n)
        {
            return 0.0;
        }
        
        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(32, 0.98, 5);
        return ilg.integrate(2000, this.getFunction(m, n), 0, a);

    }

    public double getI2(int m, int n) {
        return 0.0;
    }

    public double getI3(int m, int n) {
        return 0.0;
    }

    public double getI4(int m, int n) {
        return 0.0;
    }

    public double getI5(int m, int n) {
        return 0.0;
    }

    @Override
    public double[] getIntegralValues(int m, int n) {
        return null;
    }

    public double[] getIValues(int m, int n) {

        double[] I = new double[5];

        I[0] = getI1(m, n);
        I[1] = 0;
        I[2] = 0;
        I[3] = 0;
        I[4] = 0;

        return I;
    }

    public static void main(String[] args) {

    }

    @Override
    public double getFirstDerivativeIntegral(int m) {
        return Math.sin(Math.PI * m);
    }

    public double sin(double d) {
        return Math.sin(d);
    }

    public double sinh(double d) {
        return Math.sinh(d);
    }

    public double cos(double d) {
        return Math.cos(d);
    }

    public double cosh(double d) {
        return Math.cosh(d);
    }

}
