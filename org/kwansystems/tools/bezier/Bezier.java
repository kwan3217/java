package org.kwansystems.tools.bezier;
import org.kwansystems.tools.vector.*;

import java.util.*;

public class Bezier {
  public MathVector R0,R1,R2,R3;
  public MathVector[] R;
  public static final double kappa=4*(Math.sqrt(2)-1)/3;
  private VectorPolynomial P,PPrime;
  double Flatness;
  double Cf;
  double Vp;
  public Bezier(MathVector LR0, MathVector LR1, MathVector LR2, MathVector LR3, double LFlatness) {
    recalcCoeff(LR0,LR1,LR2,LR3);
    Flatness=LFlatness;
  }
  public Bezier(MathVector LR0, MathVector LR1, MathVector LR2, MathVector LR3) {
    this(LR0,LR1,LR2,LR3,0.01);
  }
  public void recalcCoeff(MathVector LR0, MathVector LR1, MathVector LR2, MathVector LR3) {
    R0=new MathVector(LR0);
    R1=new MathVector(LR1);
    R2=new MathVector(LR2);
    R3=new MathVector(LR3);
    R=new MathVector[] {R0,R1,R2,R3};
    recalcCoeff();
  }
  public void recalcCoeff() {
    /*
    From the Postsript Red Book, p393
    x(t)=a_x*t^3+b_x*t^2+c_x*t+x_0
    y(t)=a_y*t^3+b_y*t^2+c_y*t+y_0

    t varies from 0.0 to 1.0
    
    x_1=x_0+c_x/3
    x_2=x_1+(c_x+b_x)/3
    x_3=x_0+c_x+b_x+a_x
    
    y_1=y_0+c_y/3
    y_2=y_1+(c_y+b_y)/3
    y_3=y_0+c_y+b_y+a_y
    
    where the control points are <x_n,y_n>,n=0..3
    
    To vector notation:
    
    r(t)=a*t^3+b*t^2+c*t+r_0

    r_1=r_0+c/3
    r_2=r_1+(c+b)/3
    r_3=r_0+c+b+a
    
    where the control points are r_n,n=0..3
    
    Renaming one variable:
    
    d=r_0
    r(t)=a*t^3+b*t^2+c*t+d

    r_1=d+c/3
    r_2=r_1+(c+b)/3
    r_3=d+c+b+a
    
    Solving for vector coefficients
    
    r_1-d=c/3
    c=(r_1-d)*3
    
    r_2-r_1=(c+b)/3
    (r_2-r_1)*3=c+b
    b=(r_2-r_1)*3-c
    
    r_3=d+c+b+a
    a=r_3-b-c-d
    
    */
    MathVector A,B,C,D;
    D=new MathVector(R0);                   
    C=MathVector.sub(R1,D).mul(3);
    B=MathVector.sub(MathVector.sub(R2,R1).mul(3),C);
    A=MathVector.sub(MathVector.sub(MathVector.sub(R3,B),C),D);
    P=new VectorPolynomial(new MathVector[] {A,B,C,D});
    PPrime=new VectorPolynomial(new MathVector[] {A.mul(3),B.mul(2),C});
  }
  public String toString() {
    return "R0: "+R0.toString()+"\n"+
           "R1: "+R1.toString()+"\n"+
           "R2: "+R2.toString()+"\n"+
           "R3: "+R3.toString()+"\n"+
           "P:  "+P.toString()+"\n"+
           "PP: "+PPrime.toString();
  }  
  public MathVector Eval(double x) {
    return P.Eval(x);
  }
  public MathVector EvalPrime(double x) {
    return PPrime.Eval(x);
  }
  public MathState EvalState(double x) {
    return new MathState(Eval(x),EvalPrime(x));
  }
  public ArrayList<MathVector> Plot(double T1, MathVector R1, double T2, MathVector R2) {
    double TM=(T1+T2)/2.0;
    ArrayList<MathVector> AL1,AL2;
    MathVector RM=Eval(TM);
    MathVector RA=MathVector.add(R1,R2).mul(0.5);
    MathVector Diff=MathVector.sub(RM,RA);
    if(Diff.length()>Flatness) {
      AL1=Plot(T1,R1,TM,RM);
      AL2=Plot(TM,RM,T2,R2);
      AL1.remove(AL1.size()-1);
      AL1.addAll(AL2);
    } else {
      AL1=new ArrayList<MathVector>();
      AL1.add(R1);
      AL1.add(R2);
    }
    return AL1;  
  }
  public ArrayList<MathVector> Plot(double T1, double T2) {
    MathVector R1, R2, RM;
    double TM=(T1+T2)/2;
    R1=Eval(T1);
    RM=Eval(TM);
    R2=Eval(T2);
    ArrayList<MathVector> AL1=Plot(T1,R1,TM,RM);
    ArrayList<MathVector> AL2=Plot(TM,RM,T2,R2);
    AL1.remove(AL1.size()-1);
    AL1.addAll(AL2);
    return AL1;
  }
  public MathVector[] Plot() {
    ArrayList<MathVector> AL=Plot(0,1);
    MathVector[] A=new MathVector[AL.size()];
    return AL.toArray(A);
  }
  public static void main(String[] args) {
    Bezier test=new Bezier(
      new MathVector(0,1,0).mul(10), 
      new MathVector(0.5,-1,0).mul(10), 
      new MathVector(1.5,2,0).mul(10), 
      new MathVector(2,0,0).mul(10)
    );
    System.out.println(test.toString());
    System.out.println(test.EvalState(0.5));
    MathVector[] p=test.Plot();
    for(int i=0;i<p.length;i++) {
      System.out.println(""+i+": "+p[i].toString());
    }
  }
}
