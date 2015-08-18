package stripper.series;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import UI.LineChartWindow;
import UI.XYChartDataUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import stripper.series.Series_CC;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class Integrator extends Application {

    public double smartIntegrate(UnivariateFunction f, double from, double to , int nrOfPoints) {
        
        double chunkSize = 1.0/nrOfPoints;
        double maxTest = 0;

        double intervalSize = to - from;
        
        double nrOfCorrectingPoints = 5;
        
        double[] pointX = new double[nrOfPoints];
        double[] pointY = new double[nrOfPoints];
        
        double integral2 = 0;
        double x1 = 0;
        double h1 = 0;
        
        
        for (int i = 0; i < nrOfPoints-1; i++) 
        {
            
            
            
             x1 = i * chunkSize * intervalSize + from;
             h1 = f.value(x1);
            
            pointX[i] = x1;
            pointY[i] = h1;
            
            
            double x2 = (i + 1) * chunkSize * intervalSize + from;
            double h2 = f.value(x2);
            
            pointX[i+1] = x2;
            pointY[i+1] = h2;
            
           // System.out.println("Point1 : " +  pointX[i] + "  " + pointY[i]);
           // System.out.println("Point2 : " +  pointX[i+1] + "  " + pointY[i+1]);
            
           
            
            
            for (int j = 0; j < nrOfCorrectingPoints; j++)
            {
            maxTest = f.value(pointX[i] + j/nrOfCorrectingPoints);
               // System.out.println("max inbetween " + maxTest);
            
            if((maxTest > pointY[i+1]) && j!=0)
            {
                //System.out.println("pointX[" + (i+1) + "]  moved from " + pointX[i+1] + " to " + (x1 + j/nrOfCorrectingPoints) );
                
                pointY[i+1] = maxTest;
                pointX[i+1] = pointX[i] + j/nrOfCorrectingPoints;
                
                
                
                
                
                
            }
            }
            
            integral2 += (x2-x1)*h1 + 0.5*(x2-x1)*(h2-h1);
            
            }
        

//        System.out.println("X coords :");
//        for (int i = 0; i < nrOfPoints; i++) 
//        {
//            System.out.println(pointX[i]);
//        }
//        
//        System.out.println("Y coords :");
//        for (int i = 0; i < nrOfPoints; i++) 
//        {
//            System.out.println(pointY[i]);
//        }
        
        System.out.println("X = " + pointX[nrOfPoints-1]);
        System.out.println("Y = " + pointY[nrOfPoints-1]);
       
        
        
        return integral2;

    }
    
    public double bruteForceIntegrate(UnivariateFunction f, double from, double to , int nrOfPoints) {
        
        double chunkSize = 1.0/nrOfPoints;
        
        double intervalSize = to - from;
         
        double integral = 0;
        double x1 = 0;
        double h1 = 0;
        
        
        for (int i = 0; i < nrOfPoints-1; i++) 
        {
           
             x1 = i * chunkSize * intervalSize + from;
             h1 = f.value(x1);
            
            double x2 = (i + 1) * chunkSize * intervalSize + from;
            double h2 = f.value(x2);
            
            
            integral += (x2-x1)*h1 + 0.5*(x2-x1)*(h2-h1);
            
            }
      
        return integral;

    }
    
    public static void main (String[]args)
    {
        
        
       Application.launch(args);
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Series_CC Ym = new Series_CC(2000);
        
        
        Integrator in = new Integrator();
        
        //System.out.println(in.smartIntegrate(Ym.getFunction(40, 40), 0, 2000, 800000));
        
        
        //1992.1404966374372673694378388458
        //1992.1404966
        //1992.140496
        //1992.140
        //1992.1
        //1992.1
        //1999.993995313753952306310691754
        //1999.99399531
        
        
        
       // IterativeLegendreGaussIntegrator ilg = new IterativeLegendreGaussIntegrator(128, 0.98, 5);
      // System.out.println(ilg.integrate(2000, Ym.getF5(40, 4), 0, 2000));
       
       //System.out.println(in.smartIntegrate(Ym.getF2(10, 10), 0, 2000, 200000));
        //System.out.println(in.bruteForceIntegrate(Ym.getF2(10, 10), 0, 2000, 200000));
        
//        double [] I = Ym.getIntegralValues(1, 10);
//        
//        for (int i = 0; i < 5; i++) {
//            System.out.println("I"+ (i+1)+" = " + I[i]);
//            
//        }
        
        System.out.println(Ym.getYmIntegral(1, 2000));
        
        double [] xData = new double[2000];
        double [] yData = new double[2000];
        
        for (int i = 0; i < 2000; i++) 
        {
            yData[i] = Ym.getF2Value(i, 1, 10);
            xData[i] = i;
        }
        
        
        
//        XYChartDataUtil.addSeries(xData, yData, "");
//        LineChartWindow lcw = new LineChartWindow("", "", "", "", 0, 2000, XYChartDataUtil.getDataList());
//        Stage s = new Stage();
//        lcw.start(s);
        
        //-0.1319990064163165605150652791214
        //-0.1319990064251739
        
        //0.00001114192707159213930514932167171
        //0.000011141927070865642
        
        //0.13199900641631661261502386961801
        //0.13199900641679452
        
        
        //1.8107835630540130847836127165559
        //-0.6196960314348914
        
    }
}
