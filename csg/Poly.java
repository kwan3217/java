package csg;

class Poly
{
  short  mdeg;   // degree in the main variable
  float  ccf;    // constant polynomial
  Poly   cfs[];  // coefficients in the main variable

  Poly()
    {
     cfs = new Poly[1000];
    }

  Poly(int n)
  {
  }

  Poly(short n)
  {
    rnewcoeffs((short)3, n);
  }

  Poly newpoly(short n)
    {
      return(newauxpoly((short)3, n));
    }

  Poly 	newauxpoly(short m, short n)  // a new degree n poly with m = 0, 1, 2, 3 vars.
    {
      Poly   p;

      p = new Poly();

      p.rnewcoeffs(m, n);
      return p;
    }

  // recursive creation of new polynomial coefficients for polynomial p;
  //   n = deg(p), m is the number of variables

  void	rnewcoeffs(short m, short n)
    {
      int	i;

      if(n == 0 || m == 0)  // a constant polynomial
	{
	  mdeg = 0;
	  return;
	}
	mdeg = n;
	cfs = new Poly[n+1];
	for(i=0; i<=n; i++)
	  {
	    cfs[i] = new Poly();
	    cfs[i].rnewcoeffs((short)(m - 1), (short)(n - i));
	  }
    }

  Poly transfpoly(Mat t)
    {
      short n;
      short i;
      Poly x1, x2, x3, t0, t1, t2, t3, s, q;
      Poly pl[];

      n = mdeg;

      if(n == 0)
	return(duppoly());  // p was a constant polynomial

      pl = new Poly[3];

      // the new variables

      x1 = monomial(1);
      x2 = monomial(2);
      x3 = monomial(3);

      //      s = newpoly(1);
      s = new Poly(1);

      i = 0;
      while(i <= 2)
	{
	  //	  pl[i] = newpoly(1);
	  pl[i] = new Poly(1);

	  t0 = constpoly(t.mat[0][i]);
	  t1 = constpoly(t.mat[1][i]);
	  t2 = constpoly(t.mat[2][i]);
	  t3 = constpoly(t.mat[3][i]);
	  polymult(t0, x1, pl[i]);
	  polymult(t1, x2, s);
	  polysum(s, pl[i], pl[i]);
	  polymult(t2, x3, s);
	  polysum(s, pl[i], pl[i]);
	  polysum(t3, pl[i], pl[i]);  // pl[i] is the new xi
	  t0.killpoly();
	  t1.killpoly();
	  t2.killpoly();
	  t3.killpoly();
	  ++i;
	}

      x1.killpoly();
      x2.killpoly();
      x3.killpoly();
      s.killpoly();

      q = expand(pl);
      for(i = 0; i <= 2; ++i)
	pl[i].killpoly();
      return(q);
}


  // Expand a polynomial in the new variables

  Poly expand(Poly pl[])
    {
      short n;
      Poly r, s, q, q1, q2;

      n = mdeg;

      if(n == 0)
	return(constpoly(ccf));

      r = pl[2];  // the main new variable
      plpermute(pl);

      q = constpoly(cfs[n].ccf);
      while(n-- != 0)
	{
	  s = cfs[n];
	  q1 = new Poly(q.mdeg + 1);
	  polymult(r, q, q1);
	  q.killpoly();
	  q = new Poly(q1.mdeg);
	  q2 = s.expand(pl);
	  polysum(q1, q2, q);
	  q2.killpoly();
	  q1.killpoly();
	}
      plunpermute(pl);
      return q;
    }

  // Permute a poly list cyclically in the forward direction

    void plpermute(Poly pl[])
    {
      Poly p;

      p = pl[2];
      pl[2] = pl[1];
      pl[1] = pl[0];
      pl[0] = p;
    }

  // Permute a poly list cyclically in the backward direction

  void plunpermute(Poly pl[])
    {
      Poly p;

      p = pl[0];
      pl[0] = pl[1];
      pl[1] = pl[2];
      pl[2] = p;
    }

