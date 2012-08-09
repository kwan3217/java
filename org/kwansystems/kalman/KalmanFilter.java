package org.kwansystems.kalman;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;
import static java.lang.Math.*;

public class KalmanFilter {
  public MathVector xh;
  public MathMatrix P;
  public KalmanFilter(MathVector Lxh, MathMatrix LP) {
    xh=Lxh;
    P=LP;
  }

  public void filterUpdate(MathVector z, MathMatrix A, MathMatrix H, MathMatrix Q, MathMatrix R) {
    MathVector xhm=A.mul(xh);
    MathMatrix Pm=A.mul(P).mul(A.T()).add(Q);
    MathMatrix K=Pm.mul(H.T()).mul(H.mul(Pm).mul(H.T()).add(R).inv());
    xh=xhm.add(K.mul(z.sub(H.mul(xhm))));
    P=Pm.sub(K.mul(H).mul(Pm));
  }

/** kalman_velocity - simple driver for the Kalman fiter or smoother
 Input
  @param z measurements, in any physical or engineering unit. Should be a 1D array.
; @param t time stamp for each measurement, in some uniform time scale (such as TAI
;     or hours since some epoch). Should be a 1D array with the same number of
;     elements as z
; @param sigmav - velocity process noise, used for tuning the filter
; @param sigmaz - measurement noise, not a tuning parameter
; @param xh0 (Optional) - Two-element array, a priori (initial guess) of process state.
;     First element is estimate of initial position, second is of initial velocity.
;     [0d,0d] by default.
; @param P0 (Optional) - Two-by-two matrix, a priori covariance of process state.
;    [1,0]
;    [0,1] by default.
; @return an array of 6-element vectors. Elements 0 and 1 match xh_filt[0] and [1]
;                                        Elements 2-5 contain elemnts [0,0],[0,1],[1,0],[1,1] of P
;Notes:
;  Kalman Filter and Smoother rely on a linear process model of the following form:
;  x[i]=A##x[i-1]+w[i]
;  z[i]=H##x[i]+v[i]
;  A is the state transition matrix which describes the process and is used to calculate
;    the next state x from the previous. w is unknwn process noise with a covariance of Q
;  H is the observation matrix which describes how the observation is calculated from the
;    state. v is unknown measurement noise with a covariance of R.
;
;  This function is hard-coded to use the velocity (cart on rails) model. To supply your
;  own matrices, use kalman_filt or kalman_smooth directly. In this model, the A matrix is
;  [1,delta_T]
;  [0,      1] where delta_T is the time between measurements (If delta_T varies, so does the
;  A matrix, but this code handles all of this properly and transparently). The
;  H matrix is [1,0] which signifies that the measurement includes position but not velocity
;  information.
;
;  The process noise covariance Q is
;  [dt^4/4, dt^3/2]
;  [dt^3/2, dt^2  ]*sigma_v^2 where sigmav is the velocity process noise standard deviation, supplied on
;  input. This parameter allows the model the "wiggle room" it needs to follow unmodeled
;  dynamics. The lower the sigmav, the more the filter will trust the process model, and
;  result in a smoother output (more noise filtered out) at the expense of slower reaction
;  time to unmodeled process dynamics.
;
;  The measurement noise covariance R is sigmaz^2. This is presumed constant for all
;  measurements. You should know or have a realistic idea of your measurement noise,
;  and should apply it as best you know and not use this as a tuning parameter.
;
;  On output, you get two estimates of the state and two of the uncertainty of the state,
;  one for the filter and one for the smoother, as described above. The output uncertainty
;  in each case is expressed as a covariance matrix as follows:
;  [sigma_x0^2, sigma_x0*sigma_x1*rho_x0x1]
;  [sigma_x0*sigma_x1*rho_x0x1, sigma_x1^2]
;  You can pull the 1-sigma uncertainty off of the diagonals, but if the correlation is too
;  high, this isn't really very meaningful any more. sigma_x0=sqrt(p[0,0]) and sigma_x1=sqrt(p[1,1])
;
;  There is a bunch of code after the return statement which you don't need but shows an example of
;  how to get the data out of the structure and plot it, particularly the 1-sigma uncertainties.
;  Since it is after the return statement, it never gets executed.
 */
  public static MathVector[] kalman_velocity(double[] z, double[] t, double sigmav, double sigmaz, MathVector xh0, MathMatrix P0) {
    //Matrix constants
    MathMatrix H=new MathMatrix(new double[][] {{1d,0}});

    //Histories
    MathVector[] xh_filt=new MathVector[z.length]; //Estimated state
    MathMatrix[] P_filt=new MathMatrix[z.length];

    //A priori state estimate
    if(xh0==null) xh0=new MathVector(z[0],0);
    //A priori estimate covariance
    if(P0==null) P0 =new MathMatrix(new double[][] {{1d,0d},{0d,1d}});

    xh_filt[0]=xh0;
    P_filt[0]=P0;
    KalmanFilter F=new KalmanFilter(xh0,P0);

    for(int i=1;i<z.length;i++) {
      double dt=t[i]-t[i-1];
      MathMatrix A=new MathMatrix(new double[][] {{1d,dt},{0,1}});
      MathMatrix Q=new MathMatrix(new double[][] {{pow(dt,4)/4d,pow(dt,3)/2d},{pow(dt,3)/2,pow(dt,2)}}).mul(sigmav); //scale-free process noise model
      MathMatrix R=new MathMatrix(new double[][] {{sigmaz}});
      F.filterUpdate(new MathVector(new double[] {z[i]}),A,H,Q,R);
      xh_filt[i]=F.xh;
      P_filt[i]=F.P;
    }
    return xh_filt;
  }
}
