/*
 * WindTunnel.java
 *
 * Created on September 26, 2004, 9:28 AM
 */

package org.kwansystems.space.windtunnel;

import org.kwansystems.planet.*;
import org.kwansystems.vector.*;
import java.text.*;
import static java.lang.Math.*;
/**
 *
 * @author  chrisj
 */
public class WindTunnel extends javax.swing.JFrame {
  
  /** Creates new form WindTunnel */
  public WindTunnel() {
    initComponents();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  private void initComponents() {//GEN-BEGIN:initComponents
    java.awt.GridBagConstraints gridBagConstraints;

    SLDAltitude = new javax.swing.JSlider();
    SLDAoa = new javax.swing.JSlider();
    LBLAltitude = new javax.swing.JLabel();
    TXTAltitude = new javax.swing.JTextField();
    LBLAoa = new javax.swing.JLabel();
    TXTAoa = new javax.swing.JTextField();
    PANFoil = new javax.swing.JPanel();
    LBLFoilcL = new javax.swing.JLabel();
    TXTFoilcL = new javax.swing.JTextField();
    LBLFoilcD = new javax.swing.JLabel();
    TXTFoilcD = new javax.swing.JTextField();
    TXTFoilcM = new javax.swing.JTextField();
    LBLFoilcM = new javax.swing.JLabel();
    LBLFoilRe = new javax.swing.JLabel();
    TXTFoilRe = new javax.swing.JTextField();
    LBLFoilS = new javax.swing.JLabel();
    TXTFoilS = new javax.swing.JTextField();
    TXTFoilC = new javax.swing.JTextField();
    LBLFoilC = new javax.swing.JLabel();
    LBLFoilA = new javax.swing.JLabel();
    TXTFoilA = new javax.swing.JTextField();
    TXTFoilE = new javax.swing.JTextField();
    LBLFoilE = new javax.swing.JLabel();
    LBLFoilWS = new javax.swing.JLabel();
    TXTFoilWS = new javax.swing.JTextField();
    LBLFoilMach = new javax.swing.JLabel();
    TXTFoilMach = new javax.swing.JTextField();
    LBLFoilcDe = new javax.swing.JLabel();
    TXTFoilcDe = new javax.swing.JTextField();
    LBLFoilcDi = new javax.swing.JLabel();
    TXTFoilcDi = new javax.swing.JTextField();
    TXTFoilcDw = new javax.swing.JTextField();
    LBLFoilcDw = new javax.swing.JLabel();
    PANForce = new javax.swing.JPanel();
    LBLFoilForce = new javax.swing.JLabel();
    TXTFoilForceX = new javax.swing.JTextField();
    TXTFoilForceY = new javax.swing.JTextField();
    TXTFoilForceZ = new javax.swing.JTextField();
    LBLForceX = new javax.swing.JLabel();
    LBLForceY = new javax.swing.JLabel();
    LBLForceZ = new javax.swing.JLabel();
    TXTFoilMomentFZ = new javax.swing.JTextField();
    TXTFoilMomentFY = new javax.swing.JTextField();
    TXTFoilMomentFX = new javax.swing.JTextField();
    LBLFoilMomentF = new javax.swing.JLabel();
    LBLFoilMomentL = new javax.swing.JLabel();
    TXTFoilMomentLX = new javax.swing.JTextField();
    TXTFoilMomentLY = new javax.swing.JTextField();
    TXTFoilMomentLZ = new javax.swing.JTextField();
    LBLFoilMoment = new javax.swing.JLabel();
    TXTFoilMomentX = new javax.swing.JTextField();
    TXTFoilMomentY = new javax.swing.JTextField();
    TXTFoilMomentZ = new javax.swing.JTextField();
    LBLFoilq = new javax.swing.JLabel();
    TXTFoilq = new javax.swing.JTextField();
    TXTFoilName = new javax.swing.JTextField();
    LBLFoilName = new javax.swing.JLabel();
    TXTVel = new javax.swing.JTextField();
    LBLVel = new javax.swing.JLabel();
    SLDVel = new javax.swing.JSlider();
    BTNPrev = new javax.swing.JButton();
    BTNNext = new javax.swing.JButton();

    getContentPane().setLayout(new java.awt.GridBagLayout());

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    SLDAltitude.setMaximum(150000);
    SLDAltitude.setMinorTickSpacing(5);
    SLDAltitude.setOrientation(javax.swing.JSlider.VERTICAL);
    SLDAltitude.setValue(10000);
    SLDAltitude.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        SLDAltitudeStateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(SLDAltitude, gridBagConstraints);

    SLDAoa.setMaximum(180);
    SLDAoa.setMinimum(-180);
    SLDAoa.setOrientation(javax.swing.JSlider.VERTICAL);
    SLDAoa.setValue(0);
    SLDAoa.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        SLDAoaStateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(SLDAoa, gridBagConstraints);

    LBLAltitude.setText("Altitude (m)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    getContentPane().add(LBLAltitude, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    getContentPane().add(TXTAltitude, gridBagConstraints);

    LBLAoa.setText("AoA (deg)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    getContentPane().add(LBLAoa, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    getContentPane().add(TXTAoa, gridBagConstraints);

    PANFoil.setLayout(new java.awt.GridBagLayout());

    PANFoil.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Foil 0 (PegasusXL Wing)"));
    LBLFoilcL.setText("Lift coefficient");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilcL, gridBagConstraints);

    TXTFoilcL.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilcL.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilcL, gridBagConstraints);

    LBLFoilcD.setText("Drag coefficient");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilcD, gridBagConstraints);

    TXTFoilcD.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilcD.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilcD, gridBagConstraints);

