package java3d.life3d_6;

// Life3D.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* This version of Life3D uses four new features of Java 6:
    * splashscreens
    * the system tray API
    * the desktop API
    * scripting integration

   Usage:
     java -splash:lifeSplash.jpg Life3D

   Life3D displays a rotating 3D grid of cells which obey rules
   inspired by Conway's Game of Life. The graphics code is very
   similar to the first version of Life3D.

   This version doesn't use a properties configuration file,
   and cannot be shown full-screen.

   This class checks if Java 3D is available, and displays a
   message if it isn't, rather than trying to start the application.
   In this version, the user can click on the supplied URL
   and a browser loads the Java 3D page named in J3D_URL.

   The opening splashscreen includes an animated clock, that spins
   until the application starts.

   The system tray includes a popup menu which can be used to:
      * iconify/deiconify the application;
      * change the rotation speed of Life3D's grid of cells;
      * change the background colour used in the scene;
      * modify scripting rules that control how the cells change state;
      * view the 'about.txt' file;
      * e-mail the author;
      * exit the application

   The scripting rules are in JavaScript and are reloaded (and compiled)
   whenever they are changed by the user.
*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;


public class Life3D extends JFrame
                        implements ActionListener
{
  private static final String J3D_URL = "https://java3d.dev.java.net/";

  private WrapLife3D w3d = null;
  private Desktop desktop = null;
  private JButton visitButton;


  public Life3D() 
  {
    super("Life3D");

    // start the clock animation that appears with the splashscreen
 //   ClockAnimation ca = new ClockAnimation(7,7);
 //   ca.start();

    // get a desktop ref for using external applications
    if (Desktop.isDesktopSupported())
      desktop = Desktop.getDesktop();


    Container c = getContentPane();
    c.setLayout( new BorderLayout() );

    if (hasJ3D()) {     // start the Life3D application
      w3d = new WrapLife3D(this);
      c.add(w3d, BorderLayout.CENTER);
    }
    else
      reportProb(c);

    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e)
      { finishOff(); }
    });

    pack();
    setResizable(false);    // fixed size display

    // center this window
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension winDim = this.getSize();
    this.setLocation( (screenDim.width-winDim.width)/2,
                      (screenDim.height-winDim.height)/2);

    setVisible(true);

    // create the popup menu in the system tray
    Life3DPopup lpop = new Life3DPopup(this, w3d, desktop);
  } // end of Life3D()


  // -------------- handle Java 3D not being installed --------------

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
  /* Report the absence of Java 3D using a label and a button. 
     When the user clicks on the button,
     a browser is launched to show the associated URL.
  */
  {
    JPanel reportPanel = new JPanel();
    reportPanel.setLayout( new BoxLayout(reportPanel, BoxLayout.Y_AXIS));  // vertical
    c.add(reportPanel, BorderLayout.CENTER);

    // use a bit of HTML to jazz up the label and button
    String msgText = "<html><font size=+2>" +
                     "Java 3D <font color=red>not</font> installed" +
                     "</font></html>";
    JLabel msgLabel = new JLabel(msgText, SwingConstants.CENTER);
    reportPanel.add(msgLabel);

    String visitText = "<html><font size=+2>" +
                       "Visit https://java3d.dev.java.net/" +
                       "</font></html>";
    visitButton = new JButton(visitText);
    visitButton.setBorderPainted(false);
    if ((desktop != null) && (desktop.isSupported(Desktop.Action.BROWSE)) )
      visitButton.addActionListener(this);  
              /* only set-up a listener if the desktop API is capable
                 of launching a browser */
    reportPanel.add(visitButton);

  }  // end of reportProb()


  public void actionPerformed(ActionEvent e)
  // launch a browser to show the URL in J3D_URL
  {
    if (e.getSource() == visitButton) {
      try {   // launch browser 
        URI uri = new URI(J3D_URL);
        desktop.browse(uri);
      }
      catch (Exception ex) 
      { System.out.println(ex); }
    }
  }  // end of actionPerformed


  // -------------------- called by the popup menu -------------------


  public void changeIconify() 
  // iconify or deiconify the application
  {  
    if ((getState() & JFrame.ICONIFIED) == JFrame.ICONIFIED)
      setState(Frame.NORMAL);    // deiconify
    else
      setState(Frame.ICONIFIED);  // iconify
  } // end of changeIconify()


  public void finishOff()
  // terminate the application after reporting the update statistics
  {  
    if (w3d != null)
      w3d.reportStats();
    System.exit(0);  
  } // end of finishOff()


// -----------------------------------------

  public static void main(String[] args)
  { new Life3D();  }


} // end of Life3D class

