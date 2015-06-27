package org.kwansystems.kwire;

import java.awt.*;

public class NOT extends Component {
  public NOT(int x, int y) {
    super(x,y);
    inputs.add(new Input(0,10,this,new Boolean(false)));
    outputs.add(new Output(this,20,10));
  }
  @Override
  public Object[] execute() {
    boolean A=((Boolean)inputs.get(0).getState()).booleanValue();
    return new Object[] {new Boolean(!A)};
  }
  @Override
  public void drawCore(Graphics G, int xofs, int yofs) {
    G.setColor(Color.BLACK);
    G.drawLine(x+xofs+5, y+yofs,    x+xofs+20, y+yofs+10);
    G.drawLine(x+xofs+5, y+yofs+20, x+xofs+20, y+yofs+10);
    G.drawLine(x+xofs+5, y+yofs, x+xofs+5, y+yofs+20);
    G.drawOval(x+xofs,y+yofs+8,5,5);
  }
}
