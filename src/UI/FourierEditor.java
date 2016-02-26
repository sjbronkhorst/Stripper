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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stripper.Strip;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public class FourierEditor extends Application {
    
    Label seriesLabel = new Label("Selected Fourier series : ");
    Label termsLabel = new Label("Number of longitudinal terms : ");
    TextField termsField = new TextField(Integer.toString(Defaults.getNumberOfTerms()));
    private ChoiceBox<Series> seriesChoice = new ChoiceBox<>(Series.getSerieslList());
    
    Button saveBtn = new Button("SAVE");

     
    
    @Override
    public void start(Stage primaryStage) {
        
        
        seriesChoice.getSelectionModel().select(Defaults.getBaseModel().getFourierSeries());
       
       seriesChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                
              if(seriesChoice.getSelectionModel().selectedIndexProperty().get() >= 0)
                { 
                    
                    
            Defaults.getBaseModel().setFourierSeries(seriesChoice.getItems().get((int)newValue));
            
            Strip.clearNumbering();
            
                    for (Strip s : Defaults.getBaseModel().getStripList())
                    {
                        Strip strip = Defaults.getBaseModel().getFourierSeries().getStrip(Defaults.getBaseModel());
                        
                        strip.clone(s);
                        s = strip;
                        
                        System.out.println(s.toString());
                    }
            
            
            
            
            
                    TableViewEdit.println("Fourier series changed from " + seriesChoice.getItems().get((int)oldValue) + " to " + seriesChoice.getItems().get((int)newValue));
                }
              
              
              if(!Defaults.getBaseModel().getFourierSeries().isSimplySupported())
              {
                   ContinueWarning c = new ContinueWarning("WARNING: You have selected loaded edge boundary conditions that are not Simply-Suppported,\n a full solution could take a long time to complete.");
                    try {
                        c.start(new Stage());
                    } catch (Exception ex) {
                        Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                    }
              }
              
              if(seriesChoice.getItems().get((int)newValue).onlySupportsBuckling() && !Defaults.bucklingAnalysis)
              {
                  ContinueWarning c = new ContinueWarning("WARNING: You are in Static Analysis mode.\n The chosen Series function only supports buckling analysis. \n Please choose another function.");
                    try {
                        c.start(new Stage());
                    } catch (Exception ex) {
                        Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                    }
              }
                
                
            }
        });
       
       saveBtn.setOnAction(new EventHandler<ActionEvent>() 
       {

            @Override
            public void handle(ActionEvent event) {
                Defaults.getBaseModel().setFourierTerms(Integer.parseInt(termsField.getText()));
            }
        });
       
       
        
        VBox root = new VBox();
        root.getChildren().addAll(seriesLabel,seriesChoice , termsLabel,termsField,saveBtn);

        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Fourier Series");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    
    
   
    
}
