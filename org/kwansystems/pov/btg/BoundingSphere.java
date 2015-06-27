/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class BoundingSphere extends BTGObject {
  double x,y,z;
  float r;
  protected BoundingSphere(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
    x=swapEndian(Endian.readDouble(elements[elements.length-1],0));
    y=swapEndian(Endian.readDouble(elements[elements.length-1],8));
    z=swapEndian(Endian.readDouble(elements[elements.length-1],16));
    r=swapEndian(Endian.readFloat(elements[elements.length-1], 24));
    elements=null;
  }
  public String toPov() {
    return String.format("  bounded_by {sphere {0,%f}} translate <%f,%f,%f>\n", r,x,y,z);
  }
  public String toString() {
    return String.format("Bounding Sphere r: %f, center: <%f,%f,%f>\n", r,x,y,z);
  }
}

