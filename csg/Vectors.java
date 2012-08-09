package csg;

class Vectors
{
  float			  feq[];
  static final double     eps = 0.000001;
  static final double     RTOD = 57.295779513082321;
  static final double     DTOR = 0.017453292519943;
  static final double     PIPER2 = 1.570796326494896;

  Vectors()
  {
    feq = new float[4];
  }

  void vecMult(Vectors v, Mat m)
  {
    int		i, j;
    Vectors     tmp;

    tmp = new Vectors();

    for (i=0; i<4; i++)
      tmp.feq[i] = m.mat[0][i] * v.feq[0] + m.mat[1][i] * v.feq[1] +
	           m.mat[2][i] * v.feq[2] + m.mat[3][i] * v.feq[3];

    for(i=0; i<4; i++)
      feq[i] = tmp.feq[i];
  }

  double dot(Vectors v)
  {
    double	sum = 0.0;

    sum = feq[0]*v.feq[0] + feq[1]*v.feq[1] + v.feq[2]*feq[2];
    return sum;
  }

  double  dot2(Vectors v2, int drop)
  {
    switch(drop)
      {
        case 0:
	  return(feq[1]*v2.feq[1]+feq[2]*v2.feq[2]);

        case 1:
	  return(feq[0]*v2.feq[0]+feq[2]*v2.feq[2]);

        case 2:
	  return(feq[0]*v2.feq[0]+feq[1]*v2.feq[1]);
      }
    return(0.0);		// for lint, should not take place
}

  //	Calculate Euclidean distance of 3-points

  double edist(Vectors v2)
  {
    Vectors d;

    d = new Vectors();
    d.vecMinus(this, v2);
    return(Math.sqrt(d.feq[0]*d.feq[0] + d.feq[1]*d.feq[1] + d.feq[2]*d.feq[2]));
  }

  int vecNull(double eps)
  {
    if(comp(feq[0], 0.0, eps) == 0 &&
       comp(feq[1], 0.0, eps) == 0 &&
       comp(feq[2], 0.0, eps) == 0)
      return 1;
    else
      return 0;
  }

  // Is the projection of vec a null vector ?
  int vecnull2(double eps, int drop)
  {
    int	nulx, nuly, nulz;

    nulx = comp(feq[0], 0.0, eps);
    nuly = comp(feq[1], 0.0, eps);
    nulz = comp(feq[2], 0.0, eps);

    if(drop == 0 && nuly == 0 && nulz == 0)
      return(1);

    if(drop == 1 && nulx == 0 && nulz == 0)
      return(1);

    if(drop == 2 && nulx == 0 && nuly == 0)
      return(1);

    return(0);
  }

  void cross(Vectors v1, Vectors v2)
  {
    feq[0] = v1.feq[1]*v2.feq[2] - v1.feq[2]*v2.feq[1];
    feq[1] = v1.feq[2]*v2.feq[0] - v1.feq[0]*v2.feq[2];
    feq[2] = v1.feq[0]*v2.feq[1] - v1.feq[1]*v2.feq[0];
    feq[3] = 1;
  }

  double normalize()
  {
    double len;

    len = Math.sqrt(feq[0]*feq[0] + feq[1]*feq[1] + feq[2]*feq[2]);
    if (len != 0.0)
      {
	feq[0] /= (float)len;
	feq[1] /= (float)len;
	feq[2] /= (float)len;
	feq[3]  = (float)1.0;
      }
    return len;
  }

  void vecCopy(Vectors v)
  {
    feq[0] = v.feq[0];
    feq[1] = v.feq[1];
    feq[2] = v.feq[2];
    feq[3] = v.feq[3];
  }

  void vecPlus(Vectors v1, Vectors v2)
  {
    feq[0] = v1.feq[0] + v2.feq[0];
    feq[1] = v1.feq[1] + v2.feq[1];
    feq[2] = v1.feq[2] + v2.feq[2];
    feq[3] = 1;
  }

  void vecMinus(Vectors v1, Vectors v2)
  {
    feq[0] = v1.feq[0] - v2.feq[0];
    feq[1] = v1.feq[1] - v2.feq[1];
    feq[2] = v1.feq[2] - v2.feq[2];
    feq[3] = 1;
  }

  //	Are vectors approximatively equal ?
  int vecEqual(Vectors v)
  {
    Vectors tmp;

    tmp = new Vectors();

    tmp.vecMinus(this, v);
    return (tmp.vecNull(eps));
  }

  //	Are vectors exactly equal ?
  int vecSame(Vectors v2)
  {
    if(feq[0] == v2.feq[0] && feq[1] == v2.feq[1] && feq[2] == v2.feq[2])
      return(1);
    return(0);
  }

  //	Are vectors approximatively colinear ?
  int vecColin(Vectors v2, double eps)
  {
    Vectors	tmp;

    tmp = new Vectors();
    tmp.cross(this, v2);
    return(tmp.vecNull(eps));
  }

  //  Calculate signed distance of a vertex from a plane
  double dist(Vertex v)
  {
    return(feq[0]*v.vcoord.feq[0] + feq[1]*v.vcoord.feq[1] +
	   feq[2]*v.vcoord.feq[2] + feq[3]);
  }


