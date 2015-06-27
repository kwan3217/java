package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.Unlimited;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.Literal;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.arraytype;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.realtype;

public class PsMatrix {
  double[] MatrixData;
  public PsMatrix() {
    MatrixData=new double[] {1,0,0,1,0,0};
  }
  public PsMatrix(double a, double b, double c, double d, double e, double f) {
    MatrixData=new double[] {a,b,c,d,e,f};
  }
  public PsMatrix(double[] LMatrixData) {
    MatrixData=LMatrixData.clone();
  }
  public PsMatrix(PsMatrix LMatrix) {
    this(LMatrix.MatrixData);
  }
  public PsMatrix(PsArray A) {
    this(new double[] {
        A.get(0).GetNumber(),
        A.get(1).GetNumber(),
        A.get(2).GetNumber(),
        A.get(3).GetNumber(),
        A.get(4).GetNumber(),
        A.get(5).GetNumber()
    });
  }
  public double[] get() {
    return MatrixData.clone();
  }
  public PsObject toPsArray(ExecContext EC) {
    PsObject[] Elements=new PsObject[6];
    for(int i=0;i<6;i++) Elements[i]=new PsObject(realtype,Literal,Unlimited, new Double(MatrixData[i]));
    PsArray value=new PsArray(Elements,EC);
    return new PsObject(arraytype,Literal,Unlimited, value);
  }
  /*
  Matrix Multiplication
  [a1 c1 e1][a2 c2 e2]   [a1a2+c1b2+e1*0 a1c2+c1d2+e1*0 a1e2+c1f2+e1*1] 
  [b1 d1 f1][b2 d2 f2] = [b1a2+d1b2+f1*0 b1c2+d1d2+f1*0 b1e2+d1f2+f1*1]
  [0  0  1 ][0  0  1 ]   [0*a2+0*b2+1 *0 0*c2+0*d2+1 *0 0*e2+0*f2+1 *1]
     =
  [a1a2+c1b2 a1c2+c1d2 a1e2+c1f2+e1]
  [b1a2+d1b2 b1c2+d1d2 b1e2+d1f2+f1]
  [0         0         1           ]   
   so the last row of a matrix is indestructible by matrix multiplication   
  */
  public void Mult(PsMatrix B) {
    double[]result=new double[6];
    result[0]=MatrixData[0]*B.MatrixData[0]+MatrixData[2]*B.MatrixData[1]; result[2]=MatrixData[0]*B.MatrixData[2]+MatrixData[2]*B.MatrixData[3]; result[4]=MatrixData[0]*B.MatrixData[4]+MatrixData[2]*B.MatrixData[5]+MatrixData[4];
    result[1]=MatrixData[1]*B.MatrixData[0]+MatrixData[3]*B.MatrixData[1]; result[3]=MatrixData[1]*B.MatrixData[2]+MatrixData[3]*B.MatrixData[3]; result[5]=MatrixData[1]*B.MatrixData[4]+MatrixData[3]*B.MatrixData[5]+MatrixData[5];
    MatrixData=result;
  }
  public static PsMatrix Mult(PsMatrix A, PsMatrix B) {
    PsMatrix result=new PsMatrix(A);
    result.Mult(B);
    return result;
  }

  /*
  Matrix-Vector multiplication
  [a c e][x]   [a*x+c*y+e*1] 
  [b d f][y] = [b*x+d*y+f*1]
  [0 0 1][1]   [0*x+0*y+1*1]
  */
  double TransformX(double x,double y) {
    return x*MatrixData[0]+y*MatrixData[2]+MatrixData[4];
  }
  double TransformY(double x,double y) {
    return x*MatrixData[1]+y*MatrixData[3]+MatrixData[5];
  }
  double[] Transform(double x, double y) {
    return new double[] {TransformX(x,y),TransformY(x,y)};
  }
  /*
  Inverse transform derivation
  Forward transform
  [a c e][x]   [a*x+c*y+e*1]   [x']
  [b d f][y] = [b*x+d*y+f*1] = [y']
  [0 0 1][1]   [0*x+0*y+1*1]   [1 ]
  Gaussian elimination
  [a c e | x']
  [b d f | y'] -b/a(row 1)=-b/a[a c e x']=[-b -bc/a -be/a -bx'/a]
  [0 0 1 | 1 ]

  [a c      e      | x'      ]
  [0 d-bc/a f-be/a | y'-bx'/a] k1=d-bc/a k2=f-be/a k3=y'-bx'/a
  [0 0      1      | 1       ]

  [a c  e  | x']
  [0 k1 k2 | k3]
  [0 0  1  | 1 ]
  {ax+c y+e =x'
  {   k1y+k2=k3
  backsubstitution
  y=(k3-k2)/k1
  x=(x'-cy-e)/a
*/
  double[] InvTransform(double[] xpyp) {
    double xp=xpyp[0];
    double yp=xpyp[1];
    double k1=MatrixData[3]-MatrixData[1]*MatrixData[2]/MatrixData[0];
    double k2=MatrixData[5]-MatrixData[1]*MatrixData[4]/MatrixData[0];
    double k3=yp-MatrixData[1]*xp/MatrixData[0];
    double y=(k3-k2)/k1;
    double x=(xp-MatrixData[2]*y-MatrixData[4])/MatrixData[0];
    return new double[] {x,y};
  }
  double InvTransformX(double xp, double yp) {
    return InvTransform(new double[] {xp,yp})[0];
  }
  double InvTransformY(double xp, double yp) {
    return InvTransform(new double[] {xp,yp})[1];
  }
  
  /* Matrices
  A transform matrix is of the form
  [a  c  e]
  [b  d  f]
  [0  0  1] (This is the transpose of how square matrices are shown in the Red Book)
  and is represented in PostScript as [a b c d e f] (This matches the Red Book)
  since the last row is always [0 0 1]
  Identity is
  [1 0 0]
  [0 1 0]
  [0 0 1]
  =
  [1  0  0  1  0  0 ]

  translate is
  [1  0  tx]
  [0  1  ty]
  [0  0  1 ]
  = 
  [0  0  0  0  tx ty]
  
  scale is
  [sx 0  0]
  [0  sy 0]
  [0  0  1]
  =
  [sx 0  0  sy 0  0 ]
  rotate is
  [c  -s 0]
  [s  c  0]
  [0  0  1]
  =
  [c  s  -s c  0  0 ]
  where c=cos(theta) and s=sin(theta)
  
  Matrix-Vector multiplication
  [a c e][x]   [a*x+c*y+e*1] 
  [b d f][y] = [b*x+d*y+f*1]
  [0 0 1][1]   [0*x+0*y+1*1]

  Matrix Multiplication
  [a1 c1 e1][a2 c2 e2]   [a1a2+c1b2+e1*0 a1c2+c1d2+e1*0 a1e2+c1f2+e1*1] 
  [b1 d1 f1][b2 d2 f2] = [b1a2+d1b2+f1*0 b1c2+d1d2+f1*0 b1e2+d1f2+f1*1]
  [0  0  1 ][0  0  1 ]   [0*a2+0*b2+1 *0 0*c2+0*d2+1 *0 0*e2+0*f2+1 *1]
     =
  [a1a2+c1b2 a1c2+c1d2 a1e2+c1f2+e1]
  [b1a2+d1b2 b1c2+d1d2 b1e2+d1f2+f1]
  [0         0         1           ]
  so the last row of a matrix is indestructible by matrix multiplication   
  */

}
