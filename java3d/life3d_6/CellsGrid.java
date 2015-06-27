package java3d.life3d_6;

// CellsGrid.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* CellsGrid manages a GRID_LEN * GRID_LEN * GRID_LEN grid of
   Cell objects centered at (0,0,0) in the scene.

   TimeBehavior periodically calls CellsGrid's update() method
   to update the grid. An update will either trigger a state change 
   or a visual change. 

   A state change changes the grid's cells state by applying
   either rules loaded from a script, or predefined birth and 
   die ranges. 

   The rules are coded in Javascript, and are loaded and compiled
   for extra speed. The user can modify the rules at run time via
   the a desktop popup menu. When the rules are saved, this triggers
   their reloading (and recompilation).

   The birth and die ranges are a 3D version of the rules used 
   in Conway's Game of Life and other cellular automata.

   Timing code shows that the compiled script rules are about 10x
   slower than using birth and die ranges (i.e. ~60 ms per update
   versus ~6 ms).

   Most updates trigger visual changes to the cells, which
   affect their visibility and colour. A visual transition
   is spread out over MAX_TRANS updates.

   Every update causes the grid to rotate by ROTATE_AMT radians, 
   although the rotation axis is periodically changed, so the 
   grid moves in a random way.

   This version of Life3D does not use a properties configuration
   file. So the birth and die ranges are fixed. However, the 
   rotation amount can be varied at runtime, via the appl. popup
   menu.
*/

import java.util.*;
import java.io.*;
import java.nio.channels.*;

import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import javax.script.*;



public class CellsGrid
{
  // grid rotation amount 
  private static final double ROTATE_AMT = Math.toRadians(4);  // 4 degrees

  // number of updates used to complete a visual transition (used by Cell class)
  public static final int MAX_TRANS = 8;

  // number of cells along the x-, y-, and z- axes
  private final static int GRID_LEN = 10;  

  private static final int NUM_NEIGHBOURS = 26;
    /* A cell has 26 neighbours. A cell at a a grid edge uses the
       cells at the opposite edge as neighbours. */

  private static final String SCRIPT_FNM = "rules.js";  // holds the life rules


  // storage for the cells making up the grid
  private Cell[][][] cells;

  // reusable Transform3D object
  private Transform3D t3d = new Transform3D(); 
  private Transform3D rotT3d = new Transform3D();

  private TransformGroup baseTG;   // used to rotate the grid
  private double turnAngle = ROTATE_AMT;
  private int turnAxis = 0;

  // transition (transparency/colour change) step counter
  private int transCounter = 0;

  // birth and die ranges used in the life rules
  boolean[] birthRange, dieRange;

  private Random rand = new Random();

  // used for scripting
  private boolean usingScript;
  private File scriptFile;
  private long lastModified = 0;

  private ScriptEngine engine;
  private Compilable compEngine;
  private CompiledScript lifeScript = null;

  private boolean[] states;

  // used in the timing code
  private long totalTime = 0;
  private long numUpdates = 0;


