package pl.deathbot159.aioplugin.JSONManagers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.deathbot159.aioplugin.PluginUtils.Console;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class DataManager {
    //================================================================================
    // Variables
    //================================================================================
    private final Path defaultPath = Paths.get(System.getProperty("user.dir")+"/plugins/AiOPlugin/");
    private final String folder;
    private JSONObject jsonObject;

    //================================================================================
    // Constructor
    //================================================================================
    public DataManager(String folder){
        this.folder = folder;
        if(Files.exists(Paths.get(this.defaultPath.toString()+"/"+this.folder)))
            this.fetch(false);
        else{
            try {
                Files.createDirectory(Paths.get(defaultPath + "/" + this.folder));
                Console.log("DataManager:"+this.folder, "Data folder created.");
            }catch(IOException ex){
                Console.error("DataManager:"+this.folder, ex);
            }
        }
    }

    //================================================================================
    // Functions
    //================================================================================
    private void fetch(boolean silence){
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        try {
            File[] temp = new File(Paths.get(this.defaultPath + "/" + this.folder).toString()).listFiles();
            for (int i = 0; i < Objects.requireNonNull(temp).length; i++) {
                if (!temp[i].isDirectory()) {
                    String uuid = temp[i].getName().replaceAll(".json", "");
                    jsonBuilder.append("\"").append(uuid).append("\":\n");

                    StringBuilder fileLines = new StringBuilder();
                    Stream<String> stream = Files.lines(Paths.get(temp[i].getPath()), StandardCharsets.UTF_8);
                    stream.forEach(s -> jsonBuilder.append("\t").append(s));
                    if (i != temp.length - 1)
                        jsonBuilder.append(",\n");
                    else
                        jsonBuilder.append("\n");
                }
            }
            jsonBuilder.append("}");
            this.jsonObject = (JSONObject) new JSONParser().parse(jsonBuilder.toString());
            if (!silence)
                Console.log("DataManager:"+this.folder, "Loaded "+temp.length+" data file/s.");
        }catch(IOException | ParseException ex){
            Console.error("DataManager:"+this.folder+":fetch()", ex.getMessage());
        }
    }

    public void recreateFile(UUID dataUUID, String data){
        File file = new File(Paths.get(this.defaultPath + "/" + this.folder+"/"+dataUUID.toString()+".json").toString());
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.print(data);
            writer.close();
            this.fetch(true);
        }catch (IOException e) {
            Console.error("DataManager:"+this.folder+":recreateFile()", e.getMessage());
        }
    }

    public JSONObject getFileContent(UUID dataUUID){
        StringBuilder builder = new StringBuilder();
        try{
            Files.lines(Paths.get(this.defaultPath+"/"+this.folder+"/"+dataUUID.toString()+".json"),StandardCharsets.UTF_8).forEach(s->{
                builder.append(s).append("\n");
            });
            return (JSONObject) new JSONParser().parse(builder.toString());
        }catch(IOException | ParseException ex){
            Console.error("DataManager:"+this.folder+":getFileContent()", ex.getMessage());
        }
        return null;
    }

    public Object getFromJSONObject(JSONObject obj, String jsonPath){
        String[] split = jsonPath.split("\\.");
        Object temp = null;
        if(!jsonPath.contains(".")){
            return obj.get(jsonPath);
        }
        for(String s: split){
            if(temp != null){
                temp = ((JSONObject)temp).get(s);
            }else{
                temp = obj.get(s);
            }
        }
        return temp;
    }

    public void createDataFile(UUID dataUUID, String data){
        try {
            JSONObject jsonData = (JSONObject) new JSONParser().parse(data);
            File f = new File(Paths.get(this.defaultPath + "/" + this.folder + "/" + dataUUID.toString() + ".json").toString());
            if (f.createNewFile()) {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
                writer.write(data);
                writer.flush();
                writer.close();
                this.fetch(true);
            }
        }catch(Exception ex){
            Console.error("DataManager:"+this.folder+":createDataFile()", ex.getMessage());
        }
    }

    public void removeDataFile(UUID dataUUID){
        File f = new File(Paths.get(this.defaultPath+"/"+this.folder+"/"+dataUUID.toString()+".json").toString());
        if(f.delete())
            this.fetch(true);
        else
            Console.error("DataManager:"+this.folder+":removeDataFile()", "Data file "+dataUUID.toString()+".json cannot be removed.");
    }

    //================================================================================
    // Access methods
    //================================================================================
    public Object get(String jsonPath){
        String[] split = jsonPath.split("\\.");
        Object temp = null;
        if(!jsonPath.contains(".")){
            return this.jsonObject.get(jsonPath);
        }
        for(String s: split){
            if(temp != null){
                temp = ((JSONObject)temp).get(s);
            }else{
                temp = this.jsonObject.get(s);
            }
        }
        return temp;
    }

    public File[] getFiles(){
        return(new File(Paths.get(this.defaultPath + "/" + this.folder).toString()).listFiles());
    }
}
