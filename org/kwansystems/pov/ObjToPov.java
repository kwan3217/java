//ObjToPov.java 0.02
//Chris Jeppesen - chrisj@digiquill.com
//Version history: 
//0.03    Sep  2007                Embrace the Suck. Go heavy with Java3d. Use it's triangulator and smoother.
//0.02 01 July 2003                Source recovered from news.povray.org
//                                 Properly converts right-handed OBJ vectors to lefthanded vectors for POV-Ray
//                                 Optionally produces smooth triangles
//0.01 23 October 2001 - Mars Day! Original release

//You are free to use or modify this code for any non-commercial use
//For any other use, contact me
//Please send me any patches you may make

package org.kwansystems.pov;

import org.kwansystems.tools.vector.*;

import java.io.*;
import java.util.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;

public class ObjToPov {
  public static void main(String[] args) throws IOException {
    System.out.println("Kwan Systems ObjToPov: Wavefront .OBJ to POV-Ray mesh2 converter");
    // if (args.length!=2) {
    // System.out.println("usage: java ObjToPov infile.obj outfile.inc");
    // } else {
    // ConvertOBJ(args[0],args[1]);
    // }
    LazyConvertOBJ("ant_assy");
    LazyConvertOBJ("bus_assy");
    LazyConvertOBJ("lvr_assy");
    LazyConvertOBJ("pm_assy");
    LazyConvertOBJ("rocket_assy");
    LazyConvertOBJ("rsp_assy");
    LazyConvertOBJ("rtg_assy");
    LazyConvertOBJ("sci_assy");
    LazyConvertOBJ("huy_assy");
    LazyConvertOBJ("tank_assy");
    LazyConvertOBJ("uss_assy");
    //LazyConvertOBJ("AimSpacecraft");
    System.out.println("Done.");
  }

  public static void LazyConvertOBJ(String fn) throws FileNotFoundException, IOException {
    System.out.println("\n---"+fn+"---");
    String inpath = "/usr/codebase/pov/Cassini/";
    String outpath = "/usr/codebase/pov/Cassini/";
    String infn = inpath + fn + ".obj";
    String oufn = outpath + fn + ".inc";
    ConvertOBJ(infn, oufn);
  }
  public static void ConvertOBJ(String infn, String oufn) throws FileNotFoundException, IOException {
    PrintWriter Ouf = new PrintWriter(new FileWriter(oufn));
    Ouf.println("//" + oufn);
    Ouf.println("//Kwan Systems ObjToPov");
    Ouf.println("//Converted from " + infn);

    LineNumberReader Inf = new LineNumberReader(new FileReader(infn));
    String S = Inf.readLine();
    ArrayList<MathVector> V = new ArrayList<MathVector>();
    ArrayList<MathVector> VN = new ArrayList<MathVector>();
    V.add(new MathVector()); //Null vector at the beginning, so that indexes match up with 
                             //obj convention of 1-based vector list
    Polyhedron CurrentPoly = null;
    boolean inFacets=false;
    String groupName=null;
    while (S != null) {
      if (S.length() > 0) {
        String[] SPart=S.split("\\s+");
        if (SPart[0].equals("v")) {
          double X = Double.parseDouble(SPart[1]);
          double Y = Double.parseDouble(SPart[2]);
          double Z = Double.parseDouble(SPart[3]);
          MathVector P = new MathVector(X, Z, Y); // Convert to POV-Ray left handedness
          V.add(P);
/*        } else if (SPart[0].equals("vn")) {
          double X = Double.parseDouble(SPart[1]);
          double Y = Double.parseDouble(SPart[2]);
          double Z = Double.parseDouble(SPart[3]);
          MathVector P = new MathVector(X, Z, Y); // Convert to POV-Ray left handedness
          VN.add(P); */
        } else if (SPart[0].equals("g")) {
          if(inFacets) {
            // finish off old polyhedron
            if (CurrentPoly != null) {
              CurrentPoly.finish(V,VN);
              Ouf.println(CurrentPoly.toPov(groupName));
            }
            // Initialize polyhedron
            CurrentPoly = new Polyhedron();
          }
          if (SPart.length>1) {
            groupName=SPart[1];
          } else {
            groupName="";
          }
        } else if (SPart[0].equals("usemtl")) {
          inFacets=true;
          // finish off old polyhedron
          if (CurrentPoly != null) {
            CurrentPoly.finish(V,VN);
            Ouf.println(CurrentPoly.toPov(groupName));
          }
          // Initialize polyhedron
          CurrentPoly = new Polyhedron(S);
        } else if(SPart[0].equals("f")) {
          CurrentPoly.parseFacet(S);
        } else {
          System.out.println(S);
        }
      }
      S = Inf.readLine();
    }
    if (CurrentPoly != null) {
      CurrentPoly.finish(V,VN);
      Ouf.println(CurrentPoly.toPov(groupName));
    }
    System.out.println("Read " + (V.size() - 1) + " vectors");
    Inf.close();
    Ouf.close();
  }
}

