package csg;

import java.awt.*;
import java.util.*;
import java.applet.Applet;

class TitleSlider extends Panel
{
  String	      title, str;
  TextField	      textField;
  public Label	      label;

  // The two buttons for increment and decrement
  Button			plus;
  Button			minus;

  // The limits for the Titleslider values
  double			max;
  double			min;

  // The increment that we need to do for the TitleSlider.
  double			increment;

  // The parent class
  csgUI			parent;

  // Constructor
  TitleSlider(csgUI mytestSlider, String myTitle, double myValue,
	      double change, double mymin, double mymax)
  {
    super();

    GridBagConstraints c = new GridBagConstraints();
    GridBagLayout gridbag = new GridBagLayout();
    setLayout(gridbag);

    parent = mytestSlider;
    title = myTitle;
    increment = change ;
    min = mymin;
    max = mymax;

    //Add the label
    label = new Label(title, Label.RIGHT);
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(label, c);
    add(label);

    // Add the MINUS Button
    minus = new Button(" < ");
    c.gridx = 2;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(minus, c);
    add(minus);

    //Add the text field
    textField = new TextField("0", 10);
    textField.setText(String.valueOf(myValue));

    c.gridx = 3;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(textField, c);
    add(textField);

    // Add the PLUS Button
    plus = new Button(" > ");
    c.gridx = 4;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(plus, c);
    add(plus);
  }

  /*
   * Puts a little breathing space between the panel and its contents, which
   * lets us draw a box in the paint() method. We add more pixels to the right,
   * to work around a Choice bug.
   */
public Insets insets()
  {
    return new Insets(2,0,0,0);
  }

  /*
   * Gets the current value in the text field. That's guaranteed to be the same as the value
   * in the scroller (subject to rounding, of course).
   */

public double getValue()
  {
    double f;

    try
    {
      f = Double.valueOf(textField.getText()).doubleValue();
    }
    catch (java.lang.NumberFormatException e)
    {
      f = 0.0;
    }
    return f;
  }

  // Get the title of the corresponding slider...
public String getLabelText()
  {
    return label.getText() ;
  }

  // Respond to user actions on controls.
public boolean action(Event e, Object arg)
  {
    if (e.target instanceof Button)
    {
      if(e.target == minus)
      {
	decrTextField();
      }
      else if (e.target == plus)
      {
	incrTextField();
      }

      str = getLabelText();

      if (str.equals("Tx") || str.equals("Ty") || str.equals("Tz") ||
	  str.equals("Sx") || str.equals("Sy") || str.equals("Sz") ||
	  str.equals("Rx") || str.equals("Ry") || str.equals("Rz"))
      {
	if(parent.parent.selectedObject == -1)
	  parent.msgField.setText("Please select a object to be transformed.");
	else
	{
	  parent.msgField.setText("Transformed " + parent.shapeList.getSelectedItem() + ".");
	  parent.parent.UpdateTransformValues((float)parent.scaleX.getValue(),
					      (float)parent.scaleY.getValue(),
					      (float)parent.scaleZ.getValue(),
					      (float)parent.rotateX.getValue(),
					      (float)parent.rotateY.getValue(),
					      (float)parent.rotateZ.getValue(),
					      (float)parent.translateX.getValue(),
					      (float)parent.translateY.getValue(),
					      (float)parent.translateZ.getValue());
	}
      }
      return true;
    }
    return false;
  }

  // when "plus" Button is pressed
public void incrTextField()
  {
    double localval;

    localval = getValue() + increment;

    if (localval >= max)
    {
      localval = max;
    }
    else if (localval <= min)
    {
      localval = min;
    }
    textField.setText(String.valueOf(localval));
  }

  // When "minus" Button is pressed
public void decrTextField()
  {
    double localval;

    localval = getValue() - increment;

    if (localval >= max)
    {
      localval = max;
    }
    else if (localval <= min)
    {
      localval = min;
    }
    textField.setText(String.valueOf(localval));
  }

}
