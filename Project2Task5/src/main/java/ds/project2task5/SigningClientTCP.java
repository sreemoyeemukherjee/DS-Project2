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
import java.util.Random;

// Initially started with EchoClientTCP.java from Coulouris text
public class SigningClientTCP {
    static String clientID;
    // Each public and private key consists of an exponent and a modulus
    static BigInteger n; // n is the modulus for both the private and public keys
    static BigInteger e; // e is the exponent of the public key
    static BigInteger d; // d is the exponent of the private key

    public static void main(String args[]) {
        generateKeys(); // generate public and private keys of client
        setClientIDPublicKey(); // set clientID
        add(0); // remote procedure call (RPC)
    }

    // Initially started with RSAExample.java - Key generation and sample encryption and decryption
    // From project 2 GitHub: https://github.com/CMU-Heinz-95702/Project-2-Client-Server
    public static void generateKeys(){
        Random rnd = new Random();
        BigInteger p = new BigInteger(2048, 100, rnd);
        BigInteger q = new BigInteger(2048, 100, rnd);

        // Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");

        // Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);

        System.out.println("Client private key:\nd:\n" + d + "\nn:\n" + n);
        System.out.println("Client public key:\ne:\n" + e + "\nn:\n" + n);
    }

    public static void setClientIDPublicKey(){
        clientID = e.toString() + n.toString(); // concatenate e and n
        byte[] hashbytes = hash(clientID);
        byte [] lsbBytes = new byte[20];    // create clientID using the least significant 20 bytes of the hash of the client's public key.
        int index = 0;
        for (int i = hashbytes.length - 21; i < hashbytes.length - 1; i++){
            lsbBytes[index] = hashbytes[i];
            index++;
        }
        clientID  = new BigInteger(lsbBytes).toString();
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
                String m = clientID + "," + e + "," + n;
                // if user chooses add
                if(nextLine.equals("1")){
                    System.out.println("Enter value to add:");
                    String value = typed.readLine();
                    m+= ",add," + value + ",";  // separating message contents with ','
                    byte[] hashbytes = hash(m);
                    m+=encrypt(hashbytes);  // adding the signature to the message
                } else if (nextLine.equals("2")) {  // if user chooses subtract
                    System.out.println("Enter value to subtract:");
                    String value = typed.readLine();
                    m+= ",subtract," + value + ","; // separating message contents with ','
                    byte[] hashbytes = hash(m);
                    m+=encrypt(hashbytes);  // adding the signature to the message
                } else if (nextLine.equals("3")) {  // if user chooses to get value corresponding to ID
                    // extra ',' so that uniform structured message is sent to server
                    m+= ",get,,";   // separating message contents with ','
                    byte[] hashbytes = hash(m);
                    m+=encrypt(hashbytes);  // adding the signature to the message
                } else if (nextLine.equals("4")) {  // if user chooses to quit
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    m = "4";     // just send 4 for quitting
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

    public static String encrypt(byte[] message){
        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        byte[] posMessage = new byte[message.length + 1];
        posMessage[0] = 0; // most significant set to 0
        int i = 1;
        for (byte b:message){
            posMessage[i] = b;
            i++;
        }
        BigInteger m = new BigInteger(posMessage);
        // To encrypt a message M compute C = M^e (mod n)
        BigInteger c = m.modPow(d, n);
        return c.toString();
    }
}