package org.kwansystems.coaster;

import org.kwansystems.tools.bezier.Bezier;
import org.kwansystems.tools.vector.*;

import javax.swing.*;
import java.util.*;

public class TrackEditor extends javax.swing.JFrame {
  private static final long serialVersionUID = 1070295913379964217L;
  public TrackEditor() {
    initComponents();
  }
  
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    XYPanel = new javax.swing.JPanel();
    XZPanel = new javax.swing.JPanel();
    YZPanel = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    SegmentLabel = new javax.swing.JLabel();
    SegmentSpinner = new javax.swing.JSpinner();
    KeepTogetherCheck = new javax.swing.JCheckBox();
    KeepSlopeCheck = new javax.swing.JCheckBox();
    XLabel = new javax.swing.JLabel();
    YLabel = new javax.swing.JLabel();
    ZLabel = new javax.swing.JLabel();
    Pt0Label = new javax.swing.JLabel();
    Pt1Label = new javax.swing.JLabel();
    Pt2Label = new javax.swing.JLabel();
    Pt3Label = new javax.swing.JLabel();
    X0 = new javax.swing.JSpinner();
    Y0 = new javax.swing.JSpinner();
    Z0 = new javax.swing.JSpinner();
    X1 = new javax.swing.JSpinner();
    Y1 = new javax.swing.JSpinner();
    Z1 = new javax.swing.JSpinner();
    X2 = new javax.swing.JSpinner();
    Y2 = new javax.swing.JSpinner();
    Z2 = new javax.swing.JSpinner();
    X3 = new javax.swing.JSpinner();
    Y3 = new javax.swing.JSpinner();
    Z3 = new javax.swing.JSpinner();
    AddSegmentButton = new javax.swing.JButton();
    RemoveSegmentButton = new javax.swing.JButton();
    LoadButton = new javax.swing.JButton();
    SaveButton = new javax.swing.JButton();
    ExportButton = new javax.swing.JButton();
    CfLabel = new javax.swing.JLabel();
    CfSpinner = new javax.swing.JSpinner();
    VpSpinner = new javax.swing.JSpinner();
    VpLabel = new javax.swing.JLabel();

