package com.android.btcomm;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Gowtham on 17-12-2016.
 */

public class PairThread extends Thread {


    private final BluetoothDevice bDevice;

    public PairThread(BluetoothDevice bDevice) {

        this.bDevice = bDevice;

    }

    @Override
    public void run() {
        super.run();

        Log.i("device check","Device Name : "+bDevice.getName());
        Log.i("device check","Device Address : "+bDevice.getAddress());

        Class c;
        Method createBondMethod;
        Boolean bondState = false;

        try {

            c = Class.forName("android.bluetooth.BluetoothDevice");
            createBondMethod = c.getMethod("createBond");
            bondState = (Boolean) createBondMethod.invoke(bDevice);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        //while(bDevice.getBondState() != BluetoothDevice.BOND_BONDED){

            //Still bonding in process
            Log.i("Bonding State", "--Bonding in progress in background thread. Please Wait....--");

        //}

    }

}
