package org.kwansystems.kwire;

import java.awt.*;
import java.util.*;

public class Switch extends Component {
  private boolean state;
  public Switch(int Lx, int Ly, boolean Lstate) {
    super(Lx, Ly);
    state=Lstate;
    outputs.add(new Output(this,10,5));
  }

  @Override
  public void drawCore(Graphics G, int xofs, int yofs) {
    if((Boolean)outputs.get(0).lastTransmit) {
      G.setColor(Color.RED);
    } else {
      G.setColor(Color.BLACK);
    }
    G.drawRect(x+xofs, y+yofs, 10, 10);
  }
  @Override
  public Object[] execute() {
    return new Object[] {new Boolean(state)};
  }
  public void turnOn() {
    state=true;
  }
  public void turnOff() {
    state=false;
  }
  public void toggle() {
    state=!state;
  }
  public void set(boolean Lstate) {
    state=Lstate;
  }

}
