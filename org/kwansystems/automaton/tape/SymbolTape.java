/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.automaton.tape;

import org.kwansystems.automaton.*;

/**
 *
 * @author jeppesen
 */
public class SymbolTape implements Tape<Token> {
  private int ptr=0;
  public Object[] type;
  private Token blank;
  boolean isCrashed;
  public void setTape(Object[] Ltype) {
    isCrashed=false;
    type=Ltype;
  }
  public SymbolTape(Object[] Ltype, Object blank) {
    setTape(Ltype);
    setBlankSymbol(new Token(blank));
  }
  public Termination Left() {
    if(isCrashed) return Termination.Crash;
    ptr--;
    if(ptr>=0) {
      return Termination.Continue;
    } else {
      isCrashed=true;
      return Termination.Crash;
    }
  }
  public Termination Right() {
    if(isCrashed) return Termination.Crash;
    ptr++;
    if(ptr<type.length) {
      return Termination.Continue;
    } else {
      isCrashed=true;
      return Termination.Crash;
    }
  }
  public Token getBlankSymbol() {
    return blank;
  }
  public TapeDisplay<Token> getTapeDisplay() {
    throw new UnsupportedOperationException();
  }
  public Token read() {
    if(isCrashed) return blank;
    if(ptr<0 || ptr>=type.length) {
      isCrashed=true;
      return blank;
    }
    return new Token(type[ptr]);
  }
  public void setBlankSymbol(Token b) {
    blank=b;
  }
  public Termination write(Token newSymbol) {
    return Termination.Crash;
  }
}
