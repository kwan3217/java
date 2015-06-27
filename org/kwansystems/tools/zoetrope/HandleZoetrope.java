/**
 * 
 */
package org.kwansystems.tools.zoetrope;

import java.awt.*;
import java.awt.event.MouseEvent;

import org.kwansystems.tools.vector.*;

import javax.swing.event.MouseInputListener;

import static java.lang.Math.*;

/**
 * @author chrisj
 *
 */
public abstract class HandleZoetrope extends Zoetrope implements MouseInputListener {
  public MathVector[] handle;
  private int grabbedHandle;
  public HandleZoetrope(String LWindowTitle, int LFramePeriodMs) {
    super(LWindowTitle, LFramePeriodMs);
    Canvas.addMouseListener(this);
    Canvas.addMouseMotionListener(this);
    handle=new MathVector[] {new MathVector(new double[]{0,0}),
                             new MathVector(new double[]{1,0}),
                             new MathVector(new double[]{2,0}),
                             new MathVector(new double[]{3,0})};
  }
  public void drawHandle(Graphics g, int i) {
    drawBox(g,handle[i].X(),handle[i].Y(),2);
  }
  public void drawHandles(Graphics g) {
    for(int i=0;i<handle.length;i++) drawHandle(g,i);
  }
  /**
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    // TODO Auto-generated method stub
  }

  /**
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {}

  /**
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {}

  /**
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    //Check if we clicked close to one of the handles
    int mousex=e.getX();
    int mousey=e.getY();
    int minDist=max(abs(X(handle[0].X())-mousex),abs(Y(handle[0].Y())-mousey));
    int minHandle=0;
    for(int i=1;i<handle.length;i++) {
      int dist= max(abs(X(handle[i].X())-mousex),abs(Y(handle[i].Y())-mousey));
      if(dist<minDist) {
        minDist=dist;
        minHandle=i;
      }
    }
    if(minDist<3) {
      grabbedHandle=minHandle;
    }
  }

  /**
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
    if(grabbedHandle<0) return;
    grabbedHandle=-1;
  }

  /**
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  public void mouseDragged(MouseEvent e) {
    if(grabbedHandle>=0) {
      int mousex=e.getX();
      int mousey=e.getY();
      handle[grabbedHandle]=new MathVector(new double[]{Xinv(mousex),Yinv(mousey)});
    }
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    // TODO Auto-generated method stub

  }

}
