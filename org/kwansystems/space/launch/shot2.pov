#declare NaN=0;
#include "shot2.data"
#declare Nominal=array[50] {
<  0.33,  0.000001,   0>,
<  0.71,  0.0001  ,   0>,
<  0.88,  0.0002  ,   0>,
<  1.00,  0.0003  ,   0>,
<  1.09,  0.0004  ,   0>,
<  1.17,  0.0005  ,   0>,
<  1.24,  0.0006  ,   0>,
<  1.43,  0.001   ,   0>,
<  1.83,  0.002   ,   0>,
<  2.11,  0.003   ,   0>,
<  2.35,  0.004   ,   0>,
<  2.55,  0.005   ,   0>,
<  2.73,  0.006   ,   0>,
<  6.10,  0.04    ,   0>,
<  8.30,  0.08    ,   0>,
<  9.00,  0.12    ,   0>,
< 11.40,  0.16    ,   0>,
< 12.70,  0.20    ,   0>,
< 13.20,  0.24    ,   0>,
< 50.00,  4.70    ,   6.00>,
< 61.48,  7.35    ,   0>,
< 78.90, 13.43    ,   0>,
< 91.10, 20.00    ,   0>,
<100.00, 25.50    ,  12.00>,
<122.20, 40.00    ,   0>,
<125.93, 41.48    ,  42.05>,
<145.20, 60.00    ,   0>,
<150.00, 63.20    ,  87.00>,
<153.82, 65.75    ,  89.46>,
<154.47, 66.37    ,  90.84>,
<168.20, 80.00    ,   0>,
<192.20,100.00    ,   0>,
<200.00,105.60    , 182.00>,
<220.00,120.00    ,   0>,
<250.00,136.60    , 325.00>,
<258.00,140.00    ,   0>,
<300.00,158.00    , 473.00>,
<305.00,160.00    ,   0>,
<350.00,172.00    , 646.00>,
<395.00,180.00    ,   0>,
<400.00,181.10    , 850.00>,
<450.00,186.00    ,1085.00>,
<500.00,190.00    ,1351.00>,
<524.04,191.54    ,1504.32>,
<524.90,191.61    ,1509.67>,
<550.00,193.00    ,1642.00>,
<600.00,194.00    ,1973.00>,
<650.00,193.00    ,2313.00>,
<684.98,191.36    ,2577.30>,
<700.00,191.00    ,2666.00>,
}


#declare I=0;
#declare Graph=union {
union {
#while(I<dimension_size(Shot2,1))
  sphere {
    Shot2[I]*<1,1,1>,1
  }
  #declare I=I+1;
#end
}
#declare I=0;
union {
#while(I<dimension_size(Nominal,1))
  sphere {
    Nominal[I]*<1,1,1>,2
    pigment {color rgb<1,1,0>}
  }
  #declare I=I+1;
#end
}
}

object {
  Graph
  pigment {color rgb <1,0,0>}
  translate y*100
}

object {
  Graph
  pigment {color rgb <0,0,1>}
  rotate y*90
  translate -y*100
}
object {
  Graph
  pigment {color rgb <0,1,0>}
  rotate -x*90
  translate -y*300
}

camera {
  location <400,0,-750>
  look_at <400,0,0>
  orthographic
}

light_source {
  <0,0,-5000>
  color rgb 1.5
  shadowless
}
/*
plane {
  z,0
  pigment {checker color rgb <1,1,1> color rgbt <1,1,1,1>}
  scale 10
  translate z*10000
}
plane {
  z,0.0001
  pigment {checker color rgb <0.25,0.25,0.25> color rgb <0.75,0.75,0.75>}
  scale 100
  translate z*10000
}
*/
