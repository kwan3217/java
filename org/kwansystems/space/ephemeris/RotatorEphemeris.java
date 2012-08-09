package org.kwansystems.space.ephemeris;

import org.kwansystems.graph.Vertex;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;

public abstract class RotatorEphemeris {
  public boolean inv;
  public Frame naturalFrom,naturalTo;
  public Frame from,to;
  protected RotatorEphemeris(Frame Lfrom, Frame Lto) {
    from=Lfrom;
    to=Lto;
  }
  protected void setInv(Frame from, Frame to) {
    if(from==naturalFrom && to==naturalTo) {
      inv=false;
    } else if(from==naturalTo && to==naturalFrom) {
      inv=true;
    } else throw new IllegalArgumentException("Transformation doesn't support from="+from+", to="+to);
  }
  protected abstract Rotator CalcRotation(Time T);
  public Rotator getRotation(Time T) {
    Rotator result=CalcRotation(T);
    if(inv) result=result.inv();
    return result;
  }
  public enum Frame implements Vertex {
    J2000Ecl("Mean Ecliptic and Equinox at J2000"), 
    J2000Equ("Mean Equator and Equinox at J2000"), 
    MOD("Mean Equator and Equinox of Date"), 
    TOD("True Equator and Equinox of Date"), 
    TEME("True Equator, Mean Equinox of Date"), 
    PEF("Pseudo Earth Fixed"), 
    ECEF("Earth Centered, Earth Fixed"),
    MercuryCenteredInertial("Mercury centered Intertial"),
    MercuryCenteredFixed("Mercury centered Fixed"),
    VenusCenteredInertial("Venus centered Intertial"),
    VenusCenteredFixed("Venus centered Fixed"),
    MarsCenteredInertial("Mars centered Intertial"),
    MarsCenteredFixed("Mars centered Fixed"),
    JupiterCenteredInertial("Jupiter centered Intertial"),
    JupiterCenteredFixed("Jupiter centered Fixed"),
    SaturnCenteredInertial("Saturn centered Intertial"),
    SaturnCenteredFixed("Saturn centered Fixed"),
    UranusCenteredInertial("Uranus centered Intertial"),
    UranusCenteredFixed("Uranus centered Fixed"),
    NeptuneCenteredInertial("Neptune centered Intertial"),
    NeptuneCenteredFixed("Neptune centered Fixed"),
    PlutoCenteredInertial("Pluto centered Intertial"),
    PlutoCenteredFixed("Pluto centered Fixed"),
    SunCenteredInertial("Sun centered Intertial"),
    SunCenteredFixed("Sun centered Fixed"),
    ELP2000Natural("ELP2000 Ephemeris natural frame");
    final String desc;
    private Frame(String Ldesc) {
      desc = Ldesc;
    }
  }
}
