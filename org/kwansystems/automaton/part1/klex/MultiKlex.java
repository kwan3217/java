package org.kwansystems.automaton.part1.klex;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.*;
import org.kwansystems.automaton.tape.*;

import java.util.*;

public class MultiKlex<TokenType extends Enum<TokenType>> implements Tape<Token> {
  protected int currentState;
  protected List<Klex<TokenType>> states;
  private Token currentToken=null;
  public Object context;
  public MultiKlex() {
	  
  }
  public MultiKlex(Klex<TokenType> singleState) {
    states=new ArrayList<Klex<TokenType>>();
    states.add(singleState);
    currentState=0;
  }
  public void setState(int Lstate) {
    currentState=Lstate;
  }
  public int getState() {
    return currentState;
  }
  public void addDisplay(TextDFAListener<Character,Integer> L) {
    for(Klex<TokenType> K:states) K.addDisplay(L);
  }
  public void setTape(Tape<Character> L)  {
    currentToken=null;
    for(Klex<TokenType> K:states) K.setTape(L);
  }
  public Termination Left() {
    throw new UnsupportedOperationException();
  }
  public Termination Right() {
    currentToken=null;
    return Termination.Continue;
  }
  public Token getBlankSymbol() {
    throw new UnsupportedOperationException();
  }
  public TapeDisplay<Token> getTapeDisplay() {
    throw new UnsupportedOperationException();
  }
  public Token read() {
    if(currentToken==null) {
      do {
        Klex<TokenType> thisState=states.get(currentState);
        thisState.context=context;
        currentToken=thisState.read();
        thisState.Right();
      } while(!((KlexAction<TokenType>)(currentToken.type)).sendUp());
    }
    return currentToken;
  }
  public void setBlankSymbol(Token b) {
    throw new UnsupportedOperationException();
  }
  public Termination write(Token newSymbol) {
    return Termination.Crash;
  }
}
