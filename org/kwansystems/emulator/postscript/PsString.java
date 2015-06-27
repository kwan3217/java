package org.kwansystems.emulator.postscript;

import javax.swing.tree.DefaultMutableTreeNode;

public class PsString extends PsComposite {
  String Data;
  public PsString(String LData, ExecContext LEC) {
    super(LEC);
    Data=LData;
  }
  public String get() {
    return Data;
  }
  public void set(String LData) {
    Data=LData;
    EC.HeapChanged();
  }
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode Node=new DefaultMutableTreeNode(Data);
    return Node;
  }
  public int length() {
    return Data.length();
  }
  public String toString() {
    return Data;
  }
}
