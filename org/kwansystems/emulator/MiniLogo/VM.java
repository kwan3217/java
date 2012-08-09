package org.kwansystems.emulator.MiniLogo;

import java.io.*;
import java.awt.Color;
import java.util.*;

public class VM {
  private static final Color clBlack =new Color(0,0,0);
  private static final Color clBrown =new Color(128,64,0);
  private static final Color clRed   =new Color(255,0,0);
  private static final Color clOrange=new Color(255,128,0);
  private static final Color clYellow=new Color(255,255,0);
  private static final Color clGreen =new Color(0,255,0);
  private static final Color clBlue  =new Color(0,0,255);
  private static final Color clPurple=new Color(128,0,255);
  private static final Color clGray  =new Color(128,128,128);
  private static final Color clWhite =new Color(255,255,255);
  public static final Color[] defaultColorList=new Color[] {clBlack,clBrown,clRed,
                                                            clOrange,clYellow,
                                                            clGreen,clBlue,clPurple,
                                                            clGray,clWhite};
  private VMListener face;
  private static class TurtleState {
    public double X,Y,Heading; /*Turtle State. X is Left to right, Y is top to bottom*/
                               /*Heading is 0 degrees (North),  90 degrees (East),
                                          180 degrees (South), 270 degrees (West).*/
    public String toString() {
      return String.format("(%f,%f) %f", X,Y,Heading);
    }
    public TurtleState(double LX, double LY, double LHeading) {
      X=LX;
      Y=LY;
      Heading=LHeading;
    }
    public TurtleState(TurtleState old) {
      X=old.X;
      Y=old.Y;
      Heading=old.Heading;
    }
    public TurtleState() {}
  }

  /*Structure for the loop stack*/
  private static class LoopStruc {
    short Count;
    short Limit;
    int Top;
    public LoopStruc(short LCount,int LTop) {
      Count=0;
      Limit=LCount;
      Top=LTop;
    }
  }

  /*Structure for the procedure call stack*/
  private static class StackFrame {
    short Proc;
    int Pos;
    public StackFrame(short LProc,int LPos) {
      Proc=LProc;
      Pos=LPos;
    }
  }
  int ProcCounter=0;
  short CurrentProc=-1;
  
  Map<Short,List<Opcode>> ProcTable=new TreeMap<Short,List<Opcode>>();
  Map<Short,Short> VarTable=new TreeMap<Short,Short>();

  TurtleState Turtle;
  /**Holds the numbers for an RPN Interpreter*/
  Stack<Short> OpStack=new Stack<Short>(); 

  /**Holds the return addresses for void calls*/
  Stack<StackFrame> RetStack=new Stack<StackFrame>();

  /**Holds the loop points*/
  Stack<LoopStruc> LoopStack=new Stack<LoopStruc>();

  /**Holds the remember/goback states*/
  Stack<TurtleState> TurtleStack=new Stack<TurtleState>();

  //Execution Procedures

  /**Moves the turtle <Steps> pixels without drawing a line*/
  void DoMove(short Steps) {
    Turtle.X=Turtle.X+Steps*Math.sin(Math.toRadians(Turtle.Heading));
    Turtle.Y=Turtle.Y+Steps*Math.cos(Math.toRadians(Turtle.Heading));
    face.MoveTo(Turtle.X,Turtle.Y);
  }

  /**Moves the turtle to location (x,y) without drawing a line*/
  void DoMoveTo(double X,double Y) {
    Turtle.X=X;
    Turtle.Y=Y;
    face.MoveTo(Turtle.X,Turtle.Y);
  }

  /**Moves the turtle <Steps> pixels and draws a line*/
  void DoDraw(short Steps) {
    Turtle.X=Turtle.X+(double)Steps*Math.sin(Math.toRadians(Turtle.Heading));
    Turtle.Y=Turtle.Y+(double)Steps*Math.cos(Math.toRadians(Turtle.Heading));
    face.LineTo(Turtle.X,Turtle.Y);
  }

  /**Moves the turtle to location (x,y) and draws a line*/
  void DoDrawTo(double X, double Y) {
    Turtle.X=X;
    Turtle.Y=Y;
    face.LineTo(Turtle.X,Turtle.Y);
  }

