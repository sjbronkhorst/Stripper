/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm.series;

import fsm.Model;
import fsm.Strip;
import fsm.Strip_General;

/**
 *This Series function satisfies the C-C boundary conditions for a Strip a priori.
 * Because it only satisfies the boundary conditions a priori, it can be not be used for both static analysis.
 * However, this Series function should be faster than the normal Series_CC because the integrals are calculated analytically.
 * @author SJ
 */
public class BucklingSeries_CC extends BucklingSeries {

    public BucklingSeries_CC(double a) {
        super(a);
        isSimplySupported = false;
    }

    @Override
    public void computeAllIntegrals(int nTerms) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getFunctionValue(double y, int m) {

        return Math.sin(m * Math.PI * y / a) * Math.sin(Math.PI * y / a);

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

    @Override
    public double getMu_m(int m) {
        return m * Math.PI;

    }

    @Override
    public String toString() {
        return "Buckling C-C";
    }

    public double getI1(int m, int n) {
        if ((m == 1) && (n == 1)) {
            return 3.0 * a / 8.0;
        } else if (m == n) {
            return a / 4.0;
        } else if (Math.abs(m - n) == 2) {
            return -a / 8.0;
        } else {
            return 0.0;
        }

    }

    public double getI2(int m, int n) {
        if (m == n) {
            return -Math.PI * Math.PI * (m * m + 1) / 4.0 / a;
        } else if (m - n == 2) {
            return Math.PI * Math.PI * (m * m + 1) / 8.0 / a - Math.PI * Math.PI * m / 4.0 / a;
        } else if (m - n == -2) {
            return Math.PI * Math.PI * (m * m + 1) / 8.0 / a + Math.PI * Math.PI * m / 4.0 / a;
        } else {
            return 0.0;
        }

    }

    public double getI3(int m, int n) {
        if (m == n) {
            return -Math.PI * Math.PI * (n * n + 1) / 4.0 / a;
        } else if (m - n == 2) {
            return Math.PI * Math.PI * (n * n + 1) / 8.0 / a + Math.PI * Math.PI * n / 4.0 / a;
        } else if (m - n == -2) {
            return Math.PI * Math.PI * (n * n + 1) / 8.0 / a - Math.PI * Math.PI * n / 4.0 / a;
        } else {
            return 0.0;
        }
    }

    public double getI4(int m, int n) {
        if (m == n) {
            return Math.PI * Math.PI * Math.PI * Math.PI * ((m * m + 1) * (m * m + 1) + 4 * m * m) / 4.0 / (a * a * a);
        } else if (m - n == 2) {
            return -Math.PI * Math.PI * Math.PI * Math.PI * (m - 1) * (m - 1) * (n + 1) * (n + 1) / 8.0 / (a * a * a);
        } else if (m - n == -2) {
            return -Math.PI * Math.PI * Math.PI * Math.PI * (n - 1) * (n - 1) * (m + 1) * (m + 1) / 8.0 / (a * a * a);
        } else {
            return 0.0;
        }
    }

    public double getI5(int m, int n) {
        if (m == n) {
            return Math.PI * Math.PI * (1 + m * m) / 4.0 / a;
        } else if (Math.abs(m - n) == 2) {
            return -Math.PI * Math.PI * (m * n + 1) / 8.0 / a;
        } else {
            return 0.0;
        }
    }

    @Override
    public Strip getStrip(Model m) {
        return new Strip_General(m);
    }

    
}
