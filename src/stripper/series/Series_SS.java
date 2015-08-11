/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

import org.apache.commons.math3.analysis.UnivariateFunction;


/**
 *
 * @author SJ
 */
public class Series_SS extends Series {

    public Series_SS(double a) {
        super(a);
    }

    public double getFunctionValue(double y, int m) {
        return Math.sin(m * Math.PI * y / a);
    }
    
    public double getYmIntegral(double m, double a)
    {
        
        return (a - a*Math.cos(Math.PI*m))/(Math.PI*m);
    }
    
    public UnivariateFunction getFunction(int m) 
    {
        UnivariateFunction f = new UnivariateFunction() {

            @Override
            public double value(double x) {
                return getFunctionValue(x, m);
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
        return Math.cos(m * Math.PI * y / a) * m * Math.PI / a;
    }
    
//
//    @Override
//    public double getSecondDerivativeValue(double y, int m) {
//        return -Math.sin(m * Math.PI * y / a) * (m * Math.PI / a) * (m * Math.PI/ a);
//    }

    @Override
    public double getMu_m(int m)
    {
        return m*Math.PI;
    }
    
    public double [] getIntegralValues(int m, int n)
    {
        
        
        double [] I = new double [5];
        double pi = Math.PI;
        double pi2 = pi*pi;
        double pi4 = pi*pi*pi*pi;
        
        double m2 = m*m;
        double m4 = m*m*m*m;
        
        double n2 = n*n;
        double n4 = n*n*n*n;
        
        if(m==n)
        {
        I[0]=a/2;
        I[1]=-m2*pi2/a/2;
        I[2]=-n2*pi2/a/2;
        I[3]=pi4*m4/2/(a*a*a);
        I[4]=pi2*m2/2/a;
        }
    else
        {
        I[0]=0;
        I[1]=0;
        I[2]=0;
        I[3]=0;
        I[4]=0;
        
        }
        
        
        return I;
    }
    
    
    public static void main (String []args)
    {
       
       
    }

    @Override
    public double getFirstDerivativeIntegral(int m) {
        return Math.sin(Math.PI*m);
    }
    
    

}
