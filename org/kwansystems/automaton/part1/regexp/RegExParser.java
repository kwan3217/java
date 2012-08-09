package org.kwansystems.automaton.part1.regexp;

import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.*;
import org.kwansystems.automaton.part2.kyacc.*;
import org.kwansystems.automaton.tape.*;

import static org.kwansystems.automaton.part1.regexp.RegExParser.RegExSymbol.*;

/**
 * Regular Expression Compiler. Given a regular expression, generate a {@link 
 * org.kwansystems.automaton.regexp.RegExpTree} for it, so it can be used by
 * {@link org.kwansystems.automaton.part1.klex.MultiKlex}.
 * <p>
 * This at present uses a somewhat limited, yet complete, form of regular 
 * expressions. It doesn't even aim at PCRE, as full PCRE is actually a Part II
 * language.
 * <p>
 */
public class RegExParser extends LRParser {
  Klex<RegExSymbol> RegExKlex;
  private static final String RangeA="\0\t\n\r\f";
  private static final String RangeB=ChoiceString.Range(' ','\'')+","+ChoiceString.Range('.', '>');
  private static final String RangeC=ChoiceString.Range('@','Z')+"^_";
  private static final String RangeD=ChoiceString.Range('`','{')+"}~";
  //Characters in normal and escapable are a disjoint set
        //and the union of them are all legal characters
  private static final String normal=RangeA+RangeB+RangeC+RangeD;
  private static final String escapable=       "\\()[]*+-|?0tnrf";
  private static final String escapableReplace="\\()[]*+-|?\0\t\n\r\f";
  public static RegExParser parser=new RegExParser();

  public static RegExpTree compile(String S) throws AutomatonException {
    return parser.compileRegEx(S);
  }
  public enum RegExSymbol implements KlexAction<RegExSymbol> {
    LETTER {
      public void act(Token T, Object context) {
        char c0=T.value.toString().charAt(0);
    	  if(c0=='\\') {
          char c1=T.value.toString().charAt(1);
          int i=escapable.indexOf(c1);
          if(i>=0) {
            T.value=escapableReplace.charAt(i);
          } else {
    	      T.value=c1;
          }
    	  } else {
      	  T.value=c0;
    	  }
      }
    },
    PIPE     {public String toString() {return "|";}},
    STAR     {public String toString() {return "*";}},
    QUESTION {public String toString() {return "?";}},
    PLUS     {public String toString() {return "+";}},
    LPAREN   {public String toString() {return "(";}},
    RPAREN   {public String toString() {return ")";}},
    HYPHEN   {public String toString() {return "-";}},
    LBRACKET {public String toString() {return "[";}},
    RBRACKET {public String toString() {return "]";}},
    EOF,
    NOTHING(true),
    REGEXP,CONCATTERM,TERM,PRIMARY,BRACKETGUTS,BRACKETRANGE;
    private boolean isIgnore;
    private RegExSymbol(boolean Lignore) {isIgnore=Lignore;}
    private RegExSymbol() {this(false);}
   	public void act(Token T, Object context) {}
  	public boolean ignore() {return isIgnore;}
  	public boolean sendUp() {return true;}
  };
  public RegExParser() {
    super(new Grammar(REGEXP,EOF,new Production[] {
      new Production(REGEXP,new Object[] {CONCATTERM}),
      new Production(REGEXP,new Object[] {REGEXP,PIPE,CONCATTERM}) {
        public Token act(Token[] rightSide) {
          return new Token(null,new Choice((RegExpTree)rightSide[0].value,(RegExpTree)rightSide[2].value));
        }
      },
      new Production(CONCATTERM,new Object[] {TERM}),
      new Production(CONCATTERM,new Object[] {CONCATTERM,TERM}) {
        public Token act(Token[] rightSide) {
          return new Token(null,new Concat((RegExpTree)rightSide[0].value,(RegExpTree)rightSide[1].value));
        }
      },
      new Production(TERM,new Object[] {TERM,STAR})  {
        public Token act(Token[] rightSide) {
          return new Token(null,new Star((RegExpTree)rightSide[0].value));
        }
      },
      new Production(TERM,new Object[] {TERM,PLUS})  {
        public Token act(Token[] rightSide) {
          return new Token(null,new Plus((RegExpTree)rightSide[0].value));
        }
      },
      new Production(TERM,new Object[] {PRIMARY}),
      new Production(TERM,new Object[] {TERM,QUESTION})  {
        public Token act(Token[] rightSide) {
          return new Token(null,new Optional((RegExpTree)rightSide[0].value));
        }
      },
      new Production(PRIMARY,new Object[] {LETTER})  {
        public Token act(Token[] rightSide) {
          return new Token(null,new Letter((Character)(((Token)rightSide[0]).value)));
        }
      },
      new Production(PRIMARY,new Object[] {LBRACKET,BRACKETGUTS,RBRACKET}){
        public Token act(Token[] rightSide) {
          return new Token(null,new ChoiceString((String)rightSide[1].value));
        }
      },
      new Production(BRACKETGUTS,new Object[] {BRACKETRANGE,BRACKETGUTS}){
        public Token act(Token[] rightSide) {
          return new Token(null,(String)rightSide[0].value+(String)rightSide[1].value);
        }
      },
      new Production(BRACKETGUTS,new Object[] {BRACKETRANGE}),
      new Production(BRACKETRANGE,new Object[] {LETTER,HYPHEN,LETTER}){
        public Token act(Token[] rightSide) {
          char Letter0=(Character)(((Token)rightSide[0]).value);
          char Letter2=(Character)(((Token)rightSide[2]).value);
          return new Token(null,ChoiceString.Range(Letter0,Letter2));
        }
      },
      new Production(BRACKETRANGE,new Object[] {LETTER}){
        public Token act(Token[] rightSide) {
          return new Token(null,((Token)rightSide[0]).value.toString());
        }
      },
      new Production(PRIMARY,new Object[] {LPAREN,REGEXP,RPAREN}) {
        public Token act(Token[] rightSide) {
          return rightSide[1];
        }
      }
    }),"Data/Grammar/RegEx");
    RegExKlex=new Klex<RegExSymbol>(EOF) {
      public String DataPrefix() {
        return "Data/Grammar/RegEx";
      }
      public Collection<TokenDefinition<RegExSymbol>> getTokenDefs() {
        Collection<TokenDefinition<RegExSymbol>> Tokens=new LinkedList<TokenDefinition<RegExSymbol>>();
        //Can't use RegExParser on these, since that leads to a circular bootstrap. Good thing they are all so simple...
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('('),LPAREN));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter(')'),RPAREN));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('['),LBRACKET));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter(']'),RBRACKET));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('*'),STAR));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('+'),PLUS));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('?'),QUESTION));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('-'),HYPHEN));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Letter('|'),PIPE));
        Tokens.add(new TokenDefinition<RegExSymbol>(new ChoiceString(normal),LETTER));
        Tokens.add(new TokenDefinition<RegExSymbol>(new Concat(new Letter('\\'),new ChoiceString(normal+escapable)),LETTER));
        return Tokens;
      };
    };
  }
  public RegExpTree compileRegEx(String RegEx) throws AutomatonException {
    RegExKlex.setTape(new StringTape(RegEx));
    return (RegExpTree)parse(RegExKlex).value;
  }
  public static void main(String[] args) throws AutomatonException {
    System.out.println(RegExParser.parser);
    System.out.println(RegExParser.compile("a|b"));
    System.out.println(RegExParser.compile("[A-Zjqz0-9]+"));
    RegExParser.parser.verbose=true;
  }
}
