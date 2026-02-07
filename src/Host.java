import java.io.*;
import java.net.*;

/**
 * The Intermediate Host class that is the bridge between the Client and the Server:
 * - Main responsibility is to forward UDP packets between the Client and Server
 *
 * @author Amreen Shahid
 * @version 1.0 February 7th, 2026
 */
public class Host {

    DatagramPacket receivePacket;
    DatagramSocket sendSocket, receiveSocket;

    /**
     * Constructs the Host:
     * - Creates a UDP socket on port 5000 to receive from the Client
     * - Creates another UDP socket to send to the Servers port
     */
    public Host()
    {
        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(5000);
            System.out.println("Battle Royale Host Started on port 5000\n");

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Handles all the Host logic:
     * - Receives packet from the Client (1)
     * - Forwards the bytes to the Server (2)
     * - Receives response from the Server (3)
     * - Send response back to the Client (4)
     */
    public void receiveAndRespond()
    {
        while(true) {
            // ----------- (1) Receives packet from the Client ---------------
            byte clientData[] = new byte[1000];
            receivePacket = new DatagramPacket(clientData, clientData.length);

            try {
                receiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //Display receiving information
            System.out.println("Host: received:");
            System.out.println("From client: " + receivePacket.getAddress());
            System.out.println("Host client port: " + receivePacket.getPort());
            int len = receivePacket.getLength();
            System.out.println("Length: " + len);
            String received = new String(clientData, 0, len);
            System.out.println("Containing: " + received + "\n");

            //Saves address and port
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            // ------------- (2) Forward the bytes to the Server --------------
            DatagramPacket sendPacket = null;
            try {
                sendPacket = new DatagramPacket(receivePacket.getData(),receivePacket.getLength(), InetAddress.getLocalHost(), 6000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // Display forwarding information
            System.out.println("Host: forwarded:");
            System.out.println("To server: " + sendPacket.getAddress());
            System.out.println("To server port: " + sendPacket.getPort());
            System.out.println("Length: " + sendPacket.getLength());
            System.out.print("Containing: " + received + "\n");

            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //--------------- (3) Receive response from the Server --------------
            byte[] serverData = new byte[1000];
            DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length);

            try {
                sendSocket.receive(serverPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            String serverResponse = new String(serverData, 0, serverPacket.getLength());

            //Displays response information
            System.out.println("Host: received:");
            System.out.println("From server: " + serverPacket.getAddress());
            System.out.println("From server port: " + serverPacket.getPort());
            System.out.println("Length: " + serverPacket.getLength());
            System.out.println("Containing: " + serverResponse + "\n");

            //--------------- (4) Send response back to the Client --------------
            sendPacket = new DatagramPacket(
                    serverPacket.getData(),
                    serverPacket.getLength(),
                    clientAddress,
                    clientPort);

            //Displays sending information
            System.out.println("Host: sending response: ");
            System.out.println("To client: " + sendPacket.getAddress());
            System.out.println("To client port: " + sendPacket.getPort());
            System.out.println("Length: " + sendPacket.getLength());
            System.out.println("Containing: " + serverResponse + "\n");

            try {
                receiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main( String args[] ) {
        Host host = new Host();
        host.receiveAndRespond();
    }
}

