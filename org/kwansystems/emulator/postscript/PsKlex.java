package org.kwansystems.emulator.postscript;

import org.kwansystems.automaton.part1.regexp.Letter;
import java.io.*;
import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.MultiKlex;
import org.kwansystems.automaton.part1.klex.Klex;
import org.kwansystems.automaton.part1.regexp.*;
import org.kwansystems.automaton.tape.ReaderTape;
import org.kwansystems.automaton.tape.Tape;

import static org.kwansystems.emulator.postscript.PsKlexAction.*;

/**
 * Postscript Scanner, based on Klex.
 */
public class PsKlex extends MultiKlex<PsKlexAction> {
  public static final char eofchar='\4';
  
/** lexical states  */
  public Klex<PsKlexAction> normal=new Klex<PsKlexAction>() {
    public String DataPrefix() {
      return "Data/PsInterp/Normal";
    }
    public Collection<TokenDefinition<PsKlexAction>> getTokenDefs() {
      Collection<TokenDefinition<PsKlexAction>> Tokens=new LinkedList<TokenDefinition<PsKlexAction>>();
      String puncEsc="!#@^&.\\*\\+\\-=\\|\\\\:\";'>\\?,'_$~";
      String selfDelimitNameEsc="\\)>\\[\\]}";
      String selfDelimitSpecialEsc="\\(</{";
      String whitespaceEsc="\0\t\n\r\f ";
      String commentEsc="a-zA-Z0-9"+puncEsc+"% \t\f\0"+selfDelimitNameEsc+selfDelimitSpecialEsc;
      Tokens.add(new TokenDefinition<PsKlexAction>("[\\+\\-]?([0-9]+.[0-9]*)|([0-9]*.[0-9]+)([Ee][\\+\\-][0-9]*)?",REAL));
      Tokens.add(new TokenDefinition<PsKlexAction>("[\\+\\-]?[0-9]+(#[0-9A-Za-z]+)?",INTEGER));
      Tokens.add(new TokenDefinition<PsKlexAction>("[0-9A-Za-z"+puncEsc+"]+",NAME));
      Tokens.add(new TokenDefinition<PsKlexAction>("[\\)>\\[\\]}]",NAME)); //Self-delimiting names
      Tokens.add(new TokenDefinition<PsKlexAction>("/["+puncEsc+"0-9A-Za-z]*",LITERAL));
      Tokens.add(new TokenDefinition<PsKlexAction>("//["+puncEsc+"0-9A-Za-z]*",IMMEDIATE));
      Tokens.add(new TokenDefinition<PsKlexAction>("{",LCURLY));
      Tokens.add(new TokenDefinition<PsKlexAction>("\\(",STRINGSTART));
      Tokens.add(new TokenDefinition<PsKlexAction>("<",HEXSTART));
      Tokens.add(new TokenDefinition<PsKlexAction>("<~",BASE85START));
      Tokens.add(new TokenDefinition<PsKlexAction>("<<",NAME));
      Tokens.add(new TokenDefinition<PsKlexAction>(">>",NAME));
      Tokens.add(new TokenDefinition<PsKlexAction>("}",RCURLY));
      Tokens.add(new TokenDefinition<PsKlexAction>("["+whitespaceEsc+"]+",NOTHING));
      Tokens.add(new TokenDefinition<PsKlexAction>("%["+commentEsc+"]*([\n\r]|\n|\r)?",NOTHING));
      Tokens.add(new TokenDefinition<PsKlexAction>(new Letter('\u0004'),EOF));
      return Tokens;
    }
  };
  public Klex<PsKlexAction> ParenString=new Klex<PsKlexAction>() {
    public String DataPrefix() {
      return "Data/PsInterp/String";
    }
    public Collection<TokenDefinition<PsKlexAction>> getTokenDefs() {
      Collection<TokenDefinition<PsKlexAction>> Tokens=new LinkedList<TokenDefinition<PsKlexAction>>();
      String lowerCaseEsc="a-z";
      String puncEsc="\n\r!\"#$%^'\\*\\+,\\-./:;<\\?@\\[\\]^_`{\\|}~"; //Not ()\, handled separately 
      String upperCaseEsc="A-Z";
      String digitsEsc="0-9"+lowerCaseEsc+upperCaseEsc+puncEsc;
      String whitespaceEsc="\0\t\f ";
      Tokens.add(new TokenDefinition<PsKlexAction>("["+lowerCaseEsc+upperCaseEsc+digitsEsc+puncEsc+whitespaceEsc+"]*",STRINGDATA));
      Tokens.add(new TokenDefinition<PsKlexAction>("\\(",LPAREN));
      Tokens.add(new TokenDefinition<PsKlexAction>("\\)",RPAREN));
      Tokens.add(new TokenDefinition<PsKlexAction>("\\\\(\n|\r|\n\r)",NOTHING)); //Ignored Line Break
      Tokens.add(new TokenDefinition<PsKlexAction>("\\\\",NOTHING)); //Bare slash
      Tokens.add(new TokenDefinition<PsKlexAction>("\\\\[nrtbf\\(\\)\\\\]",ESCAPE));
      Tokens.add(new TokenDefinition<PsKlexAction>("\\\\([0-3][0-7][0-7])|([0-7][0-7])|([0-7])",ESCAPEOCT));
      return Tokens;
    }
  };
;
  public Klex<PsKlexAction> Base85String=new Klex<PsKlexAction>() {
    public String DataPrefix() {
      return "Data/PsInterp/Base85";
    }
    public Collection<TokenDefinition<PsKlexAction>> getTokenDefs() {
      Collection<TokenDefinition<PsKlexAction>> Tokens=new LinkedList<TokenDefinition<PsKlexAction>>();
      String lowerCaseEsc="a-uz"; //specifically not vwxy for Base85
      String puncEsc="!\"#$%^'\\(\\)\\*\\+,\\-./:;<\\?@\\[\\\\\\]^_`"; //Specifically not {|}~ or space for Base85
      String upperCaseEsc="A-Z";
      String digitsEsc="0-9"+lowerCaseEsc+upperCaseEsc+puncEsc;
      String whitespaceEsc="\0\t\n\r\f ";
      try {
        Tokens.add(new TokenDefinition<PsKlexAction>("["+digitsEsc+"]*",BASE85DATA));
        Tokens.add(new TokenDefinition<PsKlexAction>("~>",BASE85END));
        Tokens.add(new TokenDefinition<PsKlexAction>("["+whitespaceEsc+"]+",NOTHING));
      } catch (AutomatonException e) {throw new RuntimeException(e);}
      return Tokens;
    }
  };
  
