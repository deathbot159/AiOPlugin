package pl.deathbot159.aioplugin.JSONManagers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.deathbot159.aioplugin.PluginUtils.Console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileManager {
    //================================================================================
    // Variables
    //================================================================================
    private final Path pluginPath = Paths.get(System.getProperty("user.dir")+"/plugins/AiOPlugin/");

    private final String name,path;
    private final File file;
    private JSONObject jsonObject;

    //================================================================================
    // Constructors
    //================================================================================
    public FileManager(String name, Object defaultLines){
        this.name = name; this.path = Paths.get(this.pluginPath+"/"+this.name).toString();
        this.file = new File(this.path);
        try{
            if(!Files.exists(this.pluginPath))  Files.createDirectory(this.pluginPath);
            if(this.file.createNewFile()){
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8);
                writer.write((String)defaultLines); writer.flush(); writer.close();
                Console.log("FileManager:"+this.name, "Created new file.");
            }
            StringBuilder builder = new StringBuilder();
            Stream<String> stream = Files.lines(Paths.get(this.file.getPath()), StandardCharsets.UTF_8);
            stream.forEach(s-> builder.append(s.replaceAll("&", "§")).append("\n"));
            this.jsonObject = (JSONObject) (new JSONParser().parse(builder.toString()));
            Console.log("FileManager:"+this.name, "Loaded file.");
        }catch(IOException | ParseException ex){
            Console.error("FileManager:"+this.name, ex.getMessage());
        }
    }

    //================================================================================
    // Functions
    //================================================================================
    public JSONEntry get(String jsonPath){
        String[] splitedPath = jsonPath.split("\\.");
        if(!jsonPath.contains("\\.")) return new JSONEntry(this.jsonObject.get(jsonPath));
        Object temp = null;
        for(String s: splitedPath){
            if(temp != null) ((JSONObject)temp).get(s);
            else temp = getJSONObject().get(s);
        }
        return new JSONEntry(temp);
    }

    //================================================================================
    // Access methods
    //================================================================================
    public File getFile() {
        return file;
    }

    public JSONObject getJSONObject() {
        return jsonObject;
    }
}
