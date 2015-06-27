package org.kwansystems.space.windtunnel;

import org.kwansystems.planet.*;
import org.kwansystems.vector.*;

public abstract class ForceMomentGenerator {
  public abstract ForceMoment getForceMoment(Planet E, MathState X);
}