  /**Turns the turtle <Theta> degrees to the right*/
  void DoTurn(short Theta) {
    Turtle.Heading+=Theta;
    while(Turtle.Heading<0) Turtle.Heading+=360;
    while(Turtle.Heading>360) Turtle.Heading-=360;
  }

  /**Turns the turtle <Theta> degrees to the right*/
  void DoPoint(short Theta) {
    Turtle.Heading=Theta;
  }

  /**Centers the turtle in the canvas*/
  void DoHome() {
    DoMoveTo(0,0);
    DoPoint((short)0);
  }
  
  /**Centers the turtle in the canvas*/
  short getIter() {
    LoopStruc L=LoopStack.peek();
    return L.Count;
  }
  
  short getVar(short varNum) {
    return VarTable.get(varNum);
  }
  
  void putVar(short varNum, short value) {
    VarTable.put(varNum,value);
  }
  
  public void Load(InputStream LPCodeFile) throws IOException {
    byte[] buf=new byte[3];
    List<Opcode> MainProc=new ArrayList<Opcode>();
    ProcTable.put((short)-1,MainProc);
    List<Opcode> Proc=MainProc;
    boolean done=false;
    while(!done) {
      LPCodeFile.read(buf);
      Opcode nextOp=new Opcode(buf);
      if(nextOp.isAction(Opcode.Action.LastOp)) {
        done=true;
        Proc.add(nextOp);
      } else if(nextOp.isAction(Opcode.Action.ProcDef)) {
        Opcode lastOp=Proc.remove(Proc.size()-1); //The proc number is in the lastop
        Proc=new ArrayList<Opcode>();
        ProcTable.put(lastOp.getNumber(), Proc);
        //Don't put this opcode anywhere but down the memory hole.
      } else if(nextOp.isAction(Opcode.Action.Return)) {
        Proc.add(nextOp);
        Proc=MainProc;
      } else {
        Proc.add(nextOp);
      }
    }
    loaded=true;
  }
  boolean loaded=false;
  public void Unload() {
    ProcTable.clear();
    loaded=false;
  }
  
  public void DoProcCall(short newProc) {
    RetStack.push(new StackFrame(CurrentProc,ProcCounter));
    CurrentProc=newProc;
    ProcCounter=0;
  }
  
  public void DoProcReturn() {
    StackFrame retFrame=RetStack.pop();
    CurrentProc=retFrame.Proc;
    ProcCounter=retFrame.Pos;
  }

  /**Initializes VM pointers, and sets the drawing area on which
   * to interpret the binary pcode
   * @throws IOException 
   */
  public VM(InputStream LPCodeFile, VMListener Lface) throws IOException {
    this(Lface);
    Load(LPCodeFile);
  }
  public VM(VMListener Lface) {
    this();
    face=Lface;
  }
  public VM() {}
  
  public void pushOp(short op) {
    OpStack.push(op);
  }

  public short popOp() {
    return OpStack.pop();
  }

  /*Pushes the current turtle onto the Remember/Goback turtle stack*/
  void PushTurtle() {
    TurtleStack.push(new TurtleState(Turtle));
  }

  /*Pops the top turtle from the turtle stack and makes
   it the current turtle*/
  void PopTurtle() {
    Turtle=TurtleStack.pop();
    face.MoveTo(Math.round(Turtle.X),Math.round(Turtle.Y));
  }

  void DoLoopStart(short Count) {
    LoopStack.push(new LoopStruc(Count,ProcCounter));
  }

  void DoIterate() {
    LoopStruc currentLoop=LoopStack.peek();
    currentLoop.Count++;
    if(currentLoop.Count<currentLoop.Limit) {
      ProcCounter=currentLoop.Top;
    } else {
      LoopStack.pop();
    }
  }
  
  private boolean doneInterp=false;
  
  public void doDone() {
    doneInterp=true;
  }
  private void reset() {
    Turtle=new TurtleState();
    CurrentProc=-1;
    ProcCounter=0;
  }
  /*Interprets named binary pcode file*/
  void Interpret() {
    if(!loaded) return;
    Opcode nextOp;
    reset();
    DoHome();
    doneInterp=false;
    while(!doneInterp) {
      nextOp=ProcTable.get(CurrentProc).get(ProcCounter);
      ProcCounter++;
      nextOp.act(this);
    }
    face.finish();
  }

  public void SetColor(short s) {
    face.SetColor(s);
  }

  public void setFace(VMListener face) {
    this.face=face;
  }
}

