package org.kwansystems.tools.chart;

public interface ChartPrinter {
  //First event called. Called just once.
  //CR - reference to the chart recorder to get a preview of the data (say to scale a stripchart plotter)
  //ColumnNames - array of column names, besides Range Time which is always the first column
  public void StartOfChart(int TFirstIdx, int NRows, ChartRecorder CR,String[] ColumnNames); 
  //Called between each call to Head
  public void BetweenHead(); 
  //Called after last header cell
  public void EndOfHead();
  //Called for each column head, including Range Time
  //S - Name of column
  public void Head(String S);
  //Called at the beginning of each row of data
  public void StartOfRow();
  //Called between each data item
  public void BetweenData();
  //Called at the end of each row of data
  public void EndOfRow();
  //Called when this column has data in this row
  //S - Data that belongs in this cell
  public void Data(Object S);
  //Called when this column has no data in this row
  public void NoData();
  //Called after the last row is passed
  public void EndOfChart();
}


