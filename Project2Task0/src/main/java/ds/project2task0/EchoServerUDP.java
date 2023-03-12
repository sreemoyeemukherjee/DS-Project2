/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task0;
import java.net.*;
import java.io.*;

// Initially started with EchoServerUDP.java from Coulouris text
public class EchoServerUDP{
    public static void main(String args[]){
        System.out.println("The UDP server is running.");
        // declaring the socket
        DatagramSocket aSocket = null;
        byte[] buffer = new byte[1000];
        try{
            System.out.println("Enter the port number that the server is supposed to listen on: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int enteredPort = Integer.parseInt(reader.readLine());
            // initialising the socket. It creates, binds and starts listening on the localhost on port 6789
            //aSocket = new DatagramSocket(6789);
            // initialising the socket. It creates, binds and starts listening on the localhost on the entered port
            aSocket = new DatagramSocket(enteredPort);
            // DatagramPacket to receive the request from the client
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while(true){
                aSocket.receive(request); // accepts and reads the request
                byte[] requestData = new byte[request.getLength()];
                // request data is copied to an array with the correct number of bytes to properly handle the size of the response.
                System.arraycopy(request.getData(), request.getOffset(), requestData, 0, request.getLength());
                // DatagramPacket to send the reply to the client
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                // converting the DatagramPacket byte array to a String message
                String requestString = new String(requestData);
                System.out.println("Echoing: "+requestString);
                // sends/writes the DatagramPacket reply back to the client
                aSocket.send(reply);
                // stop the server if client sends 'halt!'
                if(requestString.equals("halt!")){
                    System.out.println("UDP Server side quitting");
                    break;
                }
            }
        }
        // to catch any error/exception that occurs in line 23.
        // For example, if the socket could not be opened, or the socket could not bind to the specified local port.
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }
        // to catch any error/exception that occurs in reading text
        // entered by the user or in sending/receiving the packet.
        // For example, if an I/O error occurs in receiving the request message
        catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {
            if(aSocket != null)
                aSocket.close();  // always close the socket
        }
    }
}