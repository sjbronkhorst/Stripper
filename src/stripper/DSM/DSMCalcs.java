/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.DSM;

import javafx.scene.control.TextArea;

/**
 *
 * @author SJ
 */
public class DSMCalcs {

    private static double local, distortional, global;

    public static void setLocalFactor(double val) {
        local = val;
        System.out.println("Local value = " + local);
    }

    public static void setDistortionalFactor(double val) {
        distortional = val;
        System.out.println("Distortional value = " + distortional);
    }

    public static void setGlobalFactor(double val) {
        global = val;
        System.out.println("Global value = " + global);
    }

    public static double getLocalFactor() {
        return local;
    }

    public static double getDistortionalFactor() {
        return distortional;
    }

    public static double getGlobalFactor() {
        return global;
    }

    private double My, Mcrl, Mcrd, Mcre, Cb, phiB;
    private TextArea calcArea = new TextArea();

    public DSMCalcs(double My, double Mcrl, double Mcrd, double Mcre, double Cb, double phiB) {

        this.Mcrd = Mcrd;
        this.Mcrl = Mcrl;
        this.My = My;
        this.Mcre = Mcre;
        this.Cb = Cb;
        this.phiB = phiB;

    }

    public void setTextArea(TextArea textArea) {
        this.calcArea = textArea;
    }

    public double getNominalFlexuralStrength(boolean braced) {

        double Mne = 0;
        double Mnl = 0;
        double Mnd = 0;

        
        calcArea.appendText("Mcre = " + Mcre + "\n");
        calcArea.appendText("Cb = " + Cb + "\n");
        Mcre = Cb * Mcre;
        calcArea.appendText("Mcre := Cb⋅Mcre = " + Mcre + "\n");

        Mne = getMne(braced);

        Mnl = getMnl(Mne);
        Mnd = getMnd();

        calcArea.appendText("--------------------------------------------------" + "\n");
        calcArea.appendText("Predicted flexural strength per DSM 1.3" + "\n");
        calcArea.appendText("--------------------------------------------------" + "\n");
        double Mn = Math.min(Math.min(Mne, Mnl), Mnd);

        calcArea.appendText("Mne = " + Mne + "\n");
        calcArea.appendText("Mnl = " + Mnl + "\n");
        calcArea.appendText("Mnd = " + Mnd + "\n");
        calcArea.appendText("per DSM 1.2.2, Mn is the minimum of Mne, Mnl, Mnd.\n");
        calcArea.appendText("Mn = " + Mn + "\n");

        calcArea.appendText("LRFD : φb = " + phiB + "\n");
        calcArea.appendText("φb⋅Mn = " + phiB * Mn + "\n");

        return Mn;
    }

    public double getMne(boolean braced) {
        calcArea.appendText("--------------------------------------------------\n");
        calcArea.appendText("Lateral-torsional buckling check per DSM 1.2.2.1\n");
        calcArea.appendText("--------------------------------------------------\n");
        double Mne = 0;

        if (!braced) {

            if (Mcre < 0.56 * My) {
                calcArea.appendText("Mne = Mcre if Mcre < 0.56⋅My        (Eq. 1.2.2-1)\n");
                Mne = Mcre;
            } else if (Mcre <= 2.78 * My) {
                calcArea.appendText("2.78⋅My ≥ Mcre ≥ 0.56⋅My     (Eq. 1.2.2-2)\n");

                Mne = (10.0 / 9.0) * My * (1 - ((10.0 * My) / (36.0 * Mcre)));
            } else if (Mcre > 2.78 * My) {
                calcArea.appendText("Mne = My if Mcre > 2.78⋅My        (Eq. 1.2.2-3)\n");
                Mne = My;
            }

        }
        if (braced) {
            Mne = My;

            calcArea.appendText("For a fully braced member lateral-torsional\n"
                    + "buckling will not occur and thus Mne = My, Mnl and Mnd must still be checked.\n");
        }

        calcArea.appendText("Mne = " + Mne + "\n");

        calcArea.appendText("\n");
        return Mne;
    }

    public double getMnl(double Mne) {
        calcArea.appendText("--------------------------------------------------\n");
        calcArea.appendText("Local buckling check per DSM 1.2.2.2\n");
        calcArea.appendText("--------------------------------------------------\n");

        double Mnl = 0;

        double lambda = Math.sqrt(Mne / Mcrl);
        calcArea.appendText("λl = " + lambda + " (Eq. 1.2.2-7)\n");

        if (lambda <= 0.776) {
            Mnl = Mne;
            calcArea.appendText("Mnl = Mne if λl ≤ 0.776         Eq. 1.2.2-5\n");

        } else {
            Mnl = ((1.0 - 0.15 * Math.pow(Mcrl / Mne, 0.4)) * Math.pow(Mcrl / Mne, 0.4) * Mne);
            calcArea.appendText("If  λl > 0.776      Eq. 1.2.2-6\n");
        }

        calcArea.appendText("Mnl = " + Mnl + "\n");
        calcArea.appendText("\n");

        return Mnl;
    }

    public double getMnd() {
        calcArea.appendText("--------------------------------------------------\n");
        calcArea.appendText("Distortional buckling check per DSM 1.2.2.3\n");
        calcArea.appendText("--------------------------------------------------\n");
        double Mnd = 0;

        double lambda = Math.sqrt(My / Mcrd);
        calcArea.appendText("λd = " + lambda + " (Eq. 1.2.2-10)\n");

        if (lambda <= 0.673) {
            Mnd = My;
            calcArea.appendText("Mnd = My if λd ≤ 0.673  (Eq. 1.2.2-8)\n");
        } else {
            Mnd = ((1.0 - 0.22 * Math.pow(Mcrd / My, 0.5)) * Math.pow(Mcrd / My, 0.5) * My);
            calcArea.appendText("if λd > 0.673  (Eq. 1.2.2-9)\n");

        }

        calcArea.appendText("Mnd = " + Mnd + "\n");
        calcArea.appendText("\n");

        return Mnd;
    }

    public static void main(String[] args) {
        DSMCalcs d = new DSMCalcs(126.55, 85, 108, 218.93, 1, 0.9); // yield , local , dist , global , cb , phiB

        double Mn = d.getNominalFlexuralStrength(true);

    }

}
