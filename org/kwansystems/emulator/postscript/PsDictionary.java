package org.kwansystems.emulator.postscript;

import java.util.HashMap;

import javax.swing.tree.*;

public class PsDictionary extends PsComposite {
  String Tag;
  HashMap<Object, PsObject> Entries;
  public PsDictionary(int Capacity, String LTag, ExecContext LEC) {
    super(LEC);
    Entries=new HashMap<Object, PsObject>(Capacity);
    Tag=LTag;
  }
  public void Define(Object Name, PsObject Value) {
    Entries.put(Name,Value);
    EC.HeapChanged();
  }
  public PsObject LookUp(Object Name) {
    return Entries.get(Name);
  }
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode Node=new DefaultMutableTreeNode("Entries");
    for(Object Name : Entries.keySet()) {
      DefaultMutableTreeNode Child=new DefaultMutableTreeNode(Name.toString());
      Child.add(Entries.get(Name).getNode());
      Node.add(Child);
    }
    return Node;
  }
  public String toString() {
    return "--"+Tag+"--";
  }
  public int length() {
    return Entries.size();
  }
  public boolean HasEntry(Object Name) {
    return Entries.containsKey(Name);
  }
}
