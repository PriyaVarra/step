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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that updates login/logout status with Users API. */
@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = UserServiceFactory.getUserService().getCurrentUser();

    String json = "{";
    json += "\"loggedIn\": ";

    // If user is logged in, write user information to json string
    if (user != null) {
      String id = user.getUserId();
      json += "true ,";
      json += "\"id\": \"" + id + "\" ,"; 
      json += "\"displayName\": \"" + getUserDisplayName(id) + "\"";

    } else {
      json += "false ";
    }

    json += "}";

    response.setContentType("application/json");    
    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String action = request.getParameter("action");

    UserService userService = UserServiceFactory.getUserService();

    // If user is logged in and trying to login again, redirect to home page otherwise provide logout URL
    String loggedInURL = action.equals("login") ? "/index.html" : userService.createLogoutURL("/index.html");

    // If user is logged out and trying to logout again, redirect to home page otherwise provide login URL
    String loggedOutURL = action.equals("login") ? userService.createLoginURL("/index.html") : "/index.html";
    
    if (userService.isUserLoggedIn()) {
      response.sendRedirect(loggedInURL);
      return;
    } else {
      response.sendRedirect(loggedOutURL);
      return;
    }
  }

  /**
   * Returns user's most recently set displayName in Datastore 
   * or returns empty string if user has not logged in before.
   */
  private String getUserDisplayName(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
      new Query("UserInfo")
        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    
    // User has not logged in before
    if (entity == null) {
      return "";
    }

    String displayName = (String) entity.getProperty("displayName");
    return displayName;
  }

}
