package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.Polynomial;
import org.kwansystems.tools.Scalar;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.zoetrope.Zoetrope;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;
import static org.kwansystems.tools.time.Time.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;

public class EarthSatSGP4File extends Ephemeris {
  private List<EarthSatVSGP4> S;
  private List<Long> epochs;
  public EarthSatSGP4File(String infn) throws IOException {
    this(new File(infn));
  }
  public EarthSatSGP4File(File infn) throws IOException {
    this(new FileReader(infn));
  }
  public EarthSatSGP4File(Reader inf) throws IOException {
    this(new LineNumberReader(inf));
  }
  public EarthSatSGP4File(LineNumberReader inf) throws IOException {
    S=new ArrayList<EarthSatVSGP4>();
    String line1,line2;
    String line = inf.readLine();
    int satnum=-1;
    while (line != null) {
      do {
        line1 = line;
        line = inf.readLine();
      } while ((line1.length()>0) && (line1.charAt(0) != '1') && (S != null));

      if (line != null) {
        line2 = line;
        line = inf.readLine();
        // convert the char string to sgp4 elements
        // includes initialization of sgp4
        EarthSatVSGP4 E=new EarthSatVSGP4(line1, line2);
//        if ((satnum>=0) && (satnum!=E.TLE.satnum)) throw new IllegalArgumentException("Not all the same satellite numbers in the file");
        satnum=E.TLE.satnum;
        S.add(new EarthSatVSGP4(line1, line2)); 
      }
    }
    Collections.sort(S);
    epochs=new ArrayList<Long>();
    for(EarthSatVSGP4 E:S) {
      epochs.add(E.epoch.getTime());
    }
  }
  public MathVector CalcPos(Time TT) {
    return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
    return defaultCalcVel(TT);
  }
  private double InterpFactor;
  private MathState diff;
  private MathState State;
  private final Polynomial deltaPolyLower =new Polynomial(new double[] {0.5,0,2},Polynomial.order.ConstFirst);
  private final Polynomial deltaPolyMiddle=new Polynomial(new double[] {0.5,0,8,-16,8},Polynomial.order.ConstFirst);
  private final Polynomial deltaPolyUpper =new Polynomial(new double[] {2.5,-4,2},Polynomial.order.ConstFirst);
  private double delta() {
    if(InterpFactor<0) {
      return deltaPolyLower.eval(InterpFactor);
    } else if(InterpFactor>1) {
      return deltaPolyUpper.eval(InterpFactor);
    } else {
      return deltaPolyMiddle.eval(InterpFactor);
    }
  }
  public double getInterpFactor() {
    return InterpFactor;
  }
  public MathState getUncertainty() {
    return new MathState(new MathState(abs(diff.get(0)),abs(diff.get(1)),abs(diff.get(2)),
                                     abs(diff.get(3)),abs(diff.get(4)),abs(diff.get(5))).mul(delta()));
  }
  public MathState getUncertaintyRTN() {
    MathMatrix RTN=Ephemeris.RTN(State);
    MathVector diffRTN=RTN.transform(diff);
    return new MathState(new MathState(abs(diffRTN.get(0)),abs(diffRTN.get(1)),abs(diffRTN.get(2)),
                                     abs(diffRTN.get(3)),abs(diffRTN.get(4)),abs(diffRTN.get(5))).mul(delta()));
  }
  public MathState getDiff() {
    return diff;
  }
  public MathState CalcState(Time TT) {
    if(S.size()==0) return S.get(0).CalcState(TT);
    int closest[]=findTwoClosest(TT);
    EarthSatVSGP4 S0,S1;
    S0=S.get(closest[0]);
    Time T0=S0.epoch;
    S1=S.get(closest[1]);
    Time T1=S1.epoch;
    MathState PV0=S0.CalcState(TT);
    MathState PV1=S1.CalcState(TT);
    State=new MathState(MathState.linterp(T0.get(), PV0, T1.get(), PV1, TT.get()));
    InterpFactor=Scalar.linterp(T0.get(), 0, T1.get(), 1, TT.get());
    diff=new MathState(MathState.sub(PV0,PV1));
    return State;
  }
  private int[] findTwoClosest(Time TT) {
    if (epochs.size()<2) throw new IllegalArgumentException("Can't find two closest elements in an array with less than two elements (epochs.size()="+epochs.size()+")");
    int closest=findClosest(TT);
    //Temporarily remove the closest from the list
    long closest_epoch=epochs.remove(closest);
    //Search the remainder of the list for the next closest
    int next_closest=findClosest(TT);
    epochs.add(closest,closest_epoch);
    //Correct the next-closest if necessary
    if(next_closest>=closest) next_closest++;
    int first,last;
    if (closest<next_closest) {
      first=closest;
      last=next_closest;
    } else {
      first=next_closest;
      last=closest;
    }
    return new int[] {first,last};
  }
  private int findClosest(Time TT) {
    if (epochs.size()<=1) return 0;
    int index=Collections.binarySearch(epochs,TT.getTime());
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
    long dm1=abs(epochs.get(index-1)-TT.getTime());
    long d0 =abs(epochs.get(index)  -TT.getTime());
    return (dm1<d0)?(index-1):index;
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
    EarthSatSGP4File E=new EarthSatSGP4File("Data/EarthSatSGP4Vallado/ISS.tle");
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
    for(Time TT=E.startTime();TT.compareTo(Tlast)<0;TT.add(5*60,Seconds)) {
      double t=TT.get(Days)-Tfirst.get(Days);
      C.Record(t,"Time",TT);
      C.Record(t, "Official State", "km,s",E.getState(TT));
      for(i=0;i<E.size();i++) { 
        C.Record(t,ENames[i],"km,s",E.S.get(i).getState(TT));
      } 
      int[] bounds=E.findTwoClosest(TT);
      C.Record(t, "Lower index", bounds[0]);
      C.Record(t, "Upper index", bounds[1]);
      C.Record(t, "Uncertainty", "km",E.getUncertainty());
      C.Record(t, "UncertaintyRTN", "km",E.getUncertaintyRTN());
      double Umag=E.getUncertainty().R().length();
      C.Record(t,"Uncertainty magnitude", "km", Umag);
    }
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
  public SGPFZoetrope(String LWindowTitle, int LFramePeriodMs, ChartRecorder LC, EarthSatSGP4File LE) {
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
