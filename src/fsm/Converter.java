/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm;

import linalg.Vector;

/**
 *
 * @author SJ
 */
public class Converter 
{
    
    public static boolean [] vecToBool(Vector vecStatus)
    {
        boolean [] status = new boolean[vecStatus.size()];
        
        for (int i = 0; i < vecStatus.size(); i++) 
        {
            if(vecStatus.get(i) == 0)
            {
                status [i] = false; 
            }
            else
            {
                status[i] = true;
            }
        }
        return status;
    }
    
    public static Vector  boolToVec(boolean [] status)
    {
        Vector vecStatus = Vector.getVector(status.length);
        
        for (int i = 0; i < status.length; i++)
        {
         if(status[i] == true)
         {
             vecStatus.set(1, i);
         }
         else
         {
            vecStatus.set(0, i); 
         }
        }
        
        return vecStatus;
    }
    
}
