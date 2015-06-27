package org.kwansystems.pov.mesh;

import java.util.*;


public class PointKeeperNode {
  int Index;
  public ArrayList<Triangle> PointUsers;
  public PointKeeperNode(int LIndex) {
    Index=LIndex;
    PointUsers=new ArrayList<Triangle>();
  }
  public void add(Triangle T) {
    PointUsers.add(T);
  }
}
