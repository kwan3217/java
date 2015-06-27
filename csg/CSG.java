package csg;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.io.*;

// Main applet that contains the initialization for the User
// Interface and the Canvas components
public class CSG extends Applet implements Runnable
{
  int                 trytx=0, tryty=0, trytz=0, componentcount=0;
  Mat                 tmat = new Mat(), smat = new Mat();
  boolean             painted = true;
  ObjDetails          Objects[];
  csgUI		      screen;
  csgCanvas	      canvas;
  Mat                 multMat = new Mat();
  int	              prevx, prevy;
  float		      xtheta, ytheta, xangle=0, yangle=0;
  Thread	      drawThread = null;
  int		      objectCount = 0;
  int		      sphereCount=0, coneCount=0, cylinderCount=0, cubeCount=0, objCount=0;
  static int	      selectedObject = -1;
  int		      flag=5, eventFlag=2, deletedCount=0;
  int		      num_solid=0, num_faces=0, num_loops=0, num_edges=0;
  int		      num_vertex=0, num_halfedge = 0;
  int		      new_snum = 1;
  Solid		      firsts=null;	// head of the list of all solids
  // CSG.c
  int		      maxs;	        // largest solid number
  int		      maxf=0;	        // largest face number
  int		      maxv=0;	       	// largest vertex number
  Tree		      firstTree;
  static final double PI = 3.14159265;
  static final double eps = 0.000001;
  static final double RTOD = 57.295779513082321;
  static int	      q = 0;
  static int nstags=0, nctags=0;

  String	      str;
  static int          nstatic=0;

  // GLOBALS for Boolean.c
  HalfEdge		hithe;
  Vertex		hitvertex = null;
  Edge                  hitedge = null;

  Sonv1			sonvv[];
  Sonv2			sonva[], sonvb[];
  int			nvtx;
  int			nvtxa, nvtxb;
  Edge			sonea[], soneb[];
  int			nedgea, nedgeb;
  Face		        sonfa[], sonfb[];
  static int		nfaca, nfacb;      // number of null faces
  static HalfEdge	endsa[];
  static HalfEdge	endsb[];
  static int  		nenda, nendb;
  static int		cura=0, curb=0;
  Sonv3			nbr[];
  int			nnbr;

  Tag                   tagplist[] = null;

  // Globals for Boolean2.c
  Nb			nba[], nbb[];
  int			nnba, nnbb;
  Sector		sectors[];
  int			nsectors;

  Flist			fstack;
  Stack                 stack;

  /////////////////////////////// NEW GWB Gglobl3.h////////////////////////
  static final int      PLAIN = 0;
  static final int      OPPOSITE = 1;
  static final int      NONOPPOSITE = 2;

  static final int      IN = 1;
  static final int      ON = 0;
  static final int      OUT = -1;
  static final double   INF1 = 100000000.0;
  static final int      UNION = 1;
  static final int      INTER = 2;
  static final int      MINUS = 3;
  static final int      ERROR = -1;
  static final int      SUCCESS = -2;
  static final double   EPS_init = 0.0000025;
  static final double   CONTVVEPS = 0.00005;

  static final short    LINE1 = 1;
  static final short    ARC1 = 2;
  static final short    PLANE1 = 3;
  static final short    CYLINDER1 = 4;
  static final short    CONE1 = 5;
  static final short    SPHERE1 = 6;
  static final short    POLYLINE1 = 9;
  static final short    ROTSURF1 = 10;
  static final short    BLENDSURF1 = 11;
  static final double   CIRCEPS = 0.0000000001;

  int	n_eopdone;	// import from setop_2.c
  Solid                 sha, shb;
  Box                   boxa, boxb, gbox;
  Bug                   bug1, bug2;
  Bug                   newbug1, newbug2; // current ends of an intersection polygon
  Bug                   loose[];
  int                   nloose=0;
  int                   loopopen=0;
  int                   Faceindexa=0, Faceindexb=0, Top;
  int                   nedg=0;
  Face                  rel_fb[];
  Nedge                 lone[];
  Relv                  rel_fa[];
  Relvf                 rel_fva[];   // vertices of a on face of b
  Relvf                 rel_fvb[];   // vertices of b on face of a
  Relef                 vtxab[];     // coincident vertices
  Eop                   eopdone[];
  Cla_type              cla[], clb[];
  int                   nrel_fa=0;
  int                   nrel_fb=0;
  int                   na=0, nb=0;  // number of rel_fva, rel_fvb
  int                   nab=0;       // number of coincident vertices;
  Face                  lonfa[];     // list of null faces on sha
  Face                  lonfb[];     // list of null faces on shb

  Nedge	                list[];      // list of null edges
  int		        n_list=0;    // # of elements in list

  int    		isok[];

  Relint                rel_int[];
  int                   n_rel_int;   // current no of intersections
  int                   n_cl=0;      // number of classified sector pairs
  Tag                   TagArray[];
  int                   maxt=0;
  int                   Gtestspecials=0;  // test for boundary case
  short                 maxv1=0;          // initial max vertex number
  double                EPS_old;          // storage of prevailing epsilon

  // Initializes the applet.
  public void init()
  {
    isok = new int[200];

    stack = new Stack();

    sonvv	= new Sonv1[10000];
    sonva	= new Sonv2[10000];
    sonvb	= new Sonv2[10000];

    sonea	= new Edge[10000];
    soneb	= new Edge[10000];
    sonfa	= new Face[20000];
    sonfb	= new Face[20000];
    lonfa       = new Face[100];
    lonfb       = new Face[100];
    endsa	= new HalfEdge[10000];
    endsb	= new HalfEdge[10000];
    lone        = new Nedge[2000];       // list of null edges
    rel_fa      = new Relv[1000];
    rel_fb      = new Face[10000];
    rel_fva     = new Relvf[1000];
    rel_fvb     = new Relvf[1000];
    vtxab       = new Relef[1000];
    rel_int     = new Relint[30];
    sectors     = new Sector[15];
    eopdone     = new Eop[200];
    cla         = new Cla_type[50];
    clb         = new Cla_type[50];
    list        = new Nedge[2000];
    loose       = new Bug[200];

    TagArray = new Tag[10000];

    for(int i=0; i< 15; i++)
      {
	sectors[i] = new Sector();
	rel_int[i] = new Relint();
	rel_int[i+15] = new Relint();
      }

    for(int i=0; i< 200; i++)
      {
	eopdone[i] = new Eop();
	loose[i] = new Bug(this);
      }

    for(int i=0; i< 50; i++)
      {
	cla[i] = new Cla_type();
	clb[i] = new Cla_type();
	lonfa[i] = new Face(this);
	lonfa[i+50] = new Face(this);
	lonfb[i] = new Face(this);
	lonfb[i+50] = new Face(this);
      }

    boxa = new Box(this);
    boxb = new Box(this);
    gbox = new Box(this);

    bug1 = new Bug(this);
    bug2 = new Bug(this);

    newbug1 = new Bug(this);
    newbug2 = new Bug(this);

    nbr     = new Sonv3[500];
    nba	    = new Nb[500];
    nbb     = new Nb[500];

    for(int i=0; i<500; i++)
      {
	rel_fa[i] = new Relv();
	rel_fa[i+500] = new Relv();
	vtxab[i] = new Relef();
	vtxab[i+500] = new Relef();
	rel_fva[i] = new Relvf();
      	rel_fva[i+500] = new Relvf();
	rel_fvb[i] = new Relvf();
	rel_fvb[i+500] = new Relvf();
       	list[i] = new Nedge(this);
	lone[i] = new Nedge(this);
      }

    Objects = new ObjDetails[15];

    // Set out the layout for the components on the display screen.
    setLayout(new GridLayout(2,0));

    // Instantiate the User Interface and Canvas components
    canvas = new csgCanvas(this);
    screen = new csgUI(this);

    // Add the User Interface and the Canvas to the screen.
    add(canvas);
    add(screen);

    validate();
  }

  // The applet's run module
  public void run()
  {
    try
      {
	// Set priority for the current thread
	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	// Call functions to draw the axes initially in the canvas.
	canvas.drawAxes();
	// This will actually draw the axes.
      }
    catch(Exception e)
      {
	System.out.println("Error in the main RUN method");
      }
  }

  // Start the applet
  public void start()
  {
    if (drawThread == null && objectCount == 0)
      {
	drawThread = new Thread(this);
	drawThread.start();
      }
  }

  // Stop the applet
  public void stop()
  {
    drawThread = null;
  }

  private synchronized void setPainted()
  {
    painted = true;
    notifyAll();
  }

  public int csgUIdraw(int number)
  {
    Solid sol;
    String tempStr;

    if (number != 0)
      {
	try
	{
	  switch(number)
	  {
	    case 1:
	      str = new String("O(3,30,15,15,0)");   // Sphere
	      canvas.flag = 0;
	      canvas.mouseDrag = 0;
	      break;

	    case 2:
	      str = new String("O(1,50,50,50,0)");   // Cube
	      canvas.flag = 0;
	      canvas.mouseDrag = 0;
	      break;

	    case 3:
	      str = new String("O(2,15,50,15,0)");   // Cylinder
	      canvas.flag = 0;
	      canvas.mouseDrag = 0;
	      break;

	    case 4:
	      str = new String("O(4,30,50,15,0)");   // Cone
	      canvas.flag = 0;
	      canvas.mouseDrag = 0;
	      break;

	    case 5:
	      break;
	  }

	  tempStr = new String(str);

	  sol = makeSolid(str);
	  str = tempStr;
	  if (sol == null)
	    return -1;

	  if (number == 5)
	  {
	    canvas.flag = 1;                       //combined object
	    canvas.mouseDrag = 0;
	  }
	  convertToPoints(sol, number);

	  if(number == 5)
	  {
	    Objects[objectCount-1].setComponentTranslateValues(trytx, tryty, trytz,
							       componentcount);
	  }
	  else
	    Objects[objectCount-1].setTransformValues(1, 1, 1, 15, 15, 0, (objectCount-1)*50, 0, 0);
	  canvas.NewDraw(Objects[objectCount-1]);
	}
	catch(Exception e)
	{
	  System.out.println("Error while drawing the object");
	  return -1;
	}
      }
    canvas.drawAxes();
    canvas.repaint();
    return 0;
  }

  String infixToPrefix(String str)
  {
    String prefix, temp;
    StringBuffer rev;
    StringBuffer prefixtemp = new StringBuffer(50);
    String symb, topsymb;
    int i, count=0, und[];

    und = new int[1];

    // First reverse the string
    rev = new StringBuffer(str);
    rev.reverse();
    temp = new String(rev);

    // Tokenize the input string
    StringTokenizer tokens = new StringTokenizer(temp," \n\t\r()",true );
    count = tokens.countTokens();

    for(i=0; i< count; i++)
    {
      // get next input symbol
      symb = tokens.nextToken();

      // If any of these delimiters show up as a token, ignore them.
      if( symb.equals(" ") || symb.charAt(0) == '\n' || symb.charAt(0) == '\t')
	continue;

      // If we have a operator
      if ( symb.equalsIgnoreCase("U")==false && symb.equalsIgnoreCase("I")==false
	   && symb.equalsIgnoreCase("M")==false && symb.equalsIgnoreCase("(")==false
	   && symb.equalsIgnoreCase(")")==false )
      {
	// add to the prefix string
	prefixtemp.append(symb);
	prefixtemp.append(" ");
      }
      else
      {
	// see what's on top of stack
	topsymb = stack.pop(und);

	while( und[0]==0 && prec(topsymb, symb) )
	{
	  // add to prefix string
	  prefixtemp.append(topsymb);
	  prefixtemp.append(" ");
	  topsymb = stack.pop(und);
	}

	if(und[0] == 0)
	  stack.push(topsymb);

	if ( und[0]==1  || symb.equals("(")==false)
	  stack.push(symb);
	else
	  topsymb = stack.pop(und);
      }
    }

    // pop all the remaining symbols from the stack into the prefix string
    while(stack.top != -1)
    {
      symb = stack.pop(und);
      prefixtemp.append(symb);
      prefixtemp.append(" ");
    }

    // reverse the prefix string, since we were processing from left to right
    prefixtemp.reverse();

    prefix = new String(prefixtemp);
    return(prefix);
  }

  boolean prec(String str1, String str2)
  {
    //Whatever is coming in left has higher precedence.
    if(str1.equalsIgnoreCase("U"))
    {
      if(str2.equalsIgnoreCase("U"))
	return false;
      if(str2.equalsIgnoreCase("I"))
	return false;
      if(str2.equalsIgnoreCase("M"))
	return false;
      if(str2.equalsIgnoreCase("("))
	return true;
      if(str2.equalsIgnoreCase(")"))
	return false;
    }

    if(str1.equalsIgnoreCase("I"))
    {
      if(str2.equalsIgnoreCase("I"))
	return false;
      if(str2.equalsIgnoreCase("U"))
	return false;
      if(str2.equalsIgnoreCase("M"))
	return false;
      if(str2.equalsIgnoreCase("("))
	return true;
      if(str2.equalsIgnoreCase(")"))
	return false;
    }

    if(str1.equalsIgnoreCase("M"))
    {
      if(str2.equalsIgnoreCase("M"))
	return false;
      if(str2.equalsIgnoreCase("I"))
	return false;
      if(str2.equalsIgnoreCase("U"))
	return false;
      if(str2.equalsIgnoreCase("("))
	return true;
      if(str2.equalsIgnoreCase(")"))
	return false;
    }

    if(str1.equalsIgnoreCase("("))
    {
      if(str2.equalsIgnoreCase("U"))
	return false;
      if(str2.equalsIgnoreCase("I"))
	return false;
      if(str2.equalsIgnoreCase("M"))
	return false;
      if(str2.equalsIgnoreCase("("))
	return true;
      if(str2.equalsIgnoreCase(")"))
	return false;
    }

    if(str1.equalsIgnoreCase(")"))
    {
      if(str2.equalsIgnoreCase("U"))
	return false;
      if(str2.equalsIgnoreCase("I"))
	return false;
      if(str2.equalsIgnoreCase("M"))
	return false;
      if(str2.equalsIgnoreCase("("))
	return false;
      if(str2.equalsIgnoreCase(")"))
	return false;
    }
    return true;
  }

