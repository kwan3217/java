package org.kwansystems.xkcd;

public class Xkcd1 {
  static int prices[]=new int[] {215,275,335,355,420,580};
  static int N_ITEMS=prices.length;
  static int[] max_order=new int[N_ITEMS];
  static int[] order=new int[N_ITEMS];
  static int target=1520;
  static int orders_tried=0;
  static int solutions_found=0;
  /*
  Solve this by recursion. http://xkcd.com/c287.html

  */

  /* Checks if an order matches the target price. If it does, print it out. */
  static void check_order() {
    orders_tried++;
    int total=0;
    for(int i=0;i<N_ITEMS;i++) {
      total+=prices[i]*order[i];
    }
    if(total==target) {
      for(int i=0;i<N_ITEMS;i++) {
        System.out.printf("%d ",order[i]);
      }
      System.out.printf("%d\n",total);
      solutions_found++;
    }
  }

  /*The main recursive part. This function takes an order slot number
    as its only argument. It iterates through all the possible values
    of this slot, and each time calls itself with the next higher
    order slot number. If the slot number is out of range, this is
    a signal that the order candidate is filled out, and it needs
    to be tried */
  static void recurse(int slot) {
    /*Are we off the end of the order? */
    if(slot>=N_ITEMS) {
      /*We have a complete order. Check it out.*/
      check_order();
    } else {
      /* Current order is incomplete.*/
      /* Fill out our slot */
      for(order[slot]=0;order[slot]<=max_order[slot];order[slot]++) {
        /* Recurse to fill out the rest of the slots */
        recurse(slot+1);
      }
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    for (target=1500;target<=2000;target+=5) {
      solutions_found=0;
      System.out.printf("Target: %d\n",target);
      /*Determine the size of the problem*/
      int size=1;
      for(int i=0;i<N_ITEMS;i++) {
         max_order[i]=target/prices[i];
         if(target % prices[i]!=0) max_order[i]++;
         size*=(max_order[i]+1);
      }
      System.out.printf("Size: %d\n",size);
      /*Solve the problem!*/
      recurse(0);
      System.out.printf("Solutions found: %d\n",solutions_found);
    }
  }

}
