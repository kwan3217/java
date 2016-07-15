package org.kwansystems.emulator.arm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kwansystems.emulator.arm.encoding.ThumbFloatingPointDecodeLine;

public class CortexM extends DatapathF {
  public CortexM() {
    decode = new CortexMDecode();
  }
  public void reset() {
    System.out.println("==Reset==");
    int VTOR=readMem4(0xE000ED08);
    r[13]=readMem4(VTOR);
    BranchWritePC(readMem4(VTOR+4));
  }
}