package csg;

class Vertex
{
  int		vertexNum;	// vertex identifier
  HalfEdge	vedge;		// pointer to a half edge
  Vectors	vcoord;		// vertex coordinate
  Vertex	nextv;		// pointer to next vertex
  Vertex	prevv;		// pointer to previous vertex
  //char		sflag;
  Solid		vsolid;
  CSG		parent;		// pointer to the parent

  static double        CONTVVEPS2 = 0.0000000025;
  static final double  CONTEVEPS2 = 0.0000000025;
  static final double EPSPARAM    = 0.00001;
  static final double EPS         = 1.0e-5;   // general epsilon
  static final double CONTFVEPS	  = 0.00005;
  static final int    REL_F       = 1000;     // max. relevant faces on the first solid
  static final int    REL_FB      = 10000;    // max. relevant face pairs
  static final int    REL_FV      = 1000;     // max. relevant vertices on face
  static final int    REL_VV      = 1000;     // max. coincident vertices
  static final int    OUT         = -1;
  static final int    UNION       = 1;
  static final int    OPPOSITE    = 1;
  static final int    NONOPPOSITE = 2;
  static final int    IN          = 1;
  static final int    NEDG        = 2000;

  Vertex(CSG par, Solid s)
  {
    parent = par;
    parent.num_vertex++;
    vcoord = new Vectors();
    addList(s);
    vedge = null;
  }

  Vertex(CSG par)
  {
    parent = par;
    vedge = null;
    vcoord = new Vectors();
    vcoord.feq[3] = 1;
  }

  void addList(Solid s)
  {
    nextv = s.sverts;
     if (s.sverts != null)
      s.sverts.prevv = this;
    prevv = null;
    vsolid = s;
    s.sverts = this;
  }

  void delList(Solid s)
  {
    if(prevv != null)
      prevv.nextv = nextv;
    if(nextv != null)
      nextv.prevv = prevv;
    if(s.sverts == this)
      s.sverts = nextv;
    vsolid = null;
  }

  int finde(HalfEdge he)
  {
    HalfEdge h;

    h = vedge;
    do
      {
	if(h == he)
	  return(1);
      }
    while((h = h.mate().next) != vedge);
    System.out.println("checke: wrong vertex ptr in halfedge");
    return(0);
  }

  //	does v have a null adjacent edge ?
  int	hasnulledge()
  {
    HalfEdge  h;

    h = vedge;
    do
      {
	if(h.nulledge()!=0 || h.mate().nulledge()!=0)
	  return(1);
	h = h.mate().next;
      }
    while(h != vedge);
    return(0);
  }

  int int2ee(Vertex v2, Vertex v3, Vertex v4, int drop, double t[])
  {
    double d, a1=0.0, a2=0.0, b1=0.0, b2=0.0, c1=0.0, c2=0.0;

    switch(drop)
      {
      case 0:
	a1 = (double)(v2.vcoord.feq[1] - vcoord.feq[1]);
	a2 = (double)(v2.vcoord.feq[2] - vcoord.feq[2]);
	b1 = (double)(v3.vcoord.feq[1] - v4.vcoord.feq[1]);
	b2 = (double)(v3.vcoord.feq[2] - v4.vcoord.feq[2]);
	break;
      case 1:
	a1 = (double)(v2.vcoord.feq[0] - vcoord.feq[0]);
	a2 = (double)(v2.vcoord.feq[2] - vcoord.feq[2]);
	b1 = (double)(v3.vcoord.feq[0] - v4.vcoord.feq[0]);
	b2 = (double)(v3.vcoord.feq[2] - v4.vcoord.feq[2]);
	break;
      case 2:
	a1 = (double)(v2.vcoord.feq[0] - vcoord.feq[0]);
	a2 = (double)(v2.vcoord.feq[1] - vcoord.feq[1]);
	b1 = (double)(v3.vcoord.feq[0] - v4.vcoord.feq[0]);
	b2 = (double)(v3.vcoord.feq[1] - v4.vcoord.feq[1]);
	break;
      }

    d = a1*b2 - a2*b1;
    if(comp(d, 0.0, EPS) == 0)
      return 0;
    else
      {
	switch(drop)
	  {
	  case 0:
	    c1 = (double)(vcoord.feq[1] - v3.vcoord.feq[1]);
	    c2 = (double)(vcoord.feq[2] - v3.vcoord.feq[2]);
	    break;
	  case 1:
	    c1 = (double)(vcoord.feq[0] - v3.vcoord.feq[0]);
	    c2 = (double)(vcoord.feq[2] - v3.vcoord.feq[2]);
	    break;
	  case 2:
	    c1 = (double)(vcoord.feq[0] - v3.vcoord.feq[0]);
	    c2 = (double)(vcoord.feq[1] - v3.vcoord.feq[1]);
	    break;
	  }
	t[0] = (c2*b1 - c1*b2)/d;
	t[1] = (a2*c1 - a1*c2)/d;
	return 1;
      }
  }

