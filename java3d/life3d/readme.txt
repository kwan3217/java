
Chapter 2. Get a Life (in 3D)

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
Files here:

  * Life3D.java, Life3DConfig.java, LifeProperties.java,
    WrapLife3D.java, CellsGrid.java, Cell.java,
    TimeBehavior.java
       // 7 Java files
   
  * life3DProps.txt
       // the application configuration files
   
  * mainClass.txt
       // used when creating a JAR

  * jssaverLife3D.cfg
       // a configuration file for JSCreenSaver that uses Life3D
   
==================================
Requirements:

* J2SE 5.0 from http://java.sun.com/j2se/1.5.0/index.jsp

* Java 3D 1.4.0 (or 1.3.2) from https://java3d.dev.java.net/

==================================
Compilation: 
  $ javac *.java

Execution: 
  $ java Life3D
         // this starts the 3D application
or
  $ java Life3D -edit
         // this puts up the configuration screen

==================================
Screensaver Software

JScreenSaver is a Windows screensaver loader which can 
execute Java programs. It was written by Yoshinori Watanabe, 
and is available from 
    http://homepage2.nifty.com/igat/igapyon/soft/jssaver.html 
and http://sourceforge.net/projects/jssaver/

NOTE: it only works with Windows (sorry to Linux/Mac users)


1. From the sourceForge link, download jssaver-1_1alpha7.zip (98 KB).

2. Unzip, and copy the following 3 files from /bin:

   jssaver.cfg-SimpleSaver
   jssaver.jar
   jssaver.scr

3. Rename jssaver.cfg-SimpleSaver to jssaver.cfg.

4. Move jssaver.cfg, jssaver.jar, jssaver.scr to c:\windows\system32
   (in Windows XP).

5. Check if the jscreensaver shows up as a screensaver choice
   in the screensaver tab of the Display control panel. Test it.
   It should display a series of moving lines and a time.


========================================
Making a Java 3D Screensaver. Step 1. Java --> JAR

Compilation:
  $ javac *.java

Package the classes as a JAR:
  $ jar cvfm Life3D.jar mainClass.txt *.class
      // at the end, check if there's a Life3D.jar

Move Life3D.jar *and* life3DProps.txt to a new location, 
so there's no chance of using the unpackaged class files 
by accident during the testing phase.

Testing:
  $ java -classpath Life3D.jar Life3D -edit 
      // tests the configuration screen

  $ java -classpath Life3D.jar Life3D
      // tests the 3D application


========================================
Making a Java 3D Screensaver. Step 2. Moving into the System


1. Move Life3D.jar *and* life3DProps.txt to
   c:\windows\system32

2. Rename jssaverLife3D.cfg to jssaver.cfg, and 
   copy it to c:\windows\system32, overwriting the 
   old one that you used earlier.

3. Revisit the screensaver tab of the Display control 
   panel, and try out jscreensaver with Life3D.

NOTE: remember that you terminate Life3D by pressing <esc>,
      'q', <ctrl>-c, or <end>. Moving or pressing the mouse
      doesn't stop the saver.

-----------
Last updated: 3rd March 2007