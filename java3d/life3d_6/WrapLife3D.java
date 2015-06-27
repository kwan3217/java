package java3d.life3d_6;

// WrapLife3D.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* WrapLife3D creates a rotating grid of cells using the CellsGrid
   class (see the addGrid() method). The grid is updated using
   a TimeBehavior object to trigger it into life every TIME_DELAY ms.

   The size of the panel is PWIDTH*PHEIGHT, with a blue
   background colour. This version of Life3D does not use a
   properties configuration file.

   The application terminates when the panel detects the pressing of 
   esc, q, end, or ctrl-c. The camera can be panned, rotated, and zoomed
   via mouse controls offered by Java 3D's OrbitBehavior class.

   The scene can be changed at runrime, by the user changing the grid's
   rotation speeda nd/or background colour via the application's popup
   menu in the system tray.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.vp.*;


public class WrapLife3D extends JPanel 
// Holds the 3D canvas where the loaded image is displayed
{
  private static final int PWIDTH = 512;   // size of panel
  private static final int PHEIGHT = 512; 

  private static final int BOUNDSIZE = 100;  // larger than world

  private static final Point3d USERPOSN = new Point3d(-2,5,10);
    // initial user position

  // time delay (in ms) to regulate speed of animation
  private static final int TIME_DELAY = 60;   // was 50


  private SimpleUniverse su;
  private BranchGroup sceneBG;
  private BoundingSphere bounds;   // for environment nodes

  private Background back;

  private CellsGrid cellsGrid;

  private Life3D topLevel;   // required at quit time


  public WrapLife3D(Life3D top)
  // construct the 3D canvas
  {
    topLevel = top;

    setLayout( new BorderLayout() );
    setOpaque( false );
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

    GraphicsConfiguration config =
					SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas3D = new Canvas3D(config);
    add("Center", canvas3D);

    canvas3D.setFocusable(true);
    canvas3D.requestFocus();    // the canvas now has focus, so receives key events

	canvas3D.addKeyListener( new KeyAdapter() {
	// listen for esc, q, end, ctrl-c on the canvas to
	// allow a convenient exit from the full screen configuration
       public void keyPressed(KeyEvent e)
       { int keyCode = e.getKeyCode();
         if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
             (keyCode == KeyEvent.VK_END) ||
             ((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
           topLevel.finishOff();
         }
       }
     });

    su = new SimpleUniverse(canvas3D);

    createSceneGraph();

    initUserPosition();        // set user's viewpoint
    orbitControls(canvas3D);   // controls for moving the viewpoint

    // depth-sort transparent objects on a per-geometry basis
    View view = su.getViewer().getView();
    view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

    su.addBranchGraph(sceneBG);
  } // end of WrapLife3D()



  private void createSceneGraph()
  {
    sceneBG = new BranchGroup();
    bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE); 

    lightScene();       // add the lights
    addBackground();    // add the sky
    addGrid();          // add cells grid

    sceneBG.compile();   // fix the scene
  }  // end of createSceneGraph()



  private void lightScene()
  /* One ambient light, 2 directional lights */
  {
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    // Set up the ambient light
    AmbientLight ambientLightNode = new AmbientLight(white);
    ambientLightNode.setInfluencingBounds(bounds);
    sceneBG.addChild(ambientLightNode);

    // Set up the directional lights
    Vector3f light1Direction  = new Vector3f(-20.0f, 20.0f, -20.0f);
    Vector3f light2Direction  = new Vector3f(20.0f, 20.0f, 20.0f);

    DirectionalLight light1 = 
            new DirectionalLight(white, light1Direction);
    light1.setInfluencingBounds(bounds);
    sceneBG.addChild(light1);

    DirectionalLight light2 = 
        new DirectionalLight(white, light2Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light2);
  }  // end of lightScene()



  private void addBackground()
  // A blue sky which can change colour
  { 
    back = new Background();
    back.setCapability(Background.ALLOW_COLOR_WRITE);  // so can change at runtime
    back.setApplicationBounds( bounds );
    back.setColor(0.17f, 0.65f, 0.92f);    // sky blue colour
    sceneBG.addChild( back );
  }  // end of addBackground()


  private void orbitControls(Canvas3D c)
  /* OrbitBehaviour allows the user to rotate around the scene, and to
     zoom in and out.
  */
  {
    OrbitBehavior orbit = 
		new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
    orbit.setSchedulingBounds(bounds);

    ViewingPlatform vp = su.getViewingPlatform();
    vp.setViewPlatformBehavior(orbit);	    
  }  // end of orbitControls()



  private void initUserPosition()
  /* Set the user's initial viewpoint using lookAt()  */
  {
    ViewingPlatform vp = su.getViewingPlatform();
    TransformGroup steerTG = vp.getViewPlatformTransform();

    Transform3D t3d = new Transform3D( );
    steerTG.getTransform( t3d );

    // args are: viewer posn, where looking, up direction
    t3d.lookAt( USERPOSN, new Point3d(0,0,0), new Vector3d(0,1,0));
    t3d.invert();

    steerTG.setTransform(t3d);
  }  // end of initUserPosition()


// ------------------------ cells grid -------------------------

  private void addGrid()
  /*  Create the cells grid and a time behaviour to update
      it at TIME_DELAY intervals. */
  {
    cellsGrid = new CellsGrid();
    sceneBG.addChild( cellsGrid.getBaseTG() );

    TimeBehavior tb = new TimeBehavior(TIME_DELAY, cellsGrid); 
    tb.setSchedulingBounds(bounds);
    sceneBG.addChild(tb);
  }  // end of addGrid()


  // -------------- methods called from the popup menu  ------------------


  public void adjustSpeed(String speedStr)
  // adjust the rotation speed of the grid
  {  cellsGrid.adjustSpeed(speedStr);  } 


  public void adjustColour(String colourStr)
  // change the background colour to blue, green, white, or black
  {
    if (colourStr.equals("Blue"))
      back.setColor(0.17f, 0.65f, 0.92f);    // sky blue colour
    else if (colourStr.equals("Green"))
      back.setColor(0.5f, 1.0f, 0.5f);       // grass colour
    else if (colourStr.equals("White"))
      back.setColor(1.0f, 1.0f, 0.8f);       // off-white
    else   // black by default
      back.setColor(0.0f, 0.0f, 0.0f);       // black
  }  // end of adjustColour()


  public void reportStats()
  // called from Life3D at termination to print update statistics
  {  cellsGrid.reportStats(); }

} // end of WrapLife3D class