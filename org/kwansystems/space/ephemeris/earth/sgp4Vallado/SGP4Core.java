package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

import static java.lang.Math.*;

import org.kwansystems.space.planet.*;

public class SGP4Core {
  private final TwoLineElement tle;
	/*     ----------------------------------------------------------------
	*
	*                                 sgp4unit.h
	*
	*    this file contains the sgp4 procedures. the code was originally
	*    released in the 1980 and 1986 papers. in 1997 and 1998, the updated and
	*    copmbined code (sgp4 and sdp4) was released by nasa on the internet.
	*                 seawifs.gsfc.nasa.gov/~seawifsp/src/bobdays/
	*
	*                            companion code for
	*               fundamentals of astrodynamics and applications
	*                                    2004
	*                              by david vallado
	*
	*       (w) 719-573-2600, email dvallado@agi.com
	*
	*    current :
	*              14 aug 06  david vallado
	*                           chg lyddane choice back to strn3, constants, fmod,
	*                           separate debug and writes, misc doc
	*    changes :
	*              26 jul 05  david vallado
	*                           fixes for paper
	*                           note that each fix is preceded by a
	*                           comment with "sgp4fix" and an explanation of
	*                           what was changed
	*              14 may 01  david vallado
	*                           2nd edition baseline
	*                     97  nasa
	*                           internet version
	*                     80  norad
	*                           original baseline
	*       ----------------------------------------------------------------      */

//	 -------------------------- structure decarations ----------------------------
	public enum gravconsttype 	{
    wgs72old(6378.135,0.0743669161,0.001082616,     -0.00000253881,   -0.00000165597,true),
    wgs72   (6378.135,398600.8,    0.001082616,     -0.00000253881,   -0.00000165597,false),
    wgs84   (6378.137,398600.5,    0.00108262998905,-0.00000253215306,-0.00000161098761,false),
                                 //0.0010826298213129214 Geometric J2 used below. Differs by 1 unit in 8th significant figure
    wgs84geo(Spheroid.WGS84.Re,Spheroid.WGS84.GM,    Spheroid.WGS84.J2,        -0.00000253215306,-0.00000161098761,false);
    public double rekm;     // km
    public double xke;
    public double mu;
    public double tumin;
    public double j2;
    public double j3;
    public double j4;
    public double j3oj2;
    private gravconsttype(double LRe,double LxkeORmu, double Lj2, double Lj3, double Lj4, boolean isxke) {
      rekm=LRe;
      if(isxke) {
        xke=LxkeORmu;
        mu=rekm*rekm*rekm*xke*xke/3600.0;
      } else {
        mu=LxkeORmu;
        xke=60.0 / sqrt(rekm*rekm*rekm/mu);
      }
      tumin=1.0/xke;
      j2=Lj2;
      j3=Lj3;
      j4=Lj4;
      j3oj2=j3/j2;
    }
	};

	  private boolean isdeep;
    private gravconsttype whichconst;

	  // Near Earth
	  private int    isimp;
	  private double aycof  , con41  , cc1    , cc4      , cc5    , d2      , d3   , d4    ,
	         delmo  , eta    , argpdot, omgcof   , sinmao , t2cof, t3cof ,
	         t4cof  , t5cof  , x1mth2 , x7thm1   , mdot   , nodedot, xlcof , xmcof ,
	         nodecf;

	//  Deep Space
	  private int    irez;
	  private double d2201  , d2211  , d3210  , d3222    , d4410  , d4422   , d5220 , d5232 ,
	         d5421  , d5433  , dedt   , del1     , del2   , del3    , didt  , dmdt  ,
	         dnodt  , domdt  , e3     , ee2      , peo    , pgho    , pho   , pinco ,
	         plo    , se2    , se3    , sgh2     , sgh3   , sgh4    , sh2   , sh3   ,
	         si2    , si3    , sl2    , sl3      , sl4    , gsto    , xfact , xgh2  ,
	         xgh3   , xgh4   , xh2    , xh3      , xi2    , xi3     , xl2   , xl3   ,
	         xl4    , xlamo  , zmol   , zmos;

	  private double a      , 
	         bstar  , inclo  , nodeo    , ecco             , argpo , mo    ,
	         no;
    
	/*     ----------------------------------------------------------------
	*
	*                               sgp4unit.cpp
	*
	*    this file contains the sgp4 procedures. the code was originally
	*    released in the 1980 and 1986 papers. in 1997 and 1998, the updated and
	*    copmbined code (sgp4 and sdp4) was released by nasa on the internet.
	*                 seawifs.gsfc.nasa.gov/~seawifsp/src/bobdays/
	*
	*                            companion code for
	*               fundamentals of astrodynamics and applications
	*                                    2004
	*                              by david vallado
	*
	*       (w) 719-573-2600, email dvallado@agi.com
	*
	*    current :
	*              14 aug 06  david vallado
	*                           chg lyddane choice back to strn3, constants, fmod,
	*                           separate debug and writes, misc doc
	*    changes :
	*              26 jul 05  david vallado
	*                           fixes for paper
	*                           note that each fix is preceded by a
	*                           comment with "sgp4fix" and an explanation of
	*                           what was changed
	*              14 may 01  david vallado
	*                           2nd edition baseline
	*                     97  nasa
	*                           internet version
	*                     80  norad
	*                           original baseline
	*       ----------------------------------------------------------------      */

//	#include "sgp4unit.h"

	/* ----------- local functions - only ever used internally by sgp4 ---------- */
 	private static class dpper_return {
    public double ep,inclp,nodep,argpp,mp;
  }
  private static class dscom_return {
    public double snodm, cnodm, sinim,  cosim, sinomm;
    public double cosomm,day,   em;
    public double emsq,  gam;
    public double rtemsq;
    public double s1,    s2,    s3,     s4,    s5;
    public double s6,    s7,    ss1,    ss2,   ss3;
    public double ss4,   ss5,   ss6,    ss7,   sz1;
    public double sz2,   sz3,   sz11,   sz12,  sz13;
    public double sz21,  sz22,  sz23,   sz31,  sz32;
    public double sz33;
    public double nm,    z1,     z2,    z3;
    public double z11,   z12,   z13,    z21,   z22;
    public double z23,   z31,   z32,    z33;
  }
  private static class dsinit_return {
    public double em,    argpm, inclm, mm;
	  public double nm,    nodem;
  }
  private static class dspace_return {
	  public double em,    argpm,  inclm;
	  public double mm,    nodem,  dndt,  nm;
  }

	/* -----------------------------------------------------------------------------
	*
	*                           procedure dpper
	*
	*  this procedure provides deep space long period periodic contributions
	*    to the mean elements.  by design, these periodics are zero at epoch.
	*    this used to be dscom which included initialization, but it's really a
	*    recurring function.
	*
	*  author        : david vallado                  719-573-2600   28 jun 2005
	*
	*  inputs        :
	*    e3          -
	*    ee2         -
	*    peo         -
	*    pgho        -
	*    pho         -
	*    pinco       -
	*    plo         -
	*    se2 , se3 , sgh2, sgh3, sgh4, sh2, sh3, si2, si3, sl2, sl3, sl4 -
	*    t           -
	*    xh2, xh3, xi2, xi3, xl2, xl3, xl4 -
	*    zmol        -
	*    zmos        -
	*    ep          - eccentricity                           0.0 - 1.0
	*    inclo       - inclination - needed for lyddane modification
	*    nodep       - right ascension of ascending node
	*    argpp       - argument of perigee
	*    mp          - mean anomaly
	*
	*  outputs       :
	*    ep          - eccentricity                           0.0 - 1.0
	*    inclp       - inclination
	*    nodep        - right ascension of ascending node
	*    argpp       - argument of perigee
	*    mp          - mean anomaly
	*
	*  locals        :
	*    alfdp       -
	*    betdp       -
	*    cosip  , sinip  , cosop  , sinop  ,
	*    dalf        -
	*    dbet        -
	*    dls         -
	*    f2, f3      -
	*    pe          -
	*    pgh         -
	*    ph          -
	*    pinc        -
	*    pl          -
	*    sel   , ses   , sghl  , sghs  , shl   , shs   , sil   , sinzf , sis   ,
	*    sll   , sls
	*    xls         -
	*    xnoh        -
	*    zf          -
	*    zm          -
	*
	*  coupling      :
	*    none.
	*
	*  references    :
	*    hoots, roehrich, norad spacetrack report #3 1980
	*    hoots, norad spacetrack report #6 1986
	*    hoots, schumacher and glover 2004
	*    vallado, crawford, hujsak, kelso  2006
   * called from sgp4 only
   * Does not write to this.*
	  ----------------------------------------------------------------------------*/

