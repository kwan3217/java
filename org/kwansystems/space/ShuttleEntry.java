/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.space;

/**
 *
 * @author chrisj
 */
public class ShuttleEntry {
  double C1,C2,C3,C4,Df,C5;
  double Ef;
  double Vf1,Vf2,Vf3,Vf4;
  final double ft_to_meter=0.3048;
  final double g0=9.80665;
  public double TransitionDrag(double E) {
    return Df+C5*(E-Ef);
  }
  public double TransitionRange(double E) {
    double D=TransitionDrag(E);
    return (E-Ef)/(D-Df)*Math.log(D/Df);
  }
  public double KineticEnergy(double V) {
    return V*V/2.0;
  }
  public double PotentialEnergy(double Alt) {
    return g0*Alt;
  }
  public void TAEMEnergy(double Alt, double V) {
    Ef=KineticEnergy(V)+PotentialEnergy(Alt);
  }
  public static void main(String[] args) {
    ShuttleEntry SE=new ShuttleEntry();
    SE.Df=5.0;
    SE.C5=1.0e-5;
    SE.TAEMEnergy(83000*SE.ft_to_meter,2500*SE.ft_to_meter);
    System.out.println("KineticEnergy/mass (m^2/s^2): "+SE.KineticEnergy(2500*SE.ft_to_meter));
    System.out.println("PotentialEnergy/mass (m^2/s^2): "+SE.PotentialEnergy(83000*SE.ft_to_meter));
    System.out.println("TAEMEnergy/mass (m^2/s^2): "+SE.Ef);
    System.out.println("TAEMEnergy/weight (m): "+SE.Ef/SE.g0/SE.ft_to_meter);
    System.out.println("DragToGo (m/s^2): "+SE.TransitionDrag(1e6));
    System.out.println("RangeToGo (m): "+SE.TransitionRange(1e6));
  }
}
