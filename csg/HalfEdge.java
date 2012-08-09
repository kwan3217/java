package csg;

class HalfEdge
{
  static final double EPS         = 0.000001;
  static final double INTREEEPS   = 0.00005;             // edge vs. edge
  static final double CONTFVEPS   = 0.00005;             // vertex vs. face
  static final int    PLAIN       = 0;
  static final int    NEDG        = 2000;                // max null edges
  static final int    ISWIDE      = 4;
  static final int    IS180       = 8;
  static final double COLINEAREPS = 0.00004;
  static final int    OUT         = -1;
  static final int    UNION       = 1;
  static final int    OPPOSITE    = 1;
  static final int    NONOPPOSITE = 2;
  static final int    IN          = 1;
  static final int    SUCCESS     = -2;

  Edge		edge;		// pointer to parent edge
  Vertex	vertex;		// pointer to starting vertex
  Loop		wloop;		// back pointer to loop
  HalfEdge	next;		// pointer to next half edge
  HalfEdge	prev;		// pointer to previous half edge
  Tag           curv;           // curve information
  CSG		parent;		// pointer to the parent

  static final int X = 0;
  static final int Y = 1;
  static final int Z = 2;
  static double	   doublex, doubley, doublez;
  static Edge	   minedge;

  HalfEdge(CSG par, int i)
  {
    parent = par;

    if(i == 0)
      parent.num_halfedge++;

    edge = null;
    vertex = null;
    wloop = null;
    next = null;
    prev = null;
    curv = null;
  }

  HalfEdge()
  {
    edge = null;
    vertex = null;
    wloop = null;
    next = null;
    prev = null;
    curv = null;
  }

  void delList()
  {

    if (parent == null)
      System.out.println("The parent is null in delList");

    parent.num_halfedge--;
  }

  HalfEdge mate()
  {
    return((this == edge.he1) ? edge.he2 : edge.he1);
  }

  int findhe(Loop l)
  {
    HalfEdge h;

    h = l.ledge;
    do
      {
	if(h == this)
	  return(1);
      }
    while((h = h.next) != l.ledge);

    System.out.println("findhe: wrong back ptr in halfedge");
    return(0);
  }

  short eqsign(Vectors n)
  {
    Tag tagp;
    Vectors v1, v2, v3;
    int sign;

    v1 = new Vectors();
    v2 = new Vectors();
    v3 = new Vectors();

    tagp = curv;

    v3.vecMult(tagp.cp, tagp.arc_transf);
    v1.vecMinus(vertex.vcoord, v3);
    v1.normalize();
    v2.vecMinus(next.vertex.vcoord, v3);
    v2.normalize();

    v3.cross(v1, v2);
    if(v3.normalize() == 0.0)
	System.out.println("eqsign: cannot determine the orientation!");

    sign = comp(v3.dot(n), 0.0, EPS);
    if(sign == 0)
	System.out.println("eqsign: cannot determine the orientation!");

    return((short)sign);
  }

  int comp(double a, double b, double to1)
  {
    double delta;

    delta = Math.abs(a-b);
    if (delta < to1)
      return 0;
    else if (a> b)
      return 1;
    else
      return -1;
  }

  void lsettag(Tag t)
  {
    Face   f;
    Tag    oldtag;

    f = wloop.lface;
    oldtag = curv;
    curv = t;

    if(t == oldtag)
      return;

    if(t == oldtag)
      return;

    if(oldtag != null && oldtag != null && oldtag != null)
      {
	oldtag.times_used--;
	if(oldtag.times_used == 0)
	  oldtag.killtag();
      }
    if(t != null && t != null)
      t.times_used++;
  }

  // Tag he and its mate as a line

  void	line_tag()
  {
    Tag l;

    if(edge == null)
	return;

    //    l = parent.newline(++parent.maxt);
    l = new Tag(parent, 1, ++parent.maxt);
    lsettag(l);
    mate().lsettag(l);
  }

  // is h a null edge
  int  nulledge()
  {
    return(vertex.vcoord.vecSame(next.vertex.vcoord));
  }

  // Cut edge, copy tags if any
  // Only meaningful if an edge is cut (i.e., to == make(from)->nxt)
  void	extlsvme(HalfEdge to, int newvertex, float x, float y, float z)
  {
    HalfEdge	newhe;

    lmev(to, newvertex, x, y, z);
    if(curv != null)
      {
	newhe = prev;
	newhe.lsettag(curv);
	newhe.mate().lsettag(curv);
      }
  }

  void lmev(HalfEdge he2, int v, float x, float y, float z)
  {
    HalfEdge	l1;
    Vertex	newVertex;
    Edge	newEdge;

    if (vertex != he2.vertex)
      System.out.println("lmev:he1.v != he2.v");

    newEdge = new Edge(parent, wloop.lface.fsolid);
    newVertex = new Vertex(parent, wloop.lface.fsolid);

    newVertex.vertexNum = v;
    newVertex.vcoord.feq[0] = x;
    newVertex.vcoord.feq[1] = y;
    newVertex.vcoord.feq[2] = z;
    newVertex.vcoord.feq[3] = 1;

    l1 = this;

    while (l1 != he2)
      {
	l1.vertex = newVertex;
	l1 = l1.mate().next;
      }

    wloop.linkl(newEdge, he2.vertex, this, 0);
    he2.wloop.linkl(newEdge, newVertex, he2, 1);

    newVertex.vedge = he2.prev;
    he2.vertex.vedge = he2;
  }

  // locate a non-strut null edge from the loop of he or around he->vtx
  HalfEdge getref1()
  {
    HalfEdge h;

    h = prev;
    do
      {
	if(h.nulledge()!=0 && h.strutnulledge()==0)
	  return(h);
	h = h.prev;
      }
    while(h != prev);

    return(null);
  }

  HalfEdge getref2()
  {
    HalfEdge h;

    h = this;
    do
      {
	if(h != this && h.nulledge()!=0 && h.strutnulledge()==0)
	  return(h);
	h = h.mate().next;
      }
    while(h != this);

    return(null);
  }

  // count various orientations of struct null edges in the loop of he
  void	getvotes(int pos[], int neg[])
  {
    HalfEdge   h;
    int	       n, p;

    n = 0;
    p = 0;
    h = prev;
    do
      {
	if(h.nulledge() != 0)
	  {
	    if(h == h.edge.he1)
	      n++;
	    else
	      p++;
	    h = h.prev;
	  }
	h = h.prev;
      }
    while(h != prev);

    pos[0] = p;
    neg[0] = n;
  }

  int  strutnulledge()
  {
    if(wloop.length > 2 &&
       (this == mate().next || this == mate().prev))
      return(1);
    return(0);
  }

  // is he "simple", i.e. are there no other null edges connected at he->vtx ?
  int  simplestrut()
  {
    HalfEdge  h;

    h = this;
    do
      {
	if(h != this && h.nulledge()!=0)
	  return(0);		// not simple
	h = h.mate().next;
      }
    while(h != this);

    return(1);
  }