    getContentPane().setLayout(new java.awt.GridBagLayout());

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Track Designer");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowOpened(java.awt.event.WindowEvent evt) {
        formWindowOpened(evt);
      }
    });

    XYPanel.setLayout(new java.awt.GridBagLayout());

    XYCanvas=new KBezierCanvas(0,1);
    XYPanel.setBackground(new java.awt.Color(0, 0, 0));
    XYPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
    XYPanel.setPreferredSize(new java.awt.Dimension(400, 400));
    XYCanvas.setBackground(new java.awt.Color(0, 0, 0));
    XYCanvas.setPreferredSize(new java.awt.Dimension(400, 400));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    XYPanel.add(XYCanvas, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(XYPanel, gridBagConstraints);

    XZPanel.setLayout(new java.awt.GridBagLayout());

    XZCanvas=new KBezierCanvas(0,2);
    XZPanel.setBackground(new java.awt.Color(0, 0, 0));
    XZPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
    XZPanel.setPreferredSize(new java.awt.Dimension(400, 400));
    XZCanvas.setBackground(new java.awt.Color(0, 0, 0));
    XZCanvas.setPreferredSize(new java.awt.Dimension(400, 400));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    XZPanel.add(XZCanvas, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(XZPanel, gridBagConstraints);

    YZPanel.setLayout(new java.awt.GridBagLayout());

    YZCanvas=new KBezierCanvas(1,2);
    YZPanel.setBackground(new java.awt.Color(0, 0, 0));
    YZPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
    YZPanel.setPreferredSize(new java.awt.Dimension(400, 400));
    YZCanvas.setBackground(new java.awt.Color(0, 0, 0));
    YZCanvas.setPreferredSize(new java.awt.Dimension(400, 400));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    YZPanel.add(YZCanvas, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(YZPanel, gridBagConstraints);

    jPanel1.setLayout(new java.awt.GridBagLayout());

    jPanel1.setBorder(new javax.swing.border.TitledBorder("Track Properties"));
    SegmentLabel.setText("Segment");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(SegmentLabel, gridBagConstraints);

    SegmentSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        SegmentSpinnerStateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(SegmentSpinner, gridBagConstraints);

    KeepTogetherCheck.setSelected(true);
    KeepTogetherCheck.setText("Keep endpoints together");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel1.add(KeepTogetherCheck, gridBagConstraints);

    KeepSlopeCheck.setSelected(true);
    KeepSlopeCheck.setText("Keep current slope");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel1.add(KeepSlopeCheck, gridBagConstraints);

    XLabel.setText("X");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(XLabel, gridBagConstraints);

    YLabel.setText("Y");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(YLabel, gridBagConstraints);

    ZLabel.setText("Z");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(ZLabel, gridBagConstraints);

    Pt0Label.setText("Terminal 0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(Pt0Label, gridBagConstraints);

    Pt1Label.setText("Control 1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(Pt1Label, gridBagConstraints);

    Pt2Label.setText("Control 2");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(Pt2Label, gridBagConstraints);

    Pt3Label.setText("Terminal 3");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(Pt3Label, gridBagConstraints);

    X0.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    X0.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        X0StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(X0, gridBagConstraints);

    Y0.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Y0.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Y0StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Y0, gridBagConstraints);

    Z0.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Z0.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Z0StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Z0, gridBagConstraints);

    X1.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    X1.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        X1StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(X1, gridBagConstraints);

    Y1.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Y1.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Y1StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Y1, gridBagConstraints);

    Z1.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Z1.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Z1StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Z1, gridBagConstraints);

    X2.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    X2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        X2StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(X2, gridBagConstraints);

    Y2.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Y2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Y2StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Y2, gridBagConstraints);

    Z2.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Z2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Z2StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Z2, gridBagConstraints);

    X3.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    X3.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        X3StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(X3, gridBagConstraints);

    Y3.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    Y3.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Y3StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Y3, gridBagConstraints);

    Z3.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    CoordSpinner=new JSpinner[][] {
      {X0,Y0,Z0},
      {X1,Y1,Z1},
      {X2,Y2,Z2},
      {X3,Y3,Z3}
    };

    Z3.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        Z3StateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(Z3, gridBagConstraints);

    AddSegmentButton.setText("Add Segment");
    AddSegmentButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        AddSegmentButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    jPanel1.add(AddSegmentButton, gridBagConstraints);

    RemoveSegmentButton.setText("Remove Last Segment");
    RemoveSegmentButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        RemoveSegmentButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    jPanel1.add(RemoveSegmentButton, gridBagConstraints);

    LoadButton.setText("Load");
    LoadButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        LoadButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    jPanel1.add(LoadButton, gridBagConstraints);

    SaveButton.setText("Save");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    jPanel1.add(SaveButton, gridBagConstraints);

    ExportButton.setText("Export to POV");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    jPanel1.add(ExportButton, gridBagConstraints);

    CfLabel.setText("Friction Coeff");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(CfLabel, gridBagConstraints);

    CfSpinner.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(CfSpinner, gridBagConstraints);

    VpSpinner.setModel(new SpinnerNumberModel(0.0,-1000.0,1000.0,0.1));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel1.add(VpSpinner, gridBagConstraints);

    VpLabel.setText("Preferred Speed");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel1.add(VpLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    getContentPane().add(jPanel1, gridBagConstraints);

    pack();
  }
  // </editor-fold>//GEN-END:initComponents

    private void LoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_LoadButtonActionPerformed

    private void RemoveSegmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveSegmentButtonActionPerformed
      Track.remove(Track.size()-1);
    }//GEN-LAST:event_RemoveSegmentButtonActionPerformed

    private void AddSegmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddSegmentButtonActionPerformed
      Bezier LastSeg=Track.get(Track.size()-1);
      MathVector NewSlope=MathVector.sub(LastSeg.R3,LastSeg.R2);
      MathVector R0=new MathVector(LastSeg.R3);
      MathVector R1=MathVector.add(R0,NewSlope);
      MathVector R2=MathVector.add(R1,NewSlope);
      MathVector R3=MathVector.add(R2,NewSlope);
      Track.add(new Bezier(R0,R1,R2,R3));
      XYCanvas.repaint();
      XZCanvas.repaint();
      YZCanvas.repaint();
    }//GEN-LAST:event_AddSegmentButtonActionPerformed

  private void Y3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Y3StateChanged
    movePoint(3,1);
  }//GEN-LAST:event_Y3StateChanged

  private void X3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_X3StateChanged
    movePoint(3,0);
  }//GEN-LAST:event_X3StateChanged

  private void Z2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Z2StateChanged
    movePoint(2,2);
  }//GEN-LAST:event_Z2StateChanged

  private void Y2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Y2StateChanged
    movePoint(2,1);
  }//GEN-LAST:event_Y2StateChanged

  private void X2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_X2StateChanged
    movePoint(2,0);
  }//GEN-LAST:event_X2StateChanged

  private void Z1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Z1StateChanged
    movePoint(1,2);
  }//GEN-LAST:event_Z1StateChanged

  private void Y1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Y1StateChanged
    movePoint(1,1);
  }//GEN-LAST:event_Y1StateChanged

  private void X1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_X1StateChanged
    movePoint(1,0);
  }//GEN-LAST:event_X1StateChanged

  private void Z0StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Z0StateChanged
    movePoint(0,2);
  }//GEN-LAST:event_Z0StateChanged

  private void Y0StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Y0StateChanged
    movePoint(0,1);
  }//GEN-LAST:event_Y0StateChanged

  private void Z3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Z3StateChanged
    movePoint(3,2);
  }//GEN-LAST:event_Z3StateChanged

  private void X0StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_X0StateChanged
    movePoint(0,0);
  }//GEN-LAST:event_X0StateChanged

  private void setSegment(int newSegment) {
    segment=newSegment;
    X0.setValue(new Double(Track.get(segment).R0.X()));
    Y0.setValue(new Double(Track.get(segment).R0.Y()));
    Z0.setValue(new Double(Track.get(segment).R0.Z()));
    X1.setValue(new Double(Track.get(segment).R1.X()));
    Y1.setValue(new Double(Track.get(segment).R1.Y()));
    Z1.setValue(new Double(Track.get(segment).R1.Z()));
    X2.setValue(new Double(Track.get(segment).R2.X()));
    Y2.setValue(new Double(Track.get(segment).R2.Y()));
    Z2.setValue(new Double(Track.get(segment).R2.Z()));
    X3.setValue(new Double(Track.get(segment).R3.X()));
    Y3.setValue(new Double(Track.get(segment).R3.Y()));
    Z3.setValue(new Double(Track.get(segment).R3.Z()));
  }
  
  private void movePoint(int Point, int Coordinate) {
    double newC=((Double)(CoordSpinner[Point][Coordinate].getValue())).doubleValue();
    double oldC=Track.get(segment).R[Point].get(Coordinate);
    Track.get(segment).R[Point].set(Coordinate,newC);
    fixEndpoint(segment,Point,Coordinate,oldC,newC);
    fixTangentInterior(segment,Point,Coordinate,oldC,newC);
    fixTangentExterior(segment,Point,Coordinate,oldC,newC);
    fixTangentControl(segment,Point,Coordinate,oldC,newC);
    Track.get(segment).recalcCoeff();
    XYCanvas.repaint();
    XZCanvas.repaint();
    YZCanvas.repaint();
  }
  
  private void fixEndpoint(int segment, int Point, int Coordinate, double oldC, double newC) {
    int segMatch,PointMatch;
    if(!KeepTogetherCheck.isSelected()) return; //Keep together not selected
    if(Point==0) { //Initial point
      if(segment==0) return;                    //No matching endpoint -- track beginning
      segMatch=segment-1; 
      PointMatch=3; 
    } else if(Point==3) { //Final point
      if(segment==Track.size()-1) return;      //No matching endpoint -- track end
      segMatch=segment+1; 
      PointMatch=0; 
    } else { //Control point
      return;                                  //Not an endpoint
    }
    double Diff=newC-oldC;
    Track.get(segMatch).R[PointMatch].set(Coordinate,Diff+Track.get(segMatch).R[PointMatch].get(Coordinate));
    Track.get(segMatch).recalcCoeff();
  }

  private void fixTangentInterior(int segment, int Point, int Coordinate, double oldC, double newC) {
    int PointMatch;
    if(!KeepSlopeCheck.isSelected()) return; //Keep together not selected
    if(Point==0) { //Initial point
      PointMatch=1; 
    } else if(Point==3) { //Final point
      PointMatch=2; 
    } else { //Control point
      return;                                  //Not an endpoint
    }
    double Diff=newC-oldC;
    double newC2=Diff+Track.get(segment).R[PointMatch].get(Coordinate);
    Track.get(segment).R[PointMatch].set(Coordinate,newC2);
    Track.get(segment).recalcCoeff();
    CoordSpinner[PointMatch][Coordinate].setValue(new Double(newC2));
  }

  private void fixTangentExterior(int segment, int Point, int Coordinate, double oldC, double newC) {
    int segMatch,PointMatch;
    if(!KeepSlopeCheck.isSelected()) return; //Keep together not selected
    if(Point==0) { //Initial point
      if(segment==0) return;                    //No matching point -- track beginning
      segMatch=segment-1;   
      PointMatch=2;  //Matching point is previous second control point
    } else if(Point==3) { //Final point
      if(segment==Track.size()-1) return;      //No matching point -- track end
      segMatch=segment+1; 
      PointMatch=1; //Matching point is next first control point
    } else { //Control point
      return;                                  //Not an endpoint
    }
    double Diff=newC-oldC;
    Track.get(segMatch).R[PointMatch].set(Coordinate,Diff+Track.get(segMatch).R[PointMatch].get(Coordinate));
    Track.get(segMatch).recalcCoeff();
  }

  private void fixTangentControl(int segment, int Point, int Coordinate, double oldC, double newC) {
    int segMatch,PointMatch;
    if(!KeepSlopeCheck.isSelected()) return; //Keep together not selected
    if(Point==1) { //First control point
      if(segment==0) return;                    //No matching point -- track beginning
      segMatch=segment-1;   
      PointMatch=2;  //Matching point is previous second control point
    } else if(Point==2) { //Second control point
      if(segment==Track.size()-1) return;      //No matching point -- track end
      segMatch=segment+1; 
      PointMatch=1; //Matching point is next first control point
    } else { //Control point
      return;                                  //Not an endpoint
    }
    double Diff=oldC-newC; //This is correct, move the other control point the opposite way
    Track.get(segMatch).R[PointMatch].set(Coordinate,Diff+Track.get(segMatch).R[PointMatch].get(Coordinate));
    Track.get(segMatch).recalcCoeff();
  }

  private void SegmentSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SegmentSpinnerStateChanged
    int newSegment=((Integer)(SegmentSpinner.getValue())).intValue();
    setSegment(newSegment);
    XYCanvas.setCurrentSeg(newSegment);
    XZCanvas.setCurrentSeg(newSegment);
    YZCanvas.setCurrentSeg(newSegment);
  }//GEN-LAST:event_SegmentSpinnerStateChanged

  private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    Track=new ArrayList<Bezier>();
    Track.add(
      new Bezier(
        new MathVector(0,0,0), 
        new MathVector(1,0,0),
        new MathVector(2,0,0),
        new MathVector(3,0,0)
      )
    );
    Track.add(
      new Bezier(
        new MathVector(3,0,0), 
        new MathVector(4,0,0),
        new MathVector(5,0,0),
        new MathVector(6,0,0)
      )
    );
    XYCanvas.Track=Track;
    XZCanvas.Track=Track;
    YZCanvas.Track=Track;
    XYCanvas.repaint();
    XZCanvas.repaint();
    YZCanvas.repaint();
  }//GEN-LAST:event_formWindowOpened
    
  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new TrackEditor().setVisible(true);
      }
    });
  }
  
  private KBezierCanvas XYCanvas,XZCanvas,YZCanvas;
  private ArrayList<Bezier> Track;
  private int segment;
  private JSpinner CoordSpinner[][];
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton AddSegmentButton;
  private javax.swing.JLabel CfLabel;
  private javax.swing.JSpinner CfSpinner;
  private javax.swing.JButton ExportButton;
  private javax.swing.JCheckBox KeepSlopeCheck;
  private javax.swing.JCheckBox KeepTogetherCheck;
  private javax.swing.JButton LoadButton;
  private javax.swing.JLabel Pt0Label;
  private javax.swing.JLabel Pt1Label;
  private javax.swing.JLabel Pt2Label;
  private javax.swing.JLabel Pt3Label;
  private javax.swing.JButton RemoveSegmentButton;
  private javax.swing.JButton SaveButton;
  private javax.swing.JLabel SegmentLabel;
  private javax.swing.JSpinner SegmentSpinner;
  private javax.swing.JLabel VpLabel;
  private javax.swing.JSpinner VpSpinner;
  private javax.swing.JSpinner X0;
  private javax.swing.JSpinner X1;
  private javax.swing.JSpinner X2;
  private javax.swing.JSpinner X3;
  private javax.swing.JLabel XLabel;
  private javax.swing.JPanel XYPanel;
  private javax.swing.JPanel XZPanel;
  private javax.swing.JSpinner Y0;
  private javax.swing.JSpinner Y1;
  private javax.swing.JSpinner Y2;
  private javax.swing.JSpinner Y3;
  private javax.swing.JLabel YLabel;
  private javax.swing.JPanel YZPanel;
  private javax.swing.JSpinner Z0;
  private javax.swing.JSpinner Z1;
  private javax.swing.JSpinner Z2;
  private javax.swing.JSpinner Z3;
  private javax.swing.JLabel ZLabel;
  private javax.swing.JPanel jPanel1;
  // End of variables declaration//GEN-END:variables
  
}
