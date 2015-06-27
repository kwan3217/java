package org.kwansystems.kwire;

import java.awt.*;
import java.util.*;

public class Light extends Component {
  public Light(int Lx, int Ly) {
    super(Lx, Ly);
    inputs.add(new Input(0,5,this,new Boolean(false)));
  }

  @Override
  public void drawCore(Graphics G, int xofs, int yofs) {
    if((Boolean)inputs.get(0).getState()) {
      G.setColor(Color.RED);
    } else {
      G.setColor(Color.BLACK);
    }
    G.fillOval(x+xofs, y+yofs, 10, 10);
    G.setColor(Color.BLACK);
    G.drawOval(x+xofs, y+yofs, 10, 10);
  }
  @Override
  public Object[] execute() {
    return new Object[] {};
  }

}
