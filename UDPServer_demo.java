//  UDPServer_demo.java

import java.net.*;

public class UDPServer_demo {
    public static void main(String[] args) throws Exception{
        byte[] buf = new byte[256];
        String strData;
        int PORT = 3301;
        DatagramSocket serverSocket = new DatagramSocket(PORT);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        serverSocket.receive(packet);
        strData = new String(packet.getData());
        InetAddress iPAddress = packet.getAddress();
        PORT = packet.getPort();
        System.out.println("Datagram received: " + iPAddress + " : " + PORT);

        buf = strData.getBytes();
        packet = new DatagramPacket(buf, buf.length, iPAddress, PORT);
        serverSocket.send(packet);
        serverSocket.close();
    }
}
