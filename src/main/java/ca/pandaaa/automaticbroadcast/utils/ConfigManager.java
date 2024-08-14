package ca.pandaaa.automaticbroadcast.utils;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class ConfigManager {
    private final FileConfiguration configuration;
    private final FileConfiguration broadcasts;
    private final FileConfiguration toggles;

    public ConfigManager(FileConfiguration configuration) {
        this.configuration = configuration;
        this.broadcasts = null;
        this.toggles = null;
    }

    public ConfigManager(FileConfiguration configuration, FileConfiguration broadcasts, FileConfiguration toggles) {
        this.configuration = configuration;
        this.broadcasts = broadcasts;
        this.toggles = toggles;
    }

    public int getTimeBetweenMessages() {
        return configuration.getInt("time-between-messages");
    }

    public boolean getRandom() {
        return configuration.getBoolean("random");
    }

    public boolean getExemptPermission() {
        return configuration.getBoolean("exempt-permission");
    }

    public List<String> getDisabledWorlds() {
        return configuration.getStringList("disabled-worlds");
    }

    public List<String> getExemptedPlayers() {
        return configuration.getStringList("exempted-players");
    }

    public String getUnknownCommandMessage() {
        return Utils.applyFormat(configuration.getString("unknown-command"));
    }

    public String getNoPermissionMessage() {
        return Utils.applyFormat(configuration.getString("no-permission"));
    }

    public String getPluginReloadMessage() {
        return Utils.applyFormat(configuration.getString("plugin-reload"));
    }

    public String getBroadcastToggleMessage(String type) {
        if(type.equals("on"))
            return Utils.applyFormat(configuration.getString("toggle-on-message"));
        else
            return Utils.applyFormat(configuration.getString("toggle-off-message"));
    }

    public String[] getBroadcastTitles() {
        ConfigurationSection broadcastsTitleSection = broadcasts.getConfigurationSection("broadcasts");
        if(broadcastsTitleSection == null)
            return new String[0];

        Set<String> broadcastsTitleSet = broadcastsTitleSection.getKeys(false);
        String[] broadcastTitlesList = new String[broadcastsTitleSet.size()];
        return broadcastsTitleSet.toArray(broadcastTitlesList);
    }

    public List<String> getBroadcastMessagesList(String broadcastTitle) {
        return broadcasts.getStringList("broadcasts." + broadcastTitle + ".messages");
    }

    public String getBroadcastClick(String broadcastTitle) {
        return broadcasts.getString("broadcasts." + broadcastTitle + ".click");
    }

    public List<String> getBroadcastHoverList(String broadcastTitle) {
        return broadcasts.getStringList("broadcasts." + broadcastTitle + ".hover");
    }

    public Sound getBroadcastSound(String broadcastTitle) {
        try {
            return Sound.valueOf(broadcasts.getString("broadcasts." + broadcastTitle + ".sound"));
        } catch(Exception exception) {
            return null;
        }
    }

    public List<String> getBroadcastExemptedPlayers(String broadcastTitle) {
        return broadcasts.getStringList("broadcasts." + broadcastTitle + ".exempted_players");
    }

    public List<String> getBroadcastConsoleCommands(String broadcastTitle) {
        return broadcasts.getStringList("broadcasts." + broadcastTitle + ".console-commands");
    }

    public void setPlayerToggle(Player player, boolean toggle) {
        toggles.set(player.getUniqueId() + ".toggled", toggle);
    }

    public boolean getPlayerToggle(Player player) {
        if(!toggles.getKeys(false).contains(player.getUniqueId().toString()))
            return true;
        return toggles.getBoolean(player.getUniqueId() + ".toggled");
    }

    public boolean isToggleDisabled() {
        return configuration.getBoolean("disable-toggle");
    }
}