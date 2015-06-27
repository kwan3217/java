package org.kwansystems.emulator.MiniLogo;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.*;

import java.util.*;

public enum TokenType implements KlexAction<TokenType> {
  NOTHING(true),
  //Tokens used by normal scanner       
  NUM {
    public void act(Token T, Object context) {
      T.value=new Short(Short.parseShort(T.value.toString())); 
    }
  },
  LBRACKET,
  RBRACKET,
  EOF,
  ID {
    public void act(Token T, Object context) {
      T.value=T.value.toString().toUpperCase();
      T.type=KWList.get(T.value);
      if(T.type==null) T.type=ID;
      if(T.type==COLORID) T.value=ColorList.get(T.value);
    }
  },
  ONEOP,
  NOOP,
  COLOR,
  REPEAT,
  TO,
  COLORID,
  IF,
  ELSE,
  PLUS,
  MINUS,
  LPAREN,
  RPAREN,
  TIMES,
  DIVIDE,
  ASSIGN,
  ITER,
  PROG,
  PROCHEAD,
  REPTHEAD,
  INBLOCK_STATEMENT,
  BLOCK,
  PROCDEF,
  REPTDEF,
  BLOCKBODY,
  STATEMENT,
  EXPR;
  private static Map<String,TokenType> KWList=new TreeMap<String,TokenType>();
  private static Map<String,Short> ColorList=new TreeMap<String,Short>();
  static {
    KWList.put("DRAW",ONEOP);
    KWList.put("MOVE",ONEOP);
    KWList.put("LEFT",ONEOP);
    KWList.put("RIGHT",ONEOP);
    KWList.put("POINT",ONEOP);
    KWList.put("COLOR",COLOR);
    KWList.put("HOME",NOOP);
    KWList.put("REMEMBER",NOOP);
    KWList.put("GOBACK",NOOP);
    KWList.put("REPEAT",REPEAT);
    KWList.put("TO",TO);
    KWList.put("IF",IF);
    KWList.put("ELSE",ELSE);
    KWList.put("ITER",ITER);
    ColorList.put("BLACK", (short)0);
    ColorList.put("BROWN", (short)1);
    ColorList.put("RED",   (short)2);
    ColorList.put("ORANGE",(short)3);
    ColorList.put("YELLOW",(short)4);
    ColorList.put("GREEN", (short)5);
    ColorList.put("BLUE",  (short)6);
    ColorList.put("PURPLE",(short)7);
    ColorList.put("GRAY",  (short)8);
    ColorList.put("WHITE", (short)9);
    for(String C:ColorList.keySet()) KWList.put(C, COLORID);
  }
  private boolean isIgnore;
  private TokenType(boolean Lignore) {isIgnore=Lignore;}
  private TokenType() {this(false);}
  public void act(Token T, Object context) {}
  public boolean ignore() {return isIgnore;}
  public boolean sendUp() {return true;}
}
