/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.space.ephemeris;

import org.kwansystems.tools.time.*;

/**
 *
 * @author jeppesen
 */
public interface PlanetRotatorEphemeris {
  /** Calculate the angle called on Earth Greenwich Mean Sidereal Time
   * @param T Time in question
   * @return Angle in radians. On Earth, this angle is Greenwich Mean Sidereal Time (GMST).
   * On other planets, there is an equivalent angle.
   */
  public double Theta(Time T);

}
