package org.kwansystems.emulator.MiniLogo;

public interface VMListener {
  public void MoveTo(double x, double y);
  public void LineTo(double x, double y);
  public void SetColor(short C);
  public void finish();
}