	private void dpper (double t, dpper_return result) {
	     /* --------------------- local variables ------------------------ */
	     final double twopi = 2.0 * PI;
	     double alfdp, betdp, cosip, cosop, dalf, dbet, dls,
	          f2,    f3,    pe,    pgh,   ph,   pinc, pl ,
	          sel,   ses,   sghl,  sghs,  shll, shs,  sil,
	          sinip, sinop, sinzf, sis,   sll,  sls,  xls,
	          xnoh,  zf,    zm;

	     /* ---------------------- constants ----------------------------- */
	     final double zns   = 1.19459e-5;
	     final double zes   = 0.01675;
	     final double znl   = 1.5835218e-4;
	     final double zel   = 0.05490;

	     /* --------------- calculate time varying periodics ----------- */
	     zm    = this.zmos + zns * t;
	     zf    = zm + 2.0 * zes * sin(zm);
	     sinzf = sin(zf);
	     f2    =  0.5 * sinzf * sinzf - 0.25;
	     f3    = -0.5 * sinzf * cos(zf);
	     ses   = this.se2* f2 + this.se3 * f3;
	     sis   = this.si2 * f2 + this.si3 * f3;
	     sls   = this.sl2 * f2 + this.sl3 * f3 + this.sl4 * sinzf;
	     sghs  = this.sgh2 * f2 + this.sgh3 * f3 + this.sgh4 * sinzf;
	     shs   = this.sh2 * f2 + this.sh3 * f3;
	     zm    = this.zmol + znl * t;
	     zf    = zm + 2.0 * zel * sin(zm);
	     sinzf = sin(zf);
	     f2    =  0.5 * sinzf * sinzf - 0.25;
	     f3    = -0.5 * sinzf * cos(zf);
	     sel   = this.ee2 * f2 + this.e3 * f3;
	     sil   = this.xi2 * f2 + this.xi3 * f3;
	     sll   = this.xl2 * f2 + this.xl3 * f3 + this.xl4 * sinzf;
	     sghl  = this.xgh2 * f2 + this.xgh3 * f3 + this.xgh4 * sinzf;
	     shll  = this.xh2 * f2 + this.xh3 * f3;
	     pe    = ses + sel;
	     pinc  = sis + sil;
	     pl    = sls + sll;
	     pgh   = sghs + sghl;
	     ph    = shs + shll;

	       //  0.2 rad = 11.45916 deg
	       //  sgp4fix for lyddane choice
	       //  add next three lines to set up use of original inclination per strn3 ver

	       pe    = pe - this.peo;
	       pinc  = pinc - this.pinco;
	       pl    = pl - this.plo;
	       pgh   = pgh - this.pgho;
	       ph    = ph - this.pho;
	       result.inclp = result.inclp + pinc;
	       result.ep    = result.ep + pe;
	       sinip = sin(result.inclp);
	       cosip = cos(result.inclp);

	       /* ----------------- apply periodics directly ------------ */
	       //  sgp4fix for lyddane choice
	       //  strn3 used original inclination - this is technically feasible
	       //  gsfc used perturbed inclination - also technically feasible
	       //  probably best to readjust the 0.2 limit value and limit discontinuity
	       //  use next line for original strn3 approach and original inclination
	       //  if (inclo >= 0.2)
	       //  use next line for gsfc version and perturbed inclination
	       if (result.inclp >= 0.2) {
           ph     = ph / sinip;
           pgh    = pgh - cosip * ph;
           result.argpp  = result.argpp + pgh;
           result.nodep  = result.nodep + ph;
           result.mp     = result.mp + pl;
         } else {
           /* ---- apply periodics with lyddane modification ---- */
           sinop  = sin(result.nodep);
           cosop  = cos(result.nodep);
           alfdp  = sinip * sinop;
           betdp  = sinip * cosop;
           dalf   =  ph * cosop + pinc * cosip * sinop;
           dbet   = -ph * sinop + pinc * cosip * cosop;
           alfdp  = alfdp + dalf;
           betdp  = betdp + dbet;
           result.nodep  = result.nodep % twopi;
           xls    = result.mp + result.argpp + cosip * result.nodep;
           dls    = pl + pgh - pinc * result.nodep * sinip;
           xls    = xls + dls;
           xnoh   = result.nodep;
           result.nodep  = atan2(alfdp, betdp);
           if (abs(xnoh - result.nodep) > PI) {
             if (result.nodep < xnoh) {
            	 result.nodep = result.nodep + twopi;
             } else {
            	 result.nodep = result.nodep - twopi;
             }
           }
           result.mp    = result.mp + pl;
           result.argpp = xls - result.mp - cosip * result.nodep;
         }
	}  // end dpper

	/*-----------------------------------------------------------------------------
	*
	*                           procedure dscom
	*
	*  this procedure provides deep space common items used by both the secular
	*    and periodics subroutines.  input is provided as shown. this routine
	*    used to be called dpper, but the functions inside weren't well organized.
	*
	*  author        : david vallado                  719-573-2600   28 jun 2005
	*
	*  inputs        :
	*    epoch       -
	*    ep          - eccentricity
	*    argpp       - argument of perigee
	*    tc          -
	*    inclp       - inclination
	*    nodep       - right ascension of ascending node
	*    np          - mean motion
	*
	*  outputs       :
	*    sinim  , cosim  , sinomm , cosomm , snodm  , cnodm
	*    day         -
	*    e3          -
	*    ee2         -
	*    em          - eccentricity
	*    emsq        - eccentricity squared
	*    gam         -
	*    peo         -
	*    pgho        -
	*    pho         -
	*    pinco       -
	*    plo         -
	*    rtemsq      -
	*    se2, se3         -
	*    sgh2, sgh3, sgh4        -
	*    sh2, sh3, si2, si3, sl2, sl3, sl4         -
	*    s1, s2, s3, s4, s5, s6, s7          -
	*    ss1, ss2, ss3, ss4, ss5, ss6, ss7, sz1, sz2, sz3         -
	*    sz11, sz12, sz13, sz21, sz22, sz23, sz31, sz32, sz33        -
	*    xgh2, xgh3, xgh4, xh2, xh3, xi2, xi3, xl2, xl3, xl4         -
	*    nm          - mean motion
	*    z1, z2, z3, z11, z12, z13, z21, z22, z23, z31, z32, z33         -
	*    zmol        -
	*    zmos        -
	*
	*  locals        :
	*    a1, a2, a3, a4, a5, a6, a7, a8, a9, a10         -
	*    betasq      -
	*    cc          -
	*    ctem, stem        -
	*    x1, x2, x3, x4, x5, x6, x7, x8          -
	*    xnodce      -
	*    xnoi        -
	*    zcosg  , zsing  , zcosgl , zsingl , zcosh  , zsinh  , zcoshl , zsinhl ,
	*    zcosi  , zsini  , zcosil , zsinil ,
	*    zx          -
	*    zy          -
	*
	*  coupling      :
	*    none.
	*
	*  references    :
	*    hoots, roehrich, norad spacetrack report #3 1980
	*    hoots, norad spacetrack report #6 1986
	*    hoots, schumacher and glover 2004
	*    vallado, crawford, hujsak, kelso  2006
   * called only from sgp4init
	  ----------------------------------------------------------------------------*/