  int	contvv(Vertex v2)
  {
    double	v[];
    double	d;

    v = new double[3];

    v[0] = vcoord.feq[0] - v2.vcoord.feq[0];
    v[1] = vcoord.feq[1] - v2.vcoord.feq[1];
    v[2] = vcoord.feq[2] - v2.vcoord.feq[2];

    // square of the Euclidean distance

    d = v[0]*v[0] + v[1]*v[1] + v[2]*v[2];

    if(d > CONTVVEPS2)
      return(0);
    else
      return(1);
  }

  int  contev(Vertex v2, Vertex v3)
  {
    double  t[];
    int	intr;

    t = new double[1];
    intr = intrve(v2, v3, t);

    if(intr == 1)
      if(t[0] >= (double)(-EPSPARAM) && t[0] <= (1.0+EPSPARAM))
	return(1);
    return(0);
  }

  // Test for intersection of vertex v3 with line v1-v2
  // - case intersection, give line parameter t for
  // intersection point, where t=0.0 <-> v1 and t=1.0 <-> v2

  int	intrve(Vertex v2, Vertex v3, double t[])
  {
    Vertex	testv;
    double	r1[], r2[], r1r1, tprime;
    double	CONTVVEPS2_old;
    int		retcode;

    r1 = new double[3];
    r2 = new double[3];

    testv = new Vertex(parent);
    CONTVVEPS2_old = CONTVVEPS2;
    CONTVVEPS2 = CONTEVEPS2;

    // calculate direction vector for line v1-v2 in r1
    r1[0] = v2.vcoord.feq[0] - vcoord.feq[0];
    r1[1] = v2.vcoord.feq[1] - vcoord.feq[1];
    r1[2] = v2.vcoord.feq[2] - vcoord.feq[2];

    // special case: v3 is appr. equal to v1
    r1r1 = r1[0]*r1[0] + r1[1]*r1[1] + r1[2]*r1[2];
    if(r1r1 < EPS)
      {
	retcode = contvv(v3);
	t[0] = 0.0;
      }
    else
      {
	// testv = projection of v3 on the line through v1 and v2

	// calculate vector from v1 to v3
	r2[0] = v3.vcoord.feq[0] - vcoord.feq[0];
	r2[1] = v3.vcoord.feq[1] - vcoord.feq[1];
	r2[2] = v3.vcoord.feq[2] - vcoord.feq[2];

	// tprime = parameter of the projection point
	tprime = (r1[0]*r2[0] + r1[1]*r2[1] + r1[2]*r2[2]) / r1r1;
	testv.vcoord.feq[0] = vcoord.feq[0] + (float)tprime * (float)r1[0];
	testv.vcoord.feq[1] = vcoord.feq[1] + (float)tprime * (float)r1[1];
	testv.vcoord.feq[2] = vcoord.feq[2] + (float)tprime * (float)r1[2];
	t[0] = tprime;

	// check equality of projection point and v3
	retcode = testv.contvv(v3);
      }
    CONTVVEPS2 = CONTVVEPS2_old;
    return(retcode);
  }

