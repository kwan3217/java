package csg;

class Lpoint
{
  Vectors  pntl;
  Lpoint   pnxt;

  Lpoint()
  {
    pntl = new Vectors();
    pnxt = new Lpoint();
  }

  //	Add a point to the list

  Lpoint addlpoint(float x, float y, float z, float w)
  {
    Lpoint p;

    p = new Lpoint();

    p.pnxt = this;
    p.pntl.feq[0] = x;
    p.pntl.feq[1] = y;
    p.pntl.feq[2] = z;
    p.pntl.feq[3] = w;

    return(p);
  }

  // copy a point list

  Lpoint  copy_plist()
  {
    Lpoint  newplist;
    Lpoint  newpnt, pnt;

    pnt = this;
    //    if(!(pnt = this)) return((Lpoint *)NIL);

    newpnt = new Lpoint();
    newplist = new Lpoint();
    newpnt.pntl.feq[0] = pnt.pntl.feq[0];
    newpnt.pntl.feq[1] = pnt.pntl.feq[1];
    newpnt.pntl.feq[2] = pnt.pntl.feq[2];
    newpnt.pntl.feq[3] = pnt.pntl.feq[3];

    while((pnt = pnt.pnxt) != null)
      {
	newpnt = new Lpoint();
	newpnt.pnxt = new Lpoint();
	newpnt.pntl.feq[0] = pnt.pntl.feq[0];
	newpnt.pntl.feq[1] = pnt.pntl.feq[1];
	newpnt.pntl.feq[2] = pnt.pntl.feq[2];
	newpnt.pntl.feq[3] = pnt.pntl.feq[3];
      }
    newpnt.pnxt = null;

    return(newplist);
  }

  //	reorient a point list

  Lpoint  reorient_plist()
  {
    Lpoint  newplist, p;

    p = this;
    newplist = null;
    while(p != null)
      {
	newplist = newplist.addlpoint(p.pntl.feq[0], p.pntl.feq[1],
			     p.pntl.feq[2], p.pntl.feq[3]);
	p = p.pnxt;
      }
    return(newplist);
  }

  //	the length of a point list

  int	plist_length()
  {
    Lpoint  p;
    int     n = 0;

    p = this;
    while(p != null)
      {
	p = p.pnxt;
	++n;
      }
    return(n);
  }

  //	transform a point list

  void	transf_plist(Mat transf)
  {
    Lpoint p;

    p = this;
    while(p != null)
      {
	p.pntl.feq[3] = 1;
	p.pntl.vecMult(p.pntl, transf);
	p = p.pnxt;
      }
  }

}
