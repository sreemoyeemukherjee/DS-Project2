/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task4;
import java.net.*;
import java.io.*;

// Initially started with EchoClientTCP.java from Coulouris text
public class RemoteVariableClientTCP {
    public static void main(String args[]){
        add(0); // remote procedure call (RPC)
    }

    // proxy design
    public static int add(int i){
        System.out.println("The client is running.");
        // declaring the socket
        Socket clientSocket = null;
        try {
            System.out.println("Please enter server port: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(reader.readLine());
            // Reads the specified host and returns an IP address for the given host name.
            InetAddress aHost = InetAddress.getByName("localhost");
            // creating and connecting the socket to the server port
            clientSocket = new Socket(aHost, serverPort);
            String nextLine;
            // printing menu for first time
            System.out.println("1. Add a value to your sum.\n2. Subtract a value from your sum.\n" +
                    "3. Get your sum.\n4. Exit client");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while ((nextLine = typed.readLine()) != null) {
                String m = null;
                // if user chooses add
                if(nextLine.equals("1")){
                    System.out.println("Enter value to add:");
                    String value = typed.readLine();
                    System.out.println("Enter your ID:");
                    String id = typed.readLine();
                    m = id + ",add," + value;   // separating message contents with ','
                } else if (nextLine.equals("2")) {  // if user chooses subtract
                    System.out.println("Enter value to subtract:");
                    String value = typed.readLine();
                    System.out.println("Enter your ID:");
                    String id = typed.readLine();
                    m = id + ",subtract," + value;  // separating message contents with ','
                } else if (nextLine.equals("3")) {  // if user chooses to get value corresponding to ID
                    System.out.println("Enter your ID:");
                    String id = typed.readLine();
                    m = id + ",get,";   // separating message contents with ','
                } else if (nextLine.equals("4")) {  // if user chooses to quit
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    m = "4";    // just send 4 for quitting
                }
                out.println(m); // it writes/sends the request stream to the server port
                out.flush();
                if(m.equals("4")){
                    clientSocket.close();   // always close the socket
                    break;
                }
                String replyData = in.readLine(); // read a line of data from the stream
                System.out.println("The result is " + Integer.parseInt(replyData) + ".\n");
                System.out.println("1. Add a value to your sum.\n2. Subtract a value from your sum.\n" +
                        "3. Get your sum.\n4. Exit client");
            }

        }
        // to catch any error/exception
        // For example, if an I/O error occurs in receiving the request message
        catch (IOException e){System.out.println("IO Exception: " + e.getMessage());
        }finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();   // always close the socket
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
        return i;
    }
}