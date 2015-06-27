package java3d.life3d_6;

// Life3DPopup.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Add a popup menu to the system tray for interacting 
   with Life3D.

   The menu contains:
       (De)Iconify
       Speed --> select from Slow, Medium, Fast
       Background --> select from Blue, Green, White, Black
       Edit Rules
       About
       Contact Author
       Exit

   '(De)Iconify' iconifies or deiconfies the application. The user
   can also double click on the tray icon for Life3D to do the 
   same thing.

   'Speed' changes the rotation speed of the appl; 'Background' changes
   the background colour.

   'Edit Rules' opens a text editor on the JavaScript rules in 
   SCRIPT_FNM which control how the cells are born and die.

   'About' displays the ABOUT_FNM file in a text viewer.

   'Contact Author' opens a e-mail client, with the written message
   to be sent to the AUTHOR_EMAIL address.

   'Edit Rules', 'About" and 'Contact Author' use the new Desktop API
   to start the external text editor, viewer, and e-mail client.

   'Exit' makes the Life3D application exit.

*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class Life3DPopup implements ActionListener, ItemListener
{
  private static final int DEFAULT_SPEED = 2;   // fast
  private static final int DEFAULT_COLOUR = 0;  // blue

  private static final String[] speedLabels =  {"Slow", "Medium", "Fast"};
  private static final String[] colourLabels =  
                                   {"Blue", "Green", "White", "Black"};

  // strings for rules file, author e-mail, and about file
  private static final String SCRIPT_FNM = "rules.js";
  private static final String AUTHOR_EMAIL = "adNOT@fivedots.coe.psu.ac.th";
  private static final String ABOUT_FNM = "about.txt";


  // the popup items (the system tray API uses AWT menu components)
  private TrayIcon trayIcon;
  private MenuItem iconifyItem, rulesItem, aboutItem, authorItem, exitItem;
  private CheckboxMenuItem[] speedItems;   // for speed values
  private CheckboxMenuItem[] colourItems;  // for background colour values

  private Life3D applWindow;
  private WrapLife3D w3d;
  private Desktop desktop;    // for accessing desktop applications


  public Life3DPopup(Life3D top, WrapLife3D w3d, Desktop d)
  {
    applWindow = top;   // used to (de-)iconify, and closing the appl.
    this.w3d = w3d;     // used to change speed and background
    desktop = d;        // used for text editing, opening, and e-mail

    if (SystemTray.isSupported())
      makeTrayIcon();
    else
      System.err.println("System tray is currently not supported.");
  }  // end of buildTray()


  private void makeTrayIcon()
  // the tray icon is an image, tooltip, and a popup menu
  {
    SystemTray tray = SystemTray.getSystemTray();
    Image trayImage = Toolkit.getDefaultToolkit().getImage("balls.gif");

    PopupMenu trayPopup = makePopup();
    trayIcon = new TrayIcon(trayImage, "Life3D", trayPopup);
    trayIcon.setImageAutoSize(true);

    // double left clicking on the tray icon causes (de-)iconification
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {  applWindow.changeIconify();  }
    };
    trayIcon.addActionListener(actionListener);

    try {
      tray.add(trayIcon);
    } 
    catch (AWTException e) 
    { System.err.println("TrayIcon could not be added.");  }
  }  // end of makeTrayIcon()



  private PopupMenu makePopup()
  /* The popup menu uses the old AWT components: PopupMenu, 
     MenuItem, and CheckboxMenuItem. There's no AWT version
     of JRadioButtonMenuItem unfortunately.
  */
  {
    PopupMenu trayPopup = new PopupMenu();
   
    iconifyItem = new MenuItem("(De)Iconify");  
    iconifyItem.addActionListener(this);
    trayPopup.add(iconifyItem);


    // --------------- speed items -----------------------------------

    Menu speedMenu = new Menu("Speed");
    trayPopup.add(speedMenu);

    speedItems = new CheckboxMenuItem[speedLabels.length];
    for(int i=0; i < speedLabels.length; i++) {
      speedItems[i] = new CheckboxMenuItem( speedLabels[i] );
      speedItems[i].addItemListener(this);
      speedMenu.add( speedItems[i] );
      if (w3d == null)   // if no 3D scene, then cannot change speed
        speedItems[i].setEnabled(false);
    }
    speedItems[DEFAULT_SPEED].setState(true);   // 'fast' is the default


    // --------------- background colour items --------------------------

    Menu bgMenu = new Menu("Background");
    trayPopup.add(bgMenu);

    colourItems = new CheckboxMenuItem[colourLabels.length];
    for(int i=0; i < colourLabels.length; i++) {
      colourItems[i] = new CheckboxMenuItem( colourLabels[i] );
      colourItems[i].addItemListener(this);
      bgMenu.add( colourItems[i] );
      if (w3d == null)   // if no 3D scene, then cannot change background
        colourItems[i].setEnabled(false);
    }
    colourItems[DEFAULT_COLOUR].setState(true);   // 'blue' is the default


    // -----------------------------------------

    rulesItem = new MenuItem("Edit Rules");
    if ((desktop != null) && (desktop.isSupported(Desktop.Action.EDIT)) )
      rulesItem.addActionListener(this);  // only if desktop text editing is possible
    else
      rulesItem.setEnabled(false);
    trayPopup.add(rulesItem);

    aboutItem = new MenuItem("About");
    if ((desktop != null) && (desktop.isSupported(Desktop.Action.OPEN)) )
      aboutItem.addActionListener(this);  // only if desktop text opening is possible
    else
      aboutItem.setEnabled(false);
    trayPopup.add(aboutItem);

    authorItem = new MenuItem("Contact Author");
    if ((desktop != null) && (desktop.isSupported(Desktop.Action.MAIL)) )
      authorItem.addActionListener(this);   // only if desktop e-mail is possible
    else
      authorItem.setEnabled(false);
    trayPopup.add(authorItem);

    MenuShortcut exitShortcut = new MenuShortcut(KeyEvent.VK_X,true);
      // ctrl-shift-x on windows

    exitItem = new MenuItem("Exit", exitShortcut);
    exitItem.addActionListener(this);
    trayPopup.add(exitItem);

    // System.out.println( exitItem.getShortcut());

    return trayPopup;
  }  // end of makePopup()


  // ------------------ action listener ------------------------------


  public void actionPerformed(ActionEvent e)
  {
    MenuItem item = (MenuItem) e.getSource();  
              // all the actions come from MenuItems

    if (item == iconifyItem) {
      applWindow.changeIconify();
      // System.out.println("(De) Iconified");
    }
    else if (item == rulesItem) {
      // System.out.println("Edit " + SCRIPT_FNM);
      launchFile(SCRIPT_FNM, Desktop.Action.EDIT);
    }
    else if (item == aboutItem) {
      // System.out.println("Open " + ABOUT_FNM);
      launchFile(ABOUT_FNM, Desktop.Action.OPEN);
    }
    else if (item == authorItem) {
      // System.out.println("Send e-mail to " + AUTHOR_EMAIL);
      launchMail(AUTHOR_EMAIL);
    }
    else if (item == exitItem) {
      // System.out.println("Exiting...");
      applWindow.finishOff();
    }
    else
      System.out.println("Unknown Action Event");
  }  // end of actionPerformed()


  private void launchFile(String fnm, Desktop.Action action)
  /* Use the desktop appl. associated with action to 
     handle the fnm file. Errors are reported as a popup
     message near the tray icon. */
  {
    File f = null;
    try {
      f = new File(fnm);
    }
    catch (Exception e)
    {  // System.out.println(e);
       trayIcon.displayMessage("File Error", 
            "Could not access " + fnm, TrayIcon.MessageType.ERROR);
       return;
    }

    if (!f.exists()) {
      // System.out.println("Error: file " + fnm + " does not exist");
      trayIcon.displayMessage("File Error", 
            "File " + fnm + " does not exist", TrayIcon.MessageType.ERROR);
      return;
    }

    if (action == Desktop.Action.OPEN)
      openFile(fnm, f);
    else if (action == Desktop.Action.EDIT)
      editFile(fnm, f);
  }  // end of launchFile()


  private void openFile(String fnm, File f)
  // open the file for reading
  {
    if (!f.canRead()) {
      // System.out.println("Error: cannot read file " + fnm);
      trayIcon.displayMessage("File Error", 
            "Cannot read file " + fnm, TrayIcon.MessageType.ERROR);
      return;
    }
    else {  // can read
      try {
        desktop.open(f.getAbsoluteFile());
      }
      catch (Exception e) 
      { // System.out.println(e); 
        trayIcon.displayMessage("File Error", 
          "Cannot open file " + fnm, TrayIcon.MessageType.ERROR);
      }
    }
  }  // end of openFile()


  private void editFile(String fnm, File f)
  {
    if (!f.canWrite()) {
      // System.out.println("Error: cannot write to file " + fnm);
      trayIcon.displayMessage("File Error", 
          "Cannot write to file " + fnm, TrayIcon.MessageType.ERROR);
      return;
    }
    else {  // can write
      try {
        desktop.edit(f.getAbsoluteFile());
      }
      catch (Exception e) 
      { // System.out.println(e); 
        trayIcon.displayMessage("File Error", 
          "Cannot edit file " + fnm, TrayIcon.MessageType.ERROR);
      }
    }
  }  // end of editFile()


  private void launchMail(String addr)
  /* Launch the e-mail client with its address field using addr.
     It's subject line will be "Life 3D Query".
  */
  {
    try {
      URI uriMail = new URI("mailto", addr + "?SUBJECT=Life 3D Query", null);  
      desktop.mail(uriMail);
    }
    catch (Exception e) 
    { // System.out.println(e); 
      trayIcon.displayMessage("E-mail Error", 
            "Cannot send e-mail to " + addr, TrayIcon.MessageType.ERROR);
    }
  }  // end of launchMail()


  // ------------------ item listener ------------------------------


  public void itemStateChanged(ItemEvent e)
  {
    CheckboxMenuItem item = (CheckboxMenuItem) e.getSource();
        /* all the item events come from CheckboxMenuItems
           which are used for the speed and bacground colour values */
    int posn = -1;

    // speed checkbox items
    if ((posn = findItem(item, speedItems)) != -1) {
      switchOffItems(posn, speedItems);
      if (w3d != null)
        w3d.adjustSpeed( speedLabels[posn] );
    }
    // colour checkbox items
    else if ((posn = findItem(item, colourItems)) != -1) {
      switchOffItems(posn, colourItems);
      if (w3d != null)
        w3d.adjustColour( colourLabels[posn] );
    }
    else
      System.out.println("Unknown Item Event");

  }  // end of itemStateChanged()



  private int findItem(CheckboxMenuItem item, CheckboxMenuItem[] items)
  // return the position of item in items, or -1
  {
    // System.out.println("Item label: " + item.getLabel());
    for(int i=0; i < items.length; i++)
      if (item == items[i])
        return i;
    return -1;
  }  // end of findItem()


  private void switchOffItems(int posn, CheckboxMenuItem[] items)
  /* Switch off the CheckBoxMenuItems in the items array, except 
     for number i. This function simulates the effect of radio
     buttons in the menu. Unfortunately there isn't a AWT version
     of JRadioButtonMenuItem. */
  {
    for(int i=0; i < items.length; i++)
      if (posn != i)
        items[i].setState(false);
  }  // end of switchOffItems()


} // end of Life3DPopup class

