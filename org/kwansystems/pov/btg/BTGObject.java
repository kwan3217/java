package org.kwansystems.pov.btg;

import java.io.*;
import static org.kwansystems.pov.btg.BTG2mesh2.*;

public class BTGObject implements Povable {
  int ObjectType;
  Property[] props;
  byte[][] elements;
  int n;
  protected BTGObject(int LobjectType, InputStream Inf) throws IOException {
    ObjectType=LobjectType;
    int nProps=readUShort(Inf);
    int nElements=readUShort(Inf);
    props=new Property[nProps];
    for(int i=0;i<nProps;i++) {
      props[i]=Property.read(Inf);
    }
    elements=new byte[nElements][];
    for(int i=0;i<nElements;i++) {
      long nBytes=readUInt(Inf);
      elements[i]=new byte[(int)nBytes];
      Inf.read(elements[i]);
    }
  }
  public static BTGObject read(InputStream Inf) throws IOException {
    int objType=readUByte(Inf);
    switch(objType) {
      case  0: return new BoundingSphere(objType,Inf);
      case  1: return new VertexList(objType,Inf);
      case  2: return new NormalList(objType,Inf);
      case  3: return new UVList(objType,Inf);
      case  4: return new ColorList(objType,Inf);
      case  9: return new Points(objType,Inf);
      case 10: return new Triangles(objType,Inf);
      case 11: return new TriangleStrip(objType,Inf);
      case 12: return new TriangleFan(objType,Inf);
      default: throw new IllegalArgumentException();
    }
  }
  public String toPov() {
    return "//"+this.getClass().getCanonicalName();
  }
  public String toString() {
    String result=this.getClass().getCanonicalName()+"\n";
    for(int i=0;i<props.length;i++) result+="  Property "+String.format("%4d",i)+": "+props[i].toString()+"\n";
    return result;
  }
}


