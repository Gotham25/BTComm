package com.android.btcomm;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Select;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.message) EditText chatMsg;
    @BindView(R.id.sendMessage) Button sendMsg;
    //@BindView(R.id.output) EditText output;
    @BindView(R.id.chatLogs) ListView chatLogsView;

    private AppGlobals globals;
    private ArrayList<ChatMessage> chatMsgList;
    private ChatArrayAdapter chatMessageArrayAdapter;
    //private DBStore dbStore;
    private DBStore1 dbStore1;

    @Override
    protected void onResume() {
        super.onResume();

        initHandlers();

    }

    private void initHandlers() {

        if(globals == null){
            globals = (AppGlobals) getApplicationContext();
        }

        globals.setChatHandler(BTChatHandler);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        globals = (AppGlobals) getApplicationContext();

        initHandlers();

        chatMsgList = new ArrayList<>();

        //Log.i("showCommUI","Local Device Address : "+globals.getLocalBTAddress());
        //Log.i("showCommUI","Target Device Address : "+globals.getTargetBTAddress());
        //Log.i("showCommUI","Target Device Address_through_target_device_object : "+ (globals.getTargetDevice()!=null?globals.getTargetDevice().getAddress():"target_device_object is Null") );


        /***********Start App Globals Check**********/
/*

        AppGlobals glob = (AppGlobals) getApplicationContext();

        Log.i("AppGlobals Check","***********Start App Globals Check in ChatActivity**********");
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

*/
        /***********End App Globals Check***********/


        Log.i("Previous Chat Logs", "" );

        /*
        for(String str :dbStore.getChatLogs()) {

            Log.i("Chat Record", str);

        }
        */

        /*if(dbStore!=null)
            for(ChatMessage chatMessage : dbStore.getChatLogs(globals.getTargetBTAddress())){
                chatMsgList.add(chatMessage);
            }*/

        List<DBStore1> dbStoreList =  new Select().from(DBStore1.class).orderBy("ID ASC").execute();
        Log.e("Chat Records", "DB Table Size : "+dbStoreList.size());

        if (dbStoreList!=null && dbStoreList.size()>0){
            for (DBStore1 dbStore1 : dbStoreList){

                ChatMessage chatMessage = new ChatMessage(
                        dbStore1.from,
                        dbStore1.to,
                        dbStore1.mode,
                        dbStore1.message,
                        dbStore1.timestamp
                );

                Log.e("Chat Records", "DB Table Record : "+dbStore1);
                chatMsgList.add(chatMessage);

            }
        }

        chatMessageArrayAdapter = new ChatArrayAdapter(this, 0, chatMsgList);
        chatLogsView.setAdapter(chatMessageArrayAdapter);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //globals.setChatHandler(BTChatHandler);

                String messageContent = chatMsg.getText().toString();

                //String encodedMsg = encodedMessage(messageContent);
                //String decodedMsg = decodedMessage(encodedMsg);

                String encodedMsg = encodedMessage(messageContent);
                String decodedMsg = decodedMessage(encodedMsg);

                Log.i("Encoded Message Content", encodedMsg);
                Log.i("Encoded Message Content", decodedMsg);

                if(!messageContent.matches("[ ]*")){

                    //dbStore.addChatRecord();

                    byte[] messageBuffer = chatMsg.getText().toString().getBytes();

                    ConnectedThread connectedThread = globals.getConnectedThread();

                    connectedThread.sendMessage(messageBuffer);

                    chatMsg.setText("");

                }else {

                    Log.i("ChatLogger","Message contains only whitespace or empty string");

                }

            }
        });


    }// End of onCreate method

    private String encodedMessage(String message) {

        message = message.replaceAll("&", ":and:");
        message = message.replaceAll("\\+", ":plus:");
        return StringEscapeUtils.escapeJava(message);

    }

    private String decodedMessage(String message) {

        message = message.replaceAll(":and:", "&");
        message = message.replaceAll(":plus:", "+");
        return StringEscapeUtils.unescapeJava(message);

    }

    public static String encodedMessage_(String message) {

        message = message.replaceAll("&", ":and:");
        message = message.replaceAll("\\+", ":plus:");
        return StringEscapeUtils.escapeJava(message);

    }

    public static String decodedMessage_(String message) {

        message = message.replaceAll(":and:", "&");
        message = message.replaceAll(":plus:", "+");
        return StringEscapeUtils.unescapeJava(message);

    }


    //Handler for BT Chat. Insert this snippet in the class where the BT Chat UI is handled.
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

                                            tmpMsg = (globals.isServer() ? globals.getClientName() : globals.getServerName()) + "\n\n"+chatMessage+"\n";

                                            tmpMsg = tmpMsg.replaceAll("'","''");

                                            String encodedMsg = encodedMessage(tmpMsg);
                                            String decodedMsg = decodedMessage(encodedMsg);

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

                                            addMsgToList(tmpMsg, false, dbStore1.timestamp);

                                            scrollMyListViewToBottom();

                                            break;

                //Case for Message write
                case AppGlobals.MSG_WRITE:
                                            chatMessageBuffer = (byte[]) msg.obj;
                                            chatMessage = new String(chatMessageBuffer);
                                            Log.i("ChatActivity", "Message Sent : "+chatMessage);

                                            tmpMsg = (globals.isServer() ? globals.getServerName() : globals.getClientName()) + "\n\n"+chatMessage+"\n";

                                            chatMessage = chatMessage.replaceAll("'","''");

                                            String encodedMsg1 = decodedMessage(chatMessage);
                                            String decodedMsg1 = encodedMessage(encodedMsg1);

                                            /*dbStore.addChatRecord(
                                                    globals.getLocalBTAddress(),  // From Device Address
                                                    globals.getTargetBTAddress(), // To Device Address
                                                    "OUTGOING",                   // Mode of the message ( INCOMING / OUTGOING )
                                                    encodedMsg1                   // Message content received as encoded form
                                            );*/

                                            dbStore1 = new DBStore1();
                                            dbStore1.from = globals.getLocalBTAddress();
                                            dbStore1.to = globals.getTargetBTAddress();
                                            dbStore1.mode = "OUTGOING";
                                            dbStore1.message = decodedMsg1;
                                            dbStore1.timestamp = new Date().toString();
                                            dbStore1.save();

                                            addMsgToList(tmpMsg, true, dbStore1.timestamp);
                                            scrollMyListViewToBottom();

                                            break;


            }

            Log.i("Chat Logs as in table", "LOGS:\n" );

            //for(String str :dbStore.getChatLogs()) {
            //    Log.i("Chat Record", str);
            //}

        }// End of handleMessage

    };// End of BT Chat Handler

    private void scrollMyListViewToBottom() {

        chatLogsView.post(new Runnable() {
            @Override
            public void run() {
                chatLogsView.setSelection(chatMessageArrayAdapter.getCount()-1);
            }
        });
    }

    private void addMsgToList(String message, boolean isSender, String timestamp) {

        ChatMessage chatMessage = new ChatMessage(message, isSender, timestamp);//, false);
        chatMsgList.add(chatMessage);
        chatMessageArrayAdapter.notifyDataSetChanged();

    }


    @Override
    public void onBackPressed() {

        getIntent().putExtra("Chat_Result","Chat Activity Closed");
        setResult(Activity.RESULT_OK, getIntent());

        super.onBackPressed();
    }
}
