//   UDPClient_002.java

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class UDPClient_002{
    protected int port = 3301;
    protected String host = "localhost";
    protected String keyPairsFileName = "keyPairs.bin";
    protected Vector<KeyPair> keyPairs;
    protected KeyPair keyPair;

    //  constructors
    public UDPClient_002(){
        init();
    }

    public UDPClient_002(String h, int p){
        port = p;
        host = new String(h);
        init();
    }

    @SuppressWarnings("unchecked")
    protected void init(){
        try{
            //  input key pairs from file
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(keyPairsFileName)));
            keyPairs = (Vector<KeyPair>)ois.readObject();
            ois.close();

            //  testing code
            System.out.println("no. of key pairs : " + keyPairs.size());
            int a = (int)(Math.random()*1000*keyPairs.size())%keyPairs.size();
            System.out.println(a);

            //  get a keypair by chance
            keyPair = keyPairs.get(a);
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
    }

    public String getMessage(){
        PublicKey publicKey = keyPair.getPublic();
        String encodedString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String strData = "ERROR";

        try{
            //  send the public key to server
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress iPAddress = InetAddress.getByName(host);
            byte[] buf = new byte[256];

            //  test code
            //  encodedString = "abcd";

            buf = encodedString.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, iPAddress, port);
            clientSocket.send(packet);

            //  wait for return message
            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            clientSocket.setSoTimeout(5000);
            clientSocket.receive(packet);
            buf = packet.getData();

            if(buf[0]==-2){
                var i = new ByteArrayInputStream(buf);

                //  consume the 1'st byte
                i.read();

                //  get the encrypted secret key
                var encryptedKey = new byte[128];
                i.read(encryptedKey);

                //  get the IV
                var b16 = new byte[16];
                i.read(b16);  // IV

                //  get the encrypted message
                var encryptedMessage = new byte[i.read()];
                i.read(encryptedMessage);
                i.close();
    
                //  get the secrete key
                var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                var b32 = cipher.doFinal(encryptedKey);

                //  test code
                System.out.println("check point 1.");

                //  get the server message
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(b32, "AES"), new IvParameterSpec(b16));
                strData = new String(cipher.doFinal(encryptedMessage), "UTF-8");
            }

            //  test code
            //  System.out.println("FROM SERVER: " + strData);

            clientSocket.close();
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }

        return strData;
    }

    public static void main(String[] args)throws Exception{
        UDPClient_002 a = new UDPClient_002();
        System.out.println("FROM SERVER: " + a.getMessage());
    }
}
