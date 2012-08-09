package org.spaceroots.rkcheck;

import java.math.BigInteger;
import java.math.BigDecimal;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class reads or write an XML file containing a Runge-Kutta
 * method description.
 * @version $Id: RungeKuttaFile.java,v 1.2 2004/05/23 18:36:27 luc Exp $
 * @author L. Maisonobe
 */
public class RungeKuttaFile
  extends XMLFile {

  /** Simple constructor.
   * <p>Build a file reader/writer for a Runge-Kutta method instance.</p>
   * @param method Runge-Kutta method instance to initialize by file
   * reading or to store by file writing
   * @exception RKCheckException if no parser can be built
   */
  public RungeKuttaFile(RungeKuttaMethod method)
    throws RKCheckException {
    this.method = method;
  }

  /** Analyze the DOM tree.
   * <p>This method is called by the {@link #read read} method after
   * having build the DOM tree. Its purpose is to analyze the tree and
   * store the contained information in application-specific ways.</p>
   * @param document DOM tree to analyze
   * @param uri URI from which the project document was read (useful
   * mainly for error messages)
   * @exception RKCheckException if some data cannot be found in the tree
   * or if there is a dimension mismatch
   * @exception NumberFormatException if some number cannot be parsed
   */
  public void analyze(Document document, String uri)
    throws RKCheckException, NumberFormatException {

    // root element
    Element rkElt = getElement(document, EltRungeKutta, false);
    method.setName(getAttribute(rkElt, AttRungeKuttaName, false));

    // time steps
    QuadraticSurd[] timeSteps = analyzeList(getElement(rkElt, EltTime, false));
    int stages = timeSteps.length;
    method.setTimeSteps(timeSteps);

    // internal weights
    QuadraticSurd[][] internalWeights = new QuadraticSurd[stages][];
    NodeList list = getElement(rkElt, EltInternal, false).getChildNodes();
    int s = 0;
    for (int i = 0; i < list.getLength(); ++i) {
      Node row = list.item(i);
      if ((row.getNodeType() == Node.ELEMENT_NODE)
          && (row.getNodeName().equals(EltRow))) {
        internalWeights[s] = analyzeList(row);
        if (internalWeights[s].length != s) {
          throw new RKCheckException("row {0} has {1} elements, but should have {2} elements",
                                     new String[] {
                                       Integer.toString(s + 1),
                                       Integer.toString(internalWeights[s].length),
                                       Integer.toString(s)
                                     });
        }
        ++s;
      }
    }
    if (s != stages) {
      throw new RKCheckException("internal weights array has {0} rows,"
                                 + " but time steps array has {1} elements",
                                 new String[] {
                                   Integer.toString(s),
                                   Integer.toString(stages)
                                 });
    }
    method.setInternalWeights(internalWeights);

    // estimation weights
    QuadraticSurd[] estimationWeights = analyzeList(getElement(rkElt, EltEstimation, false));
    if (estimationWeights.length != stages) {
      throw new RKCheckException("estimation weights array has {0} rows,"
                                 + " but time steps array has {1} elements",
                                 new String[] {
                                   Integer.toString(estimationWeights.length),
                                   Integer.toString(stages)
                                 });
    }
    method.setEstimationWeights(estimationWeights);

    // error weights
    Element errorElt = getElement(rkElt, EltError, true);
    if (errorElt != null) {
      QuadraticSurd[] errorWeights = analyzeList(errorElt);
      if (errorWeights.length != stages) {
        throw new RKCheckException("error weights array has {0} rows,"
                                   + " but time steps array has {1} elements",
                                   new String[] {
                                     Integer.toString(errorWeights.length),
                                     Integer.toString(stages)
                                   });
      }
      method.setErrorWeights(errorWeights);
    }

  }

  /** Analyze a list of numbers.
   * @param element element containing the list
   * @return an array containing the numbers
   * @exception RKCheckException if a node contains unexpected elements
   */
  private QuadraticSurd[] analyzeList(Node element)
    throws RKCheckException {
    ArrayList numbers = new ArrayList();
    NodeList list = element.getChildNodes();
    for (int i = 0; i < list.getLength(); ++i) {
      Node number = list.item(i);
      if (number.getNodeType() == Node.ELEMENT_NODE) {
        if (number.getNodeName().equals(EltZero)) {

          numbers.add(new QuadraticSurd(0));

        } else if (number.getNodeName().equals(EltOne)) {

          numbers.add(new QuadraticSurd(1));

        } else if (number.getNodeName().equals(EltInteger)) {

          numbers.add(new QuadraticSurd(new BigInteger(getContent(number).trim())));

        } else if (number.getNodeName().equals(EltRational)) {

          BigInteger p = new BigInteger(getContent(getElement(number, EltP, false)).trim());
          BigInteger q = new BigInteger(getContent(getElement(number, EltQ, false)).trim());
          numbers.add(new QuadraticSurd(p, q));

        } else if (number.getNodeName().equals(EltQSurd)) {

          BigInteger p1 = new BigInteger(getContent(getElement(number, EltP1, false)).trim());
          BigInteger p2 = new BigInteger(getContent(getElement(number, EltP2, false)).trim());
          BigInteger d  = new BigInteger(getContent(getElement(number, EltD,  false)).trim());
          BigInteger q  = new BigInteger(getContent(getElement(number, EltQ,  false)).trim());
          numbers.add(new QuadraticSurd(p1, p2, d, q));

        } else if (number.getNodeName().equals(EltReal)) {

          BigDecimal r   = new BigDecimal(getContent(number).trim());
          BigDecimal tol = new BigDecimal(BigInteger.ONE, r.scale());
          numbers.add(new QuadraticSurd(r, tol));

        }
      }
    }

    return (QuadraticSurd[]) numbers.toArray(new QuadraticSurd[numbers.size()]);

  }


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
  public void build(Document document, String uri)
    throws RKCheckException {

    // root element
    Element rkElt = (Element) document.createElement(EltRungeKutta);
    rkElt.setAttribute(AttRungeKuttaName, method.getName());
    document.appendChild(rkElt);

    // time steps
    Element timeElt = buildList(document, EltTime, method.getTimeSteps());
    rkElt.appendChild(timeElt);

    // internal weights
    QuadraticSurd[][] internalWeights = method.getInternalWeights();
    Element internalElt = (Element) document.createElement(EltInternal);
    for (int i = 0; i < internalWeights.length; ++i) {
      internalElt.appendChild(buildList(document, EltRow, internalWeights[i]));
    }
    rkElt.appendChild(internalElt);

    // estimation weights
    Element estimationElt = buildList(document, EltEstimation, method.getEstimationWeights());
    rkElt.appendChild(estimationElt);

    // error weights
    if (method.getErrorWeights() != null) {
      Element errorWeights = buildList(document, EltError, method.getErrorWeights());
      rkElt.appendChild(errorWeights);
    }

  }

  /** Build an element containing a list of numbers.
   * @param document document containing the element to build
   * @param name name of the element
   * @return numbers array containing the numbers
   */
  private Element buildList(Document document, String name, QuadraticSurd[] numbers) {
    Element element = (Element) document.createElement(name);
    for (int i = 0; i < numbers.length; ++i) {
      QuadraticSurd number = numbers[i];
      if (number.isZero()) {

        element.appendChild(document.createElement(EltZero));

      } else if (number.isOne()) {

        element.appendChild(document.createElement(EltOne));

      } else if (number.isInteger()) {

        BigInteger n = number.getRationalNumerator();
        Element integerElt = (Element) document.createElement(EltInteger);
        integerElt.appendChild(document.createTextNode(n.toString()));
        element.appendChild(integerElt);

      } else if (number.isRational()) {

        Element rationalElt = (Element) document.createElement(EltRational);
        Element pElt = (Element) document.createElement(EltP);
        pElt.appendChild(document.createTextNode(number.getRationalNumerator().toString()));
        Element qElt = (Element) document.createElement(EltQ);
        qElt.appendChild(document.createTextNode(number.getDenominator().toString()));
        rationalElt.appendChild(pElt);
        rationalElt.appendChild(qElt);
        element.appendChild(rationalElt);

      } else {

        Element qSurdElt = (Element) document.createElement(EltQSurd);
        Element p1Elt = (Element) document.createElement(EltP1);
        p1Elt.appendChild(document.createTextNode(number.getRationalNumerator().toString()));
        Element p2Elt = (Element) document.createElement(EltP2);
        p2Elt.appendChild(document.createTextNode(number.getRootCoefficient().toString()));
        Element dElt = (Element) document.createElement(EltD);
        dElt.appendChild(document.createTextNode(number.getRootElement().toString()));
        Element qElt = (Element) document.createElement(EltQ);
        qElt.appendChild(document.createTextNode(number.getDenominator().toString()));
        qSurdElt.appendChild(p1Elt);
        qSurdElt.appendChild(p2Elt);
        qSurdElt.appendChild(dElt);
        qSurdElt.appendChild(qElt);
        element.appendChild(qSurdElt);

      }
    }
    return element;
  }
    
  /** Get the public ID of the DTD.
   * @return public ID of the DTD
   */
  protected String getPublicId() {
    return "-//spaceroots.org//DTD Runge-Kutta V1.2//EN";
  }

  /** Get the system ID of the DTD.
   * @return system ID of the DTD
   */
  protected String getSystemId() {
    return "http://www.spaceroots.org/Runge-Kutta.dtd";
  }

  /** Elements names. */
  private static String EltRungeKutta  = "Runge-Kutta";
  private static String EltTime        = "time-steps";
  private static String EltInternal    = "internal-weights";
  private static String EltEstimation  = "estimation-weights";
  private static String EltError       = "error-weights";
  private static String EltRow         = "row";
  private static String EltZero        = "zero";
  private static String EltOne         = "one";
  private static String EltInteger     = "integer";
  private static String EltRational    = "rational";
  private static String EltQSurd       = "quadratic-surd";
  private static String EltReal        = "real";
  private static String EltP           = "p";
  private static String EltQ           = "q";
  private static String EltP1          = "p1";
  private static String EltP2          = "p2";
  private static String EltD           = "d";

  /** Attributes names. */
  private static String AttRungeKuttaName = "name";

  /** Runge-Kutta method to read/write */
  RungeKuttaMethod method;

}
