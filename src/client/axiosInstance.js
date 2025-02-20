import axios from 'axios';

const axiosInstance = axios.create({
  // baseURL: "http://192.168.100.59:8080", 
  baseURL: "https://chatwithmeapp.onrender.com", 
  timeout: 50000, 
  headers: {
    "Content-Type": "application/json",
  },
});

export default axiosInstance;
