package ca.pandaaa.automaticbroadcast.commands;

import ca.pandaaa.automaticbroadcast.AutomaticBroadcast;
import ca.pandaaa.automaticbroadcast.broadcast.Broadcast;
import ca.pandaaa.automaticbroadcast.utils.ConfigManager;
import ca.pandaaa.automaticbroadcast.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    ConfigManager configManager = AutomaticBroadcast.getPlugin().getConfigManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String message, String[] args) {
        if (!(sender instanceof Player)
                && !(sender instanceof ConsoleCommandSender))
            return false;

        if (command.getName().equalsIgnoreCase("automaticbroadcast")) {
            if (args.length == 0) {
                sendUnknownCommandMessage(sender);
                return false;
            }

            switch (args[0].toLowerCase()) {
                case "reload":
                    reloadPlugin(sender);
                    break;
                case "list":
                    sendList(sender);
                    break;
                case "toggle":
                    if(args.length == 2)
                        toggleBroadcast(sender, args[1]);
                    else
                        toggleBroadcast(sender, "Toggle");
                    break;
                default:
                    sendUnknownCommandMessage(sender);
                    break;
            }
        }
        return false;
    }

    private void reloadPlugin(CommandSender sender) {
        if (!sender.hasPermission("automaticbroadcast.config")) {
            sendNoPermissionMessage(sender);
            return;
        }

        AutomaticBroadcast.getPlugin().reloadConfig(sender);
    }

    private void toggleBroadcast(CommandSender sender, String type) {
        if(configManager.isToggleDisabled()) {
            sendUnknownCommandMessage(sender);
            return;
        }

        if (!sender.hasPermission("automaticbroadcast.toggle") || !(sender instanceof Player)) {
            sendNoPermissionMessage(sender);
            return;
        }

        type = type.toLowerCase();

        if(!type.equals("on") && !type.equals("off"))
            type = "Toggle";

        AutomaticBroadcast.getPlugin().getBroadcastToggle().togglePlayerBroadcast(((Player) sender), type);
    }

    private void sendList(CommandSender sender) {
        if (!sender.hasPermission("automaticbroadcast.config")) {
            sendNoPermissionMessage(sender);
            return;
        }

        for (Broadcast broadcast : AutomaticBroadcast.getPlugin().getBroadcastList()) {
            for (String broadcastMessages : broadcast.getMessages()) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof Player)
                    broadcastMessages = PlaceholderAPI.setPlaceholders((Player) sender, broadcastMessages);
                TextComponent message = new TextComponent(TextComponent.fromLegacyText(Utils.applyFormat(broadcastMessages)));
                if (sender instanceof Player)
                    Utils.setHoverBroadcastEvent(message, broadcast.getHoverMessages(), (Player) sender);
                else
                    Utils.setHoverBroadcastEvent(message, broadcast.getHoverMessages(), null);
                Utils.setClickBroadcastEvent(message, broadcast.getClickMessage());

                sender.spigot().sendMessage(message);
            }
        }
    }

    private void sendUnknownCommandMessage(CommandSender sender) {
        sender.sendMessage(configManager.getUnknownCommandMessage());
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(configManager.getNoPermissionMessage());
    }
}