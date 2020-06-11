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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.DataUtil;
import com.google.sps.data.MarkerData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving markers data. */
@WebServlet("/markers")
public class MarkersServlet extends HttpServlet {

  /** Responds with a JSON array containing marker data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    Collection<MarkerData> markersData = getMarkersData();
    Gson gson = new Gson();
    String json = gson.toJson(markersData);

    response.getWriter().println(json);
  }

  /** Accepts a POST request containing a new marker. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // User can only comment if they are logged in
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String id = userService.getCurrentUser().getUserId();
    String displayName = request.getParameter("name");
    
    // Update user's displayName in DataStore
    Entity userInfoEntity = new Entity("UserInfo", id);
    userInfoEntity.setProperty("id", id);
    userInfoEntity.setProperty("displayName", displayName); 
    
    double lat = Double.parseDouble(request.getParameter("lat"));
    double lng = Double.parseDouble(request.getParameter("lng"));   
    String content = Jsoup.clean(request.getParameter("content"), Whitelist.none());

    // Place information about marker in Datastore
    Entity markerEntity = new Entity("MarkerDataEntity");
    markerEntity.setProperty("lat", lat);
    markerEntity.setProperty("lng", lng);
    markerEntity.setProperty("id", id);
    markerEntity.setProperty("content", content);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(markerEntity);
    datastore.put(userInfoEntity);
  }

  /** Fetches markers from Datastore. */
  private Collection<MarkerData> getMarkersData() {
    Collection<MarkerData> markersData = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("MarkerDataEntity");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      double lat = (double) entity.getProperty("lat");
      double lng = (double) entity.getProperty("lng");
      String content = (String) entity.getProperty("content");
      String id = (String) entity.getProperty("id");

      String displayName = DataUtil.getUserDisplayName(id);

      String key = KeyFactory.keyToString(entity.getKey());

      MarkerData markerData = new MarkerData(key, id, displayName, lat, lng, content);
      markersData.add(markerData);
    }

    return markersData;
  }

}
