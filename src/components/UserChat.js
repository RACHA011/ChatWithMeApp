import { Pressable, StyleSheet, Text, View, Image } from "react-native";
import React, { useEffect, useState } from "react";
import { useNavigation } from "@react-navigation/native";
import { fetchGetDataWithAuth } from "../client/Client";

const UserChat = ({ item }) => {
  const [chatRoomId, setChatRoomId] = useState("");
  const [message, setMessage] = useState("");

  const navigation = useNavigation();

  const getChatRoomId = async () => {
    try {
      const response = await fetchGetDataWithAuth(
        `/chat/get-chatroom-id/${item.id}`
      );

      setChatRoomId(response.data); // Set the chat room ID in state
    } catch (err) {
      console.error("Failed to fetch messages:", err);
    }
  };
  useEffect(() => {
    const fetchLastMessage = async () => {
      try {
        const response = await fetchGetDataWithAuth(
          `/chat/get-messages/${chatRoomId}/last-message`
        );
        if (message !== response.data) {
          setMessage(response.data);
        }
      } catch (err) {
        console.error("Failed to fetch last message:", err);
      }
    };
    fetchLastMessage();
  });

  useEffect(() => {
    getChatRoomId();
  });
  const lastMessage = () => {
    if (!message) return "";
    if (message.content) {
      switch (message.content.type) {
        case "TEXT":
          return message.content.data;
        case "IMAGE":
          return "IMAGE";
        case "VIDEO":
          return "VIDEO";
        case "VOICE":
          return "VOICE";
        case "FILE":
          return "FILE";
        default:
          console.error("Invalid message type:", messageType);
      }
    }
    return "";
  };

  const formatTime = (timestamp) => {
    const now = new Date();
    const timeStampDate = new Date(timestamp);

    // Calculate the difference in milliseconds and convert to days
    const oneDayInMs = 24 * 60 * 60 * 1000; // Milliseconds in a day
    const timeDifference = now - timeStampDate;
    const isMoreThanOneDay = timeDifference > oneDayInMs;

    if (isMoreThanOneDay) {
      // Return the date if it's more than one day
      return timeStampDate.toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    } else {
      // Otherwise, return the time
      return timeStampDate.toLocaleString("en-US", {
        hour: "numeric",
        minute: "numeric",
      });
    }
  };

  return (
    <Pressable
      onPress={() => navigation.navigate("Message", { item: item })}
      style={styles.container}
    >
      <Image
        style={styles.profilePicture}
        source={{
          uri: item?.ppicture || "../../../assets/images/user.png",
        }}
      />
      <View style={styles.messagecontainer}>
        <Text style={styles.username}>{item?.username || "Unknown User"}</Text>
        <Text style={styles.lastMessage}>{message && lastMessage()}</Text>
      </View>
      <View style={{}}>
        <Text style={styles.time}>
          {message && formatTime(message.timestamp)}
        </Text>
      </View>
    </Pressable>
  );
};

export default UserChat;

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    borderWidth: 0.7,
    borderBlockColor: "#D0D0D0",
    borderTopWidth: 0,
    borderRightWidth: 0,
    borderLeftWidth: 0,
    padding: 10,
    marginBottom: 10,
  },
  profilePicture: {
    width: 50,
    height: 50,
    borderRadius: 25,
    resizeMode: "cover",
  },
  messagecontainer: {
    flex: 1,
  },
  username: {
    fontSize: 15,
    fontWeight: "500",
  },
  lastMessage: {
    marginTop: 3,
    color: "gray",
  },
  time: {
    marginTop: 5,
    color: "#585858",
    fontSize: 12,
    fontWeight: "500",
    alignSelf: "flex-end",
  },
});
