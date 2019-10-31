package com.example.hushed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hushed.models.Message;
import static com.example.hushed.Util.Action1;

import java.util.ArrayList;
import java.util.List;

public class ConversationSelectRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Action1<Message> clickListener;
    private List<Message> conversations = new ArrayList<>();

    public ConversationSelectRecyclerAdapter(Action1<Message> clickListener) {
        this.clickListener = clickListener;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messages, parent, false));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder)holder).bind(conversations.get(position), clickListener);
        }
    }

    @Override public int getItemCount() { return conversations.size(); }

    public void setList(List<Message> conversations) {
        this.conversations = conversations;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView senderText;
        private final TextView messageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            senderText = itemView.findViewById(R.id.message_sender);
            messageText = itemView.findViewById(R.id.message_text);
        }
        public void bind(Message msg, Action1<Message> clickListener) {
            senderText.setText(msg.sender);
            messageText.setText(msg.content);
            itemView.setOnClickListener((arg)->clickListener.invoke(msg));
        }
    }
}
