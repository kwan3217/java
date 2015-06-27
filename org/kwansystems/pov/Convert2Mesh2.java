package org.kwansystems.pov;

import java.io.*;
import java.util.*;

import org.kwansystems.pov.mesh.Bounds;
import org.kwansystems.pov.mesh.Point;
import org.kwansystems.pov.mesh.PointKeeper;
import org.kwansystems.pov.mesh.PointKeeperNode;
import org.kwansystems.pov.mesh.Triangle;
import org.kwansystems.pov.mesh.TriangleFifo;
import org.kwansystems.tools.vector.*;

public abstract class Convert2Mesh2 {
  public abstract ArrayList<Triangle> read(String infn, PointKeeper PK, Bounds b) throws IOException;
  public void convert(String infn, PrintStream ouf) throws IOException {
    PointKeeper PK=new PointKeeper();
    Bounds b=new Bounds();
    ArrayList<Triangle> TL=read(infn,PK,b);
    
    System.err.println("//Orienting...");
    int TrisChecked=0;
    int TrisCheckPasses=0;
    boolean done=false;
    Triangle[] TA=TL.toArray(new Triangle[0]);
    System.err.println("//Triangles to check: "+TA.length+"...");
    TriangleFifo TF=new TriangleFifo();
    while(!done) {
      TrisCheckPasses++;
      int CheckThis=-1;
      for(int i=0;i<TA.length;i++) {
        if(!TA[i].Checked) CheckThis=i;
      }
      if(CheckThis>0) {
        System.err.println("//Orienting Pass "+TrisCheckPasses+"...");
        TA[CheckThis].Checked=true;
        TrisChecked++;
        TF.add(TA[CheckThis]);
        while(TF.hasStuff()) {
          Triangle T0=TF.get();
          Triangle T1=findAdjacent(T0,PK,T0.a,T0.b);
          Triangle T2=findAdjacent(T0,PK,T0.b,T0.c);
          Triangle T3=findAdjacent(T0,PK,T0.c,T0.a);
          if(T1!=null && !T1.Checked) {
            if(!T0.doesOrientationMatch(T1)) {
              T1.Reverse();
            }
            T1.Checked=true;
            TF.add(T1);
            TrisChecked++;
          }
          if(T2!=null && !T2.Checked) {
            if(!T0.doesOrientationMatch(T2)) {
              T2.Reverse();
            }
            T2.Checked=true;
            TF.add(T2);
            TrisChecked++;
          }
          if(T3!=null && !T3.Checked) {
            if(!T0.doesOrientationMatch(T3)) {
              T3.Reverse();
            }
            T3.Checked=true;
            TF.add(T3);
            TrisChecked++;
          }
        }
        System.err.println("//Checked "+TrisChecked+" Orientations...");
      } else {
        done=true;
      }
    }
    System.err.println("//Smoothing...");
    PointKeeper NPK=new PointKeeper();
    for(int i=0;i<TA.length;i++) {
      TA[i].na=NPK.add(Smooth(TA[i],TA[i].a,PK),TA[i]);
      TA[i].nb=NPK.add(Smooth(TA[i],TA[i].b,PK),TA[i]);
      TA[i].nc=NPK.add(Smooth(TA[i],TA[i].c,PK),TA[i]);
    }
    ouf.println(b);
    WriteMeshA(PK,NPK,TA,b.Name,ouf);
    System.err.println("//Done.");
  }
  public static void WriteMeshA(PointKeeper PK, PointKeeper NPK, Triangle[] TA, String SolidName, PrintStream ouf) {
    ouf.println("#declare "+SolidName+"Mesh=mesh2 {");
    ouf.println("  vertex_vectors {");
    System.err.println("//Writing vertices...");
    Point[] PA=PK.toArray();
    ouf.println("    "+PA.length);
    for(int i=0;i<PA.length;i++) {
      ouf.println("  "+PA[i]+",");
    }
    ouf.println("  }");
    System.err.println("//Writing normals...");
    Point[] NA=NPK.toArray();
    ouf.println("  #if(Smooth)normal_vectors {");
    ouf.println("    "+NA.length);
    for(int i=0;i<NA.length;i++) {
      ouf.println("    "+NA[i]);
    }
    ouf.println("  }#end");
    System.err.println("//Writing face indices...");
    ouf.println("  face_indices {");
    ouf.println("    "+TA.length);
    for(int i=0;i<TA.length;i++) {
      ouf.println("    "+TA[i].toString(false));
    }
    ouf.println("  }");
    System.err.println("//Writing normal indices...");
    ouf.println("  #if(Smooth) normal_indices {");
    ouf.println("    "+TA.length);
    for(int i=0;i<TA.length;i++) {
      ouf.println("    "+TA[i].toString(true));
    }
    ouf.println("  } #end");
    ouf.println("  translate -x*("+SolidName+"_Min.x+"+SolidName+"_Max.x)/2");
    ouf.println("  translate -z*("+SolidName+"_Min.z+"+SolidName+"_Max.z)/2");
    ouf.println("}");
  }
  public static void WriteMeshB(PointKeeper PK, PointKeeper NPK, Triangle[] TA, String SolidName, PrintStream ouf) {
    System.err.println("//Writing vertices...");
    Point[] PA=PK.toArray();
    ouf.println("#declare "+SolidName+"_vertex_vectors=array["+PA.length+"] {");
    for(int i=0;i<PA.length;i++) {
      ouf.println("  "+PA[i]+",");
    }
    ouf.println("  }");
    System.err.println("//Writing normals...");
    Point[] NA=NPK.toArray();
    ouf.println("#declare "+SolidName+"_normal_vectors=array["+NA.length+"] {");
    for(int i=0;i<NA.length;i++) {
      ouf.println("    "+NA[i]);
    }
    ouf.println("}");
    System.err.println("//Writing face indices...");
    ouf.println("#declare "+SolidName+"_face_indices=array["+TA.length+"] {");
    for(int i=0;i<TA.length;i++) {
      ouf.println("    "+TA[i].toString(false));
    }
    ouf.println("}");
    System.err.println("//Writing normal indices...");
    ouf.println("#declare "+SolidName+"_normal_indices=array["+TA.length+"] {");
    for(int i=0;i<TA.length;i++) {
      ouf.println("    "+TA[i].toString(true));
    }
    ouf.println("}");
    ouf.println("#declare "+SolidName+"Mesh=mesh2 {");
    ouf.println("  vertex_vectors {");
    ouf.println("    dimension_size("+SolidName+"_vertex_vectors,1)");
    ouf.println("    #local I=0; ");
    ouf.println("    #while(I<dimension_size("+SolidName+"_vertex_vectors,1))");
    ouf.println("      "+SolidName+"_vertex_vectors[I]");
    ouf.println("      #local I=I+1;");
    ouf.println("    #end");
    ouf.println("  }");
    ouf.println("  #if(Smooth)normal_vectors {");
    ouf.println("    dimension_size("+SolidName+"_normal_vectors,1)");
    ouf.println("    #local I=0; ");
    ouf.println("    #while(I<dimension_size("+SolidName+"_normal_vectors,1))");
    ouf.println("      "+SolidName+"_normal_vectors[I]");
    ouf.println("      #local I=I+1;");
    ouf.println("    #end");
    ouf.println("  }#end");
    ouf.println("  face_indices {");
    ouf.println("    dimension_size("+SolidName+"_face_indices,1)");
    ouf.println("    #local I=0; ");
    ouf.println("    #while(I<dimension_size("+SolidName+"_face_indices,1))");
    ouf.println("      "+SolidName+"_face_indices[I]");
    ouf.println("      #local I=I+1;");
    ouf.println("    #end");
    ouf.println("  }");
    ouf.println("  #if(Smooth) normal_indices {");
    ouf.println("    dimension_size("+SolidName+"_normal_indices,1)");
    ouf.println("    #local I=0; ");
    ouf.println("    #while(I<dimension_size("+SolidName+"_normal_indices,1))");
    ouf.println("      "+SolidName+"_normal_indices[I]");
    ouf.println("      #local I=I+1;");
    ouf.println("    #end");
    ouf.println("  } #end");
    ouf.println("  translate -x*("+SolidName+"_Min.x+"+SolidName+"_Max.x)/2");
    ouf.println("  translate -z*("+SolidName+"_Min.z+"+SolidName+"_Max.z)/2");
    ouf.println("}");
  }
  public static Triangle findAdjacent(Triangle T,PointKeeper PK,int a,int b) {
    PointKeeperNode Pa=PK.getNode(a);
    PointKeeperNode Pb=PK.getNode(b);
    ArrayList<Triangle> Aa=Pa.PointUsers;
    ArrayList<Triangle> Ab=Pb.PointUsers;
    for(int i=0;i<Aa.size();i++) {
      Triangle Ta=Aa.get(i);
      if(Ab.contains(Aa.get(i))) {
        Triangle Tb=Aa.get(i);
        if(Tb!=T) return Tb;
      }
    }
    return null;
  }
  public static boolean isGenerate(Point PA, Point PB, Point PC) {
    MathVector A=PA.toMathVector();
    MathVector B=PB.toMathVector();
    MathVector C=PC.toMathVector();
    MathVector V1=MathVector.sub(A,B);
    MathVector V2=MathVector.sub(B,C);
    MathVector N=MathVector.cross(V1,V2);
    N=N.normal();
    Point normal=new Point(N);
    if(Float.isNaN(normal.x)) {
      System.err.println("Aiee! NaN encountered. Degenerate triangle?");
      return false;
    }
    return true;
  }
  public static Point Smooth(Triangle T,int PKIndex,PointKeeper PK) {
    double dotlimit=0.5; //Dot products less than this indicate corners
    PointKeeperNode PKN=PK.getNode(PKIndex);
    Triangle[] TA=PKN.PointUsers.toArray(new Triangle[0]);
    Point NA=new Point(0,0,0);
    for(int i=0;i<TA.length;i++) {
      float d=T.normal.dot(TA[i].normal);
      if(d>dotlimit) { 
        NA.add(TA[i].normal);
      } else {
//        System.err.println("Corner. Not that there's anything wrong with that...");
      }
    }
    NA.normalize();
    float d=NA.dot(T.normal);
    if(Float.isNaN(d)) {
      System.err.println("Aiee! NaN encountered. Degenrate triangle slipped past?");
    }
    return NA;
  }
}

