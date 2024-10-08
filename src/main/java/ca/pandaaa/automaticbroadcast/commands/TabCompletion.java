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
        if (sender.hasPermission("automaticbroadcast.reload")) {
            if(args.length == 1) {
                completionList.add("reload");
            }
        }

        if (sender.hasPermission("automaticbroadcast.broadcast")) {
            if(args.length == 1) {
                completionList.add("broadcast");
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("broadcast")) {
                getCompletionBroadcastList(sender, completionList);
            }
        }

        if (sender.hasPermission("automaticbroadcast.preview")) {
            if(args.length == 1) {
                completionList.add("preview");
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("preview")) {
                getCompletionBroadcastList(sender, completionList);
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

    private void getCompletionBroadcastList(CommandSender sender, List<String> completionList) {
        List<Broadcast> broadcastList = AutomaticBroadcast.getPlugin().getBroadcastList();
        List<Broadcast> scheduledBroadcastList = AutomaticBroadcast.getPlugin().getScheduledBroadcastList();
        for (Broadcast broadcast : broadcastList) {
            if(sender.hasPermission("automaticbroadcast." + broadcast.getTitle()))
                completionList.add(broadcast.getTitle());
        }
        if(scheduledBroadcastList != null) {
            for (Broadcast broadcast : scheduledBroadcastList) {
                if(sender.hasPermission("automaticbroadcast." + broadcast.getTitle()))
                    completionList.add(broadcast.getTitle());
            }
        }
    }
}