package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
        long meetingId = meeting.getId();
        Meeting meetingFoundById = meetingService.findById(String.valueOf(meetingId));
        if (meetingFoundById != null) {
            return new ResponseEntity<>("Unable to create. A meeting with id " + meetingId + " already exist.", HttpStatus.CONFLICT);
        }
        String title = meeting.getTitle();
        Collection<Meeting> meetingsFoundByTitle = meetingService.findByTitle(title);
        if (meetingsFoundByTitle != null) {
            return new ResponseEntity<>("Unable to create. A meeting with title " + title + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    //POST http://localhost:8080/meetings?addparticipant=user2
}