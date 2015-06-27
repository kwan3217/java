package org.kwansystems.emulator.postscript.painter;

import org.kwansystems.emulator.postscript.*;

public interface Painter {
  public void stroke(GraphicsState G);
  public void fill(GraphicsState G);
  public void showpage(GraphicsState G);
  public void done(GraphicsState G);
  public void initclip(GraphicsState G);
  public void initctm(GraphicsState G);
}
