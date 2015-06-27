package org.kwansystems;
/*
 * BitmapToMap.java
 *
 * Created on November 4, 2004, 8:06 PM
 */

/**
 *
 * @author  chrisj
 */
import java.io.*;

public class BitmapToMap {
  
  /** Creates a new instance of BitmapToMap */
  public BitmapToMap() {
    
  }
  public static void main(String args[]) throws Throwable {
    FileInputStream Inf=new FileInputStream("/home/chrisj/Russian Flag.tga");
    PrintWriter Ouf=new PrintWriter("/home/chrisj/Russian Flag.txt");
    int B;
    int Color,Color2;
    for(int i=0;i<27;i++) Inf.read();
    for (int i=0;i<90;i++) {
      if(i<10) Ouf.print("0");
      Ouf.print(i+"    ");
      Color=Inf.read();
      B=1;   
      for (int j=1;j<150;j++) {
        Color2=Inf.read();
        if(Color2!=Color) {
          Ouf.print(B);
          switch(Color) {
            case 0:
              Ouf.print("B ");
              break;
            case 1:
              Ouf.print("r ");
              break;
            case 2:
              Ouf.print("W ");
              break;
            default:
           
          }
          Color=Color2;
          B=1;
        } else {
          B++;
        }
      }
      Ouf.print(B);
      switch(Color) {
        case 0:
          Ouf.print("B ");
          break;
        case 1:
          Ouf.print("r ");
          break;
        case 2:
          Ouf.print("W ");
          break;
        default:
       
      }
      Ouf.println("");
    }
    Ouf.close();
  }
}
