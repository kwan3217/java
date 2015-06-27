package org.kwansystems.emulator.postscript;

import static java.awt.event.ItemEvent.SELECTED;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.kwansystems.emulator.postscript.execstack.ExecStackEntry;

public class PsStackDisplay extends JFrame { 
  private static final long serialVersionUID = 575749389917061720L;
  private JPanel jContentPane = null;
  private JSplitPane SplitPane1 = null;
  private JSplitPane SplitPane2 = null;
  private JPanel OpStackPanel = null;
  private JPanel ButtonPanel = null;
  private JButton StepButton = null;
  private JToggleButton AnimateButton = null;
  private JTextField AnimateStepText = null;
  private JScrollPane OpStackScrollPane = null;
  private JTree OpStackTree = null;
  private JPanel DictStackPanel = null;
  private JScrollPane DictStackScrollPane = null;
  private JTree DictStackTree = null;
  private JPanel ExecStackPanel = null;
  private JScrollPane ExecStackScrollPane = null;
  private JTree ExecStackTree = null;
  private PsStackDisplayListener PSDL;
  private Timer AnimateTimer = null;  
  
  public PsStackDisplay(PsStackDisplayListener LPSDL) {
    PSDL=LPSDL;
		initialize();
  }

  /**
   * This method initializes this
   * 
   */
  private void initialize() {
    setTitle("Execution Environment");
    this.setMinimumSize(new java.awt.Dimension(364,477));
    this.setSize(new java.awt.Dimension(606,540));
    setContentPane(getJContentPane());
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
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
      jContentPane=new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getSplitPane1(), java.awt.BorderLayout.CENTER);
      jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes SplitPane1	
   * 	
   * @return javax.swing.JSplitPane	
   */
  private JSplitPane getSplitPane1() {
    if (SplitPane1==null) {
      SplitPane1=new JSplitPane();
      SplitPane1.setLeftComponent(getSplitPane2());
      SplitPane1.setRightComponent(getExecStackPanel());
    }
    return SplitPane1;
  }

  /**
   * This method initializes SplitPane2	
   * 	
   * @return javax.swing.JSplitPane	
   */
  private JSplitPane getSplitPane2() {
    if (SplitPane2==null) {
      SplitPane2=new JSplitPane();
      SplitPane2.setLeftComponent(getOpStackPanel());
      SplitPane2.setRightComponent(getDictStackPanel());
    }
    return SplitPane2;
  }

  /**
   * This method initializes OpStackPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getOpStackPanel() {
    if (OpStackPanel==null) {
      OpStackPanel=new JPanel();
      OpStackPanel.setLayout(new BorderLayout());
      OpStackPanel.add(getOpStackScrollPane(), java.awt.BorderLayout.CENTER);
    }
    return OpStackPanel;
  }

  /**
   * This method initializes ButtonPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getButtonPanel() {
    if (ButtonPanel==null) {
      ButtonPanel=new JPanel();
      ButtonPanel.add(getStepButton(), null);
      ButtonPanel.add(getAnimateButton(), null);
      ButtonPanel.add(getAnimateStepText(), null);
    }
    return ButtonPanel;
  }

  /**
   * This method initializes StepButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getStepButton() {
    if (StepButton==null) {
      StepButton=new JButton();
      StepButton.setText("Step");
      StepButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          PSDL.Step();
        }
      });
    }
    return StepButton;
  }

  /**
   * This method initializes AnimateButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getAnimateButton() {
    if (AnimateButton==null) {
      AnimateButton=new JToggleButton();
      AnimateButton.setText("Animate");
      AnimateButton.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if(e.getStateChange()==SELECTED) {
            AnimateTimer=new Timer();
            AnimateTimer.schedule(
                new TimerTask() {
                  public void run() {
                    PSDL.Step();
                  }
                },
                0,Integer.parseInt(getAnimateStepText().getText())
            );
          } else {
            AnimateTimer.cancel();
          }
        }
      });
    }
    return AnimateButton;
  }

  /**
   * This method initializes AnimateStepText	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAnimateStepText() {
    if (AnimateStepText==null) {
      AnimateStepText=new JTextField();
      AnimateStepText.setText("100");
    }
    return AnimateStepText;
  }

  /**
   * This method initializes OpStackScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getOpStackScrollPane() {
    if (OpStackScrollPane==null) {
      OpStackScrollPane=new JScrollPane();
      OpStackScrollPane.setViewportView(getOpStackTree());
    }
    return OpStackScrollPane;
  }

  /**
   * This method initializes OpStackTree	
   * 	
   * @return javax.swing.JTree	
   */
  private JTree getOpStackTree() {
    if (OpStackTree==null) {
      DefaultMutableTreeNode Model=new DefaultMutableTreeNode("Operand Stack");
      Model.add(new DefaultMutableTreeNode("Stack base"));
      OpStackTree=new JTree(Model);
    }
    return OpStackTree;
  }
  /**
   * This method initializes jPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getDictStackPanel() {
    if (DictStackPanel==null) {
      DictStackPanel=new JPanel();
      DictStackPanel.setLayout(new BorderLayout());
      DictStackPanel.add(getDictStackScrollPane(), java.awt.BorderLayout.CENTER);
    }
    return DictStackPanel;
  }

  /**
   * This method initializes jScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getDictStackScrollPane() {
    if (DictStackScrollPane==null) {
      DictStackScrollPane=new JScrollPane();
      DictStackScrollPane.setViewportView(getDictStackTree());
    }
    return DictStackScrollPane;
  }

  /**
   * This method initializes jTree	
   * 	
   * @return javax.swing.JTree	
   */
  private JTree getDictStackTree() {
    if (DictStackTree==null) {
      DefaultMutableTreeNode Model=new DefaultMutableTreeNode("Dictionary Stack");
      Model.add(new DefaultMutableTreeNode("Stack base"));
      DictStackTree=new JTree(Model);
    }
    return DictStackTree;
  }

