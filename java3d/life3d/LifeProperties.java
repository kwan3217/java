package java3d.life3d;


// LifeProperties.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* LifePropeties acts as an interface to the properties file,
   PROP_FNM, which stores key=value pairs used by the Life3D
   application. The values are set/changed by the
   Life3DControls class, and used by WrapLife3D.

   Seven properties are stored:
     * fullscreen: whether the Life3D appl is full-screen

     * width, height: the dimensions of the window if not full-screen;
                      the dimensions must be between MIN_LEN up to 
                      the screen size. PWIDTH and PHEIGHT are the defaults.

     * bgColour: the background colour used in Life3D;
                 it may be blue, green, white, or black;
                 blue is the default.

     * speed: the speed that the balls grid rotates;
              it may be slow, medium, or fast;
              fast is the default

     * the birth and die ranges used by the Life rules in CellsGrid. 
       A range is a series of numbers separated by spaces, each 
       number representing the number of neighbours that will trigger
       the cell's birth/death.
       The defaults are BIRTH_RANGE and DIE_RANGE.
*/

import java.awt.*;
import java.util.*;
import java.io.*;


public class LifeProperties
{
  // speed constants
  public static final int SLOW = 0;
  public static final int MEDIUM = 1;
  public static final int FAST = 2;

  // background colour constants
  public static final int BLUE = 0;
  public static final int GREEN = 1;
  public static final int WHITE = 2;
  public static final int BLACK = 3;

  public static final String[] bgColours =
                  {"blue", "green", "white", "black"};   
                  // same order as the colour constants

  // default size of Life3D window
  private static final int PWIDTH = 512; 
  private static final int PHEIGHT = 512; 
  private static final int MIN_LEN = 50;  // minimum for width and height

  // default birth and die ranges
  private static final String BIRTH_RANGE = "5";
  private static final String DIE_RANGE = "3 4 5 6";

  private static final int NUM_NEIGHBOURS = 26;   // for a cell

  private static final String PROP_FNM = "life3DProps.txt";
   // where the property key=value pairs are stored


  private Properties life3DProps;
  private Dimension screenDim;   // size of the screen


  public LifeProperties()
  {
    loadProperties();
    listProperties();
    screenDim = Toolkit.getDefaultToolkit().getScreenSize();
  }  // end of LifeProperties


  private void loadProperties()
  // load the properties from the PROP_FNM file
  {
    life3DProps = new Properties();
	try {
      FileInputStream in = new FileInputStream(PROP_FNM);
      life3DProps.load(in);
      in.close();
	  System.out.println("Loaded properties from " + PROP_FNM);
	} 
    catch (IOException e) {
	  System.out.println("Could not load properties from " + PROP_FNM);
	}
  }  // end of loadProperties()


  private void listProperties()
  {
    if (life3DProps.isEmpty())
      System.out.println("No properties in " + PROP_FNM);
    else {
      System.out.println("Properties in " + PROP_FNM);
      life3DProps.list(System.out);
    }
  } // end of listProperties()


  // -------------------- speed -------------------------------

  public int getSpeed()
  /* return the speed string value (slow, medium, or fast)
     as a constant (SLOW, MEDIUM, or FAST); the default is
     FAST */
  {
    String prop;
	if ((prop = life3DProps.getProperty("speed")) != null) {
      if (prop.equals("slow"))
        return SLOW;
      else if (prop.equals("medium"))
        return MEDIUM;
      else if (prop.equals("fast"))
        return FAST;
      else {
        System.out.println("speed value " + prop + " incorrect; using fast");
        return FAST;
      }
    }
    else {
      System.out.println("No speed property found; using fast");
      return FAST;
    }
  }  // end of getSpeed()


  public void setSpeed(int speed)
  /* store the speed constant (SLOW, MEDIUM, or FAST) as a string  
    (slow, medium, or fast); the default is fast */
  {
    switch (speed) { 
      case SLOW: life3DProps.setProperty("speed", "slow"); break;
      case MEDIUM: life3DProps.setProperty("speed", "medium"); break;
      case FAST: life3DProps.setProperty("speed", "fast"); break;
      default: System.out.println("Did not understand speed setting, using fast");
               life3DProps.setProperty("speed", "fast"); break;
    }
  }  // end of setSpeed()


  // ------------------- background colour ------------------------


