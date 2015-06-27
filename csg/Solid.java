package csg;

import java.awt.*;
import java.util.*;
import java.applet.Applet;

class Solid
{
  static final int      ERROR    = -1;
  static final int      SUCCESS  = -2;
  static final double   INF1     = 100000000.0;
  static final double   EPS      = 0.000001;
  static final double   EPSPARAM = 0.00001;    // parameter eps

  int		solidNum;	// solid identifier
  Face	        sfaces;		// pointer to list of faces
  Edge	        sedges;		// pointer to list of edges
  Vertex	sverts;		// pointer to list of vertices
  Box           sbox;           // Enclosing box
  short         sbits;          // status bits

  Solid	        nexts;		// pointer to next solid
  Solid	        prevs;		// pointer to previous solid
  CSG	        parent;		// pointer to the parent

  Solid(CSG par)
  {
    parent      = par;
    sfaces	= null;
    sedges	= null;
    sverts	= null;
    sbox        = null;
    sbits       = 0;
    solidNum	= parent.new_snum;
    parent.num_solid++;
    addList();
  }

  void addList()
  {
    nexts = parent.firsts;
    prevs = null;
    if (parent.firsts != null)
      parent.firsts.prevs = this;
    parent.firsts = this;
  }

  void delList()
  {
    parent.num_solid--;
    if (parent.firsts == this)
      parent.firsts = nexts;
    if (parent.firsts != null)
      nexts.prevs = prevs;
  }

  Face fFace(int fn)
  {
    Face f;

    f = sfaces;
    while(f != null)
      {
	if(f.faceNum == fn)
	  return(f);
	f = f.nextf;
      }

    return null;
  }

  void updateMaxNames()
  {
    int         oldmaxv, oldmaxf;
    Vertex	v;
    Face	f;

    oldmaxv = parent.maxv;
    oldmaxf = parent.maxf;

    for(v=sverts; v!=null; v=v.nextv)
      {
	v.vertexNum += oldmaxv;
	if(v.vertexNum > parent.maxv)
	  parent.maxv = v.vertexNum;
      }

    for(f=sfaces; f!= null; f=f.nextf)
      {
	f.faceNum += oldmaxf;
	if(f.faceNum > parent.maxf)
	  parent.maxf = f.faceNum;
      }
  }

  void scannames()
  {
    Vertex v;
    Face f;

    parent.maxv = 0;
    parent.maxf = 0;

    v = sverts;
    while(v != null)
      {
	if(v.vertexNum > parent.maxv)
	  parent.maxv = v.vertexNum;
	v = v.nextv;
      }

    f = sfaces;
    while(f != null)
      {
	if(f.faceNum > parent.maxf)
	  parent.maxf = f.faceNum;
	f = f.nextf;
      }
  }

  void	modifynames(int face_incr, int vertex_incr)
  {
    Face	f;
    Vertex	v;

    f = sfaces;
    while(f != null)
      {
	f.faceNum += face_incr;
	f = f.nextf;
      }
    v = sverts;
    while(v != null)
      {
	v.vertexNum += vertex_incr;
	v = v.nextv;
      }
  }

  void listsolid()
  {
    Face          f;
    Loop          l;
    HalfEdge      he;

    f = sfaces;
    while(f != null)
      {
	l = f.floops;
	while(l != null)
          {
	    he = l.ledge;
	    do
	      {
		System.out.println(" " + he.vertex.vertexNum + ": ("
                    + he.vertex.vcoord.feq[0] + " " + he.vertex.vcoord.feq[1]
                    + " " + he.vertex.vcoord.feq[2] + ")" );
	      }
	    while((he = he.next) != l.ledge);

	    l = l.nextl;
          }
	f = f.nextf;
      }
  }

