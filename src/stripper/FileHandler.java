/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Defaults;
import UI.TableViewEdit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import stripper.materials.Material;
import stripper.materials.Material_User;

/**
 *
 * @author SJ
 *
 *
 *
 *
 *
 *
 */
public class FileHandler {

    private Map<Integer, Node> nodeMap = new HashMap<>();
    private ObservableList<Node> nodes = FXCollections.<Node>observableArrayList();
    private ObservableList<Strip> strips = FXCollections.<Strip>observableArrayList();
    private Material mat;
    private FileChooser fileDialog = new FileChooser();

    public FileHandler() {

    }

//    public void writeGeom(ObservableList<Node> nodes, ObservableList<UIStrip> strips) throws IOException {
//        fileDialog.getExtensionFilters().add(new ExtensionFilter("Stripper Geometry Files", "*.sgf"));
//        File file = fileDialog.showSaveDialog(null);
//
//        if (file != null) {
//
//            FileWriter fw = new FileWriter(file);
//
//            BufferedWriter bw = new BufferedWriter(fw);
//
//            for (Node node : nodes) {
//                bw.append("n " + node.getXCoord() + " " + node.getZCoord());
//                bw.newLine();
//            }
//
//            for (UIStrip strip : strips) {
//                bw.append("e " + strip.getNode1Id() + " " + strip.getNode2Id());
//                bw.newLine();
//            }
//
//            //bw.append("TEST STRING");
//            bw.close();
//            fw.close();
//        }
//        fileDialog.getExtensionFilters().clear();
//    }
    
    
    
    
     public void writeGeom(ObservableList<Node> nodes, ObservableList<Strip> strips) throws IOException {
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Stripper Geometry Files", "*.sgf"));
        File file = fileDialog.showSaveDialog(null);

        if (file != null) {

            FileWriter fw = new FileWriter(file);

            BufferedWriter bw = new BufferedWriter(fw);

            for (Node node : nodes) {
                bw.append("n " + node.getXCoord() + "," + node.getZCoord());
                bw.newLine();
            }

            for (Strip strip : strips) {
                bw.append("e," + strip.getNode1Id() + "," + strip.getNode2Id() + "," + strip.getStripThickness());
                bw.newLine();
            }

            //bw.append("TEST STRING");
            bw.close();
            fw.close();
        }
        fileDialog.getExtensionFilters().clear();
    }
     
     
     
     

    public ObservableList<Node> getNodeList() {
        return nodes;
    }

    public ObservableList<Strip> getStripList() {
        return strips;
    }

