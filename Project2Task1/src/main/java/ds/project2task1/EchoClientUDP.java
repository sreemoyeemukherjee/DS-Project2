/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task1;
import java.net.*;
import java.io.*;

// Initially started with EchoClientUDP.java from Coulouris text
public class EchoClientUDP{
    public static void main(String args[]){
        System.out.println("The UDP client is running.");
        // declaring the socket
        DatagramSocket aSocket = null;
        try {
            System.out.println("Enter the server side port number for the client to connect to: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // we are connecting to the entered server port
            int serverPort = Integer.parseInt(reader.readLine());
            // Reads the specified host and returns an IP address for the given host name.
            InetAddress aHost = InetAddress.getByName("localhost"); // Changed the client's "arg[0]" to a hardcoded "localhost"
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
                // converts the byte array reply to String for printing
                String replyString = new String(replyData);
                System.out.println("Reply from server: " + replyString);
                if(replyString.equals("halt!")){
                    System.out.println("UDP Client side quitting");
                    break;
                }
            }

        }
        // to catch any error/exception that occurs in line 18.
        // if the socket could not be opened, or the socket could not be bound
        catch (SocketException e) {System.out.println("Socket Exception: " + e.getMessage());
        }
        // to catch any error/exception that occurs in line 21.
        // For example, if an I/O error occurs in receiving the request message
        catch (IOException e){System.out.println("IO Exception: " + e.getMessage());
        }finally {
            if(aSocket != null) aSocket.close(); // always close the socket
        }
    }
}