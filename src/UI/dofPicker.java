/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import stripper.Node;

/**
 *
 * @author SJ
 */
public class dofPicker extends Application {

    CheckBox uCheck = new CheckBox(" fix U");
    CheckBox vCheck = new CheckBox(" fix V");
    CheckBox wCheck = new CheckBox(" fix W");
    CheckBox thetaCheck = new CheckBox(" fix \u0398");
    Button saveBtn = new Button("Save");
    Button cancelBtn = new Button("Cancel");
    Node nodeToEdit;
    
    
    
    public void setNode(Node n)
    {
        this.nodeToEdit = n;
    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        
        boolean[] status = nodeToEdit.getStatus();
        
        VBox root = new VBox(5);
        
        
        
        Sphere sphere=new Sphere(10);
        
        Group mygroup =new Group(sphere);
        mygroup.translateXProperty().set(10);
        
        HBox btnBox = new HBox(5);
        btnBox.getChildren().addAll(saveBtn, cancelBtn);
        root.getChildren().addAll(uCheck, vCheck, wCheck, thetaCheck, btnBox, mygroup);
        
        uCheck.setSelected(status[0]);
        vCheck.setSelected(status[1]);
        wCheck.setSelected(status[2]);
        thetaCheck.setSelected(status[3]);
        
        Scene scene = new Scene(root);
                
                
        primaryStage.setTitle("Pick global DOF" );
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        saveBtn.setOnAction(new EventHandler<ActionEvent>() 
        {

            @Override
            public void handle(ActionEvent event) {
                status[0] = uCheck.isSelected();
                status[1] = vCheck.isSelected();
                status[2] = wCheck.isSelected();
                status[3] = thetaCheck.isSelected();
                
                
                nodeToEdit.setStatus(status);
                
                primaryStage.close();
               
                
            }
        });
        
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
       dofPicker d = new dofPicker();
       Stage s = new Stage();
       d.start(s);
    }
}
