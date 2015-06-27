package org.kwansystems.tools;

import java.util.*;
import java.io.*;

public class IniFile {
  private Map<String,Map<String,List<String>>> entries;
  public IniFile(Reader Linf) throws IOException {
    entries=new HashMap<String,Map<String,List<String>>>();
    LineNumberReader inf=new LineNumberReader(Linf);
    String s=inf.readLine();
    String currentSection="";
    while(s!=null) {
      s=s.trim();
      if(s.length()>0) {
        if( s.charAt(0)=='[' && s.contains("]")) {
          currentSection=s.substring(1,s.indexOf(']')-1);
        } else if(s.charAt(0)!=';' & s.contains("=")) { //otherwise it's a comment
          String key=s.substring(0,s.indexOf('=')-1).trim();
          String value=s.substring(s.indexOf('=')+1).trim();
          if(value.contains(";")) value=value.substring(0,value.indexOf(';')).trim();
          if(!entries.containsKey(currentSection)) entries.put(currentSection, new HashMap<String,List<String>>());
          if(!entries.get(currentSection).containsKey(key)) entries.get(currentSection).put(key, new LinkedList<String>());
          entries.get(currentSection).get(key).add(value);
        }
      }
      s=inf.readLine();
    }
  }
  public IniFile(String infn) throws IOException {
    this(new FileReader(infn));
  }
  public List<String> getSections() {
    return new ArrayList(entries.keySet());
  }
  public List<String> getKeys(String section) {
    return new ArrayList(entries.get(section).keySet());
  }
  public List<String> getEntries(String section, String key) {
    return new ArrayList(entries.get(section).get(key));
  }
  public String getEntry(String section, String key) {
    return entries.get(section).get(key).get(0);
  }
  public int hasEntries(String section, String key) {
    if(!hasSection(section)) return 0;
    if(!entries.get(section).containsKey(key)) return 0;
    return entries.get(section).get(key).size();
  }
  public boolean hasEntry(String section, String key) {
    return hasEntries(section,key)>0;
  }
  public boolean hasSection(String section) {
    return entries.containsKey(section);
  }
  public static void main(String[] args) throws IOException {
    IniFile I=new IniFile("c:\\Orbiter06-P1\\config\\Earth.cfg");
    System.out.println(I.entries);
    System.out.println(I.getEntry("", "SidRotPeriod"));
  }
}
