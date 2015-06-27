
Chapter 3. Get a Life (the Java 6 Way)

From:
  Pro Java 6 3D Game Development
  Andrew Davison
  Apress, April 2007
  ISBN: 1590598172 
  http://www.apress.com/book/bookDisplay.html?bID=10256
  Web Site for the book: http://fivedots.coe.psu.ac.th/~ad/jg2


Contact Address:
  Dr. Andrew Davison
  Dept. of Computer Engineering
  Prince of Songkla University
  Hat Yai, Songkhla 90112, Thailand
  E-mail: ad@fivedots.coe.psu.ac.th


If you use this code, please mention my name, and include a link
to the book's Web site.

Thanks,
  Andrew


==================================
Files and Directories here:

  * Life3D.java, ClockAnimation.java, Life3DPopup.java, 
    WrapLife3D.java, TimeBehavior.java, CellsGrid.java,
    Cell.java
       // 7 Java files
   
  * about.txt        // the "about" text file
  * balls.gif        // the tray icon image
  * lifeSplash.jpg   // the splash image
  * rules.js         // the JavaScript rules 

  * clocks/ 
      // a directory of 8 clock images, stored in
         clock0.gif to clock7.gif; used for animating
         the splash

  * runLife3D.bat
      // a DOS batch file for running Life3D with the
         -splash command line argument

  -----
  The following directories are not used by the Life3D
  example. They contain extra, separate examples:

  * splash/
      // a splash example using the ClockAnimation class;
         see the readme.txt file in the directory for details

  * scripting/
     // several small scripting examples; 
         see the readme.txt file in the directory for details

   
==================================
Requirements:

* Java SE 6.0 from http://java.sun.com/javase/6/
  Version 6 supports splashscreens, the system tray API,
  the desktop API, and scripting used in this example.

* Java 3D 1.4.0 (or 1.3.2) from https://java3d.dev.java.net/

==================================
Compilation: 
  $ javac *.java

Execution: 
  $ java -splash:lifeSplash.jpg Life3D

or use the batch file:
  $ runLife3D


-----------
Last updated: 3rd March 2007