package org.kwansystems.space.peg;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.chart.*;
import static java.lang.Math.*;

public class MultiPEGAutopilot extends Autopilot {
  public MultiPEGAutopilot(double LMinorCycleRate, int LMajorCycleRate, PEGVehicleModel Lveh) {
    super(LMinorCycleRate,LMajorCycleRate,Lveh);
    v_e=Lveh.upperIsp();
    T_=Lveh.lowert();
    a_=Lveh.uppera();
    for(int i=0;i<v_e.length;i++) tau_[i]=v_e[i]/a_[i];
  }
  //Target variables
  @VariableDesc(desc="Targeted radius at burnout",units="m",Major=true,Minor=true)
  double[] r_T=new double[3];
  @VariableDesc(desc="Targeted vertical speed at burnout",units="m/s",Major=true,Minor=true)
  double[] rdot_T=new double[3];
  @VariableDesc(desc="Targeted horizontal speed at burnout",units="m/s",Major=true,Minor=true)
  double[] v_thetaT=new double[3];
  //Derived target variables
  @VariableDesc(desc="Targeted angular speed at burnout",units="rad/s",Major=true,Minor=false)
  double[] omega_T=new double[3];
  @VariableDesc(desc="Target specific angular momentum",units="m^2/s")
  double[] h_T=new double[3];
  @VariableDesc(desc="Target specific mechanical energy",units="m^2/s^2",Major=true,Minor=false)
  double[] energy_T=new double[3];
  //Feedback variables
  @VariableDesc(desc="Input zero order steering constant",units="",Major=true,Minor=false)
  double[] A_in=new double[3];
  @VariableDesc(desc="Input first order steering constant",units="1/s",Major=true,Minor=false)
  double[] B_in=new double[3];
  @VariableDesc(desc="Input estimated time to burnout",units="s",Major=true,Minor=false)
  double[] T_in=new double[3];
  @VariableDesc(desc="Updated zero order steering constant",units="",Major=true,Minor=false)
  double[] A_upd=new double[3];
  @VariableDesc(desc="Updated first order steering constant",units="1/s",Major=true,Minor=false)
  double[] B_upd=new double[3];
  @VariableDesc(desc="Updated estimated time to burnout",units="s",Major=true,Minor=false)
  double[] T_upd=new double[3];
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
  double[] a_=new double[3];
  @VariableDesc(desc="Effective exhaust velocity",units="m/s",Major=true,Minor=false)
  double[] v_e=new double[3];
  @VariableDesc(desc="Time to complete vehicle burn",units="s",Major=true,Minor=false)
  double[] tau_=new double[3];
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
  double[] rbar_=new double[3];
  @VariableDesc(desc="Vertical component of thrust axis at current time",units="",Major=true,Minor=false)
  double[] f_r=new double[3];
  @VariableDesc(desc="Vertical component of thrust axis at end of powered flight",units="",Major=true,Minor=false)
  double[] f_rT=new double[3];
  @VariableDesc(desc="Acceleration at burnout",units="m/s^2",Major=true,Minor=false)
  double[] a_T=new double[3];
  @VariableDesc(desc="Linear model rate of change in vertical component of thrust axis",units="1/s",Major=true,Minor=false)
  double[] fdot_r=new double[3];
  @VariableDesc(desc="Quadratic model zero order coefficent of downrange component of thrust axis",units="",Major=true,Minor=false)
  double[] f_theta=new double[3];
  @VariableDesc(desc="Quadratic model first order coefficent of downrange component of thrust axis",units="1/s",Major=true,Minor=false)
  double[] fdot_theta=new double[3];
  @VariableDesc(desc="Quadratic model second order coefficent of downrange component of thrust axis",units="1/s^2",Major=true,Minor=false)
  double[] fdotdot_theta=new double[3];
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
  double aa;
  @VariableDesc(desc="Steering integral b_1",units="m",Major=true,Minor=false)
  double bb;
  @VariableDesc(desc="Steering integral c_0",units="m",Major=true,Minor=false)
  double cc;
  @VariableDesc(desc="Steering integral c_1",units="m*s",Major=true,Minor=false)
  double dd;
  @VariableDesc(desc="Staging continuity zero order constant",units="",Major=true,Minor=true)
  double[] DeltaA_=new double[3];
  @VariableDesc(desc="Staging continuity first order constant",units="1/s",Major=true,Minor=true)
  double[] DeltaB_=new double[3];
  //Feedback variables
  @VariableDesc(desc="Current zero order steering constant",units="",Major=true,Minor=true)
  double[] A_=new double[3];
  @VariableDesc(desc="Current first order steering constant",units="1/s",Major=true,Minor=true)
  double[] B_=new double[3];
  @VariableDesc(desc="Current estimated burn time left on each stage",units="s",Major=true,Minor=true)
  double[] T_=new double[3];
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
  double[] v_T=new double[3];
  @VariableDesc(desc="Current stage",units="rockets",Major=true,Minor=false)
  int j;
  public void init(MathVector Lrvec, MathVector Lvvec, double Lt) {
    A_[1]=-0.02966;
    B_[1]=-1.77e-4;
    T_[2]=403.98;
    tlast=Lt;
    super.init(Lrvec,Lvvec,Lt);
  }
  public void init(MathVector Lrvec, MathVector Lvvec, double Lt, double Lr_T, double Lrdot_T, double Lvtheta_T) {
    toTheMoon=false;
    r_T[2]=Lr_T;
    rdot_T[2]=Lrdot_T;
    v_thetaT[2]=Lvtheta_T;
    //These are constant if the targets are constant
    h_T[2]=v_thetaT[2]*r_T[2];
    omega_T[2]=v_thetaT[2]/r_T[2];
    //Energy=vvec dot vvec-2*mu/r=|vvec|^2-2*mu/r
    energy_T[2]=v_thetaT[2]*v_thetaT[2]+rdot_T[2]*rdot_T[2]-2*mu/r_T[2];
    init(Lrvec,Lvvec,Lt);
  }
  public void init(MathVector Lrvec, MathVector Lvvec, double Lt, double Lz_p, MathVector MoonVec, double tFlight) {
    rvec=Lrvec;
    vvec=Lvvec;
    met=Lt;
    j=1;
    Navigate();
    TargetMoon(165000, MoonVec, tFlight);
    h_T[2] = sqrt(mu * p);
    if(e==1) {
      energy_T[2]=0.0; //Perfectly parabolic escape, total energy=0
    } else {
      energy_T[2] = -mu / (2 * a_m);
    }
    init(Lrvec,Lvvec,Lt,r_p,0,11000);
    toTheMoon=true;
  }
  @Override
  public void MajorCycle() {
    Navigate();
    if(a_[j]>0) {
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
    a_[j]=thrust/mass;
    v_e[j]=veh.Isp(met);
    if(a_[j]>0) tau_[j]=v_e[j]/a_[j];
    //Components of velocity in various directions
    omega=h/(r*r);
    if(omega_T[1]==0) omega_T[1]=omega; //Seed value for angular speed at first staging
    rdot=MathVector.dot(rhat, vvec);
    vtheta=MathVector.dot(thetahat, vvec);
    //Update the clock
    Deltat=met-tlast;
    tlast=met;
    //Update the old steering constants
    for(int i=1;i<=2;i++) {
      A_in[i]=A_[i];
      B_in[i]=B_[i];
      T_in[i]=T_[i];
    }
    A_[j]+=B_[j]*Deltat;
    T_[j]-=Deltat;
    for(int i=1;i<=2;i++) {
      A_upd[i]=A_[i];
      B_upd[i]=B_[i];
      T_upd[i]=T_[i];
    }
    if(T_[1]<0) {
      T_[1]=0;
      if(j==1) {
        A_[j]+=DeltaA_[j];
        B_[j]+=DeltaB_[j];
        j++;
      }
    }
  }
  //Steering integral: ideal velocity gained during powered flight
  private double b0(int j,double t_j) {
    return -v_e[j]*log(1-t_j/tau_[j]);
  }
  //Steering integral: nth moment of b0 about t=0
  private double b_(int n, int j, double t_j) {
    if(n==0) return b0(j,t_j);
    return b_(n-1,j,t_j)*tau_[j]-v_e[j]*pow(t_j,n)/n;
  }
  //Steering integral: ideal distance travelled during powered flight
  private double c0(int j,double t_j) {
    return b0(j,t_j)*t_j-b_(1,j,t_j);
  }
  //Steering integral: nth moment of c0 about t=0
  private double c_(int n, int j, double t_j) {
    if(n==0) return c0(j,t_j);
    return c_(n-1,j,t_j)*tau_[j]-v_e[j]*pow(t_j,n+1)/(n*(n+1));
  }
  private double a(int j, double t_j) {
    return a_[j]/(1-t_j/tau_[j]);
  }
  private void Estimate() {
    EstimateStaging();
    cutoffEnable=T_[2]<10;
    EstimateT();
    EstimateDeltatheta();
  }
  //Estimate vertical state and steering discontinuities at each staging point
  private void EstimateStaging() {
    //Ghosts of stages that never were
    h_T[0]=h;
    omega_T[0]=omega;
    r_T[0]=r;
    rdot_T[0]=rdot;
    T_[0]=0;
    DeltaA_[0]=0;
    DeltaB_[0]=0;
    if(j==1) {
      rdot_T[1]=rdot+b0(1,T_[1])*A_[1]+b_(1,1,T_[1])*B_[1];
      r_T[1]=r+rdot*T_[1]+c_(0,1,T_[1])*A_[1]+c_(1,1,T_[1])*B_[1];
      rbar_[1]=(r_T[1]+r)/2;
      f_r[1]=A_[1]+((mu/(r*r)-omega*omega*r)/a_[1]);
      a_T[1]=a(1,T_[1]);
      f_rT[1]=A_[1]+B_[1]*T_[1]+((mu/(r_T[1]*r_T[1])-omega_T[1]*omega_T[1]*r_T[1])/a_T[1]);
      fdot_r[1]=(f_rT[1]-f_r[1])/T_[1];
      f_theta[1]=1-f_r[1]*f_r[1]/2;
      fdot_theta[1]=-f_r[1]*fdot_r[1];
      fdotdot_theta[1]=-fdot_r[1]*fdot_r[1]/2;
      h_T[1]=h+rbar_[1]*(f_theta[1]*b0(1,T_[1])+fdot_theta[1]*b_(1,1,T_[1])+fdotdot_theta[1]*b_(2,1,T_[1]));
      v_thetaT[1]=h_T[1]/r_T[1];
      omega_T[1]=v_thetaT[1]/r_T[1];
      DeltaA_[1]=(mu/(r_T[1]*r_T[1])-omega_T[1]*omega_T[1]*r_T[1])*(1/a(1,T_[1])-1/a_[2]);
      DeltaB_[1]=(mu/(r_T[1]*r_T[1])-omega_T[1]*omega_T[1]*r_T[1])*(1/v_e[1]-1/v_e[2])
        +(3*omega_T[1]*omega_T[1]-2*mu/(r_T[1]*r_T[1]*r_T[1]))*rdot_T[1]*(1/a(1,T_[1])-1/a_[2]);
      A_[2]=A_[1]+DeltaA_[1]+B_[2]*T_[1];
      B_[2]=B_[1]+DeltaB_[1];
    } else {
      //Ghosts of stages past
      h_T[1]=h;
      omega_T[1]=omega;
      r_T[1]=r;
      rdot_T[1]=rdot;
      DeltaA_[1]=0;
      DeltaB_[1]=0;
    }
  }
  //Estimate the time required to gain the necessary angular momentum increment to reach the target orbit
  private void EstimateT() {
    if(a_[j]<=0) {
      //Just keep the old estimate if coasting
      return;
    } 
    rbar_[2]=(r_T[2]+r_T[1])/2;
    Deltah=h_T[2]-h_T[1];
    //Calculate current programmed pitch and approximate pitch rate
    f_r[2]=A_[2]+((mu/(r*r)-omega_T[1]*omega_T[1]*r)/a_[2]);
    a_T[2]=a(2,T_[2]);
    f_rT[2]=A_[j]+B_[j]*T_[j]+((mu/(r_T[2]*r_T[2])-omega_T[2]*omega_T[2]*r_T[2])/a_T[2]);
    fdot_r[2]=(f_rT[2]-f_r[2])/T_[2];
    //Calculate downrange thrust component coefficients
    f_theta[2]=1-f_r[2]*f_r[2]/2;
    fdot_theta[2]=-f_r[2]*fdot_r[2];
    fdotdot_theta[2]=-fdot_r[2]*fdot_r[2]/2;
    //Calculate required Deltav to gain targeted Deltah
    N1=Deltah/rbar_[2];
    N2a=v_e[j]*T_[2];
    N2b=fdot_theta[2]+fdotdot_theta[2]*tau_[2];
    N2=N2a*N2b;
    N3=fdotdot_theta[2]*v_e[2]*T_[2]*T_[2]/2;
    N=N1+N2+N3;
    D0=f_theta[2];
    D1=fdot_theta[2]*tau_[2];
    D2=fdotdot_theta[2]*tau_[2]*tau_[2];
    D=D0+D1+D2;
    if(abs(D)<1.0) {
      Deltav=N/D; //Normal mode, denominator is sane
    } else {
      //Something weird with the denominator, just use N1
      //equivalent to assuming rocket is pointing horizontal
      D=Double.NaN;
      Deltav=N1;
    }
    if(!cutoffEnable) T_[2]=tau_[2]*(1-exp(-Deltav/v_e[2]));
  }
  private void EstimateDeltatheta() {
    //We use the same rbar and ftheta calculated in EstimateT()
    Deltatheta=0;
    for(int i=1;i<=2;i++) {
      if(T_[i]>0) {
        //Calculate the coriolis terms
        d_3=h_T[i-1]*rdot_T[i-1]/(r_T[i-1]*r_T[i-1]*r_T[i-1]);
        d_3T=h_T[i]*rdot_T[i]/(r_T[i]*r_T[i]*r_T[i]);
        d_4=(d_3T-d_3)/T_[i];
        Deltatheta+=h_T[i-1]*T_[i]/(r_T[i-1]*r_T[i-1])+
                    (f_theta[i]*c0(i,T_[i])+fdot_theta[i]*c_(1,i,T_[i])+fdotdot_theta[i]*c_(2,i,T_[i]))/rbar_[i]-
                    d_3*T_[i]*T_[i]-d_4*T_[i]*T_[i]*T_[i]/3;
      }
    }
  }
  private void CalculateTargets() {
    sintheta=MathVector.dot(thetahat,rhat_m);
    costheta=MathVector.dot(rhat,rhat_m);
    theta=atan2(sintheta,costheta);
    if(theta<0) theta+=2*PI;
    eta_T=Deltatheta+eta_m-theta;
    r_T[2]=p/(1+e*cos(eta_T));
    rdot_T[2]=sqrt(mu/p)*e*sin(eta_T);
    v_T[2]=sqrt(2*mu/r-mu/a_m);
    v_thetaT[2]=sqrt(v_T[2]*v_T[2]-rdot_T[2]*rdot_T[2]);
    omega_T[2]=v_thetaT[2]/r_T[2];
  }
  //Calculate the steering coefficients and cutoff time
  private void GuideMajor() {
    //Calculate energy to go (needed for cutoff)
    Deltaenergy[0]=Deltaenergy[1];  energyTime[0]=energyTime[1];
    Deltaenergy[1]=Deltaenergy[2];  energyTime[1]=energyTime[2];
    energy=v*v-2*mu/r;
    Deltaenergy[2]=energy_T[2]-energy; energyTime[2]=met;
    if(T_[2]>10) {
      if(toTheMoon) {
        CalculateTargets();
      }
      cutoffEnable=false;
      aa=b0(1,T_[1])+b0(2,T_[2]);
      bb=b_(1,1,T_[1])+b_(1,2,T_[2])+b0(2,T_[1]);
      cc=c0(1,T_[1])+c0(2,T_[2])+b0(1,T_[1])*T_[2];
      dd=c_(1,1,T_[1])+b_(1,1,T_[1])*T_[2]+c0(2,T_[2])*T_[1]+c_(1,2,T_[2]);
      k_b=rdot_T[2]-rdot-b0(2,T_[2])*DeltaA_[1]-b_(1,2,T_[2])*DeltaB_[1];
      k_c=r_T[2]-r-rdot*(T_[1]+T_[2])-c0(2,T_[2])*DeltaA_[1]-c_(1,2,T_[2])*DeltaB_[1];

      B_[j]=(k_c*aa-cc*k_b)/(dd*aa-cc*bb);
      A_[j]=k_b/aa-bb*B_[j]/aa;
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
    if(a_[j]>0) { //Otherwise, we are in a coast, just keep the old acceleration to get us through
      g_term=-g/a_[j];
      cent_term=-cent/a_[j];
    }
    fhatdotrhat=A_[j]+B_[j]*(met-tlast)+g_term+cent_term;
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
    MultiPEGAutopilot PEG=new MultiPEGAutopilot(20,20,new PEGAC10());
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
    PEG.MajorRecord.PrintTable(new CSVPrinter("MultiPEGAutpilot_Major.csv"));
    PEG.MinorRecord.PrintTable(new CSVPrinter("MultiPEGAutpilot_Minor.csv"));
  }

}
