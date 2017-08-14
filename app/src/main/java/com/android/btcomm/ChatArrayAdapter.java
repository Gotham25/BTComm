package com.android.btcomm;

import android.app.Activity;
import android.content.Context;
import android.database.DatabaseUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gowtham on 04-10-2016.
 */

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private final Context context;
    private final int resource;
    private final List<ChatMessage> chatMessageList;
    private TextView msgContent;
    private TextView timestamp;

    public ChatArrayAdapter(Context context, int resource, List<ChatMessage> chatMessageList) {
        super(context, resource, chatMessageList);

        this.context = context;
        this.resource = resource;
        this.chatMessageList = chatMessageList;
        msgContent = null;
    }


    @Override
    public int getViewTypeCount() {

        return 2;//super.getViewTypeCount();

    }


    @Override
    public int getItemViewType(int position) {
        return position % 2 ;//super.getItemViewType(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) ( (Activity) context ).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ChatMessage chatMessage = getItem(position);
        View chatRowView =  inflater.inflate( chatMessage.isSender() ? R.layout.chat_row_out_msg : R.layout.chat_row_in_msg , parent, false);
        if( chatMessage.isSender() ) {
            msgContent = (TextView) chatRowView.findViewById(R.id.out_msg_txtView);
            timestamp = (TextView) chatRowView.findViewById(R.id.out_msg_time);
        }
        else {
            msgContent = (TextView) chatRowView.findViewById(R.id.in_msg_txtView);
            timestamp = (TextView) chatRowView.findViewById(R.id.in_msg_time);
        }


        /*if(chatMessage.isOldMsg()) {

            try{

                msgContent.setText(chatMessage.getMessage());

                *//*String _24hrs = chatMessage.getTime();
                SimpleDateFormat _24hrsDateFormat = new SimpleDateFormat("HH:mm:ss");
                Date _24 = _24hrsDateFormat.parse(_24hrs);
                SimpleDateFormat _12hrsDateFormat = new SimpleDateFormat("hh:mm a");
                String _12hrs = _12hrsDateFormat.format(_24);
                timestamp.setText("" + _12hrs);*//*

                timestamp.setText(chatMessage.getTimestamp());


            }catch (Exception e){
                Log.i("ChatArrayAdapter","ParseException : "+e.getMessage());
            }

        }
        else {
            msgContent.setText( chatMessage.getMsgContent() );
            //timestamp.setText("" + (new SimpleDateFormat("hh:mm a").format(new Date())));
            timestamp.setText(chatMessage.getTimestamp());
        }*/


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat _12hrsDateFormat = new SimpleDateFormat("hh:mm a");
        String displayDate = "";

        try {

            Date date = dateFormat.parse(chatMessage.getTimestamp());
            displayDate = _12hrsDateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String unEscapedMsg = unescapeMessage(chatMessage.getMessage());
        msgContent.setText(unEscapedMsg);
        timestamp.setText(displayDate);

        return chatRowView;

    }

    private String unescapeMessage(String escapedChatMessage) {

        Log.e("unescapeMessage","Msg : "+StringEscapeUtils.unescapeJava(escapedChatMessage));

        return StringEscapeUtils.unescapeJava(escapedChatMessage);
    }
}
