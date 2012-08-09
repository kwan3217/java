/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class TriangleFan extends Triangles {
  protected TriangleFan(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
  }
  public String toString() {
    String result=String.format("Triangle Fan: %d triangles\n",nTriangles);
    for(int i=0;i<props.length;i++) result+="  Property "+String.format("%4d",i)+": "+props[i].toString()+"\n";
    result+=String.format("  Elements: %d\n",elements.length);
    return result;
  }
}
