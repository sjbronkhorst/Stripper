/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm.series;

/**
 *
 * @author SJ
 */
public abstract class BucklingSeries extends Series {

    public BucklingSeries(double a) {
        super(a);
    }

    @Override
    public double getYmIntegral(int m) {
        throw new UnsupportedOperationException("This series function does not suppport static analysis"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getFirstDerivativeValue(double y, int m) {
        throw new UnsupportedOperationException("This series function does not suppport static analysis"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getSecondDerivativeValue(double y, int m) {
        throw new UnsupportedOperationException("This series function does not suppport static analysis"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getFirstDerivativeIntegral(int m) {
        throw new UnsupportedOperationException("This series function does not suppport static analysis"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getVScalingValue(double y, int m) {
        return 1;
    }
    
    
    @Override
    public boolean onlySupportsBuckling()
    {
        return true;
    }
}
