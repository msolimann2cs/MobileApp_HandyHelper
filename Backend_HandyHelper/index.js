const fs = require('fs/promises');
const express = require('express');
const cors = require('cors');
const _ = require('lodash');
const mysql = require('mysql');
const bodyParser = require('body-parser');
const { v4: uuid } = require('uuid');


const app = express();
app.use(bodyParser.json());

const connection = mysql.createConnection({
    // connectionLimit: 10,
    host: 'db4free.net',
    user: 'handyhelper',
    password: 'handyhelper',
    database: 'auc_handyhelper'
  });
  
  connection.connect(function(err) {
    if (err) throw err;
    console.log("Connected to MySQL database!");
    });
    
// insert a new user
app.post('/adduser', (req, res) => {
    // extract user data from the request body
    const { national_id, username, email, password, gender, phone_number, date_of_birth, interests } = req.body;
  //console log all variables
    // console.log(national_id);
    // console.log(username);
    // console.log(email);
    // console.log(password);
    // console.log(gender);
    // console.log(phone_number);
    // console.log(date_of_birth);
    // console.log(rating);
    // console.log(interests);
    let query ='';
   // define a SQL query to insert a new user into the 'user' table
   if (interests){
    query = `INSERT INTO users (national_id, username, email, pass, gender, phone_number, date_of_birth, interests) 
    VALUES ('${national_id}', '${username}', '${email}', '${password}', '${gender}', '${phone_number}', '${date_of_birth}','${interests}')`;
   } else {
    query = `INSERT INTO users (national_id, username, email, pass, gender, phone_number, date_of_birth) 
    VALUES ('${national_id}', '${username}', '${email}', '${password}', '${gender}', '${phone_number}', '${date_of_birth}')`;
   }
  
    // execute the query using the MySQL connection pool
    connection.query(query, (err, result) => {
      if (err) {
        console.error(err);
        res.status(500).send('Error inserting user into database');
      } else {
        console.log(`User with national ID ${national_id} inserted into database`);
        res.status(201).send('User inserted into database');
      }
    });
    // connection.query(interestQuery, (err, result) => {
    //   if (err) {
    //     console.error(err);
    //     res.status(500).send('Error inserting interest into database');
    //   } else {
    //     console.log(`Interest ${interests} inserted into database`);
    //     res.status(201).send('Interest inserted into database');
    //   }
    // });
    
  });

  //
  app.get('/login', (req, res)=>{
    const {email, pass} = req.body;
    const query = "SELECT * FROM users WHERE email = ?";
    connection.query(query, [email],(err, results)=>{
      if(err) res.status(401).send("Failed to get User")
      if(results.length>0){
        if(results[0].pass === pass){
          res.status(201).send(results[0]);
        }else{
          res.status(401).send("Wrong email or password")
        }

      }
    })
  });


    

  

  //retrieve a user by national ID
  app.get('/users/:national_id', (req, res) => {
    // extract the national ID parameter from the request URL
    const national_id = req.params.national_id.replace(':','');
  
    // define a SQL query to select a user by national ID from the 'user' table
    const query = `SELECT * FROM users WHERE national_id = ${national_id}`;
  
    // execute the query using the MySQL connection pool
    connection.query(query, (err, result) => {
      if (err) {
        console.error(err);
        res.status(500).send('Error retrieving user from database');
      } else if (result.length === 0) {
        res.status(404).send('User not found');
      } else {
        const user = result[0];
        res.status(200).json(user);
      }
    });
  });
  

  // get all posts with of a certain user
app.get('/posts/:nationalID', (req, res) => {
  // extract the interest parameter from the request URL
  const national_id = req.params.nationalID.replace(':','');
  console.log(national_id);
  // define a SQL query to select all posts with the same interest from the 'post' table
  const query = `SELECT * FROM post WHERE national_id = '${national_id}'`;
  console.log(query);
  // execute the query using the MySQL connection pool
  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error retrieving posts from database');
    } else {
      res.status(200).json(result);
    }
  });
});

//get all posts
app.get('/posts', (req, res) => {

  // define a SQL query to select all posts with the same interest from the 'post' table
  const query = `SELECT * FROM post`;

  // execute the query using the MySQL connection pool
  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error retrieving posts from database');
    } else {
      res.status(200).json(result);
    }
  });
});

//post a new post
app.post('/newpost', (req, res) => {
  // extract post data from the request body
  const { national_id, title, content, category, service_date, service_time, location_lat, location_lon,initial_price } = req.body;

  // define a SQL query to insert a new post into the 'post' table
  const query = `INSERT INTO post (national_id, title, content, category, service_date, service_time, location, initial_price) 
                 VALUES ('${national_id}', '${title}', '${content}', '${category}', '${service_date}', '${service_time}', '${location_lat}' , '${location_lon}', ${initial_price})`;

  // execute the query using the MySQL connection pool
  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error inserting post into database');
    } else {
      console.log(`Post with title "${title}" inserted into database`);
      res.status(201).send('Post inserted into database');
    }
  });
});


app.listen(3000, () => console.log('Server started'));


