/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.DSM;

/**
 *
 * @author SJ
 */
public class DSMCalcs {
    
    
    
private static double local,distortional, global;

public static void setLocalFactor(double val)
{
    local = val;
    System.out.println("Local value = " + local);
}

public static void setDistortionalFactor(double val)
{
    distortional = val;
    System.out.println("Distortional value = " + distortional);
}

public static void setGlobalFactor(double val)
{
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


    
    



    double My, Mcrl, Mcrd, Mcre;
    

    public DSMCalcs(double My, double Mcrl, double Mcrd, double Mcre) {

        this.Mcrd = Mcrd;
        this.Mcrl = Mcrl;
        this.My = My;
        this.Mcre = Mcre;

    }

    public double getNominalFlexuralStrength(boolean braced) {

        double Mne = 0;
        double Mnl = 0;
        double Mnd = 0;

        
        Mne = getMne(braced);
      

        Mnl = getMnl(getMne(true));
        Mnd = getMnd();

        System.out.println("--------------------------------------------------");
        System.out.println("Predicted flexural strength per DSM 1.3");
        System.out.println("--------------------------------------------------");
        double Mn = Math.min(Math.min(Mne, Mnl), Mnd);

        System.out.println("Mne = " + Mne);
        System.out.println("Mnl = " + Mnl);
        System.out.println("Mnd = " + Mnd);
        System.out.println("per DSM 1.2.2, Mn is the minimum of Mne, Mnl, Mnd.");
        System.out.println("Mn = " + Mn);

        return Mn;
    }

    public double getMne(boolean braced) {
        System.out.println("--------------------------------------------------");
        System.out.println("Lateral-torsional buckling check per DSM 1.2.2.1");
        System.out.println("--------------------------------------------------");
        double Mne = 0;

        if (!braced) {
            if (Mcre < 0.56 * My) {
                System.out.println("Mne = Mcre if Mcre < 0.56⋅My        (Eq. 1.2.2-1)");
                Mne = Mcre;
            } else if (Mcre <= 2.78 * My) {
                System.out.println("2.78⋅My ≥ Mcre ≥ 0.56⋅My     (Eq. 1.2.2-2)");

                Mne = (10.0 / 9.0) * My * (1 - ((10.0 * My) / (36.0 * Mcre)));
            } else if (Mcre > 2.78 * My) {
                System.out.println("Mne = My if Mcre > 2.78⋅My        (Eq. 1.2.2-3)");
                Mne = My;
            }

        }
        if (braced) {
            Mne = My;

            System.out.println("For a fully braced member lateral-torsional\n"
                    + "buckling will not occur and thus Mne = My, Mnl and Mnd must still be checked.");
        }

        System.out.println("Mne = " + Mne);
        System.out.println("");
        return Mne;
    }

    public double getMnl(double Mne) {
        System.out.println("--------------------------------------------------");
        System.out.println("Local buckling check per DSM 1.2.2.2");
        System.out.println("--------------------------------------------------");

        double Mnl = 0;

        double lambda = Math.sqrt(Mne / Mcrl);
        System.out.println("λl = " + lambda + " (Eq. 1.2.2-7)");

        if (lambda <= 0.776) {
            Mnl = Mne;
            System.out.println("Mnl = Mne if λl ≤ 0.776         Eq. 1.2.2-5");

        } else {
            Mnl = ((1.0 - 0.15 * Math.pow(Mcrl / Mne, 0.4)) * Math.pow(Mcrl / Mne, 0.4) * Mne);
            System.out.println("If  λl > 0.776      Eq. 1.2.2-6");
        }

        System.out.println("Mnl = " + Mnl);
        System.out.println("");

        return Mnl;
    }

    public double getMnd() {
        System.out.println("--------------------------------------------------");
        System.out.println("Distortional buckling check per DSM 1.2.2.3");
        System.out.println("--------------------------------------------------");
        double Mnd = 0;

        double lambda = Math.sqrt(My / Mcrd);
        System.out.println("λd = " + lambda + " (Eq. 1.2.2-10)");

        if (lambda <= 0.673) {
            Mnd = My;
            System.out.println("Mnd = My if λd ≤ 0.673  (Eq. 1.2.2-8)");
        } else {
            Mnd = ((1.0 - 0.22 * Math.pow(Mcrd / My, 0.5)) * Math.pow(Mcrd / My, 0.5) * My);
            System.out.println("if λd > 0.673  (Eq. 1.2.2-9)");

        }

        System.out.println("Mnd = " + Mnd);
        System.out.println("");

        return Mnd;
    }

    public static void main(String[] args) {
        DSMCalcs d = new DSMCalcs(126.55, 85, 108, 218.93);

        double Mne = d.getNominalFlexuralStrength(false);

    }

}
