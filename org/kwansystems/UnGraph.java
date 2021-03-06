/*
 * UnGraph.java
 *
 * Created on October 26, 2003, 11:39 AM
 */

package org.kwansystems;

import javax.swing.*;
import java.awt.*;

import org.kwansystems.tools.chart.*;

/**
 *
 * @author  chrisj
 */
public class UnGraph extends javax.swing.JFrame {
	private static final long serialVersionUID = -8213758177293828515L;
	static String ImageName;
    /** Creates new form UnGraph */
    public UnGraph() {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup2 = new javax.swing.ButtonGroup();
        fc = new javax.swing.JFileChooser();
        ButtonPanel = new javax.swing.JPanel();
        LoadDataBtn = new javax.swing.JButton();
        LoadImgBtn = new javax.swing.JButton();
        CalibrationPanel = new javax.swing.JPanel();
        CalLLBtn = new javax.swing.JToggleButton();
        CalURBtn = new javax.swing.JToggleButton();
        JLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        LLScreenX = new javax.swing.JTextField();
        LLScreenY = new javax.swing.JTextField();
        LLPhysX = new javax.swing.JTextField();
        LLPhysY = new javax.swing.JTextField();
        URScreenX = new javax.swing.JTextField();
        URScreenY = new javax.swing.JTextField();
        URPhysX = new javax.swing.JTextField();
        URPhysY = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        MainGraph = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DataTable = new javax.swing.JTable();
        InsertRad = new javax.swing.JRadioButton();
        OverwriteRad = new javax.swing.JRadioButton();
        AppendRad = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        UniformBtn = new javax.swing.JToggleButton();
        UniformInterval = new javax.swing.JTextField();
        UniformThis = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();


        setTitle("UnGraph");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        ButtonPanel.setLayout(new java.awt.GridBagLayout());

        LoadDataBtn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        LoadDataBtn.setText("Load Data");
        ButtonPanel.add(LoadDataBtn, new java.awt.GridBagConstraints());

        LoadImgBtn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        LoadImgBtn.setText("Load Image");
        LoadImgBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadImgBtnActionPerformed(evt);
            }
        });
        ButtonPanel.add(LoadImgBtn, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(ButtonPanel, gridBagConstraints);

        CalibrationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Calibration"));
        CalibrationPanel.setLayout(new java.awt.GridBagLayout());

        CalLLBtn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        CalLLBtn.setText("Lower left");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(CalLLBtn, gridBagConstraints);

        CalURBtn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        CalURBtn.setText("Upper right");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(CalURBtn, gridBagConstraints);

        JLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        JLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JLabel1.setText("Screen Coordinates");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(JLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Physical Coordinates");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(jLabel2, gridBagConstraints);

        LLScreenX.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        CalibrationPanel.add(LLScreenX, gridBagConstraints);

        LLScreenY.setText("607");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        CalibrationPanel.add(LLScreenY, gridBagConstraints);

        LLPhysX.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        CalibrationPanel.add(LLPhysX, gridBagConstraints);

        LLPhysY.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        CalibrationPanel.add(LLPhysY, gridBagConstraints);

        URScreenX.setText("722");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(URScreenX, gridBagConstraints);

        URScreenY.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(URScreenY, gridBagConstraints);

        URPhysX.setText("140");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(URPhysX, gridBagConstraints);

        URPhysY.setText("6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        CalibrationPanel.add(URPhysY, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(CalibrationPanel, gridBagConstraints);

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setResizeWeight(0.5);

        MainGraph.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        MainGraph.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MainGraphMouseClicked(evt);
            }
        });
        jSplitPane1.setLeftComponent(MainGraph);

        jPanel1.setMinimumSize(new java.awt.Dimension(20, 26));
        jPanel1.setPreferredSize(new java.awt.Dimension(20, 403));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        DataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"0", "0"},
                {"10", "0.5"},
                {"20", "1"},
                {"30", "2"},
                {"40", "3"},
                {"50", "4.5"},
                {"60", "5"},
                {"70", "6"},
                {"80", "5"},
                {"90", "4"},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "X", "Y"
            }
        ));
        DataTable.setPreferredSize(new java.awt.Dimension(50, 16000));
        jScrollPane1.setViewportView(DataTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        buttonGroup2.add(InsertRad);
        InsertRad.setText("Insert");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(InsertRad, gridBagConstraints);

        buttonGroup2.add(OverwriteRad);
        OverwriteRad.setSelected(true);
        OverwriteRad.setText("Overwrite");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(OverwriteRad, gridBagConstraints);

        buttonGroup2.add(AppendRad);
        AppendRad.setText("Append");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(AppendRad, gridBagConstraints);

        jButton1.setText("Draw");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jButton1, gridBagConstraints);

        UniformBtn.setText("Regular Interval");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel1.add(UniformBtn, gridBagConstraints);

        UniformInterval.setText("5");
        UniformInterval.setToolTipText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(UniformInterval, gridBagConstraints);

        UniformThis.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(UniformThis, gridBagConstraints);

        jButton2.setText("Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jButton2, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private int PhysToScreen(int ScreenMin, double PhysMin, int ScreenMax, double PhysMax, double Phys) {
      int ScreenWidth=ScreenMax-ScreenMin;
      double PhysWidth=PhysMax-PhysMin;
      double PhysNormal=(Phys-PhysMin)/PhysWidth;
      return ((int)(PhysNormal*ScreenWidth))+ScreenMin;
    }

    private double ScreenToPhys(int ScreenMin, double PhysMin, int ScreenMax, double PhysMax, int Screen) {
      double ScreenWidth=ScreenMax-ScreenMin;
      double PhysWidth=PhysMax-PhysMin;
      double ScreenNormal=((double)Screen-ScreenMin)/ScreenWidth;
      return ScreenNormal*PhysWidth+PhysMin;
    }

    private int PhysToScreenX(double Phys) {
      return PhysToScreen(Integer.parseInt(LLScreenX.getText()),
                          Double.parseDouble(LLPhysX.getText()),
                          Integer.parseInt(URScreenX.getText()),
                          Double.parseDouble(URPhysX.getText()),
                          Phys);
    }

    private int PhysToScreenY(double Phys) {
      return PhysToScreen(Integer.parseInt(LLScreenY.getText()),
                          Double.parseDouble(LLPhysY.getText()),
                          Integer.parseInt(URScreenY.getText()),
                          Double.parseDouble(URPhysY.getText()),
                          Phys);
    }

    private double ScreenToPhysX(int Screen) {
      return ScreenToPhys(Integer.parseInt(LLScreenX.getText()),
                          Double.parseDouble(LLPhysX.getText()),
                          Integer.parseInt(URScreenX.getText()),
                          Double.parseDouble(URPhysX.getText()),
                          Screen);
    }

    private double ScreenToPhysY(int Screen) {
      return ScreenToPhys(Integer.parseInt(LLScreenY.getText()),
                          Double.parseDouble(LLPhysY.getText()),
                          Integer.parseInt(URScreenY.getText()),
                          Double.parseDouble(URPhysY.getText()),
                          Screen);
    }
    
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
      // Add your handling code here:
      double PhysX0=Double.NaN;
      double PhysY0=Double.NaN;
      double PhysX1,PhysY1;
      int ScreenX0=0;
      int ScreenY0=0;
      int ScreenX1,ScreenY1;
      Graphics G=MainGraph.getGraphics();
      G.drawImage(image,0,0,this);
      G.setColor(Color.MAGENTA);
      for(int i=0;i<DataTable.getRowCount();i++) {
        try {
          String X1=(String)(DataTable.getValueAt(i, 0));
          String Y1=(String)(DataTable.getValueAt(i, 1));
          if(X1!=null) {
            PhysX1=Double.parseDouble(X1);
            PhysY1=Double.parseDouble(Y1);
            ScreenX1=PhysToScreenX(PhysX1);
            ScreenY1=PhysToScreenY(PhysY1);
            if(!Double.isNaN(PhysX0)) {
              G.drawLine(ScreenX0,ScreenY0,ScreenX1,ScreenY1);
            }
            G.drawRect(ScreenX1-3, ScreenY1-3, 5, 5);
            ScreenX0=ScreenX1;
            ScreenY0=ScreenY1;
            PhysX0=PhysX1;
            PhysY0=PhysY1;
          } else {
            PhysX0=Double.NaN;
            PhysY0=Double.NaN;
          }
        } catch (NumberFormatException E) {
          PhysX0=Double.NaN;
          PhysY0=Double.NaN;
        }
      }
  }                                        
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      // Add your handling code here:
      double PhysX1,PhysY1;
      ChartRecorder CR=new ArrayListChartRecorder("X");
      for(int i=0;i<DataTable.getRowCount();i++) {
        try {
          String X1=(String)(DataTable.getValueAt(i, 0));
          String Y1=(String)(DataTable.getValueAt(i, 1));
          if(X1!=null) {
            PhysX1=Double.parseDouble(X1);
            PhysY1=Double.parseDouble(Y1);
            CR.Record(PhysX1,"Y",PhysY1);            
          }
        } catch (NumberFormatException E) {
        }
      }
      CR.PrintTable(new CSVPrinter("UnGraph.csv"));
  }//GEN-LAST:event_jButton1ActionPerformed

    private void MainGraphMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MainGraphMouseClicked
        // Add your handling code here:
      int X=evt.getX();
      int Y=evt.getY();
      if(CalLLBtn.isSelected()) {
        LLScreenX.setText(Integer.toString(X));
        LLScreenY.setText(Integer.toString(Y));
      } else if(CalURBtn.isSelected()) {
        URScreenX.setText(Integer.toString(X));
        URScreenY.setText(Integer.toString(Y));
      } else {
        int RowNum=DataTable.getSelectedRow();
        if(RowNum<0) RowNum=0;
        double PhysX;
        if(UniformBtn.isSelected()) {
          PhysX=Double.parseDouble(UniformThis.getText());
          double NewX=PhysX+Double.parseDouble(UniformInterval.getText());
          UniformThis.setText(Double.toString(NewX));
          Graphics G=MainGraph.getGraphics();
          G.setColor(Color.BLACK);
          G.drawLine(PhysToScreenX(NewX), 0, PhysToScreenX(NewX), 1000);
        } else {
          PhysX=ScreenToPhysX(X);
        }
        double PhysY=ScreenToPhysY(Y);
        DataTable.setValueAt(Double.toString(PhysX),RowNum,0);
        DataTable.setValueAt(Double.toString(PhysY),RowNum,1);
        ListSelectionModel M=DataTable.getSelectionModel();
        M.clearSelection();
        M.addSelectionInterval(RowNum+1, RowNum+1);
      }
    }//GEN-LAST:event_MainGraphMouseClicked

    private void LoadImgBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadImgBtnActionPerformed
      // Add your handling code here:
      int returnVal = fc.showOpenDialog(this);  // End of variables declaration//GEN-END:variables
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        ImageName = fc.getSelectedFile().toString();
        //This is where a real application would open the file.
        ImageIcon icon = new ImageIcon(ImageName);
        image=icon.getImage();
        MainGraph.repaint();
      }
      Graphics G=MainGraph.getGraphics();
      G.drawImage(image,0,0,this);
    }//GEN-LAST:event_LoadImgBtnActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
