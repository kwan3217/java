package java3d.life3d;


// Life3D.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* This JFrame wraps up two different applications.

   When called as:
     > java Life3D

   it displays a rotating 3D grid of cells which obey rules
   inspired by Conway's Game of Life. 

   When called as:
     > java Life3D -edit

   it loads a GUI which allows 7 application properties to
   be viewed and modified:
     * fullscreen: whether the Life3D appl is full-screen

     * width, height: the dimensions of the Life3D appl window 
       if not full-screen

     * bgColour: the background colour used in Life3D;
                 it may be blue, green, white, or black

     * speed: the speed that the balls grid rotates;
              it may be slow, medium, or fast

     * the birth and die ranges used by the Life rules. 

   The application properties are managed by a LifeProperties object
   which stores them as key=value pairs in a local text file.

   The full-screen version of Life3D can be used as a screensaver
   when combined with JScreenSaver's SCR windows application
   by Yoshinori Watanabe (available from 
   http://homepage2.nifty.com/igat/igapyon/soft/jssaver.html
   and http://sourceforge.net/projects/jssaver/).
   It's actually JScreenSaver which requires the
   duel-use interface which is implemented here.

   When in full-screen mode, Life3D is terminated by the user typing
   esc, q, end, or ctrl-c.

   This class checks if Java 3D is available, and displays a
   message if it isn't, rather than trying to start the application.
*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class Life3D extends JFrame
                        implements ActionListener
{

  private JButton visitButton;


  public Life3D(String[] args) 
  {
    super("Life3D");

    LifeProperties lifeProps = new LifeProperties();

    Container c = getContentPane();
    c.setLayout( new BorderLayout() );

    if (args.length > 0 && args[0].equals("-edit")) {
      // view/change appl. properties
      Life3DConfig l3Ctrls = new Life3DConfig(lifeProps);
      c.add(l3Ctrls, BorderLayout.CENTER);
    }
    else if (hasJ3D()) {
      // start the Life3D application
      WrapLife3D w3d = new WrapLife3D(this, lifeProps);
      c.add(w3d, BorderLayout.CENTER);
      if (lifeProps.isFullScreen())
        setUndecorated(true);   // no menu bars, borders
    }
    else
      reportProb(c);

    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    pack();
    setResizable(false);    // fixed size display

    // center this window
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension winDim = this.getSize();
    // System.out.println("Window size: " + winDim);
    this.setLocation( (screenDim.width-winDim.width)/2,
                      (screenDim.height-winDim.height)/2);

    setVisible(true);
  } // end of Life3D()



  private boolean hasJ3D()
  // check if Java 3D is available
  {
    try {   // test for an essential Java 3D class
      Class.forName("com.sun.j3d.utils.universe.SimpleUniverse");
      return true;
    }
    catch(ClassNotFoundException e) {
      System.err.println("Java 3D not installed");
      return false;
    }
  } // end of hasJ3D()


  private void reportProb(Container c)
  /* Report the absence of Java 3D.
     Use a label, and a non-active button to show the Java 3D URL.
     (I'll make the URL active in a later version of this appl.)
  */
  {
    JPanel reportPanel = new JPanel();
    reportPanel.setLayout( new BoxLayout(reportPanel, BoxLayout.Y_AXIS));  // vertical
    c.add(reportPanel, BorderLayout.CENTER);

    String msgText = "<html><font size=+2>" +
                     "Java 3D <font color=red>not</font> installed" +
                     "</font></html>";
    JLabel msgLabel = new JLabel(msgText, SwingConstants.CENTER);
    reportPanel.add(msgLabel);

    String visitText = "<html><font size=+2>" +
                       "Visit https://java3d.dev.java.net/" +
                       "</font></html>";
    visitButton = new JButton(visitText);
    visitButton.addActionListener(this);
    reportPanel.add(visitButton);

  }  // end of reportProb()


  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == visitButton)
      System.out.println("Visit https://java3d.dev.java.net/");
  }  // end of actionPerformed


// -----------------------------------------

  public static void main(String[] args)
  { new Life3D(args);  }


} // end of Life3D class

