package org.kwansystems.pov;

import static java.lang.Math.abs;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;

import org.kwansystems.space.gear.*;
import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

import com.sun.j3d.loaders.*;
import com.sun.j3d.loaders.objectfile.*;
import com.sun.j3d.utils.applet.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

public class DisplayObj extends Applet implements ActionListener, TestFlightListener {
  private Button go = new Button("Go");
  private TransformGroup body;
  private Transform3D trans = new Transform3D();
  private Timer timer;
  public TransformGroup LoadOBJ(String fn) throws IOException {
    TransformGroup tg=new TransformGroup();
    
    Loader L=new ObjectFile(ObjectFile.TRIANGULATE,(float)Math.toRadians(60));
    Scene S=L.load(fn);
    BranchGroup sg=S.getSceneGroup();
 
    int max=sg.numChildren();
    for(int i=0;i<max;i++) {
      Shape3D N=(Shape3D)sg.getChild(i);
      System.out.println(N);
      int n=N.numGeometries();
      System.out.println(n);
      for(int j=0;j<n;j++) {
        TriangleArray G=(TriangleArray)N.getGeometry(j);
        System.out.println(G);
        int q=G.getInitialCoordIndex();
        int r=G.getVertexCount();
        float[] coords=G.getInterleavedVertices();
        System.out.printf("%X\n",G.getVertexFormat());
        for(int s=0;s<10;s++) {
          System.out.printf("%f,%f,%f\n",coords[s*3+0],coords[s*3+1],coords[s*3+2]);
        }
      }
    }
    Transform3D tObj=new Transform3D();
    tObj.setScale(new Vector3d(0.01,0.01,0.01));
    tg.setTransform(tObj);
    tg.addChild(sg);
    return tg;
  }
  public BranchGroup createSceneGraph() throws IOException {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    body = new TransformGroup();
    body.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    Transform3D pos1 = new Transform3D();
    body.setTransform(pos1);
    body.addChild(LoadOBJ("/usr/codebase/pov/Cassini/ant_assy.obj"));
    body.addChild(LoadOBJ("/usr/codebase/pov/Cassini/uss_assy.obj"));
    body.addChild(LoadOBJ("/usr/codebase/pov/Cassini/bus_assy.obj"));
    body.addChild(LoadOBJ("/usr/codebase/pov/Cassini/rsp_assy.obj"));
    body.addChild(LoadOBJ("/usr/codebase/pov/Cassini/sci_assy.obj"));
    body.addChild(LoadOBJ("/usr/codebase/pov/Cassini/pm_assy.obj"));
    objRoot.addChild(body);
    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000.0);
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

  public DisplayObj() throws IOException {
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
    DisplayObj bb = new DisplayObj();
    new MainFrame(bb, 256, 256);
    SixDOFState RVEw0=new SixDOFState(new MathVector(), new MathVector(), Quaternion.U, new MathVector(Math.toRadians(0),Math.toRadians(50),Math.toRadians(17)));
    Universe U=new EmptyUniverse();
    InertSixDOFMass TumblerM=new InertSixDOFMass("Tumbler",0.5,new MathVector(),SixDOFMass.RectangularPrismI(0.5,0.01,0.04,0.09));
    System.out.println(TumblerM);
    SixDOFVehicle Tumbler=new InertSixDOFVehicle(U, TumblerM);
    A=new TestFlight(Tumbler);
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

