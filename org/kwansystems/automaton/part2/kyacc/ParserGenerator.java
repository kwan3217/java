package org.kwansystems.automaton.part2.kyacc;

import java.util.*;

public abstract class ParserGenerator {
  public enum Assoc {LEFT,RIGHT,NON};
  protected void SRReducePrec(int row, Object S, Production R) {
    warnings.add("*Info: Shift-Reduce conflict resolved with precedence table, row "+row+", symbol "+S.toString());
    warnings.add("*: Reduce "+R+" has higher precedence, used in place of shift "+S);
  }
  protected void SRShiftPrec(int row, Object S, Production R) {
    warnings.add("*Info: Shift-Reduce conflict resolved with precedence table, row "+row+", symbol "+S.toString());
    warnings.add("*: Shift "+S+" has higher precedence, used in place of reduce "+R);
  }
  protected void SRLeftAssoc(int row, Object S, Production R) {
    warnings.add("*Info: Shift-Reduce conflict resolved with associativity table, row "+row+", symbol "+S.toString());
    warnings.add("*: Operator is left-associative, Reduce "+R+" used in place of shift "+S);
  }
  protected void SRRightAssoc(int row, Object S, Production R) {
    warnings.add("*Info: Shift-Reduce conflict resolved with associativity table, row "+row+", symbol "+S.toString());
    warnings.add("*: Operator is right-associative, Shift "+S+" used in place of reduce "+R);
  }
  protected void SRNonAssoc(int row, Object S, Production R) {
    warnings.add("*Info: Shift-Reduce conflict resolved with associativity table, row "+row+", symbol "+S.toString());
    warnings.add("*: Operator is non-associative, neither shift "+S+" nor reduce "+R+" is used");
  }
  protected void RR(int row, Object S, Production oldR, Production newR) {
    //If you actually do something about this, override and tell what you did.
    throw new ReduceReduceConflict(row,S,oldR,newR);
  }
  public List<String> warnings;
  protected abstract void GenerateShift(Grammar G, List context, List<Map<Object,LRTransition>> delta, String DataPrefix);
  protected abstract void GenerateReduce(Grammar G, List context, List<Map<Object,LRTransition>> delta, Map<Object,Integer> prec, Map<Object,Assoc> assoc, String DataPrefix);
  /**
   *
   * @param G
   * @param prec Precedence table. Doesn't define the grammar, but disambiguates the parse
   * tree. Can't be calculated from the grammar (if it could, the grammar
   * wouldn't be ambiguous). Maps terminals to their precedence levels, 0 is
   * lowest, -1 if the terminal doesn't have any precedence.
   * @param assoc Associativity table. Doesn't define the grammar, but disambiguates the
   * parse tree. Can't be calculated from the grammar (if it could, the grammar
   * wouldn't be ambiguous). Maps terminals to their associativity.
   * @param DataPrefix
   * @return
   */
  public List<Map<Object,LRTransition>> Generate(Grammar G, Map<Object,Integer> prec, Map<Object,Assoc> assoc, String DataPrefix) {
    warnings=new ArrayList<String>();
    //Do the framework stuff
    G.addDummyFirst();
    //Construct the state table
    List<Map<Object,LRTransition>> delta=new ArrayList<Map<Object,LRTransition>>();
    List context=new ArrayList();

    //Build the shift and goto part
    GenerateShift(G,context,delta,DataPrefix);

    //Build the reduce part
    GenerateReduce(G,context,delta,prec,assoc,DataPrefix);

    return delta;
  }
  public void buildPrecAssoc(Map<Object,Integer> prec, Map<Object,Assoc> assoc, Object[][] Lprec, Assoc[] Lassoc) {
    for(int i=0;i<Lprec.length;i++) {
      for(Object s:Lprec[i]) {
        prec.put(s,i);
        assoc.put(s, Lassoc[i]);
      }
    }
  }
  public List<Map<Object,LRTransition>> Generate(Grammar G, String DataPrefix) {
    return Generate(G,new LinkedHashMap<Object,Integer>(),new LinkedHashMap<Object,Assoc>(),DataPrefix);
  }
  public List<Map<Object,LRTransition>> Generate(Grammar G, Object[][] Lprec, Assoc[] Lassoc, String DataPrefix) {
    Map<Object,Integer> prec=new LinkedHashMap<Object,Integer>();
    Map<Object,Assoc> assoc=new LinkedHashMap<Object,Assoc>();
    buildPrecAssoc(prec,assoc,Lprec,Lassoc);
    return Generate(G,prec,assoc,DataPrefix);
  }
  /**
   * Gets the precedence of a production.
   * @param p A production
   * @return The precedence of the right-most terminal on the RHS which has
   * a precedence, or -1 if none meet this condition.
   */
  public int getPrec(Map<Object,Integer> prec, Production p) {
    for(int i=p.rightSide.size()-1;i>=0;i--) {
      Object r=p.rightSide.get(i);
      if(prec.containsKey(r)) {
        int j=prec.get(r);
        if(j>=0) return j;
      }
    }
    return -1;
  }
  public LRTransition resolvePrec(int row, LRTransition oldTrans, Object X, LRTransition newTrans, Grammar G, Map<Object,Integer> prec, Map<Object,Assoc> assoc) {
    if(prec.get(X)==null) throw new ShiftReduceConflict(X,G.prods.get(newTrans.to));
    int oldPrec=prec.get(X);
    int newPrec=getPrec(prec,G.prods.get(newTrans.to));
    if(oldPrec<0 || newPrec<0) {
      //One of the items has no precedence, we're stuck.
      throw new ShiftReduceConflict(X,G.prods.get(newTrans.to));
    } else if(newPrec>oldPrec) {
      //New transition has precedence, replace it
      SRReducePrec(row,X,G.prods.get(newTrans.to));
      return newTrans;
    } else if(newPrec==oldPrec) {
      //Same precedence, does associativity help?
      switch(assoc.get(X)) {
        case LEFT:
          //Use the new transition
          SRLeftAssoc(row,X,G.prods.get(newTrans.to));
          return newTrans;
        case RIGHT:
          //Use the old transition
          SRRightAssoc(row,X,G.prods.get(newTrans.to));
          return oldTrans;
        default:  //case NON:
          //Don't use either transition
          SRNonAssoc(row,X,G.prods.get(newTrans.to));
          return null;
      }
    } else {
      //oldPrec>newPrec
      SRShiftPrec(row,X,G.prods.get(newTrans.to));
      return oldTrans;
    }
  }
}
