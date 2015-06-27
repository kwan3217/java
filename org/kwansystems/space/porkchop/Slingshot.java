package org.kwansystems.space.porkchop;


import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.*;

import org.kwansystems.space.ephemeris.KeplerPolyEphemeris;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.Constants.*;
import static org.kwansystems.tools.time.Time.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;

import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import java.awt.Dimension;

public class Slingshot extends JFrame {
  private static final long serialVersionUID = 6298276496937156927L;
  private JPanel jContentPane=null;
  private KOrbitCanvas KOCCanvas = null;
  private JPanel PANControls = null;
  private JPanel PANOrbit = null;
  private JPanel PANOrbitBtns = null;
  private JButton BTNZoomOut = null;
  private JButton BTNZoomIn = null;
  private JPanel PANPlanets = null;
  private JPanel PANDepart = null;
  private JLabel LBLPlanet = null;
  private JLabel LBLDate = null;
  private JLabel LBLSolve = null;
  private JComboBox CBOPlanetA = null;
  private JSpinner SPNDateA = null;
  private JLabel LBLDateA = null;
  private JButton BTNSolveA = null;
  private JComboBox CBOPlanetB = null;
  private JComboBox CBOPlanetC = null;
  private JSpinner SPNDateB = null;
  private JSpinner SPNDateC = null;
  private JLabel LBLDateB = null;
  private JLabel LBLDateC = null;
  private JButton jButton = null;
  private JButton jButton1 = null;
  private JPanel PANWindow = null;
  private JSpinner SPNWindow = null;
  private JLabel LBLWindow = null;
  private JLabel LBLWindowA = null;
  private JLabel LBLWindowB = null;
  /**
   * This is the default constructor
   */
  public Slingshot() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(874, 593);
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    this.setContentPane(getJContentPane());
    this.setTitle("Slingshot In A Box");
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane==null) {
      GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
      gridBagConstraints22.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints22.gridx = -1;
      gridBagConstraints22.gridy = -1;
      gridBagConstraints22.gridwidth = 3;
      GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
      gridBagConstraints11.gridx = 0;
      gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints11.weightx = 1.0D;
      gridBagConstraints11.weighty = 1.0D;
      gridBagConstraints11.gridy = 0;
      GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.weightx = 1.0D;
      gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints1.gridy = 0;
      jContentPane=new JPanel();
      jContentPane.setLayout(new GridBagLayout());
      jContentPane.add(getPANControls(), gridBagConstraints1);
      jContentPane.add(getPANOrbit(), gridBagConstraints11);
    }
    return jContentPane;
  }

  /**
   * This method initializes kOrbitCanvas	
   * 	
   * @return org.kwansystems.porkchop.KOrbitCanvas	
   */
  private KOrbitCanvas getKOCCanvas() {
    if (KOCCanvas==null) {
      KOCCanvas=new KOrbitCanvas(new KeplerFG(Planet.Sun.S.GM));
      KOCCanvas.setBackground(java.awt.Color.black);
    }
    return KOCCanvas;
  }

  /**
   * This method initializes PANControls	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPANControls() {
    if (PANControls==null) {
      GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
      gridBagConstraints28.gridy = 4;
      gridBagConstraints28.gridx = 0;
      gridBagConstraints28.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints28.gridwidth = 1;
      GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
      gridBagConstraints21.gridx = 0;
      gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints21.gridy = 3;
      GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
      gridBagConstraints25.gridx = 0;
      gridBagConstraints25.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints25.gridy = 0;
      GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
      gridBagConstraints4.gridx = 0;
      gridBagConstraints4.weighty = 0.0D;
      gridBagConstraints4.weightx = 1.0D;
      gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints4.gridy = 2;
      GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
      gridBagConstraints3.gridx = 0;
      gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints3.weightx = 1.0D;
      gridBagConstraints3.weighty = 0.0D;
      gridBagConstraints3.gridy = 1;
      PANControls=new JPanel();
      PANControls.setLayout(new GridBagLayout());
      PANControls.setPreferredSize(new java.awt.Dimension(500,500));
      PANControls.add(getPANPlanets(), gridBagConstraints3);
      PANControls.add(getPANDepart(), gridBagConstraints4);
      PANControls.add(getPANWindow(), gridBagConstraints25);
      PANControls.add(getPANSlingshot(), gridBagConstraints21);
      PANControls.add(getPANArrive(), gridBagConstraints28);
    }
    return PANControls;
  }

  /**
   * This method initializes PANOrbit	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPANOrbit() {
    if (PANOrbit==null) {
      GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
      gridBagConstraints2.gridx = 0;
      gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints2.gridy = 1;
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridx = 0;
      PANOrbit=new JPanel();
      PANOrbit.setLayout(new GridBagLayout());
      PANOrbit.setPreferredSize(new java.awt.Dimension(500,500));
      PANOrbit.add(getKOCCanvas(), gridBagConstraints);
      PANOrbit.add(getPANOrbitBtns(), gridBagConstraints2);
    }
    return PANOrbit;
  }

  /**
   * This method initializes PANOrbitBtns	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPANOrbitBtns() {
    if (PANOrbitBtns==null) {
      PANOrbitBtns=new JPanel();
      PANOrbitBtns.add(getBTNZoomOut(), null);
      PANOrbitBtns.add(getBTNZoomIn(), null);
    }
    return PANOrbitBtns;
  }

  /**
   * This method initializes BTNZoomOut	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBTNZoomOut() {
    if (BTNZoomOut==null) {
      BTNZoomOut=new JButton();
      BTNZoomOut.setText("Zoom Out");
      BTNZoomOut.addActionListener(new java.awt.event.ActionListener() {
      	public void actionPerformed(java.awt.event.ActionEvent e) {
   	      KOCCanvas.setMPerPix(KOCCanvas.getMPerPix() * 1.2);
     	  KOCCanvas.repaint();
      	}
      });
    }
    return BTNZoomOut;
  }

  /**
   * This method initializes BTNZoomIn	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBTNZoomIn() {
    if (BTNZoomIn==null) {
      BTNZoomIn=new JButton();
      BTNZoomIn.setText("Zoom In");
      BTNZoomIn.addActionListener(new java.awt.event.ActionListener() {
       	public void actionPerformed(java.awt.event.ActionEvent e) {
    	  KOCCanvas.setMPerPix(KOCCanvas.getMPerPix() / 1.2);
       	  KOCCanvas.repaint();
        }
      });
    }
    return BTNZoomIn;
  }

  /**
   * This method initializes PANPlanets	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPANPlanets() {
    if (PANPlanets==null) {
      GridBagConstraints gridBagConstraints57 = new GridBagConstraints();
      gridBagConstraints57.gridx = 2;
      gridBagConstraints57.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints57.gridy = 3;
      GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
      gridBagConstraints56.gridx = 2;
      gridBagConstraints56.gridy = 0;
      LBLType = new JLabel();
      LBLType.setText("Type");
      GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
      gridBagConstraints55.gridx = 2;
      gridBagConstraints55.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints55.gridy = 1;
      GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
      gridBagConstraints20.gridx = 3;
      gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints20.gridy = 5;
      GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
      gridBagConstraints19.gridx = 3;
      gridBagConstraints19.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints19.gridy = 3;
      GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
      gridBagConstraints18.gridx = 0;
      gridBagConstraints18.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints18.gridwidth = 4;
      gridBagConstraints18.gridy = 6;
      LBLDateC = new JLabel();
      LBLDateC.setText("JLabel");
      GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
      gridBagConstraints17.gridx = 0;
      gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints17.gridwidth = 4;
      gridBagConstraints17.gridy = 4;
      LBLDateB = new JLabel();
      LBLDateB.setText("JLabel");
      GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
      gridBagConstraints16.gridx = 1;
      gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints16.gridy = 5;
      GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
      gridBagConstraints15.gridx = 1;
      gridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints15.gridy = 3;
      GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
      gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints14.gridy = 5;
      gridBagConstraints14.weightx = 1.0;
      gridBagConstraints14.gridx = 0;
      GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
      gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints13.gridy = 3;
      gridBagConstraints13.weightx = 1.0;
      gridBagConstraints13.gridx = 0;
      GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
      gridBagConstraints12.gridx = 3;
      gridBagConstraints12.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints12.gridy = 1;
      GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
      gridBagConstraints10.gridx = 0;
      gridBagConstraints10.weightx = 1.0D;
      gridBagConstraints10.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints10.gridwidth = 4;
      gridBagConstraints10.gridy = 2;
      LBLDateA = new JLabel();
      LBLDateA.setText("JLabel");
      GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
      gridBagConstraints9.gridx = 1;
      gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints9.gridy = 1;
      GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
      gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints8.gridy = 1;
      gridBagConstraints8.weightx = 1.0;
      gridBagConstraints8.gridx = 0;
      GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
      gridBagConstraints7.gridx = 3;
      gridBagConstraints7.gridy = 0;
      LBLSolve = new JLabel();
      LBLSolve.setText("Solve");
      GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
      gridBagConstraints6.gridx = 1;
      gridBagConstraints6.gridwidth = 1;
      gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints6.gridy = 0;
      LBLDate = new JLabel();
      LBLDate.setText("Date");
      GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
      gridBagConstraints5.gridx = 0;
      gridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints5.gridy = 0;
      LBLPlanet = new JLabel();
      LBLPlanet.setText("Planet");
      PANPlanets=new JPanel();
      PANPlanets.setLayout(new GridBagLayout());
      PANPlanets.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Course", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
      PANPlanets.add(LBLPlanet, gridBagConstraints5);
      PANPlanets.add(LBLDate, gridBagConstraints6);
      PANPlanets.add(LBLSolve, gridBagConstraints7);
      PANPlanets.add(getCBOPlanetA(), gridBagConstraints8);
      PANPlanets.add(getSPNDateA(), gridBagConstraints9);
      PANPlanets.add(LBLDateA, gridBagConstraints10);
      PANPlanets.add(getBTNSolveA(), gridBagConstraints12);
      PANPlanets.add(getCBOPlanetB(), gridBagConstraints13);
      PANPlanets.add(getCBOPlanetC(), gridBagConstraints14);
      PANPlanets.add(getSPNDateB(), gridBagConstraints15);
      PANPlanets.add(getSPNDateC(), gridBagConstraints16);
      PANPlanets.add(LBLDateB, gridBagConstraints17);
      PANPlanets.add(LBLDateC, gridBagConstraints18);
      PANPlanets.add(getJButton(), gridBagConstraints19);
      PANPlanets.add(getJButton1(), gridBagConstraints20);
      PANPlanets.add(getSPNTypeA(), gridBagConstraints55);
      PANPlanets.add(LBLType, gridBagConstraints56);
      PANPlanets.add(getSPNTypeB(), gridBagConstraints57);
    }
    return PANPlanets;
  }

  /**
   * This method initializes PANOutput	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPANDepart() {
    if (PANDepart==null) {
      GridBagConstraints gridBagConstraints54 = new GridBagConstraints();
      gridBagConstraints54.gridx = 2;
      gridBagConstraints54.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints54.gridy = 8;
      jLabel17 = new JLabel();
      jLabel17.setText("DVesc, m/s");
      GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
      gridBagConstraints53.gridx = 3;
      gridBagConstraints53.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints53.gridy = 8;
      LBLDepartDVesc = new JLabel();
      LBLDepartDVesc.setBackground(Color.white);
      LBLDepartDVesc.setOpaque(true);
      LBLDepartDVesc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartDVesc.setText("JLabel");
      LBLDepartDVesc.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
      gridBagConstraints52.gridx = 1;
      gridBagConstraints52.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints52.gridwidth = 3;
      gridBagConstraints52.gridy = 6;
      LBLDepartVp = new JLabel();
      LBLDepartVp.setBackground(Color.white);
      LBLDepartVp.setOpaque(true);
      LBLDepartVp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartVp.setText("JLabel");
      LBLDepartVp.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
      gridBagConstraints51.gridx = 1;
      gridBagConstraints51.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints51.gridy = 8;
      LBLDepartVesc = new JLabel();
      LBLDepartVesc.setBackground(Color.white);
      LBLDepartVesc.setOpaque(true);
      LBLDepartVesc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartVesc.setText("JLabel");
      LBLDepartVesc.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
      gridBagConstraints50.gridx = 3;
      gridBagConstraints50.weightx = 1.0D;
      gridBagConstraints50.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints50.gridy = 7;
      LBLDepartDVcirc = new JLabel();
      LBLDepartDVcirc.setBackground(Color.white);
      LBLDepartDVcirc.setOpaque(true);
      LBLDepartDVcirc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartDVcirc.setText("JLabel");
      LBLDepartDVcirc.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
      gridBagConstraints49.gridx = 1;
      gridBagConstraints49.weightx = 1.0D;
      gridBagConstraints49.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints49.gridy = 7;
      LBLDepartVcirc = new JLabel();
      LBLDepartVcirc.setBackground(Color.white);
      LBLDepartVcirc.setOpaque(true);
      LBLDepartVcirc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartVcirc.setText("JLabel");
      LBLDepartVcirc.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
      gridBagConstraints48.gridx = 2;
      gridBagConstraints48.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints48.gridy = 7;
      jLabel11 = new JLabel();
      jLabel11.setText("DVcirc, m/s");
      GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
      gridBagConstraints47.gridx = 0;
      gridBagConstraints47.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints47.gridy = 6;
      jLabel10 = new JLabel();
      jLabel10.setText("Vp, m/s");
      GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
      gridBagConstraints46.gridx = 0;
      gridBagConstraints46.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints46.gridy = 8;
      jLabel9 = new JLabel();
      jLabel9.setText("Vesc, m/s");
      GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
      gridBagConstraints45.gridx = 0;
      gridBagConstraints45.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints45.gridy = 7;
      jLabel8 = new JLabel();
      jLabel8.setText("Vcirc, m/s");
      GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
      gridBagConstraints44.gridx = 1;
      gridBagConstraints44.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints44.gridwidth = 3;
      gridBagConstraints44.gridy = 5;
      GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
      gridBagConstraints43.gridx = 0;
      gridBagConstraints43.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints43.gridy = 5;
      jLabel7 = new JLabel();
      jLabel7.setText("Periapse Altitude, km");
      GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
      gridBagConstraints42.gridx = 1;
      gridBagConstraints42.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints42.gridwidth = 3;
      gridBagConstraints42.gridy = 4;
      LBLDepartChP = new JLabel();
      LBLDepartChP.setBackground(Color.white);
      LBLDepartChP.setOpaque(true);
      LBLDepartChP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartChP.setText("JLabel");
      LBLDepartChP.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
      gridBagConstraints41.gridx = 1;
      gridBagConstraints41.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints41.gridwidth = 3;
      gridBagConstraints41.gridy = 3;
      LBLDepartOut = new JLabel();
      LBLDepartOut.setBackground(Color.white);
      LBLDepartOut.setOpaque(true);
      LBLDepartOut.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartOut.setText("JLabel");
      LBLDepartOut.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
      gridBagConstraints40.gridx = 1;
      gridBagConstraints40.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints40.gridwidth = 3;
      gridBagConstraints40.gridy = 2;
      LBLDepartPro = new JLabel();
      LBLDepartPro.setBackground(Color.white);
      LBLDepartPro.setOpaque(true);
      LBLDepartPro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartPro.setText("JLabel");
      LBLDepartPro.setBorder(new LineBorder(new Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
      gridBagConstraints39.gridx = 0;
      gridBagConstraints39.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints39.gridy = 4;
      jLabel6 = new JLabel();
      jLabel6.setText("Change Plane, m/s");
      GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
      gridBagConstraints38.gridx = 0;
      gridBagConstraints38.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints38.gridy = 3;
      jLabel5 = new JLabel();
      jLabel5.setText("Outward, m/s");
      GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
      gridBagConstraints37.gridx = 0;
      gridBagConstraints37.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints37.gridy = 2;
      jLabel4 = new JLabel();
      jLabel4.setText("Prograde, m/s");
      GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
      gridBagConstraints34.gridx = 1;
      gridBagConstraints34.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints34.gridwidth = 3;
      gridBagConstraints34.gridy = 1;
      LBLDepartVInf = new JLabel();
      LBLDepartVInf.setText("JLabel");
      LBLDepartVInf.setOpaque(true);
      LBLDepartVInf.setBackground(java.awt.Color.white);
      LBLDepartVInf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartVInf.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
      gridBagConstraints33.gridx = 0;
      gridBagConstraints33.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints33.gridy = 1;
      jLabel2 = new JLabel();
      jLabel2.setText("Vinf, m/s");
      GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
      gridBagConstraints32.gridx = 1;
      gridBagConstraints32.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints32.weightx = 1.0D;
      gridBagConstraints32.gridwidth = 3;
      gridBagConstraints32.gridy = 0;
      LBLDepartC3 = new JLabel();
      LBLDepartC3.setText("JLabel");
      LBLDepartC3.setOpaque(true);
      LBLDepartC3.setBackground(java.awt.Color.white);
      LBLDepartC3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      LBLDepartC3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
      GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
      gridBagConstraints31.gridx = 0;
      gridBagConstraints31.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints31.gridy = 0;
      jLabel1 = new JLabel();
      jLabel1.setText("C3, km^2/s^2");
      PANDepart=new JPanel();
      PANDepart.setLayout(new GridBagLayout());
      PANDepart.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Depart", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51,51,51)));
      PANDepart.add(jLabel1, gridBagConstraints31);
      PANDepart.add(LBLDepartC3, gridBagConstraints32);
      PANDepart.add(jLabel2, gridBagConstraints33);
      PANDepart.add(LBLDepartVInf, gridBagConstraints34);
      PANDepart.add(jLabel4, gridBagConstraints37);
      PANDepart.add(jLabel5, gridBagConstraints38);
      PANDepart.add(jLabel6, gridBagConstraints39);
      PANDepart.add(LBLDepartPro, gridBagConstraints40);
      PANDepart.add(LBLDepartOut, gridBagConstraints41);
      PANDepart.add(LBLDepartChP, gridBagConstraints42);
      PANDepart.add(jLabel7, gridBagConstraints43);
      PANDepart.add(getSPNDepartPeriapse(), gridBagConstraints44);
      PANDepart.add(jLabel8, gridBagConstraints45);
      PANDepart.add(jLabel9, gridBagConstraints46);
      PANDepart.add(jLabel10, gridBagConstraints47);
      PANDepart.add(jLabel11, gridBagConstraints48);
      PANDepart.add(LBLDepartVcirc, gridBagConstraints49);
      PANDepart.add(LBLDepartDVcirc, gridBagConstraints50);
      PANDepart.add(LBLDepartVesc, gridBagConstraints51);
      PANDepart.add(LBLDepartVp, gridBagConstraints52);
      PANDepart.add(LBLDepartDVesc, gridBagConstraints53);
      PANDepart.add(jLabel17, gridBagConstraints54);
    }
    return PANDepart;
  }

  /**
   * This method initializes CBOPlanetA	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getCBOPlanetA() {
    if (CBOPlanetA==null) {
      CBOPlanetA=new JComboBox();
      CBOPlanetA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto" }));
      CBOPlanetA.setSelectedIndex(2);
      PlanetA=Planet.Planets[CBOPlanetA.getSelectedIndex()+1];
      PolyEleA=KeplerPolyEphemeris.Planets[CBOPlanetA.getSelectedIndex()+1];
      CBOPlanetA.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          PlanetA=Planet.Planets[CBOPlanetA.getSelectedIndex()+1];
          PolyEleA=KeplerPolyEphemeris.Planets[CBOPlanetA.getSelectedIndex()+1];
          recalcLaunchWindow();
        }
      });
    }
    return CBOPlanetA;
  }

  /**
   * This method initializes SPNDateA	
   * 	
   * @return javax.swing.JSpinner	
   */
  private JSpinner getSPNDateA() {
    if (SPNDateA==null) {
      SPNDateA=new JSpinner();
      SPNDateA.setPreferredSize(new java.awt.Dimension(150,20));
      SPNDateA.setModel(new SpinnerNumberModel(0.0d,-10000.0d,10000.0d,1.0d));
      SPNDateA.addChangeListener(new javax.swing.event.ChangeListener() {
      	public void stateChanged(javax.swing.event.ChangeEvent e) {
      	  recalcTunedLaunch();
      	}
      });
    }
    return SPNDateA;
  }

  /**
   * This method initializes BTNSolveA	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBTNSolveA() {
    if (BTNSolveA==null) {
      BTNSolveA=new JButton();
    }
    return BTNSolveA;
  }

  /**
   * This method initializes jComboBox	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getCBOPlanetB() {
    if (CBOPlanetB==null) {
      CBOPlanetB=new JComboBox();
      CBOPlanetB.setModel(new DefaultComboBoxModel(new String[] {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"}));
      CBOPlanetB.setSelectedIndex(4);
      PlanetB=Planet.Planets[CBOPlanetB.getSelectedIndex()+1];
      PolyEleB=KeplerPolyEphemeris.Planets[CBOPlanetB.getSelectedIndex()+1];
      CBOPlanetB.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          PlanetB=Planet.Planets[CBOPlanetB.getSelectedIndex()+1];
          PolyEleB=KeplerPolyEphemeris.Planets[CBOPlanetB.getSelectedIndex()+1];
          recalcLaunchWindow();
        }
      });
    }
    return CBOPlanetB;
  }

  /**
   * This method initializes jComboBox	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getCBOPlanetC() {
    if (CBOPlanetC==null) {
      CBOPlanetC=new JComboBox();
      CBOPlanetC.setModel(new DefaultComboBoxModel(new String[] {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"}));
      CBOPlanetC.setSelectedIndex(5);
      PlanetC=Planet.Planets[CBOPlanetC.getSelectedIndex()+1];
      PolyEleC=KeplerPolyEphemeris.Planets[CBOPlanetC.getSelectedIndex()+1];
      CBOPlanetC.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          PlanetC=Planet.Planets[CBOPlanetC.getSelectedIndex()+1];
          PolyEleC=KeplerPolyEphemeris.Planets[CBOPlanetC.getSelectedIndex()+1];
          recalcLaunchWindow();
        }
      });
    }
    return CBOPlanetC;
  }

  /**
   * This method initializes jSpinner	
   * 	
   * @return javax.swing.JSpinner	
   */
  private JSpinner getSPNDateB() {
    if (SPNDateB==null) {
      SPNDateB=new JSpinner();
      SPNDateB.setModel(new SpinnerNumberModel(0.0d,-10000.0d,10000.0d,1.0d));
      SPNDateB.addChangeListener(new javax.swing.event.ChangeListener() {
       	public void stateChanged(javax.swing.event.ChangeEvent e) {
       	  recalcTunedLaunch();
       	}
      });
    }
    return SPNDateB;
  }

  /**
   * This method initializes jSpinner	
   * 	
   * @return javax.swing.JSpinner	
   */
  private JSpinner getSPNDateC() {
    if (SPNDateC==null) {
      SPNDateC=new JSpinner();
      SPNDateC.setModel(new SpinnerNumberModel(0.0d,-10000.0d,10000.0d,1.0d));
      SPNDateC.addChangeListener(new javax.swing.event.ChangeListener() {
       	public void stateChanged(javax.swing.event.ChangeEvent e) {
       	  recalcTunedLaunch();
       	}
      });
    }
    return SPNDateC;
  }

  /**
   * This method initializes jButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getJButton() {
    if (jButton==null) {
      jButton=new JButton();
    }
    return jButton;
  }

  /**
   * This method initializes jButton1	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getJButton1() {
    if (jButton1==null) {
      jButton1=new JButton();
    }
    return jButton1;
  }

  /**
   * This method initializes PANWindow	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPANWindow() {
    if (PANWindow==null) {
      GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
      gridBagConstraints27.gridx = 0;
      gridBagConstraints27.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints27.gridwidth = 2;
      gridBagConstraints27.gridy = 2;
      LBLWindowB = new JLabel();
      LBLWindowB.setText("JLabel");
      GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
      gridBagConstraints26.gridx = 0;
      gridBagConstraints26.gridwidth = 2;
      gridBagConstraints26.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints26.gridy = 1;
      LBLWindowA = new JLabel();
      LBLWindowA.setText("JLabel");
      GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
      gridBagConstraints24.gridx = 0;
      gridBagConstraints24.gridy = 0;
      LBLWindow = new JLabel();
      LBLWindow.setText("Window Number");
      GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
      gridBagConstraints23.gridx = 1;
      gridBagConstraints23.weighty = 0.0D;
      gridBagConstraints23.weightx = 1.0D;
      gridBagConstraints23.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints23.gridy = 0;
      PANWindow=new JPanel();
      PANWindow.setLayout(new GridBagLayout());
      PANWindow.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Launch Window", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
      PANWindow.add(getSPNWindow(), gridBagConstraints23);
      PANWindow.add(LBLWindow, gridBagConstraints24);
      PANWindow.add(LBLWindowA, gridBagConstraints26);
      PANWindow.add(LBLWindowB, gridBagConstraints27);
    }
    return PANWindow;
  }

  /**
   * This method initializes jSpinner	
   * 	
   * @return javax.swing.JSpinner	
   */
  private JSpinner getSPNWindow() {
    if (SPNWindow==null) {
      SPNWindow=new JSpinner();
      SPNWindow.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          recalcLaunchWindow();
        }
      });
    }
    return SPNWindow;
  }
  private Planet PlanetA;
  private Planet PlanetB;
  private Planet PlanetC;
  private KeplerPolyEphemeris PolyEleA;
  private KeplerPolyEphemeris PolyEleB;
  private KeplerPolyEphemeris PolyEleC;
  private Time[] TimeHohman;
  private JPanel PANSlingshot = null;
  private JPanel PANArrive = null;
  private JLabel jLabel = null;
  private JLabel LBLSlingshotDeltaV = null;
  private JLabel jLabel1 = null;
  private JLabel LBLDepartC3 = null;
  private JLabel jLabel2 = null;
  private JLabel LBLDepartVInf = null;
  private JLabel jLabel3 = null;
  private JLabel LBLSlingshotVGain = null;
  private JLabel jLabel4 = null;
  private JLabel jLabel5 = null;
  private JLabel jLabel6 = null;
  private JLabel LBLDepartPro = null;
  private JLabel LBLDepartOut = null;
  private JLabel LBLDepartChP = null;
  private JLabel jLabel7 = null;
  private JSpinner SPNDepartPeriapse = null;
  private JLabel jLabel8 = null;
  private JLabel jLabel9 = null;
  private JLabel jLabel10 = null;
  private JLabel jLabel11 = null;
  private JLabel LBLDepartVcirc = null;
  private JLabel LBLDepartDVcirc = null;
  private JLabel LBLDepartVesc = null;
  private JLabel LBLDepartVp = null;
  private JLabel LBLDepartDVesc = null;
  private JLabel jLabel17 = null;
  private JSpinner SPNTypeA = null;
  private JLabel LBLType = null;
  private JSpinner SPNTypeB = null;
  private void recalcLaunchWindow() {
    TimeHohman=LaunchWindow.calcLaunchWindow(
      PolyEleA,
      PolyEleB, 
      ((Integer)(SPNWindow.getValue())).intValue()
    );
    LBLWindowA.setText(TimeHohman[0].toString());
    LBLWindowB.setText(TimeHohman[1].toString());
    SPNDateA.setValue(0.0);
    SPNDateB.setValue(Time.difference(TimeHohman[0],TimeHohman[1],TimeUnits.Days));
    SPNDateC.setValue(Time.difference(TimeHohman[0],TimeHohman[1],TimeUnits.Days));
    recalcTunedLaunch();
  }
  private void recalcTunedLaunch() {
    Time T[]=new Time[3];
    T[0]=Time.add(TimeHohman[0], ((Double)(SPNDateA.getValue())).doubleValue());
    T[0].Units=TimeUnits.Seconds;
    T[1]=Time.add(T[0], ((Double)(SPNDateB.getValue())).doubleValue(),TimeUnits.Days);
    T[1].Units=TimeUnits.Seconds; 
    T[2]=Time.add(T[1], ((Double)(SPNDateC.getValue())).doubleValue(),TimeUnits.Days);
    T[2].Units=TimeUnits.Seconds; 
    LBLDateA.setText(T[0].toString(TimeUnits.Days));
    LBLDateB.setText(T[1].toString(TimeUnits.Days));
    LBLDateC.setText(T[2].toString(TimeUnits.Days));

    MathStateTime STA0=PlanetA.Orbit.getStateTime(T[0]);
    MathStateTime STA1=PlanetA.Orbit.getStateTime(T[1]);
    MathStateTime STB0=PlanetB.Orbit.getStateTime(T[0]);
    MathStateTime STB1=PlanetB.Orbit.getStateTime(T[1]);
    MathStateTime STC0=PlanetC.Orbit.getStateTime(T[0]);
    MathStateTime STC1=PlanetC.Orbit.getStateTime(T[1]);
    MathStateTime STC2=PlanetC.Orbit.getStateTime(T[2]);
    MathStateTime[] CourseAB=KeplerFG.target(STA0,STB1,SunGM,((Integer)(SPNTypeA.getValue())).intValue());
    MathStateTime[] CourseBC=KeplerFG.target(STB1,STC2,SunGM,((Integer)(SPNTypeB.getValue())).intValue());
    
    MathVector VInfBIn =MathVector.sub(CourseAB[1].S.V(),STB1.S.V());
    MathVector VInfBOut=MathVector.sub(CourseBC[0].S.V(),STB1.S.V());

    double[] ResolveA=LaunchWindow.ResolveDeltaV(STA0,CourseAB[0]);    //{Vinf,C3,Pro,Out,ChPl}
    LBLDepartC3.setText(String.format("%15.3f",ResolveA[1]/1e6));
    LBLDepartVInf.setText(String.format("%15.3f",ResolveA[0]));
    LBLDepartPro.setText(String.format("%15.3f",ResolveA[2]));
    LBLDepartOut.setText(String.format("%15.3f",ResolveA[3]));
    LBLDepartChP.setText(String.format("%15.3f",ResolveA[4]));
    double Rp=PlanetA.S.Re+1000.0*((Double)(SPNDepartPeriapse.getValue())).doubleValue();
    //{DVesc,DVcirc,Vcirc,Vesc,Vp};
    double[] HyperA=LaunchWindow.CalcHyper(Rp,PlanetA.S.GM,ResolveA[0]);
    LBLDepartVp.setText(String.format("%15.3f",HyperA[4]));
    LBLDepartVcirc.setText(String.format("%15.3f",HyperA[2]));
    LBLDepartVesc.setText(String.format("%15.3f",HyperA[3]));
    LBLDepartDVcirc.setText(String.format("%15.3f",HyperA[0]));
    LBLDepartDVesc.setText(String.format("%15.3f",HyperA[1]));
    
    LBLSlingshotDeltaV.setText(String.format("%15.3f",VInfBOut.length()-VInfBIn.length()));
    LBLSlingshotVGain.setText(String.format("%15.3f",CourseBC[0].S.V().length()-CourseAB[1].S.V().length()));
    

    KOCCanvas.setTraj(new PorkchopTrajectory[] {
      new PorkchopTrajectory(STA0,T[1],new Color(0,0,128),1.0),
      new PorkchopTrajectory(STA1,T[2],new Color(0,0,255),1.0),
      new PorkchopTrajectory(STB0,T[1],new Color(128,0,0),1.0),
      new PorkchopTrajectory(STB1,T[2],new Color(255,0,0),1.0),
      new PorkchopTrajectory(STC0,T[1],new Color(0,128,0),1.0),
      new PorkchopTrajectory(STC1,T[2],new Color(0,255,0),1.0),
      new PorkchopTrajectory(CourseAB[0],T[1],new Color(128,128,128),1.0),
      new PorkchopTrajectory(CourseBC[0],T[2],new Color(255,255,255),1.0)
    });
    KOCCanvas.repaint();
  }

