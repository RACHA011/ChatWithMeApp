import * as FileSystem from "expo-file-system";

export default async function encodeFileToBase64(filePath) {
  try {
    // Check if the file exists
    const fileInfo = await FileSystem.getInfoAsync(filePath);
    if (!fileInfo.exists) {
      console.warn(`File not found: ${filePath}`);
      return "data:image/png;base64,PLACEHOLDER"; // Fallback if file doesn't exist
    }

    // Read the file and encode it as Base64
    const base64String = await FileSystem.readAsStringAsync(filePath, {
      encoding: FileSystem.EncodingType.Base64,
    });
    return `data:image/png;base64,${base64String}`; // Add Base64 prefix
  } catch (error) {
    console.error("Error encoding file to Base64:", error);
    return "data:image/png;base64,PLACEHOLDER"; // Fallback
  }
}
