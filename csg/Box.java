package csg;

class Box
{
  float     xmi, ymi, zmi;           // corners of the box
  float     xma, yma, zma;
  Face      bface;                   // back pointer to face data
  CSG       parent;                  // pointer to parent

  Box(CSG par)
  {
    parent = par;
    bface = new Face(parent);
  }

  Box(CSG par, Face f)
  {
    parent = par;
    if(f != null)
      {
	bface = f;
	f.fbox = this;
      }
    else
      bface = null;
  }

  void delList(Face f)
  {
    f.fbox = null;
  }

  // widen box by eps
  void	widenbox(float eps)
  {
    xmi -= eps;
    ymi -= eps;
    zmi -= eps;
    xma += eps;
    yma += eps;
    zma += eps;
  }

  // update box b
  void	updbox(Box b)
  {
    if(xmi < b.xmi)
      b.xmi = xmi;
    if(ymi < b.ymi)
      b.ymi = ymi;
    if(zmi < b.zmi)
      b.zmi = zmi;
    if(xma > b.xma)
      b.xma = xma;
    if(yma > b.yma)
      b.yma = yma;
    if(zma > b.zma)
      b.zma = zma;
  }

  // translate box b by dx dy dz
  void	trans_box(float dx, float dy, float dz)
  {
    xmi += dx;
    ymi += dy;
    zmi += dz;
    xma += dx;
    yma += dy;
    zma += dz;
  }

  // check intersection of boxes
  int	intbb1(Box b2)
  {
    int	ret;

    ret = 1;
    if(xmi > b2.xma)
      ret = 0;
    else if(ymi > b2.yma)
      ret = 0;
    else if(zmi > b2.zma)
      ret = 0;
    else if(xma < b2.xmi)
      ret = 0;
    else if(yma < b2.ymi)
      ret = 0;
    else if(zma < b2.zmi)
      ret = 0;

    return(ret);
  }

  // does edge intersect box ?
  int	intbe(Edge e)
  {
    Vertex  v1, v2;
    float   mi, ma, tmp;

    v1 = e.he1.vertex;
    v2 = e.he2.vertex;

    mi = v1.vcoord.feq[0];
    ma = v2.vcoord.feq[0];
    if(mi > ma)
      {
	tmp = mi;
	mi = ma;
	ma = tmp;
      }
    if(ma < xmi)
      return(0);
    if(mi > xma)
      return(0);

    mi = v1.vcoord.feq[1];
    ma = v2.vcoord.feq[1];
    if(mi > ma)
      {
	tmp = mi;
	mi = ma;
	ma = tmp;
      }
    if(ma < ymi)
      return(0);
    if(mi > yma)
      return(0);

    mi = v1.vcoord.feq[2];
    ma = v2.vcoord.feq[2];
    if(mi > ma)
      {
	tmp = mi;
	mi = ma;
	ma = tmp;
      }
    if(ma < zmi)
      return(0);
    if(mi > zma)
      return(0);
    return(1);
  }

  void centerOfBox(int c[])
  {
    c[0] = (int)(xmi+xma)/2;
    c[1] = (int)(ymi+yma)/2;
    c[2] = (int)(zmi+zma)/2;
  }

}

