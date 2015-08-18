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
public class FourierEditor extends Application {
    
    Label fourierLabel = new Label("Number of Fourier terms : " + ModelProperties.getFourierTerms());
       
    
    
    
    @Override
    public void start(Stage primaryStage) {
       
       
        
        VBox root = new VBox();
        root.getChildren().addAll(fourierLabel);

        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Model Properties");
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
