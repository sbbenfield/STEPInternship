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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      doPost(request, response);
  }
  
  /**
  * The doPost method creates a Datastore object that stores an Entity with set properties for commenting.
  * A list of tasks are then created to house all entities, to be converted to Json and displayed on /data. 
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("comment", getParameter(request, "text-input", ""));
    taskEntity.setProperty("timestamp", System.currentTimeMillis());
    datastore.put(taskEntity);
    
    Query query = new Query("Task").addSort("comment", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    List<Task> tasks = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
        tasks.add(new Task(entity.getKey().getId(), (String) entity.getProperty("comment"), 
                           (long) entity.getProperty("timestamp")));
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(tasks));
  }
  
  /**
  * The getParameter method retrieves the values from the submitted form 
  * The request contains parameters with names that match the names of the input elements in the HTML form.  
  * 
  * @return String value that represents the input element's name from the request.
  */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return value == null ? defaultValue : value;
  }
}
