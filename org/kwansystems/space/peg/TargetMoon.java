package org.kwansystems.space.peg;

import org.kwansystems.tools.vector.*;
import static java.lang.Math.*;

public class TargetMoon {
  @VariableDesc(desc="Unit vector pointing from center of Earth to target at target intercept time")
  protected MathVector rvec_m;
  @VariableDesc(desc="Unit vector pointing from center of Earth to target at target intercept time")
  protected MathVector rhat_m;
  @VariableDesc(desc="Target intercept radius",units="m")
  protected double r_m;
  @VariableDesc(desc="Target periapse radius",units="m")
  protected double r_p;
  @VariableDesc(desc="Target apoapse radius",units="m")
  protected double r_a;
  @VariableDesc(desc="Target conic parameter",units="m")
  protected double p;
  @VariableDesc(desc="Target eccentricity",units="")
  protected double e;
  @VariableDesc(desc="Target semimajor axis",units="m")
  protected double a_m;
  @VariableDesc(desc="True anomaly on target orbit at target intercept",units="rad")
  protected double eta_m;
  @VariableDesc(desc="Eccentric anomaly on target orbit at target intercept",units="rad")
  protected double E_m;
  @VariableDesc(desc="Mean anomaly on target orbit at target intercept",units="rad")
  protected double M_m;
  @VariableDesc(desc="Mean motion on target orbit",units="rad/s")
  protected double n;
  //Time of flight from periapse to true anomaly eta_m (field) in gravity mu (field) and periapse r_p (field), given parameter p
  public double t_flight(Autopilot a, double p) {
    e=p/r_p-1;
    r_a=p/(1-e);
    a_m=(r_a+r_p)/2;
    eta_m=acos(p/(e*r_m)-1/e);
    if(e==1.0) {
      //Parabolic case
      E_m=sqrt(p)*tan(eta_m/2);  //Parabolic eccentric anomaly
      M_m=(p*E_m-E_m*E_m*E_m/3); //Parabolic mean anomaly
      n=1/(2*sqrt(a.mu));          //Parabolic mean motion
    } else if(e<1) {
      E_m=2*atan(sqrt((1-e)/(1+e))*tan(eta_m/2));
      M_m=E_m-e*sin(E_m);
      n=sqrt(a.mu/(a_m*a_m*a_m));
    } else {
      double coshE_m=(e+cos(eta_m))/(1+e*cos(eta_m));
      E_m=acosh(coshE_m);
      M_m=e*sinh(E_m)-E_m;
      n=sqrt(-a.mu/(a_m*a_m*a_m));
    }
    return M_m/n;
  }
  public static double asinh(double x) {
    return log(x+sqrt(x*x+1));
  }
  public static double acosh(double x) {
    return log(x+sqrt(x*x-1));
  }
  public void TargetMoon(Autopilot a, double Lz_p, MathVector Lrvec_m, double Lt_target) {
    a.z_p=Lz_p;
    r_p=a.z_p+a.Re;
    rvec_m=Lrvec_m;
    a.t_target=Lt_target;
    System.out.printf("\"\",p_min,p_max,p,a_m,e,t_target=%19.14e\n",a.t_target);
    r_m=rvec_m.length();
    rhat_m=rvec_m.normal();
    double a_min=(r_m+r_p)/2;
    double e_min=(r_m-r_p)/(r_m+r_p);
    double p_min=a_min*(1-e_min*e_min);
    if(a.t_target>t_flight(a,p_min)) throw new IllegalArgumentException("Time of flight is too long and cannot be satisfied on a single outbound leg");
    double p_max=p_min;
    do {
      p_max*=2;
    } while(t_flight(a,p_max)>a.t_target);
    double eps=p_min*1e-8;
    while((p_max-p_min)>eps) {
      p=(p_max+p_min)/2;
      if(t_flight(a,p)<a.t_target) {
        p_max=p;
      } else {
        p_min=p;
      }
    }
  }

}
