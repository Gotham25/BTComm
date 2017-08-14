package com.android.btcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gowtham on 10-12-2016.
 */

public class DeviceListReceiver extends BroadcastReceiver {

    private HashMap<String, String> bdList;
    private final DeviceList deviceList;

    public DeviceListReceiver(DeviceList deviceList) {

        this.bdList=new HashMap<>();
        this.deviceList = deviceList;

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();


        if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){

            Log.i("onReceiveDevList", "Discovery Started");

        }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

            Log.i("onReceiveDevList", "Discovery Finished");
            deviceList.showDevicesList(bdList);

        }else if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){

            Log.i("onReceiveDevList", "Connection Status Changed");

        }else if(action.equals(BluetoothDevice.ACTION_FOUND)){

            //Gets the discovered devices and adds to the list

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if( ! bdList.containsKey(device.getName()) && device.getName()!=null && device.getAddress()!=null){

                Log.i("onReceiveDevList", "Device found "+device.getName()+"   "+device.getAddress());

                bdList.put(device.getName(), device.getAddress());

            }

        }else if(action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)){

            Log.i("onReceive", "Pairing request initiated ");

        }else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){

            Log.i("onReceive", "Bond State Changed");

            deviceList.setDeviceAndFinish();

        }


    }

    public void refresh() {

        this.bdList=new HashMap<>();

    }
}
