/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

/**
 *
 * @author jeppesen
 */
public class SGP4Exception extends Exception {
  public int code;
  public double tsince;
  public SGP4Exception(String string, int Lcode, double Ltsince) {
    super(string);
    code=Lcode;
    tsince=Ltsince;
  }
}
