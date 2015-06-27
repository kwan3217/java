package csg;

class Face
{
  int	    faceNum;	        // face identifier
  Solid	    fsolid;		// back pointer to solid
  Loop	    flout;		// pointer to outer loop
  Loop	    floops;		// pointer to list of loops
  Vectors   feq;  	        // face equation
  Box       fbox;               // Enclosing box
  Tag       surf;               // surface information
  short     fbits;              // visibility and status info
  Face	    nextf;		// pointer to next face
  Face	    prevf;		// pointer to previous face
  CSG	    parent;		// pointer to the parent class

  static final double EPS         = 0.000001;
  static final double CONTBVEPS   = 20.0 * EPS;
  static final double EPSPARAM    = 0.00001;
  static final double COLINEAREPS = 0.00004;    // colinearity test
  static final double CONTFVEPS   = 0.00005;    // vertex vs. face
  static final int    REL_F       = 1000;       // max. relevant faces on the first solid
  static final int    REL_FB      = 10000;      // max. relevant face pairs
  static final int    REL_FV      = 1000;       // max. relevant vertices on face
  static final int    REL_VV      = 1000;       // max. coincident vertices
  static final int    MAX_REL_INT = 30;         // max. no of intersections
  static final int    PLAIN       = 0;
  static final int    MAX_EOPDONE = 200;

  static final int OPPOSITE = 1;

  static final int VISIBLE = 1;               // bit 0
  static final int VISIBLE1 = 16;             // bit 1
  static final int VISIBLE2 = 32;             // bit 2
  static final int VISIBLE3 = 64;             // bit 3
  static final int VISIBLE4 = 128;            // bit 4

  static final int X = 0;         // coordinate plane names
  static final int Y = 1;
  static final int Z = 2;

  static final int LOOP = 3;

  static HalfEdge from1, to1;

  // Constructor
  Face(CSG par, Solid s)
  {
    flout = null;
    floops = null;
    surf = null;
    fbox = null;
    fbits = 0;
    fbits |= VISIBLE;
    fbits |= VISIBLE1;
    fbits |= VISIBLE2;
    fbits |= VISIBLE3;
    fbits |= VISIBLE4;

    parent = par;
    feq = new Vectors();
    fbox = new Box(parent);
    parent.num_faces++;
    addList(s);
  }

  Face(CSG par)
  {
    flout = null;
    floops = null;
    surf = null;
    fbox = null;
    fbits = 0;
    fbits |= VISIBLE;
    fbits |= VISIBLE1;
    fbits |= VISIBLE2;
    fbits |= VISIBLE3;
    fbits |= VISIBLE4;

    feq = new Vectors();
    parent = par;
  }

  void addList(Solid s)
  {
    fsolid = s;
    nextf = s.sfaces;
    if (s.sfaces != null)
      s.sfaces.prevf = this;
    prevf = null;
    s.sfaces = this;
  }

  void linkNode(Solid s)
  {
    nextf = s.sfaces;
    if (s.sfaces != null)
      s.sfaces.prevf = this;
    prevf = null;
    s.sfaces = this;
    fsolid = s;
  }

  void delList(Solid s)
  {
    parent.num_faces--;
    if(prevf != null)
      prevf.nextf = nextf;
    if(nextf != null)
      nextf.prevf = prevf;
    if(s.sfaces == this)
      s.sfaces = nextf;
    if(fbox != null)
      fbox = null;
  }

  HalfEdge fhe(int vn1, int vn2)
  {
    Loop	l;
    HalfEdge	h;

    for (l=floops; l!=null; l=l.nextl)
      {
	h = l.ledge;
	do
	  {
	    if (h.vertex.vertexNum == vn1 && h.next.vertex.vertexNum == vn2)
	      return h;
	  }
	while ((h=h.next) != l.ledge);
      }
    return null;
  }

  HalfEdge sfhe(int vn1)
  {
    Loop	l;
    HalfEdge	h;

    for (l=floops; l!=null; l=l.nextl)
      {
	h = l.ledge;
	do
	  {
	    if (h.vertex.vertexNum == vn1)
	      return h;
	  }
	while ((h=h.next) != l.ledge);
      }
    return null;
  }

  int push()
  {
    Flist temp;

    temp = new Flist(parent);
    temp.face = this;
    temp.prev = null;
    temp.next = parent.fstack;
    parent.fstack = temp;
    return 0;
  }

