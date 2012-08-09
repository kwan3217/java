package csg;

class Loop
{
  short         length;         // length of loop
  HalfEdge	ledge;		// pointer to ring of half edges
  Face		lface;		// back pointer to face
  Loop		nextl;		// pointer to next loop
  //Loop		prevl;		// pointer to previous loop
  CSG		parent;		// pointer to the parent

  static final double EPSPARAM  = 0.00001;
  static final int    ERROR     = -1;
  static final double CONTBVEPS = 0.0002;  // vertex vs. box
  static final double EPS       = 0.00001;
  static final int    X         = 0;
  static final int    Y         = 1;
  static final int    Z         = 2;

  Loop(CSG par, Face f)
  {
    parent = par;
    parent.num_loops++;
    addList(f);
  }

  Loop(CSG par)
  {
    parent = par;
  }

  void addList(Face f)
  {
    nextl = f.floops;
    f.floops = this;
    lface = f;
    ledge = null;
  }

  void linkNode(Face f)
  {
    // link loop to face
    nextl = f.floops;
    f.floops = this;
    lface = f;
  }

  void delList(Face f)
  {
    Loop l, prev;

    parent.num_loops--;
    l = f.floops;
    prev = null;
    while(l != this)
      {
	prev = l;
	l = l.nextl;
      }
    if (prev !=  null)
      prev.nextl = l.nextl;
    else
      f.floops = l.nextl;
  }

  void linkl(Edge e, Vertex v, HalfEdge where, int orient)
  {
    HalfEdge he;

    length++;
    if(where.edge == null)
      {
	// empty loop
	he = where;
      }
    else
      {
	he = new HalfEdge(parent, 0);

	where.prev.next = he;
	he.prev = where.prev;
	where.prev = he;
	he.next = where;
      }
    he.edge = e;
    he.vertex = v;

    // adjust back ptr of edge e
    if(orient == 1)    // positive orientation
      e.he1 = he;
    else	       // negative orientation
      e.he2 = he;      // set back ptr of halfedge he
    he.wloop = this;
  }

  int	findl(Face f)
  {
    Loop lp;

    lp = f.floops;
    while(lp != null)
      {
	if(lp == this)
	  return(1);
	lp = lp.nextl;
      }

    System.out.println("checke: wrong back ptr in loop");
    return(0);
  }

  void newell(Vectors eq)
  {
    Vertex      v2;
    HalfEdge	he;
    double	origin_x, origin_y, origin_z;
    double	a, b, c, d;
    double	a1, b1, c1;
    double	norm;
    double	xi, yi, zi;
    double	xj, yj, zj;
    double	xc, yc, zc;
    int		i, n_null=0;

    origin_x = ledge.vertex.vcoord.feq[0];
    origin_y = ledge.vertex.vcoord.feq[1];
    origin_z = ledge.vertex.vcoord.feq[2];

    a = 0.0;
    b = 0.0;
    c = 0.0;;
    d = 0.0;

    xc = 0.0;
    yc = 0.0;
    zc = 0.0;

    he = ledge;
    v2 = he.vertex;
    xj = v2.vcoord.feq[0] - origin_x;
    yj = v2.vcoord.feq[1] - origin_y;
    zj = v2.vcoord.feq[2] - origin_z;

    i = length;
    while(i-- != 0)
      {
	v2 = (i == 0) ? ledge.vertex : he.next.vertex;

	xi = xj;
	yi = yj;
	zi = zj;
	xj = v2.vcoord.feq[0] - origin_x;
	yj = v2.vcoord.feq[1] - origin_y;
	zj = v2.vcoord.feq[2] - origin_z;

	a += (yi - yj) * (zi + zj);
	b += (zi - zj) * (xi + xj);
	c += (xi - xj) * (yi + yj);

	xc += xi;
	yc += yi;
	zc += zi;

	he = he.next;
      }

    // normalize a, b, c
    norm = Math.sqrt(a*a + b*b + c*c);
    if(norm != 0.0)
      {
	a = a / norm;
	b = b / norm;
	c = c / norm;
	n_null = 0;
      }
    else if(lface != null)
      {
	n_null = 1;
      }

    // (a1 b1 c1) = "average" point on face
    d = length;
    a1 = xc / d + origin_x;
    b1 = yc / d + origin_y;
    c1 = zc / d + origin_z;

    d = a*a1 + b*b1 + c*c1;

    eq.feq[0] = (float)a;
    eq.feq[1] = (float)b;
    eq.feq[2] = (float)c;
    eq.feq[3] = (float)(0.0-d);

    if(lface!=null && this == lface.flout && lface.feq == eq)
      {
	if(n_null == 0)
	  lface.fbits |= 2;
	else
	  lface.fbits &= ~2;
      }
  }

