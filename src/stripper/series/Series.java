/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author SJ
 */
public abstract class Series {

    protected double a; // Series domain length i.e. length of strip
    protected boolean isSimplySupported;
    private static ObservableList<Series> seriesList = FXCollections.<Series>observableArrayList();
    private static boolean listIsSet = false;
    
    
    public static ObservableList<Series> getSerieslList()
    {
        if(!listIsSet)
        {
        seriesList.clear();
        seriesList.add(new Series_SS(0));
        seriesList.add(new Series_CC(0));
        seriesList.add(new Series_CF(0));
        
        
        listIsSet = true;
        }
        
        return seriesList;
    }
    

    public Series(double a) {

        this.a = a;
    }
    public void setLength(double length)
    {
        this.a = length;
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
    
    @Override
    public abstract String toString();
   

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