  public int getBGColour()
  /* return the background colour string value (blue, green, white, or black)
     as a constant (BLUE, GREEN, WHITE, or BLACK); the default is BLUE */
  {
    String prop;
	if ((prop = life3DProps.getProperty("bgColour")) != null) {
      if (prop.equals("blue"))
        return BLUE;
      else if (prop.equals("green"))
        return GREEN;
      else if (prop.equals("white"))
        return WHITE;
      else if (prop.equals("black"))
        return BLACK;
      else {
        System.out.println("bgColour value " + prop + " incorrect; using blue");
        return BLUE;
      }
    }
    else {
      System.out.println("No bgColour property found; using blue");
      return BLUE;
    }
  }  // end of getBGColour()


  public void setBGColour(int col)
  /* store the background colour constant (BLUE, GREEN, BLACK, or WHITE) 
    as a string (blue, green, white, or black); the default is blue */
  {
    switch (col) { 
      case BLUE: life3DProps.setProperty("bgColour", "blue"); break;
      case GREEN: life3DProps.setProperty("bgColour", "green"); break;
      case BLACK: life3DProps.setProperty("bgColour", "black"); break;
      case WHITE: life3DProps.setProperty("bgColour", "white"); break;
      default: System.out.println("Did not understand bgColour setting, using blue");
               life3DProps.setProperty("bgColour", "blue"); break;
    }
  }  // end of setBGColour()


  // ----------------------- full-screen ----------------------------

  public boolean isFullScreen()
  /* return the fullscreen string value (true or false)
     as a boolean; the default is false */
  {
    String prop;
	if ((prop = life3DProps.getProperty("fullscreen")) != null)
      return Boolean.valueOf(prop);
    else {
      System.out.println("No fullscreen property found; using false");
      return false;
    }
  }  // end of isFullScreen()


  public void setFullScreen(boolean b)
  /* store the fullscreen boolean as a string */
  {
    life3DProps.setProperty("fullscreen", Boolean.toString(b));
  }  // end of setFullScreen()


  // ---------------------------- window width -----------------------

  public int getWidth()
  /* return the appl. window width string value (an integer)
     as a valid integer; the default is PWIDTH */
  {
    String prop;
	if ((prop = life3DProps.getProperty("width")) != null)
      return checkWidth(prop);
    else {
      System.out.println("No width property found; using " + PWIDTH);
      return PWIDTH;
    }
  }  // end of assignWidth()


  private int checkWidth(String widthStr)
  /* the width string must be an integer between MIN_LEN and the 
     screen's width */
  {
    int width = extractInt(widthStr);
    if (width < MIN_LEN) {
      System.out.println("width too small; set to " + MIN_LEN);
      return MIN_LEN;
    }
    else if (width > screenDim.width) {
      System.out.println("width too large; set to " + screenDim.width);
      return screenDim.width;
    }
    return width;
  }  // end of checkWidth()


  private int extractInt(String str)
  /* convert the string to an integer; the default value is -1 */
  {
    int num = -1;
    try {
      num = Integer.parseInt(str);
    }
    catch(NumberFormatException ex) {
      System.out.println("Error: " + str + " was not an integer");
    }
    return num;
  }  // end of extractInt()


  public void setWidth(String widthStr)
  /* store the appl. window width string (an integer)
     as a string. Check that the integer is valid. */
  {
    int width = checkWidth(widthStr);
    life3DProps.setProperty("width", ""+width);
  }  // end of setWidth()


  // ---------------------------- window height -----------------------

  public int getHeight()
  /* return the appl. window height string value (an integer)
     as a valid integer; the default is PHEIGHT */
  {
    String prop;
	if ((prop = life3DProps.getProperty("height")) != null) {
      return checkHeight(prop);
    }
    else {
      System.out.println("No height property found; using " + PHEIGHT);
      return PHEIGHT;
    }
  }  // end of getHeight()


  private int checkHeight(String heightStr)
  /* the height string must be an integer between MIN_LEN and the 
     screen's height */
  {
    int height = extractInt(heightStr);
    if (height < MIN_LEN) {
      System.out.println("height too small; set to " + MIN_LEN);
      return MIN_LEN;
    }
    else if (height > screenDim.height) {
      System.out.println("height too large; set to " + screenDim.height);
      return screenDim.height;
    }
    return height;
  }  // end of checkHeight()


  public void setHeight(String heightStr)
  /* store the appl. window height string (an integer)
     as a string. Check that the integer is valid. */
  {
    int height = checkHeight(heightStr);
    life3DProps.setProperty("height", ""+height);
  }  // end of setHeight()


