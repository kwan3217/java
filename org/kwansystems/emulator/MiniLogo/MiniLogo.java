package org.kwansystems.emulator.MiniLogo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;

public class MiniLogo extends JFrame implements ActionListener {
  private TurtleCanvas Canvas;
  private VM vm;
  private JPanel ChartPanel;
  private JTextArea data;
  private JTextField line;
  private JButton go;
  Lex L;
  Parse P;
  ByteArrayOutputStream BinOuf=new ByteArrayOutputStream();
  private void initComponents() {
    GridBagConstraints g = new GridBagConstraints();
    g.gridx = 0;
    g.fill = GridBagConstraints.BOTH;
    g.weighty = 1.0D;
    g.weightx = 1.0D;
    g.gridy = 0;
    g.gridheight = 3;
    ChartPanel = new javax.swing.JPanel();
    getContentPane().setLayout(new java.awt.GridBagLayout());

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    this.setSize(new Dimension(390, 352));
    this.setTitle("Kwan Systems MiniLogo");
    this.setContentPane(ChartPanel);

    ChartPanel.setLayout(new GridBagLayout());

    vm=new VM();
    Canvas = new TurtleCanvas(vm);
    vm.setFace(Canvas);
    ChartPanel.add(Canvas, g);
    g.gridheight=1;
    g.weightx=0;
    g.gridx = 1;
    data=new JTextArea(20,40);
    ChartPanel.add(data,g);
    g.weighty=0;
    g.gridy = 1;
    line=new JTextField();
    ChartPanel.add(line,g);
    g.gridy = 2;
    go=new JButton("Compile and run");
    ChartPanel.add(go,g);
    go.addActionListener(this);
    Canvas.setPreferredSize(new java.awt.Dimension(400, 400));
    pack();

    L=new Lex();
    System.out.println(L);
    P=new Parse(System.out,BinOuf);
    System.out.println(P);
  }
  public MiniLogo() throws IOException {
    initComponents();
  }
  public void interpret(String S) {
    try {
      L.setTape(new StringTape(S));
      BinOuf.reset();
      P.compile(L);
      ByteArrayInputStream BinInf=new ByteArrayInputStream(BinOuf.toByteArray());
      vm.Load(BinInf);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
  public static void main(String[] args) throws AutomatonException, IOException {
    MiniLogo F=new MiniLogo();
    F.pack();
    F.setVisible(true);
  }
  public void actionPerformed(ActionEvent e) {
    interpret(data.getText());
    Canvas.repaint();
  }

}
