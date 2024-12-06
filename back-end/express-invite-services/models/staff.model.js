module.exports = (sequelize, Sequelize) => {
    const staff = sequelize.define("staff", {
      fnumber: {
        type: Sequelize.STRING
      },
      name: {
        type: Sequelize.STRING
      },
      email: {
        type: Sequelize.STRING
      },
      status: {
        type: Sequelize.BOOLEAN
      }
    });
  
    return staff;
  };