package ca.pandaaa.automaticbroadcast.broadcast;

import ca.pandaaa.automaticbroadcast.AutomaticBroadcast;
import ca.pandaaa.automaticbroadcast.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastToggle {
    // True = will receive broadcasts
    // False = will not receive broadcasts
    private final ConcurrentHashMap<UUID, Boolean> broadcastToggle = new ConcurrentHashMap<>();
    private final ConfigManager config;

    public BroadcastToggle(ConfigManager config) {
        this.config = config;

        if(config.isToggleDisabled())
            return;

        for(Player player : Bukkit.getOnlinePlayers()) {
            restoreBroadcastToggle(player);
        }
    }

    public void togglePlayerBroadcast(Player player, String type) {
        if(!broadcastToggle.containsKey(player.getUniqueId()))
            broadcastToggle.put(player.getUniqueId(), true);

        if(type.equals("on") || (type.equals("Toggle") && !broadcastToggle.get(player.getUniqueId()))) {
            broadcastToggle.put(player.getUniqueId(), true);
            player.sendMessage(config.getBroadcastToggleMessage("on"));
        } else if(type.equals("off") || (type.equals("Toggle") && broadcastToggle.get(player.getUniqueId()))) {
            broadcastToggle.put(player.getUniqueId(), false);
            player.sendMessage(config.getBroadcastToggleMessage("off"));
        }
    }

    public boolean isBroadcastToggledOn(Player player) {
        if(!broadcastToggle.containsKey(player.getUniqueId()))
            broadcastToggle.put(player.getUniqueId(), true);

        return broadcastToggle.get(player.getUniqueId());
    }

    public void saveBroadcastToggle(Player player) {
        UUID uuid = player.getUniqueId();
        boolean state = broadcastToggle.getOrDefault(uuid, true);

        Bukkit.getScheduler().runTaskAsynchronously(AutomaticBroadcast.getPlugin(), () -> {
            config.setPlayerToggle(player, state);
        });
    }

    public void restoreBroadcastToggle(Player player) {
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(AutomaticBroadcast.getPlugin(), () -> {
            boolean state = config.getPlayerToggle(player);

            Bukkit.getScheduler().runTask(AutomaticBroadcast.getPlugin(), () -> {
                broadcastToggle.put(uuid, state);
            });
        });
    }
}
