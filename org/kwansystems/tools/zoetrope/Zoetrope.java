package org.kwansystems.tools.zoetrope;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JToggleButton;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Simple animation component. Modeled after a real <a href="http://en.wikipedia.org/Zoetrope">zoetrope</a>,
 * this object draws a series of frames at regular intervals, simulating motion.
 * <p>
 * This is an abstract class. Descendants should override the paintFrame() method to actually draw
 * the picture for each frame. Everything else is handled by the ancestor class. 
 *
 */
public abstract class Zoetrope extends javax.swing.JFrame implements Runnable {
  private void initComponents() {
    GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
    gridBagConstraints3.gridx = 0;
    gridBagConstraints3.fill = GridBagConstraints.BOTH;
    gridBagConstraints3.weighty = 1.0D;
    gridBagConstraints3.weightx = 1.0D;
    gridBagConstraints3.gridy = 0;
    GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.gridy = 1;
    gridBagConstraints2.weightx = 1.0D;
    gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints2.gridheight = 0;
    ChartPanel = new javax.swing.JPanel();
    getContentPane().setLayout(new java.awt.GridBagLayout());

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    this.setSize(new Dimension(390, 352));
    this.setTitle(WindowTitle);
    this.setContentPane(ChartPanel);

    ChartPanel.setLayout(new GridBagLayout());

    Canvas = new ZoetropeCanvas(this);
    ChartPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
    ChartPanel.add(Canvas, gridBagConstraints3);
    ChartPanel.add(getJPanel1(), gridBagConstraints2);
    Canvas.setPreferredSize(new java.awt.Dimension(400, 400));
    Canvas.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    pack();
  }

  private double Rmax;
  protected int ScaleExt, XIExt, YIExt;
  protected int CurrentFrame;
  private int NumFrames;
  protected int nViewportX, nViewportY;
  private int currentViewportX, currentViewportY;
  public final String WindowTitle;

  public int getFramePeriodMs() {
    return Integer.parseInt(FramePeriod.getText());
  }

  public void setFramePeriodMs(int LFramePeriodMs) {
    FramePeriod.setText(Integer.toString(LFramePeriodMs));
  }

  public Zoetrope(String LWindowTitle, int LFramePeriodMs) {
    WindowTitle = LWindowTitle;
    initComponents();
    setFramePeriodMs(LFramePeriodMs);
    nViewportX = 1;
    nViewportY = 1;
  }

  public void setViewport(Graphics G, int newViewportX, int newViewportY) {
    currentViewportX = newViewportX;
    currentViewportY = newViewportY;
    G.setClip(currentViewportX * XIExt, currentViewportY * YIExt, (currentViewportX + 1) * XIExt,
        (currentViewportY + 1) * YIExt);
  }

  protected int X(double x) {
    return (int) (XIExt + x/Rmax * ScaleExt) / 2 + XIExt * currentViewportX;
  }

  protected int Y(double y) {
    return (int) (YIExt + y/Rmax * ScaleExt) / 2 + YIExt * currentViewportY;
  }

  protected double Xinv(int xi) {
    //xi=(int)(XIExt + x / Rmax * ScaleExt) / 2 + XIExt * currentViewportX
    //(double)xi=(XIExt + x / Rmax * ScaleExt) / 2 + XIExt * currentViewportX
    //(double)xi-XIExt * currentViewportX=(XIExt + x / Rmax * ScaleExt) / 2
    //2*((double)xi-XIExt * currentViewportX)=XIExt + x / Rmax * ScaleExt
    //2*((double)xi-XIExt * currentViewportX)-XIExt= x / Rmax * ScaleExt
    //(2*((double)xi-XIExt * currentViewportX)-XIExt)/ScaleExt*Rmax= x
    return (2*((double)xi-XIExt * currentViewportX)-XIExt)/ScaleExt*Rmax;
  }
  
  protected double Yinv(int yi) {
    return (2*((double)yi-YIExt * currentViewportY)-YIExt)/ScaleExt*Rmax;
  }
  
  public static final Color[] colors = new Color[] { Color.BLACK, new Color(128, 64, 0), // Brown
      Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.DARK_GRAY, Color.WHITE };