  int BuildString(String str1)
  {
    String finalString=new String(), tempStr, prefixStr;
    int    index, s1=0, s2=0;
    int    errCode = 0;
    float  sxtemp, sytemp, sztemp;
    float  rxtemp, rytemp, rztemp;
    float  txtemp, tytemp, tztemp;

    trytx = 0;
    tryty = 0;
    trytz = 0;
    componentcount = 0;

    prefixStr = infixToPrefix(str1);

     // Tokenize the input string
    StringTokenizer tokens = new StringTokenizer(prefixStr);

    while( tokens.hasMoreTokens() )
    {
      tempStr  = new String(tokens.nextToken());
      if( tempStr.charAt(0) == ')' || tempStr.charAt(0)=='(')
      {
	System.out.println("Invalid String");
	return -2;
      }

      if( tempStr.equalsIgnoreCase("U") || tempStr.equalsIgnoreCase("I")
	  || tempStr.equalsIgnoreCase("M") )
	finalString = finalString + " " + tempStr;
      else
      {
	s1 = -1;
	for(int i=0; i<objectCount; i++)
	{
	  if(Objects[i].name.equalsIgnoreCase(tempStr)==true)
	  {
	    s1 = i;
	    break;
	  }
	}
	if(s1 == -1)
	{
	  System.out.println("Invalid Object specified.");
	  return -3;
	}

	sxtemp = Objects[s1].sx;
	sytemp = Objects[s1].sy;
	sztemp = Objects[s1].sz;

	rxtemp = Objects[s1].rx;
	rytemp = Objects[s1].ry;
	rztemp = Objects[s1].rz;

	txtemp = Objects[s1].tx;
	tytemp = Objects[s1].ty;
	tztemp = Objects[s1].tz;

	trytx += (int)txtemp;
	tryty += (int)tytemp;
	trytz += (int)tztemp;
	componentcount++;

	finalString = finalString + " T(" + txtemp + "," + tytemp + "," + tztemp + ")";
	finalString = finalString + " R(" + rxtemp + "," + rytemp + "," + rztemp + ")";
	finalString = finalString + " S(" + sxtemp + "," + sytemp + "," + sztemp + ")";

	if (Objects[s1].type == 5)
	{
	  finalString = finalString + " " + Objects[s1].csgStr;
	}
	else
	{
	  switch(Objects[s1].type)
	  {
	    case 1:          // Sphere
	      finalString = finalString.concat(" O(3,30,15,15,0)");
	      break;

	    case 2:          // Cube
	      finalString = finalString.concat(" O(1,50,50,50,0)");
	      break;

	    case 3:	    // Cylinder
	      finalString = finalString.concat(" O(2,15,50,15,0)");
	      break;

	    case 4:          // Cone
	      finalString = finalString.concat(" O(4,30,50,15,0)");
	      break;
	  }
	}
      }
    }

    finalString = finalString.trim();

    str = finalString;
    errCode = csgUIdraw(5);
    return (errCode);
  }

  // This is to handle the mouse down event.
  public boolean mouseDown(Event e, int x, int y)
  {
    prevx = x;
    prevy = y;
    return true;
  }

  // Handle the mouse drag event to rotate the object
  public boolean mouseDrag(Event e, int x, int y)
  {
    float xtheta = (prevy-y)*360.0f/canvas.CANVAS_WIDTH;
    float ytheta = (prevx-x)*360.0f/canvas.CANVAS_HEIGHT;

    tmat.matident();
    canvas.setRotate(xtheta, ytheta);

    canvas.mouseDrag = 1;
    xangle += xtheta;
    yangle += ytheta;
    canvas.UpdateScreenTransforms(xangle, yangle);
    prevx = x;
    prevy=y;
    return true;
   }

  public void Delete()
  {
    int i;

    for(i=selectedObject; i<objectCount-1; i++)
      Objects[i] = Objects[i+1];
    objectCount--;
    canvas.oCount--;
    canvas.flag = 0;
    canvas.DeleteDraw();
  }