  public Klex<PsKlexAction> HexString=new Klex<PsKlexAction>() {
    public String DataPrefix() {
      return "Data/PsInterp/Hex";
    }
    public Collection<TokenDefinition<PsKlexAction>> getTokenDefs() {
      Collection<TokenDefinition<PsKlexAction>> Tokens=new LinkedList<TokenDefinition<PsKlexAction>>();
      try {
        Tokens.add(new TokenDefinition<PsKlexAction>("[0-9A-Fa-f]*",HEXDATA));
        Tokens.add(new TokenDefinition<PsKlexAction>(">",HEXEND));
        Tokens.add(new TokenDefinition<PsKlexAction>("[\0\t\n\r\f ]+",NOTHING));
      } catch (AutomatonException e) {throw new RuntimeException(e);}
      return Tokens;
    }
  };
  
  private StringBuffer StringLiteral;
  private int ParenMatch;
  public void addStringLiteral(String newData) {
    StringLiteral.append(newData);
  }
  public void incParen() {
    ParenMatch++;
  }
    
  /**
   * @return true if still in string, false if this paren is the end of the string
   */
  public boolean decParen() {
    ParenMatch--;
    return ParenMatch>=0;
  }
  public void resetStringLiteral() {
    ParenMatch=0;
    StringLiteral=new StringBuffer();
  }
  public String getStringLiteral() {
    return StringLiteral.toString();
  }
  
  public PsKlex() {
    states=new ArrayList<Klex<PsKlexAction>>();
    states.add(normal);
    states.add(ParenString);
    states.add(Base85String);
    states.add(HexString);
    currentState=0;
    context=this;
  }
  public PsKlex(Tape<Character> T) {
    this();
    setTape(T);
  }
  public PsKlex(Reader inf) throws IOException {
    this(new ReaderTape(inf));
  }
  
  public static void main(String[] args) throws AutomatonException,IOException {
    PsKlex K=new PsKlex(new FileReader("Data/PsInterp/test1.tape"));
    Token T; 
    do{
      T=K.read();
      K.Right();
      System.out.println(T+"\n");
    } while(T.type!=EOF); 
  }
}

