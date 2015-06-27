package org.kwansystems.tools.vector;

import java.io.*;

/**
 * An object representing a position and velocity
 */
public class MathState extends MathVector implements Serializable {
  private static final long serialVersionUID=8724971907635431977L;
  public double VX() {
    return get(3);
  }
  public double VY() {
    return get(4);
  }
  public double VZ() {
    return get(5);
  }
  public void setVX(double newValue) {
    set(3,newValue);
  }
  public void setVY(double newValue) {
    set(4,newValue);
  }
  public void setVZ(double newValue) {
    set(5,newValue);
  }
  public Object clone() {
    return new MathState(this);
  }
  public MathVector R() {
    MathVector result=new MathVector(X(), Y(), Z());
    return result;
  }
  public MathVector V() {
    MathVector result=new MathVector(VX(),VY(),VZ());
    return result;
  }
  public MathVector setR() {
    MathVector result=new MathVector(X(), Y(), Z());
    return result;
  }
  public MathVector setV() {
    MathVector result=new MathVector(VX(),VY(),VZ());
    return result;
  }
  public double FlightPathAngle() {
    return vangle(R(),V());
  }
  public MathState(MathVector LR, MathVector LV) {
    super(new MathVector[]{LR,LV});
  }
  public MathState(double LX, double LY, double LZ,double LVX, double LVY, double LVZ) {
    super(new double[] {LX,LY,LZ,LVX,LVY,LVZ});
  }
  public MathState(double[] LA) {
    super(LA);
  }
  public MathState(MathVector LS) {
    super(LS.elements.clone());
  }
  public MathState() {
    super(6);
  }
  public MathState(MathVector[] A) {
    super(A);
  }
  public MathState replaceR(MathVector LR) {
    return new MathState(LR,V());
  }
  public MathState replaceV(MathVector LV) {
    return new MathState(R(),LV);
  }
  public void replaceREq(MathVector LR) {
    setSubVector(0,LR);
  }
  public void rAddEq(MathVector LR) {
    for(int i=0;i<3;i++) elements[i]+=LR.elements[i];
  }
  public void replaceVEq(MathVector LV) {
    setSubVector(3,LV);
  }
  public void vAddEq(MathVector LV) {
    for(int i=3;i<6;i++) elements[i]+=LV.elements[i];
  }
  public String toString() {
    return "/*Position: (Length "+R().length()+") */ "+R().toString()+"\n/*Velocity: (Length "+V().length()+") */"+V().toString();
  }
  public MathVector POC(MathVector Vel) {
    MathVector RefPos=R();
    MathVector RefVel=V();
    MathVector N=MathVector.cross(RefVel,RefPos);
    MathVector DV=MathVector.sub(Vel,RefVel);
    double Prograde=DV.Comp(RefVel);
    MathVector NotPrograde=DV.ProjPerp(RefVel);
    double Out=NotPrograde.CompPerp(N);
    double ChPl=NotPrograde.Comp(N);
    MathVector VV=new MathVector(Prograde,Out,ChPl);
    return VV;
  }
}
