package org.kwansystems.emulator.postscript.execstack;

import javax.swing.tree.DefaultMutableTreeNode;

import org.kwansystems.emulator.postscript.ExecContext;
import org.kwansystems.emulator.postscript.PostscriptError;
import org.kwansystems.emulator.postscript.PsArray;
import org.kwansystems.emulator.postscript.PsComposite;
import org.kwansystems.emulator.postscript.PsObject;
import org.kwansystems.emulator.postscript.*;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

public class ForallExecStackEntry extends ExecStackEntry {
  PsObject Source;
  protected int Position;
  public ForallExecStackEntry(PsObject LItem, PsObject LSource) {
    super(LItem);
    Position=0;
    Source=LSource;
  }
  public String toString() {return "ForallExecStackEntry";}
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode result=super.getNode();
    result.add(new DefaultMutableTreeNode("Position: "+Position));
    DefaultMutableTreeNode SourceNode=new DefaultMutableTreeNode("Source");
    SourceNode.add(Source.getNode());
    result.add(SourceNode);
    return result;
  }
  public void ExecStep(ExecContext EC) {
    switch(Source.Type) {
      case arraytype: 
        EC.Push(((PsArray)Source.get()).get(Position));
        break;
      case stringtype: 
        EC.Push(
          new PsObject(integertype,Literal,Unlimited,Source.get().toString().codePointAt(Position))
        );
        break;
      default:
        throw new PostscriptError("internalerror","Something other than an array or string is the source of a forall entry");
    }
    Position++;
    if(Position>=((PsDictionary)(Source.get())).length()) {
      EC.ExecPop(); 
    } else {
      EC.ECL.ExecChanged();
    }
    EC.ExecPush(ExecStackEntry.makeExecStackEntry(Item));
  }

}
