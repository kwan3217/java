package org.kwansystems.emulator.MiniLogo;

import java.awt.*;
import javax.swing.*;

public class TurtleCanvas extends JPanel implements VMListener {
  private static final long serialVersionUID=7120654314953126072L;
  private Graphics G;  
  private VM vm;
  private double lastX,lastY;
  public TurtleCanvas(VM Lvm) {
    vm=Lvm;
    vm.setFace(this);
  }
  public void paintComponent(Graphics LG) {
    super.paintComponent(LG);
    G=LG;
    vm.Interpret();
  }
  private int X(double x) {
    return getWidth()/2+(int)x;
  }
  private int Y(double y) {
    return getHeight()/2-(int)y;
  }
  public void LineTo(double x, double y) {
    G.drawLine(X(lastX), Y(lastY), X(x), Y(y));
    lastX=x;
    lastY=y;
  }
  public void MoveTo(double x, double y) {
    lastX=x;
    lastY=y;
    
  }
  public void SetColor(short C) {
    G.setColor(VM.defaultColorList[C]);
  }
  public void finish() {
    G=null;
  }
}
