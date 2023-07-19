const fs = require('fs/promises');
const express = require('express');
const cors = require('cors');
const _ = require('lodash');
//const mysql = require('mysql');
const mysql = require('mysql2');
const bodyParser = require('body-parser');
const { v4: uuid } = require('uuid');


const app = express();
app.use(bodyParser.json());

const connection = mysql.createPool({
  host: 'db4free.net',
  user: 'handyhelper',
  password: 'handyhelper',
  database: 'auc_handyhelper',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});
  
  // connection.connect(function(err) {
  //   if (err) throw err;
  //   console.log("Connected to MySQL database!");
  //   });
  connection.on('connection', (connection) => {
    console.log('New connection established.');
  });

  // console.log(connection.promise().isConnected() ? 'Connected to MySQL database!' : 'Not connected to MySQL database!');

  // const getConnection = () => {
  //   return new Promise((resolve, reject) => {
  //     pool.getConnection((err, connection) => {
  //       if (err) {
  //         reject(err);
  //       } else {
  //         resolve(connection);
  //       }
  //     });
  //   });
  // };
    
// insert a new user
app.post('/adduser', (req, res) => {
    // extract user data from the request body
    const { nat_ID, username, email, pass, gender, phone, date_of_birth, interest, description, notify,imageBytes} = req.body;
    let query1 =`INSERT INTO users (national_id, username, email, pass, gender, phone_number, date_of_birth`;
    let query2 =`) VALUES ('${nat_ID}', '${username}', '${email}', '${pass}', '${gender}', '${phone}', '${date_of_birth}'`;

  if (interest){
    query1 += `, interests`;
    query2 += `, '${interest}'`;
  }
  if (imageBytes!= null){
    query1 += `, image`;
    query2 += `, '${imageBytes}'`;
  }
  if (description){
    query1 += `, description`;
    query2 += `, '${description}'`;
  }
  if (notify){
    query1 += `, notify`;
    query2 += `, ${notify}`;
  }
  query1 += query2 + `)`;
  
    // execute the query using the MySQL connection pool
    connection.query(query1, (err, result) => {
      if (err) {
        console.error(err);
        res.status(500).send('Error inserting user into database');
      } else {
        console.log(`User with national ID ${nat_ID} inserted into database`);
        res.status(201).send('User inserted into database');
      }
    });
    
  });

  //
  app.get('/login', (req, res)=>{
    const email = req.query.email;
    const pass = req.query.pass;
    // const {email, pass} = req.body;
    const query = "SELECT * FROM users WHERE email = ?";
    connection.query(query, [email],(err, results)=>{
      if(err) res.status(401).send("Failed to get User")
      else{
      if(results.length>0){
        if(results[0].pass === pass){
          res.status(200).send(results[0]);
        }else{
          res.status(401).send("Wrong email or password")
        }

      }
    }
    })
  });

  //get application of a certain user to a certain service
  app.get('/apply', (req, res)=>{
    const {national_id, post_id}  = req.query;
    const query = "SELECT * FROM apply WHERE post_id = ? and national_id = ?";
    connection.query(query, [post_id, national_id], (err, results)=>{
      if(err) res.status(401).send("failed to get row");
      if(results.length > 0){
        res.json(results[0]);
      }else{
        res.send("0");
      }
    })
  })
app.get('/updateallapps', (req,res)=>{
  const {post_id} = req.query;
  const query = "SELECT * FROM apply WHERE post_id = ? and accepted_status = 'A' "

  connection.query(query,[post_id], (err, results)=>{
    if(err) return;
    if(results.length > 0){
      const query2 = "UPDATE apply set accepted_status = 'R' where post_id = ? and accepted_status = 'P'";
      connection.query(query2,[post_id], (err, results)=>{
        if (err) return;
        if(results){
          res.send("1");
        }
      })
    }else{
      res.send("0");
    }
  })

})
  app.post('/apply', (req, res)=>{
    const {post_id, national_id, apply_price} = req.body;
    const query = "INSERT INTO apply (post_id, national_id, apply_price) VALUES (?, ?, ?)";
    connection.query(query, [post_id, national_id, apply_price], (err, results)=>{
      if(err) {
        console.log(err);
        res.send("0")
      }
      if(results) res.send("1");
    })

  })


    

  

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

  const national_id = req.params.nationalID.replace(':','');
  console.log(national_id);

  const query = `SELECT * FROM post WHERE national_id = '${national_id}'`;
  console.log(query);

  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error retrieving posts from database');
    } else {
      res.status(200).json(result);
    }
  });
});

