package Client;

import java.io.IOException;
import java.util.Scanner;

import NetworkUtils.Message;

class Client {
    /*
     * Special function for clearing screen
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();  
    }

    public static void HomeScreen(Scanner inputScanner) {
        clearScreen();

        System.out.println("Welcome To Yahtzee!");
        System.out.println("You can:");
        System.out.println("1. Join Room");
        System.out.println("2. Create Room");
        System.out.println("3. Exit");

        int choice = inputScanner.nextInt();
        while (choice != 1 && choice != 2 && choice != 3) {
            System.out.println("ERROR: Choice not valid. Please enter a valid option.");
            choice = inputScanner.nextInt();
        }
        if (choice == 1) {
            JoinRoomScreen(inputScanner);
        } else if (choice == 2) {
            CreateRoomScreen(inputScanner);
        } else if (choice == 3) {
            SocketManager.sendMessage(new Message(10, "", null));
            SocketManager.stopConnection();
            System.out.println("Thanks for playing!");
        }
    }

    public static void JoinRoomScreen(Scanner inputScanner) {
        clearScreen();
        System.out.println("Enter the room that you would like to join:");
        int roomId = inputScanner.nextInt();

        Message joinRoomMessage = new Message(4, ""+roomId, null);
        SocketManager.sendMessage(joinRoomMessage);
        while (!SocketManager.hasMessageResponse(joinRoomMessage)) {

        }
        Message joinRoomResponse = SocketManager.getMessageResponse(joinRoomMessage);
        if (joinRoomResponse.getOpcode() == 6) {
            System.out.println("Join Room Failed. Exiting Program.");
            return;
        }
        RoomScreen(inputScanner,roomId);
    }

    public static void CreateRoomScreen(Scanner inputScanner) {
        clearScreen();
        System.out.println("Creating Room...");

        Message createRoomMessage = new Message(7, "", null);
        SocketManager.sendMessage(createRoomMessage);
        while (!SocketManager.hasMessageResponse(createRoomMessage)) {

        }
        Message createRoomResponse = SocketManager.getMessageResponse(createRoomMessage);
        if (createRoomResponse.getOpcode() == 9) {
            System.out.println("Create Room Failed. Exiting Program.");
            return;
        }
        RoomScreen(inputScanner,Integer.parseInt(createRoomResponse.getData()));
    }

    public static void RoomScreen(Scanner inputScanner, int roomId) {
        clearScreen();
        System.out.println("Room ID: " + roomId);
    }

    public static void main(String[] args) throws IOException{
        Scanner inputScanner = new Scanner(System.in);
        
        System.out.println("Connecting to Server....");
        SocketManager.initializeConnection("127.0.0.1", Integer.parseInt(args[0]));

        Message pingMessage = new Message(0, "", null);
        SocketManager.sendMessage(pingMessage);
        while (!SocketManager.hasMessageResponse(pingMessage)) {}

        System.out.println("Enter Username:");
        String userName = inputScanner.nextLine();
        Message userNameMessage = new Message(2, userName, null);
        SocketManager.sendMessage(userNameMessage);
        while (!SocketManager.hasMessageResponse(userNameMessage)) {}

        HomeScreen(inputScanner);
    }
}