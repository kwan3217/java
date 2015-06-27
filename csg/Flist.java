package csg;

class Flist
{
  Face  face;
  Flist prev;
  Flist next;
  CSG   parent;

  Flist(CSG inp)
  {
    parent = inp;
    if (parent == null)
      System.out.println("Sorry, parent is NULL");
  }

  Flist()
  {
    face = null;
    prev = null;
    next = null;
  }

  void init_stack()
  {
    parent.fstack = null;
  }

  Face pop()
  {
    Flist temp=null;
    Face  ftemp=null;

    if(parent == null)
      System.out.println("Parent is NULL");

    if(parent.fstack == null)
    {
      System.out.println("FSTACK is NULL");
      return null;
    }

    temp = parent.fstack;
    ftemp = parent.fstack.face;

    if(parent == null)
      System.out.println("Parent is NULL");

    parent.fstack = temp.next;
    return ftemp;
  }

}
