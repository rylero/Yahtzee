package Client;

import java.net.*;
import java.util.*;
import java.util.function.*;

import NetworkUtils.Message;

import java.io.*;

/*
 * Socket Manager Class
 * A Thread object that listens for messages from the server and can send messages
 */
public class SocketManager extends Thread {
    private static HashMap<Integer, Consumer<String>> actionMap;
    private static ArrayList<Message> messageList; 
    private static Socket clientSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static Thread listeningThread;
    private static boolean isClosed;

    public static void initializeConnection(String ip, int port) throws IOException{
        actionMap = new HashMap<Integer, Consumer<String>>();
        messageList = new ArrayList<Message>();
        
        clientSocket = new Socket(ip, port);

        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        listeningThread = new SocketManager();
        listeningThread.start();
    }

    public static void addActionToMap(Integer cmd, Consumer<String> func) {
        actionMap.put(cmd, func);
    }

    /*
     * The run function for the thread
     * This function help the socket listen for messages
     * It also maps the messages to functions in the actionMap
     */
    @Override
    public void run() {
        while (!isClosed) {
            try {
                if (!clientSocket.isClosed()) {
                    Message msg = (Message) in.readObject();
                    Consumer<String> mapResult = actionMap.get(msg.getOpcode());
                    if (mapResult != null) {
                        mapResult.accept(msg.getData());
                    } else {
                        messageList.add(0, msg);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static Message getMessageResponse(Message msg) {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getRespondingTo().getId() == msg.getId() &&
                messageList.get(i).getRespondingTo().getOpcode() == msg.getOpcode() &&
                messageList.get(i).getRespondingTo().getData().equals(msg.getData())) {
                return messageList.get(i);
            }
        }
        return null;
    }

    public static boolean hasMessageResponse(Message msg) {
        if (messageList.size() == 0) return false;
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getRespondingTo().getId() == msg.getId() &&
                messageList.get(i).getRespondingTo().getOpcode() == msg.getOpcode() &&
                messageList.get(i).getRespondingTo().getData().equals(msg.getData())) {
                return true;
            }
        }
        return false;
    }

    public static void sendMessage(Serializable obj) {
        try {
            out.writeObject(obj);
        } catch (Exception e) {
            System.out.print("ERROR: MESSAGE FAILED TO SEND");
        }
    }

    public static void stopConnection() {
        isClosed = true;
        listeningThread.setDaemon(true);
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (Exception e) {}
    }
}
