package org.kwansystems.automaton.part2.kyacc;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;
import org.kwansystems.tools.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.kwansystems.automaton.part2.kyacc.ParserGenerator.Assoc;

/**
 * LR Parser. Given a grammar, a parse table, and an input stream, parse the input stream.
 * Each time a production is encountered, call the appropriate semantic action in the grammar
 * and pass it the semantic values for the production.
 * <p>
 * Note that this is a Parser, not a Parser Generator. In other words, this program is not
 * YACC, but the driver program for the output YACC generates.
 * <p>
 * This class should require no extension. The parse tables are fields of this class, the 
 * semantic actions are held in the productions of the grammar of this class.
 */
public class LRParser {
  private Grammar grammar;
  private List<Map<Object,LRTransition>> delta;
  private transient Stack<CFGStackFrame> stack;
  public boolean verbose=false;
  public LRParser(Grammar Lgrammar, List<Map<Object,LRTransition>> Ldelta) {
    stack=new Stack<CFGStackFrame>();
    grammar=Lgrammar;
    delta=Ldelta;
  }
  public LRParser(Grammar Lgrammar, Object[][] Lprec, Assoc[] Lassoc, String DataPrefix) {
    stack=new Stack<CFGStackFrame>();
    grammar=Lgrammar;
    delta=new ParsonsSLR().Generate(grammar, Lprec,Lassoc,DataPrefix);
  }
  public LRParser(Grammar Lgrammar, String DataPrefix) {
    stack=new Stack<CFGStackFrame>();
    grammar=Lgrammar;
    delta=new ParsonsSLR().Generate(grammar, DataPrefix);
  }
  public final int getCurrentState() {
    return stack.peek().state;
  }
  public static Set getSigma(List<Map<Object,LRTransition>> delta) {
    Set result=new LinkedHashSet();
    for(int i=0;i<delta.size();i++) {
      result.addAll(delta.get(i).keySet());
    }
    return result;
  }
  public static String toColor(LRTransition T) {
    String result="";
    if(T!=null) {
      if(T.type==T.type.S) {
        result="bgcolor=#ffff00|";
      } else if(T.type==T.type.R) {
        result="bgcolor=#008000|";
      } else if(T.type==T.type.G) {
        result="style=\"background:#0000ff; color:#ffffff\"|";
      } else if (T.type==T.type.ACC) {
        result="style=\"background:#000000; color:#ffffff\"|";
      }
      result=result+T.toString();
    } else {
      result="   ";
    }
    return result;
  }
  public static String toString(List<Map<Object,LRTransition>> delta) {
    //If there is a comparator, sort the alphabet
	  Set sigma=getSigma(delta);
    Set terminal=new TreeSet();
    Set nonterminal=new TreeSet();
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result, true);
    ouf.println("{| border=1 class=\"wikitable\"");
    ouf.println("|-");
    ouf.print("!   ");
    for(int sn=0;sn<delta.size();sn++) {
      Map<Object,LRTransition> S=delta.get(sn);
      for(Object j:sigma) {
        LRTransition T=S.get(j);
        if(T!=null) {
          if(T.type==T.type.S) {
            terminal.add(j);
          } else if(T.type==T.type.R) {
            terminal.add(j);
          } else if(T.type==T.type.ACC) {
            terminal.add(j);
          } else if(T.type==T.type.G) {
            nonterminal.add(j);
          }

        }
      }
    }
    for(Object a:terminal) {
      ouf.print("!!style=\"background:#80FF80;\"|"+a.toString());
    }
    for(Object a:nonterminal) {
      ouf.print("!!style=\"background:#8080FF;\"|"+a.toString());
    }
    ouf.println();
    for(int sn=0;sn<delta.size();sn++) {
      ouf.println("|-");
      Map<Object,LRTransition> S=delta.get(sn);
      ouf.printf("|%3s",sn);
      for(Object j:terminal) {
        ouf.print("||");
        LRTransition T=S.get(j);
        ouf.print(toColor(T));
      }
      for(Object j:nonterminal) {
        ouf.print("||");
        LRTransition T=S.get(j);
        ouf.print(toColor(T));
      }
      ouf.println();
    }
    ouf.println("|}");
    return result.toString();
  }
  public final String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    ouf.println(grammar);
    ouf.println(toString(delta));
    return result.toString();
  }
  private void push(int to, Token value) {
    stack.push(new CFGStackFrame(to,value));
  }
  private void shift(Token currentToken, LRTransition T) {
    push(T.to,currentToken); 
  }
  protected void trace(Production P) {
    System.out.println(P);
  }
  private void reduce(LRTransition Tr) {
    //Look up the production we are reducing
    Production thisProd=grammar.prods.get(Tr.to);
    if(verbose)trace(thisProd);
    //Pop the handle, keeping track of all the semantic values so we can pass them to the semantic action.
    Token[] rightSide=new Token[thisProd.rightSide.size()];
    for(int i=rightSide.length-1;i>=0;i--) {
      rightSide[i]=stack.pop().semanticValue;
    }
    //Run the semantic action
    Token newValue=thisProd.act(rightSide);
    //Using the state now on top of the stack, look up the new state under the correct nonterminal
    int nextState=delta.get(getCurrentState()).get(thisProd.leftSide).to;
    //Put this next state and semantic value on the stack as the reduced handle
    push(nextState,newValue);
  }
  protected void trace(int q_m,Object a_i_type, LRTransition X) {
    System.out.println(q_m+"  "+a_i_type+"   "+X);
  }
  protected void accept() {
    
  }
  public final Token parse(Tape<Token> TS) throws AutomatonException {
    push(0,null);
    boolean done=false;
    Token a_i=TS.read();
    while(!done) {
      int q_m=getCurrentState();
      LRTransition X=delta.get(q_m).get(a_i.type);
      if(verbose)trace(q_m,a_i.type,X);
      if(X==null) ReportError("Unexpected "+a_i.type);
      switch(X.type) {
        case S:
          shift(a_i,X);
          TS.Right();
          a_i=TS.read(); //Only need a new token when one is shifted
          break;
        case R:
          reduce(X);
          break;
        case ACC:
          accept();
          done=true;
          break;
      }
    }
    return stack.pop().semanticValue;
  }
  
  protected void ReportError(String string) throws AutomatonException {
    throw new AutomatonException(string);
  }
}
