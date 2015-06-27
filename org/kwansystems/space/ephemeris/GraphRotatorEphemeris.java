package org.kwansystems.space.ephemeris;

import java.util.*;
import org.kwansystems.graph.DirectedEdge;
import org.kwansystems.graph.DirectedGraph;
import org.kwansystems.graph.Vertex;
import org.kwansystems.tools.rotation.Rotator;
import org.kwansystems.tools.time.Time;

public class GraphRotatorEphemeris extends RotatorEphemeris {
    private DirectedGraph G;
    private List<GraphREEdge> compiled;

    protected GraphRotatorEphemeris() {
      super(null,null);
      G=new DirectedGraph();
      compiled=null;
    }
    
    @Override
    public Rotator CalcRotation(Time T) {
      List<Rotator> R=CalcRotationList(T);
      Rotator result=null;
      for(Rotator thisRot:R) {
        if(result==null) result=thisRot; else result=thisRot.combine(result);
      }
      return result;
    }
    public List<Rotator> CalcRotationList(Time T) {
      if(compiled==null) throw new UnsupportedOperationException("Need to run set(source,target) first");
      List<Rotator> result=new ArrayList<Rotator>(compiled.size());
      for(GraphREEdge E:compiled) {
        Rotator thisRot=E.R.CalcRotation(T);
        if(!E.isFwd) thisRot=thisRot.inv();
        result.add(thisRot);
      }
      return result;
    }
    public void addEdge(Vertex Lfrom, Vertex Lto, RotatorEphemeris fwd) {
        G.add(new GraphREEdge(Lfrom, Lto, fwd, true));
        G.add(new GraphREEdge(Lto, Lfrom, fwd, false));
    }
    public void addEdge(RotatorEphemeris fwd) {
      addEdge(fwd.naturalFrom,fwd.naturalTo,fwd);
    }

    public void set(Vertex Lsource, Vertex Ltarget) {
        List Lcompiled = G.Djikstra(Lsource, Ltarget);
        compiled = (List<GraphREEdge>) Lcompiled;
    }

    private class GraphREEdge extends DirectedEdge {

        RotatorEphemeris R;
        boolean isFwd;

        public GraphREEdge(Vertex Lfrom, Vertex Lto, RotatorEphemeris LR, boolean LisFwd) {
            super(Lfrom,Lto);
            R = LR;
            isFwd = LisFwd;
        }

        public Rotator CalcRotation(Time T) {
            Rotator RR = R.CalcRotation(T);
            if (!isFwd) {
                RR = RR.inv();
            }
            return RR;
        }
    }

}
