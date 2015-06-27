package org.kwansystems.space;

import org.kwansystems.tools.vector.*;
import static java.lang.Math.*;

public class PEGAutopilot {
  private int N1; //State, 1==booster, 2==above
  private int N2; //State, 1==before BECO enable, 2==before BECO discrete, 3==above
  private int N3; //State, 1==sustainer, 2==centaur
  private double g;
  private double mu;
  private double Re;
  private double t;  //Mission Elapsed Time, seconds
  private double t_s; //Burn time of sustainer, seconds
  private double z_d; //
  private double r_d; //Target radius, m
  private double rdot_d; //Target vertical speed, m/s
  private double z_p; //
  private double r_p; //Target orbit periapse radius, m
  private double h_d; //Target specific angular momentum, m^2/s
  private double E_d; //Target specific energy, m^2/s^2
  private double[] t_=new double[3]; //Estimated remaining time on each stage, s
  private double[] tau_=new double[3]; //
  private double[] a_=new double[3]; //Estimated acceleration of each stage and above if firing now, m/s^2
  private double[] a_f=new double[3]; //Estimated acceleration of each stage and above at burnout, m/s^2
  private double[] G_=new double[3];  //
  private double[] d_1=new double[3]; //
  private double[] d_2=new double[3]; //
  private double[] d_3=new double[3]; //
  private double[] d_4=new double[3]; //
  private double[] fd_h=new double[3]; //
  private double[] f_r=new double[3]; //
  private double[] f_h=new double[3]; //
  private double[] f_theta=new double[3]; //
  private double[] fd_r=new double[3]; //
  private double E_1; //Booster control: BECO enable MET
  private double E_2; //Booster control: BECO discrete acceleration. Indicates booster engine cutoff
  private double E_3; //Booster control: Sustainer phase start acceleration, >E_2. Indicates booster section jettison
  private double E_4; //Sustainer control: Sustainer phase end acceleration. Indicates sustainer shutdown
  private double E_c; //Centaur control: Specific energy-to-go at termination of steering constant calculation, m^2/s^2
  private double eps; //Specific energy-to-go now, m^2/s^2
  private double p;   //Orbit target semi-paramater, m
  private double e;   //Orbit target eccentricity, m
  private double a;   //Current acceleration, m/s^2
  private double A;   //Pitch Steering coefficient
  private double B;   //Pitch Steering rate coefficient, 1/s
  private double K;   //Yaw Steering coefficient
  int j; //Current stage, 1=sustainer, 2=centaur
  private double t_last; //MET of last major cycle
  private double[] v_e=new double[3]; //Effective velocity (Isp) of each stage, m/s
  private MathVector Rhat_T; //Unit vector pointing from center of Earth to target at target intercept time
  private double eta_T;      //rad, True anomaly on target orbit at target intercept
  private double eta_d;      //rad, True anomaly on target orbit at end of powered flight
  private double thrust;     //N, Magnitude of sum of non-gravitational forces
  private double mass;       //kg, current total mass of vehicle
  private MathVector rvec,vvec,hvec;
  private double r,v,h;
  private MathVector rhat,hhat,thetahat;
  private double rdot;
  private double E;
  private double theta;
  private double G;
  private double G_f;
  public MathVector fhat;
  private double fdot_r;
  private double[] fdot_h=new double[3];
  private double[] fdot_theta=new double[3];
  private double[] fdotdot_theta=new double[3];
  private double[] Deltav=new double[3];
  private double[] b_0=new double[3];
  private double[] b_1=new double[3];
  private double[] b_2=new double[3];
  private double[] c_0=new double[3];
  private double[] c_1=new double[3];
  private double[] c_2=new double[3];
  private double f_rf;
  public double Deltat;
  public double Deltah;
  public double DeltaV;
  public double DeltaA;
  public double DeltaB;
  private double[] Delta_=new double[3];
  private double[] Deltah_=new double[3];
  private double[] r_=new double[3];
  private double[] rdot_=new double[3];
  private double[] h_=new double[3];
  private double[] hdot_=new double[3];
  private double[] Deltar_=new double[3];
  private double[] Deltardot_=new double[3];
  private double Deltar;
  private double Deltardot;
  private double[] Deltatheta_=new double[3];
  private double eps_h;
  private double alpha,beta,gamma,delta;
  public void init() {
    Box01();
    Box02();
    N1=1;
    t=150.447; //
  };
  public void MajorCycle() {
    PointB();
  }
  public void TargetMoon(MathVector R_T, double TargetT) {
    double rMoon=R_T.length();
    double aMin=(r_p+rMoon)/2.0;

    double aMax=aMin*5;
    double aTarget=0;
    for(int i=0;i<20;i++) {
      aTarget=(aMin+aMax)/2;
      double n=sqrt(mu/(aTarget*aTarget*aTarget));
      e=(aTarget-r_p)/aTarget;
      p=aTarget*(1-e*e);
      eta_T=acos(p/(rMoon*e)-1.0/e);
      double EMoon=2*atan(sqrt((1-e)/(1+e))*tan(eta_T/2));
      double tThis=(EMoon-e*sin(EMoon))/n;
      System.out.printf("%d, %20.12e, %20.12e, %20.12e, %20.12e, %20.12e\n",i,aMin,aMax,aTarget,tThis,e);
      if(tThis<TargetT) {
        //Time is too small, orbit is too fast, orbit is too high,
        //shift the range down, top of new range is middle of old range
        aMax=aTarget;
      } else {
        //Opposite
        aMin=aTarget;
      }
    }
    h_d=sqrt(mu*p);       //m^2/s, angular momentum of target orbit, find from Moon targeting
//    h_d=71902569775.06;
    E_d=-mu/(2*aTarget);      //m^2/s^2, Transfer orbit specific energy, find from Moon targeting
    p=h_d*h_d/mu; //Semi-parameter of target orbit
    e=sqrt(1+E_d*p/mu); //eccentricity of target orbit
	  double haTarget=aTarget*2-r_p-Re;
    Rhat_T=R_T.normal(); //Unit vector pointed at target at desired intercept time, find from Moon targeting

  }
  private void Booster() {
    if(N2==1) { //Box 05
      if(t>E_1) { //Box 6
        Box07();
      }
    } else if(N2==2 && a<E_2) { //Box 08
      Box10();
    } else if(a<E_3) { // Box 09
      Box11();
    }
  }
  private void Stage() {
    if(a<E_4) {//Box18
      //SECO has occurred
      PointE();
    } else {
      if(t_[1]<t) {//Box19
        Box20();
      }
      PointA();
    }
  }
  private void Steering() {
    Box27();
  }
  private void Sustainer() {
    Box21();
    Box22();
  }
  private void Centaur() {
    Box24();
    Box25();
    PointU();
  }
  private void PointA() {
    Box16();
    if(N3==1) {
      Sustainer();
    } else {
      Centaur();
    }
  }
  private void PointB() {
    Box03();  //Navigate
    if(N1==1) { //Box 04
      Booster();
    } else {
      PointC();
    }
  }
  private void PointC() {
    Box12();
    Box13();
    if(j==1) {//Box14
      Stage();
    } else if (eps<E_c) {//Box15
      Steering();
    } else {
      PointA();
    }
  }
  private void PointE() {
    Box23();
    PointA();
  }
  private void PointU() {
    Box26();
  }
  private void Box01() {
    //Initialization
    //  Miscelaneous constants
    E_c=0.17e9*0.3048*0.3048; //m^2/s^2, energy-to-go at which calculation of steering constants is terminated 0.17e9 in ft^2/s^2
    g=9.80665;                //m/s^2, Standard gravitional acceleration, NOT gravity at point where spacecraft is, 32.174 in ft/s^2
    mu=3.986004418e14;        //m,sEarth gravitational constant, 1.4076539e16 in ft,s
    Re=6371010;               //m, radius of the Earth
    //  Sustainer
    t_s=235;     //s, estimated sustainer total burn time
    v_e[1]=3009; //m/s, Sustainer effective velocity, 9745.5456 ft/s
    d_4[1]=0;
    fd_h[1]=0;
    d_2[1]=1e-7;
    //  Centaur
    v_e[2]=4354;     //m/s, Centaur effective velocity, 13818.666ft/s
    a_[2]=8;         //m/s^2, Estimate of initial Centaur acceleration, 26.4606ft/s^2
    tau_[2]=522.236; //s, Time to burn entire stage and payload mass as if it were fuel
    d_4[2]=0;
    fd_h[2]=0;
    t_[2]=430; //s, Estimate of Centaur total burn time

  }
  private void Box02() {
    //Steering
    f_r[1]=0.5;
    fd_r[1]=-1e-3;
    A=-0.4;
    B=0.0036;
    K=0;
    // Target
    z_d=196399; //m, Initial guess of height at cutoff
    r_d=z_d+Re; //m, Initial guess of radius at cutoff, 2.157e7ft
    z_p=185000; //m, periapse height of target orbit
    r_p=z_p+Re; //m, periapse radius of target orbit
    TargetMoon(new MathVector(-192689818.8801,
                                -2273264.005939,
                              -326826670.6027),
               65.0*3600.0);
  }
  private void Box03() {
    //Navigation Equations
    //(Integration of trajectory)
    //Calculate current MET
    //calculate rvec and r, position vector and magnitude
    //calculate vvec and v, velocity vector and magnitude

    a=thrust/mass; //current acceleration due to thrust
  }
  private void Box07() {
    N2=2; //N2=1 in original doc. Typo?
  }
  private void Box10() {
    N2=3;
  }
  private void Box11() {
    N1=2;
    t_[1]=t_s-t;
    t_last=t;
    j=1;
  }
  private void Box12() {
    //Coordinate basis vectors
    hvec=MathVector.cross(rvec,vvec);
    rhat=rvec.normal();
    hhat=hvec.normal();
    thetahat=MathVector.cross(rhat,hhat);
  }
  private void Box13() {
    //Radial velocity
    rdot=MathVector.dot(rhat,vvec);
    E=MathVector.dot(vvec,vvec)-2*mu/r;
    eps=E_d-E;
    double stheta=MathVector.dot(thetahat,Rhat_T);
    double ctheta=MathVector.dot(rhat,Rhat_T);
    theta=atan2(stheta,ctheta);
    G=(mu-h*h/r)/(r*r);
    a_[j]=a;
  }
  private void Box16() {
    //Update constants
    d_3[j]=h*rdot/(r*r*r);
    d_1[j]=r*sin(theta)/h;
    tau_[j]=v_e[j]/a_[j];
    f_r[j]=f_r[j]+fdot_r*Deltat;
    A=A+B*Deltat;
    f_h[j]=K*d_1[j];
    f_theta[j]=1-((f_r[j]*fdot_r+f_h[j]*fdot_h[j])/2);
    fdot_theta[j]=-(f_r[j]*fdot_r+f_h[j]*fdot_h[j]);
    fdotdot_theta[j]=-(fdot_r*fdot_r+fdot_h[j]*fdot_h[j])/2;
    t_[j]=t_[j]*Deltat;
    t_last=t;
  }
  private void Box20() {
    t_[1]=1;
  }
  private void Box21() {
    Deltav[1]=-v_e[1]*log(1-t_[1]/tau_[1]);
    b_0[1]=Deltav[1];
    b_1[1]=b_0[1]*tau_[1]-v_e[1]*t_[1];
    b_2[1]=b_1[1]*tau_[1]-v_e[1]*t_[1]*t_[1]/2;
    c_0[1]=-b_1[1]+b_0[1]*t_[1];
    c_1[1]=c_0[1]*tau_[1]-v_e[1]*t_[1]*t_[1]/2;
    c_1[1]=c_1[1]*tau_[1]-v_e[1]*t_[1]*t_[1]*t_[1]/6;
  }
  private void Box22() {
    //Estimate sustainer cutoff conditions
    Deltar_[1]=rdot*t_[1]+c_0[1]*A+c_1[1]*B;
    Deltardot_[1]=b_0[1]*A+b_1[1]*B;
    r_[1]=r+Deltar_[1];
    rdot_[1]=rdot+Deltardot_[1];
    Deltah_[1]=(r+r_[1])/2*(f_theta[1]*b_0[1]+fdot_theta[1]*b_1[1]+fdotdot_theta[1]*b_2[1]);
    h_[1]=h+Deltah_[1];
    d_3[2]=rdot_[1]*h_[1]/(r_[1]*r_[1]*r_[1]);
    d_4[1]=(d_3[2]-d_3[1])/t_[1];
    Deltatheta_[1]=h/(r*r)*t_[1]+2/(r+r_[1])*(f_theta[1]*c_0[1]+fdot_theta[1]*c_1[1]+fdotdot_theta[1]*c_2[1])-t_[1]*t_[1]*(d_3[1]+d_4[1]*t_[1]/3);
    d_1[2]=sin(theta-Deltatheta_[1])*r_[1]/h_[1];
    d_2[1]=(d_1[2]-d_1[1])*t_[1];
    G_[1]=(mu-h_[1]*h_[1]/r)/(r*r);
    a_f[1]=a_[1]/(1-t_[1]/tau_[1]);
    //Steering constant discontinuities
    DeltaA=G_[1]*(1/a_f[1]-a_[2]);
    DeltaB=G_[1]*(1/v_e[2]-1/v_e[1])+(3*h_[1]*h_[1]/r_[1]-2*mu)*rdot_[1]/(r_[1]*r_[1]*r_[1])*(1/a_f[1]-1/a_[2]);
    //Centaur constants
    f_r[2]=A+B*t_[1]+DeltaA+G_[1]/a_[2];
    f_h[2]=K*d_1[2];
    f_theta[2]=1-(f_r[2]*f_r[2]+f_h[2]*f_h[2])/2;
    fdot_theta[2]=-(f_r[2]*fdot_r+f_h[2]*fdot_h[2]);
    fdotdot_theta[2]=-(fdot_r*fdot_r+fdot_h[2]*fdot_h[2]);
  }
  private void Box23() {
    //Initialization for Centaur phase
    j=2;
    N3=2;
    A=A+DeltaA;
    B=B+DeltaB;
    DeltaA=0;
    DeltaB=0;
    t_[1]=0;
    b_0[1]=0;
    b_1[1]=0;
    b_2[1]=0;
    c_0[1]=0;
    c_1[1]=0;
    c_2[1]=0;
    Deltah_[1]=0;
    Deltar_[1]=0;
    Deltatheta_[1]=0;
  }
  private void Box24() {
    Deltah=2*(h_d-h-Deltah_[1])/(r_d+r+Deltar_[1]);
    DeltaV=(Deltah+v_e[2]*t_[2]*(fdot_theta[2]+fdotdot_theta[2]*(tau_[2]+t_[2]/2)))/((fdotdot_theta[2]*tau_[2]+fdot_theta[2])*tau_[2]+f_theta[2]);
    t_[2]=tau_[2]*(1-exp(-DeltaV/v_e[2]));
    b_0[2]=DeltaV;
    b_1[2]=b_0[2]*tau_[2]-v_e[2]*t_[2];
    b_2[2]=b_1[2]*tau_[2]-v_e[2]*t_[2]*t_[2]/2;
    c_0[2]=-b_1[2]+b_0[2]*t_[2];
    c_1[2]=c_0[2]*tau_[2]-v_e[2]*tau_[2]*tau_[2]/2;
    c_2[2]=c_1[2]*tau_[2]-v_e[2]*tau_[2]*tau_[2]*tau_[2]/6;
    Deltatheta_[2]=(h+Deltah_[1])/pow(r+Deltar_[1],2)*t_[2]+
                   2/(r_d+r+Deltar_[1])*(f_theta[2]*c_0[2]+fdot_theta[2]*c_1[2]+fdotdot_theta[2]*c_2[2])-
                   t_[2]*t_[2]*(d_3[2]+d_4[2]*t_[2]/3);
    eta_d=eta_T+Deltatheta_[1]+Deltatheta_[2]-theta;
    rdot_d=sqrt(mu/p)*e*sin(eta_d);
    r_d=p/(1+e*cos(eta_d));
    d_4[2]=(h_d*rdot_d/(rdot_d*rdot_d*rdot_d)-d_3[2])/t_[2];
    d_2[2]=(r_d*sin(theta-Deltatheta_[1]-Deltatheta_[2])/h_d-d_1[2])/t_[2];
  }
  private void Box25() {
    Deltardot=rdot_d-rdot-b_0[2]*DeltaA-b_1[2]*DeltaB;
    Deltar=r_d-r-rdot*(t_[1]+t_[2])-c_0[2]*DeltaA-c_1[2]*DeltaB;
    eps_h=MathVector.dot(hhat, Rhat_T);
    Delta_[1]=d_1[1]*d_1[1]*b_0[1]+d_1[2]*d_1[2]*b_0[2]+d_2[1]*(2*d_1[1]*b_1[1]+d_2[1]*b_2[1])+
              d_2[2]*(2*d_1[2]*b_1[2]+d_2[2]*b_2[2]);
    K=eps_h/Delta_[1];
    alpha=b_0[1]+b_0[2];
    beta =b_1[1]+b_1[2]+b_0[2]*t_[1];
    gamma=c_0[1]+c_0[2]+b_0[1]*t_[2];
    delta=c_1[1]+c_1[2]+b_1[1]*t_[2]+c_0[2]*t_[1];
    Delta_[2]=alpha*delta-beta*gamma;
    B=(alpha*Deltar-gamma*Deltardot)/Delta_[2];
    A=(Deltardot-beta*B)/alpha;
  }
  private void Box26() {
    G_f=(mu-h_d*h_d/r_d)/r_d;
    a_f[2]=a_[2]/(1-t_[2]/tau_[2]);
    f_rf=A+B*(t_[1]+t_[2])+DeltaA+t_[2]*DeltaB+G_f/a_f[2];
    fdot_h[1]=K*d_2[1];
    fdot_h[2]=K*d_2[2];
  }
  private void Box27() {
    f_r[j]=A+G/a_[j]+B*(t-t_last);
    f_h[j]=d_1[j]*K;
    f_theta[j]=sqrt(1-f_r[j]*f_r[j]-f_h[j]*f_h[j]);
    fhat=MathVector.add(MathVector.add(rhat.mul(f_r[j]),thetahat.mul(f_theta[j])),hhat.mul(f_h[j]));
    fdot_r=(f_rf-f_r[j])/(t_[1]+t_[2]);
  }
  public static void main(String[] args) {
    PEGAutopilot PEG=new PEGAutopilot();
    PEG.init();
  }
}
