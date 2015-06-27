package org.kwansystems.emulator.postscript;

import java.util.*;
import java.io.*;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

import javax.swing.*;

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

public class GraphicalPsInterp extends PsInterp implements ExecContextListener, PsStackDisplayListener {
  private PsStackDisplay PsD;

  public static void main(String argv[]) throws Throwable {
    String infn,oufn;
    if(argv.length>=2) {
      infn=argv[0];
      oufn=argv[1];
    } else {
      infn="Data/PsInterp/SpaceMt.ps";
      oufn="Data/PsInterp/out.svg";
    }
    Painter P=new SvgPainter(oufn,4*72,6*72);
    new GraphicalPsInterp(P).Interpret(infn);
  }
  public void Push(PsObject o) {
    PsD.OpPush(o);
  }
  public void Pop() {
    PsD.OpPop();
  }
  
  public void DictPush(PsObject o) {
    PsD.DictPush(o);
  }
  public void DictPop() {
    PsD.DictPop();
  }

  public void ExecPush(ExecStackEntry o) {
    PsD.ExecPush(o);
  }
  public void ExecPop() {
    PsD.ExecPop();
  }
  public void ExecChanged() {
    RedoExecStack();
  }

  public GraphicalPsInterp(Painter GL) {
    execContext=new ExecContext(GL,this);
    PsD=new PsStackDisplay(this);
    PsD.show();
    HeapChanged();
  }
  
  /* 
  In theory, since a file or string on the exec stack is scanned,
  you could put %stdin on the bottom of the exec stack. This way
  there is no difference between the input stream and the execution
  stack flow. So the exec loop goes like this (Simplicity itself!)
  
  While there is stuff on the execution stack
    Execute one step of the top entry of the exec stack
  End 
  */
  public void Interpret(String Infn) throws IOException {
    PsObject stdin=new PsObject(filetype,Executable,Unlimited,new FileReader(Infn));
    ExecStackEntry StackStdin=ExecStackEntry.makeExecStackEntry(stdin);
    execContext.ExecPush(StackStdin);
  }

  public void Step() {
    if(execContext.Done()) {
      JOptionPane.showMessageDialog(new JFrame("DialogDemo"),"Nothing left to execute!");
      PsD.pause();
    }
    try {
      execContext.Execute();
    } catch (PostscriptError E) {
      JOptionPane.showMessageDialog(new JFrame("A Postscript Error has occurred"),E.toString());
      PsD.pause();
    }
  }
  
  public void pause() {
    PsD.pause();
  }
  
  public void RedoOpStack() {
    PsD.OpStackClear();
    for(Iterator I=execContext.OpStackIterator();I.hasNext();) {
      PsObject O=(PsObject)I.next();
      PsD.OpPush(O);
    }
  }

  public void RedoDictStack() {
    PsD.DictStackClear();
    for(Iterator I=execContext.DictStackIterator();I.hasNext();) {
      PsObject D=(PsObject)I.next();
      PsD.DictPush(D);
    }
  }

  public void RedoExecStack() {
    PsD.ExecStackClear();
    for(Iterator I=execContext.ExecStackIterator();I.hasNext();) {
      ExecStackEntry E=(ExecStackEntry)I.next();
      PsD.ExecPush(E);
    }
  }

  public void HeapChanged() {
    RedoOpStack();
    RedoDictStack();
    RedoExecStack();
  }
}

