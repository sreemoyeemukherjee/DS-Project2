/**Name: Sreemoyee Mukherjee
 * Andrew ID: sreemoym
 * Email ID: sreemoym@andrew.cmu.edu
 * Project#: 2*/
package ds.project2task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class EavesdropperUDP {
    public static void main(String args[]) {
        // declaring the socket
        DatagramSocket aSocket = null, bSocket = null;
        // storing requests from the client
        byte[] requestBuffer = new byte[1000];
        try {
            InetAddress aHost = InetAddress.getByName("localhost");
            System.out.println("Enter the port number that the Eavesdropper is supposed to listen on: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int listenAtPort = Integer.parseInt(reader.readLine());
            System.out.println("Enter the port number of the server that the Eavesdropper is masquerading as: ");
            int serverPort = Integer.parseInt(reader.readLine());
            // initialising the socket. It creates, binds and starts listening on the entered port on the localhost
            // aSocket is for acting as server to the client
            aSocket = new DatagramSocket(listenAtPort);
            // bSocket is for acting as client to the server
            bSocket = new DatagramSocket();
            // DatagramPacket to receive the original request from the client
            DatagramPacket origRequest = new DatagramPacket(requestBuffer, requestBuffer.length);
            while (true) {
                aSocket.receive(origRequest); // accepts the request
                byte[] origRequestData = new byte[origRequest.getLength()];
                // request data is copied to an array with the correct number of bytes to properly handle the size of the response.
                System.arraycopy(origRequest.getData(), origRequest.getOffset(), origRequestData, 0, origRequest.getLength());
                // DatagramPacket to send the malicious request to the server
                DatagramPacket malRequest = new DatagramPacket(requestBuffer, requestBuffer.length, aHost, serverPort);
                // converting the DatagramPacket byte array to a String message
                String requestString = new String(origRequestData);
                // unless client sends 'halt!' adding a '!' to each text sent by the client
                if (!requestString.equals("halt!")) {
                    requestString += '!';
                }
                malRequest.setData(requestString.getBytes());
                malRequest.setLength(requestString.getBytes().length);
                bSocket.send(malRequest); // it connects and writes/sends the malicious request to the server port
                System.out.println("Echoing: " + requestString);
                // storing replies from the server
                byte[] replyBuffer = new byte[1000];
                // DatagramPacket to receive the reply from the server
                DatagramPacket origReply = new DatagramPacket(replyBuffer, replyBuffer.length);
                bSocket.receive(origReply); // receives/reads the reply
                byte[] origReplyData = new byte[origReply.getLength()];
                System.arraycopy(origReply.getData(), origReply.getOffset(), origReplyData, 0, origReply.getLength());
                // converts the byte array reply to String message
                String replyString = new String(origReplyData);
                // removing the extra added '!' so that client is not suspicious unless the message is 'halt!', then sends as it is in that case
                if (!requestString.equals("halt!")) {
                    replyString = replyString.substring(0, replyString.length() - 1);
                }
                System.out.println("Reply from server: " + replyString);
                // DatagramPacket to send the tampered reply to the client
                DatagramPacket malReply = new DatagramPacket(replyBuffer, replyBuffer.length);
                malReply.setData(replyString.getBytes());
                malReply.setLength(replyString.getBytes().length);
                malReply.setAddress(origRequest.getAddress());
                malReply.setPort(origRequest.getPort());
                // sends/writes the DatagramPacket reply back to the client
                aSocket.send(malReply);
            }
        }
        // to catch any error/exception
        // For example, if the socket could not be opened, or the socket could not bind to the specified local port.
        catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }
        // to catch any error/exception
        // For example, if an I/O error occurs in receiving the request message
        catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();  // always close the socket
            bSocket.close();    // always close the socket
        }
    }
}