  //	Test for intersection of an edge and a face,
  //	determine intersection type (edge-edge, plain)
  int  testint(Face f, int drop, int type, double t)
  {
    Box	     b;
    Vertex   v1, v2, testv;
    double   d;
    int	     inout;

    testv = new Vertex(parent);

    b = f.fbox;
    v1 = vertex;
    v2 = next.vertex;

    doublex = v1.vcoord.feq[0] + t * (v2.vcoord.feq[0] - v1.vcoord.feq[0]);
    doubley = v1.vcoord.feq[1] + t * (v2.vcoord.feq[1] - v1.vcoord.feq[1]);
    doublez = v1.vcoord.feq[2] + t * (v2.vcoord.feq[2] - v1.vcoord.feq[2]);

    // an intersection exists if x, y, z is interior to f
    // box test
    switch(drop)
      {
      case X:	// yz-test
	if(doubley < b.ymi || doubley > b.yma)
	  return(0);
	if(doublez < b.zmi || doublez > b.zma)
	  return(0);
	break;
      case Y:	// xz-test
	if(doublex < b.xmi || doublex > b.xma)
	  return(0);
	if(doublez < b.zmi || doublez > b.zma)
	  return(0);
	break;
      case Z:	// xy-test
	if(doublex < b.xmi || doublex > b.xma)
	  return(0);
	if(doubley < b.ymi || doubley > b.yma)
	  return(0);
	break;
      }

    // perform a point-in-polygon test
    testv.vcoord.feq[0] = (float)doublex;
    testv.vcoord.feq[1] = (float)doubley;
    testv.vcoord.feq[2] = (float)doublez;

    if(type == 0)			// a vs. b
      {
	inout = f.contfv2(testv, drop);
      }
    else				// b vs. a
      {
	// don't examine vertex or edge hits in contlv()
	parent.Gtestspecials = 0;
	inout = f.contfv2(testv, drop);
	parent.Gtestspecials = 1;
      }

    if(type == 0 && (inout == 0 || inout == 1))
      {
	// test for edge-edge intersection
	d = mindist(f);

	if(-INTREEEPS < d && d < INTREEEPS)
	  {
	    if((minedge.he1.vertex.contev(minedge.he2.vertex, testv)!=0)
	       && (minedge.he1.vertex.contvv(testv)==0)
	       && (minedge.he1.vertex.contvv(testv)==0) )
	      {
		parent.hitedge = minedge;
		parent.hitvertex = null;

		return(2);
	      }
	  }
      }
    return(inout);
  }

  double mindist(Face f)
  {
    Loop	l;
    HalfEdge	he;
    Vectors	vec;
    double	vv1[], vv2[], vv3[], tmp[];
    double	d, cr, mind;

    vv1 = new double[3];
    vv2 = new double[3];
    vv3 = new double[3];
    tmp = new double[3];

    // vv2 = vector along edge of h
    vv2[0] = next.vertex.vcoord.feq[0] - vertex.vcoord.feq[0];
    vv2[1] = next.vertex.vcoord.feq[1] - vertex.vcoord.feq[1];
    vv2[2] = next.vertex.vcoord.feq[2] - vertex.vcoord.feq[2];

    mind = 1000000000;
    minedge = f.floops.ledge.edge;
    l = f.floops;
    while(l != null)
      {
	he = l.ledge;
	do
	  {
	    if(he.nulledge() != 0)
	      continue;

	    // vv1 = vector along edge of he
	    vv1[0] = he.next.vertex.vcoord.feq[0] - he.vertex.vcoord.feq[0];
	    vv1[1] = he.next.vertex.vcoord.feq[1] - he.vertex.vcoord.feq[1];
	    vv1[2] = he.next.vertex.vcoord.feq[2] - he.vertex.vcoord.feq[2];

	    vec = new Vectors();
	    vec.feq[0] = (float)vv1[0];
	    vec.feq[1] = (float)vv1[1];
	    vec.feq[2] = (float)vv1[2];
	    vec.feq[3] = 1;

	    if(vec.vecNull(EPS) != 0)
	      continue;

	    tmp[0] = vv1[1] * vv2[2] - vv1[2] * vv2[1];
	    tmp[1] = vv1[2] * vv2[0] - vv1[0] * vv2[2];
	    tmp[2] = vv1[0] * vv2[1] - vv1[1] * vv2[0];
	    vv1[0] = tmp[0];
	    vv1[1] = tmp[1];
	    vv1[2] = tmp[2];

	    vec.feq[0] = (float)vv1[0];
	    vec.feq[1] = (float)vv1[1];
	    vec.feq[2] = (float)vv1[2];
	    vec.feq[3] = 1;
	    if(vec.vecNull(EPS) != 0)
	      continue;

	    cr = Math.sqrt(vv1[0]*vv1[0] + vv1[1]*vv1[1] + vv1[2]*vv1[2]);

	    vv3[0] = vertex.vcoord.feq[0] - he.vertex.vcoord.feq[0];
	    vv3[1] = vertex.vcoord.feq[1] - he.vertex.vcoord.feq[1];
	    vv3[2] = vertex.vcoord.feq[2] - he.vertex.vcoord.feq[2];

	    vec.feq[0] = (float)vv3[0];
	    vec.feq[1] = (float)vv3[1];
	    vec.feq[2] = (float)vv3[2];
	    vec.feq[3] = 1;
	    if(vec.vecNull(EPS) != 0)
	      continue;

	    d = (vv3[0]*vv1[0]+vv3[1]*vv1[1]+vv3[2]*vv1[2]) / cr;
	    d = (d > 0.0) ? d : -d;

	    if(d < mind)
	      {
		mind = d;
		minedge = he.edge;
	      }
	  }
	while((he = he.next) != l.ledge);
	l = l.nextl;
      }
    return(mind);
  }

