import java.io.*;
import java.net.*;

/**
 * The Server class communicated strictly with the Host using UDP packets:
 * - Receives requests from the Host
 * - Updates the game
 *
 * @author Amreen Shahid
 * @version 1.0 February 7th, 2026
 */
public class Server {
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket sendSocket, receiveSocket;
    GameState gameState;

    /**
     * Constructs the Server:
     * - Creates a UDP socket on port 6000 to receive from the Host.
     * - Creates another UDP socket to send to the Host port
     */
    public Server()
    {
        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(6000);
            gameState = new GameState();
            System.out.println("Battle Royale Server Started on port 6000\n");

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Handles all the Server logic:
     * - Receives request from the Host (1)
     * - Updates message (2)
     * - Sends message response back to the Host (3)
     */
    public void receiveAndRespond()
    {
        while(true) {
            // ----------- (1) Receives packet from the Host ---------------
            byte data[] = new byte[1000];
            receivePacket = new DatagramPacket(data, data.length);

            try {
                receiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //Displays received information
            System.out.println("Server: received:");
            System.out.println("From host: " + receivePacket.getAddress());
            System.out.println("From host port: " + receivePacket.getPort());
            int len = receivePacket.getLength();
            System.out.println("Length: " + len);
            String received = new String(data, 0, len);
            System.out.print("Containing: " + received + "\n");

            // ----------- (2) Updates message ---------------
            String response = "";
            String[] comp = received.split(":");

            if(comp[0].equals("JOIN")) {
                Player player = gameState.addNewPlayer(comp[1]);
                response = "JOINED:" + player.getId();
            }

            else if(comp[0].equals("MOVE")) {
                gameState.movePlayer(Integer.parseInt(comp[1]), Integer.parseInt(comp[2]), Integer.parseInt(comp[3]));
                response = "MOVE_OK";
            }

            else if(comp[0].equals("PICKUP")) {
                if (gameState.processPickup(Integer.parseInt(comp[1]),Integer.parseInt(comp[2]))) {
                    response = "PICKUP_OK";
                } else {
                    response = "PICKUP_FAIL";
                }
            }

            else if(comp[0].equals("STATE")) {
                response = gameState.serialize();
            }

            else if (comp[0].equals("QUIT")) {
                response = "Quitting...";
            }

            // ----------- (3) Sends message response back to the Host ---------------
            byte[] reply = response.getBytes();
            sendPacket = new DatagramPacket(reply, reply.length,
                    receivePacket.getAddress(), receivePacket.getPort());

            //Displays sending information
            System.out.println("\nServer: sent:");
            System.out.println("To host: " + sendPacket.getAddress());
            System.out.println("To host port: " + sendPacket.getPort());
            len = sendPacket.getLength();
            System.out.println("Length: " + len);
            System.out.print("Containing: " + response + "\n");

            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main( String args[] )
    {
        Server server = new Server();
        server.receiveAndRespond();
    }
}

