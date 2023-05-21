package ca.pandaaa.utils;

import ca.pandaaa.automaticbroadcast.AutomaticBroadcast;
import ca.pandaaa.automaticbroadcast.Broadcast;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    // Applies the format used threw the whole plugin (returns a formatted string) //
    public static String applyFormat(String message) {
        // Replaces the double arrows automatically //
        message = message.replace(">>", "»").replace("<<", "«");

        // Changes the hex color code to set the colors //
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]){6}");
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
            String before = message.substring(0, matcher.start());
            String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = hexPattern.matcher(message);
        }

        // Returns the message with the correct colors and formats //
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Applies the hover event on the broadcast //
    public static void setHoverBroadcastEvent(TextComponent component, List<String> hoverMessagesList, Player broadcastReceivers) {
        // Checks for empty hover arguments //
        if (hoverMessagesList.size() == 0)
            return;

        // Creates an empty component builder //
        ComponentBuilder hoverMessageBuilder = new ComponentBuilder();
        int hoverLine = 0;
        // For all the hover messages of the broadcast (config: broadcastTitle.hover) //
        for (String hoverMessages : hoverMessagesList) {
            // If PAPI is installed, changes the messages to replace the placeholders //
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && broadcastReceivers != null)
                hoverMessages = PlaceholderAPI.setPlaceholders(broadcastReceivers, hoverMessages);
            // Adds the message to the component builder //
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(applyFormat(hoverMessages)));
            hoverMessageBuilder.append(textComponent);
            // Changes the line if not at the last message //
            if (hoverLine != (hoverMessagesList.size() - 1)) {
                hoverMessageBuilder.append("\n");
            }
            hoverLine++;
        }

        // Applies the hover message (the component builder) to the message desired //
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessageBuilder.create()));
    }

    // Applies the click event on the broadcast //
    public static void setClickBroadcastEvent(TextComponent component, String click) {
        // If the click string is null, return //
        if (click == null || click.length() == 0) return;

        // Checks the char at the start of the click string (config: broadcastTitle.click)
        switch (click.charAt(0)) {
            // '/' suggests a command (with the /) //
            // '*' suggests a message (without the *) //
            // Anything else will try to open a link (will not work if the link is not a real link) //
            case '/':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click));
                break;
            case '*':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(1)));
                break;
            default:
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
                break;
        }
    }
}
