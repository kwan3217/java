package org.kwansystems.tools;

/** Given the coefficients for the Chebyshev expansion of a
    polynomial, this returns the value of the polynomial and its
    derivative evaluated at the input X.

 Disclaimer

      THIS SOFTWARE AND ANY RELATED MATERIALS WERE CREATED BY THE
      CALIFORNIA INSTITUTE OF TECHNOLOGY (CALTECH) UNDER A U.S.
      GOVERNMENT CONTRACT WITH THE NATIONAL AERONAUTICS AND SPACE
      ADMINISTRATION (NASA). THE SOFTWARE IS TECHNOLOGY AND SOFTWARE
      PUBLICLY AVAILABLE UNDER U.S. EXPORT LAWS AND IS PROVIDED "AS-IS"
      TO THE RECIPIENT WITHOUT WARRANTY OF ANY KIND, INCLUDING ANY
      WARRANTIES OF PERFORMANCE OR MERCHANTABILITY OR FITNESS FOR A
      PARTICULAR USE OR PURPOSE (AS SET FORTH IN UNITED STATES UCC
      SECTIONS 2312-2313) OR FOR ANY PURPOSE WHATSOEVER, FOR THE
      SOFTWARE AND RELATED MATERIALS, HOWEVER USED.

      IN NO EVENT SHALL CALTECH, ITS JET PROPULSION LABORATORY, OR NASA
      BE LIABLE FOR ANY DAMAGES AND/OR COSTS, INCLUDING, BUT NOT
      LIMITED TO, INCIDENTAL OR CONSEQUENTIAL DAMAGES OF ANY KIND,
      INCLUDING ECONOMIC DAMAGE OR INJURY TO PROPERTY AND LOST PROFITS,
      REGARDLESS OF WHETHER CALTECH, JPL, OR NASA BE ADVISED, HAVE
      REASON TO KNOW, OR, IN FACT, SHALL KNOW OF THE POSSIBILITY.

      RECIPIENT BEARS ALL RISK RELATING TO QUALITY AND PERFORMANCE OF
      THE SOFTWARE AND ANY RELATED MATERIALS, AND AGREES TO INDEMNIFY
      CALTECH AND NASA FOR ALL THIRD-PARTY CLAIMS RESULTING FROM THE
      ACTIONS OF RECIPIENT IN THE USE OF THE SOFTWARE.

*/
public class Chebyshev {
 /**Interpolate a Chebyshev expansion.
  *
  * @param CP NDEG+2 Chebyshev polynomial coefficients, one-based so zero element is ignored. CP is an array of coefficients OF a polynomial with
                 respect to the Chebyshev basis.  The polynomial to be
                 evaluated is assumed to be of the form:
<code>
 CP(DEGP+1)*T(DEGP,S) + CP(DEGP)*T(DEGP-1,S) + ... + CP(2)*T(1,S) + CP(1)*T(0,S)
</code>
                 where <code>T(I,S)</code> is the I'th Chebyshev polynomial
                 evaluated  at a number <code>S</code> whose double precision
                 value lies between -1 and 1.  The value of <code>S</code> is
                 computed from the input variables <code>X2S(1), X2S(2)</code> and <code>X</code>

  * @param DEGP is the degree of the Chebyshev polynomial to be
                 evaluated.
  * @param X2S Transformation parameters of polynomial, one-based so three elements, zero element ignored
  *              <code>X2S</code> is an array of two parameters.  These parameters are
                 used to transform the domain of the input variable <code>X</code>
                 into the standard domain of the Chebyshev polynomial.
                 <code>X2S[1]</code> should be a reference point in the domain of <code>X</code>;
                 <code>X2S[2]</code> should be the radius by which points are
                 allowed to deviate from the reference point and while
                 remaining within the domain of <code>X</code>.  The value of
                 <code>X</code> is transformed into the value <code>S</code> given by
<code>
S = ( X - X2S(1) ) / X2S(2)
</code>

                 Typically <code>X2S(1)</code> is the midpoint of the interval over
                 which <code>X</code> is allowed to vary and <code>X2S(2)</code> is the radius of
                 the interval.

                 The main reason for doing this is that a Chebyshev
                 expansion is usually fit to data over a span
                 from <code>A</code> to <code>B</code> where <code>A</code> and <code>B</code> are not -1 and 1
                 respectively.  Thus to get the "best fit" the
                 data was transformed to the interval [-1,1] and
                 coefficients generated. These coefficients are
                 not rescaled to the interval of the data so that
                 the numerical "robustness" of the Chebyshev fit will
                 not be lost. Consequently, when the "best fitting"
                 polynomial needs to be evaluated at an intermediate
                 point, the point of evaluation must be transformed
                 in the same way that the generating points were
                 transformed.

  * @param X   Value for which the polynomial is to be evaluated.
  * @return
  *   3-element array, zero element blank, First element is P, value of the polynomial,
  *   Second is DPDX, derivative of P with respect to X.
  *   <code>P</code> is the value of the polynomial to be evaluated.  It
                 is given by
<code>
CP(DEGP+1)*T(DEGP,S) + CP(DEGP)*T(DEGP-1,S) + ... + CP(2)*T(1,S) + CP(1)*T(0,S)
</code>
                 where <code>T(I,S)</code> is the I'th Chebyshev polynomial
                 evaluated  at a number <code>S = ( X - X2S(1) )/X2S(2)</code>

      DPDX       is the value of the derivative of the polynomial at X.
                 It is given by

                   1/X2S(2) [    CP(DEGP+1)*T'(DEGP,S)
                               + CP(DEGP)*T'(DEGP-1,S) + ...
                               .
                               .
                               .
                           ... + CP(2)*T'(1,S)
                               + CP(1)*T'(0,S) ]

                 where T(I,S) and T'(I,S)  are the I'th Chebyshev
                 polynomial and its derivative, respectively,
                 evaluated  at a number S = ( X - X2S(1) )/X2S(2)

  */
  public static double[] CHBINT (double[] CP, int DEGP, double[] X2S, double X) {
/*
C     Depending upon the user's needs, there are 3 routines available
C     for evaluating Chebyshev polynomials.
C
C        CHBVAL for evaluating a Chebyshev polynomial when no
C               derivatives are desired.
C
C        CHBINT for evaluating a Chebyshev polynomial and its
C               first derivative.
C
C        CHBDER for evaluating a Chebyshev polynomial and a user
C               or application dependent number of derivatives.
C
C     Of these 3 the one most commonly employed by NAIF software
C     is CHBINT as it is used to interpolate ephemeris state
C     vectors which requires the evaluation of a polynomial
C     and its derivative.  When no derivatives are desired one
C     should use CHBVAL, or when more than one or an unknown
C     number of derivatives are desired one should use CHBDER.
C
C     The code fragment below illustrates how this routine might
C     be used to obtain points for plotting a polynomial
C     and its derivatives.
C
C           fetch the pieces needed for describing the polynomial
C           to be evaluated.
C
C           READ  (*,*) DEGP, ( CP(I), I = 1, DEG+1 ),  BEG, END
C
C           check to see that BEG is actually less than END
C
C           IF ( BEG .GE. END ) THEN
C
C              take some appropriate action
C
C           ELSE
C
C              X2S(1) = ( END + BEG ) / 2.0D0
C              X2S(2) = ( END - BEG ) / 2.0D0
C
C           END IF
C
C           STEP = END - BEG / <number of points used for plotting>
C           X    = BEG
C
C           DO WHILE ( X .LE. END )
C
C              CALL CHBINT ( CP, DEGP, X2S, X, P, DPDX )
C
C              do something with the pairs (X,P) and (X,DPDX)
C
C              X = X + STEP
C
C           END DO
C
C$ Restrictions
C
C      One needs to be careful that the value (X-X2S(1)) / X2S(2) lies
C      between -1 and 1.  Otherwise, the routine may fail spectacularly
C      (for example with a floating point overflow).
C
C$ Exceptions
C
C     Error free
C
C     No tests are performed for exceptional values (DEGP negative,
C     etc.) This routine is expected to be used at a low level in
C     ephemeris evaluations. For that reason it has been elected as a
C     routine that will not participate in error handling.
C
C$ Files
C
C      None.
C
C$ Author_and_Institution
C
C      W.L. Taber      (JPL)
C
C$ Literature_References
C
C      "Numerical Recipes -- The Art of Scientific Computing" by
C       William H. Press, Brian P. Flannery, Saul A. Teukolsky,
C       Willam T. Vetterling.  (See Clenshaw's Recurrance Formula)
C
C      "The Chebyshev Polynomials" by Theodore J. Rivlin
C
C      "CRC Handbook of Tables for Mathematics"
C
C$ Version
C
C-    SPICELIB Version 1.0.1, 10-MAR-1992 (WLT)
C
C        Comment section for permuted index source lines was added
C        following the header.
C
C-    SPICELIB Version 1.0.0, 31-JAN-1990 (WLT)
C
C-&
*/

/*
     Local variables
 */
    int J;
    double[] W=new double[3+1];
    double[] DW=new double[3+1];
    double S;
    double S2;
/*
     Transform X to S and initialize temporary variables.
 */
    S     = (X-X2S[1]) / X2S[2];
    S2    = 2.0 * S;
    J     = DEGP  + 1;
    W[1] = 0.0;
    W[2] = 0.0;
    DW[1] = 0.0;
    DW[2] = 0.0;
/*
      Evaluate the polynomial and its derivative using recursion.
 */
    while( J > 1 ) {
      W[3] = W[2];
      W[2] = W[1];
      W[1] = CP[J]  + ( S2*W[2]  - W[3] );

      DW[3] = DW[2];
      DW[2] = DW[1];
      DW[1] = W[2]*2.0 + DW[2]*S2 - DW[3];

      J     = J - 1;
    }

    double P    = CP[1] + ( S*W[1] - W[2] );
    double DPDX =  W[1] + S*DW[1] - DW[2];
/*
      Scale the derivative by 1/X2S(2) so that we have the derivative

                       d P(S)
                       ------
                         dX
 */
    DPDX = DPDX / X2S[2];
    return new double[] {0,P,DPDX};
  }

}
