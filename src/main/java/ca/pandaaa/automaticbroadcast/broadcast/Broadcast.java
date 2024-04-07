package ca.pandaaa.automaticbroadcast.broadcast;

import org.bukkit.Sound;

import java.util.List;

public class Broadcast {
    private final List<String> messages;
    private final List<String> hoverMessages;
    private final String title;
    private final String clickMessage;
    private final Sound sound;

    public Broadcast(String title, List<String> messages, Sound sound,
                     List<String> hoverMessages, String clickMessage) {
        this.title = title;
        this.messages = messages;
        this.sound = sound;
        this.hoverMessages = hoverMessages;
        this.clickMessage = clickMessage;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Sound getSound() {
        return sound;
    }

    public List<String> getHoverMessages() {
        return hoverMessages;
    }

    public String getClickMessage() {
        return clickMessage;
    }
}

