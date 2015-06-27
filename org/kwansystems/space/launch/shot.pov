#include "shot.inc"

#declare I=0;
union {
#while(I<dimension_size(Shot,1))
  sphere {
    Shot[I]-x*6378.140,1
    rotate x*90
    rotate z*90
  }
  #declare I=I+1;
#end
  pigment {color rgb <1,0,0>}
}

camera {
  location <400,0,-750>
  look_at <400,0,0>
}

light_source {
  <0,0,-5000>
  color rgb 1.5
}
plane {
  z,0
  pigment {checker color rgb <1,1,1> color rgbt <1,1,1,1>}
  scale 10
}
plane {
  z,0.0001
  pigment {checker color rgb <0.25,0.25,0.25> color rgb <0.75,0.75,0.75>}
  scale 100
}
torus {
  6378.140,1
  rotate x*90
  translate -y*6378.140
  pigment {color rgb <0,0,1>}
}

