package org.kwansystems.tools.rotation;

import org.kwansystems.tools.vector.*;
import static java.lang.Math.*;

/**
 * A two-dimensional matrix object. This object includes all 
 * the common and less common math operations on a matrix.
 */
public class MathMatrix extends Rotator {
  //first index is row, second is column, in keeping with normal textbooks and the opposite of IDL
  private final double[][] values;

  public MathMatrix(double[][] lvalues) {
    int len=lvalues[0].length;
    for(int i=1;i<lvalues.length;i++) if(lvalues[i].length!=len) throw new IllegalArgumentException("Must pass a rectangular array");
    values=lvalues;
  }
  public MathMatrix(MathMatrix M) {
    this((double[][])M.values.clone());
    for(int i=0;i<values.length;i++) values[i]=(double[])M.values[i].clone();
  }
  public MathMatrix (MathVector[] vectors, boolean isRowMatrix) {
    if(isRowMatrix) {
      values=new double[vectors.length][vectors[0].dimension()];
      for(int row=0;row<values.length;row++) {
        if(vectors[row].dimension()!=values[row].length) throw new IllegalArgumentException("Tried to build a row matrix with unequal dimensioned vectors");
        for(int column=0;column<values[row].length;column++) {
          values[row][column]=vectors[row].get(column);
        }
      }
    } else {
      values=new double[vectors[0].dimension()][vectors.length];
      for(int row=0;row<values.length;row++) {
        for(int column=0;column<values[row].length;column++) {
          if(vectors[column].dimension()!=values.length) throw new IllegalArgumentException("Tried to build a column matrix with unequal dimensioned vectors");
          values[row][column]=vectors[column].get(row);
        }
      }
    }
  }
  public MathMatrix(double[] diagonals) {
    int len=diagonals.length;
    values=new double[len][len];
    for(int i=0;i<diagonals.length;i++) {
      values[i][i]=diagonals[i];
    }
  }
  public MathMatrix(double x, double y, double z) {
    this(new double[] {x,y,z});
  }
  public MathMatrix(int Lrows,int Lcols) {
    this(new double[Lrows][Lcols]);
  }
  public static void AssertEqualSize(MathMatrix A, MathMatrix B) throws IllegalArgumentException {
    if(A.rows()!=B.rows() || A.cols()!=B.cols()) throw new IllegalArgumentException("Matrix sizes are not identical (A="+A.rows()+"x"+A.cols()+", B="+B.rows()+"x"+B.cols()+")");
  }
  public int rows() {return values.length;}
  public int cols() {return values[0].length;}
  public String toString(String format) {
    StringBuffer S;
    S=new StringBuffer();
    S.append("[");
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        if(format!=null) {
          S.append(String.format(format,values[row][col]));
        } else {
          S.append(Double.toString(values[row][col]));
        }
        if(col<cols()-1) S.append(",");
      }
      if(row<rows()-1) S.append(";\n "); else S.append("]");
    }
    return S.toString();
  }
  @Override
  public String toString() {
    return toString(null);
  }
  public double get(int row, int col) {
    return values[row][col];
  }
  public void set(int row, int col, double x) {
	values[row][col]=x;
  }
  public MathVector getRow(int row) {
	return new MathVector(values[row]);
  }
  public MathVector getCol(int col) {
	double[] result=new double[values.length];
	for(int i=0;i<values.length;i++) result[i]=values[i][col];
	return new MathVector(result);
  }
  private double[][] getNewValues() {
    return new double[values.length][values[0].length];
  }
  public MathMatrix T() {
    double[][] newValues=new double[cols()][rows()];
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        newValues[col][row]=get(row,col);
      }
    }
    return new MathMatrix(newValues);
  }
  public MathMatrix mul(double S) {
    double[][] newValues=getNewValues();
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        newValues[row][col]=get(row,col)*S;
      }
    }
    return new MathMatrix(newValues);
  }
  public void mulEq(double S) {
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        values[row][col]=get(row,col)*S;
      }
    }
  }
  public MathMatrix add(double S) {
    double[][] newValues=getNewValues();
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        newValues[row][col]=get(row,col)+S;
      }
    }
    return new MathMatrix(newValues);
  }
  public MathMatrix sub(double S) {
    double[][] newValues=getNewValues();
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        newValues[row][col]=get(row,col)-S;
      }
    }
    return new MathMatrix(newValues);
  }
  public static MathMatrix add(MathMatrix A, MathMatrix B) {
    AssertEqualSize(A,B);
    double[][] newValues=A.getNewValues();
    for(int row=0;row<A.rows();row++) {
      for(int col=0;col<A.cols();col++) {
        newValues[row][col]=A.get(row,col)+B.get(row,col);
      }
    }
    return new MathMatrix(newValues);
  }
  public MathMatrix add(MathMatrix B) {
    return MathMatrix.add(this,B);
  }
  public void addEq(MathMatrix B) {
    AssertEqualSize(this,B);
    for(int row=0;row<rows();row++) {
      for(int col=0;col<cols();col++) {
        values[row][col]+=B.get(row,col);
      }
    }
  }

  public static MathMatrix sub(MathMatrix A, MathMatrix B) {
    AssertEqualSize(A,B);
    double[][] newValues=A.getNewValues();
    for(int row=0;row<A.rows();row++) {
      for(int col=0;col<A.cols();col++) {
        newValues[row][col]=A.get(row,col)-B.get(row,col);
      }
    }
    return new MathMatrix(newValues);
  }
  public static MathMatrix mul(MathMatrix A, MathMatrix B) {
    if(A.cols()!=B.rows()) throw new IllegalArgumentException("Incompatible matrices passed to mult: A is "+A.rows()+"x"+A.cols()+" and B is "+B.rows()+"x"+B.cols());
    double[][] newValues=new double[A.rows()][B.cols()];
    for(int row=0;row<A.rows();row++) {
      for(int col=0;col<B.cols();col++) {
        double cell=0;
        for(int index=0;index<A.cols();index++) {
          cell+=A.get(row,index)*B.get(index,col);
        }
        newValues[row][col]=cell;
      }
    }
    return new MathMatrix(newValues);
  }
  public static MathVector mul(MathMatrix A, MathVector x) {
    return A.transform(x);
  }
  public MathMatrix mul(MathMatrix B) {
    return MathMatrix.mul(this,B);
  }
  public MathVector mul(MathVector B) {
    return MathMatrix.mul(this,B);
  }
  public static MathMatrix Identity(int size) {
    double[][] newValues=new double[size][size];
    for(int i=0;i<size;i++) newValues[i][i]=1;
    return new MathMatrix(newValues);
  }
  public static MathMatrix rowMatrix(MathVector A, MathVector B, MathVector C) {
    return new MathMatrix(new double[][] {{A.X(),A.Y(),A.Z()},
                                          {B.X(),B.Y(),B.Z()},
                                          {C.X(),C.Y(),C.Z()}});
  }
  public static MathMatrix colMatrix(MathVector A, MathVector B, MathVector C) {
    return rowMatrix(A,B,C).T();
  }
  /**
   * Linear equation Ax=B solution by Gauss-Jordan elimination. This matrix is used as A. 
   * @param B Right side vector
   * @return Solution vector
   */
  public MathVector GaussJordan(MathVector B) {
    double[][] a=values.clone();
    double[][] b=new double[1][B.dimension()];
    for(int i=0;i<B.dimension();i++)b[0][i]=B.get(i);
    GaussJordan(a,b);
    double[] x=new double[B.dimension()];
    for(int i=0;i<B.dimension();i++)x[i]=b[0][i];
    return new MathVector(x);
  }
  /**
   * Multiple linear equation Ax=B solution by Gauss-Jordan elimination. This matrix is used as A. 
   * @param B Array of right side vectors
   * @return Array of solution vectors
   */
  public MathVector[] GaussJordan(MathVector[] B) {
    double[][] a=values.clone();
    double[][] b=new double[B.length][B[0].dimension()];
    for(int j=0;j<B.length;j++) {
      if(B[j].dimension()!=B[0].dimension()) throw new IllegalArgumentException("All vectors in B must be the same length");
      for(int i=0;i<B[j].dimension();i++)b[j][i]=B[j].get(i);
    }
    GaussJordan(a,b);
    MathVector[] X=new MathVector[B.length];
    for(int j=0;j<B.length;j++) {
      double[] x=new double[B[j].dimension()];
      for(int i=0;i<B[j].dimension();i++)x[i]=b[j][i];
      X[j]=new MathVector(x);
    }
    return X;
  }
  /**
   * Linear equation Ax=B solution by Gauss-Jordan elimination. 
   * @param a Input matrix. On output, this is replaced by the inverse of the input matrix
   * @param b Matrix of column vectors. On output, replaced by vector of solutions x
   */
  private static void GaussJordan(double[][] a, double [][]b) {

    int n=a.length;
    int m=b[0].length;
    int[] indxc,indxr,ipiv;
    int i,icol=0,irow=0,j,k,l,ll;
    double big,dum,pivinv,swap;
    indxc=new int[n]; //The integer arrays ipiv, indxr,andindxc are
                      //used for bookkeeping on the pivoting.
    indxr=new int[n];
    ipiv=new int[n];
    for (j=0;j<n;j++) ipiv[j]=0;
    for (i=0;i<n;i++) { //This is the main loop over the columns to be reduced.
      big=0.0;
      for (j=0;j<n;j++) { //This is the outer loop of the search for a pivot element.
        if (ipiv[j] != 1)  {
          for (k=0;k<n;k++) {
            if (ipiv[k] == 0) {
              if (Math.abs(a[j][k]) >= big) {
                big=Math.abs(a[j][k]);
                irow=j;
                icol=k;
              }
            }
          }
        }
      }
      ++(ipiv[icol]);
      //We now have the pivot element, so we interchange rows, if needed, to put the pivot
      //element on the diagonal. The columns are not physically interchanged, only relabeled:
      //indxc[i],the column of the ith pivot element, is the ith column that is reduced, while
      //indxr[i] is the row in which that pivot element was originally located. If indxr[i] !=
      //indxc[i] there is an implied column interchange. With this form of bookkeeping, the
      //solution b's will end up in the correct order, and the inverse matrix will be scrambled
      //by columns.
      if (irow != icol) {
        for (l=0;l<n;l++) {swap=a[irow][l];a[irow][l]=a[icol][l];a[icol][l]=swap;}
        for (l=0;l<m;l++) {swap=b[irow][l];b[irow][l]=b[icol][l];b[icol][l]=swap;}
      }
      indxr[i]=irow; // We are now ready to divide the pivot row by the
                     // pivot element, located at irow and icol.
      indxc[i]=icol;
      if (a[icol][icol] == 0.0) throw new IllegalArgumentException("Attempted to solve a singular matrix equation");
      pivinv=1.0/a[icol][icol];
      a[icol][icol]=1.0;
      for (l=0;l<n;l++) a[icol][l] *= pivinv;
      for (l=0;l<m;l++) b[icol][l] *= pivinv;
      for (ll=0;ll<n;ll++) {// Next, we reduce the rows...
        if (ll != icol) {   // ...except for the pivot one, of course.
          dum=a[ll][icol];
          a[ll][icol]=0.0;
          for (l=0;l<n;l++) a[ll][l] -= a[icol][l]*dum;
          for (l=0;l<m;l++) b[ll][l] -= b[icol][l]*dum;
        }
      }
    }
    //This is the end of the main loop over columns of the reduction. It only remains to unscramble
    //the solution in view of the column interchanges. We do this by interchanging pairs of
    //columns in the reverse order that the permutation was built up.
    for (l=n-1;l>=0;l--) {
      if (indxr[l] != indxc[l]) {
        for (k=0;k<n;k++) {
          swap=a[k][indxr[l]];a[k][indxr[l]]=a[k][indxc[l]];a[k][indxc[l]]=swap;
        }
      }
    } 
  }
  public MathMatrix inv() {
    if(rows()!=cols()) throw new IllegalArgumentException("Only square matrices can be inverted");
    double[][] a=new MathMatrix(this).values;
    double[][] b=Identity(rows()).values;
    GaussJordan(a,b);
    return new MathMatrix(a);
  }
  public MathMatrix pinv() {
    MathMatrix result=new MathMatrix(this);
    result=MathMatrix.mul(result.T(),this);
    result=MathMatrix.mul(result.inv(),this.T());
    return result;
  }
  public MathVector transform(MathVector In) {
    //In is a Nx1 column vector
    //this must be a MxN matrix
    //Returns a Mx1 column matrix
    if(cols()!=In.dimension()) throw new IllegalArgumentException("Matrix and vector sizes do not match");
    double[] result=new double[rows()];
    for(int row=0;row<rows();row++) {
      result[row]=0;
      for(int col=0;col<cols();col++) {
        result[row]+=get(row,col)*In.get(col);
      }
    }
    return new MathVector(result);
  }
  public MathVector invTransform(MathVector in) {
	    return inv().transform(in);
	  }
  public MathVector traTransform(MathVector in) {
	    return T().transform(in);
	  }
  //These rotation matrices seem to be standard. They match 
  //Vallado, Lieske in GalileanE5, and the almanac, and are in
  //general uncontradicted, even if called by different names.
  //Each one has a continuous 2x2 block with cos(theta) on the diagonal,
  //+sin(theta) on the upper half, and -sin(theta) on the lower half.
  //You have to connect the edges of Rot2 to see this continuous block,
  //but it is there. This gives a right-handed frame rotation around 
  //each axis, or a left-handed physical rotation
  public static MathMatrix Rot1(double Theta) {
    double S=Math.sin(Theta);
    double C=Math.cos(Theta);
    return new MathMatrix(new double[][] {
      { 1, 0, 0},
      { 0, C, S},
      { 0,-S, C}
    });
  }
  public static MathMatrix Rot2(double Theta) {
    double S=Math.sin(Theta);
    double C=Math.cos(Theta);
    return new MathMatrix(new double[][] {
      { C, 0,-S},
      { 0, 1, 0},
      { S, 0, C}
    });
  }
  public static MathMatrix Rot3(double Theta) {
    double S=Math.sin(Theta);
    double C=Math.cos(Theta);
    return new MathMatrix(new double[][] {
      { C, S, 0},
      {-S, C, 0},
      { 0, 0, 1}
    });
  }
  //Convenience functions to do rotations in degrees
  public static MathMatrix Rot1d(double Thetad) {
    return Rot1(Math.toRadians(Thetad));
  }
  public static MathMatrix Rot2d(double Thetad) {
    return Rot2(Math.toRadians(Thetad));
  }
  public static MathMatrix Rot3d(double Thetad) {
    return Rot3(Math.toRadians(Thetad));
  }
  /** Construct a double array compatible with the Java3D Transform3D constructor. This matrix
   * will be trimmed to fit as necessary and stuffed into the upper left corner of a 4x4 matrix,
   * then linearized. Diagonal elements not set in the original matrix are set to 1 in the result,
   * all other elements set to zero.
   * @return Double array suitable for input to a Transform3D constructor.
   */
  public double[] getJava3D() {
    double[] result=new double[16];
    for(int i=0;i<4;i++) result[i*4+i]=1.0; //Set identity elements first, no worries if they get overwritten
    for(int row=0;row<(rows()>4?4:rows());row++) {
      for(int col=0;col<(cols()>4?4:cols());col++) {
        result[row*4+col]=get(row,col);
      }
    }
    return result;
  }
  public static void main(String args[]) {
    double LAN=Math.toRadians(227.89);
    double AP=Math.toRadians(53.38);
    double I=Math.toRadians(87.87);
    MathMatrix PQWtoIJK=MathMatrix.mul(MathMatrix.mul(MathMatrix.Rot3(LAN),MathMatrix.Rot1(I)),MathMatrix.Rot3(AP));
//    MathMatrix PQWtoIJK=MathMatrix.Rot1(Math.PI/4);
    System.out.println(PQWtoIJK);
    MathVector X=new MathVector(1,0,0);
    System.out.println(PQWtoIJK.transform(X));
    System.out.println(new MathMatrix(new MathVector[] {new MathVector(1,2,3),
                                                        new MathVector(4,5,6),
                                                        new MathVector(7,8,9)},false));
                                                        
    //Test out point-toward using example from Kwan Hypertext Library
    MathVector p_b=new MathVector(cos(toRadians(13)),                   0,                                    -sin(Math.toRadians(13)));
    MathVector p_r=new MathVector(cos(toRadians(30))*sin(toRadians(80)),cos(toRadians(30))*cos(toRadians(80)), sin(Math.toRadians(30)));
    MathVector t_b=new MathVector(0,0, 1);
    MathVector t_r=new MathVector(0,0,-1);
    MathMatrix M_br=MathMatrix.pointToward(p_r, t_r, p_b, t_b);
    System.out.println(M_br);
    System.out.println(M_br.transform(p_b));
    System.out.println(M_br.transform(t_b));
    Quaternion q_br=M_br.toQuaternion();
    System.out.println(q_br);
    System.out.println(q_br.transform(p_b));
    System.out.println(q_br.transform(t_b));
    
    //Test out Cholesky decomposition according to Wikipedia example
    MathMatrix A=new MathMatrix(new MathVector[] {new MathVector(  4, 12,-16),
            new MathVector( 12, 37,-43),
            new MathVector(-16,-43, 98)},true);
    System.out.println(A);
    System.out.println(A.choldc());
    A.choldcEq();
    System.out.println(A);
    
    
  }
  /**
   * Constructs a new quaternion from a direction cosine matrix.
   * See  <a href="https://dejiko.kwansystems.org/wiki/index.php/Quaternion_to_Matrix#Matrix_to_Quaternion">
   * Matrix to Quaternion</a> for details and derivation.
   */
  @Override
  public Quaternion toQuaternion() {
    double w,x,y,z;
    double c11=get(0,0);double c12=get(0,1);double c13=get(0,2);
    double c21=get(1,0);double c22=get(1,1);double c23=get(1,2);
    double c31=get(2,0);double c32=get(2,1);double c33=get(2,2);
    w=sqrt(+c11+c22+c33+1.0)/2.0;
    if(w>0.5) {
      x=(c23-c32)/(4*w);  
      y=(c31-c13)/(4*w);  
      z=(c12-c21)/(4*w);  
    } else {
      x=sqrt(+c11-c22-c33+1.0)/2.0;
      if(x>0.5) {
        w=(c23-c32)/(4*x);
        y=(c12+c21)/(4*x);
        z=(c13+c31)/(4*x);
      } else {
        y=sqrt(-c11+c22-c33+1.0)/2.0;
        if(x>0.5) {
          w=(c31-c13)/(4*y);
          x=(c12+c21)/(4*y);
          z=(c23+c32)/(4*y);
        } else {
          z=sqrt(-c11-c22+c33+1.0)/2.0;
          w=(c12-c21)/(4*z);
          x=(c13+c31)/(4*z);
          y=(c23+c32)/(4*z);
        }
      }
    }
    return new Quaternion(x,y,z,w);
  }
  /**
   * Calculates the direction cosine matrix corresponding to this quaternion.
   * See <a href="https://dejiko.kwansystems.org/wiki/index.php/Quaternion_to_Matrix">
   * Quaternion to Matrix</a> for details and derivation.
   * 
   */
  public MathMatrix(Rotator R) {
    Quaternion Q=R.toQuaternion();
    double w=Q.W();
    double x=Q.X();
    double y=Q.Y();
    double z=Q.Z();
    
    double w2=w*w;
    double x2=x*x;
    double y2=y*y;
    double z2=z*z;
    values=new double[][] {
      {w2+x2-y2-z2, 2*(x*y+w*z),2*(x*z-w*y)},
      {2*(x*y-w*z), w2-x2+y2-z2,2*(y*z+w*x)},
      {2*(z*x+w*y), 2*(y*z-w*x),w2-x2-y2+z2}
    };
  }
  public void checkOrtho() {
    for(int i=0;i<3;i++) {
      MathVector RowI=new MathVector(get(i,0),get(i,1),get(i,2));
      MathVector ColI=new MathVector(get(0,i),get(1,i),get(2,i));
      System.out.println("Row vector "+i+" length: "+RowI.length());
      System.out.println("Col vector "+i+" length: "+ColI.length());
      for(int j=i+1;j<3;j++) {
        MathVector RowJ=new MathVector(get(j,0),get(j,1),get(j,2));
        MathVector ColJ=new MathVector(get(0,j),get(1,j),get(2,j));
        System.out.println("Row "+i+" dot Row "+j+": "+MathVector.dot(RowI,RowJ));
        System.out.println("Col "+i+" dot Col "+j+": "+MathVector.dot(ColI,ColJ));
        
      }
    }
  }

  public MathMatrix sub(MathMatrix B) {
    return MathMatrix.sub(this,B);
  }
  public static MathMatrix pointToward(MathVector p_r, MathVector t_r, MathVector p_b, MathVector t_b) {
	MathVector s_b=MathVector.cross(p_b,t_b).normal();
	MathVector u_b=MathVector.cross(p_b,s_b).normal();
	MathVector s_r=MathVector.cross(p_r,t_r).normal();
	MathVector u_r=MathVector.cross(p_r,s_r).normal();
    MathMatrix R=new MathMatrix(new MathVector[] {p_r,s_r,u_r},false);
	MathMatrix B=new MathMatrix(new MathVector[] {p_b,s_b,u_b},false);
	MathMatrix M_BR=MathMatrix.mul(R,B.T());
    return M_BR;
  }
  public MathMatrix choldc() throws IllegalArgumentException {
	MathMatrix result=new MathMatrix(this);
	result.choldcEq();
    for(int i_col=1;i_col<result.values.length;i_col++) for(int i_row=0;i_row<i_col;i_row++) result.values[i_row][i_col]=0; //erase the upper triangular
	return result;
  }
  public void choldcEq() throws IllegalArgumentException {
    //Copied straight from Wikipedia, so following their index names, except rebasing them at zero
	for(int j=0;j<values.length;j++) {
	  double acc=0;
	  for(int k=0;k<j;k++) acc+=values[j][k]*values[j][k];
	  double d=values[j][j]-acc;
	  if(d<0) throw new IllegalArgumentException("Input is not positive-definite");
	  values[j][j]=sqrt(d);
	  
	  for(int i=j+1;i<values.length;i++) {
		acc=0;
		for(int k=0;k<j;k++) acc+=values[i][k]*values[j][k];
		values[i][j]=(values[i][j]-acc)/values[j][j];
	  }
	}
  }
}