  // Work out edge-edge intersection
  // h1 - intersecting edge , e2 - intersected edge
  // f3 - face of intersected edge , d1a - distance of h1->vtx to f3
  void  dosetop_4(Edge e2, double d1a, Face f3)
  {
    HalfEdge	h2;	// other half of intersecting edge
    HalfEdge	h3;	// halves of intersected edges
    HalfEdge	h4;
    Face	f1;	// faces of intersecting edge
    Face	f2;
    Face	f4;	// other intersected face
    double	d1b;	// distance h1.vtx - f4
    double	d3a;	// distance h3.vtx - f1
    double	d3b;	// distance h3.vtx - f2
    HalfEdge	hx;

    // find relevant halves, faces & distances

    h2 = mate();
    h3 = (e2.he1.wloop.lface == f3) ? e2.he1 : e2.he2;
    h4 = h3.mate();
    f1 = wloop.lface;
    f2 = h2.wloop.lface;
    f4 = h4.wloop.lface;
    d1b = f4.feq.dist(vertex);
    d3a = f1.feq.dist(h3.vertex);
    d3b = f2.feq.dist(h3.vertex);

    // if the intersecting edge is coplanar with the other face,
    // process with dosetop_2()
    if(comp(d1b, 0.0, CONTFVEPS) == 0)
      {
	f4.dosetop_2(this, f4.feq.getdrop(), 0, (short)PLAIN);
	return;
      }

    // else, if the intersected edge is coplanar with either of the
    // faces of the intersecting edge, process with dosetop_2()
    if(comp(d3a, 0.0, CONTFVEPS) == 0)
      {
	f1.dosetop_2(h3, f1.feq.getdrop(), 1, (short)PLAIN);
	return;
      }
    if(comp(d3b, 0.0, CONTFVEPS) == 0)
      {
	f2.dosetop_2(h3, f2.feq.getdrop(), 1, (short) PLAIN);
	return;
      }

    // Method:
    // Check "visibility" of intersected faces from endpoints of
    // intersecting edge, and vice versa. For instance, if both
    // intersected faces are "visible" from start point of
    // intersecting face, and both intersecting faces are visible
    // from start point of intersected edge, the case
    // "intersection" holds.  Similar case analysis reveals other cases

    if((d1a * d1b > 0.0) && (d3a * d3b > 0.0))
      {
	// create "split-edge" on both edges
	if(parent.nedg == NEDG)
	  System.out.println("too many null edges");
	else
	  {
	    makesplit(d1a, doublex, doubley, doublez, 0);
	    h3.makesplit(d3a, doublex, doubley, doublez, 1);
	    parent.nedg++;
	  }
	}
    else if((d1a * d1b > 0.0) && (d3a * d3b < 0.0))
      {
	extlsvme(h2.next, ++parent.maxv,
		 (float)doublex, (float)doubley, (float)doublez);
	h3.extlsvme(h4.next, ++parent.maxv,
		    (float)doublex, (float)doubley, (float)doublez);

	if(parent.nedg == NEDG)
	  System.out.println("too many null edges");
	else
	  {
	    // select tangency face
	    if(h3.ch_fint(this) != 0)
	      parent.separ2(h3, 1, 1);
	    else
	      parent.separ2(h3.mate().next, 1, 1);

	    if(d1a < 0.0)
	      parent.separ1(mate().next, this, 0);
	    else
	      parent.separ1(this, mate().next, 0);
	    parent.nedg++;
	  }
      }
    else if((d1a * d1b < 0.0) && (d3a * d3b > 0.0))
      {
	extlsvme(h2.next, ++parent.maxv,
		 (float)doublex, (float)doubley, (float)doublez);
	h3.extlsvme(h4.next, ++parent.maxv,
		    (float)doublex, (float)doubley, (float)doublez);

	if(parent.nedg == NEDG)
	  System.out.println("too many null edges");
	else
	  {
	    // select tangency face
	    if(ch_fint(h3) != 0)
	      parent.separ2(this, 0, 1);
	    else
	      parent.separ2(mate().next, 0, 1);

	    if(d3a < 0.0)
	      parent.separ1(h3.mate().next, h3, 1);
	    else
	      parent.separ1(h3, h3.mate().next, 1);
	    parent.nedg++;
	  }
      }
    else // (d1a * d1b < 0.0) && (d3a * d3b < 0.0)
      {
	// check that faces really intersect
	if(ch_fint(h3) == 0)
	  {
	    // nonmanifold case
	    return;
	  }

	// create two "split-edges" on e and
	// two "tangency"-edges on the two faces

	if(parent.nedg == NEDG || parent.nedg == NEDG-1)
	  System.out.println("too many null edges");
	else
	  {
	    if(d1a < 0.0)
	      {
		extlsvme(mate().next, ++parent.maxv,
			 (float)doublex, (float)doubley, (float)doublez);
		hx = prev.mate();
		hx.extlsvme(hx.mate().next, ++parent.maxv,
			    (float)doublex, (float)doubley, (float)doublez);
		parent.lone[parent.nedg].nea = hx.prev.edge;
		extlsvme(mate().next, ++parent.maxv,
			 (float)doublex, (float)doubley, (float)doublez);
		parent.lone[parent.nedg+1].nea = prev.edge;
	      }
	    else
	      {
		h2.extlsvme(h2.mate().next, ++parent.maxv,
			    (float)doublex, (float)doubley, (float)doublez);
		hx = h2.prev.mate();
		hx.extlsvme(hx.mate().next, ++parent.maxv,
			    (float)doublex, (float)doubley, (float)doublez);
		parent.lone[parent.nedg].nea = hx.prev.edge;
		h2.extlsvme(h2.mate().next, ++parent.maxv,
			    (float)doublex, (float)doubley, (float)doublez);
		parent.lone[parent.nedg+1].nea = h2.prev.edge;
	      }

	    h3.extlsvme(h4.next, ++parent.maxv,
			(float)doublex, (float)doubley, (float)doublez);
	    parent.separ2(h3, 1, 1);
	    parent.nedg++;
	    hx = h3.mate().next;
	    parent.separ2(hx, 1, 1);
	    parent.nedg++;
	  }
      }
  }

  // Work out edge-vertex intersection
  void dosetop_4a(Vertex v)
  {
    extlsvme(mate().next, ++parent.maxv, v.vcoord.feq[0],
	     v.vcoord.feq[1], v.vcoord.feq[2]);

    vertex.rvvadd(v, 0, (short) PLAIN);
  }

  // Work out ordinary intersection
  void dosetop_5(Face f, double d, int type)
  {
    if(parent.nedg == NEDG || parent.nedg == NEDG-1)
      System.out.println("too many null edges");
    else
      {
	makesplit(d, doublex, doubley, doublez, type);
	f.makering(doublex, doubley, doublez, (1-type));
	parent.nedg++;
      }
  }

  // separate parts of edge
  void makesplit(double d, double x, double y, double z, int type)
  {
    HalfEdge	he2;

    if(d > 0.0)
      {
	extlsvme(mate().next, ++parent.maxv, (float)x, (float)y, (float)z);
	lmev(mate().next, ++parent.maxv, (float)x, (float)y, (float)z);
	if(type == 0)
	  parent.lone[parent.nedg].nea = prev.edge;
	else
	  parent.lone[parent.nedg].neb = prev.edge;
      }
    else
      {
	he2 = mate();
	he2.extlsvme(he2.mate().next, ++parent.maxv,
		     (float)x, (float)y, (float)z);
	he2.lmev(he2.mate().next, ++parent.maxv, (float)x, (float)y, (float)z);
	if(type == 0)
	  parent.lone[parent.nedg].nea = he2.prev.edge;
	else
	  parent.lone[parent.nedg].neb = he2.prev.edge;
      }
  }

  // Check whether faces with intersecting edges intersect
  int ch_fint(HalfEdge h2)
  {
    Vectors	in1, in2;	// vectors to inside
    double	test;

    in1 = new Vectors();
    in2 = new Vectors();

    inside(in1);
    h2.inside(in2);
    test = in1.dot(in2);

    return((test > 0.0) ? 1 : 0);
  }

  // Form a vector pointing at the inside of the face of halfedge he
  void inside(Vectors in)
  {
    Face	f;
    Vectors	eq, dir;

    eq = new Vectors();
    dir = new Vectors();

    dir.vecMinus(next.vertex.vcoord, vertex.vcoord);
    f = wloop.lface;
    if(f.haseq() == 0)
      {
	f.flout.newell(eq);
	in.cross(eq, dir);
      }
    else
      {
	in.cross(f.feq, dir);
      }
  }