    TXTFoilcM.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilcM.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilcM, gridBagConstraints);

    LBLFoilcM.setText("Moment coefficient");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilcM, gridBagConstraints);

    LBLFoilRe.setText("Reynolds Number");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilRe, gridBagConstraints);

    TXTFoilRe.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilRe.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilRe, gridBagConstraints);

    LBLFoilS.setText("Foil Area (m^2)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilS, gridBagConstraints);

    TXTFoilS.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilS.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilS, gridBagConstraints);

    TXTFoilC.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilC.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilC, gridBagConstraints);

    LBLFoilC.setText("Foil Chord (m)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilC, gridBagConstraints);

    LBLFoilA.setText("Aspect Ratio");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilA, gridBagConstraints);

    TXTFoilA.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilA.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilA, gridBagConstraints);

    TXTFoilE.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilE.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilE, gridBagConstraints);

    LBLFoilE.setText("Efficiency");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilE, gridBagConstraints);

    LBLFoilWS.setText("Foil Span (m)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilWS, gridBagConstraints);

    TXTFoilWS.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilWS.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilWS, gridBagConstraints);

    LBLFoilMach.setText("Mach Number");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilMach, gridBagConstraints);

    TXTFoilMach.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMach.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilMach, gridBagConstraints);

    LBLFoilcDe.setText("Parasite drag coefficient");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilcDe, gridBagConstraints);

    TXTFoilcDe.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilcDe.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilcDe, gridBagConstraints);

    LBLFoilcDi.setText("Induced drag coefficient");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilcDi, gridBagConstraints);

    TXTFoilcDi.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilcDi.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilcDi, gridBagConstraints);

    TXTFoilcDw.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilcDw.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilcDw, gridBagConstraints);

    LBLFoilcDw.setText("Wave drag coefficient");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilcDw, gridBagConstraints);

    PANForce.setLayout(new java.awt.GridBagLayout());

    PANForce.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Forces"));
    LBLFoilForce.setText("Force (N)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANForce.add(LBLFoilForce, gridBagConstraints);

    TXTFoilForceX.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilForceX.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilForceX, gridBagConstraints);

    TXTFoilForceY.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilForceY.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilForceY, gridBagConstraints);

    TXTFoilForceZ.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilForceZ.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilForceZ, gridBagConstraints);

    LBLForceX.setText("X (Nose to tail)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    PANForce.add(LBLForceX, gridBagConstraints);

    LBLForceY.setText("Y (Left to right)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    PANForce.add(LBLForceY, gridBagConstraints);

    LBLForceZ.setText("Z (Down to up)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    PANForce.add(LBLForceZ, gridBagConstraints);

    TXTFoilMomentFZ.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentFZ.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentFZ, gridBagConstraints);

    TXTFoilMomentFY.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentFY.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentFY, gridBagConstraints);

    TXTFoilMomentFX.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentFX.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentFX, gridBagConstraints);

    LBLFoilMomentF.setText("Moment from offset force (Nm)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANForce.add(LBLFoilMomentF, gridBagConstraints);

    LBLFoilMomentL.setText("Moment from aerodynamics (Nm)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANForce.add(LBLFoilMomentL, gridBagConstraints);

    TXTFoilMomentLX.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentLX.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentLX, gridBagConstraints);

    TXTFoilMomentLY.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentLY.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentLY, gridBagConstraints);

    TXTFoilMomentLZ.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentLZ.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentLZ, gridBagConstraints);

    LBLFoilMoment.setText("Total Moment (Nm)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANForce.add(LBLFoilMoment, gridBagConstraints);

    TXTFoilMomentX.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentX.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentX, gridBagConstraints);

    TXTFoilMomentY.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentY.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentY, gridBagConstraints);

    TXTFoilMomentZ.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilMomentZ.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANForce.add(TXTFoilMomentZ, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(PANForce, gridBagConstraints);

    LBLFoilq.setText("Dynamic Pressure (N/m^2)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilq, gridBagConstraints);

    TXTFoilq.setMinimumSize(new java.awt.Dimension(50, 19));
    TXTFoilq.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    PANFoil.add(TXTFoilq, gridBagConstraints);

    TXTFoilName.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TXTFoilNameActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    PANFoil.add(TXTFoilName, gridBagConstraints);

    LBLFoilName.setText("Foil Name");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    PANFoil.add(LBLFoilName, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(PANFoil, gridBagConstraints);

    TXTVel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TXTVelActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    getContentPane().add(TXTVel, gridBagConstraints);

    LBLVel.setText("Vel (m/s)");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    getContentPane().add(LBLVel, gridBagConstraints);

    SLDVel.setMaximum(50);
    SLDVel.setOrientation(javax.swing.JSlider.VERTICAL);
    SLDVel.setValue(0);
    SLDVel.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        SLDVelStateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(SLDVel, gridBagConstraints);

    BTNPrev.setText("< Prev");
    BTNPrev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        BTNPrevActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    getContentPane().add(BTNPrev, gridBagConstraints);

    BTNNext.setText("Next >");
    BTNNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        BTNNextActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    getContentPane().add(BTNNext, gridBagConstraints);

    pack();
  }//GEN-END:initComponents

  private void TXTFoilNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TXTFoilNameActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_TXTFoilNameActionPerformed

  private void BTNNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNNextActionPerformed
    // TODO add your handling code here:
    if(FMGPointer<TestVehicle.Foils.length) FMGPointer++;
    UpdateFoils();
  }//GEN-LAST:event_BTNNextActionPerformed

  private void BTNPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNPrevActionPerformed
    // TODO add your handling code here:
    if(FMGPointer>0) FMGPointer--;
    UpdateFoils();
  }//GEN-LAST:event_BTNPrevActionPerformed

  private void TXTVelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TXTVelActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_TXTVelActionPerformed

  private void SLDVelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SLDVelStateChanged
    // TODO add your handling code here:
    TXTVel.setText(Double.toString(Math.pow(10.0,SLDVel.getValue()/10.0)));
    UpdateFoils();
  }//GEN-LAST:event_SLDVelStateChanged

  private void SLDAoaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SLDAoaStateChanged
    // TODO add your handling code here:
    TXTAoa.setText(Double.toString(SLDAoa.getValue()));
    UpdateFoils();
  }//GEN-LAST:event_SLDAoaStateChanged

  private void SLDAltitudeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SLDAltitudeStateChanged
    // TODO add your handling code here:
    TXTAltitude.setText(Double.toString(SLDAltitude.getValue()));
    UpdateFoils();
  }//GEN-LAST:event_SLDAltitudeStateChanged
  
  private static NumberFormat N=new DecimalFormat("0.00E0");
  private void UpdateFoils() {
    MathVector RelWind=new MathVector(Math.cos(SLDAoa.getValue()*Math.PI/180.0),0,Math.sin(SLDAoa.getValue()*Math.PI/180.0));
    RelWind.mul(Math.pow(10.0,SLDVel.getValue()/10.0));
    MathVector Pos=new MathVector(0,0,6378137+SLDAltitude.getValue());
    ForceMomentGenerator FMG;
    if(FMGPointer>=TestVehicle.Foils.length) {
      FMG=TestVehicle;
      TXTFoilName.setText(TestVehicle.Name);
    } else {
      FMG=TestVehicle.Foils[FMGPointer];
      TXTFoilName.setText(TestVehicle.Names[FMGPointer]);
    }
    ForceMoment AF=FMG.getForceMoment(E,new MathState(Pos,RelWind));
    TXTFoilcD.setText(N.format(AF.cD));
    TXTFoilcL.setText(N.format(AF.cL));
    TXTFoilcM.setText(N.format(AF.cM));
    TXTFoilcDe.setText(N.format(AF.cDe));
    TXTFoilcDi.setText(N.format(AF.cDi));
    TXTFoilcDw.setText(N.format(AF.cDw));
    TXTFoilRe.setText(N.format(AF.Re));
    TXTFoilMach.setText(N.format(AF.Mach));
    TXTFoilq.setText(N.format(AF.q));

    TXTFoilS.setText(N.format(AF.FoilArea));
    TXTFoilA.setText(Double.toString(AF.Aspect));
    TXTFoilC.setText(Double.toString(AF.FoilChord));
    TXTFoilWS.setText(Double.toString(AF.FoilSpan));
    TXTFoilE.setText(Double.toString(AF.Efficiency));

    TXTFoilForceX.setText(Double.toString(AF.Force.X()));
    TXTFoilForceY.setText(Double.toString(AF.Force.Y()));
    TXTFoilForceZ.setText(Double.toString(AF.Force.Z()));

    TXTFoilMomentFX.setText(Double.toString(AF.MomentF.X()));
    TXTFoilMomentFY.setText(Double.toString(AF.MomentF.Y()));
    TXTFoilMomentFZ.setText(Double.toString(AF.MomentF.Z()));

    TXTFoilMomentLX.setText(Double.toString(AF.MomentL.X()));
    TXTFoilMomentLY.setText(Double.toString(AF.MomentL.Y()));
    TXTFoilMomentLZ.setText(Double.toString(AF.MomentL.Z()));

    TXTFoilMomentX.setText(Double.toString(AF.Moment.X()));
    TXTFoilMomentY.setText(Double.toString(AF.Moment.Y()));
    TXTFoilMomentZ.setText(Double.toString(AF.Moment.Z()));
  }
  
  public static void main(String args[]) {
    E=new Earth2();
    TestVehicle=new PegasusXL();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new WindTunnel().setVisible(true);
      }
    });
  }
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton BTNNext;
  private javax.swing.JButton BTNPrev;
  private javax.swing.JLabel LBLAltitude;
  private javax.swing.JLabel LBLAoa;
  private javax.swing.JLabel LBLFoilA;
  private javax.swing.JLabel LBLFoilC;
  private javax.swing.JLabel LBLFoilE;
  private javax.swing.JLabel LBLFoilForce;
  private javax.swing.JLabel LBLFoilMach;
  private javax.swing.JLabel LBLFoilMoment;
  private javax.swing.JLabel LBLFoilMomentF;
  private javax.swing.JLabel LBLFoilMomentL;
  private javax.swing.JLabel LBLFoilName;
  private javax.swing.JLabel LBLFoilRe;
  private javax.swing.JLabel LBLFoilS;
  private javax.swing.JLabel LBLFoilWS;
  private javax.swing.JLabel LBLFoilcD;
  private javax.swing.JLabel LBLFoilcDe;
  private javax.swing.JLabel LBLFoilcDi;
  private javax.swing.JLabel LBLFoilcDw;
  private javax.swing.JLabel LBLFoilcL;
  private javax.swing.JLabel LBLFoilcM;
  private javax.swing.JLabel LBLFoilq;
  private javax.swing.JLabel LBLForceX;
  private javax.swing.JLabel LBLForceY;
  private javax.swing.JLabel LBLForceZ;
  private javax.swing.JLabel LBLVel;
  private javax.swing.JPanel PANFoil;
  private javax.swing.JPanel PANForce;
  private javax.swing.JSlider SLDAltitude;
  private javax.swing.JSlider SLDAoa;
  private javax.swing.JSlider SLDVel;
  private javax.swing.JTextField TXTAltitude;
  private javax.swing.JTextField TXTAoa;
  private javax.swing.JTextField TXTFoilA;
  private javax.swing.JTextField TXTFoilC;
  private javax.swing.JTextField TXTFoilE;
  private javax.swing.JTextField TXTFoilForceX;
  private javax.swing.JTextField TXTFoilForceY;
  private javax.swing.JTextField TXTFoilForceZ;
  private javax.swing.JTextField TXTFoilMach;
  private javax.swing.JTextField TXTFoilMomentFX;
  private javax.swing.JTextField TXTFoilMomentFY;
  private javax.swing.JTextField TXTFoilMomentFZ;
  private javax.swing.JTextField TXTFoilMomentLX;
  private javax.swing.JTextField TXTFoilMomentLY;
  private javax.swing.JTextField TXTFoilMomentLZ;
  private javax.swing.JTextField TXTFoilMomentX;
  private javax.swing.JTextField TXTFoilMomentY;
  private javax.swing.JTextField TXTFoilMomentZ;
  private javax.swing.JTextField TXTFoilName;
  private javax.swing.JTextField TXTFoilRe;
  private javax.swing.JTextField TXTFoilS;
  private javax.swing.JTextField TXTFoilWS;
  private javax.swing.JTextField TXTFoilcD;
  private javax.swing.JTextField TXTFoilcDe;
  private javax.swing.JTextField TXTFoilcDi;
  private javax.swing.JTextField TXTFoilcDw;
  private javax.swing.JTextField TXTFoilcL;
  private javax.swing.JTextField TXTFoilcM;
  private javax.swing.JTextField TXTFoilq;
  private javax.swing.JTextField TXTVel;
  // End of variables declaration//GEN-END:variables
  private static Airfoils TestVehicle;
  private static Planet E;
  private static int FMGPointer=0;
}