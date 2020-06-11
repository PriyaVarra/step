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

package com.google.sps.data;

/** Represents a marker on the map. */
public class MarkerData {

  private String key;
  private String id;
  private String displayName;
  private final double lat;
  private final double lng;
  private final String content;

  public MarkerData(String key, String id, String displayName, double lat, double lng, String content) {
    this.key = key;
    this.id = id;
    this.displayName = displayName;
    this.lat = lat;
    this.lng = lng;
    this.content = content;
  }

  public String getKey() {
    return key;
  }

  public String getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }

  public String getContent() {
    return content;
  }
}
