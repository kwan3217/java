package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.zoetrope.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;
import static org.kwansystems.tools.time.Time.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;


public class EarthSatSGP4Shuttle extends Ephemeris {
  private List<EarthSatVSGP4> S;
  private List<Time> epochs;
  public EarthSatSGP4Shuttle(String infn) throws IOException {
    this(new File(infn));
  }
  public EarthSatSGP4Shuttle(File infn) throws IOException {
    this(new FileReader(infn));
  }
  public EarthSatSGP4Shuttle(Reader inf) throws IOException {
    this(new LineNumberReader(inf));
  }
  public EarthSatSGP4Shuttle(LineNumberReader inf) throws IOException {
    S=new ArrayList<EarthSatVSGP4>();
    epochs=new ArrayList<Time>();
    String line1,line2,title;
    title = inf.readLine();
    line1=inf.readLine();
    while(line1!=null) {
      while(line1!=null && ((line1.length()==0) || (line1.charAt(0)=='1'))) {
        title=line1;
        line1=inf.readLine();
      }
      if(line1!=null) {
        line2=inf.readLine();
      }

      title=inf.readLine();
      if(title!=null) {
        line1=inf.readLine();
      } else {
        line1=null;
      }
    }

    line2=inf.readLine();
    Collections.sort(S);
    epochs=new ArrayList<Time>();
    for(EarthSatVSGP4 E:S) {
      epochs.add(E.epoch);
    }
  }
  public MathVector CalcPos(Time TT) {
    return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
    return defaultCalcVel(TT);
  }
  public MathState CalcState(Time TT) {
    return S.get(findCorrect(TT)).CalcState(TT);
  }
  private int findCorrect(Time TT) {
    if (epochs.size()<=1) return 0;
    int index=Collections.binarySearch(epochs,TT);
    boolean found=(index>=0);
    //If one is an exact match, return it.
    if(found) return index;
    //Otherwise, transform the index. Index will be the index of the
    //first element larger than the search, or equal to the length of the
    //list if the search is larger than all the values.
    if(!found) index=-index-1;
    //If it's 0, the 0th element is it.
    if(index==0) return index;
    //If it's off the end of the list, the last element is it.
    if(index==epochs.size()) return epochs.size()-1;
    //Otherwise, look at the distance to each and choose the smaller one
    return index-1;
  }
  public Time startTime() {
    return new Time(S.get(0).epoch);
  }
  public Time stopTime() {
    return new Time(S.get(S.size()-1).epoch);
  }
  public int size() {
    return S.size();
  }
  public static void main(String[] args) throws IOException {
    EarthSatSGP4Shuttle E=new EarthSatSGP4Shuttle("Data/EarthSatSGP4Vallado/ISS.tle");
    String[] ENames=new String[E.size()];
    for(int i=0;i<E.size();i++) {
      ENames[i]=String.format("State%03d", i);
    }
    ChartRecorder C=new ArrayListChartRecorder("Time since first epoch","days");
    int i=0;
    for(EarthSatVSGP4 EE:E.S) {
      System.out.println(i);
      System.out.println(EE.TLE.toString());
      i++;
    }
    Time Tlast=E.stopTime();
    Time Tfirst=E.startTime();
    C.PrintTable(new HTMLPrinter("EarthSatSGP4File.html"));
    MathState RV=(MathState)(C.Playback(0,"Official State"));
    MathMatrix RTN=Ephemeris.RTN(RV);
    System.out.println(RV);
    System.out.println(RTN);
    //C.PrintSubTable(new String[] {"Uncertainty magnitude","Lower index","Upper index"},new DisplayPrinter());
    SGPFZoetrope Z=new SGPFZoetrope("",100,C,E); 
    Z.setVisible(true);
    Z.start();
  }
}
class SGPFZoetrope extends Zoetrope {
  ChartRecorder C;
  String[] ENames;
  public SGPFZoetrope(String LWindowTitle, int LFramePeriodMs, ChartRecorder LC, EarthSatSGP4Shuttle LE) {
    super(LWindowTitle,LFramePeriodMs);
    setElements(LC);
    ENames=new String[LE.size()];
    for(int i=0;i<LE.size();i++) {
      ENames[i]=String.format("State%03d", i);
    }
  }
  protected void FigureOutStuff() {
    setRmax(11);//C.columnMax("Uncertainty magnitude")*1.1;
  }
  public void setElements(ChartRecorder LC) {
    C=LC;
    setNumFrames(C.NumRows());
    FigureOutStuff();
  }
  protected int X(MathStateTime thisST) {
    return X(thisST.S.R().X());
  }
  protected int Y(MathStateTime thisST) {
    return Y(thisST.S.R().Z());
  }
  protected void paintFrame(Graphics G) {
    //Draw all previous actual locations
    Graphics2D G2=(Graphics2D)G;
    G.setColor(colors[0]);
    MathState OfficialState=(MathState)(C.Playback(CurrentFrame,"Official State"));
    MathMatrix RTN=Ephemeris.RTN(OfficialState);
    OfficialState=RTN.transform(OfficialState);
    nViewportX=2;
//    System.out.println(OfficialState); //Should be <R,0,0>
    int lower=(Integer)(C.Playback(CurrentFrame, "Lower index"));
    int upper=(Integer)(C.Playback(CurrentFrame, "Upper index"));
    MathState Unc=(MathState)(C.Playback(CurrentFrame, "UncertaintyRTN"));
    setColor(G,0);
    G2.setStroke(new BasicStroke(1));
    setViewport(G,0,0);
    drawX(G,0,0,10);
    drawCircle(G,0,0,1);
    drawCircle(G,0,0,5);
    drawCircle(G,0,0,10);
    setColor(G,2);
    drawEllipse(G,-Unc.Y(),-Unc.X(),Unc.Y(),Unc.X());
    setViewport(G,1,0);
    drawX(G,0,0,10);
    drawCircle(G,0,0,1);
    drawCircle(G,0,0,5);
    drawCircle(G,0,0,10);
    setColor(G,2);
    drawEllipse(G,-Unc.Z(),-Unc.X(),Unc.Z(),Unc.X());
    for(int i=0;i<ENames.length;i++) {
      MathState StateI=RTN.transform((MathState)(C.Playback(CurrentFrame,ENames[i])));
      MathState StateIdiff=new MathState(MathVector.sub(StateI, OfficialState));
      boolean isBig=(i==lower)||(i==upper);
      G2.setStroke(new BasicStroke(isBig?5:1));
      setColor(G,i);
      setViewport(G,0,0);
      drawX(G,StateIdiff.Y(),StateIdiff.X(),isBig?10:5);
      setViewport(G,1,0);
      drawX(G,StateIdiff.Z(),StateIdiff.X(),isBig?10:5);
    }
  }
}
