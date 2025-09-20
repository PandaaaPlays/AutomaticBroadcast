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
import org.bukkit.ChatColor;
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
        if(broadcastList == null || broadcastList.isEmpty())
            return;

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
            // @ means the command is using @p, @a, etc.
            if(!command.isEmpty() && !(Bukkit.getOnlinePlayers().isEmpty() && command.contains("@")))
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        }
        trySendBroadcastDebugMessage(broadcast);
    }

    // -> The player must not be in a disabled world.
    // -> The player must not be in the exempted player list.
    // -> If the exempt permission is enabled, the player must not have the exempt permission.
    // -> The player must have the broadcasts toggled on.
    // -> The player must not be exempted for the specific broadcast.
    // -> The player must have the permission if a permission is specified.
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
        if(broadcast.getPermission() != null)
            players = players.stream().filter(player -> player.hasPermission(broadcast.getPermission())).collect(Collectors.toList());

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
            plugin.getBroadcastToggle().saveBroadcastToggleAsync(event.getPlayer());
    }

    private void trySendBroadcastDebugMessage(Broadcast broadcast) {
        if(configManager.isDebugModeEnabled()) {
            sendConsoleMessage("&4[!] DEBUG MESSAGE");
            sendConsoleMessage("&cDisplaying broadcast '&4" + broadcast.getTitle() + "&c' (note that the console display may be different from in-game chat):");
            for (String broadcastMessage : broadcast.getMessages()) {
                sendConsoleMessage(broadcastMessage);
            }
            if(!broadcast.getHoverMessages().isEmpty()) {
                sendConsoleMessage("&cThe broadcast was sent with this hovering text:");
                for (String hoverMessage : broadcast.getHoverMessages()) {
                    sendConsoleMessage(hoverMessage);
                }
            }
            if(!broadcast.getClickMessage().isEmpty()) {
                sendConsoleMessage("&cThe broadcast was sent with this click action: &f" + broadcast.getClickMessage());
            }
            if(broadcast.getSound() != null) {
                sendConsoleMessage("&cThe broadcast was sent with this sound: &f" + broadcast.getSound());
            }
            if(broadcast.getPermission() != null) {
                sendConsoleMessage("&cThe broadcast was sent to the players with this permission: &f" + broadcast.getPermission());
            }
            if(getReceivers(broadcast).isEmpty()) {
                sendConsoleMessage("&4The broadcast was not sent to any player.");
            } else {
                String names = getReceivers(broadcast).stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));
                sendConsoleMessage("&cThe broadcast was sent to the following players: &f" + names);
            }
            if(!broadcast.getConsoleCommands().isEmpty()) {
                sendConsoleMessage("&cThe broadcast was sent with the following commands:");
                for (String command : broadcast.getConsoleCommands()) {
                    if (!command.isEmpty() && !(Bukkit.getOnlinePlayers().isEmpty() && command.contains("@")))
                        sendConsoleMessage("&f - " + command + " &c(The command was not sent because it was either empty or no player was found that specifies the @ parameter (if specified))");
                    else
                        sendConsoleMessage("&f - " + command);
                }
            }
        }
    }

    private void sendConsoleMessage(String unformattedMessage) {
        AutomaticBroadcast.getPlugin().getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', unformattedMessage));
    }
}