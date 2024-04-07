package ca.pandaaa.automaticbroadcast;

import ca.pandaaa.automaticbroadcast.broadcast.Broadcast;
import ca.pandaaa.automaticbroadcast.broadcast.BroadcastManager;
import ca.pandaaa.automaticbroadcast.broadcast.BroadcastToggle;
import ca.pandaaa.automaticbroadcast.commands.Commands;
import ca.pandaaa.automaticbroadcast.commands.TabCompletion;
import ca.pandaaa.automaticbroadcast.utils.ConfigManager;
import ca.pandaaa.automaticbroadcast.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutomaticBroadcast extends JavaPlugin {
    private File broadcastsFile;
    private FileConfiguration broadcastsConfig;
    private File togglesFile;
    private FileConfiguration togglesConfig;
    private static AutomaticBroadcast plugin;
    private ConfigManager configManager;
    private BroadcastManager broadcastManager;
    private BukkitTask automaticBroadcastTask;
    private List<Broadcast> broadcastList;
    private BroadcastToggle broadcastToggle;

    public List<Broadcast> getBroadcastList() {
        return broadcastList;
    }

    public BroadcastToggle getBroadcastToggle() {
        return broadcastToggle;
    }

    @Override
    public void onEnable() {
        plugin = this;
        int pluginId = 13749;
        Metrics metrics = new Metrics(this, pluginId);

        configManager = new ConfigManager(getConfig());
        saveDefaultConfigurations();
        loadConfigurations();
        // The Configuration Manager cannot be used for its broadcasts and toggles properties before this point.
        configManager = new ConfigManager(getConfig(), broadcastsConfig, togglesConfig);
        broadcastManager = new BroadcastManager(createBroadcastList());

        getCommandsAndListeners();

        broadcastToggle = new BroadcastToggle(configManager);

        startBroadcasting();

        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "   &3_____  &b_____"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "  &3|  _  |&b| __  |  &3Auto&bmatic&8Broad&7cast"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "  &3|     |&b| __ -|    &7Version " + getDescription().getVersion()));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "  &3|__|__|&b|_____|      &7by &8Pa&7nd&5aaa"));
        getServer().getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        if (configManager.isToggleDisabled())
            return;

        for(Player player : Bukkit.getOnlinePlayers()) {
            broadcastToggle.saveBroadcastToggle(player);
        }
    }

    private void saveDefaultConfigurations() {
        this.saveDefaultConfig();
        broadcastsFile = new File(getDataFolder(), "broadcasts.yml");
        if (!broadcastsFile.exists())
            saveResource("broadcasts.yml", false);
        broadcastsConfig = new YamlConfiguration();

        if(configManager.isToggleDisabled())
            return;
        togglesFile = new File(getDataFolder(), "broadcast-toggles.yml");
        if (!togglesFile.exists())
            saveResource("broadcast-toggles.yml", false);
        togglesConfig = new YamlConfiguration();
    }

    public void saveBroadcastToggles() {
        if (configManager.isToggleDisabled())
            return;

        try {
            togglesConfig.save(togglesFile);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public static AutomaticBroadcast getPlugin() {
        return plugin;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadConfig(CommandSender sender) {
        plugin.reloadConfig();
        broadcastsConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        togglesConfig = YamlConfiguration.loadConfiguration(togglesFile);

        configManager = new ConfigManager(getConfig(), broadcastsConfig, togglesConfig);
        broadcastManager = new BroadcastManager(createBroadcastList());
        if (!configManager.isToggleDisabled())
            broadcastToggle = new BroadcastToggle(configManager);
        getCommandsAndListeners();

        sender.sendMessage(configManager.getPluginReloadMessage());
        startBroadcasting();
    }

    private List<Broadcast> createBroadcastList() {
        List<Broadcast> broadcastList = new ArrayList<>();
        String[] broadcastTitles = configManager.getBroadcastTitles();

        for (String title : broadcastTitles) {
            Broadcast broadcast = new Broadcast(title, configManager.getBroadcastMessagesList(title),
                    configManager.getBroadcastSound(title), configManager.getBroadcastHoverList(title),
                    configManager.getBroadcastClick(title));
            broadcastList.add(broadcast);
        }
        this.broadcastList = broadcastList;
        return broadcastList;
    }

    private void startBroadcasting() {
        if (automaticBroadcastTask != null) automaticBroadcastTask.cancel();

        automaticBroadcastTask = new BukkitRunnable() {
            @Override
            public void run() {
                broadcastManager.automaticBroadcast();
            }
        }.runTaskTimer(plugin, 0, 20L * configManager.getTimeBetweenMessages());
    }

    private void loadConfigurations() {
        try {
            broadcastsConfig.load(broadcastsFile);
        } catch (IOException | InvalidConfigurationException exception) {
            System.out.println(exception);
        }

        try {
            if(!configManager.isToggleDisabled())
                togglesConfig.load(togglesFile);
        } catch (IOException | InvalidConfigurationException exception) {
            System.out.println(exception);
        }
    }

    private void getCommandsAndListeners() {
        PluginCommand command = getCommand("AutomaticBroadcast");
        if(command == null)
            return;

        command.setExecutor(new Commands());
        command.setTabCompleter(new TabCompletion());
        getServer().getPluginManager().registerEvents(broadcastManager, this);
    }
}