package NetworkUtils;

import java.io.Serializable;
/*
 * OPCODES
 * 0 - PING
 * 1 - PONG
 * 2 - SET USERNAME
 * 3 - USERNAME SET
 * 4 - JOIN ROOM
 * 5 - JOIN ROOM SUCCESS
 * 6 - JOIN ROOM FAILURE
 * 7 - CREATE ROOM
 * 8 - CREATE ROOM SUCCESS
 * 9 - CREATE ROOM FAILURE
 * 10 - QUIT
 */
public class Message implements Serializable {
    private int opCode;
    private String data;
    
    private int id;
    private Message respondingTo;

    public Message(int _opCode, String _data, Message respondingTo) {
        id = (short) (Math.random() * 65535);
        opCode = _opCode;
        data = _data;
        this.respondingTo = respondingTo;
    }

    public int getOpcode() {
        return opCode;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }
    public Message getRespondingTo(){
        return respondingTo;
    }

    public String toString() {
        return "Command = " + getOpcode() + " ; Data = " + getData() + " ; ID = " + getId();
    }
}