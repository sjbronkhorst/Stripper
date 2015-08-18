/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import UI.Strip;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

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
    private FileChooser fileDialog = new FileChooser();

    public FileHandler() {
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Stripper Geometry Files", "*.sgf"));
    }

    public void writeFile(ObservableList<Node> nodes, ObservableList<Strip> strips) throws IOException {

        File file = fileDialog.showSaveDialog(null);

        if (file != null) {

            FileWriter fw = new FileWriter(file);

            BufferedWriter bw = new BufferedWriter(fw);

            for (Node node : nodes) {
                bw.append("n " + node.getXCoord() + " " + node.getZCoord());
                bw.newLine();
            }

            for (Strip strip : strips) {
                bw.append("e " + strip.getNode1Id() + " " + strip.getNode2Id());
                bw.newLine();
            }

            //bw.append("TEST STRING");
            bw.close();
            fw.close();
        }

    }

    public ObservableList<Node> getNodeList() {
        return nodes;
    }

    public ObservableList<Strip> getStripList() {
        return strips;
    }

    public void ReadFile() throws FileNotFoundException, IOException, IllegalStateException {
        
        
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

            //System.out.println("Nr of lines " + nrOfLines);
            if (nrOfLines != 0) {

                for (int i = 0; i < nrOfLines; i++) {
                    s[i] = br2.readLine();
                    //System.out.println(s[i]);

                    String[] words = s[i].split(" ");

                    // System.out.println("words 0 " +words[0]);
                    if (words[0].equals("n")) {

                        Node tempNode = new Node(Double.parseDouble(words[1]), Double.parseDouble(words[2]));
                        nodeMap.put(tempNode.getNodeId(), tempNode);
                        nodes.add(tempNode);
                    } else if (words[0].equals("e")) {

                        Node n1 = nodeMap.get(Integer.parseInt(words[1]));

                        Node n2 = nodeMap.get(Integer.parseInt(words[2]));

                        if (n1 != null && n2 != null) {

                            strips.add(new Strip(n1, n2));
                        }
                    } else {
                        System.out.println("File syntax error");
                    }

                }
            } else {
                System.out.println("Error : File does not contain data");
            }

            br2.close();
            fr2.close();
        }

    }

    public static void main(String[] args) throws IOException {

        FileHandler f = new FileHandler();
        f.ReadFile();

        f.writeFile(null, null);

//        
//        for (int i = 0; i < s.length; i++)
//        {
//            System.out.println(s[i]);
//            
//        }
        System.out.println(f.nodes.toString());
        System.out.println(f.strips.toString());

    }

}
