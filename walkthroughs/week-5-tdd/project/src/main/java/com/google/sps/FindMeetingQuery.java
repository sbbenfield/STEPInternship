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
import java.util.Set;
import java.util.stream.Collectors; 
import java.util.stream.Stream; 

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    int dayStart = TimeRange.START_OF_DAY;
    int dayEnd = TimeRange.END_OF_DAY;
    int start = dayStart;
    int end;
    TimeRange previousTime = TimeRange.fromStartDuration(0,0);
    
    List<TimeRange> sortedTimeRanges = getAllAttendeeRanges(events, request);
    ArrayList<TimeRange> possibleTimeRanges = new ArrayList<>();

    //Returns an empty List should the requested duration be longer than a total day.
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) return possibleTimeRanges;

    //For-each that iterates through sortedTimeRanges to determine available times for request's duration.
    for (TimeRange timeRange : sortedTimeRanges) {
        if (timeRange.start() == dayStart) start = timeRange.end();

        //If the gap between the current start and the next event start is greater than request's duration
        //create a TimeRange for that period.
        if (timeRange.start() - start >= request.getDuration()) {
            possibleTimeRanges.add(TimeRange.fromStartEnd(start, timeRange.start(), false));
        }

        start = timeRange.end();
        end = timeRange.end();

        //Checks to see if the TimeRanges are nested.
        if (previousTime.contains(timeRange)) {
            end = previousTime.end();
        }

        //If the current TimeRange is the last in the list then determine if there is space for the request after.
        if (sortedTimeRanges.get(sortedTimeRanges.size() - 1) == timeRange) {
            if (dayEnd - end >= request.getDuration()) {
                possibleTimeRanges.add(TimeRange.fromStartEnd(end, dayEnd, true));
            }
        }

        previousTime = timeRange;
    }

    //If there are no events planned, create a TimeRange of the entire day available for the meeting request.
    if (sortedTimeRanges.isEmpty()) possibleTimeRanges.add(TimeRange.fromStartDuration(dayStart, dayEnd + 1));
    return possibleTimeRanges;
  }

  /**
   * Method that iterates through events and checks if attendees of each event matches the request's attendees, 
   * if so adds their TimeRange to a Set to be sorted.
   * @return Sorted List of TimeRanges based on attendees in ascending order.
   */
  public static List<TimeRange> getAllAttendeeRanges(Collection<Event> events, MeetingRequest request) {
    Set<TimeRange> allAttendeeRanges = new HashSet<>();

    //Iterates through events to retrieve all event attendees and reference them with meeting request's attendees inorder to
    //retrieve their TimeRanges.
    for (Event event : events) {
        Set<String> eventAttendees = event.getAttendees();
        for (String requestAttendee : request.getAttendees()) {
            for (String eventAttendee : eventAttendees) {
                if (requestAttendee.equals(eventAttendee)) {
                    allAttendeeRanges.add(event.getWhen());
                }
            }
        }
    }

    //Convert Set to ArrayList using Stream and sorts it using Collections.sort by ascending time.
    List<TimeRange> sortedTimeRanges = allAttendeeRanges.stream().collect(Collectors.toList()); 
    Collections.sort(sortedTimeRanges, TimeRange.ORDER_BY_START); 
    return sortedTimeRanges;
  }
}
