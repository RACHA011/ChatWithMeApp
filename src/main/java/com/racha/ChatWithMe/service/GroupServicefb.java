// package com.racha.ChatWithMe.service;

// import java.util.concurrent.CompletableFuture;

// import org.springframework.stereotype.Service;

// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.CollectionReference;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.WriteResult;
// import com.google.firebase.cloud.FirestoreClient;
// import com.racha.ChatWithMe.model.Group;

// @Service
// public class GroupServicefb {

//     private static final String COLLECTION_NAME = "groups";

//     // Save group to Firestore
//     public void save(Group group) {
//         try {
//             Firestore firestore = FirestoreClient.getFirestore();
//             CollectionReference groupCollection = firestore.collection(COLLECTION_NAME);
//             DocumentReference docRef = groupCollection.document(group.getChatRoomId());

//             ApiFuture<WriteResult> future = docRef.set(group);
//             future.get(); // Wait for the operation to complete (optional)
//             System.out.println("Group saved successfully to Firestore");
//         } catch (Exception e) {
//             System.err.println("Error saving group to Firestore: " + e.getMessage());
//         }
//     }

//     // Get group by ID (returns CompletableFuture)
//     public CompletableFuture<Group> getGroup(String chatRoomId) {
//         CompletableFuture<Group> futureGroup = new CompletableFuture<>();
//         try {
//             Firestore firestore = FirestoreClient.getFirestore();
//             DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(chatRoomId);

//             ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = docRef.get();
//             future.addListener(() -> {
//                 try {
//                     com.google.cloud.firestore.DocumentSnapshot document = future.get();
//                     if (document.exists()) {
//                         Group group = document.toObject(Group.class);
//                         System.out.println("Group retrieved successfully from Firestore");
//                         futureGroup.complete(group);
//                     } else {
//                         System.out.println("No group found with ID: " + chatRoomId);
//                         futureGroup.completeExceptionally(new Exception("No group found"));
//                     }
//                 } catch (Exception e) {
//                     System.err.println("Error retrieving group: " + e.getMessage());
//                     futureGroup.completeExceptionally(e);
//                 }
//             }, Runnable::run);
//         } catch (Exception e) {
//             System.err.println("Error initializing Firestore: " + e.getMessage());
//             futureGroup.completeExceptionally(e);
//         }
//         return futureGroup;
//     }

//     // Delete group by ID
//     public void deleteGroup(String chatRoomId) {
//         try {
//             Firestore firestore = FirestoreClient.getFirestore();
//             DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(chatRoomId);

//             ApiFuture<WriteResult> future = docRef.delete();
//             future.get(); // Wait for the operation to complete (optional)
//             System.out.println("Group deleted successfully from Firestore");
//         } catch (Exception e) {
//             System.err.println("Error deleting group from Firestore: " + e.getMessage());
//         }
//     }
// }
