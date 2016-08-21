/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm.series;

import fsm.Model;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import linalg.Matrix;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import fsm.Strip;
import fsm.Strip_General;

/**
 *This Series function satisfies the C-C boundary conditions for a Strip.
 * Because it satisfies ALL the boundary conditions, it can be used for both static and buckling analysis.
 * However, it may be slow because only numerical integration is possible and also because BigDecimal is used to ensure accuracy.
 * 
 * 
 * @author SJ
 */
public class Series_CC extends Series {

    public Matrix I1Mat;
    public Matrix I2Mat;
    public Matrix I3Mat;
    public Matrix I4Mat;
    public Matrix I5Mat;
    boolean integralsCalculated = false;
    public int gausspoints = 10;

    @Override
    public void computeAllIntegrals(int nTerms) {
        I1Mat = Matrix.getMatrix(nTerms, nTerms);
        I2Mat = Matrix.getMatrix(nTerms, nTerms);
        I3Mat = Matrix.getMatrix(nTerms, nTerms);
        I4Mat = Matrix.getMatrix(nTerms, nTerms);
        I5Mat = Matrix.getMatrix(nTerms, nTerms);

        double[] I = new double[5];

        for (int i = 1; i < nTerms + 1; i++) {
            for (int j = i; j < nTerms + 1; j++) {

                I = getIntegralValues(i, j);

                I1Mat.set(I[0], i - 1, j - 1);

                I2Mat.set(I[1], i - 1, j - 1);

                I3Mat.set(I[2], i - 1, j - 1);

                I4Mat.set(I[3], i - 1, j - 1);
                I5Mat.set(I[4], i - 1, j - 1);
            }

        }

        integralsCalculated = true;
    }

    public Series_CC(double a) {
        super(a);
        isSimplySupported = false;
    }

    @Override
    public strictfp double getFunctionValue(double y, int m) {

        double um = getMu_m(m);

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

        double um = getMu_m(m);
        double un = getMu_m(n);


        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));

        double km = um / a;
        double kn = un / a;

        BigDecimal Ym;
        BigDecimal Yn;

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

        double Pi = Math.PI;

        double um = getMu_m(m);
        double un = getMu_m(n);

        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));

        double km = um / a;
        double kn = un / a;

        BigDecimal Ymd2;
        BigDecimal Yn;

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
   
        double um = getMu_m(m);
        double un = getMu_m(n);


        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));

        double km = um / a;
        double kn = un / a;

        BigDecimal Ymd2;
        BigDecimal Ynd2;

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
  

        double um = getMu_m(m);
        double un = getMu_m(n);

        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double alphaN = (sin(un) - sinh(un)) / (cos(un) - cosh(un));

        double km = um / a;
        double kn = un / a;

        BigDecimal Ymd1;
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
    public double getYmIntegral(int m) {

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(gausspoints, 0.98, 5);
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
//        dY = alphaM*(km*sin(km*y) + km*sinh(km*y)) + km*cos(km*y) - km*cosh(km*y)

        double um = getMu_m(m);

        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double km = um / a;

        BigDecimal dYm;

        BigDecimal cosh = new BigDecimal(-km * cosh(km * y));
        BigDecimal sinh = new BigDecimal(alphaM * km * sinh(km * y));
        BigDecimal sin = new BigDecimal(alphaM * km * sin(km * y));
        BigDecimal cos = new BigDecimal(km * cos(km * y));

        dYm = (cos.add(sin).add(sinh).add(cosh));

        return dYm.doubleValue();

    }

    @Override
    public double getSecondDerivativeValue(double y, int m) {

        // d2Y =alphaM*(km^2*cos(km*y) + km^2*cosh(km*y)) - km^2*sin(km*y) - km^2*sinh(km*y)
        double um = getMu_m(m);

        double alphaM = (sin(um) - sinh(um)) / (cos(um) - cosh(um));

        double km = um / a;

        BigDecimal d2Ym;

        BigDecimal cosh = new BigDecimal(km*km*alphaM * cosh(km * y));
        BigDecimal sinh = new BigDecimal(-km*km*sinh(km * y));
        BigDecimal sin = new BigDecimal(-km*km*sin(km * y));
        BigDecimal cos = new BigDecimal(km*km*alphaM * cos(km * y));

        d2Ym = (cos.add(sin).add(sinh).add(cosh));

        return d2Ym.doubleValue();

    }

    @Override
    public double getMu_m(int m) {
        
        // YK Cheung - Finite Strip Method in Structural Analysis pg 9 
        
        double Pi = Math.PI;
        if (m == 1) {
            return 4.730;
        }
        if (m == 2) {
            return 7.8532;
        }
        if (m == 3) {
            return 10.9960;
        }

        return Pi * (2 * m + 1) / 2.0;
    }

    public double getI1(int m, int n) {

        if (m != n) {
            return 0.0;
        }

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(gausspoints, 0.98, 5);
        return ilg.integrate(2000, this.getF1(m, n), 0, a);

    }

    public double getI2(int m, int n) {

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(gausspoints, 0.98, 5);
        return ilg.integrate(2000, this.getF2(m, n), 0, a);
    }

    public double getI3(int m, int n) {

        return getI2(n, m);
    }

    public double getI4(int m, int n) {

        if (m != n) {
            return 0.0;
        }

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(gausspoints, 0.98, 5);
        return ilg.integrate(2000, this.getF4(n, m), 0, a);
    }

    public double getI5(int m, int n) {

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(gausspoints, 0.98, 5);
        return ilg.integrate(2000, this.getF5(n, m), 0, a);

    }

    /**
     * Computes the 5 integrals simultaneously for increased performance.
     *
     * @param m Fourier term row
     * @param n Fourier term column
     * @return double array of size 5 with indexes corresponding to integral
     * number (1-5)
     */
    @Override
    public double[] getIntegralValues(int m, int n) {

        double[] I = new double[5];

        //if (!integralsCalculated) {

            Callable<Double> tsk1 = () -> getI1(m, n);

            Callable<Double> tsk2 = () -> getI2(m, n);

            Callable<Double> tsk3 = () -> getI3(m, n);

            Callable<Double> tsk4 = () -> getI4(m, n);

            Callable<Double> tsk5 = () -> getI5(m, n);

            ExecutorService service;
            final Future<Double> thread1, thread2, thread3, thread4, thread5;

            service = Executors.newFixedThreadPool(5);
            thread1 = service.submit(tsk1);
            thread2 = service.submit(tsk2);
            thread3 = service.submit(tsk3);
            thread4 = service.submit(tsk4);
            thread5 = service.submit(tsk5);

            try {
                I[0] = thread1.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Series_CC.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                I[1] = thread2.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Series_CC.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                I[2] = thread3.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Series_CC.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                I[3] = thread4.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Series_CC.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                I[4] = thread5.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Series_CC.class.getName()).log(Level.SEVERE, null, ex);
            }

            service.shutdownNow();
