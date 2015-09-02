/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.UIStrip;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import linalg.Matrix;
import stripper.materials.Material;
import stripper.series.Series;


/**
 *
 * @author SJ
 */
public class Strip_General extends Strip {

    private int mLastComputed = 0;
    private int nLastComputed = 0;
    
    

    private double[] integralArray = new double[5];

    public Strip_General(Node node1, Node node2) {

        setNode1(node1);
        setNode2(node2);

    }
    
     public Strip_General(UIStrip uiStrip) {
        if (!uiStrip.hasBothNodes()) {

            hasNode1 = false;
            hasNode2 = false;
            this.node1Id.set(0);
            this.node2Id.set(0);
        } else {
            setNode1(uiStrip.getNode1());
            setNode2(uiStrip.getNode2());
            
            setUdlX(uiStrip.getUdlX());
            setUdlY(uiStrip.getUdlY());
            setUdlZ(uiStrip.getUdlZ());
            
            this.stripId.set(uiStrip.getStripId());
            
            this.pointLoads = uiStrip.getPointLoadList();
        }
    }

    public boolean integralLastComputed(int m, int n) {
        return ((m == mLastComputed) && (n == nLastComputed));

    }

    public void computeIntegralArray(int m, int n) {
        if (!integralLastComputed(m, n)) {
            integralArray = Y.getIntegralValues(m, n);
            mLastComputed = m;
            nLastComputed = n;
        }
    }

    public Strip_General() {

        hasNode1 = false;
        hasNode2 = false;
        this.node1Id.set(0);
        this.node2Id.set(0);

    }

    @Override
    public void setProperties(Material mat, double thickness, double length, Series Y) {
        super.mat = mat;
        super.t = thickness;
        super.a = length;
        super.Y = Y;
    }

   

