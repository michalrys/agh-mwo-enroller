package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
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

    @Autowired
    ParticipantService participantService;

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

    //PUT http://localhost:8080/meetings/meetingid=2&participantlogin=user2
    @RequestMapping(value = "/meetingid={id}&participantlogin={login}", method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") String meetingId,
                                                     @PathVariable("login") String participantLogin) {
        Participant participantFound = participantService.findByLogin(participantLogin);
        if (participantFound == null) {
            return new ResponseEntity<>("There is no such participant with login: " + participantLogin + ". This operation was terminated.", HttpStatus.NOT_FOUND);
        }
        Meeting meetingFoundById = meetingService.findById(meetingId);
        if (meetingFoundById == null) {
            return new ResponseEntity<>("There is no such meeting with id: '" + meetingId + "'. This operation was terminated.", HttpStatus.NOT_FOUND);
        }
        Collection<Participant> currentParticipants = meetingFoundById.getParticipants();
        if (currentParticipants.contains(participantFound)) {
            return new ResponseEntity<>("This participant is already added into the meeting. This operation was terminated.", HttpStatus.CONFLICT);
        }
        meetingService.updateMeetingByAddingParticipant(meetingFoundById, participantFound);
        return new ResponseEntity<>("Participant '" + participantLogin + "' was added to meeting '" + meetingFoundById.getTitle() + "'.", HttpStatus.OK);
    }

    //PUT http://localhost:8080/meetings/meetingtitle=teleconference B&participantlogin=user2
    @RequestMapping(value = "/meetingtitle={title}&participantlogin={login}", method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipantToMeetingByTitle(@PathVariable("title") String meetingTitle,
                                                     @PathVariable("login") String participantLogin) {
        Participant participantFound = participantService.findByLogin(participantLogin);
        if (participantFound == null) {
            return new ResponseEntity<>("There is no such participant with login: " + participantLogin + ". This operation was terminated.", HttpStatus.NOT_FOUND);
        }
        Collection<Meeting> meetings = meetingService.findByTitle(meetingTitle);
        if (meetings.size() == 0) {
            return new ResponseEntity<>("There is no such meeting with title: '" + meetingTitle + "'. This operation was terminated.", HttpStatus.NOT_FOUND);
        }
        if (meetings.size() > 1) {
            StringBuilder meetingsSummary = new StringBuilder();
            for (Meeting meeting : meetings) {
                meetingsSummary.append("ID = ");
                meetingsSummary.append(meeting.getId());
                meetingsSummary.append(" TITLE = ");
                meetingsSummary.append(meeting.getTitle());
            }
            return new ResponseEntity<>("There are more than one meeting with given title: '" + meetingTitle +"'. This operation was terminated. Summary:\n" + meetingsSummary.toString(), HttpStatus.CONFLICT);
        }
        Meeting meeting = null;
        for (Meeting m : meetings) {
            meeting = m;
            break;
        }
        Collection<Participant> currentParticipants = meeting.getParticipants();
        if (currentParticipants.contains(participantFound)) {
            return new ResponseEntity<>("This participant is already added into the meeting. This operation was terminated.", HttpStatus.CONFLICT);
        }
        meetingService.updateMeetingByAddingParticipant(meeting, participantFound);
        return new ResponseEntity<>("Participant '" + participantLogin + "' was added to meeting '" + meeting.getTitle() + "'.", HttpStatus.OK);
    }
}