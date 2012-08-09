package org.kwansystems.space.planet.atmosphere;

/**
 * Object representing free air aerodynamic properties.
 */
public class AirProperties {
  /**
   *Geometric (if it matters) altitude, m. This is a USSA1976 Table 1 property.
   */
  public double Altitude;      
  /**
   *Temperature, K. This is a USSA1976 Table 1 property.
   */
  public double Temperature;   
  /**
   *Pressure, Pa. This is a USSA1976 Table 1 property.
   */
  public double Pressure;      
  /**
   *Density, kg/m^3. This is a USSA1976 Table 1 property.
   */
  public double Density;       
  /**
   *Speed of sound, m/s. This is a USSA1976 Table 3 (lower atmosphere) property.
   */
  public double VSound;        
  /**
   *Molecular weight, kg/kmol. This is a USSA1976 Table 2 property.
   */
  public double MolWt;
  /**
   *Geopotential, m'. This is a USSA1976 Table 1 property.
   */
  public double Geopotential;  
  /**
   *Molecular-scale Temperature, K'. This is a USSA1976 Table 1 property.
   *It is the temperature scaled by the molecular weight, and is the important 
   *temperature for some purposes
   */
  public double MolTemp;       
  /**
   *Acceleration of Gravity, m/s^2. This is a USSA1976 Table 2 property.
   */
  public double Gravity;       
  /**
   *Pressure Scale Height, m. This is a USSA1976 Table 2 property.
   */
  public double PScaleHeight;  
  /**
   *Molecular number density, 1/m^3. This is a USSA1976 Table 2 property.
   */
  public double NumberDensity;
  /**
   *Average molecular velocity, m/s. This is a USSA1976 Table 2 property.
   */
  public double MolVel;
  /**
   *Mean free path length, m. This is a USSA1976 Table 2 property.
   */
  public double MeanFreePath;
  /**
   *Mean Colision frequency, Hz. This is a USSA1976 Table 2 property.
   */
  public double ColFreq;
  /**
   *Dynamic Viscosity. This is a USSA1976 Table 3 (lower atmosphere) property.
   *Other than being resistance to flow, I don't know what it means but its 
   *units are Ns/(m^2)
   */
  public double Viscosity;
  /**
   *Kinematic Viscosity. This is a USSA1976 Table 3 (lower atmosphere) property.
   *I don't know what it means, how it is different from ordinary viscosity, 
   *but its units are m^2/s
   */
  public double KinViscosity;
  /**
   *Thermal conductivity, W/(m*K). This is a USSA1976 Table 3 (lower atmosphere)
   *property.
   */
  public double ThermalCond;
  /**
   *Ratio of specific heats (gamma). This is usually assumed constant for given 
   *gas compostion, so is not a USSA1976 Table entry, but could be a Table 2 property
   *property.
   */
  public double SpecHeatRatio;
  /**
   *Gas number density, 1/m^3. This is a USSA1976 Table 4 (upper atmosphere)
   *property. The order of gases and which gases are represented is up to the
   *atmosphere model.
   */
  public double[] GasNumberDensity;
  /**
   *Gas molecular weight, kg/kmol. Each element gives the molecular weight
   *of the gas in the corresponding slot in GasNumberDensity
   */
  public double[] GasMolWeight;
  /**
   *Gas Name. Each element gives the molecular formula
   *of the gas in the corresponding slot in GasNumberDensity
   */
  public String[] GasName;
  /**
   * An AirProperties object representing a pure vacuum.
   */
  public static final AirProperties Vacuum=new AirProperties();
  /**
   * Construct a new AirProperties object representing a pure vacuum.
   */
  public AirProperties() {
    Altitude=0;
    Temperature=0;
    Pressure=0;
    Density=0;
    VSound=0;
    MolWt=0;
    Geopotential=0;
    MolTemp=0;
    Gravity=0;
    PScaleHeight=0;
    NumberDensity=0;
    MolVel=0;
    MeanFreePath=0;
    ColFreq=0;
    Viscosity=0;
    KinViscosity=0;
    ThermalCond=0;
    SpecHeatRatio=EarthAirConstants.gamma;
    GasNumberDensity=null;
  }
  public String toString() {
    String result="";
    result+="Altitude (m):         "+Altitude+"\n";
    result+="Temperature (K):      "+Temperature+"\n";
    result+="Pressure (Pa):        "+Pressure+"\n";
    result+="Density (kg/m^3):     "+Density+"\n";
    result+="VSound (m/s):         "+VSound+"\n";
    result+="MolWt (kg/kmol):      "+MolWt+"\n";
    result+="Geopotential (m'):    "+Geopotential+"\n";
    result+="MolTemp (K):          "+MolTemp+"\n";
    result+="Gravity (m/s^2):      "+Gravity+"\n";
    result+="PScaleHeight (m):     "+PScaleHeight+"\n";
    result+="NumberDensity (/m^3): "+NumberDensity+"\n";
    result+="MolVel (m/s):         "+MolVel+"\n";
    result+="MeanFreePath (m):     "+MeanFreePath+"\n";
    result+="ColFreq (/s):         "+ColFreq+"\n";
    result+="Viscosity (Ns/m^2):   "+Viscosity+"\n";
    result+="KinViscosity (m^2/s): "+KinViscosity+"\n";
    result+="ThermalCond (W/m/K):  "+ThermalCond+"\n";
    result+="SpecHeatRatio:        "+SpecHeatRatio+"\n";
    if(GasNumberDensity!=null) {
      for(int i=0;i<GasNumberDensity.length;i++)
        result+="GasNumberDensity["+GasName[i]+"("+GasMolWeight[i]+"kg/kmol)] (/m^3): "+GasNumberDensity[i]+"("+GasNumberDensity[i]/NumberDensity+")\n";
    }
    return result;
  }
}
