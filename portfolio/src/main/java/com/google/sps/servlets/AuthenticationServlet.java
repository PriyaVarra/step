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
    UserService userService = UserServiceFactory.getUserService();

    String json = 
      userService.isUserLoggedIn() ? createLogoutJson(userService) : createLoginJson(userService);

    response.setContentType("application/json");    
    response.getWriter().println(json);
  }

  private String createLogoutJson(UserService userService) {
    String id = userService.getCurrentUser().getUserId();
    String displayName = DataUtil.getUserDisplayName(id);
    
    // Url that allows user to logout and redirects them back to homepage
    String logoutURL = userService.createLogoutURL("/index.html");

    String json =
      "{\"loggedIn\": true, \"id\": \"%s\", \"displayName\": \"%s\", \"url\": \"%s\"}";
    
    return String.format(json, id, displayName, logoutURL);
  }
  
  private String createLoginJson(UserService userService) {
    // Url that allows user to login and redirects them back to homepage
    String loginURL = userService.createLoginURL("/index.html");

    String json = "{\"loggedIn\": false,  \"url\": \"%s\"}";
    
    return String.format(json, loginURL);
  }

}
