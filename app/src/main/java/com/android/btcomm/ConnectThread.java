package com.android.btcomm;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Gowtham on 01-11-2016.
 */

public class ConnectThread extends Thread {


    private final BluetoothSocket bSocket;
    private final boolean socketType;
    private final AppGlobals globals;

    public ConnectThread(BluetoothDevice bDevice, boolean isSecure, AppGlobals globals) {

        this.socketType = isSecure;
        this.globals = globals;

        UUID myUUID = UUID.fromString("494ad2cf-057f-484f-bbd0-54b5ec7b92a3");

        BluetoothSocket tmp = null;

        Log.i("ConnectThread dev check","Target Dev name  : "+bDevice.getName());
        Log.i("ConnectThread dev check","Target Dev Address  : "+bDevice.getAddress());
        Log.i("ConnectThread dev check","Target Dev Bond State  : "+bDevice.getBondState());

        try {

            if(isSecure)
                tmp = bDevice.createRfcommSocketToServiceRecord(myUUID);
            else
                tmp = bDevice.createInsecureRfcommSocketToServiceRecord(myUUID);

        } catch (IOException e) {
            e.printStackTrace();
        }

        bSocket = tmp;

    }

    @Override
    public void run() {

        try {

            bSocket.connect();



        } catch (IOException e) {
            e.printStackTrace();
        }

        connected(bSocket, socketType);

    }

    private void connected(BluetoothSocket bSocket, boolean socketType) {


        Log.i("connected", "--Socket Type-- : " + (socketType ? "Secured" : "InSecured"));

        Log.i("ConnectThread", "Connected to " + bSocket.getRemoteDevice().getName());
        globals.setTargetBTAddress(bSocket.getRemoteDevice().getAddress());

        //Connecting to Server. Which means this is a Client
        if (!globals.isServer()){
            Log.i("ConnectThread", "Connected to Server");
            globals.setTargetDevice(bSocket.getRemoteDevice());
        }

        ConnectedThread connectedThread=new ConnectedThread(bSocket,socketType, globals);
        globals.setConnectedThread(connectedThread);
        connectedThread.start();

    }
}
