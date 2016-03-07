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
        //calcArea.appendText("Local value = " + local);
    }

    public static void setDistortionalFactor(double val) {
        distortional = val;
        //calcArea.appendText("Distortional value = " + distortional);
    }

    public static void setGlobalFactor(double val) {
        global = val;
        //calcArea.appendText("Global value = " + global);
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

    private double Py, Pcrl, Pcrd, Pcre, phiC;
    private TextArea calcArea = new TextArea();

    public void setMy(double My) {
        this.My = My;
    }

    public void setMcrl(double Mcrl) {
        this.Mcrl = Mcrl;
    }

    public void setMcrd(double Mcrd) {
        this.Mcrd = Mcrd;
    }

    public void setMcre(double Mcre) {
        this.Mcre = Mcre;
    }

    public void setCb(double Cb) {
        this.Cb = Cb;
    }

    public void setPhiB(double phiB) {
        this.phiB = phiB;
    }

    public void setTextArea(TextArea textArea) {
        this.calcArea = textArea;
    }
    public void setPy(double Py) {
        this.Py = Py;
    }

    public void setPcrl(double Pcrl) {
        this.Pcrl = Pcrl;
    }

    public void setPcrd(double Pcrd) {
        this.Pcrd = Pcrd;
    }

    public void setPcre(double Pcre) {
        this.Pcre = Pcre;
    }

    public void setPhiC(double phiC) {
        this.phiC = phiC;
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

    public double getNominalCompressiveStrength(boolean braced) {
        double Pne = 0;
        double Pnl = 0;
        double Pnd = 0;

        //calcArea.appendText("Pcre = " + Pcre + "\n");
        //calcArea.appendText("Cb = " + Cb + "\n");
        //Pcre = Cb * Pcre;
        //calcArea.appendText("Mcre := Cb⋅Mcre = " + Mcre + "\n");

        Pne = getPne(braced);

        Pnl = getPnl(Pne);
        Pnd = getPnd();

        calcArea.appendText("--------------------------------------------------" + "\n");
        calcArea.appendText("Predicted compressive strength per DSM 1.2" + "\n");
        calcArea.appendText("--------------------------------------------------" + "\n");
        double Pn = Math.min(Math.min(Pne, Pnl), Pnd);

        calcArea.appendText("Pne = " + Pne + "\n");
        calcArea.appendText("Pnl = " + Pnl + "\n");
        calcArea.appendText("Pnd = " + Pnd + "\n");
        calcArea.appendText("per DSM 1.2.1, Pn is the minimum of Pne, Pnl, Pnd.\n");
        calcArea.appendText("Pn = " + Pn + "\n");

        calcArea.appendText("LRFD : φc = " + phiC + "\n");
        calcArea.appendText("φb⋅Mn = " + phiC * Pn + "\n");

        return Pn;
    }

    public double getPne(boolean braced) {
        calcArea.appendText("--------------------------------------------------\n");
        calcArea.appendText("Lateral-torsional buckling check per DSM 1.2.1.1\n");
        calcArea.appendText("--------------------------------------------------\n");

        double Pne = 0;

        if (!braced) {

            double lambda = Math.sqrt(Py / Pcre);
            calcArea.appendText("λc = " + lambda + " (Eq. 1.2.1-1)\n");

            if (lambda <= 1.5) {
                Pne = Math.pow(0.658, lambda * lambda) * Py;
                calcArea.appendText("Pne = 0.658λc^2⋅Py if λc ≤ 1.5 (Eq. 1.2.1-2)\n");

            }
            if (lambda > 1.5) {
                Pne = (0.877 / (lambda * lambda)) * Py;
                calcArea.appendText("Pne = (0.877/λc^2)*Py if λc > 1.5 (Eq. 1.2.1-3)\n");
            }

        }

        if (braced) {
            Pne = Py;

            calcArea.appendText("For a fully braced member lateral-torsional\n"
                    + "buckling will not occur and thus Pne = Py, Mnl and Mnd must still be checked.\n");
        }

        calcArea.appendText("Pne = " + Pne + "\n");

        calcArea.appendText("\n");

        return Pne;
    }

    public double getPnl(double Pne) {
        calcArea.appendText("--------------------------------------------------\n");
        calcArea.appendText("Local buckling check per DSM 1.2.1.2\n");
        calcArea.appendText("--------------------------------------------------\n");

        double Pnl = 0;

        double lambda = Math.sqrt(Pne / Pcrl);
        calcArea.appendText("λl = " + lambda + " (Eq. 1.2.1-7)\n");

        if (lambda <= 0.776) {
            Pnl = Pne;
            calcArea.appendText("Pnl = Pne if λl ≤ 0.776         Eq. 1.2.1-5\n");

        } else {
            Pnl = ((1.0 - 0.15 * Math.pow(Pcrl / Pne, 0.4)) * Math.pow(Pcrl / Pne, 0.4) * Pne);
            calcArea.appendText("If  λl > 0.776      Eq. 1.2.1-6\n");
        }

        calcArea.appendText("Pnl = " + Pnl + "\n");
        calcArea.appendText("\n");

        return Pnl;
    }

    public double getPnd() {
        calcArea.appendText("--------------------------------------------------\n");
        calcArea.appendText("Distortional buckling check per DSM 1.2.1.3\n");
        calcArea.appendText("--------------------------------------------------\n");
        double Pnd = 0;

        double lambda = Math.sqrt(Py / Pcrd);
        calcArea.appendText("λd = " + lambda + " (Eq. 1.2.1-10)\n");

        if (lambda <= 0.561) {
            Pnd = Py;
            calcArea.appendText("Pnd = Py if λd ≤ 0.0.561  (Eq. 1.2.1-8)\n");
        } else {
            Pnd = ((1.0 - 0.25 * Math.pow(Pcrd / Py, 0.6)) * Math.pow(Pcrd / Py, 0.6) * Py);
            calcArea.appendText("if λd > 0.0.561  (Eq. 1.2.1-9)\n");

        }

        calcArea.appendText("Pnd = " + Pnd + "\n");
        calcArea.appendText("\n");

        return Pnd;
    }

    public static void main(String[] args) {
        DSMCalcs d = new DSMCalcs();

        d.setMy(126.55);
        d.setMcrl(85);
        d.setMcrd(108);
        d.setMcre(218.93);
        d.setCb(1);
        d.setPhiB(0.9);

        d.setPy(48.42);
        d.setPcrl(5.8);
        d.setPcrd(13.1);
        d.setPcre(52.05);
        d.setPhiC(0.85);

        double Mn = d.getNominalCompressiveStrength(false);

    }

}
