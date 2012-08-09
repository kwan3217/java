package org.kwansystems.pov;

import java.io.*;
import java.util.*;

import org.kwansystems.pov.mesh.*;

public class StL2Mesh2 extends Convert2Mesh2 {
  public ArrayList<Triangle> read(String infn, PointKeeper PK, Bounds b) throws IOException {
    ArrayList<Triangle>TL=new ArrayList<Triangle>();
    String stuff;
    LineNumberReader inf;
    inf=new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(infn))));
    System.err.println("//Reading stl file...");
    stuff=inf.readLine();
    String[] values=stuff.split("\\s+");
    String SolidName=values[1];
    SolidName=SolidName.replaceAll("-","_");
    b.Name=SolidName;
    /* facet/endsolid line (priming read) */
    stuff=inf.readLine();
    while(stuff.compareTo("endsolid")!=0) {
      /* outer loop */
      stuff=inf.readLine();
      /* vectors */
      stuff=inf.readLine();
      Triangle T=new Triangle(PK);
      Point A=new Point(stuff,b);
      stuff=inf.readLine();
      Point B=new Point(stuff,b);
      stuff=inf.readLine();
      Point C=new Point(stuff,b);
      //Drop degenerate triangles.
      if(isGenerate(A,B,C)) {
        T.a=PK.add(A,T);
        T.b=PK.add(B,T);
        T.c=PK.add(C,T);
        T.calcNormal();
        TL.add(T);
      }
      /* endloop*/
      stuff=inf.readLine();
      /* endfacet */
      stuff=inf.readLine();
      /* facet/endsolid */
      stuff=inf.readLine();
    }
    return TL;
  }
  public static void main(String[] args) throws IOException {
	PrintStream ouf;
	if(args.length>1) {
	  ouf=new PrintStream(args[1]);
	} else {
	  ouf=System.out;
	}
	new StL2Mesh2().convert(args[0],ouf);
  }
}
