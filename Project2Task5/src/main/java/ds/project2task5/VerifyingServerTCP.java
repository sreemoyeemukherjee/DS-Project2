/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task5;
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// Initially started with EchoServerTCP.java from Coulouris text
public class VerifyingServerTCP {
    static BigInteger clientID;
    static Map<BigInteger, Integer> treeMap = new TreeMap<>();
    public static void main(String args[]) {
        serverCommunicate();    // remote procedure call (RPC)
    }

    // proxy design
    public static void serverCommunicate(){
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
                        BigInteger resultBigInteger = null;
                        // verifying the digital signature
                        if(verify(requestString)){
                            System.out.println("Signature verified!");
                            // to perform the requested operation on the client value
                            resultBigInteger = BigInteger.valueOf(operate(requestString));
                            System.out.println("Returning result of " + resultBigInteger + " to client");
                        }
                        else
                            System.out.println("Error in request"); // signature could not be verified
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
    public static boolean verify(String requestString){
        boolean flag1 = false, flag2 = false;
        clientID = new BigInteger(requestString.split(",")[0]);
        BigInteger e = new BigInteger(requestString.split(",")[1]);
        BigInteger n = new BigInteger(requestString.split(",")[2]);
        System.out.println("Visitor/Client public key:\ne:\n" + e + "\nn:\n" + n);
        String encryptedMessage = requestString.split(",")[5];
        String temp = e.toString() + n.toString();
        byte[] hashbytes = hash(temp);
        byte [] lsbBytes = new byte[20];    // create clientID using the least significant 20 bytes of the hash of the client's public key.
        int index = 0;
        for (int i = hashbytes.length - 21; i < hashbytes.length - 1; i++){
            lsbBytes[index] = hashbytes[i];
            index++;
        }
        temp  = new BigInteger(lsbBytes).toString();
        // check if clientID matches our computed ID
        if(temp.equals(clientID.toString()))
            flag1 = true;
        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedMessage);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);
        String messageToCheck = "";
        for(int i=0; i<requestString.split(",").length-1; i++){
            messageToCheck+=requestString.split(",")[i] + ",";  // adding ',' as split() removes it
        }
        hashbytes = hash(messageToCheck);
        byte[] posMessage = new byte[hashbytes.length + 1];
        posMessage[0] = 0; // most significant set to 0 just like done during encryption
        int i = 1;
        for (byte b:hashbytes){
            posMessage[i] = b;
            i++;
        }
        BigInteger messageBigInt = new BigInteger(posMessage);
        // check if hash of message matches
        if(decryptedHash.equals(messageBigInt))
            flag2 = true;
        // only if both are true, then signature is verified
        if(flag1 && flag2)
            return true;
        return false;
    }

    // returns SHA-256 hash of the input String message
    public static byte[] hash(String message){
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        final byte[] hashbytes = digest.digest(
                message.getBytes(StandardCharsets.UTF_8));
        return hashbytes;
    }

    // to perform the requested operation on the client value
    public static int operate(String requestString){
        Integer value = 0;
        String operate = requestString.split(",")[3];   // reading the requested operation name
        System.out.println("Visitor/Client ID: "+ clientID + " Operation requested: "+ operate);
        if (operate.equals("add")){
            value = Integer.parseInt(requestString.split(",")[4]);  // reading the operand
            value = treeMap.getOrDefault(clientID, 0) + value;  // default value is 0 or add to current value
            treeMap.put(clientID, value);
        }
        else if (operate.equals("subtract")){
            value = Integer.parseInt(requestString.split(",")[4]);  // reading the operand
            value = treeMap.getOrDefault(clientID, 0) - value;  // default value is 0 or subtract from current value
            treeMap.put(clientID, value);
        }
        return treeMap.getOrDefault(clientID, 0);   // default value is 0 or return current value
    }
}