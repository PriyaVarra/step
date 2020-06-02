// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.CommentData;
import java.io.IOException;
import java.text.*;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example comments. */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Create timestamp with date and time from comment
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");
      String timeStamp = sdf.format(date);

      // Get name and comment from form
      String name = request.getParameter("Name");
      String comment = request.getParameter("Comment");
    
      // Place comment and corresponding information in DataStore
      Entity commentDataEntity = new Entity("CommentData");
      commentDataEntity.setProperty("name", name);  
      commentDataEntity.setProperty("comment", comment);
      commentDataEntity.setProperty("timeStamp", timeStamp);  

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentDataEntity);

      // Redirect back to HTML page
      response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {    
    // Load all comments from datastore, sorted by time posted
    Query query = new Query("CommentData").addSort("timeStamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    int maxComments = Integer.parseInt(request.getParameter("max-comments"));

    // Create commentData object for each stored comment and store in list
    int numComments = 0;
    List<CommentData> commentsData = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      if (numComments == maxComments) {
          break;
      }

      String name = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");
      String timeStamp = (String) entity.getProperty("timeStamp");

      CommentData commentData = new CommentData(name, comment, timeStamp);
      commentsData.add(commentData);

      numComments++;
    }
    
    // Create json string from list of commentData objects 
    Gson gson = new Gson();
    String json = gson.toJson(commentsData);

    // Write json string to response 
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}
