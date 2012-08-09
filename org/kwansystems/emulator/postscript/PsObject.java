package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;
import org.kwansystems.automaton.*;

import javax.swing.tree.*;

public class PsObject implements TreeNodeGenerator,Comparable<PsObject> {

  public enum AccessMode {Unlimited,ReadOnly,ExecuteOnly,None};
  public enum StateMode {Literal,Executable};
  public enum TypeMode {arraytype, booleantype, dicttype, filetype, 
                 fonttype, gstatetype, integertype, marktype, 
                 nametype, nulltype, operatortype, realtype, 
                 packedarraytype, savetype, stringtype};
  public AccessMode Access;
  public StateMode State;
  public TypeMode Type;
  private Object Value;
  
  public PsObject(TypeMode LType,StateMode LState, AccessMode LAccess, Object LValue) {
    Type=LType;
    Access=LAccess;
    State=LState;
    String CN="null";
    if(LValue!=null){
      CN=LValue.getClass().getName();
    }
    switch(Type) {
      case arraytype:
      case packedarraytype:
        if(!(LValue instanceof PsArray)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type);
        break;
      case booleantype:
        if(!(LValue instanceof Boolean)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type); 
        break;
      case dicttype:
        if(!(LValue instanceof PsDictionary)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type); 
        break;
      case filetype:
        if(!(LValue instanceof java.io.Reader)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type); 
        break;
      case integertype:
        if(!(LValue instanceof Integer)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type); 
        break;
      case realtype:
        if(!(LValue instanceof Double)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type); 
        break;
      case operatortype:
        if(!(LValue instanceof Operator)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type); 
        break;
      case stringtype:
      case nametype:
        if(!(LValue instanceof PsString)) throw new PostscriptError("internalerror","Tried to assign a value of type "+CN+" to ps type "+Type);
        break;
      case nulltype:
        if(LValue!=null) throw new PostscriptError("internalerror","Tried to assign a non-null value to ps type "+Type);
        break;
    }
    Value=LValue;
  }
  public static PsObject TokenToObject(Token T, ExecContext EC) {
    switch((PsKlexAction)T.type) {
      case REAL:
        return new PsObject(realtype,Literal,Unlimited,T.value);
      case INTEGER:
        return new PsObject(integertype,Literal,Unlimited,T.value);
      case NAME:
      case LCURLY:
      case RCURLY:
        return new PsObject(nametype,Executable,Unlimited,new PsString((String)T.value,EC));
      case LITERAL:
        return new PsObject(nametype,Literal,Unlimited,new PsString((String)T.value,EC));
      case IMMEDIATE:
        return new PsObject(nametype,Executable,Unlimited,new PsString((String)T.value,EC));
      case STRING:
        return new PsObject(stringtype,Literal,Unlimited,new PsString((String)T.value,EC));
      default:
        throw new PostscriptError("internalerror","Huh? Unexpected token returned: "+T);
    }
  }
  public Object get() {
    return Value;
  }

  public String toString() {
    //Returns a string representation of *just* the value
    switch(Type) {
      case booleantype:
      case integertype:
      case stringtype:
      case realtype:
      case operatortype:
      case dicttype:
      case arraytype:
        return Value.toString();
      case nametype:
        return ((State==Literal)?"/":"")+Value.toString();
      case marktype:
        return "--mark--";
      case nulltype:
        return "--null--";
      case filetype:
        return "--file--";
      default:
        return "--nostringval--";
    }
  }
  
  double GetNumber() {
    switch(Type) {
      case realtype:
        return ((Double)Value).doubleValue();
      case integertype:
        return ((Integer)Value).intValue();
      default:
        throw new typecheck("object",new TypeMode[]{realtype,integertype},Type);
    }
  }

  int GetInt() {
    switch(Type) {
      case integertype:
        return ((Integer)Value).intValue();
      default:
        throw new typecheck("object",integertype,Type);
    }
  }

  PsString GetString() {
    switch(Type) {
      case nametype:
      case stringtype:
        return (PsString)Value;
      default:
        throw new typecheck("object",new TypeMode[]{stringtype,nametype},Type);
    }
  }
  
  public int compareTo(PsObject B) {
    //returns >0 if A>B, <0 if A<B, 0 if A==B, !0 if A!=B but unorderable
    if((Type==TypeMode.realtype || Type==TypeMode.integertype) && (B.Type==TypeMode.realtype || B.Type==TypeMode.integertype)) {
      return (GetNumber()==B.GetNumber())?0:((GetNumber()>B.GetNumber())?1:-1);
    } else if((Type==TypeMode.nametype || Type==TypeMode.stringtype) && (B.Type==TypeMode.nametype || B.Type==TypeMode.stringtype)) {
      return ((String)(Value)).compareTo((String)(B.Value));
    } else if (Type!=B.Type) {
      return -1;
    } else {
      return (Value==B.Value)?0:-1;
    }
  }
  public DefaultMutableTreeNode getNode() {
    DefaultMutableTreeNode Node=new DefaultMutableTreeNode(toString());
    Node.add(new DefaultMutableTreeNode("Type: "+Type));
    Node.add(new DefaultMutableTreeNode("State: "+State));
    Node.add(new DefaultMutableTreeNode("Access: "+Access));
    //Since the type and attribute are set above, only objects with printable structure need entries in this switch
    switch(Type) {
      case integertype:
        Node.add(new DefaultMutableTreeNode("Value: "+((Integer)Value).toString()));
        break;
      case realtype:
        Node.add(new DefaultMutableTreeNode("Value: "+((Double)Value).toString()));
        break;
      case booleantype:
        Node.add(new DefaultMutableTreeNode("Value: "+((Boolean)Value).toString()));
        break;
      case nametype:
      case stringtype:
      case arraytype:
      case dicttype:
        PsComposite A=(PsComposite)Value;
        Node.add(A.getNode());
        break;
   }
    return Node;
  }
}

