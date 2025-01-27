import {
  AntDesign,
  Entypo,
  Feather,
  FontAwesome,
  Ionicons,
  MaterialIcons,
} from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import * as ImagePicker from "expo-image-picker";
import React, { useEffect, useLayoutEffect, useRef, useState } from "react";
import {
  Image,
  Keyboard,
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import EmojiModal from "react-native-emoji-modal"; // Import EmojiModal
import {
  fetchDeleteDataWithAuth,
  fetchGetDataWithAuth,
  fetchPostDataWithAuth,
  fetchPutDataArrayWithAuth,
} from "../../client/Client";
import encodeFileToBase64 from "../../client/encoder";
import FileImage from "../../components/FileImage";

const ChatMessageScreen = ({ route }) => {
  const [showEmojiSelector, setShowEmojiSelector] = useState(false); // State for showing emoji selector
  const [message, setMessage] = useState(""); // State for the message input
  const [messages, setMessages] = useState([]); // State for the message input
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false); // State to track keyboard visibility
  const navigation = useNavigation();
  const { item } = route.params;
  const [receiverId] = useState(item.id);
  const [deletedMessages, setDeletedMessages] = useState([]);
  const [fail, setFail] = useState([]);
  const [selectedMessage, setSelectedMessage] = useState([]);
  const [video, setVideo] = useState("");
  const [voice, setVoice] = useState("");
  const [file, setFile] = useState("");
  const [chatRoomId, setChatRoomId] = useState("");
  const [modalVisible, setModalVisible] = useState(false);
  const scrollViewRef = useRef(null);

  useLayoutEffect(() => {
    navigation.setOptions({
      headerTitle: "",
      headerLeft: () => (
        <View style={{ flexDirection: "row", gap: 10, alignItems: "center" }}>
          <Pressable onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={24} color="gray" />
          </Pressable>
          {selectedMessage.length > 0 ? (
            <View>
              <Text style={{ marginLeft: 5, fontSize: 16, fontWeight: "500" }}>
                {selectedMessage.length}
              </Text>
            </View>
          ) : (
            <Pressable
              style={{ flexDirection: "row", alignItems: "center", gap: 5 }}
            >
              <Image
                source={{ uri: item.ppicture }}
                style={{
                  width: 35,
                  height: 35,
                  borderRadius: 15,
                  resizeMode: "cover",
                }}
              />
              <Text style={{ marginLeft: 5, fontSize: 15, fontWeight: "bold" }}>
                {item?.username}
              </Text>
            </Pressable>
          )}
        </View>
      ),
      headerRight: () => (
        <View>
          {selectedMessage.length > 0 ? (
            <View
              style={{ flexDirection: "row", alignItems: "center", gap: 10 }}
            >
              <Pressable>
                <Ionicons name="arrow-undo" size={24} color="black" />
              </Pressable>
              <Pressable>
                <Ionicons name="arrow-redo" size={24} color="black" />
              </Pressable>
              <Pressable>
                <FontAwesome name="star" size={24} color="black" />
              </Pressable>
              <Pressable onPress={() => handleDelete()}>
                <MaterialIcons name="delete" size={24} color="black" />
              </Pressable>
            </View>
          ) : (
            <Pressable>
              <MaterialIcons name="menu" size={24} color="black" />
            </Pressable>
          )}
        </View>
      ),
    });
    //
  }, [navigation, selectedMessage]);

  useEffect(() => {
    scrollToButtom();
  }, []);
  const scrollToButtom = () => {
    if (scrollViewRef.current) {
      scrollViewRef.current.scrollToEnd({ animated: false });
    }
  };

  const handleConntentSizeChange = () => {
    scrollToButtom();
  };
  const getChatRoomId = async () => {
    try {
      const response = await fetchGetDataWithAuth(
        `/chat/get-chatroom-id/${receiverId}`
      );

      setChatRoomId(response.data); // Set the chat room ID in state
    } catch (err) {
      console.error("Failed to fetch messages:", err);
    }
  };

  const fetchMessages = async () => {
    try {
      const response = await fetchGetDataWithAuth(
        `/chat/get-messages/${chatRoomId}`
      );
      if (message) {
        // If message exists, check if messages is different from response.data
        if (JSON.stringify(messages) !== JSON.stringify(response.data)) {
          // Update messages only if they're different
          setMessages(response.data);
        }
      } else {
        // If message does not exist, set messages to response.data
        setMessages(response.data);
      }
      
    } catch (err) {
      console.error("Failed to fetch messages:", err);
    }
  };

  useEffect(() => {
    getChatRoomId();
    fetchMessages();
  }, [chatRoomId]);
  useEffect(() => {
    fetchMessages();
  });

  // TODO:to be deleted unless i consider it to be usefull
  useEffect(() => {
    const fetchRecepients = async () => {
      try {
        // const response = await fetchGetDataWithAuth(`/auth/user/${receiverId}`);
        // setRecepeantData(response.data); // Set the recipient's data in state
      } catch (err) {
        console.error("Failed to fetch recipients:", err);
      }
    };
    fetchRecepients();
  }, [receiverId]);

  // Handle emoji button press
  const handleEmojiPress = () => {
    if (isKeyboardVisible) {
      Keyboard.dismiss(); // Dismiss keyboard if it's visible
      setShowEmojiSelector((prevState) => !prevState); // Toggle emoji selector if keyboard is not visible
    } else {
      setShowEmojiSelector((prevState) => !prevState); // Toggle emoji selector if keyboard is not visible
    }
  };

  // handle delete
  const handleDelete = async () => {
    try {
      const messageIdDTO = selectedMessage.map((item) => ({
        id: item.id,
        chatRoomId: item.chatRoomId,
      }));
      console.log(JSON.stringify(messageIdDTO));
      const formData = new FormData();
      selectedMessage.forEach((file) => {
        formData.append("messageIdDTO", file);
      });
      console.log(formData._parts);

      // Call the API to delete the messages
      const response = await fetchDeleteDataWithAuth(
        `/chat/delete-messages`,
        selectedMessage
      );
      console.log(response.data); // Log the response to inspect the data structure

      // Check if the response data has the Success and Failed properties
      setDeletedMessages(response.data.Success || []); // Ensure deletedMessages is an array
      setFail(response.data.Failed || []); // Ensure fail is an array

      // Show the modal after deletion
      setModalVisible(true);

      // Optionally, hide the modal after a delay
      setTimeout(() => setModalVisible(false), 3000);

      // Clear the selected messages and fetch messages again
      setSelectedMessage([]);
      fetchMessages();
    } catch (err) {
      console.error("Failed to delete messages:", err);
    }
  };

  const pickImage = async () => {
    try {
      let result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ["images"], // Corrected property
        // allowsEditing: true,
        // aspect: [4, 3],
        // quality: 1,
      });

      if (!result.canceled) {
        const imageResult = result.assets[0];
        // console.log(result)
        // Send the Blob
        handleSend("IMAGE", imageResult); // Pass the Blob to your handler
      }
    } catch (error) {
      console.error("Error picking or converting image:", error);
    }
  };

  // Handle send button press
  const handleSend = async (messageType, fileBytes) => {
    try {
      const formdata = new FormData();
      formdata.append("receiverId", receiverId);

      let content = {}; // Initialize content object

      // Handle different message types
      switch (messageType) {
        case "TEXT":
          content = {
            type: messageType,
            data: message,
          };
          break;
        case "IMAGE":
          content = {
            type: messageType,
            data: "",
          };
          break;
        case "VIDEO":
          content = {
            type: messageType,
            data: await encodeFileToBase64(video),
          };
          break;
        case "VOICE":
          content = {
            type: messageType,
            data: await encodeFileToBase64(voice),
          };
          break;
        case "FILE":
          content = {
            type: messageType,
            data: await encodeFileToBase64(file),
          };
          break;
        default:
          console.error("Invalid message type:", messageType);
          return; // Exit the function
      }

      // Append content as a JSON string
      formdata.append("content", content);

      // Add other metadata
      formdata.append("timestamp", new Date().toISOString());
      formdata.append("chatType", "PERSON_TO_PERSON");
      formdata.append("chatRoomId", "");

      // Send the form data
      const response = await fetchPostDataWithAuth("/chat/send", formdata);

      // TODO: the respond data was changed
      if (
        response.data === "Message sent successfully" &&
        messageType === "TEXT"
      ) {
        setMessage(""); // Clear the message input
        setFile(""); // Clear the file

        setVideo(""); // Clear the video
        setVoice(""); // Clear the voice
        fetchMessages();
      } else if (typeof response.data === "number" && messageType !== "TEXT") {
        const image = await fileBytes;
        const formImageData = new FormData();
        formImageData.append("file", {
          uri: image.uri,
          type: image.mimeType,
          name: image.fileName, // File name
        });

        const fileResponse = await fetchPutDataArrayWithAuth(
          `/chat/send-file/${chatRoomId}/${response.data}`,
          formImageData
        );

        if (fileResponse.data === "File sent successfully") {
          setMessage(""); // Clear the message input
          setFile(""); // Clear the file
          setVideo(""); // Clear the video
          setVoice(""); // Clear the voice
          fetchMessages();
        } else {
          console.error("Failed to send file:", fileResponse.statusText);
        }
      }
    } catch (error) {
      console.error("Error while sending message:", error);
    }
  };

  // Effect to listen to keyboard visibility changes
  useEffect(() => {
    const keyboardDidShowListener = Keyboard.addListener(
      "keyboardDidShow",
      () => {
        setIsKeyboardVisible(true); // Set keyboard visibility to true when keyboard is shown
        setShowEmojiSelector(false); // Hide emoji selector when keyboard is shown
      }
    );

    const keyboardDidHideListener = Keyboard.addListener(
      "keyboardDidHide",
      () => {
        setIsKeyboardVisible(false); // Set keyboard visibility to false when keyboard is hidden
      }
    );

    // Clean up listeners when the component unmounts
    return () => {
      keyboardDidHideListener.remove();
      keyboardDidShowListener.remove();
    };
  }, []);

  // const delay = (ms) => new Promise((res) => setTimeout(res, ms));
  // const wait2s = async () => {
  //   await delay(2000);
  // };

  const formatTime = (timestamp) => {
    const options = { hour: "numeric", minute: "numeric" };
    return new Date(timestamp).toLocaleString("en-US", options);
  };

  const handleSelectedMessage = (messages) => {
    const isSelected = selectedMessage.some(
      (item) =>
        item.id === messages.id && item.chatRoomId === messages.chatRoomId
    );

    if (isSelected) {
      setSelectedMessage(
        selectedMessage.filter((item) => item.id !== messages.id)
      );
    } else {
      setSelectedMessage([
        ...selectedMessage,
        { id: messages.id, chatRoomId: messages.chatRoomId },
      ]);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.container} behavior="padding">
      <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <ScrollView
          ref={scrollViewRef}
          contentContainerStyle={{ flexGrow: 1 }}
          onContentSizeChange={handleConntentSizeChange}
        >
          <View>
            {modalVisible && (
              <View style={styles.modalContainer}>
                <Text style={styles.modalText}>
                  {deletedMessages.length ? deletedMessages.length : "No"}{" "}
                  Message deleted
                  {fail.length ? `, ${fail.length} failed to delete` : ""}
                </Text>
              </View>
            )}
          </View>

          {messages.length > 0 ? (
            messages.map((item, index) => {
              const isSelected = selectedMessage.some(
                (items) =>
                  items.id === item.id && items.chatRoomId === item.chatRoomId
              );
              if (item.content.type === "TEXT") {
                return (
                  <View>
                    <Pressable
                      onLongPress={() => handleSelectedMessage(item)}
                      key={index}
                      style={[
                        item?.receiverId !== receiverId
                          ? {
                              alignSelf: "flex-start",
                              backgroundColor: "white",
                              padding: 10,
                              maxWidth: "60%",
                              borderRadius: 8,
                              margin: 5,
                            }
                          : {
                              alignSelf: "flex-end",
                              backgroundColor: "#DCF8C6",
                              padding: 10,
                              maxWidth: "60%",
                              borderRadius: 8,
                              margin: 5,
                            },
                        isSelected && {
                          width: "100%",
                          backgroundColor: "#F0ffff",
                        },
                      ]}
                    >
                      <Text
                        style={{
                          fontSize: 13,
                          textAlign: isSelected ? "right" : "left",
                        }}
                      >
                        {item.content.data}
                      </Text>
                      <Text
                        style={{
                          fontSize: 9,
                          textAlign: "right",
                          color: "gray",
                          marginTop: 5,
                        }}
                      >
                        {formatTime(item.timestamp)}
                      </Text>
                    </Pressable>
                  </View>
                );
              }
              if (item.content.type === "IMAGE") {
                return (
                  <Pressable
                    key={index}
                    style={[
                      item?.receiverId !== receiverId
                        ? {
                            alignSelf: "flex-start",
                            backgroundColor: "white",
                            padding: 10,
                            maxWidth: "60%",
                            borderRadius: 8,
                            margin: 5,
                          }
                        : {
                            alignSelf: "flex-end",
                            backgroundColor: "#DCF8C6",
                            padding: 10,
                            maxWidth: "60%",
                            borderRadius: 8,
                            margin: 5,
                          },
                      isSelected && {
                        backgroundColor: "#F0ffff",
                      },
                    ]}
                  >
                    <View>
                      <FileImage fileId={item.content.data} />

                      <Text
                        style={{
                          fontSize: 9,
                          textAlign: "right",
                          color: "gray",
                          // position:"absolute",
                          marginTop: 5,
                        }}
                      >
                        {formatTime(item.timestamp)}
                      </Text>
                    </View>
                  </Pressable>
                );
              }
            })
          ) : (
            <Text style={{ textAlign: "center", marginTop: 20, color: "gray" }}>
              Loading messages...
            </Text>
          )}
        </ScrollView>
      </TouchableWithoutFeedback>

      {showEmojiSelector && !isKeyboardVisible && (
        <EmojiModal
          onEmojiSelected={(emoji) => {
            setMessage((prevMessage) => prevMessage + emoji); // Add selected emoji to the message
          }}
        />
      )}

      <View style={styles.inputContainer}>
        {/* Emoji button */}
        <Entypo
          onPress={handleEmojiPress}
          style={styles.icon}
          name="emoji-happy"
          size={24}
          color="gray"
        />

        {/* Text input for typing messages */}
        <TextInput
          value={message}
          onChangeText={setMessage}
          style={styles.textInput}
          placeholder="Type your message..."
        />

        <View style={styles.iconGroup}>
          {/* Camera button */}
          <AntDesign
            onPress={pickImage}
            style={styles.icon}
            name="camerao"
            size={24}
            color="gray"
          />
          {/* Microphone button */}
          <Feather
            // onPress={() => handleSend("VOICE")}
            style={styles.icon}
            name="mic"
            size={24}
            color="gray"
          />
        </View>

        {/* Send button */}
        <Pressable style={styles.sendButton} onPress={() => handleSend("TEXT")}>
          <Text style={styles.sendText}>Send</Text>
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
};