//        } else {
//            if (n >= m) {
//                I[0] = I1Mat.get(m - 1, n - 1);
//                I[1] = I2Mat.get(m - 1, n - 1);
//                I[2] = I3Mat.get(m - 1, n - 1);
//                I[3] = I4Mat.get(m - 1, n - 1);
//                I[4] = I5Mat.get(m - 1, n - 1);
//            } else {
//                I[0] = I1Mat.get(n - 1, m - 1);
//                I[1] = I3Mat.get(n - 1, m - 1);
//                I[2] = I2Mat.get(n - 1, m - 1);
//                I[3] = I4Mat.get(n - 1, m - 1);
//                I[4] = I5Mat.get(n - 1, m - 1);
//            }
//        }

        return I;
    }

    @Override
    public double getFirstDerivativeIntegral(int m) {
        return 0.0;
    }

    @Override
    public String toString() {
        return "C-C";
    }

    public static void main(String[] args) {
//        int n = 240;
//        double a = 2000;
//        double y = 1000;
//        Series_CC s= new Series_CC(a);
//        double un = s.getMu_m(n);
//       // double alphaN = (s.sin(un) - s.sinh(un)) / (s.cos(un) - s.cosh(un));
//        
//        double kn = un / a;
//        
//        
//        System.out.println("un = " + un);
//        
//        System.out.println("kn = " + kn);
////        System.out.println("bigSIN = " + s.bigSin(un));
////        System.out.println("bigSINH = " + s.bigSinh(un));
////        System.out.println("bigcos = " + s.bigCos(un));
////        System.out.println("bigcosh = " + s.bigCosh(un));
//        
//        System.out.println("SIN = " + s.sin(un));
//        System.out.println("SINH = " + s.sinh(un));
//        System.out.println("cos = " + s.cos(un));
//        System.out.println("cosh = " + s.cosh(un));
//        
//        
////        BigDecimal alphaN = (s.bigSin(un).subtract(s.bigSinh(un))).divide((s.bigCos(un).subtract(s.bigCosh(un))),1000,RoundingMode.HALF_UP);
////        System.out.println("alphaN = " + alphaN);
//        
////        BigDecimal coshN = new BigDecimal("-1").multiply(alphaN).multiply(new BigDecimal("-1").multiply(s.bigCosh(kn * y)));

    }

    @Override
    public double getVScalingValue(double y, int m) {
        return Math.sin((m+1)*Math.PI/a);
    }
    
    @Override
    public boolean onlySupportsBuckling()
    {
        return false;
    }

    
     @Override
    public Strip getStrip(Model m) {
        return new Strip_General(m);
    }

}
