package com.android.btcomm;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.searchDevices) Button searchDevices;
    @BindView(R.id.connectionInfo) TextView connectionInfo;
    @BindView(R.id.chatButton) Button chatButton;

    private static final int DEVICES_LIST = 1025;
    private BluetoothAdapter ba;
    private static final int ENABLE_BT = 1021;
    private boolean SECURE = true;
    private ConnectionReceiver connectionReceiver;
    private IntentFilter connFilter;
    private static final int DISCOVERABLE_REQUEST = 1026;
    private static final int ASK_LOCATION_PERMISSIONS = 1257;
    private AcceptThread acceptThread;
    private BluetoothDevice targetDev;
    private AppGlobals globals;
    private static final int CHAT_ACTIVITY = 1679;
    private boolean backButtonDoublePressed = true;
    //private DBStore dbStore;
    private  DBStore1 dbStore1;

    @Override
    protected void onResume() {
        super.onResume();
        connectionReceiver = new ConnectionReceiver(this);
        registerReceiver(connectionReceiver, connFilter);

        if(globals.getChatHandler() == null)
            globals.setChatHandler(BTChatHandler);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(connectionReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        globals= (AppGlobals) getApplicationContext();

        connFilter = new IntentFilter();
        connFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        connFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        connFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        connFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        connFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        connFilter.addAction(BluetoothDevice.ACTION_FOUND);

        connectionInfo.setText("Not Connected");
        connectionInfo.setTextColor(Color.RED);

        /*dbStore = new DBStore(MainActivity.this);*/
        //dbStore = DBStore.getInstance(this);

        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //connectionInfo.setText("Connected");
                //connectionInfo.setTextColor(Color.GREEN);



                //Intent showDevices = new Intent(MainActivity.this, DeviceListActivity.class);
                //startActivityForResult(showDevices, DEVICE_LIST_ACTIVITY);


                //Assuming this call is initiated from the Client
                globals.setServer(false);  //true-if server ; false-if client

                if(ba.isEnabled()){

                    // For devices Marshmallow and greater than above it
                    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){

                        //Ask for Location Preference
                        askForPermission( Manifest.permission.ACCESS_FINE_LOCATION, ASK_LOCATION_PERMISSIONS );

                    }else{

                        //For devices below Marshmallow
                        initDeviceSearch();

                    }

                    //Intent showDevices = new Intent(MainActivity.this, DeviceListActivity.class);
                    //startActivityForResult(showDevices, DEVICE_LIST_ACTIVITY);
                    
                }else {
                    Toast.makeText(MainActivity.this, "Bluetooth is not turned on", Toast.LENGTH_SHORT).show();
                }



            }
        });


        initBT();


        /*

        ba = BluetoothAdapter.getDefaultAdapter();

        if(!ba.isEnabled()){

            ba.enable();

        }

        while (!ba.isEnabled()){
            // do nothing
        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){

                    askForPermission( Manifest.permission.ACCESS_FINE_LOCATION, ASK_LOCATION_PERMISSIONS);

                }else{

                    ba.startDiscovery();

                }

            }
        }, 10000);

        */


    } //End of onCreate()

    private void askForPermission(String permission, int requestCode) {

        if(ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED){

            //Should we show an explanation ???
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)){

                //This is called if the user has denied the permission before
                //In this case I'm just asking the permissions again

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            }else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            }

        }else {

            //Toast.makeText(this, ""+permission+" is already granted", Toast.LENGTH_SHORT).show();
            Log.i("askForPermission", "Permission granted already, hence initiating device search");
            initDeviceSearch();
            
        }

    }

    private void initDeviceSearch() {

        Log.i("initDeviceSearch", "Devices Search started");

        //Start the active device search discovery
        Intent devicesList=new Intent(MainActivity.this, DeviceList.class);
        startActivityForResult(devicesList, DEVICES_LIST);
        //


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED ){

            switch (requestCode){

                case ASK_LOCATION_PERMISSIONS :
                    //Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                    Log.i("onReqPermissionsResult", "Location Permission Granted");
                    initDeviceSearch();
                    break;
            }

        }else {

            Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();

        }

    }

    private void initBT() {

        ba = BluetoothAdapter.getDefaultAdapter();

        if(ba!=null){

            if(!ba.isEnabled()){

                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, ENABLE_BT);

            }

        }

    }

    private int getSDKVersion() {

        return Build.VERSION.SDK_INT;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case DEVICES_LIST:
                //Log.i("onActivityResult", "MyListView Activity Completed.\n\nResult :-\n"+Globals.item);
                //Log.i("onActivityResult", "Status : "+data.getStringExtra("ConnInfo"));

                Log.i("onActivityResult", "DeviceList Activity Finished");
                Log.i("onActivityResult", "Device Address : "+data.getExtras().get("Device Address"));
                Log.i("onActivityResult", "Accept Thread Check "+acceptThread.isAlive());

                BluetoothDevice bDevice = ba.getRemoteDevice((String) data.getExtras().get("Device Address"));
                this.targetDev = bDevice;
                connectToDevice(bDevice);

                break;

            case CHAT_ACTIVITY:

                /***********Start App Globals Check**********/

                Log.i("AppGlobals Check","***********Start App Globals Check in MainActivity**********");
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

                String chatResult = (String) data.getExtras().get("Chat_Result");
                Log.i("onActivityResult","Chat Activity Result : " + chatResult);
                break;

            case ENABLE_BT:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    Log.i("Main Activity Result", "BT is enabled");

                    String BLUETOOTH_MAC_ADDRESS = android.provider.Settings.Secure.getString(this.getContentResolver(), "bluetooth_address");

                    Log.i("onActivityResult","Bluetooth MAC Address : "+BLUETOOTH_MAC_ADDRESS);

                    globals.setLocalBTAddress(BLUETOOTH_MAC_ADDRESS);
                    globals.setTargetBTAddress(BLUETOOTH_MAC_ADDRESS);

                    Intent makeDiscoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    makeDiscoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
                    startActivityForResult(makeDiscoverableIntent, DISCOVERABLE_REQUEST);

                }
                else
                    Toast.makeText(this, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show();
                break;

            case DISCOVERABLE_REQUEST:


                Log.i("onActivityResult", "resultCode : "+resultCode);
                Log.i("onActivityResult", "Activity.RESULT_OK : "+Activity.RESULT_OK);
                Log.i("onActivityResult", "AppCompatActivity.RESULT_OK : "+AppCompatActivity.RESULT_OK);
                Log.i("onActivityResult", "RESULT_OK : "+RESULT_OK);

                if( resultCode > 0 ){

                    //AcceptThread acceptThread=new AcceptThread(ba, SECURE);
                    //acceptThread.start();
                    Log.i("DISCOVERABLE_REQUEST","Device is Discoverable for 180 seconds");

                    if(acceptThread != null)
                        acceptThread = null;

                    acceptThread=new AcceptThread(ba, SECURE, globals);
                    acceptThread.start();

                }else {

                    Toast.makeText(this, "Discoverability denied. Please try again", Toast.LENGTH_SHORT).show();
                    
                }

                break;

        }

    }

    private void connectToDevice(BluetoothDevice bDevice) {


        //Initiating Connection to the target device

        ConnectThread connectThread=new ConnectThread(bDevice, SECURE, globals);
        connectThread.start();

    }

    public void showConnected() {

        if(globals == null) {
            //Globals is null
            Log.i("showConnected" ,"Globals variable is null");
            globals = (AppGlobals) getApplicationContext();
        }

        Log.i("showConnected","Acting as "+(globals.isServer()?"Server":"Client"));

        if(globals.isServer()){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    targetDev = globals.getTargetDevice();
                    connectionInfo.setText("Connected to "+targetDev.getName());
                    connectionInfo.setTextColor(Color.GREEN);
                    globals.setServerName("Me");
                    globals.setClientName(targetDev.getName());

                    showCommUI();

                }
            }, 7000);// Wait 5 sec so that the background thread updates the target device

        }else {

            connectionInfo.setText("Connected to "+targetDev.getName());
            connectionInfo.setTextColor(Color.GREEN);
            globals.setServerName(targetDev.getName());
            globals.setClientName("Me");

            showCommUI();

        }



    }

    private void showCommUI() {

        searchDevices.setVisibility(View.INVISIBLE);

        chatButton.setVisibility(View.VISIBLE);

        globals.setChatHandler(BTChatHandler);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /***********Start App Globals Check**********/
/*

                AppGlobals glob = (AppGlobals) getApplicationContext();

                Log.i("AppGlobals Check","***********Start App Globals Check in MainActivity**********");
                Log.i("AppGlobals Check","Chat Handler Null check : "+ ( (glob.getChatHandler() == null)? "Null":"Not Null" ) );
                Log.i("AppGlobals Check","Client Name : "+glob.getClientName());
                Log.i("AppGlobals Check","Server Name : "+glob.getServerName());
                Log.i("AppGlobals Check","Target Device Check Null Check : "+ ( (glob.getTargetDevice() == null)? "Null":"Not Null" ) );
                Log.i("AppGlobals Check","Connected Thread Null Check : "+ ( (glob.getConnectedThread() == null)? "Null":"Not Null" ) );
                Log.i("AppGlobals Check","Connected Thread Alive Check : "+ ( (glob.getConnectedThread().isAlive())? "Yes":"No" ) );
                Log.i("AppGlobals Check","Connected Thread bSocket Check : "+ ( (glob.getConnectedThread().getbSocket() == null)? "Null":"Not Null" ) );
                Log.i("AppGlobals Check","Connected Thread iStream Check : "+ ( (glob.getConnectedThread().getiStream() == null)? "Null":"Not Null" ) );
                Log.i("AppGlobals Check","Connected Thread oStream Check : "+ ( (glob.getConnectedThread().getoStream() == null)? "Null":"Not Null" ) );
                Log.i("AppGlobals Check","Connected Thread Socket_Type Check : "+ ( (glob.getConnectedThread().isSocketType())? "Secured":"In-Secured" ) );
                Log.i("AppGlobals Check","Connected Thread Is Server Check: "+ (glob.isServer() ? "Yes" : "No") );
                Log.i("AppGlobals Check","***********End App Globals Check***********");

*/
                /***********End App Globals Check***********/

                Intent showChatActivity = new Intent(MainActivity.this, ChatActivity.class);
                startActivityForResult(showChatActivity, CHAT_ACTIVITY);
                //startActivity(showChatActivity);

            }
        });

    }

    public void showDisconnected() {

        searchDevices.setVisibility(View.VISIBLE);
        connectionInfo.setText("Not Connected");
        connectionInfo.setTextColor(Color.RED);
        chatButton.setVisibility(View.INVISIBLE);

    }


    //Handler for BT Chat. Insert this snippet in the class where the BT Chat UI / BT Chat Notify UI is handled.
    private Handler BTChatHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            String chatMessage;
            byte[] chatMessageBuffer;
            String tmpMsg;

            switch (msg.what){


                //Case for Message read
                case AppGlobals.MSG_READ :
                    chatMessageBuffer = (byte[]) msg.obj;
                    chatMessage = new String(chatMessageBuffer, 0, msg.arg1);
                    Log.i("ChatActivity", "Message Received : "+chatMessage);

                    tmpMsg = (globals.isServer() ? globals.getClientName() : globals.getServerName()) + "\n"+chatMessage+"\n";
                    Toast.makeText(MainActivity.this, "Message Received from "+tmpMsg, Toast.LENGTH_SHORT).show();

                    tmpMsg = tmpMsg.replaceAll("'","''");

                    String encodedMsg = ChatActivity.encodedMessage_(tmpMsg);

                    /*
                    dbStore.addChatRecord(
                            globals.getTargetBTAddress(), // From Device Address
                            globals.getLocalBTAddress(),  // To Device Address
                            "INCOMING",                   // Mode of the message ( INCOMING / OUTGOING )
                            encodedMsg                    // Message content received as encoded form
                    );
                    */

                    dbStore1 = new DBStore1();
                    dbStore1.from = globals.getTargetBTAddress();
                    dbStore1.to = globals.getLocalBTAddress();
                    dbStore1.mode = "INCOMING";
                    dbStore1.message = encodedMsg;
                    dbStore1.timestamp = new Date().toString();
                    dbStore1.save();

                    Log.i("BTChatHandler","target BT Addr : "+globals.getTargetBTAddress());

                    break;

                //Case for Message write
                case AppGlobals.MSG_WRITE:
                    chatMessageBuffer = (byte[]) msg.obj;
                    chatMessage = new String(chatMessageBuffer);
                    Log.i("ChatActivity", "Message Sent : "+chatMessage);

                    tmpMsg = (globals.isServer() ? globals.getServerName() : globals.getClientName()) + "\n"+chatMessage+"\n";
                    Toast.makeText(MainActivity.this, "Message Sent to "+tmpMsg, Toast.LENGTH_SHORT).show();

                    break;

            }



        }

    };// End of BT Chat Handler




    @Override
    public void onBackPressed() {


        if(backButtonDoublePressed){
            super.onBackPressed();
            return;
        }

        backButtonDoublePressed = true;
        Toast.makeText(MainActivity.this, "Click again to exit from the application", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                backButtonDoublePressed = false;

            }
        }, 2000);

    }
}
