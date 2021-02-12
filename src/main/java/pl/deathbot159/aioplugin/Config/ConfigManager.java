package pl.deathbot159.aioplugin.Config;

import pl.deathbot159.aioplugin.JSONManagers.FileManager;
import pl.deathbot159.aioplugin.JSONManagers.JSONEntry;
import pl.deathbot159.aioplugin.Main;
import pl.deathbot159.aioplugin.PluginUtils.Console;

public class ConfigManager extends FileManager {
    //================================================================================
    // Constructor
    //================================================================================
    public ConfigManager() {
        super("config.json", "{\n" +
                "   \"configVersion\": \"1.0\",\n" +
                "   \"customJoinLeaveMessage\": true,\n" +
                "   \"bossBarTimer\": true,\n" +
                "   \"monsterHPIndicatorType\": \"bossBar\",\n" +
                "   \"droppedItemNamesVisible\": true,\n" +
                "   \"monsterMoneyDrop\": true,\n" +
                "   \"scoreBoardEnabled\": true\n"+
                "}");
        Console.log("ConfigManager:init", "Initialized.");
    }

    //================================================================================
    // Functions
    //================================================================================
    public JSONEntry get(ConfigEntry entry){
        return this.get(entry.name());
    }
}