  void facels(int sw)
  {
    Loop        l;
    Vertex      v;
    Edge        e;
    HalfEdge    he;
    int         i;
    int         breakline;
    Vectors eq = new Vectors();

    breakline = 8;
    if(sw > 2) breakline = 3;

    System.out.println("Face no. " + faceNum +  ":    ");

    l = floops;
    while(l != null)
      {
        he = l.ledge;
        i = 0;
        do
	  {
            v = he.vertex;
            e = he.edge;
            if(sw > 2 && e != null)
	      {
		if(he == e.he1)
		  System.out.println(v.vertexNum + " -(+(" +
                  e.he1.vertex.vertexNum + "," + e.he2.vertex.vertexNum
                  + "))-> ");
		else
                  System.out.println(v.vertexNum + " -(-(" +
                  e.he1.vertex.vertexNum + "," +
                  e.he2.vertex.vertexNum + "))->");
	      }
            else
	      System.out.println(v.vertexNum + " --> ");
	    he = he.next;
	  }
        while(he != l.ledge);
        if(l == flout)
	  System.out.println("<outer>");
        else
	  System.out.println("<inner>");

        l = l.nextl;
        if(l != null)
	  System.out.println("        ");
      }
  }

  Loop floop(int vn, HalfEdge h[])
  {
    HalfEdge he;	// edges of loop
    Loop     l;

    l = floops;
    while(l != null)
      {
	he = l.ledge;
	do
	  {
	    if(he.vertex.vertexNum == vn)
	      {
		h[0] = he;
		return(l);
	      }
	    he = he.next;
	  }
	while(he != l.ledge);
	l = l.nextl;
      }
    return(null);
  }

  Loop fledg(int v1, int v2, HalfEdge ptr[])
  {
    HalfEdge  he;
    Loop      l;

    l = floops;
    while(l != null)
      {
	he = l.ledge;
	do
	  {
	    if(he.vertex.vertexNum == v1 &&
	       he.next.vertex.vertexNum == v2)
	      {
		if (he.wloop == null)
		  System.out.println("wloop null in fledg");
		ptr[0] = he;
		return(l);
	      }
	    he = he.next;
	  }
	while(he != l.ledge);
	l = l.nextl;
      }
    System.out.println("returning null from fledg");
    return(null);
  }

