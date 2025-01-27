import { useNavigation } from "@react-navigation/native";
import React, { useEffect, useState } from "react";
import {
  Dimensions,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";
import { fetchPostData, fetchGetDataWithAuth } from "../../client/Client";
import AsyncStorage from "@react-native-async-storage/async-storage";

const LoginScreen = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loginError, setLoginError] = useState("");
  const validator = require("validator");
  const navigation = useNavigation();

  useEffect(() => {
    const checkLoginStatus = async () => {
      // Fetch user data from server and check if they're logged in
      const response = await fetchGetDataWithAuth(
        "/auth/user/details-using-authentication"
      );
      // console.log(response);

      // const token = AsyncStorage.getItem("user_token");
      if (response.data.username) {
        navigation.replace("Home");
      }
    };
    checkLoginStatus();
  }, []);

  const handleLogin = async () => {
    if (!validator.isEmail(email)) {
      setLoginError("Invalid email address");
      return;
    }

    if (password.length < 6) {
      setLoginError("Password must be at least 6 characters long");
      return;
    }

    try {
      // URL-encoded form data
      const formData = { email: email, password: password };

      const response = await fetchPostData(
        "/auth/token",
        JSON.stringify(formData)
      );
      // console.log(response);
      const token = response.data.token;
      if (token) {
        console.log(`${email}  loged in`);
        await AsyncStorage.setItem("user_token", token);
        navigation.navigate("Home");
      } else {
        console.log("Invalid credentials");
        setLoginError("Invalid email or password");
      }
    } catch (error) {
      console.error("Login error:", error);
      setLoginError("An error occurred. Please try again.");
    }
  };

  const handleForgotPassword = () => {
    // ill first handle it in the backend
    console.log("Forgot Password Pressed");
  };

  const handleRegister = () => {
    navigation.navigate("Register");
  };

  return (
    <View style={styles.wrapper}>
      <Text style={styles.heading}>Login</Text>
      <View style={styles.inputContainer}>
        <View style={styles.inputWrapper}>
          <Icon name="envelope" size={20} color="#555" style={styles.icon} />
          <TextInput
            style={styles.input}
            placeholder="Email"
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            placeholderTextColor="#888"
          />
        </View>
        <View style={styles.inputWrapper}>
          <Icon name="lock" size={20} color="#555" style={styles.icon} />
          <TextInput
            style={styles.input}
            placeholder="Password"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            placeholderTextColor="#888"
          />
        </View>
      </View>

      {loginError ? <Text style={styles.errorText}>{loginError}</Text> : null}

      <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
        <Text style={styles.loginButtonText}>Login</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={handleForgotPassword}>
        <Text style={styles.forgotPassword}>Forgot Password?</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={handleRegister}>
        <Text style={styles.registerText}>Don't have an account? Sign Up</Text>
      </TouchableOpacity>
    </View>
  );
};

const { width } = Dimensions.get("window");

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f5f5f5", // Light background color
    padding: 16,
  },
  heading: {
    fontSize: 32,
    fontWeight: "bold",
    color: "#333",
    marginBottom: 24,
  },
  inputContainer: {
    width: "100%",
    marginBottom: 20,
  },
  inputWrapper: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#fff",
    borderRadius: 10,
    paddingHorizontal: 10,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: "#ddd",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 5,
    elevation: 2, // For Android shadow
  },
  input: {
    flex: 1,
    height: 50,
    fontSize: 16,
    color: "#333",
  },
  icon: {
    marginRight: 10,
  },
  loginButton: {
    backgroundColor: "#4CAF50",
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
    width: width * 0.85,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.2,
    shadowRadius: 5,
    elevation: 2,
  },
  loginButtonText: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "bold",
  },
  forgotPassword: {
    color: "#4CAF50",
    marginTop: 12,
    fontSize: 14,
  },
  registerText: {
    color: "#333",
    marginTop: 16,
    fontSize: 14,
  },
  errorText: {
    color: "red",
    marginBottom: 12,
    fontSize: 14,
  },
});

export default LoginScreen;
