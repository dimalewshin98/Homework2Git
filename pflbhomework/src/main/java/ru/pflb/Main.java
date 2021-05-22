package ru.pflb;

import ru.pflb.mq.dummy.implementation.ConnectionImpl;
import ru.pflb.mq.dummy.interfaces.Connection;
import ru.pflb.mq.dummy.interfaces.Destination;
import ru.pflb.mq.dummy.interfaces.Producer;
import ru.pflb.mq.dummy.interfaces.Session;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {

        ArrayList<String> messages = new ArrayList<>();
        messages.add("Раз");
        messages.add("Два");
        messages.add("Три");

        Connection connection = new ConnectionImpl();
        connection.start();

        Session session = connection.createSession(true);

        Destination destination = session.createDestination("myQueue");

        Producer producer = session.createProducer(destination);

        synchronized (producer) {
            for (int i = 0; i < messages.size(); i++) {
                producer.send(messages.get(i));
                if (i != messages.size()-1){
                    producer.wait(2000);
                }
            }
        }
        session.close();
        connection.close();
    }
}