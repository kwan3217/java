/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.pov.btg;

import java.io.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;

public class Points extends BTGObject {
  protected Points(int LobjectType, InputStream Inf) throws IOException {
    super(LobjectType,Inf);
  }
}
