package com.racha.ChatWithMe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racha.ChatWithMe.model.Message;

@Service
public class MessageService {

    private static final String COLLECTION_NAME = "messages";

    public Message save(Message message) {
        Message respond = message;
        CompletableFuture<Message> future = new CompletableFuture<Message>();
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = database.getReference(COLLECTION_NAME).child(message.getChatRoomId());

            // TODO: how will i mark the message as delivered
            // if (message.getStatus().equals(MessageStatus.SENT) || message.getStatus() ==
            // null) {
            // message.setStatus(MessageStatus.DELIVERED);
            // }

            // Check if the message doesn't have an ID, and generate a new one if necessary
            if (message.getId() == null || message.getId().isEmpty()) {
                // Retrieve all messages from the chat room
                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Long> messageIds = new ArrayList<>();
                        messageIds.add(0L); // Start with 0 as the first message ID

                        // Loop through each child node (message) and collect the keys (message IDs)
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                try {
                                    // Add the message ID (key) to the list, converting it to a Long
                                    messageIds.add(Long.parseLong(messageSnapshot.getKey()));
                                } catch (NumberFormatException e) {
                                    System.err.println("Invalid message ID format: " + messageSnapshot.getKey());
                                }
                            }
                        }

                        // Get the last ID and increment it for the new message
                        Long lastId = messageIds.stream().max(Long::compareTo).orElse(0L) + 1;
                        message.setId(lastId.toString()); // Set the new ID to the message object

                        // Use the message ID as the new child ID
                        DatabaseReference newMessageRef = messageRef.child(message.getId());

                        // Save the message at the specified child (using the message's ID as the key)
                        newMessageRef.setValueAsync(message);
                        future.complete(message);
                        
                        System.out.println(
                                "Message saved successfully to Firebase Realtime Database with ID: " + message.getId());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Error retrieving messages: " + databaseError.getMessage());
                    }
                });
                if(future != null) {
                    return future.get();
                }
            } else {
                // If the message already has an ID, save it directly
                DatabaseReference newMessageRef = messageRef.child(message.getId());
                newMessageRef.setValueAsync(message);
                System.out.println(
                        "Message saved successfully to Firebase Realtime Database with ID: " + message.getId());
            }
        } catch (Exception e) {
            System.err.println("Error saving message to Firebase Realtime Database: " + e.getMessage());
        }
        return respond;
    }

 
    // Get messages by chatRoomId (returns CompletableFuture)
    public CompletableFuture<List<Message>> getMessagesByChatRoomId(String chatRoomId) {
        CompletableFuture<List<Message>> futureMessages = new CompletableFuture<>();
        List<Message> messages = new ArrayList<>();

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = database.getReference(COLLECTION_NAME).child(chatRoomId);

            messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                messages.add(message);
                            }
                        }
                        // Complete the future once all messages are retrieved
                        futureMessages.complete(messages);
                    } else {
                        futureMessages.completeExceptionally(new Exception("No messages found"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    futureMessages.completeExceptionally(databaseError.toException());
                }
            });

        } catch (Exception e) {
            futureMessages.completeExceptionally(e);
        }

        return futureMessages;
    }

    // Get a message
    public CompletableFuture<Message> getMessage(String chatRoomId, String messageId) {
        CompletableFuture<Message> aMessage = new CompletableFuture<>();
        try {
            // Get Firebase database instance and reference
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = database.getReference(COLLECTION_NAME).child(chatRoomId).child(messageId);

            messageRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        Message message = dataSnapshot.getValue(Message.class);
                        aMessage.complete(message);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    aMessage.completeExceptionally(
                            new RuntimeException("Error retrieving messages: " + databaseError.getMessage()));
                }

            });

        } catch (Exception e) {
            aMessage.completeExceptionally(e);
        }

        return aMessage;
    }

    // Delete message by chatRoomId and messageId
    public boolean delete(String chatRoomId, String messageId) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = database.getReference(COLLECTION_NAME).child(chatRoomId).child(messageId);

            messageRef.removeValueAsync();
            System.out.println("Message deleted successfully from Firebase Realtime Database");

        } catch (Exception e) {
            System.err.println("Error deleting message from Firebase Realtime Database: " + e.getMessage());
            return false;
        }
        return true;
    }

}
