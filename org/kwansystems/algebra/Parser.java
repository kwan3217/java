package org.kwansystems.algebra;

import org.kwansystems.automaton.part1.regexp.Plus;
import org.kwansystems.automaton.part1.regexp.ChoiceString;
import org.kwansystems.automaton.part1.regexp.Letter;
import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.klex.*;
import org.kwansystems.automaton.part2.kyacc.*;
import org.kwansystems.automaton.part2.kyacc.ParserGenerator.Assoc;
import org.kwansystems.automaton.regexp.*;
import org.kwansystems.automaton.tape.*;

import static org.kwansystems.algebra.Parser.ExprSymbol.*;

public class Parser {
  public enum ExprSymbol implements KlexAction<ExprSymbol> {
    NUM,
    ID,
    PL {public String toString() {return "+";}},
    MI {public String toString() {return "-";}},
    TI {public String toString() {return "*";}},
    DI {public String toString() {return "/";}},
    PW {public String toString() {return "^";}},
    LP {public String toString() {return "(";}},
    RP {public String toString() {return ")";}},
    EOF{public String toString() {return "$";}},
    NOTHING(true),
    E;
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

  private static final Grammar ExprGrammar=new Grammar(E,EOF,new Production[] {
    new Production(E,new Object[] {E,PL,E}) {
      public Token act(Token[] rightSide) {
        return new Token(null,new Sum((DerivativeNode)(rightSide[0].value),(DerivativeNode)(rightSide[2].value)));
      }
    },
    new Production(E,new Object[] {E,MI,E}) {
      public Token act(Token[] rightSide) {
        return new Token(null,new Sum((DerivativeNode)(rightSide[0].value),new Product(new Constant(-1),(DerivativeNode)(rightSide[2].value))));
      }
    },
    new Production(E,new Object[] {E,TI,E}) {
      public Token act(Token[] rightSide) {
        return new Token(null,new Product((DerivativeNode)(rightSide[0].value),(DerivativeNode)(rightSide[2].value)));
      }
    },
    new Production(E,new Object[] {E,DI,E}) {
      public Token act(Token[] rightSide) {
        return new Token(null,new Fraction((DerivativeNode)(rightSide[0].value),(DerivativeNode)(rightSide[2].value)));
      }
    },
    new Production(E,new Object[] {E,PW,E}) {
      public Token act(Token[] rightSide) {
        return new Token(null,new Power((DerivativeNode)(rightSide[0].value),(DerivativeNode)(rightSide[2].value)));
      }
    },
    new Production(E,new Object[] {LP,E,RP}) {
    	public Token act(Token[] rightSide) {
    	  return rightSide[1];
    	}
    },
    new Production(E,new Object[] {MI,E}) {
    	public Token act(Token[] rightSide) {
        return new Token(null,new Product(new Constant(-1),(DerivativeNode)(rightSide[1].value)));
    	}
    },
    new Production(E,new Object[] {NUM}) {
    	public Token act(Token[] rightSide) {
    	  return new Token(null,new Constant(Double.parseDouble(rightSide[0].value.toString())));
    	}
    },
    new Production(E,new Object[] {ID}) {
    	public Token act(Token[] rightSide) {
    	  return new Token(null,new Variable(rightSide[0].value.toString()));
    	}
    },
    new Production(E,new Object[] {ID,LP,E,RP}) {
    	public Token act(Token[] rightSide) {
        String fnName=(String)rightSide[0].value;
        if(fnName.equalsIgnoreCase("sin")) return new Token(null,new Sine((DerivativeNode)(rightSide[2].value)));
        if(fnName.equalsIgnoreCase("cos")) return new Token(null,new Cosine((DerivativeNode)(rightSide[2].value)));
        if(fnName.equalsIgnoreCase("exp")) return new Token(null,new NaturalExp((DerivativeNode)(rightSide[2].value)));
        if(fnName.equalsIgnoreCase("ln")) return new Token(null,new NaturalLog((DerivativeNode)(rightSide[2].value)));
    	  throw new IllegalArgumentException("Unrecognized function name");
    	}
    }
  });
  private static final Klex<ExprSymbol> ExprKlex=new Klex<ExprSymbol>(EOF) {
    public String DataPrefix() {
      return "Data/Grammar/ExprKlex";
    }
    public Collection<TokenDefinition<ExprSymbol>> getTokenDefs() {
      Collection<TokenDefinition<ExprSymbol>> Tokens=new LinkedList<TokenDefinition<ExprSymbol>>();
      String whitespace="\0\t\n\r\f ";
    	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('+'),PL));
     	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('-'),MI));
    	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('*'),TI));
    	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('/'),DI));
    	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('^'),PW));
    	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter('('),LP));
    	Tokens.add(new TokenDefinition<ExprSymbol>(new Letter(')'),RP));
      Tokens.add(new TokenDefinition<ExprSymbol>("[0-9][1-9]*(.[0-9]+)?",NUM));
      Tokens.add(new TokenDefinition<ExprSymbol>("[A-Za-z_][A-Za-z0-9_]*",ID));
      Tokens.add(new TokenDefinition<ExprSymbol>(new Plus(new ChoiceString(whitespace)),NOTHING));
      return Tokens;
    };
  };
  private static final LRParser parser=new LRParser(ExprGrammar,new ParsonsSLR().Generate(ExprGrammar,new Object[][]
      {{PL,MI},{TI,DI},{PW}},new Assoc[] {Assoc.LEFT,Assoc.LEFT,Assoc.RIGHT}
    ,"Data/Grammar/ExprParser"));
  public static DerivativeNode parse(String S) {
    ExprKlex.setTape(new StringTape(S));
    return ((DerivativeNode)(parser.parse(ExprKlex)).value).simplify();
  }
  public static void main(String[] args) {
    System.out.println(parser);
    System.out.println(parse("a*x^2+b*x").derivative("x"));
    System.out.println(parse("cos(a*x^2+b*x+c)*exp(x^2)").derivative("x"));
  }
}
