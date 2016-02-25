/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

/**
 *
 * @author SJ
 */
public class EigenValueFixer {

    public static void fix(BucklingDataPoint[] points) {

        for (int i = 1; i < points.length; i++) {

                points[i].getMinParamVector().printf("Vector before scale ");
            for (int j = 0; j < points[i].getMinParamVector().rows(); j++) {
                if (points[i - 1].getMinParamVector().get(j) < 0 && points[i].getMinParamVector().get(j) > 0) {
                    points[i].getMinParamVector().set(points[i].getMinParamVector().get(j) * -1, j);
                }

                if (points[i - 1].getMinParamVector().get(j) > 0 && points[i].getMinParamVector().get(j) < 0) {
                    points[i].getMinParamVector().set(points[i].getMinParamVector().get(j) * -1, j);
                }

            }

                points[i].getMinParamVector().printf("Vector after scale ");
        }

    }

}
