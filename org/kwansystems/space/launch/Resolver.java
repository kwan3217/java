package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.vector.*;

public class Resolver {

  Planet P;
  
    public Resolver(Planet LP) {
      P=LP;
    }
    
    //Wherever given, MathStates and MathVectors represent an inertial reference frame, not rotating
    //except when specifcally made otherwise by a ResolveXxxR function. I and R refer to the components, not the vectors

    MathVector ResolveRadialTransverseI(MathVector R, MathVector V) {
      double TransverseVel=V.CompPerp(R);
      double RadialVel=V.Comp(R);
      return new MathVector(new double[] {RadialVel,TransverseVel});
    }
    
    MathVector ResolveRadialTransverseI(MathState X) {
      return ResolveRadialTransverseI(X.R(),X.V());
    }
    
    MathVector ResolveRadialTransverseR(MathVector R, MathVector V) {
      MathVector Vrel=MathVector.sub(V,P.Wind(R));
      return ResolveRadialTransverseI(R,Vrel);
    }
    
    MathVector ResolveRadialTransverseR(MathState X) {
      return ResolveRadialTransverseI(X.R(),X.V());
    }
    
    MathVector ResolveRadialNorthEastI(MathVector R, MathVector V) {
      return null;
    }
    
    MathVector ResolveRadialNorthEastI(MathState X) {
      return ResolveRadialNorthEastI(X.R(),X.V());
    }
    
    MathVector ResolveRadialNorthEastR(MathVector R, MathVector V) {
      MathVector Vrel=MathVector.sub(V,P.Wind(R));
      return ResolveRadialNorthEastI(R,Vrel);
    }
    
    MathVector ResolveRadialNorthEastR(MathState X) {
      return ResolveRadialNorthEastR(X.R(),X.V());
    }
    
    MathVector ComposeRadialTransverseI(MathState X, double Radial, double Transverse) {
      MathVector RadialVec=X.R().normal();
      RadialVec.mul(Radial);
      MathVector TransverseVec=MathVector.cross(MathVector.cross(X.R(),X.V()),X.R()).normal();
      TransverseVec.mul(Transverse);
      return MathVector.add(RadialVec,TransverseVec);
    }
    
    MathVector ComposeRadialTransverseR(MathState X, double Radial, double Transverse) {
      MathVector Vabs=ComposeRadialTransverseI(X,Radial,Transverse);
      Vabs=Vabs.add(P.Wind(X));
      return Vabs;
    }
    
    MathVector ComposeRadialNorthEastI(MathState X, double Radial, double North, double East) {
      MathVector RadialVec=X.R().normal();
      MathVector EastVec=MathVector.cross(X.R(),new MathVector(0,1,0)).normal();
      MathVector NorthVec=MathVector.cross(EastVec,X.R()).normal();
      RadialVec=RadialVec.mul(Radial);
      NorthVec=NorthVec.mul(North);
      EastVec=EastVec.mul(East);
      return MathVector.add(MathVector.add(RadialVec,NorthVec),EastVec);
    }

    MathVector ComposeRadiaNorthEastR(MathState X, double Radial, double North, double East) {
      MathVector Vabs=ComposeRadialNorthEastI(X,Radial,North,East);
      Vabs=Vabs.add(P.Wind(X));
      return Vabs;
    }
    
}
