package org.kwansystems.pov.mesh;

import java.util.*;


public class PointKeeper {
  public Map<Point,PointKeeperNode> A;
  public List<Point> B;
  public PointKeeper() {
    A=new HashMap<Point,PointKeeperNode>();
    B=new ArrayList<Point>();
  }
  public int add(Point o,Triangle T) {
    if(A.containsKey(o)) {
      PointKeeperNode PKN=A.get(o);
      PKN.add(T);
      return(PKN.Index);
    } else {
      B.add(o);
      PointKeeperNode PKN=new PointKeeperNode(B.size()-1);
      PKN.add(T);
      A.put(o,PKN);
      return B.size()-1;
    }
  }
  public Point get(int I) {
    return B.get(I);
  }
  public PointKeeperNode getNode(int I) {
    return A.get(B.get(I));
  }
  public Point[] toArray() {
    return B.toArray(new Point[0]);
  }
}