  void lkemr(HalfEdge he2)
  {
    HalfEdge   h, he2nxt;
    Loop       nl;
    Loop       ol;
    Edge       killedge;
    int	       count;

    if (this != he2.mate())
      System.out.println("lkemr:he1 != he2.mate");

    if (wloop != he2.wloop)
      System.out.println("lkemr:wloop != he2.wloop");

    // clear curve tags
    if(curv != null)
      lsettag(null);
    if(he2.curv != null)
      he2.lsettag(null);

    // how many halfedges are moved ?
    count = 0;
    h = this;
    while(h != he2)
      {
	count++;
	h = h.next;
      }
    count--;

    he2nxt = he2.next;
    ol = wloop;
    killedge = edge;

    // make new loop
    nl = new Loop(parent, ol.lface);

    // close new loop
    // null new loop ?
    if((nl.length = (short)count) == 0)
      {
	he2.next = he2;
	he2.prev = he2;
	he2.edge = null;
	nl.ledge = he2;
	he2.vertex.vedge = null;
      }
    else
      {
	// normal case
	he2.prev.next = next;
	next.prev = he2.prev;
	nl.ledge = next;
	he2.vertex.vedge = next;
	he2.delList();
      }

    // null old loop ?
    if((ol.length -= (count + 2)) == 0)
      {
	next = this;
	prev = this;
	edge = null;
	ol.ledge = this;
	vertex.vedge = null;
      }
    else
      {
	// normal case
	prev.next = he2nxt;
	he2nxt.prev = prev;
	ol.ledge = he2nxt;
	vertex.vedge = he2nxt;
	delList();
      }

    // update back ptrs
    h = nl.ledge;
    do
      {
	h.wloop = nl;
	h = h.next;
      }
    while(h != nl.ledge);

    // finally, kill the edge v1-v2
    killedge.delList(ol.lface.fsolid);
  }

  // determine the orientation for a strut null edge
  int  get_orient(HalfEdge he1, HalfEdge he2)
  {
    HalfEdge   mhe1, mhe2;
    int	       retcode;

    mhe1 = he1.mate().next;
	mhe2 = he2.mate().next;

	// use reverse rules for wide sectors
	if((mhe1.wloop.lface.fbits & ISWIDE)==1 &&
	   (wloop.lface.fbits & ISWIDE)==1)
	  {
	    if(mhe1 == he2 && mhe2 == he1)
	      retcode = 1-he1.convex_edg();
	    else if(mhe1 == he2)
	      retcode = 1-he1.convex_edg();
	    else if(mhe2 == he1)
	      retcode = 1-he2.convex_edg();
	    else
	      retcode = 1-he1.convex_edg();
	  }
	else
	  {
	    if(mhe1 == he2 && mhe2 == he1)
	      retcode = he1.convex_edg();
	    else if(mhe1 == he2)
	      retcode = he1.convex_edg();
	    else if(mhe2 == he1)
	      retcode = he2.convex_edg();
	    else
	      retcode = he1.convex_edg();
	  }

	return(retcode);
  }


  //	Test convexity of the edge at he
  int	convex_edg()
  {
    HalfEdge   neighbor, h2;
    Vectors    dir, cr;
    double     test;

    dir = new Vectors();
    cr = new Vectors();

    // get direction vector of the edge
    // if he is null, take the direction vector of the next halfedge
    h2 = next;
    if(nulledge() != 0)
      h2 = h2.next;
    dir.vecMinus(h2.vertex.vcoord, vertex.vcoord);

    neighbor = mate();
    cr.cross(wloop.lface.feq, neighbor.wloop.lface.feq);

    // if the cross product vanishes, i.e. the neighbor faces
    // are coplanar, consider their angle convex

    if(cr.vecNull(COLINEAREPS) != 0)
      return(1);

    // else, if the cross product points along the edge (dir), the
    // angle of the faces is concave, otherwise it's convex

    test = dir.dot(cr);

    return((test < 0.0) ? 1 : 0);
  }

  // calculate wideness of all sectors, store in face bits
  void checkwideness()
  {
    HalfEdge   he;
    int	       w;

    he = this;
    do
      {
	w = he.sector_wide();
	if(w == 1)		// wide
	  he.wloop.lface.fbits |= ISWIDE;
	else
	  he.wloop.lface.fbits &= ~ISWIDE;
	if(w == 0)		// approx. 180 degrees
	  he.wloop.lface.fbits |= IS180;
	else
	  he.wloop.lface.fbits &= ~IS180;
	he = he.mate().next;
      }
    while(he != this);
  }

