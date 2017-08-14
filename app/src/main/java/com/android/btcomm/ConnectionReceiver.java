package com.android.btcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectionReceiver extends BroadcastReceiver {


    private final MainActivity mainActivity;

    public ConnectionReceiver(MainActivity mainActivity) {

        this.mainActivity = mainActivity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        String action = intent.getAction();

        if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){

            Log.i("onReceive", "Connection State is Changed");

        }else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){

            Log.i("onReceive", "Connected");
            mainActivity.showConnected();

        }else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){

            Log.i("onReceive", "Disconnected");
            mainActivity.showDisconnected();

        }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){

            Log.i("onReceive", "ACTION_DISCOVERY_STARTED");

        }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

            Log.i("onReceive", "ACTION_DISCOVERY_FINISHED");

        }else if (action.equals(BluetoothDevice.ACTION_FOUND)){

            BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Log.i("onReceive", "Device found\t Name : "+bd.getName()+"\t Address : "+bd.getAddress());

        }



    }
}
