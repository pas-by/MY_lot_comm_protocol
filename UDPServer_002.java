//  UDPServer_002.java

import java.net.*;
import java.security.interfaces.*;
import java.security.*;
import java.util.*;
import java.io.*;

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
        byte[] buf = new byte[] {-1};  //  ERROR

        //  expected to be an authorized public key
        if(publicKeys.containsKey(strData)){
            resultString = "authorized public key received";
            //  System.out.println("check point");

            buf = new byte[] {-2};  //  OK!
        }

        packet = new DatagramPacket(buf, buf.length, iPAddress, port);
        try{
            //  send response
            socket.send(packet);
        }catch(Exception e){
            System.out.println(e);
        }
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
