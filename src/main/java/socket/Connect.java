package socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Connect extends Thread{
    private Socket client = null;
    BufferedReader in = null;
    PrintWriter out = null;

    public Connect(Socket clientSocket){
        client = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
        } catch (Exception e1) {
            try {
                client.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            }
        this.start();
        }

        public void run(){
    try{
        out.println("Hello, you are connected!");
        out.flush();
        out.close();
        in.close();
        client.close();
    }
    catch (Exception e){
        }
    }
}