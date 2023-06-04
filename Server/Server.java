package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;

class Server {
    public static int port;

    public static void main(String[] args) throws IOException{
        port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        Logging.log(Level.INFO, "Server has been initialized");


        ArrayList<ArrayList<ClientHandler>> roomGroups = new ArrayList<ArrayList<ClientHandler>>();
        roomGroups.add(new ArrayList<ClientHandler>());

        while (true)
        {
            Socket clientSocket = null;
            
            try 
            {
                // socket object to receive incoming client requests
                clientSocket = serverSocket.accept();
                
                // obtaining input and out streams
                ObjectOutputStream dos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream dis = new ObjectInputStream(clientSocket.getInputStream());

                // create a new thread object
                ClientHandler t = new ClientHandler(clientSocket, dis, dos, roomGroups);
                roomGroups.get(0).add(t);
                
                Logging.log(Level.INFO, "|"+clientSocket.getInetAddress().getHostAddress()+"| CLIENT CONNECTED");

                // Invoking the start() method
                t.start();
                
            }
            catch (Exception e){
                Logging.log(Level.SEVERE, "|"+clientSocket.getInetAddress().getHostAddress()+"| CLIENT SOCKET FAILED TO INITIALIZE");
                clientSocket.close();
                e.printStackTrace();
            }
        }
    }
}