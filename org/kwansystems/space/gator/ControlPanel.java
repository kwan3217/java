package org.kwansystems.space.gator;

import java.io.*;
import java.awt.event.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.jupiter.JupiterSatE5;
import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

public class ControlPanel extends javax.swing.JFrame {
  private static final long serialVersionUID = 3044566205727377989L;
  MathStateTime CurrentStateTime;
  Elements CurrentE;
  int Influence;
  Physics P;
  double[] SatSpheres=new double[5];
  Mapper mapper;
  PrintWriter Ouf; 
  javax.swing.Timer timer1;
  {
    for(int i=1;i<=4;i++) SatSpheres[i]=Physics.SphereOfInfluence(JupiterSatE5.a[i-1],JupiterSatE5.JupiterGM,JupiterSatE5.satGM[i]);
  }

  public ControlPanel() throws IOException {
    initComponents();
    timer1=new javax.swing.Timer(1000,new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        timer1ActionPerformed(evt);
      }
    });
    Ouf=new PrintWriter(new FileWriter("Log.txt"));
    mapper=new Mapper();
    P=null;
    timer1.start();
  }

  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    btnQuit = new javax.swing.JButton();
    panScanPlatform = new javax.swing.JPanel();
    lblAzimuth = new javax.swing.JLabel();
    txtAzimuth = new javax.swing.JTextField();
    btnSetAzimuth = new javax.swing.JButton();
    btnGetAzimuth = new javax.swing.JButton();
    lblFov = new javax.swing.JLabel();
    txtFov = new javax.swing.JTextField();
    btnSetFov = new javax.swing.JButton();
    btnGetFov = new javax.swing.JButton();
    lblElevation = new javax.swing.JLabel();
    txtElevation = new javax.swing.JTextField();
    btnSetElevation = new javax.swing.JButton();
    btnGetElevation = new javax.swing.JButton();
    btnPhotograph = new javax.swing.JButton();
    panTime = new javax.swing.JPanel();
    lblTimeRate = new javax.swing.JLabel();
    txtTimeRate = new javax.swing.JTextField();
    btnSetTimeRate = new javax.swing.JButton();
    btnGetTimeRate = new javax.swing.JButton();
    panThruster = new javax.swing.JPanel();
    lblThrAz = new javax.swing.JLabel();
    txtThrAz = new javax.swing.JTextField();
    btnSetThrAz = new javax.swing.JButton();
    btnGetThrAz = new javax.swing.JButton();
    lblThrAmt = new javax.swing.JLabel();
    txtThrAmt = new javax.swing.JTextField();
    btnSetThrAmt = new javax.swing.JButton();
    btnGetThrAmt = new javax.swing.JButton();
    btnThrustActive = new javax.swing.JToggleButton();
    cboUnits = new javax.swing.JComboBox();
    lblUnits = new javax.swing.JLabel();
    panNav = new javax.swing.JPanel();
    btnMap = new javax.swing.JButton();
    txtState = new javax.swing.JTextArea();
    btnState = new javax.swing.JButton();
    panGauss = new javax.swing.JPanel();
    lblGaussTarget = new javax.swing.JLabel();
    cboGaussTarget = new javax.swing.JComboBox();
    lblGaussTime = new javax.swing.JLabel();
    txtGaussTime = new javax.swing.JTextField();
    txtCourse = new javax.swing.JTextArea();
    btnGaussUpdate = new javax.swing.JToggleButton();
    jPanel1 = new javax.swing.JPanel();
    cboHohmanStart = new javax.swing.JComboBox();
    lblHohmanStart = new javax.swing.JLabel();
    cboHohmanTarget = new javax.swing.JComboBox();
    lblHohmanTarget = new javax.swing.JLabel();
    txtHohmanCalc = new javax.swing.JTextArea();
    btnHohmanCalculate = new javax.swing.JButton();

    getContentPane().setLayout(new java.awt.GridBagLayout());

    setTitle("Galilean Ranger Control Panel");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        exitForm(evt);
      }
    });

    btnQuit.setText("Stop Physics Package");
    btnQuit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnQuitActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    getContentPane().add(btnQuit, gridBagConstraints);

    panScanPlatform.setLayout(new java.awt.GridBagLayout());

    panScanPlatform.setBorder(new javax.swing.border.TitledBorder("Scan Platform"));
    lblAzimuth.setText("Azimuth");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panScanPlatform.add(lblAzimuth, gridBagConstraints);

    txtAzimuth.setText("0.0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panScanPlatform.add(txtAzimuth, gridBagConstraints);

    btnSetAzimuth.setText("set");
    btnSetAzimuth.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSetAzimuthActionPerformed(evt);
      }
    });

    panScanPlatform.add(btnSetAzimuth, new java.awt.GridBagConstraints());

    btnGetAzimuth.setText("get");
    btnGetAzimuth.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGetAzimuthActionPerformed(evt);
      }
    });

    panScanPlatform.add(btnGetAzimuth, new java.awt.GridBagConstraints());

    lblFov.setText("Fov");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panScanPlatform.add(lblFov, gridBagConstraints);

    txtFov.setText("0.0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panScanPlatform.add(txtFov, gridBagConstraints);

    btnSetFov.setText("set");
    btnSetFov.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSetFovActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    panScanPlatform.add(btnSetFov, gridBagConstraints);

    btnGetFov.setText("get");
    btnGetFov.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGetFovActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    panScanPlatform.add(btnGetFov, gridBagConstraints);

    lblElevation.setText("Elevation");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panScanPlatform.add(lblElevation, gridBagConstraints);

    txtElevation.setText("0.0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panScanPlatform.add(txtElevation, gridBagConstraints);

    btnSetElevation.setText("set");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    panScanPlatform.add(btnSetElevation, gridBagConstraints);

    btnGetElevation.setText("get");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    panScanPlatform.add(btnGetElevation, gridBagConstraints);

    btnPhotograph.setText("Photograph");
    btnPhotograph.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnPhotographActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    panScanPlatform.add(btnPhotograph, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    getContentPane().add(panScanPlatform, gridBagConstraints);

    panTime.setLayout(new java.awt.GridBagLayout());

    panTime.setBorder(new javax.swing.border.TitledBorder("Time Control"));
    lblTimeRate.setText("Time Rate");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panTime.add(lblTimeRate, gridBagConstraints);

    txtTimeRate.setText("0.0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panTime.add(txtTimeRate, gridBagConstraints);

    btnSetTimeRate.setText("set");
    btnSetTimeRate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSetTimeRateActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    panTime.add(btnSetTimeRate, gridBagConstraints);

    btnGetTimeRate.setText("get");
    btnGetTimeRate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGetTimeRateActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    panTime.add(btnGetTimeRate, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(panTime, gridBagConstraints);

    panThruster.setLayout(new java.awt.GridBagLayout());

    panThruster.setBorder(new javax.swing.border.TitledBorder("Thruster"));
    lblThrAz.setText("Azimuth");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panThruster.add(lblThrAz, gridBagConstraints);

    txtThrAz.setText("0.0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panThruster.add(txtThrAz, gridBagConstraints);

    btnSetThrAz.setText("set");
    btnSetThrAz.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSetThrAzActionPerformed(evt);
      }
    });

    panThruster.add(btnSetThrAz, new java.awt.GridBagConstraints());

    btnGetThrAz.setText("get");
    btnGetThrAz.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGetThrAzActionPerformed(evt);
      }
    });

    panThruster.add(btnGetThrAz, new java.awt.GridBagConstraints());

    lblThrAmt.setText("Thrust");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panThruster.add(lblThrAmt, gridBagConstraints);

    txtThrAmt.setText("0.0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panThruster.add(txtThrAmt, gridBagConstraints);

    btnSetThrAmt.setText("set");
    btnSetThrAmt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSetThrAmtActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    panThruster.add(btnSetThrAmt, gridBagConstraints);

    btnGetThrAmt.setText("get");
    btnGetThrAmt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGetThrAmtActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    panThruster.add(btnGetThrAmt, gridBagConstraints);

    btnThrustActive.setText("Fire!");
    btnThrustActive.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        btnThrustActiveItemStateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    panThruster.add(btnThrustActive, gridBagConstraints);

    cboUnits.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "g", "m/s^2", "km/s/hr", "km/s/min", "km/s^2" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    panThruster.add(cboUnits, gridBagConstraints);

    lblUnits.setText("Units");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panThruster.add(lblUnits, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    getContentPane().add(panThruster, gridBagConstraints);

    panNav.setLayout(new java.awt.GridBagLayout());

    panNav.setBorder(new javax.swing.border.TitledBorder("Navigation"));
    btnMap.setText("Map");
    btnMap.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnMapActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    panNav.add(btnMap, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    panNav.add(txtState, gridBagConstraints);

    btnState.setText("State");
    btnState.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStateActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    panNav.add(btnState, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(panNav, gridBagConstraints);

    panGauss.setLayout(new java.awt.GridBagLayout());

    panGauss.setBorder(new javax.swing.border.TitledBorder("Gauss Targeting"));
    lblGaussTarget.setText("Target");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panGauss.add(lblGaussTarget, gridBagConstraints);

    cboGaussTarget.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Io Center", "Europa Center", "Ganymede Center", "Callisto Center" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panGauss.add(cboGaussTarget, gridBagConstraints);

    lblGaussTime.setText("Intercept Time");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    panGauss.add(lblGaussTime, gridBagConstraints);

    txtGaussTime.setText(Double.toString(new Time().JD()+2));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    panGauss.add(txtGaussTime, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    panGauss.add(txtCourse, gridBagConstraints);

    btnGaussUpdate.setText("Update");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    panGauss.add(btnGaussUpdate, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(panGauss, gridBagConstraints);

    jPanel1.setLayout(new java.awt.GridBagLayout());

    jPanel1.setBorder(new javax.swing.border.TitledBorder("Hohman Targeting"));
    cboHohmanStart.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Io", "Europa", "Ganymede", "Callisto" }));
    cboHohmanStart.setSelectedIndex(2);
    cboHohmanStart.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cboHohmanStartActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(cboHohmanStart, gridBagConstraints);

    lblHohmanStart.setText("Start");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(lblHohmanStart, gridBagConstraints);

    cboHohmanTarget.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Io", "Europa", "Ganymede", "Callisto" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(cboHohmanTarget, gridBagConstraints);

    lblHohmanTarget.setText("Target");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(lblHohmanTarget, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel1.add(txtHohmanCalc, gridBagConstraints);

    btnHohmanCalculate.setText("Calculate");
    btnHohmanCalculate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnHohmanCalculateActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    jPanel1.add(btnHohmanCalculate, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(jPanel1, gridBagConstraints);

    pack();
  }
  // </editor-fold>//GEN-END:initComponents

  private void cboHohmanStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHohmanStartActionPerformed
// TODO add your handling code here:
  }//GEN-LAST:event_cboHohmanStartActionPerformed

  private void btnHohmanCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHohmanCalculateActionPerformed
    MathVector R1=P.Movers[cboHohmanStart.getSelectedIndex()].getState(CurrentStateTime.T).R();
    MathVector R2=P.Movers[cboHohmanTarget.getSelectedIndex()].getState(CurrentStateTime.T).R();
    double A=(R1.length()+R2.length())/2;                              //Transfer semimajor axis
    double T=Math.PI/Math.sqrt(JupiterSatE5.JupiterGM)*Math.sqrt(A*A*A);     //Transfer (Half-)Period
    double T1=2*Math.PI/Math.sqrt(JupiterSatE5.JupiterGM)*Math.pow(R1.length(),1.5); //Target Period
    double T2=2*Math.PI/Math.sqrt(JupiterSatE5.JupiterGM)*Math.pow(R2.length(),1.5); //Target Period
    double RelPeriod=T/T2;
    double Dropback=2*Math.PI*(RelPeriod-Math.floor(RelPeriod));
    double L1=Math.atan2(R1.Z(),R1.X()); if(L1<0)L1=L1+2*Math.PI;
    double L2=Math.atan2(R2.Z(),R2.X()); if(L2<0)L2=L2+2*Math.PI;
    if(L2<L1) L2=L2+2*Math.PI;
    double F1=2*Math.PI/T1;  //Mean motion in radians/time unit
    double F2=2*Math.PI/T2;  //Mean motion in radians/time unit
    double Diff=F2-F1;       //Target advance rate, radians/time unit
    double MeasDropback=Math.PI-(L2-L1);
    if(MeasDropback<0)MeasDropback+=2*Math.PI;
    double ToGo=MeasDropback-Dropback;
    double TToGo=ToGo/Diff;
    Time TimeToGo=Time.add(CurrentStateTime.T,TToGo);
//    double Rate=
    txtHohmanCalc.setText("Transfer A: "+A+
                        "\nTransfer T: "+T+
                        "\nTarget Period: "+T2+
                        "\nDropback: "+Math.toDegrees(Dropback)+
                        "\nL1: "+Math.toDegrees(L1)+
                        "\nL2: "+Math.toDegrees(L2)+
                        "\nMeasDropback: "+Math.toDegrees(MeasDropback)+
                        "\nToGo: "+Math.toDegrees(ToGo)+
                        "\nLaunch at: "+TimeToGo.toString());
  }//GEN-LAST:event_btnHohmanCalculateActionPerformed

  private void btnGaussCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGaussCalculateActionPerformed
    // Add your handling code here:
    MathVector R1=CurrentStateTime.S.R();
    Time TInt=new Time(Double.parseDouble(txtGaussTime.getText()));
    MathVector R2=P.Movers[cboGaussTarget.getSelectedIndex()].getState(TInt).R();
    MathVector Dist=MathVector.sub(R2,R1);
    MathVector V1;
    try {
      V1=KeplerFG.target(R1,R2,TInt.get()-CurrentStateTime.T.get(),JupiterSatE5.JupiterGM,1)[0].V();
    } catch(ArithmeticException E) {txtCourse.setText(E.getMessage()); return;}
    MathVector VDiff=MathVector.sub(V1,CurrentStateTime.S.V());
    Elements InterceptE=new Elements();
    InterceptE.PosVelToEle(new MathStateTime(new MathState(R1,V1),CurrentStateTime.T),JupiterSatE5.JupiterGM,"km");
    txtCourse.setText("Intercept Time: "+TInt.toString()+"\nDistance: "+Dist+"\nCourse Correction required: "+VDiff.toString()+"\nIntercept Orbit: \n"+InterceptE.toString());
  }//GEN-LAST:event_btnGaussCalculateActionPerformed

  private void getCurrentStateTime() {
    CurrentStateTime=P.getStateTime();
  }
  private void btnStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStateActionPerformed
    getCurrentStateTime();
    StringBuffer S=new StringBuffer(CurrentStateTime.toString()+"\n");
    S.append("Dominant Body: ");
    MathState[] Diff=new MathState[5];
    Influence=0; //Jupiter
    String Dominant="Jupiter";
    double GM=JupiterSatE5.JupiterGM;
    MathState CenterState=new MathState(0,0,0,0,0,0);
    MathStateTime Rel=CurrentStateTime;
    double Surface=JupiterSatE5.JupiterRadius;
    for(int i=1;i<=4;i++) {
      Diff[i]=(MathState)MathVector.sub(CurrentStateTime.S,P.Movers[i].getState(CurrentStateTime.T));
      if(Diff[i].R().length()<SatSpheres[i]) {
        Dominant=JupiterSatE5.SatNames[i];
        Influence=i;
        CenterState=P.Movers[i].getState(CurrentStateTime.T);
        Rel=new MathStateTime(Diff[i],CurrentStateTime.T);
        Surface=JupiterSatE5.satRadius[i];
      }
    } 
    GM=JupiterSatE5.satGM[Influence];
    S.append(Dominant+CenterState.toString()+"\n");
    if(Influence>0) {
      S.append("Relative: "+Diff[Influence].toString()+"\n"); 
    }
    CurrentE=new Elements();
    CurrentE.PosVelToEle(Rel,GM,"km");
    if(CurrentE.Periapse<Surface) S.append("Warning! Crash Orbit! (Periapsis alt="+(CurrentE.Periapse-Surface)+")\n");
    S.append(CurrentE.toString()+"\n");
    Ouf.println(CurrentStateTime.toString());
    Ouf.println(S);
    txtState.setText(S.toString());
  }//GEN-LAST:event_btnStateActionPerformed

  public void writeStateGuts(PrintWriter Ouf) throws IOException {
    Ouf.println("//Control Panel generated scene for a Map");
    Ouf.println("//Time: "+CurrentStateTime.T.toString());
    Ouf.println("#declare JupiterPos=<0,0,0>;");
    Ouf.println("#declare JupiterVel=<0,0,0>;");
    for(int i=0;i<4;i++) {
      Ouf.println("#declare "+JupiterSatE5.SatNames[i]+"Pos="+P.Movers[i].getState(CurrentStateTime.T).R()+";");
      Ouf.println("#declare "+JupiterSatE5.SatNames[i]+"Vel="+P.Movers[i].getState(CurrentStateTime.T).V()+";");
    }
    Ouf.println("#declare ProbePos="+CurrentStateTime.S.R()+";");
    Ouf.println("#declare ProbeVel="+CurrentStateTime.S.V()+";");
  }
  public void writeOrbitGuts(PrintWriter Ouf) throws IOException {
    Ouf.println("#declare A="+CurrentE.A+";");
    Ouf.println("#declare E="+CurrentE.E+";");
    Ouf.println("#declare I="+CurrentE.I+";");
    Ouf.println("#declare LAN="+CurrentE.LAN+";");
    Ouf.println("#declare AP="+CurrentE.AP+";");
    Ouf.println("#declare Tp="+CurrentE.TP.JD()+";");
  }
  public void Map() throws IOException {
    if(!mapper.isShowing()) mapper.show();
  }
  public void writeState() throws IOException {
    PrintWriter Ouf=new PrintWriter(new FileWriter("state.inc"));
    writeStateGuts(Ouf);
    writeOrbitGuts(Ouf);
    Ouf.close();
  }
  private void btnGetThrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetThrAmtActionPerformed
    double ret=P.getThrustAmount();
    double r=ret/getAccFactor();
    txtThrAmt.setText(Double.toString(r));
  }//GEN-LAST:event_btnGetThrAmtActionPerformed

  private double[] AccFactors={9.8/1000.0, //g (=exactly 9.8m/s^2)
                               1.0/1000.0, //m/s^2
                               1.0/3600.0, //km/s/hr
                               1.0/60, //km/s/min
                               1.0 //km/s^2
                              };
  private double getAccFactor() {
    //returns reciprocal of conversion factor
    //"g", "m/s^2", "km/day^2", "km/s/hr", "km/s/min", "km/s^2"
    return AccFactors[cboUnits.getSelectedIndex()];
  }
  private void btnSetThrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetThrAmtActionPerformed
    P.setThrustAmount(Double.parseDouble(txtThrAmt.getText())*getAccFactor());
  }//GEN-LAST:event_btnSetThrAmtActionPerformed

  private void btnGetThrAzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetThrAzActionPerformed
    double ret=P.getThrustAzimuth();
    txtThrAz.setText(Double.toString(ret));
  }//GEN-LAST:event_btnGetThrAzActionPerformed

  private void btnSetThrAzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetThrAzActionPerformed
    P.setThrustAzimuth(Double.parseDouble(txtThrAz.getText()));
  }//GEN-LAST:event_btnSetThrAzActionPerformed

  private void btnThrustActiveItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnThrustActiveItemStateChanged
    P.setThrustActive(evt.getStateChange()==ItemEvent.SELECTED);
  }//GEN-LAST:event_btnThrustActiveItemStateChanged

  private void btnGetTimeRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetTimeRateActionPerformed
    double ret=P.getTimeRate();
    txtTimeRate.setText(Double.toString(ret));
  }//GEN-LAST:event_btnGetTimeRateActionPerformed

  private void btnSetTimeRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetTimeRateActionPerformed
   P.setTimeRate(Double.parseDouble(txtTimeRate.getText()));
  }//GEN-LAST:event_btnSetTimeRateActionPerformed

  private void btnGetFovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetFovActionPerformed
    double ret=P.getScanPlatformFov();
    txtFov.setText(Double.toString(ret));
  }//GEN-LAST:event_btnGetFovActionPerformed

  private void btnSetFovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetFovActionPerformed
    P.setScanPlatformFov(Double.parseDouble(txtFov.getText()));
  }//GEN-LAST:event_btnSetFovActionPerformed

  private void btnSetAzimuthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetAzimuthActionPerformed
    P.setScanPlatformAz(Double.parseDouble(txtAzimuth.getText()));
  }//GEN-LAST:event_btnSetAzimuthActionPerformed

  private void btnQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitActionPerformed
    P.Quit();
  }//GEN-LAST:event_btnQuitActionPerformed

  private void btnGetAzimuthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetAzimuthActionPerformed
    double ret=P.getScanPlatformAz();
    txtAzimuth.setText(Double.toString(ret));
  }//GEN-LAST:event_btnGetAzimuthActionPerformed

  private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
    try {
      getCurrentStateTime();
      Map();
    } catch ( Exception e ) { e.printStackTrace(); }
  }//GEN-LAST:event_btnMapActionPerformed
 
  private void timer1ActionPerformed(java.awt.event.ActionEvent evt) {
    try {
      if(P==null) P=new Physics();
      btnStateActionPerformed(evt);
      btnHohmanCalculateActionPerformed(evt);
      if(mapper.isShowing()) mapper.Map(P.Movers,CurrentStateTime, Influence, CurrentE);
      if(btnGaussUpdate.isSelected()) btnGaussCalculateActionPerformed(evt);
    } catch ( Exception e ) { e.printStackTrace(); }
  }

  private void btnPhotographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhotographActionPerformed
    try {
      P.Photograph();
    } catch ( Exception e ) { e.printStackTrace(); }
  }//GEN-LAST:event_btnPhotographActionPerformed

  private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
    System.exit(0);
  }//GEN-LAST:event_exitForm

  public static void main(String args[]) throws IOException {
    new ControlPanel().show();
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JToggleButton btnGaussUpdate;
  private javax.swing.JButton btnGetAzimuth;
  private javax.swing.JButton btnGetElevation;
  private javax.swing.JButton btnGetFov;
  private javax.swing.JButton btnGetThrAmt;
  private javax.swing.JButton btnGetThrAz;
  private javax.swing.JButton btnGetTimeRate;
  private javax.swing.JButton btnHohmanCalculate;
  private javax.swing.JButton btnMap;
  private javax.swing.JButton btnPhotograph;
  private javax.swing.JButton btnQuit;
  private javax.swing.JButton btnSetAzimuth;
  private javax.swing.JButton btnSetElevation;
  private javax.swing.JButton btnSetFov;
  private javax.swing.JButton btnSetThrAmt;
  private javax.swing.JButton btnSetThrAz;
  private javax.swing.JButton btnSetTimeRate;
  private javax.swing.JButton btnState;
  private javax.swing.JToggleButton btnThrustActive;
  private javax.swing.JComboBox cboGaussTarget;
  private javax.swing.JComboBox cboHohmanStart;
  private javax.swing.JComboBox cboHohmanTarget;
  private javax.swing.JComboBox cboUnits;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JLabel lblAzimuth;
  private javax.swing.JLabel lblElevation;
  private javax.swing.JLabel lblFov;
  private javax.swing.JLabel lblGaussTarget;
  private javax.swing.JLabel lblGaussTime;
  private javax.swing.JLabel lblHohmanStart;
  private javax.swing.JLabel lblHohmanTarget;
  private javax.swing.JLabel lblThrAmt;
  private javax.swing.JLabel lblThrAz;
  private javax.swing.JLabel lblTimeRate;
  private javax.swing.JLabel lblUnits;
  private javax.swing.JPanel panGauss;
  private javax.swing.JPanel panNav;
  private javax.swing.JPanel panScanPlatform;
  private javax.swing.JPanel panThruster;
  private javax.swing.JPanel panTime;
  private javax.swing.JTextField txtAzimuth;
  private javax.swing.JTextArea txtCourse;
  private javax.swing.JTextField txtElevation;
  private javax.swing.JTextField txtFov;
  private javax.swing.JTextField txtGaussTime;
  private javax.swing.JTextArea txtHohmanCalc;
  private javax.swing.JTextArea txtState;
  private javax.swing.JTextField txtThrAmt;
  private javax.swing.JTextField txtThrAz;
  private javax.swing.JTextField txtTimeRate;
  // End of variables declaration//GEN-END:variables

}
