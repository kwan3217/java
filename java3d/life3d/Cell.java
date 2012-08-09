package java3d.life3d;


// Cell.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/*  A cell is a coloured ball that may be 'alive' or 'dead'.
    When 'alive' the ball is visible, when 'dead' it's
    invisible. The cell also has an age which is gradually
    incremented from when it is born until it dies, and is
    reset to 0 if the cell is born again.

    CellsGrid can either change a cell's state or its visual
    appearance (its transparency or colour).

    When the cell's state changes from alive to dead it will switch
    it's visual state from VISIBLE to FADE_OUT. Subsequent visual
    changes by CellsGrid trigger a gradually fade out of the cell over 
    CellsGrid.MAX_TRANS updates until it switches its visual state
    from FADE_OUT to INVISIBLE.

    When the cell's state changes from dead to alive it will switch
    it's view state from INVISIBLE to FADE_IN. Subsequent visual
    changes by CellsGrid trigger a gradually fade in of the cell over 
    CellsGrid.MAX_TRANS updates until it switches its visual state
    from FADE_IN to VISIBLE.

    At certain ages the ball will change colour. A colour visual
    change is spread over CellsGrid.MAX_TRANS updates.
*/


import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class Cell
{
  // possible visual states for a cell
  private final static int INVISIBLE = 0;
  private final static int FADE_IN = 1;
  private final static int FADE_OUT = 2;
  private final static int VISIBLE = 3;

  // material colours
  private final static Color3f RED = new Color3f(1.0f, 0.0f, 0.0f);
  private final static Color3f ORANGE = new Color3f(1.0f, 0.5f, 0.0f);
  private final static Color3f YELLOW = new Color3f(1.0f, 1.0f, 0.0f);
  private final static Color3f GREEN = new Color3f(0.0f, 1.0f, 0.0f);
  private final static Color3f BLUE = new Color3f(0.0f, 0.0f, 1.0f);

  private final static Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
  private final static Color3f WHITE = new Color3f(0.9f, 0.9f, 0.9f);

  // length of cell side (== diameter when the cell is a ball)
  private final static float CELL_LEN = 0.5f;  

  // space between cells : a factor multiplied to CELL_LEN
  private final static float SPACING = 1.5f;

  // scene graph elements
  private TransformGroup cellTG;  

  // appearance elements
  private Appearance cellApp;
  private TransparencyAttributes transAtt;
  private Material material;
  private Color3f cellCol, oldCol, newCol;

  // cell state information
  private boolean isAlive, newAliveState;
  private int visualState;
  private int age = 0;
 


  public Cell(int x, int y, int z)
  /*  A cell is a shape below a TransformGroup which is used to
      position the cell in the cells grid. The scene branch is:
             cellTG --> cellShape --> Appearance
                                     {Material,Transparency}
  */
  { 
    isAlive = (Math.random() < 0.1) ? true : false;    
      // it's more likely that a cell is initially dead (invisible)

    // create appearance
    cellApp = new Appearance();
    makeMaterial();
    setVisibility();

    // the cell shape as a cube
    // Box cellShape = new Box( CELL_LEN/2, CELL_LEN/2, CELL_LEN/2, 
	//					Box.GENERATE_NORMALS, cellApp);

    // the cell chape as a sphere
    Sphere cellShape = new Sphere(CELL_LEN/2, Sphere.GENERATE_NORMALS, cellApp); 

    // fix cell's position
    Transform3D t3d = new Transform3D();
    double xPosn = x*CELL_LEN*SPACING; 
    double yPosn = y*CELL_LEN*SPACING; 
    double zPosn = z*CELL_LEN*SPACING; 
    t3d.setTranslation( new Vector3d(xPosn, yPosn, zPosn) );

    // build scene branch
    cellTG = new TransformGroup();
    cellTG.setTransform(t3d);
    cellTG.addChild(cellShape);
  }  // end of Cell()


  private void makeMaterial()
  /* Make a coloured material, which is originally blue. The
     ambient and diffuse components of the material can be changed
     at run time. */
  {
    cellCol = new Color3f();
    oldCol = new Color3f();
    newCol = new Color3f();

    // set material
    material = new Material(WHITE, BLACK, WHITE, WHITE, 100.f);
               // sets ambient, emissive, diffuse, specular, shininess

    material.setCapability(Material.ALLOW_COMPONENT_WRITE);
    material.setLightingEnable(true);
    resetColours();
    cellApp.setMaterial(material);
  }  // end of makeMaterial()


  private void resetColours()
  // intialization of the material's colour to blue
  {
    cellCol.set(BLUE);
    oldCol.set(cellCol);   // blue as well
    newCol.set(cellCol);

    setMatColours(cellCol);
  }  // end of resetColours()


  private void setMatColours(Color3f col)
  // the ambient colour is a darker shade of the diffuse colour
  {
    material.setAmbientColor(col.x/3.0f, col.y/3.0f, col.z/3.0f);
    material.setDiffuseColor(col);
  }  // end of setMatColours()


  private void setVisibility()
  /* A cell's transparency can change at run time, ranging from
     fully opaque when the cell is 'alive' to fully transparent
     when 'dead'. When the cell is coming to life or dieing, its
     transparency setting will be somewhere between these values. */
  {
    // let transparency value change at run time
    transAtt = new TransparencyAttributes();
    transAtt.setTransparencyMode(TransparencyAttributes.BLENDED);
    transAtt.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

    if (isAlive) {
      visualState = VISIBLE;
      transAtt.setTransparency(0.0f);     // opaque
    }
    else  { // dead so invisible
      visualState = INVISIBLE;
      transAtt.setTransparency(1.0f);   // totally transparent
    }

    cellApp.setTransparencyAttributes(transAtt);
  }  // end of setVisibility()


  public TransformGroup getTG()
  // called by CellsGrid
  {  return cellTG;  }


  // ----------- get/set methods for cell's life ---------------------


  public boolean isAlive()
  {  return isAlive;  }

  public void newAliveState(boolean b)
  {  newAliveState = b;  } 


  // -------------------------- state update --------------------------

  public void updateState()
  /* If the cell is coming alive or dieing then its visual state
     must be altered, so it will fade into/out of view.

     If the cell's life state isn't changing, and it's alive,
     then it's colour may change if it's old enough.
  */
  {
    if (isAlive != newAliveState) {  // there's a state change
      if (isAlive && !newAliveState)  // alive --> dead (die)
        visualState = FADE_OUT;   // from VISIBLE      
      else {  // dead --> alive (birth)
        visualState = FADE_IN;    // from INVISIBLE
        age = 0;    // reset age since born again
        resetColours();
      }
    }
    else { // current and new states are the same
      if (isAlive) {   // cell stays alive (survives)
        age++;   // get older
        ageSetColour();
      }
    }
  }  // end of updateState()


  private void ageSetColour()
  // hardwired age values for setting the cell's new colour
  {
    if (age > 16)
      newCol.set(RED);
    else if (age > 8)
      newCol.set(ORANGE);
    else if (age > 4)
      newCol.set(YELLOW);
    else if (age > 2)
      newCol.set(GREEN);
    else
      newCol.set(BLUE);
  }  // end of ageSetColour()


  // -------------------------- visual update --------------------------

  public void visualChange(int transCounter)
  /* A cell is in one of the folowing visual states:
      * FADE_OUT, where the cell gradually disappears;
      * FADE_IN, where the cell gradually apppears;
      * VISIBLE, where the cell's colour may gradually change;
      * INVISIBLE, where nothing happens to the cell's appearance.
  */
  {
    float transFrac = ((float)transCounter)/CellsGrid.MAX_TRANS;

    if(visualState == FADE_OUT) 
      transAtt.setTransparency(transFrac);  // 1.0f is totally transparent   
    else if (visualState == FADE_IN)
      transAtt.setTransparency(1.0f-transFrac);
    else if (visualState == VISIBLE) 
      changeColour(transFrac);
    else if (visualState == INVISIBLE) {}
      // do nothing
    else
      System.out.println("Error in visualState");

    if (transCounter == CellsGrid.MAX_TRANS)
      endVisualTransition();
  }  // end of visualChange()


  private void changeColour(float transFrac)
  /* the current cell's colour is a mix of its old and
     new colours (if the two are different) */
  {
    if (!oldCol.equals(newCol)) {  // if colours are different
      float redFrac = oldCol.x*(1.0f-transFrac) + newCol.x*transFrac;
      float greenFrac = oldCol.y*(1.0f-transFrac) + newCol.y*transFrac;
      float blueFrac = oldCol.z*(1.0f-transFrac) + newCol.z*transFrac;

      cellCol.set(redFrac, greenFrac, blueFrac);
      setMatColours(cellCol);
    }
  }  // end of changeColour()


  private void endVisualTransition()
  /* At the end of a transition, the final colour is
     stored, the new cell's life state is stored, and
     the visual state is changed to VISIBLE or INVISIBLE.
  */
  {
    // store current colour as both the old and new colours;
    // used when fading in and when visible
    oldCol.set(cellCol);
    newCol.set(cellCol);

    isAlive = newAliveState;   // update alive state

    if (visualState == FADE_IN)
      visualState = VISIBLE;
    else if (visualState == FADE_OUT)
      visualState = INVISIBLE;
  }  // end of endVisualTransition()


}  // end of Cell class