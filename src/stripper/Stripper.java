/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public static void main(String[] args) throws InterruptedException, Exception {

        int modelLength = 2000;
        double force = 0.0001;

        Material usrMat = new Material_Steel();

        Node n1 = new Node(0, 0);
        Node n2 = new Node(0, 1);

        Series_SS Y = new Series_SS(modelLength);

        Strip_SS myStrip = new Strip_SS(n1, n2);
        myStrip.setProperties(usrMat, 100, modelLength, Y);

        myStrip.setUdlX(force);

       int maxTerms = 20;
        //Y.computeAllIntegrals(maxTerms);
        
       
        
            for (int k = 1; k < maxTerms; k++) {
            
        
        
            int nTerms = k;
            int nNodes = 2;

            CoupledMatrix_1 cK = new CoupledMatrix_1(nNodes, nTerms);
            CoupledVector_1 fK = new CoupledVector_1(nNodes, nTerms);

            for (int i = 1; i < nTerms + 1; i++) {

                for (int j = 1; j < nTerms + 1; j++) {

                    cK.addStiffnessMatrix(myStrip.getStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);

                }
                fK.addForceVector(myStrip.getLoadVector(i), myStrip.getNode1(), myStrip.getNode2(), i);

            }

           // SerializeUtils.serialize(cK.getMatrix(), "CC" + nTerms * 8);
            Cholesky chol = new Cholesky();
            Vector u = chol.getX(cK.getMatrix(), fK.getVector());
            //cK.getMatrix().printf("cK");
            // System.out.println("CC" + nTerms * 8 + " Done !");
            //}

//        Matrix k = SerializeUtils.deserialze("CC" + nTerms*8);
//        k.printf("k");
            ////Comparison with Euler beam theory
            double I =  myStrip.getStripThickness()* Math.pow(myStrip.getStripWidth(), 3) / 12.0;
            //  System.out.println("I=" + I);
            double E = usrMat.getEx();
            //  System.out.println("E=" + E);
            double L = myStrip.getStripLength();
            // System.out.println("L=" + L);
            double P = force;
            // System.out.println("P=" + P);
            double w = 5*P * myStrip.getStripWidth() * Math.pow(L, 4) / (384 * E * I);

            double uu1 = 0;
            double uu2 = 0;
            //System.out.println("Beam theory = " + w);

            for (int i = 0; i < nTerms; i++) {
                uu1 += u.get((i * 4)) * Y.getFunctionValue(modelLength / 2.0, i + 1);

            }
            
            for (int i = 0; i < nTerms; i++) {
                uu2 += u.get((i * 4)+4*nTerms) * Y.getFunctionValue(modelLength / 2.0, i + 1);

            }

            System.out.print("Node1 " + uu1);
            System.out.println("   Node2 " + uu2);
            double error = 100 * (w - uu1) / w;
            //System.out.println("Error: " + error);
               // System.out.println(myStrip.getStripWidth());
        
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
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
