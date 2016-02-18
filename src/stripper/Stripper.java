/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import linalg.Matrix;
import linalg.Vector;
import serialize.SerializeUtils;
import stripper.materials.*;
import stripper.series.*;

/**
 *
 * @author SJ
 */
public class Stripper {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    
    /*
    public static void main(String[] args) throws InterruptedException, Exception {

        double modelLength = 8000;
        //double force = 0.0001;
        
        int b = 100;

        
        for (modelLength = 5; modelLength < 100; modelLength = modelLength+10) {
                    
        Material usrMat = new Material_Steel();

        Node n1 = new Node(0, 0);
        Node n2 = new Node(0, b);


        //Loaded edges
       
        Series_CC Y = new Series_CC(modelLength);

        Strip_General myStrip = new Strip_General(n1, n2);
        myStrip.setProperties(usrMat, modelLength, Y);

//        myStrip.setUdlX(force);
//
//        int maxTerms = 20;
//        //Y.computeAllIntegrals(maxTerms);
//
//        for (int k = 1; k < maxTerms; k++) {
//
//            int nTerms = k;
//            int nNodes = 2;
//
//            CoupledMatrix_1 cK = new CoupledMatrix_1(nNodes, nTerms);
//            CoupledVector_1 fK = new CoupledVector_1(nNodes, nTerms);
//
//            for (int i = 1; i < nTerms + 1; i++) {
//
//                for (int j = 1; j < nTerms + 1; j++) {
//
//                    cK.addStiffnessMatrix(myStrip.getStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
//
//                }
//                fK.addForceVector(myStrip.getLoadVector(i), myStrip.getNode1(), myStrip.getNode2(), i);
//
//            }
//
//            // SerializeUtils.serialize(cK.getMatrix(), "CC" + nTerms * 8);
//            Cholesky chol = new Cholesky();
//            Vector u = chol.getX(cK.getMatrix(), fK.getVector());
//            //cK.getMatrix().printf("cK");
//            // System.out.println("CC" + nTerms * 8 + " Done !");
//            //}
//
////        Matrix k = SerializeUtils.deserialze("CC" + nTerms*8);
////        k.printf("k");
//            ////Comparison with Euler beam theory
//            double I = myStrip.getStripThickness() * Math.pow(myStrip.getStripWidth(), 3) / 12.0;
//            //  System.out.println("I=" + I);
//            double E = usrMat.getEx();
//            //  System.out.println("E=" + E);
//            double L = myStrip.getStripLength();
//            // System.out.println("L=" + L);
//            double P = force;
//            // System.out.println("P=" + P);
//            double w = 5 * P * myStrip.getStripWidth() * Math.pow(L, 4) / (384 * E * I);
//
//            double uu = 0;
//            double vv = 0;
//            double ww = 0;
//            double tt = 0;
//
//            System.out.println("Beam theory = " + w);
//
//            Vector stressVec = Vector.getVector(3);
//            stressVec.clear();
//            Vector dispVec = Vector.getVector(4);
//            dispVec.clear();
//
//            int[] ind = {0, 1, 2, 3};
//
//            for (int i = 0; i < nTerms; i++) {
//                uu = u.get((i * 4)) * Y.getFunctionValue(modelLength / 2.0, i + 1);
//                vv = u.get((i * 4) + 1 * nTerms) * Y.getFirstDerivativeValue(modelLength / 2.0, i + 1);
//                ww = u.get((i * 4) + 2 * nTerms) * Y.getFunctionValue(modelLength / 2.0, i + 1);
//                tt = u.get((i * 4) + 3 * nTerms) * Y.getFunctionValue(modelLength / 2.0, i + 1);
//
//                dispVec.set(uu, 0);
//                dispVec.set(vv, 1);
//                dispVec.set(ww, 2);
//                dispVec.set(tt, 3);
//                
//               // stressVec.add(myStrip.getPropertyMatrix().multiply(myStrip.getStrainMatrix(0, modelLength / 2.0, i+1)).multiply(dispVec));
//               // myStrip.getPropertyMatrix().printf("Prop");
//                
//               // myStrip.getStrainMatrix(0, modelLength / 2.0, i+1).printf("Strain");
//
//            }
//
//            
//
//           
//            stressVec.printf("Stress");
//
//            System.out.print("Node1 " + uu);
//           
//            double error = 100 * (w - uu) / w;
//            System.out.println("Error: " + error);
//            // System.out.println(myStrip.getStripWidth());
//
//        }
        
        
        //BUCKLING TEST
        
        myStrip.setEdgeTractionAtNode1(0.001);
        myStrip.setEdgeTractionAtNode2(0.001);
        //Matrix Ke = myStrip.getStiffnessMatrix(1, 1);
        //Matrix Kg = myStrip.getGeometricMatrix(1, 1);
        
        //Longitudinal edges
        //                    u        v       w     t        u      v      w        t
        boolean [] status = {false,  false,  false,  false,   false,   false, false,   false};
        
        
        PartitionedSystem se =new PartitionedSystem(myStrip.getStiffnessMatrix(1, 1), status);
        PartitionedSystem sg =new PartitionedSystem(myStrip.getGeometricMatrix(1, 1), status);
        
        Matrix Ke = se.getKff();
        Matrix Kg = sg.getKff();
        
        
        //Ke.printf("Ke");
       // Kg.printf("Kg");
        
        
        Matrix K = Ke.multiply(Kg.inverse());
        
        Matrix eig = K.eigenMatrix();
        
        
        //eig.printf("eig values");
        
        //ev.printf("eig vectors");
        
        double alphaMin = eig.get(0,0);
        for (int i = 0; i < eig.cols(); i++)
        {
            if(eig.get(i,i) <= alphaMin)
            {
                alphaMin = eig.get(i,i);
                
            }
        }
        
        double fc = 0.001*alphaMin;
        
          double sigma = fc;
          
          double k = sigma*12*(1-(myStrip.getMaterial().getVx()*myStrip.getMaterial().getVx()))/(Math.PI*Math.PI*myStrip.getMaterial().getEx());
          
          k= k*myStrip.getStripWidth()*myStrip.getStripWidth();
          
          k = k/(myStrip.getStripThickness()*myStrip.getStripThickness());
          
          
          System.out.println(k);
        }
        

        //THREADING EXAMPLE
        /*
         double time1 = System.currentTimeMillis();
         partialFor p1 = new partialFor(0, 25000, "One");
         partialFor p2 = new partialFor(25000, 50000, "Two");
         partialFor p3 = new partialFor(50000, 75000, "Three");
         partialFor p4 = new partialFor(75000, 100000, "Four");

         final ExecutorService service;
         final Future<Double> task1, task2, task3, task4;

         service = Executors.newFixedThreadPool(4);
         task1 = service.submit(p1);
         task2 = service.submit(p2);
         task3 = service.submit(p3);
         task4 = service.submit(p4);

         double t = task1.get();
         double t2 = task2.get();
         double t3 = task3.get();
         double t4 = task4.get();

         service.shutdownNow();

         double temp = t + t2 + t3 + t4;
         System.out.println("P total = " + temp);

         double time2 = System.currentTimeMillis();
         System.out.println("TIME    =  " + (time2 - time1));

         time1 = System.currentTimeMillis();
         double c1 = 0;

         for (int i = 0; i < 100000; i++) {

         Matrix Kt = myStrip.getStiffnessMatrixInGlobalCoordinates(2).inverse();
         c1++;

         }

         System.out.println("C1 = " + c1);

         time2 = System.currentTimeMillis();
         System.out.println("TIME2    =  " + (time2 - time1));
         */
   // }
   

}
