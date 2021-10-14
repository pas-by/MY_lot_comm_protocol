//  pki_demo.java

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.*;
import java.io.*;

public class pki_demo{
    protected PublicKey publicKey;
    protected PrivateKey privateKey;

    protected String planText;
    protected byte[] enByteArray;

    //  asymmetric
    protected String algorithm01 = "RSA/ECB/PKCS1Padding";

    //  symmetric
    protected String algorithm02 = "AES/CBC/PKCS5Padding";

    public pki_demo(PublicKey p, String planText){
        publicKey = p;
        this.planText = new String(planText);

        //  prepare a 32-byte for secret key
        SecureRandom r = new SecureRandom();
        byte[] b32 = new byte[32];
        r.nextBytes(b32);

        //  prepare a 16-byte for IV
        r.reseed();
        byte[] b16 = new byte[16];
        r.nextBytes(b16);

        try{
            Cipher cipher = Cipher.getInstance(algorithm02);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(b32, "AES"), new IvParameterSpec(b16));

            //  test code
            //  System.out.println("check point");

            //  encrypt the input message
            enByteArray = cipher.doFinal(planText.getBytes("UTF-8"));

            //  test code
            System.out.println("size in bytes : " + enByteArray.length);

            cipher = Cipher.getInstance(algorithm01);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encodedKey = cipher.doFinal(b32);

            //  test code
            //  System.out.println("size in bytes : " + b32.length);
            System.out.println("size in bytes : " + encodedKey.length);

            //  output to a byte array
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            o.write(encodedKey);  // encrypted secret key
            o.write(b16);  //  IV
            o.write(enByteArray);  //  encrypted text

            enByteArray = o.toByteArray();
            o.close();
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
    }

    public pki_demo(PrivateKey p, byte[] enByteArray){
        privateKey = p;
        this.enByteArray = enByteArray;

        try{
            ByteArrayInputStream i = new ByteArrayInputStream(enByteArray);
            byte[] encryptedKey = new byte[128];
            i.read(encryptedKey);
            byte[] b16 = new byte[16];
            i.read(b16);  // IV

            Cipher cipher = Cipher.getInstance(algorithm01);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] b32 = cipher.doFinal(encryptedKey);

            cipher = Cipher.getInstance(algorithm02);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(b32, "AES"), new IvParameterSpec(b16));
            planText = new String(cipher.doFinal(i.readAllBytes()), "UTF-8");
            i.close();
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
    }

    public String getPlanText(){
        return new String(planText);
    }

    public byte[] getEncryptedBytes(){
        return enByteArray.clone();
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args)throws Exception{
        //  read key pairs from file
        String keyPairsFileName = "keyPairs.bin";
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(keyPairsFileName)));
        Vector<KeyPair> keyPairs = (Vector<KeyPair>)ois.readObject();
        ois.close();

        //  get a key pair by chance
        int a = (int)(Math.random()*1000*keyPairs.size())%keyPairs.size();
        KeyPair keyPair = keyPairs.get(a);

        pki_demo b = new pki_demo(keyPair.getPublic(), "HELLO WORLD!");
        b = new pki_demo(keyPair.getPrivate(), b.getEncryptedBytes());
        System.out.println(b.getPlanText());
    }
}
