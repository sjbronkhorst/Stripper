/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.





 */
package UI;

import static UI.TableViewEdit.println;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import stripper.DSM.DSMCalcs;
import stripper.FileHandler;
import stripper.MyMath;
import stripper.Node;
import stripper.Strip;

/**
 *
 * @author SJ
 */
public class HomeMenuBar {

    private Menu fileMenu = new Menu("File");
    private Menu editMenu = new Menu("Edit");
    private Menu dsmMenu = new Menu("DSM");
    private Menu modelMenu = new Menu("Model");
    private MenuBar menuBar = new MenuBar();

    private MenuItem geomFileRead = new MenuItem("Open Geometry File...");
    private MenuItem geomFileWrite = new MenuItem("Save Geometry As...");
    private MenuItem materialEdit = new MenuItem("Material");
    private MenuItem setBC = new MenuItem("Fourier series");
    
    private MenuItem transLateToPrincipal = new MenuItem("Translate centroid to match origin");
    private MenuItem rotateToPrincipal = new MenuItem("Rotate model to match principal axis angle");
    private MenuItem modelPropertiesBtn = new MenuItem("Properties");
    
     
    
    private MenuItem makePath = new MenuItem("Path");
    private MenuItem dsmCalcs = new MenuItem("Calc...");

    public HomeMenuBar(TableViewEdit viewer, boolean hasPath) {

        fileMenu.getItems().addAll(geomFileRead, geomFileWrite);
        editMenu.getItems().addAll(materialEdit, setBC , modelMenu);
        dsmMenu.getItems().add(dsmCalcs);
        modelMenu.getItems().addAll(modelPropertiesBtn,transLateToPrincipal,rotateToPrincipal);
        
        

        if (hasPath) {
            editMenu.getItems().add(makePath);
        }

        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(editMenu);
        menuBar.getMenus().add(dsmMenu);

        geomFileWrite.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                FileHandler f = new FileHandler();

                try {
                    //f.writeGeom(NodeTableUtil.getNodeList(), UIStripTableUtil.getStripList());
                    f.writeGeom(Defaults.getBaseModel().getNodeList(), Defaults.getBaseModel().getStripList());

                } catch (IOException ex) {
                    Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        geomFileRead.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                FileHandler f = new FileHandler();

                try {

                    f.readGeom();
                } catch (IOException ex) {
                    Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                }

                Defaults.getBaseModel().clearNodes();

                Defaults.getBaseModel().clearStrips();

                Node.clearNumbering();
                for (Node n : f.getNodeList()) {
                    Defaults.getBaseModel().addNode(n);

                }

                Strip.clearNumbering();
                for (Strip s : f.getStripList()) {

                    Defaults.getBaseModel().addStrip(s);
                }

                viewer.draw();

            }
        });

        materialEdit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                MaterialEditor matEdit = new MaterialEditor();
                matEdit.start(new Stage());
            }
        });

        setBC.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FourierEditor fEdit = new FourierEditor();
                fEdit.start(new Stage());

            }
        });

        makePath.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                PathMaker pathM = new PathMaker();
                try {
                    pathM.start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(HomeMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        dsmCalcs.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

//                DSMCalcs d = new DSMCalcs(355, 355*DSMCalcs.getLocalFactor(), 355*DSMCalcs.getDistortionalFactor(), 355*DSMCalcs.getGlobalFactor());
//                double Mne = d.getNominalFlexuralStrength(false);
                
                
                DSMWindow d = new DSMWindow();
                try {
                    d.start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(HomeMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        transLateToPrincipal.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Defaults.getBaseModel().matchCentroidToOrigin();
                viewer.draw();
            }
        });
        
         rotateToPrincipal.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Defaults.getBaseModel().matchPrincipalAxisRotation();
                 viewer.draw();
            }
        });
         
          modelPropertiesBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                
                println("---------------------------------------------------------------------------------------------- ");
                println("Cross section properties : ");
                println(" ");
                println("Centroid x = " + MyMath.round(Defaults.getBaseModel().getCrossSectionalCentroid().getX(),2));
                println("Centroid z = " + MyMath.round(Defaults.getBaseModel().getCrossSectionalCentroid().getY(),2));
                
                println("Ixx = " + MyMath.round(Defaults.getBaseModel().getIxx(),2));
                println("Izz = " + MyMath.round(Defaults.getBaseModel().getIzz(),2));
                println("Ixz = " + MyMath.round(Defaults.getBaseModel().getIxz(),2));
                
                println("principal axis angle = " + MyMath.round(Defaults.getBaseModel().getPrincipalAxisAngle(),2) + "  radians");
                println("Ixx Principal = " + MyMath.round(Defaults.getBaseModel().getIxxPrincipal(),2));
                println("Izz Principal = " + MyMath.round(Defaults.getBaseModel().getIzzPrincipal(),2));
                println("---------------------------------------------------------------------------------------------- ");
                
            }
        });

    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

}
