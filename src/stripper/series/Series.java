/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.series;

import UI.ModelProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        
        seriesList.add(new Series_SS(ModelProperties.getModelLength()));
        seriesList.add(new Series_CC(ModelProperties.getModelLength()));
        seriesList.add(new Series_CF(ModelProperties.getModelLength()));
        seriesList.add(new BucklingSeries_CC(ModelProperties.getModelLength()));
      
        
        
       
        
        
        listIsSet = true;
        }
        
        return seriesList;
    }
    public abstract void computeAllIntegrals(int nTerms);
            
    

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
        
    public abstract double [] getIntegralValues(int m, int n);
    
    public abstract double getMu_m(int m);
    
   
    
    @Override
    public abstract String toString();
   
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
    
    
     public abstract double getYmIntegral(int m, double a);

    
    public abstract double getFirstDerivativeValue(double y, int m);

   
    public abstract double getSecondDerivativeValue(double y, int m);

    
    public abstract double getFirstDerivativeIntegral(int m);
    
    public abstract double getVScalingValue(double y, int m);
    
    public abstract boolean onlySupportsBuckling();
   
 
}
