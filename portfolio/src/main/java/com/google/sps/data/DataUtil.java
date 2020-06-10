package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/** Class containing util functions */
public final class DataUtil {
  /**
  * Returns user's most recently set displayName in Datastore 
  * or returns empty string if user has not logged in before.
  */
  public String getUserDisplayName(String id) {
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

    return (String) entity.getProperty("displayName");
  }
}
