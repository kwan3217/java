package org.kwansystems.tools.rotation;

import static java.lang.Math.*;

public class EulerAngle {
  double roll,pitch,yaw;
  /** Creates a new instance of EulerAngle */
  public EulerAngle(Quaternion Q) {
    double E0,Ex,Ey,Ez;
    E0=Q.W();
    Ex=Q.X();
    Ey=Q.Y();
    Ez=Q.Z();
    roll= atan2(2*(E0*Ex+Ey*Ez),E0*E0+Ez*Ez-Ex*Ex-Ey*Ey);
    pitch=asin(2*(E0*Ey-Ex*Ez));
    yaw=  atan2(2*(E0*Ez+Ex*Ey),E0*E0+Ex*Ex-Ex*Ex-Ez*Ez);
  }
  public String toString() {
    return "Roll: "+toDegrees(roll)+"  Pitch: "+toDegrees(pitch)+"  Yaw: "+toDegrees(yaw);
  }
}
