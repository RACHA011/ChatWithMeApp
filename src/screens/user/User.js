import { Image, Text, View, Pressable, StyleSheet } from "react-native";
import React, { useEffect, useState } from "react";
import { useNavigation } from "@react-navigation/native"; // Import useNavigation
import {
  fetchGetDataWithAuth,
  fetchPostDataWithAuth,
} from "../../client/Client";

const User = ({ item }) => {
  const [requestSent, setRequestSent] = useState(false);
  const [isFriend, setIsFriend] = useState(false);
  const navigation = useNavigation(); // Initialize navigation
  const [userId, setUserId] = useState("");

  useEffect(() => {
    const getUserId = async () => {
      const response = await fetchGetDataWithAuth("/auth/user/get-userid");
      const data = response.data;
      setUserId(data.id);
      // console.log(data.id);
    };
    getUserId();
  }, []);

  const chackIfIsFriend = () => {
    if (item?.friends?.includes(userId)) {
      setIsFriend(true);
    }
  };

  const checkRequestSent = () => {
    if (item?.friendRequests?.includes(userId)) {
      setRequestSent(true);
    }
  };

  useEffect(() => {
    chackIfIsFriend();
    checkRequestSent();
  }, [userId]);

  const sendFriendRequest = async (id) => {
    if (!id) {
      console.error("Invalid user ID");
      return;
    }

    try {
      const response = await fetchPostDataWithAuth(
        "/auth/user/add-to-friendrequests",
        { id }
      );

      if (response.data === "Friend request sent successfully") {
        console.log("Friend request sent successfully.");
        setRequestSent(true);
      } else {
        console.error(`Error sending friend request: ${response}`);
      }
    } catch (error) {
      console.error("Failed to send friend request:", error);
    }
  };

  const handlePress = () => {
    if (isFriend) {
      // Navigate to the chat page if the user is a friend
      navigation.navigate("Message", { item: item });
    } else if (!requestSent) {
      // Send a friend request if the user is not a friend and no request has been sent
      sendFriendRequest(item.id);
    }
  };

  return (
    <View style={styles.container}>
      <View>
        <Image
          style={styles.profilePicture}
          source={{
            uri: item?.ppicture || "../../../assets/images/user.png",
          }}
        />
      </View>
      <View style={styles.infoContainer}>
        <Text style={styles.username}>{item?.username || "Unknown User"}</Text>
      </View>
      <Pressable
        onPress={handlePress}
        style={[
          styles.addButton,
          isFriend && { backgroundColor: "#28a745" }, // Green for friends
          requestSent && !isFriend && { backgroundColor: "#6c757d" }, // Gray for sent requests
        ]}
        disabled={requestSent && !isFriend}
      >
        <Text style={styles.addButtonText}>
          {isFriend ? "Chat" : requestSent ? "Request Sent" : "Add Friend"}
        </Text>
      </Pressable>
    </View>
  );
};

export default User;

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    marginVertical: 10,
  },
  profilePicture: {
    width: 50,
    height: 50,
    borderRadius: 25,
    resizeMode: "cover",
  },
  infoContainer: {
    marginLeft: 12,
    flex: 1,
  },
  username: {
    fontSize: 16,
    fontWeight: "bold",
    color: "#333",
  },
  addButton: {
    backgroundColor: "#007bff", // Default button color (blue)
    paddingVertical: 10,
    paddingHorizontal: 12,
    borderRadius: 6,
    width: 105,
  },
  addButtonText: {
    textAlign: "center",
    color: "white", // Ensures text contrast
    fontSize: 13,
    fontWeight: "bold",
  },
});
