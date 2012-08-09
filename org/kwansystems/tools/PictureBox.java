/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.tools;

import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author jeppesen
 */
public class PictureBox extends JPanel {
  class PictureBoxObservable extends Observable {
    public void setChanged() {
      super.setChanged();
    };
  };
  PictureBoxObservable obs=new PictureBoxObservable();
  protected void paintComponent(Graphics G) {
    super.paintComponent(G);
    obs.setChanged();
    obs.notifyObservers(G);
  }
  public void addObserver(Observer o) {
    obs.addObserver(o);
  }
  public void deleteObserver(Observer o) {
    obs.deleteObserver(o);
  }
  public void deleteObservers() {
    obs.deleteObservers();
  }
  public int countObservers() {
    return obs.countObservers();
  }
}
