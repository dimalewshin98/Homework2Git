package ru.pflb;

import ru.pflb.mq.dummy.implementation.ConnectionImpl;
import ru.pflb.mq.dummy.interfaces.Connection;
import ru.pflb.mq.dummy.interfaces.Destination;
import ru.pflb.mq.dummy.interfaces.Producer;
import ru.pflb.mq.dummy.interfaces.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        Path myFile = null;

        if (args.length != 1){
            System.out.println("Нет аргумента");
        }
        else {
            myFile = Paths.get(args[0]);
            if (!Files.exists(myFile)) {
                System.out.println("Файл не распознан");
            }
        }

        Connection connection = new ConnectionImpl();
        connection.start();

        Session session = connection.createSession(true);

        Destination destination = session.createDestination("myQueue");

        Producer producer = session.createProducer(destination);

        synchronized (producer) {
            try {
                ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(myFile);

                for (int i = 0; i < lines.size(); i++) {
                    producer.send(lines.get(i));
                    producer.wait(2000);
                    if (i == lines.size()-1){
                        i = -1;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}