  //	Test for containment of a vertex in a loop
  //   - returns 0 <=> outside, 1 <=> inside, 2 <=> on edge, 3 <==>
  //	at vertex
  int	contlv(Vertex v, int drop)
  {
    Vertex 	v1, v2, nullv;
    HalfEdge	he1, he2;
    Vertex	aux;
    int		intersections=0, intr;
    int		c1, c2;
    double	t1=0.0, t2=0.0, t[];
    int         tmpflg;

    t = new double[2];

    nullv = new Vertex(parent);
    v1 = new Vertex(parent);
    v2 = new Vertex(parent);
    aux = new Vertex(parent);

    nullv.vcoord.feq[0] = 0;	// origo at v => v = null
    nullv.vcoord.feq[1] = 0;
    nullv.vcoord.feq[2] = 0;

    // perform box test for inner loop or if no box exists
    if((this != lface.flout) || (lface.fbox==null))
      {
	if(contlbox(v, drop) == 0)
	  {
	    return(0);
	  }
      }

    // test special cases:
    if(parent.Gtestspecials != 0)
      {
	he1 = ledge;
	do
	  {
	    // origo = test vertex v
	    v1.vcoord.feq[0] = he1.vertex.vcoord.feq[0] - v.vcoord.feq[0];
	    v1.vcoord.feq[1] = he1.vertex.vcoord.feq[1] - v.vcoord.feq[1];
	    v1.vcoord.feq[2] = he1.vertex.vcoord.feq[2] - v.vcoord.feq[2];

	    // origo = test vertex v
	    if(nullv.contvv(v1) != 0)
	      {
		parent.hitvertex = he1.vertex;
		parent.hithe = null;
		parent.hitedge = null;
		return(3);
	      }
	  }
	while((he1 = he1.next) != ledge);

	he1 = ledge;
	do
	  {
	    // origo = test vertex v
	    v1.vcoord.feq[0] = he1.vertex.vcoord.feq[0] - v.vcoord.feq[0];
	    v1.vcoord.feq[1] = he1.vertex.vcoord.feq[1] - v.vcoord.feq[1];
	    v1.vcoord.feq[2] = he1.vertex.vcoord.feq[2] - v.vcoord.feq[2];
	    v2.vcoord.feq[0] = he1.next.vertex.vcoord.feq[0] - v.vcoord.feq[0];
	    v2.vcoord.feq[1] = he1.next.vertex.vcoord.feq[1] - v.vcoord.feq[1];
	    v2.vcoord.feq[2] = he1.next.vertex.vcoord.feq[2] - v.vcoord.feq[2];

	    // origo = test vertex v
	    if(v1.contev(v2, nullv) != 0)
	      {
		parent.hitvertex = null;
		parent.hithe = he1;
		parent.hitedge = he1.edge;
		return(2);
	      }
	  }
	while((he1 = he1.next) != ledge);
      }

    he2 = ledge;
    // beware of null edges
    while(he2.nulledge()!=0)
      he2 = he2.next;

    tmpflg = 0;

    while(tmpflg == 0)
      {
        label:   // for panic retry

	// get a pair of vertices

	// origo = test vertex v
	v1.vcoord.feq[0] = he2.vertex.vcoord.feq[0] - v.vcoord.feq[0];
	v1.vcoord.feq[1] = he2.vertex.vcoord.feq[1] - v.vcoord.feq[1];
	v1.vcoord.feq[2] = he2.vertex.vcoord.feq[2] - v.vcoord.feq[2];
	v2.vcoord.feq[0] = he2.next.vertex.vcoord.feq[0] - v.vcoord.feq[0];
	v2.vcoord.feq[1] = he2.next.vertex.vcoord.feq[1] - v.vcoord.feq[1];
	v2.vcoord.feq[2] = he2.next.vertex.vcoord.feq[2] - v.vcoord.feq[2];

	// origo = test vertex v

	// let auxiliary point = midpoint of edge v1-v2
	aux.vcoord.feq[0] = (v1.vcoord.feq[0] + v2.vcoord.feq[0]) / 2;
	aux.vcoord.feq[1] = (v1.vcoord.feq[1] + v2.vcoord.feq[1]) / 2;
	aux.vcoord.feq[2] = (v1.vcoord.feq[2] + v2.vcoord.feq[2]) / 2;

	// check all intersections between the test line and the edges
	he1 = ledge;
	intersections = 0;
	do
	  {
	    // test for intersection, beware of null edges
	    if(he1.nulledge() != 0)
	      intr = 0;
	    else
	      {
		// origo = test vertex v
		v1.vcoord.feq[0] = he1.vertex.vcoord.feq[0] - v.vcoord.feq[0];
		v1.vcoord.feq[1] = he1.vertex.vcoord.feq[1] - v.vcoord.feq[1];
		v1.vcoord.feq[2] = he1.vertex.vcoord.feq[2] - v.vcoord.feq[2];
		v2.vcoord.feq[0] = he1.next.vertex.vcoord.feq[0] - v.vcoord.feq[0];
		v2.vcoord.feq[1] = he1.next.vertex.vcoord.feq[1] - v.vcoord.feq[1];
		v2.vcoord.feq[2] = he1.next.vertex.vcoord.feq[2] - v.vcoord.feq[2];

		// origo = test vertex v
		t[0] = t1;
		t[1] = t2;
		intr = nullv.int2ee(aux, v1, v2, drop, t);
		t1 = t[0];
		t2 = t[1];
	      }

	    if(intr == 1)
	      {
		c1 = comp(t2, 0.0, EPSPARAM);
		c2 = comp(t2, 1.0, EPSPARAM);

		if(c1 == 0 || c2 == 0)
		  {
		    // panic retry
		    he2 = he2.next;
		    if(he2 == ledge)
		      return(ERROR);
		    break;
		  }
		tmpflg = 1;
		if(c1 == 1 && c2 == -1)   // 0.0 < t2 < 1.0
		  {
		    if(t1 >= 0.0)
		      intersections++;
		  }
	      }
	  }
	while((he1 = he1.next) != ledge);

      }

    // the test point is inside if the number of intersections is odd
    intersections = intersections % 2;
    return(intersections);

  }

