package org.kwansystems.space.asen5050;

public class HW10 {
  public static final double kmperAU=149597870;
  public static final double EarthA=1.0*kmperAU;  //km
  public static final double MarsA=1.52367934*kmperAU; //km
  public static final double SunGM=1.32712428e11; //km,s
  public static final double EarthGM=398600.4415; //km,s
  public static final double MarsGM=43050;        //km,s
  public static final double EarthR=6378.1363;    //km
  public static final double MarsR=3397.2;        //km
  //Mars rotation rate
  public static final double MarsW=2*Math.PI/1.02595675/86400;  //rad/s
  public static final double MarsJ2=0.001964;
  public static final double LEOrad=EarthR+400;   //Earth-relative start orbit, km
  public static final double LMOrad=MarsR+300;    //Mars-relative finish orbit, km
  public static void main(String[] args) {
    //First, calculate the heliocentric orbit.
    //We will use an exact Hohman orbit 180deg 
    //transfer between circular coplanar planet
    //orbits.
    double TransferA=(EarthA+MarsA)/2;
    System.out.println("TransferA (km): "+TransferA);
    System.out.println("          (AU): "+TransferA/kmperAU);
    double TransferE=(MarsA-EarthA)/(EarthA+MarsA);
    System.out.println("TransferE (km): "+TransferE);
    //Time for 180deg transfer
    double TransferT=Math.PI*Math.sqrt(Math.pow(TransferA,3)/SunGM);
    System.out.println("TransferT (s):  "+TransferT);
    System.out.println("        (day):  "+TransferT/86400.0);
    System.out.println("       (year):  "+TransferT/86400.0/365.25);
    //Sun-relative speeds at apoapse and periapse
    double Vp=Math.sqrt(2*SunGM/EarthA-SunGM/TransferA);
    System.out.println("Vp (km/s):      "+Vp);
    double Va=Math.sqrt(2*SunGM/MarsA-SunGM/TransferA);
    System.out.println("Va (km/s):      "+Va);
    //Speed of planets in circular orbits
    double Ve=Math.sqrt(SunGM/EarthA);
    System.out.println("Ve (km/s):      "+Ve);
    double Vm=Math.sqrt(SunGM/MarsA);
    System.out.println("Vm (km/s):      "+Vm);
    //Required Vinf at earth and mars
    double Vinfe=Vp-Ve;
    System.out.println("Vinfe (km/s):   "+Vinfe);
    double Vinfm=Vm-Va;
    System.out.println("Vinfm (km/s):   "+Vinfm);
    //Departure speed from geocentric orbit
    double Vdep=Math.sqrt(Vinfe*Vinfe+2*EarthGM/LEOrad);
    System.out.println("Vdep (km/s):    "+Vdep);
    //Arrival speed to aerocentric orbit
    double Varr=Math.sqrt(Vinfm*Vinfm+2*MarsGM/LMOrad);
    System.out.println("Varr (km/s):    "+Varr);
    //DeltaV for departure
    double DVdep=Vdep-Math.sqrt(EarthGM/LEOrad);
    System.out.println("DVdep (km/s):   "+DVdep);
    //DeltaV for arrival
    double DVarr=Varr-Math.sqrt(MarsGM/LMOrad);
    System.out.println("DVarr (km/s):   "+DVarr);
    //Phasing angle
    double EarthN=Math.sqrt(SunGM/Math.pow(EarthA,3));
    System.out.println("EarthN (rad/s): "+EarthN);
    System.out.println("     (deg/day): "+Math.toDegrees(EarthN)*86400);
    double MarsN=Math.sqrt(SunGM/Math.pow(MarsA,3));
    System.out.println("MarsN (rad/s):  "+MarsN);
    System.out.println("    (deg/day):  "+Math.toDegrees(MarsN)*86400);
    double Lead=MarsN*TransferT;
    System.out.println("Lead angle (r): "+Lead);
    System.out.println("         (deg): "+Math.toDegrees(Lead));
    double Phase=Lead-Math.PI;
    System.out.println("Phase angle(r): "+Phase);
    System.out.println("         (deg): "+Math.toDegrees(Phase));
    //Synodic period (Time between launch windows)
    double SynodicT=2*Math.PI/(EarthN-MarsN);
    System.out.println("SynodicT (s):   "+SynodicT);
    System.out.println("       (day):   "+SynodicT/86400.0);
    System.out.println("      (year):   "+SynodicT/86400.0/365.25);
    //Spheres of Influence
    double EarthSOI=EarthA*Math.pow(EarthGM/SunGM,0.4);
    double MarsSOI=MarsA*Math.pow(MarsGM/SunGM,0.4);
    //Aerosynchronous orbit
    //n=sqrt(mu/a^3)
    //n^2=mu/a^3
    //1/n^2=a^3/mu
    //mu/n^2=a^3
    //a=pow(mu/n^2,1/3);
    double AEO=Math.pow(MarsGM/(MarsW*MarsW),1.0/3.0);
    System.out.println("Aerosync (km):  "+AEO);
    //Mars sunsync orbit inclination
    double LMOn=Math.sqrt(MarsGM/Math.pow(LMOrad,3));
    System.out.println("LMOn (rad/s):   "+LMOn);
    System.out.println("   (deg/day):   "+Math.toDegrees(LMOn)*86400);
    double MarsSSI=Math.acos(-2*LMOrad*LMOrad*MarsN/(3*MarsJ2*LMOn*MarsR*MarsR));
    System.out.println("Suncync i (r):  "+MarsSSI);
    System.out.println("        (deg):  "+Math.toDegrees(MarsSSI));
    //Print out the answers to each question
    System.out.println("a) Total DeltaV required (km/s):          "+(DVdep+DVarr));
    System.out.println("b) Transfer time (year):                  "+TransferT/86400.0/365.25);
    System.out.println("   Semimajor axis (km):                   "+TransferA);
    System.out.println("                  (AU):                   "+TransferA/kmperAU);
    System.out.println("   Eccentricity:                          "+TransferE);
    System.out.println("c) Sunsync orbit inclination (deg):       "+Math.toDegrees(MarsSSI));
    System.out.println("d) Aerosync orbit radius (km):            "+AEO);
    System.out.println("                altitude (km):            "+(AEO-MarsR));
    System.out.println("e) Synodic period (year):                 "+SynodicT/86400.0/365.25);
    System.out.println("f) Earth Sphere of Influence radius (km): "+EarthSOI);
    System.out.println("   Mars Sphere of Influence radius (km):  "+MarsSOI);
      
  }
}
