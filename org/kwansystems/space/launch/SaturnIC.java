package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;

public class SaturnIC extends Rocket {
  public SaturnIC(Guidance LG,Planet LP,ArrayListChartRecorder C) {
    super("S-IC",LG,LP,C);
    StartTime=0;
    StopTime=154;            //Burn to depletion, will occur before 160s
    FuelMass=1957147;        //Propellant burned, p21-11 (490)
    DryMass=179028;          //non-propellant mass of S-1C, including lower portion of lower interstage
    Flowrate=new QuadraticTable(
      new double[] {  0,      //0
                     10,      //1
                     20,      //2
                     30,      //3
                     40,      //4
                     50,      //5
                     60,      //6
                     70,      //7
                     80,      //8
                     90,      //9
                    100,      //0
                    110,      //1
                    120,      //2
                    125.7,    //3
                    125.71,   //3
                    125.93,   //4
                    129.9,    //5
                    140,      //6
                    150,      //7
                    153.81,   //8
                    153.82,   //9
                    153.83,   //0
                    153.84,   //1
                    153.85},  //2
      new double[][] {{13160,    //0
                    13211,    //1
                    13211,    //2
                    13211,    //3
                    13211,    //4
                    13218,    //5
                    13225,    //6
                    13247,    //7
                    13268,    //8
                    13312,    //9
                    13384,    //0
                    13348,    //1
                    13413,    //2
                    13434,    //3
                    13434,    //3
                    10786,    //4
                    10628,    //5
                    10657,    //6
                    10693,    //7
                    10700,    //8
                    10700,    //9
                        0,    //0
                        0,    //1
                        0}});  //2
  }
  //Isp has been shown to be almost independent of flow rate for a S-IC
  public double IspVac(double T) {
    return 2990;
  }
  public double IspSL(double T) {
    return 2583.587;
  }
  public double DragArea(double T) {
    return Math.PI*Math.pow(396/2*0.03937,2);
  }
}
