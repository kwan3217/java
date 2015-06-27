package org.kwansystems.automaton.tape;

import java.io.*;
import java.util.*;

import org.kwansystems.automaton.*;

/**
 * Read-only one-way infinite tape backed by a Reader. 
 * <p>
 * The tape has an infinite number of cells, but it is one-way infinite. In other words, it has one definite 
 * endpoint to the left of cell 0, but no definite right endpoint. A command to move off the left end of the tape
 * will be detected and should cause the automaton to crash. If it is not detected, the tape will cause a Java 
 * ArrayBoundsException when it tries to read or write off the left end of the tape. 
 * <p>
 * This tape is backed by a Reader. It can move to the right indefinitely (subject to implementation 
 * limits on reader size like filesystem max file size), but is quite limited in moving to the left. This is 
 * because it uses a pushback buffer to keep track of the last few characters read. The pushback buffer could
 * be made indefinitely long, but then it would end up containing the entire contents of the Reader read so far.
 * This limits its usefunless to such things as Turing machines, which might want to go arbitrarily far to the
 * left. Being read-only limits its usefulness further, though. This is more up the alley of Klex, which uses 
 * a DFA which itself only moves right, and occasionally pushes back characters it uses to identify the end of 
 * a token, but are not themselves part of the token.
 * <p>
 * This tape is read-only, and attempts to call the write method will be met with a ReadOnlyTapeException.
 * <p>  
 * The pushback buffer is done the Kwan Systems way. This is designed with the 
 * ReaderTape pushback buffer needs in mind.
 * <p>
 * The buffer appears to the user to function as follows: There is a read head
 * which can move forward an unlimited amount, but can move backward subject 
 * to the amount of buffer space available. As the read head moves forward, 
 * new data is gathered from a data source, and old data is dropped off the
 * tail end of the buffer. The buffer is strictly read-only, and should
 * be used with a data source where data, once created, never changes again
 * <p>
 * How it actually works is as follows: There is a character buffer of predefined
 * length, operating as an overwriting ring buffer. This buffer has two pointers:
 * <ul>
 * <li>A head pointer, pointing to the slot in the buffer which will be 
 * written to next when the buffer needs to be filled.</li>
 * <li>A here pointer, pointing to the slot in the buffer which will be
 * read from when requested. The here pointer may be moved about freely
 * with calls to {@link #Left()} and {@link #Right()} except when the pointer
 * is in the vicinity of the head pointer. In this case:
 * <ul>
 * <li>If the here pointer is one slot to the left of the head pointer and
 * moved to the right, this requires more input, so the buffer is filled and
 * the head pointer moves to the right also.</li>
 * <li>If the here pointer is at the head pointer and is read, you get the oldest
 * character still in the buffer.</li>
 * <li>If the here pointer is at the head pointer and attempts to move to the 
 * left, this crashes the machine, because it is trying to dig farther back than
 * the buffer still has. It is like going off the left end of a one-way infinite tape,
 * so in effect when stuff falls off the pushback buffer, it is like the part of the
 * tape that data is on is cut off, leaving a new tape left end.</li>
 * </ul>
 * </ul>
 * Of course special care needs to be taken in all cases when going "around the 
 * horn", so that this linear buffer in fact acts like a circular buffer.
 * <p>
 * At one point this design had a tail pointer, but since there is no explicit
 * discarding of the data, only overwriting by the data at the head pointer, 
 * there is no need, except for the special case of the first time around the 
 * buffer when the buffer isn't full yet and the read pointer tries to go left,
 * off the real left end of the Reader. To prevent this, we just fill the buffer
 * up in initialization, before the automaton using it ever has the chance
 * to screw things up. 
 */
public class ReaderTape implements Tape<Character> {
    /** Symbol present on all cells of the tape not yet written to. In this case it is the symbol
   * present off the end of the file. */
  private char blankSymbol='\4';

  public boolean isFinite=false;

  private boolean isCrashed=false;
  /** Throws an exception, as you can't write to this kind of tape.
   * @see org.kwansystems.automaton.tape.Tape#write(char)
   * @throws ReadOnlyTapeException every time it's called
   */
  public Termination write(Character newSymbol) {
    isCrashed=true;
    return Termination.Crash;
  }
  public Character getBlankSymbol() {
    return blankSymbol;
  }
  public void setBlankSymbol(Character b) {
    blankSymbol=b;
  }
  protected Reader inf;
  /** Backing store of buffer, a simple character array */
  private char[] buf;
  private int head;
  private int here;
  
  public ReaderTape(Reader Linf,int size) throws IOException {
    inf=Linf;
    buf=new char[size];
    head=0;
    here=0;
    for(int i=0;i<size;i++) getNextChar();
  }
  public ReaderTape(Reader inf) throws IOException {
    this(inf,80);
  }
    
  /** Reads the buffer without moving the read pointer
   * @return character at the read pointer
   */
  public Character read() {
    if(isCrashed) return blankSymbol;
    return buf[here];
  }
  protected void getNextChar() throws IOException {
    int next=inf.read();
    if (next<0) {
      next=(int)blankSymbol;
      if(!isFinite) isCrashed=true;;
    }
    buf[head]=(char)next;
    head++;
    if(head>=buf.length) head=0;
  }
  /** 
   * @see org.kwansystems.automaton.tape.Tape#Left()
   */
  public Termination Left() {
    if(here==head) {
      isCrashed=true;
      return Termination.Crash;
    }
    here--;
    if(here<0) here=buf.length-1;
    return Termination.Continue;
  }
  /**
   * @see org.kwansystems.automaton.tape.Tape#Right()
   */
  public Termination Right() {
    if(isCrashed) return Termination.Crash;
    if(here+1==head || (here==buf.length-1 && head==0)) {
      try {
        getNextChar(); 
      } catch (IOException E) {
        isCrashed=true;
        return Termination.Crash;
      }
    }
    here++;
    if(here>=buf.length) here=0;
    return Termination.Continue;
  }
  public TapeDisplay<Character> getTapeDisplay() {
    TapeDisplay<Character> result=new TapeDisplay<Character>();
    result.tapeData=new ArrayList<Character>(buf.length);
    for(int i=0;i<buf.length;i++) result.tapeData.set(i,buf[i]);
    result.pointers=new int[] {here,head};
    result.pointerNames=new String[] {"Tape Head","Next Read Slot"};
    return result;
  }
}