  void	killpoly()
    {
      rkillpoly();
    }

  void	rkillpoly()  // recursive kill of a polynomial
    {
      short i;

      if(mdeg > 0)
	{
	  for(i=0; i<=mdeg; i++)
	    cfs[i].rkillpoly();
	}
    }

  void polymult(Poly p1, Poly p2, Poly p3)
    {
      short n1, n2, n3, n;
      short i, j, m;
      Poly p;

      n1 = p1.mdeg;
      n2 = p2.mdeg;
      n3 = p3.mdeg;

      // arrange n1 <= n2
      if(n1 > n2)
	{
	  i = n1;
	  n1 = n2;
	  n2 = i;
	  p = p1;
	  p1 = p2;
	  p2 = p;
	}

      if(n1 + n2 != n3)
	  System.out.println("polymult: degree of p3 incorrect");

      if(n2 == 0)  // p1 and p2 constant polynomials
	{
	  p3.ccf = (p1.ccf) * (p2.ccf);
	  return;
	}

      if(n1 == 0)  // p1 constant polynomial, p2 not
	{
	  i = 0;
	  while(i <= n2)
	    {
	      polymult(p1,p2.cfs[i],p3.cfs[i]);
	      ++i;
	    }
	  return;
	}

      // both p1 and p2 are non-constant polynomials

      p = new Poly(1);

      i = 0;
      while(i < n1)
	{
	  polymult(p1.cfs[0], p2.cfs[i], p3.cfs[i]);

	  // create storage for intermediate results
	  if(i > 0)
	    {
	      n = (short)(n1 + n2 - i); // degree of (p3->cflist.cfs)[i]
	      m = vars(p3.cfs[i]); // no. of vars. of (p3->cflist.cfs[i]
	      p = newauxpoly(m,  n);
	    }

	  j = 1;
	  while(j <= i)
	    {

	      polymult(cfs[j], p2.cfs[i - j], p);
	      polysum(p, p3.cfs[i], p3.cfs[i]);
	      ++j;
	    }
	  if(i > 0)
	    p.killpoly();
	  ++i;
	}

      while(i <= n2)
	{

	  polymult(p1.cfs[0], p2.cfs[i], p3.cfs[i]);

	  // create storage for intermediate results
	  n = (short)(n1 + n2 - i); // degree of (p3->cflist.cfs)[i]
	  m = vars(p3.cfs[i]); // no. of vars. of (p3->cflist.cfs[i]
	  p = newauxpoly(m, n);

	  j = 1;
	  while(j <= n1)
	    {
	      polymult(cfs[j], p2.cfs[i - j], p);
	      polysum(p, p3.cfs[i], p3.cfs[i]);
	      ++j;
	    }
	  p.killpoly();
	  ++i;
	}

      while(i < n1 + n2)
	{

	  polymult(cfs[i-n2], p2.cfs[n2], p3.cfs[i]);

	  // create storage for intermediate results
	  n = (short)(n1 + n2 - i); // degree of (p3->cflist.cfs)[i]
	  m = vars(p3.cfs[i]); // no. of vars. of (p3->cflist.cfs[i]
	  p = newauxpoly(m, n);

	  j = (short)(i - n2 + 1);
	  while(j <= n1)
	    {
	      polymult(cfs[j], p2.cfs[i - j], p);
	      polysum(p, p3.cfs[i], p3.cfs[i]);
	      ++j;
	    }
	  p.killpoly();
	  ++i;
	}
      polymult(p1.cfs[n1], p2.cfs[n2], p3.cfs[n1 + n2]);
    }

  short vars(Poly p)
    {
      short i;

      if(p.mdeg == 0)
	return((short)0);

      i = 0;
      while(p.mdeg > 0)
	{
	  p = p.cfs[0];
	  ++i;
	}
      return(i);
    }

