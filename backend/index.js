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

  app.get('/combinedData', (req, res) => {
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
  GROUP BY
    post.title, post.initial_price, post.content, post.id, post.service_date, users.username, users.rating
    Order by post.service_date DESC
  
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
    const { national_id, title, location_lat, location_lon, date, time, compensation, description } = req.body;
    
    // Construct the SQL query
    const query = `
      INSERT INTO post (title, content, created_at, national_id, service_date, service_time, location_lat, location_lon, initial_price)
      VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?)
    `;
    
    // Execute the query
    connection.query(query, [title, description, national_id, date, time, location_lat, location_lon, compensation], (error, results) => {
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


  
// Start the server
app.listen(3000, () => {
  console.log('Server started on port 3000');
});
