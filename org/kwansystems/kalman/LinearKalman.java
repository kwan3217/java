package org.kwansystems.kalman;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

public class LinearKalman {
  public MathVector xh;
  public MathMatrix P;

  void update(MathVector z, MathMatrix A, MathMatrix H, MathMatrix Q, MathMatrix R) {

    //extract the filter state

    //Time update
    MathVector xhm=A.mul(xh);             //Projected old state estimate
    MathMatrix Pm=A.mul(P).mul(A.T()).add(Q);   //Projected old estimate covariance

    //compare measurement to estimate
    MathVector yh=z.sub(H.mul(xhm));         //Measurement Residual
    MathMatrix Gamma=H.mul(Pm).mul(H.T()).add(R); //Residual covariance
    MathMatrix S=Pm.mul(H.T());
    //calculate gain
    MathMatrix K=S.mul(Gamma.inv());  //Kalman gain

    //Measurement update
    xh=xhm.add(K.mul(yh));        //New estimate of state
    P=Pm.sub(K.mul(Gamma).mul(K.T())); //new estimate covariance
  }

function kalman_velocity_step,z,dt,sigmav,sigmaz,LL,xh0,p0,state=state
  ;Matrix constants
  H=[1d,0]
  Q=[[0d,0],[0,sigmav^2]]

  ;A priori state estimate
  if n_elements(xh0) eq 0 then xh0=[[z[0]],[0]]
  ;A priori estimate covariance
  if n_elements(P0)  eq 0 then P0 =[[1d,0d],[0d,1d]]
  if n_elements(LL)  eq 0 then LL=0

  xh_filt[*,0]=xh0
  p_filt[*,*,0]=p0
  xh_smoo[*,0]=xh0
  p_smoo[*,*,0]=p0

  sz=sigmaz
  A=[[1d,dt],[0,1]]
  R=sigmaz^2
  result=kalman_filter(z,A,H,Q,R,xh0=xh0,P0=P0,state=state)
  xh_filt[*]=result.filter_xh
  p_filt[*,*]=result.filter_p

  return,{          $
    xh_filt:result.filter_xh,$
    p_filt:result.filter_p   $
  }

end

/** kalman_velocity - simple driver for the Kalman fiter or smoother
 @param z measurements, in any physical or engineering unit. Should be a 1D array.
 @param t time stamp for each measurement, in some uniform time scale (such as TAI
     or hours since some epoch). Should be a 1D array with the same number of
     elements as z
 @param sigmav velocity process noise, used for tuning the filter
 @param sigmaz measurement noise, not a tuning parameter

 Notes:
  Kalman Filter and Smoother rely on a linear process model of the following form:
  x[i]=A##x[i-1]+w[i]
  z[i]=H##x[i]+v[i]
  A is the state transition matrix which describes the process and is used to calculate
    the next state x from the previous. w is unknwn process noise with a covariance of Q
  H is the observation matrix which describes how the observation is calculated from the
    state. v is unknown measurement noise with a covariance of R.

  This function is hard-coded to use the velocity (cart on rails) model. To supply your
  own matrices, use kalman_filt or kalman_smooth directly. In this model, the A matrix is
  [1,delta_T]
  [0,      1] where delta_T is the time between measurements (If delta_T varies, so does the
  A matrix, but this code handles all of this properly and transparently). The
  H matrix is [1,0] which signifies that the measurement includes position but not velocity
  information.

  The process noise covariance Q is
  [0,       0]
  [0,sigmav^2] where sigmav is the velocity process noise standard deviation, supplied on
  input. This parameter allows the model the "wiggle room" it needs to follow unmodeled
  dynamics. The lower the sigmav, the more the filter will trust the process model, and
  result in a smoother output (more noise filtered out) at the expense of slower reaction
  time to unmodeled process dynamics.

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
*/
double[][]function kalman_velocity,z,t,sigmav,sigmaz,LL,xh0,p0,state=state

  ;Matrix constants
  H=[1d,0]
  Q=[[0d,0],[0,sigmav^2]]

  ;Histories
  xh_filt=dblarr(2,n_elements(z)) ;Estimated state
  xh_smoo=dblarr(2,n_elements(z))
  p_filt=dblarr(2,2,n_elements(z))
  p_smoo=dblarr(2,2,n_elements(z))

  ;If passed in a filter state, pull off the input
  ;state estimate and covariance as xh0 and p0
  if n_elements(state) gt 0 then begin
    xh0=state.xh
    p0=state.p
    LL=state.LL
  end

  ;A priori state estimate
  if n_elements(xh0) eq 0 then xh0=[[z[0]],[0]]
  ;A priori estimate covariance
  if n_elements(P0)  eq 0 then P0 =[[sigmaz^2,0d],[0d,sigmaz^2]]
  if n_elements(LL)  eq 0 then LL=0

  xh_filt[*,0]=xh0
  p_filt[*,*,0]=p0
  xh_smoo[*,0]=xh0
  p_smoo[*,*,0]=p0

  for i=1L,n_elements(z)-1 do begin
    dt=t[i]-t[i-1]
    if n_elements(sigmaz) gt 1 then sz=sigmaz[i] else sz=sigmaz
    A=[[1d,dt],[0,1]]
    R=sigmaz^2
    result=kalman_smooth(z[i],A,H,Q,R,LL=LL,xh0=xh0,P0=P0,state=state)
    xh_filt[*,i]=result.filter_xh
    p_filt[*,*,i]=result.filter_p
    xh_smoo[*,i]=result.filter_xh
    p_smoo[*,*,i]=result.filter_p
    if i-LL ge 1 then begin
      xh_smoo[*,i-LL]=result.smooth_xh
      p_smoo[*,*,i-LL]=result.smooth_p
    end
  end

  return,{          $
    xh_filt:xh_filt,$
    xh_smoo:xh_smoo,$
    p_filt:p_filt,  $
    p_smoo:p_smoo   $
  }
}
