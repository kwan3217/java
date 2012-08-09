package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.vector.*;

public class MultiStage extends Rocket {
  Rocket[] Stages;
  double[] DropTimes;
  int[] Columns; //The drag factor of the rocket is the sum of the largest drag factors in each column
  public MultiStage(String LName,Rocket[] LStages,double[] LDropTimes,int[]LColumns,Guidance LG,Planet LP, ArrayListChartRecorder LC) {
    super(LName,LG,LP,LC);
    Stages=LStages;
    DropTimes=LDropTimes;
    Columns=LColumns;
    StartTime=9e99;
    StopTime=-1;
    for(int i=0;i<Stages.length;i++) {
      if(StartTime>Stages[i].StartTime) StartTime=Stages[i].StartTime;
      if(StopTime<Stages[i].StopTime) StopTime=Stages[i].StopTime;
    }
  }
  public double Thrust(double T, MathState X, boolean IsMajor) {
    double ThrustTotal=0;
    for(int i=0;i<Stages.length;i++) {
      if(Stages[i].StageActive(T,IsMajor) & T<DropTimes[i]) ThrustTotal+=Stages[i].Thrust(T,X,IsMajor);
    }
    return ThrustTotal;
  }
  public boolean StageActive(double T, boolean IsMajor) {
    if(T<StartTime) return false;
    if(T>StopTime) return false;
    return true;
  }
  public double FuelMassLeft(double T,boolean IsMajor) {
    double FuelTotal=0;
    for(int i=0;i<Stages.length;i++) {
      if(T<DropTimes[i]) FuelTotal+=Stages[i].FuelMassLeft(T,IsMajor);
    }
    return FuelTotal;
  }
  public double TotalMass(double T, boolean IsMajor) {
    double MassTotal=0;
    for(int i=0;i<Stages.length;i++) {
      if(T<DropTimes[i]) {
        double M=0;
        M=Stages[i].TotalMass(T,IsMajor);
        if(IsMajor)C.Record(T,Stages[i].Name+" (Stage " + i + ") mass","kg",M);
        MassTotal+=M;
      } else {
        if(IsMajor)C.Record(T,Stages[i].Name+" (Stage " + i + ") mass","kg","dropped");
      }
    }
    if(IsMajor)C.Record(T,Name+" mass total","kg",MassTotal);
    return MassTotal;
  }
  public double IspVac(double T) {
    for(int i=0;i<Stages.length;i++) if(Stages[i].StageActive(T,false)) return Stages[i].IspVac(T);
    return 0;
  }
  public double IspSL(double T) {
    for(int i=0;i<Stages.length;i++) if(Stages[i].StageActive(T,false)) return Stages[i].IspSL(T);
    return 0;
  }
  public double DragArea(double T) {
    double[] Drags=new double[Columns.length];
    for(int i=0;i<Stages.length;i++) {
      if(DropTimes[i]>T) {
        double DF=Stages[i].DragArea(T);
        int C=Columns[i];
        if(Drags[C]<DF) Drags[C]=DF;
      }
    }
    double DragSum=0;
    for(int i=0;i<Drags.length;i++) {
      DragSum+=Drags[i];
    }
    return DragSum;
  }
}
