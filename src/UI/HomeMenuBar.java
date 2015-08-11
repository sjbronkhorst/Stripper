/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import stripper.FileHandler;
import stripper.Node;

/**
 *
 * @author SJ
 */
public class HomeMenuBar {

    private Menu fileMenu = new Menu("File");
    private Menu editMenu = new Menu("Edit");
    private MenuBar menuBar = new MenuBar();
    
    
    
    private MenuItem fileRead = new MenuItem("Open File...");
    private MenuItem fileWrite = new MenuItem("Save As...");
    private MenuItem materialEdit = new MenuItem("Material");
    private MenuItem setFourierTerms = new MenuItem("Fourier terms");

    public HomeMenuBar(TableViewEdit viewer) {

        fileMenu.getItems().addAll(fileRead, fileWrite);
        editMenu.getItems().addAll(materialEdit , setFourierTerms);
        
        
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(editMenu);
        
        fileWrite.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                FileHandler f = new FileHandler();

                try {
                    f.writeFile(NodeTableUtil.getNodeList(), StripTableUtil.getStripList());
                } catch (IOException ex) {
                    Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        fileRead.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                FileHandler f = new FileHandler();

                try {

                    f.ReadFile();
                } catch (IOException ex) {
                    Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                }

                NodeTableUtil.clearNodes();
                StripTableUtil.clearStrips();

                for (Node n : f.getNodeList()) {
                    NodeTableUtil.addNode(n);

                }

                for (Strip s : f.getStripList()) {
                    StripTableUtil.addStrip(s);
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
        
        setFourierTerms.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FourierEditor fEdit = new FourierEditor();
                fEdit.start(new Stage());
               
            }
        });

    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

}
