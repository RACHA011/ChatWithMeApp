import { StyleSheet, Text, View, ScrollView, Pressable } from "react-native";
import React, { useEffect, useState } from "react";
import { fetchGetDataWithAuth } from "../../client/Client";
import UserChat from "../../components/UserChat";

const ChatScreen = () => {
  const [friends, setFriends] = useState([]);
  useEffect(() => {
    const fetchFriends = async () => {
      // Fetch friends data
      try {
        const respond = await fetchGetDataWithAuth("/auth/user/friends");
        setFriends(respond.data);
        //   console.log(respond);
      } catch (err) {
        console.error("Failed to fetch friends", err);
      }
    };
    fetchFriends();
  }, []);
  return (
    <ScrollView showsHorizontalScrollIndicator={false}>
      {friends.map((item, index) => (
        <UserChat key={index} item={item} />
      ))}
    </ScrollView>
  );
};

export default ChatScreen;

const styles = StyleSheet.create({});
