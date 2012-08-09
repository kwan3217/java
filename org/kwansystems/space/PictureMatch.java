package org.kwansystems.space;

import org.kwansystems.tools.rootfind.optimize.Amoeba;
import org.kwansystems.tools.rootfind.optimize.OptimizeMultiDFunction;
import java.awt.*;
import javax.swing.*;

import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

public class PictureMatch extends JFrame {
  private static final long serialVersionUID = 5690478637584835328L;

  /* Image 15 Coordinates */
  /*
  public static double[] XPixel=new double[] {1068.15034460651, 1052.53055728196,
      775.112033195021, 708.063754843255, 507.613483146067, 341.062117433689,
      79.778717948718};

  public static double[] YPixel=new double[] {319.103061388043, 514.120986119743,
      549.096680497925, 395.662205001761, 339.55393258427, 277.588301972342,
      357.21282051282};
  */
  /* Image 14 Coordinates */
  public static double[] XPixel=new double[] {
    1069.04861967555,
    1052.63456090652,
    775.112033195021,
    708.102353585112,
    507.756944444444,
    341.088688263409,
    79.7010198604402           
  };

  public static double[] YPixel=new double[] { 
    318.075087752585,
    514.1850802644,
  549.096680497925,
  396.131362889984,
  339.672067901235,
  278.074880509825,
  357.351583467525,          
  };

  public static double[][] rahms=new double[][] {
    { 11,  3, 43.7},
    { 11,  1, 50.5},
    { 11, 53, 49.8},
    { 12, 15, 25.6},
    { 12, 54,  1.7},
    { 13, 23, 55.5},
    { 13, 47, 32.4}
  };
  public static double[][] decdms=new double[][] {
    { 61, 45,  3},
    { 56, 22, 57},
    { 53, 41, 41},
    { 57,  1, 57},
    { 55, 57, 35},
    { 54, 55, 31},
    { 49, 18, 48}
  };
  public static double[] ra=new double[rahms.length];
  public static double[] dec=new double[rahms.length];
  static {
    for(int i=0;i<ra.length;i++) {
      double rah=rahms[i][0]+rahms[i][1]/60.0+rahms[i][2]/3600.0;
      ra[i]=toRadians(rah*15.0);
      double decd=decdms[i][0]+decdms[i][1]/60.0+decdms[i][2]/3600.0;
      dec[i]=toRadians(decd);
    }
  }
  public static int CCDWidth =2048;
  public static int CCDHeight=1536;
  public static int CCDWidth2=CCDWidth/2;
  public static int CCDHeight2=CCDWidth/2;
  public static double HorizonHeight= 1449.8;

  private JPanel jContentPane=null;

