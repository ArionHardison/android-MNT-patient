package com.dietmanager.app.activities.fcm_chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.dietmanager.app.R;

import java.util.List;


/**
 * Created by santhosh@appoets.com on 23-01-2018.
 */

public class ChatMessageAdapter extends ArrayAdapter<Chat> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;
    private Context context;

    public ChatMessageAdapter(Context context, List<Chat> data) {
        super(context, R.layout.item_mine_message, data);
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Chat item = getItem(position);

        assert item != null;
        if (item.getSender().equals(ChatActivity.sender)) {
            return MY_MESSAGE;
        } else
            return OTHER_MESSAGE;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        int viewType = getItemViewType(position);
        final Chat chat = getItem(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
            if (chat.getType().equals("text")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.text);
                textView.setText(getItem(position).getText());
                //TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
                //timestamp.setText(String.valueOf(getDisplayableTime(chat.getTimestamp())));
            }
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
            if (chat.getType().equals("text")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.text);
                textView.setText(getItem(position).getText());
                TextView tvSenderName = (TextView) convertView.findViewById(R.id.tvSenderName);
                tvSenderName.setText(chat.getName()+" ("+chat.getSender()+")");
                //TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
                //timestamp.setText(String.valueOf(getDisplayableTime(chat.getTimestamp())));
            }
        }

        return convertView;
    }
}
