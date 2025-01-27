import React, { useState } from "react";
import { StyleSheet, Text, View, Pressable, Image } from "react-native";
import { fetchPostDataWithAuth } from "../client/Client";
import { useNavigation } from "@react-navigation/native";

const FriendRequests = ({ item, friendRequest, setFriendRequest }) => {
  const navigation = useNavigation();
  const acceptFriendRequest = async (id) => {
    if (!id) {
      console.error("Invalid user ID");
      return;
    }
    try {
      const response = await fetchPostDataWithAuth(
        "/auth/user/accept-friend-request",
        { id }
      );
      if (response.data === "Friend request accepted successfully") {
        console.log("Friend request accepted successfully");

        // Remove the accepted friend request from the state
        setFriendRequest((prevRequests) =>
          prevRequests.filter((request) => request.id !== id)
        );
        navigation.navigate("Chat");
      } else {
        console.error("Failed to accept friend request:", response);
      }
    } catch (error) {
      console.error("Failed to accept friend request", error);
    }
  };

  return (
    <Pressable style={styles.container}>
      <View>
        <Image
          style={styles.profilePicture}
          source={{
            uri: item?.ppicture || "../../../assets/images/user.png",
          }}
        />
      </View>

      <Text style={styles.username}>
        {item?.username} sent you a friend request
      </Text>

      <Pressable
        onPress={() => acceptFriendRequest(item.id)}
        style={styles.addButton}
      >
        <Text style={styles.addButtonText}>Accept</Text>
      </Pressable>
    </Pressable>
  );
};

export default FriendRequests;

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    marginVertical: 10,
    justifyContent: "space-between",
  },
  profilePicture: {
    width: 50,
    height: 50,
    borderRadius: 25,
    // resizeMode: "cover",
  },
  infoContainer: {
    // marginLeft: 12,
    // flex: 1,
  },
  username: {
    fontSize: 15,
    fontWeight: "400",
    marginLeft: 10,
    flex: 1,
  },
  addButton: {
    backgroundColor: "#0066b2",
    padding: 10,
    borderRadius: 6,
    width: 105,
  },
  addButtonText: {
    textAlign: "center",
    color: "white",
    fontSize: 13,
    fontWeight: "bold",
  },
});
