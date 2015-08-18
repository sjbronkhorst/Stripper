/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.math.BigDecimal;
import linalg.Matrix;
import linalg.Vector;

/**
 *
 * @author SJ
 */
public class Cholesky 
{
    
 public Vector getX(Matrix A , Vector y)
 {
     
     Matrix L = getL(A);
     Vector b = getB(L, y);
     Vector x = Vector.getVector(b.rows());
     
     
     for (int i = b.rows()-1; i > -1; i--)
     {
     double sum = 0;
     
         for (int j = b.rows()-1; j > i; j--) 
         {
             sum += x.get(j)*L.get(j, i);
             
             }
         
         x.set((b.get(i)-sum)/L.get(i, i), i);
         
         if(L.get(i, i) == 0)
         {
             System.out.println("Dividing by zero (Cholesky line 40)");
         }
         
         
     }
     
     
     //x.printf("X");
     return x;
     
     
 }
    
    
    
 public strictfp Matrix getL(Matrix A)
 {
     Matrix R = Matrix.getMatrix(A.rows(), A.cols());
     R.clear();
     
     for (int i = 0; i < A.rows(); i++) 
     {
         for (int j = 0; j <=i; j++)
         {
         
            
                 double sum = 0;
                 
                 for (int k = 0; k < j; k++)
                 {
                 sum+=R.get(i, k) * R.get(j, k);
                 
                 
                 
                 }
                 
                 
                 
            if(i==j)
            {
                
                
                
                
                double ans = Math.sqrt(A.get(i, i)-sum);
                
                              
                

               

                
                
                if(Double.isNaN(ans))
                {
                    System.out.println("A :"+A.get(i,i));
                    System.out.println("Sum " + sum);
                   
                    
                    System.out.println("ERROR : Matrix is not positive-definite");
                }
                
                R.set(ans, i, i);
            }
            else
             {
                 R.set(1.0/R.get(j,j) * (A.get(i, j) - sum) , i , j);
                 
                 if(R.get(j,j) == 0)
                 {
                     System.out.println("Dividing by zero (Cholesky line 94)");
                 }
             }
             
             
         }
         
     }
    // R.printf("R");
     
     return R;
 }
 
 public Vector getB(Matrix L , Vector y)
 {
  
     Vector b = Vector.getVector(y.rows());
     
     for (int i = 0; i < b.rows(); i++)
     {
     double sum = 0;
     
         for (int j = 0; j < i; j++) 
         {
             sum += b.get(j)*L.get(i, j);
         }
         
         b.set((y.get(i)-sum)/L.get(i, i), i);
         
        
         
         
         
     }
     
     
     //b.printf("b");
    return b; 
 }
 
 
 public static void main (String[]args)
 {
     Matrix K = Matrix.getMatrix(3, 3);
     
     K.set(25, 0, 0);
     K.set(15, 0, 1);
     K.set(-5,0 , 2);
     
     K.set(15 , 1, 0);
     K.set(18 , 1 , 1);
     K.set(0 , 1 ,2);
     
     K.set(-5,2,0);
     K.set(0,2,1);
     K.set(11,2,2);
     
     K.printf("K");
     
     Cholesky c =new Cholesky();
     
     c.getL(K).printf("L");
     
     
     
     
 }
 
 
 
}
