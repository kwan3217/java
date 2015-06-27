package org.kwansystems.space.gear;

import static java.lang.Math.*;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;

import com.sun.j3d.utils.geometry.*;

import javax.swing.Timer;

import org.kwansystems.space.gear.*;
import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

public class SpaceGear extends Applet implements ActionListener, TestFlightListener {
  private static final long serialVersionUID = 3360855728242130606L;
  private Button go = new Button("Go");
  private TransformGroup body;
  private Transform3D trans = new Transform3D();
  private Timer timer;
  public TransformGroup createVector(Point3d vhead, float rshaft, float rneck, Color3f color) {
    return createVector(new Point3d(0,0,0),vhead,rshaft,rneck,color);
  }
  public TransformGroup createVector(Point3d vtail, Point3d vhead, float rshaft, float rneck, Color3f color) {
    Material M=new Material();
    M.setDiffuseColor(color);
//    M.setAmbientColor(color);
    Appearance A=new Appearance();
    A.setMaterial(M);
    TransformGroup result=new TransformGroup();
    TransformGroup tgShaft=new TransformGroup();
    TransformGroup tgHead=new TransformGroup();
    double xtail=0.0;
    Vector3d l=new Vector3d();
    l.sub(vtail,vhead);
    double xhead=l.length();
    double xneck=xhead-2*rneck;
    Primitive head=new Cone(1.0f,1.0f);
    head.setAppearance(A);
    Primitive shaft=new Cylinder(1.0f,1.0f);
    shaft.setAppearance(A);
    Transform3D tShaft = new Transform3D();
    Transform3D tHead = new Transform3D();
    tShaft.setScale(new Vector3d(rshaft,abs(xtail-xneck),rshaft));
    tShaft.setTranslation(new Vector3d(0,xtail+abs(xtail-xneck)/2.0,0));
    tgShaft.setTransform(tShaft);
    tgShaft.addChild(shaft);
    tHead.setScale(new Vector3d(rneck,abs(xhead-xneck),rneck));
    tHead.setTranslation(new Vector3d(0,xneck+abs(xhead-xneck)/2.0,0));
    tgHead.setTransform(tHead);
    tgHead.addChild(head);
    Transform3D tVector=new Transform3D();
    Vector3d vcross=new Vector3d(1.0,1.0,1.0);
    tVector.lookAt(vtail, vhead, vcross);
    
//    result.addChild(head);
    result.addChild(tgShaft);
    result.addChild(tgHead);
    result.setTransform(tVector);
    return result;
  }
  public TransformGroup createBox(double x, double y, double z) {
    // Create a scaled box branch
    TransformGroup objScale=new TransformGroup();
    Transform3D scale1 = new Transform3D();
    scale1.setScale(new Vector3d(x,y,z));
    objScale.setTransform(scale1);
    Primitive obj = new Box();
    objScale.addChild(obj);
    return objScale;
  }
  public BranchGroup createSceneGraph() {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    body = new TransformGroup();
    body.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    Transform3D pos1 = new Transform3D();
    body.setTransform(pos1);
    body.addChild(createBox(0.01,0.04,0.09));
    body.addChild(createVector(new Point3d(0.1f,0.0f,0.0f),0.005f,0.010f,new Color3f(1f,0f,0f)));
    body.addChild(createVector(new Point3d(0.0f,0.1f,0.0f),0.005f,0.010f,new Color3f(0f,1f,0f)));
    body.addChild(createVector(new Point3d(0.0f,0.0f,0.1f),0.005f,0.010f,new Color3f(0f,0f,1f)));
    objRoot.addChild(body);
    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
    Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
    Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
    DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
    light1.setInfluencingBounds(bounds);
    objRoot.addChild(light1);

    // Set up the ambient light
    Color3f ambientColor = new Color3f(1.0f, 1.0f, 1.0f);
    AmbientLight ambientLightNode = new AmbientLight(ambientColor);
    ambientLightNode.setInfluencingBounds(bounds);
    objRoot.addChild(ambientLightNode);
    return objRoot;
  }

  public SpaceGear() {
    setLayout(new BorderLayout());
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    Canvas3D c = new Canvas3D(config);
    add("Center", c);

    timer = new Timer(1000/60,this);

    //timer.start();

    Panel p =new Panel();

    p.add(go);
    add("North",p);
    go.addActionListener(this);

   // Create a simple scene and attach it to the virtual universe
    BranchGroup scene = createSceneGraph();
    SimpleUniverse u = new SimpleUniverse(c);
    u.getViewingPlatform().setNominalViewingTransform();
    u.addBranchGraph(scene);
  }
  private MathMatrix Rot=MathMatrix.Identity(3);
  public void actionPerformed(ActionEvent e ) {
    // start timer when button is pressed
    if (e.getSource()==go){
      if (!timer.isRunning()) {
         t0=System.currentTimeMillis();
         timer.start();
      }
    } else {
      A.StepTest();
    }
  }
  public long t0;
  public long frames=0;
  public void StepEventHandler(double T, MathVector X) {
    SixDOFState RVEw=new SixDOFState(X);
    Quaternion E=RVEw.E();
    
    Rot=new MathMatrix(E);
    trans.set(Rot.getJava3D());
    body.setTransform(trans);
    frames++;
    long t1=System.currentTimeMillis();
    if(frames==100) {
      System.out.printf("%d frames in %d ms, %f frames/sec\n",frames,t1-t0,(((double)(frames))/((double)(t1-t0)/1000.0)));
      frames=0;
      t0=t1;
    }
  }
  public static TestFlight A;
  public static void main(String[] args) throws IOException {
    System.out.println("Program Started");
    SpaceGear bb = new SpaceGear();
    new MainFrame(bb, 256, 256);
    SixDOFState RVEw0=new SixDOFState(new MathVector(), new MathVector(), Quaternion.U, new MathVector(0,0,0));
    Universe U=new EmptyUniverse();
    A=new TestFlight(new TestJoystickRCS(U));
    A.listener=bb;
    /*
    TestFlight A=new TestFlight(Tumbler) {
      public void RecordAdditionalTestData(double T, MathVector X) {
        SixDOFState RVEw=new SixDOFState(X);
        MathVector w=RVEw.w();
        CR.Record(T, "w.x", "deg/s",toDegrees(w.X()));
        CR.Record(T, "w.y", "deg/s",toDegrees(w.Y()));
        CR.Record(T, "w.z", "deg/s",toDegrees(w.Z()));
      }
    };
//    A.DoTest(RVEw0, 0, 50,50*100);
//    A.CR.PrintTable(new HTMLPrinter("Tumbler.html"));
//    A.CR.PrintSubTable(new String[]{"w.x","w.y","w.z"}, new DisplayPrinter());
 * */
    A.StartTest(RVEw0, 0, 1.0/49.0);
  }
}

