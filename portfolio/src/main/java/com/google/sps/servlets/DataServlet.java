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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final Gson gson = new Gson();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String message = request.getParameter("message");
    final long timestamp = System.currentTimeMillis();

    final Entity messageEntity = new Entity("Message");
    messageEntity.setProperty("message", message);
    messageEntity.setProperty("timestamp", timestamp);

    final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(messageEntity);

    response.sendRedirect("/index.html");
  }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);

    final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    final PreparedQuery results = datastore.prepare(query);

    ArrayList<String> messages = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String message = (String) entity.getProperty("message");
      messages.add(message);
    }

    response.setContentType("text/html;");
    String json = gson.toJson(messages);
    response.getWriter().println(json);
  }

}
