package pl.deathbot159.aioplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.deathbot159.aioplugin.Config.ConfigManager;
import pl.deathbot159.aioplugin.Holograms.Commands.HologramCommand;
import pl.deathbot159.aioplugin.Holograms.Events.displayHoloOnJoin;
import pl.deathbot159.aioplugin.Holograms.HologramManager;
import pl.deathbot159.aioplugin.Language.LanguageManager;
import pl.deathbot159.aioplugin.Logger.LoggerManager;
import pl.deathbot159.aioplugin.PluginUtils.Console;
import pl.deathbot159.aioplugin.PredefinedMessages.PredefinedMessageManager;

public final class Main extends JavaPlugin {
    //================================================================================
    // Managers
    //================================================================================
    private static LoggerManager loggerManager;
    private static ConfigManager configManager;
    private static LanguageManager languageManager;
    private static PredefinedMessageManager predefinedMessageManager;
    private static HologramManager hologramManager;

    //================================================================================
    // Plugin methods
    //================================================================================
    @Override
    public void onEnable() {
        // Initialize static managers
        loggerManager = new LoggerManager();
        configManager = new ConfigManager();
        languageManager = new LanguageManager();
        predefinedMessageManager = new PredefinedMessageManager();
        hologramManager = new HologramManager();

        if(!loggerManager.isWorking){
            Console.error("Main:loggerManager", "LoggerManager isn't working properly! Please contact with plugin developer.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Console.warning("Main:onEnable", "Plugin enabled.");

        // Events
        Bukkit.getPluginManager().registerEvents(new displayHoloOnJoin(), this);

        // Commands
        getCommand("hologram").setExecutor(new HologramCommand());

        // Schedulers
        hologramManager.enableUpdateScheduler(this);
    }

    @Override
    public void onDisable() {
        Console.warning("Main:onDisable", "Plugin disabled.");
    }

    //================================================================================
    // Access methods
    //================================================================================
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static LoggerManager getLoggerManager() {
        return loggerManager;
    }

    public static PredefinedMessageManager getPredefinedMessageManager() {
        return predefinedMessageManager;
    }

    public static LanguageManager getLanguageManager() {
        return languageManager;
    }

    public static HologramManager getHologramManager(){
        return hologramManager;
    }
}
