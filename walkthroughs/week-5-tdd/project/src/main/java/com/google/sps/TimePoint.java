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

package com.google.sps;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a point in time that is either the start or end of an event and the
 * required attendees from a meeting request corresponding to the event.
 */
public final class TimePoint {
  
  /**
   * A comparator for sorting points in ascending order. Breaks ties by choosing start points
   */
  public static final Comparator<TimePoint> ORDER_POINTS = new Comparator<TimePoint>() {
    @Override
    public int compare(TimePoint a, TimePoint b) {
      int compare = Long.compare(a.time, b.time);

      if (compare == 0 && a.isStart != b.isStart) {
        compare = a.isStart ? -1 : 1;
      }

      return compare;
    }
  };

  private final int time;
  private final boolean isStart;
 

  public TimePoint(int time, boolean isStart) {
    this.time = time;
    this.isStart = isStart;
  }

  /**
   * Returns the time of the point in minutes.
   */
  public int time() {
    return time;
  }

  /**
   * Returns flag indicating if point is start of a range
   */
  public boolean isStart() {
    return isStart;
  }

}

