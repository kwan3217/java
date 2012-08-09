package org.kwansystems.automaton.part1.regexp;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.NFA;

public abstract class RegExpTree {
  /** Create an NFA equivalent to the regular expression defined by this tree root
   * and all its branches, by Thomson's Construction. 
   * @param StatePrefix All state names in the machine should be prefixed
   * by this string. 
   * @return an NFA. This object relinquishes all claim to the NFA, so the calling
   * function may do what it will to the machine. For Thomson's Construction to work,
   * any NFA returned should have exactly one accepting state, with no outgoing transitions,
   * and one start state, with no incoming transitions
   */
  public abstract NFA<Character,String> getNFA(String StatePrefix);
}
