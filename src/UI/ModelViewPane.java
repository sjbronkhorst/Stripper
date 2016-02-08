/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import linalg.Vector;

import stripper.Node;

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

    List<Button> btnList = new ArrayList<Button>();
    List<CheckBox> checkList = new ArrayList<CheckBox>();

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
    CheckBox wireFrameCheck;
    CheckBox axisCheck;
    CheckBox nodesCheck;

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

    PhongMaterial yellowStuff = new PhongMaterial();
    PhongMaterial greenStuff = new PhongMaterial();
    PhongMaterial redStuff = new PhongMaterial();

    public ModelViewPane() {

        yellowStuff.setDiffuseColor(Color.YELLOW);
        yellowStuff.setSpecularColor(Color.GRAY);

        greenStuff.setDiffuseColor(Color.GREEN);
        greenStuff.setSpecularColor(Color.GRAY);

        redStuff.setDiffuseColor(Color.RED);
        redStuff.setSpecularColor(Color.GRAY);

        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);

        canvas = new ResizableCanvas();

        nodeLabelCheck = new CheckBox("Node labels");
        nodeLabelCheck.selectedProperty().set(false);

        stripLabelCheck = new CheckBox("Strip labels");
        stripLabelCheck.selectedProperty().set(false);

        wireFrameCheck = new CheckBox("Wireframe");
        wireFrameCheck.selectedProperty().set(false);

        axisCheck = new CheckBox("Show Global Axis");
        axisCheck.selectedProperty().set(false);

        nodesCheck = new CheckBox("Show Nodes");
        nodesCheck.selectedProperty().set(true);

        stripLabelCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (stripLabelCheck.isSelected()) {
                    wireFrameCheck.setSelected(true);
                }
                draw();
            }
        });

        nodeLabelCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (nodeLabelCheck.isSelected()) {

                    nodesCheck.setSelected(true);
                    wireFrameCheck.setSelected(true);
                }
                draw();
            }
        });

        wireFrameCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                draw();
            }
        });

        axisCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                draw();
            }
        });

        nodesCheck.setOnAction(new EventHandler<ActionEvent>() {

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

        viewBox.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

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

        btnList.add(zoomBtn);
        btnList.add(dezoomBtn);
        btnList.add(upBtn);
        btnList.add(downBtn);
        btnList.add(leftBtn);
        btnList.add(rightBtn);//zoomBtn,dezoomBtn, leftBtn,rightBtn,upBtn,downBtn,leftRotateBtn, rightRotateBtn, upRotateBtn, downRotateBtn, nodeLabelCheck, stripLabelCheck
        btnList.add(leftRotateBtn);
        btnList.add(rightRotateBtn);
        btnList.add(upRotateBtn);
        btnList.add(downRotateBtn);
        checkList.add(nodeLabelCheck);
        checkList.add(stripLabelCheck);
        checkList.add(wireFrameCheck);
        checkList.add(axisCheck);
        checkList.add(nodesCheck);

        zoomBox.getChildren().addAll(btnList);
        zoomBox.getChildren().addAll(checkList);

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

        draw();

    }

    public VBox getPane() {
        VBox pane = new VBox(10);
        pane.getStylesheets().add("Style.css");
        pane.getChildren().addAll(viewBox, zoomBox);
        viewBox.getStyleClass().addAll("vbox");

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

            gc.setStroke(Color.DARKGRAY);
            gc.strokeLine(x3, y3, x4, y4);
            gc.setStroke(Color.BLACK);

            gc.setStroke(Color.BLACK);

            gc.strokeLine(x1, y1, x2, y2);

            double theta = 180 * s.getStripAngle() / Math.PI;

            Box b = new Box(s.getStripWidth(), s.getStripThickness(), ModelProperties.getModelLength());

            if (stripLabelCheck.isSelected()) {
                Text t = new Text(s.toString());
                t.setFont(Font.font("Calibri", FontWeight.BOLD, 0.7 * s.getStripWidth()));

                t.translateXProperty().set(((x1 + x2) / 2.0) /*+ s.getStripThickness() / 2.0 + 1 + 1*/);
                t.translateYProperty().set(((y1 + y2) / 2.0) /*- s.getStripThickness() / 2.0 - 1 - 1*/);

                t.getTransforms().add(new Rotate(90, new Point3D(0, 1, 0)));

                t.getTransforms().add(new Rotate(-90 - theta, new Point3D(1, 0, 0)));

//                Text t2 = new Text(s.toString());
//                t2.setFont(Font.font("Calibri", FontWeight.BOLD, s.getStripWidth()));
//
//                t2.translateXProperty().set(((x1 + x2) / 2.0) - s.getStripThickness() / 2.0 - 1 - 1);
//                t2.translateYProperty().set(((y1 + y2) / 2.0) + s.getStripThickness() / 2.0 + 1 + 1);
//
//                t2.getTransforms().add(new Rotate(90, new Point3D(0, 1, 0)));
//
//                t2.getTransforms().add(new Rotate(90 - theta, new Point3D(1, 0, 0)));
                stripGroup.getChildren().add(t);
                // stripGroup.getChildren().add(t2);
            }

            b.setMaterial(redStuff);

            b.setRotate(theta);
            if (wireFrameCheck.isSelected()) {
                b.setDrawMode(DrawMode.LINE);
            }

            for (PointLoad p : s.getPointLoadList()) {
                gc.strokeOval(s.getNode1().getXCoord() + p.getX() * Math.cos(s.getStripAngle()) - 5, s.getNode1().getZCoord() + p.getX() * Math.sin(s.getStripAngle()) - 5, 10, 10);

                addZArrowAt(x1, y1, theta, p);
            }

            b.setTranslateX((x1 + x2) / 2.0);
            b.setTranslateY((y1 + y2) / 2.0);
            
            stripGroup.getChildren().add(b);

            if (s.getUdlZ() != 0) {

                for (int i = 0; i <= (int) (s.getStripWidth()); i = i + 10) {
                    for (int j = 0; j <= (int) (ModelProperties.getModelLength()); j = j + 10) {

                        PointLoad pl = new PointLoad();
                        pl.setXCoord((double) (i));
                        pl.setYCoord((double) (j));
                        pl.setMagnitude(s.getUdlZ());

                        addZArrowAt(x1, y1, theta, pl);

                    }

                }
            }

            if (s.getUdlX() != 0) {

                for (int i = 0; i <= (int) (s.getStripWidth()); i = i + 10) {
                    for (int j = 0; j <= (int) (ModelProperties.getModelLength()); j = j + 10) {

                        PointLoad pl = new PointLoad();
                        pl.setXCoord((double) (i));
                        pl.setYCoord((double) (j));
                        pl.setMagnitude(10 * s.getUdlX() / Math.abs(s.getUdlX()));

                        addXArrowAt(x1, y1, theta, pl);

                    }

                }
            }

            if (s.getUdlY() != 0) {

                for (int i = 0; i <= (int) (s.getStripWidth()); i = i + 10) {
                    for (int j = 0; j <= (int) (ModelProperties.getModelLength()); j = j + 10) {

                        PointLoad pl = new PointLoad();
                        pl.setXCoord((double) (i));
                        pl.setYCoord((double) (j));
                        pl.setMagnitude(10 * s.getUdlY() / Math.abs(s.getUdlY()));

                        addYArrowAt(x1, y1, theta, pl);

                    }

                }
            }

            if (s.getF1() != 0 || s.getF2() != 0) {

                  for (int i = 0; i <= (int) (s.getStripWidth()); i = i + 10) {
                    

                        PointLoad pl = new PointLoad();
                        pl.setXCoord((double) (i));
                        pl.setYCoord(0.0);
                        pl.setMagnitude(((s.getF2()-s.getF1())/s.getStripWidth())*i + s.getF1());

                        
                        if(pl.getMagnitude() != 0.0)
                        {
                        addYArrowAt(x1, y1, theta, pl);
                        }

                    

                }

            }

        }

        ////////////////////////////////////////////
        //DRAW NODES
        nodeGroup.getChildren().clear();

        if (nodesCheck.isSelected()) {

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
                    Text t = new Text(Integer.toString(n.getNodeId()));
                    t.setFont(Font.font("Calibri", FontWeight.BOLD, 30));

                    t.translateXProperty().set(n.getXCoord() + 10);
                    t.translateYProperty().set(n.getZCoord() + 10);

                    nodeGroup.getChildren().add(t);
                }

                s.setMaterial(blueStuff);

                nodeGroup.getChildren().add(s);

            }
        }

        axisGroup.getChildren().clear();

        if (axisCheck.isSelected()) {

            //////////////////////////////
            //CREATE 3D AXIS
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

            Box arrowHeadX = new Box(10, 10, 10);
            arrowHeadX.setMaterial(yellowStuff);
            Box arrowHeadY = new Box(10, 10, 10);
            arrowHeadY.setMaterial(yellowStuff);
            Box arrowHeadZ = new Box(10, 10, 10);
            arrowHeadZ.setMaterial(yellowStuff);

            arrowHeadX.setTranslateX(100);
            arrowHeadY.setTranslateY(100);
            arrowHeadZ.setTranslateZ(100);

            axisGroup.getChildren().addAll(arrowHeadX, arrowHeadY, arrowHeadZ);
        }

    }

    public void addZArrowAt(double x1, double y1, double theta, PointLoad p) {
        Cylinder pointLoad = createConnection(new Point3D(p.getX(), 0, p.getY()), new Point3D(p.getX(), p.getMagnitude(), p.getY()), 0.5);
        //Sphere arrowHead = createArrowHead(new Point3D(p.getX(), 0, p.getY()), new Point3D(p.getX(), p.getMagnitude(), p.getY()));

        int sign = 1;
        if (p.getMagnitude() < 0) {
            sign = -1;
        }

        for (int i = 1; i < 10; i++) {
            Cylinder arrowHead = createConnection(new Point3D(p.getX(), p.getMagnitude() - sign * i, p.getY()), new Point3D(p.getX(), p.getMagnitude() - sign * i - sign * 1, p.getY()), i / 2);
            arrowHead.setTranslateX(x1);
            arrowHead.setTranslateY(y1);
            arrowHead.setTranslateZ(-ModelProperties.getModelLength() / 2.0);
            arrowHead.setRotate(theta);

            arrowHead.setMaterial(greenStuff);
            stripGroup.getChildren().add(arrowHead);
        }

        //pointLoad.setDrawMode(DrawMode.LINE);
        pointLoad.setTranslateX(x1);
        pointLoad.setTranslateY(y1);
        pointLoad.setTranslateZ(-ModelProperties.getModelLength() / 2.0);
        pointLoad.setRotate(theta);

        pointLoad.setMaterial(greenStuff);

        stripGroup.getChildren().add(pointLoad);
    }

    public void addXArrowAt(double x1, double y1, double theta, PointLoad p) {
        Cylinder pointLoad = createConnection(new Point3D(p.getX(), 0, p.getY()), new Point3D(p.getX() + p.getMagnitude(), 0, p.getY()), 0.5);
        //Sphere arrowHead = createArrowHead(new Point3D(p.getX(), 0, p.getY()), new Point3D(p.getX(), p.getMagnitude(), p.getY()));

        int sign = 1;
        if (p.getMagnitude() < 0) {
            sign = -1;
        }

        for (int i = 1; i < 10; i++) {
            Cylinder arrowHead = createConnection(new Point3D(p.getX() + p.getMagnitude() - sign * i, 0, p.getY()), new Point3D(p.getX() + p.getMagnitude() - sign * i - sign * 1, 0, p.getY()), i / 2);
            arrowHead.setTranslateX(x1);
            arrowHead.setTranslateY(y1);
            arrowHead.setTranslateZ(-ModelProperties.getModelLength() / 2.0);
            arrowHead.setRotate(theta);

            arrowHead.setMaterial(greenStuff);
            stripGroup.getChildren().add(arrowHead);
        }

        //pointLoad.setDrawMode(DrawMode.LINE);
        pointLoad.setTranslateX(x1);
        pointLoad.setTranslateY(y1);
        pointLoad.setTranslateZ(-ModelProperties.getModelLength() / 2.0);
        pointLoad.setRotate(theta);

        pointLoad.setMaterial(greenStuff);

        stripGroup.getChildren().add(pointLoad);
    }

    public void addYArrowAt(double x1, double y1, double theta, PointLoad p) {
        Cylinder pointLoad = createConnection(new Point3D(p.getX(), 0, p.getY()), new Point3D(p.getX(), 0, p.getY() + p.getMagnitude()), 0.5);
        //Sphere arrowHead = createArrowHead(new Point3D(p.getX(), 0, p.getY()), new Point3D(p.getX(), p.getMagnitude(), p.getY()));

        int sign = 1;
        if (p.getMagnitude() < 0) {
            sign = -1;
        }

        for (int i = 1; i < 10; i++) {
            Cylinder arrowHead = createConnection(new Point3D(p.getX(), 0, p.getY() + p.getMagnitude() - sign * i), new Point3D(p.getX(), 0, p.getY() + p.getMagnitude() - sign * i - sign * 1), i / 2);
            arrowHead.setTranslateX(x1);
            arrowHead.setTranslateY(y1);
            arrowHead.setTranslateZ(-ModelProperties.getModelLength() / 2.0);
            arrowHead.setRotate(theta);

            arrowHead.setMaterial(greenStuff);
            stripGroup.getChildren().add(arrowHead);
        }

        //pointLoad.setDrawMode(DrawMode.LINE);
        pointLoad.setTranslateX(x1);
        pointLoad.setTranslateY(y1);
        pointLoad.setTranslateZ(-ModelProperties.getModelLength() / 2.0);
        pointLoad.setRotate(theta);

        pointLoad.setMaterial(greenStuff);

        stripGroup.getChildren().add(pointLoad);
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

        return (line);
    }

    public Cylinder createConnection(Point3D origin, Point3D target, double thickness) {
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
        Cylinder line = new Cylinder(thickness, height);
        line.setMaterial(blackStuff);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return (line);
    }

    public Sphere createArrowHead(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(target.getX(), target.getY(), target.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        PhongMaterial blackStuff = new PhongMaterial();
        blackStuff.setDiffuseColor(Color.BLACK);
        blackStuff.setSpecularColor(Color.BLACK);
        Sphere line = new Sphere(2);
        line.setMaterial(blackStuff);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return (line);
    }

}