app.get('/post', (req, res)=>{
  const post_id = req.query.post_id;
  
    const query = "SELECT * FROM post WHERE id = ?";
    connection.query(query, [post_id], (err, results)=>{
      if(err) {
        console.log(err);
        res.send("failed to get row");
      }
      else{
      if(results.length > 0){
        res.json(results[0]);
      }else{
        res.json({message:"No entry"});
      }
    }
    })
})

//get all posts
app.get('/posts', (req, res) => {

  const query = `SELECT * FROM post p join users u on u.national_id = p.national_id where state = "n"`;

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
  const { national_id, title, content, category, service_date, service_time, location,initial_price } = req.body;

  const query = `INSERT INTO post (national_id, title, content, category, service_date, service_time, location, initial_price) 
                 VALUES ('${national_id}', '${title}', '${content}', '${category}', '${service_date}', '${service_time}', '${location}', ${initial_price})`;

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

//update status of post

app.get('/updatepoststatus', (req, res)=>{
  const{post_id, status } = req.query;
  const query = "UPDATE post SET state = ? where id = ?";
  connection.query(query, [status, post_id], (err, results)=>{
    if(err){
      console.log(err);
      res.json({done:0});
    }else{
      if(results.length > 0){
        console.log("hi")
        res.json({done:1});
      }
      res.json({done:1});
    }
  })
})


app.get('/rejectAll', (req, res)=>{
  const{post_id } = req.query;
  const query = "UPDATE apply SET state = 'R' where post_id = ? and state = 'P'";
  connection.query(query, [, post_id], (err, results)=>{
    if(err){
      console.log(err);
      res.json({done:0});
    }else{
      if(results.length > 0){
        console.log("hi")
        res.json({done:1});
      }
      res.json({done:1});
    }
  })
})




//add a new job application
app.post('/applies', (req, res) => {

  const { national_id, post_id, apply_price } = req.body;

  const query = `INSERT INTO apply (national_id, post_id, apply_price) 
                 VALUES ('${national_id}', ${post_id}, ${apply_price})`;

  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error inserting job application into database');
    } else {
      console.log(`Job application for post ID ${post_id} inserted into database`);
      res.status(201).send('Job application inserted into database');
    }
  });
});

// get all job applications of a certain user
app.get('/applies/user/:national_id', (req, res) => {
  const national_id = req.params.national_id.replace(':','');

  const query = `SELECT * FROM apply 
                 INNER JOIN post ON apply.post_id = post.id 
                 WHERE apply.national_id = '${national_id}'`;

  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error retrieving job applications from database');
    } else {
      res.status(200).json(result);
    }
  });
});

// get all job applications of a certain post
app.get('/applies/post/:post_id', (req, res) => {
  const post_id = req.params.post_id.replace(':','');

  const query = `SELECT * FROM apply 
                 INNER JOIN users ON apply.national_id = users.national_id 
                 WHERE apply.post_id = '${post_id}'`;
  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error retrieving job applications from database');
    } else {
      res.status(200).json(result);
    }
  });
});

 
app.use(bodyParser.json())
// Update user description by username
app.put('/users/:username/description', (req, res) => {
  const { username } = req.params;
  const { description } = req.body;

  const sql = 'UPDATE users SET description = ? WHERE username = ?';
  connection.query(sql, [description, username], (err, result) => {
    if (err) {
      console.error('Error updating user description: ', err);
      res.status(500).json({ error: 'Failed to update user description' });
      return;
    }
    console.log(`User "${username}" description updated successfully`);
    res.json({ message: 'User description updated successfully' });
  });
});


// Update user password by username
app.put('/users/:username/password', (req, res) => {
  const { username } = req.params;
  const { password } = req.body;

  const sql = 'UPDATE users SET pass = ? WHERE username = ?';
  connection.query(sql, [password, username], (err, result) => {
    if (err) {
      console.error('Error updating user password: ', err);
      res.status(500).json({ error: 'Failed to update user password' });
      return;
    }
    console.log(`User "${username}" password updated successfully`);
    res.json({ message: 'User password updated successfully' });
  });
});

// Update user email or phone number by username
app.put('/users/:username/contact', (req, res) => {
  const { username } = req.params;
  const { email, phoneNumber } = req.body;

  // Check if either email or phone number is provided
  if (!email && !phoneNumber) {
    res.status(400).json({ error: 'Either email or phone number must be provided' });
    return;
  }

  // Build the SQL query dynamically based on the provided fields
  let sql = 'UPDATE users SET';
  const values = [];

  if (email) {
    sql += ' email = ?,';
    values.push(email);
  }
  if (phoneNumber) {
    sql += ' phone_number = ?,';
    values.push(phoneNumber);
  }

  // Remove the trailing comma from the query
  sql = sql.slice(0, -1);

  // Add the WHERE clause to update the user with the specified username
  sql += ' WHERE username = ?';
  values.push(username);

  connection.query(sql, values, (err, result) => {
    if (err) {
      console.error('Error updating user contact information: ', err);
      res.status(500).json({ error: 'Failed to update user contact information' });
      return;
    }
    console.log(`User "${username}" contact information updated successfully`);
    res.json({ message: 'User contact information updated successfully' });
  });
});

