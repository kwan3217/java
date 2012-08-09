package org.kwansystems.emulator.MiniLogo;

import java.io.*;
import java.util.*;

public class CodeGen {
  public PrintStream TextPCode;
  public OutputStream BinPCode;
  public CodeGen(PrintStream LTextPCode, OutputStream LBinPCode) {
    TextPCode=LTextPCode;
    BinPCode=LBinPCode;
  }
  private static class SymbolTableEntry {
    public enum Kind {Proc,Var}
    public Kind kind;
    public short num;
    public SymbolTableEntry(Kind Lkind, short Lnum) {
      kind=Lkind;
      num=Lnum;
    }
  }
  private Map<String,SymbolTableEntry> SymbolTable=new TreeMap<String,SymbolTableEntry>();
  private void putOp(Opcode op) {
    byte[] bin=op.toBin();
    if(TextPCode!=null) TextPCode.printf("%02X %02X %02X %s\n",bin[0],bin[1],bin[2],op.toString());
    if(BinPCode!=null) {
      try {
        BinPCode.write(bin);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
  public void putOp(short num) {
    putOp(new Opcode(num));
  }
  public void putOp(Opcode.Action action) {
    putOp(new Opcode(action));
  }
  private static Map<String,Opcode.Action> NoOpActionMap=new TreeMap<String,Opcode.Action>();
  private static Map<String,Opcode.Action> OneOpActionMap=new TreeMap<String,Opcode.Action>();
  static {
    NoOpActionMap.put("HOME",Opcode.Action.Home);
    NoOpActionMap.put("REMEMBER",Opcode.Action.Remember);
    NoOpActionMap.put("GOBACK",Opcode.Action.GoBack);
    OneOpActionMap.put("DRAW",Opcode.Action.Draw);
    OneOpActionMap.put("MOVE",Opcode.Action.Move);
    OneOpActionMap.put("RIGHT",Opcode.Action.Right);
    OneOpActionMap.put("LEFT",Opcode.Action.Left);
    OneOpActionMap.put("POINT",Opcode.Action.Point);
  }
  public void GenerateNoOp(String operator) {
    putOp(new Opcode(NoOpActionMap.get(operator)));
  }
  public void GenerateOneOp(String operator) {
    putOp(OneOpActionMap.get(operator));
  }
  public void GenerateProcCall(String ProcName) {
    if(!SymbolTable.containsKey(ProcName)) throw new IllegalArgumentException("Undefined symbol "+ProcName);
    SymbolTableEntry ste=SymbolTable.get(ProcName);
    if(ste.kind!=SymbolTableEntry.Kind.Proc) throw new IllegalArgumentException("Attempt to call a variable "+ProcName);
    putOp(SymbolTable.get(ProcName).num);
    putOp(Opcode.Action.ProcCall);

  }
  public void GenerateVarRef(String VarName) {
    if(!SymbolTable.containsKey(VarName)) throw new IllegalArgumentException("Undefined symbol "+VarName);
    SymbolTableEntry ste=SymbolTable.get(VarName);
    if(ste.kind!=SymbolTableEntry.Kind.Var) throw new IllegalArgumentException("Attempt to reference a procedure "+VarName);
    putOp(SymbolTable.get(VarName).num);
    putOp(Opcode.Action.Ref);

  }
  public void GenerateColor(short ColorName) {
    putOp(ColorName);
    putOp(Opcode.Action.SetColor);
  }
  public void GenerateProcHead(String ProcName) {
    if(SymbolTable.containsKey(ProcName)) throw new IllegalArgumentException("Attempt to redefine symbol "+ProcName);
    short nextSymbol=(short)SymbolTable.size();
    SymbolTable.put(ProcName, new SymbolTableEntry(SymbolTableEntry.Kind.Proc,nextSymbol));
    putOp(nextSymbol);
    putOp(Opcode.Action.ProcDef);
  }
  public void GenerateVarDef(String VarName) {
    SymbolTableEntry ste;
    if(SymbolTable.containsKey(VarName)) {
      ste=SymbolTable.get(VarName);
      if(ste.kind!=SymbolTableEntry.Kind.Var) throw new IllegalArgumentException("Attempt to reference a procedure "+VarName);
    } else {
      short nextSymbol=(short)SymbolTable.size();
      ste=new SymbolTableEntry(SymbolTableEntry.Kind.Var,nextSymbol);
      SymbolTable.put(VarName, ste);
    }
    putOp(ste.num);
    putOp(Opcode.Action.Assign);
  }
}