/**
 * This method initializes jPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getPANSlingshot() {
	if (PANSlingshot == null) {
		GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
		gridBagConstraints36.gridx = 1;
		gridBagConstraints36.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints36.gridy = 1;
		LBLSlingshotVGain = new JLabel();
		LBLSlingshotVGain.setBackground(Color.white);
        LBLSlingshotVGain.setOpaque(true);
		LBLSlingshotVGain.setText("JLabel");
		LBLSlingshotVGain.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		LBLSlingshotVGain.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
		gridBagConstraints35.gridx = 0;
		gridBagConstraints35.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints35.gridy = 1;
		jLabel3 = new JLabel();
		jLabel3.setText("Velocity Gain (m/s)");
		GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
		gridBagConstraints30.gridx = 1;
		gridBagConstraints30.weightx = 1.0D;
		gridBagConstraints30.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints30.gridy = 0;
		LBLSlingshotDeltaV = new JLabel();
		LBLSlingshotDeltaV.setText("JLabel");
		LBLSlingshotDeltaV.setBackground(java.awt.Color.white);
        LBLSlingshotDeltaV.setOpaque(true);
        LBLSlingshotDeltaV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        LBLSlingshotDeltaV.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));

		GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
		gridBagConstraints29.gridx = 0;
		gridBagConstraints29.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("Slingshot DeltaV (m/s)");
		PANSlingshot = new JPanel();
		PANSlingshot.setLayout(new GridBagLayout());
		PANSlingshot.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Slingshot", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51,51,51)));
		PANSlingshot.add(jLabel, gridBagConstraints29);
		PANSlingshot.add(LBLSlingshotDeltaV, gridBagConstraints30);
		PANSlingshot.add(jLabel3, gridBagConstraints35);
		PANSlingshot.add(LBLSlingshotVGain, gridBagConstraints36);
	}
	return PANSlingshot;
}

/**
 * This method initializes jPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getPANArrive() {
	if (PANArrive == null) {
		PANArrive = new JPanel();
		PANArrive.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Arrive", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51,51,51)));
	}
	return PANArrive;
}

/**
 * This method initializes jSpinner	
 * 	
 * @return javax.swing.JSpinner	
 */
