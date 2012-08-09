package org.kwansystems.space.ephemeris;

import static java.lang.Math.*;
import java.util.regex.*;

import static org.kwansystems.space.ephemeris.RotationTerm.Scale.*;
import static org.kwansystems.space.ephemeris.RotationTerm.Type.*;

public class RotationTerm {
  public enum Type {Cst,Pol,Sin,Cos};
  public enum Scale {d,T};
  protected Type type;
  protected Scale scale;
  protected double coeff1;
  protected int Pindex;
  protected int coeff2; //For Pol, power of polynomial (ie for d6, 6)
                        //For Sin or Cos, coeff of argument (ie for sin 4J1, 4)
  protected static Pattern p=Pattern.compile("([-+][0-9]+\\.[0-9]*(e[-+]?[0-9]+)?)\\s*((d|T)([0-9]+)?)?(((sin)|(cos))\\s*([0-9]+)?([A-Z]([0-9]+)))?\\s*");
  public RotationTerm(Type Ltype, Scale Lscale, double Lcoeff1, int LPindex, int Lcoeff2) {
    type=Ltype;
    scale=Lscale;
    coeff1=Lcoeff1;
    Pindex=LPindex;
    coeff2=Lcoeff2;
  }
  public RotationTerm(Type Ltype, Scale Lscale, double Lcoeff1, int LPindex) {
    this(Ltype,Lscale,Lcoeff1,LPindex,1);
  }
  public RotationTerm(Type Ltype, Scale Lscale, double Lcoeff1) {
    this(Ltype,Lscale,Lcoeff1,-1);
  }
  public RotationTerm(Type Ltype, double Lcoeff1) {
    this(Ltype,Scale.T,Lcoeff1);
  }
  public RotationTerm(String s) {
	Matcher m=p.matcher(s);
	if (m.matches()) {
//      for(int i=0;i<=m.groupCount();i++) {
//     	System.out.println("group "+i+": "+m.group(i));
//      }
  	  coeff1=Double.parseDouble(m.group(1));
	  String polyVar=m.group(4);
	  String trigFunc=m.group(7);
      String polyCoeff=m.group(5);
      String perCoeff=m.group(10);
      String perIndex=m.group(12);
	  if(polyVar!=null) {
	    type=Pol;
	    if(polyVar.equalsIgnoreCase("d")) scale=d; else scale=T;
  	    if(polyCoeff!=null) coeff2=Integer.parseInt(polyCoeff); else coeff2=1;
	  } else if(trigFunc!=null) {
	    if(trigFunc.equalsIgnoreCase("sin")) type=Sin; else type=Cos;
	    if(perCoeff!=null) coeff2=Integer.parseInt(perCoeff); else coeff2=1;
	    Pindex=Integer.parseInt(perIndex);
	  } else {
	    type=Cst;
	  }
	}
  }
  public double Evaluate(double d, RotationTerm[][] P) {
    double T=d/36525.0;
    double varTerm=1;
    switch(type) {
      case Cst:
        break;
      case Pol:
        switch(scale) {
          case d:
            varTerm=pow(d,coeff2);
          case T:
            varTerm=pow(T,coeff2);
        }
        break;
      case Sin:
      case Cos:
        double arg=toRadians(coeff2*Evaluate(d,P,P[Pindex]));
        if(type==Sin) varTerm=sin(arg); else varTerm=cos(arg);
        break;
    }
    return varTerm*coeff1;
  }
  public static double Evaluate(double D, RotationTerm[][] P, RotationTerm[] Series) {
    double acc=0;
    for(RotationTerm Term:Series) acc+=Term.Evaluate(D,P);
    return acc;
  }
  public String toString() {
	StringBuffer S=new StringBuffer("coeff1: ");
	S.append(Double.toString(coeff1));S.append('\n');
	S.append("type:   ");S.append(type.toString());S.append('\n');
	if(scale!=null){S.append("scale: ");S.append(scale.toString());S.append('\n');}
	S.append("coeff2: ");S.append(Integer.toString(coeff2));S.append('\n');
	S.append("Pindex: ");S.append(Integer.toString(Pindex));
	return S.toString();
  }
  public static void main(String[] args) {
	RotationTerm R1=new RotationTerm("+0.01e-12 cos 2J1");
	RotationTerm R2=new RotationTerm("-0.01d");
	RotationTerm R3=new RotationTerm("+0.01e+12T3");
	RotationTerm R4=new RotationTerm("+12.345");
	System.out.println(R1);
	System.out.println(R2);
	System.out.println(R3);
	System.out.println(R4);
  }
}