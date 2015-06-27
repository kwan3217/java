package org.kwansystems.space;

public class Hyperbola {
  public static double PolarConic(double a, double e, double theta) {
    return a*(1-e*e)/(1+e*Math.cos(theta));
  }
  public static void main(String[] args) {
    double a=-150;
    double e=5.0/3.0;
    double xc=0;
    double yc=0;
    
    double c=a*e;
    double b=Math.sqrt(c*c-a*a);
    double y0=b*c/a;
    double m=b/a;
    double x,y;
    
    System.out.println("C: "+c+"   B: "+b);
    x=xc+PolarConic(a,e,0)*Math.cos(0);
    y=yc+PolarConic(a,e,0)*Math.sin(0);
    System.out.println("M "+(xc+PolarConic(a,e,0))+","+(y));
    for(double theta=Math.toRadians(1);y<750;theta+=Math.toRadians(1)) {
      x=xc+PolarConic(a,e,theta)*Math.cos(theta);
      y=yc+PolarConic(a,e,theta)*Math.sin(theta);
      System.out.println("L "+x+","+y);
    }
    System.out.println("\n\n");
    System.out.println("M "+(-c)+",0 L "+((y-y0)/m)+","+y);

  }

}
