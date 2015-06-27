/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class ColorList extends BTGObject{
  float[] r,g,b,a;

  protected ColorList(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
    int acc=0;
    for(int i=0;i<elements.length;i++) acc+=elements[i].length;
    r=new float[acc/16];
    g=new float[acc/16];
    b=new float[acc/16];
    a=new float[acc/16];
    int k=0;
    for(int i=0;i<elements.length;i++) {
      for(int j=0;j<elements[i].length/12;j++) {
        r[k]=swapEndian(Endian.readFloat(elements[i], j*16+ 0));
        g[k]=swapEndian(Endian.readFloat(elements[i], j*16+ 4));
        b[k]=swapEndian(Endian.readFloat(elements[i], j*16+ 8));
        a[k]=swapEndian(Endian.readFloat(elements[i], j*16+12));
        k++;
      }
    }
    elements=null;
  }
  public String toString() {
    return String.format("Color List: %d colors",r.length);
  }
}