	private void dscom (double epoch, dscom_return result) {
	     /* -------------------------- constants ------------------------- */
	     final double zes     =  0.01675;
	     final double zel     =  0.05490;
	     final double c1ss    =  2.9864797e-6;
	     final double c1l     =  4.7968065e-7;
	     final double zsinis  =  0.39785416;
	     final double zcosis  =  0.91744867;
	     final double zcosgs  =  0.1945905;
	     final double zsings  = -0.98088458;
	     final double twopi   =  2.0 * PI;

	     /* --------------------- local variables ------------------------ */
	     int lsflg;
	     double a1    , a2    , a3    , a4    , a5    , a6    , a7    ,
	        a8    , a9    , a10   , betasq, cc    , ctem  , stem  ,
	        x1    , x2    , x3    , x4    , x5    , x6    , x7    ,
	        x8    , xnodce, xnoi  , zcosg , zcosgl, zcosh , zcoshl,
	        zcosi , zcosil, zsing , zsingl, zsinh , zsinhl, zsini ,
	        zsinil, zx    , zy;

	     result.nm     = this.no;
	     result.em     = this.ecco;
	     result.snodm  = sin(this.nodeo);
	     result.cnodm  = cos(this.nodeo);
	     result.sinomm = sin(this.argpo);
	     result.cosomm = cos(this.argpo);
	     result.sinim  = sin(this.inclo);
	     result.cosim  = cos(this.inclo);
	     result.emsq   = result.em * result.em;
	     betasq = 1.0 - result.emsq;
	     result.rtemsq = sqrt(betasq);

	     /* ----------------- initialize lunar solar terms --------------- */
	     this.peo    = 0.0;
	     this.pinco  = 0.0;
	     this.plo    = 0.0;
	     this.pgho   = 0.0;
	     this.pho    = 0.0;
	     result.day    = epoch + 18261.5;
	     xnodce = (4.5236020 - 9.2422029e-4 * result.day)%(twopi);
	     stem   = sin(xnodce);
	     ctem   = cos(xnodce);
	     zcosil = 0.91375164 - 0.03568096 * ctem;
	     zsinil = sqrt(1.0 - zcosil * zcosil);
	     zsinhl = 0.089683511 * stem / zsinil;
	     zcoshl = sqrt(1.0 - zsinhl * zsinhl);
	     result.gam    = 5.8351514 + 0.0019443680 * result.day;
	     zx     = 0.39785416 * stem / zsinil;
	     zy     = zcoshl * ctem + 0.91744867 * zsinhl * stem;
	     zx     = atan2(zx, zy);
	     zx     = result.gam + zx - xnodce;
	     zcosgl = cos(zx);
	     zsingl = sin(zx);

	     /* ------------------------- do solar terms --------------------- */
	     zcosg = zcosgs;
	     zsing = zsings;
	     zcosi = zcosis;
	     zsini = zsinis;
	     zcosh = result.cnodm;
	     zsinh = result.snodm;
	     cc    = c1ss;
	     xnoi  = 1.0 / result.nm;

	     for (lsflg = 1; lsflg <= 2; lsflg++)
	       {
	         a1  =   zcosg * zcosh + zsing * zcosi * zsinh;
	         a3  =  -zsing * zcosh + zcosg * zcosi * zsinh;
	         a7  =  -zcosg * zsinh + zsing * zcosi * zcosh;
	         a8  =   zsing * zsini;
	         a9  =   zsing * zsinh + zcosg * zcosi * zcosh;
	         a10 =   zcosg * zsini;
	         a2  =   result.cosim * a7 + result.sinim * a8;
	         a4  =   result.cosim * a9 + result.sinim * a10;
	         a5  =  -result.sinim * a7 + result.cosim * a8;
	         a6  =  -result.sinim * a9 + result.cosim * a10;

	         x1  =  a1 * result.cosomm + a2 * result.sinomm;
	         x2  =  a3 * result.cosomm + a4 * result.sinomm;
	         x3  = -a1 * result.sinomm + a2 * result.cosomm;
	         x4  = -a3 * result.sinomm + a4 * result.cosomm;
	         x5  =  a5 * result.sinomm;
	         x6  =  a6 * result.sinomm;
	         x7  =  a5 * result.cosomm;
	         x8  =  a6 * result.cosomm;

	         result.z31 = 12.0 * x1 * x1 - 3.0 * x3 * x3;
	         result.z32 = 24.0 * x1 * x2 - 6.0 * x3 * x4;
	         result.z33 = 12.0 * x2 * x2 - 3.0 * x4 * x4;
	         result.z1  =  3.0 *  (a1 * a1 + a2 * a2) + result.z31 * result.emsq;
	         result.z2  =  6.0 *  (a1 * a3 + a2 * a4) + result.z32 * result.emsq;
	         result.z3  =  3.0 *  (a3 * a3 + a4 * a4) + result.z33 * result.emsq;
	         result.z11 = -6.0 * a1 * a5 + result.emsq *  (-24.0 * x1 * x7-6.0 * x3 * x5);
	         result.z12 = -6.0 *  (a1 * a6 + a3 * a5) + result.emsq *
	                (-24.0 * (x2 * x7 + x1 * x8) - 6.0 * (x3 * x6 + x4 * x5));
	         result.z13 = -6.0 * a3 * a6 + result.emsq * (-24.0 * x2 * x8 - 6.0 * x4 * x6);
	         result.z21 =  6.0 * a2 * a5 + result.emsq * (24.0 * x1 * x5 - 6.0 * x3 * x7);
	         result.z22 =  6.0 *  (a4 * a5 + a2 * a6) + result.emsq *
	                (24.0 * (x2 * x5 + x1 * x6) - 6.0 * (x4 * x7 + x3 * x8));
	         result.z23 =  6.0 * a4 * a6 + result.emsq * (24.0 * x2 * x6 - 6.0 * x4 * x8);
	         result.z1  = result.z1 + result.z1 + betasq * result.z31;
	         result.z2  = result.z2 + result.z2 + betasq * result.z32;
	         result.z3  = result.z3 + result.z3 + betasq * result.z33;
	         result.s3  = cc * xnoi;
	         result.s2  = -0.5 * result.s3 / result.rtemsq;
	         result.s4  = result.s3 * result.rtemsq;
	         result.s1  = -15.0 * result.em * result.s4;
	         result.s5  = x1 * x3 + x2 * x4;
	         result.s6  = x2 * x3 + x1 * x4;
	         result.s7  = x2 * x4 - x1 * x3;

	         /* ----------------------- do lunar terms ------------------- */
	         if (lsflg == 1)
	           {
	        	 result.ss1   = result.s1;
	        	 result.ss2   = result.s2;
	        	 result.ss3   = result.s3;
	        	 result.ss4   = result.s4;
	        	 result.ss5   = result.s5;
	        	 result.ss6   = result.s6;
	        	 result.ss7   = result.s7;
	        	 result.sz1   = result.z1;
	        	 result.sz2   = result.z2;
	        	 result.sz3   = result.z3;
	        	 result.sz11  = result.z11;
	        	 result.sz12  = result.z12;
	        	 result.sz13  = result.z13;
	        	 result.sz21  = result.z21;
	        	 result.sz22  = result.z22;
	        	 result.sz23  = result.z23;
	        	 result.sz31  = result.z31;
	        	 result.sz32  = result.z32;
	        	 result.sz33  = result.z33;
	             zcosg = zcosgl;
	             zsing = zsingl;
	             zcosi = zcosil;
	             zsini = zsinil;
	             zcosh = zcoshl * result.cnodm + zsinhl * result.snodm;
	             zsinh = result.snodm * zcoshl - result.cnodm * zsinhl;
	             cc    = c1l;
	          }
	       }

	     this.zmol = (4.7199672 + 0.22997150  * result.day - result.gam)%(twopi);
	     this.zmos = (6.2565837 + 0.017201977 * result.day)%(twopi);

	     /* ------------------------ do solar terms ---------------------- */
	     this.se2  =   2.0 * result.ss1 * result.ss6;
	     this.se3  =   2.0 * result.ss1 * result.ss7;
	     this.si2  =   2.0 * result.ss2 * result.sz12;
	     this.si3  =   2.0 * result.ss2 * (result.sz13 - result.sz11);
	     this.sl2  =  -2.0 * result.ss3 * result.sz2;
	     this.sl3  =  -2.0 * result.ss3 * (result.sz3 - result.sz1);
	     this.sl4  =  -2.0 * result.ss3 * (-21.0 - 9.0 * result.emsq) * zes;
	     this.sgh2 =   2.0 * result.ss4 * result.sz32;
	     this.sgh3 =   2.0 * result.ss4 * (result.sz33 - result.sz31);
	     this.sgh4 = -18.0 * result.ss4 * zes;
	     this.sh2  =  -2.0 * result.ss2 * result.sz22;
	     this.sh3  =  -2.0 * result.ss2 * (result.sz23 - result.sz21);

	     /* ------------------------ do lunar terms ---------------------- */
	     this.ee2  =   2.0 * result.s1 * result.s6;
	     this.e3   =   2.0 * result.s1 * result.s7;
	     this.xi2  =   2.0 * result.s2 * result.z12;
	     this.xi3  =   2.0 * result.s2 * (result.z13 - result.z11);
	     this.xl2  =  -2.0 * result.s3 * result.z2;
	     this.xl3  =  -2.0 * result.s3 * (result.z3 - result.z1);
	     this.xl4  =  -2.0 * result.s3 * (-21.0 - 9.0 * result.emsq) * zel;
	     this.xgh2 =   2.0 * result.s4 * result.z32;
	     this.xgh3 =   2.0 * result.s4 * (result.z33 - result.z31);
	     this.xgh4 = -18.0 * result.s4 * zel;
	     this.xh2  =  -2.0 * result.s2 * result.z22;
	     this.xh3  =  -2.0 * result.s2 * (result.z23 - result.z21);

//	#include "debug2.cpp"
	}  // end dscom

	/*-----------------------------------------------------------------------------
	*
	*                           procedure dsinit
	*
	*  this procedure provides deep space contributions to mean motion dot due
	*    to geopotential resonance with half day and one day orbits.
	*
	*  author        : david vallado                  719-573-2600   28 jun 2005
	*
	*  inputs        :
	*    cosim, sinim-
	*    emsq        - eccentricity squared
	*    argpo       - argument of perigee
	*    s1, s2, s3, s4, s5      -
	*    ss1, ss2, ss3, ss4, ss5 -
	*    sz1, sz3, sz11, sz13, sz21, sz23, sz31, sz33 -
	*    t           - time
	*    tc          -
	*    gsto        - greenwich sidereal time                   rad
	*    mo          - mean anomaly
	*    mdot        - mean anomaly dot (rate)
	*    no          - mean motion
	*    nodeo       - right ascension of ascending node
	*    nodedot     - right ascension of ascending node dot (rate)
	*    xpidot      -
	*    z1, z3, z11, z13, z21, z23, z31, z33 -
	*    eccm        - eccentricity
	*    argpm       - argument of perigee
	*    inclm       - inclination
	*    mm          - mean anomaly
	*    xn          - mean motion
	*    nodem       - right ascension of ascending node
	*
	*  outputs       :
	*    em          - eccentricity
	*    argpm       - argument of perigee
	*    inclm       - inclination
	*    mm          - mean anomaly
	*    nm          - mean motion
	*    nodem       - right ascension of ascending node
	*    irez        - flag for resonance           0-none, 1-one day, 2-half day
	*    atime       -
	*    d2201, d2211, d3210, d3222, d4410, d4422, d5220, d5232, d5421, d5433    -
	*    dedt        -
	*    didt        -
	*    dmdt        -
	*    dndt        -
	*    dnodt       -
	*    domdt       -
	*    del1, del2, del3        -
	*    ses  , sghl , sghs , sgs  , shl  , shs  , sis  , sls
	*    theta       -
	*    xfact       -
	*    xlamo       -
	*    xli         -
	*    xni
	*
	*  locals        :
	*    ainv2       -
	*    aonv        -
	*    cosisq      -
	*    eoc         -
	*    f220, f221, f311, f321, f322, f330, f441, f442, f522, f523, f542, f543  -
	*    g200, g201, g211, g300, g310, g322, g410, g422, g520, g521, g532, g533  -
	*    sini2       -
	*    temp        -
	*    temp1       -
	*    theta       -
	*    xno2        -
	*
	*  coupling      :
	*    getgravconst
	*
	*  references    :
	*    hoots, roehrich, norad spacetrack report #3 1980
	*    hoots, norad spacetrack report #6 1986
	*    hoots, schumacher and glover 2004
	*    vallado, crawford, hujsak, kelso  2006
   * Called only from sgp4init
	  ----------------------------------------------------------------------------*/