  //	Do 2 sectors intersect?
  //
  //	Method:
  //	  Sectors intersect if the intersection line of the respective
  //	  planes lies within both sectors.  Lines that lie at sector
  //	  boundariers are reclassified by grouping rules.
  //	  Hence coplanar sectors are considered not to intersect at all.
  int  sector_int(HalfEdge h2, short kind, int op,
		  int i1[], int i2[], int i3[], int i4[])
  {
    HalfEdge   h1p, h1n, h2p, h2n;
    int	       w1, w2;
    short      k;
    double     d1, d2, d3, d4;
    int	       c1, c2, c3, c4;
    int	       retcode;

    // check wideness
    w1 = wloop.lface.fbits & ISWIDE;
    w2 = h2.wloop.lface.fbits & ISWIDE;

    // calculate distances
    // ignore null edges
    h1p = prev;
    while(h1p.vertex.vcoord.vecSame(vertex.vcoord) != 0)
      h1p = h1p.prev;
    h1n = next;
    while(h1n.vertex.vcoord.vecSame(vertex.vcoord) != 0)
      h1n = h1n.next;
    h2p = h2.prev;
    while(h2p.vertex.vcoord.vecSame(h2.vertex.vcoord) != 0)
      h2p = h2p.prev;
    h2n = h2.next;
    while(h2n.vertex.vcoord.vecSame(h2.vertex.vcoord) != 0)
      h2n = h2n.next;

    d1 = h2.wloop.lface.feq.dist(h1p.vertex);
    d2 = h2.wloop.lface.feq.dist(h1n.vertex);
    d3 = wloop.lface.feq.dist(h2p.vertex);
    d4 = wloop.lface.feq.dist(h2n.vertex);

    // compare to zero
    c1 = comp(d1, 0.0, CONTFVEPS);
    c2 = comp(d2, 0.0, CONTFVEPS);
    c3 = comp(d3, 0.0, CONTFVEPS);
    c4 = comp(d4, 0.0, CONTFVEPS);

    // new ...
    if(c1 == 0 && c2 == 0 && c3 == 0 && c4 == 0)
      return(0);

    // apply grouping rules
    if(c1 == 0)
      {
	if((kind & OPPOSITE) != 0)
	  k = h1p.mate().sector_kind(h2);
	else
	  k = kind;

	if(h1p.mate().sector_within(h2, w2) != 0)
	  c1 = (op == UNION) ?
	    ((k & OPPOSITE)!=0 ? IN : OUT) :
	  ((k & OPPOSITE)!=0 ? OUT : IN) ;
	else
	  c1 = (op == UNION) ?
	    ((k & OPPOSITE)!=0 ? OUT : IN) :
	  ((k & OPPOSITE)!=0 ? IN : OUT) ;
      }
    if(c2 == 0)
      {
	if((kind & OPPOSITE) != 0)
	  k = sector_kind(h2);
	else
	  k = kind;
	if(sector_within(h2, w2) != 0)
	  c2 = (op == UNION) ?
	    ((k & OPPOSITE)!=0 ? IN : OUT) :
	  ((k & OPPOSITE)!=0 ? OUT : IN) ;
	else
	  c2 = (op == UNION) ?
	    ((k & OPPOSITE)!=0 ? OUT : IN) :
	  ((k & OPPOSITE)!=0 ? IN : OUT) ;
      }
    if(c3 == 0)
      {
	if((kind & OPPOSITE) != 0)
	  k = h2p.mate().sector_kind(this);
	else
	  k = kind;
	if(h2p.mate().sector_within(this, w1) != 0)
	  c3 = (op == UNION) ?
	    ((k & OPPOSITE)!=0 ? IN : IN) :
	    ((k & OPPOSITE)!=0 ? OUT : OUT) ;
	else
	  c3 = (op == UNION) ?
	    ((k & OPPOSITE)!=0 ? OUT : OUT) :
	    ((k & OPPOSITE)!=0 ? IN : IN) ;
      }
    if(c4 == 0)
      {
	if((kind & OPPOSITE) != 0)
	  k = h2.sector_kind(this);
	else
	  k = kind;
	if(h2.sector_within(this, w1) != 0)
	  c4 = (op == UNION) ?
	       ((k & OPPOSITE)!=0 ? IN : IN) :
	       ((k & OPPOSITE)!=0 ? OUT : OUT) ;
	else
	  c4 = (op == UNION) ?
	       ((k & OPPOSITE)!=0 ? OUT : OUT) :
	       ((k & OPPOSITE)!=0 ? IN : IN) ;
      }

    i1[0] = c1;
    i2[0] = c2;
    i3[0] = c3;
    i4[0] = c4;

    // are both sectors wide ?
    if(w1!=0 && w2!=0)
      {
	retcode = sector_test(h2, w1, w2);
      }
    // else, if one or the other wide ?
    else if(w1 != 0)
      {
	if(c3 == -c4)
	  {
	    if(c1 == c2)
	      {
		i1[0] = -c3;
		i2[0] = -c4;
		retcode = 1;
	      }
	    else
	      retcode = sector_test(h2, w1, w2);
	  }
	else
	  retcode = 0;
      }
    else if(w2 != 0)
      {
	if(c1 == -c2)
	  {
	    if(c3 == c4)
	      {
		i3[0] = -c1;
		i4[0] = -c2;
		retcode = 1;
	      }
	    else
	      retcode = h2.sector_test(this, w2, w1);
	  }
	else
	  retcode = 0;
      }
    // both are small
    else
      {
	if((c1 == -c2) && (c3 == -c4))
	  retcode = sector_test(h2, w1, w2);
	else
	  retcode = 0;
      }
    return(retcode);
  }

  //	Is sector wider than 180 degrees ?
  //
  //	Return:
  //	   1 for > 180 degrees
  //	   0 for ~ 180 degrees
  //	  -1 for < 180 degrees

  int  sector_wide()
  {
    Vectors    cr, dir1, dir2;
    HalfEdge   hep, hen;

    cr = new Vectors();
    dir1 = new Vectors();
    dir2 = new Vectors();

    // ignore null edges
    hep = prev;
    while(hep.vertex.vcoord.vecSame(vertex.vcoord) != 0)
      hep = hep.prev;
    hen = next;
    while(hen.vertex.vcoord.vecSame(vertex.vcoord) != 0)
      hen = hen.next;

    dir1.vecMinus(hen.vertex.vcoord, vertex.vcoord);
    dir2.vecMinus(hep.vertex.vcoord, vertex.vcoord);

    cr.cross(dir1, dir2);

    if(cr.vecNull(COLINEAREPS) != 0)
      return(0);

    return((cr.dot(wloop.lface.feq) < 0.0) ? 1 : -1);
  }

  //	Is edge h1 within sector of h2 ?
  int	sector_within(HalfEdge h2, int iswide)
  {
    Vectors	dir;		// direction of tested edge
    int		retcode;

    dir = new Vectors();

    // direction of test edge
    dir.vecMinus(next.vertex.vcoord, vertex.vcoord);

    retcode = h2.sector_within2(dir, iswide);

    return(retcode);
  }

  //	  Calculate direction vector of the intersection line of the
  //	  sectors.  If the line is within both sectors, they intersect.

  int	sector_test(HalfEdge h2, int w1, int w2)
  {
    Vectors	intrs;
    int		c1, c2, retcode;
    Face	f1, f2;

    intrs = new Vectors();

    // get normal vectors of the sectors
    f1 = wloop.lface;
    f2 = h2.wloop.lface;

    // intersection line = cross product of the normals
    intrs.cross(f1.feq, f2.feq);

    // if the calculated intersection line is a null vector, the
    // sectors are coplanar => no intersection

    if(f1.equaleqs(f2)!=0 || f1.oppositeeqs(f2)!=0)
      {
	retcode = 0;
      }
    else
      {
	// else, check the containment of intrs with sector_within2
	c1 = sector_within2(intrs, w1);
	c2 = h2.sector_within2(intrs, w2);

	// if the line is within both sectors they intersect
	if(c1!=0 && c2!=0)
	  retcode = 1;
	// else, if the line is within the complements, the
	// sectors intersect
	else if(c1==0 && c2==0)
	  retcode = 1;
	else
	  retcode = 0;
      }

    return(retcode);
  }

  // check type of coplanarity of a sector
  short	sector_kind(HalfEdge h2)
  {
    HalfEdge h;
    Face     f2;
    short    retcode;

    retcode = NONOPPOSITE;
    f2 = h2.wloop.lface;
    h = this;
    do
      {
	if(f2.oppositeeqs(h.wloop.lface) != 0)
	  {
	    retcode = OPPOSITE;
	    break;
	  }
	h = h.mate().next;
      }
    while(h != this);

    return(retcode);
  }

