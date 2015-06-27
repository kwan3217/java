package csg;


class Tag
{
  static final short LINE1 = 1;
  static final short ARC1 = 2;
  static final short PLANE1 = 3;
  static final short CYLINDER1 = 4;
  static final short CONE1 = 5;
  static final short SPHERE1 = 6;
  static final short POLYLINE1 = 9;
  static final short ROTSURF1 = 10;
  static final short BLENDSURF1 = 11;
  static final double CIRCEPS = 0.0000000001;
  static final double DTOR = 0.017453292519943;

  CSG   parent;

  short      tag_type;      // Plane1
  short      times_used;
  Poly       poly_eq;
  Vectors    pl_eq;

  short      sign;          // Cylinder1
  Mat        cy_transf;
  float      cy_rad;

  Mat       co_transf;      // Cone1
  float     co_rad;
  float     co_h;

  Mat       sph_transf;     // Sphere1
  float     sph_rad;

  Mat       ro_transf;      // Rotsurf1
  float     ro_y0;
  float     ro_a;
  float     ro_b;
  float     ro_theta;

  Mat       bl_transf;     // Blendsurf1
  Poly      bl_eq;
  float     rho;
  float     r;
  int       fno;
  short     n;

  Mat arc_transf;         // Cparc1
  Vectors p1, p2, cp;
  Vectors arc_eq;

  Lpoint lpnt;

  Tag(CSG par)
  {
    parent = par;
  }

  Tag(CSG par, float a, float b, float c, float d)    // PLANE1
  {
    parent = par;

    poly_eq = new Poly();
    pl_eq = new Vectors();

    tag_type = PLANE1;
    times_used = 0;
    poly_eq = null;
    pl_eq.feq[0] = a;
    pl_eq.feq[1] = b;
    pl_eq.feq[2] = c;
    pl_eq.feq[3] = d;
  }

  Tag(CSG par, int type, int num)            // LINE
  {
    parent = par;
    newline(num);
  }

  Tag(CSG par, float r, Mat m, short sig, int val)    // CYLINDER
  {
    parent = par;
    poly_eq = new Poly();

    if (val == 1)
      {
	cy_transf = new Mat();
	poly_eq = new Poly();

	tag_type = CYLINDER1;
	times_used = 0;
	sign = sig;
	poly_eq = null;
	cy_rad = r;
	cy_transf.matcopy(m);
      }
    else
      {
	sph_transf = new Mat();

	tag_type = SPHERE1;
	times_used = 0;
	sign = sig;
	poly_eq = null;
	sph_rad = r;
	sph_transf.matcopy(m);
      }
  }

  Tag(CSG par, float p1x, float p1y, float p1z,
      float p2x, float p2y, float p2z,
      float cx, float cy, float cz,
      float a, float b, float c, float d)         // CPARC
  {
    parent = par;
    arc_transf = new Mat();
    p1 = new Vectors();
    p2 = new Vectors();
    cp = new Vectors();
    arc_eq = new Vectors();

    tag_type = ARC1;
    times_used = 0;
    arc_transf.matident();
    cp.feq[0] = cx;
    cp.feq[1] = cy;
    cp.feq[2] = cz;
    p1.feq[0] = p1x;
    p1.feq[1] = p1y;
    p1.feq[2] = p1z;
    p2.feq[0] = p2x;
    p2.feq[1] = p2y;
    p2.feq[2] = p2z;
    cp.feq[3] = 1;
    p1.feq[3] = 1;
    p2.feq[3] = 1;
    arc_eq.feq[0] = a;
    arc_eq.feq[1] = b;
    arc_eq.feq[2] = c;
    arc_eq.feq[3] = d;
  }

  Tag(CSG par, Lpoint pstart)   // POLYLINE
  {
    parent = par;
    lpnt = new Lpoint();

    tag_type = POLYLINE1;
    times_used = 0;
    lpnt = pstart;
  }

  Tag (CSG par, float r, float h, Mat m, short sign)   // CONE
  {
    parent = par;
    poly_eq = new Poly();
    co_transf = new Mat();

    tag_type = CONE1;
    times_used = 0;
    sign = sign;
    poly_eq = null;
    co_rad = r;
    co_h = h;
    co_transf.matcopy(m);
  }

