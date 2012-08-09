package org.kwansystems.space.peg;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.chart.*;
import static java.lang.Math.*;

public class KPEGAutopilot extends Autopilot {
  public KPEGAutopilot(double LMinorCycleRate, int LMajorCycleRate, PEGVehicleModel Lveh) {
    super(LMinorCycleRate,LMajorCycleRate,Lveh);
  }
  //Target variables
  @VariableDesc(desc="Targeted radius at burnout",units="m",Major=true,Minor=true)
  double r_T;
  @VariableDesc(desc="Targeted vertical speed at burnout",units="m/s",Major=true,Minor=true)
  double rdot_T;
  @VariableDesc(desc="Targeted horizontal speed at burnout",units="m/s",Major=true,Minor=true)
  double vtheta_T;
  //Derived target variables
  @VariableDesc(desc="Targeted angular speed at burnout",units="rad/s",Major=true,Minor=false)
  double omega_T;
  @VariableDesc(desc="Target specific angular momentum",units="m^2/s")
  double h_T;
  @VariableDesc(desc="Target specific mechanical energy",units="m^2/s^2",Major=true,Minor=false)
  double energy_T;
  //Feedback variables
  @VariableDesc(desc="Input zero order steering constant",units="",Major=true,Minor=false)
  double A_in;
  @VariableDesc(desc="Input first order steering constant",units="1/s",Major=true,Minor=false)
  double B_in;
  @VariableDesc(desc="Input estimated time to burnout",units="s",Major=true,Minor=false)
  double T_in;
  @VariableDesc(desc="Updated zero order steering constant",units="",Major=true,Minor=false)
  double A_upd;
  @VariableDesc(desc="Updated first order steering constant",units="1/s",Major=true,Minor=false)
  double B_upd;
  @VariableDesc(desc="Updated estimated time to burnout",units="s",Major=true,Minor=false)
  double T_upd;
  //Navigation variables
  @VariableDesc(desc="Distance to center of central body",units="m",Major=true,Minor=true)
  double r;
  @VariableDesc(desc="Magnitude of velocity relative to central body",units="m/s",Major=true,Minor=false)
  double v;
  @VariableDesc(desc="Altitude above surface of central body",units="m",Major=true,Minor=true)
  double z;
  @VariableDesc(desc="Specific angular momentum vector",units="m^2/s",Major=true,Minor=false)
  protected MathVector hvec;
  @VariableDesc(desc="Specific angular momentum magnitude",units="m^2/s",Major=true,Minor=false)
  double h;
  @VariableDesc(desc="Vertical basis vector",units="",Major=true,Minor=true)
  MathVector rhat;
  @VariableDesc(desc="Crossrange basis vector",units="",Major=true,Minor=true)
  MathVector hhat;
  @VariableDesc(desc="Downrange basis vector",units="",Major=true,Minor=true)
  MathVector thetahat;
  @VariableDesc(desc="Total vehicle thrust",units="N",Major=true,Minor=false)
  double thrust;
  @VariableDesc(desc="Total vehicle mass",units="kg",Major=true,Minor=false)
  double mass;
  @VariableDesc(desc="Total vehicle acceleration",units="m/s^2",Major=true,Minor=true)
  double a;
  @VariableDesc(desc="Effective exhaust velocity",units="m/s",Major=true,Minor=false)
  double v_e;
  @VariableDesc(desc="Time to complete vehicle burn",units="s",Major=true,Minor=false)
  double tau;
  @VariableDesc(desc="Angular speed",units="rad/s",Major=true,Minor=true)
  double omega;
  @VariableDesc(desc="Vertical speed",units="m/s",Major=true,Minor=true)
  double rdot;
  @VariableDesc(desc="Downrange speed",units="m/s",Major=true,Minor=true)
  double vtheta;
  //Clock variables
  @VariableDesc(desc="Time since last major cycle",units="s",Major=true,Minor=false)
  double Deltat;
  @VariableDesc(desc="MET of last major cycle",units="s",Major=true,Minor=true)
  double tlast;
  //Estimation variables
  @VariableDesc(desc="Specific angular momentum to gain",units="m^2/s",Major=true,Minor=false)
  double Deltah;
  @VariableDesc(desc="Mean distance from center of central body over remaining powered flight",units="m",Major=true,Minor=false)
  double rbar;
  @VariableDesc(desc="Vertical component of thrust axis at current time",units="",Major=true,Minor=false)
  double f_r;
  @VariableDesc(desc="Vertical component of thrust axis at end of powered flight",units="",Major=true,Minor=false)
  double f_rT;
  @VariableDesc(desc="Acceleration at burnout",units="m/s^2",Major=true,Minor=false)
  double a_T;
  @VariableDesc(desc="Linear model rate of change in vertical component of thrust axis",units="1/s",Major=true,Minor=false)
  double fdot_r;
  @VariableDesc(desc="Linear model rate of change in vertical component of thrust axis, estimate 2",units="1/s",Major=true,Minor=false)
  double fdot_r2;
  @VariableDesc(desc="Difference in estimates of Linear model rate of change in vertical component of thrust axis",units="1/s",Major=true,Minor=false)
  double fdot_rdiff;
  @VariableDesc(desc="Quadratic model zero order coefficent of downrange component of thrust axis",units="",Major=true,Minor=false)
  double f_theta;
  @VariableDesc(desc="Quadratic model first order coefficent of downrange component of thrust axis",units="1/s",Major=true,Minor=false)
  double fdot_theta;
  @VariableDesc(desc="Quadratic model second order coefficent of downrange component of thrust axis",units="1/s^2",Major=true,Minor=false)
  double fdotdot_theta;
  @VariableDesc(desc="First term of numerator for Deltav calculation",units="m/s",Major=true,Minor=false)
  double N1;
  @VariableDesc(desc="Second term, first factor, of numerator for Deltav calculation",units="m/s",Major=true,Minor=false)
  double N2a;
  @VariableDesc(desc="Second term, second factor, of numerator for Deltav calculation",units="m/s",Major=true,Minor=false)
  double N2b;
  @VariableDesc(desc="Second term of numerator for Deltav calculation",units="m/s",Major=true,Minor=false)
  double N2;
  @VariableDesc(desc="Third term of numerator for Deltav calculation",units="m/s",Major=true,Minor=false)
  double N3;
  @VariableDesc(desc="Numerator for Deltav calculation",units="",Major=true,Minor=false)
  double N;
  @VariableDesc(desc="Zero term of denominator for Deltav calculation",units="",Major=true,Minor=false)
  double D0;
  @VariableDesc(desc="First term of denominator for Deltav calculation",units="",Major=true,Minor=false)
  double D1;
  @VariableDesc(desc="Second term of denominator for Deltav calculation",units="",Major=true,Minor=false)
  double D2;
  @VariableDesc(desc="Denominator for Deltav calculation",units="",Major=true,Minor=false)
  double D;
  @VariableDesc(desc="Ideal change in velocity before burnout",units="m/s",Major=true,Minor=false)
  double Deltav;
  @VariableDesc(desc="Targeting constant k_b",units="",Major=true,Minor=false)
  double k_b;
  @VariableDesc(desc="Targeting constant k_c",units="",Major=true,Minor=false)
  double k_c;
  @VariableDesc(desc="Steering integral b_0",units="m/s",Major=true,Minor=false)
  double b_0;
  @VariableDesc(desc="Steering integral b_1",units="m",Major=true,Minor=false)
  double b_1;
  @VariableDesc(desc="Steering integral c_0",units="m",Major=true,Minor=false)
  double c_0;
  @VariableDesc(desc="Steering integral c_1",units="m*s",Major=true,Minor=false)
  double c_1;
  //Feedback variables
  @VariableDesc(desc="Current zero order steering constant",units="",Major=true,Minor=true)
  double A;
  @VariableDesc(desc="Input first order steering constant",units="1/s",Major=true,Minor=true)
  double B;
  @VariableDesc(desc="Input estimated time to burnout",units="s",Major=true,Minor=true)
  double T;
  //Guidance variables
  @VariableDesc(desc="Local acceleration of gravity",units="m/s^2",Major=true,Minor=true)
  double g;
  @VariableDesc(desc="Local centrifugal acceleration",units="m/s^2",Major=true,Minor=true)
  double cent;
  @VariableDesc(desc="gravity pitch term",units="",Major=true,Minor=true)
  double g_term;
  @VariableDesc(desc="centrifugal pitch term",units="",Major=true,Minor=true)
  double cent_term;
  @VariableDesc(desc="Vertical component of thrust axis",units="",Major=true,Minor=true)
  double fhatdotrhat;
  @VariableDesc(desc="Crossrange component of thrust axis",units="",Major=true,Minor=true)
  double fhatdothhat;
  @VariableDesc(desc="Downrange component of thrust axis",units="",Major=true,Minor=true)
  double fhatdotthetahat;
  @VariableDesc(desc="Current specific mechanical energy",units="m^2/s^2",Major=true,Minor=false)
  double energy;
  @VariableDesc(desc="Specific mechanical energy history",units="m^2/s^2",Major=true,Minor=false)
  double[] Deltaenergy=new double[3];
  @VariableDesc(desc="Specific mechanical energy time history",units="s",Major=true,Minor=false)
  double[] energyTime=new double[3];
  @VariableDesc(desc="Cutoff enabled",units="",Major=true,Minor=false)
  boolean cutoffEnable;
  @VariableDesc(desc="Do we want to go to the moon or not?",units="",Major=true,Minor=false)
  boolean toTheMoon;
  @VariableDesc(desc="Predicted accurate cutoff time",units="s",Major=true,Minor=true)
  double t_cutoff;
  @VariableDesc(desc="Coriolis constant term",units="",Major=true,Minor=false)
  double d_3;
  @VariableDesc(desc="Coriolis constant term at burnout",units="",Major=true,Minor=false)
  double d_3T;
  @VariableDesc(desc="Coriolis linear term",units="",Major=true,Minor=false)
  double d_4;
  @VariableDesc(desc="Estimated travel angle to burnout",units="",Major=true,Minor=false)
  double Deltatheta;
  @VariableDesc(desc="Sine of polar angle",units="",Major=true,Minor=false)
  double sintheta;
  @VariableDesc(desc="Cosine of polar angle",units="",Major=true,Minor=false)
  double costheta;
  @VariableDesc(desc="Polar angle",units="rad",Major=true,Minor=false)
  double theta;
  @VariableDesc(desc="True anomaly at end of powered flight",units="rad",Major=true,Minor=false)
  double eta_T;
  @VariableDesc(desc="Total inertial velocity at end of powered flight",units="rad",Major=true,Minor=false)
  double v_T;
  public void init(MathVector Lrvec, MathVector Lvvec, double Lt, double Lr_T, double Lrdot_T, double Lvtheta_T) {
    toTheMoon=false;
    r_T=Lr_T;
    rdot_T=Lrdot_T;
    vtheta_T=Lvtheta_T;
    A=-0.02966;
    B=-1.77e-4;
    T=403.98;
    tlast=Lt;
    //These are constant if the targets are constant
    h_T=vtheta_T*r_T;
    omega_T=vtheta_T/r_T;
    //Energy=vvec dot vvec-2*mu/r=|vvec|^2-2*mu/r
    energy_T=vtheta_T*vtheta_T+rdot_T*rdot_T-2*mu/r_T;
    omega_T=vtheta_T/r_T;
    super.init(Lrvec,Lvvec,Lt);
  }
  public void init(MathVector Lrvec, MathVector Lvvec, double Lt, double Lz_p, MathVector MoonVec, double tFlight) {
    toTheMoon=true;
    rvec=Lrvec;
    vvec=Lvvec;
    met=Lt;
    Navigate();
    TargetMoon(165000, MoonVec, tFlight);
    A=-0.02966;
    B=-1.77e-4;
    T=100;
    vtheta_T=11000;
    rdot_T=0;
    r_T=r_p;
    h_T = sqrt(mu * p);
    if(e==1) {
      energy_T=0.0; //Perfectly parabolic escape, total energy=0
    } else {
      energy_T = -mu / (2 * a_m);
    }
    super.init(Lrvec,Lvvec,Lt);
  }
  @Override
  public void MajorCycle() {
    Navigate();
    if(a>0) {
      Estimate();
      GuideMajor();
    } //Otherwise, just remember the old values
  }
  private void Navigate() {
    //Vehicle state vector
    r=rvec.length();
    v=vvec.length();
    //Altitude
    z=r-Re;
    //Angular momentum
    hvec=MathVector.cross(rvec,vvec);
    h=hvec.length();
    //Basis vectors
    rhat=rvec.normal();
    hhat=hvec.normal();
    thetahat=MathVector.cross(hhat,rhat);
    //Measure the vehicle
    thrust=ThrustMag();
    mass=Mass();
    a=thrust/mass; 
    v_e=veh.Isp(met);
    tau=v_e/a; 
    //Components of velocity in various directions
    omega=h/(r*r);
    rdot=MathVector.dot(rhat, vvec);
    vtheta=MathVector.dot(thetahat, vvec);
    //Update the clock
    Deltat=met-tlast;
    tlast=met;
    //Update the old steering constants
    A_in=A;
    B_in=B;
    T_in=T;
    A+=B*Deltat;
    T-=Deltat;
    A_upd=A;
    B_upd=B;
    T_upd=T;
}
  //Steering integral: ideal velocity gained during powered flight
  private double b0(double T) {
    return -v_e*log(1-T/tau);
  }
  //Steering integral: nth moment of b0 about t=0
  private double b_(int n, double T) {
    if(n==0) return b0(T);
    return b_(n-1,T)*tau-v_e*pow(T,n)/n;
  }
  //Steering integral: ideal distance travelled during powered flight
  private double c0(double T) {
    return b0(T)*T-b_(1,T);
  }
  //Steering integral: nth moment of c0 about t=0
  private double c_(int n, double T) {
    if(n==0) return c0(T);
    return c_(n-1,T)*tau-v_e*pow(T,n+1)/(n*(n+1));
  }
  private void Estimate() {
    if(T>10) {
      cutoffEnable=false;
      EstimateT();
    }
    EstimateDeltatheta();
  }
  //Estimate the time required to gain the necessary angular momentum increment to reach the target orbit
  private void EstimateT() {
    if(a<=0) {
      return;
    } //Just keep the old estimate if coasting
    Deltah=h_T-h;
    rbar=(r_T+r)/2;
    //Calculate current programmed pitch and approximate pitch rate
    f_r=A+((mu/(r*r)-omega*omega*r)/a);
    a_T=a/(1-T/tau);
    f_rT=A+B*T+((mu/(r_T*r_T)-omega_T*omega_T*r_T)/a_T);
    fdot_r=(f_rT-f_r)/T;
    //Calculate downrange thrust component coefficients
    f_theta=1-f_r*f_r/2;
    fdot_theta=-f_r*fdot_r;
    fdotdot_theta=-fdot_r*fdot_r/2;
    //Calculate required Deltav to gain targeted Deltah
    N1=Deltah/rbar;
    N2a=v_e*T;
    N2b=fdot_theta+fdotdot_theta*tau;
    N2=N2a*N2b;
    N3=fdotdot_theta*v_e*T*T/2;
    N=N1+N2+N3;
    D0=f_theta;
    D1=fdot_theta*tau;
    D2=fdotdot_theta*tau*tau;
    D=D0+D1+D2;
    if(abs(D)<1.0) {
      Deltav=N/D; //Normal mode, denominator is sane
    } else {
      //Something weird with the denominator, just use N1
      //equivalent to assuming rocket is pointing horizontal
      D=Double.NaN;
      Deltav=N1;
    }
    T=tau*(1-exp(-Deltav/v_e));
  }
  private void EstimateDeltatheta() {
    //We use the same rbar and ftheta calculated in EstimateT()
    //Calculate the coriolis terms
    d_3=h*rdot/(r*r*r);
    d_3T=h_T*rdot_T/(r_T*r_T*r_T);
    d_4=(d_3T-d_3)/T;
    Deltatheta=(f_theta*c0(T)+fdot_theta*c_(1,T)+fdotdot_theta*c_(2,T))/rbar-d_3*T*T-d_4*T*T*T/3+h*T/(r*r);
  }
  private void CalculateTargets() {
    sintheta=MathVector.dot(thetahat,rhat_m);
    costheta=MathVector.dot(rhat,rhat_m);
    theta=atan2(sintheta,costheta);
    if(theta<0) theta+=2*PI;
    if(met>285) {
      eta_T=Deltatheta+eta_m-theta;
    } else {
      eta_T=toRadians(-10);
    }
    r_T=p/(1+e*cos(eta_T));
    rdot_T=sqrt(mu/p)*e*sin(eta_T);
    v_T=sqrt(2*mu/r-mu/a_m);
    vtheta_T=sqrt(v_T*v_T-rdot_T*rdot_T);
    omega_T=vtheta_T/r_T;
  }
  //Calculate the steering coefficients and cutoff time
  private void GuideMajor() {
    //Calculate energy to go (needed for cutoff)
    Deltaenergy[0]=Deltaenergy[1];  energyTime[0]=energyTime[1];
    Deltaenergy[1]=Deltaenergy[2];  energyTime[1]=energyTime[2];
    energy=v*v-2*mu/r;
    Deltaenergy[2]=energy_T-energy; energyTime[2]=met;
    if(T>10) {
      if(toTheMoon) {
        CalculateTargets();
      }
      cutoffEnable=false;
      b_0=b0(T);
      b_1=b_(1,T);
      c_0=c0(T);
      c_1=c_(1,T);
      k_b=rdot_T-rdot;
      k_c=r_T-r-rdot*T;

      B=(k_c*b_0-c_0*k_b)/(c_1*b_0-c_0*b_1);
      A=k_b/b_0-b_1*B/b_0;
    } else {
      cutoffEnable=true;
      //Cutoff guidance - use the energy history to calculate the cutoff time
      double x0,x1,x2,y0,y1,y2,CC,BB,AA;
      x0=energyTime[0];y0=Deltaenergy[0];
      x1=energyTime[1];y1=Deltaenergy[1];
      x2=energyTime[2];y2=Deltaenergy[2];
      double Delta=x1*x2*x2+x0*x1*x1+x0*x0*x2-x1*x1*x2-x0*x2*x2-x0*x0*x1;
      double DeltaC=y0*x1*x2*x2 +x0*x1*x1*y2 +x0*x0*y1*x2 -y0*x1*x1*x2 -x0*y1*x2*x2 -x0*x0*x1*y2;
      double DeltaB=   y1*x2*x2 +y0*x1*x1    +x0*x0*y2    -   x1*x1*y2 -   y0*x2*x2 -x0*x0*y1;
      double DeltaA=      x1*y2 +x0*y1       +y0*x2       -y1*x2       -x0*y2       -y0*x1;
      CC=DeltaC/Delta;
      BB=DeltaB/Delta;
      AA=DeltaA/Delta;
      double Q=-(BB+(BB>0?1:-1)*sqrt(BB*BB-4*AA*CC))/2;
      double t1=Q/AA;
      double t2=CC/Q;
      if((t1>met && t1<t2) || t2<met) {
        t_cutoff=t1;
      } else {
        t_cutoff=t2;
      }
    }
  }
  private void GuideMinor() {
    g=-mu/(r*r);
    cent=omega*omega*r;
    if(a>0) { //Otherwise, we are in a coast, just keep the old acceleration to get us through
      g_term=-g/a;
      cent_term=-cent/a;
    }
    fhatdotrhat=A+B*(met-tlast)+g_term+cent_term;
    fhatdothhat=0;
    fhatdotthetahat=sqrt(1-fhatdotrhat*fhatdotrhat-fhatdothhat*fhatdothhat);
    fhat=MathVector.add(MathVector.add(rhat.mul(fhatdotrhat),hhat.mul(fhatdothhat)), thetahat.mul(fhatdotthetahat));
  }
  private void Control() {
    
  }
  @Override
  public void MinorCycle() {
    GuideMinor();
    Control();
  }
  @Override
  public boolean Cutoff() {
    if(met>720) return true; //Backup cutoff signal
    if(cutoffEnable) return (met>t_cutoff);
    return false;
  }
  public static void main(String[] args) {
    KPEGAutopilot PEG=new KPEGAutopilot(20,20,new PEGAC10());
    double FPA=Math.toRadians(0.73);
    double zT=103000;
    double VMECO=7859.7;
    double rdotT=VMECO*sin(FPA);
    double v_thetaT=VMECO*cos(FPA);
    PEG.init(new MathVector(
               4978218.099098,
               3728940.222046,
               1717573.239846
             ),
             new MathVector(
               158.5018486752,
              2663.464702413,
              -828.8113686591
             ),
             149.669,185000,new MathVector(
               -192689301.9282,
               -326827002.7394,
                 -2273320.5603
             ),65.0*3600.0
             );
    PEG.fly();
    PEG.MajorRecord.PrintTable(new CSVPrinter("KPEGAutpilot_Major.csv"));
    PEG.MinorRecord.PrintTable(new CSVPrinter("KPEGAutpilot_Minor.csv"));
  }

}
