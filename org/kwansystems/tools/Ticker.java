package org.kwansystems.tools;

public class Ticker {
  private static long firstTic;
  private static long lastTic;
  public static void tic() {
	firstTic=System.nanoTime();
	lastTic=firstTic;
  }
  public static double toc(String S) {
	long thisToc=System.nanoTime();
	System.out.println(S);
	double result=((double)(thisToc-lastTic))/1e9;
	System.out.printf("Elapsed Time: %f\n",result);
	System.out.printf("Total Elapsed Time: %f\n",((double)(thisToc-firstTic))/1e9);
	lastTic=thisToc;
	return result;
  }
  public static void toc() {
	toc("");
  }

}
