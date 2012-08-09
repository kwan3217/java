/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kwansystems;

/**
 *
 * @author chrisj
 */
public class Paradox3 {
  public static boolean check(int[] Clock, int Start, int Branches, int[] Path) {
    boolean[] Mark=new boolean[Clock.length];
    int here=Start;
    for (int i=0;i<Clock.length;i++) {
      if(Mark[here]) return false;
      Path[i]=here;
      Mark[here]=true;
      int jump=Clock[here];
      if((Branches & 1)!=0) jump=-jump;
      here+=jump;
      if(here<0) here+=Clock.length;
      here=here % Clock.length;
      Branches=Branches/2;
    }
    return true;
  }
  
  public static void main(String[] args) {
    int[] Clock=new int[] {3,5,3,5,2,1,5,3,2,3,3};
    int[] Path=new int[Clock.length]; 
    int acc=0;
    for(int start=0;start<Clock.length;start++) {
      for(int branches=0;branches<(1 << Clock.length);branches++) {
        acc++;
        if(check(Clock,start,branches,Path)) {
          System.out.println("Found a path");
          for(int step=0;step<Path.length;step++) {
            System.out.printf("Step %2d: %2d (%2d)\n",step+1,Path[step],Clock[Path[step]]);
          }
          System.out.printf("Checked %d possibilities\n",acc);
          System.exit(0);
        }
      }
    }
  }
}