  // --------------------- birth ranges ---------------------------

  public String getBirthStr()
  /* Return the birth range string as a valid string. The
     validity is done by converting the string to a boolean
     array, and then back to a string. The default is BIRTH_RANGE */
  {  
    String prop;
	if ((prop = life3DProps.getProperty("birth")) != null) {
      boolean[] rangeBools = strToBools(prop);
      return boolsToStr(rangeBools);
    }
    else {
      System.out.println("No birth property found; using " + BIRTH_RANGE);
      return BIRTH_RANGE;
    }
  } // end of getBirthStr()


  private boolean[] strToBools(String rangeStr)
  /* The range string must be a series of integers separated
     by spaces (e.g. "3 5 1 23"). The numbers must be between
     1 and NUM_NEIGHBOURS, otherwise they are ignored.

     A boolean array is created, of size NUM_NEIGHBOURS+1, 
     with true in the cells whose indicies appear in the range
     string. Cell 0 is not used. */
  {
    boolean [] rangeBools = new boolean[NUM_NEIGHBOURS+1];
               // don't use 0th cell
    for (int i=0; i <= NUM_NEIGHBOURS; i++)
       rangeBools[i] = false;

    String[] numStrs = rangeStr.split(" ");
    int num;
    for (int i=0; i < numStrs.length; i++) {
       num = extractInt(numStrs[i]);
       if ((num >= 1) && (num <= NUM_NEIGHBOURS))
         rangeBools[num] = true;
       else
         System.out.println("Ignoring " + num + " since out of range");
    }
    return rangeBools;
  }  // end of strToBools()


  private String boolsToStr(boolean[] rangeBools)
  /* Convert the boolean array into a string of numbers
     separated by spaces. A number appears if the boolean
     array has true in that number's index position. */
  {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i <= NUM_NEIGHBOURS; i++)
      if (rangeBools[i])
        sb.append("" + i + " ");

    return sb.toString();
  }  // end of boolsToStr()


  public boolean[] getBirth()
  /* Return the boolean array representing the birth range string. 
     BIRTH_RANGE is the default result. */
  {  
    String prop;
	if ((prop = life3DProps.getProperty("birth")) != null)
      return strToBools(prop);
    else {
      System.out.println("No birth property found; using " + BIRTH_RANGE);
      return strToBools(BIRTH_RANGE);
    }
  } // end of getBirth()


  public void setBirth(String sStr)
  /* Store the string as a valid birth range string value. The
     validity is done by converting the string to a boolean
     array, and then back to a string. */
  {  
    boolean[] rangeBools = strToBools(sStr);
    life3DProps.setProperty("birth", "" + boolsToStr(rangeBools));
  } // end of setBirth()


  // --------------------- die ranges ---------------------------

  public String getDieStr()
  /* Return the die range string as a valid string. The
     validity is done by converting the string to a boolean
     array, and then back to a string. The default is DIE_RANGE */
  {  
    String prop;
	if ((prop = life3DProps.getProperty("die")) != null) {
      boolean[] rangeBools = strToBools(prop);
      return boolsToStr(rangeBools);
    }
    else {
      System.out.println("No die property found; using " + DIE_RANGE);
      return DIE_RANGE;
    }
  } // end of getDieStr()


  public boolean[] getDie()
  /* Return the boolean array representing the die range string. 
     DIE_RANGE is the default result. */
  {  
    String prop;
	if ((prop = life3DProps.getProperty("die")) != null)
      return strToBools(prop);
    else {
      System.out.println("No die property found; using " + DIE_RANGE);
      return strToBools(DIE_RANGE);
    }
  } // end of getDie()


  public void setDie(String bStr)
  /* Store the string as a valid die range string value. The
     validity is done by converting the string to a boolean
     array, and then back to a string. */
  {  
    boolean[] rangeBools = strToBools(bStr);
    life3DProps.setProperty("die", "" + boolsToStr(rangeBools));
  }


  // --------------------------- saving -----------------------------

  public void saveProperties()
  // save the properties to the PROP_FNM file
  {
    listProperties();

	try {
      FileOutputStream out = new FileOutputStream(PROP_FNM);
      life3DProps.store(out, "Life3D Properties");
      out.close();
	  System.out.println("Saved properties in " + PROP_FNM);
	} 
    catch (IOException e) {
	  System.out.println("Could not save properties in " + PROP_FNM);
	}
  }  // end of saveProperties()


} // end of LifeProperties class
