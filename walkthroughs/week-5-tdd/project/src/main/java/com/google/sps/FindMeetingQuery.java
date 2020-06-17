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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    // Returns true if an event contains at least one required or optional attendee
    Predicate<Event> containsAnyAttendees = e -> {
      boolean containsRequiredAttendees = 
          !Collections.disjoint(e.getAttendees(), requiredAttendees); 
      boolean containsOptionalAttendees = 
          !Collections.disjoint(e.getAttendees(), optionalAttendees);

      return containsRequiredAttendees || containsOptionalAttendees;
    };

    Collection<Event> requiredAndOptionalEvents = 
        filterEvents(containsAnyAttendees, events);
    Collection<TimePoint> requiredAndOptionalPoints =
        createSortedTimePoints(requiredAndOptionalEvents); 
    Collection<TimeRange> allAvailableTimes = 
        getAvailableTimes(requiredAndOptionalPoints, request.getDuration());

    // Return result if optional attendees can be accomodated or there is only one type of attendee
    if (!allAvailableTimes.isEmpty() || requiredAttendees.isEmpty() 
        || optionalAttendees.isEmpty()) {
        return allAvailableTimes;
    }

    // Returns true if an event contains at least one required attendee
    Predicate<Event> containsAnyRequiredAttendees = e -> {   
      return !Collections.disjoint(e.getAttendees(), requiredAttendees); 
    };
    
    // If optional attendees cannot be accomodated, try again with only required attendees
    Collection<Event> requiredEvents = 
        filterEvents(containsAnyRequiredAttendees, requiredAndOptionalEvents);
    Collection<TimePoint> requiredPoints =
        createSortedTimePoints(requiredEvents); 
    
    return getAvailableTimes(requiredPoints, request.getDuration());
  }

  /** Filters collection of events according to predicate function. **/
  @SuppressWarnings("unchecked")
  private Collection<Event> filterEvents(Predicate<Event> filterFn, Collection<Event> events) { 
    return events.stream().filter(filterFn).collect(Collectors.toList());
  }

  /** Separates start and end points for each event time range and sorts collection. */
  private Collection<TimePoint> createSortedTimePoints(Collection<Event> events) {
    ArrayList<TimePoint> points = new ArrayList<TimePoint>();

    for (Event event : events) {
      TimeRange timeRange = event.getWhen();
      points.add(new TimePoint(timeRange.start(), true));
      points.add(new TimePoint(timeRange.end(), false));
    }

    Collections.sort(points, TimePoint.ORDER_POINTS);

    return points;
  }

  /**
   * Finds and returns all gaps in schedules for a group of attendees that are long enough for 
   * the requested meeting duration.
   */ 
  private Collection<TimeRange> getAvailableTimes(Collection<TimePoint> points, long reqDuration) {
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();

    int eventCount = 0;
    int lastEndTime = TimeRange.START_OF_DAY;
    
    for (TimePoint point: points) {
      if (point.isStart()) {
        eventCount++;

        if (eventCount == 1) {
          int freeDuration = point.time() - lastEndTime;
          if (freeDuration >= reqDuration) {
            availableTimes.add(TimeRange.fromStartDuration(lastEndTime, freeDuration));
          }
        }  
      } else {
        eventCount--;

        if (eventCount == 0) {
          lastEndTime = point.time(); 
        }
      }
    }
    
    int lastDuration = TimeRange.END_OF_DAY + 1 - lastEndTime;
    if (lastDuration >= reqDuration) {
        availableTimes.add(TimeRange.fromStartDuration(lastEndTime, lastDuration));
    }

    return availableTimes;
  }
}