/*
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
  */
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new UnGraph().show();
    }
    
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JRadioButton AppendRad;
  private javax.swing.JPanel ButtonPanel;
  private javax.swing.JToggleButton CalLLBtn;
  private javax.swing.JToggleButton CalURBtn;
  private javax.swing.JPanel CalibrationPanel;
  private javax.swing.JTable DataTable;
  private javax.swing.JRadioButton InsertRad;
  private javax.swing.JLabel JLabel1;
  private javax.swing.JTextField LLPhysX;
  private javax.swing.JTextField LLPhysY;
  private javax.swing.JTextField LLScreenX;
  private javax.swing.JTextField LLScreenY;
  private javax.swing.JButton LoadDataBtn;
  private javax.swing.JButton LoadImgBtn;
  private javax.swing.JPanel MainGraph;
  private javax.swing.JRadioButton OverwriteRad;
  private javax.swing.JTextField URPhysX;
  private javax.swing.JTextField URPhysY;
  private javax.swing.JTextField URScreenX;
  private javax.swing.JTextField URScreenY;
  private javax.swing.JToggleButton UniformBtn;
  private javax.swing.JTextField UniformInterval;
  private javax.swing.JTextField UniformThis;
  private javax.swing.ButtonGroup buttonGroup2;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSplitPane jSplitPane1;
  //Create a file chooser
  private javax.swing.JFileChooser fc;
  private Image image;
}
