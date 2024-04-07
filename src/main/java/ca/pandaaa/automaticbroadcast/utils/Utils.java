package ca.pandaaa.automaticbroadcast.utils;

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
    public static String applyFormat(String message) {
        message = message.replace(">>", "»").replace("<<", "«");

        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]){6}");
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
            String before = message.substring(0, matcher.start());
            String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = hexPattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void setHoverBroadcastEvent(TextComponent component, List<String> hoverMessagesList, Player broadcastReceivers) {
        if (hoverMessagesList.isEmpty())
            return;

        ComponentBuilder hoverMessageBuilder = new ComponentBuilder();
        int hoverLine = 0;
        for (String hoverMessages : hoverMessagesList) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && broadcastReceivers != null)
                hoverMessages = PlaceholderAPI.setPlaceholders(broadcastReceivers, hoverMessages);
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(applyFormat(hoverMessages)));
            hoverMessageBuilder.append(textComponent);
            if (hoverLine != (hoverMessagesList.size() - 1)) {
                hoverMessageBuilder.append("\n");
            }
            hoverLine++;
        }

        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessageBuilder.create()));
    }

    public static void setClickBroadcastEvent(TextComponent component, String click) {
        if (click == null || click.isEmpty()) return;

        switch (click.charAt(0)) {
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