  void    solidls(int sw)
  {
    Face        f;
    Vertex      v;
    Edge        e;
    HalfEdge    he;

    System.out.println("solid " + solidNum + ":");
    f = sfaces;
    System.out.println("faces: ");
    while(f != null)
      {
        f.facels(sw);
	System.out.println("FEQS:<" + f.feq.feq[0] + ", " + f.feq.feq[1]
			   + ", " + f.feq.feq[2] + ", " + f.feq.feq[3] + ">");
        f = f.nextf;
      }

    if(sw>1)
      {
        System.out.println("vertices:");
        v = sverts;
        while(v != null)
	  {
            System.out.println(v.vertexNum + " <" + v.vcoord.feq[0] + ", "
                        + v.vcoord.feq[1] + ", " + v.vcoord.feq[2] + ">");
            if(sw>2)
	      {
                he = v.vedge;
                if(he != null)
		  {
                    do
		      {
                        e = he.edge;
                        if(he == e.he1)
			  System.out.println(" +");
                        else
			  System.out.println(" -");
                        System.out.println( "<" + e.he1.vertex.vertexNum + ","
                                            + e.he2.vertex.vertexNum + ">");
		      }
                    while((he = he.mate().next) != v.vedge);
		  }
	      }
            v = v.nextv;
	  }
      }
    if(sw > 2)
      {
	System.out.println("edges: ");
	e = sedges;
	while(e != null)
	  {
	    System.out.println("v1, v2, f1, f2 = <" + e.he1.vertex.vertexNum
			       + ", " + e.he2.vertex.vertexNum + ", " +
			       e.he1.wloop.lface.faceNum + ", " +
			       e.he2.wloop.lface.faceNum + ">");
	    e = e.nexte;
	  }
      }
  }

  int	findf(Face f)
  {
    Face ff;

    ff = sfaces;
    while(ff != null)
      {
	if(ff == f)
	  return(1);
	ff = ff.nextf;
      }

    return(0);
  }

  int findv(Vertex v)
  {
    Vertex vtx;

    vtx = sverts;
    while(vtx != null)
      {
	if(vtx == v)
	  return(1);
	vtx = vtx.nextv;
      }
    return(0);
  }

  void  hline_tag(int fn, int v1, int v2)
  {
    Face	f;
    HalfEdge	he[];

    he = new HalfEdge[1];
    he[0] = new HalfEdge(parent, 0);
    f = fFace(fn);
    if(f != null)
      {
	if(f.fledg(v1, v2, he) != null)
	  he[0].line_tag();
      }
  }

  // Set tag of an element
  int	settag(int fno, int v1, int v2, Tag t)
  {
    Face      f;
    HalfEdge  he[];

    if(t == null)
       {
	 return(SUCCESS);
       }
    else if((f = fFace(fno)) == null)
      {
	System.out.println("settag: face not found");
	return(ERROR);
      }
    else if(t.linetag()!=0 || t.arctag()!=0 || t.polyltag()!=0)
      {
	he = new HalfEdge[1];
	he[0] = new HalfEdge(parent, 0);
	if(f.fledg(v1, v2, he) != null)
	  {
	    he[0].lsettag(t);
	  }
	else
	  {
	    System.out.println("settag: edge not found in face");
	    return(ERROR);
	  }
      }
    else
      {
	f.lsettag(t);
      }

    return(SUCCESS);
  }

  // calculate boxes of a solid
  void	eval_boxes()
  {
    Face  f;

    f = sfaces;
    while(f != null)
      {
	f.facebox();
	f = f.nextf;
      }
  }

  // make box for solid s from face boxes
  void	volbox(Box b)
  {
    Face  f;

    b.xmi = (float)INF1;
    b.ymi = (float)INF1;
    b.zmi = (float)INF1;

    b.xma = (float)(-INF1);
    b.yma = (float)(-INF1);
    b.zma = (float)(-INF1);

    f = sfaces;
    while(f != null)
      {
	f.fbox.updbox(b);
	f = f.nextf;
      }
  }

