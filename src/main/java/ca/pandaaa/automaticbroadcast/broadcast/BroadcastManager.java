package ca.pandaaa.automaticbroadcast.broadcast;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ca.pandaaa.automaticbroadcast.AutomaticBroadcast;
import ca.pandaaa.automaticbroadcast.utils.ConfigManager;
import ca.pandaaa.automaticbroadcast.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BroadcastManager implements Listener {

    // Classes instances //
    AutomaticBroadcast plugin = AutomaticBroadcast.getPlugin();
    ConfigManager configManager = plugin.getConfigManager();

    // Attributes //
    private int currentIndex;
    private final List<Broadcast> broadcastList;

    // Constructor //
    public BroadcastManager(List<Broadcast> broadcastList) {
        currentIndex = 0;
        this.broadcastList = broadcastList;
    }

    // Sends the automatic broadcasts (the principal function of the plugin) //
    public void automaticBroadcast() {
        if (configManager.getRandom())
            sendRandomBroadcast();
        else {
            sendBroadcast(broadcastList.get(currentIndex));
            // Increments the index (which broadcast is being displayed next) //
            if (currentIndex == broadcastList.size() - 1) currentIndex = 0;
            else currentIndex++;
        }
    }

    // Changes the index of the next broadcast message to be random //
    private void sendRandomBroadcast() {
        // Getting a random integer (from 0 to the size of the broadcastList (list of all the broadcasts))
        int randomNumber = new Random().nextInt(broadcastList.size());
        // If the number chosen randomly is the same as the last displayed broadcast's index, we call the function again //
        // If there is only one broadcast, we continue with the same broadcast (cannot choose another one...) //
        if (randomNumber == currentIndex && broadcastList.size() != 1) {
            sendRandomBroadcast();
            return;
        }
        currentIndex = randomNumber;

        sendBroadcast(broadcastList.get(currentIndex));
    }

    // Sends the automatic broadcast //
    private void sendBroadcast(Broadcast broadcast) {
        for (Player broadcastReceivers : getReceivers()) {
            // If the broadcast has a determined sound, it is played at the location of the player //
            if (configManager.getBroadcastSound(broadcastList.get(currentIndex).getTitle()) != null)
                broadcastReceivers.playSound(broadcastReceivers.getLocation(), broadcast.getSound(), 1, 1);

            // For all the messages in the broadcast (config: broadcastTitle.messages) //
            for (String broadcastMessages : broadcast.getMessages()) {

                // Changes the placeholder(s) using PAPI (if applicable)
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    broadcastMessages = PlaceholderAPI.setPlaceholders(broadcastReceivers, broadcastMessages);

                // Creates a component message, which is the formatted message //
                TextComponent message = new TextComponent(TextComponent.fromLegacyText(Utils.applyFormat(broadcastMessages)));
                // Calls the functions to add the hover and click events to this component //
                Utils.setHoverBroadcastEvent(message, broadcast.getHoverMessages(), broadcastReceivers);
                Utils.setClickBroadcastEvent(message, broadcast.getClickMessage());

                // Sends the message with the correct format and the click and hover events (if applicable) //
                broadcastReceivers.spigot().sendMessage(message);
            }
        }
    }

    // Returns all the player(s) that will receive the broadcast //
    // -> The player must not be in a disabled world.
    // -> The player must not be in the exempted player list.
    // -> If the exempt permission is enabled, the player must not have the exempt permission.
    public Collection<? extends Player> getReceivers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (!configManager.getDisabledWorlds().isEmpty())
            players = players.stream().filter(player -> !configManager.getDisabledWorlds().contains(player.getWorld().getName())).collect(Collectors.toList());
        if (!configManager.getExemptedPlayers().isEmpty())
            players = players.stream().filter(player -> !configManager.getExemptedPlayers().contains(player.getName())).collect(Collectors.toList());
        if (configManager.getExemptPermission())
            players = players.stream().filter(player -> !player.hasPermission("automaticbroadcast.exempt")).collect(Collectors.toList());
        players = players.stream().filter(player -> plugin.getBroadcastToggle().isBroadcastToggledOn(player)).collect(Collectors.toList());
        return players;
    }

    // Restores the player's toggle status on join event. //
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        plugin.getBroadcastToggle().restoreBroadcastToggle(event.getPlayer());
    }

    // Saves the player's toggle status on quit event. //
    @EventHandler
    public void onLeaveEvent(PlayerQuitEvent event) {
        plugin.getBroadcastToggle().saveBroadcastToggle(event.getPlayer());
    }
}