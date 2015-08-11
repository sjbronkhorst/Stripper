/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author SJ
 */
public class MaterialEditor extends Application {
    
    Label poissonLabel = new Label("Poisson's ratio (v) : " + ModelProperties.getModelMaterial().getVx());
    Label youngsModulusLabel = new Label("Youngs Modulus (E) : " + ModelProperties.getModelMaterial().getEx());
    Label shearModulusLabel = new Label("Shear Modulus (G) : " + ModelProperties.getModelMaterial().getG());
    
    
    
    
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        VBox root = new VBox();
        root.getChildren().addAll(btn , youngsModulusLabel , shearModulusLabel , poissonLabel);
//        root.getChildren().add(0, btn);
//        root.getChildren().add(1, youngsModulusLabel);
//        root.getChildren().add(2, shearModulusLabel);
//        root.getChildren().add(3, poissonLabel);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Material Properties");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
