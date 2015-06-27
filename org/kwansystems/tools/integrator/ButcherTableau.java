package org.kwansystems.tools.integrator;

public interface ButcherTableau {
  public double[][] getA();
  public double[][] getB();
  public double[] getB(int i);
  public double[] getC();
  public double[] getE();
  public void checkConsistency();
  public String toWiki();
}