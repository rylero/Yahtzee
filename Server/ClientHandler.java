package Server;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.ArrayList;

import NetworkUtils.Message;

class ClientHandler extends Thread {
    final ObjectInputStream in;
    final ObjectOutputStream out;
    final Socket socket;
    private String username;

    ArrayList<ArrayList<ClientHandler>> roomGroups;
    int roomIndex;
      
    // Constructor
    public ClientHandler(Socket s, ObjectInputStream in, ObjectOutputStream out,  ArrayList<ArrayList<ClientHandler>> _roomGroups) 
    {
        this.socket = s;
        this.in = in;
        this.out = out;
        roomGroups = _roomGroups;
        roomIndex = 0;
    }

    public void sendMessageToClient(Message msg){
        try {out.writeObject(msg);}catch(Exception e) {}
    }

    public void broadcast(int _roomIndex, Message msg) {
        for (ClientHandler c : roomGroups.get(_roomIndex)) {
            if (c == this) {
                continue;
            }
            c.sendMessageToClient(msg);
        }
    }

    public boolean joinRoom(int newRoom) {
        if (newRoom == roomIndex) {
            return true;
        }
        if (newRoom > roomGroups.size()-1) {return false;}
        roomGroups.get(roomIndex).remove(roomGroups.get(roomIndex).indexOf(this));
        roomGroups.get(newRoom).add(this);
        roomIndex = newRoom;

        return true;
    }

    @Override
    public void run() 
    {
        while (true) {
            try {
                Message msg = (Message) in.readObject();
                Logging.log(Level.INFO, "|"+ socket.getInetAddress().getHostAddress()+"| RECIVED MSG ("+msg.toString()+")");
                if (msg.getOpcode() == 10) {
                    socket.close();
                    roomGroups.get(roomIndex).remove(roomGroups.get(roomIndex).indexOf(this));
                    return;
                }
                
                if (msg.getOpcode() == 0) {
                    out.writeObject(new Message(1, msg.getData(), msg));
                }
                if (msg.getOpcode() == 2) {
                    username = msg.getData();
                    out.writeObject(new Message(3, msg.getData(), msg));
                }

                if (msg.getOpcode() == 4) {
                    int id = Integer.parseInt(msg.getData());
                    if (!joinRoom(id)) {
                        out.writeObject(new Message(6, ""+id, msg));
                    }
                    out.writeObject(new Message(5, ""+id, msg));
                }
                if (msg.getOpcode() == 7) {
                    roomGroups.add(new ArrayList<ClientHandler>());
                    int id = roomGroups.size()-1;
                    if (!joinRoom(id)) {
                        out.writeObject(new Message(9, ""+id, msg));
                        roomGroups.remove(id);
                    }
                    out.writeObject(new Message(8, ""+id, msg));
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}