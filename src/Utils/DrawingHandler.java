/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 *
 * @author SJ
 */
public class DrawingHandler {
    
    private static int imgID = 0;

    public static File createBucklingCurveSnapShot(LineChart<Number, Number> chart, Pane boxImagePane, String text1, String text2) {

        WritableImage chartImg = chart.snapshot(new SnapshotParameters(), null);
        WritableImage modelImg = boxImagePane.snapshot(new SnapshotParameters(), null);

        File imageFile = new File(imgID + ".png");
        imgID ++;

        try {

            BufferedImage source = SwingFXUtils.fromFXImage(chartImg, null);

            BufferedImage logo = SwingFXUtils.fromFXImage(modelImg, null);

            Graphics g = source.getGraphics();
            g.drawImage(logo, (int) chart.getWidth() - (int) boxImagePane.getWidth() - 50, 100, null);

            Graphics2D gO = source.createGraphics();
            gO.setColor(java.awt.Color.BLACK);
            gO.setFont(new Font("SansSerif", Font.BOLD, 20));

           // gO.drawString("Local buckling factor = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor(), 2), (int) chart.getWidth() - (int) mvp.box2D.getWidth() - 75, 100);
            // gO.drawString("Physical length = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength(), 2), (int) chart.getWidth() - (int) mvp.box2D.getWidth() - 75, 125);
            gO.drawString(text1, (int) chart.getWidth() - (int) boxImagePane.getWidth() - 75, 100);
            gO.drawString(text2, (int) chart.getWidth() - (int) boxImagePane.getWidth() - 75, 125);

            try {
                ImageIO.write(source, "png", imageFile);
            } catch (Exception s) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return imageFile;
        

    }

    public static File createSnapshot(Pane boxImagePane) {
        
        File imageFile = new File(imgID + ".png");
        imgID ++;
        
        WritableImage modelImg = boxImagePane.snapshot(new SnapshotParameters(), null);
        BufferedImage source = SwingFXUtils.fromFXImage(modelImg, null);
        try {
            ImageIO.write(source, "png", imageFile);
        } catch (Exception s) {
        }
        
        return imageFile;
        

    }

}
