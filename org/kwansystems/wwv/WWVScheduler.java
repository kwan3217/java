package org.kwansystems.wwv;

import static org.kwansystems.wwv.WWVScheduler.special.*;

import java.util.*;

public class WWVScheduler {
  public int bgToneFreq; //Background tone frequency in Hz, 0 for no background
  public int tickFreq;   //Tick frequency in Hz, 0 for no tick, 1000Hz for WWV, 1200Hz for WWVH, 1500Hz for top of hour. 
                  //If tick is nonzero, tick should have 10ms before and 25ms after of silence 
  public boolean doubleTick; //True if there is a second tick. If present, matches tickFreq, starts at 100ms, 5ms duration, no silence guard
  public double tickLen;    //Tick length in seconds, 0 for no tick, 0.005 for tick, 0.8 for top of minute
  public boolean endGuard; //Has 10ms of silence at end of second. Set most of the time, cleared for seconds 28 and 58
  public int tt[];
  public enum special {None,StationID,NISTReserved1,Storm1,Storm2,Storm3,GPS1,GPS2,NISTReserved2,Geoalert};

  @Override public int hashCode() {
    int hash = 7;
    hash = 47 * hash + this.bgToneFreq;
    hash = 47 * hash + this.tickFreq;
    hash = 47 * hash + (this.doubleTick ? 1 : 0);
    hash = 47 * hash + (int) (Double.doubleToLongBits(this.tickLen) ^ (Double.doubleToLongBits(this.tickLen) >>> 32));
    hash = 47 * hash + (this.endGuard ? 1 : 0);
    hash = 47 * hash + (this.bcd != null ? this.bcd.hashCode() : 0);
    return hash;
  }
  @Override public boolean equals(Object that) {
    if(that instanceof WWVScheduler) {
      if(((WWVScheduler)that).bgToneFreq!=bgToneFreq) return false;
      if(((WWVScheduler)that).tickFreq  !=tickFreq  ) return false;
      if(((WWVScheduler)that).doubleTick!=doubleTick) return false;
      if(((WWVScheduler)that).tickLen   !=tickLen   ) return false;
      if(((WWVScheduler)that).endGuard  !=endGuard  ) return false;
      if(((WWVScheduler)that).bcd       !=bcd       ) return false;
      return true;
    }  
    return false;
  }
  public enum BCDBits {
//BCD subcarrier bit length. All BCD bits start at 0ms, 100Hz. Possible values are 0.8 (marker), 0.5 (1 bit), 0.2 (0 bit), 0 (top of minute)    
    Pos('p',0.8),One('1',0.5),Zero('0',0.2),Blank('b',0.0);
    public double length;
    public char initial;
    BCDBits(char Linitial, double Llength) {initial=Linitial;length=Llength;};
  };
  public BCDBits bcd;     
  special Special;
  //Tick amplitude is reference (+-0.45)
  //BCD code is 0.15
  //Background tone is 0.25

  /*
   * Overwrite the slots in the "bits" array with 500 as needed to
   * represent the "num", in little-endian order. For WWV-variant IRIG
   * time code, "bits" should be preinitialized to 200. */ 
  private static void bcdcode(BCDBits[] bits, int ofs, int num) { 
    while (num > 0) { 
      if ((num & 1)> 0) {
        bits[ofs] = BCDBits.One; 
      }
      num >>= 1; ofs++; 
    } 
  }
  int dut1=-4;
  int dst=1;
  int ls=1;
  
  int[] wwv_tone_schedule=new int[] {
        //x0  x1  x2  x3  x4 : x5  x6  x7  x8  x9
	   0,600,440,600,500, 600,500,600,500,500, //0x
	 500,600,500,600,500, 500,500,600,500,600, //1x
	 500,600,500,600,500, 600,500,600,500,  0, //2x
	 500,600,500,600,500, 600,500,600,500,600, //3x
	 500,600,500,  0,  0,   0,  0,  0,  0,  0, //4x
	   0,  0,500,600,500, 600,500,600,500,  0  //5x
  };
  