    public void readGeom() throws FileNotFoundException, IOException, IllegalStateException {
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Stripper Geometry Files", "*.sgf"));
        File file = fileDialog.showOpenDialog(null);

        if (file != null) {

            Node.clearNumbering();
            Strip.clearNumbering();
            
            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr);
            int nrOfLines = 0;

            String line = br.readLine();

            while (line != null) {
                nrOfLines++;
                line = br.readLine();
            }

            br.close();
            fr.close();

            FileReader fr2 = new FileReader(file);
            BufferedReader br2 = new BufferedReader(fr2);
            String[] s = new String[nrOfLines];

            //TableViewEdit.println("Nr of lines " + nrOfLines);
            if (nrOfLines != 0) {

                for (int i = 0; i < nrOfLines; i++) {
                    s[i] = br2.readLine();
                    //TableViewEdit.println(s[i]);

                    String[] words = s[i].split(",");

                    // TableViewEdit.println("words 0 " +words[0]);
                    if (words[0].equals("n")) {

                        Node tempNode = new Node(Double.parseDouble(words[1]), Double.parseDouble(words[2]), Defaults.getBaseModel());
                        nodeMap.put(tempNode.getNodeId(), tempNode);
                        nodes.add(tempNode);
                    } else if (words[0].equals("e")) {

                        Node n1 = nodeMap.get(Integer.parseInt(words[1]));

                        Node n2 = nodeMap.get(Integer.parseInt(words[2]));
                        
                        double thickness = Double.parseDouble(words[3]);

                        if (n1 != null && n2 != null) {

                            
                            Strip strip = Defaults.getBaseModel().getFourierSeries().getStrip(Defaults.getBaseModel());
                            strip.setNode1(n1);
                            strip.setNode2(n2);
                            strip.setStripThickness(thickness);
                            strips.add(strip);   
                        }
                    } else {
                        TableViewEdit.println("File syntax error");
                    }

                }
            } else {
                TableViewEdit.println("Error : File does not contain data");
            }

            br2.close();
            fr2.close();
        }
        fileDialog.getExtensionFilters().clear();
    }

    public void writeCSV(String[][] data) throws IOException {
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Comma Separated Values", "*.csv"));
        int cols = data[0].length;
        int rows = data.length;

        File file = fileDialog.showSaveDialog(null);

        if (file != null) {

            try {

                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        bw.append(data[i][j] + ";");

                    }
                    bw.newLine();
                }

                bw.close();
                fw.close();

            } catch (Exception e) {

                TableViewEdit.println("File not found/available, close it and try again");
                return;

            }

            fileDialog.getExtensionFilters().clear();
        }
    }
    
    public void writeCSV(String[][][] data) throws IOException {
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Comma Separated Values", "*.csv"));
        int cols = data[0][0].length;
        int rows = data[0].length;

        File file = fileDialog.showSaveDialog(null);

        if (file != null) {

            try {

                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                for (int k = 0; k < data.length; k++) 
                {
                    
                
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        bw.append(data[k][i][j] + ";");

                    }
                    bw.newLine();
                }
                
                }

                bw.close();
                fw.close();

            } catch (Exception e) {

                TableViewEdit.println("File not found/available, close it and try again");
                return;

            }

            fileDialog.getExtensionFilters().clear();
        }
    }
    
    public void writeMaterial(Material mat)
    {
         fileDialog.getExtensionFilters().add(new ExtensionFilter("Stripper Material File", "*.material"));
         
         File file = fileDialog.showSaveDialog(null);

        if (file != null) {

            try {

                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.append(mat.getName());
                bw.newLine();
                bw.append(Double.toString(mat.getEx()));
                bw.newLine();
                bw.append(Double.toString(mat.getEy()));
                bw.newLine();
                bw.append(Double.toString(mat.getVx()));
                bw.newLine();
                bw.append(Double.toString(mat.getVy()));
                bw.newLine();
                bw.append(Double.toString(mat.getG()));
               
               
                bw.close();
                fw.close();

            } catch (Exception e) {

                TableViewEdit.println("File not found/available, close it and try again");
                return;

            }

            fileDialog.getExtensionFilters().clear();
        }
        
        this.mat = mat;
         
    }
    
    public void readMaterial()throws FileNotFoundException, IOException, IllegalStateException {
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Stripper Material File", "*.material"));
         
         File file = fileDialog.showOpenDialog(null);

        if (file != null) {

            

            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr);
           
            String name = br.readLine();
            double Ex = Double.parseDouble(br.readLine());
            double Ey = Double.parseDouble(br.readLine());
            double vx = Double.parseDouble(br.readLine());
            double vy = Double.parseDouble(br.readLine());
            double G = Double.parseDouble(br.readLine());
            double fy = Double.parseDouble(br.readLine());
            mat = new Material_User(name, Ex, Ey, vx, vy, G , fy);
            
            br.close();
            fr.close();
            
        }
    }
    public Material getMaterial()
    {
        return mat;
    }
    
    public static void createReport(String heading) throws FileNotFoundException, IOException
    {
        
        XWPFDocument doc = new XWPFDocument();

        XWPFParagraph p1 = doc.createParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);
        //p1.setBorderBottom(Borders.DOUBLE);
        //p1.setBorderTop(Borders.DOUBLE);

        //p1.setBorderRight(Borders.DOUBLE);
        //p1.setBorderLeft(Borders.DOUBLE);
        //p1.setBorderBetween(Borders.SINGLE);

        //p1.setVerticalAlignment(TextAlignment.TOP);

        
        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setText(heading);
        r1.setBold(true);
        r1.setFontFamily("Arial");
        r1.setUnderline(UnderlinePatterns.SINGLE);
        r1.setTextPosition(100);
        
        FileOutputStream out = new FileOutputStream("simple.docx");
        doc.write(out);
        out.close();
        
    }
    
    public static void main (String []args) throws IOException
    {
        FileHandler.createReport("HEADING !!!!!!!!");
        
        
    }
    
}
