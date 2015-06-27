package org.kwansystems.emulator.MiniLogo;

import java.io.*;

import org.kwansystems.automaton.AutomatonException;
import org.kwansystems.automaton.tape.StringTape;

public class PostscriptVM implements VMListener {
  PrintStream ouf;
  boolean openPath=false;
  double Lastx, Lasty;
  public PostscriptVM(PrintStream Louf) throws IOException {
    ouf=Louf;
    ouf.println("%!");
  }
  protected static final String[] ColorList=new String[VM.defaultColorList.length];
  static {
    for(int i=0;i<VM.defaultColorList.length;i++) {
      ColorList[i]=String.format("%f %f %f setrgbcolor", 
        VM.defaultColorList[i].getRed()  ==255?1:((double)VM.defaultColorList[i].getRed())  /256.0,
        VM.defaultColorList[i].getGreen()==255?1:((double)VM.defaultColorList[i].getGreen())/256.0,
        VM.defaultColorList[i].getBlue() ==255?1:((double)VM.defaultColorList[i].getBlue()) /256.0
      );
    }
  }
  
  public void LineTo(double x, double y) {
    openPath=true;
    ouf.printf("%f %f lineto\n",x+8.5/2*72,y+11/2*72);
    Lastx=x;
    Lasty=y;
  }
  public void MoveTo(double x, double y) {
    finishPath();
    ouf.printf("%f %f moveto\n",x+8.5/2*72,y+11/2*72);
  }
  public void SetColor(short C) {
    finishPath();
    ouf.println(ColorList[C]);
  }
  public void finish() {
    finishPath();
    ouf.println("showpage\n\u0004");
  }
  private void finishPath() {
    if(openPath) {
      ouf.println("stroke");
      ouf.printf("%f %f moveto\n",Lastx+8.5/2*72,Lasty+11/2*72);
    }
    openPath=false;
  }
  public static void main(String[] args) throws AutomatonException, IOException {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    ouf.println("Repeat 360 [remember left iter Color Iter draw iter+10 goback]");
    ouf.println("");

    Lex L=new Lex(new StringTape(result.toString()));
    ByteArrayOutputStream BinOuf=new ByteArrayOutputStream();
    Parse P=new Parse(System.out,BinOuf);
    P.compile(L);
    ByteArrayInputStream BinInf=new ByteArrayInputStream(BinOuf.toByteArray());
    VM Ps=new VM(BinInf,new PostscriptVM(new PrintStream("Data/MiniLogo/Logo.ps")));
    Ps.Interpret();
  }

}
