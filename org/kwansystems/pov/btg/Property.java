package org.kwansystems.pov.btg;

import java.io.*;
import static org.kwansystems.pov.btg.BTG2mesh2.*;

public class Property {
  int propType;
  long propNumBytes;
  byte[] propData;
  protected Property(int LpropType, InputStream Inf) throws IOException {
    propType=LpropType;
    propNumBytes=readUInt(Inf);
    propData=new byte[(int)propNumBytes];
    Inf.read(propData);
  }
  public static Property read(InputStream Inf) throws IOException {
    int propType=readUByte(Inf);
    switch(propType) {
      case 0: return new Material(propType,Inf);
      case 1: return new IndexTypes(propType,Inf);
      default: throw new IllegalArgumentException();
    }
  }
}