	private void dsinit(dscom_return dsc_r,double xpidot,double eccsq,  dsinit_return result)	{
	     /* --------------------- local variables ------------------------ */
	     final double twopi = 2.0 * PI;

	     double ainv2 , aonv=0.0, cosisq, eoc, f220 , f221  , f311  ,
	          f321  , f322  , f330  , f441  , f442  , f522  , f523  ,
	          f542  , f543  , g200  , g201  , g211  , g300  , g310  ,
	          g322  , g410  , g422  , g520  , g521  , g532  , g533  ,
	          ses   , sgs   , sghl  , sghs  , shs   , shll  , sis   ,
	          sini2 , sls   , temp  , temp1 , theta , xno2  ,
	          emo   , emsqo, emsq, dndt;
       emsq=dsc_r.emsq;
	     final double q22    = 1.7891679e-6;
	     final double q31    = 2.1460748e-6;
	     final double q33    = 2.2123015e-7;
	     final double root22 = 1.7891679e-6;
	     final double root44 = 7.3636953e-9;
	     final double root54 = 2.1765803e-9;
	     final double rptim  = 4.37526908801129966e-3; // this equates to 7.29211514668855e-5 rad/sec
	     final double root32 = 3.7393792e-7;
	     final double root52 = 1.1428639e-7;
	     final double x2o3   = 2.0 / 3.0;
	     final double znl    = 1.5835218e-4;
	     final double zns    = 1.19459e-5;

	     // sgp4fix identify constants and allow alternate values

	     /* -------------------- deep space initialization ------------ */
	     this.irez = 0;
	     if ((result.nm < 0.0052359877) && (result.nm > 0.0034906585))
	    	 this.irez = 1;
	     if ((result.nm >= 8.26e-3) && (result.nm <= 9.24e-3) && (result.em >= 0.5))
	    	 this.irez = 2;

	     /* ------------------------ do solar terms ------------------- */
	     ses  =  dsc_r.ss1 * zns * dsc_r.ss5;
	     sis  =  dsc_r.ss2 * zns * (dsc_r.sz11 + dsc_r.sz13);
	     sls  = -zns * dsc_r.ss3 * (dsc_r.sz1 + dsc_r.sz3 - 14.0 - 6.0 * emsq);
	     sghs =  dsc_r.ss4 * zns * (dsc_r.sz31 + dsc_r.sz33 - 6.0);
	     shs  = -zns * dsc_r.ss2 * (dsc_r.sz21 + dsc_r.sz23);
	     // sgp4fix for 180 deg incl
	     if ((result.inclm < 5.2359877e-2) || (result.inclm > PI - 5.2359877e-2))
	       shs = 0.0;
	     if (dsc_r.sinim != 0.0)
	       shs = shs / dsc_r.sinim;
	     sgs  = sghs - dsc_r.cosim * shs;

	     /* ------------------------- do lunar terms ------------------ */
	     this.dedt = ses + dsc_r.s1 * znl * dsc_r.s5;
	     this.didt = sis + dsc_r.s2 * znl * (dsc_r.z11 + dsc_r.z13);
	     this.dmdt = sls - znl * dsc_r.s3 * (dsc_r.z1 + dsc_r.z3 - 14.0 - 6.0 * emsq);
	     sghl = dsc_r.s4 * znl * (dsc_r.z31 + dsc_r.z33 - 6.0);
	     shll = -znl * dsc_r.s2 * (dsc_r.z21 + dsc_r.z23);
	     // sgp4fix for 180 deg incl
	     if ((result.inclm < 5.2359877e-2) || (result.inclm > PI - 5.2359877e-2)) shll = 0.0;
	     this.domdt = sgs + sghl;
	     this.dnodt = shs;
	     if (dsc_r.sinim != 0.0) {
	    	 this.domdt = this.domdt - dsc_r.cosim / dsc_r.sinim * shll;
	    	 this.dnodt = this.dnodt + shll / dsc_r.sinim;
	     }

	     /* ----------- calculate deep space resonance effects -------- */
	     dndt   = 0.0;
	     theta  = (this.gsto)%(twopi);
	     //   sgp4fix for negative inclinations
	     //   the following if statement should be commented out
	     //if (inclm < 0.0)
	     //  {
	     //    inclm  = -inclm;
	     //    argpm  = argpm - PI;
	     //    nodem = nodem + PI;
	     //  }

	     /* -------------- initialize the resonance terms ------------- */
	     if (this.irez != 0)
	       {
	         aonv = pow(result.nm / this.whichconst.xke, x2o3);

	         /* ---------- geopotential resonance for 12 hour orbits ------ */
	         if (this.irez == 2)
	           {
	             cosisq = dsc_r.cosim * dsc_r.cosim;
	             emo    = result.em;
	             result.em     = this.ecco;
	             emsqo  = emsq;
	             emsq   = eccsq;
	             eoc    = result.em * emsq;
	             g201   = -0.306 - (result.em - 0.64) * 0.440;

	             if (result.em <= 0.65)
	               {
	                 g211 =    3.616  -  13.2470 * result.em +  16.2900 * emsq;
	                 g310 =  -19.302  + 117.3900 * result.em - 228.4190 * emsq +  156.5910 * eoc;
	                 g322 =  -18.9068 + 109.7927 * result.em - 214.6334 * emsq +  146.5816 * eoc;
	                 g410 =  -41.122  + 242.6940 * result.em - 471.0940 * emsq +  313.9530 * eoc;
	                 g422 = -146.407  + 841.8800 * result.em - 1629.014 * emsq + 1083.4350 * eoc;
	                 g520 = -532.114  + 3017.977 * result.em - 5740.032 * emsq + 3708.2760 * eoc;
	               }
	               else
	               {
	                 g211 =   -72.099 +   331.819 * result.em -   508.738 * emsq +   266.724 * eoc;
	                 g310 =  -346.844 +  1582.851 * result.em -  2415.925 * emsq +  1246.113 * eoc;
	                 g322 =  -342.585 +  1554.908 * result.em -  2366.899 * emsq +  1215.972 * eoc;
	                 g410 = -1052.797 +  4758.686 * result.em -  7193.992 * emsq +  3651.957 * eoc;
	                 g422 = -3581.690 + 16178.110 * result.em - 24462.770 * emsq + 12422.520 * eoc;
	                 if (result.em > 0.715)
	                     g520 =-5149.66 + 29936.92 * result.em - 54087.36 * emsq + 31324.56 * eoc;
	                   else
	                     g520 = 1464.74 -  4664.75 * result.em +  3763.64 * emsq;
	               }
	             if (result.em < 0.7)
	               {
	                 g533 = -919.22770 + 4988.6100 * result.em - 9064.7700 * emsq + 5542.21  * eoc;
	                 g521 = -822.71072 + 4568.6173 * result.em - 8491.4146 * emsq + 5337.524 * eoc;
	                 g532 = -853.66600 + 4690.2500 * result.em - 8624.7700 * emsq + 5341.4  * eoc;
	               }
	               else
	               {
	                 g533 =-37995.780 + 161616.52 * result.em - 229838.20 * emsq + 109377.94 * eoc;
	                 g521 =-51752.104 + 218913.95 * result.em - 309468.16 * emsq + 146349.42 * eoc;
	                 g532 =-40023.880 + 170470.89 * result.em - 242699.48 * emsq + 115605.82 * eoc;
	               }

	             sini2=  dsc_r.sinim * dsc_r.sinim;
	             f220 =  0.75 * (1.0 + 2.0 * dsc_r.cosim+cosisq);
	             f221 =  1.5 * sini2;
	             f321 =  1.875 * dsc_r.sinim  *  (1.0 - 2.0 * dsc_r.cosim - 3.0 * cosisq);
	             f322 = -1.875 * dsc_r.sinim  *  (1.0 + 2.0 * dsc_r.cosim - 3.0 * cosisq);
	             f441 = 35.0 * sini2 * f220;
	             f442 = 39.3750 * sini2 * sini2;
	             f522 =  9.84375 * dsc_r.sinim * (sini2 * (1.0 - 2.0 * dsc_r.cosim- 5.0 * cosisq) +
	                     0.33333333 * (-2.0 + 4.0 * dsc_r.cosim + 6.0 * cosisq) );
	             f523 = dsc_r.sinim * (4.92187512 * sini2 * (-2.0 - 4.0 * dsc_r.cosim +
	                    10.0 * cosisq) + 6.56250012 * (1.0+2.0 * dsc_r.cosim - 3.0 * cosisq));
	             f542 = 29.53125 * dsc_r.sinim * (2.0 - 8.0 * dsc_r.cosim+cosisq *
	                    (-12.0 + 8.0 * dsc_r.cosim + 10.0 * cosisq));
	             f543 = 29.53125 * dsc_r.sinim * (-2.0 - 8.0 * dsc_r.cosim+cosisq *
	                    (12.0 + 8.0 * dsc_r.cosim - 10.0 * cosisq));
	             xno2  =  result.nm * result.nm;
	             ainv2 =  aonv * aonv;
	             temp1 =  3.0 * xno2 * ainv2;
	             temp  =  temp1 * root22;
	             this.d2201 =  temp * f220 * g201;
	             this.d2211 =  temp * f221 * g211;
	             temp1 =  temp1 * aonv;
	             temp  =  temp1 * root32;
	             this.d3210 =  temp * f321 * g310;
	             this.d3222 =  temp * f322 * g322;
	             temp1 =  temp1 * aonv;
	             temp  =  2.0 * temp1 * root44;
	             this.d4410 =  temp * f441 * g410;
	             this.d4422 =  temp * f442 * g422;
	             temp1 =  temp1 * aonv;
	             temp  =  temp1 * root52;
	             this.d5220 =  temp * f522 * g520;
	             this.d5232 =  temp * f523 * g532;
	             temp  =  2.0 * temp1 * root54;
	             this.d5421 =  temp * f542 * g521;
	             this.d5433 =  temp * f543 * g533;
	             this.xlamo =  (this.mo + this.nodeo + this.nodeo-theta - theta)%(twopi);
	             this.xfact =  this.mdot + this.dmdt + 2.0 * (this.nodedot + this.dnodt - rptim) - this.no;
	             result.em    = emo;
	             emsq  = emsqo;
	           }

	         /* ---------------- synchronous resonance terms -------------- */
	         if (this.irez == 1)
	           {
	             g200  = 1.0 + emsq * (-2.5 + 0.8125 * emsq);
	             g310  = 1.0 + 2.0 * emsq;
	             g300  = 1.0 + emsq * (-6.0 + 6.60937 * emsq);
	             f220  = 0.75 * (1.0 + dsc_r.cosim) * (1.0 + dsc_r.cosim);
	             f311  = 0.9375 * dsc_r.sinim * dsc_r.sinim * (1.0 + 3.0 * dsc_r.cosim) - 0.75 * (1.0 + dsc_r.cosim);
	             f330  = 1.0 + dsc_r.cosim;
	             f330  = 1.875 * f330 * f330 * f330;
	             this.del1  = 3.0 * result.nm * result.nm * aonv * aonv;
	             this.del2  = 2.0 * this.del1 * f220 * g200 * q22;
	             this.del3  = 3.0 * this.del1 * f330 * g300 * q33 * aonv;
	             this.del1  = this.del1 * f311 * g310 * q31 * aonv;
	             this.xlamo = (this.mo + this.nodeo + this.argpo - theta)%(twopi);
	             this.xfact = this.mdot + xpidot - rptim + this.dmdt + this.domdt + this.dnodt - this.no;
	           }

	         /* ------------ for sgp4, initialize the integrator ---------- */
	         result.nm    = this.no + dndt;
	       }

	}  // end dsinit