  void  strans(float tx, float ty, float tz)
  {
    Vertex v;
    Face   f;
    Mat    m;

    m = new Mat();

    // translate object
    v = sverts;
    while(v != null)
      {
	v.vcoord.feq[0] += tx;
	v.vcoord.feq[1] += ty;
	v.vcoord.feq[2] += tz;
	v = v.nextv;
      }

    // update face equations & boxes (if evaluated)
    f = sfaces;
    while(f != null)
      {
	f.fbits &= ~2;
	if(f.fbox != null)
	  f.fbox.trans_box(tx, ty, tz);
	f = f.nextf;
      }

    // update tags
    m.maketrans(tx, ty, tz);
    adjusttags(m);
  }

  void	adjusttags(Mat mat)
  {
    Face   f;
    Edge   e;
    Tag	   staglist[], ctaglist[], tagp1, tagp2;
    int i=0;

    // adjust surface tags
    f = sfaces;
    while(f != null)
      {
	i++;
	f = f.nextf;
      }

    staglist = new Tag[i];
    parent.nstags = 0;

    for (int j=0; j<i; j++)
      staglist[j] = new Tag(parent);

    f = sfaces;
    while(f != null)
      {
	tagp1 = f.surf;
	if(tagp1!=null && tagp1.not_in_staglist(staglist)==1)
	  {
	    tagp1.transformtag(mat);
	    staglist[parent.nstags++] = tagp1;
	  }
	f = f.nextf;
      }

    // adjust curve tags
    i = 0;
    e = sedges;
    while (e != null)
      {
	i++;
	e = e.nexte;
      }
    ctaglist = new Tag[i];
    parent.nctags = 0;

    for (int j=0; j<i; j++)
      ctaglist[j] = new Tag(parent);

    e = sedges;
    while(e != null)
      {
	tagp1 = e.he1.curv;
	tagp2 = e.he2.curv;
	if(tagp1!=null && tagp1 == tagp2 && tagp1.not_in_ctaglist(ctaglist)==1)
	  {
	    tagp1.transformtag(mat);
	    ctaglist[parent.nctags++] = tagp1;
	  }
	e = e.nexte;
      }
  }

  //	Test for containment of a vertex in a solid; returns
  //	-1 <=> outside, 1 <=> inside, 0 <=> on boundary
  //
  //	Method:
  //		Send a ray from the vertex and find first intersection
  //		along the ray.  If the test vertex is on the inside of that
  //		face, it is inside he solid and vice versa.

  int  contsv(Vertex v)
  {
    Face      f;
    Vertex    auxv, testv;
    double    t, d1, d2;
    double    min_t, min_d;
    int	      cont, tmpflg;

    auxv = new Vertex(parent);
    testv = new Vertex(parent);

    // make a ray upwards
    auxv.vcoord.feq[0] = v.vcoord.feq[0];
    auxv.vcoord.feq[1] = v.vcoord.feq[1];
    auxv.vcoord.feq[2] = v.vcoord.feq[2] + 1;

    min_t = 1000000;
    min_d = -1.0;

    tmpflg = 0;
    f = sfaces;
    while(f != null)
      {
	// calculate face equation if the face hasn't got one
	if(f.haseq() == 0)
	  f.flout.newell(f.feq);

	// distances of the ray end points from the face
	d1 = f.feq.dist(v);
	d2 = f.feq.dist(auxv);

	// if the ray is coplanar with the face, ignore this intersection

	if(comp((d1 - d2), 0.0, EPS) == 0)
	  {
	    f = f.nextf;
	    continue;
	  }

	// calculate parameter t of the intersection of ray with f
	t = d1 / (d1 - d2);

	if(t >= 0.0)
	  {
	    // if a nearer intersection has been found,
	    // don't bother to test for intersection

	    if(t > min_t)
	      {
		f = f.nextf;
		continue;
	      }

	    // calculate the intersection point
	    testv.vcoord.feq[0] = v.vcoord.feq[0];
	    testv.vcoord.feq[1] = v.vcoord.feq[1];
	    testv.vcoord.feq[2] = v.vcoord.feq[2] +
	      (float)t * (auxv.vcoord.feq[2] - v.vcoord.feq[2]);

	    // is the intersection within f ?
	    cont = f.contfv(testv);

	    if(cont != 0)
	      {
		// update min_t, min_d

		// if the ray hits two faces at the same
		// point (i.e., it hits an edge or a vertex,
		// take the largest distance

		if(comp(t, min_t, EPSPARAM) == 0)
		  {
		    min_d = (min_d > d1) ? min_d : d1;
		  }

		if(t < min_t)
		  {
		    min_t = t;
		    min_d = d1;
		  }
	      }
	  }
      next:
	f = f.nextf;
      }

    return(comp(min_d, 0.0, EPS));
  }

