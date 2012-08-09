package org.kwansystems.space.porkchop;

import org.kwansystems.space.kepler.Course;
import org.kwansystems.space.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.time.*;

public interface PorkchopUI {
  public void setModel(PorkchopModel Lmodel);
  public void updateDepartHp(double departHp);
  public void updateArriveHp(double arriveHp);
  public void updateDepartTime(Time departTime);
  public void updateArriveTime(Time departTime);
  public void updateCourse(Course course);
  public void updateDepartPlanet(Planet departPlanet);
  public void updateArrivePlanet(Planet arrivePlanet);
  public void updateConstrain(boolean constrainDepart, boolean constrainArrive);
}