	/**-----------------------------------------------------------------------------
	*
	*                           procedure dspace
	*
	*  this procedure provides deep space contributions to mean elements for
	*    perturbing third body.  these effects have been averaged over one
	*    revolution of the sun and moon.  for earth resonance effects, the
	*    effects have been averaged over no revolutions of the satellite.
	*    (mean motion)
	*
	*  author        : david vallado                  719-573-2600   28 jun 2005
	*
	*  inputs        :
	*    d2201, d2211, d3210, d3222, d4410, d4422, d5220, d5232, d5421, d5433 -
	*    dedt        -
	*    del1, del2, del3  -
	*    didt        -
	*    dmdt        -
	*    dnodt       -
	*    domdt       -
	*    irez        - flag for resonance           0-none, 1-one day, 2-half day
	*    argpo       - argument of perigee
	*    argpdot     - argument of perigee dot (rate)
	*    t           - time
	*    tc          -
	*    gsto        - gst
	*    xfact       -
	*    xlamo       -
	*    no          - mean motion
	*    atime       -
	*    em          - eccentricity
	*    ft          -
	*    argpm       - argument of perigee
	*    inclm       - inclination
	*    xli         -
	*    mm          - mean anomaly
	*    xni         - mean motion
	*    nodem       - right ascension of ascending node
	*
	*  outputs       :
	*    atime       -
	*    em          - eccentricity
	*    argpm       - argument of perigee
	*    inclm       - inclination
	*    xli         -
	*    mm          - mean anomaly
	*    xni         -
	*    nodem       - right ascension of ascending node
	*    dndt        -
	*    nm          - mean motion
	*
	*  locals        :
	*    delt        -
	*    ft          -
	*    theta       -
	*    x2li        -
	*    x2omi       -
	*    xl          -
	*    xldot       -
	*    xnddt       -
	*    xndt        -
	*    xomi        -
	*
	*  coupling      :
	*    none        -
	*
	*  references    :
	*    hoots, roehrich, norad spacetrack report #3 1980
	*    hoots, norad spacetrack report #6 1986
	*    hoots, schumacher and glover 2004
	*    vallado, crawford, hujsak, kelso  2006
   * Note - All parameters except result are used read-only.
   * does not write to this.*
   * called only from sgp4
	  ----------------------------------------------------------------------------*/
	private void dspace(double t, dspace_return result) {
     final double twopi = 2.0 * PI;
     int iretn , iret;
     double delt, theta, x2li, x2omi, xl, xldot , xnddt, xndt, xomi;
     double atime,xni,xli;
     double ft    = 0.0;

     final double fasx2 = 0.13130908;
     final double fasx4 = 2.8843198;
     final double fasx6 = 0.37448087;
     final double g22   = 5.7686396;
     final double g32   = 0.95240898;
     final double g44   = 1.8014998;
     final double g52   = 1.0508330;
     final double g54   = 4.4108898;
     final double rptim = 4.37526908801129966e-3; // this equates to 7.29211514668855e-5 rad/sec
     final double stepp =    720.0;
     final double stepn =   -720.0;
     final double step2 = 259200.0;

     /* ----------- calculate deep space resonance effects ----------- */
     result.dndt   = 0.0;
     theta  = (this.gsto + t * rptim)%(twopi);
     result.em     = result.em + this.dedt * t;

     result.inclm  = result.inclm + this.didt * t;
     result.argpm  = result.argpm + this.domdt * t;
     result.nodem  = result.nodem + this.dnodt * t;
     result.mm     = result.mm + this.dmdt * t;

     //   sgp4fix for negative inclinations
     //   the following if statement should be commented out
     //  if (inclm < 0.0)
     // {
     //    inclm = -inclm;
     //    argpm = argpm - PI;
     //    nodem = nodem + PI;
     //  }

     /* - update resonances : numerical (euler-maclaurin) integration - */
     /* ------------------------- epoch restart ----------------------  */
     //   sgp4fix for propagator problems
     //   the following integration works for negative time steps and periods
     //   the specific changes are unknown because the original code was so convoluted

     ft    = 0.0;
     atime = 0.0; //atime not used above this point, so overwritten
     //Vallado 2008 does not overwrite atime here, so its integration
     //is not reset each time. This is more efficient if you sequentially
     //call the integrator many times, all a short distance from each other
     //and a long distance from the epoch. We actually don't want that,
     //since it will confuse the idl version.
     if (this.irez != 0) {
//	         if ((atime == 0.0) || ((t >= 0.0) && (atime < 0.0)) ||
//	             ((t < 0.0) && (atime >= 0.0))) //always true? atime set to 0.0 just above this
//	           {
             if (t >= 0.0) {
               delt = stepp;
             } else {
               delt = stepn;
             }
             atime  = 0.0;
             xni    = this.no;      //xni overwritten in this branch, not used above this point
             xli    = this.xlamo;   //xli overwriiten in this branch, not used above this point
//	           }
         iretn = 381; // added for do loop
         iret  =   0; // added for loop
         do {// while iretn = 381 //CDJ made this a do loop to tell the compiler that the loop runs at least once
                                //so it knows that certain variables are always initialized and can be read
                                //below the loop
           if ((abs(t) < abs(atime)) || (iret == 351)) {
             if (t >= 0.0) {
               delt = stepn;
             } else {
               delt = stepp;
             }
             iret  = 351;
             iretn = 381;
           } else {
             if (t > 0.0) { // error if prev if has atime:=0.0 and t:=0.0 (ge)
               delt = stepp;
             } else {
               delt = stepn;
             }
             if (abs(t - atime) >= stepp) {
               iret  = 0;
               iretn = 381;
             } else {
               ft    = t - atime;
               iretn = 0;
             }
           }

           /* ------------------- dot terms calculated ------------- */
           /* ----------- near - synchronous resonance terms ------- */
           if (this.irez != 2) {
             xndt  = this.del1 * sin(xli - fasx2) + this.del2 * sin(2.0 * (xli - fasx4)) +
                        this.del3 * sin(3.0 * (xli - fasx6));
             xldot = xni + this.xfact;
             xnddt = this.del1 * cos(xli - fasx2) +
                         2.0 * this.del2 * cos(2.0 * (xli - fasx4)) +
                         3.0 * this.del3 * cos(3.0 * (xli - fasx6));
             xnddt = xnddt * xldot;
           } else {
                 /* --------- near - half-day resonance terms -------- */
             xomi  = this.argpo + this.argpdot * atime;
             x2omi = xomi + xomi;
             x2li  = xli + xli;
             xndt  = this.d2201 * sin(x2omi + xli - g22) + this.d2211 * sin(xli - g22) +
                       this.d3210 * sin(xomi + xli - g32)  + this.d3222 * sin(-xomi + xli - g32)+
                       this.d4410 * sin(x2omi + x2li - g44)+ this.d4422 * sin(x2li - g44) +
                       this.d5220 * sin(xomi + xli - g52)  + this.d5232 * sin(-xomi + xli - g52)+
                       this.d5421 * sin(xomi + x2li - g54) + this.d5433 * sin(-xomi + x2li - g54);
             xldot = xni + this.xfact;
             xnddt = this.d2201 * cos(x2omi + xli - g22) + this.d2211 * cos(xli - g22) +
                       this.d3210 * cos(xomi + xli - g32) + this.d3222 * cos(-xomi + xli - g32) +
                       this.d5220 * cos(xomi + xli - g52) + this.d5232 * cos(-xomi + xli - g52) +
                       2.0 * (this.d4410 * cos(x2omi + x2li - g44) +
                       this.d4422 * cos(x2li - g44) +this. d5421 * cos(xomi + x2li - g54) +
                       this.d5433 * cos(-xomi + x2li - g54));
             xnddt = xnddt * xldot;
           }

             /* ----------------------- integrator ------------------- */
           if (iretn == 381) {
             xli   = xli + xldot * delt + xndt * step2;
             xni   = xni + xndt * delt + xnddt * step2;
             atime = atime + delt;
           }
         } while (iretn == 381);

         result.nm = xni + xndt * ft + xnddt * ft * ft * 0.5;
         xl = xli + xldot * ft + xndt * ft * ft * 0.5;
         if (this.irez != 1) {
           result.mm   = xl - 2.0 * result.nodem + 2.0 * theta;
           result.dndt = result.nm - this.no;
         } else {
           result.mm   = xl - result.nodem - result.argpm + theta;
           result.dndt = result.nm - this.no;
         }
         result.nm = this.no + result.dndt;
       }
	}  // end dspace