export default ChatMessageScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F0F0F0",
  },
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    padding: 10,
    borderTopWidth: 1,
    borderTopColor: "#ddd",
  },
  textInput: {
    flex: 1,
    height: 40,
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 10,
    paddingHorizontal: 10,
    backgroundColor: "#fff",
  },
  icon: {
    marginHorizontal: 5,
  },
  iconGroup: {
    flexDirection: "row",
    alignItems: "center",
  },
  sendButton: {
    backgroundColor: "#007bff",
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 20,
    marginLeft: 10,
    justifyContent: "center",
    alignItems: "center",
  },
  sendText: {
    fontSize: 14,
    fontWeight: "bold",
    color: "#fff",
  },
  modalContainer: {
    backgroundColor: "#ffe5e7", // Softer reddish-pink for a modern look
    borderColor: "#ffccd1", // Subtle border for contrast
    borderWidth: 1,
    borderRadius: 12, // More rounded corners
    width: "90%", // Slightly smaller width for better alignment
    alignSelf: "center", // Center the modal horizontally
    paddingVertical: 15, // Vertical padding for a clean, spacious feel
    paddingHorizontal: 20, // Horizontal padding for symmetry
    marginBottom: 16, // Reduced margin for a closer modern layout
    shadowColor: "#000", // Subtle shadow for depth
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 3, // Shadow for Android
  },
  modalText: {
    color: "#d32f2f", // Vibrant red for text
    textAlign: "center", // Center-align the text
    fontSize: 18, // Slightly larger text for better visibility
    fontWeight: "600", // Semi-bold for a balanced modern feel
  },
});