  public CellsGrid()
  /* The grid (3D array) of Cells is created, and are connected
     to a baseTG TransformGroup. When baseTG is rotated at run 
     time, the entire grid moves. 
  */
  { 
    // will the rules come from a script or be predefined ranges?
    usingScript = hasScriptFile(SCRIPT_FNM);
    if (usingScript)
      initScripting();
    else
      initRanges();

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
				new Cell(i-GRID_LEN/2, j-GRID_LEN/2, k-GRID_LEN/2);
          baseTG.addChild( cells[i][j][k].getTG() );  // connect cell to baseTG
        }
  }  // end of CellsGrid()



  private void initRanges()
  /* the default birth range is 5;
     the dfault die ranges are 3 4 5 6;
  */
  {
    System.out.println("Using default birth and die ranges");

    birthRange = new boolean[NUM_NEIGHBOURS+1];
    dieRange = new boolean[NUM_NEIGHBOURS+1];
               // don't use 0th cell

    for (int i=0; i <= NUM_NEIGHBOURS; i++) {
      birthRange[i] = false;
      dieRange[i] = false;
    }

    // switch default values to true
    birthRange[5] = true;

    dieRange[3] = true;
    dieRange[4] = true;
    dieRange[5] = true;
    dieRange[6] = true;
  }  // end of initRanges()


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
    long startTime = System.nanoTime();

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

    totalTime += (System.nanoTime() - startTime);
    numUpdates++;
  }  // end of update()


  public void reportStats()
  // called from Life3D at termination time, via WrapLife3D
  {  
    int avgTime = (int) ((totalTime/numUpdates)/1000000);  // in ms
    System.out.println("Average update time: " + avgTime + 
                       "ms;  no. updates: " + numUpdates);
  }


  private void stateChange()
  /* A two phase operation: first calculate the next life state
     for each cell, then update the cells. 

     The next life state may be obtained using the script rules
     or using predefined birth and die ranges.
  */
  {
    boolean willLive;

    // calculate next state for each cell
    if (!usingScript) {   // using ranges
      for (int i=0; i < GRID_LEN; i++)
        for (int j=0; j < GRID_LEN; j++)
          for (int k=0; k < GRID_LEN; k++) {
            willLive = aliveNextState(i, j, k);
            cells[i][j][k].newAliveState(willLive);
          }
    }
    else {  // using a script
      if (isScriptModified())
        loadCompileScript(SCRIPT_FNM);   // reload it if it's been modified

      for (int i=0; i < GRID_LEN; i++)
        for (int j=0; j < GRID_LEN; j++)
          for (int k=0; k < GRID_LEN; k++) {
            willLive = aliveScript(i, j, k);
            cells[i][j][k].newAliveState(willLive);
          }
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
    // look at all the neighbours, but not the cell itself
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

    // ** Life Rules **: adjust the cell's life state
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
  /* rotate the object turnAngle radians around an axis;
     turnAngle may change */
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


  // ---------------- called from popup menu -----------------------


  public void adjustSpeed(String speedStr)
  /* A faster speed property is converted into a larger 
     rotation angle, which makes the grid turn faster at
     run time. */
  {
    if (speedStr.equals("Slow"))
      turnAngle = ROTATE_AMT/4;
    else if (speedStr.equals("Medium"))
      turnAngle = ROTATE_AMT/2;
    else  // fast --> large rotation
      turnAngle = ROTATE_AMT;
  } // end of adjustSpeed()


  // ----------------------- scripting --------------------

  private boolean hasScriptFile(String fnm)
  // check if the script file, fnm, is available
  {
    scriptFile = null;
    try {
      scriptFile = new File(fnm);
    }
    catch (NullPointerException e) {  
      System.out.println("Could not access " + fnm);
      return false;
    }
    if (!scriptFile.exists()) {
      System.out.println("No script file " + fnm);
      return false;
    }
    return true;
  }  // end of hasScriptFile()


  private void initScripting()
  /* Initialize the states[] array which will be reused later
     to hold state info for the neighbours of cells.

     Also initialize a scripting engine suitable for compiling
     scripts written using JavaScript.
  */
  {
    // state array used by the scripting rules
    states = new boolean[NUM_NEIGHBOURS+1];   // includes self state
    for (int i=0; i <= NUM_NEIGHBOURS; i++)
      states[i] = false;


    // create a script engine manager
    ScriptEngineManager factory = new ScriptEngineManager();
    if (factory == null) {
      System.out.println("Could not create script engine manager");
      usingScript = false;
      return;
    }

    // create JavaScript engine
    engine = factory.getEngineByName("js");
    if (engine == null) {
      System.out.println("Could not create javascript engine");
      usingScript = false;
      return;
    }

    // create compilable engine
    compEngine = (Compilable) engine;
    if (compEngine == null) {
      System.out.println("Could not create a compilable javascript engine");
      usingScript = false;
      return;
    }

    // add reference to states[] array to engine
    engine.put("states", states);

  } // end of initScripting()


  private boolean isScriptModified()
  // has the script file been modified
  {
    long modTime = scriptFile.lastModified();
    if (modTime > lastModified) {
      lastModified = modTime;
      return true;
    }
    return false;
  }  // end of isScriptModified()



  private void loadCompileScript(String fnm)
  // (re-)load and compile the script in fnm
  {
    System.out.println("Loading script from " + fnm);
    lifeScript = null;

    try {
      FileReader fr = new FileReader(fnm);
      lifeScript = compEngine.compile(fr); 
      // lifeScript = compEngine.compile("if (states[4]) beBorn = true;");
      fr.close();
    }
    catch(FileNotFoundException e)
    {  System.out.println("Could not find " + fnm);  }
    catch(IOException e)
    {  System.out.println("Could not read " + fnm);  }
    catch(ScriptException e)
    {  System.out.println("Problem compiling script in " + fnm);  }
    catch(NullPointerException e)
    {  System.out.println("Problem reading script in " + fnm);  }
    // catch(Exception e)
    // {  System.out.println("Problem with script in " + fnm);  }

  } // end of loadCompileScript()


  private boolean aliveScript(int i, int j, int k)
  /* The script is given two inputs:
      * the states[] array which holds the life state values for
        all the cell's neighbours, _and_ the cell itself.

      * numberLiving -- the number of life states in states[]
        which are true.

     The script calculates boolean values for 
     beBorn and toDie, and their values are copied from
     the script at the end of its execution.

     beBorn and toDie and the cell's current state are used
     to calculate the next state for the cell
  */
  {
   if (lifeScript == null)    // no script so just return current state
     return isAlive(i,j,k); 

    /* collect life states and number of living cells for all 
       the neighbours, _and_ the cell */
    int w = 0;
    int  numberLiving = 0;
    for(int r=i-1; r <= i+1; r++)  // range i-1 to i+1
      for(int s=j-1; s <= j+1; s++)  // range j-1 to j+1
        for(int t=k-1; t <= k+1; t++) {  // range k-1 to k+1
          states[w] = isAlive(r,s,t);
          if (states[w]) 
            numberLiving++;
          w++;
        }

    // store input values in the engine
    // engine.put("states", states);    // no need to update objects
    engine.put("numberLiving", numberLiving);

    // execute the script and get the beBorn and toDie results
    boolean beBorn = false;   // default values
    boolean toDie = false;
    try {
      lifeScript.eval();
      beBorn = (Boolean) engine.get("beBorn");
      toDie = (Boolean) engine.get("toDie");
    }
    catch(ScriptException e)
    {  System.out.println("Error in script execution of " + SCRIPT_FNM);  
       lifeScript  = null;   // to stop this error message appearing multiple times
    }


    // get the cell's current life state
    boolean currAliveState = isAlive(i,j,k);

    // ** Life Rules **: adjust the cell's life state
    if (beBorn && !currAliveState)   // to be born && dead now
      return true;   // make alive
    else if (toDie  && currAliveState)  // to die && alive now
      return false;  // kill off
    else
      return currAliveState;  // no change
  }  // end of aliveScript()


}  // end of CellsGrid class

