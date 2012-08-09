/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

import java.io.*;
import java.util.*;

/**
 *
 * @author jeppesen
 */
public class NMEALevel0C {
  public static void main(String[] args) throws IOException {
    java.io.File F=new File("Data/Rollercoasterometry/DisneyII/Level0B");
    List<File> FileList=new ArrayList<File>();
    for(File f:F.listFiles()) FileList.add(f);
    Collections.sort(FileList);
    for(File f:FileList) {
      LineNumberReader inf=new LineNumberReader(new FileReader(f));
      String S=inf.readLine();
      while(S!=null) {
        String[] part=S.split(",");
      }
      S=inf.readLine();
      inf.close();
    }
  }
}
