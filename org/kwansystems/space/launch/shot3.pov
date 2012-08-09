#include "shot.data"

#declare I=0;
union {
#while(I<dimension_size(Shot,1))
  sphere {
    Shot[I],1.5
    rotate y*I/4/86400*360
  }
  #declare I=I+1;
  #if(I=393)
  pigment {color rgb <1,1,0>}
}
union {
  #end
#end
  pigment {color rgb <1,0,0>}
}

#declare LaunchLat=radians(28.60832);
#declare LaunchLon=radians(-80.60414);

#declare CamLat=radians(18.60832);
#declare CamLon=radians(-80.60414);

#declare CamPoint=<cos(CamLat)*cos(CamLon),sin(CamLat),cos(CamLat)*sin(CamLon)>;
#declare LaunchPoint=<cos(LaunchLat)*cos(LaunchLon),sin(LaunchLat),cos(LaunchLat)*sin(LaunchLon)>;

camera {
//  location <LaunchPoint.x,0,LaunchPoint.z>*12000
  location CamPoint*12000
  look_at LaunchPoint*6378.140
  angle 15
}

light_source {
  LaunchPoint*20e6
  color rgb 1.5
}

sphere {
  0,6378.140
  pigment{
    image_map {
      png "EarthMap.png"
      map_type 1
      interpolate 2
    }
    rotate y*180
  }
}