  public void setColor(Graphics G, int colorIndex) {
    G.setColor(colors[colorIndex % 10]);
  }
  protected void drawLine(Graphics G, double x1, double y1, double x2, double y2) {
    G.drawLine(X(x1),Y(y1),X(x2),Y(y2));
  }
  protected void drawX(Graphics G, double x, double y, int length) {
    int thisX = X(x);
    int thisY = Y(y);
    G.drawLine(thisX - length, thisY - length, thisX + length, thisY + length);
    G.drawLine(thisX - length, thisY + length, thisX + length, thisY - length);
  }

  protected void drawX(Graphics G, double x, double y) {
    drawX(G, x, y, 2);
  }

  protected void drawCircle(Graphics G, double x, double y, double r) {
    drawEllipse(G, x - r, y - r, x + r, y + r);
  }

  protected void drawEllipse(Graphics G, double x1, double y1, double x2, double y2) {
    G.drawOval(X(x1), Y(y1), X(x2) - X(x1), Y(y2) - Y(y1));
  }

  protected ZoetropeCanvas Canvas;
  private JPanel ChartPanel;
  private JPanel jPanel1 = null;
  private JButton ToBeginning = null;
  private JButton Rewind = null;
  private JToggleButton Reverse = null;
  private JToggleButton Pause = null;
  private JToggleButton Play = null;
  private JButton FFwd = null;
  private JButton ToEnd = null;
  private JCheckBox LoopAnimation = null;
  private JLabel jLabel = null;
  private JTextField FramePeriod = null;
  private JLabel jLabel1 = null;

  public void run() {
    while (isVisible()) {
      for (CurrentFrame = 0; CurrentFrame < getNumFrames(); CurrentFrame++) {
        Canvas.repaint();
        try {
          Thread.sleep(getFramePeriodMs());
        } catch (InterruptedException e) {
        }
      }
    }
  }

  public void start() {
    new Thread(this).start();
  }

  protected abstract void paintFrame(Graphics G);

  protected String FrameName() {
    return String.format("%d/%d", CurrentFrame, getNumFrames());
  }

  public void paintFrame(Graphics g, int width, int height) {
    XIExt = width / nViewportX;
    YIExt = height / nViewportY;
    ScaleExt = XIExt > YIExt ? YIExt : XIExt;
    setViewport(g, 0, 0);
    paintFrame(g);
    g.setClip(null);
    g.drawString(FrameName(), 10, 10);
  }
  public void drawBox(Graphics g, double x, double y, int size) {
    int thisX=X(x);
    int thisY=Y(y);
    g.drawLine(thisX - size, thisY - size, thisX - size, thisY + size);
    g.drawLine(thisX - size, thisY + size, thisX + size, thisY + size);
    g.drawLine(thisX + size, thisY + size, thisX + size, thisY - size);
    g.drawLine(thisX + size, thisY - size, thisX - size, thisY - size);
  }
  /**
   * This method initializes jPanel1
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel1() {
    if (jPanel1 == null) {
      GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
      gridBagConstraints13.gridx = 1;
      gridBagConstraints13.gridwidth = -1;
      gridBagConstraints13.anchor = GridBagConstraints.WEST;
      gridBagConstraints13.gridy = 2;
      jLabel1 = new JLabel();
      jLabel1.setText("Frame Period (ms)");
      GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
      gridBagConstraints12.fill = GridBagConstraints.BOTH;
      gridBagConstraints12.gridy = 2;
      gridBagConstraints12.weightx = 0.0D;
      gridBagConstraints12.gridx = 0;
      GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
      gridBagConstraints11.gridx = 1;
      gridBagConstraints11.gridwidth = -1;
      gridBagConstraints11.anchor = GridBagConstraints.WEST;
      gridBagConstraints11.gridy = 1;
      jLabel = new JLabel();
      jLabel.setText("Loop Animation");
      GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
      gridBagConstraints10.gridx = 0;
      gridBagConstraints10.gridy = 1;
      GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
      gridBagConstraints9.gridx = 6;
      gridBagConstraints9.gridy = 0;
      GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
      gridBagConstraints8.gridx = 5;
      gridBagConstraints8.gridy = 0;
      GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
      gridBagConstraints7.gridx = 4;
      gridBagConstraints7.gridy = 0;
      GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
      gridBagConstraints6.gridx = 3;
      gridBagConstraints6.gridy = 0;
      GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
      gridBagConstraints5.gridx = 2;
      gridBagConstraints5.gridy = 0;
      GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
      gridBagConstraints4.gridx = 1;
      gridBagConstraints4.gridy = 0;
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      jPanel1 = new JPanel();
      jPanel1.setLayout(new GridBagLayout());
      jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Zoetrope Controls", TitledBorder.DEFAULT_JUSTIFICATION,
          TitledBorder.DEFAULT_POSITION, null, null));
      jPanel1.add(getToBeginning(), gridBagConstraints);
      jPanel1.add(getRewind(), gridBagConstraints4);
      jPanel1.add(getReverse(), gridBagConstraints5);
      jPanel1.add(getPause(), gridBagConstraints6);
      jPanel1.add(getPlay(), gridBagConstraints7);
      jPanel1.add(getFFwd(), gridBagConstraints8);
      jPanel1.add(getToEnd(), gridBagConstraints9);
      jPanel1.add(getLoopAnimation(), gridBagConstraints10);
      jPanel1.add(jLabel, gridBagConstraints11);
      jPanel1.add(getFramePeriod(), gridBagConstraints12);
      jPanel1.add(jLabel1, gridBagConstraints13);
    }
    return jPanel1;
  }

  /**
   * This method initializes ToBeginning
   * 
   * @return javax.swing.JButton
   */
  private JButton getToBeginning() {
    if (ToBeginning == null) {
      ToBeginning = new JButton();
      ToBeginning.setText("|<");
    }
    return ToBeginning;
  }