  void polysum(Poly p1, Poly p2, Poly p3)
    {
      short  n1, n2, i;
      Poly p;

      n1 = p1.mdeg;
      n2 = p2.mdeg;

      // arrange n1 <= n2
      if(n1 > n2)
	{
	  i = n1;
	  n1 = n2;
	  n2 = i;
	  p = p1;
	  p1 = p2;
	  p2 = p;
	}

      if(p3.mdeg != n2)
	  System.out.println("polysum: degree of p3 incorrect");

      if(n2 == 0)  // p1 and p2 constant polys
	{
	  p3.ccf = p1.ccf + p2.ccf;
	  return;
	}

      if(n1 == 0)  // p1 constant poly, p2 not
	{
	  polysum(p1, p2.cfs[0], p3.cfs[0]);
	  i = (short)(n1 + 1);
	  while(i <= n2)
	    {
	      p2.cfs[i].copypoly(p3.cfs[i]);
	      ++i;
	    }
	  return;
	}

      i = 0;
      while(i <= n1)
	{
	  polysum(p1.cfs[i], p2.cfs[i], p3.cfs[i]);
	  ++i;
	}
      while(i <= n2)
	{
	  p2.cfs[i].copypoly(p3.cfs[i]);
	  ++i;
	}
    }

  void copypoly(Poly p2)
    {
      short  n;
      short i;

      n = mdeg;

      if(n != p2.mdeg)
	  System.out.println("copypoly: degrees don't match");

      if(n == 0)
	{
	  p2.ccf = ccf;
	  return;
	}

      i = 0;
      while(i <= n)
	{
	  cfs[i].copypoly(p2.cfs[i]);
	  ++i;
	}
    }

  Poly duppoly()
    {
      Poly q;

      q = new Poly(mdeg);
      copypoly(q);
      return(q);
    }

  Poly constpoly(float a)
    {
      Poly p;

      p = new Poly(0);
      p.ccf = a;
      return(p);
    }

  Poly	monomial(int i)
    {
      Poly p;

      p = new Poly(1);
      p.setzero();
      //p = zeropoly(1);
      switch(i)
	{
	case 1:
	  p.setcoeff((short)0, (short)0, (short)1, 1);
	  break;
	case 2:
	  p.setcoeff((short)0, (short)1, (short)0, 1);
	  break;
	case 3:
	  p.setcoeff((short)1, (short)0, (short)0, 1);
			break;
	}
      return(p);
    }

  Poly zeropoly(short n)
    {
      Poly p;

      p = new Poly(n);
      p.setzero();
      return(p);
    }

  // sets the coefficients of a polynomial all equal to zero

  void setzero()
    {
      short  n;
      int    i;

      n = mdeg;

      if(n == 0)
	{
	  ccf = 0;
	  return;
	}

      i = 0;
      while(i <= n)
	{
	  cfs[i].setzero();
	  ++i;
	}
    }

  // Gives the coefficient of (x**k)*(y**j)*(z**i) in a non-constant
  // polynomial and, in case of a constant polynomial, its constant value

  float getcoeff(short i, short j, short k)
    {
      short n;

      n = mdeg;

      if(n == 0)
	return(ccf);

      if(i == n)
	return(cfs[i].ccf);
      else if(j == n - i)
	return(cfs[i].cfs[j].ccf);
      else
	return(cfs[i].cfs[j].cfs[k].ccf);
    }

  // Sets the coefficient of (x**k)*(y**j)*(z**i) in a non-constant
  // polynomial and the constant value of a constant polynomial

  void setcoeff(short i, short j, short k, float a)
    {
      short n;

      n = mdeg;

      if(n == 0)
	{
	  ccf = a;
	  return;
	}

      if(i == n)
	cfs[i].ccf = a;
      else if(j == n - i)
	cfs[i].cfs[j].ccf = a;
      else
	cfs[i].cfs[j].cfs[k].ccf = a;
    }
}

