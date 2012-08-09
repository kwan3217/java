package org.kwansystems.emulator.postscript.execstack;

import java.io.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.kwansystems.emulator.postscript.*;

import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;

public class ExecStackEntry {
  protected PsObject Item;
  protected ExecStackEntry(PsObject LItem) {
    Item=LItem;
  }
  public static ExecStackEntry makeExecStackEntry(PsObject LItem) {
    if(LItem.State==Literal) return new ExecStackEntry(LItem);
    try {
      switch(LItem.Type) {
        case filetype:
          return new ScannerExecStackEntry(LItem,new PsKlex((Reader)(LItem.get())));
        case stringtype:
          return new ScannerExecStackEntry(LItem,new PsKlex(new StringReader(LItem.get().toString())));
        case arraytype:
          return new ArrayExecStackEntry(LItem);
        default:
          return new ExecStackEntry(LItem);
      }
    } catch (IOException E) {
      throw new PostscriptError("ioerror","in makeExecStackEntry",E);
    }
  }
  public String toString() {return Item.toString();}
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode result=new DefaultMutableTreeNode(toString());
    DefaultMutableTreeNode ItemNode=new DefaultMutableTreeNode("Item");
    ItemNode.add(Item.getNode());
    result.add(ItemNode);
    return result;
  }
  public void ExecStep(ExecContext EC) {
    if(Item.State == Literal) {
      EC.Push(Item);
      EC.ExecPop();
    } else {
      switch(Item.Type) {
        case operatortype:
          EC.ExecPop();
          ((Operator)Item.get()).Operate(EC);
          break;
        case integertype:
        case realtype:
        case booleantype:
        case dicttype:
        case marktype:
        case savetype:
        case gstatetype:
        case fonttype:
          EC.ExecPop();
          EC.Push(Item);
          break;
        case nametype:
          EC.ExecPop();
          PsObject Value=EC.LookupName(Item.get().toString());
          EC.ExecPush(ExecStackEntry.makeExecStackEntry(Value));
          break;
        case arraytype:
        case stringtype:
        case filetype:
          EC.ExecPush(ExecStackEntry.makeExecStackEntry(Item));
          break;
      }
    }
  }
}