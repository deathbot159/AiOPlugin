package pl.deathbot159.aioplugin.PredefinedMessages;

import pl.deathbot159.aioplugin.JSONManagers.FileManager;
import pl.deathbot159.aioplugin.PluginUtils.Console;
import pl.deathbot159.aioplugin.PluginUtils.StringManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PredefinedMessageManager extends FileManager {
    //================================================================================
    // Variables
    //================================================================================
    private HashMap<String, String> predefinedMessages = new HashMap<>();

    //================================================================================
    // Constructor
    //================================================================================
    public PredefinedMessageManager() {
        super("predefinedMessages.json", "{\n\n}");
        this.load();
        Console.log("PredefinedMessageManager:init", "Initialized.");
    }

    //================================================================================
    // Functions
    //================================================================================
    private void load(){
        String temp = StringManager.removeChar(0, this.getJSONObject().toString());
        temp = StringManager.removeChar(temp.length()-1, temp);
        String[] splited = temp.split(",", -1);
        if(splited.length > 1)
            for (String s : splited) {
                String[] json = s.replaceAll("\"", "").split(":", 2);
                String key = json[0];
                String value = json[1];
                if(json.length > 2){
                    List<String> stringList = new ArrayList<>();
                    for (int i = 1; i < json.length; i++) {
                        stringList.add(json[i]+":");
                    }
                    value = String.join("", stringList);
                }
                this.predefinedMessages.put(key, value);
            }
    }

    //================================================================================
    // Access methods
    //================================================================================
    public String getMessage(String key){
        if(getPredefinedMessages().containsKey(key))
            return getPredefinedMessages().get(key);
        return null;
    }

    public HashMap<String, String> getPredefinedMessages() {
        return predefinedMessages;
    }
}
