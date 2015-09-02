/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import stripper.Node;
import stripper.Strip;

/**
 *
 * @author SJ
 */
public class ModelViewPane {

    ResizableCanvas canvas;
    Button zoomBtn = new Button("+");
    Button dezoomBtn = new Button("- ");
    
    CheckBox nodeLabelCheck;
    CheckBox stripLabelCheck;
    
    private double xScale = 1;
    private double yScale = 1;
    
    VBox viewBox = new VBox(10);
    
    HBox zoomBox = new HBox(10);

        
    
    

    public ModelViewPane() {
        
        canvas = new ResizableCanvas();
        
        
        
        nodeLabelCheck = new CheckBox("Node labels");
        nodeLabelCheck.selectedProperty().set(true);

        stripLabelCheck = new CheckBox("Strip labels");
        stripLabelCheck.selectedProperty().set(true);
        
        stripLabelCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                draw();
            }
        });

        nodeLabelCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                draw();
            }
        });
        
         zoomBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                xScale = xScale * 1.1;
                yScale = yScale * 1.1;
                gc.scale(1.1, 1.1);
                draw();
            }
        });

        dezoomBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                xScale = xScale / 1.1;
                yScale = yScale / 1.1;
                gc.scale(1.0 / 1.1, 1.0 / 1.1);
                draw();
            }
        });
        
        zoomBox.getChildren().addAll(zoomBtn, dezoomBtn, nodeLabelCheck , stripLabelCheck);

        viewBox.getChildren().addAll(canvas);
        
        zoomBox.setMinHeight(40);
        zoomBox.setMinWidth(40);

        viewBox.setMinHeight(0);
        viewBox.setMinWidth(0);

        viewBox.setPrefWidth(1200);
        viewBox.setPrefHeight(2000);
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        viewBox.setEffect(dropShadow);

        canvas.widthProperty().bind(viewBox.widthProperty());
        canvas.heightProperty().bind(viewBox.heightProperty());

        canvas.widthProperty().addListener(evt -> draw());
        canvas.heightProperty().addListener(evt -> draw());
        
        draw();

    }
    
    public VBox getPane()
    {
     VBox pane = new VBox(10);
     pane.getChildren().addAll(viewBox , zoomBox);
     return pane;
    }
    
     public void draw() {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        

        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth() / xScale, canvas.getHeight() / yScale);
        gc.setFill(Color.BLACK);

        gc.setStroke(Color.GRAY);

        gc.strokeRect(0, 0, canvas.getWidth() / xScale, canvas.getHeight() / yScale);
        gc.setStroke(Color.BLACK);

        for (Node n : NodeTableUtil.getNodeList()) {

            gc.setFill(Color.DARKGRAY);

            gc.fillOval(n.getXCoord() + n.getDisplacedXCoord() - (4.0 / xScale), n.getZCoord() + n.getDisplacedZCoord() - (4.0 / yScale), 8.0 / xScale, 8.0 / yScale);
            gc.setLineWidth(1 / xScale);
            gc.setFill(Color.BLACK);

            gc.fillOval(n.getXCoord() - (4.0 / xScale), n.getZCoord() - (4.0 / yScale), 8.0 / xScale, 8.0 / yScale);
            gc.setLineWidth(1 / xScale);

            if (nodeLabelCheck.isSelected()) {
                gc.setFont(Font.font("Calibri", FontWeight.BOLD, 30 / xScale));

                gc.setStroke(Color.CRIMSON);
                gc.strokeText(Integer.toString(n.getNodeId()), n.getXCoord() - 15 / xScale, n.getZCoord() + 15 / xScale);
                gc.setStroke(Color.BLACK);
            }

            

        }

        double x3 = 0;
        double y3 = 0;
        double x4 = 0;
        double y4 = 0;

        for (UIStrip s : StripTableUtil.getStripList()) {

            if (s.hasBothNodes()) {

                x1 = s.getNode1().getXCoord();
                y1 = s.getNode1().getZCoord();

                x2 = s.getNode2().getXCoord();
                y2 = s.getNode2().getZCoord();

                x3 = s.getNode1().getDisplacedXCoord() + x1;
                y3 = s.getNode1().getDisplacedZCoord() + y1;

                x4 = s.getNode2().getDisplacedXCoord() + x2;
                y4 = s.getNode2().getDisplacedZCoord() + y2;
                
                

            }

            if (stripLabelCheck.isSelected()) {
                gc.setFont(Font.font("Calibri", FontWeight.BOLD, 30 / xScale));

                gc.setFill(Color.BLUE);
                gc.fillText(Integer.toString(s.getStripId()), (x1 + x2) / 2.0, (y1 + y2) / 2.0);
                gc.setFill(Color.BLACK);
            }

            if (s.getUdlZ() != 0) {
                gc.setStroke(Color.LIME);

                gc.strokeLine(x1, y1, (x1 + s.getUdlZ() * ((y2 - y1) / s.getStripWidth())), (y1 + s.getUdlZ() * ((x1 - x2) / s.getStripWidth())));

                gc.strokeLine(x2, y2, (x2 + s.getUdlZ() * ((y2 - y1) / s.getStripWidth())), (y2 + s.getUdlZ() * ((x1 - x2) / s.getStripWidth())));

                gc.strokeLine((x1 + s.getUdlZ() * ((y2 - y1) / s.getStripWidth())), (y1 + s.getUdlZ() * ((x1 - x2) / s.getStripWidth())), (x2 + s.getUdlZ() * ((y2 - y1) / s.getStripWidth())), (y2 + s.getUdlZ() * ((x1 - x2) / s.getStripWidth())));

            }
            
            for(PointLoad p : s.getPointLoadList())
            {
                gc.strokeOval(s.getNode1().getXCoord() + p.getX()*Math.cos(s.getStripAngle()) -5, s.getNode1().getZCoord() + p.getX()*Math.sin(s.getStripAngle()) -5, 10, 10);
                 }

            gc.setStroke(Color.DARKGRAY);
            gc.strokeLine(x3, y3, x4, y4);
            gc.setStroke(Color.BLACK);

            gc.setStroke(Color.BLACK);
            
            gc.strokeLine(x1, y1, x2, y2);
            
            

        }

    }

}
