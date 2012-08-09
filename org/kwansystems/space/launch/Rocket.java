package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.atmosphere.AirProperties;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public abstract class Rocket extends DerivativeSet {
  //This is designed to be used in a SecondDerivative set
  Table Flowrate; //Total stage flowrate, kg/sec
  public double DryMass;
  public double FuelMass;
  public double StartTime;
  public double StopTime;
  public double IspVac;  //Vacuum Isp, N-s/kg
  public double IspSL;   //Sea level Isp, N-s/kg
			 //For a rocket only operated at one altitude, 
			 //set both Isp's to the value for that altitude
  public double Isptau=8500; 
                         //Atmosphere scale height for ISP calc, km
                         //1-1/e (63.2%) change in Isp altitude
			 //Set to zero to use the IspVac value for all altitudes
  public String Name;
  public Guidance G;
  public Planet P;
  public ArrayListChartRecorder C;
  public Rocket(String LName,Planet LP,ArrayListChartRecorder LC) {
    this(LName,null,LP,LC);
  }
  public Rocket(String LName,Guidance LG,Planet LP,ArrayListChartRecorder LC) {
    G=LG;
    Name=LName;
    P=LP;
    C=LC;
  }
  public boolean StageActive(double T, boolean IsMajor) {
    if(T<StartTime) return false;
    if(T>StopTime) return false;
    if(FuelMassLeft(T,IsMajor)<=0) return false;
    return true;
  }
  public double FuelUsed(double T, boolean IsMajor) {
    double F=Flowrate.Integrate(0,T,0);
    if(F>FuelMass) F=FuelMass;
    if(IsMajor)C.Record(T,Name + " Fuel Used","kg",F);
    return F;
  }
  //return Thrust in newtons
  public double Thrust(double T, MathState X, boolean IsMajor) {
    double Alt=X.R().length()-P.R;
    double L=Flowrate.Interp(T,0);
    double F=L*Isp(Alt,T);
    if(IsMajor)C.Record(T,Name+" Thrust","N",F);
    if(IsMajor)C.Record(T,Name+" Isp","N-s/kg",Isp(Alt,T));
    if(IsMajor)C.Record(T,Name+" FlowRate","kg/s",L);
    return F;
  }
  public double FuelMassLeft(double T, boolean IsMajor) {
    return FuelMass-FuelUsed(T, IsMajor);
  }
  public double TotalMass(double T, boolean IsMajor) {
    return DryMass+FuelMassLeft(T,IsMajor);
  }
  //Override these to provide Isp as a function of time
  public double IspVac(double T) {return 0;}
  public double IspSL(double T) {return IspVac(T);}
  public final double Isp(double Alt,double T) {
    //returns predicted Isp (N-s/kg) as a function of altitude (km)
    double max=IspVac(T);
    double min=IspSL(T);
    if(min>0) {
      return max-(max-min)*Math.exp(-Alt/Isptau);
    } else {
      return max;
    }
  }
  public MathVector RelativeVelocity(MathState S) {
    return MathVector.sub(S.V(),P.Wind(S));
  }
  Table TransonicDragFactor=new LinearTable(0.0,5.0,new double[][] 
  {{0.130,0.128,0.125,0.123,0.121,0.123,0.125,0.127,0.129,0.131,0.135, //Mach 0.00-0.50
         0.143,0.150,0.162,0.178,0.210,0.255,0.295,0.335,0.385,0.435, //Mach 0.55-1.00
         0.485,0.535,0.570,0.600,0.610,0.617,0.620,0.620,0.617,0.616, //Mach 1.05-1.50
         0.610,0.606,0.600,0.592,0.585,0.577,0.572,0.562,0.552,0.545, //Mach 1.55-2.00
         0.533,0.525,0.516,0.507,0.499,0.491,0.483,0.476,0.470,0.463, //Mach 2.05-2.50
         0.455,0.451,0.447,0.441,0.437,0.431,0.429,0.422,0.418,0.415, //Mach 2.55-3.00
         0.411,0.408,0.404,0.401,0.399,0.395,0.391,0.389,0.387,0.382, //Mach 3.05-3.50
         0.381,0.380,0.378,0.375,0.371,0.370,0.369,0.367,0.364,0.362, //Mach 3.55-4.00
         0.360,0.359,0.358,0.357,0.356,0.354,0.353,0.352,0.351,0.350, //Mach 4.05-4.50
         0.349,0.348,0.347,0.347,0.346,0.345,0.344,0.343,0.342,0.342}} //Mach 4.55-5.00
	 );
  Table DynamicPressureTable=new QuadraticTable(
    new double [] {  0,5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100,105,110,115,120,125,130,135,140},
    new double [][] {{0,0.02,0.1,0.32,0.63,1.17,1.79,2.51,3.29,4.1,4.68,5.08,5.25,5.03,4.58,3.89,3.05,2.14,1.52,0.98,0.68,0.41,0.25,0.14,0.08,0.04,0.03,0.02,0}}
  );
  //Return the effective drag cross section area in m^2
  public abstract double DragArea(double T);
  //Override this if the lift area has some significance to your vehicle
  public double LiftArea(double T) {return DragArea(T);}
  double ConeHalfAngle=30*Math.PI/180;
  double TransonicFactor=2/0.620;
  //Override these as needed for a model other than cone
  public double DragCoeff(double T,double Mach, double alpha, boolean IsMajor) {
    //Return Drag coefficient as function of time, speed (m/s), and AoA
    if(Mach>5) Mach=5;
    double TDF=TransonicDragFactor.Interp(Mach,0);
    if(IsMajor)C.Record(T,Name+" Transonic Factor",null,TDF);
    if(T<140 & IsMajor) C.Record(T,Name+" Actual Q","psi",DynamicPressureTable.Interp(T));
    return (Math.cos(alpha)*Ca(alpha)+Math.sin(alpha)*Cn(alpha));
  }
  public double LiftCoeff(double T,double Mach, double alpha) {
    //Return Lift coefficient as function of time, speed (m/s), and AoA
    return 0;
  }
  //Cone model equations
  public double Cn(double alpha) {
    return Math.pow(Math.cos(ConeHalfAngle),2)*Math.sin(2*alpha);
  }
  public double Ca(double alpha) {
    return 2*Math.pow(Math.sin(ConeHalfAngle),2)+Math.pow(Math.sin(alpha),2)*(1-3*Math.pow(Math.sin(ConeHalfAngle),2));
  }
  //Return total atmospheric force vector in newtons
  public MathVector AeroForceVector(double T, MathState S, MathVector AV, boolean IsMajor) {
    if(IsMajor)C.Record(T,Name+" Position","m",S.R());
    MathVector VRel=RelativeVelocity(S);
    double Alt=S.length()-P.R;
    if(IsMajor)C.Record(T,Name+" Ground Relative Velocity","m/s",VRel);
    double V=(VRel.length());
    AirProperties Air=P.Atm.calcProps(Alt);
    if(IsMajor)C.Record(T,Name+" Airspeed","m/s",V);
    double Mach=Air.VSound>0?V/Air.VSound:0;
    if(IsMajor)C.Record(T,Name+" Speed of Sound","m/s",Air.VSound);
    if(IsMajor)C.Record(T,Name+" Mach","Mach Number",Mach);
    if(IsMajor)C.Record(T,Name+" Wind","m/s",P.Wind(S));
    double alpha;
    if (AV.length()>0) {
      alpha=AV.vangle(VRel);
    } else {
      alpha=0;
    }
    if(IsMajor)C.Record(T,Name+" Angle of Attack","deg",alpha*180/Math.PI);
    double Dens=Air.Density; //Dens is in kg/m^3
    if(IsMajor)C.Record(T,"Atmospheric Density","kg/m^3",Dens);
    double Cd=DragCoeff(T,Mach,alpha,IsMajor);        //Cd is unitless
    if(IsMajor)C.Record(T,"Drag Coefficient",null,Cd);
    double A=DragArea(T);                  //A is m^2
    if(IsMajor)C.Record(T,"Drag Area","m^2",A);
    double Force=Dens*V*V*Cd*A/2;
    if(IsMajor)C.Record(T,"Drag Force","N",Force);
    if(IsMajor)C.Record(T,"Dynamic pressure","N/m^2",Force/A);
    if(V==0) {
      return new MathVector(0,0,0);
    } else {
      return MathVector.mul(VRel,-Force/V);
    }
  }
  public final double DragFactor(double T) {return 0;}
  public MathVector dxdt(double T, MathVector X, boolean IsMajor) {
    double F,AccAvail=0;
    MathState S=(MathState)X;
    MathVector AV;

    boolean Cutoff=(T<StartTime);  //Ignition check
    Cutoff=Cutoff&(T>StopTime);    //Programmed burnout check
    if(!Cutoff) {
      F=FuelMassLeft(T,IsMajor);
      Cutoff=Cutoff&(F<=0);          //Fuel depletion check
      if(!Cutoff) AccAvail=Thrust(T,S,IsMajor); 
    }
    double M=TotalMass(T,IsMajor);
    AV=MathVector.div(G.ThrustVector(T,S,AccAvail,IsMajor),M);    //F=ma, a=F/m
    if(IsMajor)C.Record(T,Name+" Acceleration from Thrust","m/s^2",AV);
    MathVector DV=MathVector.div(AeroForceVector(T,S,AV,IsMajor),M); //F=ma, a=F/m
    if(IsMajor)C.Record(T,Name+" Acceleration from Aerodynamics","m/s^2",DV);
    MathVector TRA=MathVector.add(AV,DV);
    if(IsMajor)C.Record(T,Name+" Total non-gravity Acceleration","m/s^2",TRA);
    return TRA;
  }
}