	/*-----------------------------------------------------------------------------
	*
	*                             procedure sgp4init
	*
	*  this procedure initializes variables for sgp4.
	*
	*  author        : david vallado                  719-573-2600   28 jun 2005
	*
	*  inputs        :
	*    satn        - satellite number
	*    bstar       - sgp4 type drag coefficient              kg/m2er
	*    ecco        - eccentricity
	*    epoch       - epoch time in days from jan 0, 1950. 0 hr
	*    argpo       - argument of perigee (output if ds)
	*    inclo       - inclination
	*    mo          - mean anomaly (output if ds)
	*    no          - mean motion
	*    nodeo       - right ascension of ascending node
	*
	*  outputs       :
	*    satrec      - common values for subsequent calls
	*    return code - non-zero on error.
	*                   1 - mean elements, ecc >= 1.0 or ecc < -0.001 or a < 0.95 er
	*                   2 - mean motion less than 0.0
	*                   3 - pert elements, ecc < 0.0  or  ecc > 1.0
	*                   4 - semi-latus rectum < 0.0
	*                   5 - epoch elements are sub-orbital
	*                   6 - satellite has decayed
	*
	*  locals        :
	*    cnodm  , snodm  , cosim  , sinim  , cosomm , sinomm
	*    cc1sq  , cc2    , cc3
	*    coef   , coef1
	*    cosio4      -
	*    day         -
	*    dndt        -
	*    em          - eccentricity
	*    emsq        - eccentricity squared
	*    eeta        -
	*    etasq       -
	*    gam         -
	*    argpm       - argument of perigee
	*    nodem       -
	*    inclm       - inclination
	*    mm          - mean anomaly
	*    nm          - mean motion
	*    perige      - perigee
	*    pinvsq      -
	*    psisq       -
	*    qzms24      -
	*    rtemsq      -
	*    s1, s2, s3, s4, s5, s6, s7          -
	*    sfour       -
	*    ss1, ss2, ss3, ss4, ss5, ss6, ss7         -
	*    sz1, sz2, sz3
	*    sz11, sz12, sz13, sz21, sz22, sz23, sz31, sz32, sz33        -
	*    tc          -
	*    temp        -
	*    temp1, temp2, temp3       -
	*    tsi         -
	*    xpidot      -
	*    xhdot1      -
	*    z1, z2, z3          -
	*    z11, z12, z13, z21, z22, z23, z31, z32, z33         -
	*
	*  coupling      :
	*    getgravconst-
	*    initl       -
	*    dscom       -
	*    dpper       -
	*    dsinit      -
	*    sgp4        -
	*
	*  references    :
	*    hoots, roehrich, norad spacetrack report #3 1980
	*    hoots, norad spacetrack report #6 1986
	*    hoots, schumacher and glover 2004
	*    vallado, crawford, hujsak, kelso  2006
	  ----------------------------------------------------------------------------*/
  public SGP4Core(String Line1, String Line2, gravconsttype whichconst) {
    this(new TwoLineElement(Line1, Line2, whichconst));
  }
  public SGP4Core(String Line1, String Line2) {
    this(new TwoLineElement(Line1, Line2));
  }
  public SGP4Core(TwoLineElement TLE) {
    final double xpdotp =  1440.0 / (2.0 * PI);  // 229.1831180523293
    tle=TLE;
    bstar=TLE.bstar;
    whichconst=TLE.whichconst;

    ecco=TLE.ecco;

    // ---- convert to sgp4 units ----
    no   = TLE.no / xpdotp; // * rad/min
    a    = pow( TLE.no*whichconst.tumin , (-2.0/3.0) );

    // ---- find standard orbital elements ----
    inclo = toRadians(TLE.inclo);
    nodeo = toRadians(TLE.nodeo);
    argpo = toRadians(TLE.argpo);
    mo    = toRadians(TLE.mo);

    sgp4init(TLE.jdsatepoch-2433281.5);

  }
	private void sgp4init (double epoch)	{
	     /* --------------------- local variables ------------------------ */
    double ao, con42, cosio, sinio, cosio2, eccsq,
    omeosq, posq,   rp,     rteosq,
    cc1sq ,
    cc2   , cc3   , coef  , coef1 , cosio4, 
    em    , eeta  , etasq , 
    inclm , nm    , perige, pinvsq, psisq , qzms24,
    sfour ,
    temp  , temp1 , temp2 , temp3 , tsi   , xpidot,
    xhdot1, 
    qzms2t, ss,  x2o3;

     /* ------------------------ initialization --------------------- */
     // sgp4fix divisor for divide by zero check on inclination
     final double temp4    =   1.0 + cos(PI-1.0e-9);

     /* ----------- set all near earth variables to zero ------------ */
     this.isimp   = 0;   this.isdeep = false; this.aycof    = 0.0;
     this.con41   = 0.0; this.cc1    = 0.0; this.cc4      = 0.0;
     this.cc5     = 0.0; this.d2     = 0.0; this.d3       = 0.0;
     this.d4      = 0.0; this.delmo  = 0.0; this.eta      = 0.0;
     this.argpdot = 0.0; this.omgcof = 0.0; this.sinmao   = 0.0;
     this.t2cof  = 0.0; this.t3cof    = 0.0;
     this.t4cof   = 0.0; this.t5cof  = 0.0; this.x1mth2   = 0.0;
     this.x7thm1  = 0.0; this.mdot   = 0.0; this.nodedot  = 0.0;
     this.xlcof   = 0.0; this.xmcof  = 0.0; this.nodecf   = 0.0;

     /* ----------- set all deep space variables to zero ------------ */
     this.irez  = 0;   this.d2201 = 0.0; this.d2211 = 0.0;
     this.d3210 = 0.0; this.d3222 = 0.0; this.d4410 = 0.0;
     this.d4422 = 0.0; this.d5220 = 0.0; this.d5232 = 0.0;
     this.d5421 = 0.0; this.d5433 = 0.0; this.dedt  = 0.0;
     this.del1  = 0.0; this.del2  = 0.0; this.del3  = 0.0;
     this.didt  = 0.0; this.dmdt  = 0.0; this.dnodt = 0.0;
     this.domdt = 0.0; this.e3    = 0.0; this.ee2   = 0.0;
     this.peo   = 0.0; this.pgho  = 0.0; this.pho   = 0.0;
     this.pinco = 0.0; this.plo   = 0.0; this.se2   = 0.0;
     this.se3   = 0.0; this.sgh2  = 0.0; this.sgh3  = 0.0;
     this.sgh4  = 0.0; this.sh2   = 0.0; this.sh3   = 0.0;
     this.si2   = 0.0; this.si3   = 0.0; this.sl2   = 0.0;
     this.sl3   = 0.0; this.sl4   = 0.0; this.gsto  = 0.0;
     this.xfact = 0.0; this.xgh2  = 0.0; this.xgh3  = 0.0;
     this.xgh4  = 0.0; this.xh2   = 0.0; this.xh3   = 0.0;
     this.xi2   = 0.0; this.xi3   = 0.0; this.xl2   = 0.0;
     this.xl3   = 0.0; this.xl4   = 0.0; this.xlamo = 0.0;
     this.zmol  = 0.0; this.zmos  = 0.0;

     /* ------------------------ earth constants ----------------------- */
     // sgp4fix identify constants and allow alternate values
     ss     = 78.0 / this.whichconst.rekm + 1.0;
     qzms2t = pow(((120.0 - 78.0) / this.whichconst.rekm), 4);
     x2o3   =  2.0 / 3.0;

     /* body of initl function (only used here) */
     double ak, d1, del, adel, po;

     /* ------------- calculate auxillary epoch quantities ---------- */
     eccsq  = this.ecco * this.ecco;
     omeosq = 1.0 - eccsq;
     rteosq = sqrt(omeosq);
     cosio  = cos(this.inclo);
     cosio2 = cosio * cosio;

     /* ------------------ un-kozai the mean motion ----------------- */
     ak    = pow(this.whichconst.xke / this.no, x2o3);
     d1    = 0.75 * this.whichconst.j2 * (3.0 * cosio2 - 1.0) / (rteosq * omeosq);
     del   = d1 / (ak * ak);
     adel  = ak * (1.0 - del * del - del *
             (1.0 / 3.0 + 134.0 * del * del / 81.0));
     del   = d1/(adel * adel);
     this.no    = this.no / (1.0 + del);

     ao    = pow(this.whichconst.xke / this.no, x2o3);
     sinio = sin(this.inclo);
     po    = ao * omeosq;
     con42 = 1.0 - 5.0 * cosio2;
     this.con41 = -con42-cosio2-cosio2;
     posq  = po * po;
     rp    = ao * (1.0 - this.ecco);
     this.isdeep = false;

     this.gsto = gstime(epoch + 2433281.5);
     /* end of initl function */

     if ((omeosq >= 0.0 ) || ( this.no >= 0.0)) {
       this.isimp = 0;
       if (rp < (220.0 / this.whichconst.rekm + 1.0)) this.isimp = 1;
       sfour  = ss;
       qzms24 = qzms2t;
       perige = (rp - 1.0) * this.whichconst.rekm;

       /* - for perigees below 156 km, s and qoms2t are altered - */
       if (perige < 156.0) {
         sfour = perige - 78.0;
         if (perige < 98.0) sfour = 20.0;
         qzms24 = pow(((120.0 - sfour) / this.whichconst.rekm), 4.0);
         sfour  = sfour / this.whichconst.rekm + 1.0;
       }
       pinvsq = 1.0 / posq;

       tsi  = 1.0 / (ao - sfour);
       this.eta  = ao * this.ecco * tsi;
       etasq = this.eta * this.eta;
       eeta  = this.ecco * this.eta;
       psisq = abs(1.0 - etasq);
       coef  = qzms24 * pow(tsi, 4.0);
       coef1 = coef / pow(psisq, 3.5);
       cc2   = coef1 * this.no * (ao * (1.0 + 1.5 * etasq + eeta *
               (4.0 + etasq)) + 0.375 * this.whichconst.j2 * tsi / psisq * this.con41 *
               (8.0 + 3.0 * etasq * (8.0 + etasq)));
       this.cc1   = this.bstar * cc2;
       cc3   = 0.0;
       if (this.ecco > 1.0e-4) {
             cc3 = -2.0 * coef * tsi * this.whichconst.j3oj2 * this.no * sinio / this.ecco;
       }
       this.x1mth2 = 1.0 - cosio2;
       this.cc4    = 2.0* this.no * coef1 * ao * omeosq *
                     (this.eta * (2.0 + 0.5 * etasq) + this.ecco *
                     (0.5 + 2.0 * etasq) - this.whichconst.j2 * tsi / (ao * psisq) *
                     (-3.0 * this.con41 * (1.0 - 2.0 * eeta + etasq *
                     (1.5 - 0.5 * eeta)) + 0.75 * this.x1mth2 *
                     (2.0 * etasq - eeta * (1.0 + etasq)) * cos(2.0 * this.argpo)));
       this.cc5 = 2.0 * coef1 * ao * omeosq * (1.0 + 2.75 *
                  (etasq + eeta) + eeta * etasq);
       cosio4 = cosio2 * cosio2;
       temp1  = 1.5 * this.whichconst.j2 * pinvsq * this.no;
       temp2  = 0.5 * temp1 * this.whichconst.j2 * pinvsq;
       temp3  = -0.46875 *this.whichconst.j4 * pinvsq * pinvsq * this.no;
       this.mdot     = this.no + 0.5 * temp1 * rteosq * this.con41 + 0.0625 *
                       temp2 * rteosq * (13.0 - 78.0 * cosio2 + 137.0 * cosio4);
       this.argpdot  = -0.5 * temp1 * con42 + 0.0625 * temp2 *
                       (7.0 - 114.0 * cosio2 + 395.0 * cosio4) +
                       temp3 * (3.0 - 36.0 * cosio2 + 49.0 * cosio4);
       xhdot1            = -temp1 * cosio;
       this.nodedot = xhdot1 + (0.5 * temp2 * (4.0 - 19.0 * cosio2) +
                      2.0 * temp3 * (3.0 - 7.0 * cosio2)) * cosio;
       xpidot            =  this.argpdot+ this.nodedot;
       this.omgcof   = this.bstar * cc3 * cos(this.argpo);
       this.xmcof    = 0.0;
       if (this.ecco > 1.0e-4) this.xmcof = -x2o3 * coef * this.bstar / eeta;
       this.nodecf = 3.5 * omeosq * xhdot1 * this.cc1;
       this.t2cof   = 1.5 * this.cc1;
         // sgp4fix for divide by zero with xinco = 180 deg
       if (abs(cosio+1.0) > 1.5e-12) {
         this.xlcof = -0.25 * this.whichconst.j3oj2 * sinio * (3.0 + 5.0 * cosio) / (1.0 + cosio);
       } else {
         this.xlcof = -0.25 * this.whichconst.j3oj2 * sinio * (3.0 + 5.0 * cosio) / temp4;
       }
       this.aycof   = -0.5 * this.whichconst.j3oj2 * sinio;
       this.delmo   = pow((1.0 + this.eta * cos(this.mo)), 3);
       this.sinmao  = sin(this.mo);
       this.x7thm1  = 7.0 * cosio2 - 1.0;

       /* --------------- deep space initialization ------------- */
       if ((2*PI / this.no) >= 225.0) {
         this.isdeep = true;
         this.isimp  = 1;
         inclm = this.inclo;
         dscom_return dsc_r=new dscom_return();

         dscom(epoch, dsc_r);
         em=dsc_r.em;
         nm=dsc_r.nm;

         dsinit_return dsi_r=new dsinit_return();
         dsi_r.em=em;
         dsi_r.argpm=0.0;
         dsi_r.inclm=inclm;
         dsi_r.mm=0.0;
         dsi_r.nm=nm;
         dsi_r.nodem=0.0;

         dsinit(dsc_r, xpidot,eccsq, dsi_r);
         em=dsi_r.em;
         inclm=dsi_r.inclm;
         nm=dsi_r.nm;
       }

       /* ----------- set variables if not deep space ----------- */
       if (this.isimp != 1) {
         cc1sq          = this.cc1 * this.cc1;
         this.d2    = 4.0 * ao * tsi * cc1sq;
         temp           = this.d2 * tsi * this.cc1 / 3.0;
         this.d3    = (17.0 * ao + sfour) * temp;
         this.d4    = 0.5 * temp * ao * tsi * (221.0 * ao + 31.0 * sfour) *
                            this.cc1;
         this.t3cof = this.d2 + 2.0 * cc1sq;
         this.t4cof = 0.25 * (3.0 * this.d3 + this.cc1 *
                            (12.0 * this.d2 + 10.0 * cc1sq));
         this.t5cof = 0.2 * (3.0 * this.d4 +
                            12.0 * this.cc1 * this.d3 +
                            6.0 * this.d2 * this.d2 +
                            15.0 * cc1sq * (2.0 * this.d2 + cc1sq));
       }
     } // if omeosq = 0 ...

	}  // end sgp4init


