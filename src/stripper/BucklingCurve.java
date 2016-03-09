/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Model;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;


/**
 *
 * @author SJ
 */
public class BucklingCurve {

    private List<Model> modelList = new ArrayList<>();
    private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(this, "");
    private static int bcNum = 0;
     public File crossSectionImage , localImage , distortionalImage , globalImage;


    double localFactor = 0;
    double globalFactor = 0;
    double distortionalFactor = 0;

    public double getLocalFactor() {
        return localFactor;
    }

    public void setLocalFactor(double localFactor) {
        this.localFactor = localFactor;
    }

    public double getGlobalFactor() {
        return globalFactor;
    }

    public void setGlobalFactor(double globalFactor) {
        this.globalFactor = globalFactor;
    }

    public double getDistortionalFactor() {
        return distortionalFactor;
    }

    public void setDistortionalFactor(double distortionalFactor) {
        this.distortionalFactor = distortionalFactor;
    }

    public BucklingCurve(String name) {
        this.name.set(name);
        bcNum++;
    }

    public void addModel(Model model) {
        modelList.add(model);
    }

    public double[] getLoadFactors() {
        double[] data = new double[modelList.size()];

        int i = 0;
        for (Model m : modelList) {
            data[i] = m.getBucklePoint().getMinLoadFactor();
            i++;
        }

        return data;
    }

    public double[] getPhysicalLengths() {
        double[] data = new double[modelList.size()];

        int i = 0;
        for (Model m : modelList) {
            data[i] = m.getBucklePoint().getPhysicalLength();
            i++;
        }

        return data;
    }
    
    public BucklingDataPoint[] getBucklingDataPoints() {
        BucklingDataPoint [] data = new BucklingDataPoint[modelList.size()];

        int i = 0;
        for (Model m : modelList) {
            data[i] = m.getBucklePoint();
            i++;
        }

        return data;
    }

    public BucklingDataPoint getPoint(double physicalLength) {
        for (Model m : modelList) {
            if (m.getBucklePoint().getPhysicalLength() == physicalLength) {
                return m.getBucklePoint();
            }
        }

        System.out.println("No such point");
        return null;
    }

    public Model getModel(double physicalLength) {
        for (Model m : modelList) {
            if (m.getBucklePoint().getPhysicalLength() == physicalLength) {
                return m;
            }
        }

        System.out.println("No such Model");
        return null;
    }
    
    public List<Model> getModels()
    {
        return modelList;
    }

    public String getName() {
        return name.get();
    }
    
    public StringProperty nameProperty()
    {
        return name;
    }
    
    public String toString()
    {
        return name.get();
    }

}