  /**
   * This method initializes Rewind
   * 
   * @return javax.swing.JButton
   */
  private JButton getRewind() {
    if (Rewind == null) {
      Rewind = new JButton();
      Rewind.setText("<<");
    }
    return Rewind;
  }

  /**
   * This method initializes Reverse
   * 
   * @return javax.swing.JButton
   */
  private JToggleButton getReverse() {
    if (Reverse == null) {
      Reverse = new JToggleButton();
      Reverse.setText("<");
    }
    return Reverse;
  }

  /**
   * This method initializes Pause
   * 
   * @return javax.swing.JButton
   */
  private JToggleButton getPause() {
    if (Pause == null) {
      Pause = new JToggleButton();
      Pause.setText("||");
    }
    return Pause;
  }

  /**
   * This method initializes Play
   * 
   * @return javax.swing.JToggleButton
   */
  private JToggleButton getPlay() {
    if (Play == null) {
      Play = new JToggleButton();
      Play.setText(">");
    }
    return Play;
  }

  /**
   * This method initializes FFwd
   * 
   * @return javax.swing.JButton
   */
  private JButton getFFwd() {
    if (FFwd == null) {
      FFwd = new JButton();
      FFwd.setText(">>");
    }
    return FFwd;
  }

  /**
   * This method initializes ToEnd
   * 
   * @return javax.swing.JButton
   */
  private JButton getToEnd() {
    if (ToEnd == null) {
      ToEnd = new JButton();
      ToEnd.setText(">|");
    }
    return ToEnd;
  }

  /**
   * This method initializes LoopAnimation
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getLoopAnimation() {
    if (LoopAnimation == null) {
      LoopAnimation = new JCheckBox();
    }
    return LoopAnimation;
  }

  /**
   * This method initializes FramePeriod
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getFramePeriod() {
    if (FramePeriod == null) {
      FramePeriod = new JTextField();
      FramePeriod.setText("100");
    }
    return FramePeriod;
  }

  public void setRmax(double rmax) {
    Rmax = rmax;
  }

  public double getRmax() {
    return Rmax;
  }

  public void setNumFrames(int numFrames) {
    NumFrames = numFrames;
  }

  public int getNumFrames() {
    return NumFrames;
  }
}
