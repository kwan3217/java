/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;

public class Material extends Property {
  String s;
  protected Material(int LpropType, InputStream Inf) throws IOException {
    super(LpropType,Inf);
    s=new String(propData);
    propData=null;
  }
  public String toString() {
    return s;
  }
}
