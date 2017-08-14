package com.android.btcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Gowtham on 01-11-2016.
 */

public class AcceptThread extends Thread {


    private final AppGlobals globals;
    private final boolean socketType;
    private BluetoothServerSocket bServerSocket;
    private BluetoothAdapter bAdapter;
    private BluetoothSocket bSocket;

    public AcceptThread(BluetoothAdapter bAdapter, boolean isSecured, AppGlobals globals) {

        this.bAdapter = bAdapter;
        this.socketType = isSecured;
        this.globals = globals;

        BluetoothServerSocket temp = null;

        try {

            UUID myUUID = UUID.fromString("494ad2cf-057f-484f-bbd0-54b5ec7b92a3");

            if(isSecured)
                temp = bAdapter.listenUsingRfcommWithServiceRecord("BTComm_Secured", myUUID);
            else
                temp = bAdapter.listenUsingInsecureRfcommWithServiceRecord("BTComm_InSecured", myUUID);

        } catch (IOException e) {
            e.printStackTrace();
        }

        bServerSocket = temp;

    }

    @Override
    public void run() {

        try {

            while (true){

                Log.i("Accept Thread","Waiting for connection.......");
                bSocket = bServerSocket.accept();

                //Accepting the connection from the client. Which means this is a Server
                if(globals.isServer()) {
                    Log.i("AcceptThread", "Accepted client connection");
                    globals.setTargetDevice(bSocket.getRemoteDevice());
                    globals.setTargetBTAddress(bSocket.getRemoteDevice().getAddress());
                }

                Log.i("Accept Thread", "Connected to device : "+bSocket.getRemoteDevice().getName());
                Log.i("Accept Thread", "Accepted connection");

                ConnectedThread connectedThread=new ConnectedThread(bSocket, socketType, globals);
                globals.setConnectedThread(connectedThread);
                connectedThread.start();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
