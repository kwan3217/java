package org.kwansystems.pov; 

import static java.lang.Math.*;
import static org.kwansystems.space.planet.Spheroid.*;
import static org.kwansystems.tools.vector.MathVector.*;

import org.kwansystems.tools.vector.*;

import java.text.*;


public class BlueMarble {
  public static boolean TileOK(int Shell,int LatRow,int LonRow,MathVector Loc,MathVector Look,double Angle,double Pix,double Re) {
    if(InTile(Shell,LatRow,LonRow,Loc,Re)) return true;
    return (
      InHorizon(Shell,LatRow,LonRow,Loc,Re)
      &&
//      InFov(Shell,LatRow,LonRow,Loc,Look,Angle,Re)
//      &&
      ResOK(Shell,LatRow,LonRow,Loc,Angle,Pix,Re)
    );
  }

  public static int NumLatRows(int Shell) { 
    return 6*(int)pow(2,Shell-1);
  }
  public static void PrintNumber(String Tag, double Num) {
    NonPov(Tag+Num);
  }
  public static void PrintVector(String Tag, MathVector Num) {
    NonPov(Tag+Num);
  }
  public static String PovPrefix="";
  public static void Pov(String S) {
    System.out.println(PovPrefix+S);
  }
  public static void NonPov(String S) {
    System.err.println(S);
  }
  public static double CenterLat(int Shell, double LatRow) {
    double Limit=NumLatRows(Shell);
    double TileSize=180.0/(double)Limit;
    return 90-(LatRow+0.5)*TileSize;
  }

  public static double CenterLon(int Shell, double LonRow) {
    double Limit=2*NumLatRows(Shell);
    double TileSize=360.0/Limit;
    return ((LonRow+0.5)*TileSize)-180.0;
  }
  
  public static MathVector LLtoXYZ(double Lat, double Lon, double Re) {
    double CLat=cos(toRadians(Lat));
    double CLon=cos(toRadians(Lon));
    double SLat=sin(toRadians(Lat));
    double SLon=sin(toRadians(Lon));
    return new MathVector(CLat*CLon*Re,SLat*Re,CLat*SLon*Re);
  }

  public static double XYZtoLat(MathVector XYZ) {
    return (90-toDegrees(vangle(new MathVector(0,1,0),XYZ)));
  }

  public static double XYZtoLon(MathVector XYZ) {
    return (toDegrees(atan2(XYZ.Z(),XYZ.X())));
  }

  public static MathVector Center(int Shell,double LatRow,double LonRow,double Re) {
    return LLtoXYZ(CenterLat(Shell,LatRow),CenterLon(Shell,LonRow),Re);
  }

  public static MathVector[] NearFar(int Shell,int LatRow,int LonRow,MathVector Loc,double Re) {
    MathVector Near, Far;
    MathVector[] Corner=new MathVector[4];
    Corner[0]=Center(Shell,LatRow-0.5,LonRow-0.5,Re);
    Corner[1]=Center(Shell,LatRow-0.5,LonRow+0.5,Re);
    Corner[2]=Center(Shell,LatRow+0.5,LonRow-0.5,Re);
    Corner[3]=Center(Shell,LatRow+0.5,LonRow+0.5,Re);
    Near=Corner[0];
    double NearDist=MathVector.sub(Corner[0],Loc).length();
    Far=Near;
    double FarDist=NearDist;
    for(int I=1;I<4;I++) {
      double Dist=MathVector.sub(Corner[I],Loc).length();
      if(Dist<NearDist) {
        Near=Corner[I];
        NearDist=Dist;
      }
      if(Dist>FarDist) {
        Far=Corner[I];
        FarDist=Dist;
      }
    }
    return new MathVector[] {Near,Far};
  }

  public static boolean InHorizon(int Shell, int LatRow, int LonRow, MathVector Loc,double Re) {
    boolean verbose=false;
    if(verbose) {PrintNumber("LatRow: ",LatRow); }
    if(verbose) {PrintNumber("LonRow: ",LonRow); }
    
    //Calcualte the location of the nearest corner of the tile
    MathVector NearXYZ;
    MathVector[] NF=NearFar(Shell,LatRow,LonRow,Loc,Re);
    NearXYZ=NF[0];
    if(verbose) {PrintVector("Loc:  ",Loc); }
    if(verbose) {PrintVector("Near: ",NearXYZ); }
    
    //Calculate the vector pointing from the near corner to the zenith
    MathVector Vec1=NearXYZ.normal();
    
    //Calculate the vector pointing from the near corner to the camera
    MathVector Vec2=sub(Loc,NearXYZ).normal();
    
    if(verbose) {PrintVector("Vec1: ",Vec1); }
    if(verbose) {PrintVector("Vec2: ",Vec2); }
    
    //If the dot product is greater than one, the angle is less than 90deg
    //and the camera is above the horizon at the near corner
    if(verbose) {PrintNumber("Dot:  ",MathVector.dot(Vec1,Vec2)); }
    boolean result=MathVector.dot(Vec1,Vec2)>0;
    if(verbose) {NonPov(result?"Using tile":"Not using tile");}
    return result;
}

