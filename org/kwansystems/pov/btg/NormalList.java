/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class NormalList extends BTGObject {
  float[] x,y,z;

  protected NormalList(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
    int acc=0;
    for(int i=0;i<elements.length;i++) acc+=elements[i].length;
    n=acc/3;
    x=new float[n];
    y=new float[n];
    z=new float[n];
    int k=0;
    for(int i=0;i<elements.length;i++) {
      for(int j=0;j<elements[i].length/3;j++) {
        x[k]=((int)elements[i][j*3+0]) & 0xFF;
        x[k]=x[k]/127.5f-1.0f;
        y[k]=((int)elements[i][j*3+1]) & 0xFF;
        y[k]=y[k]/127.5f-1.0f;
        z[k]=((int)elements[i][j*3+2]) & 0xFF;
        z[k]=z[k]/127.5f-1.0f;
        k++;
      }
    }
    elements=null;
  }
  public String toString() {
    return String.format("Normal List: %d Normals",x.length);
  }
  public String toPov() {
    StringWriter S=new StringWriter();
    PrintWriter ouf=new PrintWriter(S);
    for(int i=0;i<x.length;i++) ouf.printf(",\n    <%6.3f,%6.3f,%6.3f>",x[i],y[i],z[i]);
    return S.toString();
  }
}

