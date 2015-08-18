/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

import java.math.BigDecimal;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;

/**
 *
 * @author SJ
 */
public class Series_CC extends Series {

    public Series_CC(double a) {
        super(a);
    }

    @Override
    public strictfp double getFunctionValue(double y, int m) {
        double Pi = Math.PI;

        double um = Pi * (2 * m + 1) / 2.0;

        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));
        
        

        double km = um / a;

        BigDecimal Ym;

        BigDecimal cosh = new BigDecimal(-alphaM * -cosh(km * y));
        BigDecimal sinh = new BigDecimal(-sinh(km * y));
        BigDecimal sin = new BigDecimal(sin(km * y));
        BigDecimal cos = new BigDecimal(-alphaM * cos(km * y));

        Ym = (cos.add(sin).add(sinh).add(cosh));

        return Ym.doubleValue();
    }

    public strictfp double getF1Value(double y, int m, int n) {
        double Pi = Math.PI;

        double um = Pi * (2 * m + 1) / 2.0;
        double un = Pi * (2 * n + 1) / 2.0;
        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));
        double km = um / a;
        double kn = un / a;

        BigDecimal Ym ;
        BigDecimal Yn ;

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

    }

    public strictfp double getF2Value(double y, int m, int n) {
        // Yn = sin(kn*y) - sinh(kn*y)-alphaN*(cos(kn*y) - cosh(kn*y))

        //Ymd2 = alphaM*(km^2*cos(km*y) + km^2*cosh(km*y)) - km^2*sin(km*y) - km^2*sinh(km*y)
        double Pi = Math.PI;

        double um = Pi * (2 * m + 1) / 2.0;
        double un = Pi * (2 * n + 1) / 2.0;
        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));
        double km = um / a;
        double kn = un / a;

        BigDecimal Ymd2 ;
        BigDecimal Yn ;

        BigDecimal cosh = new BigDecimal(alphaM * km * km * cosh(km * y));

        BigDecimal sinh = new BigDecimal(-km * km * sinh(km * y));
        BigDecimal sin = new BigDecimal(-km * km * sin(km * y));
        BigDecimal cos = new BigDecimal(alphaM * km * km * cos(km * y));

        Ymd2 = (cos.add(sin).add(sinh).add(cosh));

        BigDecimal coshN = new BigDecimal(-alphaN * -cosh(kn * y));
        BigDecimal sinhN = new BigDecimal(-sinh(kn * y));
        BigDecimal sinN = new BigDecimal(sin(kn * y));
        BigDecimal cosN = new BigDecimal(-alphaN * cos(kn * y));

        Yn = cosN.add(sinN).add(sinhN).add(coshN);

        BigDecimal ans = Ymd2.multiply(Yn);

        return ans.doubleValue();
    }

    public strictfp double getF4Value(double y, int m, int n) {
        // Yn = sin(kn*y) - sinh(kn*y)-alphaN*(cos(kn*y) - cosh(kn*y))

        //Ymd2 = alphaM*(km^2*cos(km*y) + km^2*cosh(km*y)) - km^2*sin(km*y) - km^2*sinh(km*y)
        double Pi = Math.PI;

        double um = Pi * (2 * m + 1) / 2.0;
        double un = Pi * (2 * n + 1) / 2.0;
        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));
        double km = um / a;
        double kn = un / a;

        BigDecimal Ymd2 ;
        BigDecimal Ynd2 ;

        BigDecimal cosh = new BigDecimal(alphaM * km * km * cosh(km * y));

        BigDecimal sinh = new BigDecimal(-km * km * sinh(km * y));
        BigDecimal sin = new BigDecimal(-km * km * sin(km * y));
        BigDecimal cos = new BigDecimal(alphaM * km * km * cos(km * y));

        Ymd2 = (cos.add(sin).add(sinh).add(cosh));

        BigDecimal coshN = new BigDecimal(alphaN * kn * kn * cosh(kn * y));

        BigDecimal sinhN = new BigDecimal(-kn * kn * sinh(kn * y));
        BigDecimal sinN = new BigDecimal(-kn * kn * sin(kn * y));
        BigDecimal cosN = new BigDecimal(alphaN * kn * kn * cos(kn * y));

        Ynd2 = cosN.add(sinN).add(sinhN).add(coshN);

        BigDecimal ans = Ymd2.multiply(Ynd2);

        return ans.doubleValue();
    }

    public strictfp double getF5Value(double y, int m, int n) {
        //Ymd1 = alphaM*(km*sin(km*y) + km*sinh(km*y)) + km*cos(km*y) - km*cosh(km*y)
        //Ynd1 = alphaN*(kn*sin(kn*y) + kn*sinh(kn*y)) + kn*cos(kn*y) - kn*cosh(kn*y)

        double Pi = Math.PI;

        double um = Pi * (2 * m + 1) / 2.0;
        double un = Pi * (2 * n + 1) / 2.0;
        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));
        double km = um / a;
        double kn = un / a;

        BigDecimal Ymd1 ;
        BigDecimal Ynd1;

        BigDecimal cosh = new BigDecimal(-km * cosh(km * y));

        BigDecimal sinh = new BigDecimal(alphaM * km * sinh(km * y));
        BigDecimal sin = new BigDecimal(alphaM * km * sin(km * y));
        BigDecimal cos = new BigDecimal(km * cos(km * y));

        Ymd1 = (cos.add(sin).add(sinh).add(cosh));

        BigDecimal coshN = new BigDecimal(-kn * cosh(kn * y));

        BigDecimal sinhN = new BigDecimal(alphaN * kn * sinh(kn * y));
        BigDecimal sinN = new BigDecimal(alphaN * kn * sin(kn * y));
        BigDecimal cosN = new BigDecimal(kn * cos(kn * y));

        Ynd1 = cosN.add(sinN).add(sinhN).add(coshN);

        BigDecimal ans = Ymd1.multiply(Ynd1);

        return ans.doubleValue();
    }

    @Override
    public double getYmIntegral(int m, double a) {
       

       IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, this.getFunction(m), 0, a);
    }

    public UnivariateFunction getFunction(int m) {

        UnivariateFunction f = (double x) -> getFunctionValue(x, m);

        return f;
    }
    public UnivariateFunction getF1(int m, int n) {

        UnivariateFunction f = (double x) -> getF1Value(x, m, n);

        return f;
    }

    public UnivariateFunction getF2(int m, int n) {

        UnivariateFunction f = (double x) -> getF2Value(x, m, n);

        return f;
    }

    public UnivariateFunction getF4(int m, int n) {

        UnivariateFunction f = (double x) -> getF4Value(x, m, n);

        return f;
    }

    public UnivariateFunction getF5(int m, int n) {

        UnivariateFunction f = (double x) -> getF5Value(x, m, n);

        return f;
    }


    @Override
    public double getFirstDerivativeValue(double y, int m) {
        return 0.0;
    }


    @Override
    public double getMu_m(int m) {
        double Pi = Math.PI;
        
        return Pi * (2 * m + 1) / 2.0;
    }

    public double getI1(int m, int n) {
        
        if(m != n)
        {
            return 0.0;
        }

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, this.getF1(m, n), 0, a);

    }

    public double getI2(int m, int n) {
       
        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, this.getF2(m, n), 0, a);
    }

    public double getI3(int m, int n) {

        return getI2(n, m);
    }

    public double getI4(int m, int n) {
        
        if(m != n)
        {
            return 0.0;
        }

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, this.getF4(n, m), 0, a);
    }

    public double getI5(int m, int n) {
        

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, this.getF5(n, m), 0, a);

    }

    @Override
    public double[] getIntegralValues(int m, int n) {

        double[] I = new double[5];

        I[0] = getI1(m, n);
        I[1] = getI2(m, n);
        I[2] = getI3(m, n);
        I[3] = getI4(m, n);
        I[4] = getI5(m, n);

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
