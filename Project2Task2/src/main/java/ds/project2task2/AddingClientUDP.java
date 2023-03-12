/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task2;
import java.net.*;
import java.io.*;

// Initially started with EchoClientUDP.java from Coulouris text
public class AddingClientUDP {
    public static void main(String args[]){
        add(0); // remote procedure call (RPC)
    }

    // proxy design
    public static int add(int i){
        System.out.println("The client is running.");
        // declaring the socket
        DatagramSocket aSocket = null;
        try {
            System.out.println("Please enter server port: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(reader.readLine());
            // Reads the specified host and returns an IP address for the given host name.
            InetAddress aHost = InetAddress.getByName("localhost");
            // creating the socket
            aSocket = new DatagramSocket();
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while ((nextLine = typed.readLine()) != null) {
                byte [] m = nextLine.getBytes();
                // DatagramPacket to send the request to the server
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
                aSocket.send(request); // it connects and writes/sends the request to the server port
                byte[] buffer = new byte[1000];
                // DatagramPacket to receive the reply from the server
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply); // receives/reads the reply
                byte[] replyData = new byte[reply.getLength()];
                // reply data is copied to an array with the correct number of bytes to properly handle the size of the response.
                System.arraycopy(reply.getData(), reply.getOffset(), replyData, 0, reply.getLength());
                // stop the client if reply is 'halt!'
                if(new String(replyData).equals("halt!")){
                    System.out.println("Client side quitting");
                    break;
                }
                // converting the reply data from byte array to int value
                // Snippet taken from https://www.baeldung.com/java-byte-array-to-number
                int value = 0;
                for (byte b : replyData) {
                    value = (value << 8) + (b & 0xFF);
                }
                System.out.println("The server returned " + value + ".");
            }

        }
        // to catch any error/exception
        // if the socket could not be opened, or the socket could not be bound
        catch (SocketException e) {System.out.println("Socket Exception: " + e.getMessage());
        }
        // to catch any error/exception
        // For example, if an I/O error occurs in receiving the request message
        catch (IOException e){System.out.println("IO Exception: " + e.getMessage());
        }finally {
            if(aSocket != null) aSocket.close(); // always close the socket
        }
        return i;
    }
}