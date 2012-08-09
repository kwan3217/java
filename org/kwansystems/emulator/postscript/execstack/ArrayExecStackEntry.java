package org.kwansystems.emulator.postscript.execstack;

import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.kwansystems.emulator.postscript.ExecContext;
import org.kwansystems.emulator.postscript.Operator;
import org.kwansystems.emulator.postscript.PsArray;
import org.kwansystems.emulator.postscript.PsObject;
import org.kwansystems.emulator.postscript.PsString;

public class ArrayExecStackEntry extends ExecStackEntry {
  protected int deferLevel;
  protected int Position;

  public ArrayExecStackEntry(PsObject LItem) {
    super(LItem);
  }
  public String toString() {return "ArrayExecStackEntry: "+Item.toString();}
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode result=super.getNode();
    result.add(new DefaultMutableTreeNode("Position: "+Position));
    result.add(new DefaultMutableTreeNode("Defer Level: "+deferLevel));
    return result;
  }
  public void ExecStep(ExecContext EC) {
    //This is where things start to get fun... Procedures
    PsArray Procedure=(PsArray)Item.get();
    PsObject current=Procedure.get(Position);
    Position++;

    //This finishes a procedure. Supposedly it allows unlimited tail recursion
    //if it is done this way. It's also elegant.
    if(Position==Procedure.length()) {
      EC.ExecPop();
    } else {
      EC.ECL.ExecChanged();
    }

    //If it's an array we see, push it to the op stack regardless of exe/lit setting
    //Otherwise we would immediately execute the array, bad for things like ifelse.
    if(current.Type==arraytype) {
      EC.Push(current);
    } else {
      EC.ExecPush(new ExecStackEntry(current));
    }
  }
}
