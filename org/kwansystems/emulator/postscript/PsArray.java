package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.Unlimited;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.Literal;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.nulltype;

import javax.swing.tree.DefaultMutableTreeNode;

public class PsArray extends PsComposite {
  PsObject[] Elements;
  public PsArray(int NumElements, ExecContext LEC) {
    super(LEC);
    Elements=new PsObject[NumElements];
    for(int i=0;i<Elements.length;i++) {
      Elements[i]=new PsObject(nulltype,Literal,Unlimited,null);
    }
  }
  public PsArray(PsObject[] LElements, ExecContext LEC) {
    super(LEC);
    Elements=LElements.clone();
  }
  public PsObject get(int Index) {
    return Elements[Index];
  }
  public void set(int Index, PsObject newElement) {
    Elements[Index]=newElement;
    EC.HeapChanged();
  }
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode Node=new DefaultMutableTreeNode("Elements");
    for(int i=0;i<Elements.length;i++) {
      Node.add(Elements[i].getNode());
    }
    return Node;
  }
  public int length() {
    return Elements.length;
  }
  public String toString() {
    return "--"+Elements.length+" array--";
  }
}
