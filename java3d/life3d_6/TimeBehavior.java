package java3d.life3d_6;

// TimeBehavior.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Update the cells grid every timeDelay ms 
*/


import java.util.Enumeration;
import javax.media.j3d.*;


public class TimeBehavior extends Behavior
{
  private WakeupCondition timeOut;
  private int timeDelay;
  private CellsGrid cellsGrid;


  public TimeBehavior(int td, CellsGrid cg)
  { 
    timeDelay = td; 
    cellsGrid = cg;
    timeOut = new WakeupOnElapsedTime(timeDelay);
  }


  public void initialize()
  { wakeupOn(timeOut); }


  public void processStimulus(Enumeration criteria)
  {
    cellsGrid.update();      // ignore criteria
    wakeupOn(timeOut);
  }


}  // end of TimeBehavior class
