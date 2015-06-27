package org.kwansystems.emulator.postscript.execstack;

import javax.swing.tree.DefaultMutableTreeNode;

import org.kwansystems.emulator.postscript.*;

public class RepeatExecStackEntry extends ExecStackEntry {
  protected int Position,Count;
  public RepeatExecStackEntry(PsObject LItem, int LCount) {
    super(LItem);
    Position=0;
    Count=LCount;
  }
  public String toString() {return "RepeatExecStackEntry";}
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode result=super.getNode();
    result.add(new DefaultMutableTreeNode("Position: "+Position));
    result.add(new DefaultMutableTreeNode("Count: "+Count));
    return result;
  }
  public void ExecStep(ExecContext EC) {
    Position++;
    if(Position>=Count) {
      EC.ExecPop(); 
    } else {
      EC.ECL.ExecChanged();
    }
    EC.ExecPush(ExecStackEntry.makeExecStackEntry(Item));
  }

}
