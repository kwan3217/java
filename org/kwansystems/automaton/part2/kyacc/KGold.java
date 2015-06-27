package org.kwansystems.automaton.part2.kyacc;

import org.kwansystems.automaton.part1.regexp.RegExParser;
import org.kwansystems.automaton.part1.regexp.RegExpTree;
import org.kwansystems.automaton.part1.regexp.Choice;
import org.kwansystems.automaton.part1.regexp.Letter;
import java.io.*;
import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.DFA;
import org.kwansystems.automaton.part1.klex.*;
import org.kwansystems.automaton.part2.kyacc.KGold.*;
import org.kwansystems.automaton.part2.kyacc.Grammar.*;
import org.kwansystems.automaton.part2.kyacc.ParserGenerator.Assoc;
import org.kwansystems.automaton.part1.regexp.*;
import org.kwansystems.automaton.tape.*;

import static org.kwansystems.automaton.part2.kyacc.KGold.TokenType.*;

public class KGold {
  enum TokenType implements KlexAction<TokenType> {
    ID,REGEX {
      public void act(Token T, Object context) {
        String S=(String)(T.value);
        S=S.substring(1,S.length()-1);
        T.value=S.trim();
      }
    },SEMI,PIPE,PNOTHING,PLEFT,PRIGHT,PNON,ARROW,NOTHING,EOF,
    FILE,STATEMENTS,STATEMENT,IDS,PRODS,PROD;
    public void act(Token T, Object context) {}
    public boolean sendUp() {return true;}
    public boolean ignore() {return this==NOTHING;}
  }
  private static Klex<TokenType> KGoldLex=new Klex<TokenType>(EOF) {
    public String DataPrefix() {
      return "Data/KGold/";
    }
    public Collection<TokenDefinition<TokenType>> getTokenDefs() {
      Collection<TokenDefinition<TokenType>> Tokens=new LinkedList<TokenDefinition<TokenType>>();
      try {
        Tokens.add(new TokenDefinition<TokenType>("[A-Za-z_][A-Za-z0-9_]*",ID));
        Tokens.add(new TokenDefinition<TokenType>(":([ -:<-\\[\\]-~]|(\\\\[ -~]))*;",REGEX));
        Tokens.add(new TokenDefinition<TokenType>(new Letter(';'),SEMI));
        Tokens.add(new TokenDefinition<TokenType>(new Letter('|'),PIPE));
        Tokens.add(new TokenDefinition<TokenType>("%NOTHING",PNOTHING));
        Tokens.add(new TokenDefinition<TokenType>("%LEFT",PLEFT));
        Tokens.add(new TokenDefinition<TokenType>("%RIGHT",PRIGHT));
        Tokens.add(new TokenDefinition<TokenType>("%NON",PNON));
        Tokens.add(new TokenDefinition<TokenType>("\\->",ARROW));
        Tokens.add(new TokenDefinition<TokenType>("[\0\t\n\r\f ]+",NOTHING));
        Tokens.add(new TokenDefinition<TokenType>("//[ -~]*(\n|\r|\r\n|\n\r)",NOTHING)); //Comment
      } catch (AutomatonException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
      return Tokens;
    }
  };
  private static LinkedHashMap<String,List<Production>> prods=new LinkedHashMap<String,List<Production>>();
  private static LinkedList<List> prec=new LinkedList<List>();
  private static LinkedList<Assoc> assoc=new LinkedList<Assoc>();
  private static LinkedHashMap<String,RegExpTree> terminal=new LinkedHashMap<String,RegExpTree>();
  private static LinkedHashSet<String> tokentype=new LinkedHashSet<String>();
  private static int indexOf(Set s, Object o) {
    int i=0;
    for(Object q:s) {
      if(q.equals(o)) return i;
      i++;
    }
    return -1;
  }
  private static Object get(Set s, int j) {
    int i=0;
    for(Object q:s) {
      if(i==j) return q;
      i++;
    }
    return null;
  }
  private static Grammar KGoldGrammar=new Grammar(FILE,EOF,new Production[] {
      new Production(FILE,      new Object[] {STATEMENTS}),
      new Production(STATEMENTS,new Object[] {STATEMENTS,STATEMENT}),
      new Production(STATEMENTS,new Object[] {STATEMENT}),
      new Production(STATEMENT, new Object[] {ID,REGEX}) {
        public Token act(Token[] rightSide) {
          String S=(String)(rightSide[0].value);
          String SS=(String)(rightSide[1].value);
          RegExpTree R=terminal.get(S);
          if(terminal.containsKey(S)) {
            if(R==null)throw new IllegalArgumentException("Tried to make a regex terminal, already an empty terminal or nonterminal of that name.");
          }
          tokentype.add(S);
          RegExpTree RR=RegExParser.compile(SS);
          if(R==null) {
            R=RR;
          } else {
            R=new Choice(R,RR);
          }
          terminal.put((String)(rightSide[0].value), RR);
          return null;
        }
      },
      new Production(STATEMENT, new Object[] {ID,SEMI}) {
        public Token act(Token[] rightSide) {
          String S=(String)(rightSide[0].value);
          if(terminal.containsKey(leftSide)) throw new IllegalArgumentException("Tried to make an empty terminal, already a nonterminal or regex terminal of that name.");
          tokentype.add(S);
          terminal.put(S, null);
          return null;
        }
      },
      new Production(STATEMENT, new Object[] {PNOTHING,REGEX}) {
        public Token act(Token[] rightSide) {
          String S="%NOTHING";
          String SS=(String)(rightSide[1].value);
          RegExpTree R=terminal.get(S);
          tokentype.add(S);
          RegExpTree RR=RegExParser.compile(SS);
          if(R==null) {
            R=RR;
          } else {
            R=new Choice(R,RR);
          }
          terminal.put((String)(rightSide[0].value), R);
          return null;
        }
      },
      new Production(STATEMENT, new Object[] {PLEFT,IDS,SEMI}) {
        public Token act(Token[] rightSide) {
          List L=(List)rightSide[1].value;
          //Precedence table is in the file with the
          //highest precedence line first. Since
          //we want the higher precedence lines at
          //higher numbers, and since the higher
          //numbered lines were already processed,
          //stick this line at the beginning of the
          //precedence table, to make it lower than any
          //precedence yet seen.
          prec.addFirst(L);
          assoc.addFirst(Assoc.LEFT);
          return null;
        }
      },
      new Production(STATEMENT, new Object[] {PRIGHT,IDS,SEMI}) {
        public Token act(Token[] rightSide) {
          List L=(List)rightSide[1].value;
          prec.addFirst(L);
          assoc.addFirst(Assoc.RIGHT);
          return null;
        }
      },
      new Production(STATEMENT, new Object[] {PNON,IDS,SEMI}) {
        public Token act(Token[] rightSide) {
          List L=(List)rightSide[1].value;
          prec.addFirst(L);
          assoc.addFirst(Assoc.NON);
          return null;
        }
      },
      new Production(STATEMENT, new Object[] {ID,ARROW,PRODS}) {
        public Token act(Token[] rightSide) {
          LinkedList<List> L=(LinkedList<List>)rightSide[2].value;
          String S=(String)(rightSide[0].value);
          List LL=prods.get(S);
          if(terminal.containsKey(S)) throw new IllegalArgumentException("Tried to define nonterminal with same name as existing regex terminal: "+S);
          tokentype.add(S);
          if(LL==null) {
            LL=new ArrayList();
            prods.put(S, LL);
          }
          for(List LLL:L) {
            Production P=new Production(S,LLL.toArray());
            LL.add(P);
          }
          return null;
        }
      },
      new Production(PRODS,     new Object[] {IDS,SEMI}) {
        public Token act(Token[] rightSide) {
          LinkedList L=new LinkedList();
          L.addFirst(rightSide[0].value);
          return new Token(null,L);
        }
      },
      new Production(PRODS,     new Object[] {IDS,PIPE,PRODS}){
        public Token act(Token[] rightSide) {
          LinkedList L=(LinkedList)(rightSide[2].value);
          L.addFirst(rightSide[0].value);
          return new Token(null,L);
        }
      },
      new Production(IDS,       new Object[] {ID}) {
        public Token act(Token[] rightSide) {
          LinkedList L=new LinkedList();
          L.addFirst(rightSide[0].value);
          return new Token(null,L);
        }
      },
      new Production(IDS,       new Object[] {ID,IDS}) {
        public Token act(Token[] rightSide) {
          LinkedList L=(LinkedList)(rightSide[1].value);
          L.addFirst(rightSide[0].value);
          return new Token(null,L);
        }
      }
  });
  private static final LRParser KGoldLR=new LRParser(KGoldGrammar,new ParsonsSLR().Generate(KGoldGrammar, "Data/Grammar/KGold"));
  public static String reEscape(String S) {
    StringBuffer SB=new StringBuffer(S);
    char[] from=new char[] {'\0','\t','\n','\r','\f'};
    char[] to=new char[] {'0','t','n','r','f'};
    for(int i=0;i<from.length;i++) {
      int pos=SB.indexOf(""+from[i]);
      while(pos>=0) {
        SB.setCharAt(pos, to[i]);
        SB.insert(pos, '\\');
        pos=SB.indexOf(""+from[i]);
      }
    }
    return SB.toString();
  }
  private static class UserTokenType implements KlexAction<UserTokenType>,Comparable<UserTokenType> {
    private int num;
    private boolean isNothing;
    private String name;
    public UserTokenType(int Lnum, String Lname, boolean LisNothing) {
      num=Lnum;
      name=Lname;
      isNothing=LisNothing;
    }
    public int intValue() {return num;}
    public String toString() {return name;}
    public boolean ignore() {return isNothing;}
    public boolean sendUp() {return true;}
    public void act(Token T, Object context) {}
    public int compareTo(UserTokenType o) {
      if(num<o.num) return -1;
      if(num==o.num) return 0;
      return 1;
    }
  }
  public static String toString(DFA<Character,Integer> dfa, LRParser UserLR) {
    StringWriter result=new StringWriter();
    PrintWriter P=new PrintWriter(result);

    int i=0;
    P.println("==Token ID assignment==");
    for(String SSS:tokentype) {
      if(terminal.containsKey(SSS)) {
        RegExpTree T=terminal.get(SSS);
        if(T==null) {
          P.printf(" (%3d) %s\n",i,SSS);
        } else {
          P.printf(" (%3d) %s: %s\n",i,SSS,reEscape(T.toString()));
        }
      } else {
        P.printf(" (%3d) %s\n",i,SSS);
      }
      i++;
    }
    P.println("<graphviz caption='User DFA' alt='User DFA' format='svg+png'>");
    P.println(dfa.DotTransitionTable("UserDFA"));
    P.println("</graphviz>");
    P.println(dfa.WikiTransitionTable());
    if(assoc.size()>0) {
      P.println("==Precedence Table==");
      P.println("{| class=\"wikitable\"");
      P.println("|-");
      P.println("!Precedence!!Associativity!!Tokens");
      for(int k=assoc.size()-1;k>=0;k--) {
        P.println("|-");
        P.printf("|%d||%s||",k,assoc.get(k).toString());
        for(Object p:prec.get(k)) {
          P.printf("%s,",p);
        }
        P.println();
      }
      P.println("|}");
    } else {
      P.println("==No Precedence Table==");
    }
    P.println("==Production ID assignment==");
    P.println(UserLR.toString());
    
    return result.toString();
  }
  public static void main(String args[]) throws IOException {
    StringBuffer SS=new StringBuffer();
    LineNumberReader inf=new LineNumberReader(new FileReader("Data/KGold/NewNMEA.txt"));
    String S=inf.readLine();
    while(S!=null) {
      SS.append(S);
      SS.append('\n');
      S=inf.readLine();
    }
    terminal.put("%EOF", null);
    tokentype.add("%EOF");
    KGoldLex.setTape(new StringTape(SS.toString()));
    KGoldLR.parse(KGoldLex);

    System.out.println("Number the user tokens");
    final UserTokenType[] userTokenTypes=new UserTokenType[tokentype.size()];
    Map<String,UserTokenType> invMap=new LinkedHashMap<String,UserTokenType>();
    int i=0;
    for(String SSS:tokentype) {
      userTokenTypes[i]=new UserTokenType(i,SSS,SSS.equals("%NOTHING"));
      invMap.put(SSS, userTokenTypes[i]);
      i++;
    }
    Object[][] UserPrec=new Object[prec.size()][];
    Assoc[] UserAssoc=new Assoc[prec.size()];
    for(i=0;i<prec.size();i++) {
      UserAssoc[i]=assoc.get(i);
      UserPrec[i]=new Object[prec.get(i).size()];
      for(int j=0;j<prec.get(i).size();j++) {
        UserPrec[i][j]=userTokenTypes[invMap.get(prec.get(i).get(j)).num];
      }
    }

    System.out.println("Build the DFA table");
    Klex<UserTokenType> UserLex=new Klex<UserTokenType>(userTokenTypes[0]) {
      public String DataPrefix() {
        return "Data/KGold/User";
      }
      public Collection<TokenDefinition<UserTokenType>> getTokenDefs() {
        Collection<TokenDefinition<UserTokenType>> Tokens=new LinkedList<TokenDefinition<UserTokenType>>();
        try {
          for(String S:tokentype) {
            RegExpTree R=terminal.get(S);
            if(R!=null) {
              int i=indexOf(terminal.keySet(),S);
              Tokens.add(new TokenDefinition<UserTokenType>(R,userTokenTypes[i]));
            }
          }
        } catch (AutomatonException e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
        return Tokens;
      }
    };
    DFA<Character,Integer> dfa=UserLex.dfa;

    System.out.println("Number the productions and tokens");
    List<Production> LuserProds=new ArrayList<Production>();
    int nProds=0;
    for(String SSS:prods.keySet()) {
      List<Production> L=prods.get(SSS);
      for(Production P:L) {
        LuserProds.add(P);
        nProds++;
      }
    }
    Production[] userProds=LuserProds.toArray(new Production[]{});
    
    System.out.println("Build the parse table");
    for(Production P:userProds) {
      P.leftSide=userTokenTypes[indexOf(tokentype,P.leftSide)];
      for(int k=0;k<P.rightSide.size();k++) {
        P.rightSide.set(k, userTokenTypes[indexOf(tokentype,P.rightSide.get(k))]);
      }
    }

    String startProd=(String)get(prods.keySet(),0);
    UserTokenType startSymbol=userTokenTypes[indexOf(tokentype,startProd)];
    Grammar KUserGrammar=new Grammar(startSymbol,userTokenTypes[0],userProds);
    LRParser KUserLR=new LRParser(KUserGrammar,new ParsonsLR1().Generate(KUserGrammar,UserPrec,UserAssoc,"Data/Grammar/KGoldUser"));

    System.out.println("Print out the results");
    System.out.println(toString(dfa,KUserLR));
  }
}
