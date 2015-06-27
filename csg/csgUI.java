package csg;

import java.awt.*;

public class csgUI extends Panel
{
  CSG				parent;
  GridBagLayout			screenLayout;
  GridBagConstraints		screenConstraints;
  private Font			headingFont;
  private Font                  f;

  Label				title, object, selObject, translate;
  Label				textString, scale, rotate;
  List				shapes;
  List				shapeList;
  TextField			csgField, msgField;
  Button			Delete;
  Button                        Reset;
  Button                        Clear;

  // The sliders for the translate, scale and rotate tables.
  TitleSlider			translateX;
  TitleSlider			translateY;
  TitleSlider			translateZ;
  TitleSlider			rotateX;
  TitleSlider			rotateY;
  TitleSlider			rotateZ;
  TitleSlider			scaleX;
  TitleSlider			scaleY;
  TitleSlider			scaleZ;

  public csgUI(CSG testParent)
  {
    parent = testParent;

    screenLayout = new GridBagLayout(); // The applet layout
    setLayout (screenLayout);
    // Instantiate GridBag constraints
    screenConstraints = new GridBagConstraints();

    headingFont = new Font("Helvetica", Font.BOLD, 14);
    f = new Font("Helvetica", Font.BOLD, 12);

    // Set the title for the main applet screen.
    title = new Label("CSG Operations Applet", Label.RIGHT);
    title.setBackground(Color.lightGray);
    title.setForeground(Color.magenta);
    title.setFont(headingFont);
    screenConstraints.fill = GridBagConstraints.BOTH;
    addComponent(title, screenLayout, screenConstraints, 0,0,2,1);

    // Create the Reset button
    Reset = new Button("Reset");
    screenConstraints.fill = GridBagConstraints.NONE;
    screenConstraints.weighty = 10;
    addComponent(Reset, screenLayout, screenConstraints, 0,3,1,1);

    // Set the title for the Objects list.
    object = new Label("Objects:", Label.LEFT);
    object.setForeground(Color.red);
    object.setFont(f);
    screenConstraints.fill = GridBagConstraints.BOTH;
    addComponent(object, screenLayout, screenConstraints, 2,0,1,1);

    // Create the list from which to choose the solids.
    shapes = new List(4,false);	// create a list of 4 items
    shapes.addItem("sphere");
    shapes.addItem("cube");
    shapes.addItem("cylinder");
    shapes.addItem("cone");
    screenConstraints.fill = GridBagConstraints.NONE;
    addComponent(shapes, screenLayout, screenConstraints, 3,0,1,3);

    // Set the title for the Translation Sliders.
    translate = new Label("Translate:", Label.LEFT);
    translate.setForeground(Color.blue);
    translate.setFont(f);
    screenConstraints.fill = GridBagConstraints.BOTH;
    addComponent(translate, screenLayout, screenConstraints, 2,1,1,1);

    // Layout the sliders for Translation.
    translateX = new TitleSlider(this, "Tx", 0.0,10.0,-600,600);
    screenConstraints.weightx = 10;
    addComponent(translateX, screenLayout, screenConstraints, 3,1,1,1);
    translateY = new TitleSlider(this, "Ty", 0.0,10.0,-600,600);
    screenConstraints.weightx = 10;
    addComponent(translateY, screenLayout, screenConstraints, 4,1,1,1);
    translateZ = new TitleSlider(this, "Tz", 0.0,10.0,-600,600);
    screenConstraints.weightx = 10;
    addComponent(translateZ, screenLayout, screenConstraints, 5,1,1,1);

    // Set the title for the selected object list.
    selObject = new Label("Objects Placed:", Label.LEFT);
    selObject.setForeground(Color.red);
    selObject.setFont(f);
    screenConstraints.fill = GridBagConstraints.BOTH;
    addComponent(selObject, screenLayout, screenConstraints, 2,2,2,1);

    // Create the selected object list.
    shapeList = new List(16,false);
    screenConstraints.fill = GridBagConstraints.VERTICAL;
    addComponent(shapeList, screenLayout, screenConstraints, 3,2,2,7);

    // Set the title for the Scaling Sliders.
    scale = new Label("Scale:", Label.LEFT);
    scale.setForeground(Color.blue);
    scale.setFont(f);
    screenConstraints.fill = GridBagConstraints.BOTH;
    addComponent(scale, screenLayout, screenConstraints, 6,0,1,1);

    // Layout the sliders for Scaling.
    scaleX = new TitleSlider(this, "Sx", 1.0,0.5,-10,10);
    addComponent(scaleX, screenLayout, screenConstraints, 7,0,1,1);
    scaleY = new TitleSlider(this, "Sy", 1.0,0.5,-10,10);
    addComponent(scaleY, screenLayout, screenConstraints, 8,0,1,1);
    scaleZ = new TitleSlider(this, "Sz", 1.0,0.5,-10,10);
    addComponent(scaleZ, screenLayout, screenConstraints, 9,0,1,1);

    // Set the label title for the Rotation Sliders.
    rotate = new Label("Rotate:", Label.LEFT);
    rotate.setForeground(Color.blue);
    rotate.setFont(f);
    screenConstraints.fill = GridBagConstraints.BOTH;
    addComponent(rotate, screenLayout, screenConstraints, 6,1,1,1);

    // Layout the sliders for Rotation.
    rotateX = new TitleSlider(this, "Rx", 0.0,5.0,-360,360);
    addComponent(rotateX, screenLayout, screenConstraints, 7,1,1,1);
    rotateY = new TitleSlider(this, "Ry", 0.0,5.0,-360,360);
    addComponent(rotateY, screenLayout, screenConstraints, 8,1,1,1);
    rotateZ = new TitleSlider(this, "Rz", 0.0,5.0,-360,360);
    addComponent(rotateZ, screenLayout, screenConstraints, 9,1,1,1);

    // Create the message field to display messages to the user.
    msgField = new TextField(70);
    screenConstraints.fill = GridBagConstraints.NONE;
    screenConstraints.weighty = 10;
    msgField.setEditable(false);
    addComponent(msgField, screenLayout, screenConstraints, 10,0,2,1);

    // Create the Delete button.
    Delete = new Button("Delete");
    screenConstraints.fill = GridBagConstraints.NONE;
    screenConstraints.weighty = 10;
    addComponent(Delete, screenLayout, screenConstraints, 10,2,1,1);

    // Create the Clear Button
    Clear = new Button("Clear All");
    screenConstraints.fill = GridBagConstraints.NONE;
    screenConstraints.weighty = 10;
    addComponent(Clear, screenLayout, screenConstraints, 10,3,1,1);

    // Set the title for the text field  for the CSG string.
    textString = new Label("Enter operation string:", Label.LEFT);
    textString.setForeground(Color.red);
    textString.setFont(f);
    screenConstraints.fill = GridBagConstraints.HORIZONTAL;
    addComponent(textString, screenLayout, screenConstraints, 11,0,3,1);

    // Create the text field where the user can enter the CSG string.
    csgField = new TextField(20);
    msgField.setEditable(false);
    addComponent(csgField, screenLayout, screenConstraints, 12,0,3,1);
    validate();
  }

