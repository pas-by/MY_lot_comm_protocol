//  UDPServer_001.java

import java.net.*;

public class UDPServer_001 extends Thread{
    protected int port;
    protected DatagramSocket socket;
    protected DatagramPacket packet;

    //  constructor
    public UDPServer_001(DatagramSocket s, DatagramPacket p){
        socket = s;
        packet = p;
    }

    public void run(){
        String strData = new String(packet.getData());
        InetAddress iPAddress = packet.getAddress();
        port  = packet.getPort();
        System.out.println("Datagram received: " + iPAddress + " : " + port);

        byte[] buf = strData.getBytes();
        packet = new DatagramPacket(buf, buf.length, iPAddress, port);
        try{
            //  send response
            socket.send(packet);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws Exception{
        byte[] buf = new byte[256];
        int PORT = 3301;
        DatagramSocket serverSocket = new DatagramSocket(PORT);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while(true){
            serverSocket.receive(packet);
            (new UDPServer_001(serverSocket, packet)).start();
        }
    }
}
