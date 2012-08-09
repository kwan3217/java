package csg;

class ObjDetails
{
  CSG          parent;

  String       name;
  String       csgStr;
  float        points[];
  int          nPoints[];
  long         ct;
  long         counter;
  float        sx, sy, sz;
  float        rx, ry, rz;
  float        tx, ty, tz;
  int          trytx=0, tryty=0, trytz=0;
  int          componentcount=0;
  int          type;             // simple -- shape or compound object.

  ObjDetails(CSG par, long ct1, long counter1, int ttype, String str1,
	     float sx1, float sy1, float sz1,
	     float rx1, float ry1, float rz1,
	     float tx1, float ty1, float tz1)
  {
    parent = par;
    ct = ct1;
    counter = counter1;
    points = new float[(int)counter];
    nPoints = new int[(int)ct];
    type = ttype;
    csgStr = new String(str1);

    switch(type)
    {
      case 1:
	name = new String("S" + parent.sphereCount);
	break;

      case 2:
	name = new String("C" + parent.cubeCount);
	break;

      case 3:
	name = new String("L" + parent.cylinderCount);
	break;

      case 4:
	name = new String("N" + parent.coneCount);
	break;

      case 5:
	name = new String("O" + parent.objCount);
	break;
    }

    setTransformValues(sx1, sy1, sz1, rx1, ry1, rz1, tx1, ty1, tz1);
    setComponentTranslateValues(0, 0, 0, 0);
  }

  ObjDetails(CSG par)
  {
    parent = par;

    setTransformValues(1, 1, 1, 0, 0, 0, 0, 0, 0);
    setComponentTranslateValues(0, 0, 0, 0);
  }

  void setTransformValues(float isx,float isy,float isz,float irx,float iry,float irz,
		     float itx,float ity,float itz)
  {
    setScaleValues(isx, isy, isz);
    setRotateValues(irx, iry, irz);
    setTranslateValues(itx, ity, itz);
  }

  void setScaleValues(float isx, float isy, float isz)
  {
    sx = isx;
    sy = isy;
    sz = isz;
  }

  void setRotateValues(float irx, float iry, float irz)
  {
    rx = irx;
    ry = iry;
    rz = irz;
  }

  void setTranslateValues(float itx, float ity, float itz)
  {
    tx = itx;
    ty = ity;
    tz = itz;
  }

  // Function to perform the scale, rotate and translate operations.
  public void performOps()
  {
    int tmp;

    sscale( sx, sy, sz);
    srotate(rx, ry, rz);
    strans(tx, ty, tz);
  }

  // Function to scale the object
  void sscale(float sx, float sy, float sz)
  {
    int i, j;
    int temp=0;

    for(i=0;i<ct;i++)
    {
      for(j=0;j<nPoints[i];j++)
      {
	points[temp] *= sx;
	points[temp+1] *= sy;
	points[temp+2] *= sz;
	temp += 3;
      }
    }
  }

  // Function to translate an object.
  void strans(float tx, float ty, float tz)
  {
    int i, j;
    int temp=0;

    for(i=0;i<ct;i++)
    {
      for(j=0;j<nPoints[i];j++)
      {
	points[temp] += tx;
	points[temp+1] += ty;
	points[temp+2] += tz;
	temp += 3;
      }
    }
  }

  // Function to rotate the object
  void srotate(float rx, float ry, float rz)
  {
    int i, j, temp=0;
    Mat m = new Mat();

    for(i=0;i<ct;i++)
    {
      for(j=0;j<nPoints[i];j++)
      {
	m.matident();
	m.mat[0][0] = points[temp];
	m.mat[0][1] = points[temp+1];
	m.mat[0][2] = points[temp+2];
	m.mat[0][3] = 1;
	m.matrotate(rx, ry, rz);
	points[temp] = (int)m.mat[0][0];
	points[temp+1] = (int)m.mat[0][1];
	points[temp+2] = (int)m.mat[0][2];
	temp += 3;
      }
    }
  }

  void copyObject(ObjDetails source)
  {
    System.arraycopy(source.points, 0, points, 0, (int)(source.counter));
    System.arraycopy(source.nPoints, 0, nPoints, 0, (int)(source.ct));

    ct = source.ct;
    counter = source.counter;

    sx = source.sx;
    sy = source.sy;
    sz = source.sz;

    rx = source.rx;
    ry = source.ry;
    rz = source.rz;

    tx = source.tx;
    ty = source.ty;
    tz = source.tz;

    trytx = source.trytx;
    tryty = source.tryty;
    trytz = source.trytz;
    componentcount = source.componentcount;

    type = source.type;
  }

  void setComponentTranslateValues(int ttx, int tty, int ttz, int cc)
  {
    trytx = ttx;
    tryty = tty;
    trytz = ttz;

    componentcount = cc;
  }
}

