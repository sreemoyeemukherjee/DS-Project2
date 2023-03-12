/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task2;
import java.math.BigInteger;
import java.net.*;
import java.io.*;

// Initially started with EchoServerUDP.java from Coulouris text
public class AddingServerUDP {
    static int sum = 0;
    public static void main(String args[]){
        communicate();  // remote procedure call (RPC)
    }

    // proxy design
    public static void communicate(){
        System.out.println("Server started");
        // declaring the socket
        DatagramSocket aSocket = null;
        byte[] buffer = new byte[1000];
        try{
            System.out.println("Enter the port number that the server is supposed to listen on: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int enteredPort = Integer.parseInt(reader.readLine());
            // initialising the socket. It creates, binds and starts listening on the localhost on the entered port
            aSocket = new DatagramSocket(enteredPort);
            while(true){
                // DatagramPacket to receive the request from the client
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request); // accepts the request
                byte[] requestData = new byte[request.getLength()];
                // request data is copied to an array with the correct number of bytes to properly handle the size of the response.
                System.arraycopy(request.getData(), request.getOffset(), requestData, 0, request.getLength());
                // converting the DatagramPacket byte array to a String message
                String requestString = new String(requestData);
                // stop the server if client sends 'halt!'
                if(requestString.equals("halt!")) {
                    DatagramPacket reply = new DatagramPacket(request.getData(),
                            request.getLength(), request.getAddress(), request.getPort());
                    aSocket.send(reply); // send reply to client to halt it
                    continue; // for halt don't do below steps
                }
                int i;
                try {
                    i = Integer.parseInt(requestString);
                    System.out.println("Adding: "+ i + " to " + sum);
                    add(i);
                    BigInteger sumBigInteger = BigInteger.valueOf(sum);
                    request.setData(sumBigInteger.toByteArray());
                    request.setLength(sumBigInteger.toByteArray().length);
                    // sends/writes the DatagramPacket reply back to the client
                    System.out.println("Returning sum of " + sum + " to client");
                }
                catch (NumberFormatException e) {
                    continue;
                }
                // DatagramPacket to send the reply to the client
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        }
        // to catch any error/exception
        // For example, if the socket could not be opened, or the socket could not bind to the specified local port.
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }
        // to catch any error/exception
        // For example, if an I/O error occurs in receiving the request message
        catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {
            if(aSocket != null)
                aSocket.close();  // always close the socket
        }
    }
    // to add i to sum
    public static int add(int i){
        sum+=i;
        return sum;
    }
}