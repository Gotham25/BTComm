package com.android.btcomm;

/**
 * Created by Gowtham on 04-10-2016.
 */

public class ChatMessage {

    private String msgContent;
    private boolean isSender;
    //private boolean isInitMsg;

    private int id;
    private String fromAddr;
    private String toAddr;
    private String mode;
    private String message;
    private String date;
    private String time;
    private String timestamp;
    private boolean isOldMsg;

    public ChatMessage(String from, String to, String mode, String message, String timestamp) {
        this.fromAddr = from;
        this.toAddr = to;
        this.mode = mode;
        this.message = message;
        this.timestamp = timestamp;

        if (this.mode.toString().equals("OUTGOING"))
            this.isSender = true;
        else
            this.isSender = false;
    }

    public int getId() {
        return id;
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public String getToAddr() {
        return toAddr;
    }

    public String getMode() {
        return mode;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public ChatMessage(String message, boolean isSender, String timestamp) {

        this.message = message;
        this.isSender = isSender;
        this.timestamp = timestamp;

    }

    public String getMsgContent() {
        return msgContent;
    }

    /*
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
    */

    public boolean isSender() {
        return isSender;
    }

    public boolean isOldMsg() {
        return isOldMsg;
    }

    public void setOldMsg(boolean isOldMsg) {
        this.isOldMsg = isOldMsg;
    }

    /*
    public void setSender(boolean sender) {
        isSender = sender;
    }

    public boolean isInitMsg() {
        return isInitMsg;
    }

    public void setInitMsg(boolean initMsg) {
        isInitMsg = initMsg;
    }

    public ChatMessage(String msgContent, boolean isSender, boolean isInitMsg) {
        this.msgContent = msgContent;
        this.isSender = isSender;
        this.isInitMsg = isInitMsg;
    }
    */



}
