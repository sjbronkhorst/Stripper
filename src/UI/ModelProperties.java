/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.ArrayList;
import stripper.Strip;
import stripper.Strip_General;
import stripper.Strip_SS;
import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public class ModelProperties 
{
    
  private static Material modelMaterial = new Material_Steel();
  private static double modelLength = 4000;
  private static int fourierTerms = 10;
  private static Series fourierSeries = Series.getSerieslList().get(0);
  private static ArrayList<Strip> strips = new ArrayList<>();
  

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
        fourierSeries.setLength(modelLength);
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
        ModelProperties.fourierSeries.setLength(modelLength);
        ModelProperties.getFourierSeries().computeAllIntegrals(ModelProperties.getFourierTerms());
    }

    
    public static ArrayList<Strip> getStripList()
    {
        return strips;
    }
    
    /**
     * Strips should only be added to the model once all data is known e.g. boundary conditions
     * @param uistrip is a dummy strip that can be displayed and has no mathematical significance. It helps with the separation of model and viewer.
     */
    public static void addStrip(UIStrip uistrip)
    {
        if(ModelProperties.fourierSeries.isSimplySupported())
        {
            strips.add(new Strip_SS(uistrip));
        }
        else
        {
            strips.add(new Strip_General(uistrip));
        }
    }
   
    
  
  
  
  
  
    
}
