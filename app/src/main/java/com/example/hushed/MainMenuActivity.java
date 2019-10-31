package com.example.hushed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hushed.models.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainMenuActivity extends AppCompatActivity {

    // Can't use a lambda for this, or a function reference,
    // as the API requires an extension of TimerTask...
    private static class LoadDocumentTask extends TimerTask {
        private final MainMenuActivity activity;
        LoadDocumentTask(MainMenuActivity activity) {
            this.activity = activity;
        }
        public void run() {
            Log.v("Firebase", "Requesting updated snapshot");
            activity.db.document(DataSource.Companion.getDeviceID())
                    .get()
                    .addOnSuccessListener(activity::onLoadDocument);
        }
    }
    private CollectionReference db = FirebaseFirestore.getInstance().collection("db");

    private List<Message> dummyMessages = Collections.unmodifiableList(Arrays.asList(
            new Message("Hey there!", "Friend Unit 1"),
            new Message("Please call me back", "Parental Unit 1"),
            new Message("Just wanted to let you know...", "Friend Unit 2"),
            new Message("Please don't tell Parental Unit 1 about this", "Sibling Unit 1"),
            new Message("Bruh!", "Friend Unit 3"),
            new Message("Need the report to be finished soon", "Group Member 1"),
            new Message("See you this weekend", "Parental Unit 2"),
            new Message("You Shall Not PASS!", "Gandalf the Grey")
    ));

    private Map<String, List<Map<String, String>>> dummyData = Util.toMap(
            dummyMessages,
            Message::getSender,
            it->Arrays.asList(Util.toMap(Arrays.asList(it),
                    msg->"received",
                    Message::getContent
            )));

    private Timer timer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataSource.Companion.setDeviceID(checkAddress());
        Data.initUserID(checkAddress());

        findViewById(R.id.button_connect).setOnClickListener(this::onPressConnect);
        findViewById(R.id.button_settings).setOnClickListener(this::onPressSettings);
        findViewById(R.id.button_about).setOnClickListener(this::onPressAbout);
        findViewById(R.id.button_dummy).setOnClickListener(this::onPressDummy);

    }

    public void onResume() {
        super.onResume();
        // schedule document loading in background
        Log.i("Lifecycle", "MainMenuActivity foregrounded");
        try { if (timer != null) { timer.cancel(); } } catch (Exception e) {}

        timer = new Timer();
        timer.scheduleAtFixedRate(new LoadDocumentTask(this), 10*1000L, 10*1000L);
    }

    public void onPause() {
        super.onPause();
        // Stop all scheduled tasks
        Log.i("Lifecycle", "MainMenuActivity backgrounded");
        try { if (timer != null) { timer.cancel(); } } catch (Exception e) { }
    }

    private void onPressConnect(View it) {

        db.document(DataSource.Companion.getDeviceID())
                .get()
                .addOnSuccessListener(doc -> {
                    onLoadDocument(doc);
                    Intent intent = new Intent(this, ConversationSelectActivity.class);
                    startActivity(intent);
                });
    }

    private void onPressSettings(View it) {
        Log.i("Button", "Click: button_settings");
    }

    private void onPressAbout(View it) {
        Log.i("Button", "Click: button_about");
    }

    private void onPressDummy(View it) {

        Log.i("Firebase", "Sending " + dummyData.size() + " dummy messages");

        db.document(Data.getUserID())
                .set(dummyData, SetOptions.merge())
                .addOnSuccessListener( (doc) -> Log.d("Firebase", "DocumentSnapshot successfully written!"))
                .addOnFailureListener( (err) -> Log.w("Firebase", "Error Writing document", err));

    }

    private void onLoadDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            Log.v("Firebase", "No data received from server");
            return;
        }

        Log.v("Firebase", "Loading data from server");
        List<Message> conversationList = Data.getConversationList();
        Map<String, List<Message>> conversationMap = Data.getConversations();
        String id = Data.getUserID();

        Map<String, Object> updates = new HashMap<>();

        for (String partnerId : data.keySet()) {
            // Log.i("Load", "Loading convo for " + partnerId);
            // If we don't have a conversation for this user, create it
            if (!conversationMap.containsKey(partnerId)) {
                conversationMap.put(partnerId, new ArrayList<>());
            }
            // put an update to delete the messages from db.
            updates.put(partnerId, FieldValue.delete());

            // Add data to local conversation
            List<Message> convo = conversationMap.get(partnerId);

            Object value = doc.get(partnerId);
            if (value instanceof List) {
                List allMessages = (List) value;
                // Delete conversation and add messages to conversation on success

                for (Object messageData : allMessages) {
                    if (messageData instanceof Map) {
                        Message msg = extractMessage((Map<?, ?>) messageData, partnerId, id);
                        convo.add(msg);
                        // Log.i("Load", "Loaded Message = " + msg.sender +": " + msg.content);
                    }
                }
            }
        }

        // Rebuild the conversation list to display
        conversationList.clear();
        for (String key : conversationMap.keySet()) {
            List<Message> convo = conversationMap.get(key);

            if (convo.size() > 0) {

                Util.sort(convo, Message::compareByTime);

                Message last = convo.get(convo.size()-1);
                conversationList.add(new Message(last.content, key, last.id, last.time));
            }
        }

        db.document(id).update(updates)
            .addOnSuccessListener(result -> {
                Log.i("Firebase", "Deleted " + updates.size() + " messages.");
            });
    }


    /** Read a message from a database entry, and transform it into a Message object
     * using the known ids of two users as a sender and receiver */
    private Message extractMessage(Map<?,?> data, String partnerId, String ownId) {
        String content = "[NO MESSAGE]";
        String sender = "[NO SENDER]";
        String messageId = "[NO ID]";
        long timestamp = 0;

        Object received = data.get("received");
        Object sent = data.get("sent");
        Object id = data.get("id");
        Object time = data.get("time");

        if (received instanceof String) {
            sender = partnerId;
            content = (String)received;
        }
        if (sent instanceof String) {
            sender = ownId;
            content = (String)sent;
        }
        if (id instanceof String) {
            messageId = (String)id;
        }
        if (time instanceof Number) {
            timestamp = (Long)time;
        }

        return new Message(content, sender, messageId, timestamp);
    }

    private String checkAddress() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String uuid = prefs.getString("UUID", null);
        if (uuid != null) {
            return uuid;
        }

        uuid = UUID.randomUUID().toString();
        prefs.edit().putString("UUID", uuid).apply();

        return uuid;
    }


}
