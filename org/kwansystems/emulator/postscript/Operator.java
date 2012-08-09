package org.kwansystems.emulator.postscript;

import java.util.*;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

public abstract class Operator {
  /** Creates new Operator  */
  public String opName;
  private ArrayList<PsObject> PopData;
  private ArrayList<PsObject> PushData;
  private ExecContext EC;
  public Operator(String LName) {
    opName=LName;
  }
  public void Operate(ExecContext LEC) {
    PopData=new ArrayList<PsObject>();
    PushData=new ArrayList<PsObject>();
    EC=LEC;
    try {
      doOperate(LEC);
      for(PsObject Obj:PushData) {
        EC.Push(Obj);
      }
    } catch (PostscriptError E) {
      //The official Postscript error handling system is...
      //1. Restore the stack to its state before the operator
      for(PsObject Obj:PopData) {
        EC.Push(Obj);
      }
      //2. Push the operator onto the stack
      EC.Push(new PsObject(operatortype,Executable,Unlimited,this));
      //3. Call the entry in errordict which matches the error name.
      //For now, we do Java exception propagation instead. Just re-throw 
      //the exception
      throw new PostscriptError(E,opName);
    } 
  }
  protected PsObject Pop() {
    PsObject Obj=EC.Pop(opName);
    PopData.add(0,Obj);
    return Obj;
  }
  protected void Push(PsObject Obj) {
    PushData.add(Obj);
  }
  protected abstract void doOperate(ExecContext EC);
  public String toString() {
    return "--"+opName+"--";
  }
}

