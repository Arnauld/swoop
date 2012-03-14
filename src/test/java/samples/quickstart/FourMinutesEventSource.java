package samples.quickstart;

import static swoop.Swoop.define;
import static swoop.Swoop.get;
import static swoop.support.ResourceBasedContent.resourcePath;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import swoop.EventSource;
import swoop.EventSourceConnection;
import swoop.support.ResourceBasedContent;
import swoop.util.New;

public class FourMinutesEventSource {

    public static void main(String[] args) {
        final Pusher pusher = new Pusher ();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(pusher, 5, 5, TimeUnit.SECONDS);
        get(new ResourceBasedContent("/hello", resourcePath(FourMinutesEventSource.class)+"/FourMinutesEventSource.html"));
        define(new EventSource("/events") {
            @Override
            public void onOpen(EventSourceConnection connection) {
                pusher.addConnection(connection);
            }
        });
    }
    
    public static class Pusher implements Runnable {
        private CopyOnWriteArraySet<EventSourceConnection> connections = New.copyOnWriteArraySet();
        private int count = 1;

        public void addConnection(EventSourceConnection connection) {
            connection.data("id", count++);
            connections.add(connection);
            broadcast("Client " + connection.data("id") + " joined");
        }

        public void removeConnection(EventSourceConnection connection) {
            connections.remove(connection);
            broadcast("Client " + connection.data("id") + " left");
        }

        public void run() {
            broadcast(new Date().toString());
        }

        private void broadcast(String message) {
            for (EventSourceConnection connection : connections) {
                connection.send(message);
            }
        }
    }
}