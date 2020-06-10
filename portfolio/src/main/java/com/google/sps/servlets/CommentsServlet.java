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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.data.CommentData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that stores and displays comments. */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
      
    // User can only comment if they are logged in
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    // Create timestamp with date and time from comment
    Date utcDate = new Date();

    String id = userService.getCurrentUser().getUserId(); 
    String comment = request.getParameter("Comment");
    
    // Place comment and corresponding information in DataStore
    Entity commentDataEntity = new Entity("CommentData");
    commentDataEntity.setProperty("id", id);      
    commentDataEntity.setProperty("content", comment);
    commentDataEntity.setProperty("utcDate", utcDate);

    String displayName = request.getParameter("Name");
    
    // Update user's displayName in DataStore
    Entity userInfoEntity = new Entity("UserInfo", id);
    userInfoEntity.setProperty("id", id);
    userInfoEntity.setProperty("displayName", displayName); 

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentDataEntity);
    datastore.put(userInfoEntity);

  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {    
    // Load all comments from datastore, sorted by time posted
    Query query = new Query("CommentData").addSort("utcDate", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    int maxComments = Integer.parseInt(request.getParameter("max-comments"));

    // Create commentData object for each stored comment, up to maxComments, and store in list
    int numComments = 0;
    List<CommentData> commentsData = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      if (numComments == maxComments) {
        break;
      }

      String id = (String) entity.getProperty("id");
      String comment = (String) entity.getProperty("content");
      Date utcDate = (Date) entity.getProperty("utcDate");

      // Create string representation of key for json storage
      String key = KeyFactory.keyToString(entity.getKey());

      String displayName = DataUtil.getUserDisplayName(id);

      CommentData commentData = new CommentData(key, id, displayName, comment, utcDate);
      commentsData.add(commentData);

      numComments++;
    }

    // Create json string from list of commentData objects 
    Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy hh:mm a z").create();
    String json = gson.toJson(commentsData);

    // Write json string to response 
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}
