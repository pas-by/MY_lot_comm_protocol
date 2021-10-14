//  genKeyPairs.java

import java.security.*;
import java.util.*;
import java.io.*;

public class genKeyPairs{
    protected String keyPairsFileName = "keyPairs.bin";
    protected String publicKeysFileName = "publicKeys.bin";
    protected int numOfKeyPairs = 100;

    public void kickStart(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            Vector<KeyPair> keyPairs = new Vector<KeyPair>();
            HashMap<String, PublicKey> publicKeys = new HashMap<String, PublicKey>();

            //  generate a bunch of key pairs
            for(int index=0; index<numOfKeyPairs; index++){
                KeyPair keyPair = keyGen.genKeyPair();
                keyPairs.add(keyPair);

                //  testing code
                System.out.println(index + 1 + ". " + keyPair.toString());

                PublicKey publicKey = keyPair.getPublic();
                String encodedString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
                publicKeys.put(encodedString, publicKey);
            }
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(keyPairsFileName)));
            //  write key pairs into file
            oos.writeObject(keyPairs);
            oos.close();

            //  write public key into file
            ObjectOutputStream oos2 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(publicKeysFileName)));
            oos2.writeObject(publicKeys);
            oos2.close();
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
    }

    public static void main(String[] args)throws Exception{
        genKeyPairs a = new genKeyPairs();
        a.kickStart();
    }
}
