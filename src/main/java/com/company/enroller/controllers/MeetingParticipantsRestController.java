package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingParticipantsRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

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
            return new ResponseEntity<>("There are more than one meeting with given title: '" + meetingTitle + "'. This operation was terminated. Summary:\n" + meetingsSummary.toString(), HttpStatus.CONFLICT);
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

    // GET http://localhost:8080/meetings/participantsfrom/meetingid=2
    @RequestMapping(value = "/participantsfrom/meetingid={id}", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantsFromMeeting(@PathVariable("id") String meetingId) {
        Meeting meetingFoundById = meetingService.findById(meetingId);
        if (meetingFoundById == null) {
            return new ResponseEntity<>("Meeting with given id = '" + meetingId + "' was not found.", HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meetingFoundById.getParticipants();
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    // GET http://localhost:8080/meetings/participantsfrom/meetingtitle=some title
    @RequestMapping(value = "/participantsfrom/meetingtitle={title}", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantsFromMeetingByTitle(@PathVariable("title") String meetingTitle) {
        // TODO later
        return null;
    }
}
