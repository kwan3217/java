package csg;

class Nedge
{
  CSG   parent;
  Edge  nea;   // on solid a
  Edge  neb;   // on solid b

  Nedge(CSG par)
  {
    parent = par;
    nea = new Edge(parent);
    neb = new Edge(parent);
  }
}
