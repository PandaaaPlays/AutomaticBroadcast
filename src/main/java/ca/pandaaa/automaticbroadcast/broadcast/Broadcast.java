package ca.pandaaa.automaticbroadcast.broadcast;

import org.bukkit.Sound;

import java.util.List;

// This is the actual broadcast. All the broadcast are created once on start //
public class Broadcast {
    // Attributes //
    private final List<String> messages;
    private final List<String> hoverMessages;
    private final String title;
    private final String clickMessage;
    private final Sound sound;

    // Constructor //
    public Broadcast(String title, List<String> messages, Sound sound,
                     List<String> hoverMessages, String clickMessage) {
        this.title = title;
        this.messages = messages;
        this.sound = sound;
        this.hoverMessages = hoverMessages;
        this.clickMessage = clickMessage;
    }

    // Returns the title of the broadcast //
    public String getTitle() {
        return title;
    }

    // Returns the messages of the broadcast //
    public List<String> getMessages() {
        return messages;
    }

    // Returns the sound of the broadcast //
    public Sound getSound() {
        return sound;
    }

    // Returns the hover messages of the broadcast //
    public List<String> getHoverMessages() {
        return hoverMessages;
    }

    // Returns the click messages of the broadcast //
    public String getClickMessage() {
        return clickMessage;
    }
}

