package com.google.sps.data;

import java.util.Date;

/** Class containing comment data. */
public final class CommentData {

  private final String name;
  private final String comment;
  private final String timeStamp;
  
  public CommentData(String name, String comment, String timeStamp) {
    this.name = name;
    this.comment = comment;
    this.timeStamp = timeStamp;
  }

  public String getName() {
    return name;
  }

  public String getComment() {
    return comment;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

}
