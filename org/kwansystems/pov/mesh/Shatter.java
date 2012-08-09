package org.kwansystems.pov.mesh;

import java.io.*;
import java.util.*;

public class Shatter {
  public static void main(String[] args) throws IOException {
    // Open the mesh file
    LineNumberReader Inf=new LineNumberReader(new FileReader("c:\\Orbiter06-P1\\KwanSolarPanel.mshpart"));
    PrintStream Ouf=new PrintStream(new FileOutputStream("c:\\Orbiter06-P1\\KwanOneSolarPanel.mshpart"));
    // Read the header
    String S=Inf.readLine();
    String[] part=S.split(" ");
    int numVertices=Integer.parseInt(part[1]);
    int numFacets=Integer.parseInt(part[2]);
    // Allocate space for the data
    double[][] Vertices=new double[numVertices][8];
    int[] VertexMap=new int[numVertices];
    int[][] Facets=new int[numFacets][3];
    int[][] mappedFacets=new int[numFacets][3];
    // Read all the vertices
    for (int v=0; v<numVertices; v++) {
      S=Inf.readLine();
      part=S.split(" ");
      for (int p=0; p<8; p++)
        Vertices[v][p]=Double.parseDouble(part[p]);
    }
    // Read all the facets
    for (int f=0; f<numFacets; f++) {
      S=Inf.readLine();
      part=S.split(" ");
      for (int p=0; p<3; p++) {
        Facets[f][p]=Integer.parseInt(part[p]);
      }
    }
    // Build the matching vertex map
    VertexMap[0]=0;
    for (int i=1; i<numVertices; i++) {
      VertexMap[i]=i;
      for (int j=i-1; j>=0; j--) {
        if (Vertices[i][0]==Vertices[j][0]&&Vertices[i][1]==Vertices[j][1]&&Vertices[i][2]==Vertices[j][2]) {
          VertexMap[i]=j;
        }
      }
    }
    // Build the facet map with matched vertices
    for (int i=0; i<numFacets; i++) {
      for (int j=0; j<3; j++) {
        mappedFacets[i][j]=VertexMap[Facets[i][j]];
      }
    }
    // Find linkage.
    // 0) Facets are linked, not vertices
    // 1) The first unlinked facets is marked
    // 2) If any vertex in this facet are also in a previously marked facet,
    // this facet is marked
    // 3) If you have checked all facets, you have marked the linked ones
    // 4) Once all tiles are linked, you are done
    boolean done=false;
    boolean[] allLinked=new boolean[numFacets];
    int numGroups=0;
    while (!done) {
      boolean[] thisLinked=new boolean[numFacets];
      // Find the first facet not previously linked
      int firstUnlinked=-1;
      for (int i=0; i<numFacets; i++) {
        if (!allLinked[i]&&firstUnlinked<0) firstUnlinked=i;
      }
      if (firstUnlinked<0) {
        //If everything is linked, we are done
        done=true;
      } else {
        thisLinked[firstUnlinked]=true;
        for (int i=1; i<numFacets; i++) {// This facet
          for (int j=0; j<i; j++) {// The other facet
            if (thisLinked[j]) {
              for (int k=0; k<3; k++) {// This facet vertex index
                for (int l=0; l<3; l++) {// The other facet vertex index
                  if (mappedFacets[i][k]==mappedFacets[j][l]) {
                    thisLinked[i]=true;
                  }
                }
              }
            }
          }
        }
        // Count linked facets
        int numLinkedFacets=0;
        for (int i=0; i<numFacets; i++)
          if (thisLinked[i]) numLinkedFacets++;
        // Gather and order used vertices
        int[] new2oldVertices=new int[numVertices];
        int[] old2newVertices=new int[numVertices];
        boolean[] linkedVertexRecorded=new boolean[numVertices];
        int numLinkedVertices=0;
        for (int i=0; i<numFacets; i++) {
          if (thisLinked[i]) {
            for (int j=0; j<3; j++) {
              if (!linkedVertexRecorded[Facets[i][j]]) {
                new2oldVertices[numLinkedVertices]=Facets[i][j];
                old2newVertices[Facets[i][j]]=numLinkedVertices;
                linkedVertexRecorded[Facets[i][j]]=true;
                numLinkedVertices++;
              }
            }
          }
        }
        // Write out new header
        Ouf.println("MATERIAL 10");
        Ouf.println("TEXTURE 0");
        Ouf.println("GEOM "+numLinkedVertices+" "+numLinkedFacets);
        // Write out new vertices.
        for (int i=0; i<numLinkedVertices; i++) {
          for (int j=0; j<8; j++) {
            Ouf.print(""+Vertices[new2oldVertices[i]][j]+" ");
          }
          Ouf.println();
        }
        // Write out new facets
        for (int i=0; i<numFacets; i++) {
          if (thisLinked[i]) {
            allLinked[i]=true;
            for (int j=0; j<3; j++) {
              Ouf.print(""+old2newVertices[Facets[i][j]]+" ");
            }
            Ouf.println();
          }
        }
        numGroups++;
      }
    }
    Ouf.println("GROUPS "+numGroups);
    Ouf.println("MATERIALS 1");
    Ouf.println("Material0");
    Ouf.println("MATERIAL Material0");
    Ouf.println("0 0 1 1");
    Ouf.println("0 0 1 1");
    Ouf.println("1 1 1 1");
    Ouf.println("0 0 0 1");
    Ouf.println("TEXTURES 1");
    Ouf.println("0");
  }
}