// Define a route to retrieve the requests
app.get('/requests', (req, res) => {
  // Query to retrieve the requests
  const query = `SELECT post.title, post.initial_price, post.content, post.service_date, users.username, users.rating FROM apply INNER JOIN post ON apply.post_id = post.id INNER JOIN users ON apply.national_id = users.national_id`;

  // Execute the query
  connection.query(query, (error, results) => {
    if (error) {
      console.error('Error retrieving requests:', error);
      res.status(500).json({ error: 'Failed to retrieve requests' });
    } else {
      res.json(results);
    }
  });
});

// API endpoint to retrieve the number of applications for each request
app.get('/numOfApplicants', (req, res) => {
    const query = `
    SELECT post.id, post.title, COUNT(apply.post_id) AS num_applications
    FROM post
    LEFT JOIN apply ON post.id = apply.post_id AND apply.accepted_status <> 'R'
    GROUP BY post.id, post.title
    `;
  
    connection.query(query, (err, results) => {
      if (err) {
        console.error('Error executing query:', err);
        res.status(500).json({ error: 'An error occurred' });
      } else {
        res.json(results);
      }
    });
  });

  app.get('/combinedData/:nationalID', (req, res) => {
    const nationalID = req.params.nationalID;
  
    const query = `
      SELECT
        post.title, post.initial_price, post.content, post.service_date, post.id,
        users.username, users.rating,
        COUNT(CASE WHEN apply.accepted_status <> 'R' THEN 1 END) AS num_applications,
        EXISTS (
          SELECT 1
          FROM apply
          WHERE apply.post_id = post.id
            AND apply.accepted_status = 'A'
          LIMIT 1
        ) AS has_accepted_applicant
      FROM
        post
      LEFT JOIN
        apply ON post.id = apply.post_id
      INNER JOIN
        users ON post.national_id = users.national_id
      WHERE
        post.national_id = ?
      GROUP BY
        post.title, post.initial_price, post.content, post.id, post.service_date, users.username, users.rating
      ORDER BY
        post.service_date DESC
    `;
  
    connection.query(query, [nationalID], (err, results) => {
      if (err) {
        console.error('Error executing query:', err);
        res.status(500).json({ error: 'An error occurred' });
      } else {
        res.json(results);
      }
    });
  });
  
  
  // Define the route for retrieving applicants based on post ID
app.get('/applicants/:postId', (req, res) => {
    const postId = req.params.postId;
  
    // Execute the SQL query to fetch applicants
    const query = `
      SELECT users.username, users.email, users.rating, users.image, users.national_id, apply.apply_at, apply.accepted_status, apply.apply_price
      FROM apply
      JOIN users ON users.national_id = apply.national_id
      WHERE apply.post_id = ? AND apply.accepted_status != 'R'
    `;
  
    // Execute the query with the post ID as a parameter
    connection.query(query, [postId], (error, results) => {
      if (error) {
        console.error('Error retrieving applicants:', error);
        res.status(500).json({ error: 'An error occurred while retrieving applicants.' });
      } else {
        // Return the applicants as a JSON response
        res.json(results);
      }
    });
  });


  app.post('/updateApplicationStatus', (req, res) => {
    const { national_id, post_id, status } = req.body;
    const query = `
      UPDATE
        apply
      SET
        accepted_status = ?
      WHERE
        national_id = ?
        AND post_id = ?
    `;
    
    connection.query(query, [status, national_id, post_id], (error) => {
      if (error) {
        console.error('Error updating application status:', error);
        res.sendStatus(500);
      } else {
        res.sendStatus(200);
      }
    });
  });
  app.post('/createPost', (req, res) => {
    const { national_id, title, location_lat, location_lon, date, time, compensation, description, category } = req.body;
    
    // Construct the SQL query
    const query = `
      INSERT INTO post (title, content, created_at, national_id, service_date, service_time, location_lat, location_lon, initial_price, category)
      VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)
    `;
    
    // Execute the query
    connection.query(query, [title, description, national_id, date, time, location_lat, location_lon, compensation, category], (error, results) => {
      if (error) {
        console.error('Error creating post:', error);
        res.sendStatus(500);
      } else {
        res.sendStatus(200);
      }
    });
  });
  
  
  
  app.get('/getApplicationStatus', (req, res) => {
    const national_id = req.query.national_id;
    const post_id = req.query.post_id;
    const query = `
      SELECT accepted_status
      FROM apply
      WHERE national_id = ?
      AND post_id = ?
    `;
    
    connection.query(query, [national_id, post_id], (error, results) => {
      if (error) {
        console.error('Error retrieving application status:', error);
        res.sendStatus(500);
      } else {
        if (results.length > 0) {
          const acceptedStatus = results[0].accepted_status;
          res.status(200).json({ acceptedStatus });
        } else {
          res.status(404).json({ message: 'Application not found' });
        }
      }
    });
  });
  
