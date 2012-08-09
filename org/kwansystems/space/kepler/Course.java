package org.kwansystems.space.kepler;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.vector.*;
import java.io.*;

public class Course {
  public MathStateTime depart,arrive;
  public int type;
  public double TransAngle;
  public Course(MathStateTime Ldepart, MathStateTime Larrive, int Ltype) {
    depart=Ldepart;
    arrive=Larrive;
    type=Ltype;
    TransAngle=MathVector.vangle(depart.S.R(),arrive.S.R());
    boolean isMoreThanHalf=(type%2)==0;
    if(isMoreThanHalf) {
      TransAngle=2*Math.PI-TransAngle;
    }
    int extraRevs=(type-1)/2;
    TransAngle+=extraRevs*2*Math.PI;
  }
  public Terminal ResolveDeltaVdepart(Planet P, double LHp) {
    return new Terminal(new MathStateTime(depart.S,depart.T),P,LHp);
  }
  public Terminal ResolveDeltaVarrive(Planet P, double LHp) {
    return new Terminal(new MathStateTime(arrive.S,arrive.T),P,LHp);
  }
  @Override
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result);
    ouf.println(depart.toString());
    ouf.print(arrive.toString());
    return result.toString();
  }
}