private JSpinner getSPNDepartPeriapse() {
	if (SPNDepartPeriapse == null) {
		SPNDepartPeriapse = new JSpinner();
		SPNDepartPeriapse.setModel(new SpinnerNumberModel(185d, 0.0d, 1e6, 1.0d));
	}
	return SPNDepartPeriapse;
}

/**
 * This method initializes jSpinner	
 * 	
 * @return javax.swing.JSpinner	
 */
private JSpinner getSPNTypeA() {
  if (SPNTypeA==null) {
    SPNTypeA=new JSpinner();
    SPNTypeA.setPreferredSize(new java.awt.Dimension(50,20));
    SPNTypeA.setModel(new SpinnerNumberModel(-1, -1, 1, 1));
    SPNTypeA.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        recalcTunedLaunch();
      }
    });
  }
  return SPNTypeA;
}

/**
 * This method initializes jSpinner	
 * 	
 * @return javax.swing.JSpinner	
 */
private JSpinner getSPNTypeB() {
  if (SPNTypeB==null) {
    SPNTypeB=new JSpinner();
    SPNTypeB.setPreferredSize(new java.awt.Dimension(50,20));
    SPNTypeB.setModel(new SpinnerNumberModel(-1, -1, 1, 1));
    SPNTypeB.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        recalcTunedLaunch();
      }
    });
  }
  return SPNTypeB;
}

public static void main(String args[]) {
  java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
      new Slingshot().setVisible(true);
    }
  });
}

  

}  //  @jve:decl-index=0:visual-constraint="10,10"
