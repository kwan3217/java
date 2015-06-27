package org.kwansystems.emulator.MiniLogo;

import static org.kwansystems.emulator.MiniLogo.TokenType.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part2.kyacc.*;
import org.kwansystems.automaton.tape.*;

import java.io.*;
import org.kwansystems.automaton.part2.kyacc.ParserGenerator.Assoc;

public class Parse {
  private CodeGen CG;
  public Parse(PrintStream LTextPCode, OutputStream LBinPCode) {
    CG=new CodeGen(LTextPCode,LBinPCode);
  }
  private Grammar G=new Grammar(PROG,EOF,new Production[] {
    new Production(PROG,new Object[] {STATEMENT}),
    new Production(PROG,new Object[] {STATEMENT,PROG}),
    new Production(STATEMENT,new Object[] {PROCDEF}),
    new Production(STATEMENT,new Object[] {INBLOCK_STATEMENT}),
    new Production(PROCDEF,new Object[] {PROCHEAD,BLOCK}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Return);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {NOOP}) {
      public Token act(Token[] rightSide) {
        CG.GenerateNoOp((String)rightSide[0].value);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {ID})  {
      public Token act(Token[] rightSide) {
        CG.GenerateProcCall((String)rightSide[0].value);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {ID,ASSIGN,EXPR})  {
      public Token act(Token[] rightSide) {
        CG.GenerateVarDef((String)rightSide[0].value);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {ONEOP,EXPR}) {
      public Token act(Token[] rightSide) {
        CG.GenerateOneOp((String)rightSide[0].value);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {COLOR,COLORID})  {
      public Token act(Token[] rightSide) {
        CG.GenerateColor((Short)rightSide[1].value);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {COLOR,EXPR})  {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.SetColor);
        return super.act(rightSide);
      }
    },
    new Production(INBLOCK_STATEMENT,new Object[] {REPTDEF}),
    new Production(PROCHEAD,new Object[] {TO,ID}) {
      public Token act(Token[] rightSide) {
        CG.GenerateProcHead((String)rightSide[1].value);
        return super.act(rightSide);
      }
    },
    new Production(BLOCK,new Object[] {LBRACKET,BLOCKBODY}),
    new Production(BLOCKBODY,new Object[] {RBRACKET}),
    new Production(BLOCKBODY,new Object[] {INBLOCK_STATEMENT,BLOCKBODY}),
    new Production(REPTDEF,new Object[] {REPTHEAD,BLOCK}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Loop);
        return super.act(rightSide);
      }
    },
    new Production(REPTHEAD,new Object[] {REPEAT,EXPR}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Rep);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {EXPR,PLUS,EXPR}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Add);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {EXPR,MINUS,EXPR}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Subtract);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {EXPR,TIMES,EXPR}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Multiply);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {EXPR,DIVIDE,EXPR}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Divide);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {LPAREN,EXPR,RPAREN}) {
      public Token act(Token[] rightSide) {
        return new Token(EXPR,rightSide[1].value);
      }
    },
    new Production(EXPR,new Object[] {ID}) {
      public Token act(Token[] rightSide) {
        CG.GenerateVarRef((String)rightSide[0].value);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {NUM}) {
      public Token act(Token[] rightSide) {
        CG.putOp((Short)rightSide[0].value);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {MINUS,NUM}) {
      public Token act(Token[] rightSide) {
        CG.putOp((Short)rightSide[1].value);
        CG.putOp(Opcode.Action.Neg);
        return super.act(rightSide);
      }
    },
    new Production(EXPR,new Object[] {ITER}) {
      public Token act(Token[] rightSide) {
        CG.putOp(Opcode.Action.Iter);
        return super.act(rightSide);
      }
    }
  });
  private LRParser parse=new LRParser(G,
     new Object[][] {{PLUS,MINUS},{TIMES,DIVIDE}}, //Precedence table
     new Assoc[] {Assoc.LEFT,Assoc.LEFT},               //Association table
     "Data/MiniLogo/") {
    public void accept() {
      CG.putOp(Opcode.Action.LastOp);
    }
  };
  public String toString() {
    return parse.toString();
  }
  public static void main(String[] args) throws AutomatonException {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    ouf.println("Draw 100");
    ouf.println("Left 40+-50");
    ouf.println("");
    ouf.println("Color Red");
    ouf.println("Repeat 10 [Draw 20 Right 36]");
    ouf.println("");
    ouf.println("To Square [");
    ouf.println("  Repeat 4 [");
    ouf.println("    Draw 50");
    ouf.println("    Left 90");
    ouf.println("  ]");
    ouf.println("]");
    ouf.println("");
    ouf.println("To Circle [");
    ouf.println("  Repeat 180*2 [");
    ouf.println("    Draw 1");
    ouf.println("    Left 1");
    ouf.println("  ]");
    ouf.println("]");
    ouf.println("");
    ouf.println("Color Green");
    ouf.println("Repeat 10 [Square Left 36]");
    ouf.println("");
    ouf.println("Color Blue");
    ouf.println("Circle");
    Lex L=new Lex(new StringTape(result.toString()));
    Parse P=new Parse(System.out,null);
    System.out.println(P.parse);
    P.parse.parse(L);
  }
  public Token compile(Lex l) throws AutomatonException {
    return parse.parse(l);
  }
}
