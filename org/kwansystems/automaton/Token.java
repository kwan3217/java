package org.kwansystems.automaton;

public class Token {
  public Object type;
  public Object value;
  public Token(Object Ltype, Object Lvalue) {
    type=Ltype;
    value=Lvalue;
  }
  public Token(Object Ltype) {
    this(Ltype,null);
  }
  public String toString() {
    return("Type:   "+type+((value!=null)?"\nValue:  "+value.toString():""));
  }
}
