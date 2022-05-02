package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class QuickRawTests {
    public static void main(String[] args) {
        DatabaseConnector instance = DatabaseConnector.getInstance();

        Session session = instance.getSession();
        //Meeting meeting = session.get(Meeting.class, Long.valueOf(2));

        String title = "teleconference B";
        Query query = session.createQuery("FROM Meeting WHERE title='" + title + "'");

        List list = query.list();

    }
}
