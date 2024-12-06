const { where } = require("sequelize");
const db = require("../models");
const Staff = db.staff;
const Op = db.Sequelize.Op;


// Retrieve all Staffs from the database.
exports.findAll = (req, res) => {
    const title = req.query.title;
    // var condition = title ? { title: { [Op.iLike]: `%${title}%` } } : null;
  
    Staff.findAll()
      .then(data => {
        res.send(data);
      })
      .catch(err => {
        res.status(500).send({
          message:
            err.message || "Some error occurred while retrieving Staffs."
        });
      });
};

// Find a single Staff with an id
exports.findByFNumber = (req, res) => {
    const fnumber = req.params.fnumber;
    console.log(fnumber)
    Staff.findOne({where : {fnumber}})
      .then(data => {
        if (data) {
          res.send(data);
        } else {
          res.status(404).send({
            message: `Cannot find Staff with id=${fnumber}.`
          });
        }
      })
      .catch(err => {
        res.status(500).send({
          message: "Error retrieving Staff with id=" + fnumber
        });
      });
};

// Update a Staff by the id in the request
exports.updateStatus = (req, res) => {
    const fnumber = req.params.fnumber;
    console.log(req.body)
    Staff.update(req.body, {
      where: { fnumber: fnumber }
    })
      .then(num => {
        if (num == 1) {
          res.send({
            message: "Staff was updated successfully."
          });
        } else {
          res.send({
            message: `Cannot update Staff with id=${fnumber}. Maybe Staff was not found or req.body is empty!`
          });
        }
      })
      .catch(err => {
        res.status(500).send({
          message: "Error updating Staff with id=" + fnumber
        });
      });
};

// Delete a Staff with the specified id in the request
exports.delete = (req, res) => {
    const id = req.params.id;

    Staff.destroy({
      where: { id: id }
    })
      .then(num => {
        if (num == 1) {
          res.send({
            message: "Staff was deleted successfully!"
          });
        } else {
          res.send({
            message: `Cannot delete Staff with id=${id}. Maybe Staff was not found!`
          });
        }
      })
      .catch(err => {
        res.status(500).send({
          message: "Could not delete Staff with id=" + id
        });
      });
};

// Delete all Staffs from the database.
exports.deleteAll = (req, res) => {
    Staff.destroy({
        where: {},
        truncate: false
      })
        .then(nums => {
          res.send({ message: `${nums} Staffs were deleted successfully!` });
        })
        .catch(err => {
          res.status(500).send({
            message:
              err.message || "Some error occurred while removing all Staffs."
          });
        });
};

// Find all published Staffs
exports.findAllPublished = (req, res) => {
    Staff.findAll({ where: { published: true } })
    .then(data => {
      res.send(data);
    })
    .catch(err => {
      res.status(500).send({
        message:
          err.message || "Some error occurred while retrieving Staffs."
      });
    });
}