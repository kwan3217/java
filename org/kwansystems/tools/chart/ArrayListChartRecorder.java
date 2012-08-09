package org.kwansystems.tools.chart;

import java.util.*;

public class ArrayListChartRecorder extends ChartRecorder {
  ArrayList<String> ColumnNames;
  ArrayList<String> Units;  //A map of unit names mapped by column name
  ArrayList<Double> TVar;
  ArrayList<Object[]> Data;
  ArrayList<Double> Max;
  ArrayList<Integer> MaxIdx;
  ArrayList<Double> Min;
  ArrayList<Integer> MinIdx;
  Object[] CurrentRow;
  double CurrentT;
  int ColStart;
  public ArrayListChartRecorder() {
    this(500,20);
  }
  /**
   * @param RowStart Starting number of rows to allocate in each column
   * @param LColStart Starting number of columns to allocate 
   */
  private void init(int RowStart, int LColStart) {
    ColStart=LColStart;
    ColumnNames=new ArrayList<String>(ColStart);
    Min=new ArrayList<Double>(ColStart);
    Max=new ArrayList<Double>(ColStart);
    MinIdx=new ArrayList<Integer>(ColStart);
    MaxIdx=new ArrayList<Integer>(ColStart);
    Units=new ArrayList<String>();
    Data=new ArrayList<Object[]>(RowStart);
    CurrentT=Double.NaN;
    TVar=new ArrayList<Double>(RowStart);
  }
  public ArrayListChartRecorder(String LTVarName, String LTVarUnits,int RowStart, int LColStart) {
    super(LTVarName,LTVarUnits);
    init(RowStart,ColStart);
  }
  public ArrayListChartRecorder(String LTVarName) {
    this(LTVarName,null);
  }
  public ArrayListChartRecorder(String LTVarName, String LTVarUnits) {
    this(LTVarName,LTVarUnits,500,20);
  }
  public ArrayListChartRecorder(int RowStart, int LColStart) {
    super();
    init(RowStart,ColStart);
  }
  public ArrayListChartRecorder(int RowStart, int LColStart, String LTVarName) {
    this(RowStart, LColStart);
    TVarName=LTVarName;
  }
  public void EndOfData() {
    TVar.add(CurrentT);
    Data.add(CurrentRow);
  }
  public int getColumnIdx(String Column) {
    return ColumnNames.indexOf(Column);
  }    
  public String getUnits(String Column) {
    return Units.get(getColumnIdx(Column));
  }    
  public void Record(double T,String Column,String LUnits, Object Value) {
    if(Double.isNaN(CurrentT)) {
      CurrentRow=new Object[0];
      CurrentT=T;
    } else if(T!=CurrentT) {
      //Put the old row into data, reset the row
      TVar.add(CurrentT);
      Data.add(CurrentRow);
      CurrentRow=new Object[ColumnNames.size()];
      CurrentT=T;
    }
    int idx=getColumnIdx(Column);
    if (idx<0) {
      //Make a new column
      ColumnNames.add(Column);
      Units.add(LUnits);
      Min.add(Double.POSITIVE_INFINITY);
      Max.add(Double.NEGATIVE_INFINITY);
      MinIdx.add(TVar.size());
      MaxIdx.add(TVar.size());
      idx=ColumnNames.size()-1;
      Object[] NewRow=new Object[ColumnNames.size()];
      System.arraycopy(CurrentRow,0, NewRow, 0, CurrentRow.length);
      CurrentRow=NewRow;
    }
    CurrentRow[idx]=Value;
    if(Value.getClass().getName().equals("java.lang.Double")) {
      Double D1=(Double)Value;
      if(!D1.isNaN()) {
        if(Max.get(idx)<D1) {
          Max.set(idx,D1);
          MaxIdx.set(idx, TVar.size());
        }
        if(Min.get(idx)>D1) {
          Min.set(idx,D1);
          MinIdx.set(idx, TVar.size());
        }
      }
    }
  }
  public int getTIdx(double T) {
    int RangeTimeIdx=Collections.binarySearch(TVar,T);
    if(RangeTimeIdx<0) return -RangeTimeIdx-2;
    return RangeTimeIdx;
  }
  public double[] getT() {
    double[] result=new double[TVar.size()];
    for(int i=0;i<TVar.size();i++) {
      result[i]=TVar.get(i).doubleValue();
    }
    return result;
  }
  public Object Playback(int TimeIdx,int ColumnIdx) {
    if(TimeIdx<0) throw new IllegalArgumentException("Time index "+TimeIdx+" does not exist");
    Object[] Row=Data.get(TimeIdx);
    if(ColumnIdx>=Row.length)return null;
    return Row[ColumnIdx];
  }  
  public int NumRows() {
    return TVar.size();
  }
  public double columnMin(String Column) {
    int ColumnIdx=ColumnNames.indexOf(Column);
    if (ColumnIdx<0) throw new IllegalArgumentException("Table column "+Column+" does not exist");
    return Min.get(ColumnIdx);
  }
  public double columnMax(String Column) {
    int ColumnIdx=ColumnNames.indexOf(Column);
    if (ColumnIdx<0) throw new IllegalArgumentException("Table column "+Column+" does not exist");
    return Max.get(ColumnIdx);
  }
  public double getTMax() {
    return TVar.get(TVar.size()-1);
  }
  public void PrintAllRows(ChartPrinter CP) {
    int NTimes=TVar.size();
    PrintSomeRows(0,NTimes, CP);
  }    
  public void PrintSomeRows(int TFirstIdx, int NTimes, ChartPrinter CP) {
    if(NTimes+TFirstIdx>=TVar.size()) NTimes=TVar.size()-TFirstIdx;
    for(int TimeIdx=0;TimeIdx<NTimes;TimeIdx++) {
      PrintRow(TFirstIdx+TimeIdx,CP);
    }
  }    
  public double getTFromIdx(int TimeIdx) {
    return TVar.get(TimeIdx);
  }
  public void PrintTable(ChartPrinter CP) {
    String[] Names=new String[0];
    Names=ColumnNames.toArray(Names);
    PrintSubTable(Names,CP);
  }
}
