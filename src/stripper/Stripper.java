/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Strip_General;
import UI.Strip;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import linalg.Matrix;
import serialize.ProfileMatrix;
import linalg.Vector;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
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
     */
    public static void main(String[] args) throws InterruptedException, Exception {

        int modelLength = 2000;
        double force = 2;

        
        
        Material usrMat = new  Material_Steel();
        
        Node n1 = new Node(0, 0);
        Node n2 = new Node(100, 0);
        

        
       // System.out.println(Y.getI1(8, 8));

        Series_CC Y = new Series_CC(modelLength);
        
        Strip_General myStrip = new Strip_General(n1, n2);
        
        
        
        myStrip.setProperties(usrMat, 100, 2000, Y);
      
        

       
       //int [] validTerms = {0,1};
        

       
//       s.printf("S");
//      
//       Vector f = Vector.getVector(8);
//       f.clear();
//        
//       Cholesky chol = new Cholesky();
//       chol.getX(s, f).printf("U");
        
     
        

        int nTerms = 2;
        int nNodes = 2;

        Vector u = Vector.getVector(8);
        
        Vector f = Vector.getVector(nTerms*8);
        f.clear();
        

       CoupledMatrix_1 cK = new CoupledMatrix_1(nNodes, nTerms);
       
       
      
        
        

        int[] indices = new int[8*nTerms];
        
        for (int i = 1; i < nTerms+1; i++) 
        {
            indices[i-1] = i-1;          
            
            for (int j = 1; j < nTerms+1; j++) 
        {
        
            
            
           cK.addStiffnessMatrix(myStrip.getStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
            
            
            
        }
            
            
            

        }

        
       // cK.getMatrix().printf("cK");
        SerializeUtils.serialize(cK.getMatrix(), "SS"+ 8*nTerms);
        
        
        //Matrix m = SerializeUtils.deserialze("SS"+ 8*nTerms);
        
        //m.printf("des");
        
        
        Cholesky chol = new Cholesky();
        chol.getX(cK.getMatrix(), f).printf("U");

       Matrix s = myStrip.getStiffnessMatrix(7,7);
       
       
       
//       s.printf("S");
//      
//       Vector f = Vector.getVector(8);
//       f.clear();
//        
//       Cholesky chol = new Cholesky();
//       chol.getX(s, f).printf("U");

        

//        int nTerms = 6;
//        int nNodes = 2;
//
//        Vector u = Vector.getVector(8);
//        
//        Vector f = Vector.getVector(nTerms*8);
//        f.clear();
//        
//
//       CoupledMatrix_1 cK = new CoupledMatrix_1(nNodes, nTerms);
//       
//       
//      
//        
//        
//
//        
//        
//        for (int i = 1; i < nTerms+1; i++) 
//        {
//                      
//            
//            for (int j = 1; j < nTerms+1; j++) 
//        {
//        
//            
//            
//           cK.addStiffnessMatrix(myStrip.getStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
//            
//            
//            
//        }
//            
//            
//            
//
//        }
//
//        
//         cK.getMatrix().printf("cK");
//        
//        Cholesky chol = new Cholesky();
//        chol.getX(cK.getMatrix(), f).printf("U");

//        for (int n = 0; n < nNodes; n++) {
//            
//        
//        for (int m = 1; m < nTerms+1; m++)
//        {
//            for (int r = 0; r < nDofPerNode; r++)
//            {
//                u.add(Uck.get(Fck.scaledIndex(n,r,m)),(r+nDofPerNode*n)); 
//            }
//           
//        }
//        }
        
        
       
   
        
        
        
       //Uck.printf("UCK");

      //  u.printf("U : ", System.out);
        
//        myStrip.getRotationMatrix().transpose().multiply(u).printf("U local");
//        for (int i = 0; i < u.size(); i = i + 2) {
//            System.out.println(u.get(i));
//        }

// Comparison with Euler beam theory
        
        /*
        double I = myStrip.getStripThickness()*Math.pow(myStrip.getStripWidth(), 3)/12.0;
        System.out.println("I=" + I);
        double E = usrMat.getEx();
        System.out.println("E=" + E);
        double L = myStrip.getStripLength();
        System.out.println("L=" + L);
        double P = force;
        System.out.println("P=" + P);
        double w = P*myStrip.getStripWidth() * Math.pow(L, 4) / (384 * E * I);
        
       
          
       
        System.out.println("Beam theory = " + w);
        
        double error = 100*(w-u.get(0))/w;
        
        System.out.println("Error: " + error);
        */

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

    //verandering gemaak op PC!
    //
    //
}
