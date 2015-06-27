package org.kwansystems.automaton.part1;

import java.util.*;

import org.kwansystems.automaton.Transition;
import org.kwansystems.tools.*;

public class NFAState<AlphabetType,StateNameType> {
  private Map<AlphabetType,Set<Transition<StateNameType>>> rowTable;
//  private static final long serialVersionUID = 1957497941772850909L;
  public boolean Accept;
  public String comment;
  public Object MooreOutput;
  public boolean Marked;
  public Collection<Set<Transition<StateNameType>>> getTransitions() {
    return rowTable.values();
  }
  public NFAState() {
    rowTable=new TreeMap<AlphabetType,Set<Transition<StateNameType>>>(NullComparator.NC);
  }
  public String toString() {
    return rowTable.toString();
  }
  public void putCopy(AlphabetType a,Collection<Transition<StateNameType>> T) {
    Set<Transition<StateNameType>> ts=new TreeSet<Transition<StateNameType>>(NullComparator.NC);
    for(Transition<StateNameType> t:T) ts.add(t);
    rowTable.put(a,ts);
  }
  public void put(AlphabetType a,Set<Transition<StateNameType>> T) {
    rowTable.put(a,T);
  }
  public void put(AlphabetType a,Transition<StateNameType> T) {
    Set<Transition<StateNameType>> ts=new TreeSet<Transition<StateNameType>>(NullComparator.NC);
    ts.add(T);
    rowTable.put(a,ts);
  }
  public Set<Transition<StateNameType>> get(AlphabetType a) {
    return rowTable.get(a);
  }
  public void addTrans(AlphabetType a,Transition<StateNameType> T) {
    Set<Transition<StateNameType>> ts=rowTable.get(a);
    if(ts==null){
      ts=new TreeSet<Transition<StateNameType>>(NullComparator.NC);
      rowTable.put(a,ts);
    }
    ts.add(T);
  }
  public void addTrans(AlphabetType a,Collection<Transition<StateNameType>> T) {
    Set<Transition<StateNameType>> ts=rowTable.get(a);
    if(ts==null){
      ts=new TreeSet<Transition<StateNameType>>(NullComparator.NC);
      rowTable.put(a,ts);
    }
    for(Transition<StateNameType> t:T) ts.add(t);
  }
  public void clear() {
    rowTable.clear();
    Accept=false;
    comment=null;
    MooreOutput=null;
    Marked=false;
  }
  public Set<AlphabetType> getAlphabet() {
    return rowTable.keySet();
  }
}