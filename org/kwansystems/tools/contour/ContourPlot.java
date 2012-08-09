package org.kwansystems.tools.contour;

import java.util.*;
import org.kwansystems.tools.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.swing.*;

/** Contour plot of a regular 2D data grid. To use, first you supply the data and have 
 * this class ponder it deeply and generate the contour lines. Then you give it a Graphics
 * object to draw on. As long as the underlying data doesn't change, you can do the second
 * thing many times while only doing the first thing once.
 */
public class ContourPlot {
  class ContourPoint implements Comparable<ContourPoint> {
    double x,y;
    int x1,y1,x2,y2;
    long square1,square2;
    boolean isHorizontal;
    boolean onEdge;
    boolean isStart;
    boolean isUsed;
    public int compareTo(ContourPoint arg0) {
      if(x<arg0.x) return -1;
      if(x>arg0.x) return 1;
      if(y<arg0.y) return -1;
      if(y>arg0.y) return 1;
      return 0;
    }
    public boolean equals(Object arg0) {
      return compareTo((ContourPoint)arg0)==0;      
    }
    public int hashCode() {
      return new Double(x).hashCode()+new Double(y).hashCode();
    }
    public ContourPoint(double Lx,double Ly,boolean LisHorizontal) {
      x=Lx;
      y=Ly;
      isHorizontal=LisHorizontal;
    }
  }
  //A contour sector is composed of many segments. A complete contour is composed of many sectors 
  class ContourSegment {
    double x0,y0,x1,y1;
    //Maybe add stuff here later for connectedness, smoothing, etc
  }
  private double[][] data;
  private int rows,cols;
  private ContourSegment[][][] curves;
  private ContourPoint[][] points;
  private double[] levels;
  public ContourPlot(double[][] Ldata, double[] Llevels) {
    this(Llevels);
    setData(Ldata);
  }
  public ContourPlot(double[] Llevels) {
    levels=Llevels;
  }
  public double[][] getData() {
    return data;
  }
  public void setData(double[][] Ldata) {
    data=Ldata;
    rows=data.length;
    cols=data[0].length;
    constructCurves();
  }
  private void constructCurves() {
    curves=new ContourSegment[levels.length][][];
    points=new ContourPoint[levels.length][];
    for(int i=0;i<curves.length;i++) {
      curves[i]=constructCurve(i);
    }
  }
  private ContourSegment[][] constructCurve(int level) {
    points[level]=collectPoints(levels[level]);
    
    return connectPoints(points[level]);
  }
  private ContourPoint[] collectPoints(double level) {
    SortedSet<ContourPoint> result=new TreeSet<ContourPoint>();
    //Check the horizontal segments
    for(int row=0;row<rows;row++) {
      for(int col=0;col<cols-1;col++) {
        //If level is between the two endpoints of this segment
        if((level==data[row][col]) & (level==data[row][col+1])) {
          result.add(new ContourPoint(0.5+col,row,true));
        } else if(((level<data[row][col]) & (level>data[row][col+1])) | ((level>data[row][col]) & (level<data[row][col+1]))) {
          //There is a contour point here. Construct and add it.
          result.add(new ContourPoint(Scalar.linterp(data[row][col],col,data[row][col+1],col+1,level),row,true));
        }
      }
    }
    //Check the vertical segments
    for(int col=0;col<cols;col++) {
        for(int row=0;row<rows-1;row++) {
        //If level is between the two endpoints of this segment
        if((level==data[row][col]) & (level==data[row+1][col])) {
          result.add(new ContourPoint(col,0.5+row,false));
        } else if(((level<data[row][col]) & (level>data[row+1][col])) | ((level>data[row][col]) & (level<data[row+1][col]))) {
          //There is a contour point here. Construct and add it.
          result.add(new ContourPoint(col,Scalar.linterp(data[row][col],col,data[row+1][col],row+1,level),false));
        }
      }
    }
    return result.toArray(new ContourPoint[]{});
  }
  private ContourSegment[][] connectPoints(ContourPoint[] points) {
    List<ContourSegment[]> result=new LinkedList<ContourSegment[]>();
    
    return result.toArray(new ContourSegment[][]{});
  }
  public void paint(Graphics G_, int width,int height) {
    Graphics2D G=(Graphics2D)G_;

    G.setColor(new Color(128,128,128));
    for(int i=0;i<=cols;i++) {
      int x=(int)Scalar.linterp(0, 0, cols-1, width, i);        
      G.drawLine(x,0,x,height);
    }
    G.setColor(new Color(255,128,0));
    for(ContourPoint[] i:points) {
      for(ContourPoint j:i) {
        int x=(int)Scalar.linterp(0, 0, cols-1, width, j.x);        
        int y=(int)Scalar.linterp(0, 0, rows-1, height, j.y);
        G.drawLine(x-3, y, x+3, y);
        G.drawLine(x, y-3, x, y+3);
      }
    }

  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    final double[][] data=new double[][] {
      {0.5, 1.1, 1.5, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 0.1},
      {1.0, 1.5, 3.0, 5.0, 6.0, 2.0, 1.0, 1.2, 1.0, 4.0},
      {0.9, 2.0, 2.1, 3.0, 6.0, 7.0, 3.0, 2.0, 1.0, 1.4},
      {1.0, 1.5, 3.0, 4.0, 6.0, 5.0, 2.0, 1.5, 1.0, 2.0},
      {0.8, 2.0, 3.0, 3.0, 4.0, 4.0, 3.0, 2.4, 2.0, 3.0},
      {0.6, 1.1, 1.5, 1.0, 4.0, 3.5, 3.0, 2.0, 3.0, 4.0},
      {1.0, 1.5, 3.0, 5.0, 6.0, 2.0, 1.0, 1.2, 2.7, 4.0},
      {0.8, 2.0, 3.0, 3.0, 5.5, 6.0, 3.0, 2.0, 1.0, 1.4},
      {1.0, 1.5, 3.0, 4.0, 6.0, 5.0, 2.0, 1.0, 0.5, 0.2}
    };
    //Create and set up the window.
    JFrame frame = new JFrame("GridBagLayoutDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final ContourPlot CP=new ContourPlot(data, new double[] {0,1,2,3,4,5,6});
  
    //Set up the content pane.
    frame.getContentPane().add(new JPanel() {
      public void paint(Graphics G) {
        super.paint(G);
        Dimension D=getSize();
        CP.paint(G,D.width,D.height);
      }
    });
  
    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }
  
  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });


  }

}
