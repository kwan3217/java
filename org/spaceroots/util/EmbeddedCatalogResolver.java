package org.spaceroots.util;

import java.util.HashMap;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * This class implements a resolver for external entities that are
 * cached locally in the same jar file as the application.
 * @version $Id: EmbeddedCatalogResolver.java,v 1.1 2002/08/13 16:54:17 luc Exp $
 * @author L. Maisonobe
 */
public class EmbeddedCatalogResolver
  extends DefaultHandler {

  /** Simple constructor.
   * Build an empty resolver.
   * @param catalog catalog path (inside the jar)
   * @param factory parser factory to use for building the catalog parser
   */
  public EmbeddedCatalogResolver(String catalog, SAXParserFactory factory) {

    int index = catalog.lastIndexOf('/');
    if (index < 0) {
      base = "";
      catalogName = catalog;
    } else {
      base = catalog.substring(0, index + 1);
      catalogName = catalog.substring(index + 1);
    }

    this.factory = factory;

    map = new HashMap();

  }

  /** Get the resource associated with a public identifier
   * @param id public identifier
   * @return the resource name or null if the identifier is unknown
   * @exception SAXException if parsing the catalog throws one
   */
  private String getResourceName(String id)
    throws SAXException {

    try {

      if (id.equals("-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN")) {
        // special case, this should be handled BEFORE trying to
        // parse the catalog, because it is needed during this parsing
        // (this is a chicken and egg problem)
        return base + "catalog.dtd";
      }

      if (map.isEmpty()) {
        // read the catalog containing the entities mapping
        InputStream is = getClass().getResourceAsStream(base + catalogName);
        if (is == null) {
          return null;
        }
        factory.newSAXParser().parse(is, this);
      }

      // return the mapping we have for this entity, or null
      return (String) map.get(id);

    } catch (ParserConfigurationException pce) {
      throw new SAXException(pce.getMessage());
    } catch (IOException ioe) {
      throw new SAXException(ioe.getMessage());
    }

  }

  public InputSource resolveEntity (String publicId, String systemId)
    throws SAXException {

    String dtdResource = getResourceName(publicId);

    if (dtdResource != null) {
      // the DTD is cached locally in the jar
      InputStream is = getClass().getResourceAsStream(dtdResource);
      return (is != null) ? new InputSource(is) : null;
    } else {
      // we did not find the DTD, we let the parser use the default behaviour
      return null;
    }

  }

  public void startElement(String namespaceURI,
                           String localName, String qName,
                           Attributes attrs)
    throws SAXException {

    String eltName = localName.equals("") ? qName : localName;

    if (eltName.equals("public")) {
      // the current element is a public entry of the catalog

      String publicId = attrs.getValue("publicId");
      String uri = attrs.getValue("uri");
      if ((publicId != null) && (uri != null)) {

        // store the publicId/uri mapping
        // (adding the base prefix if uri is relative)
        map.put(publicId, uri.startsWith("/") ? uri : (base + uri));

      }
    }

  }

  /** Base directory (inside the jar file). */
  String base;

  /** Catalog name (in the base directory). */
  String catalogName;

  /** Parser factory to use. */
  SAXParserFactory factory;

  /** Map for public identifiers. */
  HashMap map;

}
