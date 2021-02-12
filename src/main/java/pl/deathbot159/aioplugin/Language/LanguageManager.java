package pl.deathbot159.aioplugin.Language;

import pl.deathbot159.aioplugin.JSONManagers.FileManager;
import pl.deathbot159.aioplugin.JSONManagers.JSONEntry;
import pl.deathbot159.aioplugin.PluginUtils.Console;

public class LanguageManager extends FileManager {
    //================================================================================
    // Constructor
    //================================================================================
    public LanguageManager() {
        super("language.json", "{\n" +
                "   \"EventMessages\":{\n" +
                "       \"JoinMessage\": \"&a[+] &l%{displayname}%&r&a joined to game!\",\n" +
                "       \"Leave\": \"&c[-] &l%{displayname}%&r&c left a game!\",\n" +
                "   },\n" +
                "   \"Tab\":{\n" +
                "       \"top\": \"Top message\\nChange it in AiOPlugin\\language.json\",\n" +
                "       \"bottom\": \"Bottom message\"\n" +
                "   }\n" +
                "}");
        Console.log("LanguageManager:init", "Initialized.");
    }
}
