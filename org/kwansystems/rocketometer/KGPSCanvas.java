package org.kwansystems.rocketometer;

import org.kwansystems.space.porkchop.*;
import java.awt.*;
import javax.swing.*;


import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;

/**
 *
 * @author  chrisj
 */
public class KGPSCanvas extends javax.swing.JPanel {

  /** Creates new form KOrbitCanvas */
  public KGPSCanvas() {
    super();
    initComponents(); 
  }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setBackground(new java.awt.Color(0, 0, 0));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

      //Override this if you want something other than an asterisk for the center
  public void drawCenter(Graphics G, int CenterX, int CenterY) {

  }
  @Override
  public void paintComponent(Graphics G) {
    super.paintComponent(G);

  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

        /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("KGPSCanvas Main");
        KGPSCanvas TP = new KGPSCanvas();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        frame.getContentPane().add(TP);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });
    }


}