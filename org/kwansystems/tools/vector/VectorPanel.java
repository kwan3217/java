package org.kwansystems.tools.vector;

import java.awt.*;
import javax.swing.*;

public class VectorPanel extends JPanel {
  public VectorPanel(String vectorLabel, String[] compLabel, String[] units, MathVector Lvector) {
    initComponents(vectorLabel,compLabel,units);
    if(Lvector!=null) setVector(Lvector);
  }
  public VectorPanel(String vectorLabel, String[] compLabel, String[] units) {
    this(vectorLabel,compLabel,units,null);
  }
  private JLabel[] lblComp;
  private JLabel[] lblValue;
  private JLabel[] lblUnit;
  private MathVector vector;
  
  private void initComponents(String vectorLabel,String[] compLabel, String[] units) {
    if(vectorLabel!=null)setBorder(javax.swing.BorderFactory.createTitledBorder(vectorLabel));
    setLayout(new GridBagLayout());
    
    lblComp=new JLabel[compLabel.length];
    lblValue=new JLabel[compLabel.length];
    lblUnit=new JLabel[compLabel.length];

    GridBagConstraints c = new GridBagConstraints();
    for(int i=0;i<compLabel.length;i++) {
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridy = i;
 
      lblComp[i] = new JLabel(compLabel[i]);
      lblComp[i].setFont(getFont());
      lblComp[i].setHorizontalAlignment(SwingConstants.TRAILING);
      c.weightx = 0;
      c.gridx = 0;
      add(lblComp[i], c);

      lblValue[i] = new JLabel("0.000");
      lblValue[i].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0,0,0)));
      lblValue[i].setHorizontalAlignment(SwingConstants.TRAILING);
      c.weightx = 1;
      c.gridx = 1;
      add(lblValue[i], c);

      lblUnit[i] = new JLabel(units[i]);
      lblUnit[i].setFont(getFont());
      c.weightx = 0;
      c.gridx = 2;
      add(lblUnit[i], c);
    }
  }

  public MathVector getVector() {
    return new MathVector(vector);
  }
  public static final char[] smallUnits={' ','m','\u03BC','n','p','f','a','z','y'};
  public static final char[] largeUnits={' ','k','M',     'G','T','P','E','Z','Y'};
  public void setVector(MathVector Lvector) {
    vector=new MathVector(Lvector);
    char suffix=' ';
    for(int i=0;i<vector.dimension();i++) {
      double value=vector.get(i);
      int ptr=0;
      if(Math.abs(value)>1000) {
        while(Math.abs(value)>1000) {
          ptr++;
          value/=1000;
        }
        suffix=largeUnits[ptr];
      } else {
        while(Math.abs(value)<1) {
          ptr++;
          value*=1000;
        }
        suffix=smallUnits[ptr];
      }
      lblValue[i].setText(String.format("%8.3f%c",value,suffix));
    }
  }
  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("GridBagLayoutDemo");
        VectorPanel TP=new VectorPanel("Test",new String[] {"X","Y","Z"},new String[] {"m/s","m/s","m/s"},new MathVector(1,2,-3));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
        //Set up the content pane.
        frame.getContentPane().add(TP);
      
        //Display the window.
        frame.pack();
        frame.setVisible(true);
      }
    });
  }
}
