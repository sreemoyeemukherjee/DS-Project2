/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task4;
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// Initially started with EchoServerTCP.java from Coulouris text
public class RemoteVariableServerTCP {
    static Map<Integer, Integer> treeMap = new TreeMap<>();
    public static void main(String args[]) {
        communicate();  // remote procedure call (RPC)
    }

    // proxy design
    public static void communicate(){
        System.out.println("Server started");
        // declaring the socket
        Socket serverSocket = null;
        try {
            System.out.println("Please enter server port: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(reader.readLine());
            // try-with-resources - creates and binds the socket to the server port on the localhost
            try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
                // Create a new server socket
                serverSocket = listenSocket.accept();   // listen and accept request from client
                Scanner in = new Scanner(serverSocket.getInputStream());

                // Set up "out" to write to the client socket
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())));
                while (true) {
                    /*
                     * Block waiting for a new connection request from a client.
                     * When the request is received, "accept" it, and the rest
                     * the tcp protocol handshake will then take place, making
                     * the socket ready for reading and writing.
                     *  */
                    if (serverSocket == null) {
                        serverSocket = listenSocket.accept();   // listen and accept request from client
                        in = new Scanner(serverSocket.getInputStream());

                        // Set up "out" to write to the client socket
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())));
                    }
                    if (in.hasNext()) {
                        String requestString = in.nextLine();   // read a line of data from the stream
                        if (requestString.equals("4")) {
                            serverSocket = null;    // set server socket to null if client quits
                            continue;
                        }
                        // to perform the requested operation on the mentioned id
                        BigInteger resultBigInteger = BigInteger.valueOf(operate(requestString));
                        System.out.println("Returning result of " + resultBigInteger + " to client");
                        out.println(resultBigInteger);  // it writes/sends the reply stream back to the client
                        out.flush();
                    }
                }
            }
            // to catch any error/exception
            // For example, if an I/O error occurs in receiving the request message
            catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } finally {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();   // always close the socket
                    }
                } catch (IOException e) {
                    // ignore exception on close
                }
            }
        }
        catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    // to perform the requested operation on the mentioned id
    public static int operate(String requestString){
        Integer value = 0;
        Integer id = Integer.parseInt(requestString.split(",")[0]); // reading the id
        String operate = requestString.split(",")[1];    // reading the requested operation name
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