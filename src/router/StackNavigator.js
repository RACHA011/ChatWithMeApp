import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { NavigationContainer } from "@react-navigation/native";
import React from "react";

import LoginScreen from "../screens/auth/LoginScreen";
import RegisterScreen from "../screens/auth/RegisterScreen";
import Home from "../screens/Home";
import FriendScreen from "../screens/friends/FriendScreen";
import ChatScreen from "../screens/chat/ChatScreen";
import ChatMessageScreen from "../screens/chat/ChatMessageScreen";

const StackNavigator = () => {
  const Stack = createNativeStackNavigator();

  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Screen
          name="Login"
          component={LoginScreen}
          options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Register"
          component={RegisterScreen}
          options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Home"
          component={Home}
          // options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Friends"
          component={FriendScreen}
          // options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Chat"
          component={ChatScreen}
          // options={{ headerShown: false }}
        /><Stack.Screen
          name="Message"
          component={ChatMessageScreen}
          // options={{ headerShown: false }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default StackNavigator;
