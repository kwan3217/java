package org.kwansystems.algebra;

import java.util.*;

public abstract class DerivativeNode {
  public static boolean inJava=false;
  public abstract double evaluate(Map<String,Double> varValues);
  public double evaluate(String[] varNames, double[] varValues) {
    Map<String,Double> m=new LinkedHashMap<String,Double>();
    for(int i=0;i<varNames.length;i++) m.put(varNames[i], varValues[i]);
    return evaluate(m);
  }
  public double evaluate(String varName, double varValue) {
    Map<String,Double> m=new LinkedHashMap<String,Double>();
    m.put(varName, varValue);
    return evaluate(m);
  }
  public abstract DerivativeNode derivative(String respectTo);
  public abstract DerivativeNode simplify();
  public abstract int precedence();
}
