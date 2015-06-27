package org.kwansystems.space.asen5050;

import org.kwansystems.space.kepler.*;

public class HW9 {
  public static void main(String[] args) {
    Elements E=new Elements();
    E.parseTwoLine(
        "1 25544U 98067A 99274.46549890 .00020633 00000-0 22255-3 0 9641",
        "2 25544 51.5928  21.9569 0010233  27.6753 332.4812 15.63579496 49206"
    );
    System.out.println("Baseline Orbit: \n"+E);
    double J2=0.0010826269;
    double Mu=398600.4415;
    double Re=6378.1363;
    double LANdot=-3*E.N*Re*Re*J2/(2*E.P*E.P)*Math.cos(E.I);
    System.out.println("Nodal Regression: "+Math.toDegrees(LANdot*86400)+"deg/day");
    double APdot=3*E.N*Re*Re*J2/(4*E.P*E.P)*(4-5*Math.pow(Math.sin(E.I),2));
    System.out.println("Periapse Advance: "+Math.toDegrees(APdot*86400)+"deg/day");
    double M0dot=-3*E.N*Re*Re*J2*Math.sqrt(1-E.E*E.E)/(4*E.P*E.P)*(3*Math.pow(Math.sin(E.I),2)-2);
    System.out.println("M0 Advance: "+Math.toDegrees(M0dot*86400)+"deg/day");
    E.LAN=E.LAN+86400.0*LANdot;
    E.AP=E.AP+86400.0*APdot;
    E.M=E.M+86400.0*M0dot;
    System.out.println("Perturbed orbit after 24h: \n"+E);
  }
}
