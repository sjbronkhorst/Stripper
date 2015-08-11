/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.control.ProgressIndicator;
import linalg.Matrix;
import linalg.Vector;
import stripper.Cholesky;
import stripper.Node;
import stripper.series.Series;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class SystemEquation {

    List<Strip> strips = new ArrayList<>();
    List<Node> nodes = new ArrayList<>();
    int[][] localToGlobalConfNumbering;
    double[] dataPoints;
    Vector[] Uarr = new Vector[101];
    private ProgressIndicator pi;
    
    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(0);
    
    public ReadOnlyDoubleProperty progressProperty()
    {
        return progress;
    }

    public SystemEquation(List<Strip> strips, List<Node> nodes) {
        this.strips = strips;
        this.nodes = nodes;

        localToGlobalConfNumbering = new int[strips.size() * 8][2];

        for (int i = 0; i < strips.size() * 8; i++) {
            localToGlobalConfNumbering[i][0] = i;
        }
        int count = 0;

        for (Strip s : strips) {

            localToGlobalConfNumbering[count * 8][1] = (s.getNode1Id() - 1) * 4;
            localToGlobalConfNumbering[count * 8 + 1][1] = (s.getNode1Id() - 1) * 4 + 1;
            localToGlobalConfNumbering[count * 8 + 2][1] = (s.getNode1Id() - 1) * 4 + 2;
            localToGlobalConfNumbering[count * 8 + 3][1] = (s.getNode1Id() - 1) * 4 + 3;

            localToGlobalConfNumbering[count * 8 + 4][1] = (s.getNode2Id() - 1) * 4;
            localToGlobalConfNumbering[count * 8 + 5][1] = (s.getNode2Id() - 1) * 4 + 1;
            localToGlobalConfNumbering[count * 8 + 6][1] = (s.getNode2Id() - 1) * 4 + 2;
            localToGlobalConfNumbering[count * 8 + 7][1] = (s.getNode2Id() - 1) * 4 + 3;

            count++;

        }

    }
    
    

    public Vector[] getDisplacementVector() {
        
        

        for (int i = 0; i < 101; i++) {
            Uarr[i] = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);
        }

        Assembler a = new Assembler(StripTableUtil.getStripList(), NodeTableUtil.getNodeList().size() * 4, localToGlobalConfNumbering);
        a.getK(1).printf("Global K");
        a.getF(1).printf("Global P");

        Cholesky c = new Cholesky();

        Vector U = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);

        Series Y = new Series_SS(StripTableUtil.getStripList().get(0).getStripLength());

        double[] xData = new double[101];
        double[] yData = new double[101];

        for (int i = 1; i < ModelProperties.getFourierTerms(); i++) {

            Vector temp = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);

            temp = c.getX(a.getK(i), a.getF(i));

            for (int j = 0; j < 101; j++) {
                Vector temp2 = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);
                temp2.add(temp);

                temp2.scale(Y.getFunctionValue(StripTableUtil.getStripList().get(0).getStripLength() * (j / 100.0), i));
                Uarr[j].add(temp2);

            }
            
            progress.set((i+1)/ModelProperties.getFourierTerms());
            //System.out.println(i+1/ModelProperties.getFourierTerms());

        }

        for (int i = 0; i < 101; i++) {
            xData[i] = StripTableUtil.getStripList().get(0).getStripLength() * (i / 100.0);
            yData[i] = Uarr[i].get(2);
        }

        

        XYChartDataUtil.addSeries(xData, yData, "Displacement");

        return Uarr;
    }
    
    public void setDisplacedState(int distPercentage)
    {
        
        for (int i = 0; i < NodeTableUtil.getNodeList().size(); i++) {
            NodeTableUtil.getNodeList().get(i).setDisplacedXCoord(Uarr[distPercentage].get((i * 4)));
            NodeTableUtil.getNodeList().get(i).setDisplacedZCoord(Uarr[distPercentage].get((i * 4 + 2)));
        }
        
        
    }

   
}
