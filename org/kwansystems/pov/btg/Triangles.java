/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class Triangles extends BTGObject{
  int nTriangles;
  int[][] vertexIndex;
  int[][] normalIndex;
  int[][] colorIndex;
  int[][] UVIndex;
  protected Triangles(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
    IndexTypes I;
    for(Property P:props) {
      if(P instanceof IndexTypes) I=(IndexTypes)P;
    }
    
    for(int i=0;i<elements.length;i++);
  }
}
