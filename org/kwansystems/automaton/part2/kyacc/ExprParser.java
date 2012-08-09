package org.kwansystems.automaton.part2.kyacc;

import org.kwansystems.automaton.part1.regexp.Plus;
import org.kwansystems.automaton.part1.regexp.ChoiceString;
import org.kwansystems.automaton.part1.regexp.Letter;
import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.*;
import org.kwansystems.automaton.part2.kyacc.ParserGenerator.Assoc;
import org.kwansystems.automaton.regexp.*;
import org.kwansystems.automaton.tape.*;

import static org.kwansystems.automaton.part2.kyacc.ExprParser.ExprSymbol.*;

public class ExprParser {
  public enum ExprSymbol implements KlexAction<ExprSymbol> {
    ID {
      public void act(Token T, Object context) {
    	  T.value=Integer.valueOf(T.value.toString());
      }
    },
    PL {public String toString() {return "+";}},
    MI {public String toString() {return "-";}},
    TI {public String toString() {return "*";}},
    DI {public String toString() {return "/";}},
    LP {public String toString() {return "(";}},
    RP {public String toString() {return ")";}},
    EOF{public String toString() {return "$";}},
    NOTHING(true),
    E,
    T,
    F;
    private boolean isIgnore;
    private ExprSymbol(boolean Lignore) {isIgnore=Lignore;}
    private ExprSymbol() {this(false);}
	  public void act(Token T, Object context) {}
	  public boolean ignore() {
	    return isIgnore;
	  }
	  public boolean sendUp() {
      return true;
	  }
  };
  
  public static void main(String[] args) throws AutomatonException {
    Grammar G1=new Grammar(E,EOF,new Production[] {
      new Production(E,new Object[] {E,PL,T}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value+(Integer)rightSide[2].value);
        }
      },
      new Production(E,new Object[] {E,MI,T}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value-(Integer)rightSide[2].value);
        }
      },
      new Production(E,new Object[] {T}),
      new Production(T,new Object[] {T,TI,F}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value*(Integer)rightSide[2].value);
        }
      },
      new Production(T,new Object[] {T,DI,F}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value/(Integer)rightSide[2].value);
        }
      },
      new Production(T,new Object[] {F}),
      new Production(F,new Object[] {LP,E,RP}) {
      	public Token act(Token[] rightSide) {
      	  return rightSide[1];
      	}
      },
      new Production(F,new Object[] {ID}) {
      	public Token act(Token[] rightSide) {
      	  return rightSide[0];
      	}
      }
    });
    Grammar G2=new Grammar(E,EOF,new Production[] {
      new Production(E,new Object[] {E,PL,E}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value+(Integer)rightSide[2].value);
        }
      },
      new Production(E,new Object[] {E,MI,E}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value-(Integer)rightSide[2].value);
        }
      },
      new Production(E,new Object[] {E,TI,E}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value*(Integer)rightSide[2].value);
        }
      },
      new Production(E,new Object[] {E,DI,E}) {
        public Token act(Token[] rightSide) {
          return new Token(null,(Integer)rightSide[0].value/(Integer)rightSide[2].value);
        }
      },
      new Production(E,new Object[] {LP,E,RP}) {
      	public Token act(Token[] rightSide) {
      	  return rightSide[1];
      	}
      },
      new Production(E,new Object[] {ID}) {
      	public Token act(Token[] rightSide) {
      	  return rightSide[0];
      	}
      }
    });
    Klex<ExprSymbol> ExprKlex=new Klex<ExprSymbol>(EOF) {
      public String DataPrefix() {
        return "Data/Grammar/ExprKlex";
      }
      public Collection<TokenDefinition<ExprSymbol>> getTokenDefs() {
        Collection<TokenDefinition<ExprSymbol>> Tokens=new LinkedList<TokenDefinition<ExprSymbol>>();
      	String digits="1234567890";
        String whitespace="\0\t\n\r\f ";
      	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('+'),PL));
       	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('-'),MI));
      	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('*'),TI));
      	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('/'),DI));
      	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('('),LP));
      	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter(')'),RP));
        Tokens.add(new TokenDefinition<ExprSymbol>(new Plus(new ChoiceString(digits)),ID));
        Tokens.add(new TokenDefinition<ExprSymbol>(new Plus(new ChoiceString(whitespace)),NOTHING));
        return Tokens;
      };
    };
    ExprKlex.setTape(new StringTape("1+(2*3+4)*5"));
    LRParser SLR=new LRParser(G1,new ParsonsSLR().Generate(G1,"Data/Grammar/ExprParser"));
    SLR.verbose=false;
    System.out.println(SLR.toString());
//    System.out.println(SLR.parse(ExprKlex).value);

    ExprKlex.setTape(new StringTape("1+(2*3)"));
    SLR=new LRParser(G2,new ParsonsSLR().Generate(G2,new Object[][]
      {{PL,MI},{TI,DI}},new Assoc[] {Assoc.LEFT,Assoc.LEFT}
    ,"Data/Grammar/ExprParser"));
    SLR.verbose=false;
    System.out.println(SLR.toString());
 //  System.out.println(SLR.parse(ExprKlex).value);
  }
  
}
