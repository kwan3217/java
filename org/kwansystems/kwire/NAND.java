package org.kwansystems.kwire;

import java.awt.*;
import java.util.*;

public class NAND extends Component {
  public NAND(int x, int y) {
    super(x,y);
    inputs.add(new Input(0,2,this,new Boolean(false)));
    inputs.add(new Input(0,7,this,new Boolean(false)));
    outputs.add(new Output(this,10,5));
  }
  @Override
  public Object[] execute() {
    boolean A=((Boolean)inputs.get(0).getState()).booleanValue();
    boolean B=((Boolean)inputs.get(1).getState()).booleanValue();
    return new Object[] {new Boolean(!(A & B))};
  }
  @Override
  public void drawCore(Graphics G, int xofs, int yofs) {
    G.drawRect(x+xofs, y+yofs, 10, 10);
  }
}
