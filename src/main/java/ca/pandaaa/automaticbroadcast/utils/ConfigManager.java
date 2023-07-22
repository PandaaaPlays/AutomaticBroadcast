package ca.pandaaa.automaticbroadcast.utils;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class ConfigManager {
    // Attributes //
    private final FileConfiguration configuration;
    private final FileConfiguration broadcasts;
    private final FileConfiguration toggles;

    // Constructor //
    public ConfigManager(FileConfiguration configuration, FileConfiguration broadcasts, FileConfiguration toggles) {
        this.configuration = configuration;
        this.broadcasts = broadcasts;
        this.toggles = toggles;
    }

    // Returns the time between every broadcast (in ticks.. 20 ticks is 1 sec) //
    public int getTimeBetweenMessages() {
        return configuration.getInt("time-between-messages");
    }

    // Returns true if the broadcasts should be sent randomly //
    public boolean getRandom() {
        return configuration.getBoolean("random");
    }

    // Returns true if the exempt permission is enabled //
    public boolean getExemptPermission() {
        return configuration.getBoolean("exempt-permission");
    }

    // Returns a list of the disabled world(s) //
    public List<String> getDisabledWorlds() {
        return configuration.getStringList("disabled-worlds");
    }

    // Returns a list of the exempted player(s) //
    public List<String> getExemptedPlayers() {
        return configuration.getStringList("exempted-players");
    }

    // Returns the unknown command message //
    public String getUnknownCommandMessage() {
        return Utils.applyFormat(configuration.getString("unknown-command"));
    }

    // Returns the no permission message //
    public String getNoPermissionMessage() {
        return Utils.applyFormat(configuration.getString("no-permission"));
    }

    // Returns the plugin reload command message //
    public String getPluginReloadMessage() {
        return Utils.applyFormat(configuration.getString("plugin-reload"));
    }

    // Returns the plugin toggle command messages //
    public String getBroadcastToggleMessage(String type) {
        if(type.equals("on"))
            return Utils.applyFormat(configuration.getString("toggle-on-message"));
        else
            return Utils.applyFormat(configuration.getString("toggle-off-message"));
    }

    // Returns a list of all the existing broadcasts (Staff, Discord, etc.) //
    public String[] getBroadcastTitles() {
        // Creates a Set with all the titles of the messages directly from the "broadcasts.yml" file //
        Set<String> broadcastTitlesSet = broadcasts.getConfigurationSection("broadcasts").getKeys(false);
        // Creates a list of the size of the "broadcastTitlesSet" Set //
        String[] broadcastTitlesList = new String[broadcastTitlesSet.size()];
        // Changes the created array to set the collected titles (Set) inside it //
        return broadcastTitlesSet.toArray(broadcastTitlesList);
    }

    // Returns a list of all the messages in a broadcast //
    public List<String> getBroadcastMessagesList(String broadcastTitle) {
        return broadcasts.getStringList("broadcasts." + broadcastTitle + ".messages");
    }

    // Returns the broadcast "click" message/command/suggestion string if applicable //
    public String getBroadcastClick(String broadcastTitle) {
        return broadcasts.getString("broadcasts." + broadcastTitle + ".click");
    }

    // Returns the broadcast "hover" message(s) list if applicable //
    public List<String> getBroadcastHoverList(String broadcastTitle) {
        return broadcasts.getStringList("broadcasts." + broadcastTitle + ".hover");
    }

    // Tries to get the broadcast sound value (if it exist...) //
    public Sound getBroadcastSound(String broadcastTitle) {
        try {
            return Sound.valueOf(broadcasts.getString("broadcasts." + broadcastTitle + ".sound"));
        } catch(Exception exception) {
            return null;
        }
    }

    public void setPlayerToggle(Player player, boolean toggle) {
        toggles.set(player.getUniqueId() + ".toggled", toggle);
    }

    public boolean getPlayerToggle(Player player) {
        if(!toggles.getKeys(false).contains(player.getUniqueId().toString()))
            return true;
        return toggles.getBoolean(player.getUniqueId() + ".toggled");
    }
}