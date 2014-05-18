package org.kwansystems.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.Arrays;

public class KwanCryptWrapper {
  private static final boolean debug=false;
  private static final Whirlpool w=new Whirlpool();
  private static byte[] hash(byte[] bitstream) {
    w.NESSIEinit();
    w.NESSIEadd(bitstream,bitstream.length*8);

    byte[] digest = new byte[Whirlpool.DIGESTBYTES];
    w.NESSIEfinalize(digest);
    return digest;
  }
  private static byte[] hash(String s) {
    byte[] bitstream;
    try {
      bitstream=s.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      throw new IllegalArgumentException(e);
    }
    return hash(bitstream);
  }
  /**
   * Wrapper around the Twofish block encryption algorithm and the Whirlpool hashing algorithm. This is designed
   * to take a string of arbitrary length as passphrase and another as plaintext, then perform the following:
   * 
   *   #Hash the passphrase to generate a constant length pseudorandom bit string
   *   #Use the appropriate-sized portion of that bit string as the key for Twofish
   *   #Hash the current time and use the appropriate-sized portion to generate an initialization vector (IV)
   *   #Run the Twofish algorithm in output feedback mode. Do the following steps
   *     #Convert the input string into bytes according to UTF-8
   *     #Break the bytes up into blocks of the appropriate length. The last block may be short.
   *     #With the IV as the initial state vector (SV), encrypt the last state vector
   *     #XOR this block byte-by-byte with the block. If the block is short, that's ok. This is a ciphertext block.
   *     #Accumulate the original IV and each ciphertext block into a big byte array
   *     #Convert the byte array back to a string by hex encoding each byte. This is guaranteed to be acceptable and 
   *        round-trip convertable in the way any random string of bytes is NOT guaranteed to be a valid UTF-8 stream.
   */
  public static String encrypt(String passphrase, String plaintext) throws InvalidKeyException {
    return encrypt(passphrase,plaintext,makeIV(System.currentTimeMillis()));
  }
  private static void encBytes(StringBuffer buf, byte[] in) {
    buf.append(Whirlpool.display(in));
  }
  private static byte[] decBytes(String buf) {
    byte[] result=new byte[buf.length()/2];
    for(int i=0;i<result.length;i++) {
      result[i]=((byte)((Integer.parseInt(buf.substring(i*2,i*2+2).toLowerCase(),16) & 0xFF)));
    }
    return result;
  }
  private static byte[] makeIV(Long seed) {
    byte[] tsb=new byte[8]; 
    for (int i = 0; i < 8; i++) {
      tsb[i] = (byte) (seed >>> i*8);
    }
    byte[] tshash=hash(tsb);
    byte[] IV=new byte[Twofish_Algorithm.blockSize()]; //Initial value is IV
    System.arraycopy(tshash,0,IV,0,IV.length);
    return IV;
  }
  private static Object makeKey(String passphrase,int rounds) throws InvalidKeyException {
    byte[] passphrasehash=hash(passphrase);
    for(int i=1;i<rounds;i++) passphrasehash=hash(passphrasehash);
    byte[] key=new byte[256/8]; //Use 256 bits of passphrase hash as key
    System.arraycopy(passphrasehash, 0, key, 0, key.length);
    return Twofish_Algorithm.makeKey(key);
  }
  public static String encrypt(String passphrase, String plaintext, byte[] IV) throws InvalidKeyException {
    byte[] ciphertextBytes;
    try {
      ciphertextBytes=plaintext.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
    if(debug) System.out.printf("encrypt plaintext: %s\n",Whirlpool.display(ciphertextBytes));
    byte[] SV=new byte[Twofish_Algorithm.blockSize()];
    System.arraycopy(IV,0,SV,0,SV.length);
    StringBuffer ciphertext=new StringBuffer("");
    encBytes(ciphertext,IV);
    int nblocks=ciphertextBytes.length/SV.length;
    if (0 != (ciphertextBytes.length % SV.length)) nblocks++;
    Object sessionKey=makeKey(passphrase,1000);
    for(int i=0;i<nblocks;i++) {
      SV=Twofish_Algorithm.blockEncrypt(SV, 0, sessionKey);
      if(debug) System.out.printf("encrypt SV %02d: %s\n",i,Whirlpool.display(SV));
      int j=0;
      //Do the xor in-place, the plaintext is gradually converted to ciphertext
      while(j<SV.length && (j+i*SV.length)<ciphertextBytes.length) {
        if(debug) System.out.printf("Encrypt %02d: %02x xor %02x",j+i*SV.length, ciphertextBytes[j+i*SV.length],SV[j]);
        ciphertextBytes[j+i*SV.length]^=SV[j];
        if(debug) System.out.printf(" = %02x\n", ciphertextBytes[j+i*SV.length]);
        j++;
      }
    }
    encBytes(ciphertext,ciphertextBytes);
    return ciphertext.toString();
  }
  public static String decrypt(String passphrase, String ciphertext) throws InvalidKeyException {
    return decrypt(passphrase,ciphertext,1000);
  }
  public static String decrypt(String passphrase, String ciphertext, int rounds) throws InvalidKeyException {
    byte[] ciphertextBytes=decBytes(ciphertext);
    byte[] SV=new byte[Twofish_Algorithm.blockSize()];
    System.arraycopy(ciphertextBytes,0,SV,0,SV.length);
    if(debug) System.out.println("IV: "+Whirlpool.display(SV));
    byte[] plaintextBytes=new byte[ciphertextBytes.length-SV.length];
    System.arraycopy(ciphertextBytes,SV.length,plaintextBytes,0,plaintextBytes.length);
    int nblocks=plaintextBytes.length/SV.length;
    if (0 != (plaintextBytes.length % SV.length)) nblocks++;
    Object sessionKey=makeKey(passphrase,rounds);
    for(int i=0;i<nblocks;i++) {
      SV=Twofish_Algorithm.blockEncrypt(SV, 0, sessionKey);
      if(debug) System.out.printf("decrypt SV %02d: %s\n",i,Whirlpool.display(SV));
      int j=0;
      //Do the xor in-place, the ciphertext is gradually converted to plaintext
      while(j<SV.length && (j+i*SV.length)<plaintextBytes.length) {
        if(debug) System.out.printf("Decrypt %02d: %02x xor %02x",j+i*SV.length, plaintextBytes[j+i*SV.length],SV[j]);
        plaintextBytes[j+i*SV.length]^=SV[j];
        if(debug) System.out.printf(" = %02x\n", plaintextBytes[j+i*SV.length]);
        j++;
      }
    }
    if(debug) System.out.printf("decrypt plaintext: %s\n",Whirlpool.display(plaintextBytes));
    try {
      return new String(plaintextBytes,"UTF-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      throw new IllegalArgumentException(e);
    }
  }
  /**
   * @param args
   * @throws InvalidKeyException 
   */
  public static void main(String[] args) throws InvalidKeyException {
    String plaintext="Lorem \u00C0Ipsum";
    String passphrase="Secret key, do not reveal!";
    long IVseed=3217;
    byte[] IV=makeIV(IVseed);
//    System.out.println("IV: "+Whirlpool.display(IV));
    String ciphertext=encrypt(passphrase,plaintext);
    System.out.println("Ciphertext: "+ciphertext);
    System.out.println("Decrypted:  "+decrypt(passphrase,ciphertext));
  }

}