	/*-----------------------------------------------------------------------------
	*
	*                             procedure sgp4
	*
	*  this procedure is the sgp4 prediction model from space command. this is an
	*    updated and combined version of sgp4 and sdp4, which were originally
	*    published separately in spacetrack report #3. this version follows the nasa
	*    release on the internet. there are a few fixes that are added to correct
	*    known errors in the existing implementations.
	*
	*  author        : david vallado                  719-573-2600   28 jun 2005
	*
	*  inputs        :
	*    satrec	 - initialised structure from sgp4init() call.
	*    tsince	 - time eince epoch (minutes)
	*
	*  outputs       :
	*    r           - position vector                     km
	*    v           - velocity                            km/sec
	*  return code - non-zero on error.
	*                   1 - mean elements, ecc >= 1.0 or ecc < -0.001 or a < 0.95 er
	*                   2 - mean motion less than 0.0
	*                   3 - pert elements, ecc < 0.0  or  ecc > 1.0
	*                   4 - semi-latus rectum < 0.0
	*                   5 - epoch elements are sub-orbital
	*                   6 - satellite has decayed
	*
	*  locals        :
	*    am          -
	*    axnl, aynl        -
	*    betal       -
	*    cosim   , sinim   , cosomm  , sinomm  , cnod    , snod    , cos2u   ,
	*    sin2u   , coseo1  , sineo1  , cosi    , sini    , cosip   , sinip   ,
	*    cosisq  , cossu   , sinsu   , cosu    , sinu
	*    delm        -
	*    delomg      -
	*    dndt        -
	*    eccm        -
	*    emsq        -
	*    ecose       -
	*    el2         -
	*    eo1         -
	*    eccp        -
	*    esine       -
	*    argpm       -
	*    argpp       -
	*    omgadf      -
	*    pl          -
	*    r           -
	*    rtemsq      -
	*    rdotl       -
	*    rl          -
	*    rvdot       -
	*    rvdotl      -
	*    su          -
	*    t2  , t3   , t4    , tc
	*    tem5, temp , temp1 , temp2  , tempa  , tempe  , templ
	*    u   , ux   , uy    , uz     , vx     , vy     , vz
	*    inclm       - inclination
	*    mm          - mean anomaly
	*    nm          - mean motion
	*    nodem       - right asc of ascending node
	*    xinc        -
	*    xincp       -
	*    xl          -
	*    xlm         -
	*    mp          -
	*    xmdf        -
	*    xmx         -
	*    xmy         -
	*    nodedf      -
	*    xnode       -
	*    nodep       -
	*    np          -
	*
	*  coupling      :
	*    getgravconst-
	*    dpper
	*    dpspace
	*
	*  references    :
	*    hoots, roehrich, norad spacetrack report #3 1980
	*    hoots, norad spacetrack report #6 1986
	*    hoots, schumacher and glover 2004
	*    vallado, crawford, hujsak, kelso  2006
	  ----------------------------------------------------------------------------*/

