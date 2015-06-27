package org.kwansystems.tools.rootfind.optimize;

import static java.lang.Math.*;

/**
 * Implements the downhill simplex (amoeba) multidimensional optimizer.
 */
public class Amoeba {
  /**
   * A small number
   */
  private static final double TINY=1.0e-10; 
  /**
   * Maximum allowed number of function evaluations. 
   */
  private static final double NMAX=5000;    
  /**
   * Multidimensional minimization of a function, by the downhill simplex method of Nelder and Mead.
   * @param p Simplex. The array p[1..ndim+1] [1..ndim] is the input simplex. Its ndim+1 rows are ndim-dimensional 
   * vectors which are the vertices of the simplex. On output, p will have been reset to ndim+1 new points 
   * all within ftol of a local minimum.
   * @param y Function evaluated at vertices of simplex. Must be preinitialized to the values of funk 
   * evaluated at the ndim+1 vertices (rows) of p. On output, y will have the function evaluated
   * at all the vertices of the minimizing simplex.
   * @param ftol Fractional convergence tolerance. Upon successful completion, all the corners of the simplex, when
   * evaluated, will be within ftol of a minimum.
   * @param funk Function to be minimized
   * @return Number of function evaluations required
   */
  public static int amoeba(double[][] p, double[] y, double ftol, OptimizeMultiDFunction funk) {
    int ndim=y.length-1;
    int i,ihi,ilo,inhi,j,mpts=ndim+1; 
    double rtol,sum,swap,ysave,ytry;
    double[] psum;
    psum=new double[ndim];

    int nfunk=0;
    for (j=0;j<ndim;j++) {
      for (sum=0.0,i=0;i<mpts;i++) sum += p[i][j];
      psum[j]=sum;
    }
    for (;;) { 
      ilo=0; 
      /*First we must determine which point is the highest (worst), next-highest,
        and lowest (best), by looping over the points in the simplex. */
      if(y[0]>y[1]) {
        inhi=1;
        ihi=0;
      } else {
        inhi=0;
        ihi=1; 
      }
      for (i=0;i<mpts;i++) { 
        if (y[i] <= y[ilo]) ilo=i;
        if (y[i] > y[ihi]) { 
          inhi=ihi; ihi=i; 
        } else if (y[i] > y[inhi] && i != ihi) inhi=i; 
      } 
      rtol=2.0*abs(y[ihi]-y[ilo])/(abs(y[ihi])+abs(y[ilo])+TINY); 
      //Compute the fractional range from highest to lowest and return if satisfactory. 
      if (rtol < ftol) { 
        //If returning, put best point and value in slot 1. 
        swap=y[0];
        y[0]=y[ilo];
        y[ilo]=swap;
        for (i=0;i<ndim;i++) {
          swap=p[0][i];
          p[0][i]=p[ilo][i];
          p[ilo][i]=swap;
        }
        break; 
      } 
      if (nfunk >= NMAX) throw new ArithmeticException("NMAX exceeded"); 
      nfunk += 2; 
      /*Begin a new iteration. First extrapolate by a factor  1 through the face
        of the simplex across from the high point, i.e., reflect the simplex 
        from the high point. */
      ytry=amotry(p,y,psum,funk,ihi,-1.0); 
      if (ytry <= y[ilo]) //Gives a result better than the best point, so try an additional extrapolation by a factor 2. 
        ytry=amotry(p,y,psum,funk,ihi,2.0); 
      else if (ytry >= y[inhi]) { 
        /*The reflected point is worse than the second-highest, so look for an 
         intermediate lower point, i.e., do a one-dimensional contraction. */
        ysave=y[ihi]; 
        ytry=amotry(p,y,psum,funk,ihi,0.5); 
        if (ytry >= ysave) { 
          //Can't seem to get rid of that high point. Better contract around the
          //lowest (best) point. 
          for (i=0;i<mpts;i++) {  
            if(i != ilo) { 
              for (j=0;j<ndim;j++) p[i][j]=psum[j]=0.5*(p[i][j]+p[ilo][j]);
              y[i]=funk.eval(psum); 
            } 
          } 
          nfunk += ndim; //Keep track of function evaluations. 
          //GET_PSUM //Recompute psum. 
          for (j=0;j<ndim;j++) {
            for (sum=0.0,i=0;i<mpts;i++) sum += p[i][j];
            psum[j]=sum;
          }
        } 
      } else nfunk--; // Correct the evaluation count. 
    } //Go back for the test of doneness and the next iteration. 
    return nfunk;
  }
  /**
   * Simplex improver. Extrapolates by a factor fac through the face of the simplex across from 
   * the high point, tries it, and replaces the high point if the new point is better.
   * @param p Simplex
   * @param y Function evaluated at all the corners of the simplex
   * @param psum Working variable
   * @param funk Function to be minimized
   * @param ihi Index of highest point on the simplex
   * @param fac Extrapolation factor
   * @return Function value at new corner of simplex
   */
  private static double amotry(double[][] p, double[] y, double[] psum, OptimizeMultiDFunction funk, int ihi, double fac) {
    int j; 
    double fac1,fac2,ytry;
    double[] ptry;
    int ndim=y.length-1;
    ptry=new double[ndim]; 
    fac1=(1.0-fac)/ndim; 
    fac2=fac1-fac; 
    for (j=0;j<ndim;j++) ptry[j]=psum[j]*fac1-p[ihi][j]*fac2; 
    ytry=funk.eval(ptry); //Evaluate the function at the trial point. 
    if (ytry < y[ihi]) { //If it s better than the highest, then replace the highest.
      y[ihi]=ytry;
      for (j=0;j<ndim;j++) {
        psum[j] += ptry[j]-p[ihi][j]; 
        p[ihi][j]=ptry[j]; 
      } 
    } 
    return ytry; 
  }
  /**
   * Builds a simplex based on an initial guess and an epsilon for each dimension
   * @param initialGuess A point close to a local minimum
   * @param epsilon Size of each edge of initial simplex
   * @param ftol Fractional convergence tolerance
   * @param funk Function to minimize
   * @return A point where all elements are within ftol of a local minimum
   */
  public static double[] amoeba(double[] initialGuess, double[] epsilon, double ftol, OptimizeMultiDFunction funk) {
    int ndim=initialGuess.length;
    double[][] p=new double[ndim+1][ndim];
    double[] y=new double[ndim+1];
    //Initialize simplex points
    for(int i=0;i<ndim+1;i++) {
      for(int j=0;j<ndim;j++) {
        p[i][j]=initialGuess[j];
      }
    }
    for(int i=0;i<ndim;i++) {
      p[i+1][i]+=epsilon[i];
    }
    //Get first function values
    for(int i=0;i<ndim+1;i++) {
      y[i]=funk.eval(p[i]);
    }
    amoeba(p, y, ftol, funk);
    return p[0];
  }
  /**
   * Builds a simplex based on an initial guess and a single epsilon for all dimensions
   * Returns a point where the function is within ftol of a local minimum
   * @param initialGuess A point close to a local minimum
   * @param eps Size of all edges of initial simplex
   * @param ftol Fractional convergence tolerance
   * @param funk Function to minimize
   * @return A point where all elements are within ftol of a local minimum
   */
  public static double[] amoeba(double[] initialGuess, double eps, double ftol, OptimizeMultiDFunction funk) {
    double epsilon[]=new double[initialGuess.length];
    for(int i=0;i<epsilon.length;i++) epsilon[i]=eps;
    return amoeba(initialGuess,epsilon,ftol, funk);
  }
  /**
   * Runs amoeba with a default simplex size
   * @param initialGuess A point close to a local minimum
   * @param ftol Fractional convergence tolerance
   * @param funk Function to minimize
   * @return A point where all elements are within ftol of a local minimum
   */
  public static double[] amoeba(double[] initialGuess, double ftol, OptimizeMultiDFunction funk) {
    return amoeba(initialGuess,0.1,ftol,funk);
  }
  /**
   * Runs amoeba with a default simplex size and tolerance
   * @param initialGuess A point close to a local minimum
   * @param funk Function to minimize
   * @return A point where all elements are within ftol of a local minimum
   */
  public static double[] amoeba(double[] initialGuess, OptimizeMultiDFunction funk) {
    return amoeba(initialGuess,0.01,funk);
  }
  /**
   * Tests out the Amoeba function
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    OptimizeMultiDFunction A=new OptimizeMultiDFunction() {
      public double eval(double[] args) {
        return args[0]*args[0]+args[1]*args[1];
      }
    };
    double[] P=Amoeba.amoeba(new double[] {1,1}, A);
    for(int i=0;i<P.length;i++) System.out.println(P[i]);
    double Y=A.eval(P);
    System.out.println(Y);
  }
}
