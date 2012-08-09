package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.*;

public class typecheck extends PostscriptError {
  private static final long serialVersionUID = 7677467750326542447L;
  private static String FormatTMA(TypeMode[] TMA) {
    String result="{";
    for(int i=0;i<TMA.length;i++) {
      if(i>0)result+=",";
      result+=TMA[i].toString();
    }
    return result+"}";
  }
  public typecheck(String Comment) {
    super("typecheck",Comment);
  }
  public typecheck(String Target, StateMode ExpectedState, StateMode ActualState) {
    super("typecheck", "Expected state of "+Target+" to be "+ExpectedState+" instead of "+ActualState);
  }
  public typecheck(String Target, TypeMode ExpectedType, TypeMode ActualType) {
    super("typecheck", "Expected type of "+Target+" to be "+ExpectedType+" instead of "+ActualType);
  }
  public typecheck(String Target, TypeMode[] ExpectedType, TypeMode ActualType) {
    super("typecheck", "Expected type of "+Target+" to be "+FormatTMA(ExpectedType)+" instead of "+ActualType);
  }
  public typecheck(String Target, StateMode ExpectedState, TypeMode ExpectedType, StateMode ActualState, TypeMode ActualType) {
    super("typecheck", "Expected type of "+Target+" to be "+ExpectedState+" "+ExpectedType+" instead of "+ActualState+" "+ActualType);
  }
}
