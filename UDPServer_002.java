//  UDPServer_002.java

import java.net.*;
import java.security.interfaces.*;
import java.security.*;
import java.util.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class UDPServer_002 extends Thread{
    protected int port;
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected HashMap<String, RSAPublicKey> publicKeys;

    //  constructor
    public UDPServer_002(DatagramSocket s, DatagramPacket p, HashMap<String, RSAPublicKey> k){
        socket = s;
        packet = p;
        publicKeys = k;
    }

    public void run(){
        var resultString = "ERROR - not a authorized public key!";
        var strData = new String(packet.getData());
        //  remove white spaces
        //  strData = strData.replaceAll("\\s+", "");
        strData = strData.trim();

        InetAddress iPAddress = packet.getAddress();
        port  = packet.getPort();
        System.out.println("Datagram received: " + iPAddress + " : " + port);

        //  test code
        //  printEachPublicKey();
        //  System.out.println();
        //  System.out.println("no. of public keys : " + publicKeys.size());
        //  System.out.println(strData);
        //  System.out.println(strData.length());

        //  define the response message
        var buf = new byte[] {-1};  //  ERROR

        //  expected to be an authorized public key
        if(publicKeys.containsKey(strData)){
            resultString = "authorized public key received";
            //  System.out.println("check point");

            buf = encryptMessage("HELLO WORLD!", publicKeys.get(strData));
        }

        packet = new DatagramPacket(buf, buf.length, iPAddress, port);
        try{
            //  send response
            socket.send(packet);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    protected byte[] encryptMessage(String plainText, RSAPublicKey k){
        var retValue = new byte[] {-1};

        //  prepare a 32-byte for secret key
        var r = new SecureRandom();
        var b32 = new byte[32];
        r.nextBytes(b32);

        //  prepare a 16-byte for IV
        r.reseed();
        var b16 = new byte[16];
        r.nextBytes(b16);

        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(b32, "AES"), new IvParameterSpec(b16));

            //  encrypt the input message
            var enByteArray = cipher.doFinal(plainText.getBytes("UTF-8"));

            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, k);

            //  encrypt the secret key
            var encodedKey = cipher.doFinal(b32);

            //  pack all data into a byte array
            var o = new ByteArrayOutputStream();
            o.write(-2);  //  1st byte, status byte
            o.write(encodedKey);  // encrypted secret key
            o.write(b16);  //  IV
            o.write(enByteArray.length);  //  length of encrypted text
            o.write(enByteArray);  //  encrypted text

            retValue = o.toByteArray();
            o.close();
        }catch(Exception e){
            System.out.println(e);
        }

        return retValue;
    }

    protected void printEachPublicKey(){
        var s = publicKeys.keySet();
        String[] sKeys = new String[s.size()];
        s.toArray(sKeys);
        for(var index=0; index< sKeys.length; index++){
            System.out.println(sKeys[index]);
            System.out.println(sKeys[index].length());
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception{
        //  define public keys file name
        //  Reference : genKeyPairs.java
        var publicKeysFileName = "publicKeys.bin";

        //  fetch public keys from file
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(publicKeysFileName)));
        HashMap<String, RSAPublicKey> publicKeys = (HashMap<String, RSAPublicKey>)ois.readObject();
        ois.close();

        byte[] buf = new byte[256];
        int PORT = 3301;
        DatagramSocket serverSocket = new DatagramSocket(PORT);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while(true){
            serverSocket.receive(packet);
            (new UDPServer_002(serverSocket, packet, publicKeys)).start();
        }
    }
}
