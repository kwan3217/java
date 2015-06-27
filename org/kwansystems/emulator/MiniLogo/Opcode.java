package org.kwansystems.emulator.MiniLogo;

class Opcode {
  public enum Action {
    Draw {
      public void act(VM target) {
        target.DoDraw(target.popOp());
      }
    },
    Move {
      public void act(VM target) {
        target.DoMove(target.popOp());
      }
    },
    Right {
      public void act(VM target) {
        target.DoTurn(target.popOp());
      }
    },
    Point  {
      public void act(VM target) {
        target.DoPoint(target.popOp());
      }
    },
    Home  {
      public void act(VM target) {
        target.DoHome();
      }
    },
    Remember  {
      public void act(VM target) {
        target.PushTurtle();
      }
    },
    GoBack {
      public void act(VM target) {
        target.PopTurtle();
      }
    },
    ProcDef {
      public void act(VM target) {
        throw new IllegalArgumentException("ProcDef opcodes should never be executed!");
      }
    },
    Return {
      public void act(VM target) {
        target.DoProcReturn();
      }
    },
    Loop {
      public void act(VM target) {
        target.DoIterate();
      }
    },
    ProcCall {
      public void act(VM target) {
        target.DoProcCall(target.popOp());
      }
    },
    Rep {
      public void act(VM target) {
        target.DoLoopStart(target.popOp());
      }
    },
    SetColor {
      public void act(VM target) {
        target.SetColor((short)(target.popOp()%VM.defaultColorList.length));
      }
    },
    LastOp {
      public void act(VM target) {
        target.doDone();
      }
    },
    Left {
      public void act(VM target) {
        target.DoTurn((short)(-target.popOp()));
      }
    },
    Add {
      public void act(VM target) {
        short argy=target.popOp();
        short argx=target.popOp();
        target.pushOp((short)(argx+argy));
      }
    },
    Subtract {
      public void act(VM target) {
        short argy=target.popOp();
        short argx=target.popOp();
        target.pushOp((short)(argx-argy));
      }
    },
    Multiply {
      public void act(VM target) {
        short argy=target.popOp();
        short argx=target.popOp();
        target.pushOp((short)(argx*argy));
      }
    },
    Divide {
      public void act(VM target) {
        short argy=target.popOp();
        short argx=target.popOp();
        target.pushOp((short)(argx/argy));
      }
    },
    Iter {
      public void act(VM target) {
        target.pushOp(target.getIter());
      }
    },
    Ref {
      public void act(VM target) {
        short varNum=target.popOp();
        target.pushOp(target.getVar(varNum));
      }
    },
    Assign {
      public void act(VM target) {
        short varNum=target.popOp();
        short value=target.popOp();
        target.putVar(varNum,value);
      }
    },
    Neg {
      public void act(VM target) {
        short argx=target.popOp();
        target.pushOp((short)-argx);
      }
    };
    public void act(VM target) {
      
    }
  };
  public static enum Klass {
    Number {
      public void act(Opcode opcode, VM target) {
        target.pushOp(opcode.value);         
      }
    },
    Action {
      public void act(Opcode opcode, VM target) {
        opcode.action.act(target);        
      }
    };
    public void act(Opcode opcode, VM target) {}
  }; 
  Klass klass;
  Action action;
  short value;
  public Opcode(Action Laction) {
    klass=Klass.Action;
    action=Laction;
  }
  public Opcode(short Lvalue) {
    klass=Klass.Number;
    value=Lvalue;
  }
  public Opcode(byte[] bin) {
    klass=Klass.values()[bin[0]];
    if(klass==Klass.Action) {
      action=Action.values()[bin[1]]; //OK as long as there aren't more than 128 action codes
    } else {
      int v=(bin[2]<<8)|(bin[1]&0xff);
      value=(short)v;
    }
  }
  public boolean isNumber() {
    return klass==Klass.Number;
  }
  public short getNumber() {
    if(!isNumber()) throw new IllegalArgumentException("Opcode is not a number");
    return value;
  }
  public boolean isAction() {
    return klass==Klass.Action;
  }
  public boolean isAction(Opcode.Action Laction) {
    return (klass==Klass.Action && action==Laction);
  }
  public Opcode.Action getAction() {
    if(!isAction()) throw new IllegalArgumentException("Opcode is not a number");
    return action;
  }
  public String toString() {
    if(klass==Klass.Action) {
      return action.toString();
    }
    return Short.toString(value);
  }
  public byte[] toBin() {
    byte[] result=new byte[3];
    result[0]=(byte)klass.ordinal();
    if(klass==Klass.Action) {
      result[1]=(byte)action.ordinal();
    } else {
      result[1]=(byte)(value & 0xff);
      result[2]=(byte)(value >>> 8);
    }
    return result;
  }
  public void act(VM target) {
    klass.act(this, target); 
  }
}