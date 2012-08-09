package org.kwansystems.space.asen5050;

public class Lab4_3 {
  public static final double kmperAU=149597870;
  public static final double EarthA=1.0*kmperAU;  //km
  public static final double VenusA=0.72332982*kmperAU; //km
  public static final double SunGM=1.32712428e11; //km,s
  public static final double EarthGM=398600.4415; //km,s
  public static final double VenusGM=325700;        //km,s
  public static final double EarthR=6378.1363;    //km
  public static final double VenusR=6052;        //km
  //Venus rotation rate
  public static final double VenusW=2*Math.PI/-243.01/86400;  //rad/s
  public static final double VenusJ2=0.000027;
  public static final double LEOrad=EarthR+400;   //Earth-relative start orbit, km
  public static final double LVOrad=7000;    //Venus-relative finish orbit, km
  public static void main(String[] args) {
    //First, calculate the heliocentric orbit.
    //We will use an exact Hohman orbit 180deg 
    //transfer between circular coplanar planet
    //orbits.
    double TransferA=(EarthA+VenusA)/2;
    System.out.println("TransferA (km): "+TransferA);
    System.out.println("          (AU): "+TransferA/kmperAU);
    double TransferE=(EarthA-VenusA)/(EarthA+VenusA);
    System.out.println("TransferE (km): "+TransferE);
    //Time for 180deg transfer
    double TransferT=Math.PI*Math.sqrt(Math.pow(TransferA,3)/SunGM);
    System.out.println("TransferT (s):  "+TransferT);
    System.out.println("        (day):  "+TransferT/86400.0);
    System.out.println("       (year):  "+TransferT/86400.0/365.25);
    //Sun-relative speeds at apoapse and periapse
    double Vp=Math.sqrt(2*SunGM/EarthA-SunGM/TransferA);
    System.out.println("Vp (km/s):      "+Vp);
    double Va=Math.sqrt(2*SunGM/VenusA-SunGM/TransferA);
    System.out.println("Va (km/s):      "+Va);
    //Speed of planets in circular orbits
    double Ve=Math.sqrt(SunGM/EarthA);
    System.out.println("Ve (km/s):      "+Ve);
    double Vm=Math.sqrt(SunGM/VenusA);
    System.out.println("Vm (km/s):      "+Vm);
    //Required Vinf at earth and Venus
    double Vinfe=Vp-Ve;
    System.out.println("Vinfe (km/s):   "+Vinfe);
    double Vinfm=Vm-Va;
    System.out.println("Vinfm (km/s):   "+Vinfm);
    //Departure speed from geocentric orbit
    double Vdep=Math.sqrt(Vinfe*Vinfe+2*EarthGM/LEOrad);
    System.out.println("Vdep (km/s):    "+Vdep);
    //Arrival speed to venucentric orbit
    double Varr=Math.sqrt(Vinfm*Vinfm+2*VenusGM/LVOrad);
    System.out.println("Varr (km/s):    "+Varr);
    //DeltaV for departure
    double DVdep=Vdep-Math.sqrt(EarthGM/LEOrad);
    System.out.println("DVdep (km/s):   "+DVdep);
    //DeltaV for arrival
    double DVarr=Varr-Math.sqrt(VenusGM/LVOrad);
    System.out.println("DVarr (km/s):   "+DVarr);
    //Phasing angle
    double EarthN=Math.sqrt(SunGM/Math.pow(EarthA,3));
    System.out.println("EarthN (rad/s): "+EarthN);
    System.out.println("     (deg/day): "+Math.toDegrees(EarthN)*86400);
    double VenusN=Math.sqrt(SunGM/Math.pow(VenusA,3));
    System.out.println("VenusN (rad/s):  "+VenusN);
    System.out.println("    (deg/day):  "+Math.toDegrees(VenusN)*86400);
    double Lead=VenusN*TransferT;
    System.out.println("Lead angle (r): "+Lead);
    System.out.println("         (deg): "+Math.toDegrees(Lead));
    double Phase=Lead-Math.PI;
    System.out.println("Phase angle(r): "+Phase);
    System.out.println("         (deg): "+Math.toDegrees(Phase));
    //Synodic period (Time between launch windows)
    double SynodicT=2*Math.PI/(EarthN-VenusN);
    System.out.println("SynodicT (s):   "+SynodicT);
    System.out.println("       (day):   "+SynodicT/86400.0);
    System.out.println("      (year):   "+SynodicT/86400.0/365.25);
    //Spheres of Influence
    double EarthSOI=EarthA*Math.pow(EarthGM/SunGM,0.4);
    double VenusSOI=VenusA*Math.pow(VenusGM/SunGM,0.4);
    //venusynchronous orbit
    //n=sqrt(mu/a^3)
    //n^2=mu/a^3
    //1/n^2=a^3/mu
    //mu/n^2=a^3
    //a=pow(mu/n^2,1/3);
    double VEO=Math.pow(VenusGM/(VenusW*VenusW),1.0/3.0);
    System.out.println("venusync (km):  "+VEO);
    //Venus sunsync orbit inclination
    double LVOn=Math.sqrt(VenusGM/Math.pow(LVOrad,3));
    System.out.println("LVOn (rad/s):   "+LVOn);
    System.out.println("   (deg/day):   "+Math.toDegrees(LVOn)*86400);
    double VenusSSI=Math.acos(-2*LVOrad*LVOrad*VenusN/(3*VenusJ2*LVOn*VenusR*VenusR));
    System.out.println("Suncync i (r):  "+VenusSSI);
    System.out.println("        (deg):  "+Math.toDegrees(VenusSSI));
    //Print out the answers to each question
    System.out.println("a) Total DeltaV required (km/s):          "+(DVdep+DVarr));
    System.out.println("b) Transfer time (year):                  "+TransferT/86400.0/365.25);
    System.out.println("   Semimajor axis (km):                   "+TransferA);
    System.out.println("                  (AU):                   "+TransferA/kmperAU);
    System.out.println("   Eccentricity:                          "+TransferE);
    System.out.println("c) Sunsync orbit inclination (deg):       "+Math.toDegrees(VenusSSI));
    System.out.println("d) venusync orbit radius (km):            "+VEO);
    System.out.println("                altitude (km):            "+(VEO-VenusR));
    System.out.println("e) Synodic period (year):                 "+SynodicT/86400.0/365.25);
    System.out.println("f) Earth Sphere of Influence radius (km): "+EarthSOI);
    System.out.println("   Venus Sphere of Influence radius (km):  "+VenusSOI);
      
  }
}
