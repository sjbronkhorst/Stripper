/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;

/**
 *
 * @author SJ
 */
public class Series_CF extends Series{

    public Series_CF(double a) {
        super(a);
    }

    @Override
    public double getFunctionValue(double y, int m) {
        

        double um = getMu_m(m);

        double alphaM = (sin(um) + sinh(um)) / (cos(um) + cosh(um));

        double km = um / a;

        BigDecimal Ym;

        BigDecimal cosh = new BigDecimal(-alphaM * -cosh(km * y));
        BigDecimal sinh = new BigDecimal(-sinh(km * y));
        BigDecimal sin = new BigDecimal(sin(km * y));
        BigDecimal cos = new BigDecimal(-alphaM * cos(km * y));

        Ym = (cos.add(sin).add(sinh).add(cosh));

        return Ym.doubleValue();
    }

    public double getF1Value(double y, int m, int n) {
        
        
        
        double um = getMu_m(m);
        double un = getMu_m(n);
        double alphaM = (sin(um) + sinh(um)) / (cos(um) + cosh(um));

        double alphaN = (sin(un) + sinh(un)) / (cos(un) + cosh(un));
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

    public double getF2Value(double y, int m, int n) {
        double Pi = Math.PI;

        double um = getMu_m(m);
        double un = getMu_m(n);
        double alphaM = (sin(um) + sinh(um)) / (cos(um) + cosh(um));

        double alphaN = (sin(un) + sinh(un)) / (cos(un) + cosh(un));
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

    public double getF4Value(double y, int m, int n) {
        double Pi = Math.PI;

        double um = getMu_m(m);
        double un = getMu_m(n);
        double alphaM = (sin(um) + sinh(um)) / (cos(um) + cosh(um));

        double alphaN = (sin(un) + sinh(un)) / (cos(un) + cosh(un));
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

    public double getF5Value(double y, int m, int n) {
       

        double um = getMu_m(m);
        double un = getMu_m(n);
        double alphaM = (sin(um) + sinh(um)) / (cos(um) + cosh(um));

        double alphaN = (sin(un) + sinh(un)) / (cos(un) + cosh(un));
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
    public double getYmIntegral(int m, double a) {

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, getFunction(m), 0, a);
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
        
        if(m == 1)
        {
            return 1.875;
        }
        if(m == 2)
        {
            return 4.694;
        }

        return Pi * (2 * m - 1) / 2.0;
    }

    public double getI1(int m, int n) {

        if (m != n) {
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

       if (m != n) {
            return 0.0;
        }

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
        return ilg.integrate(2000, this.getF4(n, m), 0, a);
    }

    public double getI5(int m, int n) {

        IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(64, 0.98, 5);
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
            Logger.getLogger(Series_CF.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            I[1] = thread2.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(Series_CF.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            I[2] = thread3.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(Series_CF.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            I[3] = thread4.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(Series_CF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            I[4] = thread5.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(Series_CF.class.getName()).log(Level.SEVERE, null, ex);
        }

        service.shutdownNow();

        return I;
    }

    @Override
    public double getFirstDerivativeIntegral(int m) {
        return 0.0;
    }
    
    public static void main(String[]args)
    {
        
        Series_CF y = new Series_CF(2000);
        
        double I [] = y.getIntegralValues(2, 1);
        
        for (int i = 0; i < 5; i++)
        {
            System.out.println(i+" " + I[i]);
        }
            
            }

    @Override
    public String toString() {
        return "C-F";
    }

    @Override
    public void computeAllIntegrals(int nTerms) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getSecondDerivativeValue(double y, int m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getVScalingValue(double y, int m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean onlySupportsBuckling()
    {
        return false;
    }

}
