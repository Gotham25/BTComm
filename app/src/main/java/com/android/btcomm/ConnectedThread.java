package com.android.btcomm;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Gowtham on 18-12-2016.
 */

public class ConnectedThread extends Thread{


    private final BluetoothSocket bSocket;
    private final boolean socketType;
    private final InputStream iStream;
    private final OutputStream oStream;
    private final AppGlobals globals;

    public AppGlobals getGlobals() {
        return globals;
    }



    public BluetoothSocket getbSocket() {
        return bSocket;
    }

    public boolean isSocketType() {
        return socketType;
    }

    public InputStream getiStream() {
        return iStream;
    }

    public OutputStream getoStream() {
        return oStream;
    }

    public Handler getBTChatHandler() {
        return BTChatHandler;
    }

    private final Handler BTChatHandler;

    public ConnectedThread(BluetoothSocket bSocket, boolean socketType, AppGlobals globals) {

        //

        this.bSocket = bSocket;
        this.socketType = socketType;
        this.BTChatHandler = globals.getChatHandler();
        this.globals = globals;

        InputStream tmpInputStream = null;
        OutputStream tmpOutputStream = null;

        try{

            tmpInputStream = bSocket.getInputStream();
            tmpOutputStream = bSocket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.iStream = tmpInputStream;
        this.oStream = tmpOutputStream;

        Log.i("ConnectedThread", "iStream : "+iStream);
        Log.i("ConnectedThread", "oStream : "+oStream);

    }

    @Override
    public void run() {


        byte[] msgBuffer = new byte[1024];
        int bytesRead;

        Log.i("ConnectedThread", "Inside run() in ConnectedThread");


        while (true){

            try {

                Log.i("ConnectedThread", "Stream check\n\niStream==null : "+( iStream==null )+"\n\n");

                Log.i("ConnectedThread", "Waiting for Connection..............");

                bytesRead = iStream.read(msgBuffer);

                globals.getChatHandler().obtainMessage(AppGlobals.MSG_READ, bytesRead, -1, msgBuffer).sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }



    public void sendMessage(byte[] msgBuffer) {


        /***********Start App Globals Check**********/

        Log.i("AppGlobals Check","***********Start App Globals Check in ConnectedThread**********");
        Log.i("AppGlobals Check","Chat Handler Null check : "+ ( (globals.getChatHandler() == null)? "Null":"Not Null" ) );
        Log.i("AppGlobals Check","Client Name : "+globals.getClientName());
        Log.i("AppGlobals Check","Server Name : "+globals.getServerName());
        Log.i("AppGlobals Check","Target Device Check Null Check : "+ ( (globals.getTargetDevice() == null)? "Null":"Not Null" ) );
        Log.i("AppGlobals Check","Connected Thread Null Check : "+ ( (globals.getConnectedThread() == null)? "Null":"Not Null" ) );
        Log.i("AppGlobals Check","Connected Thread Alive Check : "+ ( (globals.getConnectedThread().isAlive())? "Yes":"No" ) );
        Log.i("AppGlobals Check","Connected Thread bSocket Check : "+ ( (globals.getConnectedThread().getbSocket() == null)? "Null":"Not Null" ) );
        Log.i("AppGlobals Check","Connected Thread iStream Check : "+ ( (globals.getConnectedThread().getiStream() == null)? "Null":"Not Null" ) );
        Log.i("AppGlobals Check","Connected Thread oStream Check : "+ ( (globals.getConnectedThread().getoStream() == null)? "Null":"Not Null" ) );
        Log.i("AppGlobals Check","Connected Thread Socket_Type Check : "+ ( (globals.getConnectedThread().isSocketType())? "Secured":"In-Secured" ) );
        Log.i("AppGlobals Check","Connected Thread Is Server Check: "+ (globals.isServer() ? "Yes" : "No") );
        Log.i("AppGlobals Check","***********End App Globals Check***********");

        /***********End App Globals Check***********/

        try {

            Log.i("ConnectedThread", "Stream check\n\noStream==null : "+( oStream==null )+"\n\n");

            oStream.write(msgBuffer);

            globals.getChatHandler().obtainMessage(AppGlobals.MSG_WRITE, -1, -1, msgBuffer).sendToTarget();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
