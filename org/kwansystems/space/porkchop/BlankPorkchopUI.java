package org.kwansystems.space.porkchop;

import org.kwansystems.space.kepler.Course;
import org.kwansystems.space.planet.Planet;
import org.kwansystems.tools.time.Time;

public class BlankPorkchopUI implements PorkchopUI {
  PorkchopModel model;
  @Override public void updateArrivePlanet(Planet arrivePlanet) {  }
  @Override public void updateArriveTime(Time departTime) {  }
  @Override public void updateCourse(Course course) {  }
  @Override public void updateDepartPlanet(Planet departPlanet) {  }
  @Override public void updateDepartTime(Time departTime) {  }
  @Override public void updateConstrain(boolean constrainDepart, boolean constrainArrive) {  }
  @Override public void setModel(PorkchopModel Lmodel) { 
    model=Lmodel;
  }
  @Override public void updateDepartHp(double departHp) {  }
  @Override public void updateArriveHp(double arriveHp) {  }
}
