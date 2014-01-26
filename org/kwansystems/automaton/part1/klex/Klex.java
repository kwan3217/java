package org.kwansystems.automaton.part1.klex;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.*;
import org.kwansystems.automaton.part2.kyacc.*;
import org.kwansystems.automaton.part1.regexp.*;
import org.kwansystems.automaton.tape.*;

import static org.kwansystems.automaton.part1.DFA.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Kwan Lexical Analyzer. DFA driver and setup code to scan a tape for tokens,
 * and return their types and lexemes.
 * <p>
 * Klex is an abstract type. Extensions to this type are equivalent to .l files
 * for the (f)Lex program as conventionally used. However, the way Klex is
 * implemented, there is a better separation of the driver, the tables, and the
 * lexical actions.
 * <p>
 * Klex works as follows. It checks to see if the tables it needs have already 
 * been generated, by looking for the .serial.gz file the tables are stored in.
 * If they are present, the routine just loads them. If not, then it
 * runs {@link getTokenDefs()} to construct the {@link TokenDefinition}s that 
 * this lexer should work with. It runs the forked Thompson construction on the
 * definitions to get an NFA, runs {@link NFA#Kleene()} to turn the NFA into a
 * DFA, then runs {@link DFA.optimize()} to optimize the DFA. Once the DFA is
 * constructed, it is saved out to the .serial.gz file for next time.
 */
public abstract class Klex<TokenType> implements MooreListener,SymbolListener<Character>,Tape<Token> {
  public DFA<Character,Integer> dfa;
  public String read,lexeme;
  public KlexAction<TokenType> type;
  public Object context;
  private Token currentToken=null;
  private boolean isCrashed=false;
  /** Get Token Definitions.
   * @return
   */
  public abstract Collection<TokenDefinition<TokenType>> getTokenDefs();
  public abstract String DataPrefix();
  Token EOFSymbol=null;
  public Klex(Object LEOFSymbol) {
    Load();
    dfa.addMooreListener(this);
    dfa.addSymbolListener(this);
    EOFSymbol=new Token(LEOFSymbol);
  }
  private File SerialFilename() throws IOException {
    return new File(DataPrefix()+"Klex.serial.gz");
  }
  private void LoadSerial() throws IOException, ClassNotFoundException {
    ObjectInputStream inf = new ObjectInputStream(new GZIPInputStream(new FileInputStream(SerialFilename())));
    dfa=(DFA<Character,Integer>)inf.readObject();
    inf.close(); 
  }
  private void SaveSerial() throws IOException {
    ObjectOutputStream ouf = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(SerialFilename())));
    ouf.writeObject(dfa);
    ouf.close();
  }
  protected void Load() {
//    try {
//      if(!SerialFilename().canRead()) {
    	
        Generate();
//        SaveSerial();
//      } else {
//        LoadSerial();
//      }
//    } catch (IOException E) {
//      throw new RuntimeException(E);
//    } catch (ClassNotFoundException E) {
//      throw new RuntimeException(E);
//    }
  }
  private void Generate() {
    Fork<TokenType> RT=new Fork<TokenType>(getTokenDefs());
    NFA<Character,String> a=RT.getNFA("Klex");
//    printDebug(DataPrefix()+"KlexNFAS.dot",a.DotNFATransitionTable("Klex"));
//    printDebug(DataPrefix()+"KlexNFAS.txt",a.WikiNFATransitionTable());

    NFA<Character,Integer> b=a.numberStates();
    a=null;
//    printDebug(DataPrefix()+"KlexNFA.dot",b.DotNFATransitionTable("Klex"));
//    printDebug(DataPrefix()+"KlexNFA.txt",b.WikiNFATransitionTable());
    
    DFA<Character,Set<Integer>> dfaS=b.Kleene();
//    printDebug(DataPrefix()+"KlexNonoptDFAN.dot",dfaS.DotTransitionTable("Klex"));
//    printDebug(DataPrefix()+"KlexNonoptDFAN.txt",dfaS.WikiTransitionTable());

    dfa=dfaS.numberStates();
//    printDebug(DataPrefix()+"KlexNonoptDFA.dot",dfa.DotTransitionTable("Klex"));
//    printDebug(DataPrefix()+"KlexNonoptDFA.txt",dfa.WikiTransitionTable());

    dfa.optimize(-1);
//    printDebug(DataPrefix()+"KlexDFA.dot",dfa.DotTransitionTable("Klex"));
//    printDebug(DataPrefix()+"KlexDFA.txt",dfa.WikiTransitionTable());
  }
  public void OutputMoore(Object MooreOutput) {
    if(MooreOutput!=null) {
      type=(KlexAction<TokenType>)MooreOutput;
      lexeme+=read;
      read="";
    }
  }
  public void ShowCurrentSymbol(Character c) {
    read+=c;
  }
  public void setTape(Tape<Character> t) {
    currentToken=null;
    isCrashed=false;
    dfa.setTape(t);
  }
  public Tape<Character> getTape() {
    return dfa.getTape();
  }
  public void addDisplay(DFAListener<Character,Integer> d) {
    dfa.addAutomatonListener(d);
  }
  public Termination Left() {
    isCrashed=true;
    return Termination.Crash;
  }
  /**
   * Get the next token from the token string. If the token found is ignorable, ignore it and look for another token,
   * and continue until a significant token is found. This can be used for instance to ignore white space
   * and comments.
   * @return A token with a non-null type and with the lexeme set properly
   * @throws AutomatonException if no valid token can be formed from the prefix of the tape at this point
   */
  private Termination getToken() {
    do {
      type=null;
      dfa.Reset();
      lexeme="";
      read="";
      //Drive the machine
      dfa.RunToCrash();
      if(type==null) {
        currentToken=EOFSymbol;
        isCrashed=true;
        return Termination.Crash;
      }
    } while (type.ignore());
    //Push back as needed. We read one symbol more than needed and crashed on the it,
    //but didn't add it to read. Machine crashes before ShowCurrentSymbol runs. So, don't push it back
    for(int i=0;i<read.length()-1;i++) dfa.getTape().Left();
    currentToken=new Token(type,lexeme);
    ((KlexAction<TokenType>)currentToken.type).act(currentToken,context);
    return Termination.Continue;
  }
  public Termination Right() {
    if(isCrashed) return Termination.Crash;
    return getToken();
  }
  public Token getBlankSymbol() {
    return EOFSymbol;
  }
  public TapeDisplay<Token> getTapeDisplay() {
    throw new UnsupportedOperationException();
  }
  public Token read() {
    if(isCrashed) return EOFSymbol;
    if(currentToken==null && getToken()==Termination.Crash) {
      isCrashed=true;
      return EOFSymbol;
    }
    return currentToken;
  }
  public void setBlankSymbol(Token b) {
    EOFSymbol=b;
  }
  public Termination write(Token newSymbol) {
    throw new UnsupportedOperationException("Read only tape");
  }
  public String toString() {
    return dfa.WikiTransitionTable();
  }
}
