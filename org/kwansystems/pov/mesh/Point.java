package org.kwansystems.pov.mesh;

import org.kwansystems.tools.vector.*;

public class Point {
  public float x,y,z;
  public Point(String stuff, Bounds b) {
    String[] result = stuff.split("\\s+");
    x=Float.parseFloat(result[2]);
    y=Float.parseFloat(result[3]);
    z=Float.parseFloat(result[4]);
    if(false) {
      float RoundOff=100000;
      int xi=(int)(x*RoundOff);
      int yi=(int)(y*RoundOff);
      int zi=(int)(z*RoundOff);
      x=((float)xi)/RoundOff;
      y=((float)yi)/RoundOff;
      z=((float)zi)/RoundOff;
    }
    b.SETBOUNDS(x,y,z);
  }
  public Point(MathVector V) {
    x=(float)V.X();
    y=(float)V.Y();
    z=(float)V.Z();
  }
  public Point(float Lx, float Ly, float Lz) {
    x=Lx;
    y=Ly;
    z=Lz;
  }
  public void add(Point P) {
    x=x+P.x;
    y=y+P.y;
    z=z+P.z;
  }
  public void neg() {
    x=-x;
    y=-y;
    z=-z;
  }
  public float Length() {
    return (float)Math.sqrt(dot(this));
  }
  public float dot(Point P) {
    return x*P.x+y*P.y+z*P.z;
  }
  public void normalize() {
    float l=Length();
    x=x/l;
    y=y/l;
    z=z/l;
  }
  public boolean equals(Object o) {
    try {
      Point p=(Point)o;
      return(p.x==x & p.y==y & p.z==z);
    } catch (ClassCastException E) {return false;}
  }
  public int hashCode() {
    return Float.floatToRawIntBits(x)^Float.floatToRawIntBits(y)^Float.floatToRawIntBits(z);
  }
  public String toString() {
    return "<"+x+","+y+","+z+">";
  }
  public MathVector toMathVector() {
    return new MathVector(x,y,z);
  }
}
