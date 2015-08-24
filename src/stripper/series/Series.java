/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

/**
 *
 * @author SJ
 */
public abstract class Series {

    protected double a; // Series domain length i.e. length of strip
    protected boolean isSimplySupported;
    

    public Series(double a) {

        this.a = a;
    }
    
    public boolean isSimplySupported()
    {
        return isSimplySupported;
    }
    
    public abstract double getFunctionValue(double y,int m);
    
    
    
    public abstract double getYmIntegral(int m, double a);

   

    public abstract double getFirstDerivativeValue(double y, int m);

    //public abstract double getSecondDerivativeValue(double y, int m);
    
    public abstract double getFirstDerivativeIntegral(int m);
    
    public abstract double [] getIntegralValues(int m, int n);
    
    public abstract double getMu_m(int m);
    
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
    

//    public UnivariateFunction getFunction(int m) {
//
//        return new UnivariateFunction() {
//
//            @Override
//            public double value(double y) {
//                return getFunctionValue(y, m);
//            }
//        };
//
//    }
    
    

//    public UnivariateFunction getFirstDerivative(int m) {
//
//        return new UnivariateFunction() {
//
//            @Override
//            public double value(double y) {
//                return getFirstDerivativeValue(y, m);
//            }
//        };
//
//    }

//    public UnivariateFunction getSecondDerivative(int m) {
//
//        return new UnivariateFunction() {
//
//            @Override
//            public double value(double y) {
//                
//                return getSecondDerivativeValue(y, m);
//            }
//        };
//
//    }

//    public UnivariateFunction[] getIntegralFunctions(int m, int n) {
//
//        UnivariateFunction[] f = new UnivariateFunction[5];
//
//        f[0] = new UnivariateFunction() {
//
//            @Override
//            public double value(double x) {
//                
//                return getFunctionValue(x, m) * getFunctionValue(x, n);
//            }
//
//        };
//        
//        f[1] = new UnivariateFunction() {
//
//            @Override
//            public double value(double x) {
//                
//                return getSecondDerivativeValue(x, m) * getFunctionValue(x, n);
//            }
//
//        };
//        
//        f[2] = new UnivariateFunction() {
//
//            @Override
//            public double value(double x) 
//            {
//               
//                    
//                return getFunctionValue(x, m) * getSecondDerivativeValue(x, n);
//            }
//
//        };
//        
//        f[3] = new UnivariateFunction() {
//
//            @Override
//            public double value(double x) {
//               
//                return getSecondDerivativeValue(x, m) * getSecondDerivativeValue(x, n);
//            }
//
//        };
//        
//        f[4] = new UnivariateFunction() {
//
//            @Override
//            public double value(double x) {
//                
//                return getFirstDerivativeValue(x, m) * getFirstDerivativeValue(x, n);
//            }
//
//        };
//        
//        
//        
//        return f;
//    }
    
    

}
