//   UDPClient_001.java

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class UDPClient_001{
    protected int port = 3301;
    protected String host = "localhost";
    protected String keyPairsFileName = "keyPairs.bin";
    protected Vector<KeyPair> keyPairs;
    protected KeyPair keyPair;

    //  constructors
    public UDPClient_001(){
        init();
    }

    public UDPClient_001(String h, int p){
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
            buf = encodedString.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, iPAddress, port);
            clientSocket.send(packet);

            //  wait for return message
            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            clientSocket.receive(packet);
            strData = new String(packet.getData());
            System.out.println("FROM SERVER: " + strData);
            clientSocket.close();
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }

        return strData;
    }

    public static void main(String[] args)throws Exception{
        UDPClient_001 a = new UDPClient_001();
        System.out.println(a.getMessage());
    }
}
