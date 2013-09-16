package org.kwansystems.emulator.MiniLogo;

import org.kwansystems.automaton.part1.regexp.Letter;
import java.io.*;
import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.*;
import org.kwansystems.automaton.part1.regexp.*;
import org.kwansystems.automaton.tape.*;

import static org.kwansystems.emulator.MiniLogo.TokenType.*;

public class Lex extends Klex<TokenType> {
  public String DataPrefix() {
    return "Data/MiniLogo/";
  }
  public Collection<TokenDefinition<TokenType>> getTokenDefs() {
    Collection<TokenDefinition<TokenType>> Tokens=new LinkedList<TokenDefinition<TokenType>>();
    try {
      Tokens.add(new TokenDefinition<TokenType>("[0-9]+",NUM));
      Tokens.add(new TokenDefinition<TokenType>("[A-Za-z][0-9A-Za-z]*",ID));
      Tokens.add(new TokenDefinition<TokenType>("\\[",LBRACKET));
      Tokens.add(new TokenDefinition<TokenType>("\\]",RBRACKET));
      Tokens.add(new TokenDefinition<TokenType>(new Letter('+'),PLUS));
      Tokens.add(new TokenDefinition<TokenType>(new Letter('-'),MINUS));
      Tokens.add(new TokenDefinition<TokenType>(new Letter('*'),TIMES));
      Tokens.add(new TokenDefinition<TokenType>(new Letter('/'),DIVIDE));
      Tokens.add(new TokenDefinition<TokenType>(new Letter('('),LPAREN));
      Tokens.add(new TokenDefinition<TokenType>(new Letter(')'),RPAREN));
      Tokens.add(new TokenDefinition<TokenType>(new Letter('='),ASSIGN));
      Tokens.add(new TokenDefinition<TokenType>("[\0\t\n\r\f ]+",NOTHING));
    } catch (AutomatonException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return Tokens;
  }
  public Lex() {
    super(EOF);
  }
  public Lex(Tape<Character> T) {
    this();
    setTape(T);
  }
  public static void main(String[] args) throws AutomatonException,IOException {
    Lex K=new Lex(new StringTape("To Square repeat 4 [draw 10 right 90]"));
//    K.addDisplay(new TextDFAListener(System.out));
    Token T; 
    do{
      T=K.read();
      K.Right();
      System.out.println(T+"\n");
    } while(T.type!=EOF); 
  }
}

