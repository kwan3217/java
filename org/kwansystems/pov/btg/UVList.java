/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class UVList extends BTGObject {
  float[] u,v;

  protected UVList(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
    int acc=0;
    for(int i=0;i<elements.length;i++) acc+=elements[i].length;
    u=new float[acc/8];
    v=new float[acc/8];
    int k=0;
    for(int i=0;i<elements.length;i++) {
      for(int j=0;j<elements[i].length/8;j++) {
        u[k]=swapEndian(Endian.readFloat(elements[i], j*8+0));
        v[k]=swapEndian(Endian.readFloat(elements[i], j*8+4));
        k++;
      }
    }
    elements=null;
  }
  public String toString() {
    return String.format("UV List: %d UV vectors",u.length);
  }
}
