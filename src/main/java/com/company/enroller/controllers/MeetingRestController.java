package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    // GET http://localhost:8080/meetings
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    //GET http://localhost:8080/meetings/id=2
    @RequestMapping(value = "/id={id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingById(@PathVariable("id") String id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    //GET http://localhost:8080/meetings/title=some title
    @RequestMapping(value = "/title={id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingByTitle(@PathVariable("id") String title) {
        Collection<Meeting> meetings = meetingService.findByTitle(title);
        if (meetings == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    //POST http://localhost:8080/meetings   +  json
    // assumption: title of meeting is unique
    // example json:
    //{
    //    "title": "teleconference C",
    //    "description": "some meeting C",
    //    "date": "some other date"
    //}
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
        long meetingId = meeting.getId();
        Meeting meetingFoundById = meetingService.findById(String.valueOf(meetingId));
        if (meetingFoundById != null) {
            return new ResponseEntity<>("Unable to create. A meeting with id " + meetingId + " already exist.", HttpStatus.CONFLICT);
        }
        String title = meeting.getTitle();
        Collection<Meeting> meetingsFoundByTitle = meetingService.findByTitle(title);
        if (meetingsFoundByTitle.size() != 0) {
            return new ResponseEntity<>("Unable to create. A meeting with title " + title + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/title={id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeetingByTitle(@PathVariable("id") String meetingTitle) {
        Collection<Meeting> meetingsFoundByTitle = meetingService.findByTitle(meetingTitle);
        if (meetingsFoundByTitle.size() == 0) {
            return new ResponseEntity<>("No such meeting titled as '" + meetingTitle + "'. Nothing was done.", HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meetingsFoundByTitle);
        return new ResponseEntity<>("Meeting with title '" + meetingTitle + "' was deleted.", HttpStatus.OK);
    }

    @RequestMapping(value = "/title={id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeetingByTitle(@PathVariable("id") String title, @RequestBody Meeting meetingWithNewData) {
        Collection<Meeting> meetingsFoundByTitle = meetingService.findByTitle(title);
        if (meetingsFoundByTitle.size() == 0) {
            return new ResponseEntity<>("No such meeting titled as '" + title + "'. Nothing was done.", HttpStatus.NOT_FOUND);
        } else if (meetingsFoundByTitle.size() > 1) {
            StringBuilder info = new StringBuilder();
            info.append("There are more than one meeting with the same title called '" + title + "'.");
            info.append("\nFollowing meetings were found:\n");
            for (Meeting m : meetingsFoundByTitle) {
                info.append("\t-> id: " + m.getId() + "\n");
            }
            info.append("\n\nNothing was done. Update meeting by id not by title.");
            return new ResponseEntity<>(info.toString(), HttpStatus.CONFLICT);
        }
        Optional<Meeting> meetingFirstFound = meetingsFoundByTitle.stream().findFirst();
        meetingService.update(meetingFirstFound.get(), meetingWithNewData);
        return new ResponseEntity<>("Meeting was updated.", HttpStatus.OK);
    }
}