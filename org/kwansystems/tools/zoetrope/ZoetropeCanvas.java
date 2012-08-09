package org.kwansystems.tools.zoetrope;

import java.awt.*;
import javax.swing.*;

public class ZoetropeCanvas extends JPanel {
  private static final long serialVersionUID=-2526209004182425856L;
  protected Zoetrope ZParent;
  public ZoetropeCanvas(Zoetrope LParent) {
	  ZParent=LParent;
  }
  public void paintComponent(Graphics G) {
	  super.paintComponent(G);
	  ZParent.paintFrame(G,getWidth(),getHeight());
  }
}
