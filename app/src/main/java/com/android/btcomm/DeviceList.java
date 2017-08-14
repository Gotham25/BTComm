package com.android.btcomm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceList extends Activity {

    @BindView(R.id.devicesListView) ListView devicesListView;
    @BindView(R.id.scanDevices) Button scanDevices;
    @BindView(R.id.wait) TextView wait;

    private KProgressHUD kProgressHUD;
    private DeviceListReceiver deviceListReceiver;
    private IntentFilter deviceListFilter;
    private BluetoothAdapter ba;
    private boolean showProgress = true;
    private ArrayAdapter<String> adapter;
    private BluetoothDevice bDevice;
    private PairThread pairThread;


    @Override
    protected void onResume() {
        super.onResume();

        deviceListReceiver = new DeviceListReceiver(this);
        registerReceiver(deviceListReceiver, deviceListFilter);

    }


    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(deviceListReceiver);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        this.setFinishOnTouchOutside(false);

        ButterKnife.bind(this);

        deviceListFilter = new IntentFilter();
        deviceListFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        deviceListFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        deviceListFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        deviceListFilter.addAction(BluetoothDevice.ACTION_FOUND);
        deviceListFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        deviceListFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        ba = BluetoothAdapter.getDefaultAdapter();
        setTitle("");
        devicesListView.setVisibility(View.INVISIBLE);
        scanDevices.setVisibility(View.INVISIBLE);

        if(kProgressHUD == null){

            kProgressHUD = KProgressHUD.create(DeviceList.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Searching for nearby devices...")
                    .setDetailsLabel("Please Wait")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

            if(ba.isDiscovering())
                ba.cancelDiscovery();
            else {
                ba.startDiscovery();
            }


            //closeDialog();

        }else {

            if(kProgressHUD.isShowing()){
                kProgressHUD.dismiss();
            }else {
                kProgressHUD
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Searching for nearby devices...")
                        .setDetailsLabel("Please Wait")
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();

                //closeDialog();

            }

        }// End of else block



        scanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setProgressBarIndeterminateVisibility(true);

                setTitle("Device Search in progress");
                devicesListView.setVisibility(View.INVISIBLE);
                scanDevices.setVisibility(View.INVISIBLE);
                wait.setVisibility(View.VISIBLE);

                deviceListReceiver.refresh();

                if(ba==null){
                    ba = BluetoothAdapter.getDefaultAdapter();
                }

                if(ba.isDiscovering())
                    ba.cancelDiscovery();
                else {
                    ba.startDiscovery();
                }

            }
        });


    }// end of onCreate function

    private void closeDialog() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                kProgressHUD.dismiss();

            }
        }, 5000);

    }

    public void showDevicesList(final HashMap<String, String> bdList) {

        Set<String> devicesSet = bdList.keySet();

        if(devicesSet.size() > 0)
            Log.i("showDevicesList", "Devices :\n"+ devicesSet.toArray( new String[0] ));

        List<String> devicesList=new ArrayList<>();
        for (String deviceName : devicesSet) {
            devicesList.add(deviceName);
        }

        devicesListView.setVisibility(View.VISIBLE);
        scanDevices.setVisibility(View.VISIBLE);

        setTitle("Bluetooth Devices");

        if(adapter == null){

            kProgressHUD.dismiss();

            adapter=new ArrayAdapter<>(DeviceList.this, android.R.layout.simple_list_item_1, devicesList);

            devicesListView.setAdapter(adapter);

        }else {

            //Executed when refreshed is called...

            setProgressBarIndeterminateVisibility(false);

            wait.setVisibility(View.INVISIBLE);

            adapter=new ArrayAdapter<>(DeviceList.this, android.R.layout.simple_list_item_1, devicesList);

            devicesListView.setAdapter(adapter);

        }

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView item = (TextView) view;

                //Toast.makeText(DeviceList.this, "Sel Item : "+item.getText(), Toast.LENGTH_SHORT).show();

                Toast.makeText(DeviceList.this, "Dev Addr : "+bdList.get(item.getText()), Toast.LENGTH_SHORT).show();

                bDevice = ba.getRemoteDevice( bdList.get(item.getText()) );

                if( bDevice.getBondState() == BluetoothDevice.BOND_NONE ){

                    //Unpaired device

                    pairThread=new PairThread(bDevice);

                    pairThread.start();


                }else{

                    //Paired Device

                    setDeviceAndFinish();

                }

            }
        });


    }

    public void setDeviceAndFinish() {


        if(bDevice.getBondState() == BluetoothDevice.BOND_BONDED){

            //Log.i("PairThread Check","isAlive : "+pairThread.isAlive());

            //Device is bonded
            Log.i("Bonding State","--Device Bonded--");

            Intent finishIntent=new Intent();
            finishIntent.putExtra("Device Address",bDevice.getAddress());
            setResult(Activity.RESULT_OK, finishIntent);
            finish();

        }else {
            Toast.makeText(DeviceList.this, "Some error occurred. Try again...", Toast.LENGTH_SHORT).show();
        }

    }
}