  Tag(CSG par, float y0, float a, float b,
		     float theta, Mat m, short sign)          // ROTSURF
  {
    parent = par;
    poly_eq = new Poly();
    ro_transf = new Mat();

    tag_type = ROTSURF1;
    times_used = 0;
    sign = sign;
    poly_eq = null;
    ro_y0 = y0;
    ro_a = a;
    ro_b = b;
    ro_theta = theta;
    ro_transf.matcopy(m);
  }

  Tag(CSG par, Poly poly_eq1, Mat m, float rho, float r[],
      int fno[], short n, short sign)
  {
    parent = par;
    poly_eq = new Poly();
    bl_transf = new Mat();

    tag_type = BLENDSURF1;
    times_used = 0;
    if(poly_eq != null)
      {
	poly_eq = poly_eq1.duppoly();
	bl_eq = poly_eq1.transfpoly(m);
      }
    else
      {
	poly_eq = null;
	bl_eq = null;
      }

    bl_transf.matcopy(m);

    if(r != null && fno != null)
      for(int i=0; i<n; i++)
	{
	  r[i]   = r[i];
	  fno[i] = fno[i];
	}
    rho = rho;
    n = n;
    sign = sign;
  }

  int planetag()
  {
    if (tag_type == PLANE1)
      return 1;
    else
      return 0;
  }

  int linetag()
  {
    if (tag_type == LINE1)
      return 1;
    else
      return 0;
  }

  int polyltag()
  {
    if (tag_type == POLYLINE1)
      return 1;
    else
      return 0;
  }

  int arctag()
  {
    if (tag_type == ARC1)
      return 1;
    else
      return 0;
  }

  //	Make a duplicate of a curve tag and return the duplicate

  Tag  duplicatetag()  // make a duplicate of a tag
  {
    int	    type;
    Tag	    tagp1;
    float   x1, y1, z1, x2, y2, z2, x3, y3, z3, a, b, c, d;

    type = tag_type;

    switch(type)
      {
      case LINE1:
	tagp1 = new Tag(parent, LINE1, ++parent.maxt);
	break;
      case ARC1:
	x1 = p1.feq[0];
	y1 = p1.feq[1];
	z1 = p1.feq[2];
	x2 = p2.feq[0];
	y2 = p2.feq[1];
	z2 = p2.feq[2];
	x3 = cp.feq[0];
	y3 = cp.feq[1];
	z3 = cp.feq[2];
	a = arc_eq.feq[0];
	b = arc_eq.feq[1];
	c = arc_eq.feq[2];
	d = arc_eq.feq[3];
	tagp1 = newcparc(++parent.maxt, x1, y1, z1, x2, y2, z2, x3, y3, z3,
			 a, b, c, d);
	tagp1.arc_transf.matcopy(arc_transf);
	break;
      case POLYLINE1:
	tagp1 = newpolyline(++parent.maxt, lpnt.copy_plist());
	break;
      case CONE1:
	tagp1 = new_m_cone(++parent.maxt, co_rad, co_h, co_transf, sign);
	break;
      case CYLINDER1:
	tagp1 = parent.new_m_cylinder(++parent.maxt, cy_rad, cy_transf, sign);
	break;
      case PLANE1:
	tagp1 = parent.newplane(++parent.maxt, pl_eq.feq[0], pl_eq.feq[1],
			 pl_eq.feq[2], pl_eq.feq[3]);
	break;
      case SPHERE1:
	tagp1 = new_m_sphere(++parent.maxt, sph_rad, sph_transf, sign);
	break;
      case ROTSURF1:
	tagp1 = new_m_rotsurf(++parent.maxt, ro_y0, ro_a, ro_b,
			      ro_theta, ro_transf, sign);
	break;
      default:
	System.out.println("duplicatetag: Unknown tagtype");
	return(null);
      }
    return(tagp1);
  }

  // make new line tag

  void  newline(int tag_id)
  {
    tag_type = LINE1;
    times_used = 0;
    parent.TagArray[tag_id] = this;
  }

