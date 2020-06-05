package com.google.sps.data;

import java.util.Date;

/** Class containing comment data. */
public final class CommentData {
  
  private final String key;
  private final String id;
  private final String displayName;
  private final String comment;
  private final Date utcDate;
  
  /** Populates data associated with comment left by authenticated user. */
  public CommentData(String key, String id, String displayName, String comment, Date utcDate) {
    this.key = key;
    this.id = id;
    this.displayName = displayName;
    this.comment = comment;
    this.utcDate = utcDate;
  }

  /** Returns string representation of comment's key in Datastore. */
  public String getKey() {
    return key;
  }

  /** Returns string representation of commenter's user id. */ 
  public String getID() {
    return id;
  }

  /** Returns display name chosen by commenter. */
  public String getDisplayName() {
    return displayName;
  }

  /** Returns comment left by commenter. */
  public String getComment() {
    return comment;
  }

  /** Returns Date object representing time in UTC that comment was left. */
  public Date getUTCDate() {
    return utcDate;
  }

}
