package org.kwansystems.automaton.part1.regexp;

public class RegExpString extends Concat {
  String letters;
  public RegExpString(String Lletters) {
    super(null);
    letters=Lletters;
    children=new RegExpTree[Lletters.length()];
    for(int i=0;i<Lletters.length();i++) {
      children[i]=new Letter(Lletters.charAt(i));
    }
  }
  public String toString() {
	return letters;
  }
}