  public static boolean InFov(int Shell,int LatRow,int LonRow, MathVector Loc,MathVector Look,double Angle,double Re) {
    double Limit=cos(Angle*sqrt(2));
    MathVector NearXYZ;
    MathVector[] NF=NearFar(Shell,LatRow,LonRow,Loc,Re);
    NearXYZ=NF[0];
    MathVector Vec1=sub(Loc,NearXYZ).normal();
    MathVector Vec2=sub(Loc,Look).normal();
    return (MathVector.dot(Vec1,Vec2)>Limit);
  }

  public static boolean InTile(int Shell,int LatRow,int LonRow,MathVector Loc,double Re) {
    double TLat=CenterLat(Shell,LatRow);
    double TLon=CenterLat(Shell,LonRow);
    double LLat=XYZtoLat(Loc);
    double LLon=XYZtoLon(Loc);
    int Limit=NumLatRows(Shell);
    double TileSize=180.0/Limit;
    return ((abs(TLat-LLat)<(TileSize*0.5001))&&(abs(TLon-LLon)<(TileSize*0.5001)));
  }

  public static boolean ResOK(int Shell,int LatRow,int LonRow,MathVector Loc,double Angle,double Pix,double Re) {
    double Ratio=0.25;
    MathVector NearXYZ;
    //MathVector FarXYZ;
    MathVector[] NF=NearFar(Shell,LatRow,LonRow,Loc,Re);
    NearXYZ=NF[0];
    //FarXYZ=NF[1];
    double PixSubtend=tan(Angle)*2/Pix;
    double Limit=NumLatRows(Shell);
    double MapPixSize=Re*PI/(Limit*270);
    double NearDist=sub(Loc,NearXYZ).length();
    double MapSubtend=MapPixSize/NearDist;
    return ((MapSubtend/PixSubtend)>Ratio);
  }

  public static int MakeShellTable(int Shell,double Re,MathVector CameraLoc,MathVector CameraLook,double Angle,double Pix,boolean[][] Table) {
    int UsedTiles=0;
    if(Shell>0) {
      double Ratio=0.25;
      double Limit=NumLatRows(Shell);
      double PixSubtend=tan(toRadians(Angle*2/Pix));
      double MapPixSize=Re*PI/(Limit*270);
      double NearDist=CameraLoc.length()-Re;
      double MapSubtend=MapPixSize/NearDist;
      NonPov("Thinking about doing shell "+Shell);
      PrintNumber("PixSubtend (rad): ",PixSubtend);
      PrintNumber("MapPixSize (km):  ",MapPixSize);
      PrintNumber("Dist (km):        ",NearDist);
      PrintNumber("MapSubtend (rad): ",MapSubtend);
      PrintNumber("Ratio:            ",MapSubtend/PixSubtend);
      PrintNumber("Critical Ratio:   ",Ratio);
      if((MapSubtend/PixSubtend)>Ratio) {
        NonPov("Going to do shell "+Shell);
        for(int LonTile=0;LonTile<Limit*2;LonTile++) {
          for(int LatTile=0;LatTile<Limit;LatTile++) {
            if(TileOK(Shell,LatTile,LonTile,CameraLoc,CameraLook,Angle,Pix,Re)) {
              Table[LonTile][LatTile]=true;
              UsedTiles++;
            } else {
              Table[LonTile][LatTile]=false;
            }
          }
        }
        int NumTiles=2*(int)Math.pow(NumLatRows(Shell),2);
        DecimalFormat TD=new DecimalFormat("0.00");
        NonPov("Used "+UsedTiles+" of "+NumTiles+" ("+TD.format((double)UsedTiles/(double)NumTiles*100)+"%)");
      } else {
        NonPov("Not Doing shell "+Shell);
      }
    } else {
      UsedTiles=1;
    }
    return UsedTiles;
  }

