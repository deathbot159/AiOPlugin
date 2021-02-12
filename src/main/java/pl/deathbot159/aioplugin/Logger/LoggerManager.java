package pl.deathbot159.aioplugin.Logger;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import pl.deathbot159.aioplugin.PluginUtils.Console;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerManager {
    //================================================================================
    // Variables
    //================================================================================
    private final Path pluginPath = Paths.get(System.getProperty("user.dir")+"/plugins/AiOPlugin/");

    private String name,path;
    private File file;
    private JSONObject jsonObject;

    public boolean isWorking = false;
    //================================================================================
    // Constructor
    //================================================================================
    public LoggerManager(){
        try {
            if(!Files.exists(Paths.get(this.pluginPath.toString())))
                Files.createDirectory(this.pluginPath);
            if (!Files.exists(Paths.get(this.pluginPath.toString() + "/logs")))
                Files.createDirectory(Paths.get(this.pluginPath.toString() + "/logs"));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime now = LocalDateTime.now();
            int i = 1;
            while (Files.exists(Paths.get(this.pluginPath.toString() + "/logs/" + "logs-" + dtf.format(now) + "#" + i+".log"))) {
                i++;
            }
            this.name = "logs-" + dtf.format(now) + "#" + i+".log";
            this.path = Paths.get(this.pluginPath.toString() + "/logs/" + this.name).toString();
            this.file = new File(this.path);
            if(this.file.createNewFile()){
                isWorking = true;
            }
        }catch(IOException ex){
            ex.printStackTrace();
            isWorking = false;
        }
    }

    //================================================================================
    // Utils
    //================================================================================
    private String prefix(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss @ dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now)+" | AiOPlugin | ";
    }
    //================================================================================
    // Functions
    //================================================================================
    public void log(String functionName, String text){
        if(!isWorking) return;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.file, true), StandardCharsets.UTF_8);
            writer.write(prefix()+functionName+" | "+text+"\n");
            writer.flush(); writer.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