class TaggedMathVector extends MathVector {
  private static final long serialVersionUID = -248608380580823449L;
  int Tag;

  public TaggedMathVector(MathVector v, int t) {
    super(v);
    Tag = t;
  }
}

class Polyhedron {
  public List<MathVector> VertexList; // Array of MathVector, Local Vertex List
  public List<MathVector> NormalList = null; // Array of normal vectors,
  public List<int[]> FacetsVertexIndexes = null; // Array of int[]
  public List<int[]> FacetsNormalIndexes = null; // Array of int[]
  String Material;
  static String lastMaterial;

  public void parseFacet(String S) {
    String[] SPart=S.split("\\s+");
    int len=SPart.length-1;
    int[] NewVertices = new int[len];
    int[] NewNormals = new int[len];
    for(int i=0;i<len;i++) {
      String[] SSubPart=SPart[i+1].split("/");
      NewVertices[i] = Integer.parseInt(SSubPart[0]);
      if (SSubPart.length>2) NewNormals[i]=Integer.parseInt(SSubPart[2]);
    }
    FacetsVertexIndexes.add(NewVertices);
    FacetsNormalIndexes.add(NewNormals);
  }
  public Polyhedron() {
    this("usemtl "+lastMaterial);
  }
  public Polyhedron(String usemtl) {
    Material = usemtl.substring(7);
    lastMaterial=Material;
    FacetsVertexIndexes = new ArrayList<int[]>();
    FacetsNormalIndexes = new ArrayList<int[]>();
  }
  public String toString() {
    return Material;
  }
  public String toPov(String groupName) {
    StringBuffer S = new StringBuffer();
    if(groupName!=null) S.append("//Group Name: "+groupName+"\n");
    
    if (FacetsVertexIndexes.size() > 0) {
      S.append("mesh2 {\n");
      S.append("  vertex_vectors {\n");
      S.append("    " + VertexList.size() + "\n");
      for (MathVector F : VertexList) {
        S.append("    <" + F.X() + "," + F.Y() + "," + F.Z() + ">\n");
      }
      S.append("  }\n");
      if (NormalList != null) {
        S.append("  normal_vectors {\n");
        S.append("    " + NormalList.size() + "\n");
        for(MathVector N:NormalList) {
          S.append("    <" + N.X() + "," + N.Y() + "," + N.Z() + ">\n");
        }
        S.append("  }\n");
      }
      S.append("  face_indices {\n");
      S.append("    " + FacetsVertexIndexes.size() + "\n");
      for (int[] F : FacetsVertexIndexes) {
        S.append("    <" + (F[0]) + "," + (F[1]) + "," + (F[2]) + ">\n");
      }
      S.append("  }\n");
      if (NormalList != null) {
        S.append("  normal_indices {\n");
        S.append("    " + FacetsNormalIndexes.size() + "\n");
        for (int[] F : FacetsNormalIndexes) {
          S.append("    <" + (F[0]) + "," + (F[1]) + "," + (F[2]) + ">\n");
        }
        S.append("  }\n");
      }
      S.append("  texture {" + Material + "}\n");
      S.append("}\n");
    } else {
      S.append("//Null mesh\n");
    }
    return S.toString();
  }
  public GeometryInfo toGI(ArrayList<MathVector> V, ArrayList<MathVector> VN) {
    int[] stripCount=new int[FacetsVertexIndexes.size()];
    int i=0;
    int totalVectors=0;
    for(int[] facet:FacetsVertexIndexes) {
      stripCount[i]=facet.length;
      totalVectors+=facet.length;
      i++;
    }
    double[] coords=new double[totalVectors*3];
    i=0;                               
    for(int[] facet:FacetsVertexIndexes) {
      for(int j=0;j<facet.length;j++) {
        coords[i*3+0]=V.get(facet[j]).X();
        coords[i*3+1]=V.get(facet[j]).Y();
        coords[i*3+2]=V.get(facet[j]).Z();
        i++;
      }
    }
    GeometryInfo GI=new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
    GI.setCoordinates(coords);
    GI.setStripCounts(stripCount);
    return GI;    
  }
  public void fromGI(GeometryInfo GI) {
    Point3f[] v=GI.getCoordinates();
    VertexList=new ArrayList<MathVector>();
    for(Point3f p:v) {
      VertexList.add(new MathVector(p.x,p.y,p.z));
    }

    int[] vl=GI.getCoordinateIndices();
    FacetsVertexIndexes=new ArrayList<int[]>();
    for(int i=0;i<vl.length/3;i++) {
      int[] facet=new int[3];
      facet[0]=vl[i*3+0];facet[1]=vl[i*3+1];facet[2]=vl[i*3+2];
      FacetsVertexIndexes.add(facet);
    }

    Vector3f[] vn=GI.getNormals();
    NormalList=new ArrayList<MathVector>();
    for(Vector3f p:vn) {
      NormalList.add(new MathVector(p.x,p.y,p.z));
    }

    int[] vnl=GI.getNormalIndices();
    if (vnl.length!=vl.length) throw new RuntimeException("Huh?");
    FacetsNormalIndexes=new ArrayList<int[]>();
    for(int i=0;i<vnl.length/3;i++) {
      int[] facet=new int[3];
      facet[0]=vnl[i*3+0];facet[1]=vnl[i*3+1];facet[2]=vnl[i*3+2];
      FacetsNormalIndexes.add(facet);
    }
  }
  public void removeDegenerateFacet1(List<int[]> F1, List<int[]> F2) {
    int i=0;
    for(Iterator<int[]> I=F1.iterator();I.hasNext();i++) {
      int[] f=I.next();
      if(f[0]==f[1] || f[0]==f[2] || f[1]==f[2]) {
        I.remove();
        F2.remove(i);
        i--;
      }
    }
  }
  class GrabVectorsResult {
    public List<int[]> Indexes;
    public List<MathVector> Vectors;
    public GrabVectorsResult() {
      Indexes=new ArrayList<int[]>();
      Vectors=new ArrayList<MathVector>();
    }
  }
  public GrabVectorsResult grabVectors(List<int[]> Indexes, List<MathVector> Vectors) {
    Map<Integer,Integer> NewToOld=new TreeMap<Integer,Integer>();
    Map<Integer,Integer> OldToNew=new TreeMap<Integer,Integer>();
    GrabVectorsResult result=new GrabVectorsResult();

    //Number of vectors seen so far. This is also the new index of the next old vector seen

    //Gather the used vertex vectors, and construct a new->old mapping
    for(int[] facet:FacetsVertexIndexes) {
      int newFacet[]=new int[3];
      //For each vertex vector referenced...
      for (int i=0;i<facet.length;i++) {
        int n;  //New index to write in the facet
        int o=facet[i];
        if(!OldToNew.containsKey(o)) { //If this vector hasn't been seen yet...
          n=result.Vectors.size();           //See how many are in the list now, 
                                                 //this will be the new index of the vector
          result.Vectors.add(VertexList.get(o)); //Put the vector in the vector list
          NewToOld.put(n,o);
          OldToNew.put(o,n);
        } else {
          n=OldToNew.get(o);
        }
        newFacet[i]=n;
      }
      result.Indexes.add(newFacet);
    }
    return result;
  }
  public void finish(ArrayList<MathVector> V, ArrayList<MathVector> VN) {
    GeometryInfo GI=toGI(V,VN);
    NormalGenerator NG=new NormalGenerator(Math.toRadians(44));
    NG.generateNormals(GI);
    GI.compact();
    fromGI(GI);
  }
}