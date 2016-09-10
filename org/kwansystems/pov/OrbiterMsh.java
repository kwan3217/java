package org.kwansystems.pov;

import java.io.*;

import org.kwansystems.image.dxt.DirectDrawSurface;
import org.kwansystems.tools.vector.*;

public class OrbiterMsh {
  protected MathVector[][] Vectors;
  protected int[][][] Triangles;
  protected int[] MaterialIndexes;
  protected int[] TextureIndexes;
  protected String Name;
  private void readGroups(LineNumberReader Inf) throws IOException {
    String line=Inf.readLine();
    String[] parts=line.split("\\s+");  
    if(!parts[0].equalsIgnoreCase("GROUPS")) throw new IOException("Format error");
    int groups=Integer.parseInt(parts[1]);
    Vectors=new MathVector[groups][];
    Triangles=new int[groups][][];
    MaterialIndexes=new int[groups];
    TextureIndexes=new int[groups];
    int material=0;
    int texture=0;
    System.out.print("Reading groups");
    for(int group=0;group<groups;group++) {
      if(group>0) material=MaterialIndexes[group-1];
      boolean done=false;
      int vectors=0;
      int triangles=0;
      while(!done) {
        line=Inf.readLine();
        parts=line.split("\\s+");
        if(parts[0].equalsIgnoreCase("MATERIAL")) {
          material=Integer.parseInt(parts[1]);
        } else if(parts[0].equalsIgnoreCase("TEXTURE")) {
          texture=Integer.parseInt(parts[1]);
        } else if(parts[0].equalsIgnoreCase("GEOM")) {
          vectors=Integer.parseInt(parts[1]);
          triangles=Integer.parseInt(parts[2]);
          done=true;
        }
      }
      MaterialIndexes[group]=material;
      TextureIndexes[group]=texture;
      Vectors[group]=new MathVector[vectors];
      Triangles[group]=new int[3][triangles];
      for(int vector=0;vector<vectors;vector++) {
      line=Inf.readLine();
      parts=line.split("\\s");
      double[] Components=new double[parts.length];
      for(int component=0;component<parts.length;component++) Components[component]=Double.parseDouble(parts[component]);
        Vectors[group][vector]=new MathVector(Components);
      }
      for(int triangle=0;triangle<triangles;triangle++) {
        line=Inf.readLine();
        parts=line.split("\\s");
        for(int component=0;component<parts.length;component++) Triangles[group][component][triangle]=Integer.parseInt(parts[component]);
      }
      System.out.print(".");
    }
    System.out.println();
    
  }
  protected double[][][] Materials;
  protected String[] MaterialNames;
  private void readMaterials(LineNumberReader Inf) throws IOException {
    System.out.print("Reading materials");    
    String line=Inf.readLine();
    String[] parts=line.split("\\s+"); 
    if(!parts[0].equalsIgnoreCase("MATERIALS")) throw new IOException("Format error");
    int materials=Integer.parseInt(parts[1]);
    Materials=new double[materials+1][4][5];
    MaterialNames=new String[materials+1];
    for(int material=1;material<=materials;material++) {
      line=Inf.readLine();
      MaterialNames[material]=line;
    }
    for(int material=1;material<=materials;material++) {
      line=Inf.readLine();
      parts=line.split("\\s+",2);
      if(!parts[0].equalsIgnoreCase("MATERIAL")) throw new IOException("Format error");
      for(int row=0;row<4;row++) {
        line=Inf.readLine();
        parts=line.split("\\s+");
        for(int component=0;component<parts.length;component++) Materials[material][row][component]=Double.parseDouble(parts[component]);
      }
      System.out.print(".");
    }
    System.out.println();
    
  }
  protected String[] TextureNames;
  public void readTextures(LineNumberReader Inf) throws IOException {
    System.out.print("Reading textures");    
    String line=Inf.readLine();
    String[] parts=line.split("\\s+"); 
    if(!parts[0].equalsIgnoreCase("TEXTURES")) throw new IOException("Format error");
    int textures=Integer.parseInt(parts[1]);
    TextureNames=new String[textures+1];
    for(int texture=1;texture<=textures;texture++) {
      line=Inf.readLine();
      TextureNames[texture]=line;
      if(TextureNames[texture].charAt(TextureNames[texture].length()-1)=='D') TextureNames[texture]=TextureNames[texture].substring(0, TextureNames[texture].length()-2);
      System.out.print(".");
    }
    System.out.println();
    System.out.println("Converting textures...");    
    for(int texture=1;texture<=textures;texture++) {
      String NewName=TextureNames[texture]+".png";
      try {      
        DirectDrawSurface DDS=new DirectDrawSurface(new FileInputStream("C:\\Users\\jeppesen\\Documents\\LocalApps\\Orbiter10-P1\\Textures2\\"+TextureNames[texture]));
        System.out.print(String.format("%s",TextureNames[texture]/*,DDS.ddsd.dwWidth,DDS.ddsd.dwHeight*/));
        DDS.toPng("c:\\Users\\jeppesen\\desktop\\STSws\\"+NewName);
        TextureNames[texture]=NewName;
       
        System.out.println(" done");
      } catch (Throwable E){E.printStackTrace();}
    }
  }
  protected String Header;
  public OrbiterMsh(String LName,LineNumberReader Inf) throws IOException {
    Name=LName;
    String line=Inf.readLine();
    Header=line;
    if(!Header.equalsIgnoreCase("MSHX1")) throw new IOException("Unrecognized file type");
    readGroups(Inf);
    readMaterials(Inf);
    readTextures(Inf);
  }
  protected void writeGroups(PrintWriter Ouf) {
    int groups=Vectors.length;
    Ouf.println(String.format("#local "+Name+"_Group=array[%d] {",groups));
    for(int group=0;group<groups;group++) {
      Ouf.printf("  mesh2 { //Group %d\n",group);
      Ouf.println(String.format("    vertex_vectors { \n      %d",Vectors[group].length));
      for(int vector=0;vector<Vectors[group].length;vector++) {
        Ouf.print(String.format("      <%s>",Vectors[group][vector].subVector(0,3).toString()));
        if(vector<Vectors[group].length-1) Ouf.print(",");
        Ouf.println();
      }
      Ouf.println(String.format("    }"));
      Ouf.println(String.format("    normal_vectors { \n      %d",Vectors[group].length));
      for(int vector=0;vector<Vectors[group].length;vector++) {
      Ouf.print(String.format("      <%s>",Vectors[group][vector].subVector(3,3).toString()));
      if(vector<Vectors[group].length-1) Ouf.print(",");
        Ouf.println();
      }
      Ouf.println(String.format("    }"));
      if(Vectors[group][0].dimension()==8) {
        Ouf.println(String.format("    uv_vectors {   \n      %d",Vectors[group].length));
        for(int vector=0;vector<Vectors[group].length;vector++) {
      
          //Vectors[group][vector].set(6,1-Vectors[group][vector].get(6));        
          Vectors[group][vector]=Vectors[group][vector].replace(7,1-Vectors[group][vector].get(7));       
        Ouf.print(String.format("      <%s>",Vectors[group][vector].subVector(6,2).toString()));
        if(vector<Vectors[group].length-1) Ouf.print(",");
          Ouf.println();
        }
        Ouf.println(String.format("    }"));
      }
      Ouf.println(String.format("    face_indices {   \n      %d",Triangles[group][0].length));
      for(int vector=0;vector<Triangles[group][0].length;vector++) {
      Ouf.print(String.format("      <%d,%d,%d>",Triangles[group][0][vector],Triangles[group][1][vector],Triangles[group][2][vector]));
      if(vector<Vectors[group].length-1) Ouf.print(",");
        Ouf.println();
      }
      Ouf.println(String.format("    }"));
      Ouf.println(String.format("  },"));
    }
    Ouf.println(String.format("}"));
    Ouf.printf("#declare "+Name+"_GroupMaterial=array[%d] {\n",groups);
    for(int i=0;i<groups;i++) {
      Ouf.println(String.format("  %d,",MaterialIndexes[i]));
    }
    Ouf.println("}");
    Ouf.println(String.format("#local "+Name+"_GroupTexture=array[%d] {",groups));
    for(int i=0;i<groups;i++) {
      Ouf.println(String.format("  %d,",TextureIndexes[i]));
    }
    Ouf.println("}");
  }
  protected void writeTextures(PrintWriter Ouf) {
    int textures=TextureNames.length-1;
    Ouf.printf("#declare "+Name+"_Texture=array[%d] {\n",textures+1);
    Ouf.println("  \"\",//Ignored");
    for(int i=1;i<textures+1;i++) {
      Ouf.println("  \""+TextureNames[i]+"\",");
    }
    Ouf.println("}");
  }
  protected void writeMaterials(PrintWriter Ouf) {
    int materials=Materials.length-1;
    Ouf.printf("#declare %s_Finish=array[%d] {\n",Name,materials+1);
    Ouf.printf("  <0,0,0>,//Ignored\n");
    for(int i=1;i<materials+1;i++) {
      Ouf.printf("  <%f,%f,%f>,\n",Materials[i][1][0],Materials[i][1][1],Materials[i][1][2]);
    }
    Ouf.println(String.format("}"));
    Ouf.printf("#declare %s_Pigment=array[%d] {\n",Name,materials+1);
    Ouf.printf("  <0,0,0>,//Ignored\n");
    for(int i=1;i<materials+1;i++) {
      Ouf.printf("  <%f,%f,%f>\n",Materials[i][0][0],Materials[i][0][1],Materials[i][0][2]);
    }
    Ouf.println("}");
  }
  public void writePov(boolean fullPov, PrintWriter Ouf) {
    Ouf.println("#ifndef(MeshAmbient)#declare MeshAmbient=0.1;#end");
    writeGroups(Ouf);
    writeTextures(Ouf);
    writeMaterials(Ouf);
    boolean Animate=false;
    
    Ouf.printf("#macro OrbiterMesh(Group,GroupMaterial,GroupTexture,Texture,Finish,Pigment");
    if(Animate) Ouf.printf(",AnimControl,AnimGroup,AnimParent,AnimType,AnimVec");
    Ouf.printf("  )\n");
    Ouf.printf("  #local I=0;\n");
    Ouf.printf("  union {\n");
    Ouf.printf("    #while(I<dimension_size(Group,1))\n");
    Ouf.printf("      object {\n");
    Ouf.printf("        Group[I]\n");
    Ouf.printf("        texture {\n");
    Ouf.printf("          uv_mapping\n");
    Ouf.printf("          finish {ambient MeshAmbient*Finish[GroupMaterial[I]]}\n");
    Ouf.printf("          #if(GroupTexture[I]>0)\n");
    Ouf.printf("            pigment {image_map {png Texture[GroupTexture[I]]}}\n");
    Ouf.printf("          #else\n");
    Ouf.printf("            pigment {color rgb Pigment[GroupMaterial[I]]}\n");
    Ouf.printf("          #end\n");
    Ouf.printf("        }\n");
    if(Animate) {
      Ouf.printf("        #if(AnimGroup[I]>=0)\n");
      Ouf.printf("          Animate(AnimGroup[I],AnimControl,AnimParent,AnimType,AnimVec)\n");
      Ouf.printf("        #end\n");
    }
    Ouf.printf("      }\n");
    Ouf.printf("      #local I=I+1;\n");
    Ouf.printf("    #end\n");    
    Ouf.printf("    rotate y*90\n");
    Ouf.printf("  }\n");
    Ouf.printf("#end\n");
    
    if(Animate) {
      Ouf.printf("#include \"transforms.inc\"\n");
      Ouf.printf("\n");
      Ouf.printf("#macro Animate(AnimGroup,AnimControl,AnimParent,AnimType,AnimVec)\n");
      Ouf.printf("  #switch(AnimType[AnimGroup])\n");
      Ouf.printf("    #case(0) //Translate\n");
      Ouf.printf("      translate AnimVec[AnimGroup][0]*AnimControl[AnimGroup]\n");
      Ouf.printf("      #break\n");
      Ouf.printf("    #case(1) //Scale\n");
      Ouf.printf("      translate -AnimVec[AnimGroup][0]\n");
      Ouf.printf("      scale AnimVec[AnimGroup][1]*AnimControl[AnimGroup]\n");
      Ouf.printf("      translate AnimVec[AnimGroup][0]\n");
      Ouf.printf("      #break\n");
      Ouf.printf("    #case(2) //Rotate\n");
      Ouf.printf("      translate -AnimVec[AnimGroup][0]\n");
      Ouf.printf("      Axis_Rotate_Trans(AnimVec[AnimGroup][1], AnimVec[AnimGroup][2].x*AnimControl[AnimGroup])\n");
      Ouf.printf("      translate AnimVec[AnimGroup][0]\n");
      Ouf.printf("      #break\n");
      Ouf.printf("  #end\n");
      Ouf.printf("  #if(AnimParent[AnimGroup]>=0)\n");                     
      Ouf.printf("    Animate(AnimParent[AnimGroup],AnimControl,AnimParent,AnimType,AnimVec)\n");
      Ouf.printf("  #end\n");
      Ouf.printf("#end\n");
      Ouf.printf("    \n");
      Ouf.printf("#macro AddTrans(A,V,Parent,Groups,AnimGroup,AnimParent,AnimType,AnimVec)\n");
      Ouf.printf("  #local I=0;\n");
      Ouf.printf("  #while(I<dimension_size(Groups,1))\n");
      Ouf.printf("    #declare AnimGroup[Groups[I]]=A;\n");
      Ouf.printf("    #local I=I+1;\n");
      Ouf.printf("  #end\n");
      Ouf.printf("  #declare AnimParent[A]=Parent;\n");
      Ouf.printf("  #declare AnimType[A]=0;\n");
      Ouf.printf("  #declare AnimVec[A][0]=<V.x,-V.z,V.y>;\n");
      Ouf.printf("#end\n");
      Ouf.printf("\n");
      Ouf.printf("#macro AddScale(A,Vc,Vs,Parent,Groups,AnimGroup,AnimParent,AnimType,AnimVec)\n");
      Ouf.printf("  #local I=0;\n");
      Ouf.printf("  #while(I<dimension_size(Groups,1))\n");
      Ouf.printf("    #declare AnimGroup[Groups[I]]=A;\n");
      Ouf.printf("    #local I=I+1;\n");
      Ouf.printf("  #end\n");
      Ouf.printf("  #declare AnimParent[A]=Parent;\n");
      Ouf.printf("  #declare AnimType[A]=1;\n");
      Ouf.printf("  #declare AnimVec[A][0]=<Vc.x,-Vc.z,Vc.y>;\n");
      Ouf.printf("  #declare AnimVec[A][1]=<Vs.x,-Vs.z,Vs.y>;\n");
      Ouf.printf("#end\n");
      Ouf.printf("\n");
      Ouf.printf("#macro AddRot(A,Vc,Vs,Amt,Parent,Groups,AnimGroup,AnimParent,AnimType,AnimVec)\n");
      Ouf.printf("  #local I=0;\n");
      Ouf.printf("  #while(I<dimension_size(Groups,1))\n");
      Ouf.printf("    #declare AnimGroup[Groups[I]]=A;\n");
      Ouf.printf("    #local I=I+1;\n");
      Ouf.printf("  #end\n");
      Ouf.printf("  #declare AnimParent[A]=Parent;\n");
      Ouf.printf("  #declare AnimType[A]=2;\n");
      Ouf.printf("  #declare AnimVec[A][0]=<Vc.x,-Vc.z,Vc.y>;\n");
      Ouf.printf("  #declare AnimVec[A][1]=<Vs.x,-Vs.z,Vs.y>;\n");
      Ouf.printf("  #declare AnimVec[A][2]=<Amt,Amt,Amt>;\n");
      Ouf.printf("#end\n");
    }


    Ouf.printf("#macro %s(",Name);
    if(Animate) Ouf.printf("AnimControl,AnimGroup,AnimParent,AnimType,AnimVec");
    Ouf.printf(")\n");
    Ouf.printf("  OrbiterMesh(\n");
    Ouf.printf("    %s_Group,%s_GroupMaterial,%s_GroupTexture,%s_Texture,%s_Finish,%s_Pigment",Name,Name,Name,Name,Name,Name);
    if(Animate) Ouf.printf(",\n    AnimControl,AnimGroup,AnimParent,AnimType,AnimVec");
    Ouf.printf("\n  )\n");
    Ouf.println("#end\n");
    if(Animate) {
      Ouf.printf("#declare %s_AnimGroup=array[dimension_size(%s_Group,1)]\n",Name,Name);
      Ouf.printf("#local %s_N_Anim=%d;\n",Name,1);
      Ouf.printf("#declare %s_AnimParent=array[%s_N_Anim]\n",Name,Name);
      Ouf.printf("#declare %s_AnimType=array[%s_N_Anim]\n",Name,Name);
      Ouf.printf("#declare %s_AnimVec=array[%s_N_Anim][3]\n",Name,Name);
      Ouf.printf("\n");
      Ouf.printf("#local I=0;\n");
      Ouf.printf("#while(I<dimension_size(%s_AnimGroup,1))\n",Name);
      Ouf.printf("  #declare %s_AnimGroup[I]=-1;\n",Name);
      Ouf.printf("  #local I=I+1;\n");
      Ouf.printf("#end\n");                     
      Ouf.printf("\n");
      Ouf.printf("#local I=0;\n");
      Ouf.printf("#while(I<%s_N_Anim)\n",Name);
      Ouf.printf("  #declare %s_AnimParent[I]=-1;\n",Name);
      Ouf.printf("  #declare %s_AnimType[I]=-1;\n",Name);
      Ouf.printf("  #declare %s_AnimVec[I][0]=<0,0,0>;\n",Name);
      Ouf.printf("  #declare %s_AnimVec[I][1]=<0,0,0>;\n",Name);
      Ouf.printf("  #declare %s_AnimVec[I][2]=<0,0,0>;\n",Name);
      Ouf.printf("  #local I=I+1;\n");
      Ouf.printf("#end\n");                      
    }
    Ouf.printf("%s(",Name);
    if(Animate) {
     Ouf.printf("array[1] {clock},%s_AnimGroup,%s_AnimParent,%s_AnimType,%s_AnimVec",Name,Name,Name,Name); 
    }
    Ouf.printf(")\n");
    Ouf.println("light_source {");
    Ouf.println("  <20,20,-20>*1000");
    Ouf.println("  color 1");
    Ouf.println("}");
    Ouf.println("camera {");
    Ouf.println("  location <0,2,-5>*0.5");
    Ouf.println("  look_at <0,0,0>");
    Ouf.println("}");
    Ouf.close();
    
  }
  public static void main(String args[]) throws IOException {
    String[] Meshes=new String[] {"STSws"};
    for(int i=0;i<Meshes.length;i++) {
      OrbiterMsh O=new OrbiterMsh(Meshes[i],new LineNumberReader(new FileReader("C:\\Users\\jeppesen\\Documents\\LocalApps\\Orbiter10-P1\\Meshes\\"+Meshes[i]+".msh")));
      O.writePov(true,new PrintWriter(new FileWriter("C:\\Users\\jeppesen\\Desktop\\STSws\\"+Meshes[i]+".msh.pov")));
    }
  }
}
