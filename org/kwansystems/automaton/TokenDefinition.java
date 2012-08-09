package org.kwansystems.automaton;

import org.kwansystems.automaton.part1.regexp.RegExParser;
import org.kwansystems.automaton.part1.regexp.RegExpTree;
import org.kwansystems.automaton.regexp.*;

public class TokenDefinition<E> {
  public RegExpTree definition;
  public E type;
  public TokenDefinition(RegExpTree Ldefinition, E Ltype) {
    definition=Ldefinition;
    type=Ltype;
  }
  public TokenDefinition(String Ldefinition, E Ltype) throws AutomatonException {
    definition=RegExParser.compile(Ldefinition);
    type=Ltype;
  }
  public String toString() {
	return type.toString()+definition.toString();
  }
}
