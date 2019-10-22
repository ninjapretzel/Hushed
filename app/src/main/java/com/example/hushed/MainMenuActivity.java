package com.example.hushed;

import android.content.Context;
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
import java.util.UUID;

public class MainMenuActivity extends AppCompatActivity {
    private CollectionReference db = FirebaseFirestore.getInstance().collection("db");

    private List<Message> dummyMessages = Collections.unmodifiableList(Arrays.asList(
            new Message("Hey there!", "Friend Unit 1"),
            new Message("Please call me back", "Parental Unit 1"),
            new Message("Just wanted to let you know...", "Friend Unit 2"),
            new Message("Please don't tell Parental Unit 1 about this", "Sibling Unit 1"),
            new Message("Friend Unit 3", "Bruh!"),
            new Message("Group Member 1", "Need the report to be finished soon"),
            new Message("Parental Unit 2", "See you this weekend"),
            new Message("Gandalf the Grey", "You Shall Not PASS!")
    ));

    private Map<String, Map<String, String>> dummyData = Util.toMap(
            dummyMessages,
            Message::getSender,
            it->Util.toMap(Arrays.asList(it),
                    msg->"received",
                    Message::getContent
            )
    );

    private Timer timer = new Timer();

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

    private void onPressConnect(View it) {

        db.document(DataSource.Companion.getDeviceID())
                .get()
                .addOnSuccessListener(this::onLoadDocument);

    }

    private void onPressSettings(View it) {
        Log.i("Button", "Click: button_settings");
    }

    private void onPressAbout(View it) {
        Log.i("Button", "Click: button_about");
    }

    private void onPressDummy(View it) {

        db.document(Data.getUserID())
                .set(dummyData, SetOptions.merge())
                .addOnSuccessListener( (doc) -> Log.d("Firebase", "DocumentSnapshot successfully written!"))
                .addOnFailureListener( (err) -> Log.w("Firebase", "Error Writing document", err));

    }

    private void onLoadDocument(DocumentSnapshot doc) {

        List<Message> conversationList = Data.getConversationList();
        Map<String, List<Message>> conversationMap = Data.getConversations();
        String id = Data.getUserID();

        Map<String, Object> data = doc.getData();
        Map<String, Object> updates = new HashMap<>();

        for (String partnerId : data.keySet()) {
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
                        convo.add(extractMessage((Map<?, ?>) messageData, partnerId, id));
                    }
                }

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
