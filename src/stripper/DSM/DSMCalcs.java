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

    private double Py, Pcrl, Pcrd, Pcre, phiC;
    //private TextArea calcArea = new TextArea();

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

//    public void setTextArea(TextArea textArea) {
//        this.calcArea = textArea;
//    }
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

        System.out.println("Mcre = " + Mcre + "\n");
        System.out.println("Cb = " + Cb + "\n");
        Mcre = Cb * Mcre;
        System.out.println("Mcre := Cb⋅Mcre = " + Mcre + "\n");

        Mne = getMne(braced);

        Mnl = getMnl(Mne);
        Mnd = getMnd();

        System.out.println("--------------------------------------------------" + "\n");
        System.out.println("Predicted flexural strength per DSM 1.3" + "\n");
        System.out.println("--------------------------------------------------" + "\n");
        double Mn = Math.min(Math.min(Mne, Mnl), Mnd);

        System.out.println("Mne = " + Mne + "\n");
        System.out.println("Mnl = " + Mnl + "\n");
        System.out.println("Mnd = " + Mnd + "\n");
        System.out.println("per DSM 1.2.2, Mn is the minimum of Mne, Mnl, Mnd.\n");
        System.out.println("Mn = " + Mn + "\n");

        System.out.println("LRFD : φb = " + phiB + "\n");
        System.out.println("φb⋅Mn = " + phiB * Mn + "\n");

        return Mn;
    }

    public double getMne(boolean braced) {
        System.out.println("--------------------------------------------------\n");
        System.out.println("Lateral-torsional buckling check per DSM 1.2.2.1\n");
        System.out.println("--------------------------------------------------\n");
        double Mne = 0;

        if (!braced) {

            if (Mcre < 0.56 * My) {
                System.out.println("Mne = Mcre if Mcre < 0.56⋅My        (Eq. 1.2.2-1)\n");
                Mne = Mcre;
            } else if (Mcre <= 2.78 * My) {
                System.out.println("2.78⋅My ≥ Mcre ≥ 0.56⋅My     (Eq. 1.2.2-2)\n");

                Mne = (10.0 / 9.0) * My * (1 - ((10.0 * My) / (36.0 * Mcre)));
            } else if (Mcre > 2.78 * My) {
                System.out.println("Mne = My if Mcre > 2.78⋅My        (Eq. 1.2.2-3)\n");
                Mne = My;
            }

        }
        if (braced) {
            Mne = My;

            System.out.println("For a fully braced member lateral-torsional\n"
                    + "buckling will not occur and thus Mne = My, Mnl and Mnd must still be checked.\n");
        }

        System.out.println("Mne = " + Mne + "\n");

        System.out.println("\n");
        return Mne;
    }

    public double getMnl(double Mne) {
        System.out.println("--------------------------------------------------\n");
        System.out.println("Local buckling check per DSM 1.2.2.2\n");
        System.out.println("--------------------------------------------------\n");

        double Mnl = 0;

        double lambda = Math.sqrt(Mne / Mcrl);
        System.out.println("λl = " + lambda + " (Eq. 1.2.2-7)\n");

        if (lambda <= 0.776) {
            Mnl = Mne;
            System.out.println("Mnl = Mne if λl ≤ 0.776         Eq. 1.2.2-5\n");

        } else {
            Mnl = ((1.0 - 0.15 * Math.pow(Mcrl / Mne, 0.4)) * Math.pow(Mcrl / Mne, 0.4) * Mne);
            System.out.println("If  λl > 0.776      Eq. 1.2.2-6\n");
        }

        System.out.println("Mnl = " + Mnl + "\n");
        System.out.println("\n");

        return Mnl;
    }

    public double getMnd() {
        System.out.println("--------------------------------------------------\n");
        System.out.println("Distortional buckling check per DSM 1.2.2.3\n");
        System.out.println("--------------------------------------------------\n");
        double Mnd = 0;

        double lambda = Math.sqrt(My / Mcrd);
        System.out.println("λd = " + lambda + " (Eq. 1.2.2-10)\n");

        if (lambda <= 0.673) {
            Mnd = My;
            System.out.println("Mnd = My if λd ≤ 0.673  (Eq. 1.2.2-8)\n");
        } else {
            Mnd = ((1.0 - 0.22 * Math.pow(Mcrd / My, 0.5)) * Math.pow(Mcrd / My, 0.5) * My);
            System.out.println("if λd > 0.673  (Eq. 1.2.2-9)\n");

        }

        System.out.println("Mnd = " + Mnd + "\n");
        System.out.println("\n");

        return Mnd;
    }

    public double getNominalCompressiveStrength(boolean braced) {
        double Pne = 0;
        double Pnl = 0;
        double Pnd = 0;

        //System.out.println("Pcre = " + Pcre + "\n");
        //System.out.println("Cb = " + Cb + "\n");
        //Pcre = Cb * Pcre;
        //System.out.println("Mcre := Cb⋅Mcre = " + Mcre + "\n");

        Pne = getPne(braced);

        Pnl = getPnl(Pne);
        Pnd = getPnd();

        System.out.println("--------------------------------------------------" + "\n");
        System.out.println("Predicted compressive strength per DSM 1.2" + "\n");
        System.out.println("--------------------------------------------------" + "\n");
        double Pn = Math.min(Math.min(Pne, Pnl), Pnd);

        System.out.println("Pne = " + Pne + "\n");
        System.out.println("Pnl = " + Pnl + "\n");
        System.out.println("Pnd = " + Pnd + "\n");
        System.out.println("per DSM 1.2.1, Pn is the minimum of Pne, Pnl, Pnd.\n");
        System.out.println("Pn = " + Pn + "\n");

        System.out.println("LRFD : φc = " + phiC + "\n");
        System.out.println("φb⋅Mn = " + phiC * Pn + "\n");

        return Pn;
    }

    public double getPne(boolean braced) {
        System.out.println("--------------------------------------------------\n");
        System.out.println("Lateral-torsional buckling check per DSM 1.2.1.1\n");
        System.out.println("--------------------------------------------------\n");

        double Pne = 0;

        if (!braced) {

            double lambda = Math.sqrt(Py / Pcre);
            System.out.println("λc = " + lambda + " (Eq. 1.2.1-1)\n");

            if (lambda <= 1.5) {
                Pne = Math.pow(0.658, lambda * lambda) * Py;
                System.out.println("Pne = 0.658λc^2⋅Py if λc ≤ 1.5 (Eq. 1.2.1-2)\n");

            }
            if (lambda > 1.5) {
                Pne = (0.877 / (lambda * lambda)) * Py;
                System.out.println("Pne = (0.877/λc^2)*Py if λc > 1.5 (Eq. 1.2.1-3)\n");
            }

        }

        if (braced) {
            Pne = Py;

            System.out.println("For a fully braced member lateral-torsional\n"
                    + "buckling will not occur and thus Pne = Py, Mnl and Mnd must still be checked.\n");
        }

        System.out.println("Pne = " + Pne + "\n");

        System.out.println("\n");

        return Pne;
    }

    public double getPnl(double Pne) {
        System.out.println("--------------------------------------------------\n");
        System.out.println("Local buckling check per DSM 1.2.1.2\n");
        System.out.println("--------------------------------------------------\n");

        double Pnl = 0;

        double lambda = Math.sqrt(Pne / Pcrl);
        System.out.println("λl = " + lambda + " (Eq. 1.2.1-7)\n");

        if (lambda <= 0.776) {
            Pnl = Pne;
            System.out.println("Pnl = Pne if λl ≤ 0.776         Eq. 1.2.1-5\n");

        } else {
            Pnl = ((1.0 - 0.15 * Math.pow(Pcrl / Pne, 0.4)) * Math.pow(Pcrl / Pne, 0.4) * Pne);
            System.out.println("If  λl > 0.776      Eq. 1.2.1-6\n");
        }

        System.out.println("Pnl = " + Pnl + "\n");
        System.out.println("\n");

        return Pnl;
    }

    public double getPnd() {
        System.out.println("--------------------------------------------------\n");
        System.out.println("Distortional buckling check per DSM 1.2.1.3\n");
        System.out.println("--------------------------------------------------\n");
        double Pnd = 0;

        double lambda = Math.sqrt(Py / Pcrd);
        System.out.println("λd = " + lambda + " (Eq. 1.2.1-10)\n");

        if (lambda <= 0.561) {
            Pnd = Py;
            System.out.println("Pnd = Py if λd ≤ 0.0.561  (Eq. 1.2.1-8)\n");
        } else {
            Pnd = ((1.0 - 0.25 * Math.pow(Pcrd / Py, 0.6)) * Math.pow(Pcrd / Py, 0.6) * Py);
            System.out.println("if λd > 0.0.561  (Eq. 1.2.1-9)\n");

        }

        System.out.println("Pnd = " + Pnd + "\n");
        System.out.println("\n");

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