  special[] wwv_special=new special[] {
//  NISTReserved1,NISTReserved1,NISTReserved1,NISTReserved1,NISTReserved1, NISTReserved1,NISTReserved1,NISTReserved1,NISTReserved1,NISTReserved1,
    StationID,    None,         None,         None,         NISTReserved1, None,         None,         None,         Storm1,       Storm2,
    Storm3,       None,         None,         None,         GPS1,          GPS2,         NISTReserved2,None,         Geoalert,     None,        
    None,         None,         None,         None,         None,          None,         None,         None,         None,         None,        
    StationID,    None,         None,         None,         None,          None,         None,         None,         None,         None,        
    None,         None,         None,         None,         None,          None,         None,         None,         None,         None,        
    None,         None,         None,         None,         None,          None,         None,         None,         None,         None        
  };

  public WWVScheduler(Date DD, boolean isWWVH) {
    Special=special.None;
    Calendar cal=new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    cal.setTime(DD);
    int Y=cal.get(Calendar.YEAR);
    int D=cal.get(Calendar.DAY_OF_YEAR);
    int H=cal.get(Calendar.HOUR_OF_DAY);
    int N=cal.get(Calendar.MINUTE);
    int S=cal.get(Calendar.SECOND);
    int MS=cal.get(Calendar.MILLISECOND);
    tt=new int[] {Y,D,H,N,S,MS};
    
    //BCD code. Do the whole minute, but just report one second
    BCDBits[] bits=new BCDBits[60];
    for(int i=0;i<60;i++) bits[i]=BCDBits.Zero; 
    bits[0]=BCDBits.Blank; 
    for(int i=0;i<6;i++) bits[i*10+9]=BCDBits.Pos;
    bcdcode(bits,10, N % 10); 
    bcdcode(bits,15, N / 10); 
    bcdcode(bits,20, H % 10); 
    bcdcode(bits,25, H / 10); 
    bcdcode(bits,30, D % 10); 
    bcdcode(bits,35, (D / 10) % 10);
    bcdcode(bits,40, D / 100);
    Y=Y%100; //WWV has Y2K issue
    bcdcode(bits, 4, Y % 10);
    bcdcode(bits,51, Y / 10);

    TimeZone TZ=TimeZone.getTimeZone("America/Denver");
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    boolean dst0=TZ.inDaylightTime(cal.getTime());
    cal.add(Calendar.DAY_OF_YEAR,1);
    boolean dst1=TZ.inDaylightTime(cal.getTime());
    if(dst0) bits[ 2]=BCDBits.One;
    if(dst1) bits[55]=BCDBits.One;
    
    if(dut1!=0) {
      if(dut1>0) bits[50]=BCDBits.One;
      bcdcode(bits,56,dut1>0?dut1:-dut1);
    }
    //Set up defaults that can be changed below
    bgToneFreq=Special==special.None?wwv_tone_schedule[N]:0;
    tickFreq=(isWWVH?1200:1000);
    doubleTick=false;
    tickLen=0.005;
    bcd=bits[S];
    endGuard=true;
    if(S==0) {
      tickLen=0.8;
      bgToneFreq=0;
      if(N==0) {
        tickFreq=1500;
      }
    } else if(S>=45) {
      bgToneFreq=0;
    }
    if(S==28 | S==58) {
      endGuard=false;
    }
    if(S==29 | S==59) {
      tickLen=0;
    }
    if(dut1>0 & S>=1 & S<=dut1) {
      doubleTick=true;
    }
    if(dut1<0 & S>=9 & S<(-dut1+9)) {
      doubleTick=true;
    }
  }
  public String toString() {
    return "b"+String.format("%03d",bgToneFreq)+
           (doubleTick?"dt":"st")+
           String.format("%04d",tickFreq)+
           "l"+String.format("%03d",(int)(tickLen*1000))+
           (endGuard?"eg":"ng")+
           bcd.initial;
  }
  public static void main(String[] args) {
    Calendar cal=new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    cal.set(2012,2,11,0,0,9);
    WWVScheduler WWVS=new WWVScheduler(cal.getTime(), true);
    System.out.println(WWVS);
  }
}