  public static void UseShellTable(int Shell,String MapName,boolean[][] Table,int UsedTiles) {
    if(Shell>0) {
      DecimalFormat TD=new DecimalFormat("000");
      int Limit=NumLatRows(Shell);
      Pov("pigment{");
      Pov("  image_map{");
      Pov("    tile_map{");
      Pov("      "+Limit*2+","+Limit);
      for(int LonTile=0;LonTile<Limit*2;LonTile++) {
        for(int LatTile=0;LatTile<Limit;LatTile++) {
          if(Table[LonTile][LatTile]) {
            String Filename;
            if(UseTest) {
              Filename=""+Shell+"test.png";
            } else {
              Filename="Earth"+MapName+"."+Date+"/"+Shell+"/"+TD.format(LatTile)+"x"+TD.format(LonTile)+".png";
            }
            Pov("      ["+LonTile+" "+LatTile+" png \""+Filename+"\"]");
          }
        }
      }
      Pov("    }");
      if(AEarth==BEarth) {
        Pov("    map_type spherical");
      } else {
        Pov("    map_type spheroid");
        Pov("    flatness 1/"+1/(1-BEarth/AEarth));
      }
      Pov("  }");
      Pov("  rotate y*180");
      Pov("}");
    } else {
      Pov("pigment {");
      Pov("  image_map {");
      Pov("    png \"Earth"+MapName+"."+Date+"/Earth"+MapName+"0.png\"");
      if(AEarth==BEarth) { 
        Pov("    map_type spherical");
      } else {
        Pov("    map_type spheroid");
        Pov("    flatness 1/"+1/(1-BEarth/AEarth));
      }
      Pov("  }");
      Pov("  rotate y*180");
      Pov("}");
    }
  }

  public static void CalcEarthMod(MathVector LCamLoc,MathVector Look,double Angle,double Pix) {
    int UsedTiles[]=new int[7];
    for(int Shell=0;Shell<=6;Shell++) {
      if(UseShell[Shell]) {
        boolean[][] Table=new boolean[NumLatRows(Shell)*2][NumLatRows(Shell)];
        UsedTiles[Shell]=MakeShellTable(Shell,AEarth,LCamLoc,Look,Angle,Pix,Table);
        if(UsedTiles[Shell]>0) {
          NonPov("Doing shell "+Shell);
          Pov("#declare Shell"+Shell+"=texture {");
          PovPrefix="  ";
          UseShellTable(Shell,"Map",Table,UsedTiles[Shell]);
          PovPrefix="";
          Pov("  finish { brilliance 0 ambient 0.5}");
          Pov("}");
        }
      }
    }
  }

  public static double AEarth=WGS84.Re;
  public static double BEarth=WGS84.Rp;
  public static String Date="200404";
  public static boolean[] UseShell=new boolean[] {true,true,true,true,true,true,true};
  public static boolean UseTest=false;
  
  public static void main(String[] args) {
    MathVector LCamLoc,Look;
    double Angle,Pix;
    if(args.length>0) {
      LCamLoc=new MathVector(Double.parseDouble(args[0]),Double.parseDouble(args[1]),Double.parseDouble(args[2]));
      Look=new MathVector(Double.parseDouble(args[3]),Double.parseDouble(args[4]),Double.parseDouble(args[5]));
      Angle=Double.parseDouble(args[6]);
      Pix=Double.parseDouble(args[7]);
      UseTest=Integer.parseInt(args[8])==1;
      UseShell[0]=Integer.parseInt(args[9])==1;
      UseShell[1]=Integer.parseInt(args[10])==1;
      UseShell[2]=Integer.parseInt(args[11])==1;
      UseShell[3]=Integer.parseInt(args[12])==1;
      UseShell[4]=Integer.parseInt(args[13])==1;
      UseShell[5]=Integer.parseInt(args[14])==1;
      UseShell[6]=Integer.parseInt(args[15])==1;
      AEarth=Double.parseDouble(args[16]);
      BEarth=Double.parseDouble(args[17]);
      Date=args[18];
    } else {
      LCamLoc=new MathVector(6500000,0,0);
      Look=new MathVector(6499000,0,0);
      Angle=44;
      Pix=768;
    }
    PrintVector("LCamLoc:   ",LCamLoc);
    PrintVector("Look:      ",Look);
    PrintNumber("Angle:     ",Angle);
    PrintNumber("Pix:       ",Pix);
    NonPov(     "UseTest:   "+(UseTest?"true":"false"));
    NonPov(     "UseShell0: "+(UseShell[0]?"true":"false"));
    NonPov(     "UseShell1: "+(UseShell[1]?"true":"false"));
    NonPov(     "UseShell2: "+(UseShell[2]?"true":"false"));
    NonPov(     "UseShell3: "+(UseShell[3]?"true":"false"));
    NonPov(     "UseShell4: "+(UseShell[4]?"true":"false"));
    NonPov(     "UseShell5: "+(UseShell[5]?"true":"false"));
    NonPov(     "UseShell6: "+(UseShell[6]?"true":"false"));
    PrintNumber("AEarth:    ",AEarth);
    PrintNumber("BEarth:    ",BEarth);
    CalcEarthMod(LCamLoc,Look,Angle,Pix);
  }
}