  double dist(Vectors p)
  {
    // double	t, s, v, d, dis;

    //v = p.feq[0]*feq[0] + p.feq[1]*feq[1] + p.feq[2]*feq[2] + p.feq[3];
    //d = p.feq[0]*p.feq[0] + p.feq[1]*p.feq[1] + p.feq[2]*p.feq[2];

    //if (comp(d,0,eps) == 0)
    //return 0;
    //t = v/Math.sqrt(d);
    //return t;
    return((double)(feq[0]*p.feq[0] + feq[1]*p.feq[1] + feq[2]*p.feq[2] + feq[3]*p.feq[3]));
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

  //	Multiply vector by a scalar
  void vecScale(double sc, Vectors v)
  {
    feq[0] = (float)sc * v.feq[0];
    feq[1] = (float)sc * v.feq[1];
    feq[2] = (float)sc * v.feq[2];
  }

  //	Select largest absolute value of vec (for dropping)
  int getdrop()
  {
    double   max, tmp;
    int	     res;

    max = Math.abs(feq[0]);
    res = 0;

    if((tmp = Math.abs(feq[1])) > max)
      {
	max = tmp;
	res = 1;
      }
    if(Math.abs(feq[2]) > max)
	res = 2;

    return(res);
  }

  float qabs(float x)
  {
    return (x>0 ? x : -x);
  }

  void vecprint()
  {
    System.out.println(feq[0] + " " + feq[1] + " " + feq[2] + " " + feq[3]);
  }


  //	Calculate the rotation angles rx, ry, rz when v1 => v2.
  //	Angles (positive in clockwise direction) are calculated in
  //	the order rz, ry, rz.

  void	vecrot_v2(Vectors v2, double rx[], double ry[], double rz[])
  {
    double	alfa;
    double	x1, y1, z1;

    // rotation around the z-axis
    if((feq[0] == 0.0 && feq[1] == 0.0) || (v2.feq[0] == 0.0 && v2.feq[1] == 0.0))
      {
	rz[0] = 0.0;
	x1 = feq[0];
	y1 = feq[1];
      }
    else
      {
	alfa = Math.atan2(v2.feq[0], v2.feq[1]) - Math.atan2(feq[0], feq[1]);
	rz[0] = RTOD*alfa;
	x1 = feq[1]*Math.sin(alfa) + feq[0]*Math.cos(alfa);
	y1 = feq[1]*Math.cos(alfa) - feq[0]*Math.sin(alfa);
      }
    z1 = feq[2];

    // rotation around the y-axis
    if((x1 == 0.0 && z1 == 0.0) || (v2.feq[0] == 0.0 && v2.feq[2] == 0.0))
      ry[0] = 0.0;
    else
      {
	alfa = Math.atan2(v2.feq[2], v2.feq[0]) - Math.atan2(z1, x1);
	ry[0] = RTOD*alfa;
	z1 = x1*Math.sin(alfa) + z1*Math.cos(alfa);
      }

    // rotation around the x-axis
    if((y1 == 0.0 && z1 == 0.0) || (v2.feq[1] == 0.0 && v2.feq[2] == 0.0))
      rx[0] = 0.0;
    else
      rx[0] = RTOD*(Math.atan2(v2.feq[1], v2.feq[2]) - Math.atan2(y1, z1));
  }

  // rotation about the x-axis

  void	vecrotx(double rx[], double ry[], double rz[])
  {
    Vectors	v2;

    v2 = new Vectors();
    v2.feq[0] = 1;
    v2.feq[1] = 0;
    v2.feq[2] = 0;
    v2.feq[3] = 1;

    vecrot_v2(v2, rx, ry, rz);
  }

  // rotation about the y-axis

  void	vecroty(double rx[], double ry[], double rz[])
  {
    Vectors	v2;

    v2 = new Vectors();
    v2.feq[0] = 0;
    v2.feq[1] = 1;
    v2.feq[2] = 0;
    v2.feq[3] = 1;

    vecrot_v2(v2, rx, ry, rz);
  }

  // rotation about the z-axis

  void	vecrotz(double rx[], double ry[], double rz[])
  {
    Vectors	v2;

    v2 = new Vectors();

    v2.feq[0] = 0;
    v2.feq[1] = 0;
    v2.feq[2] = 1;
    v2.feq[3] = 1;

    vecrot_v2(v2, rx, ry, rz);
  }

  //	Determine the rotation angles (rx, ry, rz) when the global coordinate
  //	system (xyz) is transformed to be alongled with a local coordinate
  //	system (x1y1z1) which is specified by three direction vectors vx (x1-axis
  //	direction), vy, and vz. Two of them is required to determine the
  //	transformation.

  void	rotate_xyz(Vectors vy, Vectors vz,
		   double rx[], double ry[], double rz[])
  {
    Mat	    m;
    Vectors v;

    rx[0] = 0.0;
    ry[0] = 0.0;
    rz[0] = 0.0;

    m= new Mat();
    v = new Vectors();

    vecrotx(rx, ry, rz);
    if(vy != null)
      {
	v.feq[0] = vy.feq[0];
	v.feq[1] = vy.feq[1];
	v.feq[2] = vy.feq[2];
	v.feq[3] = 1;

	m.makerotate((float)rx[0], (float)ry[0], (float)rz[0]);
	m.matinv(m);
	v.vecMult(v, m);
	if(v.feq[1] != 0 || v.feq[2] != 0)
	  rx[0] += RTOD*(PIPER2 - Math.atan2(v.feq[1], v.feq[2]));
      }
    else if(vz != null)
      {
	v.feq[0] = vz.feq[0];
	v.feq[1] = vz.feq[1];
	v.feq[2] = vz.feq[2];
	v.feq[3] = 1;

	m.makerotate((float)rx[0], (float)ry[0], (float)rz[0]);
	m.matinv(m);
	    v.vecMult(v, m);
	    if(v.feq[1] != 0 || v.feq[2] != 0)
	      rx[0] -= RTOD*Math.atan2(v.feq[1], v.feq[2]);
      }
  }

}
