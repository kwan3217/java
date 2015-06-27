package org.kwansystems.pov.mesh;

public class Bounds {
  float minx,miny,minz;
  float maxx,maxy,maxz;
  public String Name;
  public String toString() {
    return "  #declare "+Name+"_Min=<"+minx+","+miny+","+minz+">;\n"+
    "  #declare "+Name+"_Max=<"+maxx+","+maxy+","+maxz+">;";
  }
  public Bounds() {
    minx=Float.POSITIVE_INFINITY;
    miny=Float.POSITIVE_INFINITY;
    minz=Float.POSITIVE_INFINITY;
    maxx=Float.NEGATIVE_INFINITY;
    maxy=Float.NEGATIVE_INFINITY;
    maxz=Float.NEGATIVE_INFINITY;
  }
  void SETBOUNDS(float x,float y,float z) {
    if(x<minx) minx=x;
    if(x>maxx) maxx=x;
    if(y<miny) miny=y;
    if(y>maxy) maxy=y;
    if(z<minz) minz=z;
    if(z>maxz) maxz=z;
  }
}
