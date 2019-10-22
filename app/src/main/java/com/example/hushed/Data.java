package com.example.hushed;

import android.util.Log;

import com.example.hushed.models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import static com.example.hushed.Util.*;

public class Data {
    /** Thread-Safe list of conversations */
    private static CopyOnWriteArrayList<Message> conversationList = new CopyOnWriteArrayList<>();
    /** Thread-Safe map of partnerIDs to conversation with that partner. */
    private static ConcurrentHashMap<String, List<Message>> conversations = new ConcurrentHashMap<>();
    /** UserID for this device */
    private static String userID;

    /** UserID of partner if currently viewing a conversation */
    private static String viewingConversation;

    /** public accessor for conversation list  */
    public static List<Message> getConversationList() { return conversationList; }

    /** Thread-safe Map of event names to callbacks for those events */
    private static ConcurrentHashMap<Class, List<Action1<? extends Object>>> callbackMap = new ConcurrentHashMap<>();

    /** Register a new callback into the callback map */
    public static void addCallback(Class event, Action1<? extends Object> callback) {
        if (!callbackMap.containsKey(event)) {
            callbackMap.put(event, new CopyOnWriteArrayList<>());
        }
        callbackMap.get(event).add(callback);
    }

    /** Un-register a callback that was previously added to an event */
    public static void removeCallback(Class event, Action1<? extends Object> callback) {
        if (callbackMap.containsKey(event)) {
            callbackMap.get(event).remove(callback);
        }
    }
    /** Invoke all registered callbacks for the given event */
    public static <Event extends Object> void on(Event args) {
        Class event = args.getClass();
        if (callbackMap.containsKey(event)) {
            List<Action1<? extends Object>> callbacks = callbackMap.get(event);
            for (Action1<? extends Object> cb : callbacks) {
                try {
                    Action1<Event> action = (Action1<Event>) cb;
                    action.invoke(args);
                } catch (Exception e) {
                    // May get some errors if types don't match, but that is fine.
                    // We will want to know if we register callbacks for the wrong types.
                    Log.e("Callback", "Error in callback for " + event + ". ", e);
                }
            }
        }
    }

    /** public accessor for the conversation map */
    public static ConcurrentHashMap<String, List<Message>> getConversations() {
        return conversations;
    }

    /** Accessor for User ID */
    public static String getUserID() { return userID; }

    /** One-time setter for userID */
    public static void initUserID(String id) {
        if (userID != null) { throw new RuntimeException("User ID already set"); }
        userID = id;
    }

    /** Returns the UserID of partner of the conversation being viewed */
    public static String getViewingConversation() {
        return viewingConversation;
    }

    /** Sets the UserID of the partner of the conversation being viewed */
    public static void setViewingConversation(String viewingConversation) {
        Data.viewingConversation = viewingConversation;
    }

}
