/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import linalg.Matrix;
import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.series.Series;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class partialFor implements Callable<Double>
{
int startValue,endValue;
String threadName;
private Thread t;
 private volatile double total;
 int modelLength = 300;
        int force = 10;

        Material usrMat = new Material_Steel();
        Node n1 = new Node(0, 0);
        Node n2 = new Node(10, 0);
        
        Series Ym = new Series_SS(modelLength);
       


    public partialFor(int startValue, int endValue,String threadName) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.threadName = threadName;
       
    }

   public  void plus()
   {
       
       
       total++;
   }
   
   
    @Override
    public Double call() throws Exception {
        
    for (int i = startValue; i < endValue; i++)
        {
            //System.out.println(i); 
            plus();
            
        }   
    
    return total;
    }
    
   
    
}
