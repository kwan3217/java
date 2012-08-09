package org.kwansystems.rollercoasterometer;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.vector.*;

public class Integrate {
  RungeKutta I;
  enum Component {TIME,AX,AY,AZ,RX,RY,RZ};
  double toPhysical(Component c, int value) {

  }
  const g_z=9.79;
  DerivativeSet F=new DerivativeSet() {

    @Override
    public MathVector dxdt(double T, MathVector X, boolean IsMajor) {
      ;
    }
  };
}
