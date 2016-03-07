/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.materials;

/**
 *
 * @author SJ
 */
public class Material_Steel extends Material {

    public Material_Steel() {
        name  = "S355";
        Ex = 210000;
        Ey = 210000;
        vx = 0.3;
        vy = 0.3;
        G = 81000;
        fy = 354;
    }
}
