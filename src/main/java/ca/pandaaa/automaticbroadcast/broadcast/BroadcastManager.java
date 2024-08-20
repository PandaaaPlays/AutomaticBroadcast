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
    AutomaticBroadcast plugin = AutomaticBroadcast.getPlugin();
    ConfigManager configManager = plugin.getConfigManager();
    private int currentIndex;
    private final List<Broadcast> broadcastList;

    public BroadcastManager(List<Broadcast> broadcastList) {
        currentIndex = 0;
        this.broadcastList = broadcastList;
    }

    public void automaticBroadcast() {
        if (configManager.getRandom())
            sendRandomBroadcast();
        else {
            sendBroadcast(broadcastList.get(currentIndex));
            if (currentIndex == broadcastList.size() - 1) currentIndex = 0;
            else currentIndex++;
        }
    }

    private void sendRandomBroadcast() {
        int randomNumber = new Random().nextInt(broadcastList.size());
        if (randomNumber == currentIndex && broadcastList.size() != 1) {
            sendRandomBroadcast();
            return;
        }
        currentIndex = randomNumber;

        sendBroadcast(broadcastList.get(currentIndex));
    }

    public void sendBroadcast(Broadcast broadcast) {
        for (Player broadcastReceiver : getReceivers(broadcast)) {
            if (configManager.getBroadcastSound(broadcastList.get(currentIndex).getTitle()) != null)
                broadcastReceiver.playSound(broadcastReceiver.getLocation(), broadcast.getSound(), 1, 1);

            for (String broadcastMessage : broadcast.getMessages()) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    broadcastMessage = PlaceholderAPI.setPlaceholders(broadcastReceiver, broadcastMessage);

                TextComponent message = new TextComponent(TextComponent.fromLegacyText(Utils.applyFormat(broadcastMessage)));
                Utils.setHoverBroadcastEvent(message, broadcast.getHoverMessages(), broadcastReceiver);
                Utils.setClickBroadcastEvent(message, broadcast.getClickMessage());

                broadcastReceiver.spigot().sendMessage(message);
            }
        }
        for(String command : broadcast.getConsoleCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        }
    }

    // -> The player must not be in a disabled world.
    // -> The player must not be in the exempted player list.
    // -> If the exempt permission is enabled, the player must not have the exempt permission.
    // -> The player must have the broadcasts toggled on.
    // -> The player must not be exempted for the specific broadcast.
    public Collection<? extends Player> getReceivers(Broadcast broadcast) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (!configManager.getDisabledWorlds().isEmpty())
            players = players.stream().filter(player -> !configManager.getDisabledWorlds().contains(player.getWorld().getName())).collect(Collectors.toList());
        if (!configManager.getExemptedPlayers().isEmpty())
            players = players.stream().filter(player -> !configManager.getExemptedPlayers().contains(player.getName())).collect(Collectors.toList());
        if (configManager.getExemptPermission())
            players = players.stream().filter(player -> !player.hasPermission("automaticbroadcast.exempt")).collect(Collectors.toList());
        if (!configManager.isToggleDisabled())
            players = players.stream().filter(player -> plugin.getBroadcastToggle().isBroadcastToggledOn(player)).collect(Collectors.toList());
        players = players.stream().filter(player -> !broadcast.getExemptedPlayers().contains(player.getName())).collect(Collectors.toList());

        return players;
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if (!configManager.isToggleDisabled())
            plugin.getBroadcastToggle().restoreBroadcastToggle(event.getPlayer());
    }

    @EventHandler
    public void onLeaveEvent(PlayerQuitEvent event) {
        if (!configManager.isToggleDisabled())
            plugin.getBroadcastToggle().saveBroadcastToggle(event.getPlayer());
    }
}