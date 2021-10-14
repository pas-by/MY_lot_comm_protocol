//   UDPClient_demo.java

import java.io.*;
import java.net.*;

public class UDPClient_demo {
    public static void main(String[] args)throws Exception{
        int PORT = 3301;
        String HOST = "localhost";
        String strData;

        System.out.println("Please enter your text:");
        BufferedReader inputLine = new BufferedReader(new InputStreamReader(System.in));

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress iPAddress = InetAddress.getByName(HOST);
        byte[] buf = new byte[256];
        strData = inputLine.readLine();
        buf = strData.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, iPAddress, PORT);
        clientSocket.send(packet);

        packet = new DatagramPacket(buf, buf.length);
        clientSocket.receive(packet);
        strData = new String(packet.getData());
        System.out.println("FROM SERVER: " + strData.toUpperCase());
        clientSocket.close();
    }
}
