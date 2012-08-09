/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

import java.awt.*;
import java.util.*;
import org.kwansystems.tools.zoetrope.*;

/**
 *
 * @author jeppesen
 */
public class SatPosZoetrope extends Zoetrope {
  List<List<int[]>> satData;
  public SatPosZoetrope(String LWindowTitle, int LFramePeriodMs) {
    super(LWindowTitle,LFramePeriodMs);
    satData=new ArrayList<List<int[]>>
  }

  @Override
  protected void paintFrame(Graphics G) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
