package org.kwansystems.space.ephemeris;

import java.io.*;
import java.util.zip.*;

public abstract class TableEphemeris extends Ephemeris {
  protected abstract void LoadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException;
  protected abstract void LoadText() throws IOException;
  protected abstract void SaveSerial(ObjectOutputStream ouf) throws IOException;
  protected abstract String SerialFilenameCore();
  private File SerialFilename() throws IOException {
	  return new File("Data/"+SerialFilenameCore()+".serial.gz");
  }
  private void LoadSerial() throws IOException, ClassNotFoundException {
    ObjectInputStream inf = new ObjectInputStream(new GZIPInputStream(new FileInputStream(SerialFilename())));
    LoadSerial(inf);
    inf.close(); 
  }
  private void SaveSerial() throws IOException {
    ObjectOutputStream ouf = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(SerialFilename())));
    SaveSerial(ouf);
    ouf.close();
  }
  protected void Load() {
	  try {
  	  if(!SerialFilename().canRead()) {
	      LoadText();
  	    SaveSerial();
	    }
      LoadSerial();
	  } catch (IOException E) {
	    throw new RuntimeException(E);
	  } catch (ClassNotFoundException E) {
	    throw new RuntimeException(E);
	  }
  }
  public TableEphemeris(Ephemeris LReference) {
    super(LReference);
  }
  public TableEphemeris() {
    this(null);
  }
}
