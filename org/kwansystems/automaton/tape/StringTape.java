package org.kwansystems.automaton.tape;

import org.kwansystems.automaton.*;


/**
 * Tape of <tt>Character</tt>s with a simplified loading mechanism to load a
 * full <tt>String</tt> at once.
 *
 */
public class StringTape implements Tape<Character> {
  private static final long serialVersionUID = 5650255164172856102L;
  private final String data;
  private int index;
  private boolean isCrashed;
  public StringTape(String Lcontents) {
    data=new String(Lcontents);
    index=0;
    isCrashed=false;
  }
  public Character read() {
    if(isCrashed) return null;
    if(index>=data.length()||index<0) {
      isCrashed=true;
      return null;
    }
    return data.charAt(index);
  }

  public Termination write(Character newSymbol){
    isCrashed=true;
    return Termination.Crash;
  }

  public Termination Right() {
    if(isCrashed) return Termination.Crash;
    index++;
    if(index>=data.length()) return Termination.Crash;
    return Termination.Continue;
  }

  public Termination Left() {
    if(isCrashed) return Termination.Crash;
    index--;
    if(index<0) return Termination.Crash;
    return Termination.Continue;
  }

  public TapeDisplay<Character> getTapeDisplay() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void setBlankSymbol(Character blank) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Character getBlankSymbol() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
