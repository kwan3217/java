/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class VertexList extends BTGObject {
  float[] x,y,z;
  protected VertexList(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
    int acc=0;
    for(int i=0;i<elements.length;i++) acc+=elements[i].length;
    n=acc/12;
    x=new float[n];
    y=new float[n];
    z=new float[n];
    int k=0;
    for(int i=0;i<elements.length;i++) {
      for(int j=0;j<elements[i].length/12;j++) {
        x[k]=swapEndian(Endian.readFloat(elements[i], j*12+0));
        y[k]=swapEndian(Endian.readFloat(elements[i], j*12+4));
        z[k]=swapEndian(Endian.readFloat(elements[i], j*12+8));
        k++;
      }
    }
    elements=null;
  }
  public String toString() {
    return String.format("Vertex List: %d vertices",x.length);
  }
  public String toPov() {
    StringWriter S=new StringWriter();
    PrintWriter ouf=new PrintWriter(S);
    for(int i=0;i<x.length;i++) ouf.printf(",\n    <%13.6e,%13.6e,%13.6e>",x[i],y[i],z[i]);
    return S.toString();
  }
}

