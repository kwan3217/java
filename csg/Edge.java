package csg;

class Edge
{
  HalfEdge	he1;	// pointer to right half edge
  HalfEdge	he2;	// pointer to left half edge
  Edge		nexte;	// pointer to next edge
  Edge		preve;	// pointer to previous edge
  Solid		esolid;
  CSG		parent;	// pointer to the parent

  // Constructor
  Edge(CSG par, Solid s)
  {
    parent = par;
    parent.num_edges++;
    he1 = null;
    he2 = null;
    addList(s);
  }

  Edge(CSG par)
  {
    parent = par;
    he1 = null;
    he2 = null;
  }

  void addList(Solid s)
  {
    nexte = s.sedges;
    if (s.sedges != null)
      s.sedges.preve = this;
    preve = null;
    s.sedges = this;
    esolid = s;
  }

  void delList(Solid s)
  {
    if(preve != null)
      preve.nexte = nexte;
    if(nexte != null)
      nexte.preve = preve;
    if(s.sedges == this)
      s.sedges = nexte;
    esolid = null;
  }

  HalfEdge addhe(Vertex v, HalfEdge where, int sign)
  {
    HalfEdge	he;

    if(where.edge == null)
      he = where;
    else
      {
	he = new HalfEdge(parent, 1);
	where.prev.next = he;
	he.prev = where.prev;
	where.prev = he;
	he.next = where;
      }

    he.edge = this;
    he.vertex = v;
    he.wloop = where.wloop;
    if( sign == 0)				// Plus
      he1 = he;
    else
      he2 = he;
    return(he);
  }

  // check the orientation of null edge e
  int  checknulledge()
  {
    HalfEdge	ref, t_he;
    int		pos[], neg[];

    pos = new int[1];
    neg = new int[1];

    if(he1.strutnulledge() == 0)
      return(0);			// not a strut

    // let this = the half of e connected to other edges
    if(he1 == he2.next)
      t_he = he2;
    else
      t_he = he1;

    // ignore if not "simple" strut edge
    if(t_he.simplestrut() == 0)
      return(0);

    // try to locate a non-strut null edge from the loop of he
    if((ref = t_he.getref1()) != null)
      {
	// check whether the orientations of this and ref match
	if(ref == ref.edge.he1 && t_he == t_he.edge.he2)
	  return(0);
	if(ref == ref.edge.he2 && t_he == t_he.edge.he1)
	  return(0);
	return(1);
      }

    // try to locate a non-strut null edge around he.vtx
    if((ref = t_he.getref2()) != null)
      {
	// check whether the orientations of this and ref match
	if(ref == ref.edge.he1 && t_he == t_he.edge.he1)
	  return(0);
	if(ref == ref.edge.he2 && t_he == t_he.edge.he2)
	  return(0);
	return(1);
      }

    // all null edges of this loop were struts - let majority decide
    t_he.getvotes(pos, neg);
    if(pos[0] > neg[0])
      {
	if(t_he == t_he.edge.he2)
	  return(1);
	return(0);
      }
    else
      {
	if(t_he == t_he.edge.he2)
	  return(0);
	return(1);
      }
  }

}
