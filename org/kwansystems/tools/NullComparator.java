package org.kwansystems.tools;

import java.io.*;
import java.util.*;

public class NullComparator implements Comparator<Object>, Serializable {
  public static final NullComparator NC=new NullComparator();
  private NullComparator() {
    super();
  }
  //We need to define a comparator in order to allow nulls.
  //Nulls are last.
  public int compare(Object arg0, Object arg1) {
    if(arg0==null && arg1==null) return 0;
    if(arg0==null) return 1;
    if(arg1==null) return -1;
    try {
      if(arg0 instanceof Comparable) {
        return ((Comparable<Object>)arg0).compareTo(arg1);
      } else {
        if(arg0.equals(arg1)) return 0;
        if(arg0.hashCode()<arg1.hashCode()) return 1;
        return -1;
      }
    } catch (Throwable e) {
      if(arg0.equals(arg1)) return 0;
      if(arg0.hashCode()<arg1.hashCode()) return 1;
      return -1;
    }
  }
  //We need to define a comparator in order to allow nulls.
  //Nulls are last.
  public boolean equals(Object arg0, Object arg1) {
    if(arg0==null && arg1==null) return true;
    if(arg0==null) return false;
    if(arg1==null) return false;
    return arg0.equals(arg1);
  }
}