  // make pair <v1, v2> relevant
  void	rvvadd(Vertex v2, int type, short kind)
  {
    Vertex	va, vb;
    int	        i;

    if(type == 0)
      {
	va = this;
	vb = v2;
      }
    else
      {
	vb = this;
	va = v2;
      }

    // don't add if it's already there
    for(i=0; i<parent.nab; i++)
      if(parent.vtxab[i].va == va || parent.vtxab[i].vb == vb)
	{
	  parent.vtxab[i].kind |= kind;
	  return;
	}

    // check against overflow
    if(parent.nab == REL_VV)
      {
	System.out.println("too many coincident vertices");
	return;
      }

    parent.vtxab[parent.nab].va = va;
    parent.vtxab[parent.nab].vb = vb;
    parent.vtxab[parent.nab++].kind = kind;
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

  // handle vertex-on-face intersection
  void	vtxfac(Face f, short kind, int op, int type)
  {
    HalfEdge  from, to;
    int	      i, count, class1;

    // count edges of v
    count = 0;
    from = vedge;
    do
      {
	count++;
      }
    while((from = from.mate().next) != vedge);

    // find one IN neighbor
    from = vedge;
    class1 = from.next.vertex.vclass(f.feq, kind, op, type);
    i = 0;
    while(class1 == OUT)
      {
	from = from.mate().next;
	i++;

	// all OUT ?
	if(i == count)
	  {
	    // check for special treatment of coplanar things
	    if(op == UNION && ((kind & OPPOSITE)!=0))
	      handleonon(f, op, type);

	    return;
	  }

	class1 = from.next.vertex.vclass(f.feq, kind, op, type);
      }

    // find head of OUT sequence
    i = 0;
    while(class1 == IN)
      {
	from = from.mate().next;
	i++;

	// all IN ?
	if(i == count)
	  {
	    // check for special treatment of coplanar things
	    if(op != UNION && ((kind & OPPOSITE)!=0))
	      handleonon(f, op, type);

	    return;
	  }

	class1 = from.next.vertex.vclass(f.feq, kind, op, type);
      }

    to = from;
    // advance to while its "up"
    while(class1 == OUT)
      {
	to = to.mate().next;
	class1 = to.next.vertex.vclass(f.feq, kind, op, type);
      }

    if(parent.nedg == NEDG)
      System.out.println("too many null edges");
    else
      {
	// separate IN
	parent.separ1(from, to, type);

	// make ``ring'' edge on f
	f.makering(vcoord.feq[0], vcoord.feq[1], vcoord.feq[2], (1-type));

	parent.nedg++;
      }
  }

  // classify vertex against plane, reclassify ``on''-cases
  int	vclass(Vectors eq, short kind, int op, int type)
  {
    double	delta;
    int 	ret, com;

    delta = eq.dist(this);
    com = comp(delta, 0.0, 5 * CONTFVEPS);

    if(com == 0)	// on
      if(op == UNION)
	if(type == 0)
	  ret = (kind & OPPOSITE)!=0 ? IN : OUT;
	else
	  ret = (kind & OPPOSITE)!=0 ? IN : IN;
      else
	if(type == 0)
	  ret = (kind & OPPOSITE)!=0 ? OUT : IN;
	else
	  ret = (kind & OPPOSITE)!=0 ? OUT : OUT;
    else
      ret = (com == -1) ? OUT : IN;

    return(ret);
  }

  // give special treatment for certain on-on cases
  void	handleonon(Face f, int op, int type)
  {
    HalfEdge  h;
    double    delta;
    int	      cl, prevcl;

    prevcl = 1;

    // find two consecutive "on" edges of v

    h = vedge;
    delta = f.feq.dist(h.next.vertex);
    cl = comp(delta, 0.0, CONTFVEPS);
    while(cl != 0 || prevcl != 0)
      {
	prevcl = cl;
	h = h.mate().next;
	delta = f.feq.dist(h.next.vertex);
	cl = comp(delta, 0.0, CONTFVEPS);
      }

    parent.separ2(h, type, (op == UNION) ? 1 : 0);
    f.makering(vcoord.feq[0], vcoord.feq[1], vcoord.feq[2], (1-type));
    parent.nedg++;
  }

  void	vtxvtx(Vertex v2, short kind, int op)
  {
    int		test, prevtest;
    HalfEdge	he1, he2;
    HalfEdge	h1, h2;
    int		int1[], int2[], int3[], int4[];
    int		prevint1, prevint2, prevint3, prevint4;
    int		pair=0;
    int		i, tmpflg;

    parent.n_cl = 0;
    prevtest = 2;
    prevint1 = 2;
    prevint2 = 2;
    prevint3 = 2;
    prevint4 = 2;

    int1 = new int[1];
    int2 = new int[1];
    int3 = new int[1];
    int4 = new int[1];

    // check wideness of all sectors
    vedge.checkwideness();
    v2.vedge.checkwideness();

    tmpflg = 0;
    // find first intersection if any
    he1 = vedge;
    do
      {
	he2 = v2.vedge;
	do
	  {
	    test = he1.sector_int(he2, kind, op,
				  int1, int2, int3, int4);
	    if(test!=0 && int1[0] != int2[0] && int3[0] != int4[0] &&
	       int1[0] != int3[0])
	      {
		pair = 0;
		tmpflg = 1;
		break;
	      }
	  }
	while((he2 = he2.mate().next) != v2.vedge);

	if (tmpflg == 1)
	  break;
      }
    while((he1 = he1.mate().next) != vedge);

    if (tmpflg == 0)
      return;

  l1:
    // did found an intersection
    h1 = he1;
    do
      {
	h2 = he2;
	do
	  {
	    test = h1.sector_int(h2, kind, op,
			      int1, int2, int3, int4);

	    if(
	       (pair==0 && test!=0 && int1[0] != int2[0] &&
		int3[0] != int4[0] && int1[0] != int3[0]) ||
	       (pair!=0 && int1[0] == -prevint1 && int2[0] == -prevint2 &&
		int3[0] == -prevint3 && int4[0] == -prevint4)
	       )
	      {
		if(pair == 0 && prevtest == 0 &&
		   prevint1 == int1[0] && prevint2 == int2[0] &&
		   prevint3 == int3[0] && prevint4 == int4[0])
		  {
		    // overwrite previous
		    parent.n_cl--;
		    pair = 1-pair;
		  }

		parent.cla[parent.n_cl].cl_hed = h1;
		parent.cla[parent.n_cl].cl_res = int1[0];
		parent.clb[parent.n_cl].cl_hed = h2;
		parent.clb[parent.n_cl++].cl_res = int3[0];
		prevint1 = int1[0];
		prevint2 = int2[0];
		prevint3 = int3[0];
		prevint4 = int4[0];
		prevtest = test;
		pair = 1-pair;
	      }
	  }
	while((h2 = h2.mate().next) != he2);
      }
    while((h1 = h1.mate().next) != he1);

    for(i = 0; i < parent.n_cl; i++)
      {
	he1 = parent.cla[i].cl_hed;
	he2 = parent.clb[i].cl_hed;
	int1[0] = parent.cla[i].cl_res;
	int3[0] = parent.clb[i].cl_res;

	// recover from null edges inserted
	if((i+1) < parent.n_cl)
	  {
	    h1 = parent.cla[i+1].cl_hed;
	    h2 = parent.clb[i+1].cl_hed;

	    if(parent.nedg == NEDG)
	      System.out.println("too many null edges");
	    else
	      {
		if(he1 == h1)
		  {
		    if(int3[0] == 1)
		      {
			parent.separ2(he1, 0, he1.get_orient(he2, h2));
			parent.separ1(he2, h2, 1);
		      }
		    else
		      {
			parent.separ2(he1, 0, he1.get_orient(h2, he2));
			parent.separ1(h2, he2, 1);
		      }
		  }
		else if(he2 == h2)
		  {
		    if(int1[0] == 1)
		      {
			parent.separ2(he2, 1, he2.get_orient(he1, h1));
			parent.separ1(he1, h1, 0);
		      }
		    else
		      {
			parent.separ2(he2, 1, he2.get_orient(h1, he1));
			parent.separ1(h1, he1, 0);
		      }
		  }
		else
		  {
		    if(int1[0] == 1)
		      parent.separ1(he1, h1, 0);
		    else
		      parent.separ1(h1, he1, 0);
		    if(int3[0] == 1)
		      parent.separ1(he2, h2, 1);
		    else
		      parent.separ1(h2, he2, 1);
		  }
		i++;
		parent.nedg++;
	      }
	  }
	else
	  {
	    System.out.println("did not find a pair");
	    return;
	  }
      }
  }

}


