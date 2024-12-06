const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const path = require('path');

const app = express();

var corsOptions = {
  origin: ["http://localhost:8081", "http://localhost:8021"]
};
const staticPath = __dirname + '/views/';

app.use(express.static(staticPath));

app.use(cors(corsOptions));

// parse requests of content-type - application/json
app.use(bodyParser.json());

// parse requests of content-type - application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));

// simple route
app.get("/", (req, res) => {
  // res.sendFile(path.resolve(staticPath, 'index.html'));
  res.json({message: "A message"})
});

require("./routes/tutorial.routes")(app);
require("./routes/staff.routes")(app);



// set port, listen for requests
const PORT = process.env.PORT || 9090;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}.`);
});

const db = require("./models");
db.sequelize.sync({})
  .then(() => {
    console.log("Synced db.");
  })
  .catch((err) => {
    console.log("Failed to sync db: " + err.message);
  });

 