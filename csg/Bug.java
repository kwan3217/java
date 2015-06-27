package csg;

class Bug
{
  CSG      parent;
  HalfEdge buga;
  HalfEdge bugb;

  static final int NFAC = 100;

  Bug(CSG par)
  {
    parent = par;
    buga = new HalfEdge(parent, 1);
    bugb = new HalfEdge(parent, 1);
  }

  // are edges connectible
  int dreach(Nedge ne, Bug newbug)
  {
    HalfEdge  ha, hb;

    ha = buga.reach2(ne.nea);
    hb = bugb.reach2(ne.neb);

    if(ha!=null && hb!=null)
      {
	newbug.buga = ha;
	newbug.bugb = hb;
	return(1);
      }
    return(0);
  }

  // cut a dual null edge away
  void  dcut()
  {
    HalfEdge	he;
    HalfEdge	neighbor;

    he = buga;
    if(he.edge.he1.wloop == he.edge.he2.wloop)
      {
	if(parent.nfaca == NFAC)
	  {
	    System.out.println("too many intersection polygons");
	    return;
	  }
	else if(he.wloop.length < 8)
	  {
	    System.out.println("ignoring a polygon of length" + he.wloop.length);
	    return;
	  }
	else
	  parent.lonfa[parent.nfaca++] = he.wloop.lface;

	// select orientation so that the intersection polygon
	// of the "in" component becomes an inner loop in the
	// intersection face

	neighbor = he.edge.he1.next.mate();
	// swap orientation if "inner"
	if(neighbor.wloop != neighbor.wloop.lface.flout)
	  he.edge.he1.lkemr(he.edge.he2);
	else
	  he.edge.he2.lkemr(he.edge.he1);
      }
    else
      he.edge.he1.lkef(he.edge.he2);

    he = bugb;
    if(he.edge.he1.wloop == he.edge.he2.wloop)
      {
	if(parent.nfacb == NFAC)
	  {
	    System.out.println("too many intersection polygons");
	  }
	else
	  parent.lonfb[parent.nfacb++] = he.edge.he1.wloop.lface;

	neighbor = he.edge.he1.next.mate();
	if(neighbor.wloop != neighbor.wloop.lface.flout)
	  he.edge.he1.lkemr(he.edge.he2);
	else
	  he.edge.he2.lkemr(he.edge.he1);
      }
    else
      he.edge.he1.lkef(he.edge.he2);

  }

  // check whether the other half of b is still loose
  int isloose()
  {
    int	i;

    for(i=0; i<parent.nloose; i++)
      {
	if(parent.isok[i] == 0)
	  {
	    if(parent.loose[i].buga == buga.mate())
	      return(1);
	    if(parent.loose[i].bugb == bugb.mate())
	      return(1);
	  }
      }
    return(0);
  }


}
