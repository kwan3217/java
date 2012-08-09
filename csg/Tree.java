package csg;

class Tree
{
  int		ntype;
  Tree	        left;
  Tree	        right;
  int		stype;
  Vectors	param;
  int		ttype;
  float	        dx, dy, dz;
  int		etype;

  Tree()
  {
    left = null;
    right = null;
    param = new Vectors();
  }

}
