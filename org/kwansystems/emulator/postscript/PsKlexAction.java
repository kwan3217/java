package org.kwansystems.emulator.postscript;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.*;

import java.util.*;

public enum PsKlexAction implements KlexAction<PsKlexAction>,SymbolType {
  NOTHING(true,false),
  //Tokens used by normal scanner       
  INTEGER(false,true) {
    public void act(Token T, Object context) {
      T.value=new Integer(Integer.parseInt(T.value.toString())); 
    }
  },
  REAL(false,true) {
    public void act(Token T, Object context) {
      T.value=new Double(Double.parseDouble(T.value.toString())); 
    }
  },
  LCURLY(false,true),
  RCURLY(false,true),
  LITERAL(false,true) {
    public void act(Token T, Object context) {
      T.value=T.value.toString().substring(1); 
    }
  },
  IMMEDIATE(false,true) {
    public void act(Token T, Object context) {
      T.value=T.value.toString().substring(2); 
    }
  },
  EOF(false,true),
  STRINGSTART(false,false) {
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      K.resetStringLiteral();
      K.setState(1);
    }
  },
  HEXSTART(false,false) {
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      K.resetStringLiteral();
      K.setState(2);
    }
  },
  BASE85START(false,false) {
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      K.resetStringLiteral();
      K.setState(3);
    }
  },  
  NAME(false,true),  //Name gets last precedence, as pretty much
                     //anything that doesn't look like something 
                     //else looks like a name
  STRING(false,true),//A String token is not recognized by any 
                     //single klex state, but by a cooperation of 
                     //all of them, so no individual NFA will have
                     //a string as an accept token
  //Tokens used by Hex scanner
  HEXDATA(false,false),
  HEXEND(false,true), 
  //Tokens used by Base85 scanner
  BASE85DATA(false,false),
  BASE85END(false,true),
  //Tokens used by String scanner
  STRINGDATA(false,false) {
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      K.addStringLiteral(T.value.toString());
    }
  },  
  LPAREN(false,false){
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      K.addStringLiteral(T.value.toString());
      K.incParen();
    }
  }, 
  RPAREN(false,false){
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      if(K.decParen()) {
        K.addStringLiteral(T.value.toString());
      } else {
        T.type=STRING; 
        T.value=K.getStringLiteral();
        K.setState(0);
      }
    }
  }, 
  ESCAPE(false,false) {
    public void act(Token T, Object context) {
      PsKlex K=(PsKlex)context;
      K.addStringLiteral(escapeMap.get(T.value.toString()));
    }
  },
  ESCAPEOCT(false,false) {
    
  };
  
  public boolean Ignore;
  public boolean SendUp;
  private static HashMap<String,String> escapeMap=new HashMap<String,String>();
  static {
    escapeMap.put("\\n","\n");
    escapeMap.put("\\r","\r");
    escapeMap.put("\\t","\t");
    escapeMap.put("\\b","\b");
    escapeMap.put("\\f","\f");
    escapeMap.put("\\\\","\\");
    escapeMap.put("\\(","(");
    escapeMap.put("\\)",")");
  }
  private PsKlexAction(boolean LIgnore, boolean LSendUp) {
    Ignore=LIgnore;
    SendUp=LSendUp;
  }
  public void act(Token T, Object context) {}
  public boolean ignore() {
    return Ignore;
  }
  public boolean sendUp() {
    return SendUp;
  }
  public boolean isTerminal() {
    return true;
  }
  public SymbolType.Assoc getAssoc() {
    return SymbolType.Assoc.LEFT;
  }
  public Integer getPrecedence() {
    return null;
  }
}
