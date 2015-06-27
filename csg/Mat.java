package csg;

import java.io.*;

class Mat
{
  float mat[][];
  static final double DTOR = 0.017453292519943;

  Mat()
  {
    mat = new float[4][4];
  }

  void matident()
  {
    mat[0][0] = 1;
    mat[0][1] = 0;
    mat[0][2] = 0;
    mat[0][3] = 0;

    mat[1][0] = 0;
    mat[1][1] = 1;
    mat[1][2] = 0;
    mat[1][3] = 0;

    mat[2][0] = 0;
    mat[2][1] = 0;
    mat[2][2] = 1;
    mat[2][3] = 0;

    mat[3][0] = 0;
    mat[3][1] = 0;
    mat[3][2] = 0;
    mat[3][3] = 1;
  }

  //	Test identity of matrix
  int testident()
  {
    int i, j;

    for (i=0; i<4; i++)
      for (j=0; j<4; j++)
	{
	  if (i==j)
	    {
	      if (mat[i][j] != 1)
		return 0;
	    }
	  else if (mat[i][j] != 0)
	    return 0;
	}
    return 1;
}

  void mattrans(float tx, float ty,float tz)
  {
    Mat mt = new Mat();

    mt.maketrans(tx, ty, tz);
    matmult(this, mt);
  }

  void maketrans(float tx, float ty, float tz)
  {
    matident();
    mat[3][0] += tx;
    mat[3][1] += ty;
    mat[3][2] += tz;
  }

  void matrotate (float rx, float ry, float rz)
  {
    Mat mr = new Mat();

    mr.makerotate(rx, ry, rz);
    matmult(this, mr);
  }

  void makerotate(float ax, float ay, float az)
  {
    double sx, cx, sy, cy, sz, cz;

    ax *= (float)DTOR;
    ay *= (float)DTOR;
    az *= (float)DTOR;

    if(ax == 0.0)
      {
	sx = 0.0;
	cx = 1.0;
      }
    else
      {
	sx = Math.sin(ax);
	cx = Math.cos(ax);
      }

    if(ay == 0.0)
      {
	sy = 0.0;
	cy = 1.0;
      }
    else
      {
	sy = Math.sin(ay);
	cy = Math.cos(ay);
      }

    if(az == 0.0)
      {
	sz = 0.0;
	cz = 1.0;
      }
    else
      {
	sz = Math.sin(az);
	cz = Math.cos(az);
	}

    mat[0][0] = (float)(cy*cz);
    mat[0][1] = (float)(cy*sz);
    mat[0][2] = (float)(-sy);
    mat[0][3] = 0;

    mat[1][0] = (float)(-cx*sz + sx*sy*cz);
    mat[1][1] = (float)(cx*cz + sx*sy*sz);
    mat[1][2] = (float)(sx*cy);
    mat[1][3] = 0;

    mat[2][0] = (float)(cx*sy*cz + sx*sz);
    mat[2][1] = (float)(cx*sy*sz - sx*cz);
    mat[2][2] = (float)(cx*cy);
    mat[2][3] = 0;

    mat[3][0] = 0;
    mat[3][1] = 0;
    mat[3][2] = 0;
    mat[3][3] = 1;
  }

  void matmult(Mat m1, Mat m2)
  {
    Mat     tmp;
    int	    i, j;

    tmp = new Mat();
    for(i=0; i<4; i++)
    {
      for(j=0; j<4; j++)
	tmp.mat[i][j] = m1.mat[i][0]*m2.mat[0][j] + m1.mat[i][1]*m2.mat[1][j] +
	  m1.mat[i][2]*m2.mat[2][j] + m1.mat[i][3]*m2.mat[3][j];
    }

    for(i=0; i<4;i++)
      for(j=0;j<4;j++)
	mat[i][j] = tmp.mat[i][j];
  }

  //	Apply a scaling to matrix m
  void	matscale(float sx, float sy, float sz)
  {
    Mat	m1;

    m1 = new Mat();

    m1.makescale(sx, sy, sz);
    matmult(this, m1);
  }

  //	Set up a scaling matrix for concatenation
  void	makescale(float sx, float sy, float sz)
  {
    matident();
    mat[0][0] = sx;
    mat[1][1] = sy;
    mat[2][2] = sz;
  }

  //	Copy matrix m2 to matrix m1
  void	matcopy(Mat m2)
  {
    mat[0][0] = m2.mat[0][0];
    mat[0][1] = m2.mat[0][1];
    mat[0][2] = m2.mat[0][2];
    mat[0][3] = m2.mat[0][3];
    mat[1][0] = m2.mat[1][0];
    mat[1][1] = m2.mat[1][1];
    mat[1][2] = m2.mat[1][2];
    mat[1][3] = m2.mat[1][3];
    mat[2][0] = m2.mat[2][0];
    mat[2][1] = m2.mat[2][1];
    mat[2][2] = m2.mat[2][2];
    mat[2][3] = m2.mat[2][3];
    mat[3][0] = m2.mat[3][0];
    mat[3][1] = m2.mat[3][1];
    mat[3][2] = m2.mat[3][2];
    mat[3][3] = m2.mat[3][3];
  }

  //	Invert a rotational matrix
  void	matinv(Mat minv)
  {
    int	i, j;
    Mat m;

    m = new Mat();

    m.mat[3][3] = 1;

    for(i=0; i<3; i++)
      {
	for(j=0; j<3; j++)
	  m.mat[i][j] = mat[j][i];
	m.mat[i][3] = m.mat[3][i] = 0;
      }

    for(i=0; i<3; i++)
      for(j=0; j<3; j++)
	m.mat[3][i] -= mat[3][j] * mat[i][j];

    minv.matcopy(m);
  }

  //	Transpose a matrix
  void 	mattranspose(Mat mtransp)
  {
    int 	i, j;
    Mat 	tmp;

    tmp = new Mat();

    for(i = 0; i < 4; i++)
      for(j = 0; j < 4; j++)
	tmp.mat[i][j] = mat[j][i];
    mtransp.matcopy(tmp);
  }

  void	invmat(Mat inv, int n)
  {
    float 	detA;
    int  	i, j;

    detA = determ(n);

    for (i = 0; i < n; i++)
	for (j = 0; j < n; j++)
	    inv.mat[j][i] = cofactor(n, i, j)/detA;
  }

  //	Calculate cofactor of element ij in matrix mat.
  float	cofactor(int dim, int i, int j)
  {
    float	tmp;
    Mat		minor;
    int	minordim, m, n, k, l;

    minor = new Mat();
    minordim = dim-1;
    m = 0;

    for (k = 0; k < minordim; k++)
    {
      n = 0;
      if (m == i)
	m++;
	for (l = 0; l < minordim; l++)
	{
	  if (n == j)
	    n++;
	  minor.mat[k][l] = mat[m][n];
	  n++;
	}
	m++;
    }

    tmp = minor.determ(minordim);

    if ((i+j)%2 == 0)
      return(tmp);
    else
      return(-(tmp));
  }

  //	Calculate determinant of matrix.
  float	determ(int dim)
  {
    float  sum, alfa;
    int    i;

    if (dim == 1)
      return(mat[0][0]);

    sum = 0;
    for (i = 0; i < dim; i++)
      {
	alfa = cofactor(dim, 0, i);
	sum += mat[0][i]*alfa;
      }

    return(sum);
  }

}
