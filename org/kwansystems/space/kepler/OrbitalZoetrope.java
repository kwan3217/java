package org.kwansystems.space.kepler;

import java.awt.Graphics;
import static java.lang.Math.*;

import org.kwansystems.tools.zoetrope.*;

public class OrbitalZoetrope extends HandleZoetrope {
  private static final long serialVersionUID = 1457222903388635124L;
  protected Object[] EE;
  protected double EccGain;
  
  public OrbitalZoetrope(String LWindowTitle, int LFramePeriodMs, Object[] LEE, double LEccGain) {
    super(LWindowTitle, LFramePeriodMs);
    setElements(LEE, LEccGain);
  }

  protected void FigureOutStuff() {
    if (EccGain < 1)
      EccGain = 1;
    for (Object OE : EE) {
      Elements E=(Elements)OE;
      double thisRmax = E.A * (1 + EccGain * E.E);
      if (thisRmax > getRmax())
        setRmax(thisRmax);
    }
    setRmax(getRmax() * 1.1);
  }

  public void setElements(Object[] LEE, double LEccGain) {
    EE = LEE;
    EccGain = LEccGain;
    setNumFrames(EE.length);
    FigureOutStuff();
  }

  public double Radius(Elements E, double U) {
    double TA = U - E.AP;
    double EE = E.E * EccGain;
    double P = E.A * (1 - EE * EE);
    double R = P / (1 + EE * cos(TA));
    return R;
  }

  protected int X(Elements E, double U) {
    return X(Radius(E, U) * cos(U));
  }

  protected int Y(Elements E, double U) {
    return Y(Radius(E, U) * sin(U));
  }

  protected void paintFrame(Graphics G) {
    // Draw all previous actual locations
    G.setColor(colors[0]);
    for (int i = 0; i <= CurrentFrame; i++) {
      Elements E = (Elements)EE[i];
      int thisX = X(E, E.U);
      int thisY = Y(E, E.U);
      G.drawLine(thisX - 2, thisY - 2, thisX + 2, thisY + 2);
      G.drawLine(thisX - 2, thisY + 2, thisX + 2, thisY - 2);
    }
    Elements E = (Elements)EE[CurrentFrame];
    G.drawLine(X(0), Y(0), X(E, E.U), Y(E, E.U));
    // Draw circle with radius E.A
    G.setColor(colors[7]);
    G.drawOval(X(-E.A), Y(-E.A), X(E.A) - X(-E.A), Y(E.A) - Y(-E.A));
    G.setColor(colors[2]);
    int x1 = X(E, E.AP);
    int y1 = Y(E, E.AP);
    G.drawLine(X(0), Y(0), x1, y1);
    x1 = X(E, E.AP + PI);
    y1 = Y(E, E.AP + PI);
    G.drawOval(x1 - 3, y1 - 3, 7, 7);
    for (int UU = 0; UU < 200 * PI; UU++) {
      double U = UU / 100.0;
      int x0 = X(E, U - 0.01);
      int y0 = Y(E, U - 0.01);
      x1 = X(E, U);
      y1 = Y(E, U);
      G.drawLine(x0, y0, x1, y1);
    }
    G.setColor(colors[0]);
    drawHandles(G);
  }

}
