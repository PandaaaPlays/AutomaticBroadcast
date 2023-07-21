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

public class Commands implements CommandExecutor {
    // ConfigManager instance //
    ConfigManager configManager = AutomaticBroadcast.getPlugin().getConfigManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String message, String[] args) {
        // If the sender is neither a player nor the console, cancel! //
        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) return false;

        if (command.getName().equalsIgnoreCase("automaticbroadcast")) {
            // If there are no arguments, sends the error and return false //
            if (args.length == 0) {
                sendUnknownCommandMessage(sender);
                return false;
            }

            // Checks the first argument //
            switch (args[0].toLowerCase()) {
                // "reload" will reload the configurations //
                case "reload":
                    reloadPlugin(sender);
                    break;
                // "list" will display all the broadcasts in one shot //
                case "list":
                    sendList(sender);
                    break;
                // "toggle" will toggle the
                case "toggle":
                    if(args.length == 2)
                        toggleBroadcast(sender, args[1]);
                    else
                        toggleBroadcast(sender, "Toggle");
                    break;
                // Anything else will send the error //
                default:
                    sendUnknownCommandMessage(sender);
                    break;
            }
        }
        return false;
    }

    // Reloads the plugin //
    public void reloadPlugin(CommandSender sender) {
        if (!sender.hasPermission("automaticbroadcast.config")) {
            sendNoPermissionMessage(sender);
            return;
        }

        // Reloads the configurations and sends the confirmation message //
        AutomaticBroadcast.getPlugin().reloadConfig(sender);
    }

    // Toggle the broadcasts //
    public void toggleBroadcast(CommandSender sender, String type) {
        if (!sender.hasPermission("automaticbroadcast.toggle") || !(sender instanceof Player)) {
            sendNoPermissionMessage(sender);
            return;
        }

        type = type.toLowerCase();

        if(!type.equals("on") && !type.equals("off"))
            type = "Toggle";

        // Reloads the configurations and sends the confirmation message //
        AutomaticBroadcast.getPlugin().getBroadcastToggle().togglePlayerBroadcast(((Player) sender), type);
    }

    // Sends all the broadcast to the sender of the command //
    public void sendList(CommandSender sender) {
        if (!sender.hasPermission("automaticbroadcast.config")) {
            sendNoPermissionMessage(sender);
            return;
        }

        // For all the broadcasts //
        for (Broadcast broadcast : AutomaticBroadcast.getPlugin().getBroadcastList()) {
            // For all the messages of each broadcast //
            for (String broadcastMessages : broadcast.getMessages()) {
                // Creates a message component which is the message formatted //
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof Player)
                    broadcastMessages = PlaceholderAPI.setPlaceholders((Player) sender, broadcastMessages);
                TextComponent message = new TextComponent(TextComponent.fromLegacyText(Utils.applyFormat(broadcastMessages)));
                // Applies the hover and click event to the component //
                if (sender instanceof Player)
                    Utils.setHoverBroadcastEvent(message, broadcast.getHoverMessages(), (Player) sender);
                else
                    Utils.setHoverBroadcastEvent(message, broadcast.getHoverMessages(), null);
                Utils.setClickBroadcastEvent(message, broadcast.getClickMessage());

                // Sends the message with the correct format and all it's events to the sender //
                sender.spigot().sendMessage(message);
            }
        }
    }

    // Sends the unknownCommand error message //
    private void sendUnknownCommandMessage(CommandSender sender) {
        sender.sendMessage(configManager.getUnknownCommandMessage());
    }

    // Sends the noPermission error message //
    private void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(configManager.getNoPermissionMessage());
    }
}