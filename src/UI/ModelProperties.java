/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.series.Series;
import stripper.series.Series_CC;
import stripper.series.Series_CF;

/**
 *
 * @author SJ
 */
public class ModelProperties 
{
    
  private static Material modelMaterial = new Material_Steel();
  private static double modelLength = 2000;
  private static int fourierTerms = 10;
  private static Series fourierSeries = new Series_CF(modelLength);

    public static Material getModelMaterial() {
        return modelMaterial;
    }

    public static void setModelMaterial(Material modelMaterial) {
        ModelProperties.modelMaterial = modelMaterial;
    }

    public static double getModelLength() {
        return modelLength;
    }

    public static void setModelLength(double modelLength) {
        ModelProperties.modelLength = modelLength;
    }

    public static int getFourierTerms() {
        
        return fourierTerms;
    }

    public static void setFourierTerms(int fourierTerms) {
        ModelProperties.fourierTerms = fourierTerms;
    }

    public static Series getFourierSeries() {
        return fourierSeries;
    }

    public static void setFourierSeries(Series fourierSeries) {
        ModelProperties.fourierSeries = fourierSeries;
    }

   
    
  
  
  
  
  
    
}
