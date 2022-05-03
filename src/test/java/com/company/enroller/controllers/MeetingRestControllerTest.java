package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(MeetingRestController.class)
public class MeetingRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private ParticipantService participantService;

    @Test
    public void getMeetings() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setId(1);
        meeting.setDate("some date");
        meeting.setTitle("meetingA");
        meeting.setDescription("important meeting");

        List<Meeting> allMeetings = Collections.singletonList(meeting);

        BDDMockito.given(meetingService.getAll()).willReturn(allMeetings);

        mvc.perform(MockMvcRequestBuilders.get("/meetings").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(meeting.getId())))  //FIXME problem with int <-> long
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].date", Matchers.is(meeting.getDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is(meeting.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(meeting.getDescription())));
    }

    @Test
    public void getMeetingByTitle() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setId(1);
        meeting.setDate("some date");
        String titleWrong = "meetingANotExist";
        String titleCorrect = "meetingA";
        meeting.setTitle(titleCorrect);
        meeting.setDescription("important meeting");
        List<Meeting> meetingsWithTheSameTitle = Collections.singletonList(meeting);

        BDDMockito.given(meetingService.findByTitle(titleWrong)).willReturn(null);
        mvc.perform(MockMvcRequestBuilders.get("/meetings/title=" + titleWrong).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        BDDMockito.verify(meetingService).findByTitle(titleWrong);

        BDDMockito.given(meetingService.findByTitle(titleCorrect)).willReturn(meetingsWithTheSameTitle);
        mvc.perform(MockMvcRequestBuilders.get("/meetings/title=" + titleCorrect).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].date", Matchers.is(meeting.getDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is(meeting.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(meeting.getDescription())));
        BDDMockito.verify(meetingService).findByTitle(titleCorrect);
    }

    @Test
    public void createMeeting() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setId(1);
        meeting.setDate("some date");
        meeting.setTitle("meetingA");
        meeting.setDescription("important meeting");
        String inputJSON = "{\"id\":\"1\", \"title\":\"meetingA\", \"description\":\"important meeting\", \"date\":\"some date\"}";

        BDDMockito.given(meetingService.findByTitle(meeting.getTitle())).willReturn(new ArrayList<>());
        BDDMockito.given(meetingService.findById(String.valueOf(meeting.getId()))).willReturn(null);

        mvc.perform(MockMvcRequestBuilders.post("/meetings").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", Matchers.is(meeting.getDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(meeting.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(meeting.getDescription())));
        BDDMockito.verify(meetingService).findByTitle(meeting.getTitle());
        BDDMockito.verify(meetingService).findById(String.valueOf(meeting.getId()));

    }


}