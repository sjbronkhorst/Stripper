/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import stripper.FileHandler;
import stripper.MyMath;

/**
 *
 * @author SJ
 */
public class TestClass extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileHandler fh = new FileHandler();
        
        Model m = Defaults.getBaseModel();
        
        
         //File f1 = new File("local.png");
        File f1 = null;
         File f2 = new File("dist.png");
         File f3 = new File("global.png");
         
         File cs = new File("cs.png");
                            try {
                                fh.createReport("SJ Bronkhorst" , "1234",cs, f1,f2,f3 , m , "simple.docx");
                            } catch (IOException ex) {
                                Logger.getLogger(LineChartWindow.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvalidFormatException ex) {
                                Logger.getLogger(LineChartWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
    }
    
    public static void main(String[] args) {

        Application.launch(args);
    }
    
}
