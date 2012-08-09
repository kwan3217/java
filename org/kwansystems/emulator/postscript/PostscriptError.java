package org.kwansystems.emulator.postscript;

public class PostscriptError extends RuntimeException {
  private static final long serialVersionUID = 196130400722122316L;
  public String error;
  private String opName;
  public String Comment;
  public Throwable cause;
  public ExecContext EC;
  public PostscriptError(PostscriptError Lsource, String LopName) {
    super("error: "+Lsource.error+((LopName!=null)?("  operator: "+LopName):(""))+((Lsource.Comment!=null)?(" ("+Lsource.Comment+")"):("")));
    error=Lsource.error;
    opName=LopName;
    Comment=Lsource.Comment;
    cause=Lsource.cause;
    EC=Lsource.EC;
  }
  public PostscriptError(String Lerror,String LComment, Throwable Lcause) {
    super("error: "+Lerror+((LComment!=null)?(" ("+LComment+")"):("")));
    error=Lerror;
    Comment=LComment;
    cause=Lcause;
  }
  public PostscriptError(String Lerror,String LComment) {
    this(Lerror,LComment,null);
  }
}