  //	Do a box test
  int	contlbox(Vertex v, int drop)
  {
    HalfEdge	 he;
    Vertex	 vtx;
    double	 umin, umax, vmin, vmax;

    umin = 10000000000.0;
    vmin = 10000000000.0;
    umax = -10000000000.0;
    vmax = -10000000000.0;

    he = ledge;
    do
      {
	vtx = he.vertex;

	switch(drop)
	  {
	  case X:
	    umin = (umin < vtx.vcoord.feq[1]) ? umin : (double)vtx.vcoord.feq[1] ;
	    vmin = (vmin < vtx.vcoord.feq[2]) ? vmin : (double)vtx.vcoord.feq[2] ;
	    break;
	  case Y:
	    umin = (umin < vtx.vcoord.feq[0]) ? umin : (double)vtx.vcoord.feq[0] ;
	    vmin = (vmin < vtx.vcoord.feq[2]) ? vmin : (double)vtx.vcoord.feq[2] ;
	    break;
	  case Z:
	    umin = (umin < vtx.vcoord.feq[0]) ? umin : (double)vtx.vcoord.feq[0] ;
	    vmin = (vmin < vtx.vcoord.feq[1]) ? vmin : (double)vtx.vcoord.feq[1] ;
	    break;
	  }

	switch(drop)
	  {
	  case X:
	    umax = (umax > vtx.vcoord.feq[1]) ? umax : (double)vtx.vcoord.feq[1] ;
	    vmax = (vmax > vtx.vcoord.feq[2]) ? vmax : (double)vtx.vcoord.feq[2] ;
	    break;
	  case Y:
	    umax = (umax > vtx.vcoord.feq[0]) ? umax : (double)vtx.vcoord.feq[0] ;
	    vmax = (vmax > vtx.vcoord.feq[2]) ? vmax : (double)vtx.vcoord.feq[2] ;
	    break;
	  case Z:
	    umax = (umax > vtx.vcoord.feq[0]) ? umax : (double)vtx.vcoord.feq[0] ;
	    vmax = (vmax > vtx.vcoord.feq[1]) ? vmax : (double)vtx.vcoord.feq[1] ;
	    break;
	  }

	he = he.next;
      }
    while(he != ledge);

    umin -= CONTBVEPS;
    umax += CONTBVEPS;
    vmin -= CONTBVEPS;
    vmax += CONTBVEPS;

    switch(drop)
      {
      case X:
	if(v.vcoord.feq[1] < umin || v.vcoord.feq[1] > umax)
	  return(0);
	if(v.vcoord.feq[2] < vmin || v.vcoord.feq[2] > vmax)
	  return(0);
	break;
      case Y:
	if(v.vcoord.feq[0] < umin || v.vcoord.feq[0] > umax)
	  return(0);
	if(v.vcoord.feq[2] < vmin || v.vcoord.feq[2] > vmax)
	  return(0);
	break;
      case Z:
	if(v.vcoord.feq[0] < umin || v.vcoord.feq[0] > umax)
	  return(0);
	if(v.vcoord.feq[1] < vmin || v.vcoord.feq[1] > vmax)
	  return(0);
	break;
      }
    return(1);
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

  //	Check whether an "inner" face f is properly oriented w.r.t. the
  //	exterior.  If not, replace its loop with l.

  void	check_orientation(Face f)
  {
    Loop      l2;
    Vectors   tmp;

    tmp = new Vectors();

    // get the equation of the outer loop of the face of l
    if(lface.haseq() == 0)
      lface.flout.newell(lface.feq);

    if(lface.feq.vecNull(EPS) != 0)
      {
	System.out.println("null face ignored");
	return;
      }

    // get the equation of the inner loop
    newell(tmp);

    // are normals opposite ?
    tmp.vecPlus(tmp, lface.feq);
    if(tmp.vecNull(EPS) == 0)
      {
	System.out.println("swap loops");
	// non-opposite normals !
	// swap l and the outer loop of f
	l2 = f.flout;
	f.lringmv(lface, l2, 0);
	lface.lringmv(f, this, 1);
      }
  }

}
