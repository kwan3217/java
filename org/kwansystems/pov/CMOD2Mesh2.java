package org.kwansystems.pov;

import java.io.*;
import java.util.*;
import static org.kwansystems.tools.Endian.*;
import static org.kwansystems.pov.CMOD2Mesh2.*;

public class CMOD2Mesh2 {
  public static final int CMOD_Material_ID       = 1001; //0x03E9
  public static final int CMOD_EndMaterial_ID    = 1002; //0x03EA
  public static final int CMOD_Diffuse_ID        = 1003; //0x03EB
  public static final int CMOD_Specular_ID       = 1004; //0x03EC
  public static final int CMOD_SpecularPower_ID  = 1005; //0x03ED
  public static final int CMOD_Opacity_ID        = 1006; //0x03EE
  public static final int CMOD_Texture_ID        = 1007; //0x03EF
  public static final int CMOD_Mesh_ID           = 1009; //0x03F1
  public static final int CMOD_EndMesh_ID        = 1010; //0x03F2
  public static final int CMOD_VertexDesc_ID     = 1011; //0x03F3
  public static final int CMOD_EndVertexDesc_ID  = 1012; //0x03F4
  public static final int CMOD_Vertices_ID       = 1013; //0x03F5
  public static final int CMOD_Emissive_ID       = 1014; //0x03F6
  public static final int CMOD_Blend_ID          = 1015; //0x03F7
  public static final int CMOD_Float1_ID         = 1;
  public static final int CMOD_Float2_ID         = 2;
  public static final int CMOD_Float3_ID         = 3;
  public static final int CMOD_Float4_ID         = 4;
  public static final int CMOD_String_ID         = 5;
  public static final int CMOD_Uint32_ID         = 6;
  public static final int CMOD_Color_ID          = 7;
  enum VertexSemantic {
    Position("vertex_vectors"),
    Color0("Color0"),
    Color1("Color1"),
    Normal("normal_vectors"),
    Tangent("Tangent"),
    Texture0("uv_vectors"),
    Texture1("Texture1"),
    Texture2("Texture2"),
    Texture3("Texture3"),
    PointSize("Color0");
    public String povrayStart=null;
    private VertexSemantic() {}
    private VertexSemantic(String LpovrayStart) {
      povrayStart=LpovrayStart;
    }
  };
  enum VertexFormat {
    Float1(1),
    Float2(2),
    Float3(3),
    Float4(4),
    UByte4(4) {
      public float[] read(RandomAccessFile Inf) throws IOException {
        float[] result=new float[stride];
        for(int i=0;i<result.length;i++) {
          byte b=Inf.readByte();
          long bb=b;
          bb &= 0xFF;
          result[i]=bb;
        }
        return result;
      }
    };
    public int stride;
    private VertexFormat(int Lstride) {
      stride=Lstride;
    }
    public float[] read(RandomAccessFile Inf) throws IOException {
      float[] result=new float[stride];
      for(int i=0;i<result.length;i++) {
        result[i]=swapEndian(Inf.readFloat());
      }
      return result;
    }
  };
  enum PrimitiveType {
    TriList,TriStrip,TriFan,LineList,LineStrip,PointList,SpriteList
  };
  class CMOD_Float1 {
    float data;
    public CMOD_Float1(float Ldata) {
      data=Ldata;
    }
    public CMOD_Float1(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Float1_ID) throw new IllegalArgumentException("Not a CMOD_Float1");
      data=swapEndian(Inf.readFloat());
    }
  };
  class CMOD_Float2 {
    float data1,data2;
    public CMOD_Float2(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Float2_ID) throw new IllegalArgumentException("Not a CMOD_Float2");
      data1=swapEndian(Inf.readFloat());
      data2=swapEndian(Inf.readFloat());
    }
  };
  class CMOD_Float3 {
    float data1,data2,data3;
    public CMOD_Float3(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Float3_ID) throw new IllegalArgumentException("Not a CMOD_Float3");
      data1=swapEndian(Inf.readFloat());
      data2=swapEndian(Inf.readFloat());
      data3=swapEndian(Inf.readFloat());
    }
  };
  class CMOD_Float4 {
    float data1,data2,data3,data4;
    public CMOD_Float4(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Float4_ID) throw new IllegalArgumentException("Not a CMOD_Float4");
      data1=swapEndian(Inf.readFloat());
      data2=swapEndian(Inf.readFloat());
      data3=swapEndian(Inf.readFloat());
      data4=swapEndian(Inf.readFloat());
    }
  };
  class CMOD_String {
    String s;
    public CMOD_String(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_String_ID) throw new IllegalArgumentException("Not a CMOD_String");
      Short len=swapEndian(Inf.readShort());
      byte[] b=new byte[len];
      Inf.read(b);
      s=new String(b);
    }
  };
  class CMOD_Uint32 {
    long data;
    public CMOD_Uint32(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Uint32_ID) throw new IllegalArgumentException("Not a CMOD_Uint32");
      int len=swapEndian(Inf.readInt());
      data=len;
      data &= 0xFFFFFFFF;
    }
  };
  class CMOD_Color {
    float red,green,blue;
    public String toString() {
      return String.format("rgb <%f,%f,%f>", red,green,blue);
    }
    public CMOD_Color(float Lred, float Lgreen, float Lblue) {
      red  =Lred;
      green=Lgreen;
      blue =Lblue;
    }
    public CMOD_Color(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Color_ID) throw new IllegalArgumentException("Not a CMOD_Color");
      red  =swapEndian(Inf.readFloat());
      green=swapEndian(Inf.readFloat());
      blue =swapEndian(Inf.readFloat());
    }
  };
  class CMOD_Material {
    CMOD_Color diffuse=new CMOD_Color(0,0,0);
    CMOD_Color specular=new CMOD_Color(0,0,0);
    CMOD_Color emissive=null;
    CMOD_Float1 specularPower=new CMOD_Float1(1);
    CMOD_Float1 opacity=new CMOD_Float1(1);
    short blend=0;
    CMOD_String[] texture=new CMOD_String[4];
    public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result);
      toString(ouf);
      return result.toString();
    }
    public void toString(PrintWriter ouf) {
      ouf.println("texture {");
      ouf.printf("  pigment {color %s}\n", diffuse);
      ouf.println("  finish {");
      ouf.printf("    reflection %s\n", specular);
      if(emissive!=null) ouf.printf("    ambient %s\n", emissive);
      if (specular.red>0) {
        ouf.printf("    phong %f phong_size %f\n",specular.red, specularPower.data);
      }
      ouf.printf("  }//opacity %f\n", opacity.data);
      ouf.print("}");
    }
    public CMOD_Material(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      while(type!=CMOD_EndMaterial_ID) {
        switch(type) {
          case CMOD_Diffuse_ID:
            diffuse=new CMOD_Color(Inf);
            break;
          case CMOD_Specular_ID:
            specular=new CMOD_Color(Inf);
            break;
          case CMOD_Emissive_ID:
            emissive=new CMOD_Color(Inf);
            break;
          case CMOD_Opacity_ID:
            opacity=new CMOD_Float1(Inf);
            break;
          case CMOD_SpecularPower_ID:
            specularPower=new CMOD_Float1(Inf);
            break;
          case CMOD_Blend_ID:
            blend=swapEndian(Inf.readShort());
            break;
          case CMOD_Texture_ID:
            int texType=swapEndian(Inf.readShort());
            texture[texType]=new CMOD_String(Inf);
            break;
        }
        type=swapEndian(Inf.readShort());
      }
    }
  }
  class CMOD_VertexDescription {
    List<VertexSemantic> semantic=new LinkedList<VertexSemantic>();
    List<VertexFormat> format=new LinkedList<VertexFormat>();
    public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result);
      ouf.println("  VertexDesc {");
      for(int i=0;i<semantic.size();i++) {
        ouf.printf("    %s (%s)\n",semantic.get(i),format.get(i));
      }
      ouf.print("  }");
      return result.toString();
    }
    public CMOD_VertexDescription(RandomAccessFile Inf) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_VertexDesc_ID) throw new IllegalArgumentException("Expected a Vertex Description");
      type=swapEndian(Inf.readShort());
      while(type!=CMOD_EndVertexDesc_ID) {
        int fmt=swapEndian(Inf.readShort());
        semantic.add(VertexSemantic.values()[type]);
        format.add(VertexFormat.values()[fmt]);
        type=swapEndian(Inf.readShort());
      }
    }
  }
  class CMOD_Vertices {
    CMOD_VertexDescription desc;
    long length;
    Map<VertexSemantic,float[][]> data=new HashMap<VertexSemantic,float[][]>();
    public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result);
      toString(ouf);
      return result.toString();
    }
    public void toString(PrintWriter ouf) {
      for(VertexSemantic s:desc.semantic) {
        float[][] d=data.get(s);
        ouf.printf("  %s {\n",s.povrayStart);
        ouf.printf("    %d",d.length);
        for(int i=0;i<d.length;i++) {
          ouf.printf(",\n    <%f",d[i][0]);
          for(int j=1;j<d[i].length;j++) {
            ouf.printf(",%f",d[i][j]);
          }
          ouf.print(">");
        }
        ouf.println("\n  }");
      }
    }
    public CMOD_Vertices(RandomAccessFile Inf, CMOD_VertexDescription Ldesc) throws IOException {
      int type=swapEndian(Inf.readShort());
      if(type!=CMOD_Vertices_ID) throw new IllegalArgumentException("Expected a Vertex Description");
      desc=Ldesc;
      int len=swapEndian(Inf.readInt());
      length=len;
      length &= 0xFFFFFFFF;
      for(int i=0;i<desc.format.size();i++) {
        data.put(desc.semantic.get(i), new float[(int)length][]);
      }
      for(int i=0;i<length;i++) {
        for(int j=0;j<desc.format.size();j++) {
          data.get(desc.semantic.get(j))[i]=desc.format.get(j).read(Inf);
        }
      }
    }
  }
  class PrimitiveGroup {
    PrimitiveType type;
    int materialIdx;
    int[] data;
    public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result);
      toString(ouf);
      return result.toString();
    }
    public void toString(PrintWriter ouf) {
      ouf.println("  face_indices {");
      ouf.printf("    %d",data.length/3);
      for(int i=0;i<data.length/3;i++) {
        ouf.printf(",\n    <%d",data[i*3+0]);
        for(int j=1;j<3;j++) {
          ouf.printf(",%d",data[i*3+j]);
        }
        ouf.print(">");
      }
      ouf.println("\n  }");
    }
    public PrimitiveGroup(int Ltype, RandomAccessFile Inf) throws IOException {
      type=PrimitiveType.values()[Ltype];
      materialIdx=swapEndian(Inf.readInt());
      int indexCount=swapEndian(Inf.readInt());
      data=new int[indexCount];
      for(int i=0;i<indexCount;i++) {
        data[i]=swapEndian(Inf.readInt());
      }
    }
  }
  class CMOD_Mesh {
    CMOD_VertexDescription desc;
    CMOD_Vertices V;
    List<PrimitiveGroup> groups=new LinkedList<PrimitiveGroup>();
    public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result);
      toString(ouf);
      return result.toString();
    }
    public void toString(PrintWriter ouf) {
      int mat=groups.get(0).materialIdx;
//      if(mat!=674 && mat!=39) {
        ouf.println("mesh2 {");
        ouf.println(V.toString());
        for(PrimitiveGroup G:groups) {
          ouf.println(G.toString());
        }
        //HGA dish is at 769
        //IRIS weird cover is 674 and 39
        int start=39;
        int step=0;
        if(mat>=start+0*step && mat<start+1*step) {
          ouf.println("  pigment {color rgb <1,0,0>}");
        } else if(mat>=start+1*step && mat<start+2*step) {
          ouf.println("  pigment {color rgb <1,0.5,0>}");
        } else if(mat>=start+2*step && mat<start+3*step) {
          ouf.println("  pigment {color rgb <1,1,0>}");
        } else if(mat>=start+3*step && mat<start+4*step) {
          ouf.println("  pigment {color rgb <0,1,0>}");
        } else if(mat>=start+4*step && mat<start+5*step) {
          ouf.println("  pigment {color rgb <0,1,1>}");
        } else if(mat>=start+5*step && mat<start+6*step) {
          ouf.println("  pigment {color rgb <0,0,1>}");
        } else if(mat>=start+6*step && mat<start+7*step) {
          ouf.println("  pigment {color rgb <1,0,1>}");
        } else {
//          ouf.printf("  #if(frame_number=%03d)pigment{color rgb <1,0,0>} #else texture {material%03d}#end\n",mat,mat);
//          ouf.printf("  texture {material%03d}\n",mat);
        }
        ouf.print("}");
//      }
    }
    public CMOD_Mesh(RandomAccessFile Inf) throws IOException {
      desc=new CMOD_VertexDescription(Inf);
      V=new CMOD_Vertices(Inf,desc);
      int type=swapEndian(Inf.readShort());
      while(type!=CMOD_EndMesh_ID) {
        groups.add(new PrimitiveGroup(type,Inf));
        type=swapEndian(Inf.readShort());
      }
    }
  }
  String header;
  LinkedList<CMOD_Material> materials=new LinkedList<CMOD_Material>();
  LinkedList<CMOD_Mesh> meshes=new LinkedList<CMOD_Mesh>();
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result);
    toString(ouf);
    return result.toString();
  }
  public void toString(PrintWriter ouf) {
    int i=0;
    ouf.printf("#declare materialArray=array[%d];",materials.size());
    for(CMOD_Material M:materials) {
      ouf.printf("#declare material%03d=",i);
      if(i==769) {
        M.specular.red=0;
        M.specular.green=0;
        M.specular.blue=0;
        M.diffuse.red=1;
        M.diffuse.green=1;
        M.diffuse.blue=1;
        M.specularPower.data=4.0f;
      }
      M.toString(ouf);
      ouf.printf("#declare materialArray[%d]=material%03d;",i,i);
      ouf.println();
      i++;
    }
    i=0;
    ouf.printf("#declare partArray=array[%d];",meshes.size());
    for(CMOD_Mesh M:meshes) {
      ouf.printf("#declare partArray[%d]=%s\n",i,M.toString());
//      ouf.printf("d=",i);
      i++;
    }
    ouf.println("#declare Voyager=union {");
    ouf.println("  #local I=0;");
    ouf.println("  #while(I<dimension_size(partArray,1))");
    ouf.println("    object{partArray[I] texture {materialArray[I]}}");
    ouf.println("    #local I=I+1;");
    ouf.println("  #end");
    ouf.println("}");
  }
  public CMOD2Mesh2(RandomAccessFile Inf) throws IOException {
    byte[] b=new byte[16];
    Inf.read(b);
    header=new String(b);
    int type=swapEndian(Inf.readShort());
    boolean seenMesh=false;
    while(type>0) {
      switch(type) {
        case CMOD_Material_ID:
          if(seenMesh) throw new IllegalArgumentException("All materials must be before any meshes");
          materials.add(new CMOD_Material(Inf));
          break;
        case CMOD_Mesh_ID:
          seenMesh=true;
          meshes.add(new CMOD_Mesh(Inf));
          break;
      }
      try {
        type=swapEndian(Inf.readShort());
      } catch (EOFException E) {
        type=-1;
      }
    }
  }
  public static void main(String args[] ) throws IOException {
    RandomAccessFile Inf=new RandomAccessFile("Data/Mesh/voyager.cmod","r");
    CMOD2Mesh2 Imp=new CMOD2Mesh2(Inf);
    PrintWriter ouf=new PrintWriter(new FileWriter("Data/Mesh/voyager.mesh2"));
    Imp.toString(ouf);
    ouf.close();
  }
}
