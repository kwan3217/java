
package org.kwansystems.space.windtunnel;

import org.kwansystems.vector.*;
import org.kwansystems.planet.*;

public class Airfoils extends ForceMomentGenerator {
  public ForceMomentGenerator[] Foils;
  public String Name;
  public String[] Names;
  public Airfoils(String LName, String[] LNames, ForceMomentGenerator[] LFoils) {
    Name=LName;
    Names=LNames;
    Foils=LFoils;
  }
  public ForceMoment getForceMoment(Planet E, MathState X) {
    ForceMoment FM=new ForceMoment();
    FM.Force=new MathVector(0,0,0);
    FM.Moment=new MathVector(0,0,0);
    for(int i=0;i<Foils.length;i++) {
      ForceMoment AF2=Foils[i].getForceMoment(E,X);
      FM.Force.add(AF2.Force);
      FM.Moment.add(AF2.Moment);
    }
    return FM;
  }
  public static void main(String[] args) {
    Planet E=new Earth();
    ForceMomentGenerator[] A={
      new PegasusXLWing(new MathVector(0,0,1),new MathVector(1,0,0),new MathVector(0,0,0)),
      new PointMass(23010,new MathVector(10,0,0))
    };
    Airfoils As=new Airfoils("PegasusXL",new String[]{"Wing","Body Mass"},A);
    MathState X=new MathState(0,0,6378137,100,0,0);
    As.getForceMoment(E,X);
  }
}