  // revert a solid's topology
  void	revert()
  {
    HalfEdge  l3;
    Face      f;
    Vertex    prev;
    Vertex    sav;
    Loop      l;
    HalfEdge  l3next;
    Edge      e;

    f = sfaces;
    while(f != null)
      {
	l = f.floops;
	while(l != null)
	  {
	    l3 = l.ledge;
	    do
	      {
		// revert the direction
		l3next = l3.next;
		l3.next = l3.prev;
		l3.prev = l3next;
		l3 = l3next;
	      }
	    while(l3 != l.ledge);
	    prev = l3.prev.vertex;
	    do
	      {
		sav = l3.vertex;
		l3.vertex = prev;
		l3.vertex.vedge = l3;
		prev = sav;
		l3 = l3.next;
	      }
	    while(l3 != l.ledge);
	    l = l.nextl;
	  }
	f = f.nextf;
      }

    // for polyline edges, switch he1 and he2
    e = sedges;
    while(e != null)
      {
	if(e.he1.curv.polyltag() != 0)
	  {
	    l3 = e.he1;
	    e.he1 = e.he2;
	    e.he2 = l3;
	  }
	e = e.nexte;
      }
  }

  // create the surface tag pointer list of the solid s.

  void surftagplist()
  {
    Face   f;
    int	   nfaces, ntagps, i;

    // find the number of faces of s and allocate storage for the
    // tag pointer list

    f = sfaces;
    for(nfaces = 0; f!=null; ++nfaces)
      f = f.nextf;

    parent.tagplist = new Tag[nfaces+1];

    // form the tag pointer list
    ntagps = 0;
    f = sfaces;
    while(f != null)
      {
	for(i = 0; (f.surf != parent.tagplist[i]) && (i < ntagps); i++);
	if(i == ntagps)
	  parent.tagplist[ntagps++] = f.surf;
	f = f.nextf;
      }
    parent.tagplist[ntagps] = null;  // end of the tag pointer list
  }

  void	merge(Solid s1, Solid s2)
  {
    Face f;

    // join faces of s1 to the list of faces of s
    f = sfaces;
    if(f != null)
      {
	while(f.nextf != null)
	  {
	    f = f.nextf;
	  }
	f.nextf = s1.sfaces;
	if(s1.sfaces != null)
	  s1.sfaces.prevf = f;
      }
    else
     sfaces = s1.sfaces;

    // scan to end of face list of s1  ...
    f = s1.sfaces;
    while(f.nextf != null)
      {
	f.fsolid = this;	// make its faces point to s
	f = f.nextf;
      }

    // ... and faces of s2 to the list
    f.nextf = s2.sfaces;
    if(s2.sfaces != null)
      s2.sfaces.prevf = f;

    while(f != null)
      {
	f.fsolid = this;	// make faces of s2 point to s
	f = f.nextf;
      }

    s1.sfaces = null;
    s2.sfaces = null;
    s1.sedges = null;
    s2.sedges = null;
    s1.sverts = null;
    s2.sverts = null;

    // recostruct other lists
    redo();
  }

