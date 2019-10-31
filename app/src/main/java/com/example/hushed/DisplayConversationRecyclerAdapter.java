package com.example.hushed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hushed.models.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayConversationRecyclerAdapter extends RecyclerView.Adapter<DisplayConversationRecyclerAdapter.DisplayConversationViewHolder> {

    private List<Message> conversation = new ArrayList<>();

    @Override public DisplayConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DisplayConversationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false));
    }

    @Override public void onBindViewHolder(DisplayConversationViewHolder holder, int position) {
        holder.bind(conversation.get(position));
    }

    @Override public int getItemCount() {
        return 0;
    }

    public void submitList(List<Message> msgs) {
        conversation = msgs;
        notifyDataSetChanged();
    }

    public static class DisplayConversationViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView recievedMessageText;
        private final TextView recievedTimeText;
        private final TextView sentMessageText;
        private final TextView sentTimeText;
        public DisplayConversationViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            recievedMessageText = itemView.findViewById(R.id.txtOtherMessage);
            recievedTimeText = itemView.findViewById(R.id.txtOtherMessageTime);
            sentMessageText = itemView.findViewById(R.id.txtMyMessage);
            sentTimeText = itemView.findViewById(R.id.txtMyMessageTime);
        }

        public void bind(Message msg) {
            String ownId = Data.getUserID();

            if (msg.sender.equals(ownId)) {
                // When displaying our own (sent) message,
                // show only 'sent' views...
                sentMessageText.setText(msg.content);
                sentMessageText.setVisibility(View.VISIBLE);
                sentTimeText.setText(new Date(msg.time).toString());
                sentTimeText.setVisibility(View.VISIBLE);
                // and clear/hide received views
                recievedMessageText.setText("");
                recievedMessageText.setVisibility(View.GONE);
                recievedTimeText.setText("");
                recievedTimeText.setVisibility(View.GONE);
            } else {
                // When displaying a received message,
                // show only 'received' views...
                recievedMessageText.setText(msg.content);
                recievedMessageText.setVisibility(View.VISIBLE);
                recievedTimeText.setText(new Date(msg.time).toString());
                recievedTimeText.setVisibility(View.VISIBLE);
                // and clear/hide sent views
                sentMessageText.setText("");
                sentMessageText.setVisibility(View.GONE);
                sentTimeText.setText("");
                sentTimeText.setVisibility(View.GONE);
            }
        }

    }

}
