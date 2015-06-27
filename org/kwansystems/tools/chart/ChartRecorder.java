package org.kwansystems.tools.chart;

public abstract class ChartRecorder {
  String[] PrintColumns;
  String TVarName;
  String TVarUnits;
  int[] PrintColumnsIdx;
  public ChartRecorder() {
    this("Range Time","s");
  }
  public ChartRecorder(String LTVarName) {
    TVarName=LTVarName;
    TVarUnits=null;
  }
  public ChartRecorder(String LTVarName,String LTVarUnits) {
    TVarName=LTVarName;
    TVarUnits=LTVarUnits;
  }
  public abstract void EndOfData();
  public abstract void Record(double T,String Column,String LUnits, Object Value);
  public void Record(double T, String Column, Object Value) {Record(T,Column,null,Value);}
  public Object Playback(double T,String Column) {
    return Playback(T,getColumnIdx(Column));
  }
  public Object Playback(int TIdx,String Column) {
    return Playback(TIdx,getColumnIdx(Column));
  }
  public abstract String getUnits(String Column);
  public Object Playback(double T, int ColumnIdx) {
    return Playback(getTIdx(T),ColumnIdx);
  }
  public abstract Object Playback(int TIdx, int ColumnIdx);
  public abstract int getColumnIdx(String Column);
  public abstract int getTIdx(double T);
  public abstract int NumRows();
  public abstract double columnMin(String Column);
  public abstract double columnMax(String Column);
  public abstract double getTMax();
  public abstract double[] getT();
  public Object[] getColumn(String Column) {
    Object[] result=new Object[NumRows()];
    int ColIdx=getColumnIdx(Column);
    for(int i=0;i<NumRows();i++) {
      result[i]=Playback(i,ColIdx);
    }
    return result;
  }
  public double[] getDoubleColumn(String Column) {
    double[] result=new double[NumRows()];
    int ColIdx=getColumnIdx(Column);
    for(int i=0;i<NumRows();i++) {
      Object Q=Playback(i,ColIdx);
      if(Q==null) {
        result[i]=0;
      } else {
        result[i]=((Double)Q).doubleValue();
      }
    }
    return result;
  }
  public void PrintHeader(int TFirstIdx, int NRows, String[] LColumnNames,ChartPrinter CP) {
    PrintColumns=LColumnNames;
    PrintColumnsIdx=new int[LColumnNames.length];
    CP.StartOfChart(TFirstIdx,NRows,this,LColumnNames);
    CP.Head(TVarName+(TVarUnits==null?"":(" ("+TVarUnits+")")));
    for(int i=0;i<LColumnNames.length;i++) {
      int ColumnIdx=getColumnIdx(LColumnNames[i]);
      if(ColumnIdx<0) throw new IllegalArgumentException("Table column "+LColumnNames[i]+" does not exist");
      PrintColumnsIdx[i]=ColumnIdx;
      CP.BetweenHead();
      String Title=LColumnNames[i];
      String Unit=getUnits(LColumnNames[i]);
      if(Unit!=null) Title=Title+" ("+Unit+")";
      CP.Head(Title);
    }
    CP.EndOfHead();
  }
  //You *ALWAYS* get the independent variable. You don't need to specify it.
  public void PrintSubTable(String[] LColumnNames,ChartPrinter CP) {
    PrintHeader(0, NumRows(), LColumnNames,CP);
    PrintAllRows(CP);
    CP.EndOfChart();
  }
  public abstract void PrintAllRows(ChartPrinter CP);
  public abstract void PrintSomeRows(int TIdxFirst, int nRows, ChartPrinter CP);
  public abstract void PrintTable(ChartPrinter CP);
  public void PrintRow(Double T,ChartPrinter CP) {
    CP.StartOfRow();
    CP.Data(T);
    
    for(int i=0;i<PrintColumns.length;i++) {
      CP.BetweenData();
      Object D=Playback(T,PrintColumnsIdx[i]);
      if(D!=null) {
        CP.Data(D);
      } else {
        CP.NoData();
      }
    }
    CP.EndOfRow();
  }
  public abstract double getTFromIdx(int TIdx);
  public void PrintRow(int TIdx,ChartPrinter CP) {
    CP.StartOfRow();
    CP.Data(getTFromIdx(TIdx));
    
    for(int i=0;i<PrintColumns.length;i++) {
      CP.BetweenData();
      Object D=Playback(TIdx,PrintColumnsIdx[i]);
      if(D!=null) {
        CP.Data(D);
      } else {
        CP.NoData();
      }
    }
    CP.EndOfRow();
  }
  public void PrintSubTable(int TIdxFirst, int nRows, String[] LColumnNames,ChartPrinter CP) {
    PrintHeader(TIdxFirst, nRows, LColumnNames,CP);
    PrintSomeRows(TIdxFirst,nRows,CP);
    CP.EndOfChart();
  }
}
