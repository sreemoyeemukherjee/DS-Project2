/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task3;
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

// Initially started with EchoServerUDP.java from Coulouris text
public class RemoteVariableServerUDP {
    static Map<Integer, Integer> treeMap = new TreeMap<>();
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
                // to perform the requested operation on the mentioned id
                BigInteger resultBigInteger = BigInteger.valueOf(operate(requestString));
                request.setData(resultBigInteger.toByteArray());
                request.setLength(resultBigInteger.toByteArray().length);
                System.out.println("Returning result of " + resultBigInteger + " to client");
                // DatagramPacket to send the reply to the client
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                // sends/writes the DatagramPacket reply back to the client
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

    // to perform the requested operation on the mentioned id
    public static int operate(String requestString){
        Integer value = 0;
        Integer id = Integer.parseInt(requestString.split(",")[0]); // reading the id
        String operate = requestString.split(",")[1];   // reading the requested operation name
        System.out.println("Visitor/Client ID: "+ id + " Operation requested: "+ operate);
        if (operate.equals("add")){
            value = Integer.parseInt(requestString.split(",")[2]);  // reading the operand
            value = treeMap.getOrDefault(id, 0) + value;    // default value is 0 or add to current value
            treeMap.put(id, value);
        }
        else if (operate.equals("subtract")){
            value = Integer.parseInt(requestString.split(",")[2]);  // reading the operand
            value = treeMap.getOrDefault(id, 0) - value;    // default value is 0 or subtract from current value
            treeMap.put(id, value);
        }
        return treeMap.getOrDefault(id, 0); // default value is 0 or return current value
    }
}