  //	Is direction dir within sector h2 ?
  int	sector_within2(Vectors dir, int iswide)
  {
    HalfEdge  r1, r2;
    Vectors   ref1;		// reference edge 1
    Vectors   ref2;		// reference edge 2
    Vectors   refcr;		// ... their cross product
    Vectors   in;		// vector towards inside of sector
    Vectors   c1;		// cross products
    Vectors   c2;
    double    test1, test2;
    int	      itest1, itest2;
    int	      retcode;

    ref1 = new Vectors();
    ref2 = new Vectors();
    refcr = new Vectors();
    in = new Vectors();
    c1 = new Vectors();
    c2 = new Vectors();

    // beware of colinear sector edges
    if((wloop.lface.fbits & IS180) != 0)
      {
	// make vector pointing inside the sector
	inside(in);

	test1 = dir.dot(in);
	retcode = ( (comp(test1, 0.0, EPS) >= 0) ? 1 : 0 );
      }
    else
      {
	// get vectors along sector boundaries ref1, ref2
	// beware of null edges
	r1 = prev;
	while(r1.vertex.vcoord.vecSame(vertex.vcoord) != 0)
	  r1 = r1.prev;
	r2 = next;
	while(r2.vertex.vcoord.vecSame(vertex.vcoord) != 0)
	  r2 = r2.next;
	ref1.vecMinus(r1.vertex.vcoord, vertex.vcoord);
	ref2.vecMinus(r2.vertex.vcoord, vertex.vcoord);

	c1.cross(dir, ref1);
	c2.cross(ref2, dir);

	if(c1.vecNull(COLINEAREPS) != 0)
	  {
	    retcode = (iswide!=0 ? 1 : ((ref1.dot(dir) > 0.0) ? 1 : 0));
	  }
	else if(c2.vecNull(COLINEAREPS) != 0)
	  {
	    retcode = (iswide!=0 ? 1 : ((ref2.dot(dir) > 0.0) ? 1 : 0));
	  }
	else
	  {
	    refcr.cross(ref2, ref1);
	    test1 = c1.dot(refcr);
	    test2 = c2.dot(refcr);
	    itest1 = comp(test1, 0.0, EPS);
	    itest2 = comp(test2, 0.0, EPS);

	    if(itest1 == 0)
	      itest1 = itest2;
	    if(itest2 == 0)
	      itest2 = itest1;

	    retcode = (iswide!=0 ? ((itest1<0 || itest2<0) ? 1 : 0) :
		       ((itest1>0) && (itest2>0) ? 1 : 0));
	  }
      }
    return(retcode);
  }

  //	Can halfedge h1 connect with edge e2 (i.e. does e2 have a
  //	matching halfedge in the same face) ? If so, return the matching
  //	halfedge of e2, else return NIL.

  HalfEdge reach2(Edge e2)
  {

    // check matching half of e2
    if(this == edge.he1)
      {
	if(wloop.lface == e2.he2.wloop.lface)
	  return(e2.he2);
      }
    else
      {
	if(wloop.lface == e2.he1.wloop.lface)
	  return(e2.he1);
	}
    return(null);
  }

  //	Connect two null edges h1, h2 with new edges
  //
  //	Assumptions:
  //	- h1, h2 occur in the same face
  //	- h1, h2 "match", i.e. h1 is oriented negatively, h2 positively

  void	join2(HalfEdge h2)
  {
    Face  fac, newf;
    Loop  l;
    int	  be_wary;

    fac = wloop.lface;

    // connect the tail of h1 to the head of h2

    // do h1, h2 occur in the same loop ?
    if(wloop == h2.wloop)
      {
	// check that they are not connected yet
	if(prev.prev != h2)
	  {
	    // if an "inner" face is created, be wary as
	    // for its orientation

	    be_wary = ((l = wloop) != fac.flout) ? 1 : 0;

	    newf = extlmef(h2.next, ++parent.maxf);

	    if(be_wary != 0)
	      l.check_orientation(newf);
	  }
	else
	  newf = null;
      }
    // else h1, h2 are in different loops
    else
      {
	lmekr(h2.next);
	newf = null;
      }

    // connect the head of h1 to the tail of h2

    // check whether h1, h2 are already connected
    if(next.next != h2)
      {
	h2.extlmef(next, ++parent.maxf);

	//   if a non-null new face was created, check the
	//   containment of all inner loops of fac

	if(newf!=null && fac.floops.nextl!=null)
	  fac.laringmv(newf);
      }
  }

  //	Do lmef(), copy tag from the old face to the new face
  Face extlmef(HalfEdge h2, int fno)
  {
    Face  oldf, newf;

    oldf = wloop.lface;
    newf = lmef(h2, fno);

    if(oldf.surf != null)
      newf.lsettag(oldf.surf);

    return(newf);
  }

  // are halfedges he1, he2 joinable ?
  int breach(HalfEdge he2)
  {
    if(this == he2.next || he2 == next)
      return(0);

    // bugs are joinable if have opposite orientations and
    // occur in the same face
    if(((this == edge.he1 && he2 == he2.edge.he2) ||
	(this == edge.he2 && he2 == he2.edge.he1)) &&
       (wloop.lface == he2.wloop.lface))
      {
	return(1);
      }
    return(0);
  }

  int canrepair1(HalfEdge he2)
  {
    // he1 and he2 are repairable if the end vertex of he2 occurs in the
    // face of he1
    return(he2.mate().vertexlookup(this));
  }

  int canrepair2(HalfEdge he2)
  {
    // he1 and he2 are repairable if the start vertex of he2 occurs in the
    // face of he1
    return(he2.vertexlookup(this));
  }

  int dorepair1(HalfEdge he2)
  {
    HalfEdge	newhe=null, oldhe=null;

    // fix he2 by forcing it to occur in the face of he1
    while(he2.wloop.lface != wloop.lface)
      {
	// rotate he2 aroung its end vertex towards the face of he1

	// make temporary face
	he2.lmef(he2.next.next, parent.maxf+1);
	newhe = he2.prev;

	oldhe = he2.next.mate();
	// if oldhe is a null edge, update loose
	if(oldhe.nulledge()!=0)
	  oldhe.updateloose(newhe);

	// delete it this way so as to preserve tags
	he2.next.mate().lkef(he2.next);
      }
    return(SUCCESS);
  }

  int dorepair2(HalfEdge he2)
  {
    HalfEdge	newhe, oldhe;

    // fix he2 by forcing it to occur in the face of he1
    while(he2.wloop.lface != wloop.lface)
      {
	// rotate he2 around its start vertex towards the face of he1

	// make temporary face
	he2.prev.lmef(he2.next, parent.maxf+1);
	newhe = he2.next;

	oldhe = he2.prev.mate();
	// if oldhe is a null edge, update loose
	if(oldhe.nulledge()!=0)
	  oldhe.updateloose(newhe);

	// delete it this way so as to preserve tags
	he2.prev.mate().lkef(he2.prev);
      }
    return(SUCCESS);
  }

  int canrepair3(HalfEdge he2, HalfEdge he[])
  {
    HalfEdge	ha, hb;

    ha = he2.prev;
    hb = he2.next.next;

    // null edge ?
    if(ha.vertex.vcoord.feq[0] == hb.vertex.vcoord.feq[0] &&
       ha.vertex.vcoord.feq[1] == hb.vertex.vcoord.feq[1] &&
       ha.vertex.vcoord.feq[2] == hb.vertex.vcoord.feq[2])
      {
	if(canrepair2(ha)!=0)
	  {
	    he[0] = ha;
	    he[1] = hb;
	    return(1);
	  }
      }
    return(0);
  }

  int dorepair3(HalfEdge he2, HalfEdge he[])
  {
    HalfEdge	newhe, oldhe;

    // fix he2a by forcing it to occur in the face of he1
    //while(he2a.wloop.lface != he1.wloop.lface)
    while(he[0].wloop.lface != wloop.lface)
      {
	// move edges of he2a to he2b until he2a
	// occurs in the face of he1

	// make temporary face
	he[0].prev.lmef(he[1], parent.maxf+1);
	newhe = he[1].prev.mate();

	oldhe = he[0].prev.mate();
	// if oldhe is a null edge, update loose
	if(oldhe.nulledge() != 0)
	  oldhe.updateloose(newhe);

	// delete it this way so as to preserve tags
	he[0].prev.mate().lkef(he[0].prev);
	he[1] = he[1].prev.mate();
      }
    return(SUCCESS);
  }

