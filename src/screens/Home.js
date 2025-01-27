import { Ionicons, MaterialIcons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import React, { useEffect, useLayoutEffect, useState } from "react";
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  ActivityIndicator,
  TouchableOpacity,
  Pressable,
  Image,
} from "react-native";
import { fetchGetDataWithAuth } from "../client/Client";
import User from "./user/User";

const Home = () => {
  const navigation = useNavigation();
  const [users, setUsers] = useState([]);

  const navigateTo = (navigations) => {
    console.log("navigate to chat");
    navigation.navigate(navigations);
  };

  useLayoutEffect(() => {
    navigation.setOptions({
      headerTitle: "",
      headerLeft: () => (
        <Text style={{ fontSize: 16, fontWeight: "bold" }}>CWM</Text>
      ),
      headerRight: () => (
        <View style={{ flexDirection: "row", alignItems: "center", gap: 15 }}>
          <Pressable
            onPress={() => navigateTo("Chat")}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          >
            <Ionicons name="chatbox-ellipses-outline" size={28} color="black" />
          </Pressable>

          <Pressable
            onPress={() => navigation.navigate("Friends")}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          >
            <MaterialIcons name="people-outline" size={28} color="black" />
          </Pressable>
        </View>
      ),
    });
    //
  }, [navigation]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetchGetDataWithAuth("/auth/user/users");
        // console.log(response)
        const data = response.data;

        setUsers(data); // Set the entire list of users in state
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };
    fetchUsers();
  }, [users]);
  //
  return (
    <View style={{ flex: 1, padding: 10 }}>
      {users.length > 0 ? (
        <ScrollView>
          {users.map((item, index) => (
            <User key={index} item={item} />
          ))}
        </ScrollView>
      ) : (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="gray" />
        </View>
      )}
    </View>
  );
};

export default Home;

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
});
