package java3d.life3d_6;

// ClockAnimation.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Show an animated clock over the top of the splashscreen
   until the real application starts. The clock is actually
   NUM_CLOCKS images, which are shown in a cycle until the 
   splash is turned invisible (by the main application starting),
   or MAX_REPEATS is reached.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;


public class ClockAnimation extends Thread
{
  private static final int MAX_REPEATS = 100;
  private static final int TIME_DELAY = 250;   // ms

  private static final int NUM_CLOCKS = 8;


  private ImageIcon clockImages[];  // stores the clock images
  private int currClock = 0;
  private int clockWidth, clockHeight;
  private int offsetX, offsetY;  // offset of images from bottom right corner


  public ClockAnimation(int oX, int oY)
  {
    offsetX = oX; offsetY = oY;

    // load the clock images
    clockImages = new ImageIcon[NUM_CLOCKS];
    for (int i = 0; i < NUM_CLOCKS; i++) 
      clockImages[i] = new ImageIcon(
           getClass().getResource("clocks/clock" + i + ".gif"));

    // get clock dimensions; assume all images are same size
    clockWidth = clockImages[0].getIconWidth();
    clockHeight = clockImages[0].getIconHeight();
  }  // end of ClockAnimation()


  public void run()
  {
    // get a reference to the splash
    SplashScreen splash = SplashScreen.getSplashScreen();
    if (splash == null) {
      System.out.println("No splashscreen found");
      return;
    }

    // get a reference to the splash's drawing surface
    Graphics2D g = splash.createGraphics();
    if (g == null) {
      System.out.println("No graphics context for splash");
      return;
    }

    /* calculate a (x,y) position for the clock images near the 
       the bottom right of the splash image. */
    Dimension splashDim = splash.getSize();
    int xPosn = splashDim.width - clockWidth - offsetX;
    int yPosn = splashDim.height - clockHeight - offsetY;

    // start cycling through the images
    boolean splashVisible = true;
    for (int i = 0; ((i < MAX_REPEATS) && splashVisible); i++) {
      clockImages[currClock].paintIcon(null, g, xPosn, yPosn);
      currClock = (currClock + 1) % NUM_CLOCKS;

      // only update the splash if it's visible
      if (splash.isVisible())   // will turn invisible when the appl. starts
        splash.update();
      else
        splashVisible = false;

      try {
        Thread.sleep(TIME_DELAY);
      }
      catch (InterruptedException e) {}
    }
  }  // end of run()

}  // end of ClockAnimation class