  int canrepair4(HalfEdge he2, HalfEdge he[])
  {
    HalfEdge	ha, hb;

    ha = he2.next.next;
    hb = he2.prev;

    // null edge ?
    if(ha.vertex.vcoord.feq[0] == hb.vertex.vcoord.feq[0] &&
       ha.vertex.vcoord.feq[1] == hb.vertex.vcoord.feq[1] &&
       ha.vertex.vcoord.feq[2] == hb.vertex.vcoord.feq[2])
      {
	if(canrepair2(ha) != 0)
	  {
	    he[0] = ha.prev;
	    he[1] = hb;
	    return(1);
	  }
      }
    return(0);
  }

  int dorepair4(HalfEdge he2, HalfEdge he[])
  {
    HalfEdge	newhe, oldhe;

    // fix he2a by forcing it to occur in the face of he1
    while(he[0].wloop.lface != wloop.lface)
      {
	// move edges of he2a to he2b until he2a
	// occurs in the face of he1

	// make temporary face
	he[1].lmef(he[0].next.next, parent.maxf+1);
	newhe = he[1].prev.mate();

	oldhe = he[0].next.mate();
	// if oldhe is a null edge, update loose
	if(oldhe.nulledge() != 0)
	  oldhe.updateloose(newhe);

	// delete it this way so as to preserve tags
	he[0].next.mate().lkef(he[0].next);
      }
    return(SUCCESS);
  }

  void updateloose(HalfEdge newhe)
  {
    int 	i;

    for(i=0; i<parent.nloose; i++)
      {
	if(parent.loose[i].buga == this)
	  {
	    parent.loose[i].buga = newhe;
	    return;
	  }
	if(parent.loose[i].buga == mate())
	  {
	    parent.loose[i].buga = newhe.mate();
	    return;
	  }
	if(parent.loose[i].bugb == this)
	  {
	    parent.loose[i].bugb = newhe;
	    return;
	  }
	if(parent.loose[i].bugb == mate())
	  {
	    parent.loose[i].bugb = newhe.mate();
	    return;
	  }
      }
  }

  /* does he1->vtx occur in the face of he2 ? */
  int vertexlookup(HalfEdge he2)
  {
    HalfEdge	hscn;

    hscn = this;
    do
      {
	if(hscn.wloop.lface == he2.wloop.lface)
	  return(1);
	hscn = hscn.mate().next;
      }
    while(hscn != this);
    return(0);
  }

  // do he1.vertex and he2.vertex have a common face ?
  // if yes, return the corresponding halfedges
  int facelookup(HalfEdge he2, HalfEdge che[])
  {
    HalfEdge	scn1, scn2;

    scn1 = this;
    do
      {
	scn2 = he2;
	do
	  {
	    if(scn1.wloop.lface == scn2.wloop.lface)
	      {
		che[0] = scn1;
		che[1] = scn2;
		return(1);
	      }
	    scn2 = scn2.mate().next;
	  }
	while(scn2 != he2);
	scn1 = scn1.mate().next;
      }
    while(scn1 != this);
    return(0);
  }

  Face lmef(HalfEdge he2, int f)
  {
    Face        oldFace;
    Solid       oldSolid;
    Face	newFace;
    Loop        oldLoop;
    Loop	newLoop;
    Edge	newEdge;
    HalfEdge	nhe1, nhe2;

    oldLoop = wloop;
    oldFace = oldLoop.lface;
    oldSolid = oldFace.fsolid;

    if (wloop != he2.wloop)
      System.out.println("lmef:he1.wloop != he2.wloop");

    if (oldLoop.findl(oldFace) != 1)
      System.out.println("lmef:findl_1 != 1");

    if (oldSolid.findf(oldFace) != 1)
      System.out.println("lmef:findl_2 != 1");

    // make new face and loop
    newFace = new Face(parent, oldSolid);
    newFace.faceNum = f;
    newLoop = new Loop(parent, newFace);
    newFace.flout = newLoop;

    // move edges from he1 to he2 into new loop
    nhe1 = this;
    newLoop.ledge = this;
    newLoop.length = 0;
    while(nhe1 != he2)
      {
	// update back ptrs
	nhe1.wloop = newLoop;
	nhe1 = nhe1.next;
	newLoop.length++;
	oldLoop.length--;
      }

    // make the new edge
    newEdge = new Edge(parent, oldSolid);

    // add the new edge to both loops
    if (this == he2)
      {
	nhe2 = new HalfEdge(parent, 1);

	// a "circular" new face
	nhe1 = new HalfEdge(parent, 0);
	newLoop.ledge = new HalfEdge(parent, 0);

	nhe1.prev = nhe1;
	nhe1.next = nhe1;
	nhe1.vertex = vertex;
	nhe1.edge = newEdge;
	nhe1.wloop = newLoop;

	newEdge.he2 = nhe1;

	newLoop.length = 1;

	oldLoop.linkl(newEdge, he2.vertex, this, 1);

	vertex.vedge = this;
      }
    else
      {
	// normal case

	// add edge vert2-vert1 to new loop
	nhe1 = new HalfEdge(parent, 0);

	nhe1.vertex = he2.vertex;
	nhe1.edge = newEdge;
	nhe1.next = this;
	nhe1.prev = he2.prev;
	he2.prev.next = nhe1;

	// add edge vert1-vert2 to old loop
	nhe2 = new HalfEdge(parent, 0);

	nhe2.vertex = vertex;
	nhe2.edge = newEdge;
	nhe2.next = he2;
	nhe2.prev = prev;
	prev.next = nhe2;

	prev = nhe1;
	he2.prev = nhe2;

	nhe1.wloop = newLoop;
	nhe2.wloop = oldLoop;

	newLoop.length++;
	oldLoop.length++;

	// adjust back ptrs of edge to show orientation
	newEdge.he1 = nhe2;
	newEdge.he2 = nhe1;
      }

    // make sure that old face will point to the correct loop

    oldLoop.ledge = he2;

    if (nhe1.findhe(newLoop) != 1)
      System.out.println("lmef:findhe_1 != 1");

    if (nhe2.findhe(oldLoop) != 1)
      System.out.println("lmef:findhe_2 != 1");

    if (findhe(newLoop) != 1)
      System.out.println("lmef:findhe_3 != 1");

    if (he2.findhe(oldLoop) != 1)
      System.out.println("lmef:findhe_4 != 1");

    if (newLoop.findl(newFace) != 1)
      System.out.println("lmef:findhe_5 != 1");

    if (oldLoop.findl(oldFace) != 1)
      System.out.println("lmef:findhe_6 != 1");

    return newFace;
  }