	public double[] sgp4(double tsince) throws SGP4Exception {
    double am   , axnl  , aynl , betal ,  cosim , cnod  ,
       cos2u, coseo1, cosi , cosip ,  cosisq, cossu , cosu,
       delm , delomg, em   , emsq  ,  ecose , el2   , eo1 ,
       ep   , esine , argpm, argpp ,  argpdf, pl,     mrt = 0.0,
       mvt  , rdotl , rl   , rvdot ,  rvdotl, sinim ,
       sin2u, sineo1, sini , sinip ,  sinsu , sinu  ,
       snod , su    , t2   , t3    ,  t4    , tem5  , temp,
       temp1, temp2 , tempa, tempe ,  templ , u     , ux  ,
       uy   , uz    , vx   , vy    ,  vz    , inclm , mm  ,
       nm   , nodem, xinc , xincp ,  xl    , xlm   , mp  ,
       xmdf , xmx   , xmy  , nodedf, xnode , nodep,
       twopi, x2o3  ,l_aycof,l_xlcof,l_con41,l_x1mth2,l_x7thm1,
       vkmpersec;
    int ktr;

    /* ------------------ set mathematical constants --------------- */
    // sgp4fix divisor for divide by zero check on inclination
    final double temp4    =   1.0 + cos(PI-1.0e-9);
    twopi = 2.0 * PI;
    x2o3  = 2.0 / 3.0;
    // sgp4fix identify constants and allow alternate values
    vkmpersec     = this.whichconst.rekm * this.whichconst.xke/60.0;

    /* ------- update for secular gravity and atmospheric drag ----- */
    xmdf    = this.mo + this.mdot * tsince;
    argpdf  = this.argpo + this.argpdot * tsince;
    nodedf  = this.nodeo + this.nodedot * tsince;
    argpm   = argpdf;
    mm      = xmdf;
    t2      = tsince*tsince;
    nodem   = nodedf + this.nodecf * t2;
    tempa   = 1.0 - this.cc1 * tsince;
    tempe   = this.bstar * this.cc4 * tsince;
    templ   = this.t2cof * t2;

    if (this.isimp != 1) {
      delomg = this.omgcof * tsince;
      delm   = this.xmcof *
              (pow((1.0 + this.eta * cos(xmdf)), 3) -
              this.delmo);
      temp   = delomg + delm;
      mm     = xmdf + temp;
      argpm  = argpdf - temp;
      t3     = t2 * tsince;
      t4     = t3 * tsince;
      tempa  = tempa - this.d2 * t2 - this.d3 * t3 -
                      this.d4 * t4;
      tempe  = tempe + this.bstar * this.cc5 * (sin(mm) -
                      this.sinmao);
      templ  = templ + this.t3cof * t3 + t4 * (this.t4cof +
                      tsince * this.t5cof);
    }

    nm    = this.no;
    em    = this.ecco;
    inclm = this.inclo;
    if (this.isdeep) {
      dspace_return ds_r=new dspace_return();
      ds_r.em=em;
      ds_r.argpm=argpm;
      ds_r.inclm=inclm;
      ds_r.mm=mm;
      ds_r.nodem=nodem;
      ds_r.nm=nm;
      dspace(tsince, ds_r);
      em=ds_r.em;
      argpm=ds_r.argpm;
      inclm=ds_r.inclm;
      mm=ds_r.mm;
      nodem=ds_r.nodem;
      nm=ds_r.nm;
    } // if isdeep = d

    if (nm <= 0.0) throw new SGP4Exception("nm <= 0.0",2,tsince);
    double ratio=(this.whichconst.xke / nm);
    double power=pow(ratio,x2o3);
    am = power * tempa * tempa;
    nm = this.whichconst.xke / pow(am, 1.5);
    em = em - tempe;

    // fix tolerance for error recognition
    if ((em >= 1.0) || (em < -0.001) || (am < 0.95)) throw new SGP4Exception("em or am out of range",1,tsince);
    if (em < 0.0) em  = 1.0e-6;
    mm     = mm + this.no * templ;
    xlm    = mm + argpm + nodem;
    emsq   = em * em;
    temp   = 1.0 - emsq;

    nodem  = (nodem)%(twopi);
    argpm  = (argpm)%(twopi);
    xlm    = (xlm)%(twopi);
    mm     = (xlm - argpm - nodem)%(twopi);

    /* ----------------- compute extra mean quantities ------------- */
    sinim = sin(inclm);
    cosim = cos(inclm);

    /* -------------------- add lunar-solar periodics -------------- */
    ep     = em;
    xincp  = inclm;
    argpp  = argpm;
    nodep  = nodem;
    mp     = mm;
    sinip  = sinim;
    cosip  = cosim;
    if (this.isdeep) {
      dpper_return dpp_r=new dpper_return();
      //       double ep,inclp,nodep,argpp,mp;
      dpp_r.ep=ep;
      dpp_r.inclp=xincp;
      dpp_r.nodep=nodep;
      dpp_r.argpp=argpp;
      dpp_r.mp=mp;
      dpper(tsince,dpp_r);
      ep=dpp_r.ep;
      xincp=dpp_r.inclp;
      nodep=dpp_r.nodep;
      argpp=dpp_r.argpp;
      mp=dpp_r.mp;
      if (xincp < 0.0) {
      xincp  = -xincp;
      nodep = nodep + PI;
      argpp  = argpp - PI;
      }
      if ((ep < 0.0 ) || ( ep > 1.0)) throw new SGP4Exception("Corrected eccentricity out of range",3,tsince);
      /* -------------------- long period periodics ------------------ */

      sinip =  sin(xincp);
      cosip =  cos(xincp);
      l_aycof = -0.5*this.whichconst.j3oj2*sinip;
      // sgp4fix for divide by zero for xincp = 180 deg
      if (abs(cosip+1.0) > 1.5e-12) {
        l_xlcof = -0.25 * this.whichconst.j3oj2 * sinip * (3.0 + 5.0 * cosip) / (1.0 + cosip);
      } else {
        l_xlcof = -0.25 * this.whichconst.j3oj2 * sinip * (3.0 + 5.0 * cosip) / temp4;
      }
    } else {
      l_aycof=this.aycof;
      l_xlcof=this.xlcof;
    }
    axnl = ep * cos(argpp);
    temp = 1.0 / (am * (1.0 - ep * ep));
    aynl = ep* sin(argpp) + temp * l_aycof;
    xl   = mp + argpp + nodep + temp * l_xlcof * axnl;

    /* --------------------- solve kepler's equation --------------- */
    u    = (xl - nodep)%(twopi);
    eo1  = u;
    tem5 = 9999.9;
    ktr = 1;
    //   sgp4fix for kepler iteration
    //   the following iteration needs better limits on corrections
    do {//while (( abs(tem5) >= 1.0e-12) && (ktr <= 10) )
         //CDJ made this a do loop to tell the compiler that the loop runs at least once
         //so it knows that certain variables are always initialized and can be read
         //below the loop
      sineo1 = sin(eo1);
      coseo1 = cos(eo1);
      tem5   = 1.0 - coseo1 * axnl - sineo1 * aynl;
      tem5   = (u - aynl * coseo1 + axnl * sineo1 - eo1) / tem5;
      if(abs(tem5) >= 0.95) tem5 = tem5 > 0.0 ? 0.95 : -0.95;
      eo1    = eo1 + tem5;
      ktr = ktr + 1;
    } while (( abs(tem5) >= 1.0e-12) && (ktr <= 10) );

     /* ------------- short period preliminary quantities ----------- */
     ecose = axnl*coseo1 + aynl*sineo1;
     esine = axnl*sineo1 - aynl*coseo1;
     el2   = axnl*axnl + aynl*aynl;
     pl    = am*(1.0-el2);
     if (pl < 0.0) throw new SGP4Exception(String.format("error pl %f",pl),4,tsince);

     double[] rv=new double[6];
     rl     = am * (1.0 - ecose);
     rdotl  = sqrt(am) * esine/rl;
     rvdotl = sqrt(pl) / rl;
     betal  = sqrt(1.0 - el2);
     temp   = esine / (1.0 + betal);
     sinu   = am / rl * (sineo1 - aynl - axnl * temp);
     cosu   = am / rl * (coseo1 - axnl + aynl * temp);
     su     = atan2(sinu, cosu);
     sin2u  = (cosu + cosu) * sinu;
     cos2u  = 1.0 - 2.0 * sinu * sinu;
     temp   = 1.0 / pl;
     temp1  = 0.5 * this.whichconst.j2 * temp;
     temp2  = temp1 * temp;

     /* -------------- update for short period periodics ------------ */
     if (this.isdeep) {
       cosisq                 = cosip * cosip;
       l_con41  = 3.0*cosisq - 1.0;
       l_x1mth2 = 1.0 - cosisq;
       l_x7thm1 = 7.0*cosisq - 1.0;
     } else {
       l_con41=this.con41;
       l_x1mth2=this.x1mth2;
       l_x7thm1=this.x7thm1;
     }
     mrt   = rl * (1.0 - 1.5 * temp2 * betal * l_con41) + 0.5 * temp1 * l_x1mth2 * cos2u;
     su    = su - 0.25 * temp2 * l_x7thm1 * sin2u;
     xnode = nodep + 1.5 * temp2 * cosip * sin2u;
     xinc  = xincp + 1.5 * temp2 * cosip * sinip * cos2u;
     mvt   = rdotl - nm * temp1 * l_x1mth2 * sin2u / this.whichconst.xke;
     rvdot = rvdotl + nm * temp1 * (l_x1mth2 * cos2u + 1.5 * l_con41) / this.whichconst.xke;

     /* --------------------- orientation vectors ------------------- */
     sinsu =  sin(su);
     cossu =  cos(su);
     snod  =  sin(xnode);
     cnod  =  cos(xnode);
     sini  =  sin(xinc);
     cosi  =  cos(xinc);
     xmx   = -snod * cosi;
     xmy   =  cnod * cosi;
     ux    =  xmx * sinsu + cnod * cossu;
     uy    =  xmy * sinsu + snod * cossu;
     uz    =  sini * sinsu;
     vx    =  xmx * cossu - cnod * sinsu;
     vy    =  xmy * cossu - snod * sinsu;
     vz    =  sini * cossu;

     /* --------- position and velocity (in km and km/sec) ---------- */
     rv[0] = (mrt * ux)* this.whichconst.rekm;
     rv[1] = (mrt * uy)* this.whichconst.rekm;
     rv[2] = (mrt * uz)* this.whichconst.rekm;
     rv[3] = (mvt * ux + rvdot * vx) * vkmpersec;
     rv[4] = (mvt * uy + rvdot * vy) * vkmpersec;
     rv[5] = (mvt * uz + rvdot * vz) * vkmpersec;

     // sgp4fix for decaying satellites
     if (mrt < 1.0) {
       throw new SGP4Exception(String.format("Decay condition %11.6f",mrt),6,tsince);
     }
     return rv;
	}  // end sgp4

	/* -----------------------------------------------------------------------------
	*
	*                           function gstime
	*
	*  this function finds the greenwich sidereal time.
	*
	*  author        : david vallado                  719-573-2600    1 mar 2001
	*
	*  inputs          description                    range / units
	*    jdut1       - julian date in ut1             days from 4713 bc
	*
	*  outputs       :
	*    gstime      - greenwich sidereal time        0 to 2pi rad
	*
	*  locals        :
	*    temp        - temporary variable for doubles   rad
	*    tut1        - julian centuries from the
	*                  jan 1, 2000 12 h epoch (ut1)
	*
	*  coupling      :
	*    none
	*
	*  references    :
	*    vallado       2004, 191, eq 3-45
	* --------------------------------------------------------------------------- */

	private static double gstime(double jdut1) {
    final double twopi = 2.0 * PI;
    final double deg2rad = PI / 180.0;
    double       temp, tut1;

    tut1 = (jdut1 - 2451545.0) / 36525.0;
    double a3=-6.2e-6* tut1 * tut1 * tut1;
    double a2=+ 0.093104 * tut1 * tut1;
    double a1=(876600.0*3600 + 8640184.812866) * tut1;
    double a0=67310.54841;
    temp =  a3+a2+a1+a0;  // sec
    temp = -6.2e-6* tut1 * tut1 * tut1 + 0.093104 * tut1 * tut1 +
           (876600.0*3600 + 8640184.812866) * tut1 + 67310.54841;  // sec
    temp = (temp * deg2rad / 240.0)%(twopi); //360/86400 = 1/240, to deg, to rad

    // ------------------------ check quadrants ---------------------
    if (temp < 0.0) temp += twopi;
	  return temp;
  }  // end gstime

  public static void main(String args[]) throws SGP4Exception {
    TwoLineElement Tle=new TwoLineElement(
            "1 31304U 07015A   07115.96127779 -.01194035 +00000-0 -12850-0 0 00033",
            "2 31304 097.7911 213.9513 0010672 304.2182 055.8024 14.91490047000022");
    SGP4Core Sgp4=new SGP4Core(Tle);
    double[] rv=Sgp4.sgp4(0);
    for(int i=0;i<3;i++) System.out.print(rv[i  ]+" "); System.out.println();
    for(int i=0;i<3;i++) System.out.print(rv[i+3]+" "); System.out.println();
  }

}
