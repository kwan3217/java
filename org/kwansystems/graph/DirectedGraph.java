package org.kwansystems.graph;

import java.util.*;
import java.io.*;

public class DirectedGraph {
  Set<Vertex> vertices;
  Set<DirectedEdge> edges;

  public String DotTransitionTable(String objectName) {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    //Header
    ouf.println("digraph "+objectName+" {");
    
    //Labels for all vertices
    for(Vertex v:vertices) {
      String label=v.toString();
      String shape="ellipse";
      ouf.println("  {node [shape = "+shape+"]; \""+label+"\";}");
    }
    
    
    //Draw edges
    for(DirectedEdge e:edges) {
      ouf.println(e.DotTransitionTable());
    }
    ouf.println("}");
    return result.toString();
  }

  private static Vertex smallestDist(Set<Vertex> Q, Map<Vertex,Integer> dist) {
    Vertex result=null;
    int smallest=Integer.MAX_VALUE;
    for (Vertex q:Q) {
      int dist_q=dist.get(q);
      if(dist_q<smallest) {
        smallest=dist_q;
        result=q;
      }
    }
    return result;
  }
  
  public List<DirectedEdge> Djikstra(Vertex source, Vertex target) {
    //Run Djikstra's Algorithm, stopping when target vertex is reached. Follows
    //http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm and extensively
    //uses the Collections framework
    
    //Contains current best distance from the source to any particular vertex
    Map<Vertex,Integer> dist=new HashMap<Vertex,Integer>();
    //Contains the last edge in the shortest path from the source to any particular vertex
    //IE following the edge backwards gives the "previous" node in the path.
    Map<Vertex,DirectedEdge> previous=new HashMap<Vertex,DirectedEdge>();
    //Index of edges. For each vertex, keep a list of all edges leading out of it
    Map<Vertex,Set<DirectedEdge>> index=new HashMap<Vertex,Set<DirectedEdge>>(vertices.size());
    for(DirectedEdge e:edges) {
      if(!index.containsKey(e.from)) index.put(e.from, new HashSet<DirectedEdge>());
      index.get(e.from).add(e);
    }
    
    //Build the distance list, initially setting all distances to infinity, except for
    //the distance from the source to the source, which is zero
    for(Vertex v:vertices) {
      dist.put(v, Integer.MAX_VALUE);               // Unknown distance function from source to v
    // 4          previous[v] := undefined          // Previous node in optimal path from source
                                                    // Represented in Java by having an empty map
    }
    dist.put(source, 0);// Distance from source to source

    Set<Vertex> Q=new HashSet<Vertex>(vertices); // All nodes in the graph start not known to be optimized - thus are in Q
    while(Q.size()>0) {  //While Q is not empty -- the main loop
      //We'll work with the node that has the smallest distance which isn't known to be optimized.
      Vertex u=smallestDist(Q,dist);
      //If there isn't one, there is no path from source to some nodes. Since we are working towards a target
      //node and quit once we find it, if we get here then there is no path to that target, so abort.
      if(u==null) throw new IllegalArgumentException("No path from "+source.toString()+" to "+target.toString()+" was found");
      Q.remove(u); //u had already been optimized, but now we know it so remove it from unoptimized set
      if(u==target) break; //If the target has been optimized, we're done with this part
      if(index.containsKey(u)) {
        Set<DirectedEdge> index_u=index.get(u);
        for(DirectedEdge E:index_u) if(Q.contains(E.to)) { // Consider each unoptimized neighbor v
          Vertex v=E.to;
          int dist_u=dist.get(u);
        
          //Calculate an alternate distance to v through best path to u. Be careful to conserve infinity
          int alt=(dist_u==Integer.MAX_VALUE)?Integer.MAX_VALUE:dist_u+E.weight();
          if(alt<dist.get(v)) { //Is this better than the current record to v?
            //Keep track of it
            dist.put(v, alt);
            //The current shortest path to v ends with the edge from u to v
            previous.put(v, E);
          }
        }
      }
    }
    
    //Follow the chain of previous edges back to get the shortest path to the target
    LinkedList<DirectedEdge> S=new LinkedList<DirectedEdge>();
    Vertex u=target;
    DirectedEdge previous_u=previous.get(u);
    while(previous_u!=null) {
      S.addFirst(previous_u);
      u=previous_u.from;
      previous_u=previous.get(u);
    }
    
    return S;
  }
  public DirectedGraph() {
    edges=new HashSet<DirectedEdge>();
    vertices=new HashSet<Vertex>();
  }
  public DirectedGraph(List<DirectedEdge> LEdge) {
    edges=new HashSet<DirectedEdge>(LEdge);
    vertices=new HashSet<Vertex>(LEdge.size());
    for(DirectedEdge e:edges) {
      vertices.add(e.from);
      vertices.add(e.to);
    }
  }
  public void add(DirectedEdge Ledge) {
    edges.add(Ledge);
    vertices.add(Ledge.from);
    vertices.add(Ledge.to);
  }
  public void add(Vertex from, Vertex to) {
    add(new DirectedEdge(from,to));
  }
  @Override
  public String toString() {
    StringBuffer result=new StringBuffer("");
    for(Vertex v:vertices) result.append(v.toString()+"\n");
    for(DirectedEdge e:edges) result.append(e.toString()+"\n");
    return result.toString();
  }
  private static class StringVertex implements Vertex {
    String S;
    public StringVertex(String LS) {
      S=LS;
    }
    public String toString() {
      return S;
    }
  }
  private static void addVertex(Vertex[] v, DirectedGraph G, int start, int[] stop) {
    for(int i:stop) {
      G.add(v[start],v[i]);
    }
  }
  public static void main(String[] args) {
    Vertex[] v=new Vertex[55];
    for(int i=1;i<=54;i++) {
      v[i]=new StringVertex(String.format("%02d",i));
    }
    DirectedGraph G=new DirectedGraph(); 
    addVertex(v,G, 1,new int[] { 7,14});
    addVertex(v,G, 2,new int[] {54});
    addVertex(v,G, 3,new int[] { 9});
    addVertex(v,G, 4,new int[] {41,23});
    addVertex(v,G, 5,new int[] {19});
    addVertex(v,G, 6,new int[] {43});
    addVertex(v,G, 7,new int[] {26,44});
    addVertex(v,G, 8,new int[] { 3});
    addVertex(v,G, 9,new int[] {54});
    addVertex(v,G,10,new int[] {47, 2});
    addVertex(v,G,11,new int[] {39,21,15,33});
    addVertex(v,G,12,new int[] {36});
    addVertex(v,G,13,new int[] {50});
    addVertex(v,G,14,new int[] { 7,51});
    addVertex(v,G,15,new int[] {33});
    addVertex(v,G,16,new int[] { 9,46});
    addVertex(v,G,17,new int[] {45,53});
    addVertex(v,G,18,new int[] {30,11});
    addVertex(v,G,19,new int[] {});
    addVertex(v,G,20,new int[] {32,13,50});
    addVertex(v,G,21,new int[] {33});
    addVertex(v,G,22,new int[] {34,21,52});
    addVertex(v,G,23,new int[] { 8,35});
    addVertex(v,G,24,new int[] {36});
    addVertex(v,G,25,new int[] {37, 5});
    addVertex(v,G,26,new int[] {38,20});
    addVertex(v,G,27,new int[] {});
    addVertex(v,G,28,new int[] {});
    addVertex(v,G,29,new int[] {54}); 
    addVertex(v,G,30,new int[] {48, 4, 3});
    addVertex(v,G,31,new int[] {12,49});
    addVertex(v,G,32,new int[] {50});
    addVertex(v,G,33,new int[] {});
    addVertex(v,G,34,new int[] {16,28});
    addVertex(v,G,35,new int[] {22,40, 8});
    addVertex(v,G,36,new int[] {18,11});
    addVertex(v,G,37,new int[] {49,31, 5,42});
    addVertex(v,G,38,new int[] {20});
    addVertex(v,G,39,new int[] {28});
    addVertex(v,G,40,new int[] { 9});
    addVertex(v,G,41,new int[] {17,29,10});
    addVertex(v,G,42,new int[] {});
    addVertex(v,G,43,new int[] {25,24,36});
    addVertex(v,G,44,new int[] {26,50});
    addVertex(v,G,45,new int[] {});
    addVertex(v,G,46,new int[] {});
    addVertex(v,G,47,new int[] {});
    addVertex(v,G,48,new int[] {54});
    addVertex(v,G,49,new int[] {19});
    addVertex(v,G,50,new int[] { 6,43});
    addVertex(v,G,51,new int[] {});
    addVertex(v,G,52,new int[] {33});
    addVertex(v,G,53,new int[] {});
    addVertex(v,G,54,new int[] {});
    System.out.println(G.DotTransitionTable("G"));
    
  //  System.out.println(G);
    List<DirectedEdge> AE=G.Djikstra(v[1],v[54]);
    for(DirectedEdge e:AE) System.out.println(e.toString());
  }
}
