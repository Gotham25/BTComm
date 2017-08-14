package com.android.btcomm;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Gowtham on 18-12-2016.
 */

public class AppGlobals extends Application {


    public static final int MSG_READ = 101;
    public static final int MSG_WRITE = 010;
    private BluetoothDevice targetDevice;
    private boolean isServer = true;
    private ConnectedThread connectedThread;
    private Handler chatHandler;
    private String clientName;
    private String serverName;
    private String localBTAddress;
    private String targetBTAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    public String getTargetBTAddress() {
        return targetBTAddress;
    }

    public void setTargetBTAddress(String targetBTAddress) {
        this.targetBTAddress = targetBTAddress;
    }


    public String getLocalBTAddress() {
        return localBTAddress;
    }

    public void setLocalBTAddress(String localBTAddress) {
        this.localBTAddress = localBTAddress;
    }

    public Handler getChatHandler() {
        Log.i("getChatHandler", "Check if ChatHandler is Null : "+chatHandler==null?"Null":"Not null");
        return chatHandler;
    }

    public void setChatHandler(Handler chatHandler) {
        this.chatHandler = chatHandler;
        Log.i("setChatHandler", "Chat Handler is set");
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public BluetoothDevice getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(BluetoothDevice targetDevice) {
        this.targetDevice = targetDevice;

        Log.i("AppGlobals", "target Device set for the "+(isServer()?"Server":"Client"));

    }


}
