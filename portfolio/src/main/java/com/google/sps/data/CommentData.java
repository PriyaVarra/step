package com.google.sps.data;

import java.util.Date;

/** Class containing comment data. */
public final class CommentData {

  private final String name;
  private final String comment;
  private final Date utcDate;
  
  public CommentData(String name, String comment, Date utcDate) {
    this.name = name;
    this.comment = comment;
    this.utcDate = utcDate;
  }

  public String getName() {
    return name;
  }

  public String getComment() {
    return comment;
  }

  public Date getUTCDate() {
    return utcDate;
  }

}
