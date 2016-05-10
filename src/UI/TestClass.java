/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import fsm.Model;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import DSM.DSMCalcs;
import Utils.FileHandler;
import Utils.MyMath;

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
        File f1 = new File("local.png");
         File f2 = new File("dist.png");
         File f3 = new File("global.png");
         
        DSMCalcs d = new DSMCalcs();
                d.setTextArea(new TextArea());

//                d.setMy(126.55);
//                d.setMcrl(85);
//                d.setMcrd(108);
//                d.setMcre(218.93);
//                d.setCb(1.67);
//                d.setPhiB(0.9);
//                
//                //d.setTextArea(calcArea);
//                d.getNominalFlexuralStrength(false);
//                d.setAnalysisType(DSMCalcs.analysisType.BEAM);
                
                 d.setPy(48.42);
                d.setPcrl(5.8);
                d.setPcrd(13.1);
                d.setPcre(52.05);
                d.setPhiC(0.85);
                
                //d.setTextArea(calcArea);
                d.getNominalCompressiveStrength(true);
                d.setAnalysisType(DSMCalcs.analysisType.COLUMN);
         
         
//         File cs = new File("cs.png");
//                            try {
//                                fh.createReport("SJ Bronkhorst" , "1234",cs, f1,f2,f3 , m,d , "simple.docx");
//                            } catch (IOException ex) {
//                                Logger.getLogger(LineChartWindow.class.getName()).log(Level.SEVERE, null, ex);
//                            } catch (InvalidFormatException ex) {
//                                Logger.getLogger(LineChartWindow.class.getName()).log(Level.SEVERE, null, ex);
//                            }
    }
    
    public static void main(String[] args) {

        Application.launch(args);
    }
    
}
