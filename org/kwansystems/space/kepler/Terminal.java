package org.kwansystems.space.kepler;

import org.kwansystems.tools.vector.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import java.io.*;

public class Terminal {
  private MathStateTime st;
  private MathState base;
  private MathVector DV;
  private Planet P;
  private double Hp;
  private double Vinf,C3,Pro,Out,ChPl;
  private double Vcirc,Vesc,Vp,DVesc,DVcirc;
  @Override
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    ouf.printf("Pro:  %sm/s\n",AutoRange.DisplayEngUnit(Pro));
    ouf.printf("Out:  %sm/s\n",AutoRange.DisplayEngUnit(Out));
    ouf.printf("ChPl: %sm/s\n",AutoRange.DisplayEngUnit(ChPl));
    return result.toString();
  }
  public double[] CalcHyper(double Hp, double Vinf) {
    return new double[] {DVesc,DVcirc,Vcirc,Vesc,Vp};
  }    
  public Terminal(MathStateTime Lst, Planet LP, double LHp) {
    P=LP;
    st=Lst;
    base=LP.Orbit.getState(Lst.T);
    MathVector N=MathVector.cross(base.V(),base.R());
    DV=MathVector.sub(st.S.V(),base.V());
    Pro=DV.Comp(base.V());
    MathVector NotPrograde=DV.ProjPerp(base.V());
    Out=NotPrograde.CompPerp(N);
    ChPl=NotPrograde.Comp(N);
    Vinf=DV.length();
    C3=Vinf*Vinf;
    double Rad=P.S.Re+Hp;
    double Mu=P.S.GM;
    Vcirc=Math.sqrt(Mu/Rad);
    Vesc =Vcirc*Math.sqrt(2.0);
    Vp=Math.hypot(Vinf,Vesc);
    DVesc=Vp-Vesc;
    DVcirc=Vp-Vcirc;
  }
  public double getC3() {
    return C3;
  }
  public double getVinf() {
    return Vinf;
  }
  public double getDVesc() {
    return DVesc;
  }
  public double getDVcirc() {
    return DVcirc;
  }
  public double getPro() {
    return Pro;
  }
  public double getChPl() {
    return ChPl;
  }
  public double getOut() {
    return Out;
  }
  public MathVector getVinfVec() {
    return new MathVector(DV);
  }
}
