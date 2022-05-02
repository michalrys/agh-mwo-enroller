package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private ParticipantService participantService;

    @Test
    public void getParticipants() throws Exception {
        Participant participant = new Participant();
        participant.setLogin("testlogin");
        participant.setPassword("testpassword");

        Collection<Participant> allParticipants = singletonList(participant);
        given(participantService.getAll()).willReturn(allParticipants);

        mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login", is(participant.getLogin())));
    }

    @Test
    public void addParticipant() throws Exception {
        Participant participant = new Participant();
        participant.setLogin("testlogin");
        participant.setPassword("testpassword");
        String inputJSON = "{\"login\":\"testlogin\", \"password\":\"somepassword\"}";

        given(participantService.findByLogin("testlogin")).willReturn((Participant) null);
//		given(participantService.add(participant)).willReturn(participant);
        mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(participantService.findByLogin("testlogin")).willReturn(participant);
        mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(participantService, times(2)).findByLogin("testlogin");
    }

    @Test
    public void getParticipantById() throws Exception {
        Participant participant = new Participant();
        String login = "testlogin";
        participant.setLogin(login);
        participant.setPassword("testpassword");

        given(participantService.findByLogin(login)).willReturn(participant);

        mvc.perform(get("/participants/" + login).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(participant.getLogin())))
                .andExpect(jsonPath("$.password", is(participant.getPassword())));
    }

    @Test
    public void deleteParticipant() throws Exception {
        Participant participant = new Participant();
        String login = "testlogin";
        participant.setLogin(login);
        participant.setPassword("testpassword");

        given(participantService.findByLogin(login)).willReturn(participant);
//        given(participantService.delete(participant));

        mvc.perform(delete("/participants/" + login).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(participant.getLogin())))
                .andExpect(jsonPath("$.password", is(participant.getPassword())));

        given(participantService.findByLogin(login)).willReturn(null);

        mvc.perform(delete("/participants/" + login).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateParticipant() throws Exception {
        Participant participant = new Participant();
        String login = "testlogin";
        String loginBad = "testloginBad";
        participant.setLogin(login);
        participant.setPassword("testpassword");
        String inputJSON = "{\"login\":\"testlogin\", \"password\":\"testpassword\"}";
        String inputJSONbad = "{\"login\":\"testloginBad\", \"password\":\"testpassword\"}";

        BDDMockito.given(participantService.findByLogin(loginBad)).willReturn(null);

        mvc.perform(put("/participants").content(inputJSONbad).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        given(participantService.findByLogin(login)).willReturn(participant);
        mvc.perform(put("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(login)))
                .andExpect(jsonPath("$.password", is(participant.getPassword())));
    }

}