    public Matrix getBendingStiffnessMatrix(int m, int n, double[] integralArray) {

        Matrix S = Matrix.getMatrix(4, 4);

        BigDecimal _1 = BigDecimal.ONE;
        BigDecimal _neg1 = new BigDecimal("-1");
        BigDecimal _12 = new BigDecimal("12");
        BigDecimal _5040 = new BigDecimal("5040");
        BigDecimal _504 = new BigDecimal("504");
        BigDecimal _156 = new BigDecimal("156");
        BigDecimal _2016 = new BigDecimal("2016");
        BigDecimal _420 = new BigDecimal("420");
        BigDecimal _462 = new BigDecimal("462");
        BigDecimal _42 = new BigDecimal("42");
        BigDecimal _22 = new BigDecimal("22");
        BigDecimal _168 = new BigDecimal("168");
        BigDecimal _2520 = new BigDecimal("2520");
        BigDecimal _54 = new BigDecimal("54");
        BigDecimal _13 = new BigDecimal("13");
        BigDecimal _1680 = new BigDecimal("1680");
        BigDecimal _56 = new BigDecimal("56");
        BigDecimal _4 = new BigDecimal("4");
        BigDecimal _224 = new BigDecimal("224");
        BigDecimal _840 = new BigDecimal("840");
        BigDecimal _3 = new BigDecimal("3");
        BigDecimal _14 = new BigDecimal("14");

        //double b = getStripWidth();
        BigDecimal b = new BigDecimal(getStripWidth());

        //double Ex = mat.getEx();
        BigDecimal Ex = new BigDecimal(mat.getEx());
        //double Ey = mat.getEy();
        BigDecimal Ey = new BigDecimal(mat.getEy());
        //double vx = mat.getVx();
        BigDecimal vx = new BigDecimal(mat.getVx());
        //double vy = mat.getVy();
        BigDecimal vy = new BigDecimal(mat.getVy());
        //double G = mat.getG();
        BigDecimal G = new BigDecimal(mat.getG());

        BigDecimal _t = new BigDecimal(getStripThickness());

        //double Dx = (Ex * t * t * t) / (12 * (1 - vx * vy));
        BigDecimal Dx = (Ex.multiply(_t).multiply(_t).multiply(_t)).divide(_12.multiply(_1.subtract((vx.multiply(vy)))), 1000, RoundingMode.HALF_UP);

        //double Dy = (Ey * t * t * t) / (12 * (1 - vx * vy));
        BigDecimal Dy = (Ey.multiply(_t).multiply(_t).multiply(_t)).divide(_12.multiply(_1.subtract((vx.multiply(vy)))), 1000, RoundingMode.HALF_UP);

        //double D1 = (vx * Ey * t * t * t) / (12 * (1 - vx * vy));
        BigDecimal D1 = (vx.multiply(Ey).multiply(_t).multiply(_t).multiply(_t)).divide(_12.multiply(_1.subtract((vx.multiply(vy)))), 1000, RoundingMode.HALF_UP);

        //double Dxy = G * t * t * t / 12;
        BigDecimal Dxy = G.multiply(_t).multiply(_t).multiply(_t).divide(_12, 1000, RoundingMode.HALF_UP);

        computeIntegralArray(m, n);
        double[] Idoubles = integralArray;
        BigDecimal I[] = new BigDecimal[5];
        I[0] = new BigDecimal(Idoubles[0]);
        I[1] = new BigDecimal(Idoubles[1]);
        I[2] = new BigDecimal(Idoubles[2]);
        I[3] = new BigDecimal(Idoubles[3]);
        I[4] = new BigDecimal(Idoubles[4]);

        //double c = 1.0 / (420 * b * b * b);
        BigDecimal c = _1.divide((_420.multiply(b).multiply(b).multiply(b)), 1000, RoundingMode.HALF_UP);

//        double K11 = c * (5040 * Dx * I[0]
//                - 504 * b * b * D1 * I[1]
//                - 504 * b * b * D1 * I[2]
//                + 156 * b * b * b * b * Dy * I[3]
//                + 2016 * b * b * Dxy * I[4]);
        BigDecimal K111 = _5040.multiply(Dx).multiply(I[0]);
        BigDecimal K112 = _neg1.multiply(_504).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K113 = _neg1.multiply(_504).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K114 = _156.multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K115 = _2016.multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K11B = K111.add(K112).add(K113).add(K114).add(K115);
        K11B = K11B.multiply(c);
        double K11 = K11B.doubleValue();

//        double K12 = c * (2520 * b * Dx * I[0]
//                - 462 * b * b * b * D1 * I[1]
//                - 42 * b * b * b * D1 * I[2]
//                + 22 * b * b * b * b * b * Dy * I[3]
//                + 168 * b * b * b * Dxy * I[4]);
        BigDecimal K121 = _2520.multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K122 = _neg1.multiply(_462).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K123 = _neg1.multiply(_42).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K124 = _22.multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K125 = _168.multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K12B = K121.add(K122).add(K123).add(K124).add(K125);
        K12B = K12B.multiply(c);
        double K12 = K12B.doubleValue();

//        double K13 = c * (-5040 * Dx * I[0]
//                + 504 * b * b * D1 * I[1]
//                + 504 * b * b * D1 * I[2]
//                + 54 * b * b * b * b * Dy * I[3]
//                - 2016 * b * b * Dxy * I[4]);
        BigDecimal K131 = _neg1.multiply(_5040).multiply(Dx).multiply(I[0]);
        BigDecimal K132 = _504.multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K133 = _504.multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K134 = _54.multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K135 = _neg1.multiply(_2016).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K13B = K131.add(K132).add(K133).add(K134).add(K135);
        K13B = K13B.multiply(c);
        double K13 = K13B.doubleValue();

//        double K14 = c * (2520 * b * Dx * I[0]
//                - 42 * b * b * b * D1 * I[1]
//                - 42 * b * b * b * D1 * I[2]
//                - 13 * b * b * b * b * b * Dy * I[3]
//                + 168 * b * b * b * Dxy * I[4]);
        BigDecimal K141 = _2520.multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K142 = _neg1.multiply(_42).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K143 = _neg1.multiply(_42).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K144 = _neg1.multiply(_13).multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K145 = _168.multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K14B = K141.add(K142).add(K143).add(K144).add(K145);
        K14B = K14B.multiply(c);
        double K14 = K14B.doubleValue();
        
         BigDecimal K211 = _2520.multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K212 = _neg1.multiply(_462).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K213 = _neg1.multiply(_42).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K214 = _22.multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K215 = _168.multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K21B = K211.add(K212).add(K213).add(K214).add(K215);
        K21B = K21B.multiply(c);
        double K21 = K21B.doubleValue();

        

//        double K22 = c * (1680 * b * b * Dx * I[0]
//                - 56 * b * b * b * b * D1 * I[1]
//                - 56 * b * b * b * b * D1 * I[2]
//                + 4 * b * b * b * b * b * b * Dy * I[3]
//                + 224 * b * b * b * b * Dxy * I[4]);
        BigDecimal K221 = _1680.multiply(b).multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K222 = _neg1.multiply(_56).multiply(b).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K223 = _neg1.multiply(_56).multiply(b).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K224 = _4.multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K225 = _224.multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K22B = K221.add(K222).add(K223).add(K224).add(K225);
        K22B = K22B.multiply(c);
        double K22 = K22B.doubleValue();

        double K23 = -K14;

//        double K24 = c * (840 * b * b * Dx * I[0]
//                + 14 * b * b * b * b * D1 * I[1]
//                + 14 * b * b * b * b * D1 * I[2]
//                - 3 * b * b * b * b * b * b * Dy * I[3]
//                - 56 * b * b * b * b * Dxy * I[4]);
        BigDecimal K241 = _840.multiply(b).multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K242 = _14.multiply(b).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K243 = _14.multiply(b).multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K244 = _neg1.multiply(_3).multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K245 = _neg1.multiply(_56).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K24B = K241.add(K242).add(K243).add(K244).add(K245);
        K24B = K24B.multiply(c);
        double K24 = K24B.doubleValue();

       BigDecimal K311 = _neg1.multiply(_5040).multiply(Dx).multiply(I[0]);
        BigDecimal K312 = _504.multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K313 = _504.multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K314 = _54.multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K315 = _neg1.multiply(_2016).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K31B = K311.add(K312).add(K313).add(K314).add(K315);
        K31B = K31B.multiply(c);
        double K31 = K31B.doubleValue();
        
        
        
         BigDecimal K321 = _neg1.multiply(_2520).multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K322 = _42.multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K323 = _42.multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K324 = _13.multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K325 = _neg1.multiply(_168).multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K32B = K321.add(K322).add(K323).add(K324).add(K325);
        K32B = K32B.multiply(c);
        double K32 = K32B.doubleValue();
        
        
        double K33 = K11;
        
         BigDecimal K341 = _neg1.multiply(_2520).multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K342 = _462.multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K343 = _42.multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K344 = _neg1.multiply(_22).multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K345 = _neg1.multiply(_168).multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K34B = K341.add(K342).add(K343).add(K344).add(K345);
        K34B = K34B.multiply(c);
        double K34 = K34B.doubleValue();

        double K41 = K14;
        double K42 = K24;
        
         BigDecimal K431 = _neg1.multiply(_2520).multiply(b).multiply(Dx).multiply(I[0]);
        BigDecimal K432 = _462.multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[2]);
        BigDecimal K433 = _42.multiply(b).multiply(b).multiply(b).multiply(D1).multiply(I[1]);
        BigDecimal K434 = _neg1.multiply(_22).multiply(b).multiply(b).multiply(b).multiply(b).multiply(b).multiply(Dy).multiply(I[3]);
        BigDecimal K435 = _neg1.multiply(_168).multiply(b).multiply(b).multiply(b).multiply(Dxy).multiply(I[4]);