  // kill a line tag

  void	killline()
  {
    int	tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killline: tag NIL");
      return;
    }
    if(times_used == 0)
      parent.TagArray[tag_id] = null;
  }

  // make new polyline tag

  Tag newpolyline(int tag_id, Lpoint pstart)
  {
    Tag pol;

    pol = new Tag(parent, pstart);
    parent.TagArray[tag_id] = pol;

    return pol;
  }

  // kill a polyline Tag

  void killpolyline()
  {
    Lpoint  p;
    int	    i = 0;
    int	    tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killpolyline: tag NIL");
      return;
    }

    if(times_used == 0)
      parent.TagArray[tag_id] = null;
  }

  // make new center point arc tag

  Tag newcparc(int tag_id, float p1x, float p1y, float p1z,
	       float p2x, float p2y, float p2z,
	       float cx, float cy, float cz,
	       float a, float b, float c, float d)
  {
    Tag	cpa;

  cpa = new Tag(parent, p1x, p1y, p1z, p2x, p2y, p2z,
	        cx, cy, cz, a, b, c, d);

    parent.TagArray[tag_id] = cpa;

    return cpa;
  }

  // kill a center point arc tag

  void	killcparc()
  {
    int     tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killcparc: tag NIL");
      return;
    }
    if(times_used == 0)
      parent.TagArray[tag_id] = null;
  }


  // kill a cone tag

  void	killcone()
  {
    int     tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killcone: tag NIL");
      return;
    }
    if(times_used == 0)
      {
	if(poly_eq != null)
	  poly_eq.killpoly();
	parent.TagArray[tag_id] = null;
      }
  }

  // kill a cylinder tag

  void	killcylinder()
  {
    int     tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killcylinder: tag NIL");
      return;
    }

    if(times_used == 0)
      {
	if(poly_eq != null)
	  poly_eq.killpoly();
	parent.TagArray[tag_id] = null;
      }
  }

  // kill a plane tag

  void	killplane()
  {
    int     tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killplane: tag NIL\n");
      return;
    }
    if(times_used == 0)
      {
	if(poly_eq != null)
	  poly_eq.killpoly();
	parent.TagArray[tag_id] = null;
      }
  }

  Tag new_m_sphere(int tag_id, float r, Mat m, short sign)
  {
    Tag	ptr;

    ptr = new Tag(parent, r, m, sign, 2);
    parent.TagArray[tag_id] = ptr;

    return ptr;
  }

  // kill a sphere tag

  void	killsphere()
  {
    int     tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killsphere: tag NIL\n");
      return;
    }
    if(times_used == 0)
	{
	  if(poly_eq != null)
	    poly_eq.killpoly();
	  parent.TagArray[tag_id] = null;
	}
  }

    // kill a rotsurf tag

  void	killrotsurf()
  {
    int     tag_id = tagno();

    if(tag_id < 0) {
      System.out.println("killrotsurf: tag NIL");
      return;
    }
    if(times_used == 0)
      {
	if(poly_eq != null)
	  poly_eq.killpoly();
	parent.TagArray[tag_id] = null;
      }
  }

  // initialize a blendsurf tag

  Tag  newblendsurf(int tag_id, Poly poly_eq, Mat m,
		    float rho, float r[], int fno[], short n, short sign)
  {
    Tag  ptr;
    int	 i;

    ptr = new Tag(parent, poly_eq, m, rho, r, fno, n, sign);
    parent.TagArray[tag_id] = ptr;

    return ptr;
  }

  // kill a blendsurf tag

  void		killblendsurf()
  {
    float	r[];
    int	tag_id = tagno();
    short	i, j, k, l, m, n;
    short 	deg;

    r = new float[4];

    if(tag_id < 0)
      {
	System.out.println("killblendsurf: tag NIL\n");
	return;
      }

    if(times_used > 0)
      return;

    if(poly_eq != null)
      poly_eq.killpoly();

    if(bl_eq != null)
      bl_eq.killpoly();

    parent.TagArray[tag_id] = null;
  }

  //	Remove tag t

  void	killtag()
  {
    if(times_used == 0)
      switch(tag_type)
	{
	case LINE1:
	  killline();
	  break;
	case POLYLINE1:
	  killpolyline();
	  break;
	case ARC1:
	  killcparc();
	  break;
	case PLANE1:
	  killplane();
	  break;
	case CYLINDER1:
	  killcylinder();
	  break;
	case CONE1:
	  killcone();
	  break;
	case SPHERE1:
	  killsphere();
	  break;
	case ROTSURF1:
	  killrotsurf();
	  break;
	case BLENDSURF1 :
	  killblendsurf();
	  break;
	default:
	  break;
	}
  }

  //	Get tag number of tag t

  int	tagno()
  {
    int	i;

    for(i = 0; i <= parent.maxt; i++)
      {
	if(parent.TagArray[i] == this)
	  return(i);
      }

    System.out.println("tagno: tag not found");
    return(-1);
  }

  void	transformtag(Mat mat)
  {
    int     type;
    Mat     m;
    Lpoint  plist;

    m = new Mat();

    type = tag_type;

    switch(type)
      {
      case ARC1:
	arc_transf.matmult(arc_transf, mat);
	if(testcirc((float)CIRCEPS) != 0)
	  {
	    // update the arc data
	    p1.vecMult(p1, arc_transf);
	    p2.vecMult(p2, arc_transf);
	    cp.vecMult(cp, arc_transf);
	    arc_transf.invmat(m, 4);
	    m.mattranspose(m);
	    arc_eq.vecMult(arc_eq, m);
	    if(arc_transf.determ(3) < 0.0)
	      {
		arc_eq.vecScale(-1.0, arc_eq);
		arc_eq.feq[3] = -arc_eq.feq[3];
	      }
	    arc_transf.matident();
	  }
	break;

      case POLYLINE1:
	plist = lpnt;
	do
	  plist.pntl.vecMult(plist.pntl, mat);
	while((plist = plist.pnxt) != null);
	break;

      case PLANE1:
	mat.invmat(m, 4);
	m.mattranspose(m);
	pl_eq.vecMult(pl_eq, m);
	if(poly_eq != null)
	  poly_eq = formpolyeq();
	break;

      case CYLINDER1:
	cy_transf.matmult(cy_transf, mat);
	if(poly_eq != null)
	  poly_eq = formpolyeq();
	break;

      case CONE1:
	co_transf.matmult(co_transf, mat);
	if (poly_eq != null)
	  poly_eq = formpolyeq();
	break;

      case SPHERE1:
	sph_transf.matmult(sph_transf, mat);
	if(poly_eq != null)
	  poly_eq = formpolyeq();
	break;

      case ROTSURF1:
	ro_transf.matmult(ro_transf, mat);
	if(poly_eq != null)
	  poly_eq = formpolyeq();
	break;
      case BLENDSURF1:
	bl_transf.matmult(bl_transf, mat);
	if(poly_eq != null)
	  {
	    bl_transf.invmat(m, 4);
	    poly_eq = bl_eq.transfpoly(m);
	  }
	break;

      default:
	if(type != LINE1)
	  System.out.println("transformtag: tag type unknown!");
	break;
      }
  }

  // forms the polynomial equation of a primitive surface

  Poly	formpolyeq()
  {
    float	s=0, r, R, h, a, b, c, d;
    float	y0, theta, x1, x2, y1, y2, s1, s2, s12;
    Poly	p, q;
    int	        type;
    Mat 	t;


    t = new Mat();
    q = new Poly();
    p = new Poly();

    type = tag_type;
    if(type == CYLINDER1 || type == CONE1  ||
       type == SPHERE1 || type == ROTSURF1)
      s = (float)sign;

    switch(type)
      {
      case PLANE1:
	a = pl_eq.feq[0];
	b = pl_eq.feq[1];
	c = pl_eq.feq[2];
	d = pl_eq.feq[3];
	p = new Poly(1);
	p.setcoeff((short)0, (short)0, (short)1, a);
	p.setcoeff((short)0, (short)1, (short)0, b);
	p.setcoeff((short)1, (short)0, (short)0, c);
	p.setcoeff((short)0, (short)0, (short)0, d);
	break;
      case CYLINDER1:
	r = cy_rad;
	//q = zeropoly(2);
	q = new Poly(2);
	q.setzero();

	q.setcoeff((short)0, (short)0, (short)2, (float)(-1 * s));
	q.setcoeff((short)0, (short)2, (short)0, (float)(-1 * s));
	q.setcoeff((short)0, (short)0, (short)0, (float)(r * r * s));
	cy_transf.invmat(t, 4);
	p = q.transfpoly(t);
	break;
      case CONE1:
	r = co_rad;
	h = co_h;
	//q = zeropoly(2);
	q = new Poly(2);
	q.setzero();

	q.setcoeff((short)0, (short)0, (short)2, (float)(-1.0 * s));
	q.setcoeff((short)0, (short)2, (short)0, (float)(-1 * s));
	q.setcoeff((short)2, (short)0, (short)0, (float)((r / h) * (r / h) * s));
	q.setcoeff((short)1, (short)0, (short)0, (float)(-((2.0 * r * r) / h) * s));
	q.setcoeff((short)0, (short)0, (short)0, (float)(r * r * s));
	co_transf.invmat(t, 4);
	p = q.transfpoly(t);
	break;
      case SPHERE1:
	r = sph_rad;
	//	q = zeropoly(2);
	q = new Poly(2);
	q.setzero();

	q.setcoeff((short)0, (short)0, (short)2, (float)(-1 * s));
	q.setcoeff((short)0, (short)2, (short)0, (float)(-1 * s));
	q.setcoeff((short)2, (short)0, (short)0, (float)(-1 * s));
	q.setcoeff((short)0, (short)0, (short)0, (float)(r * r * s));
	sph_transf.invmat(t, 4);
	p = q.transfpoly(t);
	break;
      case ROTSURF1:
	y0 = ro_y0;
	a = ro_a;
	b = ro_b;
	theta = (float)(DTOR * ro_theta);
	x1 = (float)(a * Math.cos(theta));
	x2 = (float)(a * Math.sin(theta));
	y1 = (float)(-(b * Math.sin(theta)));
	y2 = (float)(b * Math.cos(theta));
	d = x1 * y2 - x2 * y1;
	s1 = x1 * x1 + y1 * y1;
	s2 = x2 * x2 + y2 * y2;
	s12 = x1 * x2 + y1 * y2;

	//	q = zeropoly(4);
	q = new Poly(4);
	q.setzero();

	q.setcoeff((short)0, (short)0, (short)0, (float)(-(d*d-y0*y0*s1)*(d*d-y0*y0*s1)*s));
	q.setcoeff((short)2, (short)0, (short)0, (float)(2*(s1*y0*y0+d*d)*s1*s));
	q.setcoeff((short)4, (short)0, (short)0, (float)(-s1*s1*s));
	q.setcoeff((short)0, (short)2, (short)0, (float)(2*(s1*y0*y0+d*d)*s1*s));
	q.setcoeff((short)2, (short)2, (short)0, (float)(-2*s1*s1*s));
	q.setcoeff((short)0, (short)4, (short)0, (float)(-s1*s1*s));
	q.setcoeff((short)0, (short)0, (short)1, (float)(-4*(s1*y0*y0-d*d)*s12*y0*s));
	q.setcoeff((short)2, (short)0, (short)1, (float)(4*s1*s12*y0*s));
	q.setcoeff((short)0, (short)2, (short)1, (float)(4.0*s1*s12*y0*s));
	q.setcoeff((short)0, (short)0, (short)2, (float)(-2*(((x1*y2+x2*y1)*(x1*y2+x2*y1)+
				   s12*s12+
				   2.0*(x1*x1*x2*x2+y1*y1*y2*y2)))
				  *y0*y0-d*d*s2)*s);
	q.setcoeff((short)2, (short)0, (short)2, (float)(2*(s12*s12-d*d)*s));
	q.setcoeff((short)0, (short)2, (short)2, (float)(2*(s12*s12-d*d)*s));
	q.setcoeff((short)0, (short)0, (short)3, (float)(-4*s12*s2*y0*s));
	q.setcoeff((short)0, (short)0, (short)4, (float)(-s2*s2*s));
	ro_transf.invmat(t, 4);
	p = q.transfpoly(t);
	break;
      case BLENDSURF1:
	if(poly_eq != null)
	  return(poly_eq);
	else if(bl_eq != null)
	  {
	    bl_transf.invmat(t, 4);
	    return(bl_eq.transfpoly(t));
	  }
	else
	  return null;
      default:
	System.out.println("formpolyeq: tag type wrong!");
      }
    if(type != PLANE1)
      q.killpoly();
    return(p);
  }

  int testcirc(float tol)
  {
    short   type;
    Vectors u, v, w, w1, w2;
    float   normu, normv;

    u = new Vectors();
    v = new Vectors();
    w = new Vectors();
    w1 = new Vectors();
    w2 = new Vectors();

    type = tag_type;

    switch(type)
      {
      case ARC1:
	u.vecMinus(p1, cp);
	if(u.normalize() == 0.0)
	  return(0);

	v.cross(arc_eq, u);
	if(v.normalize() == 0.0)
	  return(0);

	u.feq[3] = 0;
	v.feq[3] = 0;

	u.vecMult(u, arc_transf);
	v.vecMult(v, arc_transf);

	normu = (float)u.normalize();
	if(normu == 0)
	  return(0);

	normv = (float)v.normalize();
	if(normv == 0)
	  return(0);

	if(comp(2*((normu - normv)/(normu + normv)), 0.0, tol) != 0)
	  return(0);
	if(comp(u.dot(v), 0, tol) != 0)
	  return(0);

	return(1);

      case CYLINDER1:
	u.feq[0] = cy_transf.mat[0][0];
	u.feq[1] = cy_transf.mat[0][1];
	u.feq[2] = cy_transf.mat[0][2];
	u.feq[3] = 0;

	v.feq[0] = cy_transf.mat[1][0];
	v.feq[1] = cy_transf.mat[1][1];
	v.feq[2] = cy_transf.mat[1][2];
	v.feq[3] = 0;

	w.feq[0] = cy_transf.mat[2][0];
	w.feq[1] = cy_transf.mat[2][1];
	w.feq[2] = cy_transf.mat[2][2];
	w.feq[3] = 0;

	if(w.normalize() == 0.0)
	  return(0);

	w1.vecScale(u.dot(w), w);
	u.vecMinus(u, w1);
	w2.vecScale(v.dot(w), w);
	v.vecMinus(v, w2);

	normu = (float)u.normalize();
	if(normu == 0)
	  return(0);

	normv = (float)v.normalize();
	if(normv == 0)
	  return(0);

	if(comp(2*((normu - normv)/(normu + normv)), 0.0, tol) != 0)
	  return(0);

	if(comp(u.dot(v), 0, tol) != 0)
	  return(0);

	return(1);
      default:
	System.out.println("testcirc: don't know how to test this curve/surface type");
	return(0);
      }
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

 Tag  new_m_cone(int tag_id, float r, float h, Mat m, short sign)
  {
    Tag  ptr;
    int	 n;

    ptr = new Tag(parent, r, h, m, sign);
    parent.TagArray[tag_id] = ptr;

    return ptr;
  }

  Tag  new_m_rotsurf(int tag_id, float y0, float a, float b,
		     float theta, Mat m, short sign)
  {
    Tag  ptr;

    ptr = new Tag(parent, y0, a, b, theta, m, sign);
    parent.TagArray[tag_id] = ptr;
    return ptr;
  }

  int	not_in_staglist(Tag taglistptr[])
  {
    int i;

    i = 0;
    while(i < parent.nstags)
      {
	if(this == taglistptr[i])
	  return(0);
	++i;
      }
    return(1);
  }

  int	not_in_ctaglist(Tag taglistptr[])
  {
    int i;

    i = 0;
    while(i < parent.nctags)
      {
	if(this == taglistptr[i])
	  return(0);
	++i;
      }
    return(1);
  }


}

