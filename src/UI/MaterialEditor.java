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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.FileHandler;
import fsm.material.Material;
import fsm.material.Material_User;

/**
 *
 * @author SJ
 */
public class MaterialEditor extends Application {

    
    Label nameLabel = new Label("Name : ");
    TextField nameField = new TextField(Defaults.getBaseModel().getModelMaterial().getName());

    Label exLabel = new Label("Youngs Modulus (Ex) : ");
    TextField exField = new TextField(Double.toString(Defaults.getBaseModel().getModelMaterial().getEx()));

    Label eyLabel = new Label("Youngs Modulus (Ey) : ");
    TextField eyField = new TextField(Double.toString(Defaults.getBaseModel().getModelMaterial().getEy()));

    Label vxLabel = new Label("Poisson (vx) : ");
    TextField vxField = new TextField(Double.toString(Defaults.getBaseModel().getModelMaterial().getVx()));
    
    Label vyLabel = new Label("Poisson (vy) : ");
    TextField vyField = new TextField(Double.toString(Defaults.getBaseModel().getModelMaterial().getVy()));
    
    Label gLabel = new Label("Shear Modulus (G) : ");
    TextField gField = new TextField(Double.toString(Defaults.getBaseModel().getModelMaterial().getG()));

    
    Label fyLabel = new Label("Yield Stress (G) : ");
    TextField fyField = new TextField(Double.toString(Defaults.getBaseModel().getModelMaterial().getFy()));

    
    @Override
    public void start(Stage primaryStage) {
       
        
        Button saveBtn = new Button();
        saveBtn.setText("Save As ...");
        saveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileHandler f = new FileHandler();
                
                
                Material mat = new Material_User(nameField.getText(), Double.parseDouble(exField.getText()), Double.parseDouble(eyField.getText()), Double.parseDouble(vxField.getText()), Double.parseDouble(vyField.getText()), Double.parseDouble(gField.getText()), Double.parseDouble(fyField.getText()));
                f.writeMaterial(mat);
                            
                Defaults.getBaseModel().setModelMaterial(f.getMaterial());
                
                nameField.setText(Defaults.getBaseModel().getModelMaterial().getName());
                
                exField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getEx()));
                eyField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getEy()));
                
                vxField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getVx()));
                vyField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getVy()));
                
                gField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getG()));

                fyField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getFy()));
            }
        });

        Button loadBtn = new Button("Load...");
        loadBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileHandler f = new FileHandler();
                try {

                    f.readMaterial();
                    Defaults.getBaseModel().setModelMaterial(f.getMaterial());

                } catch (IOException ex) {
                    Logger.getLogger(MaterialEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(MaterialEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                nameField.setText(Defaults.getBaseModel().getModelMaterial().getName());
                
                exField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getEx()));
                eyField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getEy()));
                
                vxField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getVx()));
                vyField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getVy()));
                
                gField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getG()));
                
                fyField.setText(Double.toString(Defaults.getBaseModel().getModelMaterial().getFy()));
            }
        });

        VBox root = new VBox();
        HBox h = new HBox(nameLabel, nameField);
        HBox h1 = new HBox(exLabel, exField);
        HBox h2 = new HBox(eyLabel, eyField);
        HBox h3 = new HBox(vxLabel,vxField);
        HBox h4 = new HBox(vyLabel,vyField);
        HBox h5 = new HBox(gLabel,gField);
        HBox h6 = new HBox(fyLabel,fyField);
        HBox h7 = new HBox(loadBtn,saveBtn);
        root.getChildren().addAll(h,h1,h2,h3,h4,h5,h6,h7);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Material Properties");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

  

}