        BigDecimal K43B = K431.add(K432).add(K433).add(K434).add(K435);
        K43B = K43B.multiply(c);
        double K43 = K43B.doubleValue();
        
        double K44 = K22;

        S.set(K11, 0, 0);
        S.set(K12, 0, 1);
        S.set(K13, 0, 2);
        S.set(K14, 0, 3);

        S.set(K21, 1, 0);
        S.set(K22, 1, 1);
        S.set(K23, 1, 2);
        S.set(K24, 1, 3);

        S.set(K31, 2, 0);
        S.set(K32, 2, 1);
        S.set(K33, 2, 2);
        S.set(K34, 2, 3);

        S.set(K41, 3, 0);
        S.set(K42, 3, 1);
        S.set(K43, 3, 2);
        S.set(K44, 3, 3);

        return S;
    }

    public Matrix getOldBendingStiffnessMatrix(int m, int n) {

        Matrix S = Matrix.getMatrix(4, 4);

        double b = getStripWidth();

        double Ex = mat.getEx();

        double Ey = mat.getEy();

        double vx = mat.getVx();

        double vy = mat.getVy();

        double G = mat.getG();

        //double t = getStripThickness();
        double Dx = (Ex * t * t * t) / (12 * (1 - vx * vy));

        double Dy = (Ey * t * t * t) / (12 * (1 - vx * vy));

        double D1 = (vx * Ey * t * t * t) / (12 * (1 - vx * vy));

        double Dxy = G * t * t * t / 12;

        computeIntegralArray(m, n);
        double[] I = integralArray;

        double c = 1.0 / (420 * b * b * b);

        double K11 = c * (5040 * Dx * I[0]
                - 504 * b * b * D1 * I[1]
                - 504 * b * b * D1 * I[2]
                + 156 * b * b * b * b * Dy * I[3]
                + 2016 * b * b * Dxy * I[4]);

        double K12 = c * (2520 * b * Dx * I[0]
                - 462 * b * b * b * D1 * I[1]
                - 42 * b * b * b * D1 * I[2]
                + 22 * b * b * b * b * b * Dy * I[3]
                + 168 * b * b * b * Dxy * I[4]);

        double K13 = c * (-5040 * Dx * I[0]
                + 504 * b * b * D1 * I[1]
                + 504 * b * b * D1 * I[2]
                + 54 * b * b * b * b * Dy * I[3]
                - 2016 * b * b * Dxy * I[4]);

        double K14 = c * (2520 * b * Dx * I[0]
                - 42 * b * b * b * D1 * I[1]
                - 42 * b * b * b * D1 * I[2]
                - 13 * b * b * b * b * b * Dy * I[3]
                + 168 * b * b * b * Dxy * I[4]);

        double K21 = K12;

        double K22 = c * (1680 * b * b * Dx * I[0]
                - 56 * b * b * b * b * D1 * I[1]
                - 56 * b * b * b * b * D1 * I[2]
                + 4 * b * b * b * b * b * b * Dy * I[3]
                + 224 * b * b * b * b * Dxy * I[4]);

        double K23 = -K14;

        double K24 = c * (840 * b * b * Dx * I[0]
                + 14 * b * b * b * b * D1 * I[1]
                + 14 * b * b * b * b * D1 * I[2]
                - 3 * b * b * b * b * b * b * Dy * I[3]
                - 56 * b * b * b * b * Dxy * I[4]);

        double K31 = K13;
        double K32 = K23;
        double K33 = K11;
        double K34 = -K21;

        double K41 = K14;
        double K42 = K24;
        double K43 = K34;
        double K44 = K22;

        S.set(K11, 0, 0);
        S.set(K12, 0, 1);
        S.set(K13, 0, 2);
        S.set(K14, 0, 3);

        S.set(K21, 1, 0);
        S.set(K22, 1, 1);
        S.set(K23, 1, 2);
        S.set(K24, 1, 3);

        S.set(K31, 2, 0);
        S.set(K32, 2, 1);
        S.set(K33, 2, 2);
        S.set(K34, 2, 3);

        S.set(K41, 3, 0);
        S.set(K42, 3, 1);
        S.set(K43, 3, 2);
        S.set(K44, 3, 3);

        return S;
    }

    public Matrix getMembraneStiffnessMatrix(int m, int n, double[] integralArray) {

        Matrix M = Matrix.getMatrix(4, 4);
        int scale = 1000;

        //double b = getStripWidth();
        BigDecimal b = new BigDecimal(getStripWidth());

        //double Ex = mat.getEx();
        BigDecimal Ex = new BigDecimal(mat.getEx());
        //double Ey = mat.getEy();
        BigDecimal Ey = new BigDecimal(mat.getEy());
        //double vx = mat.getVx();
        BigDecimal vx = new BigDecimal(mat.getVx());
        //double vy = mat.getVy();
        BigDecimal vy = new BigDecimal(mat.getVy());
        //double G = mat.getG();
        BigDecimal G = new BigDecimal(mat.getG());

        BigDecimal _1 = BigDecimal.ONE;
        BigDecimal _neg1 = new BigDecimal("-1");
        BigDecimal _2 = new BigDecimal("2");
        BigDecimal _3 = new BigDecimal("3");
        BigDecimal _6 = new BigDecimal("6");

        BigDecimal _a = new BigDecimal(getStripLength());
        BigDecimal mu_m = new BigDecimal(Y.getMu_m(m));
        BigDecimal mu_n = new BigDecimal(Y.getMu_m(n));

        double[] Idoubles = integralArray;

        BigDecimal I[] = new BigDecimal[5];
        I[0] = new BigDecimal(Idoubles[0]);
        I[1] = new BigDecimal(Idoubles[1]);
        I[2] = new BigDecimal(Idoubles[2]);
        I[3] = new BigDecimal(Idoubles[3]);
        I[4] = new BigDecimal(Idoubles[4]);

        //double C1 = Y.getMu_m(m) / a;
        BigDecimal C1 = mu_m.divide(_a, scale, RoundingMode.HALF_UP);
        //double C2 = Y.getMu_m(n) / a;
        BigDecimal C2 = mu_n.divide(_a, scale, RoundingMode.HALF_UP);

        //double K1 = Ex / (1 - vx * vy);
        BigDecimal K1 = Ex.divide((_1.subtract(vx.multiply(vy))), scale, RoundingMode.HALF_EVEN);
        //double K2 = vx * Ey / (1 - vx * vy);
        BigDecimal K2 = vx.multiply(Ey).divide((_1.subtract(vx.multiply(vy))), scale, RoundingMode.HALF_EVEN);
        //double K3 = Ey / (1 - vx * vy);
        BigDecimal K3 = Ey.divide((_1.subtract(vx.multiply(vy))), scale, RoundingMode.HALF_EVEN);
        //double K4 = G;
        BigDecimal K4 = G;

        if (b.equals(BigDecimal.ZERO)) {
            System.out.println("B is zero");
        }

//        double K11 = K1 * (1.0 / b) * I[0]
//                + K4 * (b / 3.0) * I[4];
        BigDecimal K111 = K1.multiply((_1.divide(b, scale, RoundingMode.HALF_DOWN))).multiply(I[0]);
        BigDecimal K112 = K4.multiply((b.divide(_3, scale, RoundingMode.HALF_UP))).multiply(I[4]);

        BigDecimal K11B = K111.add(K112);
        double K11 = K11B.doubleValue();

//        double K12 = K2 * (-1.0 / (2 * C2)) * I[2]
//                + K4 * (-1.0 / (2 * C2)) * I[4];
        BigDecimal K121 = K2.multiply(_neg1).divide((_2.multiply(C2)), scale, RoundingMode.HALF_DOWN).multiply(I[2]);
        BigDecimal K122 = K4.multiply(_neg1).divide((_2.multiply(C2)), scale, RoundingMode.HALF_UP).multiply(I[4]);

        BigDecimal K12B = K121.add(K122);
        double K12 = K12B.doubleValue();

//        double K13 = K1 * (-1.0 / (b)) * I[0]
//                + K4 * (b / 6.0) * I[4];
        BigDecimal K131 = K1.multiply((_neg1.divide(b, scale, RoundingMode.HALF_DOWN))).multiply(I[0]);
        BigDecimal K132 = K4.multiply((b.divide(_6, scale, RoundingMode.HALF_UP))).multiply(I[4]);

        BigDecimal K13B = K131.add(K132);
        double K13 = K13B.doubleValue();

//        double K14 = K2 * (-1.0 / (2 * C2)) * I[2]
//                + K4 * (1.0 / (2 * C2)) * I[4];
        BigDecimal K141 = K2.multiply(_neg1).divide((_2.multiply(C2)), scale, RoundingMode.HALF_DOWN).multiply(I[2]);
        BigDecimal K142 = K4.multiply(_1).divide((_2.multiply(C2)), scale, RoundingMode.HALF_UP).multiply(I[4]);

        BigDecimal K14B = K141.add(K142);
        double K14 = K14B.doubleValue();

        BigDecimal K211 = K2.multiply(_neg1.divide(_2.multiply(C1), scale, RoundingMode.HALF_DOWN)).multiply(I[1]);
        BigDecimal K212 = K4.multiply(_neg1.divide(_2.multiply(C1), scale, RoundingMode.HALF_UP)).multiply(I[4]);
        BigDecimal K21B = K211.add(K212);
        double K21 = K21B.doubleValue();

//        double K22 = K3 * (b / (3 * C1 * C2)) * I[3]
//                + K4 * (1.0 / (b * C1 * C2)) * I[4];
        BigDecimal K221 = K3.multiply((b.divide(_3.multiply(C1).multiply(C2), scale, RoundingMode.HALF_DOWN))).multiply(I[3]);

        BigDecimal K222 = K4.multiply((_1.divide(b.multiply(C1).multiply(C2), scale, RoundingMode.HALF_UP))).multiply(I[4]);

        BigDecimal K22B = K221.add(K222);
        double K22 = K22B.doubleValue();

//        double K23 = K2 * (1.0 / (2 * C1)) * I[1]
//                + K4 * (-1.0 / (2 * C1)) * I[4];
        BigDecimal K231 = K2.multiply((_1.divide((_2.multiply(C1)), scale, RoundingMode.HALF_DOWN))).multiply(I[1]);
        BigDecimal K232 = K4.multiply((_neg1.divide((_2.multiply(C1)), scale, RoundingMode.HALF_UP))).multiply(I[4]);

        BigDecimal K23B = K231.add(K232);
        double K23 = K23B.doubleValue();

//        double K24 = K3 * (b / (6 * C1 * C2)) * I[3]
//                + K4 * (-1.0 / (b * C1 * C2)) * I[4];
        BigDecimal K241 = K3.multiply((b.divide((_6.multiply(C1).multiply(C2)), scale, RoundingMode.HALF_DOWN))).multiply(I[3]);
        BigDecimal K242 = K4.multiply((_neg1.divide(b.multiply(C1).multiply(C2), scale, RoundingMode.HALF_UP))).multiply(I[4]);

        BigDecimal K24B = K241.add(K242);
        double K24 = K24B.doubleValue();

        double K33 = K11;
        double K34 = -K12;

        double K44 = K22;

        double K31 = K13;
        double K32 = -K14;
        double K41 = -K23;
        double K42 = K24;
        double K43 = -K21;

        M.set(K11, 0, 0);
        M.set(K12, 0, 1);
        M.set(K13, 0, 2);
        M.set(K14, 0, 3);

        M.set(K21, 1, 0);
        M.set(K22, 1, 1);
        M.set(K23, 1, 2);
        M.set(K24, 1, 3);

        M.set(K31, 2, 0);
        M.set(K32, 2, 1);
        M.set(K33, 2, 2);
        M.set(K34, 2, 3);

        M.set(K41, 3, 0);
        M.set(K42, 3, 1);
        M.set(K43, 3, 2);
        M.set(K44, 3, 3);

        M.scale(super.t);

        return M;
    }

    public Matrix getOldMembraneStiffnessMatrix(int m, int n, double[] integralArray) {

        Matrix M = Matrix.getMatrix(4, 4);

        double b = getStripWidth();

        double Ex = mat.getEx();
        double Ey = mat.getEy();
        double vx = mat.getVx();
        double vy = mat.getVy();
        double G = mat.getG();

        double[] I = integralArray;

        double C1 = Y.getMu_m(m) / a;
        double C2 = Y.getMu_m(n) / a;

        double K1 = Ex / (1 - vx * vy);
        double K2 = vx * Ey / (1 - vx * vy);
        double K3 = Ey / (1 - vx * vy);
        double K4 = G;

        if (b == 0) {
            System.out.println("B is zero");
        }

        double K11 = K1 * (1.0 / b) * I[0]
                + K4 * (b / 3.0) * I[4];

        double K12 = K2 * (-1.0 / (2 * C2)) * I[2]
                + K4 * (-1.0 / (2 * C2)) * I[4];

        double K13 = K1 * (-1.0 / (b)) * I[0]
                + K4 * (b / 6.0) * I[4];

        double K14 = K2 * (-1.0 / (2 * C2)) * I[2]
                + K4 * (1.0 / (2 * C2)) * I[4];

        double K22 = K3 * (b / (3 * C1 * C2)) * I[3]
                + K4 * (1.0 / (b * C1 * C2)) * I[4];

        double K23 = K2 * (1.0 / (2 * C1)) * I[1]
                + K4 * (-1.0 / (2 * C1)) * I[4];

        double K24 = K3 * (b / (6 * C1 * C2)) * I[3]
                + K4 * (-1.0 / (b * C1 * C2)) * I[4];

        double K33 = K11;
        double K34 = -K12;

        double K44 = K22;

        double K21 = K12;
        double K31 = K13;
        double K32 = -K14;
        double K41 = -K23;
        double K42 = K24;
        double K43 = -K21;

        M.set(K11, 0, 0);
        M.set(K12, 0, 1);
        M.set(K13, 0, 2);
        M.set(K14, 0, 3);

        M.set(K21, 1, 0);
        M.set(K22, 1, 1);
        M.set(K23, 1, 2);
        M.set(K24, 1, 3);

        M.set(K31, 2, 0);
        M.set(K32, 2, 1);
        M.set(K33, 2, 2);
        M.set(K34, 2, 3);

        M.set(K41, 3, 0);
        M.set(K42, 3, 1);
        M.set(K43, 3, 2);
        M.set(K44, 3, 3);

        M.scale(getStripThickness());

        return M;
    }

    @Override
    public Matrix getStiffnessMatrix(int m, int n) {
        Matrix K = Matrix.getMatrix(8, 8);

        computeIntegralArray(m, n);

        K.clear();

        int[] bendingIndices = {2, 3, 6, 7};
        int[] membraneIndices = {0, 1, 4, 5};

         Callable<Void> tsk1 = () -> {
        K.addSubmatrix(getBendingStiffnessMatrix(m, n, integralArray), bendingIndices);
            return null;
         };

        Callable<Void> tsk2 = () -> {
        K.addSubmatrix(getMembraneStiffnessMatrix(m, n, integralArray), membraneIndices);

             return null;
         };
        ExecutorService service;
        final Future<Void> thread1, thread2;

        service = Executors.newFixedThreadPool(2);
        thread1 = service.submit(tsk1);
        thread2 = service.submit(tsk2);
        
        
        try {
            thread1.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(Strip_General.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Strip_General.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            thread2.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(Strip_General.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Strip_General.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        service.shutdownNow();
        return K;
    }

//    public Matrix getOldStiffnessMatrix(int m, int n) {
//        Matrix K = Matrix.getMatrix(8, 8);
//
//        K.clear();
//
//        int[] bendingIndices = {2, 3, 6, 7};
//
//        K.addSubmatrix(getOldBendingStiffnessMatrix(m, n), bendingIndices);
//
//        int[] membraneIndices = {0, 1, 4, 5};
//
//        K.addSubmatrix(getOldMembraneStiffnessMatrix(m, n), membraneIndices);
//
//        return K;
//    }
    

}
