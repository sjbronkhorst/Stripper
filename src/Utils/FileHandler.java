/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import UI.Defaults;
import UI.Model;
import UI.Main;
import UI.TestClass;
import fsm.Node;
import fsm.Strip;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import serialize.ResourceLoader;
import DSM.DSMCalcs;
import fsm.material.Material;
import fsm.material.Material_User;

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
                        Main.println("File syntax error");
                    }
                    
                }
            } else {
                Main.println("Error : File does not contain data");
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
                
                Main.println("File not found/available, close it and try again");
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
                for (int k = 0; k < data.length; k++) {
                    
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
                
                Main.println("File not found/available, close it and try again");
                return;
                
            }
            
            fileDialog.getExtensionFilters().clear();
        }
    }
    
    public void writeMaterial(Material mat) {
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
                
                Main.println("File not found/available, close it and try again");
                return;
                
            }
            
            fileDialog.getExtensionFilters().clear();
        }
        
        this.mat = mat;
        
    }
    
    public void readMaterial() throws FileNotFoundException, IOException, IllegalStateException {
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
            mat = new Material_User(name, Ex, Ey, vx, vy, G, fy);
            
            br.close();
            fr.close();
            fileDialog.getExtensionFilters().clear();
            
        }
    }
    
    public Material getMaterial() {
        return mat;
    }
    
    public void createReport(String designerName, String projectName, File crossSectionImg, File img1, File img2, File img3, Model model, DSMCalcs dsm) throws FileNotFoundException, IOException, InvalidFormatException {

        //Start of document
        XWPFDocument doc = new XWPFDocument();
        
        XWPFTable poiTable = doc.createTable(1, 3);

        //Paragraph 1
        XWPFParagraph p1 = poiTable.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);
        p1.setVerticalAlignment(TextAlignment.CENTER);
        p1.setSpacingAfter(0);
        
        boldText(p1, "Designer: ");
        text(p1, designerName);

        //Project
        XWPFParagraph p8 = poiTable.getRow(0).getCell(1).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);
        p8.setVerticalAlignment(TextAlignment.CENTER);
        p8.setSpacingAfter(0);
        boldText(p8, "Project: ");
        text(p8, projectName);
        
        XWPFParagraph p9 = poiTable.getRow(0).getCell(2).getParagraphs().get(0);
        p9.setAlignment(ParagraphAlignment.CENTER);
        p9.setVerticalAlignment(TextAlignment.CENTER);
        p9.setSpacingAfter(0);
        
        boldText(p9, "Date: ");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        text(p9, dateFormat.format(date));
        
        CTTbl table = poiTable.getCTTbl();
        CTTblPr pr = table.getTblPr();
        CTTblWidth tblW = pr.getTblW();
        tblW.setW(BigInteger.valueOf(5000));
        tblW.setType(STTblWidth.PCT);
        pr.setTblW(tblW);
        table.setTblPr(pr);
        
        XWPFParagraph modelParagraph = doc.createParagraph();
        modelParagraph.setAlignment(ParagraphAlignment.LEFT);
        lineBreak(modelParagraph);
        boldText(modelParagraph, "Model Properties:", true);
        lineBreak(modelParagraph);
        boldText(modelParagraph, "File Path: ");
        text(modelParagraph, "C:\\Users\\SJ\\Documents\\NetBeansProjects\\Stripper");
        lineBreak(modelParagraph);
        boldText(modelParagraph, "Maximum allowable stress: ");
        text(modelParagraph, Double.toString(model.getAllowableStress()));
        lineBreak(modelParagraph);
        boldText(modelParagraph, "Cross-sectional Area: ");
        text(modelParagraph, Double.toString(model.getCrossSectionalArea()));
        lineBreak(modelParagraph);
        
        boldText(modelParagraph, "Ixx: ");
        text(modelParagraph, Double.toString(model.getIxx()));
        lineBreak(modelParagraph);
        
        boldText(modelParagraph, "Ixx (principal): ");
        text(modelParagraph, Double.toString(model.getIxxPrincipal()));
        lineBreak(modelParagraph);
        
        boldText(modelParagraph, "Izz: ");
        text(modelParagraph, Double.toString(model.getIzz()));
        lineBreak(modelParagraph);
        
        boldText(modelParagraph, "Izz (principal): ");
        text(modelParagraph, Double.toString(model.getIzzPrincipal()));
        
        XWPFParagraph materialParagraph = doc.createParagraph();
        materialParagraph.setAlignment(ParagraphAlignment.LEFT);
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Material Properties: ", true);
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Material name: ");
        text(materialParagraph, model.getModelMaterial().getName());
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Young's Modulus (Ex): ");
        text(materialParagraph, "" + model.getModelMaterial().getEx());
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Young's Modulus (Ey): ");
        text(materialParagraph, "" + model.getModelMaterial().getEy());
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Poisson Ratio (vx): ");
        text(materialParagraph, "" + model.getModelMaterial().getVx());
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Poisson Ratio (vy): ");
        text(materialParagraph, "" + model.getModelMaterial().getVy());
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Shear Modulus (G): ");
        text(materialParagraph, "" + model.getModelMaterial().getG());
        lineBreak(materialParagraph);
        boldText(materialParagraph, "Yield Stress: ");
        text(materialParagraph, "" + model.getModelMaterial().getFy());
        lineBreak(materialParagraph);
        
        if (crossSectionImg != null) {
            boldText(doc.createParagraph(), "Cross-Section: ", true);
            fullWidthPicture(doc, crossSectionImg);
        }
        pageBreak(doc.createParagraph());
        
        if (img1 != null) {
            XWPFParagraph p = doc.createParagraph();
            boldText(p, "Local Buckling Factor chosen by user: ",true);
            p.setSpacingAfter(0);
            lineBreak(p);
            fullWidthPicture(doc, img1);
        }
        
        if (img2 != null) {
            XWPFParagraph p = doc.createParagraph();
            boldText(p, "Distortional Buckling Factor chosen by user: ",true);
            p.setSpacingAfter(0);
            lineBreak(p);
            fullWidthPicture(doc, img2);            
        }
       
        if (img3 != null) {
            XWPFParagraph p = doc.createParagraph();
            boldText(p, "Global Buckling Factor chosen by user: ",true);
            p.setSpacingAfter(0);
            lineBreak(p);
            fullWidthPicture(doc, img3);
        }
        pageBreak(doc.createParagraph());
        
        String type = "None";
        String local = "";
        String distortional = "";
        String global = "";
        String yield = "";
        double l = 0;
        double d = 0;
        double g = 0;
        double y = 0;
        
        
        if (dsm.getAnalysisType() == DSMCalcs.analysisType.BEAM) {
            type = "Beam";
            local = "Mcrl";
            distortional = "Mcrd";
            global = "Mcrd";
            yield = "My";
            l = dsm.getMcrl();
            d = dsm.getMcrd();
            g = dsm.getMcre();
            y = dsm.getMy();
            
        }
        if (dsm.getAnalysisType() == DSMCalcs.analysisType.COLUMN) {
            type = "Column";
            local = "Pcrl";
            distortional = "Pcrd";
            global = "Pcre";
            yield = "Py";
            l = dsm.getPcrl();
            d = dsm.getPcrd();
            g = dsm.getPcre();
            y=dsm.getPy();
        }
        
        XWPFParagraph inputParagraph = doc.createParagraph();
        boldText(inputParagraph, "DSM Input: " , true);
        lineBreak(inputParagraph);
        text(inputParagraph, "Analysis type: " + type);
        lineBreak(inputParagraph);
        text(inputParagraph, yield +" = " + y);
        lineBreak(inputParagraph);
        text(inputParagraph, local +" = " + l);
        lineBreak(inputParagraph);
        text(inputParagraph, distortional +" = " + d);
        lineBreak(inputParagraph);
        text(inputParagraph, global +" = " + g);
        
        String[] calcs = dsm.getTextArea().getText().split("\n");
        XWPFParagraph calcParagraph = doc.createParagraph();
        boldText(calcParagraph, "DSM Calculations: ",true);
        lineBreak(calcParagraph);
        for (int i = 0; i < calcs.length; i++) {
            text(calcParagraph, calcs[i]);
            lineBreak(calcParagraph);
        }

        fileDialog.getExtensionFilters().add(new ExtensionFilter("MS Word", "*.docx"));
        File file = fileDialog.showSaveDialog(null);
        
        
        FileOutputStream out = new FileOutputStream(file);
        doc.write(out);
        out.close();
        fileDialog.getExtensionFilters().clear();
        
    }
    
    private void boldText(XWPFParagraph paragraph, String text) {
        XWPFRun r = paragraph.createRun();
        r.setText(text);
        r.setBold(true);
    }
    
    private void boldText(XWPFParagraph paragraph, String text, boolean underline) {
        XWPFRun r = paragraph.createRun();
        r.setText(text);
        r.setBold(true);
        
        if (underline) {
            r.setUnderline(UnderlinePatterns.SINGLE);
        }
    }
    
    private void lineBreak(XWPFParagraph paragraph) {
        XWPFRun r = paragraph.createRun();
        r.addCarriageReturn();
    }
    
    private void pageBreak(XWPFParagraph paragraph) {
        XWPFRun r = paragraph.createRun();
        r.addBreak(BreakType.PAGE);
    }
    
    private void text(XWPFParagraph paragraph, String text) {
        XWPFRun r = paragraph.createRun();
        r.setText(text);
        
    }
    
    private void fullWidthPicture(XWPFDocument doc, File imgFile) throws InvalidFormatException, IOException {
        XWPFParagraph p2 = doc.createParagraph();
        p2.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun imageRun = p2.createRun();
        imageRun.setTextPosition(10);
        imageRun.setText("        ");
        imageRun.setFontSize(1);
        imageRun.addPicture(new FileInputStream(imgFile), XWPFDocument.PICTURE_TYPE_PNG, "image", Units.toEMU(469), Units.toEMU(274));
        p2.setBorderBottom(Borders.SINGLE);
        p2.setBorderTop(Borders.SINGLE);
        p2.setBorderRight(Borders.SINGLE);
        p2.setBorderLeft(Borders.SINGLE);
        
    }
    
    public static void serialize(Object obj, String fileName) {
        try {
            
            File f = ResourceLoader.getFileUserDirectory(fileName);
            FileOutputStream fout = new FileOutputStream(f);
            
            ObjectOutputStream oos = new ObjectOutputStream(fout);            
            oos.writeObject(obj);
            
            oos.close();
            System.out.println("Done serialising...");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Object deserialze(String givenfileName) {
        
        Object in;
        
        try {
            
            File f = ResourceLoader.getFileUserDirectory(givenfileName);
            FileInputStream fin = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fin);
            in = ois.readObject();
            ois.close();
            
            System.out.println("Done deserialising...");
            
            return in;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }        
    }    
    
}
