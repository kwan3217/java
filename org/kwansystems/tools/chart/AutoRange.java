package org.kwansystems.tools.chart;

import static java.lang.Math.*;

public class AutoRange {
  public static String DisplayEngUnit(double x, int precis) {
    String BigUnit=  " kMGTPEZY";
    String SmallUnit=" munpfazy";
    String Unit=BigUnit;
    int index=0;
    if(abs(x)>=1e-24) { 
      while(abs(x)>=1000 & index<8) {
        x/=1000;
        index++;
      }
      while(abs(x)<1 & index<8) {
        Unit=SmallUnit;
        x*=1000;
        index++;
      }
    }
    return String.format("%"+(5+precis)+"."+precis+"f%c",x,Unit.charAt(index));
  }

  public static String DisplayEngUnit(double x) {
    return DisplayEngUnit(x,3);
  }
  
  public static double[] calcAutoRange(double lo, double hi, int NumMajorTicks) {
    double diff;
    double diffScale, MajorTickSize;
    
    if(lo>hi) {
      double q=hi;
      hi=lo;
      lo=q;
    }
    
    diff=hi-lo;
    if(diff==0) diff=1.0;
    
    diffScale=pow(10,floor(log10(diff)));
    MajorTickSize=diffScale;
    
    double[] Div=new double[]{2.0,2.5,2.0};
    int DivIdx=0;
    while(NumMajorTicks*MajorTickSize>diff) {
      MajorTickSize/=Div[DivIdx];
      DivIdx++;
      if(DivIdx>=3) DivIdx=0;
    }
    
    hi=ceil(hi/MajorTickSize)*MajorTickSize;
    lo=floor(lo/MajorTickSize)*MajorTickSize;
    
    return new double[] {lo,hi,MajorTickSize};
  }

  public static void main(String[] args) {
    double[] TestRange=calcAutoRange(-1.23e-6,1.67046e-6,6);
    System.out.println(DisplayEngUnit(TestRange[0],2)+DisplayEngUnit(TestRange[1],2)+DisplayEngUnit(TestRange[2],2));
    System.out.println(DisplayEngUnit(1.0e-6));
    System.out.println(DisplayEngUnit(-123.456789e9,6));
  }
}

