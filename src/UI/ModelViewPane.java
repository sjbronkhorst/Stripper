/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Translate;

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
    Button leftBtn = new Button("<");
    Button rightBtn = new Button(">");
    Button downBtn = new Button("v");
    Button upBtn = new Button("^");

    CheckBox nodeLabelCheck;
    CheckBox stripLabelCheck;

    private double xScale = 1;
    private double yScale = 1;

    VBox viewBox = new VBox(10);

    HBox zoomBox = new HBox(10);

    Group nodeGroup = new Group();
    Group stripGroup = new Group();

    VBox threeDBox = new VBox(10);

    SubScene scene3d;

    PerspectiveCamera camera = new PerspectiveCamera(true);

    public ModelViewPane() {

        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-1000);

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

                camera.setTranslateZ(camera.getTranslateZ() + 100);
                camera.setNearClip(0.1);
                camera.setFarClip(camera.getTranslateZ() * 2);
                camera.setFieldOfView(35);
                scene3d.setCamera(camera);

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

                camera.setTranslateZ(camera.getTranslateZ() - 100);
                camera.setNearClip(0.1);
                camera.setFarClip(camera.getTranslateZ() * 2);
                camera.setFieldOfView(35);
                scene3d.setCamera(camera);

                GraphicsContext gc = canvas.getGraphicsContext2D();
                xScale = xScale / 1.1;
                yScale = yScale / 1.1;
                gc.scale(1.0 / 1.1, 1.0 / 1.1);
                draw();
            }
        });

        leftBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                camera.setTranslateX(camera.getTranslateX() + 50);

                camera.setFieldOfView(35);
                scene3d.setCamera(camera);

                draw();
            }
        });

        rightBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                camera.setTranslateX(camera.getTranslateX() - 50);

                camera.setFieldOfView(35);
                scene3d.setCamera(camera);

                draw();
            }
        });

        upBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                camera.setTranslateY(camera.getTranslateY() + 50);

                camera.setFieldOfView(35);
                scene3d.setCamera(camera);

                draw();
            }
        });

        downBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                camera.setTranslateY(camera.getTranslateY() - 50);

                camera.setFieldOfView(35);
                scene3d.setCamera(camera);

                draw();
            }
        });

        zoomBox.getChildren().addAll(zoomBtn, dezoomBtn, leftBtn, rightBtn, upBtn, downBtn, nodeLabelCheck, stripLabelCheck);

        Group threeDGroup = new Group(nodeGroup, stripGroup);

        scene3d = new SubScene(threeDGroup, 100, 100);
        scene3d.setCamera(camera);

        viewBox.addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) -> {

            double notches = e.getDeltaY();

            if (notches > 0) {
                camera.setTranslateZ(camera.getTranslateZ() + 50);

            } else {
                camera.setTranslateZ(camera.getTranslateZ() - 50);

            }

            double dx = e.getX() - camera.getTranslateX();
            double dy = e.getY() - camera.getTranslateY();
            
            double dist = Math.sqrt(dx*dx + dy*dy);
            
            
            camera.setTranslateX(camera.getTranslateX() + dx/5);
            camera.setTranslateY(camera.getTranslateX() + dy/5);
            

            camera.setNearClip(0.1);
            camera.setFarClip(camera.getTranslateZ() * 2);
            camera.setFieldOfView(35);
            scene3d.setCamera(camera);
        });

        viewBox.getChildren().addAll(/*canvas*/scene3d);

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

        scene3d.widthProperty().bind(viewBox.widthProperty());
        scene3d.heightProperty().bind(viewBox.heightProperty());

        scene3d.widthProperty().addListener(evt -> draw());
        scene3d.heightProperty().addListener(evt -> draw());

        draw();

    }

    public VBox getPane() {
        VBox pane = new VBox(10);
        pane.getChildren().addAll(viewBox, zoomBox);
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

        nodeGroup.getChildren().clear();
        for (Node n : NodeTableUtil.getNodeList()) {

//            gc.setFill(Color.DARKGRAY);
//
//            gc.fillOval(n.getXCoord() + n.getDisplacedXCoord() - (4.0 / xScale), n.getZCoord() + n.getDisplacedZCoord() - (4.0 / yScale), 8.0 / xScale, 8.0 / yScale);
//            gc.setLineWidth(1 / xScale);
//            gc.setFill(Color.BLACK);
//
//            gc.fillOval(n.getXCoord() - (4.0 / xScale), n.getZCoord() - (4.0 / yScale), 8.0 / xScale, 8.0 / yScale);
//            gc.setLineWidth(1 / xScale);
//
//            if (nodeLabelCheck.isSelected()) {
//                gc.setFont(Font.font("Calibri", FontWeight.BOLD, 30 / xScale));
//
//                gc.setStroke(Color.CRIMSON);
//                gc.strokeText(Integer.toString(n.getNodeId()), n.getXCoord() - 15 / xScale, n.getZCoord() + 15 / xScale);
//                gc.setStroke(Color.BLACK);
//            }
            Sphere s = new Sphere(10);
            s.translateXProperty().set(n.getXCoord());
            s.translateYProperty().set(n.getZCoord());

            nodeGroup.getChildren().add(s);

        }

        double x3 = 0;
        double y3 = 0;
        double x4 = 0;
        double y4 = 0;

        stripGroup.getChildren().clear();

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

            for (PointLoad p : s.getPointLoadList()) {
                gc.strokeOval(s.getNode1().getXCoord() + p.getX() * Math.cos(s.getStripAngle()) - 5, s.getNode1().getZCoord() + p.getX() * Math.sin(s.getStripAngle()) - 5, 10, 10);
            }

            gc.setStroke(Color.DARKGRAY);
            gc.strokeLine(x3, y3, x4, y4);
            gc.setStroke(Color.BLACK);

            gc.setStroke(Color.BLACK);

            gc.strokeLine(x1, y1, x2, y2);

            double theta = 180 * s.getStripAngle() / Math.PI;

            Box b = new Box(s.getStripWidth(), 10, 1);

            b.setRotate(theta);

            b.setTranslateX((x1 + x2) / 2.0);
            b.setTranslateY((y1 + y2) / 2.0);

            stripGroup.getChildren().add(b);

        }

    }

}
