/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task3;
import java.net.*;
import java.io.*;

// Initially started with EchoClientUDP.java from Coulouris text
public class RemoteVariableClientUDP {
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
            // printing menu for first time
            System.out.println("1. Add a value to your sum.\n2. Subtract a value from your sum.\n" +
                    "3. Get your sum.\n4. Exit client");
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while ((nextLine = typed.readLine()) != null) {
                byte [] m = null;
                // if user chooses add
                if(nextLine.equals("1")){
                    System.out.println("Enter value to add:");
                    nextLine = typed.readLine();
                    byte [] operation = "add,".getBytes();  // separating message contents with ','
                    byte [] value = nextLine.getBytes();
                    System.out.println("Enter your ID:");
                    nextLine = typed.readLine();
                    byte [] id = (nextLine + ',').getBytes();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(id);
                    outputStream.write(operation);
                    outputStream.write(value);
                    m = outputStream.toByteArray(); // creating byte array of entire message
                } else if (nextLine.equals("2")) {  // if user chooses subtract
                    System.out.println("Enter value to subtract:");
                    nextLine = typed.readLine();
                    byte [] operation = "subtract,".getBytes(); // separating message contents with ','
                    byte [] value = nextLine.getBytes();
                    System.out.println("Enter your ID:");
                    nextLine = typed.readLine();
                    byte [] id = (nextLine + ',').getBytes();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(id);
                    outputStream.write(operation);
                    outputStream.write(value);
                    m = outputStream.toByteArray(); // creating byte array of entire message
                } else if (nextLine.equals("3")) {  // if user chooses to get value corresponding to ID
                    System.out.println("Enter your ID:");
                    nextLine = typed.readLine();
                    byte [] operation = "get".getBytes();
                    byte [] id = (nextLine + ',').getBytes();   // separating message contents with ','
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(id);
                    outputStream.write(operation);
                    m = outputStream.toByteArray(); // creating byte array of entire message
                } else if (nextLine.equals("4")) {  // if user chooses to quit
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    break;
                }
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
                // converting the reply data from byte array to int value
                // Snippet taken from https://www.baeldung.com/java-byte-array-to-number
                int value = 0;
                for (byte b : replyData) {
                    value = (value << 8) + (b & 0xFF);
                }
                System.out.println("The result is " + value + ".\n");
                System.out.println("1. Add a value to your sum.\n2. Subtract a value from your sum.\n" +
                        "3. Get your sum.\n4. Exit client");

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