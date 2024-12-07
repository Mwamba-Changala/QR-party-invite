const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const path = require("path");
const fs = require("fs");
const https = require("https");

const app = express();

// Load the certificate and key
const options = {
  key: fs.readFileSync("./localhost+2-key.pem"), // Path to your private key
  cert: fs.readFileSync("./localhost+2.pem"),   // Path to your certificate
};

// CORS Configuration
var corsOptions = {
  origin: true, // Allow all origins for testing purposes
};

const staticPath = __dirname + "/views/";
app.use(express.static(staticPath));
app.use(cors(corsOptions));

// Parse requests of content-type - application/json
app.use(bodyParser.json());

// Parse requests of content-type - application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));

// Simple route
app.get("/", (req, res) => {
  res.json({ message: "A message" });
});

// Include routes
require("./routes/tutorial.routes")(app);
require("./routes/staff.routes")(app);

// Database synchronization
const db = require("./models");
db.sequelize
  .sync({})
  .then(() => {
    console.log("Synced db.");
  })
  .catch((err) => {
    console.log("Failed to sync db: " + err.message);
  });

// Start the HTTPS server
const PORT = process.env.PORT || 9090;
https.createServer(options, app).listen(PORT, '192.168.13.180', () => {
  console.log(`HTTPS Server running at https://localhost:${PORT}`);
});

// const PORT = process.env.PORT || 9090;
// app.listen(PORT , () => {
//   console.log(`HTTP Server running at http://localhost:${PORT}`);
// });

// const express = require("express");
// const bodyParser = require("body-parser");
// const cors = require("cors");
// const path = require("path");

// const app = express();

// // CORS Configuration
// var corsOptions = {
//   origin: true, // Allow all origins for testing purposes
// };

// app.use(cors(corsOptions));

// // Parse requests of content-type - application/json
// app.use(bodyParser.json());

// // Parse requests of content-type - application/x-www-form-urlencoded
// app.use(express.urlencoded({ extended: true }));

// // Serve static files from the Vue app's dist folder
// const vueAppPath = path.join(__dirname, "views/dist");
// app.use(express.static(vueAppPath));

// // Catch-all route to handle Vue app routing (SPA support)
// app.get("*", (req, res) => {
//   res.sendFile(path.join(vueAppPath, "index.html"));
// });

// // Include routes
// require("./routes/tutorial.routes")(app);
// require("./routes/staff.routes")(app);

// // Database synchronization
// const db = require("./models");
// db.sequelize
//   .sync({})
//   .then(() => {
//     console.log("Synced db.");
//   })
//   .catch((err) => {
//     console.log("Failed to sync db: " + err.message);
//   });

// // Start the HTTPS server
// // const PORT = process.env.PORT || 9090;
// // createServer(options, app).listen(PORT, '192.168.0.162', () => {
// //   console.log(`HTTPS Server running at https://localhost:${PORT}`);
// // });

// const PORT = process.env.PORT || 9090;
// app.listen(PORT, () => {
//   console.log(`Server is running on port ${PORT}.`);
// });

