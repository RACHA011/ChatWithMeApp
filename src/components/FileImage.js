import React, { useEffect, useState } from "react";
import { ActivityIndicator, Image } from "react-native";
import { fetchGetDataWithAuth } from "../client/Client";

const FileImage = ({ fileId }) => {
  const [imageUri, setImageUri] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchImage = async (fileId) => {
    try {
      const response = await fetchGetDataWithAuth(`/chat/downloaded-file/${fileId}`);
      
      return response.data;
    } catch (err) {
      console.error("Error fetching image:", err);
      return null;
    }
  };

  useEffect(() => {
    const loadImage = async () => {
      const uri = await fetchImage(fileId);
      setImageUri(uri);
      setLoading(false);
    };

    loadImage();
  }, [fileId]);

  if (loading) {
    return <ActivityIndicator size="large" color="#0000ff" />;
  }

  return (
    <Image
      source={{ uri: imageUri }}
      style={{
        width: 200,
        height: 200,
        borderRadius: 8,
        marginBottom: 10,
      }}
    />
  );
};

export default FileImage;
