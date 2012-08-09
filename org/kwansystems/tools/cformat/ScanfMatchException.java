package org.kwansystems.tools.cformat;

import java.io.IOException;

/** 
  * Exception class used by the <code>scan</code> methods within
  * ScanfReader when the input does not match the specified format.
  * 
  * @author John E. Lloyd, Fall 2000
  * @see ScanfReader
  */
public class ScanfMatchException extends IOException
{
	/** 
	  * Creates a new ScanfMatchException with the given message. 
	  * 
	  * @param msg Error message
	  * @see ScanfReader
	  */
	public ScanfMatchException (String msg)
	 { super (msg);
	 }
}
