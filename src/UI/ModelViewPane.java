/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGPhongMaterial;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import linalg.Matrix;
import linalg.Vector;

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

    Button leftRotateBtn = new Button("<R");
    Button rightRotateBtn = new Button("R>");
    Button downRotateBtn = new Button("Rv");
    Button upRotateBtn = new Button("R^");

    double xRotation = 0;
    double yRotation = 0;
    double zRotation = 0;

    double pressedX;
    double pressedY;
    Point3D pressedPoint;

    Point3D origin = new Point3D(0, 0, 0);
    Point3D xAxis = new Point3D(1, 0, 0);
    Point3D yAxis = new Point3D(0, 1, 0);
    Point3D zAxis = new Point3D(0, 0, 1);

    CheckBox nodeLabelCheck;
    CheckBox stripLabelCheck;

    private double xScale = 1;
    private double yScale = 1;

    VBox viewBox = new VBox(10);

    HBox zoomBox = new HBox(10);

    Group nodeGroup = new Group();
    Group stripGroup = new Group();
    Group axisGroup = new Group();

    VBox threeDBox = new VBox(10);

    SubScene scene3d;
    Group threeDGroup;

    PerspectiveCamera camera = new PerspectiveCamera(true);

    public ModelViewPane() {

        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);

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
                camera.setFarClip(Math.abs(camera.getTranslateZ() * 4));
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
                camera.setFarClip(Math.abs(camera.getTranslateZ() * 4));
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

        leftRotateBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                threeDGroup.getTransforms().add(new Rotate(5, yAxis));
                yRotation += 5;

                draw();
            }
        });

        rightRotateBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                threeDGroup.getTransforms().add(new Rotate(-5, yAxis));
                yRotation += -5;

                draw();
            }
        });

        upRotateBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                threeDGroup.getTransforms().add(new Rotate(5, xAxis));
                xRotation += 5;

            }
        });

        downRotateBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                threeDGroup.getTransforms().add(new Rotate(-5, xAxis));

                xRotation += -5;

                draw();
            }
        });

        viewBox.setOnMouseEntered(new EventHandler() {

            @Override
            public void handle(Event event) {
                System.out.println("Mouse entered");
            }
        });

        viewBox.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("Mouse pressed at x = " + event.getSceneX() + " y = " + event.getSceneY());
                pressedX = event.getSceneX();
                pressedY = event.getSceneY();
                pressedPoint = new Point3D(pressedX, pressedY, camera.getTranslateZ());
            }
        });

        viewBox.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Point3D newPoint = new Point3D(event.getSceneX(), event.getSceneY(), camera.getTranslateZ());

                double dx = pressedPoint.getX() / newPoint.getX();
                double dy = pressedPoint.getY() / newPoint.getY();

                Point3D pivot = pressedPoint.crossProduct(newPoint);
                threeDGroup.getTransforms().add(new Rotate(0.8 * dy / dx, pivot));
                pressedPoint = newPoint;
            }
        });

        viewBox.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("Mouse released at x = " + event.getSceneX() + " y = " + event.getSceneY());
            }
        });

        zoomBox.getChildren().addAll(zoomBtn, dezoomBtn, leftBtn, rightBtn, upBtn, downBtn, leftRotateBtn, rightRotateBtn, upRotateBtn, downRotateBtn, nodeLabelCheck, stripLabelCheck);

        threeDGroup = new Group(stripGroup, nodeGroup, axisGroup);

        scene3d = new SubScene(threeDGroup, 100, 100, true, SceneAntialiasing.BALANCED);
        scene3d.setCamera(camera);

        viewBox.addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) -> {

            double notches = e.getDeltaY();

            if (notches > 0) {
                camera.setTranslateZ(camera.getTranslateZ() + 50);

            } else {
                camera.setTranslateZ(camera.getTranslateZ() - 50);

            }

//            double dx = e.getX() - camera.getTranslateX();
//            double dy = e.getY() - camera.getTranslateY();
//
//            double dist = Math.sqrt(dx * dx + dy * dy);
//
//            camera.setTranslateX(camera.getTranslateX() + dx / 5);
//            camera.setTranslateY(camera.getTranslateX() + dy / 5);

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

        viewBox.setPrefWidth(1500);
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

        axisGroup.getChildren().add(createConnection(origin, new Point3D(100, 0, 0)));
        axisGroup.getChildren().add(createConnection(origin, new Point3D(0, 100, 0)));
        axisGroup.getChildren().add(createConnection(origin, new Point3D(0, 0, 100)));
        Text xText = new Text("X");
        xText.setFont(Font.font("Calibri", FontWeight.BOLD, 30));
        xText.translateXProperty().set(110);
        axisGroup.getChildren().add(xText);
        
        Text yText = new Text("Z");
        yText.setFont(Font.font("Calibri", FontWeight.BOLD, 30));
        yText.translateYProperty().set(110);
        axisGroup.getChildren().add(yText);
        
        Text zText = new Text("Y");
        zText.setFont(Font.font("Calibri", FontWeight.BOLD, 30));
        zText.translateZProperty().set(110);
        axisGroup.getChildren().add(zText);

        
        PhongMaterial yellowStuff = new PhongMaterial();
                yellowStuff.setDiffuseColor(Color.YELLOW);
                yellowStuff.setSpecularColor(Color.GRAY);
                
         Box arrowHeadX = new Box(10, 10,10);       
         arrowHeadX.setMaterial(yellowStuff);
         Box arrowHeadY = new Box(10, 10,10);    
         arrowHeadY.setMaterial(yellowStuff);
         Box arrowHeadZ = new Box(10, 10,10);       
         arrowHeadZ.setMaterial(yellowStuff);
         
         arrowHeadX.setTranslateX(100);
         arrowHeadY.setTranslateY(100);
         arrowHeadZ.setTranslateZ(100);
         
        axisGroup.getChildren().addAll(arrowHeadX,arrowHeadY,arrowHeadZ);
        
        
        MeshView ding = new MeshView(null);
        
        
        
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

                Box b = new Box(2, 20, 2);
                PhongMaterial greenStuff = new PhongMaterial();
                greenStuff.setDiffuseColor(Color.GREEN);
                greenStuff.setSpecularColor(Color.GRAY);
                b.setMaterial(greenStuff);

            }

            gc.setStroke(Color.DARKGRAY);
            gc.strokeLine(x3, y3, x4, y4);
            gc.setStroke(Color.BLACK);

            gc.setStroke(Color.BLACK);

            gc.strokeLine(x1, y1, x2, y2);

            double theta = 180 * s.getStripAngle() / Math.PI;

            Box b = new Box(s.getStripWidth(), 10, ModelProperties.getModelLength());
            PhongMaterial redStuff = new PhongMaterial();
            redStuff.setDiffuseColor(Color.RED);
            redStuff.setSpecularColor(Color.GRAY);

            if (stripLabelCheck.isSelected()) {
                Text t = new Text(s.toString());
                t.setFont(Font.font("Calibri", FontWeight.BOLD, 30));

                t.translateXProperty().set(((x1 + x2) / 2.0) - 5 - 1);
                t.translateYProperty().set(((y1 + y2) / 2.0) - 5 - 1);

                t.getTransforms().add(new Rotate(90, new Point3D(0, 1, 0)));

                t.getTransforms().add(new Rotate(-90 + theta, new Point3D(1, 0, 0)));

                //t.getTransforms().add(new Rotate(90,new Point3D(0, 0, 1)));
                stripGroup.getChildren().add(t);
            }

            b.setMaterial(redStuff);

            b.setRotate(theta);

            b.setTranslateX((x1 + x2) / 2.0);
            b.setTranslateY((y1 + y2) / 2.0);

            stripGroup.getChildren().add(b);

        }

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
            PhongMaterial blueStuff = new PhongMaterial();
            blueStuff.setDiffuseColor(Color.BLUE);
            blueStuff.setSpecularColor(Color.BLACK);

            Sphere s = new Sphere(10);
            s.translateXProperty().set(n.getXCoord());
            s.translateYProperty().set(n.getZCoord());

            if (nodeLabelCheck.isSelected()) {
                Text t = new Text(n.toString());
                t.setFont(Font.font("Calibri", FontWeight.BOLD, 30));

                t.translateXProperty().set(n.getXCoord() + 10);
                t.translateYProperty().set(n.getZCoord() + 10);

                nodeGroup.getChildren().add(t);
            }

            s.setMaterial(blueStuff);

            nodeGroup.getChildren().add(s);

        }

    }

    public Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        PhongMaterial blackStuff = new PhongMaterial();
        blackStuff.setDiffuseColor(Color.BLACK);
        blackStuff.setSpecularColor(Color.BLACK);
        Cylinder line = new Cylinder(1, height);
        line.setMaterial(blackStuff);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

}