  /**
   * This method initializes jPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getExecStackPanel() {
    if (ExecStackPanel==null) {
      ExecStackPanel=new JPanel();
      ExecStackPanel.setLayout(new BorderLayout());
      ExecStackPanel.add(getExecStackScrollPane(), java.awt.BorderLayout.CENTER);
    }
    return ExecStackPanel;
  }

  /**
   * This method initializes jScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getExecStackScrollPane() {
    if (ExecStackScrollPane==null) {
      ExecStackScrollPane=new JScrollPane();
      ExecStackScrollPane.setViewportView(getExecStackTree());
    }
    return ExecStackScrollPane;
  }

  /**
   * This method initializes jTree	
   * 	
   * @return javax.swing.JTree	
   */
  private JTree getExecStackTree() {
    if (ExecStackTree==null) {
      DefaultMutableTreeNode Model=new DefaultMutableTreeNode("Execution Stack");
      Model.add(new DefaultMutableTreeNode("Stack base"));
      ExecStackTree=new JTree(Model);
    }
    return ExecStackTree;
  }

  public static void main(String[] args) {
    new PsStackDisplay(null).show();
  }
  
  public void OpPush(PsObject O) {
    DefaultTreeModel Model=(DefaultTreeModel)OpStackTree.getModel();
    MutableTreeNode Root=(MutableTreeNode)Model.getRoot();
    Model.insertNodeInto(O.getNode(),Root,Root.getChildCount());
  }

  public void OpPop() {
    DefaultTreeModel Model=(DefaultTreeModel)OpStackTree.getModel();
    MutableTreeNode Root=(MutableTreeNode)Model.getRoot();
    Model.removeNodeFromParent((MutableTreeNode)Model.getChild(Root,Root.getChildCount()-1));
  }
  
  public void StackClear(JTree StackTree,String Title) {
    DefaultMutableTreeNode Root=new DefaultMutableTreeNode(Title);
    Root.add(new DefaultMutableTreeNode("Stack base"));
    DefaultTreeModel Model=new DefaultTreeModel(Root);
    StackTree.setModel(Model);
  }
  public void OpStackClear() {StackClear(OpStackTree,"Operand Stack");}
  public void DictStackClear() {StackClear(DictStackTree,"Dictionary Stack");}
  public void ExecStackClear() {StackClear(ExecStackTree,"Execution Stack");}

  public void DictPush(PsObject O) {
    DefaultTreeModel Model=(DefaultTreeModel)DictStackTree.getModel();
    MutableTreeNode Root=(MutableTreeNode)Model.getRoot();
    Model.insertNodeInto(O.getNode(),Root,Root.getChildCount());
  }

  public void DictPop() {
    DefaultTreeModel Model=(DefaultTreeModel)DictStackTree.getModel();
    MutableTreeNode Root=(MutableTreeNode)Model.getRoot();
    Model.removeNodeFromParent((MutableTreeNode)Model.getChild(Root,Root.getChildCount()-1));
  }
  public void ExecPush(ExecStackEntry O) {
    DefaultTreeModel Model=(DefaultTreeModel)ExecStackTree.getModel();
    MutableTreeNode Root=(MutableTreeNode)Model.getRoot();
    Model.insertNodeInto(O.getNode(),Root,Root.getChildCount());
  }
  public void ExecPop() {
    DefaultTreeModel Model=(DefaultTreeModel)ExecStackTree.getModel();
    MutableTreeNode Root=(MutableTreeNode)Model.getRoot();
    Model.removeNodeFromParent((MutableTreeNode)Model.getChild(Root,Root.getChildCount()-1));
  }
  public void pause() {
    getAnimateButton().setSelected(false);
  }
}  //  @jve:decl-index=0:visual-constraint="10,10"
