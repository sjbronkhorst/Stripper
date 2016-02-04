/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author SJ
 */
public class StripperPreloader extends Preloader {
    
    ProgressBar bar;
    Stage stage;
    Button buckle = new Button("Buckling Analysis");
    Button staticAnalysis = new Button("Static Analysis");
    
    private Scene createPreloaderScene() {
        bar = new ProgressBar();
        BorderPane p = new BorderPane();
        p.setCenter(bar);
        p.setLeft(buckle);
        p.setRight(staticAnalysis);
        return new Scene(p, 300, 150);        
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(createPreloaderScene());  
        
        stage.show();
    }
    
//    @Override
//    public void handleStateChangeNotification(StateChangeNotification scn) {
//        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
//            stage.hide();
//        }
//    }
    
//    @Override
//    public void handleProgressNotification(ProgressNotification pn) {
//        bar.setProgress(pn.getProgress());
//    }    
    
}
