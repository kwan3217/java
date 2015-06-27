package org.spaceroots.rkcheck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/** This class is the base class for application-specific XML files
 * reading/writing.

 * <p>In order to read/write application-specific XML files, a
 * concrete class extending this abstract base class must be created
 * and the four {@link #analyze analyze}, {@link #build build}, {@link
 * #getPublicId getPublicId} and {@link #getSystemId getSystemId}
 * methods must be implemented.</p>

 * @version $Id: XMLFile.java,v 1.2 2004/05/23 20:14:04 luc Exp $
 * @author Luc Maisonobe
 */
public abstract class XMLFile
  implements ErrorHandler {

  /** Simple constructor.
   * @exception RKCheckException if no parser can be built
   */
  public XMLFile()
    throws RKCheckException {
    try {
      factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);   
      resolver = new EmbeddedCatalogResolver("/org/spaceroots/resources/catalog",
                                             factory.newDocumentBuilder());
    } catch (ParserConfigurationException pce) {
      throw new RKCheckException(pce);
    }

  }

  /** Parse an XML document containing an application-specific configuration.
   * @param uri URI of the project document to read
   * @exception RKCheckException if an error occurs during parsing (mainly
   * because there is an error in the document)
   * @exception IOException if there is low level read error
   * @exception FileNotFoundException if there is low level file error
   */
  public void read(String uri)
    throws RKCheckException, IOException, FileNotFoundException {
    try {

      // build the DOM tree from the file
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setEntityResolver(resolver);
      builder.setErrorHandler(this);
      Document document = builder.parse(uri);

      // analyze the DOM tree
      analyze(document, uri);

    } catch (NumberFormatException nfe) {
      throw new RKCheckException(nfe);
    } catch (ParserConfigurationException pce) {
      throw new RKCheckException(pce);
    } catch (SAXException se) {
      if ((se.getException() != null)
          && (se.getException() instanceof RKCheckException)) {
        throw (RKCheckException) se.getException();
      } else {
        throw new RKCheckException(se);
      }
    }

  }

  /** Write an XML document.
   * @param writer writer to use for producing the document
   * @param uri URI of the document
   * @exception RKCheckException if an error occurs during write
   * @exception IOException if there is low level read error
   */
  public void write(Writer writer, String uri)
    throws RKCheckException, IOException {
    try {

      // build the DOM tree
      Document document = factory.newDocumentBuilder().newDocument();
      build(document, uri);
      indent(document, document.getDocumentElement(), "", "  ");
      document.getDocumentElement().normalize();

      // write down the tree into the file
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer();
      t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, getPublicId());
      t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, getSystemId());
      t.transform(new DOMSource(document.getDocumentElement()),
                  new StreamResult(writer));
      writer.write(System.getProperty("line.separator"));

    } catch (ParserConfigurationException pce) {
      throw new RKCheckException(pce);
    } catch (TransformerException te) {
      throw new RKCheckException(te);
    }

  }

  /** Indent an element sub-elements.
   * <p>If an element doesn't have any sub-elements, its content is unchanged.</p>
   * @param document complete DOM tree beeing built
   * @param element element to indent
   * @param base base indentation (it has already been inserted before the node start)
   * @param increment increment indentation added at each level
   */
  private void indent(Document document, Element element,
                      String base, String increment) {

    String  newBase  = base + increment;
    boolean indented = false;

    for (Node node = element.getFirstChild();
         node != null;
         node = node.getNextSibling()) {
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        indented = true;
        element.insertBefore(document.createTextNode("\n" + newBase), node);
        indent(document, (Element) node, newBase, increment);
      }
    }

    if (indented) {
      element.appendChild(document.createTextNode("\n" + base));
    }

  }

  /** Get a specific child element node.
   * @param parent parent node
   * @param name name of the element
   * @param missingOK if true no exception is generated if the element
   * is not found
   * @return element node with the given name (null if not found and
   * <code>missingOK</code> is true)
   * @exception RKCheckException if the element cannot be found and
   * <code>missingOK</code> is false
   */
  protected Element getElement(Node parent, String name, boolean missingOK)
    throws RKCheckException {

    for (Node node = parent.getFirstChild();
         node != null;
         node = node.getNextSibling()) {
      if ((node.getNodeType() == Node.ELEMENT_NODE)
          && node.getNodeName().equals(name)) {
        return (Element) node;
      }
    }

    if (missingOK) {
      return null;
    } else {
      throw new RKCheckException("element {0} cannot be found in file {1}",
                                 new String[] { name, uri });
    }

  }

  /** Get an attribute value.
   * @param element element containing the attribute
   * @param name attribute name
   * @param missingOK if true no exception is generated if the attribute
   * is not found
   * @return attribute value (null if not found and
   * <code>missingOK</code> is true)
   * @exception RKCheckException if the attribute cannot be found and
   * <code>missingOK</code> is false
   */
  protected String getAttribute(Node element, String name, boolean missingOK)
    throws RKCheckException {
    Attr attribute = ((Element) element).getAttributeNode(name);
    if ((attribute == null) && ! missingOK) {
      throw new RKCheckException("missing {0} attribute in {1}",
                                 new String[] { name, uri });
    }
    return (attribute == null) ? null : attribute.getValue();
  }

  /** Get the content of a node.
   * @param node node to analyze
   * @return node content
   * @exception RKCheckException if the node contains unexpected elements
   */
  protected String getContent(Node node)
    throws RKCheckException {
    StringBuffer buffer = new StringBuffer();
    for (Node child = node.getFirstChild();
         child != null;
         child = child.getNextSibling()) {
      switch(child.getNodeType()) {
      case Node.TEXT_NODE :
        buffer.append(child.getNodeValue());
        break;
      case Node.ENTITY_REFERENCE_NODE :
        buffer.append(getContent(child));
        break;
      case Node.CDATA_SECTION_NODE :
        buffer.append(child.getNodeValue());
        break;
      default:
        throw new RKCheckException("unexpected content in element {0} of file {1}",
                                new String[] { node.getNodeName(), uri });
      }
    }
    return buffer.toString();
  }

  /** Receive notification of a recoverable error.
   * @param exception the recoverable error
   * @exception SAXException the error is directly thrown as an exception
   */
  public void error(SAXParseException exception)
    throws SAXParseException {
    throw exception;
  }

  /** Receive notification of a non-recoverable error.
   * @param exception the non-recoverable error
   * @exception SAXException the error is directly thrown as an exception
   */
  public void fatalError(SAXParseException exception)
    throws SAXParseException {
    throw exception;
  }

  /** Receive notification of a warning.
   * @param exception the warning
   * @exception SAXException the error is directly thrown as an exception
   */
  public void warning(SAXParseException exception)
    throws SAXParseException {
    throw exception;
  }

  /** Analyze the DOM tree.
   * <p>This method is called by the {@link #read read} method after
   * having build the DOM tree. Its purpose is to analyze the tree and
   * store the contained information in application-specific ways.</p>
   * @param document DOM tree to analyze
   * @param uri URI from which the project document was read (useful
   * mainly for error messages)
   * @exception RKCheckException if some data cannot be found in the tree
   * @exception NumberFormatException if some number cannot be parsed
   */
  public abstract void analyze(Document document, String uri)
    throws RKCheckException, NumberFormatException;

  /** Build the DOM tree.
   * <p>This method is called by the {@link #write write} method
   * before writing the DOM tree. Its purpose is to build the DOM tree
   * in application-specific ways.</p>
   * @param document empty DOM tree to build
   * @param uri URI where the project document will be written (useful
   * mainly for error messages)
   * @exception RKCheckException if some problem occurs while building
   * the tree
   */
  public abstract void build(Document document, String uri)
    throws RKCheckException;

  /** Get the public ID of the DTD.
   * @return public ID of the DTD
   */
  protected abstract String getPublicId();

  /** Get the system ID of the DTD.
   * @return system ID of the DTD
   */
  protected abstract String getSystemId();

  /** URI of the configuration file. */
  protected String uri;

  /** Documents builder. */
  private DocumentBuilderFactory factory;

  /** Entity resolver and public/system id mapper. */
  private EmbeddedCatalogResolver resolver;

}
