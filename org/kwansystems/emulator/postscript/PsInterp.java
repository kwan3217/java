package org.kwansystems.emulator.postscript;

import java.io.*;
import java.util.*;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

import org.kwansystems.emulator.postscript.execstack.*;
import org.kwansystems.emulator.postscript.painter.*;

/* Memory Areas
 
  The PostScript heap is the subset of the Java heap which holds PostscriptObjects. 
  A PostScript pointer IS a Java refeference. This way the PostScript and Java can
  share garbage collectors
 
  The dup operator duplicates the reference to a Postscript composite object, but
  duplicates the value of a simple object. Since all PostscriptObjects are composite
  to Java, special handling will be needed for this. 
 
 */

public class PsInterp {
  protected ExecContext execContext;
  protected Date start;
  protected int Steps;
  public static void main(String argv[]) throws Throwable {
    String infn,oufn;
    if(argv.length>=2) {
      infn=argv[0];
      oufn=argv[1];
    } else {
      infn="Data/PsInterp/SpaceMt.ps";
      oufn="Data/PsInterp/out.svg";
    }
    new PsInterp(new SvgPainter(oufn,4*72,6*72),new NullExecContextListener()).Interpret(infn);
  }
  public PsInterp() {}
  public PsInterp(Painter GL, ExecContextListener ECL) {
    execContext=new ExecContext(GL,ECL);
  }
  public void InterpretStart(String Infn) throws IOException {
    start=new Date();
    Steps=0;
    PsObject stdin=new PsObject(filetype,Executable,Unlimited,new FileReader(Infn));
    ExecStackEntry StackStdin=ExecStackEntry.makeExecStackEntry(stdin);
    execContext.ExecPush(StackStdin);
  }
  public void InterpretStep() throws IOException,PostscriptError {
    execContext.Execute();
    Steps++;
  }
  public boolean isInterpretDone() {
    return execContext.Done();
  }
  public void Interpret(String Infn) throws IOException {
    InterpretStart(Infn);
    while(!execContext.Done()) {
      try {
        InterpretStep();
      } catch (PostscriptError E) {
        System.err.println(E.toString());
      }
    }
    execContext.painter.done(execContext.currentGState);
    System.out.println("Took "+Steps+" steps in "+(new Date().getTime()-start.getTime())+" ms.");
  }
}

