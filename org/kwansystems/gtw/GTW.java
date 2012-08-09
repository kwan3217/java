package org.kwansystems.gtw;

/**
 * Extremely primitive Global Thermonuclear War simulator. Based off of an old old
 * DOS game I knew long long ago. The prototype was purely in Text mode.
 */
public class GTW {
  static final int ShortFlightTime=5;
  static final int LongFlightTime=10;
  static final int Missiles=100;
  static class Country {
    public String Name;
    public String[] TargetList;
    public Country Enemy;
    public boolean[] StillStanding;
    public boolean[] MissileBaseIntact;
    public int[] MissilesLeft;
    public int MissileRoundRobin;
    public Missile[] Arsenal;
    public int MissilesLaunched;
    public Interceptor[] Interceptors;
    public int InterceptorsLaunched;
    public int InterceptorsLeft;
    
    public Country(String LName,String[] LTargetList,Country LEnemy) {
      Name=LName;
      TargetList=LTargetList;
      Enemy=LEnemy;
      StillStanding=new boolean[TargetList.length];
      MissileBaseIntact=new boolean[5];
      MissilesLeft=new int[5];
      Arsenal=new Missile[Missiles];
      MissilesLaunched=0;
      MissileRoundRobin=0;
      for(int i=0;i<TargetList.length;i++) StillStanding[i]=true;
      for(int i=0;i<MissileBaseIntact.length;i++) MissileBaseIntact[i]=true;
      for(int i=0;i<MissileBaseIntact.length;i++) MissilesLeft[i]=20;
      Interceptors=new Interceptor[20];
      InterceptorsLaunched=0;
      InterceptorsLeft=20;
    }
    public boolean StillAlive() {
      if(HasTargets()) return true;
      for(int i=0;i<5;i++) if(MissileBaseIntact[i]) return true;
      for(int i=0;i<MissilesLaunched;i++) if(Arsenal[i].InFlight) return true;
      for(int i=0;i<InterceptorsLaunched;i++) if(Interceptors[i].InFlight) return true;
      return false;
    }
    public boolean HasTargets() {
      for(int i=0;i<18;i++) if(StillStanding[i]) return true;
      for(int i=0;i<5;i++) if(MissileBaseIntact[i]) return true;
      return false;
    }
    public boolean HasMissiles() {
      if(InterceptorsLeft>0) return true;
      for(int i=0;i<5;i++) if(MissilesLeft[i]>0) return true;
      for(int i=0;i<MissilesLaunched;i++) if(Arsenal[i].InFlight) return true;
      for(int i=0;i<InterceptorsLaunched;i++) if(Interceptors[i].InFlight) return true;
      return false;
    }
    public void Launch(int Target) {
      if(MissilesLaunched<Arsenal.length & RoundRobin()) {
        int Tof=(int)Math.floor(Math.random()*(LongFlightTime-ShortFlightTime))+ShortFlightTime;
        Arsenal[MissilesLaunched]=new Missile(this,MissilesLaunched,Enemy,Target,Tof);
        MissilesLeft[MissileRoundRobin]--;
        MissilesLaunched++;
      }
    }
    public void Intercept(int Target) {
      if(InterceptorsLeft>0) {
        Interceptors[InterceptorsLaunched]=new Interceptor(this,InterceptorsLaunched,Enemy,Target);
        InterceptorsLeft--;
        InterceptorsLaunched++;
      }
    }
    //Joshua goes here
    public void AutoTurn() {
      if(Enemy.HasTargets() && Math.random()>0.5) {
        AutoLaunch();
      } else {
        AutoIntercept();
      }
    }
    public boolean TargetStanding(int Target) {
      if(Target<18) {
    	return StillStanding[Target];   
      } else {
    	return this.MissileBaseIntact[Target-18];
      }
    }
    public void AutoLaunch() {
      boolean done=false;
      int Tgt;
      do {
        Tgt=(int)Math.floor(Math.random()*(Enemy.TargetList.length+Enemy.MissileBaseIntact.length));
      } while(!Enemy.TargetStanding(Tgt));
      Launch(Tgt);
    }
    public void AutoIntercept() {
      for(int i=0;i<5;i++) {
        int tgt=(int)(Math.random()*Enemy.MissilesLaunched);
        if(Enemy.Arsenal[tgt]!=null) {
          if(Enemy.Arsenal[tgt].InFlight) {
            Intercept(tgt);
            return;
          }
        }
      }
    }
    public void ManualTurn() throws java.io.IOException {
      System.out.println("(L)aunch or (I)ntercept?");
      String S=readln();
      if(S.length()==0) return;
      if(S.charAt(0)=='i') {
    	ManualIntercept();  
      } else {
    	ManualLaunch();
      }
    }
    public void ManualLaunch() throws java.io.IOException {
      System.out.print("Select Target City: ");
      try {
        int Tgt=Integer.parseInt(readln());
        Launch(Tgt);
      } catch (NumberFormatException E) {System.out.println("No launch this round");}
    }
    public void ManualIntercept() throws java.io.IOException {
      System.out.print("Select Target Missile: ");
      try {
        int Tgt=Integer.parseInt(readln());
        Intercept(Tgt);
      } catch (NumberFormatException E) {System.out.println("No Intercept this round");}
    }
    public void step() {
      for(int i=0;i<MissilesLaunched;i++) {
        if(Arsenal[i].InFlight) {
          Arsenal[i].step();
        }
      }
      for(int i=0;i<InterceptorsLaunched;i++) {
        if(Interceptors[i].InFlight) {
          Interceptors[i].step();
        }
      }
    }
    public boolean RoundRobin() {
      int RR=MissileRoundRobin+1;
      if(RR>=5) RR=0;
      while(true) {
        if(MissilesLeft[RR]>0) {
          MissileRoundRobin=RR;
          return true;
        }
        if(RR==MissileRoundRobin) {
          return false;
        }
        RR++;
        if(RR>=5) RR=0;
      }
    }
  }
  static class Missile {
    public boolean InFlight;
    static final double MalfProb=0.99;
    public int MissileID;
    public Country SourceCountry;
    public Country TargetCountry;
    public int Target;
    public int TimeToImpact;
    public boolean isTargetedOnCity() {
      return MissileBaseTarget()<0;
    }
    public int MissileBaseTarget() {
      return Target-TargetCountry.TargetList.length;
    }
    public String TargetName() {
      if(isTargetedOnCity()) {
        return TargetCountry.TargetList[Target];
      } else {
        return TargetCountry.Name+" Missile Base "+MissileBaseTarget();
      }
    }
    public String toString() {
      return MissileName() + "    Target: " + TargetName() + "    Time to Impact: " + TimeToImpact;
    }
    public String MissileName() {
      return SourceCountry.Name+Integer.toString(MissileID);
    }
    public void step() {
      TimeToImpact--;
      if(TimeToImpact<0) {
        Detonate();
      } else if(Math.random()>MalfProb) {
        InFlight=false;
        System.out.println("Missile "+ MissileName() +" malfunctions and detonates in flight");
      } else {
        System.out.println(toString());
      }
    }
    public void Detonate() {
      InFlight=false;
      boolean HitSomething;
      if(isTargetedOnCity()) {
        if(TargetCountry.StillStanding[Target]) {
          TargetCountry.StillStanding[Target]=false;
          if(Target==17) TargetCountry.InterceptorsLeft=0;
          HitSomething=true;
        } else {
          HitSomething=false;
        }
      } else {
        if(TargetCountry.MissileBaseIntact[MissileBaseTarget()]) {
          TargetCountry.MissileBaseIntact[MissileBaseTarget()]=false;
          TargetCountry.MissilesLeft[MissileBaseTarget()]=0;
          HitSomething=true;
        } else {
          HitSomething=false;
        }
      }
      if(HitSomething) {
        System.out.println("Missile " + MissileName() + " impacts, destroying "+TargetName());
      } else {
        System.out.println("Missile " + MissileName() + " falls into smoldering crater which used to be "+TargetName());
      }
    }
    public Missile(Country LSourceCountry,int LMissileID, Country LTargetCountry,int LTarget, int LTimeToImpact) {
      InFlight=true;
      SourceCountry=LSourceCountry;
      MissileID=LMissileID;
      TargetCountry=LTargetCountry;
      Target=LTarget;
      TimeToImpact=LTimeToImpact;
      System.out.println("Launch Detection! "+toString());
    }
  }
  static class Interceptor {
    public boolean InFlight;
    static final double MalfProb=0.90;
    static final double HitProb=0.60;
    public int MissileID;
    public Country SourceCountry;
    public Country TargetCountry;
    public int Target;
    public int TimeToImpact;
    public String TargetName() {
      return TargetCountry.Arsenal[Target].MissileName();
    }
    public String toString() {
      return MissileName() + "    Target: " + TargetName() + "    Time to Impact: " + TimeToImpact;
    }
    public String MissileName() {
      return SourceCountry.Name+"Int"+Integer.toString(MissileID);
    }
    public void step() {
      TimeToImpact--;
      if(TimeToImpact<0) {
        Detonate();
      } else if(Math.random()>MalfProb) {
        InFlight=false;
        System.out.println("Interceptor "+ MissileName() +" malfunctions and detonates in flight");
      } else {
        System.out.println(toString());
      }
    }
    public void Detonate() {
      InFlight=false;
      if(TargetCountry.Arsenal[Target].InFlight) {
        if(Math.random()<HitProb) {
          TargetCountry.Arsenal[Target].InFlight=false;
          System.out.println("Interceptor " + MissileName() + " intercepts, destroying "+TargetName());
        } else {
          System.out.println("Interceptor " + MissileName() + " fails to intercepts "+TargetName());
        }
      } else {
        System.out.println("Interceptor " + MissileName() + " intercepts the hole in the sky that used to be "+TargetName());
      }
    }
    public Interceptor(Country LSourceCountry,int LMissileID, Country LTargetCountry,int LTarget) {
      InFlight=true;
      SourceCountry=LSourceCountry;
      MissileID=LMissileID;
      TargetCountry=LTargetCountry;
      Target=LTarget;
      TimeToImpact=TargetCountry.Arsenal[Target].TimeToImpact/2;
      System.out.println("Interceptor Launch Detection! "+toString());
    }
  }
  static Country USA=new Country("USA",new String[] {
    "New York City",
    "Los Angeles",
    "Boulder",
    "Seattle",
    "Chicago",
    "Atlanta",
    "Grand Forks",
    "Houston",
    "Washington DC",
    "Medford",
    "San Francisco",
    "St Louis",
    "Miami",
    "Richmond",
    "Charolette",
    "San Diego",
    "Santa Fe",
    "NORAD COC"
  },null);
  static Country USSR=new Country("USSR",new String[] {
    "Russian Target #01",
    "Russian Target #02",
    "Russian Target #03",
    "Russian Target #04",
    "Russian Target #05",
    "Russian Target #06",
    "Russian Target #07",
    "Russian Target #08",
    "Russian Target #09",
    "Russian Target #10",
    "Russian Target #11",
    "Russian Target #12",
    "Russian Target #13",
    "Russian Target #14",
    "Russian Target #15",
    "Russian Target #16",
    "Russian Target #17",
    "Russian Strategic Rocket Forces Command"
  },USA);
  static {USA.Enemy=USSR;}
  public static String makeString(int Length, String S, char C) {
    char[] c=new char[Length-S.length()];
    for (int i=0;i<c.length;i++) c[i]=C;
    return new String(c);
  }
  public static String readln() throws java.io.IOException {
    String S="";
    char c=(char)System.in.read();
    while(c!=10) {
      S=S+c;
      c=(char)System.in.read();
    }
    return S;
  }
  public static void PrintCities() {
    System.out.println("United States of America            | Union of Soviet Socialist Republics");
    System.out.println("------------------------------------|----------------------------------------");
    for(int i=0;i<17;i++) {
      if(USA.StillStanding[i]) System.out.print(" "); else System.out.print("*");
      System.out.print(String.format("%02d: ", i));
      System.out.print(USA.TargetList[i]);
      System.out.print(makeString(31,USA.TargetList[i],' '));
      System.out.print("|");
      if(USSR.StillStanding[i]) System.out.print(" "); else System.out.print("*");
      System.out.print(String.format("%02d: ", i));
      System.out.println(USSR.TargetList[i]);
    }
    if(USA.StillStanding[17]) System.out.print(" "); else System.out.print("*");
    System.out.print(String.format("%02d: ", 17));
    System.out.print(USA.TargetList[17]+": "+USA.InterceptorsLeft);
    System.out.print(makeString(31,USA.TargetList[17]+": "+USA.InterceptorsLeft,' '));
    System.out.print("|");
    if(USSR.StillStanding[17]) System.out.print(" "); else System.out.print("*");
    System.out.print(String.format("%02d: ", 17));
    System.out.println(USSR.TargetList[17]+": "+USSR.InterceptorsLeft);
    for(int i=0;i<5;i++) {
      if(USA.MissileBaseIntact[i]) System.out.print(" "); else System.out.print("*");
      System.out.print(String.format("%02d: ", 18+i));
      System.out.print("Missile Base "+i+": "+USA.MissilesLeft[i]);
      System.out.print(makeString(31,"Missile Base "+i+": "+USA.MissilesLeft[i],' '));
      System.out.print("|");
      if(USSR.MissileBaseIntact[i]) System.out.print(" "); else System.out.print("*");
      System.out.print(String.format("%02d: ", 18+i));
      System.out.println("Missile Base "+i+": "+USSR.MissilesLeft[i]);
    }
  }
  public static void main(String[] args) throws java.io.IOException {
    while((USA.HasMissiles() || USSR.HasMissiles()) && USA.StillAlive() && USSR.StillAlive()) {
      PrintCities();
      USA.step();
      USSR.step();
      USA.ManualTurn();
      USSR.AutoTurn();
    }
    if(!USSR.StillAlive() && USA.StillAlive()) {
      System.out.println("You win! All enemy targets destroyed.");
    } else if(!USA.StillAlive() && USSR.StillAlive()) {
      System.out.println("Sorry, you lose. All friendly targets destroyed.");
    } else if(!USA.StillAlive() && !USSR.StillAlive()){
      System.out.println("Mutual Assured Destruction!");
    } else {
      System.out.println("Stalemate, from weapon exhasution.");
    }
  }
  
}
