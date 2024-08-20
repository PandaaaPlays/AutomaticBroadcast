package ca.pandaaa.automaticbroadcast.api;

import ca.pandaaa.automaticbroadcast.AutomaticBroadcast;
import ca.pandaaa.automaticbroadcast.broadcast.Broadcast;
import ca.pandaaa.automaticbroadcast.broadcast.BroadcastManager;

import java.util.List;

public class BroadcastAPI {
    BroadcastManager broadcastManager;

    public BroadcastAPI(BroadcastManager broadcastManager) {
        this.broadcastManager = broadcastManager;
    }

    public void sendBroadcast(Broadcast broadcast) {
       broadcastManager.sendBroadcast(broadcast);
    }

    public void createScheduledBroadcastList(List<Broadcast> broadcasts) {
        AutomaticBroadcast.getPlugin().createScheduledBroadcastList(broadcasts);
    }
}
