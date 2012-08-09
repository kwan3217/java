package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.*;

public abstract class DerivativeSet {
  public abstract MathVector dxdt(double T, MathVector X, boolean IsMajor);
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException E) {return null;}
  }
}
