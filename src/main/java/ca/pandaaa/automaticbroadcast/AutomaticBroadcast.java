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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutomaticBroadcast extends JavaPlugin {
    // Generates the broadcasts.yml file //
    private File broadcastsFile;
    private FileConfiguration broadcastsConfig;
    // Generates the broadcast-toggles.yml file //
    private File togglesFile;
    private FileConfiguration togglesConfig;

    // Attributes //
    private static AutomaticBroadcast plugin;
    private ConfigManager configManager;
    private BroadcastManager broadcastManager;
    private BukkitTask automaticBroadcastTask;
    private List<Broadcast> broadcastList;
    private BroadcastToggle broadcastToggle;

    // What should happen when the plugin enables //
    @Override
    public void onEnable() {
        // Plugin initialization //
        plugin = this;

        // BStats initialization //
        int pluginId = 13749;
        Metrics metrics = new Metrics(this, pluginId);

        // Configurations initialization //
        saveDefaultConfigurations();
        loadConfigurations();
        configManager = new ConfigManager(getConfig(), broadcastsConfig, togglesConfig);

        // Changes the manager with a new one (because the configurations might have changed) //
        broadcastManager = new BroadcastManager(createBroadcastList());

        // Creates the command and sync the listener //
        getCommandsAndListeners();

        broadcastToggle = new BroadcastToggle(configManager);

        // Starts the broadcasting //
        startBroadcasting();

        // Console message when the plugin is fully enabled //
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "   &3_____  &b_____"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "  &3|  _  |&b| __  |  &3Auto&bmatic&8Broad&7cast"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "  &3|     |&b| __ -|    &7Version " + getDescription().getVersion()));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "  &3|__|__|&b|_____|      &7by &8Pa&7nd&5aaa"));
        getServer().getConsoleSender().sendMessage("");
    }

    // Saves the online players' toggle status on disabled of the plugin //
    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            broadcastToggle.saveBroadcastToggle(player);
        }
    }

    // Saves the default configuration files (creates them in the case that they don't already exist) //
    private void saveDefaultConfigurations() {
        this.saveDefaultConfig();
        broadcastsFile = new File(getDataFolder(), "broadcasts.yml");
        if (!broadcastsFile.exists())
            saveResource("broadcasts.yml", false);
        broadcastsConfig = new YamlConfiguration();

        togglesFile = new File(getDataFolder(), "broadcast-toggles.yml");
        if (!togglesFile.exists())
            saveResource("broadcast-toggles.yml", false);
        togglesConfig = new YamlConfiguration();
    }

    // Saves the broadcast-toggles.yml file //
    public void saveBroadcastToggles() {
        try {
            togglesConfig.save(togglesFile);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    // Tries to load the broadcasts.yml and broadcast-toggles.yml files //
    private void loadConfigurations() {
        try {
            broadcastsConfig.load(broadcastsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            togglesConfig.load(togglesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    // Creates or changes the command //
    private void getCommandsAndListeners() {
        getCommand("AutomaticBroadcast").setExecutor(new Commands());
        getCommand("AutomaticBroadcast").setTabCompleter(new TabCompletion());
        getServer().getPluginManager().registerEvents(broadcastManager, this);
    }

    // Returns the plugin //
    public static AutomaticBroadcast getPlugin() {
        return plugin;
    }

    // Returns the configuration manager //
    public ConfigManager getConfigManager() {
        return configManager;
    }

    // Starts the broadcasting... //
    private void startBroadcasting() {
        // Stops the runnable if it was already instantiated //
        if (automaticBroadcastTask != null) automaticBroadcastTask.cancel();

        // Generates a runnable that executes the following code in loop //
        automaticBroadcastTask = new BukkitRunnable() {
            @Override
            public void run() {
                broadcastManager.automaticBroadcast();
            }
            // Time between every interval (in seconds) //
        }.runTaskTimer(plugin, 0, 20L * configManager.getTimeBetweenMessages());
    }

    // Reloads the configurations //
    public void reloadConfig(CommandSender sender) {
        // Deletes the config data (not in the file but in the program) //
        plugin.reloadConfig();
        // Replaces the messages data by the ones from the file //
        broadcastsConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        togglesConfig = YamlConfiguration.loadConfiguration(togglesFile);

        // Replaces the commands and attributes //
        configManager = new ConfigManager(getConfig(), broadcastsConfig, togglesConfig);
        broadcastManager = new BroadcastManager(createBroadcastList());
        broadcastToggle = new BroadcastToggle(configManager);
        getCommandsAndListeners();

        // Sends the confirmation message to the command executor //
        sender.sendMessage(configManager.getPluginReloadMessage());
        // Starts the broadcasting //
        startBroadcasting();
    }

    // Creates a list of broadcast for the broadcasts configured in the broadcasts.yml file //
    private List<Broadcast> createBroadcastList() {
        List<Broadcast> broadcastList = new ArrayList<>();
        String[] broadcastTitles = configManager.getBroadcastTitles();
        // If there are broadcast (not 0) //
        if (broadcastTitles.length != 0) {
            for (String title : broadcastTitles) {
                Broadcast broadcast = new Broadcast(title, configManager.getBroadcastMessagesList(title),
                        configManager.getBroadcastSound(title), configManager.getBroadcastHoverList(title),
                        configManager.getBroadcastClick(title));
                broadcastList.add(broadcast);
            }
        }
        this.broadcastList = broadcastList;
        return broadcastList;
    }

    // Returns the list of broadcasts created on start //
    public List<Broadcast> getBroadcastList() {
        return broadcastList;
    }

    // Returns the toggle manager of the plugin //
    public BroadcastToggle getBroadcastToggle() {
        return broadcastToggle;
    }
}