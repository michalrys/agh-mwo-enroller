package com.company.enroller.persistence;

import java.util.Collection;
import java.util.List;

import com.company.enroller.model.Participant;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

    DatabaseConnector connector;

    public MeetingService() {
        connector = DatabaseConnector.getInstance();
    }

    public Collection<Meeting> getAll() {
        String hql = "FROM Meeting";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public Meeting findById(String meetingId) {
        if (!meetingId.matches("[1-9]+[0-9]*")) {
            return null;
        }
        Session session = connector.getSession();
        return session.get(Meeting.class, Long.valueOf(meetingId));
    }

    public Collection<Meeting> findByTitle(String title) {
        Session session = connector.getSession();
        Query query = session.createQuery("FROM Meeting WHERE title='" + title + "'");
        return query.list();
    }

    public void add(Meeting meeting) {
        Session session = connector.getSession();
        Transaction transaction = session.beginTransaction();
        session.save(meeting);
        transaction.commit();
    }

    public void updateMeetingByAddingParticipant(Meeting meeting, Participant participant) {
        Session session = connector.getSession();
        Transaction transaction = session.beginTransaction();
        meeting.addParticipant(participant);
        session.update(meeting);
        transaction.commit();
    }

    public void delete(Meeting meeting) {
        //TODO: it is not working - not deleting
        Session session = connector.getSession();
        Transaction transaction = session.getTransaction();
        List allParticipants = session.createQuery("FROM Participant").list();
        for (Object participant : allParticipants) {
            meeting.removeParticipant(participant);
        }
        session.update(meeting);
        session.delete(meeting);
        transaction.commit();
    }
}