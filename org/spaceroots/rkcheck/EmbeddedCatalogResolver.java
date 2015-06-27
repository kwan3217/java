package org.spaceroots.rkcheck;

import java.util.HashMap;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder; 
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class implements a resolver for external entities that are
 * cached locally in the same jar file as the application.
 * @version $Id: EmbeddedCatalogResolver.java,v 1.1 2004/05/23 13:30:17 luc Exp $
 * @author L. Maisonobe
 */
public class EmbeddedCatalogResolver
  implements EntityResolver {

  /** Simple constructor.
   * Build an empty resolver.
   * @param catalog catalog path (inside the jar)
   * @param builder document builder (as a side effect, the builder
   * will use the instance as its entity resolver)
   */
  public EmbeddedCatalogResolver(String catalog, DocumentBuilder builder) {

    int index = catalog.lastIndexOf('/');
    if (index < 0) {
      base = "";
      catalogName = catalog;
    } else {
      base = catalog.substring(0, index + 1);
      catalogName = catalog.substring(index + 1);
    }

    map = new HashMap();
    this.builder = builder;
    builder.setEntityResolver(this);

  }

  /** Get the resource associated with a public identifier
   * @param publicId public identifier
   * @return the resource name or null if the identifier is unknown
   * @exception IOException if parsing the catalog throws one
   * @exception SAXException if parsing the catalog throws one
   */
  public String getResourceName(String publicId)
    throws IOException, SAXException {

    if (publicId.equals("-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN")) {
      // special case, this should be handled BEFORE trying to
      // parse the catalog, because it is needed during this parsing
      // (this is a chicken and egg problem)
      return base + "catalog.dtd";
    }

    if (map.isEmpty()) {
      // read the catalog containing the entities mapping
      parseCatalog();
    }

    // return the mapping we have for this entity, or null
    return (String) map.get(publicId);

  }

  /** Parse the resources catalog.
   * @exception SAXException if a parse error is encountered during
   * catalog analysis
   * @exception IOException if the catalog cannot be read
   */
  private void parseCatalog()
    throws SAXException, IOException {

    // parse the catalog
    InputStream is = getClass().getResourceAsStream(base + catalogName);
    if (is == null) {
      return;
    }
    Document document = builder.parse(is);

    // build the map
    for (Node child = document.getDocumentElement().getFirstChild();
         child != null;
         child = child.getNextSibling()) {
      if ((child.getNodeType() == Node.ELEMENT_NODE)
          && (child.getNodeName().equals("public"))) {
        String publicId = ((Element) child).getAttribute("publicId");
        String uri      = ((Element) child).getAttribute("uri");
        if ((publicId != null) && (uri != null)) {
          // store the publicId/uri mapping
          // (adding the base prefix if uri is relative)
          map.put(publicId, uri.startsWith("/") ? uri : (base + uri));
        }
      }
    }

  }

  public InputSource resolveEntity (String publicId, String systemId)
    throws SAXException {
    try {
      String resourceName = getResourceName(publicId);
      if (resourceName != null) {
        // the resource is cached locally in the jar
        InputStream is = getClass().getResourceAsStream(resourceName);
        return (is != null) ? new InputSource(is) : null;
      } else {
        // we did not find the resource,
        // we let the parser use the default behaviour
        return null;
      }
    } catch (IOException ioe) {
      throw new SAXException(ioe);
    }
  }

  /** Base directory (inside the jar file). */
  String base;

  /** Catalog name (in the base directory). */
  String catalogName;

  /** Document builder. */
  DocumentBuilder builder;

  /** Map for public identifiers. */
  HashMap map;

}
