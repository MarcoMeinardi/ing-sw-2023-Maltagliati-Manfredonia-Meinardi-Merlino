package it.polimi.ingsw;

import socket.Client;
import socket.Server;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try{
            Server server = new Server();
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        Client client = new Client();
        try {
            client.clientSocket();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }
}
