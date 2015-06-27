package java3d.life3d;


// CellsGrid.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* CellsGrid manages a GRID_LEN * GRID_LEN * GRID_LEN grid of
   Cell objects centered at (0,0,0) in the scene.

   TimeBehavior periodically calls CellsGrid's update() method
   to update the grid. An update will either trigger a state change 
   or a visual change. 

   A state change changes the grid's
   cells state by applying birth and die ranges. These ranges
   are a 3D version of the rules used in Conway's Game of Life
   and other cellular automata.

   Most updates trigger visual changes to the cells, which
   affect their visibility and colour. A visual transition
   is spread out over MAX_TRANS updates.

   Every update causes the grid to rotate, although the rotation
   axis is periodically changed, so the grid moves in a
   random way.

   The birth and die ranges, and the rotation speed, are specified
   by properties obtained from the lifeProps object. 
*/

import java.util.*;

import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class CellsGrid
{
  // grid rotation amount 
  private static final double ROTATE_AMT = Math.toRadians(4);  // 4 degrees

  // number of updates used to complete a visual transition (used by Cell class)
  public static final int MAX_TRANS = 8;

  // number of cells along the x-, y-, and z- axes
  private final static int GRID_LEN = 10;  

  // storage for the cells making up the grid
  private Cell[][][] cells;

  // reusable Transform3D object
  private Transform3D t3d = new Transform3D(); 
  private Transform3D rotT3d = new Transform3D();

  private TransformGroup baseTG;   // used to rotate the grid
  private double turnAngle;
  private int turnAxis = 0;

  // transition (transparency/colour change) step counter
  private int transCounter = 0;

  private LifeProperties lifeProps;

  // birth and die ranges used in the life rules
  boolean[] birthRange, dieRange;

  private Random rand = new Random();



  public CellsGrid(LifeProperties lps)
  /* The grid (3D array) of Cells is created, and are connected
     to a baseTG TransformGroup. When baseTG is rotated at run 
     time, the entire grid moves. 
  */
  { 
    lifeProps = lps;

    // load birth and die ranges
    birthRange = lifeProps.getBirth();
    dieRange = lifeProps.getDie();

    setTurnAngle();

    /* Allow baseTG to be read and changed at run time (so
       it can be rotated). */
    baseTG = new TransformGroup();   
    baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // initialize the grid with Cell objects
    cells = new Cell[GRID_LEN][GRID_LEN][GRID_LEN];
    for (int i=0; i < GRID_LEN; i++) 
      for (int j=0; j < GRID_LEN; j++)
        for (int k=0; k < GRID_LEN; k++) {
          cells[i][j][k] = 
				new Cell(i-GRID_LEN/2, j-GRID_LEN/2, k-GRID_LEN/2);  // so grid is centered
          baseTG.addChild( cells[i][j][k].getTG() );  // connect cell to baseTG
        }
  }  // end of CellsGrid()


  private void setTurnAngle()
  /* A faster speed property is converted into a larger 
     rotation angle, which makes the grid turn faster at
     tun time. */
  {
    int speed = lifeProps.getSpeed();

    if (speed == LifeProperties.SLOW)
      turnAngle = ROTATE_AMT/4;
    else if (speed == LifeProperties.MEDIUM)
      turnAngle = ROTATE_AMT/2;
    else  // fast --> large rotation
      turnAngle = ROTATE_AMT;
  }  // end of setTurnAngle()


  public TransformGroup getBaseTG()
  {  return baseTG;  } 


  // ------------------------ update the grid ------------------------

  public void update()
  /* An update() call either triggers a state change or a visual
     change. update() is called periodically by TimeBehavior.

     The transCounter is incremented from 0 to MAX_TRANS, then
     repeats. When transCounter is 0, the state of the grid's 
     cells is updated, and the grid's rotation axis changed.

     At other times, the cells' visuals are changed, which may 
     mean their visibility and/or colour changing.

     At every update, the grid is rotated. 
  */
  {
    if (transCounter == 0) {   // time for grid state change
      stateChange();
      turnAxis = rand.nextInt(3);  // change rotation axis
      transCounter = 1;
    }
    else {   // make a visual change
      for (int i=0; i < GRID_LEN; i++)
        for (int j=0; j < GRID_LEN; j++)
          for (int k=0; k < GRID_LEN; k++)
            cells[i][j][k].visualChange(transCounter);

      transCounter++;
      if (transCounter > MAX_TRANS)
        transCounter = 0;   // finished, so reset
    }

    doRotate();   // rotate in every update() call
  }  // end of update()


  private void stateChange()
  /* A two phase operation: first calculate the next life state
     for each cell, then update the cells
  */
  {
    boolean willLive;

    // calculate next state for each cell
    for (int i=0; i < GRID_LEN; i++)
      for (int j=0; j < GRID_LEN; j++)
        for (int k=0; k < GRID_LEN; k++) {
          willLive = aliveNextState(i, j, k);
          cells[i][j][k].newAliveState(willLive);
        }

    // update each cell
    for (int i=0; i < GRID_LEN; i++)
      for (int j=0; j < GRID_LEN; j++)
        for (int k=0; k < GRID_LEN; k++) {
          cells[i][j][k].updateState();
          cells[i][j][k].visualChange(0);
        }
  }  // end of stateChange()


  // ---------------------- life calculations ------------------------


  private boolean aliveNextState(int i, int j, int k)
  /* The life calculation depends on the number of neigbouring cells
     which are currently alive, which is stored in numberLiving.

     The next state for cell[i][j[k] depends on it's current alive
     state and whether numberLiving appears in the birth or die
     ranges. These ranges are specified when Life3D is being 
     configured.
  */
  {
    // count all the living neighbours, but not the cell itself
    int numberLiving = 0;
    for(int r=i-1; r <= i+1; r++)  // range i-1 to i+1
      for(int s=j-1; s <= j+1; s++)  // range j-1 to j+1
        for(int t=k-1; t <= k+1; t++) {  // range k-1 to k+1
          if ((r==i) && (s==j) && (t==k))
            continue;   // skip self
          else if (isAlive(r,s,t))
            numberLiving++;
        }

    // get the cell's current life state
    boolean currAliveState = isAlive(i,j,k);

    // ** Life Rules **: calculate the cell's next life state
    if (birthRange[numberLiving] && !currAliveState)   // to be born && dead now
      return true;   // make alive
    else if (dieRange[numberLiving]  && currAliveState)  // to die && alive now
      return false;  // kill off
    else
      return currAliveState;  // no change
  }  // end of aliveNextState()


  private boolean isAlive(int i, int j, int k)
  {
    // deal with edge cases for cells array
    i = rangeCorrect(i);
    j = rangeCorrect(j);
    k = rangeCorrect(k);
    return  cells[i][j][k].isAlive();
  }  // end of isAlive()


  private int rangeCorrect(int index)
  /* if the cell index is out of range then use the index of
     the opposite edge */
  {
    if (index < 0)
      return (GRID_LEN + index);
    else if (index > GRID_LEN-1)
      return (index - GRID_LEN);
    else // make no change
      return index;
  }  // end of rangeCorrect()


  // ------------------- rotation ------------------------------


  private void doRotate()
  // rotate the object turnAngle radians around an axis
  {
    baseTG.getTransform(t3d);  // get current rotation
    rotT3d.setIdentity();      // reset the rotation transform object

    switch (turnAxis) {    // set the rotation based on the current axis
      case 0: rotT3d.rotX(turnAngle); break;
      case 1: rotT3d.rotY(turnAngle); break;
      case 2: rotT3d.rotZ(turnAngle); break;
      default: System.out.println("Unknown axis of rotation"); break;
    }

    t3d.mul(rotT3d);            // 'add' new rotation to current one
    baseTG.setTransform(t3d);   // update the TG
  }  // end of doRotate()


}  // end of CellsGrid class

