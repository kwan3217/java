package org.kwansystems.algebra;

import java.util.*;

public class Variable extends DerivativeNode {
  private final String varName;
  public Variable(String LvarName) {
    varName=LvarName;
  }
  @Override
  public DerivativeNode derivative(String respectTo) {
    if(respectTo.equals(varName)) return new Constant(1);
    return new Constant(0);
  }
  @Override
  public double evaluate(Map<String,Double> varValues) {
    if(!varValues.containsKey(varName)) throw new IllegalArgumentException("Variable "+varName+" not found in value table");
    return varValues.get(varName);
  }
  @Override
  public String toString() {
    return new String(varName);
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    System.out.println(V);
    System.out.println(V.derivative("V"));
    System.out.println(V.derivative("X"));
  }
  public DerivativeNode simplify() {
    return this;
  }
  public int precedence() {
    return 10;
  }
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Variable)) return false;
    return ((Variable)o).varName.equals(varName);
  }
  @Override
  public int hashCode() {
    return varName.hashCode();
  }
  public String getName() {
    return new String(varName);
  }
}
