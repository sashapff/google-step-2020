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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns users messages content that they send. */
@WebServlet("/messages-data")
public class MessagesDataServlet extends HttpServlet {
  private final Gson gson = new Gson();

  /** Store message structure. */
  private class Message {
    final String sender;
    final String content;
    final String email;

    Message(String sender, String content, String email) {
      this.sender = sender;
      this.content = content;
      this.email = email;
    }
  }

  private final String SENDER_PROPERTY = "sender";
  private final String CONTENT_PROPERTY = "content";
  private final String EMAIL_PROPERTY = "email";
  private final String TIMESTAMP_PROPERTY = "timestamp";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    
    final String sender = request.getParameter("message-sender");
    final String content = request.getParameter("message-content");
    final String email = userService.getCurrentUser().getEmail();
    final long timestamp = System.currentTimeMillis();

    putMessage(sender, content, email, timestamp);

    response.sendRedirect("/index.html#messages");
  }

  /** Create new message and put it to Datastore. */
  private void putMessage(String sender, String content, String email, long timestamp) {
    Entity messageEntity = new Entity("Message");
    messageEntity.setProperty(SENDER_PROPERTY, sender);
    messageEntity.setProperty(CONTENT_PROPERTY, content);
    messageEntity.setProperty(EMAIL_PROPERTY, email);
    messageEntity.setProperty(TIMESTAMP_PROPERTY, timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(messageEntity);
  }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final ArrayList<Message> messages = loadMessages();

    response.setContentType("text/html;");
    String json = gson.toJson(messages);
    response.getWriter().println(json);
  }

  /** Load messages from Datastore. */
  private ArrayList<Message> loadMessages() {
    final Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);

    final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    final PreparedQuery results = datastore.prepare(query);

    ArrayList<Message> messages = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String sender = (String) entity.getProperty(SENDER_PROPERTY);
      String content = (String) entity.getProperty(CONTENT_PROPERTY);
      String email = (String) entity.getProperty(EMAIL_PROPERTY);
      messages.add(new Message(sender, content, email));
    }
    return messages;
  }

}