package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

import org.kwansystems.emulator.postscript.execstack.ExecStackEntry;

class CompoundOperator extends Operator {
  PsObject FunctionBody;
  public CompoundOperator(String LName, PsArray LFunctionBody) {
    super(LName);
    FunctionBody=new PsObject(arraytype,Executable,Unlimited,LFunctionBody);
  }
  protected void doOperate(ExecContext EC) {
    throw new PostscriptError("internalerror","Tried to run the doOperate method of a CompoundOperator");
  }
  public void Operate(ExecContext EC) {
    EC.ExecPush(ExecStackEntry.makeExecStackEntry(FunctionBody));
  }
  public static PsArray makeCOArray(String[] ops) {
    PsObject[] A=new PsObject[ops.length];
    for(int i=0;i<ops.length;i++) {
      A[i]=new PsObject(nametype,Executable,Unlimited,new PsString(ops[i],null));
    }
    return new PsArray(A,null); 
  }
}