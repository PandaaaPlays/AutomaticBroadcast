package ca.pandaaa.automaticbroadcast.broadcast;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Broadcast {
    private final List<String> messages;
    private final List<String> hoverMessages;
    private final String title;
    private final String clickMessage;
    private final Sound sound;
    private final List<Player> exemptedPlayers;
    private final List<String> consoleCommands;

    public Broadcast(String title, List<String> messages, Sound sound,
                     List<String> hoverMessages, String clickMessage,
                     List<String> exemptedPlayers, List<String> consoleCommands) {
        this.title = title;
        this.messages = messages;
        this.sound = sound;
        this.hoverMessages = hoverMessages;
        this.clickMessage = clickMessage;
        this.exemptedPlayers = new ArrayList<>();
        for(String string : exemptedPlayers) {
            Player player = Bukkit.getPlayer(string);
            if (player != null)
                this.exemptedPlayers.add(player);
        }
        this.consoleCommands = consoleCommands;
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

    public List<Player> getExemptedPlayers() {
        return exemptedPlayers;
    }

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }
}

