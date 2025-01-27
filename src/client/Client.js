import axiosInstance from "./axiosInstance";
import AsyncStorage from "@react-native-async-storage/async-storage";

// Helper function for error handling
const handleRequestError = (uri, err) => {
  console.error(
    `Error with request to URL: ${uri}`,
    err.response?.data || err.message
  );
  throw err;
};

// Function for GET requests without authentication
const fetchGetData = async (uri) => {
  try {
    const response = await axiosInstance.get(uri);
    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};

// Function for POST requests without authentication
const fetchPostData = async (uri, payload) => {
  try {
    const response = await axiosInstance.post(uri, payload);
    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};

// Function for GET requests with authentication
const fetchGetDataWithAuth = async (uri) => {
  try {
    const token = await AsyncStorage.getItem("user_token");

    if (!token) {
      throw new Error("No token available for authenticated request.");
    }

    const response = await axiosInstance.get(uri, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};

// Function for POST requests with authentication
const fetchPostDataWithAuth = async (uri, payload) => {
  try {
    const token = await AsyncStorage.getItem("user_token");

    if (!token) {
      throw new Error("No token available for authenticated request.");
    }

    const response = await axiosInstance.post(uri, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};
const fetchPutDataWithAuth = async (uri, payload) => {
  try {
    const token = await AsyncStorage.getItem("user_token");

    if (!token) {
      throw new Error("No token available for authenticated request.");
    }

    const response = await axiosInstance.put(uri, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};

const fetchDeleteDataWithAuth = async (uri, payload) => {
  try {
    const token = await AsyncStorage.getItem("user_token");

    if (!token) {
      throw new Error("No token available for authenticated request.");
    }

    const response = await axiosInstance.delete(uri, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: payload, // Include payload in the config object
    });


    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};
const fetchPutDataArrayWithAuth = async (uri, payload) => {
  try {
    const token = await AsyncStorage.getItem("user_token");

    if (!token) {
      throw new Error("No token available for authenticated request.");
    }

    const response = await axiosInstance.put(uri, payload, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Bearer ${token}`,
      },
    });

    return response;
  } catch (err) {
    handleRequestError(uri, err);
  }
};

export {
  fetchGetData,
  fetchPostData,
  fetchGetDataWithAuth,
  fetchPostDataWithAuth,
  fetchPutDataWithAuth,
  fetchPutDataArrayWithAuth,
  fetchDeleteDataWithAuth,
};
