package org.kwansystems.image.dxt;

import static org.kwansystems.image.dxt.DirectDrawSurface.*;

import java.io.*;

public class DXT45 extends DXT1 {
  public DXT45(InputStream Inf, int Lformat) throws IOException {
    super(Inf,Lformat);
    if(Format==2) multiplyAlpha();
  }
  public void readAlpha(InputStream Inf) throws IOException {
    for(int i=0;i<4;i++) c0=readShort(Inf);
  }
  public void multiplyAlpha() {
    
  }
}
