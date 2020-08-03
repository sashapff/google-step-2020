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
import java.util.Set;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.function.Function;

public final class FindMeetingQuery {
  private Collection<Event> events;
  private MeetingRequest request;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    this.events = events;
    this.request = request;

    Collection<TimeRange> mandatoryAndOptional = 
      getSuited(getUnsuited(hasMandatoryOptionalCommonAttendee));
    if (!mandatoryAndOptional.isEmpty()) {
      return mandatoryAndOptional;
    }
    
    return getSuited(getUnsuited(hasMandatoryCommonAttendee));
  }

  /** Returns whether some event and request event have at least one mandatory attendee in common. */
  private Function<Event, Boolean> hasMandatoryCommonAttendee = (Event event) -> {
    final Collection<String> requestAttendees = request.getAttendees();
    for (String attendee : event.getAttendees()) {
      if (requestAttendees.contains(attendee)) {
        return true;
      }
    }
    return false;
  };

   /** 
    * Returns whether some event and request event have at least 
    * one mandatory or optional attendee in common. 
    */
  private Function<Event, Boolean> hasMandatoryOptionalCommonAttendee = (Event event) -> {
    final Collection<String> requestAttendees = request.getAttendees();
    final Collection<String> requestOptionalAttendees = request.getOptionalAttendees();
    for (String attendee : event.getAttendees()) {
      if (requestAttendees.contains(attendee) || requestOptionalAttendees.contains(attendee)) {
        return true;
      }
    }
    return false;
  };

  /** 
   * Takes the function that checks whether events have common attendee and 
   * returns time ranges sorted by start when the meeting can't be. 
   */
  private Collection<TimeRange> getUnsuited(Function<Event, Boolean> checkCommonAttendee) {
    Collection<TimeRange> unsuited = new TreeSet<>(TimeRange.ORDER_BY_START);
    final long requestEventDuration = request.getDuration();
    for (Event event : events) {
      if (checkCommonAttendee.apply(event)) {
        unsuited.add(event.getWhen());
      }
    }
    return unsuited;
  }

  /** Returns whether time range duration more at least request duration. */
  private boolean checkDuration(int start, int end) {
    return end - start >= request.getDuration();
  }

  /** Takes unsuited time ranges sorted by start and returns time ranges when the meeting can be. */
  private Collection<TimeRange> getSuited(Collection<TimeRange> unsuited) {
    Collection<TimeRange> suited = new ArrayList<>();
    int lastEnd = 0;
    for (TimeRange timeRange : unsuited) {
      final int start = timeRange.start();
      if (checkDuration(lastEnd, start)) {
        suited.add(TimeRange.fromStartEnd(lastEnd, start, false));
      }
      lastEnd = Math.max(timeRange.end(), lastEnd);
    } 
    // Time range between the end of last unsuited time range and the end of the day can be suited.
    if (checkDuration(lastEnd, TimeRange.MINUTES_A_DAY)) {
      suited.add(TimeRange.fromStartEnd(lastEnd, TimeRange.MINUTES_A_DAY, false));
    }
    return suited;
  }
}
