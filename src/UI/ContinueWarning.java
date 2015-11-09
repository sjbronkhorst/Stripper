/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author SJ
 */
public class ContinueWarning extends Application {

    Label warningLbl = new Label();
   

    

    public ContinueWarning(String warning) {
        warningLbl.setText(warning);

    }

   

    @Override
    public void start(Stage stage) throws Exception {

        VBox root = new VBox(5);
        
        warningLbl.setWrapText(true);
        root.setMinSize(200, 200);
                
        root.getChildren().addAll(warningLbl);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("WARNING");
        stage.show();
        
        
    }

}