  private void addComponent(Component c, GridBagLayout g,
			  GridBagConstraints gc, int row, int column, int width, int height)
  {
    // set gridx and gridy
    gc.gridx = column;
    gc.gridy = row;

    // set grid width and grid height
    gc.gridwidth = width;
    gc.gridheight = height;

    // set contraints
    g.setConstraints(c, gc);
    // add component to applet
    add(c);
  }

  // Function to handle the different events like mouse clicks.
public boolean action(Event e, Object arg)
  {
    Object   target = e.target;
    Graphics g;
    int      number=0, flag=0;
    int      errCode, i;
    Float    ftemp, itemp;

    if(e.target instanceof Button)
    {
      if(e.target == Delete && parent.selectedObject == -1)
      {
	msgField.setText("Please select the object to be deleted.");
      }
      else if(e.target == Delete && parent.selectedObject > -1)
      {
	msgField.setText("Deleted " + shapeList.getSelectedItem() + ".");
	parent.Delete();
	shapeList.delItem(parent.selectedObject);

	translateX.textField.setText("0");
	translateY.textField.setText("0");
	translateZ.textField.setText("0");
	scaleX.textField.setText("1");
	scaleY.textField.setText("1");
	scaleZ.textField.setText("1");
	rotateX.textField.setText("0");
	rotateY.textField.setText("0");
	rotateZ.textField.setText("0");

	parent.selectedObject = -1;
      }
      else if (e.target == Reset)
      {
	if (parent.selectedObject == -1)
	{
	  translateX.textField.setText("0");
	  translateY.textField.setText("0");
	  translateZ.textField.setText("0");
	  scaleX.textField.setText("1");
	  scaleY.textField.setText("1");
	  scaleZ.textField.setText("1");
	  rotateX.textField.setText("0");
	  rotateY.textField.setText("0");
	  rotateZ.textField.setText("0");
	}
	parent.canvas.mouseDrag = 0;
	parent.canvas.drawAxes();
	parent.canvas.repaint();
      }
      else if (e.target == Clear)
      {
	parent.selectedObject = -1;
	parent.sphereCount = 0;
	parent.coneCount = 0;
	parent.cylinderCount = 0;
	parent.cubeCount = 0;
	parent.objCount = 0;
	parent.canvas.oCount = 0;

	for (i=0; i<parent.objectCount; i++)
	  shapeList.delItem(0);

	translateX.textField.setText("0");
	translateY.textField.setText("0");
	translateZ.textField.setText("0");
	scaleX.textField.setText("1");
	scaleY.textField.setText("1");
	scaleZ.textField.setText("1");
	rotateX.textField.setText("0");
	rotateY.textField.setText("0");
	rotateZ.textField.setText("0");
	csgField.setText(" ");
	parent.objectCount = 0;
	parent.canvas.repaint();
      }
      return true;
    }
    else if (e.target instanceof List)
    {
      if (e.arg.equals("sphere"))
      {
	if (parent.objectCount > 14)
	{
	  msgField.setText("Only allowed to insert 15 objects for CSG operations.");
	  return false;
	}
	number = 1;
	msgField.setText("Drawing specified object.....Please wait.");
	parent.sphereCount++;
	errCode = parent.csgUIdraw(number);
	if (errCode == -1)
	{
	  msgField.setText("Specified object is null.");
	  parent.sphereCount--;
	}
	else
	{
	  shapeList.addItem("Sphere S" + parent.sphereCount);
	  parent.selectedObject = -1;
	  // Always clear the text fields of the different transformations.
	  itemp = new Float(parent.Objects[parent.objectCount-1].tx);
	  translateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ty);
	  translateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].tz);
	  translateZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sx);
	  scaleX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sy);
	  scaleY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sz);
	  scaleZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rx);
	  rotateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ry);
	  rotateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rz);
	  rotateZ.textField.setText(itemp.toString());
	  msgField.setText("Created a new sphere named S" + parent.sphereCount + ".");
	}
      }
      else if (e.arg.equals("cube"))
      {
	if (parent.objectCount > 14)
	{
	  msgField.setText("Only allowed to insert 15 objects for CSG operations.");
	  return false;
	}
	number = 2;
	msgField.setText("Drawing specified object.....Please wait.");
	parent.cubeCount++;
	errCode = parent.csgUIdraw(number);
	if (errCode == -1)
	{
	  msgField.setText("Specified object is null.");
	  parent.cubeCount--;
	}
	else
	{
	  shapeList.addItem("Cube C" + parent.cubeCount);
	  parent.selectedObject = -1;
	  // Always clear the text fields of the different transformations.
	  itemp = new Float(parent.Objects[parent.objectCount-1].tx);
	  translateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ty);
	  translateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].tz);
	  translateZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sx);
	  scaleX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sy);
	  scaleY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sz);
	  scaleZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rx);
	  rotateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ry);
	  rotateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rz);
	  rotateZ.textField.setText(itemp.toString());
	  msgField.setText("Created a new Cube named C" + parent.cubeCount + ".");
	}
      }
      else if (e.arg.equals("cylinder"))
      {
	if (parent.objectCount > 14)
	{
	  msgField.setText("Only allowed to insert 15 objects for CSG operations.");
	  return false;
	}
	number = 3;
	msgField.setText("Drawing specified object.....Please wait.");
	parent.cylinderCount++;
	errCode = parent.csgUIdraw(number);
	if (errCode == -1)
	{
	  msgField.setText("Specified object is null.");
	  parent.cylinderCount--;
	}
	else
	{
	  shapeList.addItem("Cylinder L" + parent.cylinderCount);
	  parent.selectedObject = -1;
	  // Always clear the text fields of the different transformations.
	  itemp = new Float(parent.Objects[parent.objectCount-1].tx);
	  translateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ty);
	  translateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].tz);
	  translateZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sx);
	  scaleX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sy);
	  scaleY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sz);
	  scaleZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rx);
	  rotateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ry);
	  rotateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rz);
	  rotateZ.textField.setText(itemp.toString());
	  msgField.setText("Created a new cylinder named L" + parent.cylinderCount + ".");
	}
      }
      else if (e.arg.equals("cone"))
      {
	if (parent.objectCount > 14)
	{
	  msgField.setText("Only allowed to insert 15 objects for CSG operations.");
	  return false;
	}
	number = 4;
	msgField.setText("Drawing specified object.....Please wait.");
	parent.coneCount++;
	errCode = parent.csgUIdraw(number);
	if (errCode == -1)
	{
	  msgField.setText("Specified object is null.");
	  parent.coneCount--;
	}
	else
	{
	  shapeList.addItem("Cone N" + parent.coneCount);
	  parent.selectedObject = -1;
	  // Always clear the text fields of the different transformations.
	  itemp = new Float(parent.Objects[parent.objectCount-1].tx);
	  translateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ty);
	  translateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].tz);
	  translateZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sx);
	  scaleX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sy);
	  scaleY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].sz);
	  scaleZ.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rx);
	  rotateX.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].ry);
	  rotateY.textField.setText(itemp.toString());
	  itemp = new Float(parent.Objects[parent.objectCount-1].rz);
	  rotateZ.textField.setText(itemp.toString());
	  msgField.setText("Created a new cone named N" + parent.coneCount + ".");
	}
      }
      else
      {
	parent.selectedObject = shapeList.getSelectedIndex();
	msgField.setText("Selected " + shapeList.getSelectedItem() + ".");

	// To show the previous transformation values for an object.
	ftemp = new Float(parent.Objects[parent.selectedObject].tx);
	translateX.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].ty);
	translateY.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].tz);
	translateZ.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].sx);
	scaleX.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].sy);
	scaleY.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].sz);
	scaleZ.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].rx);
	rotateX.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].ry);
	rotateY.textField.setText(ftemp.toString());
	ftemp = new Float(parent.Objects[parent.selectedObject].rz);
	rotateZ.textField.setText(ftemp.toString());
	parent.paintCanvas();
      }
      return true;
    }
    else if(e.target == translateX.textField || e.target == translateY.textField ||
	    e.target == translateZ.textField || e.target == scaleX.textField  ||
	    e.target == scaleY.textField     || e.target == scaleZ.textField
	    || e.target == rotateX.textField || e.target == rotateY.textField
	    || e.target == rotateZ.textField)
    {
      if(parent.selectedObject == -1)
	msgField.setText("Please select a object to be transformed.");
      else
      {
	msgField.setText("Transformed " + shapeList.getSelectedItem() + ".");
	parent.UpdateTransformValues((float)scaleX.getValue(), (float)scaleY.getValue(),
				     (float)scaleZ.getValue(), (float)rotateX.getValue(),
				     (float)rotateY.getValue(), (float)rotateZ.getValue(),
				     (float)translateX.getValue(), (float)translateY.getValue(),
				     (float)translateZ.getValue());
      }
      return true;
    }
    else if(e.target == csgField)
    {
      if (parent.objectCount > 14)
      {
	msgField.setText("Only allowed to insert 15 objects for CSG operations.");
	return false;
      }

      String str = csgField.getText();

      str = str.trim();
      if (str.length() == 0)
      {
      msgField.setText("Please enter a string.");
      return false;
      }

      msgField.setText("Drawing specified object.....Please wait.");
      parent.objCount++;
      errCode = parent.BuildString(str);
      if (errCode == -1)
      {
	msgField.setText("Specified object is null.");
	parent.objCount--;
      }
      else if (errCode == -2)
      {
	msgField.setText("Invalid string entred. Please try again.");
	parent.objCount--;
      }
      else if (errCode == -3)
      {
	msgField.setText("Invalid object in string. Please re-enter.");
	parent.objCount--;
      }
      else
      {
	shapeList.addItem("Object O" + parent.objCount);
	parent.selectedObject = -1;
	// Always clear the text fields of the different transformations.
	itemp = new Float(parent.Objects[parent.objectCount-1].tx);
	translateX.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].ty);
	translateY.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].tz);
	translateZ.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].sx);
	scaleX.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].sy);
	scaleY.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].sz);
	scaleZ.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].rx);
	rotateX.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].ry);
	rotateY.textField.setText(itemp.toString());
	itemp = new Float(parent.Objects[parent.objectCount-1].rz);
	rotateZ.textField.setText(itemp.toString());
	msgField.setText("Created a new object named O" + parent.objCount + ".");
      }
    }
    return false;
  }

  void updateRotates(int msgFlag, float rotx, float roty, float rotz)
  {
    Float itemp;

    if (msgFlag == -1)
      msgField.setText("Please select the object to be rotated.");
    else
    {
      itemp = new Float(parent.Objects[parent.selectedObject].rx+rotx);
      rotateX.textField.setText(itemp.toString());
      itemp = new Float(parent.Objects[parent.selectedObject].ry+roty);
      rotateY.textField.setText(itemp.toString());
      itemp = new Float(parent.Objects[parent.selectedObject].rz+rotz);
      rotateZ.textField.setText(itemp.toString());
      msgField.setText("Rotated object " + parent.Objects[parent.selectedObject].name);
    }
  }

}
