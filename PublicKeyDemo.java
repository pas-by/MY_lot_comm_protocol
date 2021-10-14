//  PublicKeyDemo.java

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import javax.crypto.*;

public class PublicKeyDemo{
    public static void main(String[] args)throws Exception{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.genKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        System.out.println(publicKey.getFormat());
        System.out.println(publicKey.toString());

        String encodedString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println(encodedString);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey2 = keyFactory.generatePublic(spec);
        System.out.println(publicKey2.toString());

        int maxKeySize = Cipher.getMaxAllowedKeyLength("AES");
        //  in case of limited policy files, it return 128.
        //  in case of 2147483647, the JCE uses unlimited policy files.
        System.out.println("max. key size is " + maxKeySize);
    }
}
