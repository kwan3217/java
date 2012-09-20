package org.kwansystems.space.porkchop;

import java.awt.*;
import javax.swing.*;


import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;

/**
 *
 * @author  chrisj
 */
public class KOrbitCanvas extends javax.swing.JPanel {
  private PorkchopTrajectory traj[];
  private double MPerPix;
  public Color SunColor=Color.YELLOW;
  public KeplerFG K;

  /** Creates new form KOrbitCanvas */
  public KOrbitCanvas() {
    super();
    K=GaussFG.Sun;
    setMPerPix(2e9);
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

        btnZoomOut = new javax.swing.JButton();
        btnZoomIn = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 0, 0));

        btnZoomOut.setText("Zoom Out");
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });

        btnZoomIn.setText("Zoom In");
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(244, Short.MAX_VALUE)
                .addComponent(btnZoomOut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnZoomIn))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(277, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnZoomIn)
                    .addComponent(btnZoomOut)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomOutActionPerformed
  setMPerPix(getMPerPix()*1.2);
  repaint();
}//GEN-LAST:event_btnZoomOutActionPerformed

private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomInActionPerformed
  setMPerPix(getMPerPix()/1.2);
  repaint();
}//GEN-LAST:event_btnZoomInActionPerformed

      //Override this if you want something other than an asterisk for the center
  public void drawCenter(Graphics G, int CenterX, int CenterY) {
    G.setColor(SunColor);
    G.drawLine(CenterX-10,CenterY,CenterX+10,CenterY);
    G.drawLine(CenterX,CenterY-10,CenterX,CenterY+10);
    G.drawLine(CenterX-7,CenterY-7,CenterX+7,CenterY+7);
    G.drawLine(CenterX-7,CenterY+7,CenterX+7,CenterY-7);
  }
  @Override
  public void paintComponent(Graphics G) {
    super.paintComponent(G);
    int CenterX=getWidth()/2;
    int CenterY=getHeight()/2;
    drawCenter(G,CenterX,CenterY);
    if(traj==null) return;
    
    for(int i = 0; i<traj.length; i++) if(traj[i]!=null) {
      G.setColor(traj[i].color);
      int OldX=((int)(CenterX+traj[i].Start.S.X()/MPerPix));
      int OldY=((int)(CenterY-traj[i].Start.S.Y()/MPerPix));
      try {
        double diff=Time.difference(traj[i].Start.T,traj[i].Stop,traj[i].Start.T.Units);
        for(double j=traj[i].DeltaT;j<diff;j+=traj[i].DeltaT) {
          MathState ThisState=K.propagate(traj[i].Start,Time.add(traj[i].Start.T,j,traj[i].Start.T.Units));
          if(ThisState!=null) {
            int NewX=((int)(CenterX+ThisState.X()/MPerPix));
            int NewY=((int)(CenterY-ThisState.Y()/MPerPix));
            G.drawLine(OldX,OldY,NewX,NewY);
            OldX=NewX;
            OldY=NewY;
          }
        }
      } catch (ArithmeticException E) {System.err.println("Can't draw trajectory "+i);}
    }
  }
  public void setMPerPix(double mPerPix) {
    MPerPix = mPerPix;
  }
  public double getMPerPix() {
    return MPerPix;
  }
  public void setTraj(PorkchopTrajectory[] traj) {
    this.traj = traj;
    repaint();
  }
  public PorkchopTrajectory[] getTraj() {
    return traj;
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    // End of variables declaration//GEN-END:variables

        /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("KOrbitCanvas Main");
        KOrbitCanvas TP = new KOrbitCanvas();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PorkchopModel model=new PorkchopModel();
        model.setDepartPlanet(3);
        model.setArrivePlanet(2);
        model.setLaunchWindow(54);
        model.setDepartTime(new Time(82709.6,TimeUnits.Days,TimeScale.UTC,TimeEpoch.MJD));
        model.setConstrainDepart(true);
        model.setDepartWeight(1);
        model.setArriveWeight(0);
        model.optimizeDVesc();
        if(model.getCourse()==null) return;
        if(model.getDepartPlanet()==null) return;
        if(model.getArrivePlanet()==null) return;
        PorkchopTrajectory[] traj=new PorkchopTrajectory[3];
        traj[0]=new PorkchopTrajectory(model.getDepartPlanet().Orbit.getStateTime(model.getCourse().depart.T), model.getCourse().arrive.T, new Color(  0,  0,255), 1);
        traj[1]=new PorkchopTrajectory(model.getArrivePlanet().Orbit.getStateTime(model.getCourse().depart.T), model.getCourse().arrive.T, new Color(255,  0,  0), 1);
        traj[2]=new PorkchopTrajectory(model.getCourse().depart,                                    model.getCourse().arrive.T, new Color(255,255,255), 1);
        TP.setTraj(traj);
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