  void	lsettag(Tag t)
  {
    HalfEdge   he;
    Tag        oldtag;

    he = flout.ledge;
    oldtag = surf;
    surf = t;

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

  // calculate box of a face
  void	facebox()
  {
    Box	      b;
    Vertex    v;
    Loop      l;
    HalfEdge  he;

    if (fbox == null)
      fbox = new Box(parent, this);
    b = fbox;

    b.xmi = flout.ledge.vertex.vcoord.feq[0];
    b.xma = flout.ledge.vertex.vcoord.feq[0];
    b.ymi = flout.ledge.vertex.vcoord.feq[1];
    b.yma = flout.ledge.vertex.vcoord.feq[1];
    b.zmi = flout.ledge.vertex.vcoord.feq[2];
    b.zma = flout.ledge.vertex.vcoord.feq[2];

    l = floops;
    while(l != null)
      {
	he = l.ledge;
	do
	  {
	    v = he.vertex;
	    if(v.vcoord.feq[0] < b.xmi)
	      b.xmi = v.vcoord.feq[0];
	    if(v.vcoord.feq[1] < b.ymi)
	      b.ymi = v.vcoord.feq[1];
	    if(v.vcoord.feq[2] < b.zmi)
	      b.zmi = v.vcoord.feq[2];
	    if(v.vcoord.feq[0] > b.xma)
	      b.xma = v.vcoord.feq[0];
	    if(v.vcoord.feq[1] > b.yma)
	      b.yma = v.vcoord.feq[1];
	    if(v.vcoord.feq[2] > b.zma)
	      b.zma = v.vcoord.feq[2];
	  }
	while((he = he.next) != l.ledge);
	l = l.nextl;
      }
    b.widenbox((float)CONTBVEPS);
  }

  // add relevant face pair
  void	addrel_f(Face f2)
  {
    if(parent.rel_fa[parent.nrel_fa].rfac != this)
      {
	if(parent.nrel_fa == REL_F)
	  System.out.println("too many relevant faces REL_F");
	else
	  {
	    parent.rel_fa[parent.nrel_fa++].last = (short)parent.nrel_fb;
	    parent.rel_fa[parent.nrel_fa].rfac = this;
	    parent.rel_fa[parent.nrel_fa].first = (short)parent.nrel_fb;
	  }
      }
    if(parent.nrel_fb == REL_FB)
      System.out.println("too many relevant faces, REL_FB");
    else
      parent.rel_fb[parent.nrel_fb++] = f2;
  }

  int haseq()
  {
    if ((fbits & 2) != 0)
      return 1;
    else
      return 0;
  }

  // are equations (almost) opposite ?
  int	oppositeeqs(Face f2)
  {
    int	     ret;
    Vectors  tmp;

    tmp = new Vectors();

    if(comp((double)feq.feq[3], (double)(-f2.feq.feq[3]), COLINEAREPS) == 0)
      {
	tmp.vecPlus(feq, f2.feq);
	ret = tmp.vecNull(COLINEAREPS);
      }
    else
      ret = 0;

    return(ret);
  }

  // are two faces coplanar ?
  int	equaleqs(Face f2)
  {
    int	     ret;
    Vectors  tmp;

    tmp = new Vectors();

    if(comp((double)feq.feq[3], (double)f2.feq.feq[3], COLINEAREPS) == 0)
      {
	tmp.vecMinus(feq, f2.feq);
	ret = tmp.vecNull(COLINEAREPS);
      }
    else
      ret = 0;

    return(ret);
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

  void	do_setop_1(Face f2, short kind)
  {
    Loop      l1, l2, l;
    HalfEdge  h1, h2, h;
    int	      drop;

    drop = feq.getdrop();

    // f1 vs. f2:
    l1 = floops;
    while(l1 != null)
      {
	h1 = l1.ledge;
	do
	  {
	    // handle starting vertex
	    f2.dosetop_3(h1.vertex, drop, 0, kind);

	    // handle other intersections
	    f2.handl_edg(h1, drop, 0, kind);
	  }
	while((h1 = h1.next) != l1.ledge);
	l1 = l1.nextl;
      }

    // f2 vs. f1:
    l2 = f2.floops;
    while(l2 != null)
      {
	h2 = l2.ledge;
	do
	  {
	    // handle only vertices
	    dosetop_3(h2.vertex, drop, 1, kind);
	  }
	while((h2 = h2.next) != l2.ledge);
	l2 = l2.nextl;
      }

    // none of the neighbors are relevant anymore
    // remove (f1) vs. (neighbors of f2) from relevant face pairs
    l = f2.floops;
    while(l != null)
      {
	h = l.ledge;
	do
	  {
	    rem_rel(h.mate().wloop.lface);
	  }
	while((h = h.next) != l.ledge);
	l = l.nextl;
      }

    // remove (neighbors of f1) vs. (f2)
    l = floops;
    while(l != null)
      {
	h = l.ledge;
	do
	  {
	    h.mate().wloop.lface.rem_rel(f2);
	  }
	while((h = h.next) != l.ledge);
	l = l.nextl;
      }

    rem_rel(f2);
  }

  //	Handle vertex on face
  void  dosetop_3(Vertex v, int drop, int type, short kind)
  {
    int		intr;
    HalfEdge	h;
    Box		b;

    // ignore if v is adjacent to a null edge
    if(v.hasnulledge() != 0)
      {
	return;
      }

    // don't process a vertex twice
    if(vertex_on_plane_done(v, type) != 0)
      {
	return;
      }

    // do box test
    b = fbox;
    switch(drop)
      {
      case X:
	if(v.vcoord.feq[1] < b.ymi || v.vcoord.feq[1] > b.yma
	   || v.vcoord.feq[2] < b.zmi || v.vcoord.feq[2] > b.zma)
	  return;
	break;
      case Y:
	if(v.vcoord.feq[0] < b.xmi || v.vcoord.feq[0] > b.xma
	   || v.vcoord.feq[2] < b.zmi || v.vcoord.feq[2] > b.zma)
	  return;
	break;
      case Z:
	if(v.vcoord.feq[0] < b.xmi || v.vcoord.feq[0] > b.xma
	   || v.vcoord.feq[1] < b.ymi || v.vcoord.feq[1] > b.yma)
	  return;
	break;
      }

    // test containment
    intr = contfv2(v, drop);

    if(intr >= 2)
      {
	// hit vertex or edge of f
	if(parent.hitvertex != null)
	  {
	    // hit a vertex of f -- make it relevant
	    v.rvvadd(parent.hitvertex, type, kind);
	  }
	else
	  {
	    // hit edge of f - create vertex on the edge
	    h = (parent.hitedge.he1.wloop.lface == this) ?
	      parent.hitedge.he1 : parent.hitedge.he2;
	    h.extlsvme(h.mate().next, ++parent.maxv, v.vcoord.feq[0],
		     v.vcoord.feq[1], v.vcoord.feq[2]);
	    v.rvvadd(h.vertex, type, kind);
	  }
      }
    else if(intr == 1)
      {
	// interior point -- make pair <f, v> relevant
	rfvadd(v, type, kind);
      }
  }

  // make pair <f, v> relevant
  void rfvadd(Vertex v, int type, short kind)
  {
    int i;

    if(type == 0)
      {
	// don't add if it's already there
	for(i=0; i<parent.na; i++)
	  if(parent.rel_fva[i].rfac == this && parent.rel_fva[i].on_vtx == v)
	    {
	      // the kinds are or'd together
	      parent.rel_fva[i].kind |= kind;
	      return;
	    }

	// check against overflow
	if(parent.na == REL_FV)
	  {
	    System.out.println("too many vertices on a face");
	    return;
	  }

	parent.rel_fva[parent.na].rfac = this;
	parent.rel_fva[parent.na].on_vtx = v;
	parent.rel_fva[parent.na++].kind = kind;
      }
    else
      {
	// don't add if it's already there
	for(i=0; i<parent.nb; i++)
	  if(parent.rel_fvb[i].rfac == this && parent.rel_fvb[i].on_vtx == v)
	    {
	      // the kinds are or'd together
	      parent.rel_fvb[i].kind |= kind;
	      return;
	    }

	// check against overflow
	if(parent.nb == REL_FV)
	  {
	    System.out.println("too many vertices on a face");
	    return;
	  }

	parent.rel_fvb[parent.nb].rfac = this;
	parent.rel_fvb[parent.nb].on_vtx = v;
	parent.rel_fvb[parent.nb++].kind = kind;
      }
  }

  // has the vertex-on-plane processing for v and f been done ?
  int	vertex_on_plane_done(Vertex v, int type)
  {
    int	 i;

    if(type == 0)
      {
	for(i=0; i<parent.na; i++)
	  if(parent.rel_fva[i].rfac == this && parent.rel_fva[i].on_vtx == v)
	    return(1);
	for(i=0; i<parent.nab; i++)
	  if(parent.vtxab[i].va == v)
	    return(1);
	return(0);
      }
    else
      {
	for(i=0; i<parent.nb; i++)
	  if(parent.rel_fvb[i].rfac == this && parent.rel_fvb[i].on_vtx == v)
	    return(1);
	for(i=0; i<parent.nab; i++)
	  if(parent.vtxab[i].vb == v)
	    return(1);
	return(0);
      }
  }

  int	contfv(Vertex v)
  {
    int	drop;

    // get face equation
    if(haseq() == 0)
      flout.newell(feq);

    // select projection plane
    drop = feq.getdrop();

    return(contfv2(v, drop));
  }

  int contfv2(Vertex v, int drop)
  {
    Loop   l;
    int    cont;

    // check outer loop first
    cont = flout.contlv(v, drop);

    if(cont == 0)     // out
      return(0);

    if(cont >= 2)     // on the boundary
      return(cont);

    // check inner loops, if any
    l = floops;
    while(l != null)
      {
	// do not take outer loop or ring-type null edges
        if((l != flout) && (l.length>2))
	  {
            cont = l.contlv(v, drop);
            if(cont == 1)
	      return(0);
            if(cont >= 2)
	      return(cont);
	  }
        l = l.nextl;
      }
    return(1);
  }

  //Handle coplanar edge
  void handl_edg(HalfEdge he, int drop, int type, short kind)
  {
    Loop	l;
    HalfEdge	h, h2;
    double	x, y, z;
    //double	t1, t2;
    double      t[];
    int		i;
    Box		b;
    Vertex	v1, v2;
    Vertex      testv;

    testv = new Vertex(parent);
    t = new double[2];

    // do box test
    b = fbox;
    v1 = he.vertex;
    v2 = he.next.vertex;
    switch(drop)
      {
      case X:
	if(v1.vcoord.feq[1] < b.ymi && v2.vcoord.feq[1] < b.ymi)
	  return;
	if(v1.vcoord.feq[1] > b.yma && v2.vcoord.feq[1] > b.yma)
	  return;
	if(v1.vcoord.feq[2] < b.zmi && v2.vcoord.feq[2] < b.zmi)
	  return;
	if(v1.vcoord.feq[2] > b.zma && v2.vcoord.feq[2] > b.zma)
	  return;
	break;
      case Y:
	if(v1.vcoord.feq[0] < b.xmi && v2.vcoord.feq[0] < b.xmi)
	  return;
	if(v1.vcoord.feq[0] > b.xma && v2.vcoord.feq[0] > b.xma)
	  return;
	if(v1.vcoord.feq[2] < b.zmi && v2.vcoord.feq[2] < b.zmi)
	  return;
	if(v1.vcoord.feq[2] > b.zma && v2.vcoord.feq[2] > b.zma)
	  return;
	break;
      case Z:
	if(v1.vcoord.feq[0] < b.xmi && v2.vcoord.feq[0] < b.xmi)
	  return;
	if(v1.vcoord.feq[0] > b.xma && v2.vcoord.feq[0] > b.xma)
	  return;
	if(v1.vcoord.feq[1] < b.ymi && v2.vcoord.feq[1] < b.ymi)
	  return;
	if(v1.vcoord.feq[1] > b.yma && v2.vcoord.feq[1] > b.yma)
	  return;
	break;
      }

    parent.n_rel_int = 0;
    l = floops;
    while(l != null)
      {
	h2 = l.ledge;
	do
	  {
	    if(he.vertex.int2ee(he.next.vertex, h2.vertex, h2.next.vertex,
				drop, t) != 0)
	      {
		// check that 0.0 < t1, t2 < 1.0
		// change: allow also equality
		if((comp(t[0], 0.0, EPSPARAM) != -1) &&
		   (comp(t[0], 1.0, EPSPARAM) != 1) &&
		   (comp(t[1], 0.0, EPSPARAM) != -1) &&
		   (comp(t[1], 1.0, EPSPARAM) != 1))
		  {
		    if(parent.n_rel_int == MAX_REL_INT)
		      {
			System.out.println("Too many intersections / edge!");
		      }
		    parent.rel_int[parent.n_rel_int].he_int = h2;
		    parent.rel_int[parent.n_rel_int].t1 = t[0];
		    parent.n_rel_int++;
		  }
	      }
	  }
	while((h2 = h2.next) != l.ledge);
	l = l.nextl;
      }

    // any intersectons ?
    if(parent.n_rel_int > 0)
      {
	sort_int();

	for(i=0; i<parent.n_rel_int; i++)
	  {
	    h2 = parent.rel_int[i].he_int;
	    t[0] = parent.rel_int[i].t1;

	    // ignore intersections if the edges share an end point
	    if(h2.vertex.contvv(he.vertex)!= 0 ||
	       h2.next.vertex.contvv(he.vertex)!=0 ||
	       h2.vertex.contvv(he.next.vertex)!=0 ||
	       h2.next.vertex.contvv(he.next.vertex)!=0)
	      continue;

	    // calculate intersection point
	    x = v1.vcoord.feq[0] + t[0]*(v2.vcoord.feq[0] - v1.vcoord.feq[0]);
	    y = v1.vcoord.feq[1] + t[0]*(v2.vcoord.feq[1] - v1.vcoord.feq[1]);
	    z = v1.vcoord.feq[2] + t[0]*(v2.vcoord.feq[2] - v1.vcoord.feq[2]);
	    testv.vcoord.feq[0] = (float)x;
	    testv.vcoord.feq[1] = (float)y;
	    testv.vcoord.feq[2] = (float)z;

	    // subdivide edges, if necessary

	    // start point of he ?
	    if(he.vertex.contvv(testv) != 0)
	      {
		h = he;		// no subdivision
	      }
	    // final point of he ?
	    else if(he.mate().vertex.contvv(testv) != 0)
	      {
		h = he.mate();	// no subdivision
	      }
	    // it's a middle point of he --- subdivide
	    else
	      {
		he.extlsvme(he.mate().next, ++parent.maxv,
			    (float)x, (float)y, (float)z);
		h = he;
	      }

	    // hit start of h2 ?
	    if(h2.vertex.contvv(h.vertex) != 0)
	      {
		h.vertex.rvvadd(h2.vertex, type, kind);
	      }
	    // hit end of h2 ?
	    else if(h2.mate().vertex.contvv(h.vertex) != 0)
	      {
		h.vertex.rvvadd(h2.mate().vertex, type, kind);
	      }

	    // hit middle of h2 --- subdivide it
	    else
	      {
		h2.extlsvme(h2.mate().next, ++parent.maxv,
			    h.vertex.vcoord.feq[0],
			    h.vertex.vcoord.feq[1],
			    h.vertex.vcoord.feq[2]);
		h.vertex.rvvadd(h2.vertex, type, kind);
	      }
	  }
      }
  }

  // Sort intersections of an edge on plane along the edge
  void sort_int()
  {
    int	        i, j;
    Relint	temp;

    temp = new Relint();

    i = 0;
    while(i < parent.n_rel_int)
      {
	j = i+1;
	while(j < parent.n_rel_int)
	  {
	    if(parent.rel_int[i].t1 > parent.rel_int[j].t1)
	      {
		temp.he_int = parent.rel_int[j].he_int;
		temp.t1 = parent.rel_int[j].t1;
		parent.rel_int[j].he_int = parent.rel_int[i].he_int;
		parent.rel_int[j].t1 = parent.rel_int[i].t1;
		parent.rel_int[i].he_int = temp.he_int;
		parent.rel_int[i].t1 = temp.t1;
	      }
	    j++;
	  }
	i++;
      }
  }

  // remove face pair
  void	rem_rel(Face f2)
  {
    int	   i, j;
    int    flg;

    flg = 0;

    for(i=1; i<parent.nrel_fa; i++)
      if(parent.rel_fa[i].rfac == this)
	{
	  flg = 1;
	  break;
	}

    if (flg != 1)
      return;

  found1:
    flg = 0;
    for(j=parent.rel_fa[i].first; j<parent.rel_fa[i].last; j++)
      if(parent.rel_fb[j] == f2)
	{
	  flg = 1;
	  break;
	}

    if (flg != 1)
      return;

  found2:
    parent.rel_fb[j] = null;
  }

  //	Compare edges of f1 to f2
  void	testff(Face f2, int type)
  {
    Loop       l;
    HalfEdge   he, henxt;
    double     d1, d2;
    double     t;
    int	       c1, c2, inout, drop;

    Vertex     prevvtx;		// if not NIL, the distance
    double     prevdist = 0.0;	// of this vertex is here
    int	       prevcomp = 0;	// and the res of comp() here
                                // initial values are for lint only

    if(comp(f2.feq.feq[0], 0.0, COLINEAREPS) == 0 &&
       comp(f2.feq.feq[1], 0.0, COLINEAREPS) == 0 &&
       comp(f2.feq.feq[2], 0.0, COLINEAREPS) == 0)
      {
	System.out.println("setop: null face ignored");
	return;
      }

    drop = f2.feq.getdrop();

    l = floops;
    while(l != null)
      {
	prevvtx = null;
	he = l.ledge;
	// ignore ring null edges
	if(he.next.next != he)
	  do
	    {
	      // ignore null edges
	      if(he.nulledge() != 0)
		continue;

	      // box test
	      if(f2.fbox.intbe(he.edge) == 0)
		continue;

	      // avoid calculating distances twice
	      if(prevvtx == he.vertex)
		{
		  d1 = prevdist;
		  c1 = prevcomp;
		}
	      else
		{
		  d1 = f2.feq.dist(he.vertex);
		  c1 = comp(d1, 0.0, CONTFVEPS);
		}

	      d2 = f2.feq.dist(he.next.vertex);
	      c2 = comp(d2, 0.0, CONTFVEPS);
	      prevvtx = he.next.vertex;
	      prevdist = d2;
	      prevcomp = c2;

	      // edge on plane ?
	      if(c1 == 0 && c2 == 0)
		{
		  // don't handle the edge twice
		  if(edge_on_plane_done(f2) == 0)
		    f2.dosetop_2(he, drop, type, (short)PLAIN);
		}

	      // vertex on plane ?
	      else if(c1 == 0)
		{
		  f2.dosetop_3(he.vertex, drop, type, (short)PLAIN);
		}
	      else if(c2 == 0)
		{
		  // will be handled anyway
		}

	      // else it's either an edge-edge or
	      // an ordinary intersection
	      else if(c1 == -1 && c2 == 1 || c1 == 1 && c2 == -1)
		{
		  t = d1 / (d1 - d2);
		  // find out which
		  inout = he.testint(f2, drop, type, t);
		  if(inout == 1)
		    {
		      henxt = he.next;
		      he.dosetop_5(f2, d1, type);
		      // consider next halfedge next
		      he = henxt.prev;
		    }
		  else if(inout >= 2)
		    {
		      // All these should be caught for a vs. b (type == 0)
		      if(type == 0)
			{
			  if(parent.hitedge != null)
			    {
			      henxt = he.next;
			      he.dosetop_4(parent.hitedge, d1, f2);
			      he = henxt.prev;
			    }
			  else
			    {
			      henxt = he.next;
			      he.dosetop_4a(parent.hitvertex);
			      he = henxt.prev;
			    }
			}
		    }
		}
	    }
	  while((he = he.next) != l.ledge);
	l = l.nextl;
      }
  }

  int	edge_on_plane_done(Face f2)
  {
    int	i;

    for(i = 0; i < parent.n_eopdone; i++)
      if(parent.eopdone[i].f1 == this && parent.eopdone[i].f2 == f2)
	return(1);

    if(parent.n_eopdone < MAX_EOPDONE)
	{
		parent.eopdone[parent.n_eopdone].f1 = this;
		parent.eopdone[parent.n_eopdone++].f2 = f2;
	}
    return(0);
  }

  // Handle edge on face
  void dosetop_2(HalfEdge he, int drop, int type, short kind)
  {
    // handle the end points of he
    dosetop_3(he.vertex, drop, type, kind);
    dosetop_3(he.next.vertex, drop, type, kind);

    // handle intersections internal to he
    handl_edg(he, drop, type, kind);
  }

  // add ring null edge
  void	makering(double x, double y, double z, int type)
  {
    HalfEdge	he1, he2;

    flout.ledge.lmev(flout.ledge, ++parent.maxv, (float)x, (float)y, (float)z);
    he1 = flout.ledge.prev;
    he1.lmev(he1, ++parent.maxv, (float)x, (float)y, (float)z);
    he2 = he1.prev;
    he1.prev.prev.prev.lkemr(he1);

    if(type == 0)
      parent.lone[parent.nedg].nea = he2.edge;
    else
      parent.lone[parent.nedg].neb = he2.edge;
  }

  //	- low-level version of ringmv()

  void	lringmv(Face f2, Loop l, int out)
  {
    l.delList(this);
    l.linkNode(f2);

    if(out != 0)
      f2.flout = l;
  }

  void	laringmv(Face f2)
  {
    int	   drop, in;
    Loop   ring;
    Loop   l;

    if(haseq() == 0)
      flout.newell(feq);
    drop = feq.getdrop();

    // for each interior loop of f1, perform a point-in-polygon
    // test and move it to f2, if not interior any more

    l = floops;
    while(l != null)
      {
	if((ring = l) != flout)
	  {
	    l = l.nextl;
	    in = contfv2(ring.ledge.vertex, drop);

	    if(in == 0)
	      {
		ring.delList(this);
		ring.linkNode(f2);
	      }
	  }
	else l = l.nextl;
      }
  }

  void	lkfmrh(Face f2)
  {
    // kill surface tag
    if(f2.surf != null)
      f2.lsettag(null);

    // link loop of face f2 to face f1
    f2.floops.nextl = floops;
    floops = f2.floops;
    floops.lface = this;

    // remove f2 from solid
    f2.delList(fsolid);
  }

  // Glue two similar loops of a face
  void	lglue()
  {
    Loop	l1, l2;
    HalfEdge	h1, h2, h1next;
    int		tempfaceno;

    tempfaceno = 32000;

    // find the common start point of gluing
    l1 = floops;
    l2 = floops.nextl;
    h1 = l1.ledge;
    h2 = l2.ledge;
    while(h1.vertex.contvv(h2.vertex) == 0)
      h2 = h2.next;

    h1.lmekr(h2);
    h1.prev.ljvke(h2.prev);
    while(h1.next != h2)
      {
	h1next = h1.next;
	h1.next.lmef(h1.prev, tempfaceno--);
	h1.next.ljvke(h1.next.mate());
	h1.mate().lkef(h1);
	h1 = h1next;
      }
    h1.mate().lkef(h1);
  }

  //	Make face, kill ring-hole
  Face	mfmg(Loop l, int fnr)
  {
    Face nf;

    // create a new face
    nf = new Face(parent, fsolid);
    nf.faceNum = fnr;

    // remove loop l from face f and link it to new face
    l.delList(this);
    l.linkNode(nf);
    nf.flout = l;
    if(flout == l)
      flout = floops;

    // finally return the address of the new face
    return(nf);
  }

  // are faces identical?
  int identif(Face f2)
  {
    Loop        l1, l2;
    HalfEdge	he1, he2;
    int		i, tmpflg=0;

    l1 = flout;
    l2 = f2.flout;

    if(l1.length != l2.length)
      return(0);

    i = 0;
    he1 = l1.ledge;
    he2 = l2.ledge;
    while(i <= l2.length)
      {
	if(he1.vertex.contvv(he2.vertex) != 0)
	  {
	    tmpflg = 1;
	    break;
	  }
	he2 = he2.next;
	i++;
      }

    if(tmpflg != 1)
      return(0);	// no identical vertex found

  found:

    // are other vertices identical ?
    i = 0;
    while(i <= l2.length)
      {
	if(he1.vertex.contvv(he2.vertex) == 0)
	  return(0);
	he1 = he1.next;
	he2 = he2.prev;
	i++;
      }
    return(1);
  }

  // is f an ``inner'' face ?
  int	innernullf()
  {
    HalfEdge	he, nbr_he;
    Face        nbr_f;

    he = floops.ledge;
    nbr_he = he.mate();
    nbr_f = nbr_he.wloop.lface;

    if(nbr_he.wloop != nbr_f.flout)
      return(1);
    return(0);
  }

  // check whether f is a "tangent null face", i.e. surrounded by just one face
  int tangentnullf()
  {
    HalfEdge	he;
    Face	nbr_f;

    he = floops.ledge;
    nbr_f = he.mate().wloop.lface;

    he = he.next;
    do
      {
	if(he.mate().wloop.lface != nbr_f)
	  return(0);
	he = he.next;
      }
    while(he != floops.ledge);

    // all had same neighbor - it's within that
    return(1);
  }

  // detach a tangent null face from its neighbor
  void	detachnullf()
  {
    HalfEdge	he;

    // locate detachment points
    he = floops.ledge;
    from1 = he.mate();
    while(from1.prev == he.next.mate())
      {
	he = he.next;
	from1 = he.mate();
      }
    to1 = he.next.mate().next;

    // split & detach
    from1.lmev(to1, ++parent.maxv, from1.vertex.vcoord.feq[0],
	       from1.vertex.vcoord.feq[1], from1.vertex.vcoord.feq[2]);
    from1.prev.lkemr(to1.prev);
  }

  // swap loops of an inner null face f and its neighbor
  void	swapnullf()
  {
    Face	f2;
    HalfEdge	h;

    // get neighbor face
    h = floops.ledge;
    f2 = h.mate().wloop.lface;

    // swap loops of f and f2
    lringmv(f2, h.wloop, 0);
    f2.lringmv(this, h.mate().wloop, 1);
  }

  // attach a tangent null face back
  void	attachnullf()
  {
    HalfEdge	he, at;

    // locate attachment point
    he = floops.ledge;
    while(he.vertex.contvv(to1.vertex) == 0)
      he = he.next;
    at = he.mate().next;

    // attach & join
    to1.lmekr(at);
    at.prev.ljvke(to1.prev);
  }

  void	markfac(Solid s)
  {
    Loop	l;
    HalfEdge	he;
    Face	f2;

    fsolid = s;

    l = floops;
    while(l != null)
      {
	he = l.ledge;
	do
	  {
	    f2 = he.mate().wloop.lface;
	    if(f2.fsolid != s)
	      f2.markfac(s);
	  }
	while((he = he.next) != l.ledge);
	l = l.nextl;
      }
  }

  void	lkvsf()
  {
    Solid   s;
    Loop    l;

    s = fsolid;

    // kill surface tag if there
    if(surf != null)
      lsettag(null);

    // kill the face and the other stuff
    delList(s);
    l = flout;
    l.delList(this);
    l.ledge.vertex.delList(s);
    l.ledge.delList();
    // N.B. pointer to the solid remains valid
  }

  // remove empty rings of f after a mef
  void	handlerings(Loop l)
  {
    Loop	ring;
    Vertex	v;

    ring = floops;
    while(ring != null)
      {
	if(l != ring)
	  {
	    // if the ring is empty, remove it
	    if(ring.length == 0)
	      {
		v = ring.ledge.vertex;
		ring = ring.nextl;

		fsolid.mekr(faceNum, l.ledge.vertex.vertexNum, v.vertexNum);
		fsolid.kev(faceNum, l.ledge.vertex.vertexNum, v.vertexNum);
	      }
	    else
	      ring = ring.nextl;
	  }
	else
	  ring = ring.nextl;
      }
  }

}
