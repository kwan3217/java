package org.kwansystems.space.gator;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.vector.*;

public class Mapper extends javax.swing.JFrame {
  private static final long serialVersionUID = -4502755967293754132L;
  public Mapper() {
    initComponents();
  }
  double[] Scales=new double[] {1e4,2e4,5e4,
                                1e5,2e5,5e5,
                                1e6,2e6,5e6,
                                1e7,2e7,5e7,
                                1e8};
  int Influence=0;    
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    MapperPanel = new javax.swing.JPanel();
    CenterCombo = new javax.swing.JComboBox();
    ScaleSlider = new javax.swing.JSlider();
    ScaleLabel = new javax.swing.JLabel();

    getContentPane().setLayout(new java.awt.GridBagLayout());

    setTitle("Mapper");
    setBackground(new java.awt.Color(0, 0, 0));
    MapperPanel.setLayout(new java.awt.GridBagLayout());

    MapperCanvas=new KMapperCanvas();
    MapperPanel.setBackground(new java.awt.Color(0, 0, 0));
    MapperPanel.setPreferredSize(new java.awt.Dimension(400, 300));
    MapperCanvas.setBackground(new java.awt.Color(0, 0, 0));
    MapperCanvas.setPreferredSize(new java.awt.Dimension(1000, 1000));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    MapperPanel.add(MapperCanvas, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(MapperPanel, gridBagConstraints);

    CenterCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jupiter", "Io", "Europa", "Ganymede", "Callisto", "Influence" }));
    CenterCombo.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        CenterComboItemStateChanged(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    getContentPane().add(CenterCombo, gridBagConstraints);

    ScaleSlider.setMajorTickSpacing(3);
    ScaleSlider.setMaximum(12);
    ScaleSlider.setMinorTickSpacing(1);
    ScaleSlider.setPaintTicks(true);
    ScaleSlider.setSnapToTicks(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(ScaleSlider, gridBagConstraints);

    ScaleLabel.setText("jLabel1");
    ScaleLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    getContentPane().add(ScaleLabel, gridBagConstraints);

    pack();
  }
  // </editor-fold>//GEN-END:initComponents

  private void CenterComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CenterComboItemStateChanged
// TODO add your handling code here:
  }//GEN-LAST:event_CenterComboItemStateChanged

  double getWidthKm() {
    double Scale=Scales[ScaleSlider.getValue()];
    ScaleLabel.setText(""+Scale);
    return Scale;
    
  }
  int getReference() {
    int Center=CenterCombo.getSelectedIndex();
    if(Center==5) Center=Influence;
    return Center;
  }
  void rescale() {
    MapperCanvas.reMap(getReference(),getWidthKm());
  }
  
  void Map(
    Ephemeris[] Sat,
    MathStateTime S,
    int LInfluence,
    Elements LE
  ) {
    Influence=LInfluence;
    MapperCanvas.Map(Sat,S,getReference(),LInfluence,LE,getWidthKm());
  }

  private KMapperCanvas MapperCanvas;
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox CenterCombo;
  private javax.swing.JPanel MapperPanel;
  private javax.swing.JLabel ScaleLabel;
  private javax.swing.JSlider ScaleSlider;
  // End of variables declaration//GEN-END:variables
  
}