// Retrieve the posts that the user applied to along with the count of non-rejected applications, ordered by service_date
app.get('/appliedPosts/:userId', (req, res) => {
  const userId = req.params.userId;

  const query = `
    SELECT post.id, post.title, post.content, post.service_date, post.service_time, post.location_lat, post.location_lon, post.category, post.initial_price, post.state, apply.accepted_status,
    COUNT(CASE WHEN apply.accepted_status != 'R' THEN 1 END) AS num_applications
    FROM post
    INNER JOIN apply ON post.id = apply.post_id
    WHERE apply.national_id = ?
    GROUP BY post.id
    ORDER BY post.service_date ASC
  `;

  connection.query(query, [userId], (error, results) => {
    if (error) {
      console.error('Error retrieving applied posts:', error);
      res.status(500).json({ error: 'Failed to retrieve applied posts' });
    } else {
      res.json(results);
    }
  });
});

// API to get specific columns from the users table
// image, rating, interests, and description
app.get('/users/:username/details', (req, res) => {
  const username = req.params.username;

  const query = `SELECT image, rating, interests, description FROM users WHERE username = '${username}'`;

  connection.query(query, (err, result) => {
    if (err) {
      console.error(err);
      res.status(500).send('Error retrieving user details from the database');
    } else {
      if (result.length > 0) {
        const userDetails = result[0];
        res.status(200).json(userDetails);
      } else {
        console.log('No user found in the database.'); // Add this line to log the scenario.
        res.status(404).send('User not found');
      }
    }
  });
});

// Update user password by username
app.put('/users/:username/password', (req, res) => {
  const { username } = req.params;
  const { password } = req.body;

  const sql = 'UPDATE users SET pass = ? WHERE username = ?';
  connection.query(sql, [password, username], (err, result) => {
    if (err) {
      console.error('Error updating user password: ', err);
      res.status(500).json({ error: 'Failed to update user password' });
      return;
    }
    console.log(`User "${username}" password updated successfully`);
    res.json({ message: 'User password updated successfully' });
  });
});


app.post('/update_interests', (req, res) => {
  const { username, interests } = req.body;

  // Update the interests column in the user table for the matching username
  const sql = 'UPDATE users SET interests = ? WHERE username = ?';
  connection.query(sql, [interests, username], (err, result) => {
    if (err) {
      console.error('Error updating interests:', err);
      res.status(500).json({ error: 'An error occurred while updating interests' });
    } else {
      if (result.affectedRows === 0) {
        res.status(404).json({ error: 'User not found' });
      } else {
        res.json({ message: 'Interests updated successfully' });
      }
    }
  });
});

// API endpoint to handle user updates
app.post('/update_user', (req, res) => {
  const { username, email, newUsername, phone_number } = req.body;

  // Update the email, username, and phone_number columns in the user table for the matching username
  const sql = 'UPDATE users SET email = ?, username = ?, phone_number = ? WHERE username = ?';
  connection.query(sql, [email, newUsername, phone_number, username], (err, result) => {
    if (err) {
      console.error('Error updating user:', err);
      res.status(500).json({ error: 'An error occurred while updating user' });
    } else {
      if (result.affectedRows === 0) {
        res.status(404).json({ error: 'User not found' });
      } else {
        res.json({ message: 'User updated successfully' });
      }
    }
  });
});

app.get('/user/:userId', (req, res) => {
  const userId = req.params.userId;

  // Retrieve user's name, interests, description, and rating from the database
  const query = `SELECT username, interests, description, rating FROM users WHERE national_id = ?`;
  connection.query(query, [userId], (error, results) => {
    if (error) {
      console.error('Error retrieving user information:', error);
      res.status(500).send('Failed to retrieve user information');
    } else {
      if (results.length > 0) {
        const user = results[0];
        res.status(200).json({
          name: user.username,
          interests: user.interests,
          description: user.description,
          rating: user.rating // Include the rating field in the JSON response
        });
      } else {
        console.log('User not found in the database.'); // Add this line to log the scenario.
        res.status(404).send('User not found');
      }
    }
  });
});




app.listen(3000, () => console.log('Server started'));


