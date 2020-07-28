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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns form to new message or link to login. */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/home");
      out.println("<h3>Please login to send messages</h3>");
      out.println("<p><a href=\"" + loginUrl + "\">Login</a></p>");
    } else {
      newMessagesForm(out);
    }
  }

  /** Create form for messages. */
  private void newMessagesForm(PrintWriter out) {
    out.println("<p>Leave a comment about me:</>");
    out.println("<form action=\"/data\" method=\"POST\">");
    out.println("<input type=\"text\" name=\"message-sender\" placeholder=\"Your name or nick\" required><br><br>");
    out.println("<input type=\"text\" name=\"message-content\" placeholder=\"Message\" required><br><br>");
    out.println("<input type=\"submit\" value=\"Send\">");
    out.println("</form>");
  }
}
