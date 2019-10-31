package com.example.hushed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hushed.models.Message;

public class ConversationSelectActivity extends AppCompatActivity {

    public static final String MESSAGE = "com.example.hushed.MESSAGE";
    public static final String SENDER = "com.example.hushed.SENDER";

    private ConversationSelectRecyclerAdapter messageAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_messages);
        initRecyclerView();

        messageAdapter.setList(Data.getConversationList());

        findViewById(R.id.new_message).setOnClickListener(this::newMessageClicked);
    }

    private void newMessageClicked(View view) {
        Log.i("Click", "New Message clicked");
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        messageAdapter = new ConversationSelectRecyclerAdapter(this::messageClicked);
        RecyclerView rview = this.findViewById(R.id.recyclerViewHome);
        rview.setLayoutManager(new LinearLayoutManager(this));
        rview.setAdapter(messageAdapter);
    }

    private void messageClicked(Message msg) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(SENDER, msg.sender);
        startActivity(intent);
    }

}
