package org.kwansystems.tools.integrator;

import org.kwansystems.tools.Rational;

/**
 * Butcher Tableau representation of Runge-Kutta coefficents. This class can be used to describe any explicit Runge-Kutta
 * type integrator of any order. 
 * <p>
 * The Butcher Tableau is consists of three parts: The step size column <tt>c</tt>, the intermediate slope weight table <tt>a</tt>, and the 
 * final slope weight table <tt>b</tt>. 
 * <p>
 * The explicit Runge-Kutta method in these terms consists then of the following steps.
 */
public class RationalButcherTableau implements ButcherTableau {
  Rational[][] a,b;
  Rational[] c;
  Rational[] e;
  RationalButcherTableau(Rational[] Lc, Rational[][] La, Rational[][] Lb) {
    a=La;
    b=Lb;
    c=Lc;
    if(b.length>1) {
      e=new Rational[Lb[0].length];
      for(int i=0;i<b[0].length;i++) {
        e[i]=b[0][i].subtract(b[1][i]);
      }
    } else {
      e=null;
    }
    checkConsistency();
  }
  RationalButcherTableau(Rational[] Lc, Rational[][] La, Rational[] Lb) {
    this(Lc,La,new Rational[][]{Lb});
  }
  RationalButcherTableau(Rational[] Lc, Rational[][] La, Rational[] Lb, Rational[] Le) {
    a=La;
    Rational[] b1=new Rational[Le.length];
    for(int i=0;i<b1.length;i++) {
      b1[i]=Lb[i].add(Le[i]);
    }
    b=new Rational[][]{Lb,b1};
    c=Lc;
    e=Le;
    checkConsistency();
  }
  public void checkConsistency() {
    Rational acc;
    int s=b[0].length;
    for(int i=0;i<s;i++) {
      acc=c[i];
      for(int j=0;j<i;j++) {
        acc=acc.subtract(a[i][j]);
      }
      if(acc.getNumerator()!=0) {
        String S=c[i].toString()+"|";
        for(int j=0;j<i;j++) {
          S+=a[i][j].toString()+",";
        }
        throw new IllegalArgumentException("Inconsistent tableau in row "+i+", difference of "+acc.toString()+"\n"+S);
      }
    }
  }
  /* (non-Javadoc)
   * @see org.kwansystems.tools.integrator.ButcherTableau#getA()
   */
  public double[][] getA() {
    double[][] result=new double[a.length][];
    for(int i=0;i<a.length;i++) {
      result[i]=new double[a[i].length];
      for(int j=0;j<a[i].length;j++) {
        result[i][j]=a[i][j].doubleValue();
      }
    }
    return result;
  }
  /* (non-Javadoc)
   * @see org.kwansystems.tools.integrator.ButcherTableau#getB()
   */
  public double[][] getB() {
    double[][] result=new double[b.length][];
    for(int i=0;i<b.length;i++) {
      result[i]=getB(i);
    }
    return result;
  }
  /* (non-Javadoc)
   * @see org.kwansystems.tools.integrator.ButcherTableau#getB(int)
   */
  public double[] getB(int i) {
    double[] result=new double[b[i].length];
    for(int j=0;j<b[i].length;j++) {
      result[j]=b[i][j].doubleValue();
    }
    return result;
  }
  /* (non-Javadoc)
   * @see org.kwansystems.tools.integrator.ButcherTableau#getC()
   */
  public double[] getC() {
    double[] result=new double[c.length];
    for(int j=0;j<c.length;j++) {
      result[j]=c[j].doubleValue();
    }
    return result;
  }
  /* (non-Javadoc)
   * @see org.kwansystems.tools.integrator.ButcherTableau#getE()
   */
  public double[] getE() {
    double[] result=new double[e.length];
    for(int j=0;j<e.length;j++) {
      result[j]=e[j].doubleValue();
    }
    return result;
  }
  public static ButcherTableau RK4Tableau=new RationalButcherTableau(
      new Rational[] {
        new Rational(0,1),
        new Rational(1,2),
        new Rational(1,2),
        new Rational(1,1)
      },
      new Rational[][] {
        {},
        {new Rational(1,2)},
        {new Rational(0,1),new Rational(1,2)},
        {new Rational(0,1),new Rational(0,1),new Rational(1,1)}
      },
      new Rational[] {
        new Rational(1,6),new Rational(1,3),new Rational(1,3),new Rational(1,6)
      }
   );
  public static ButcherTableau MidpointTableau=new RationalButcherTableau(
      new Rational[] {
        new Rational(0,1),
        new Rational(1,2)
      },
      new Rational[][] {
        {},
        {new Rational(1,2)}
      },
      new Rational[] {
        new Rational(0,1),new Rational(1,1)
      }
   );
  public static ButcherTableau EulerTableau=new RationalButcherTableau(
      new Rational[] {
        new Rational(0,1),
      },
      new Rational[][] {
        {},
      },
      new Rational[] {
        new Rational(1,1)
      }
   );
  public static ButcherTableau CashKarpTableau=new RationalButcherTableau(
      new Rational[] {
          new Rational(0, 1),
          new Rational(1, 5),
          new Rational(3,10),
          new Rational(3, 5),
          new Rational(1, 1),
          new Rational(7, 8)
      },
      new Rational[][] {
          {},
          {new Rational(   1,    5)},
          {new Rational(   3,   40),new Rational(  9, 40)},
          {new Rational(   3,   10),new Rational( -9, 10),new Rational(    6,    5)},
          {new Rational( -11,   54),new Rational(  5,  2),new Rational(  -70,   27),new Rational(   35,    27)},
          {new Rational(1631,55296),new Rational(175,512),new Rational(  575,13824),new Rational(44275,110592),new Rational(253, 4096)}
      },
      new Rational[][] {
          {new Rational(  37,  378),new Rational(  0,  1),new Rational(  250,  621),new Rational(  125,   594),new Rational(  0,    1),new Rational(512,1771)},
          {new Rational(2825,27648),new Rational(  0,  1),new Rational(18575,48384),new Rational(13525, 55296),new Rational(277,14336),new Rational(  1,   4)}
      }
  );      
  public static ButcherTableau DormandPrinceTableau=new RationalButcherTableau(
      new Rational[] {
          new Rational( 0, 1),
          new Rational( 1, 5),
          new Rational( 3,10),
          new Rational( 4, 5),
          new Rational( 8, 9),
          new Rational( 1, 1),
          new Rational( 1, 1)
      },
      new Rational[][] {
          {},
          {new Rational(    1,    5)},
          {new Rational(    3,   40),new Rational(     9,  40)},
          {new Rational(   44,   45),new Rational(   -56,  15),new Rational(   32,   9)},
          {new Rational(19372, 6561),new Rational(-25360,2187),new Rational(64448,6561),new Rational( -212,  729)},
          {new Rational( 9017, 3168),new Rational(  -355,  33),new Rational(46732,5247),new Rational(   49,  176),new Rational(-5103,18656)},
          {new Rational(   35,  384),new Rational(     0,   1),new Rational(  500,1113),new Rational(  125,  192),new Rational(-2187, 6784),new Rational(11,84)}
      }, 
      new Rational[][] {{
           new Rational(  5197,57600),new Rational(    0,   1),new Rational( 7571,16695),new Rational( 393,640),new Rational(-92097,339200),new Rational(187,2100),new Rational(1,40)
      },{
           new Rational(  35,384),new Rational(0,1),new Rational(    500,1113),new Rational(125,192),new Rational(-2187,6784),new Rational(  11,84),new Rational(0,1)
      }}
  );      
  public static ButcherTableau FehlbergTableau=new RationalButcherTableau(
      new Rational[] {
          new Rational( 0, 1),
          new Rational( 1, 4),
          new Rational( 3, 8),
          new Rational(12,13),
          new Rational( 1, 1),
          new Rational( 1, 2)
      },
      new Rational[][] {
          {},
          {new Rational(   1,    4)},
          {new Rational(   3,   32),new Rational(    9,  32)},
          {new Rational(1932, 2197),new Rational(-7200,2197),new Rational( 7296,2197)},
          {new Rational( 439,  216),new Rational(   -8,   1),new Rational( 3680, 513),new Rational( -845, 4104)},
          {new Rational(  -8,   27),new Rational(    2,   1),new Rational(-3544,2565),new Rational( 1859, 4104),new Rational(-11,40)}
      },
      new Rational[] {
           new Rational(  25,  216),new Rational(    0,   1),new Rational( 1408,2565),new Rational( 2197, 4104),new Rational( -1, 5),new Rational(0, 1)
      },
      new Rational[] {
           new Rational(   1,  360),new Rational(    0,   1),new Rational( -128,4275),new Rational(-2197,75240),new Rational(  1,50),new Rational(2,55)
      }
  );      
  public String toString() {
    StringBuffer result=new StringBuffer("");
    for(int i=0;i<c.length;i++) {
      result.append(c[i].toString());
      result.append("|");
      for(int j=0;j<a[i].length;j++) {
        result.append(a[i][j].toString());
        result.append(",");
      }
      result.append("\n");
    }
    result.append("-------------\n");
    for(int i=0;i<b.length;i++) {
      result.append("b["+i+"]|");
      for(int j=0;j<b[i].length;j++) {
        result.append(b[i][j].toString());
        result.append(",");
      }
      result.append("\n");
    }
    if(e!=null) {
      result.append("err |");
      for(int j=0;j<e.length;j++) {
        result.append(e[j].toString());
        result.append(",");
      }
    }
    return result.toString();
  }
  public String toWiki() {
    StringBuffer result=new StringBuffer("{| cellpadding=3px cellspacing=0px\n");
    //Handle first row special
    result.append("|width=\"20px\"| || style=\"border-right:1px solid;\" | <math>"+c[0].toTeX()+"</math>\n");
    //Now handle the middle rows in a loop
    for(int i=1;i<c.length-1;i++) {
      result.append("|-\n");
      result.append("||| style=\"border-right:1px solid;\" | <math>"+c[i].toTeX()+"</math> ");
      for(int j=0;j<a[i].length;j++) {
        result.append("|| <math>"+a[i][j].toTeX()+"</math>");
      }
      result.append("\n");
    }
    //Last row special
    {
      int i=c.length-1;
      result.append("|-\n");
      result.append("||| style=\"border-right:1px solid; border-bottom:1px solid;\" | <math>"+c[i].toTeX()+"</math> ");
      for(int j=0;j<a[i].length;j++) {
        result.append("|| style=\"border-bottom:1px solid;\" | <math>"+a[i][j].toTeX()+"</math>");
      }
      result.append("|| style=\"border-bottom:1px solid;\" |\n");
    }
    String cellStuff;
    for(int i=0;i<b.length;i++) {
      result.append("|-\n");
      if(i<b.length-1 || b.length>0) {
        result.append("||| style=\"border-right:1px solid; border-bottom:1px solid;\" |  ");
        cellStuff="style=\"border-bottom:1px solid;\" |";
      } else {
        result.append("||| style=\"border-right:1px solid;\" |  ");
        cellStuff="";
      }
      for(int j=0;j<b[i].length;j++) {
        result.append("|| "+cellStuff+"<math>"+b[i][j].toTeX()+"</math>");
      }
      result.append("\n");
    }
    if(b.length>1) {
      result.append("|-\n");
      result.append("||| style=\"border-right:1px solid;\" | <math>\\Delta</math>");
      for(int j=0;j<e.length;j++) {
        result.append("|| <math>"+e[j].toTeX()+"</math>");
      }
      result.append("\n");
    }
    result.append("|}");
    return result.toString();
  }
  public static void main(String args[]) {
    System.out.println(CashKarpTableau.toWiki());
    System.out.println(FehlbergTableau.toWiki());
    System.out.println(RK4Tableau.toWiki());
    System.out.println(DormandPrinceTableau.toWiki());
  }
}