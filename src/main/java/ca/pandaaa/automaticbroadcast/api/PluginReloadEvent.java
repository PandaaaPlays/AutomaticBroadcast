package ca.pandaaa.automaticbroadcast.api;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PluginReloadEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private CommandSender sender;

    public PluginReloadEvent(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getReloadSender() {
        return sender;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
