/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;

public class IndexTypes extends Property {
  int indexTypes;
  protected IndexTypes(int LpropType, InputStream Inf) throws IOException {
    super(LpropType,Inf);
    indexTypes=propData[0];
    propData=null;
    indexTypes=indexTypes & 0xFF;
  }
  public boolean hasVertices() {return (indexTypes & 0x01)!=0;}
  public boolean hasNormals()  {return (indexTypes & 0x02)!=0;}
  public boolean hasColors()   {return (indexTypes & 0x04)!=0;}
  public boolean hasUV()       {return (indexTypes & 0x08)!=0;}
  public String toString() {
    return String.format("  Index Types: %d",indexTypes);
  }
}
