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
        return applyFormat(message, false);
    }

    public static String applyFormat(String message, boolean isHoverBox) {
        message = message.replace(">>", "»").replace("<<", "«");

        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]){6}");
        Matcher matcherHex = hexPattern.matcher(message);
        while (matcherHex.find()) {
            ChatColor hexColor = ChatColor.of(matcherHex.group().substring(1));
            String before = message.substring(0, matcherHex.start());
            String after = message.substring(matcherHex.end());
            message = before + hexColor + after;
            matcherHex = hexPattern.matcher(message);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if(!isHoverBox) {
            Pattern centeredPattern = Pattern.compile("\\[centered].*");
            Matcher matcherCentered = centeredPattern.matcher(message);
            while (matcherCentered.find()) {
                message = getCenteredMessage(matcherCentered.group().substring(10));

                matcherCentered = centeredPattern.matcher(message);
            }
        }

        return message;
    }

    public static void setHoverBroadcastEvent(TextComponent component, List<String> hoverMessagesList, Player broadcastReceivers) {
        if (hoverMessagesList == null || hoverMessagesList.isEmpty())
            return;

        ComponentBuilder hoverMessageBuilder = new ComponentBuilder();
        int hoverLine = 0;
        for (String hoverMessages : hoverMessagesList) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && broadcastReceivers != null)
                hoverMessages = PlaceholderAPI.setPlaceholders(broadcastReceivers, hoverMessages);
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(applyFormat(hoverMessages, true)));
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
            // '*' suggests a message or command (without the *) //
            // '/' runs a command //
            // Anything else will try to open a link (will not work if the link is not a real link) //
            case '/':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click));
                break;
            case '*':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(1)));
                break;
            default:
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
                break;
        }
    }

    private static String getCenteredMessage(String message) {
        if(message == null || message.isEmpty())
            return message;

        int messagePixelSize = 0;
        boolean previousIsSectionSign = false;
        boolean isBold = false;

        for(char character : message.toCharArray()){
            if(character == '§'){
                previousIsSectionSign = true;
            } else if(previousIsSectionSign) {
                previousIsSectionSign = false;
                isBold = character == 'l' || character == 'L';
            } else {
                FontInformation fontInformation = FontInformation.getDefaultFontInformation(character);
                messagePixelSize += isBold ? fontInformation.getBoldLength() : fontInformation.getLength();
                messagePixelSize++;
            }
        }

        int centerPixel = 154;
        int halvedMessageSize = messagePixelSize / 2;
        int toCompensate = centerPixel - halvedMessageSize;
        int spaceLength = FontInformation.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while(compensated < toCompensate){
            stringBuilder.append(" ");
            compensated += spaceLength;
        }
        return stringBuilder + message;
    }
}
