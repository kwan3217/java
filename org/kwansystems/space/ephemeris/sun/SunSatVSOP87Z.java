package org.kwansystems.space.ephemeris.sun;

import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;
import java.io.*;

import static java.lang.Math.*;

/**
 * This implements theory zero, heliocentric equinoctal elements J2000
 */
public class SunSatVSOP87Z extends SunSatVSOP87 {
  public SunSatVSOP87Z(String LSuffix, double Lprec) {
  	super(' ',LSuffix,Lprec);
  }
  public SunSatVSOP87Z(String LSuffix) {
	  this(LSuffix,0);
  }
  public SunSatVSOP87Z(int LWorld, double Lprec) {
	  super(' ',LWorld,Lprec);
  }
  public SunSatVSOP87Z(int LWorld) {
	  this(LWorld,0);
  }
  private static double calcpsi(double h, double k) {
    return 1.0/(1+sqrt(1-h*h-k*k));
  }
/** Solves the equinoctial version of Kepler's equation.
 * @param ML Mean longitude (=Mean anomaly+argument of periapse+longitude of asc node) in radians
 * @param H  Equinoctal element h=e*sin(arg periapse+lon asc node)
 * @param K  Equinoctal element h=e*cos(arg periapse+lon asc node)
 * @return A value F such that ML = F + h*COS(F) - k*SIN(F). Here F is an offset
 *         from the eccentric anomaly E:  F = E - argument of periapse - longitude of ascending node.
 * [This matches the form in VSOP87 where we need to solve
 *  Eps-k*sin(Eps)+h*cos(Eps)=lambda . Eps in VSOP87 matches F here. Note that h and k correspond.
*/
  private static double KEPLEQ(double ML, double H, double K) {
    double[] EVEC=new double[2];
    double E2 = H*H + K*K;

    if( E2>=0.81 ) throw new IllegalArgumentException(String.format(
      "The values of H and K supplied to KEPLEQ\n"+
      "must satisfy the inequality H*H + K*K <\n"+
      "ECC**2 where ECC is the eccentricity\n"+
      "threshold of 0.9.  The values of H and K\n"+
      "are: %f and %f respectively. H*H + K*K =\n"+
      "%f. ",H,K,E2));

    /*
      Instead of solving the equation
            ML  = F + H*DCOS(F) - K*DSIN(F)
      We set X equal to F - ML and solve the equivalent equation

            0   = X + H*DCOS(ML+X) - K*DSIN(ML+X)

                = X + H*{DCOS(ML)*DCOS(X) - DSIN(ML)*DSIN(X)}
                    - K*{DSIN(ML)*DCOS(X) + DCOS(ML)*DSIN(X)}

                = X + { H*DCOS(ML) - K*DSIN(ML) }*DCOS(X)
                    - { H*DSIN(ML) + K*DCOS(ML) }*DSIN(X)


     We can rearrange this to:

                                 -                    -     -       -
                                |  DCOS(ML)  -DSIN(ML) |   | DCOS(X) |
            0 = X + [ H  -K ] * |  DSIN(ML)   DCOS(ML) | * | DSIN(X) |
                                 -                    -     -       -

     Finally if we let

C                                       -                    -
                                       |  DCOS(ML)  -DSIN(ML) |
      EVEC =  [ EX  EY ] = [ -H  K ] * |  DSIN(ML)   DCOS(ML) |
                                        -                    -

     and

              DCOS(X)
      U(X) =  DSIN(X)

     Then we can rewrite the equation as:

        0  =  X - < EVEC, U(X) >

     where <,> denotes the dot product operation.  Note that X
     is necessarily in the range from -ECC to ECC where ECC = | EVEC |

     Once we've computed X, F is just ML + X.

     For those of you who are fans of the classical keplerian
     elements:

        x = F - ML = E - M

     where E denotes eccentric anomaly and M denotes mean anomaly.

     The routine KPEVEC returns the value of X that solves
     the equation X - < EVEC, UVEC(X) >
*/

      EVEC[0] = -H*cos(ML) + K*sin(ML);
      EVEC[1] =  H*sin(ML) + K*cos(ML);
      return ML         + KPSOLV( EVEC ); //That's it?
  }
/** Solve Keplers Equation --- Vector Form.
*    This routine solves the equation X = dot(EVEC, U(X)) where
*    U(X) is the unit vector [ Cos(X), SIN(X) ]
* @param EVEC A 2-vector whose magnitude is less than 1.
* @return The value X such that X = EVEC[0]COS(X) + EVEC[1]SIN(X)
*
*     This routine uses bisection and Newton's method to find
*     the root of the equation
*
*        X = EVEC(1)COS(X) + EVEC(2)SIN(X).
*
*     This equation is just a "vector form" of Kepler's equation.
*
* Examples
*
*     Suppose you need to solve the equation
*
*         M = E - e SIN(E)                           [ 1 ]
*
*     for E. If we let X = E - M the equation is transformed to
*
*        0 = X - e SIN( X + M )
*
*          = X - e SIN( M ) COS(X) - e COS(M) SIN ( X )
*
*     Thus if we solve the equation
*
*        X = e SIN(M) COS(X) + e COS(M) SIN(X)
*
*     we can find the value of X we can compute E.
*
*     The code fragment below illustrates how this routine can
*     be used to solve equation [1].
*
*         EVEC[0] = ECC * DSIN(M)
*         EVEC[1] = ECC * DCOS(M)
*         E       = M   + KPSOLV( EVEC )
*
*/
  private static final double KPSOLV(double[] EVEC ) {
    /**The number of iterations we will perform
     in the Newtons method for finding the solution to
     the vector form of Kepler's equation.  It has been
     empirically determined that 5 iterations is always
     sufficient on computers have 64 bit double precision
     numbers. */
    final int MXNEWT= 5;
    double COSX, ECC, ECC2, H, K, SINX, X, XL, XM, XU, Y0, YPX, YX, YXM;
    int I,MAXIT;

    H      = EVEC[0];
    K      = EVEC[1];
    ECC2   = H*H + K*K;
    if(ECC2>1.0) throw new IllegalArgumentException(String.format(
      "The magnitude of the vector EVEC = ( %f, "+
      "%f ) must be less than 1.  However, the "+
      "magnitude of this vector is %f. ", H,K,sqrt(ECC2)
    ));
/*

     We first approximate the equation 0 = X - H * COS(X) - K * SIN(X)
     using bisection.  If we let Y(X) = X - H * COS(X) - K * SIN(X)

        Y( ECC) =  ECC - <EVEC,U(X)>  =   ECC - ECC*COS(ANGLE_X) > 0
        Y(-ECC) = -ECC - <EVEC,U(X)>  =  -ECC - ECC*COS(ANGLE_X) < 0

     where ANGLE_X is the angle between U(X) and EVEC. Thus -ECC
     and ECC necessarily bracket the root of the equation Y(X) = 0.

     Also note that Y'(X) = 1 - < EVEC, V(X) > where V(X) is the
     unit vector given by U'(X).  Thus Y is an increasing function
     over the interval from -ECC to ECC.

     The mid point of ECC and -ECC is 0 and Y(0) = -H.  Thus
     we can do the first bisection step without doing
     much in the way of computations.
*/

      Y0  = -H;
      XM  =  0.0;
      ECC =  sqrt(ECC2);

      if( Y0 > 0.0 ) {
         XU  =  0.0;
         XL  = -ECC;
      } else if ( Y0 < 0.0D ) {
         XU =  ECC;
         XL =  0.0;
      } else {
         return 0.0;
      }
/*
     Iterate until we are assured of being in a region where
     Newton's method will converge quickly.  The formula
     below was empirically determined to give good results.

*/
     MAXIT =(int)min(32, max( 1, floor(0.5+1.0/(1.0-ECC)) ) );
     for(I = 1;I<=MAXIT;I++) {

/*
        Compute the next midpoint.  We bracket XM by XL and XU just in
        case some kind of strange rounding occurs in the computation
        of the midpoint.
*/
         XM     = max( XL, min( XU, 0.5*(XL + XU) ) );

/*
        Compute Y at the midpoint of XU and XL
*/
         YXM = XM - H*cos(XM) - K*sin(XM);
/*
        Determine the new upper and lower bounds.
*/
         if ( YXM > 0.0 ) {
            XU = XM;
         } else {
            XL = XM;
         }


     }

/*
     We've bisected into a region where we can now get rapid
     convergence using Newton's method.
*/
     X   = XM;

     for(I = 1;I<=MXNEWT;I++) {

         COSX = cos(X);
         SINX = sin(X);
/*
        Compute Y and Y' at X.  Use these to get the next
        iteration for X.

        For those of you who might be wondering, "Why not put
        in a check for YX .EQ. 0 and return early if we get
        an exact solution?"  Here's why.  An empirical check
        of those cases where you can actually escape from the
        Do-loop  showed that the test YX .EQ. 0 is true
        only about once in every 10000 case of random inputs
        of EVEC.  Thus on average the check is a waste of
        time and we don't bother with it.
*/
         YX   = X     - H*COSX - K*SINX;
         YPX  = 1.0   + H*SINX - K*COSX;
         X    = X     - YX/YPX;

      }

      return X;
  }
  public MathState CalcState(double[] Parameters) {
    double a=Parameters[1];
    double lambda=Parameters[2];
    double k=Parameters[3];
    double h=Parameters[4];
    double q=Parameters[5];
    double p=Parameters[6];
    double Eps=KEPLEQ(lambda, h, k); //Kepler's equation
    double psi=calcpsi(h,k);
    double rcosw=-a*k+a*(1-h*h*psi)*cos(Eps)+a*h*k*psi*sin(Eps);
    double rsinw=-a*h+a*h*k*psi*cos(Eps)+a*(1-k*k*psi)*sin(Eps);
    return new MathState(new MathVector(
      (1-2*p*p)*rcosw+2*p*q*rsinw,
      (1-2*q*q)*rsinw+2*p*q*rcosw,
      -2*sqrt(1-p*p-q*q)*(p*rcosw-q*rsinw)
    ), new MathVector(0,0,0));
  }
  public double[] getConvElements(double[] Parameters) {
    double a=Parameters[1];
    double lambda=Parameters[2]%(2*PI);
    if(lambda<0)lambda+=2*PI;
    double k=Parameters[3];
    double h=Parameters[4];
    double q=Parameters[5];
    double p=Parameters[6];
    double e=sqrt(k*k+h*h);
    double lp=atan2(h,k);
    double sinio2=sqrt(q*q+p*p);
    double i=asin(2*sinio2);
    double lan=atan2(p,q)%(2*PI);
    if(lan<0)lan+=2*PI;
    double ap=(lp-lan)%(2*PI);
    if(ap<0)ap+=2*PI;
    double M=(lambda-lp)%(2*PI);
    if(M<0)M+=2*PI;

    return new double[] {Parameters[0],a,e,i,lan,lp,lambda,ap,M};
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    for(int w=1;w<=8;w++) {
      SunSatVSOP87Z V = new SunSatVSOP87Z(w);
      double[][] testCase=V.LoadTestCase();
      for (int i = 0; i < testCase.length; i++) {
        System.out.printf("%-9s JD%9.1f\n",worldName[0][w],testCase[i][0]*365250.0+2451545.0);
        V.CheckVariables(testCase, i);
        double[] ele=V.getConvElements(V.CalcVariables(testCase[i][0]));
        System.out.printf("  a=%16.10f  e=%16.10f      i=%16.10f\n",ele[1],ele[2],toDegrees(ele[3]));
        System.out.printf("lan=%16.10f lp=%16.10f lambda=%16.10f\n",toDegrees(ele[4]),toDegrees(ele[5]),toDegrees(ele[6]));
      }
    }
  }
}
