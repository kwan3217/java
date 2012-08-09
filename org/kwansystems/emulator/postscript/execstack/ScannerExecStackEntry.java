package org.kwansystems.emulator.postscript.execstack;

import javax.swing.tree.DefaultMutableTreeNode;
import org.kwansystems.emulator.postscript.*;
import org.kwansystems.automaton.*;

public class ScannerExecStackEntry extends ExecStackEntry {
  private PsKlex scanner;
  protected int deferLevel;

  public ScannerExecStackEntry(PsObject LItem, PsKlex Lscanner) {
    super(LItem);
    scanner=Lscanner;
    deferLevel=0;
  }
  public String toString() {return "ScannerExecStackEntry";}
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode result=super.getNode();
    result.add(new DefaultMutableTreeNode("Defer Level: "+deferLevel));
    return result;
  }
  public void ExecStep(ExecContext EC) {
    //Scan from this string or file
    try {
      PsObject current;
      Token T=scanner.read();
      scanner.Right();
      if(T.type==PsKlexAction.EOF) {
        EC.ExecPop();
        return;
      } else {
        current=PsObject.TokenToObject(T,EC);
      }
      if(T.type==PsKlexAction.RCURLY) {
        EC.ExecPush(new ExecStackEntry(current)); //Execute the rcurly immediately and make the array
        deferLevel--;
      } else if(T.type==PsKlexAction.LCURLY) {
        EC.ExecPush(new ExecStackEntry(current)); //Execute the lcurly immediately to lay down a mark instead of a --{--
        deferLevel++;
      } else if(deferLevel>0) {
        EC.Push(current);
      } else {
        EC.ExecPush(new ExecStackEntry(current));
      }
    } catch(AutomatonException F) {
      throw new PostscriptError("syntaxerror",F.toString());
    }
  }
}
