package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

import java.util.*;

import org.kwansystems.emulator.postscript.execstack.*;
import org.kwansystems.emulator.postscript.painter.*;

public class ExecContext {
  private Stack<PsObject> OpStack;
  private Stack<ExecStackEntry> ExecStack; 
  private Stack<PsObject> DictStack;
  private Stack<GraphicsState> GStateStack;
  private PsDictionary SystemDict;
  private PsDictionary UserDict;
  public GraphicsState currentGState;
  public ExecContextListener ECL;
  public Painter painter;
  public boolean HeapRefreshEnabled;
  
  public ExecContext(Painter LP,ExecContextListener LECL) {
    OpStack=new Stack<PsObject>();
    ExecStack=new Stack<ExecStackEntry>();
    DictStack=new Stack<PsObject>();
    GStateStack=new Stack<GraphicsState>();
    SystemDict=new SystemDictionary(this);
    DictPush(new PsObject(dicttype,Literal,Unlimited,SystemDict));
    //Bind the compound entries in the systemdict
    for(PsObject I:SystemDict.Entries.values()) {
      Object PCO=I.get();
      if(PCO!=null) {
        String CN=PCO.getClass().getCanonicalName();
        if(CN!=null && CN.equals("org.kwansystems.psinterp.CompoundOperator")) {
          Bind(((PsArray)((CompoundOperator)PCO).FunctionBody.get()));
        }
      }
    }

    UserDict=new PsDictionary(200,"userdict",this);
    DictPush(new PsObject(dicttype,Literal,Unlimited,UserDict));
    currentGState=new GraphicsState();
    //Don't set up the listeners until the initial environment is set up.
    ECL=LECL;
    painter=LP;
    painter.initclip(currentGState);
    painter.initctm(currentGState);
    HeapRefreshEnabled=true;
  }

  public void Push(PsObject o) {
    OpStack.push(o);
    if(ECL!=null)ECL.Push(o);
  }
  public void Push(PsObject[] A) {
    for(int i=0;i<A.length;i++) {
      Push(A[i]);
    }
  }
  public PsObject Pop(String opname) {
    if(OpStack.size()<=0)throw new PostscriptError("stackunderflow",opname,null);
    if(ECL!=null)ECL.Pop();
    return OpStack.pop();
  }
  public Iterator OpStackIterator() {
    return OpStack.iterator();
  }
  
  
  public void DictPush(PsObject o) {
    DictStack.push(o);
    if(ECL!=null)ECL.DictPush(o);
  }
  public PsObject DictPop(String opName) {
    if(DictStack.size()<=2)throw new PostscriptError("dictstackunderflow",opName,null);
    if(ECL!=null)ECL.DictPop();
    return DictStack.pop();
  }
  public void GSave() {
    GStateStack.push(new GraphicsState(currentGState));
  }
  public void GRestore() {
    if(GStateStack.size()>0) currentGState=GStateStack.pop();
  }

  //This isn't really the right semantics, opName should be a PsObject, because
  //a dictionary key can be any kind of object
  public PsObject where(String opName) {
    PsObject result=null;
    for(PsObject I : DictStack) {
      PsDictionary Dict=(PsDictionary)I.get();
      if(Dict.HasEntry(opName)) result=I;
    }
    return result;
  }
  public PsDictionary currentdict() {
    return (PsDictionary)(DictStack.peek().get());
  }
  public PsObject LookupName(String opName) {
    PsObject result=null;
    for(PsObject I : DictStack) {
      PsDictionary Dict=(PsDictionary)I.get();
      if(Dict.HasEntry(opName)) result=Dict.LookUp(opName);
    }
    if(result==null) throw new PostscriptError("undefined",opName,null);
    return result;
  }
  public void Define(String Name, PsObject Value) {
    PsObject TopDict=DictStack.peek();
    ((PsDictionary)(TopDict.get())).Define(Name,Value);
    if(ECL!=null)ECL.HeapChanged();
  }
  
  public Iterator DictStackIterator() {
    return DictStack.iterator();
  }
  
  public void ExecPush(ExecStackEntry o) {
    ExecStack.push(o);
    if(ECL!=null)ECL.ExecPush(o);
  }
  public ExecStackEntry ExecPop() {
    if(ECL!=null)ECL.ExecPop();
    return ExecStack.pop();
  }
  public void Execute() {
    ExecStack.peek().ExecStep(this);
  }
  public boolean Done() {
    return ExecStack.size()==0;
  }
  public Iterator ExecStackIterator() {
    return ExecStack.iterator();
  }
  public void HeapChanged() {
    if(ECL!=null & HeapRefreshEnabled)ECL.HeapChanged();
  }
  public void Bind(PsArray ThingToBind) {
    for(int i=0;i<ThingToBind.length();i++) {
      PsObject Element=ThingToBind.get(i);
      if(Element.State==Executable) {
        switch(Element.Type) {
          case nametype:
            try {
              PsObject NewElement=LookupName(Element.get().toString());
              if(NewElement.Type==operatortype) ThingToBind.set(i,NewElement);
            } catch (PostscriptError E) {
              if(E.error.equals("undefined")) {
                //Ignore undefined errors, just go on with life
              } else {
                throw E;
              }
            }
            break;
          case arraytype: //Executable Arrays are recursively bound and bound again as necessary
            Bind((PsArray)(Element.get()));
            break;
        }
      }
    }
  }
}
