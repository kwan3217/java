package csg;

import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class csgCanvas extends Canvas
{
  CSG                   parent;
  static int		oCount=0;         // Object Count
  ObjDetails            drawObjects[];    // Array to maintain the objects to be drawn.
  ObjDetails            screenObjects[];  // Array to maintain the objects currently on the screen.
  // Used to show the rotates associated with the mouse drag
  Mat                   screenMat = new Mat();
  Mat                   transformMat = new Mat();

  int			transformAxes[];
  float			axes[];
  int			max[], min[];

  long			tpc[], tcc[];
  Mat                   m = new Mat();

  int                   flag=0;               // denotes simple or composite object
  int                   mouseDrag = 0;
  int                   numScreenObjects = 0;

  static  final int CANVAS_WIDTH = 600;
  static  final int CANVAS_HEIGHT = 300;

  // The constructor
  public csgCanvas(CSG par)
  {
    parent = par;
    //    resize(600,300);
    resize(CANVAS_WIDTH, CANVAS_HEIGHT);
    setBackground(Color.black);
    axes = new float[12];
    max = new int[3];
    min = new int[3];
    transformAxes = new int[12];
    drawObjects = new ObjDetails[15];
    screenObjects = new ObjDetails[15];
  }

  public void NewDraw(ObjDetails newObject)
  {
    int temp=0;

    // If a new object is being created.
    drawObjects[oCount] = new ObjDetails(parent, newObject.ct, newObject.counter,
					 newObject.type, newObject.csgStr,
					 1, 1, 1, 0, 0, 0, 0, 0, 0);
    drawObjects[oCount].copyObject(newObject);

    screenObjects[oCount] = new ObjDetails(parent, newObject.ct, newObject.counter,
					 newObject.type, newObject.csgStr,
					 1, 1, 1, 0, 0, 0, 0, 0, 0);
    screenObjects[oCount].copyObject(newObject);

    // center the composite object
    if(drawObjects[oCount].type == 5)
    {
      temp = drawObjects[oCount].componentcount;

      if(temp != 0)
      {
	drawObjects[oCount].tx -= drawObjects[oCount].trytx/temp;
	drawObjects[oCount].ty -= drawObjects[oCount].tryty/temp;
	drawObjects[oCount].tz -= drawObjects[oCount].trytz/temp;

	screenObjects[oCount].tx -= drawObjects[oCount].trytx/temp;
	screenObjects[oCount].ty -= drawObjects[oCount].tryty/temp;
	screenObjects[oCount].tz -= drawObjects[oCount].trytz/temp;
      }
    }

    drawObjects[oCount].performOps();
    oCount++;
    repaint();
  }

  void UpdateScreenTransforms(float xangle, float yangle)
  {
    float tempx=0, tempy=0, tempz=0;

    if (flag == 0)   // multiple objects on the screen
    {
      for (int i=0; i<oCount; i++)
      {
	screenObjects[i].copyObject(parent.Objects[i]);
	tempx = screenObjects[i].rx;
	tempy = screenObjects[i].ry;
	tempz = screenObjects[i].rz;
	screenObjects[i].sscale(screenObjects[i].sx, screenObjects[i].sy, screenObjects[i].sz);
	screenObjects[i].srotate(tempx, tempy, tempz);
	screenObjects[i].strans(screenObjects[i].tx+(CANVAS_WIDTH/2),
				screenObjects[i].ty+(CANVAS_HEIGHT/2), screenObjects[i].tz);
	screenObjects[i].strans(-CANVAS_WIDTH/2, -CANVAS_HEIGHT/2, 0);
	screenObjects[i].srotate(xangle, yangle, 0);
	screenObjects[i].strans(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, 0);
      }
    }
    else if (flag == 1)   // single composite object on the screen
    {
      int temp = parent.Objects[oCount-1].componentcount;
      screenObjects[oCount-1].copyObject(parent.Objects[oCount-1]);
      screenObjects[oCount-1].sscale(screenObjects[oCount-1].sx,
				     screenObjects[oCount-1].sy,
				     screenObjects[oCount-1].sz);
      screenObjects[oCount-1].srotate(screenObjects[oCount-1].rx+xangle,
				      screenObjects[oCount-1].ry+yangle,
				      screenObjects[oCount-1].rz);
      screenObjects[oCount-1].tx -= drawObjects[oCount-1].trytx/temp;
      screenObjects[oCount-1].ty -= drawObjects[oCount-1].tryty/temp;
      screenObjects[oCount-1].tz -= drawObjects[oCount-1].trytz/temp;
      screenObjects[oCount-1].strans(screenObjects[oCount-1].tx,
				     screenObjects[oCount-1].ty,
				     screenObjects[oCount-1].tz);
    }
    else if (flag == 2)
    {
      int temp = parent.Objects[parent.selectedObject].componentcount;
      screenObjects[parent.selectedObject].copyObject(parent.Objects[parent.selectedObject]);
      screenObjects[parent.selectedObject].sscale(screenObjects[parent.selectedObject].sx,
				     screenObjects[parent.selectedObject].sy,
				     screenObjects[parent.selectedObject].sz);
      screenObjects[parent.selectedObject].srotate(screenObjects[parent.selectedObject].rx+xangle,
						   screenObjects[parent.selectedObject].ry+yangle,
						   screenObjects[parent.selectedObject].rz);
      screenObjects[parent.selectedObject].tx -= drawObjects[parent.selectedObject].trytx/temp;
      screenObjects[parent.selectedObject].ty -= drawObjects[parent.selectedObject].tryty/temp;
      screenObjects[parent.selectedObject].tz -= drawObjects[parent.selectedObject].trytz/temp;
      screenObjects[parent.selectedObject].strans(screenObjects[parent.selectedObject].tx,
				     screenObjects[parent.selectedObject].ty,
				     screenObjects[parent.selectedObject].tz);
    }

    repaint();
  }

  public void UpdateDraw(ObjDetails changedObject, int SelectedObject)
  {
    drawObjects[SelectedObject].copyObject(changedObject);
    screenObjects[SelectedObject].copyObject(changedObject);

    // center to ensure proper rotates
    if(drawObjects[SelectedObject].type == 5)
    {
      int temp = drawObjects[SelectedObject].componentcount;

      if(temp != 0)
      {
	drawObjects[SelectedObject].tx -= drawObjects[SelectedObject].trytx/temp;
	drawObjects[SelectedObject].ty -= drawObjects[SelectedObject].tryty/temp;
	drawObjects[SelectedObject].tz -= drawObjects[SelectedObject].trytz/temp;

	screenObjects[SelectedObject].tx -= drawObjects[SelectedObject].trytx/temp;
        screenObjects[SelectedObject].ty -= drawObjects[SelectedObject].tryty/temp;
	screenObjects[SelectedObject].tz -= drawObjects[SelectedObject].trytz/temp;
      }
    }

    drawObjects[SelectedObject].performOps();
  }

  public void DeleteDraw()
  {
    for(int i=0; i<oCount; i++)
    {
      drawObjects[i] = new ObjDetails(parent, parent.Objects[i].ct, parent.Objects[i].counter,
				      parent.Objects[i].type, parent.Objects[i].csgStr,
				      1, 1, 1, 0, 0, 0, 0, 0, 0);
      drawObjects[i].copyObject(parent.Objects[i]);
      drawObjects[i].performOps();
    }
    repaint();
  }

  public void paint(Graphics g)
  {
    int xcoords[], ycoords[], zcoords[];
    int i, j , k, temp, incrCol;
    int red, green, blue;
    int xplace, yplace;

    xplace = CANVAS_WIDTH/2;
    yplace = CANVAS_HEIGHT/2;

    if(flag == 0)
    {
      for(k=0; k<oCount; k++)
      {
	if(drawObjects[k].type == 5)
	  continue;

	switch (drawObjects[k].type)
	{
	  case 1 :                   // Sphere
	    g.setColor(Color.pink);
	    break;
	  case 2 :                   // Cube
	    g.setColor(Color.yellow);
	    break;
	  case 3 :                   // Cylinder
	    g.setColor(Color.blue);
	    break;
	  case 4 :                   // Cone
	    g.setColor(Color.green);
	    break;
	  case 5 :                   // Cone
	    g.setColor(Color.magenta);
	    break;
	  default :
	    break;
	}

	temp=0;

	for(i=0; i<drawObjects[k].ct; i++)
	{
	  xcoords = new int[drawObjects[k].nPoints[i]+1];
	  ycoords = new int[drawObjects[k].nPoints[i]+1];
	  zcoords = new int[drawObjects[k].nPoints[i]+1];

	  for(j=0;j<drawObjects[k].nPoints[i];j++)
	  {
	    if (mouseDrag == 1)
	    {
	      xcoords[j] = (int)screenObjects[k].points[temp++];
	      ycoords[j] = (int)screenObjects[k].points[temp++];
	    }
	    else
	    {
	      xcoords[j] = (int)drawObjects[k].points[temp++] + xplace;
	      ycoords[j] = (int)drawObjects[k].points[temp++] + yplace;
	    }
	    temp++;
	  }

	  xcoords[j] = xcoords[0];
	  ycoords[j] = ycoords[0];

	  g.drawPolygon(xcoords, ycoords, drawObjects[k].nPoints[i]+1);
	}
      }
    }
    else if (flag == 1)
    {
      // New Composite Object
      g.setColor(Color.magenta);
      temp=0;

      for(i=0; i<drawObjects[oCount-1].ct; i++)
      {
	xcoords = new int[drawObjects[oCount-1].nPoints[i]+1];
	ycoords = new int[drawObjects[oCount-1].nPoints[i]+1];
	zcoords = new int[drawObjects[oCount-1].nPoints[i]+1];

	for(j=0;j<drawObjects[oCount-1].nPoints[i];j++)
	{
	  if (mouseDrag == 1)
	  {
	    xcoords[j] = (int)screenObjects[oCount-1].points[temp++] + xplace;
	    ycoords[j] = (int)screenObjects[oCount-1].points[temp++] + yplace;
	  }
	  else
	  {
	    xcoords[j] = (int)drawObjects[oCount-1].points[temp++] + xplace;
	    ycoords[j] = (int)drawObjects[oCount-1].points[temp++] + yplace;
	  }
	  temp++;
	}

	xcoords[j] = xcoords[0];
	ycoords[j] = ycoords[0];

	g.drawPolygon(xcoords, ycoords, drawObjects[oCount-1].nPoints[i]+1);
      }
    }
    else if(flag == 2)
    {
      // Modified Composite Object
      g.setColor(Color.magenta);
      temp=0;

      for(i=0; i<drawObjects[parent.selectedObject].ct; i++)
      {
	xcoords = new int[drawObjects[parent.selectedObject].nPoints[i]+1];
	ycoords = new int[drawObjects[parent.selectedObject].nPoints[i]+1];
	zcoords = new int[drawObjects[parent.selectedObject].nPoints[i]+1];

	for(j=0;j<drawObjects[parent.selectedObject].nPoints[i];j++)
	{
	  if (mouseDrag == 1)
	  {
	    xcoords[j] = (int)screenObjects[parent.selectedObject].points[temp++] + xplace;
	    ycoords[j] = (int)screenObjects[parent.selectedObject].points[temp++] + yplace;
	  }
	  else
	  {
	    xcoords[j] = (int)drawObjects[parent.selectedObject].points[temp++] + xplace;
	    ycoords[j] = (int)drawObjects[parent.selectedObject].points[temp++] + yplace;
	  }
	  temp++;
	}

	xcoords[j] = xcoords[0];
	ycoords[j] = ycoords[0];
	g.drawPolygon(xcoords, ycoords, drawObjects[parent.selectedObject].nPoints[i]+1);
      }
    }

    // Draw the coordinate axes
    g.setColor(Color.yellow);
    g.drawLine((int)axes[0]+475, (int)axes[1], (int)axes[3]+475, (int)axes[4]);
    g.drawString("Z", (int)axes[3]+5+475, (int)axes[4]);
    g.setColor(Color.green);
    g.drawLine((int)axes[0]+475, (int)axes[1], (int)axes[6]+475, (int)axes[7]);
    g.drawString("Y", (int)axes[6]+5+475, (int)axes[7]);
    g.setColor(Color.red);
    g.drawLine((int)axes[0]+475, (int)axes[1], (int)axes[9]+475, (int)axes[10]);
    g.drawString("X", (int)axes[9]+475, (int)axes[10]);
  }

  void drawAxes()
  {
    axes[0] = 40.0f;
    axes[1] = 300.0f;
    axes[2] = 50.0f;

    axes[3] = 40.0f;
    axes[4] = 300.0f;
    axes[5] = 10.0f;

    axes[6] = 40.0f;
    axes[7] = 260.0f;
    axes[8] = 50.0f;

    axes[9] = 80.0f;
    axes[10] = 300.0f;
    axes[11] = 50.0f;

    for (int i=0; i<3; i++)
      {
	max[i] = (int)axes[i];
	min[i] = (int)axes[i];
      }

    // Determine the bounding box of the axes.
    for (int i=0; i<3; i++)
      {
	for(int j=i; j<12; j+=3)
	  {
	    if (axes[j] > max[i])
	      max[i] = (int)axes[j];
	    if (axes[j] < min[i])
	      min[i] = (int)axes[j];
	  }
      }

    // Transform the axes to the required position.
    transformMat.matident();
  }

  // Function that sets screenMatrix values so that mouse rotations are remembered.
  public void setRotate(float xtheta, float ytheta)
  {

    Mat tempMat = new Mat();

    for(int i=0; i<4; i++)
    {
      tempMat.matident();

      tempMat.mat[0][0] = axes[0+3*i]-40;
      tempMat.mat[0][1] = axes[1+3*i]-300;
      tempMat.mat[0][2] = axes[2+3*i]-50;
      tempMat.mat[0][3] = 1;

      tempMat.matrotate(xtheta, ytheta, 0);

      axes[0+3*i] = tempMat.mat[0][0]+40;
      axes[1+3*i] = tempMat.mat[0][1]+300;
      axes[2+3*i] = tempMat.mat[0][2]+50;
    }
  }

}