  /**
   * This is the default constructor
   */
  public PictureMatch() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    this.setSize(1200, 900);
    this.setContentPane(getJContentPane());
    this.setTitle("PictureMatch");
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        System.exit(0);
      }
    });
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane==null) {
      GridBagConstraints gridBagConstraints101 = new GridBagConstraints();
      gridBagConstraints101.gridx = 2;
      gridBagConstraints101.gridy = 8;
      GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
      gridBagConstraints91.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints91.gridy = 8;
      gridBagConstraints91.weightx = 1.0;
      gridBagConstraints91.gridwidth = 2;
      gridBagConstraints91.gridx = 0;
      GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
      gridBagConstraints71.gridx = 1;
      gridBagConstraints71.gridy = 7;
      GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
      gridBagConstraints61.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints61.gridy = 6;
      gridBagConstraints61.weightx = 1.0;
      gridBagConstraints61.gridx = 1;
      GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
      gridBagConstraints51.gridx = 1;
      gridBagConstraints51.gridy = 5;
      GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
      gridBagConstraints41.gridx = 0;
      gridBagConstraints41.gridy = 7;
      GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
      gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints32.gridy = 6;
      gridBagConstraints32.weightx = 1.0;
      gridBagConstraints32.gridx = 0;
      GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
      gridBagConstraints12.gridx = 0;
      gridBagConstraints12.gridy = 5;
      GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
      gridBagConstraints111.gridx = 3;
      gridBagConstraints111.gridy = 4;
      GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
      gridBagConstraints10.gridx = 3;
      gridBagConstraints10.gridy = 3;
      GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
      gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints9.gridx = 3;
      gridBagConstraints9.gridy = 2;
      gridBagConstraints9.weightx = 1.0;
      GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
      gridBagConstraints8.gridx = 3;
      gridBagConstraints8.gridy = 1;
      GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
      gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints7.gridy = 4;
      gridBagConstraints7.weightx = 1.0;
      gridBagConstraints7.gridwidth = 3;
      gridBagConstraints7.gridx = 0;
      GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
      gridBagConstraints6.gridx = 2;
      gridBagConstraints6.gridy = 3;
      GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
      gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints5.gridy = 2;
      gridBagConstraints5.weightx = 1.0;
      gridBagConstraints5.gridx = 2;
      GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
      gridBagConstraints4.gridx = 2;
      gridBagConstraints4.gridy = 1;
      GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
      gridBagConstraints31.gridx = 1;
      gridBagConstraints31.gridy = 3;
      GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
      gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints21.gridy = 2;
      gridBagConstraints21.weightx = 1.0;
      gridBagConstraints21.gridx = 1;
      GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
      gridBagConstraints11.gridx = 1;
      gridBagConstraints11.gridy = 1;
      GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
      gridBagConstraints3.gridx = 0;
      gridBagConstraints3.gridy = 3;
      GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
      gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints2.gridy = 2;
      gridBagConstraints2.weightx = 1.0;
      gridBagConstraints2.gridx = 0;
      GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 1;
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.gridy = 0;
      jContentPane=new JPanel();
      jContentPane.setLayout(new GridBagLayout());
      jContentPane.add(getJPanel(), gridBagConstraints);
      jContentPane.add(getBtnPitchUp(), gridBagConstraints1);
      jContentPane.add(getTxtPitch(), gridBagConstraints2);
      jContentPane.add(getBtnPitchDown(), gridBagConstraints3);
      jContentPane.add(getBtnYawUp(), gridBagConstraints11);
      jContentPane.add(getTxtYaw(), gridBagConstraints21);
      jContentPane.add(getBtnYawDown(), gridBagConstraints31);
      jContentPane.add(getBtnRollUp(), gridBagConstraints4);
      jContentPane.add(getTxtRoll(), gridBagConstraints5);
      jContentPane.add(getBtnRollDown(), gridBagConstraints6);
      jContentPane.add(getBtnZoomUp(), gridBagConstraints8);
      jContentPane.add(getTxtZoom(), gridBagConstraints9);
      jContentPane.add(getBtnZoomDown(), gridBagConstraints10);
      jContentPane.add(getTxtCameraError(), gridBagConstraints7);
      jContentPane.add(getBtnAutofitCamera(), gridBagConstraints111);
      jContentPane.add(getBtnDecUp(), gridBagConstraints12);
      jContentPane.add(getTxtDec(), gridBagConstraints32);
      jContentPane.add(getBtnDecDown(), gridBagConstraints41);
      jContentPane.add(getBtnRAUp(), gridBagConstraints51);
      jContentPane.add(getTxtRA(), gridBagConstraints61);
      jContentPane.add(getBtnRADown(), gridBagConstraints71);
      jContentPane.add(getTxtHorizonError(), gridBagConstraints91);
      jContentPane.add(getBtnAutofitHorizon(), gridBagConstraints101);
    }
    return jContentPane;
  }

  private JPanel jPanel = null;

  private JButton BtnPitchUp = null;

  private JTextField TxtPitch = null;

  private JButton BtnPitchDown = null;

  private JButton BtnYawUp = null;

  private JTextField TxtYaw = null;

  private JButton BtnYawDown = null;

  private JButton BtnRollUp = null;

  private JTextField TxtRoll = null;

  private JButton BtnRollDown = null;

  private JTextField TxtCameraError = null;

  private JButton BtnZoomUp = null;

  private JTextField TxtZoom = null;

  private JButton BtnZoomDown = null;

  private JButton BtnAutofitCamera = null;

  /**
   * This method initializes jPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getJPanel() {
    if (jPanel==null) {
      jPanel=new JPanel() {
		private static final long serialVersionUID = 305849254793320058L;

		public void paintComponent(Graphics G) {
          super.paintComponent(G);
          drawPixStar(G);
          drawCatStar(G);
          drawPixHorizon(G);
          drawPosHorizon(G);
        }
      };
      jPanel.setPreferredSize(new java.awt.Dimension(1200,900));
    }
    return jPanel;
  }

  /**
   * This method initializes BtnPitchUp	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnPitchUp() {
    if (BtnPitchUp==null) {
      BtnPitchUp=new JButton();
      BtnPitchUp.setText("Pitch +1");
      BtnPitchUp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtPitch(),1);
        }
      });
    }
    return BtnPitchUp;
  }

  /**
   * This method initializes TxtPitch	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtPitch() {
    if (TxtPitch==null) {
      TxtPitch=new JTextField();
      TxtPitch.setText("0");
    }
    return TxtPitch;
  }

  private void Adjust(JTextField T, double delta) {
    T.setText(Double.toString(Double.parseDouble(T.getText())+delta));
    jPanel.repaint();
  }
  /**
   * This method initializes BtnPitchDown	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnPitchDown() {
    if (BtnPitchDown==null) {
      BtnPitchDown=new JButton();
      BtnPitchDown.setText("Pitch -10");
      BtnPitchDown.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtPitch(),-10);
        }
      });
    }
    return BtnPitchDown;
  }

  /**
   * This method initializes jButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnYawUp() {
    if (BtnYawUp==null) {
      BtnYawUp=new JButton();
      BtnYawUp.setText("Yaw +10");
      BtnYawUp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtYaw(),10);
        }
      });
    }
    return BtnYawUp;
  }

  /**
   * This method initializes TxtYaw	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtYaw() {
    if (TxtYaw==null) {
      TxtYaw=new JTextField();
      TxtYaw.setText("0");
    }
    return TxtYaw;
  }

  /**
   * This method initializes BtnYawDown	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnYawDown() {
    if (BtnYawDown==null) {
      BtnYawDown=new JButton();
      BtnYawDown.setText("Yaw -1");
      BtnYawDown.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtYaw(),-1);
        }
      });
    }
    return BtnYawDown;
  }

  /**
   * This method initializes BtnRollUp	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnRollUp() {
    if (BtnRollUp==null) {
      BtnRollUp=new JButton();
      BtnRollUp.setText("Roll +10");
      BtnRollUp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtRoll(),10);
        }
      });
    }
    return BtnRollUp;
  }

  /**
   * This method initializes TxtRoll	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtRoll() {
    if (TxtRoll==null) {
      TxtRoll=new JTextField();
      TxtRoll.setText("0");
    }
    return TxtRoll;
  }

  /**
   * This method initializes BtnRollDown	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnRollDown() {
    if (BtnRollDown==null) {
      BtnRollDown=new JButton();
      BtnRollDown.setText("Roll -1");
      BtnRollDown.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtRoll(),-1);
        }
      });
    }
    return BtnRollDown;
  }

  /**
   * This method initializes TxtError	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtCameraError() {
    if (TxtCameraError==null) {
      TxtCameraError=new JTextField();
    }
    return TxtCameraError;
  }

  /**
   * This method initializes BtnZoomUp	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnZoomUp() {
    if (BtnZoomUp==null) {
      BtnZoomUp=new JButton();
      BtnZoomUp.setText("Zoom +1");
      BtnZoomUp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtZoom(),1);
        }
      });
    }
    return BtnZoomUp;
  }

  /**
   * This method initializes TxtZoom	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtZoom() {
    if (TxtZoom==null) {
      TxtZoom=new JTextField();
      TxtZoom.setText("1");
    }
    return TxtZoom;
  }

  /**
   * This method initializes BtnZoomDown	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnZoomDown() {
    if (BtnZoomDown==null) {
      BtnZoomDown=new JButton();
      BtnZoomDown.setText("Zoom -0.05");
      BtnZoomDown.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtZoom(),-0.05);
        }
      });
    }
    return BtnZoomDown;
  }

  /**
   * This method initializes BtnOptimize	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnAutofitCamera() {
    if (BtnAutofitCamera==null) {
      BtnAutofitCamera=new JButton();
      BtnAutofitCamera.setText("Autofit");
      BtnAutofitCamera.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Amoeba.amoeba(
            new double[] {
              Double.parseDouble(TxtPitch.getText()),
              Double.parseDouble(TxtYaw.getText()),
              Double.parseDouble(TxtRoll.getText()),
              Double.parseDouble(TxtZoom.getText()),
            },1,1e-10,new OptimizeMultiDFunction() {
              public double eval(double[] x) {
                TxtPitch.setText(Double.toString(x[0]));
                TxtYaw.setText(Double.toString(x[1]));
                TxtRoll.setText(Double.toString(x[2]));
                TxtZoom.setText(Double.toString(x[3]));
                Adjust(TxtPitch,0);
                calcCatStar();
                return starDist;
              }
            }
          );
        }
      });
    }
    return BtnAutofitCamera;
  }

  /**
   * This method initializes BtnDecUp	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnDecUp() {
    if (BtnDecUp==null) {
      BtnDecUp=new JButton();
      BtnDecUp.setText("Dec +10");
      BtnDecUp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtDec(),10);
        }
      });
    }
    return BtnDecUp;
  }

  /**
   * This method initializes TxtDec	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtDec() {
    if (TxtDec==null) {
      TxtDec=new JTextField();
      TxtDec.setText("0");
    }
    return TxtDec;
  }

  /**
   * This method initializes BtnDecDown	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnDecDown() {
    if (BtnDecDown==null) {
      BtnDecDown=new JButton();
      BtnDecDown.setText("Dec -1");
      BtnDecDown.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtDec(),-1);
        }
      });
    }
    return BtnDecDown;
  }

  /**
   * This method initializes BtnRAUp	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnRAUp() {
    if (BtnRAUp==null) {
      BtnRAUp=new JButton();
      BtnRAUp.setText("RA +10");
      BtnRAUp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtRA(),+10);
        }
      });
    }
    return BtnRAUp;
  }

  /**
   * This method initializes TxtRA	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtRA() {
    if (TxtRA==null) {
      TxtRA=new JTextField();
      TxtRA.setText("0");
    }
    return TxtRA;
  }

  /**
   * This method initializes BtnRADown	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnRADown() {
    if (BtnRADown==null) {
      BtnRADown=new JButton();
      BtnRADown.setText("RA -1");
      BtnRADown.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Adjust(getTxtRA(),-1);
        }
      });
    }
    return BtnRADown;
  }

  /**
   * This method initializes TxtHorizonError	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getTxtHorizonError() {
    if (TxtHorizonError==null) {
      TxtHorizonError=new JTextField();
    }
    return TxtHorizonError;
  }

  /**
   * This method initializes BtnAutofitHorizon	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBtnAutofitHorizon() {
    if (BtnAutofitHorizon==null) {
      BtnAutofitHorizon=new JButton();
      BtnAutofitHorizon.setText("Autofit");
      BtnAutofitHorizon.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Amoeba.amoeba(
            new double[] {
              HorizonHeight,
              Double.parseDouble(TxtRA.getText())
            },1,1e-10,new OptimizeMultiDFunction() {
              public double eval(double[] x) {
                HorizonHeight=x[0];
                TxtRA.setText(Double.toString(x[1]));
                Adjust(TxtDec,0);
                calcPosHorizon();
                return horizonDist;
              }
            }
          );
        }
      });
    }
    return BtnAutofitHorizon;
  }

  public static void main(String args[]) {
    new PictureMatch().show();
  }
  public void drawPixStar(Graphics G) {
    for(int i=0;i<XPixel.length;i++) {
      markRed(G,XPixel[i],YPixel[i]);
    }
  }
  public void drawPixHorizon(Graphics G) {
    for(int i=0;i<CCDWidth;i+=50) {
      markRed(G,i,HorizonHeight);
    }
  }
  private double starDist,horizonDist;

  private JButton BtnDecUp = null;

  private JTextField TxtDec = null;

  private JButton BtnDecDown = null;

  private JButton BtnRAUp = null;

  private JTextField TxtRA = null;

  private JButton BtnRADown = null;

  private JTextField TxtHorizonError = null;

  private JButton BtnAutofitHorizon = null;
  public MathMatrix calcCameraOrientECI() {
    double P=toRadians(Double.parseDouble(getTxtPitch().getText()));
    double Y=toRadians(Double.parseDouble(getTxtYaw().getText()));
    double R=toRadians(Double.parseDouble(getTxtRoll().getText()));
    return MathMatrix.mul(MathMatrix.Rot1(R),MathMatrix.mul(MathMatrix.Rot3(Y),MathMatrix.Rot2(P)));
  }
  public double[][] calcCatStar() {
    double[][] result=new double[ra.length][2];
    starDist=0;
    MathMatrix M=calcCameraOrientECI();
    Quaternion Q=new Quaternion(M.inv());
    System.out.println("quat=["+Q.X()+"d,"+Q.Y()+"d,"+Q.Z()+"d,"+Q.W()+"d]");
    double Z=Double.parseDouble(getTxtZoom().getText());
    for(int i=0;i<ra.length;i++) {
      MathVector V=M.transform(llr2xyz(dec[i],ra[i],1));
      result[i][0]=CCDWidth2-(double)CCDWidth2*(Z*V.Y()/V.X());
      result[i][1]=CCDHeight2-(double)CCDWidth2*(Z*V.Z()/V.X());
      if(i<XPixel.length) starDist+=pow(hypot(XPixel[i]-result[i][0],YPixel[i]-result[i][1]),2);
    }
    
    return result;
  }
  public void drawCatStar(Graphics G) {
    double[][]p=calcCatStar();
    for(int i=0;i<ra.length;i++) {
      markBlue(G,p[i][0],p[i][1]);
    }
    TxtCameraError.setText(Double.toString(starDist));
  }
  public static MathVector llr2xyz(double lat, double lon, double r) {
    return new MathVector(cos(lon)*cos(lat)*r,sin(lon)*cos(lat)*r,sin(lat)*r);
  }
  public double[][] calcPosHorizon() {
    double[][] result=new double[72][2];
    horizonDist=0;
    MathMatrix M=calcCameraOrientECI();
    double Z=            Double.parseDouble(getTxtZoom().getText());
    double Dec=toRadians(Double.parseDouble(getTxtDec() .getText()));
    double RA= toRadians(Double.parseDouble(getTxtRA()  .getText()));
    MathVector Zenith=llr2xyz(Dec,RA,1);
    MathVector Pole=new MathVector(0,0,1);
    MathVector East=MathVector.cross(Pole,Zenith);
    East=East.normal();
    MathVector North=MathVector.cross(Zenith,East);
    North=North.normal();
    int nPix=0;
    for(int i=0;i<72;i++) {
      double theta=2.0*PI*i/72.0;
      MathVector V=M.transform(MathVector.add(North.mul(cos(theta)),East.mul(sin(theta))));
      result[i][0]=CCDWidth2-(double)CCDWidth2*(Z*V.Y()/V.X());
      result[i][1]=CCDHeight-(double)CCDWidth2*(Z*V.Z()/V.X());
      if(V.X()>0 && result[i][0]>0 && result[i][0]<CCDWidth) {
        horizonDist+=pow(HorizonHeight-result[i][1],2);
        nPix++;
      }
      horizonDist/=nPix;
    }
    
    return result;
  }
  public void drawPosHorizon(Graphics G) {
    double[][]p=calcPosHorizon();
    markCross(G,p[0][0],p[0][1],Color.BLACK);
    //System.out.println(p[0][0]+", "+p[0][1]);
    for(int i=1;i<p.length;i++) {
      markGreen(G,p[i][0],p[i][1]);
    }
    TxtHorizonError.setText(Double.toString(horizonDist));
  }
  public static void markRed(Graphics G, double x, double y) {
    markCross(G,x,y,Color.RED);
  }
  public static void markBlue(Graphics G, double x, double y) {
    markX(G,x,y,Color.BLUE);
  }
  public static void markGreen(Graphics G, double x, double y) {
    markLine(G,x,y,Color.GREEN);
  }
  public static void markCross(Graphics G, double x, double y, Color C) {
    G.setColor(C); 
    int xp=(int)(x/CCDWidth*1200);
    int yp=(int)(y/CCDHeight*900);
    G.drawLine(xp-5,yp,xp+5,yp);
    G.drawLine(xp,yp-5,xp,yp+5);
  }
  public static void markX(Graphics G, double x, double y, Color C) {
    G.setColor(C); 
    int xp=(int)(x/CCDWidth*1200);
    int yp=(int)(y/CCDHeight*900);
    G.drawLine(xp-3,yp-3,xp+3,yp+3);
    G.drawLine(xp+3,yp-3,xp-3,yp+3);
  }
  public static void markLine(Graphics G, double x, double y, Color C) {
    G.setColor(C); 
    int xp=(int)(x/CCDWidth*1200);
    int yp=(int)(y/CCDHeight*900);
    G.drawLine(xp-5,yp,xp+5,yp);
  }

}  //  @jve:decl-index=0:visual-constraint="10,10"
