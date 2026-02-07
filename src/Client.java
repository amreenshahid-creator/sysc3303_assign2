import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * The Client class communicated strictly with the Host using UDP packets:
 * - Allows users to join the game
 * - Sends commands (JOIN, MOVE, PICKUP, STATE, QUIT) and responds accordingly
 *
 * @author Amreen Shahid
 * @version 1.0 February 7th, 2026
 */
public class Client {
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket socket;

    /**
     * Constructs the Client:
     * - Creates UDP socket bound to any ephemeral port.
     */
    public Client() {
        try {
            socket = new DatagramSocket();
            System.out.println("Client started. Socket on random port.");
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Starts the Client gameplay interaction process:
     * - Allows users to join the game
     * - Asks for commands via user input
     */
    public void start() {
        Scanner scanner = new Scanner(System.in); //takes input

        //--------------- Connect and Join ------------------
        System.out.print("Enter your player name: ");
        String playerName = scanner.nextLine();

        send("JOIN:" + playerName);
        String reply = receive();
        int playerID = Integer.parseInt(reply.split(":") [1]);
        System.out.println("Joined game with playerID = " + playerID);

        //--------------- Gameplay Loop ---------------------
        while(true) {
            System.out.println("Commands: MOVE dx dy | PICKUP lootID | STATE | QUIT");
            String cmd = scanner.nextLine();
            String msg = "";

            if (cmd.startsWith("MOVE")) {
                String[] comp = cmd.split(" ");  //Split the command to extract the coordinates
                msg = "MOVE:" + playerID + ":" + comp[1] + ":" + comp[2];
            }

            else if(cmd.startsWith("PICKUP")) {
                String[] comp = cmd.split(" ");   //Split the command to extract the lootID
                msg = "PICKUP:" + playerID + ":" + comp[1];
            }

            else if(cmd.equals("STATE")) {
                msg = "STATE";
            }

            else if(cmd.equals("QUIT")) {
                System.out.println("Client closed.");
                socket.close();
                break;
            }

            //Send message request to host and process a response
            send(msg);
            String response = receive();

            if(response.startsWith("PLAYERS")) {
                System.out.println("Game State: " + response);
            } else {
                System.out.println("Server: " + response);
            }
        }
        //--------------------------------------------------
    }

    /**
     * Sends client message to the Host
     * @param msg the message to be sent.
     */
    public void send(String msg) {
        byte[] data = msg.getBytes();

        try {
            sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 5000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Displays information regarding message
        System.out.println("\nClient: sent:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("To host port: " + sendPacket.getPort());
        System.out.println("Length: " + sendPacket.getLength());
        System.out.println("Containing: " + msg + "\n");

        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Receives response from the Host.
     * @return the response from the Host.
     */
    public String receive() {
        byte[] data = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            socket.receive(receivePacket);
        }catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String response = new String(data, 0, receivePacket.getLength());

        //Displays information regarding response from host
        System.out.println("Client: received:");
        System.out.println("From Host: " + receivePacket.getAddress());
        System.out.println("From Host Port: " + receivePacket.getPort());
        System.out.println("Length: " + receivePacket.getLength());
        System.out.println("Containing: " + response + "\n");
        return response;
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.start();
    }
}