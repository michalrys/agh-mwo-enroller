package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
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


}