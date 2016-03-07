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
public class Material_User extends Material {
    
    public Material_User(String name, double Ex, double Ey, double vx, double vy, double G , double fy) {
        this.name = name;
        this.Ex = Ex;
        this.Ey = Ey;
        this.vx = vx;
        this.vy = vy;
        this.G = G;
        this.fy = fy;
    }
    
}
