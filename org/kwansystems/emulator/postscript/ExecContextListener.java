package org.kwansystems.emulator.postscript;

import org.kwansystems.emulator.postscript.execstack.ExecStackEntry;

public interface ExecContextListener {
  public void Push(PsObject O);
  public void Pop();
  public void ExecPush(ExecStackEntry O);
  public void ExecPop();
  public void DictPush(PsObject O);
  public void DictPop();
  public void HeapChanged();
  public void ExecChanged();
  public void pause();
}
