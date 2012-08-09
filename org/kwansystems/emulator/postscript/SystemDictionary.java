package org.kwansystems.emulator.postscript;

import static org.kwansystems.emulator.postscript.PsObject.*;
import static org.kwansystems.emulator.postscript.PsObject.AccessMode.*;
import static org.kwansystems.emulator.postscript.PsObject.StateMode.*;
import static org.kwansystems.emulator.postscript.PsObject.TypeMode.*;

import java.util.*;

import org.kwansystems.emulator.postscript.execstack.*;

public class SystemDictionary extends PsDictionary {
  private static final long serialVersionUID=-8608613370193455643L;
  public SystemDictionary(ExecContext EC) {
    super(200,"systemdict",EC);
    Define( "add",new PsObject(operatortype,Executable,Unlimited,
        new Operator("add") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        PsObject C;
        if(
            (A.Type==realtype && B.Type==realtype) ||
            (A.Type==realtype && B.Type==integertype) ||
            (A.Type==integertype && B.Type==realtype)
        ) {
          C=new PsObject(realtype,Literal,Unlimited,A.GetNumber()+B.GetNumber());
        } else if(A.Type==integertype && B.Type==integertype) {
          C=new PsObject(integertype,Literal,Unlimited,A.GetInt()+B.GetInt());
        } else {
          throw new typecheck("First argument type is "+A.Type+", second is "+B.Type);
        }
        Push(C);
      }
    }
    ));
    Define( "sub",new PsObject(operatortype,Executable,Unlimited,
        new Operator("sub") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        PsObject C;
        if(
            (A.Type==realtype && B.Type==realtype) ||
            (A.Type==realtype && B.Type==integertype) ||
            (A.Type==integertype && B.Type==realtype)
        ) {
          C=new PsObject(A.Type,Literal,Unlimited,A.GetNumber()-B.GetNumber());
        } else if(A.Type==integertype && B.Type==integertype) {
          C=new PsObject(A.Type,Literal,Unlimited,A.GetInt()-B.GetInt());
        } else {
          throw new typecheck("First argument type is "+A.Type+", second is "+B.Type);
        }
        Push(C);
      }
    }
    ));
    Define("neg",new PsObject(operatortype,Executable,Unlimited,
        new Operator("neg") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject C;
        if(B.Type==realtype) {
          C=new PsObject(realtype,Literal,Unlimited,-B.GetNumber());
        } else if(B.Type==integertype) {
          C=new PsObject(integertype,Literal,Unlimited,-B.GetInt());
        } else {
          throw new typecheck("argument",new TypeMode[]{realtype,integertype},B.Type);
        }
        Push(C);
      }
    }
    ));
    Define( "mul",new PsObject(operatortype,Executable,Unlimited,
        new Operator("mul") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        PsObject C;
        if(
            (A.Type==realtype && B.Type==realtype) ||
            (A.Type==realtype && B.Type==integertype) ||
            (A.Type==integertype && B.Type==realtype)
        ) {
          C=new PsObject(A.Type,Literal,Unlimited,A.GetNumber()*B.GetNumber());
        } else if(A.Type==integertype && B.Type==integertype) {
          C=new PsObject(A.Type,Literal,Unlimited,A.GetInt()*B.GetInt());
        } else {
          throw new typecheck("First argument type is "+A.Type+", second is "+B.Type);
        }
        Push(C);
      }
    }
    ));
    Define( "div",new PsObject(operatortype,Executable,Unlimited,
        new Operator("div") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        PsObject C;
        if(
            (A.Type==realtype && B.Type==realtype) ||
            (A.Type==realtype && B.Type==integertype) ||
            (A.Type==integertype && B.Type==realtype) ||
            (A.Type==integertype && B.Type==integertype)
        ) {
          //Always do floating point division, even if both operands are integers
          if(B.GetNumber()==0) throw new PostscriptError("undefinedresult","Division by zero");
          C=new PsObject(realtype,Literal,Unlimited,A.GetNumber()/B.GetNumber());
        } else {
          throw new typecheck("First argument type is "+A.Type+", second is "+B.Type);
        }
        Push(C);
      }
    }
    ));
    Define( "idiv",new PsObject(operatortype,Executable,Unlimited,
        new Operator("idiv") {
      public void doOperate(ExecContext EC) {
        int b=Pop().GetInt();
        int a=Pop().GetInt();
        PsObject C;
        if(b==0) throw new PostscriptError("undefinedresult","Division by zero");
        int c=a/b;
        C=new PsObject(integertype,Literal,Unlimited,new Integer(c));
        Push(C);
      }
    }
    ));
    Define("dict",new PsObject(operatortype,Executable,Unlimited,
        new Operator("dict") {
      public void doOperate(ExecContext EC) {
        int Capacity=Pop().GetInt();
        Push(new PsObject(dicttype,Literal,Unlimited,new PsDictionary(Capacity,""+Capacity+" dict",EC)));
      }
    }
    ));
    Define( "def",new PsObject(operatortype,Executable,Unlimited,
        new Operator("def") {
      public void doOperate(ExecContext EC) {
        PsObject Value=Pop();
        PsObject N=Pop();
        //This is not correct semantics, any object should be usable as a dictionary key
        if(N.Type!=nametype) {
          throw new typecheck("first argument",nametype,N.Type);
        }
        EC.Define(N.get().toString(),Value);
      }
    }
    ));
    Define( "bind",new PsObject(operatortype,Executable,Unlimited,
        new Operator("bind") {
      public void doOperate(ExecContext EC) {
        PsObject Proc=Pop();
        if(Proc.Type!=arraytype || Proc.State!=Executable) throw new typecheck("argument",Executable,arraytype,Proc.State,Proc.Type);
        EC.Bind((PsArray)(Proc.get()));
        Push(Proc);
        EC.HeapChanged();
      }
    }
    ));
    Define( "begin",new PsObject(operatortype,Executable,Unlimited,
        new Operator("begin") {
      public void doOperate(ExecContext EC) {
        PsObject DictToBegin=(PsObject)Pop();
        if(DictToBegin.Type!=dicttype) throw new typecheck("argument",dicttype,DictToBegin.Type);
        EC.DictPush(DictToBegin);
      }
    }
    ));
    Define( "end",new PsObject(operatortype,Executable,Unlimited,
        new Operator("end") {
      public void doOperate(ExecContext EC) {
        EC.DictPop("end");
      }
    }
    ));
    Define( "{",new PsObject(operatortype,Executable,Unlimited,
        new Operator("{") {
      public void doOperate(ExecContext EC) {
        Push(new PsObject(marktype,Executable,Unlimited,"{"));
      }
    }
    ));
    Define( "[",new PsObject(operatortype,Executable,Unlimited,
        new Operator("[") {
      public void doOperate(ExecContext EC) {
        Push(new PsObject(marktype,Literal,Unlimited,"["));
      }
    }
    ));
    Define( "<<",new PsObject(operatortype,Executable,Unlimited,
        new Operator("<<") {
      public void doOperate(ExecContext EC) {
        Push(new PsObject(marktype,Literal,Unlimited,"<<"));
      }
    }
    ));
    Define( "}",new PsObject(operatortype,Executable,Unlimited,
        new CompoundOperator("}",CompoundOperator.makeCOArray(new String[] {
            "]","cvx"
        }))
    ));
    Define( "]",new PsObject(operatortype,Executable,Unlimited,
        new CompoundOperator("]",CompoundOperator.makeCOArray(new String[] {
            "counttomark","array","astore","exch","pop"
        }))
    ));
    Define("mark",new PsObject(operatortype,Executable,Unlimited,
        new Operator("mark") {
      public void doOperate(ExecContext EC) {
        Push(new PsObject(marktype,Literal,Unlimited,"mark"));
      }
    }
    ));
    Define( ">>",new PsObject(operatortype,Executable,Unlimited,
        new CompoundOperator(">>",CompoundOperator.makeCOArray(new String[] {
            "counttomark","two","idiv","dup","dict","begin","{","def","}","repeat","pop","currentdict","end"
        }))
    ));
    Define( "counttomark",new PsObject(operatortype,Executable,Unlimited,
        new Operator("counttomark") {
      public void doOperate(ExecContext EC) {
        int ArrayLength=-1;
        for(Iterator I=EC.OpStackIterator();I.hasNext();) {
          PsObject ThisOperand=(PsObject)I.next();
          if(ThisOperand.Type==marktype) {
            ArrayLength=0;
          } else if (ArrayLength>=0) {
            ArrayLength++;
          }
        }
        if(ArrayLength<0) throw new PostscriptError("unmatchedmark","No marks found on the stack");
        Push(new PsObject(integertype,Literal,Unlimited,ArrayLength));
      }
    }
    ));
    Define( "cleartomark",new PsObject(operatortype,Executable,Unlimited,
        new Operator("cleartomark") {
      public void doOperate(ExecContext EC) {
        int ArrayLength=-1;
        for(Iterator I=EC.OpStackIterator();I.hasNext();) {
          PsObject ThisOperand=(PsObject)I.next();
          if(ThisOperand.Type==marktype) {
            ArrayLength=0;
          } else if (ArrayLength>=0) {
            ArrayLength++;
          }
        }
        if(ArrayLength<0) throw new PostscriptError("unmatchedmark","No marks found on the stack");
        //This pops the mark too
        for(int i=0;i<=ArrayLength;i++) Pop();
      }
    }
    ));
    Define( "save",new PsObject(operatortype,Executable,Unlimited,
        new Operator("save") {
      public void doOperate(ExecContext EC) {
        //TODO: Make this really save
        Push(new PsObject(savetype,Literal,Unlimited,null));
      }
    }
    ));
    Define( "restore",new PsObject(operatortype,Executable,Unlimited,
        new Operator("restore") {
      public void doOperate(ExecContext EC) {
        //TODO: Make this really restore
        PsObject P=Pop();
        if(P.Type!=savetype) throw new typecheck("argument",savetype,P.Type);
      }
    }
    ));
    Define( "setlinewidth",new PsObject(operatortype,Executable,Unlimited,
        new Operator("setlinewidth") {
      public void doOperate(ExecContext EC) {
        PsObject Width=Pop();
        EC.currentGState.setlinewidth(Width.GetNumber());
      }
    }
    ));
    Define("currentmatrix",new PsObject(operatortype,Executable,Unlimited,
        new Operator("currentmatrix") {
      /*
       A transform matrix is of the form
       [a  c  e]
       [b  d  f]
       [0  0  1] (This is the transpose of how square matrices are shown in the Red Book)
       and is represented in PostScript as [a b c d e f] (This matches the Red Book)
       since the last row is always [0 0 1]
       */
      public void doOperate(ExecContext EC) {
        Pop();
        Push(EC.currentGState.CTM.toPsArray(EC));
      }
    }
    ));
    Define("matrix",new PsObject(operatortype,Executable,Unlimited,
        new Operator("matrix") {
      /*
       Identity is
       [1 0 0]
       [0 1 0]
       [0 0 1]
       =
       [1  0  0  1  0  0 ]
       */
      public void doOperate(ExecContext EC) {
        Push(new PsMatrix().toPsArray(EC));
      }
    }
    ));
    Define( "translate",new PsObject(operatortype,Executable,Unlimited,
        new Operator("translate") {
      /*
       translate is
       [1  0  tx]
       [0  1  ty]
       [0  0  1 ]
       = 
       [1  0  0  1  tx ty]
       */
      public void doOperate(ExecContext EC) {
        double ty=Pop().GetNumber();
        double tx=Pop().GetNumber();
        EC.currentGState.CTM.Mult(new PsMatrix(1,0,0,1,tx,ty));
      }
    }
    ));
    Define( "scale",new PsObject(operatortype,Executable,Unlimited,
        new Operator("scale") {
      /*
       scale is
       [sx 0  0]
       [0  sy 0]
       [0  0  1]
       =
       [sx 0  0  sy 0  0 ]
       */
      public void doOperate(ExecContext EC) {
        double sy=Pop().GetNumber();
        double sx=Pop().GetNumber();
        EC.currentGState.CTM.Mult(new PsMatrix(sx,0,0,sy,0,0));
      }
    }
    ));
    Define( "rotate",new PsObject(operatortype,Executable,Unlimited,
        new Operator("rotate") {
      /*
       rotate is
       [c  -s 0]
       [s  c  0]
       [0  0  1]
       =
       [c  s  -s c  0  0 ]
       where c=cos(theta) and s=sin(theta)
       */
      public void doOperate(ExecContext EC) {
        double theta=Pop().GetNumber();
        double c=Math.cos(theta*Math.PI/180);
        double s=Math.sin(theta*Math.PI/180);
        EC.currentGState.CTM.Mult(new PsMatrix(c,s,-s,c,0,0));
      }
    }
    ));
    Define("currentpoint",new PsObject(operatortype,Executable,Unlimited,
        new Operator("currentpoint") {
      public void doOperate(ExecContext EC) {
        //Transform device space back into current user space
        double[] currentPoint=EC.currentGState.currentPath.getCurrentPoint();
        currentPoint=EC.currentGState.CTM.InvTransform(currentPoint);
        //Put the answer on the stack
        Push(new PsObject(realtype,Literal,Unlimited,new Double(currentPoint[0])));
        Push(new PsObject(realtype,Literal,Unlimited,new Double(currentPoint[1])));
      }
    }
    ));
    Define( "moveto",new PsObject(operatortype,Executable,Unlimited,
        new Operator("moveto") {
      public void doOperate(ExecContext EC) {
        double Y=Pop().GetNumber();
        double X=Pop().GetNumber();
        EC.currentGState.moveto(EC.currentGState.CTM.TransformX(X,Y),EC.currentGState.CTM.TransformY(X,Y));
      }
    }
    ));
    Define( "lineto",new PsObject(operatortype,Executable,Unlimited,
        new Operator("lineto") {
      public void doOperate(ExecContext EC) {
        if (EC.currentGState.currentPath.size()==0) throw new PostscriptError("nocurrentpoint","No current point");
        double Y=Pop().GetNumber();
        double X=Pop().GetNumber();
        EC.currentGState.lineto(EC.currentGState.CTM.TransformX(X,Y),EC.currentGState.CTM.TransformY(X,Y));
      }
    }
    ));
    Define( "curveto",new PsObject(operatortype,Executable,Unlimited,
        new Operator("curveto") {
      public void doOperate(ExecContext EC) {
        if (EC.currentGState.currentPath.size()==0) throw new PostscriptError("nocurrentpoint","No current point");
        double Y3=Pop().GetNumber();
        double X3=Pop().GetNumber();
        double Y2=Pop().GetNumber();
        double X2=Pop().GetNumber();
        double Y1=Pop().GetNumber();
        double X1=Pop().GetNumber();
        EC.currentGState.curveto(
            EC.currentGState.CTM.TransformX(X1,Y1),EC.currentGState.CTM.TransformY(X1,Y1),
            EC.currentGState.CTM.TransformX(X2,Y2),EC.currentGState.CTM.TransformY(X2,Y2),
            EC.currentGState.CTM.TransformX(X3,Y3),EC.currentGState.CTM.TransformY(X3,Y3)
        );
      }
    }
    ));
    Define( "rmoveto",new PsObject(operatortype,Executable,Unlimited,
        new Operator("rmoveto") {
      public void doOperate(ExecContext EC) {
        double[] currentPoint=EC.currentGState.currentPath.getCurrentPoint();
        currentPoint=EC.currentGState.CTM.InvTransform(currentPoint);
        double Y=Pop().GetNumber()+currentPoint[1];
        double X=Pop().GetNumber()+currentPoint[0];
        EC.currentGState.moveto(EC.currentGState.CTM.TransformX(X,Y),EC.currentGState.CTM.TransformY(X,Y));
      }
    }
    ));
    Define( "rlineto",new PsObject(operatortype,Executable,Unlimited,
        new Operator("rlineto") {
      public void doOperate(ExecContext EC) {
        double[] currentPoint=EC.currentGState.currentPath.getCurrentPoint();
        currentPoint=EC.currentGState.CTM.InvTransform(currentPoint);
        double Y=Pop().GetNumber()+currentPoint[1];
        double X=Pop().GetNumber()+currentPoint[0];
        EC.currentGState.lineto(EC.currentGState.CTM.TransformX(X,Y),EC.currentGState.CTM.TransformY(X,Y));
      }
    }
    ));
    Define( "rcurveto",new PsObject(operatortype,Executable,Unlimited,
        new Operator("rcurveto") {
      public void doOperate(ExecContext EC) {
        double[] currentPoint=EC.currentGState.currentPath.getCurrentPoint();
        currentPoint=EC.currentGState.CTM.InvTransform(currentPoint);
        double Y3=Pop().GetNumber()+currentPoint[1];
        double X3=Pop().GetNumber()+currentPoint[0];
        double Y2=Pop().GetNumber()+currentPoint[1];
        double X2=Pop().GetNumber()+currentPoint[0];
        double Y1=Pop().GetNumber()+currentPoint[1];
        double X1=Pop().GetNumber()+currentPoint[0];
        EC.currentGState.curveto(
            EC.currentGState.CTM.TransformX(X1,Y1),EC.currentGState.CTM.TransformY(X1,Y1),
            EC.currentGState.CTM.TransformX(X2,Y2),EC.currentGState.CTM.TransformY(X2,Y2),
            EC.currentGState.CTM.TransformX(X3,Y3),EC.currentGState.CTM.TransformY(X3,Y3)
        );
      }
    }
    ));
    Define( "closepath",new PsObject(operatortype,Executable,Unlimited,
        new Operator("closepath") {
      public void doOperate(ExecContext EC) {
        EC.currentGState.closepath();
      }
    }
    ));
    Define( "fill",new PsObject(operatortype,Executable,Unlimited,
        new Operator("fill") {
      public void doOperate(ExecContext EC) {
        if(!EC.currentGState.currentPath.isClosed()) EC.currentGState.closepath();
        EC.painter.fill(EC.currentGState);
        EC.currentGState.newpath();
      }
    }
    ));
    Define( "showpage",new PsObject(operatortype,Executable,Unlimited,
        new Operator("showpage") {
      public void doOperate(ExecContext EC) {
        EC.painter.showpage(EC.currentGState);
      }
    }
    ));
    Define( "stroke",new PsObject(operatortype,Executable,Unlimited,
        new Operator("stroke") {
      public void doOperate(ExecContext EC) {
        EC.painter.stroke(EC.currentGState);
        EC.currentGState.newpath();
      }
    }
    ));
    Define( "gsave",new PsObject(operatortype,Executable,Unlimited,
        new Operator("gsave") {
      public void doOperate(ExecContext EC) {
        EC.GSave();
      }
    }
    ));
    Define( "grestore",new PsObject(operatortype,Executable,Unlimited,
        new Operator("grestore") {
      public void doOperate(ExecContext EC) {
        EC.GRestore();
      }
    }
    ));
    Define( "currentfile",new PsObject(operatortype,Executable,Unlimited,
        new Operator("currentfile") {
      public void doOperate(ExecContext EC) {
        //TODO: Really get the current file
        throw new PostscriptError("internalerror","doOperate() not yet implemented");
      }
    }
    ));
    Define( "cvx",new PsObject(operatortype,Executable,Unlimited,
        new Operator("cvx") {
      public void doOperate(ExecContext EC) {
        PsObject ToBeX=Pop();
        ToBeX.State=Executable;
        Push(ToBeX);
      }
    }
    ));
    Define( "cvs",new PsObject(operatortype,Executable,Unlimited,
        new Operator("cvs") {
      public void doOperate(ExecContext EC) {
        PsObject Target=Pop();
        PsObject ToBeS=Pop();
        if(Target.Type!=stringtype) throw new typecheck("target",stringtype,Target.Type);
        ((PsString)(Target.get())).set(ToBeS.get().toString());
        Push(Target);
      }
    }
    ));
    Define( "exec",new PsObject(operatortype,Executable,Unlimited,
        new Operator("exec") {
      public void doOperate(ExecContext EC) {
        PsObject ToBeX=Pop();
        ExecStackEntry ESE;
        ESE=ExecStackEntry.makeExecStackEntry(ToBeX);
        EC.ExecPush(ESE);
      }
    }
    ));
    Define( "exch",new PsObject(operatortype,Executable,Unlimited,
        new Operator("exch") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        PsObject B=Pop();
        Push(A);
        Push(B);
      }
    }
    ));
    Define( "dup",new PsObject(operatortype,Executable,Unlimited,
        new Operator("dup") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        //This is fine for composite objects, but the simple
        //objects have to be cloned somehow.
        Push(A);
        Push(A);
      }
    }
    ));
    Define( "length",new PsObject(operatortype,Executable,Unlimited,
        new Operator("length") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        int Len;
        switch(A.Type) {
          case arraytype:
          case nametype:
          case stringtype:
          case dicttype:
            Len=((PsComposite)A.get()).length();
            break;
          default:
            throw new typecheck("argument",new TypeMode[]{arraytype,stringtype,dicttype},A.Type);
        }
        Push(new PsObject(integertype,Literal,Unlimited,new Integer(Len)));
      }
    }
    ));
    Define("string",new PsObject(operatortype,Executable,Unlimited,
        new Operator("string") {
      public void doOperate(ExecContext EC) {
        int a=Pop().GetInt();
        if(a<0) throw new PostscriptError("rangecheck","String length must be non-negative");
        Push(new PsObject(stringtype,Literal,Unlimited,new PsString(new String(new char[a]),EC)));
      }
    }
    ));
    Define("putinterval",new PsObject(operatortype,Executable,Unlimited,
        new Operator("putinterval") {
      public void doOperate(ExecContext EC) {
        PsObject Source=Pop();
        int Idx=Pop().GetInt();
        PsObject Target=Pop();

        if(Source.Type!=Target.Type) throw new typecheck("target",Source.Type,Target.Type);
        switch(Target.Type) {
          case arraytype:
            PsArray SourceA=(PsArray)Source.get();
            PsArray TargetA=(PsArray)Target.get();
            for(int i=0;i<SourceA.length();i++) TargetA.set(Idx,SourceA.get(i));
            break;
          case stringtype:
            String SourceS=((PsString)Source.get()).toString();
            PsString TargetPS=(PsString)Target.get();
            String TargetS=TargetPS.toString();
            TargetPS.set(TargetS.substring(0,Idx)+SourceS+TargetS.substring(Idx+SourceS.length()));
            break;
          default:
            throw new typecheck("target",new TypeMode[]{arraytype,stringtype},Target.Type);
        }
      }
    }
    ));
    Define("put",new PsObject(operatortype,Executable,Unlimited,
        new Operator("put") {
      public void doOperate(ExecContext EC) {
        PsObject Source=Pop();
        PsObject Index=Pop();
        PsObject Target=Pop();

        switch(Target.Type) {
          case arraytype:
            if(Index.Type!=integertype) throw new typecheck("index",integertype,Index.Type);
            int Idx=((Integer)Index.get()).intValue();
            PsArray TargetA=(PsArray)Target.get();
            TargetA.set(Idx,Source);
            break;
          case dicttype:
          case stringtype:
            throw new PostscriptError("internalerror","put not yet implemented for dict or string");
          default:
            throw new typecheck("target",new TypeMode[]{arraytype,dicttype,stringtype},Target.Type);
        }
      }
    }
    ));
    Define("forall",new PsObject(operatortype,Executable,Unlimited,
        new Operator("forall") {
      public void doOperate(ExecContext EC) {
        PsObject Proc=Pop();
        PsObject Source=Pop();
        if(Proc.Type!=arraytype || Proc.State!=Executable) throw new typecheck("procedure",Executable,arraytype,Proc.State,Proc.Type);
        switch(Source.Type) {
          case arraytype:
          case stringtype:
          case dicttype:
            EC.ExecPush(new ForallExecStackEntry(Proc,Source));
            break;
          default:
            throw new typecheck("source",new TypeMode[]{arraytype,stringtype,dicttype},Source.Type);
        }
      }
    }
    ));
    Define("repeat",new PsObject(operatortype,Executable,Unlimited,
        new Operator("repeat") {
      public void doOperate(ExecContext EC) {
        PsObject Proc=Pop();
        int count=Pop().GetInt();
        if(Proc.Type!=arraytype || Proc.State!=Executable) throw new typecheck("procedure",Executable,arraytype,Proc.State,Proc.Type);
        if(count<0) throw new PostscriptError("rangecheck","Count must be non-negative");
        if(count>0) EC.ExecPush(new RepeatExecStackEntry(Proc,count));
        //Otherwise, we just do nothing
      }
    }
    ));
    Define("setmatrix",new PsObject(operatortype,Executable,Unlimited,
        new Operator("setmatrix") {
      public void doOperate(ExecContext EC) {
        PsArray A=(PsArray)(Pop().get());
        EC.currentGState.CTM=new PsMatrix(A);
      }
    }
    ));
    Define("get",new PsObject(operatortype,Executable,Unlimited,
        new Operator("get") {
      public void doOperate(ExecContext EC) {
        PsObject Index=Pop();
        if(Index.Type!=integertype) throw new typecheck("index",integertype,Index.Type);
        int Idx=((Integer)Index.get()).intValue();
        PsObject A=Pop();
        switch(A.Type) {
          case arraytype:
            PsArray SourceA=(PsArray)A.get();
            Push(SourceA.get(Idx));
            break;
          case stringtype:
            int SourceI=((String)A.get()).codePointAt(Idx);
            Push(new PsObject(integertype,Literal,Unlimited,new Integer(SourceI)));
            break;
          default:
            throw new typecheck("source",new TypeMode[]{arraytype,stringtype},A.Type);
        }
      }
    }
    ));
    Define("load",new PsObject(operatortype,Executable,Unlimited,
        new Operator("load") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        A=EC.LookupName(A.GetString().toString());
        Push(A);
      }
    }
    ));
    Define("where",new PsObject(operatortype,Executable,Unlimited,
        new Operator("where") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        A=EC.where(A.GetString().toString());
        if(A==null) {
          Push(new PsObject(booleantype,Literal,Unlimited, false));
        } else {
          Push(A);
          Push(new PsObject(booleantype,Literal,Unlimited, true));
        }
      }
    }
    ));
    Define("store",new PsObject(operatortype,Executable,Unlimited,
        new Operator("store") {
      public void doOperate(ExecContext EC) {
        PsObject value=Pop();
        PsObject key=Pop();
        PsObject A=EC.where(key.GetString().toString());
        PsDictionary D;
        if(A==null) {
          D=EC.currentdict();
        } else {
          D=(PsDictionary)A.get();
        }
        D.Define(key.GetString().toString(), value);
      }
    }
    ));
    Define("currentdict",new PsObject(operatortype,Executable,Unlimited,
        new Operator("currentdict") {
      public void doOperate(ExecContext EC) {
        Push(new PsObject(dicttype,Literal,Unlimited,EC.currentdict()));
      }
    }
    ));
    Define("aload",new PsObject(operatortype,Executable,Unlimited,
        new Operator("aload") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        switch(A.Type) {
          case arraytype:
            PsArray SourceA=(PsArray)A.get();
            for(int i=0;i<SourceA.length();i++) {
              Push(SourceA.get(i));
            }
            Push(A);
            break;
          default:
            throw new typecheck("source",arraytype,A.Type);
        }
      }
    }
    ));
    Define("astore",new PsObject(operatortype,Executable,Unlimited,
        new Operator("astore") {
      public void doOperate(ExecContext EC) {
        PsObject A=Pop();
        switch(A.Type) {
          case arraytype:
            PsArray SourceA=(PsArray)A.get();
            EC.HeapRefreshEnabled=false;
            for(int i=SourceA.length()-1;i>=0;i--) {
              SourceA.set(i,Pop());
            }
            EC.HeapRefreshEnabled=true;
            EC.HeapChanged();
            Push(A);
            break;
          default:
            throw new typecheck("source",arraytype,A.Type);
        }
      }
    }
    ));
    Define("array",new PsObject(operatortype,Executable,Unlimited,
        new Operator("array") {
      public void doOperate(ExecContext EC) {
        int N=Pop().GetInt();
        if(N<0) throw new PostscriptError("rangecheck","Length of array must be non-negative");
        PsArray A=new PsArray(N,EC);
        Push(new PsObject(arraytype,Literal,Unlimited, A));
      }
    }
    ));
    Define("pop",new PsObject(operatortype,Executable,Unlimited,
        new Operator("pop") {
      public void doOperate(ExecContext EC) {
        Pop();
      }
    }
    ));
    Define("eq",new PsObject(operatortype,Executable,Unlimited,
        new Operator("eq") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        Push(new PsObject(booleantype,Literal,Unlimited,new Boolean(A.compareTo(B)==0)));
      }
    }
    ));
    Define("ne",new PsObject(operatortype,Executable,Unlimited,
        new Operator("ne") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        Push(new PsObject(booleantype,Literal,Unlimited,new Boolean(A.compareTo(B)!=0)));
      }
    }
    ));
    Define("ge",new PsObject(operatortype,Executable,Unlimited,
        new Operator("ge") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        Push(new PsObject(booleantype,Literal,Unlimited,new Boolean(A.compareTo(B)>=0)));
      }
    }
    ));
    Define("gt",new PsObject(operatortype,Executable,Unlimited,
        new Operator("gt") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        Push(new PsObject(booleantype,Literal,Unlimited,new Boolean(A.compareTo(B)>0)));
      }
    }
    ));
    Define("lt",new PsObject(operatortype,Executable,Unlimited,
        new Operator("lt") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        Push(new PsObject(booleantype,Literal,Unlimited,new Boolean(A.compareTo(B)<0)));
      }
    }
    ));
    Define("le",new PsObject(operatortype,Executable,Unlimited,
        new Operator("le") {
      public void doOperate(ExecContext EC) {
        PsObject B=Pop();
        PsObject A=Pop();
        Push(new PsObject(booleantype,Literal,Unlimited,new Boolean(A.compareTo(B)<=0)));
      }
    }
    ));
    Define("true",new PsObject(operatortype,Executable,Unlimited,
            new Operator("true") {
          public void doOperate(ExecContext EC) {
            Push(new PsObject(booleantype,Literal,Unlimited,true));
          }
        }
        ));
    Define("false",new PsObject(operatortype,Executable,Unlimited,
            new Operator("false") {
          public void doOperate(ExecContext EC) {
            Push(new PsObject(booleantype,Literal,Unlimited,false));
          }
        }
        ));
    Define("if",new PsObject(operatortype,Executable,Unlimited,
        new Operator("if") {
      public void doOperate(ExecContext EC) {
        PsObject ProcT=Pop();
        PsObject Cond=Pop();
        if(ProcT.Type!=arraytype || ProcT.State!=Executable) throw new typecheck("procedure",Executable,arraytype,ProcT.State,ProcT.Type);
        if (Cond.Type!=booleantype) throw new typecheck("condition",booleantype,Cond.Type);
        if (((Boolean)(Cond.get())).booleanValue()) EC.ExecPush(ExecStackEntry.makeExecStackEntry(ProcT));
      }
    }
    ));
    Define("ifelse",new PsObject(operatortype,Executable,Unlimited,
        new Operator("ifelse") {
      public void doOperate(ExecContext EC) {
        PsObject ProcF=Pop();
        PsObject ProcT=Pop();
        PsObject Cond=Pop();
        if(ProcF.Type!=arraytype || ProcF.State!=Executable) throw new typecheck("false procedure",Executable,arraytype,ProcF.State,ProcF.Type);
        if(ProcT.Type!=arraytype || ProcT.State!=Executable) throw new typecheck("true procedure",Executable,arraytype,ProcT.State,ProcT.Type);
        if (Cond.Type!=booleantype) throw new typecheck("condition",booleantype,Cond.Type);
        EC.ExecPush(ExecStackEntry.makeExecStackEntry(((Boolean)(Cond.get())).booleanValue()?ProcT:ProcF));
      }
    }
    ));
    Define("null",new PsObject(nulltype,Literal,Unlimited,null));
    Define("pause",new PsObject(operatortype,Executable,Unlimited,
        new Operator("pause") {
      public void doOperate(ExecContext EC) {
        EC.ECL.pause();
      }
    }
    ));
    Define("index",new PsObject(operatortype,Executable,Unlimited,
        new Operator("index") {
      public void doOperate(ExecContext EC) {
        List<PsObject> A=new ArrayList<PsObject>();
        int n=Pop().GetInt();
        for(int i=0;i<=n;i++) {
          A.add(Pop());
        }
        for(int i=0;i<=n;i++) {
          Push(A.get(n-i));
        }
        Push(A.get(n));
      }
    }
    ));
    Define("and",new PsObject(operatortype,Executable,Unlimited,
      new Operator("and") {
        public void doOperate(ExecContext EC) {
          PsObject a=Pop();
          PsObject b=Pop();
           
          if(a.Type!=b.Type) throw new typecheck("First argument type is "+a.Type+", second is "+b.Type);
          if(a.Type!=booleantype && a.Type!=integertype)throw new typecheck("operands",booleantype,a.Type);
          if(a.Type==booleantype) {
            Push(new PsObject(booleantype,Literal,Unlimited, (((Boolean) (a.get())).booleanValue() & ((Boolean) (a.get())).booleanValue())));
          } else {
            Push(new PsObject(booleantype,Literal,Unlimited,new Integer(
              ((Integer)(a.get())).intValue()
              &
              ((Integer)(a.get())).intValue()
            )));
          }
        }
      }
    ));
    Define("setlinecap",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setlinecap") {
        public void doOperate(ExecContext EC) {
          int cap=Pop().GetInt();
          if(cap<0) throw new PostscriptError("rangecheck","");
          if(cap>2) throw new PostscriptError("rangecheck","");
          EC.currentGState.setlinecap(cap);
        }
      }
    ));
    Define("setlinejoin",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setlinejoin") {
        public void doOperate(ExecContext EC) {
          int join=Pop().GetInt();
          if(join<0) throw new PostscriptError("rangecheck","");
          if(join>2) throw new PostscriptError("rangecheck","");
          EC.currentGState.setlinejoin(join);
        }
      }
    ));
    Define("setmiterlimit",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setmiterlimit") {
        public void doOperate(ExecContext EC) {
          double limit=Pop().GetNumber();
          if(limit<1.0) throw new PostscriptError("rangecheck","Miter limit must be greater than or equal to 1");
          EC.currentGState.setmiterlimit(limit);
        }
      }
    ));
    Define("setdash",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setdash") {
        public void doOperate(ExecContext EC) {
          double offset=Pop().GetNumber();
          PsObject A=Pop();
          if (A.Type!=arraytype) throw new typecheck("array",arraytype,A.Type);
          PsObject[] AA=(PsObject[])A.get();
          double[] DA=new double[AA.length];
          for(int i=0;i<AA.length;i++) {
            DA[i]=AA[i].GetNumber();
            if(DA[i]<0) throw new PostscriptError("rangecheck","All dash array elements must be non-negative");
          }
          EC.currentGState.setdash(DA,offset);
        }
      }
    ));
    Define("setflat",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setflat") {
        public void doOperate(ExecContext EC) {
          double flat=Pop().GetNumber();
          if(flat<0.2) flat=0.2;
          if(flat>100.0) flat=100.0;
          EC.currentGState.setflat(flat);
        }
      }
    ));
    Define("setgray",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setgray") {
        public void doOperate(ExecContext EC) {
          double w=Pop().GetNumber();
          EC.currentGState.setgray(w);
        }
      }
    ));
    Define("setrgbcolor",new PsObject(operatortype,Executable,Unlimited,
      new Operator("setrgbcolor") {
        public void doOperate(ExecContext EC) {
          double b=Pop().GetNumber();
          double g=Pop().GetNumber();
          double r=Pop().GetNumber();
          EC.currentGState.setrgbcolor(r,g,b);
        }
      }
    ));
    Define("clip",new PsObject(operatortype,Executable,Unlimited,
      new Operator("clip") {
        public void doOperate(ExecContext EC) {
          //TODO: Something!
        }
      }
    ));
    Define("newpath",new PsObject(operatortype,Executable,Unlimited,
      new Operator("newpath") {
        public void doOperate(ExecContext EC) {
          EC.currentGState.newpath();
        }
      }
    ));
    Define("roll",new PsObject(operatortype,Executable,Unlimited,
        new Operator("roll") {
      public void doOperate(ExecContext EC) {
        List<PsObject> A=new ArrayList<PsObject>();
        int j=Pop().GetInt();
        int n=Pop().GetInt();
        for(int i=0;i<n;i++) {
          A.add(0,Pop());
        }
        for(int i=0;i<n;i++) {
          int idx=(i-j)%n; //Stupid no-good negative blankety-blank mod! Why can no one do it right?
          if(idx<0)idx+=n;
          Push(A.get(idx));
        }
      }
    }
    ));
    Define("copy",new PsObject(operatortype,Executable,Unlimited,
        new Operator("copy") {
      public void doOperate(ExecContext EC) {
        List<PsObject> A=new ArrayList<PsObject>();
        PsObject B=Pop();
        switch(B.Type) {
          case integertype:
            int n=B.GetInt();
            for(int i=0;i<n;i++) {
              A.add(0,Pop());
            }
            for(int i=0;i<n;i++) {
              Push(A.get(i));
            }
            for(int i=0;i<n;i++) {
              Push(A.get(i));
            }
            break;
          case arraytype:
          case dicttype:
          case stringtype:
            throw new PostscriptError("internalerror","Not implemented for these types");
          default:
            throw new typecheck("argument",new TypeMode[]{arraytype,stringtype,dicttype,integertype},B.Type);

        }
      }
    }
    ));
 }
}