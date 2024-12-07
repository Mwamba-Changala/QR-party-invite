import axios from "axios";

const http = axios.create({
  baseURL: "https://192.168.13.180:9090/api",
  headers: {
    "Content-type": "application/json",
  }, // Adjust the URL as needed
});

export default http;
