/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author SJ
 */
public class BooleanChoicePrompt {

    private boolean bucklingAnalysis;

    public BooleanChoicePrompt(Window owner, String title , String trueBtnText , String falseBtnText) {
        final Stage dialog = new Stage();

        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setX(owner.getX() + owner.getWidth());
        dialog.setY(owner.getY());

        final Button trueBtn = new Button(trueBtnText);
        final Button falseBtn = new Button(falseBtnText);

        trueBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                bucklingAnalysis = true;
                dialog.close();

            }
        });

        falseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                bucklingAnalysis = false;
                dialog.close();

            }
        });

        final HBox layout = new HBox(10);
        layout.setPrefSize(400, 150);
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().setAll(
                trueBtn,
                falseBtn
        );
        
        

        dialog.setScene(new Scene(layout));
        dialog.centerOnScreen();
        dialog.showAndWait();
        
        
        
       
        

        
    }

    public boolean getResult() {
        return bucklingAnalysis;
    }
}
