/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.




 */
package UI;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public class FourierEditor extends Application {
    
    Label seriesLabel = new Label("Selected Fourier series : ");
    Label termsLabel = new Label("Number of longitudinal terms : " + ModelProperties.getFourierTerms());
    private ChoiceBox<Series> seriesChoice = new ChoiceBox<>(Series.getSerieslList());

     
    
    @Override
    public void start(Stage primaryStage) {
        
        
        seriesChoice.getSelectionModel().select(ModelProperties.getFourierSeries());
       
       seriesChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                
              if(seriesChoice.getSelectionModel().selectedIndexProperty().get() >= 0)
                { 
            ModelProperties.setFourierSeries(seriesChoice.getItems().get((int)newValue));
            
                    System.out.println("Fourier series changed from " + seriesChoice.getItems().get((int)oldValue) + " to " + seriesChoice.getItems().get((int)newValue));
                }
              
              
              if(!ModelProperties.getFourierSeries().isSimplySupported())
              {
                   ContinueWarning c = new ContinueWarning("WARNING: You have selected loaded edge boundary conditions that are not Simply-Suppported, a full solution could take a long time to complete.");
                    try {
                        c.start(new Stage());
                    } catch (Exception ex) {
                        Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                    }
              }
                
                
            }
        });
       
       
        
        VBox root = new VBox();
        root.getChildren().addAll(seriesLabel,seriesChoice , termsLabel);

        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Fourier Series");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    
    
   
    
}