  // reconstruct edge & vertex lists of a solid by its face list
  void	redo()
  {
    Face	f;
    Loop	l;
    HalfEdge	h;

    sedges = null;
    sverts = null;
    f = sfaces;
    while(f != null)
      {
	l = f.floops;
	while(l != null)
	  {
	    h = l.ledge;
	    do
	      {
		if(h.edge != null)
		  {
		    if(h == h.edge.he1)
		      elink(h.edge);
		    if(h == h.vertex.vedge)
		      vlink(h.vertex);
		  }
		else if(h == h.vertex.vedge)
		  vlink(h.vertex);
		h = h.next;
	      }
	    while(h != l.ledge);
	    l = l.nextl;
	  }
	f = f.nextf;
      }
  }

  void	vlink(Vertex v)
  {
    v.nextv = sverts;
    if(v.nextv != null)
      v.nextv.prevv = v;
    v.prevv = null;
    sverts = v;
  }

  void	elink(Edge e)
  {
    e.nexte = sedges;
    if(e.nexte != null)
      e.nexte.preve = e;
    e.preve = null;
    sedges = e;
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


  // Divide solid into two solids
  // Faces of s1 and s2 (that have been moved to their solids with
  // relink()) are assumed to belong to two distinct
  // components of s; they are classified to s1 and s2,
  // respectively.  Other faces are classified by their adjacency.
  int	detach(Solid s1, Solid s2)
  {
    Face    f;

    // mark other faces of s actually adjacent to s1
    f = s1.sfaces;
    while(f != null)
      {
	f.markfac(s1);
	f = f.nextf;
      }

    // move marked faces from s to s1
    f = sfaces;
    while(f != null)
      {
	// marked ?
	if(f.fsolid == s1)
	  {
	    // remove f from s
	    f.delList(this);
	    // link f to s1
	    f.linkNode(s1);
	  }
	else
	  {
	    // remove f from s
	    f.delList(this);
	    // link f to s2
	    f.linkNode(s2);
	  }
	f = sfaces;
      }
    return(SUCCESS);
  }

  // Remove a solid
  void	solidrm()
  {
    HalfEdge	h1, h2;
    Edge	e;
    Vertex	v1, v2;
    Face	f1, f2;
    Loop	l1, l2;
    Loop	ring;
    Loop	lout;
    int		foundhole;
    int		tempfaceno;
    int         tmpflg = 0;

    tempfaceno = 10000;

    // step 1: scan thru edges of the solid and remove them

    e = sedges;
    while(e != null)
      {
	// remove all struts removable

	while (tmpflg == 0)
	  {
	    nextstrut:

	    tmpflg = 1;
	    foundhole = 0;
	    while(e != null)
	      {
		h1 = e.he1;
		h2 = e.he2;
		l1 = h1.wloop;
		l2 = h2.wloop;
		f1 = l1.lface;
		f2 = l2.lface;
		v1 = e.he1.vertex;
		v2 = e.he2.vertex;

		// is e a strut ?
		if(f1 == f2)
		  {
		    // are the loops equal ?
		    if(l1 == l2)
		      {
			if(h2.next == h1)
			  {
			    // v1 is a strut vertex
			    h1.ljvke(h2);
			    e = sedges;
			  }
			else if(h1.next == h2)
			  {
			    // v2 is a strut
			    h2.ljvke(h1);
			    e = sedges;
			  }
			// no, so try the next one
			else
			  {
			    e = e.nexte;
			  }
		      }
		    // else e bounds a hole
		    else
		      {
			foundhole = 1;
			e = e.nexte;
		      }
		  }
		// try next edge
		else
		  e = e.nexte;
	      }   // while e ends

	    if(foundhole != 0)
	      {
		// remove a hole
		e = sedges;
		while(e != null)
		  {
		    l1 = e.he1.wloop;
		    l2 = e.he2.wloop;
		    f1 = l1.lface;
		    f2 = l2.lface;
		    if(f1 == f2)
		      {
			if(l1 != l2)
			  {
			    // the "outer" loop
			    lout = f1.flout;

			    // find the associated loop
			    h1 = e.he2;
			    l1 = h1.wloop;
			    // don't move the outer loop
			    if(l1 == lout)
			      {
				h1 = e.he1;
				l1 = h1.wloop;
			      }
			    // modify the hole to a new face
			    f1.mfmg(l1, tempfaceno++);
			    e = sedges;

			    tmpflg = 0;
			    break;
			  }
		      }
		    e = e.nexte;
		  }

		if(tmpflg == 0)
		  continue;

		// something to make it go back to the top, maybe while
		foundhole = 0;
		break;
	      }
	  }

	tmpflg = 0;
	// remove the first removable edge by kef
	e = sedges;
	if(e != null)
	  {
	    while (tmpflg == 0)
	      {
	        tryagain:

		f1 = e.he1.wloop.lface;
		f2 = e.he2.wloop.lface;
		while(f1 == f2)
		  {
		    e = e.nexte;
		    if(e == null)
		      {
			tmpflg = 1;
			// in rare cases, a kemr will be needed ..
			// find an applicable edge
			e = sedges;
			e.he1.lkemr(e.he2);

			e = sedges;
			break;
		      }
		    f1 = e.he1.wloop.lface;
		    f2 = e.he2.wloop.lface;
		  }

		if (tmpflg == 1)
		  break;

		h1 = e.he1;
		h2 = e.he2;

		if(h1.uniqmef(h2) != 0)
		  {
		    f2.handlerings(h2.wloop);
		    h1.lkef(h2);
		  }
		else if(h2.uniqmef(h1) != 0)
		  {
		    f1.handlerings(h1.wloop);
		    h2.lkef(h1);
		  }
		else
		  {
		    // try another edge
		    e = e.nexte;
		    if(e == null)
		      {
			System.out.println("solidrm: cannot continue");
		      }
		    tmpflg = 0;
		    continue;
		  }
		e = sedges;
		break;

		//dokemr:
		// in rare cases, a kemr will be needed ..
		// find an applicable edge
		//		e = sedges;

		//	e.he1.lkemr(e.he2);

		//      done:
		//	e = sedges;

	      }
	  }
      }

    // step 2: remove remaining faces and vertices
    f1 = sfaces;
    while(f1 != null)
      {
	// "outer" loop
	lout = f1.flout;
	v1 = lout.ledge.vertex;

	// remove ring vertices
	ring = f1.floops;
	while(f1.floops.nextl != null)
	  {
	    if(ring != lout)
	      {
		v2 = ring.ledge.vertex;
		mekr(f1.faceNum, v1.vertexNum, v2.vertexNum);
		kev(f1.faceNum, v1.vertexNum, v2.vertexNum);
		ring = f1.floops;
	      }
	    else
	      ring = ring.nextl;
	  }
	// next face (if any)
	f1 = f1.nextf;
      }

    // remove faces
    f1 = sfaces;
    v1 = f1.flout.ledge.vertex;
    // remove other faces than f1 (if in the list)
    while(f1.nextf != null)
      {
	f2 = f1.nextf;
	v2 = f2.flout.ledge.vertex;
	kfmrh(f1.faceNum, f2.faceNum);

	mekr(f1.faceNum, v1.vertexNum, v2.vertexNum);

	kev(f1.faceNum, v1.vertexNum, v2.vertexNum);

	f1 = sfaces;
      }
    kvsf(f1.faceNum);
  }

  int	kev(int fnr, int v1, int v2)
  {
    Face	f;
    Loop	l1;
    HalfEdge	h1, h2, he1[], he2[];

    he1 = new HalfEdge[1];
    he2 = new HalfEdge[1];

    // find needed objects, check existence
    if((f = fFace(fnr)) == null)
      {
	System.out.println("kev: face not found");
	return(ERROR);
      }

    if( (l1 = f.fledg(v1, v2, he1))==null || (f.fledg(v2, v1, he2))==null )
	{
	  System.out.println("kev: edge not found in face ");
	  return(ERROR);
	}

    h1 = he1[0];
    h2 = he2[0];

    // check that edge occurs in only one loop
    if(h1.wloop != h2.wloop)
      {
	System.out.println("kev: edge isn't a strut");
	return(ERROR);
      }

    // check whether v2 is an endpoint or not
    if(h1.next != h2)
      {
	System.out.println("kev: vertex is not an endpoint");
	return(ERROR);
      }

    // call ljvke() to do the real work
    h2.ljvke(h1);

    return(SUCCESS);
  }

  //   Kill Vertex, Shell, Face
  // - remove a face with just one vertex
  int kvsf(int fn)
  {
    Face	f;
    HalfEdge	he;

    f = fFace(fn);
    if(f == null)
      {
	System.out.println("kvsf: face not found");
	return(ERROR);
      }

    if(f.floops.nextl != null)
      {
	System.out.println("kvsf: face must have only one loop");
	return(ERROR);
      }

    he = f.floops.ledge;
    if(he.next != he || he.prev != he)
      {
	System.out.println("kvsf: face must contain only one vertex");
	return(ERROR);
      }

    // after checks, call lkvsf() to do the real work
    f.lkvsf();

    return(SUCCESS);
  }

  // Make Edge, Kill Ring
  // - create a new edge joining two loops in face number fno
  // together via vertices v1 and v2
  int	mekr(int fno, int v1, int v2)
  {
    Loop	l1, l2;
    HalfEdge	he1, he2, he[];
    Face	f;

    he = new HalfEdge[1];

    // find pointer to face, check that it exists
    if((f = fFace(fno)) == null)
      {
	System.out.println("mekr: face nonexistent");
	return(ERROR);
      }

    // find pointers to the two loops; check that they exist and
    // are distinct
    l1 = f.floop(v1, he);
    he1 = he[0];
    l2 = f.floop(v2, he);
    he2 = he[0];
    if(l1==null || l2==null)
      {
	System.out.println("mekr: vertex nonexistent in face");
	return(ERROR);
      }
    if(l1 == l2)
      {
	// special case: hole-loop -- try to find v2 in another loop
	l1.delList(f);
	l2 = f.floop(v2, he);
	he2 = he[0];
	l1.linkNode(f);
	if(l2 == null)
	  {
	    // not found, try to find v1 somewhere else
	    l2 = f.floop(v2, he);
	    he2 = he[0];
	    l2.delList(f);
	    l1 = f.floop(v1, he);
	    he1 = he[0];
	    l2.addList(f);
	    if(l1 == null)
	      {
		// not found, error
		System.out.println("mekr: vertices not in distinct loops");
		return(ERROR);
	      }
	  }
      }

    // after finding the necessary pointers, call lmekr()
    he1.lmekr(he2);
    return(SUCCESS);
  }

  int kfmrh(int f1, int f2)
  {
    Solid	oldSolid;
    Face	oldFace1, oldFace2;

    oldSolid = this;

    // get faces
    if ((oldFace1 = oldSolid.fFace(f1)) == null)
      {
	System.out.println("kfmrh: oldface1 not found");
	return ERROR;
      }

    if ((oldFace2 = oldSolid.fFace(f2)) == null)
      {
	System.out.println("kfmrh: oldface2 not found");
	return ERROR;
      }

  // face must be simple
    if(oldFace2.floops.nextl != null)
      {
	System.out.println("kfmrh: face must be simple.");
      }

    oldFace1.lkfmrh(oldFace2);
    return 1;
  }

}
