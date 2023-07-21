package ca.pandaaa.automaticbroadcast.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {

    // Proposes completion for the automaticbroadcast command //
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String arg, String[] args) {
        List<String> completionList = new ArrayList<>();
        if (sender.hasPermission("automaticbroadcast.config")) {
            if(args.length == 1) {
                completionList.add("list");
                completionList.add("reload");
            }
        }
        if(sender.hasPermission("automaticbroadcast.toggle")) {
            if(args.length == 1)
                completionList.add("toggle");
            if(args.length == 2) {
                completionList.add("on");
                completionList.add("off");
            }
        }
        return completionList;
    }
}