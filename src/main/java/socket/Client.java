package socket;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public void clientSocket() throws Exception {
        BufferedReader in = null;
        PrintStream out = null;
        Socket socket = null;
        String message;

        try {
            socket = new Socket("localhost", 4000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            message = in.readLine();
            System.out.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
