module.exports = app => {
    const staff = require("../contoller/staff.controller");
  
    var router = require("express").Router();
  
    // Create a new Tutorial
    // router.post("/", tutorials.create);
  
    // Retrieve all Tutorials
    router.get("/all", staff.findAll);
  
    router.get("/:fnumber", staff.findByFNumber)
    
  // Update a Tutorial with id
    router.put("/:fnumber", staff.updateStatus);


    app.use('/api/invite', router);
  };