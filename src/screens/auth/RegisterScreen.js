import { useNavigation } from "@react-navigation/native";
import React, { useEffect, useState } from "react";
import {
  Alert,
  Dimensions,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";
import { fetchGetData, fetchPostData } from "../../client/Client";

const RegisterScreen = () => {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const validator = require("validator");
  const navigation = useNavigation();

  useEffect(() => {
    const checkLoginStatus = async () => {
      // Fetch user data from server and check if they're logged in
      const response = await fetchGetDataWithAuth(
        "/auth/user/details-using-authentication"
      );
      console.log(response.data);

      // const token = AsyncStorage.getItem("user_token");
      if (response.data.username) {
        navigation.replace("Home");
      }
    };
    checkLoginStatus();
  }, []);
  const handleRegister = async () => {
    if (!username || !email || !password) {
      setErrorMessage("All fields are required.");
      return;
    }
    if (!validator.isEmail(email)) {
      setLoginError("Invalid email address");
      return;
    }
    if (password.length < 6) {
      setErrorMessage("Password must be at least 6 characters long.");
      return;
    }
    setErrorMessage("");
    const formData = {
      username: username,
      email: email,
      password: password,
    }
    

    try {
      const respond = await fetchPostData(
        "/auth/user/add",
        JSON.stringify(formData)
      );
      console.log("respond:",respond);
      if (respond === "Account added successfully") {
        Alert.alert(
          "Registrations successfully",
          "you have been registered successfully"
        );
        setEmail("");
        setUsername("");
        setPassword("");
        navigation.navigate("Login");
      } else {
        Alert.alert("Registration failed", respond);
        console.log(respond);
      }
    } catch (err) {
      Alert.alert("Registration failed", "An error occurred while registering");
      console.error(err);
    }
  };

  return (
    <View style={styles.wrapper}>
      <Text style={styles.heading}>Register</Text>
      <View style={styles.inputContainer}>
        <View style={styles.inputWrapper}>
          <Icon name="user" size={20} color="#555" style={styles.icon} />
          <TextInput
            style={styles.input}
            placeholder="Username"
            value={username}
            onChangeText={setUsername}
            placeholderTextColor="#888"
          />
        </View>
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

      {errorMessage ? (
        <Text style={styles.errorText}>{errorMessage}</Text>
      ) : null}

      <TouchableOpacity style={styles.registerButton} onPress={handleRegister}>
        <Text style={styles.registerButtonText}>Register</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigation.navigate("Login")}>
        <Text style={styles.loginText}>Already have an account? Login</Text>
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
    backgroundColor: "#f5f5f5",
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
    elevation: 2,
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
  registerButton: {
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
  registerButtonText: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "bold",
  },
  loginText: {
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

export default RegisterScreen;
