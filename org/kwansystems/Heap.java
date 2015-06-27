package org.kwansystems;

public class Heap {
  int pqmaxsize;
  class stdelement {
	Object f;
	Object stuff;
  }
  int pqsize;
  int pqueue[];
  void enqueue(int py) {
	pqsize++;
	pqueue[pqsize]=py;
	siftup(pqsize);
  }
  int serve() {
	int result=pqueue[1];
	pqueue[1]=pqueue[pqsize];
	pqsize--;
	siftdown(1,pqsize);
	return result;
  }
  Heap(int Lmaxsize) {
	pqmaxsize=Lmaxsize;
	pqueue=new int[pqmaxsize+1];
    clear();
  }
  void clear() {
	pqsize=0;
  }
  void siftup(int pos) {
	int j=pos/2,k=pos;
	int py=pqueue[pos];
	
	pqueue[0]=pqueue[pos];
	while(pqueue[j]>py) {
      pqueue[k]=pqueue[j];
      k=j;j=j/2;
	}
	pqueue[k]=pqueue[0];
  }
  void siftdown(int pos, int n) {
	int i=pos,j=2*pos;
	int save=pqueue[pos];
	boolean finished=false;

	while(j<=n & !finished) {
      if(j<n) {
    	if(pqueue[j]>pqueue[j+1]){
    	  j++;
    	}
      }
      if(save<=pqueue[j]){
    	finished=true;
      } else {
        pqueue[i]=pqueue[j];
        i=j;j=2*i;
      }
	}
	pqueue[i]=save;
  }
  boolean empty() {
	return pqsize==0;
  }
  boolean full() {
	return pqsize==pqmaxsize;
  }
  boolean checkHeapCond(int i) {
	if(2*i>pqsize) return true;
	System.out.printf("i=%d, r[i]=%d, r[2i=%d]=%d   %b\n",i,pqueue[i],2*i,pqueue[2*i],pqueue[i]<=pqueue[2*i]);
	if(pqueue[i]>pqueue[2*i]) return false;
	if(2*i+1>pqsize) return true;
	System.out.printf("     r[i]=%d, r[2i+1=%d]=%d %b\n",pqueue[i],2*i+1,pqueue[2*i+1],pqueue[i]<=pqueue[2*i+1]);
	if(pqueue[i]>pqueue[2*i+1]) return false;
	return true;
  }
  boolean checkHeapCond() {
	for(int i=1;i<=pqsize;i++) if(!checkHeapCond(i)) return false;
	return true;
  }
  public static void main(String[] args) {
    Heap H=new Heap(15);
    int stuffit[]=new int[]{1,2,3,4,5,6,7,8};
    for(int n:stuffit) H.enqueue(n);
    for(int n=1;n<=H.pqsize;n++) System.out.printf("%d: %d\n",n,H.pqueue[n]);
    while(!H.empty()) {
      H.checkHeapCond();
      System.out.printf("Now serving: %d\n", H.serve());
    }

    H.clear();
    stuffit=new int[]{8,7,6,5,4,3,2,1};
    for(int n:stuffit) H.enqueue(n);
    for(int n=1;n<=H.pqsize;n++) System.out.printf("%d: %d\n",n,H.pqueue[n]);
    while(!H.empty()) System.out.printf("%d\n", H.serve());

    H.clear();
    stuffit=new int[]{8,1,7,2,6,3,5,4};
    for(int n:stuffit) H.enqueue(n);
    for(int n=1;n<=H.pqsize;n++) System.out.printf("%d: %d\n",n,H.pqueue[n]);
    while(!H.empty()) System.out.printf("%d\n", H.serve());
 }

}
