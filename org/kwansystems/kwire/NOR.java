package org.kwansystems.kwire;

import java.util.*;
import java.awt.*;

public class NOR extends Component {
  public NOR(int x, int y) {
    super(x,y);
    inputs.add(new Input(0, 5,this,new Boolean(false)));
    inputs.add(new Input(0,15,this,new Boolean(false)));
    outputs.add(new Output(this,20,10));
  }
  @Override
  public Object[] execute() {
    boolean A=((Boolean)inputs.get(0).getState()).booleanValue();
    boolean B=((Boolean)inputs.get(1).getState()).booleanValue();
    return new Object[] {new Boolean(!(A | B))};
  }
  @Override
  public void drawCore(Graphics G, int xofs, int yofs) {
    G.setColor(Color.BLACK);
    G.drawRect(x+xofs, y+yofs, 20, 20);
  }
}
