package ca.pandaaa.automaticbroadcast.commands;

import ca.pandaaa.automaticbroadcast.AutomaticBroadcast;
import ca.pandaaa.automaticbroadcast.broadcast.Broadcast;
import ca.pandaaa.automaticbroadcast.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, @NotNull Command cmd, @NotNull String arg, String[] args) {
        List<String> completionList = new ArrayList<>();
        if (sender.hasPermission("automaticbroadcast.config")) {
            if(args.length == 1) {
                completionList.add("preview");
                completionList.add("reload");
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("preview")) {
                List<Broadcast> broadcastList = AutomaticBroadcast.getPlugin().getBroadcastList();
                for (Broadcast broadcast : broadcastList) {
                    completionList.add(broadcast.getTitle());
                }
            }
        }

        ConfigManager configManager = AutomaticBroadcast.getPlugin().getConfigManager();
        if(sender.hasPermission("automaticbroadcast.toggle") && !configManager.isToggleDisabled()) {
            if(args.length == 1)
                completionList.add("toggle");
            if(args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
                completionList.add("on");
                completionList.add("off");
            }
        }
        return completionList;
    }
}