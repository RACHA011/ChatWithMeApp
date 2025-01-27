import {
  StyleSheet,
  Text,
  View,
  Pressable,
  ScrollView,
  ActivityIndicator,
} from "react-native";
import React, { useState, useEffect } from "react";
import { fetchGetDataWithAuth } from "../../client/Client"; // Assuming you have this utility
import FriendRequests from "../../components/FriendRequests";

const FriendScreen = () => {
  const [friendRequest, setFriendRequest] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetchGetDataWithAuth(
          "/auth/user/get-friendrequests"
        );
        // console.log(response);
        const data =  response.data;
        setFriendRequest(data);
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };
    fetchUsers();
  }, [friendRequest]);

  return (
    <ScrollView style={styles.screenContainer}>
      <Text style={styles.headerText}>Your Friend Requests</Text>
      {friendRequest.length > 0 ? (
        <View style={styles.requestListContainer}>
          {friendRequest.map((item, index) => (
            <FriendRequests
              key={index}
              item={item}
              friendRequest={friendRequest}
              setFriendRequest={setFriendRequest}
            />
          ))}
        </View>
      ) : (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#007BFF" />
          <Text style={styles.loadingText}>Loading friend requests...</Text>
        </View>
      )}
    </ScrollView>
  );
};

export default FriendScreen;

const styles = StyleSheet.create({
  screenContainer: {
    flex: 1,
    padding: 20,
    backgroundColor: "#F9F9F9",
  },
  headerText: {
    textAlign: "center",
    fontSize: 20,
    fontWeight: "bold",
    color: "#333",
    marginBottom: 15,
  },
  requestListContainer: {
    flex: 1,
    marginTop: 10,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: "#555",
  },
});