  // Kill Edge, Face
  // kill edge v1 -> v2; kill the other face connected with it
  // and move its loop to face fno
  void lkef(HalfEdge he2)
  {
    Face		f1, f2;
    Loop                l, l1, l2;
    HalfEdge            he, he3;

    if(this != he2.mate())
      System.out.println("he1 != he2.mate() in lkef");
    if(wloop == he2.wloop)
      System.out.println("he1.wloop == he2.wloop in lkef");
    if(wloop.lface == he2.wloop.lface)
      System.out.println("he1.wloop.lface == he2.wloop.lface in lkef");
    if(he2.vertex != next.vertex)
      System.out.println("he2.vertex != he1.next.vertex in lkef");
    if(vertex != he2.next.vertex)
      System.out.println("he1.vertex != he2.next.vertex in lkef");

    // kill curve tags
    if(curv != null)
      lsettag(null);
    if(he2.curv != null)
      he2.lsettag(null);
    // kill surface tag
    if(he2.wloop.lface.surf != null)
      he2.wloop.lface.lsettag(null);

    // get facs and loops
    l1  = wloop;
    f1  = l1.lface;
    l2  = he2.wloop;
    f2  = l2.lface;

    // move loops of face f2 into face f1
    l = f2.floops;
    while(l != null)
      {
	l.delList(f2);
	l.linkNode(f1);
	l = f2.floops;
      }

    // update back pointers of l2
    he = l2.ledge;

    do
      {
	he.wloop = l1;
	he = he.next;
      }
    while(he != l2.ledge);

    // link the loops together via he1 and he2
    l1.length += l2.length - 2 ;
    if(l1.length == 0)
      {
	// l1 shrinks to null
	next = this;
	prev = this;
	vertex.vedge = null;
      }
    else
      {
	prev.next = he2.next;
	he2.next.prev = prev;
	he2.prev.next = next;
	next.prev = he2.prev;
	vertex.vedge = he2.next;
	he2.vertex.vedge = next;
      }

    // make sure that l1 points to an existing halfedge
    if(l1.ledge == this)
      l1.ledge = next;

    // finally remove unnecessary objects
    f2.delList(f1.fsolid);
    l2.delList(f1);
    edge.delList(f1.fsolid);

    if(l1.length > 0)
      delList();
    else
      edge = null;
    he2.delList();
  }

  void lmekr(HalfEdge he2)
  {
    HalfEdge	nhe1, nhe2;
    Edge	nedge;
    Loop	l1, l2;
    Face        f;

    if (wloop == he2.wloop)
      {
	System.out.println("he1.wloop == he2.wloop in lmekr");
	return;
      }
    if (wloop.lface != he2.wloop.lface)
      {
	System.out.println("he1.wloop.lface != he2.wloop.lface in lmekr");
	return;
      }

    l1 = wloop;
    l2 = he2.wloop;
    f = l1.lface;

    // update back pointers of loop of l2
    nhe1 = l2.ledge;
    do
      {
	nhe1.wloop = l1;
	nhe1 = nhe1.next;
      }
    while(nhe1 != l2.ledge);

    // create new edge connecting v1 and v2
    nedge = new Edge(parent, f.fsolid);

    nhe1 = null;
    nhe2 = null;

    // link the two loops together via v1 -> v2

    // are both old loops null ?
    if(l1.length == 0 && l2.length == 0)
      {
	// use old halfedges
	next = he2;
	prev = he2;
	he2.next = this;
	he2.prev = this;
	edge = nedge;
	he2.edge = nedge;
	nedge.he1 = this;
	nedge.he2 = he2;
	vertex.vedge = this;
	he2.vertex.vedge = he2;
      }
    // else, is either l1 or l2 null ?
    else if(l1.length == 0)
      {
	nhe1 = new HalfEdge(parent, 0);
	nhe1.edge = nedge;
	edge = nedge;
	nhe1.vertex = he2.vertex;
	he2.prev.next = nhe1;
	nhe1.prev = he2.prev;
	prev = nhe1;
	nhe1.next = this;
	he2.prev = this;
	next = he2;
	nedge.he1 = this;
	nedge.he2 = nhe1;
	vertex.vedge = this;
      }
    else if(l2.length == 0)
      {
	nhe2 = new HalfEdge(parent, 0);
	nhe2.edge = nedge;
	he2.edge = nedge;
	nhe2.vertex = vertex;
	prev.next = nhe2;
	nhe2.prev = prev;
	he2.prev = nhe2;
	nhe2.next = he2;
	prev = he2;
	he2.next = this;
	nedge.he1 = nhe2;
	nedge.he2 = he2;
	he2.vertex.vedge = he2;
      }
    else
      {
	// "normal" case: both are nonnull
	nhe1 = new HalfEdge(parent,0);
	nhe1.edge = nedge;
	nhe1.vertex = vertex;
	nhe1.prev = prev;
	nhe1.next = he2;

	nhe2 = new HalfEdge(parent, 0);
	nhe2.edge = nedge;
	nhe2.vertex = he2.vertex;
	nhe2.prev = he2.prev;
	nhe2.next = this;

	prev.next = nhe1;
	he2.prev.next = nhe2;

	prev = nhe2;
	he2.prev = nhe1;

	nedge.he1 = nhe1;
	nedge.he2 = nhe2;
      }

    l1.length += l2.length + 2;

    // adjust back ptrs
    if(nhe1 != null)
      nhe1.wloop = l1;
    if(nhe2 != null)
      nhe2.wloop = l1;

    // adjust "outer" info
    if(f.flout == l2)
      f.flout = l1;

    // remove unnecessary loop
    l2.delList(l2.lface);
  }

  void	ljvke(HalfEdge he2)
  {
    HalfEdge	h1;
    HalfEdge	nh1, nh2;
    Vertex	delvtx;

    if(this != he2.mate())
      System.out.println("he1 and mate of he2 are not equal in ljvke");
    if(vertex == he2.vertex)
      System.out.println("he1.vertex and he2.vertex are equal in ljvke");

    // clear curve tags
    if(curv != null)
      lsettag(null);
    if(he2.curv != null)
      he2.lsettag(null);

    h1 = he2.next;
    while(h1 != this)
      {
	h1.vertex = he2.vertex;
	h1 = h1.mate().next;
      }

    prev.next = next;
    next.prev = prev;
    he2.prev.next = he2.next;
    he2.next.prev = he2.prev;
    wloop.length--;
    he2.wloop.length--;
    wloop.ledge = next;
    he2.wloop.ledge = he2.next;

    he2.vertex.vedge = he2.next;

    // the case of null result loop
    if(he2.wloop.length == 0)
      {
	he2.prev = he2.next = he2;
	he2.edge = null;
	he2.vertex.vedge = null;
	he2.wloop.ledge = he2;
      }
    else
      {
	he2.delList();
      }
    delList();

    edge.delList(wloop.lface.fsolid);
    vertex.delList(wloop.lface.fsolid);
  }

  // check uniqueness of mef representation in undo log
  int	uniqmef(HalfEdge he2)
  {
    HalfEdge	he3;

    he3 = he2.next;
    while(he3.vertex != he2.vertex)
      he3 = he3.next;
    return((he2 == he3) ? 1 : 0);
  }

}