  // Builds up the CSG tree depending on the input string.
  Tree BuildTree(char c)
  {
    Tree	n;
    int	        ot;
    int	        ind, tind;
    float	t1 = 0, t2 = 0, t3 = 0, t4 = 0;
    float	f1 = 0, f2 = 0, f3 = 0;
    String	s, tempStr;
    Integer     i;
    Float	f;

    if (str != null)
      s = str;

    if (c == 'O')
    {
      n = new Tree();
      n.ntype = 1;				// n object
      n.left = null;
      n.right = null;

      ind = str.indexOf(',',0);                 // Find the index where the first comma occurs.
      tempStr = str.substring(2, ind);	        // Get the first integer.
      i = new Integer(tempStr);
      ot = i.intValue();			// Assign the extracted interger to ot
      tind = ind;

      ind = str.indexOf(',',tind+1);		// Find the next comma.
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      t1 = f.floatValue();
      tind = ind;

      ind = str.indexOf(',',tind+1);		// Find the next comma.
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      t2 = f.floatValue();
      tind = ind;

      ind = str.indexOf(',',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      t3 = f.floatValue();
      tind = ind;

      ind = str.indexOf(')',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      t4 = f.floatValue();

      if(str.length() >= ind+2)
	str = str.substring(ind+2,str.length());

      n.stype = ot;
      n.param.feq[0] = t1;
      n.param.feq[1] = t2;
      n.param.feq[2] = t3;
      n.param.feq[3] = t4;

      //      System.out.println("Object is: "+ot+" "+t1+" "+t2+" "+t3+" "+t4);

      return(n);
    }
    else if (c == 'S')
    {
      // Scale operation is specified.
      n = new Tree();
      n.ntype = 2;		// n trans

      ind = str.indexOf(',',0);
      tempStr = str.substring(2,ind);
      f = new Float(tempStr);
      f1 = f.floatValue();
      tind = ind;

      ind = str.indexOf(',',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      f2 = f.floatValue();
      tind = ind;

      ind = str.indexOf(')',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      f3 = f.floatValue();

      if(str.length() >= ind+2)
	str = str.substring(ind+2,str.length());

      n.ttype = 1;	// scale
      n.dx = f1;
      n.dy = f2;
      n.dz = f3;
      n.left = n.right = BuildTree(str.charAt(0));
    }
    else if (c == 'T')
    {
      // Translate operation is specified.
      n = new Tree();
      n.ntype = 2;		// n trans

      ind = str.indexOf(',',0);
      tempStr = str.substring(2,ind);
      f = new Float(tempStr);
      f1 = f.floatValue();
      tind = ind;

      ind = str.indexOf(',',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      f2 = f.floatValue();
      tind = ind;

      ind = str.indexOf(')',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      f3 = f.floatValue();

      if(str.length() >= ind+2)
	str = str.substring(ind+2,str.length());

      n.ttype = 2;	// translate
      n.dx = f1;
      n.dy = f2;
      n.dz = f3;

      n.left = n.right = BuildTree(str.charAt(0));
    }
    else if (c == 'R')
    {
      // Rotate operation is specified.
      n = new Tree();
      n.ntype = 2;		// n trans

      ind = str.indexOf(',',0);
      tempStr = str.substring(2,ind);
      f = new Float(tempStr);
      f1 = f.floatValue();
      tind = ind;

      ind = str.indexOf(',',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      f2 = f.floatValue();
      tind = ind;

      ind = str.indexOf(')',tind+1);
      tempStr = str.substring(tind+1, ind);
      f = new Float(tempStr);
      f3 = f.floatValue();

      if(str.length() >= ind+2)
	str = str.substring(ind+2,str.length());

      n.ttype = 3;	// rotate
      n.dx = f1;
      n.dy = f2;
      n.dz = f3;
      n.left = n.right = BuildTree(str.charAt(0));
    }
    else if (c == 'U' || c=='u')
    {
      // Union operation is specified.
      n = new Tree();
      n.ntype = 3;		// n euler
      n.etype = 1;		// Union

      if(str.length() >= 2)
	str = str.substring(2,str.length());

      n.left = BuildTree(str.charAt(0));
      n.right = BuildTree(str.charAt(0));
    }
    else if (c == 'I' || c=='i')
    {
      // Intersection operation is specified.
      n = new Tree();
      n.ntype = 3;		// n euler
      n.etype = 2;		// Intersection

      if(str.length() >= 2)
	str = str.substring(2,str.length());

      n.left = BuildTree(str.charAt(0));
      n.right = BuildTree(str.charAt(0));
    }
    else if (c == 'M' || c == 'm')
    {
      // Difference operation is specified.
      n = new Tree();
      n.ntype = 3;		// n euler
      n.etype = 3;		// Difference

      if(str.length() >= 2)
	str = str.substring(2,str.length());

      n.left = BuildTree(str.charAt(0));
      n.right = BuildTree(str.charAt(0));
    }
    else
    {
      System.out.println("Error in String");
      return null;
    }
    return n;
  }

  Solid BuildGWBobject(Tree t)
  {
    Solid  s1=null, s2=null, s3=null, temps3=null;

    if (t.ntype == 1)			//Nobject
    {
      if(t.stype == 1)
      {
	s1 = block(new_snum++, t.param.feq[0], t.param.feq[1], t.param.feq[2]);
      }
      else if(t.stype == 2)
	s1 = cyl(new_snum++, t.param.feq[0], t.param.feq[1], (int)t.param.feq[2]);
      else if(t.stype == 3)
	s1 = ball(new_snum++, t.param.feq[0], (int)t.param.feq[1], (int)t.param.feq[2]);
      else if(t.stype == 4)
	s1 = cone(new_snum++,t.param.feq[0], t.param.feq[1], (int)t.param.feq[2]);
      else
	s1 = null;
      return s1;
    }
    else if(t.ntype == 2)		//  NTrans
    {
      if((s1 = BuildGWBobject(t.left))==null)
	return(null);
      if(t.ttype == 1)		        // Scale
	sscale(s1,t.dx, t.dy, t.dz);
      else if(t.ttype == 2)		// Translate
	s1.strans(t.dx, t.dy, t.dz);
      else if(t.ttype == 3)		// Rotate
	srotate(s1,t.dx, t.dy, t.dz);
      else
	return null;
      return s1;
    }
    else if(t.ntype == 3)		//Euler
    {
      if((s1 = BuildGWBobject(t.left))==null)
	return null;
      if((s2 = BuildGWBobject(t.right))==null)
	return null;
      if(t.etype == 1)		            // Union
	s3 = setOp(s1,s2,temps3,UNION,1);
      else if(t.etype == 2)		    // Intersection
	s3 = setOp(s1,s2,temps3,INTER,1);
      else if(t.etype == 3)		    // Diff
	s3 = setOp(s1,s2,temps3,MINUS,1);
      else
	return null;

      if(s3 == null)
	System.out.println("returning null union object");

      return s3;
    }
    else
      return null;
  }

  Solid block(int sn, float dx, float dy, float dz)
  {
    Solid  res;
    Tag    tagp1 = null, tagp2 =null;

    res = mvfs(sn, 1, 1, 0, 0, 0);
    smev(sn, 1, 1, 2, dx, 0, 0);
    smev(sn, 1, 2, 3, dx, dy, 0);
    smev(sn, 1, 3, 4, 0, dy, 0);
    smef(sn, 4, 1, 1, 2);

    tagp1 = newplane(++maxt, 0, 0, 1, 0);
    tagp2 = newplane(++maxt, 0, 0, -1, 0);

    res.fFace(1).lsettag(tagp1);
    res.fFace(2).lsettag(tagp2);

    res.hline_tag(1, 1, 2);
    res.hline_tag(1, 2, 3);
    res.hline_tag(1, 3, 4);
    res.hline_tag(1, 4, 1);
    sweep(res, dz);
    return res;
  }

  Solid cyl(int sn, float rad, float height, int nfacets)
  {
    Solid       res;
    Tag		tagp1, tagp2, tagp3;
    int	        i;
    double	delta;
    double	angle;
    double	xx, yy;

    // make sure the cylinder has an even number of faces;
    // otherwise arcs > 180 deg will result from conversion.
    nfacets = 2 * ((nfacets + 1) / 2);

    delta = (double)(2.0 * PI / nfacets);
    angle = 0.0;
    xx = (double)rad;
    yy = 0.0;

    res = mvfs(sn, 1, 1, (float)xx, (float)yy, 0);
    // tag the created face
    tagp1 = newplane(++maxt, 0, 0, -1, 0);
    res.fFace(1).lsettag(tagp1);

    tagp2 = newcparc(++maxt, rad, 0, 0,(float)(-rad), 0, 0, 0, 0, 0, 0, 0, 1, 0);
    tagp3 = newcparc(++maxt, (float)(-rad), 0, 0, rad, 0, 0, 0, 0, 0, 0, 0, 1, 0);

    i = 1;
    // make bottom edges
    while(i < nfacets)
    {
      angle += delta;
      xx = Math.cos(angle)*rad;
      yy = Math.sin(angle)*rad;
      smev(sn, 1, i, (i+1), (float)xx, (float)yy, 0);
      // tag the new edge with proper half-circle
      res.settag(1, i, (i+1), ((i > nfacets / 2) ? tagp3 : tagp2));
      res.settag(1, (i+1), i, ((i > nfacets / 2) ? tagp3 : tagp2));
      i++;
    }

    // close bottom face
    smef(sn, 1, nfacets, 1, 2);

    // tag the new edge and the new face
    res.settag(1, 1, nfacets, tagp3);
    res.settag(2, nfacets, 1, tagp3);
    tagp1 = newplane(++maxt, 0, 0, 1, 0);
    res.fFace(2).lsettag(tagp1);

    // make side faces
    sweep(res, height);

    return(res);
  }

  Solid	ball(int sn, float rad, int nfacets, int nhor)
  {
    Solid	res;
    Tag		tagp2;
    float	delta;
    float	angle;
    float       xx, yy;
    int	        i;

    if(nfacets < 3)
      nfacets = 3;

    delta = (float)(PI / nfacets);
    angle = 0;
    xx = rad;
    yy = 0;

    // make arc
    tagp2 = newcparc(++maxt, xx, yy, 0, (float)(-xx), yy, 0, 0, 0, 0, 0, 0, 1, 0);

    res = mvfs(sn, 1, 1, xx, yy, 0);
    i = 1;
    while(i <= nfacets)
    {
      angle += delta;
      xx = (float)(Math.cos(angle)*rad);
      yy = (float)(Math.sin(angle)*rad);
      smev(sn, 1, i, (i+1), xx, yy, 0);
      res.settag(1, i, (i+1), tagp2);
      res.settag(1, (i+1), i, tagp2);
      i++;
    }
    // rotate
    newrsweep(res, nfacets);
    return(res);
  }

  Solid cone(int sn, float rad, float height, int nfacets)
  {
    Solid	res;
    Tag		tagp1, tagp2;
    float	delta;
    float      	angle;
    float      	xx, yy;
    int	        i;
    int		nsides;

    // make sure the cone has an even number of faces;
    // otherwise arcs > 180 deg will result from conversion.
    nsides = 2 * ((nfacets + 1) / 2);
    if(nsides < 4) nsides = 4;

    delta = (float)(2.0 * PI / (nsides));
    angle = 0;
    xx = rad;
    yy = 0;

    res = mvfs(sn, 1, 1, xx, yy, 0);
    // tag the bottom as a plane
    res.fFace(1).lsettag(newplane(++maxt, 0, 0, 1, 0));
    tagp1 = newcparc(++maxt, rad, 0, 0, (float)(-rad), 0, 0, 0, 0, 0, 0, 0, 1, 0);
    tagp2 = newcparc(++maxt, (float)(-rad), 0, 0, rad, 0, 0, 0, 0, 0, 0, 0, 1, 0);

    i = 1;
    // make arc
    while(i < nsides)
    {
      angle += delta;
      xx = (float)(Math.cos(angle)*rad);
      yy = (float)(Math.sin(angle)*rad);
      smev(sn, 1, i, (i+1), xx, yy, 0);
      // tag the edge with proper half-circle
      res.settag(1, i, i+1, ((i > nsides / 2) ? tagp2 : tagp1));
      res.settag(1, i+1, i, ((i > nsides / 2) ? tagp2 : tagp1));
      i++;
    }

    // close bottom
    smef(sn, nsides, 1, 1, 2);
    res.settag(1, nsides, 1, tagp2);
    res.settag(2, 1, nsides, tagp2);

    // make cone tags
    tagp1 = newcone(++maxt, rad, height, 0, 0, 0, 0, 0, 0, (short)1);
    tagp2 = newcone(++maxt, rad, height, 0, 0, 0, 0, 0, 0, (short)1);
    res.fFace(2).lsettag(tagp1);

    // make sides
    maxv = nsides;
    maxf = 2;
    smev(sn, 2, 1, (nsides+1), 0, 0, height);
    res.hline_tag(2, 1, (nsides+1));

    for(i=1; i<nsides; i++)
    {
      smef(sn, (i+1), (nsides+1), 2, (++maxf));
      res.hline_tag(2, (i+1), (nsides+1));
      res.fFace(maxf).lsettag(((i > nsides / 2) ? tagp1 : tagp2));
    }

    return(res);
  }

  void arc(int s, int f, int v, float cx, float cy, float rad, float h,
	   float phi1, float phi2, int n)
  {
    float x, y, angle, inc;
    int prev, i;

    angle = (float)(phi1 * PI/180);
    inc = (float)((phi2 - phi1)* PI/(180*n));
    prev = v;
    getMaxNames(s);
    for(i=0; i<n; i++)
    {
      angle += inc;
      x = cx + (float)Math.cos(angle)*rad;
      y = cy + (float)Math.sin(angle)*rad;
      smev(s,f,prev,++maxv,x,y,h);
      prev = maxv;
    }
  }

  void sweep(Solid s, float dz)
  {
    Face     f;
    Vectors  tmp;

    tmp = new Vectors();

    // select orientation
    f = s.sfaces.nextf;
    f.flout.newell(tmp);
    tmp.feq[2] *= dz;
    if(tmp.feq[2] > 0.0)
      f = s.sfaces;

    extsweep2(s, f.faceNum, dz);
  }

  void	extsweep2(Solid s, int fno, float dz)
  {
    Face     f, newf;
    Loop     l;
    HalfEdge first, scan;
    Vertex   v;

    Mat      mat, mat1, mat2, invmat2;
    Vectors  tmp, w, n;
    double   a, b, c, d, x, y, z, r;
    short    sign;

    Tag      tagp, prevtag;

    tmp = new Vectors();
    w = new Vectors();
    n = new Vectors();

    mat = new Mat();
    mat1 = new Mat();
    mat2 = new Mat();
    invmat2 = new Mat();

    // find maxv and maxf
    s.scannames();

    f = s.fFace(fno);
    l = f.flout;
    l.newell(tmp);

    // check for a lamina (should have dz > 0.0)
    if(s.sfaces.nextf.nextf == null && dz < 0.0)
      System.out.println("extsweep2: cannot sweep the lamina");

    a = -tmp.feq[0] * dz;
    b = -tmp.feq[1] * dz;
    c = -tmp.feq[2] * dz;

    // tag the top of the solid
    tagp = f.surf;
    if(tagp != null)
    {
      d = tagp.pl_eq.feq[3] - tagp.pl_eq.feq[0] * a -
	tagp.pl_eq.feq[1] * b - tagp.pl_eq.feq[2] * c;
      if(tagp.times_used == 1)
	tagp.pl_eq.feq[3] = (float)d;
      else
      {
	tagp = newplane(++maxt, tagp.pl_eq.feq[0], tagp.pl_eq.feq[1], tagp.pl_eq.feq[2], (float)d);
	f.lsettag(tagp);
      }
    }

    l = f.floops;
    while(l != null)
    {
      first = l.ledge;
      // be sure to start off from the head of an arc, if any
      while((first.curv != null) && (first.curv == first.prev.curv))
	first = first.prev;

      scan = first;
      first = first.prev;
      prevtag = first.curv;

      // make 1st edge
      v = scan.vertex;
      scan.lmev(scan, ++maxv, (float)(v.vcoord.feq[0]+a), (float)(v.vcoord.feq[1]+b),
		(float)(v.vcoord.feq[2]+c));

      // tag the first edge
      scan.prev.line_tag();

      while(scan != first)
      {
	v = scan.next.vertex;
	scan.next.lmev(scan.next, ++maxv, (float)(v.vcoord.feq[0]+a),
		       (float)(v.vcoord.feq[1]+b),  (float)(v.vcoord.feq[2]+c));

	// tag the new edge
	scan.next.line_tag();

	newf = scan.prev.lmef(scan.next.next, ++maxf);
	// tag the new edge and the new face

	// different curve as previous ?
	if((scan.curv != null) || (scan.curv != prevtag))
	{
	  if(scan.curv != null)
	  {
	    // tag with line and plane
	    scan.next.next.line_tag();
	    w.vecMinus(scan.next.vertex.vcoord, scan.vertex.vcoord);
	    w.normalize();
	    n.cross(tmp, w);
	    if(dz < 0.0)
	      n.vecScale(-1.0, n);
	    d = -n.dot(scan.vertex.vcoord);
	    tagp = newplane(++maxt, n.feq[0], n.feq[1], n.feq[2], (float)d);
	    newf.lsettag(tagp);
	  }
	  else
	  {
	    // tag with arc and cylinder
	    mat.maketrans((float)a, (float)b, (float)c);
	    tagp = scan.curv.duplicatetag();
	    tagp.transformtag(mat);
	    scan.next.next.lsettag(tagp);
	    scan.next.next.mate().lsettag(tagp);

	    r = scan.curv.p1.edist(scan.curv.cp);
	    x = scan.curv.cp.feq[0];
	    y = scan.curv.cp.feq[1];
	    z = scan.curv.cp.feq[2];

	    sign = scan.eqsign(tmp);
	    if(dz < 0.0)
	      sign = (short)(-sign);

	    // find the transformation matrix of the cylinder surface
	    n.vecCopy(scan.curv.arc_eq);
	    n.normalize();
	    mat1.matident();
	    mat1.matrotate(0,(float)(RTOD * Math.acos(n.feq[2])),
			   polarangle(n.feq[0], n.feq[1]));
	    mat1.mattrans((float)x, (float)y, (float)z);
	    scan.curv.arc_transf.invmat(mat, 4);
	    mat.mattranspose(mat);
	    n.vecMult(scan.curv.arc_eq, mat);
	    n.normalize();
	    mat2.matident();
	    mat2.matrotate(0, (float)(RTOD * Math.acos(n.feq[2])),
			   polarangle(n.feq[0], n.feq[1]));
	    w.vecMult(scan.curv.cp, scan.curv.arc_transf);
	    mat2.mattrans(w.feq[0], w.feq[1], w.feq[2]);
	    mat2.matinv(invmat2);   // no scaling!
	    mat.matmult(mat1, scan.curv.arc_transf);
	    mat.matmult(mat, invmat2);
	    mat.mat[2][0] = (float)0.0;
	    mat.mat[2][1] = (float)0.0;
	    mat.mat[2][2] = (float)1.0;
	    mat.matmult(mat, mat2);

	    tagp = new_m_cylinder(++maxt, (float)r, mat, sign);

	    newf.lsettag(tagp);
	  }
	}
	else
	{
	  // copy tags
	  tagp = scan.prev.mate().next.curv;
	  scan.next.next.lsettag(tagp);
	  scan.next.next.mate().lsettag(tagp);
	  tagp = scan.prev.mate().wloop.lface.surf;
	  newf.lsettag(tagp);
	}
	prevtag = scan.curv;
	scan = scan.next.mate().next;
      }

      newf = scan.prev.lmef(scan.next.next, ++maxf);
      // tag the new edge and the new face as above
      //	if((scan.curv.linetag()!=0) || (scan.curv != prevtag))
      if(scan.curv != prevtag)
      {
	if(scan.curv.linetag() != 0)
	{
	  scan.next.next.line_tag();
	  w.vecMinus(scan.next.vertex.vcoord, scan.vertex.vcoord);
	  w.normalize();
	  n.cross(tmp, w);
	  if(dz < 0.0)
	    n.vecScale(-1.0, n);
	  d = -n.dot(scan.vertex.vcoord);
	  tagp = newplane(++maxt, n.feq[0], n.feq[1], n.feq[2], (float)d);
	  newf.lsettag(tagp);
	}
	else
	{
	  mat.maketrans((float)a, (float)b, (float)c);
	  tagp = scan.curv.duplicatetag();
	  tagp.transformtag(mat);
	  scan.next.next.lsettag(tagp);
	  scan.next.next.mate().lsettag(tagp);

	  r = scan.curv.p1.edist(scan.curv.cp);
	  x = scan.curv.cp.feq[0];
	  y = scan.curv.cp.feq[1];
	  z = scan.curv.cp.feq[2];

	  sign = scan.eqsign(tmp);
	  if(dz < 0.0)
	    sign = (short)(-sign);

	  // find the transformation matrix of the cylinder surface
	  n.vecCopy(scan.curv.arc_eq);
	  n.normalize();
	  mat1.matident();
	  mat1.matrotate(0, (float)(RTOD * Math.acos(n.feq[2])), polarangle(n.feq[0], n.feq[1]));
	  mat1.mattrans((float)x, (float)y, (float)z);
	  scan.curv.arc_transf.invmat(mat, 4);
	  mat.mattranspose(mat);
	  n.vecMult(scan.curv.arc_eq, mat);
	  n.normalize();		mat2.matident();
	  mat2.matrotate(0, (float)(RTOD * Math.acos(n.feq[2])), polarangle(n.feq[0], n.feq[1]));
	  w.vecMult(scan.curv.cp, scan.curv.arc_transf);
	  mat2.mattrans(w.feq[0], w.feq[1], w.feq[2]);
	  mat2.matinv(invmat2);   // no scaling!
	  mat.matmult(mat1, scan.curv.arc_transf);
	  mat.matmult(mat, invmat2);
	  mat.mat[2][0] = 0;
	  mat.mat[2][1] = 0;
	  mat.mat[2][2] = 1;
	  mat.matmult(mat, mat2);

	  tagp = new_m_cylinder(++maxt, (float)r, mat, sign);
	  newf.lsettag(tagp);
	}
      }
      else
      {
	tagp = scan.prev.mate().next.curv;
	scan.next.next.lsettag(tagp);
	scan.next.next.mate().lsettag(tagp);
	tagp = scan.prev.mate().wloop.lface.surf;
	newf.lsettag(tagp);
      }

      l = l.nextl;
    }
    f.fbits &= ~2;
  }

  void	newrsweep(Solid s, int nfaces)
  {
    Face	f, newf;
    Loop	l;
    HalfEdge	first, cfirst, last, scan;
    HalfEdge	he, minx;
    Vertex	v;
    double	yp, zp,	angle, delta, co, si;
    int		nsteps, nstepshalf;
    short       sign;

    // storage for coordinates of the swept line
    int		npoints;	// no of points
    double	xx[];
    double	yy[];

    // surfaces created during the sweep
    Tag		surfaces[];
    Tag		tmptag;

    // circles created during the sweep
    Tag		circles[];

    // for copying & transforming curve tags
    Tag		curves[];
    Mat  	m;

    // for dealing with some special cases
    int		didcut;		// 1 if a closed figure was opened
    int		headface, tailface=0;
    int		leftnonzero, rightnonzero;
    int     tempflag;

    // for testing the orientation of the figure
    Vectors	test1;
    int		j=0;

    xx = new double[200];
    yy = new double[200];

    surfaces = new Tag[200];
    circles = new Tag[200];
    curves = new Tag[200];

    m = new Mat();
    test1 = new Vectors();

    scan = new HalfEdge(this, 0);

    nsteps = 2 * ((nfaces + 1) / 2);	// even no. of sweep steps
    if(nsteps > 180)
      nsteps = 180;
    else if(nsteps < 4)
      nsteps = 4;

    nstepshalf = nsteps / 2 - 1;	// -1 to make "while(--nsteps)" work

    s.scannames();			// find maxv and maxf
    didcut = 0;
    tempflag = 0;
    f = s.sfaces;

    // a closed figure ?
    if(f.nextf != null)
    {
      // edge on x-axis ?
      l = f.floops;
      minx = l.ledge;
      he = l.ledge;
      do
      {
	tempflag = 0;
	if(comp(he.vertex.vcoord.feq[1], 0.0, eps) == 0)
	{
	  if(comp(he.next.vertex.vcoord.feq[1], 0.0, eps) == 0)
	  {
	    // two consecutive points of the axis - remove the edge between them
	    he.lkef(he.mate());
	    f = s.sfaces;
	    tempflag = 1;
	    break;
	  }
	  else if(comp(he.prev.vertex.vcoord.feq[1], 0.0, eps) == 0)
	  {
	    he.prev.lkef(he.prev.mate());
	    f = s.sfaces;
	    tempflag = 1;
	    break;
	  }
	  else
	  {
	    System.out.println("rsweep: cannot swing that");
		    return;
	  }
	}
	if(he.vertex.vcoord.feq[0] < minx.vertex.vcoord.feq[0])
	  minx = he;
	else if(he.vertex.vcoord.feq[0] == minx.vertex.vcoord.feq[0] &&
		he.vertex.vcoord.feq[1] < minx.vertex.vcoord.feq[1])
	  minx = he;
      }
      while((he = he.next) != l.ledge);

      if(tempflag != 1)
      {
	// cut the figure at minx
	minx.lmev(minx.mate().next, ++maxv, minx.vertex.vcoord.feq[0],
		  minx.vertex.vcoord.feq[1], minx.vertex.vcoord.feq[2]);
	minx.prev.lkef(minx.prev.mate());
	f = s.sfaces;
	didcut = 1;
      }
    }

    headface = f.faceNum;

    // get head and tail of the swing line; gather coordinates
    first = s.sfaces.floops.ledge;

    while(first.edge != first.next.edge)
      first = first.next;

    last = first.next;
    npoints = 0;
    xx[npoints] = last.vertex.vcoord.feq[0];
    yy[npoints++] = last.vertex.vcoord.feq[1];

    while(last.edge != last.next.edge)
      {
	last = last.next;
	xx[npoints] = last.vertex.vcoord.feq[0];
	yy[npoints++] = last.vertex.vcoord.feq[1];
      }

    xx[npoints] = last.next.vertex.vcoord.feq[0];
    yy[npoints++] = last.next.vertex.vcoord.feq[1];

    // make sure of orientation
    j = f.floops.length;
    f.floops.length = (short)npoints;
    f.floops.ledge = first.next;
    if(didcut != 0)
      f.floops.newell(test1);

    // test the orientation
    if((didcut == 0) && first.next.vertex.vcoord.feq[0] < last.next.vertex.vcoord.feq[0]
       || didcut!=0 && test1.feq[2] < 0.0)
    {
      // wrong orientation! swap first, last, xx, yy
      first = last;
      last = first.next;
      npoints = 0;
      xx[npoints] = last.vertex.vcoord.feq[0];
      yy[npoints++] = last.vertex.vcoord.feq[1];

      while(last.edge != last.next.edge)
      {
	last = last.next;
	xx[npoints] = last.vertex.vcoord.feq[0];
	yy[npoints++] = last.vertex.vcoord.feq[1];
      }

      xx[npoints] = last.next.vertex.vcoord.feq[0];
      yy[npoints++] = last.next.vertex.vcoord.feq[1];
    }
    f.floops.length = (short)j;

    // figure out proper surface types to be created
    he = first.next;
    j = 0;

    while(j < npoints-1)
    {
      // make sure all edges have tags on them
      if(he.curv == null)
	he.curv = new Tag(this, LINE1, ++maxt);

      // a straight line ?
      if(he.curv.linetag() != 0)
      {
	// line parallel to y-axis ?
	if(comp(xx[j], xx[j+1], eps) == 0)
	{
	  // make a plane
	  sign = (short)comp(yy[j], yy[j + 1], eps);
	  if(sign == 1)
	    surfaces[j] = newplane(++maxt, 1, 0, 0, (float)(-xx[j]));
	  else if(sign == -1)
	    surfaces[j] = newplane(++maxt, -1, 0, 0, (float)xx[j]);
	  else
	  {
	    System.out.println("newsweep: cannot determine plane");
	    return;
	  }
	}
	// line parallel to x-axis ?
	else if(comp(yy[j], yy[j+1], eps) == 0)
	{
	  // make a cylinder
	  sign = (short)comp(xx[j], xx[j + 1], eps);
	  if(sign == 0)
	  {
	    System.out.println("newsweep: cannot determine cylinder");
	    return;
	  }
	  surfaces[j] = newcylinder(++maxt, (float)yy[j], 0, 90, 0, 0, 0, 0, sign);
	}
	else
	{
	  // so make a cone
	  double rad, x0, h, a, t;

	  x0 = (xx[j] * yy[j+1] - xx[j+1] * yy[j]) / (yy[j+1] - yy[j]);
	  sign = -1;
	  if(comp(yy[j], yy[j + 1], eps) == 1)
	  {
	    rad = yy[j];
	    h = x0 - xx[j];
	    a = 90.0;
	    t = xx[j];
	    if(comp(xx[j], xx[j + 1], eps) == 1)
	    {
	      h = (double)(-h);
	      a = (double)(-a);
	      sign = (short)(-sign);
	    }
	  }
	  else
	  {
	    rad = yy[j + 1];
	    h = xx[j + 1] - x0;
	    a = -90.0;
	    t = xx[j + 1];
	    if(comp(xx[j], xx[j + 1], eps) == 1)
	    {
	      h = (double)(-h);
	      a = (double)(-a);
	      sign = (short)(-sign);
	    }
	  }
	  surfaces[j] = newcone(++maxt, (float)rad, (float)h,
				(float)0, (float)a, (float)0, (float)t, (float)0, (float)0, sign);
	}
	curves[j] = he.curv;
	j++;
	he = he.next;
      }
      else	// it's an arc
      {
	double r=0.0, R=0.0, x1=0.0, x2=0.0, y1=0.0, y2=0.0, s1=0.0, s2=0.0;
	double A=0.0, B=0.0, C, d=0.0, x0=0.0, y0=0.0, a=0.0, b=0.0;
	double theta=0.0, x_div=0.0, y_div=0.0;
	double x_test=0.0, y_test=0.0, test=0.0;
	Vectors u, v2, w, w0, n, u1, v1;
	int i, k, reverse;
	HalfEdge testhe;
	Tag arc1, arc2;

	u = new Vectors();
	v2 = new Vectors();
	w = new Vectors();
	w0 = new Vectors();
	n = new Vectors();
	u1 = new Vectors();
	v1 = new Vectors();

	u.vecMinus(he.curv.p1, he.curv.cp);
	u.feq[3] = 0;
	r = u.normalize();
	v2.cross(he.curv.arc_eq, u);
	v2.feq[3] = 0;
	v2.normalize();
	w.vecMult(u, he.curv.arc_transf);
	x1 = w.feq[0];
	x2 = w.feq[1];
	w.vecMult(v2, he.curv.arc_transf);
	y1 = w.feq[0];
	y2 = w.feq[1];
	s1 = x1 * x1 + y1 * y1;
	s2 = x2 * x2 + y2 * y2;
	if(s1 == 0.0 || s2 == 0.0)
	{
	  System.out.println("newrsweep: null ellipse!");
	  return;
	}
	w.vecMult(he.curv.cp, he.curv.arc_transf);
	x0 = w.feq[0];
	y0 = w.feq[1];
	if(comp(x1 * x2 + y1 * y2, 0.0, CIRCEPS) == 0)
	{
	  // affine transformation of a torus
	  s1 = Math.sqrt(s1);
	  s2 = Math.sqrt(s2);
	  R = y0 * s1;
	  r = s1 * s2 * r;

	  n.feq[0] = 0;
	  n.feq[1] = 0;
	  n.feq[2] = 1;
	  n.feq[3] = 1;
	  sign = he.eqsign(n);

	  if(comp(R, 0.0, eps) == 0)
	  {
	    surfaces[j] = newsphere(++maxt, (float)r, 0, 0, 0, 0, 0, 0, sign);
	    surfaces[j].sph_transf.matscale((float)(1.0/s2), (float)(1.0/s1), (float)(1.0/s1));
	    surfaces[j].sph_transf.mattrans((float)x0, 0, 0);
	  }

	  curves[j] = he.curv;
	  // copy this tag to all edges of the arc
	  he = he.next;
	  j++;
	  while(he.curv == he.prev.curv && j < npoints-1)
	  {
	    surfaces[j] = surfaces[j-1];
	    curves[j] = he.curv;
	    he = he.next;
	    j++;
	  }
	}
	else
	{
	  // general rotational surface
	  d = x1 * y2 - x2 * y1;
	  if(d == 0.0)
	  {
	    System.out.println("newrsweep: null ellipse!");
	    return;
	  }
	  A = s2 / (r*r * d*d);
	  B = s1 / (r*r * d*d);
	  C = -(x1 * x2 + y1 * y2) / (r*r * d*d);

	  // find the ellipse parameters
	  a = 0.5 * (A + B - Math.sqrt((A - B) * (A - B) + 4.0 * C*C));
	  a = 1.0 / Math.sqrt(a);
	  b = 0.5 * (A + B + Math.sqrt((A - B) * (A - B) + 4.0 * C*C));
	  b = 1.0 / Math.sqrt(b);
	  theta = Math.atan((1.0 / (2.0 * C)) * (B - A - Math.sqrt((A - B) * (A - B) +
								   4.0 * C*C)));

	  n.feq[0] = 0;
	  n.feq[1] = 0;
	  n.feq[2] = 1;
	  n.feq[3] = 1;

	  sign = he.eqsign(n);

	  test = (y0*y0) * (1 - (A*B) / (C*C)) + 1.0/B;
	  if(test <= 0.0)
	  {
	    // no self-intersection outside x-axis
	    testhe = he;
	    if(comp(testhe.vertex.vcoord.feq[1], 0.0, eps) != 1)
	    {
	      testhe = testhe.next;
	      if(comp(testhe.vertex.vcoord.feq[1], 0.0, eps) != 1)
	      {
		System.out.println("rsweep: cannot swing that");
		return;
	      }
	    }
	    x_test = (double)testhe.vertex.vcoord.feq[0] - x0;
	    y_test = (double)testhe.vertex.vcoord.feq[1];
	    test = A * x_test * x_test + B * (y_test + y0) * (y_test + y0) -
	      2.0 * C * x_test * (y_test + y0) - 1.0;
	    if(test < 0.0)
	      sign = (short)(-sign);
	    surfaces[j] = newrotsurf(++maxt, (float)y0, (float)a,
				     (float)b, (float)(RTOD * theta), 0, 0, 0,
				     (float)x0, 0, 0, sign);

	    curves[j] = he.curv;
	    // copy this tag to all edges of the arc
	    he = he.next;
	    j++;
	    while(he.curv == he.prev.curv && j < npoints-1)
	    {
	      surfaces[j] = surfaces[j-1];
	      curves[j] = he.curv;
	      he = he.next;
	      j++;
	    }
	  }
	  else
	  {
	    // serious self-intersection
	    x_div = (B / C) * y0 + x0;
	    y_div = Math.sqrt((y0*y0) * (1 - (A*B) / (C*C)) + 1.0/B);

	    // test if the self intersection cuts the
	    w.feq[0] = (float)x_div;
	    w.feq[1] = (float)y_div;
	    w.feq[2] = 0;
	    w.feq[3] = 1;

	    he.curv.arc_transf.invmat(m, 4);
	    w.vecMult(w, m);
	    w.vecMinus(w, he.curv.cp);
	    w.normalize();
	    u.vecMinus(he.curv.p1, he.curv.cp);
	    u.normalize();
	    v2.vecMinus(he.curv.p2, he.curv.cp);
	    v2.normalize();
	    n.vecCopy(he.curv.arc_eq);
	    n.normalize();
	    u1.cross(u, w);
	    v1.cross(w, v2);
	    if(u1.dot(n) <=  0.0 || v1.dot(n) <=  0.0)
	    {
	      // the arc is not cut
	      testhe = he;
	      if(comp(testhe.vertex.vcoord.feq[1], 0.0, eps) != 1)
	      {
		testhe = testhe.next;
		if(comp(testhe.vertex.vcoord.feq[1], 0.0, eps) != 1)
		{
		  System.out.println("rsweep: cannot swing that");
		  return;
		}
	      }
	      x_test = testhe.vertex.vcoord.feq[0]- x0;
	      y_test = testhe.vertex.vcoord.feq[1];
	      test = A * x_test * x_test + B * (y_test + y0) *
		(y_test + y0) - 2 * C * x_test * (y_test + y0) - 1.0;
	      if(test < 0.0)
		sign = (short)(-sign);
	      surfaces[j] = newrotsurf(++maxt, (float)y0, (float)a,
				       (float)b, (float)(RTOD * theta), 0, 0, 0,
				       (float)x0, 0, 0, sign);

	      curves[j] = he.curv;
	      // copy this tag to all edges of the arc
	      he = he.next;
	      j++;
	      while(he.curv == he.prev.curv && j < npoints-1)
	      {
		surfaces[j] = surfaces[j-1];
		curves[j] = he.curv;
		he = he.next;
		j++;
	      }
	    }
	    else
	    {
	      // the arc has to be cut figure out surface signs
	      u1.vecCopy(he.vertex.vcoord);
	      u1.feq[3] = 1;
	      u1.vecMult(u1, m);
	      u1.vecMinus(u1, he.curv.cp);
	      u1.normalize();
	      if(u1.vecEqual(u) != 0)
	      {
		u1.vecPlus(u, w);
		reverse = 0;
	      }
	      else if(u1.vecEqual(v2) != 0)
	      {
		u1.vecPlus(v2, w);
		reverse = 1;
	      }
	      else
	      {
		System.out.println("newrsweep: cannot determ ine arc orientation");
		return;
	      }
	      if(u1.normalize() == 0.0)
	      {
		System.out.println("newrsweep: cannot divide arc");
		return;
	      }
	      u1.vecScale(r, u1);
	      u1.vecPlus(u1, he.curv.cp);
	      u1.feq[3] = 1;
	      u1.vecMult(u1, he.curv.arc_transf);
	      x_test = (double)u1.feq[0] - x0;
	      y_test = (double)u1.feq[1];
	      test = A * x_test * x_test + B * (y_test + y0) *
		(y_test + y0) - 2 * C * x_test * (y_test + y0) - 1.0;
	      if(test < 0.0)
		sign = (short)-sign;

	      // find the cutting point
	      testhe = he;
	      k = 0;
	      do
	      {
		testhe = testhe.next;
		++k;
		u1.vecCopy(testhe.vertex.vcoord);
		u1.feq[3] = 1;
		u1.vecMult(u1, m);
		u1.vecMinus(u1, he.curv.cp);
		u1.normalize();
		u1.cross(u1, w);
		test = (reverse!=0 ? (-u1.dot(n)) : u1.dot(n));
	      }
	      while(test > 0.0);

	      w.feq[0] = (float)x_div;
	      w.feq[1] = (float)y_div;
	      w.feq[2] = 0;
	      w.feq[3] = 1;

	      if(test < 0.0)
	      {
		// new point added
		if(testhe.prev == (testhe.mate()))
		  testhe.mate().lmev(testhe.next, ++maxv, w.feq[0], w.feq[1], 0);
		else
		  testhe.mate().next.lmev(testhe, ++maxv, w.feq[0], w.feq[1], 0);

		// update xx and yy
		for(i = npoints; i > j + k; --i)
		{
		  xx[i] = xx[i-1];
				yy[i] = yy[i-1];
		}
		xx[j + k] = w.feq[0];
		yy[j + k] = w.feq[1];
		++npoints;
	      }
	      else
	      {
		// a vertex hit
		w.vecCopy(testhe.vertex.vcoord);
	      }
	      arc1 = he.curv;
	      arc2 = arc1.duplicatetag();
	      w0.vecMult(w, m);
	      if(reverse != 0)
	      {
		arc1.p1.vecCopy(w0);
		arc2.p2.vecCopy(w0);
	      }
	      else
	      {
		arc1.p2.vecCopy(w0);
		arc2.p1.vecCopy(w0);
	      }
	      surfaces[j] = newrotsurf(++maxt, (float)y0, (float)a, (float)b,
				       (float)(RTOD * theta), 0, 0, 0,
				       (float)x0, 0, 0, sign);
	      curves[j] = he.curv;
	      he = he.next;
	      ++j;
	      while(he.vertex.vcoord.vecSame(w) == 0)
	      {
		surfaces[j] = surfaces[j - 1];
		curves[j] = he.curv;
		he = he.next;
		++j;
	      }
	      if(he.prev.curv == null)
	      {
		he.prev.lsettag(arc1);
		he.prev.mate().lsettag(arc1);
		curves[j - 1] = arc1;
	      }

	      surfaces[j] = newrotsurf(++maxt, (float)y0, (float)a, (float)b,
				       (float)(RTOD * theta),
				       0, 0, 0, (float)x0, 0, 0, (short)(-sign));
	      he.lsettag(arc2);
	      he.mate().lsettag(arc2);
	      curves[j] = he.curv;
	      he = he.next;
	      ++j;
	      while(he.curv == arc1 && j < npoints -1)
	      {
		surfaces[j] = surfaces[j- 1];
		he.lsettag(arc2);
		he.mate().lsettag(arc2);
		curves[j] = he.curv;
		he = he.next;
		++j;
	      }
	    }
	  }
	}
      }
    }

    // generate circles of sweeping
    for(j=0; j<npoints; j++)
    {
      circles[j] = newcparc(++maxt, (float)xx[j], (float)yy[j], 0, (float)xx[j], (float)(-yy[j]),
			    0, (float)xx[j], 0, 0, 1, 0, 0, (float)(-xx[j]));
    }

    // does the wire hit the rotation axis ?
    rightnonzero  = comp(yy[0], 0.0, eps);
    leftnonzero = comp(yy[npoints-1], 0.0, eps);

    cfirst = first;
    angle = 0.0;
    delta = 2.0 * PI / nsteps;

    // transformation for a single rotation
    m.makerotate((float)(RTOD * delta), 0, 0);

    while(--nsteps != 0)
    {
      angle += delta;

      // generate  rotated copies of all curves swept tmptag will hold the "previous" tag
      tmptag = null;
      for(j=0; j<npoints-1; j++)
      {
	// same tag as previous ?
	if(curves[j] == tmptag)
	{
	  // just use the previous rotated copy
	  tmptag = curves[j];
	  curves[j] = curves[j-1];
	}
	else
	{
	  // make a rotated copy
	  tmptag = curves[j];
	  curves[j] = curves[j].duplicatetag();
	  curves[j].transformtag(m);
	}
      }

      // after sweeping past 180 degrees, start new surfaces and circles
      if(nsteps == nstepshalf)
      {
	// generate copies of all surfaces
	tmptag = null;
	for(j=0; j<npoints-1; j++)
	{
	  // same tag as previous ?
	  if(surfaces[j] == tmptag)
	  {
	    tmptag = surfaces[j];
	    surfaces[j] = surfaces[j-1];
	  }
	  else
	  {
	    tmptag = surfaces[j];
	    surfaces[j] = surfaces[j].duplicatetag();
	  }
	}
	// generate second halves of circles of sweeping
	for(j=0; j<npoints; j++)
	{
	  circles[j] = newcparc(++maxt, (float)xx[j], (float)(-yy[j]), 0,
				(float)xx[j], (float)yy[j], 0, (float)xx[j], 0, 0,
				1, 0, 0, (float)(-xx[j]));
	}
      }

      co = Math.cos(angle);
      si = Math.sin(angle);
      v = cfirst.next.vertex;
      j = 0;
      if(rightnonzero != 0)
      {
	yp = yy[j] * co;
	zp = yy[j] * si;
	cfirst.next.lmev(cfirst.next, ++maxv, v.vcoord.feq[0], (float)yp, (float)zp);
	cfirst.next.lsettag(circles[j]);
	cfirst.next.mate().lsettag(circles[j]);
      }
      scan = cfirst.next;
      while(scan != last.next)
      {
	v = scan.prev.vertex;
	j++;
	if(leftnonzero!=0 || j < npoints-1)
	{
	  yp = yy[j] * co;
	  zp = yy[j] * si;
	  scan.prev.lmev(scan.prev, ++maxv, v.vcoord.feq[0], (float)yp, (float)zp);
	  scan.prev.prev.lsettag(circles[j]);
	  scan.prev.prev.mate().lsettag(circles[j]);
	}
	if(rightnonzero!=0 || j > 1)
	{
	  if(leftnonzero!=0 || j < npoints-1)
	  {
	    newf = scan.prev.prev.lmef(scan.next, ++maxf);
	    newf.lsettag(surfaces[j-1]);
	    scan.next.lsettag(curves[j-1]);
	    scan.next.mate().lsettag(curves[j-1]);
	    scan = scan.next.next.mate();
	  }
	  else
	  {
	    newf = scan.prev.lmef(scan.next, ++maxf);
	    newf.lsettag(surfaces[j-1]);
	    scan.next.lsettag(curves[j-1]);
	    scan.next.mate().lsettag(curves[j-1]);
	    scan = scan.next.mate();
	  }
	}
	else
	{
	  newf = scan.prev.prev.lmef(scan, ++maxf);
	  newf.lsettag(surfaces[j-1]);
	  scan.prev.lsettag(curves[j-1]);
	  scan.prev.mate().lsettag(curves[j-1]);
	  scan = scan.prev.prev;
	}
      }
      if(leftnonzero != 0)
	last = scan;
      if(rightnonzero != 0)
	cfirst = cfirst.next.next.mate();
      else
	cfirst = cfirst.next.mate();
    }

    j = 0;
    if(rightnonzero != 0)
    {
      newf = cfirst.next.lmef(first.mate(), ++maxf);
      newf.lsettag(newplane(++maxt, -1, 0, 0, (float)xx[j]));
      cfirst.next.lsettag(circles[j]);
      cfirst.next.mate().lsettag(circles[j]);
      tailface = maxf;
    }
    else
    {
      newf = cfirst.lmef(cfirst.next.next, ++maxf);
      newf.lsettag(surfaces[j]);
      cfirst.prev.lsettag(circles[j+1]);
      cfirst.prev.mate().lsettag(circles[j+1]);
      cfirst = cfirst.prev.mate().prev;
      j++;
    }

    while(cfirst != scan)
    {
      newf = cfirst.lmef(cfirst.next.next.next, ++maxf);
      newf.lsettag(surfaces[j]);
      cfirst.prev.lsettag(circles[j+1]);
      cfirst.prev.mate().lsettag(circles[j+1]);
      cfirst = cfirst.prev.mate().prev;
      j++;
    }

    if(leftnonzero == 0)
    {
      s.fFace(headface).lsettag(surfaces[j]);
    }
    else
    {
      s.fFace(headface).lsettag(newplane(++maxt, 1, 0, 0, (float)(-xx[j])));
    }

    // mend cut, if done
    if(didcut != 0)
    {
      s.kfmrh(headface, tailface);
      s.fFace(headface).lglue();
    }
  }

  void srotate(Solid s, float rx, float ry, float rz)
  {
    Mat m;
    Vertex v;

    m = new Mat();

    m.matident();
    m.matrotate(rx,ry,rz);
    for(v = s.sverts; v!=null; v= v.nextv)
      v.vcoord.vecMult(v.vcoord, m);
  }

  void sscale(Solid s, float sx, float sy, float sz)
  {
    Vertex v;

    for(v=s.sverts;v!= null; v= v.nextv)
    {
      v.vcoord.feq[0] *= sx;
      v.vcoord.feq[1] *= sy;
      v.vcoord.feq[2] *= sz;
    }
  }

  float qabs(float x)
  {
    return (x>0 ? x : -x);
  }

  Solid mvfs(int s, int f, int v, float x, float y, float z)
  {
    Solid	newSolid;
    Face	newFace;
    Vertex	newVertex;
    HalfEdge	newhe;
    Loop	newLoop;

    // make solid
    newSolid = new Solid(this);

    // make face
    newFace = new Face(this, newSolid);
    newFace.faceNum = f;

    // make loop
    newLoop = new Loop(this, newFace);
    newFace.flout = newLoop;
    newLoop.length = 0;

    // make HalfEdge
    newhe = new HalfEdge(this, 0);
    newLoop.ledge = newhe;
    newhe.next = newhe;
    newhe.prev = newhe;
    newhe.edge = null;
    newhe.wloop = newLoop;

    // make vertex
    newVertex = new Vertex(this, newSolid);
    // link vertex to loop
    newhe.vertex = newVertex;
    // initialize vertex
    newSolid.solidNum = s;
    newVertex.vertexNum = v;
    newVertex.vcoord.feq[0] = x;
    newVertex.vcoord.feq[1] = y;
    newVertex.vcoord.feq[2] = z;
    newVertex.vcoord.feq[3] = 1;

    return newSolid;
  }

  Solid setOp(Solid a, Solid b, Solid res, int op, int flag)
  {
    int   i, ct;
    int   conta, contb;
    Face  f;
    Solid temp;

    temp = new Solid(this);

    // initialize set operation
    setop_init(a, b);
    Gtestspecials = 1;

    // generate relevant faces
    setop_gen(a, b);

    nedg = 0;
    na = 0;
    nb = 0;
    nab = 0;

    // handle coplanar faces
    setop_1(a, b);

    // handle remaining cases
    setop_2(a, b);

    // handle relevant vertices
    for(i=0; i<na; i++)
      rel_fva[i].on_vtx.vtxfac(rel_fva[i].rfac, rel_fva[i].kind, op, 0);

    for(i=0; i<nb; i++)
      rel_fvb[i].on_vtx.vtxfac(rel_fvb[i].rfac, rel_fvb[i].kind, op, 1);

    for(i=0; i<nab; i++)
      vtxab[i].va.vtxvtx(vtxab[i].vb, vtxab[i].kind, op);

      // were intersections found ?
    if(nedg == 0)
    {
	// check whether a contains wholly b or vice versa
      System.out.println("setop: no intersections found");

      conta = -1;
      contb = -1;

      // did the solids have touching parts ?
      if(na + nb > 0)
      {
	// check containment by orientation
	for(i=0; i<na; i++)
	{
	  if((rel_fva[i].kind & NONOPPOSITE) != 0)
	  {
	    // a had a vertex on face of b with identical orientation  => b contains a
	    contb = 1;
	    break;
	  }
	}
	if(contb == -1)
	  for(i=0; i<nb; i++)
	  {
	    if((rel_fvb[i].kind & NONOPPOSITE) != 0)
	    {
	      conta = 1;
	      break;
	    }
	  }
      }

      // else, check containment with contsv()
      if(conta == -1 && contb == -1)
      {
	conta = a.contsv(b.sfaces.flout.ledge.vertex);
	if(conta != 1)
	  contb = b.contsv(a.sfaces.flout.ledge.vertex);
	    else
	      contb = -1;
      }

      if(conta == 1)		// a contains b
      {
	System.out.println("setop: a contains b");

	if(op == INTER)
	  res = b;
	else if(op == UNION)
	  res = a;
	else
	{
	  b.revert();

	  // revert the face equations and surf tag signs of b
	  f = b.sfaces;
	  while(f != null)
	  {
	    if(f.haseq() != 0)
	    {
	      f.feq.feq[0] = -f.feq.feq[0];
	      f.feq.feq[1] = -f.feq.feq[1];
	      f.feq.feq[2] = -f.feq.feq[2];
	      f.feq.feq[3] = -f.feq.feq[3];
	    }
	    f = f.nextf;
	  }
	  b.surftagplist();

	  ct = 0;
	  while(tagplist[ct] != null)
	  {
	    if(tagplist[ct].tag_type == PLANE1)
	    {
	      tagplist[ct].pl_eq.feq[0] = -tagplist[ct].pl_eq.feq[0];
	      tagplist[ct].pl_eq.feq[1] = -tagplist[ct].pl_eq.feq[1];
	      tagplist[ct].pl_eq.feq[2] = -tagplist[ct].pl_eq.feq[2];
	      tagplist[ct].pl_eq.feq[3] = -tagplist[ct].pl_eq.feq[3];
	    }
	    else
	      tagplist[ct].sign = (short)(-tagplist[ct].sign);
	    ct++;
	  }

	  res = new Solid(this);
	  res.merge(a, b);
	}
      }
      else if(contb == 1)	// b contains a
      {
	System.out.println("setop: b contains a");

	if(op == INTER)
	  res = a;
	else if(op == UNION)
	  res = b;
	else
	  res = null;
	  }
      else			// a and b are separate
      {
	System.out.println("setop: a and b are separate.");

	if(op == INTER)
	  res = null;
	else if(op == UNION)
	{
	  res = new Solid(this);
	  res.merge(a, b);
	}
	else
	  res = a;
      }

      if(res != null)
	res.strans(gbox.xmi, gbox.ymi, gbox.zmi);
      else
      {
	a.strans(gbox.xmi, gbox.ymi, gbox.zmi);
	b.strans(gbox.xmi, gbox.ymi, gbox.zmi);
      }
      return (res);
    }

    // make intersection polygons
    nfaca = 0;
    nfacb = 0;

    checklone();
    setop_connect();

    if(nfaca == 0)
    {
      System.out.println("setop: failed to make intersection polygons");
      res = null;
      a.strans(gbox.xmi, gbox.ymi, gbox.zmi);
      b.strans(gbox.xmi, gbox.ymi, gbox.zmi);
      return (null);
    }

    // calculate results
    // setop_finish(a, b, res, a, b, op);
    res = setop_finish(a, b, temp, a, b, op);

    Gtestspecials = 0;
    res.strans(gbox.xmi, gbox.ymi, gbox.zmi);

    return(res);
  }

  Solid	setop_finish(Solid a, Solid b, Solid res, Solid ares, Solid bres, int op)
  {
    int		i, ct;
    Face	f;
    Solid	ainb, aoutb, bina, bouta;

    // step 6: modify null faces into pairs of null faces
    for(i=0; i<nfaca; i++)
      {
	// save the new faces
	lonfa[nfaca+i] = lonfa[i].mfmg(lonfa[i].floops.nextl, ++maxf);
	lonfb[nfacb+i] = lonfb[i].mfmg(lonfb[i].floops.nextl, ++maxf);

	// check orientation
	if(lonfa[i].identif(lonfb[i]) == 0)
	  {
	    // if the intersection polygon is within an inner loop
	    // of a face swap those null faces
	    if(lonfb[i].innernullf() != 0 || lonfb[nfacb+i].innernullf() != 0)
	      {
		f = lonfb[i];
		lonfb[i] = lonfb[nfacb+i];
		lonfb[nfacb+i] = f;
	      }
	    else if(lonfa[i].innernullf()!=0 || lonfa[nfaca+i].innernullf()!=0)
	      {
		f = lonfa[i];
		lonfa[i] = lonfa[nfaca+i];
		lonfa[nfaca+i] = f;
	      }
	    // else, one of the null faces must be a "tangent"
	    // null face, i.e. be surrounded by one face only
	    else if(lonfa[i].tangentnullf() != 0)
	      {
		// make it inner
		lonfa[i].detachnullf();
		// swap
		lonfa[i].swapnullf();
		// attach the swapped face back
		lonfa[i].attachnullf();
	      }
	    else if(lonfa[nfaca+i].tangentnullf() != 0)
	      {
	        lonfa[nfaca+i].detachnullf();
		lonfa[nfaca+i].swapnullf();
		lonfa[nfaca+i].attachnullf();
	      }
	    else if(lonfb[i].tangentnullf() != 0)
	      {
		lonfb[i].detachnullf();
		lonfb[i].swapnullf();
		lonfb[i].attachnullf();
	      }
	    else if(lonfb[nfacb+i].tangentnullf() != 0)
	      {
		lonfb[nfacb+i].detachnullf();
		lonfb[nfacb+i].swapnullf();
		lonfb[nfacb+i].attachnullf();
	      }
	    else
	      {
		System.out.println("setop_finish: cannot fix orientation");
	      }
	  }
      }

    // step 7: construct results

    // create new solids
    ainb = new Solid(this);
    aoutb = new Solid(this);

    // ... and classify split-faces into them
    for(i=0; i<nfaca; i++)
      {
	lonfa[i].delList(a);
	lonfa[i].linkNode(aoutb);
	lonfa[i+nfaca].delList(a);
	lonfa[i+nfaca].linkNode(ainb);
      }

    // classify remaining faces of a by their adjacency
    a.detach(ainb, aoutb);

    // do the similar steps for b
    bina = new Solid(this);
    bouta = new Solid(this);

    for(i=0; i<nfacb; i++)
      {
	lonfb[i].delList(b);
	lonfb[i].linkNode(bouta);
	lonfb[i+nfacb].delList(b);
	lonfb[i+nfacb].linkNode(bina);
      }
    b.detach(bina, bouta);

    // step 8: construct the desired result

    res = new Solid(this);

    switch(op)
      {
      case UNION:
	res.merge(aoutb, bouta);
	ainb.redo();
	ainb.solidrm();
	bina.redo();
	bina.solidrm();

	// glue null faces
	for(i=0; i<nfaca; i++)
	  {
	    lonfa[i].lkfmrh(lonfb[i]);
	    lonfa[i].lglue();
	  }
	break;

      case INTER:
	res.merge(ainb, bina);
	aoutb.redo();
	aoutb.solidrm();
	bouta.redo();
	bouta.solidrm();

	// glue null faces
	for(i=0; i<nfaca; i++)
	  {
	    lonfa[nfaca+i].lkfmrh(lonfb[nfaca+i]);
	    lonfa[nfaca+i].lglue();
	  }
	break;

      case MINUS:
	// revert the topology of bina
	bina.revert();
	// revert face equations
	f = bina.sfaces;
	while(f != null)
	  {
	    if(f.haseq() != 0)
	      {
		f.feq.feq[0] = -f.feq.feq[0];
		f.feq.feq[1] = -f.feq.feq[1];
		f.feq.feq[2] = -f.feq.feq[2];
		f.feq.feq[3] = -f.feq.feq[3];
	      }
	    f = f.nextf;
	  }

	// revert the surface tags too
	bina.surftagplist();
	ct = 0;
	while(tagplist[ct] != null)
	  {
	    if(tagplist[ct].tag_type == PLANE1)
	      {
		tagplist[ct].pl_eq.feq[0] = -tagplist[ct].pl_eq.feq[0];
		tagplist[ct].pl_eq.feq[1] = -tagplist[ct].pl_eq.feq[1];
		tagplist[ct].pl_eq.feq[2] = -tagplist[ct].pl_eq.feq[2];
		tagplist[ct].pl_eq.feq[3] = -tagplist[ct].pl_eq.feq[3];
	      }
	    else
	      tagplist[ct].sign = (short)(-tagplist[ct].sign);
	    ct++;
	  }

	res.merge(aoutb, bina);
	ainb.redo();
	ainb.solidrm();
	bouta.redo();
	bouta.solidrm();

	// glue null faces
	for(i=0; i<nfaca; i++)
	  {
	    lonfa[i].lkfmrh(lonfb[nfaca+i]);
	    lonfa[i].lglue();
	  }
	break;
      }
    return res ;
  }

  void	setop_init(Solid a, Solid b)
  {
    double   range;

    // put arguments into global spots
    sha = a;
    shb = b;

    // initialize counters etc.
    n_eopdone = 0;

    // adjust names
    a.scannames();
    b.modifynames(maxf, maxv);
    b.scannames();

    // store max. vertex no.
    maxv1 = (short)maxv;

    // calculate boxes for all faces
    a.eval_boxes();
    b.eval_boxes();

    // calculate boxes of solids
    a.volbox(boxa);
    b.volbox(boxb);

    // calculate global box
    gbox.xmi = (float)INF1;
    gbox.ymi = (float)INF1;
    gbox.zmi = (float)INF1;
    gbox.xma = (float)(-INF1);
    gbox.yma = (float)(-INF1);
    gbox.zma = (float)(-INF1);

    boxa.updbox(gbox);
    boxb.updbox(gbox);

    // translate to origin
    boxa.trans_box((float)(-gbox.xmi), (float)(-gbox.ymi), (float)(-gbox.zmi));
    boxb.trans_box((float)(-gbox.xmi), (float)(-gbox.ymi), (float)(-gbox.zmi));
    a.strans((float)(-gbox.xmi), (float)(-gbox.ymi), (float)(-gbox.zmi));
    b.strans((float)(-gbox.xmi), (float)(-gbox.ymi), (float)(-gbox.zmi));

    // set epsilons
    range = (double)(gbox.xma - gbox.xmi);
    if(gbox.yma - gbox.ymi > (float)range)
      range = (double)(gbox.yma - gbox.ymi);
    if(gbox.zma - gbox.zmi > (float)range)
      range = (double)(gbox.zma - gbox.zmi);

    EPS_old = eps;
  }

  void	checklone()
  {
    int	i;

    for(i=0; i<nedg; i++)
      {
	if(lone[i].nea.checknulledge() != 0)
	  {
	    HalfEdge tmp;

	    tmp = lone[i].nea.he1;
	    lone[i].nea.he1 = lone[i].nea.he2;
	    lone[i].nea.he2 = tmp;
	  }

	if(lone[i].neb.checknulledge() != 0)
	  {
	    HalfEdge tmp;

	    tmp = lone[i].neb.he1;
	    lone[i].neb.he1 = lone[i].neb.he2;
	    lone[i].neb.he2 = tmp;
	  }
      }
  }

  int	setop_connect()
  {
    int	i;
    int	i1, i2;
    int	advance;	// did bugs advance ?

    // copy the list of null edges
    for(i=0; i<nedg; i++)
      list[i] = lone[i];

    n_list = nedg;

    // sort copied list lexicographically by x, y, z
    dlexsort();

    // initialize
    dinibug();
    nloose = 0;

    // loop while null edges remain
    while(n_list > 0)
      {
	advance = 0;
	for(i=0; i<n_list; i++)
	  {
	    // check whether list[i] can be appended to the intersection polygon
	    i1 = bug1.dreach(list[i], newbug1);

	    i2 = bug2.dreach(list[i], newbug2);

	    if(i1!=0 && i2!=0)
	      {
		// finish up with this polygon
		bug1.buga.join2(newbug1.buga);
		bug1.bugb.join2(newbug1.bugb);
		bug2.buga.join2(newbug2.buga);
		bug2.bugb.join2(newbug2.bugb);

		// cut all three null edges
		if(bug1.buga.edge == bug2.buga.edge)
		  bug1.dcut();
		else
		  {
		    bug1.dcut();
		    bug2.dcut();
		  }
		newbug1.dcut();
		dremove(i);

		// the current loop is closed
		loopopen = 0;

		// start a new intersection face (if any)
		dinibug();
		advance = 1;
		break;
	      }

	    else if(i1 != 0)
	      {
		// append it to the intersection face
		bug1.buga.join2(newbug1.buga);

		bug1.bugb.join2(newbug1.bugb);

		if(bug1.buga.edge != bug2.buga.edge)
		  bug1.dcut();
		bug1.buga = newbug1.buga.mate();
		bug1.bugb = newbug1.bugb.mate();
		dremove(i);
		advance = 1;
		break;
	      }
	    else if(i2 != 0)
	      {
		bug2.buga.join2(newbug2.buga);
		bug2.bugb.join2(newbug2.bugb);
		if(bug2.buga.edge != bug1.buga.edge)
		  bug2.dcut();
		bug2.buga = newbug2.buga.mate();
		bug2.bugb = newbug2.bugb.mate();
		dremove(i);
		advance = 1;
		break;
	      }
	  }

	if(advance == 0)
	  {
	    // couldn't close an intersection polygon -- store loose ends for repair
	    loose[nloose].buga = bug1.buga;
	    loose[nloose].bugb = bug1.bugb;
	    nloose++;
	    loose[nloose].buga = bug2.buga;
	    loose[nloose].bugb = bug2.bugb;
	    nloose++;
	    // continue with a new polygon
	    dinibug();
	  }
	}

    // is a loop still open ?
    if(loopopen != 0)
      {
	System.out.println("setop_connect: did not complete an intersection polygon");

	// couldn't close the last intersection polygon -- store loose ends for repair
	loose[nloose].buga = bug1.buga;
	loose[nloose].bugb = bug1.bugb;
	nloose++;
	loose[nloose].buga = bug2.buga;
	loose[nloose].bugb = bug2.bugb;
	nloose++;
      }

    // if there are loose ends remaining, try to repair them
    if(nloose > 0)
      {
	System.out.println("loose ends---attempting repair..." + nloose);
	repair();

	// if all loose ends can be repaired, signal success
	if(nloose == 0)
	  {
	    System.out.println("repair successful!");
	    return(SUCCESS);
	  }
	else
	  {
	    System.out.println("repair failed, loose ends remaining " + nloose);
	    return(ERROR);
	  }
      }
    return(SUCCESS);
  }

  // initialization step: start a new intersection face
  void dinibug()
  {
    // are there null edges not connected yet ?
    if(n_list > 0)
      {
	// initialize a new intersection face
	bug1.buga = list[0].nea.he1;
	bug2.buga = list[0].nea.he2;
	bug1.bugb = list[0].neb.he2;		// N.B. orientation
	bug2.bugb = list[0].neb.he1;
	dremove(0);
	// a loop was opened
	loopopen = 1;
      }
  }

  // lexicographic sort of list
  void dlexsort()
  {
    int	i, j;

    for(i=0; i<nedg-1; i++)
      for(j=i+1; j<nedg; j++)
	if(dgreater(i, j) == 1)
	  dswap(i, j);
  }

  int dgreater(int i, int j)
  {
    Vertex  v1, v2;
    int     com, retcode;

    v1 = list[i].nea.he1.vertex;
    v2 = list[j].nea.he1.vertex;

    retcode = 1;
    if((com = comp(v1.vcoord.feq[0], v2.vcoord.feq[0], CONTVVEPS)) == -1)
      retcode = -1;
    else if(com == 0)
      {
	if((com = comp(v1.vcoord.feq[1], v2.vcoord.feq[1], CONTVVEPS)) == -1)
	  retcode = -1;
	else if(com == 0)
	  {
	    if((com = comp(v1.vcoord.feq[2], v2.vcoord.feq[2], CONTVVEPS)) == -1)
	      retcode = -1;
	  }
      }
    return(retcode);
  }

  void	dswap(int i, int j)
  {
    Nedge   temp;

    temp = list[i];
    list[i] = list[j];
    list[j] = temp;
  }

  // remove a null edge from list
  void dremove(int i)
  {
    int n;

    for(n = i+1; n<n_list; n++)
      list[n-1] = list[n];
    n_list--;
  }

  //	Try to correct classification errors -- last resort of the joining algorithm
  void repair()
  {
    int		i, j;
    HalfEdge	he2a, he2b, he[];
    int         tmpflg = 0;

    he = new HalfEdge[2];

    // initially, none of loose ends have been salvaged
    for(i=0; i<nloose; i++)
      isok[i] = 0;

    // for each loose end not salvaged yet, try to find a
    // repairable neighbor
    while (tmpflg == 0)
      {
	// first pass: apply "simple" repairs only
	for(i=0; i<nloose; i++)
	  {
	    // ignore if this has been repaired already
	    if(isok[i] != 0)
	      continue;

	    bug1 = loose[i];

	    // N.B. every pair will be examined twice so we can
	    // check for one-way repairability only
	    for(j=0; j<nloose; j++)
	      {
		// don't compare with itself
		if(isok[j]!=0 || i == j)
		  continue;

		bug2 = loose[j];

		// don't consider bugs just joined
		he2a = bug1.buga;
		he2b = bug2.buga;

		if((he2a.mate().wloop == he2b.mate().wloop) &&
		   he2a.mate().wloop.length == 4)
		  continue;

		// don't consider halves of a single edge
		if(bug1.buga == bug2.buga.mate())
		  continue;

		if(bug1.buga.breach(bug2.buga)!=0 &&
		   bug1.bugb.canrepair1(bug2.bugb)!=0)
		  {
		    bug1.bugb.dorepair1(bug2.bugb);
		    joinbugs();
		    isok[i] = 1;
		    isok[j] = 1;
		    //a successful repair can have side effects as for the
		    // repairability of some preceding cases => restart
		    tmpflg = 1;
		    break;
		  }
		if(bug1.buga.breach(bug2.buga)!=0 &&
		   bug1.bugb.canrepair2(bug2.bugb)!=0)
		  {
		    bug1.bugb.dorepair2(bug2.bugb);
		    joinbugs();
		    isok[i] = 1;
		    isok[j] = 1;
		    tmpflg = 1;
		    break;
		  }
		if(bug1.bugb.breach(bug2.bugb)!=0 &&
		   bug1.buga.canrepair1(bug2.buga)!=0)
		  {
		    bug1.buga.dorepair1(bug2.buga);
		    joinbugs();
		    isok[i] = 1;
		    isok[j] = 1;
		    tmpflg = 1;
		    break;
		  }
		if(bug1.bugb.breach(bug2.bugb)!=0 &&
		   bug1.buga.canrepair2(bug2.buga)!=0)
		  {
		    bug1.buga.dorepair2(bug2.buga);
		    joinbugs();
		    isok[i] = 1;
		    isok[j] = 1;
		    tmpflg = 1;
		    break;
		  }
	      }

	    if(tmpflg == 1)
	      break;
	  }
      	if(tmpflg == 1)
	  continue;

	// count remaining loose ends
	j = 0;
	for(i=0; i<nloose; i++)
	  if(isok[i] == 0)
	    {
	      j++;
	    }

	// second pass: attempt more serious repairs
	if(j > 0)
	  for(i=0; i<nloose; i++)
	    {
	      if(isok[i]!=0)
		continue;
	      bug1 = loose[i];

	      for(j=0; j<nloose; j++)
		{
		  if(isok[j]!=0 || i == j)
		    continue;

		  bug2 = loose[j];

		  he2a = bug1.buga;
		  he2b = bug2.buga;

		  if((he2a.mate().wloop == he2b.mate().wloop) &&
		     he2a.mate().wloop.length == 4)
		    continue;

		  if(bug1.buga == bug2.buga.mate())
		    continue;

		  he[0] = he2a;
		  he[1] = he2b;

		  if(bug1.buga.breach(bug2.buga)!=0 &&
		     bug1.bugb.canrepair3(bug2.bugb, he)!=0)
		    {
		      he2a = he[0];
		      he2b = he[1];
		      bug1.bugb.dorepair3(bug2.bugb, he);
		      he2a = he[0];
		      he2b = he[1];
		      joinbugs();
		      isok[i] = 1;
		      isok[j] = 1;
		      tmpflg = 1;
		      break;
		    }
		  he2a = he[0];
		  he2b = he[1];
		  if(bug1.bugb.breach(bug2.bugb)!=0 &&
		     bug1.buga.canrepair3(bug2.buga, he)!=0)
		    {
		      he2a = he[0];
		      he2b = he[1];
		      bug1.buga.dorepair3(bug2.buga, he);
		      he2a = he[0];
		      he2b = he[1];
		      joinbugs();
		      isok[i] = 1;
		      isok[j] = 1;
		      tmpflg = 1;
		      break;
		    }
		  he2a = he[0];
		  he2b = he[1];
		  if(bug1.buga.breach(bug2.buga)!=0 &&
		     bug1.bugb.canrepair4(bug2.bugb, he)!=0)
		    {
		      he2a = he[0];
		      he2b = he[1];
		      bug1.bugb.dorepair4(bug2.bugb, he);
		      he2a = he[0];
		      he2b = he[1];
		      joinbugs();
		      isok[i] = 1;
		      isok[j] = 1;
		      tmpflg = 1;
		      break;
		    }
		  he2a = he[0];
		  he2b = he[1];
		  if(bug1.bugb.breach(bug2.bugb)!=0 &&
		     bug1.buga.canrepair4(bug2.buga, he)!=0)
		    {
		      he2a = he[0];
		      he2b = he[1];
		      bug1.buga.dorepair4(bug2.buga, he);
		      he2a = he[0];
		      he2b = he[1];
		      joinbugs();
		      isok[i] = 1;
		      isok[j] = 1;
		      tmpflg = 1;
		      break;
		    }
		  he2a = he[0];
		  he2b = he[1];
		}
	      if (tmpflg == 1)
		break;
	    }
	if (tmpflg == 1)
	  continue;

	// count remaining loose ends
	j = 0;
	for(i=0; i<nloose; i++)
	  if(isok[i] == 0)
	    {
	      j++;
	    }

	// third pass: last resort of last resort, repair two ends at a time
	if(j > 0)
	  for(i=0; i<nloose; i++)
	    {
	      if(isok[i] != 0)
		continue;
	      bug1 = loose[i];

	      for(j=0; j<nloose; j++)
		{
		  if(isok[j]!=0 || i == j)
		    continue;

		  bug2 = loose[j];

		  // don't mess intersection polygons
		  he2a = bug1.buga;
		  he2b = bug2.buga;
		  if(he2a.mate().wloop == he2b.mate().wloop)
		    continue;

		  if(bug1.buga == bug2.buga.mate())
		    continue;

		  he2a = he[0];
		  he2b = he[1];

		  if(bug1.bugb.breach(bug2.buga)!=0)
		    {
		      if(bug1.bugb.facelookup(bug2.bugb, he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair2(bug1.bugb);
			  he2a.dorepair2(bug2.bugb);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		      if(bug1.bugb.mate().facelookup(bug2.bugb, he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair1(bug1.bugb);
			  he2a.dorepair2(bug2.bugb);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		      if(bug1.bugb.facelookup(bug2.bugb.mate(),he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair2(bug1.bugb);
			  he2a.dorepair1(bug2.bugb);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		      if(bug1.bugb.mate().facelookup(bug2.bugb.mate(), he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair1(bug1.bugb);
			  he2a.dorepair1(bug2.bugb);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		    }

		  if(bug1.bugb.breach(bug2.bugb)!=0)
		    {
		      if(bug1.buga.facelookup(bug2.buga, he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair2(bug1.buga);
			  he2a.dorepair2(bug2.buga);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		      if(bug1.buga.mate().facelookup(bug2.buga, he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair1(bug1.buga);
			  he2a.dorepair2(bug2.buga);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		      if(bug1.buga.facelookup(bug2.buga.mate(), he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair2(bug1.buga);
			  he2a.dorepair1(bug2.buga);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
			}
		      he2a = he[0];
		      he2b = he[1];
		      if(bug1.buga.mate().facelookup(bug2.buga.mate(), he)!=0)
			{
			  he2a = he[0];
			  he2b = he[1];
			  he2b.dorepair1(bug1.buga);
			  he2a.dorepair1(bug2.buga);
			  joinbugs();
			  isok[i] = 1;
			  isok[j] = 1;
			  tmpflg = 1;
			  break;
		    }
		      he2a = he[0];
		      he2b = he[1];
		    }
		}
	      if (tmpflg == 1)
		break;
	    }
	if (tmpflg == 1)
	  continue;

	j = 0;
	for(i=0; i<nloose; i++)
	  if(isok[i] == 0)
	    {
	      if(loose[i].isloose()!=0)
	    System.out.println("---dangling null edge, likely to be OK");
	      j++;
	    }
	nloose = j;

	if (tmpflg == 0)
	  break;
      }
  }

  // join bugs after repair
  void joinbugs()
  {
    bug1.buga.join2(bug2.buga);
    bug1.bugb.join2(bug2.bugb);

    // cut the repaired bug first --- cannot trust its orientation

    if(bug2.isloose() == 0)	// don't cut if its mate is still loose
      bug2.dcut();

    if(bug1.buga.edge.he1.wloop.lface == bug1.buga.edge.he2.wloop.lface &&
       bug1.bugb.edge.he1.wloop.lface != bug1.bugb.edge.he2.wloop.lface)
      {
	System.out.println("bug1.bugb out of order:");
      }
    if(bug1.buga.edge.he1.wloop.lface != bug1.buga.edge.he2.wloop.lface &&
       bug1.bugb.edge.he1.wloop.lface == bug1.bugb.edge.he2.wloop.lface)
      {
	System.out.println("bug1.buga out of order");
      }
    if(bug1.isloose() == 0)
      bug1.dcut();
  }

  void setop_1(Solid a, Solid b)
  {
    Face   f1, f2;

    while((f1 = scn_faca()) != null)
      {
	while((f2 = scn_facb()) != null)
	  {
	    if(f1.oppositeeqs(f2) != 0)
	      {
		f1.do_setop_1(f2, (short) OPPOSITE);
	      }
	    else if(f1.equaleqs(f2) != 0)
	      {
		f1.do_setop_1(f2, (short) NONOPPOSITE);
	      }
	  }
      }
  }

  void	setop_2(Solid a, Solid b)
  {
    Face  f1, f2;

    while((f1 = scn_faca()) != null)
      {
	while((f2 = scn_facb()) != null)
	  {
	    if(f1.haseq() == 0)
	      {
		System.out.println("face f1 has no equation!");
		f1.flout.newell(f1.feq);
	      }
	    if(f2.haseq() == 0)
	      {
		System.out.println("face f2 has no equation!");
		f2.flout.newell(f2.feq);
	      }
	    f1.testff(f2, 0);
	    f2.testff(f1, 1);
	  }
      }
  }

  void	setop_gen(Solid s1, Solid s2)
  {
    Face  f1, f2;

    // make "sentinel"
    rel_fa[0].rfac = null;
    rel_fa[0].first = 0;
    rel_fa[0].last = 0;
    nrel_fa = 0;
    nrel_fb = 0;

    f1 = s1.sfaces;
    while(f1 != null)
      {
	// does the box of f1 intersect the other solid's box ?
	if(f1.fbox.intbb1(boxb) != 0)
	  {
	    // yes, scan all faces of s2
	    f2 = s2.sfaces;
	    while(f2 != null)
	      {
		// box test
		if(f1.fbox.intbb1(f2.fbox) != 0)
		  {
		    // make this face pair relevant
		    f1.addrel_f(f2);

		    // evaluate face equations
		    if(f1.haseq() == 0)
		      f1.flout.newell(f1.feq);
		    if(f2.haseq() == 0)
		      f2.flout.newell(f2.feq);
		  }
		f2 = f2.nextf;
	      }
	  }

	f1 = f1.nextf;
      }

    rel_fa[nrel_fa].last = (short)nrel_fb;
    nrel_fa++;		             // last + 1
    Faceindexa = 1;		     // because 0 = sentinel
  }

  // scan relevant faces
  Face	scn_faca()
  {
    Face  ret;

    if(Faceindexa == nrel_fa)
      {
	Faceindexa = 1;
	return(null);
      }
    else
      {
	Faceindexb = rel_fa[Faceindexa].first;
	Top = rel_fa[Faceindexa].last;
	ret = rel_fa[Faceindexa++].rfac;

	return(ret);
      }
  }

  Face	scn_facb()
  {
    Face	ret;

    if(Faceindexb == Top)
      return(null);
    else
      {
	// don't return removed things
	if((ret = rel_fb[Faceindexb++]) == null)
	  return(scn_facb());
	else
	  {
	    return(ret);
	  }
      }
  }

  void setOpGenerate(Solid a, Solid b)
  {
    Edge e;

    nvtx = 0;
    nvtxa = 0;
    nvtxb = 0;

    for (e = a.sedges; e!= null; e=e.nexte)
      processEdge(e,b,0);
    for (e = b.sedges; e!= null; e=e.nexte)
      processEdge(e,a,1);
  }

  void processEdge(Edge e, Solid s, int bvsa)
  {
    Face f;

    for (f = s.sfaces; f!= null; f=f.nextf)
	doSetOpGenerate(e,f,bvsa);
  }

  void doSetOpGenerate(Edge e, Face f, int bvsa)
  {
    Vertex v1, v2;
    double d1, d2, t, x, y, z;
    int    s1, s2, cont;

    v1 = e.he1.vertex;
    v2 = e.he2.vertex;
    s1 = comp((d1 = (double)v1.vcoord.dist(f.feq)),0,eps);
    s2 = comp((d2 = (double)v2.vcoord.dist(f.feq)),0,eps);

    if( s1 == -1 && s2 == 1 || s1 == 1 && s2 == -1 )
      {
	t = d1/(d1-d2);
	x = v1.vcoord.feq[0] + t*(v2.vcoord.feq[0]-v1.vcoord.feq[0]);
	y = v1.vcoord.feq[1] + t*(v2.vcoord.feq[1]-v1.vcoord.feq[1]);
	z = v1.vcoord.feq[2] + t*(v2.vcoord.feq[2]-v1.vcoord.feq[2]);
	cont = contfp(f, (float)x, (float)y, (float)z);
	if(cont == 1)
	  {
	    e.he1.lmev(e.he2.next, ++maxv, (float)x, (float)y, (float)z);
	    addSovf(e.he1.vertex, f, bvsa);
	    processEdge(e.he1.prev.edge, f.fsolid, bvsa);
	  }
	else if(cont == 2)
	  {
	    e.he1.lmev(e.he2.next, ++maxv, (float)x, (float)y, (float)z);
	    hithe.lmev(hithe.mate().next, ++maxv, (float)x, (float)y, (float)z);
	    addSovv(e.he1.vertex, hithe.vertex, bvsa);
	    processEdge(e.he1.prev.edge, f.fsolid, bvsa);
	  }
	else if (cont == 3)
	  {
	    e.he1.lmev(e.he2.next, ++maxv, (float)x, (float)y, (float)z);
	    addSovv(e.he1.vertex, hitvertex, bvsa);
	    processEdge(e.he1.prev.edge, f.fsolid, bvsa);
	  }
      }
    else
      {
	if(s1==0)
	  doVertexOnFace(v1,f, bvsa);
	if(s2==0)
	  doVertexOnFace(v2,f,bvsa);
      }
  }

  void doVertexOnFace(Vertex v, Face f, int bvsa)
  {
    int cont;

    cont = f.contfv(v);
    if(cont == 1)
      addSovf(v,f,bvsa);
    else if (cont == 2)
      {
	hithe.lmev(hithe.mate().next, ++maxv, v.vcoord.feq[0],
	     v.vcoord.feq[1], v.vcoord.feq[2]);
	addSovv(v, hithe.vertex, bvsa);
      }
    else if (cont == 3)
      addSovv(v, hitvertex, bvsa);
  }

  void addSovf(Vertex v, Face f, int bvsa)
  {
    int i;

    if(bvsa != 0)
      {
	for(i=0;i< nvtxb; i++)
	  if(( sonvb[i].v == v) && (sonvb[i].f == f))
	    return;
	sonvb[nvtxb].v = v;
	sonvb[nvtxb++].f = f;
      }
    else
      {
	for(i=0;i< nvtxa; i++)
	  if(( sonva[i].v == v) && (sonva[i].f == f))
	    return;
	sonva[nvtxa].v = v;
	sonva[nvtxa++].f = f;
      }
  }

  void addSovv(Vertex v1, Vertex v2, int bvsa)
  {
    int i;

    if(bvsa != 0)
      {
	for(i=0;i<nvtx;i++)
	  if( (sonvv[i].vb == v1) && (sonvv[i].va == v2) )
	    return;
	sonvv[nvtx].vb = v1;
	sonvv[nvtx++].va = v2;
      }
    else
      {
	for(i=0;i<nvtx;i++)
	  if( (sonvv[i].vb == v2) && (sonvv[i].va == v1) )
	    return;
	sonvv[nvtx].vb = v2;
	sonvv[nvtx++].va = v1;
      }
  }

  // separate edge-sequence from ... to
  void	separ1(HalfEdge from, HalfEdge to, int type)
  {
    // recover from null edges already inserted
    if(from.prev.nulledge()!=0 && from.prev.strutnulledge()!=0)
      {
	// look at orientation
	if(from.prev == from.prev.edge.he2)
	  {
	    from = from.prev.prev;
	  }
      }
    if(to.prev.nulledge()!=0 && to.prev.strutnulledge()!=0)
      {
	if(to.prev == to.prev.edge.he1)
	  {
	    to = to.prev.prev;
	  }
      }
    if(from.vertex != to.vertex)
      {
	if(from.prev == to.prev.mate())
	  {
	    from = from.prev;
	  }
	else if(from.prev.vertex == to.vertex)
	  {
	    from = from.prev;
	  }
	else if(to.prev.vertex == from.vertex)
	  {
	    to = to.prev;
	  }
      }

    from.lmev(to, ++maxv, from.vertex.vcoord.feq[0],
	      from.vertex.vcoord.feq[1], from.vertex.vcoord.feq[2]);

    if(type == 0)
      lone[nedg].nea = from.prev.edge;
    else
      lone[nedg].neb = from.prev.edge;
  }

  // separate `interior' of the face of he
  void separ2(HalfEdge he, int type, int orient)
  {
    HalfEdge	tmp;

    // recover from null edges inserted
    if(he.prev.nulledge() != 0)
      {
	if(((he.prev == he.prev.edge.he1) && orient != 0) ||
	   ((he.prev == he.prev.edge.he2) && orient == 0))
	  {
	    he = he.prev;
	  }
      }

    he.lmev(he, ++maxv, he.vertex.vcoord.feq[0], he.vertex.vcoord.feq[1],
		he.vertex.vcoord.feq[2]);

    // a piece of Black Art: reverse orientation of the null edge
    if(orient != 0)
      {
	tmp = he.prev.edge.he1;
	he.prev.edge.he1 = he.prev.edge.he2;
	he.prev.edge.he2 = tmp;
      }

    if(type == 0)
      lone[nedg].nea = he.prev.edge;
    else
      lone[nedg].neb = he.prev.edge;
  }

  int faceeq(Loop l, Vectors eq)
  {
    HalfEdge	he;
    double	a, b, c, norm;
    double	xi, yi, zi, xj, yj, zj, xc, yc, zc;
    int		len;

    a = b = c = xc = yc = zc = 0;
    len = 0;
    he = l.ledge;

    do
      {
	xi = he.vertex.vcoord.feq[0];
	yi = he.vertex.vcoord.feq[1];
	zi = he.vertex.vcoord.feq[2];

	xj = he.next.vertex.vcoord.feq[0];
	yj = he.next.vertex.vcoord.feq[1];
	zj = he.next.vertex.vcoord.feq[2];

	a += (yi - yj) * (zi + zj);
	b += (zi - zj) * (xi + xj);
	c += (xi - xj) * (yi + yj);

	xc += xi;
	yc += yi;
	zc += zi;

	len++;
      }
    while ((he = he.next) != l.ledge);

    if ((norm = Math.sqrt(a*a + b*b + c*c)) != 0)
      {
	eq.feq[0] = (float)(a/norm);
	eq.feq[1] = (float)(b/norm);
	eq.feq[2] = (float)(c/norm);
	eq.feq[3] = (float)((eq.feq[0]*xc + eq.feq[1]*yc + eq.feq[2]*zc)/(-len));
	return 1;
      }
    else
      return -1;
  }

  int contfp(Face f, float x, float y, float z)
  {
    Vertex v1;

    v1 = new Vertex(this);

    v1.vcoord.feq[0] = x;
    v1.vcoord.feq[1] = y;
    v1.vcoord.feq[2] = z;
    v1.vcoord.feq[3] = 1;
    return (f.contfv(v1));
  }

  int smev(int s, int f1, int v1, int v2, float x, float y, float z)
  {
    Solid	oldSolid;
    Face	oldFace;
    Loop        oldLoop;
    HalfEdge	he1[];

    if ((oldSolid = getSolid(s)) == null)
      return ERROR;

    if ((oldFace = oldSolid.fFace(f1)) == null)
      return ERROR;

    // get first halfedge that includes v1
    he1 = new HalfEdge[1];
    he1[0] = new HalfEdge(this, 0);
    oldLoop = oldFace.floop(v1, he1);

    if (oldLoop == null)
      return ERROR;

    he1[0].lmev(he1[0], v2, x, y, z);
    return SUCCESS;
  }

  int smef(int s, int v1, int v2, int f1, int f2)
  {
    Solid	oldSolid;
    Loop        oldLoop;
    Face	oldFace1;
    HalfEdge	he1[], he2[];
    int         count;

    if ((oldSolid = getSolid(s)) == null)
      return ERROR;

    if ((oldFace1 = oldSolid.fFace(f1)) == null)
      return ERROR;

    he1 = new HalfEdge[1];
    he1[0] = new HalfEdge(this, 0);
    // get vert1 in the old loop
    oldLoop = oldFace1.floop(v1, he1);
    if(oldLoop == null)
      return ERROR;

    // get the next occurrence of v2
    he2 = new HalfEdge[1];
    he2[0] = he1[0];
    count = 0;
    while (he2[0].vertex.vertexNum != v2)
      {
	he2[0] = he2[0].next;
	count++;
	if(count > oldLoop.length)
	  return ERROR;
      }

    he1[0].lmef(he2[0], f2);
    return SUCCESS;
  }

  int comp(double a, double b, double to1)
  {
    double delta;

    delta = Math.abs(a-b);
    if (delta < to1)
      return 0;
    else if (a > b)
      return 1;
    else
      return -1;
  }

  int intRev(Vertex v1, Vertex v2, Vertex v3, double t[])
  {
    Vertex		testv;
    Vectors		r1, r2;
    float		r1r1, tprime;

    r1 = new Vectors();
    r2 = new Vectors();
    testv = new Vertex(this);

    r1.feq[0] = v2.vcoord.feq[0] - v1.vcoord.feq[0];
    r1.feq[1] = v2.vcoord.feq[1] - v1.vcoord.feq[1];
    r1.feq[2] = v2.vcoord.feq[2] - v1.vcoord.feq[2];

    r1r1  = (float)r1.dot(r1);
    if(r1r1 < eps*eps)
      {
	//t = 0;
	t[0] = 0;
	return (v1.contvv(v3));
      }
    else
      {
	r2.feq[0] = v3.vcoord.feq[0] - v1.vcoord.feq[0];
	r2.feq[1] = v3.vcoord.feq[1] - v1.vcoord.feq[1];
	r2.feq[2] = v3.vcoord.feq[2] - v1.vcoord.feq[2];

	tprime = (float)r1.dot(r2)/r1r1;

	testv.vcoord.feq[0] = v1.vcoord.feq[0] + tprime * r1.feq[0];
	testv.vcoord.feq[1] = v1.vcoord.feq[1] + tprime * r1.feq[1];
	testv.vcoord.feq[2] = v1.vcoord.feq[2] + tprime * r1.feq[2];

	//t = (double)tprime;
	t[0] = (double)tprime;
	return (testv.contvv(v3));
      }
  }

  // initialize a plane tag
  Tag newplane(int tag_id, float a, float b, float c, float d)
  {
    Tag	 ptr;
    int	 n;

    ptr = new Tag(this, a, b, c, d);

    TagArray[tag_id] = ptr;

    return ptr;
  }

  Tag newcparc(int tag_id, float p1x, float p1y, float p1z,
	       float p2x, float p2y, float p2z,
	       float cx, float cy, float cz,
	       float a, float b, float c, float d)
  {
    Tag	cpa;

    cpa = new Tag(this, p1x, p1y, p1z, p2x, p2y, p2z,
		  cx, cy, cz, a, b, c, d);
    TagArray[tag_id] = cpa;

    return cpa;
  }

  // initialize a cone tag

  Tag  newcone(int tag_id, float r, float h, float rx, float ry, float rz,
	       float tx, float ty, float tz, short sign)
  {
    Mat	m;

    m = new Mat();

    m.matident();
    m.matrotate(rx, ry, rz);
    m.mattrans(tx, ty, tz);

    return (new_m_cone(tag_id, r, h, m, sign));
  }

  Tag  new_m_cone(int tag_id, float r, float h, Mat m, short sign)
  {
    Tag  ptr;
    int	 n;

    ptr = new Tag(this, r, h, m, sign);

    TagArray[tag_id] = ptr;

    return ptr;
  }

  Tag  newrotsurf(int tag_id, float y0, float a, float b, float theta,
		  float rx, float ry, float rz,
		  float tx, float ty, float tz, short sign)
  {
    Mat	m;

    m = new Mat();
    m.matident();
    m.matrotate(rx, ry, rz);
    m.mattrans(tx, ty, tz);

    return (new_m_rotsurf(tag_id, y0, a, b, theta, m, sign));
  }

  Tag  new_m_rotsurf(int tag_id, float y0, float a, float b,
		     float theta, Mat m, short sign)
  {
    Tag  ptr;

    ptr = new Tag(this, y0, a, b, theta, m, sign);

    TagArray[tag_id] = ptr;
    return ptr;
  }

  // initialize a sphere tag
  Tag  newsphere(int tag_id, float r, float rx, float ry, float rz,
		 float tx, float ty, float tz, short sign)
  {
    Mat	m;

    m = new Mat();

    m.matident();
    m.matrotate(rx, ry, rz);
    m.mattrans(tx, ty, tz);

    return (new_m_sphere(tag_id, r, m, sign));
  }

  Tag new_m_sphere(int tag_id, float r, Mat m, short sign)
  {
    Tag	ptr;

    ptr = new Tag(this, r, m, sign, 2);

    TagArray[tag_id] = ptr;

    return ptr;
  }

  // initialize a cylinder tag

  Tag  newcylinder(int tag_id, float r, float rx, float ry, float rz,
		   float tx, float ty, float tz, short sign)
  {
    Mat	m;

    m = new Mat();

    m.matident();
    m.matrotate(rx, ry, rz);
    m.mattrans(tx, ty, tz);

    return (new_m_cylinder(tag_id, r, m, sign));
  }

  Tag  new_m_cylinder(int tag_id, float r, Mat m, short sign)
  {
    Tag	 ptr;
    int	 n;

    ptr = new Tag(this, r, m, sign, 1);

    TagArray[tag_id] = ptr;

    return ptr;
  }

  Solid getSolid(int sn)
  {
    Solid s;

    for (s=firsts; s!=null; s=s.nexts)
      if (s.solidNum == sn)
	return s;
    return null;
  }

  void getMaxNames(int sn)
  {
    Solid	s;
    Vertex	v;
    Face	f;

    s = getSolid(sn);
    for (v=s.sverts, maxv=0; v!=null; v=v.nextv)
      if (v.vertexNum > maxv)
	maxv = v.vertexNum;
    for (f=s.sfaces, maxf=0; f!=null; f=f.nextf)
      if (f.faceNum > maxf)
	maxf = f.faceNum;
  }

  float	polarangle(float x, float y)
  {
    float phi;

    if(x == 0)
      {
	if(y == 0)
	  {
	    return(0);
	  }
	else if(y > 0)
	  return(90);
	else
	  return(270);
      }
    else
      {
	if(x > 0)
	  phi = (y >= 0) ? (float)(Math.atan(y/x)) :
	  (float)(2.0 * PI + Math.atan(y/x));
	else
	  phi = (float)(PI - Math.atan(y/(-x)));
	return((float)((180 / PI) * phi));
      }
  }

  Solid makeSolid(String inp)
  {
    // String is the entire CSG tree description being fed in
    Solid	res;
    Tree	csgroot;

    csgroot = BuildTree(inp.charAt(0));
    if(csgroot == null)
      return null;

    res = BuildGWBobject(csgroot);
    if(res == null)
      System.out.println("RES is null");

    return(res);
  }

  void convertToPoints(Solid res, int type)
  {
    int         numPoints[], i;
    float       points[];
    long	counter=0, ct=0;
    int         npoints;
    Face	f;
    Loop	l;
    Vertex	v;
    HalfEdge	l3;

    // count the number of faces
    f = res.sfaces;
    while(f != null)
    {
      // send outer loop first
      l = f.flout;
      npoints = l.length;
      l3 = l.ledge;

      ct++;
      for(i=0; i < npoints; i++)
      {
	v = l3.vertex;
	counter+=3;
	l3 = l3.next;
      }
      f= f.nextf;
    }

    numPoints = new int[(int)ct];
    points = new float[(int)counter];

    ct=0;
    counter = 0;

    // count the number of faces
    f = res.sfaces;
    while(f != null)
      {
	// send outer loop first
	l = f.flout;
	npoints = l.length;
	l3 = l.ledge;
	numPoints[(int)ct++] = npoints;
	for(i=0; i < npoints; i++)
	  {
	    v = l3.vertex;
	    points[(int)(counter+3*i)] = v.vcoord.feq[0];
	    points[(int)(counter+3*i+1)] = v.vcoord.feq[1];
	    points[(int)(counter+3*i+2)] = v.vcoord.feq[2];

	    l3 = l3.next;
	  }
	counter += npoints*3;
	f= f.nextf;
      }

    Objects[objectCount] = new ObjDetails(this, ct, counter, type, str,
					     1, 1, 1, 0, 0, 0, 0, 0, 0);
    Objects[objectCount].points = points;
    Objects[objectCount++].nPoints = numPoints;
  }

  // sets the appropriate paint flag depending on the item which has been clicked
  void paintCanvas()
  {
    if (Objects[selectedObject].type == 5)    // display a combined object
      canvas.flag = 2;
    else
      canvas.flag = 0;                        // display all the individual obejcts

    if (canvas.mouseDrag ==1)
      canvas.UpdateScreenTransforms(xangle, yangle);
    else
      canvas.repaint();
  }

  void UpdateTransformValues(float sx, float sy, float sz, float rx, float ry, float rz,
			     float tx, float ty, float tz)
  {
    if(Objects[selectedObject].type == 5)   // transform a combined object
      canvas.flag = 2;
    else
      canvas.flag = 0;

    Objects[selectedObject].setTransformValues(sx, sy, sz, rx, ry, rz, tx, ty, tz);
    canvas.UpdateDraw(Objects[selectedObject], selectedObject);

    if (canvas.mouseDrag ==1)
      canvas.UpdateScreenTransforms(xangle, yangle);
    else
      canvas.repaint();
  }

  /*
  void copyObjects()
  {
    canvas.copyScreenObjects();
  } */

  void paint()
  {
    canvas.repaint();
    setPainted();
  }

}
