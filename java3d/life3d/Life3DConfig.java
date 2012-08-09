package java3d.life3d;


// Life3DConfig.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* A set of controls for modifying the 7 properties managed by
   the LifeProperties object, lifeProps:
     * fullscreen: whether the Life3D appl is full-screen

     * width, height: the dimensions of the window if not full-screen

     * bgColour: the background colour used in Life3D;
                 it may be blue, green, white, or black

     * speed: the speed that the balls grid rotates;
              it may be slow, medium, or fast

     * the birth and die ranges used by the Life rules in CellsGrid. 
       A range is a series of numbers separated by spaces, each 
       number representing the number of neighbours that will trigger
       the cell's birth/death.
 
   The GUI uses:
      * a group of RadioButtons for the speed values
      * a combo box for the background colours
      * a group of two radio buttons for fullscreen and window
      * two textfields for the window's width and height
      * two textfields for the birth and die ranges

*/

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


public class Life3DConfig extends JPanel
						         implements ActionListener
{
  // the GUI elements
  private JRadioButton slowButton, mediumButton, fastButton;   // for speed
  private JComboBox bgSelection;   // for background colours
  private JRadioButton fullButton, winButton;   // fullscreen or window
  private JTextField widthTF, heightTF;         // window dimensions
  private JTextField birthTF, dieTF;            // birth and die ranges
  private JButton okButton, cancelButton;

  // properties data displayed and modified via the GUI
  private int speed;
  private int bgColour;
  private boolean isFullScreen;
  private int width, height;
  private String birthRange, dieRange;

  private LifeProperties lifeProps;


  public Life3DConfig(LifeProperties lps) 
  {
    lifeProps = lps;
    initData();
    initGUI();
  } // end of Life3DConfig()


  private void initData()
  /* load speed, bgColour, fullscreen, width, height,
     and the birth and die ranges properties 
     for populating the GUI */
  {
    speed = lifeProps.getSpeed();
    bgColour = lifeProps.getBGColour();
    isFullScreen = lifeProps.isFullScreen();
    width = lifeProps.getWidth();
    height = lifeProps.getHeight();

    birthRange = lifeProps.getBirthStr();
    dieRange = lifeProps.getDieStr();
  }  // end of initData()




  private void initGUI()
  {
    this.setLayout( new BorderLayout() );

    JPanel ctrlPanel = new JPanel();
    ctrlPanel.setLayout( new BorderLayout() );
    this.add(ctrlPanel, BorderLayout.CENTER);

    Border blackline = BorderFactory.createLineBorder(Color.black);


    // ------------ data input controls at top of panel ------------------

    JPanel inputTopPanel = new JPanel();
    inputTopPanel.setLayout( new BorderLayout() );
    ctrlPanel.add(inputTopPanel, BorderLayout.CENTER);

    // ------------------------ speed area ------------------------------
    // speed can be slow, medium, or fast

    TitledBorder speedTitle = BorderFactory.createTitledBorder(
                                                   blackline, "Speed");
    // speed control panel on left
    JPanel speedPanel = new JPanel();
    speedPanel.setLayout( new BoxLayout(speedPanel, BoxLayout.Y_AXIS));  // vertical
    speedPanel.setBorder(speedTitle); 
    inputTopPanel.add(speedPanel, BorderLayout.WEST);
    
    // the speed radio buttons
    slowButton = new JRadioButton("Slow");
    slowButton.addActionListener(this);
    speedPanel.add(slowButton);

    mediumButton = new JRadioButton("Medium");
    mediumButton.addActionListener(this);
    speedPanel.add(mediumButton);

    fastButton = new JRadioButton("Fast");
    fastButton.addActionListener(this);
    speedPanel.add(fastButton);

    // group the radio buttons
    ButtonGroup speedGroup = new ButtonGroup();
    speedGroup.add(slowButton);
    speedGroup.add(mediumButton);
    speedGroup.add(fastButton);

    // set selection using initial data
    switch (speed) { 
      case LifeProperties.SLOW: slowButton.setSelected(true); break;
      case LifeProperties.MEDIUM: mediumButton.setSelected(true); break;
      case LifeProperties.FAST: fastButton.setSelected(true); break;
      default: mediumButton.setSelected(true); break;
    }


    // ----------- input controls in the middle  of the panel -------------

    JPanel middlePanel = new JPanel();
    middlePanel.setLayout( new BorderLayout() );
    inputTopPanel.add(middlePanel, BorderLayout.CENTER);


    // ------------------ background colours area ---------------------

    // background controls at top of middle panel
    JPanel bgPanel = new JPanel();
    bgPanel.setLayout( new BoxLayout(bgPanel, BoxLayout.X_AXIS));  // horizontal
    middlePanel.add(bgPanel, BorderLayout.NORTH);

    bgPanel.add( new JLabel("Background: ") );

    bgSelection = new JComboBox(LifeProperties.bgColours);
    bgSelection.addActionListener(this);
    bgPanel.add(bgSelection);

    // set selection using initial data
    bgSelection.setSelectedIndex(bgColour);


    // ------------------------ window size area ------------------------------

    TitledBorder sizeTitle = BorderFactory.createTitledBorder(
                                                   blackline, "Size");

    // size control panel in center of middle panel
    JPanel sizePanel = new JPanel();
    sizePanel.setLayout( new BoxLayout(sizePanel, BoxLayout.Y_AXIS));  // vertical
    sizePanel.setBorder(sizeTitle); 
    middlePanel.add(sizePanel, BorderLayout.CENTER);
    
    // ----------------- type of window ---------------------------------

    JPanel winTypePanel = new JPanel();
    winTypePanel.setLayout( new BoxLayout(winTypePanel, BoxLayout.X_AXIS));  // horizontal
    sizePanel.add(winTypePanel);

    // the size radio buttons
    fullButton = new JRadioButton("Full Screen");
    fullButton.addActionListener(this);
    winTypePanel.add(fullButton);

    winButton = new JRadioButton("Window");
    winButton.addActionListener(this);
    winTypePanel.add(winButton);

    // group the radio buttons
    ButtonGroup sizeGroup = new ButtonGroup();
    sizeGroup.add(fullButton);
    sizeGroup.add(winButton);

    // set selection using initial data
    if (isFullScreen)
      fullButton.setSelected(true);
    else
      winButton.setSelected(true);


    // ----------------- window size dimensions -----------------------------

    JPanel dimPanel = new JPanel();
    dimPanel.setLayout( new BoxLayout(dimPanel, BoxLayout.X_AXIS));  // horizontal
    sizePanel.add(dimPanel);

    // width field
    dimPanel.add( new JLabel("Width: ") );
    widthTF = new JTextField(5);
    widthTF.setText(""+width);
    if (isFullScreen)
      widthTF.setEnabled(false);
    dimPanel.add(widthTF);
    
    // height field
    dimPanel.add( new JLabel("  Height: ") );
    heightTF = new JTextField(5);
    heightTF.setText(""+height);
    if (isFullScreen)
      heightTF.setEnabled(false);
    dimPanel.add(heightTF);


    // ------------ data input controls at bottom ----------------------------

    JPanel inputBotPanel = new JPanel();
    inputBotPanel.setLayout( new BorderLayout() );
    ctrlPanel.add(inputBotPanel, BorderLayout.SOUTH);


    // ------------------------ rules area ------------------------------

    TitledBorder rulesTitle = BorderFactory.createTitledBorder(
                                                   blackline, "Rules");

    // rules control panel in center of input bottom panel
    JPanel rulesPanel = new JPanel();
    rulesPanel.setLayout( new BoxLayout(rulesPanel, BoxLayout.X_AXIS));  // horizontal
    rulesPanel.setBorder(rulesTitle); 
    inputBotPanel.add(rulesPanel, BorderLayout.CENTER);

    // birth ranges
    rulesPanel.add( new JLabel("Birth: ") );
    birthTF = new JTextField(10);
    birthTF.setText(birthRange);
    // birthTF.addActionListener(this);
    rulesPanel.add(birthTF);
    
    // die ranges
    rulesPanel.add( new JLabel("  Die: ") );
    dieTF = new JTextField(10);
    dieTF.setText(dieRange);
    // dieTF.addActionListener(this);
    rulesPanel.add(dieTF);


    // --------------------- finishing buttons -----------------------

    JPanel finPanel = new JPanel();
    this.add(finPanel, BorderLayout.SOUTH);

    okButton = new JButton("Ok");
    okButton.addActionListener(this);
    finPanel.add(okButton);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    finPanel.add(cancelButton);
  }  // end of initGUI()



  public void actionPerformed(ActionEvent e)
  {
    // speed radio buttons
    if (e.getSource() == slowButton)
      speed = LifeProperties.SLOW;
    else if (e.getSource() == mediumButton)
      speed = LifeProperties.MEDIUM;
    else if (e.getSource() == fastButton)
      speed = LifeProperties.FAST;

    // background colours combo box
    else if (e.getSource() == bgSelection)
      bgColour = bgSelection.getSelectedIndex();

    // window size radio buttons
    else if (e.getSource() == fullButton) {
      isFullScreen = true;
      widthTF.setEnabled(false);    // disable width and height fields
      heightTF.setEnabled(false);
    }
    else if (e.getSource() == winButton) {
      isFullScreen = false;
      widthTF.setEnabled(true);    // enable width and height fields
      heightTF.setEnabled(true);
    }

    // finishing buttons
    else if (e.getSource() == okButton) {
      System.out.println("Pressed ok");
      saveProperties();
      System.exit(0);
    }
    else if (e.getSource() == cancelButton) {
      System.out.println("Pressed cancel");
      System.exit(0);
    }
    else
      System.out.println("Unknown GUI Source");
  }  // end of actionPerformed()



  private void saveProperties()
  /* save speed, bgColour, fullscreen, width, height,
     and the birth and die ranges properties */
  {
    lifeProps.setSpeed(speed);
    lifeProps.setBGColour(bgColour);
    lifeProps.setFullScreen(isFullScreen);

    // store current width and height values
    lifeProps.setWidth( widthTF.getText() ); 
    lifeProps.setHeight( heightTF.getText() );

    // store current birth and die ranges
    lifeProps.setBirth( birthTF.getText() ); 
    lifeProps.setDie( dieTF.getText() );

    lifeProps.saveProperties();
  }  // end of saveProperties()


} // end of Life3DConfig class

