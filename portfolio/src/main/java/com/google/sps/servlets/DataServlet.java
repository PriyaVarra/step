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
public class DataServlet extends HttpServlet {

  private final List<CommentData> commentsData = new ArrayList<CommentData>();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Populate data for comment and add to list 
      CommentData commentData = createCommentData(request);
      commentsData.add(commentData);

      // Redirect back to the HTML page
      response.sendRedirect("/index.html");
  }
  
  /* Creates CommentData object containing name, comment, and date and time of comment */
  private CommentData createCommentData(HttpServletRequest request) {
      // Create timestamp with date and time from comment
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat ("MM/dd/yyyy  hh:mm a");
      String timeStamp = sdf.format(date);

      // Get name and comment from form
      String name = request.getParameter("Name");
      String comment = request.getParameter("Comment");

      return new CommentData(name, comment, timeStamp);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create json string from list of commentData objects 
    Gson gson = new Gson();
    String json = gson.toJson(commentsData);

    // Write json string to response 
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}
