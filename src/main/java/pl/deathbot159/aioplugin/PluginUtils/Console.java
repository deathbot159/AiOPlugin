package pl.deathbot159.aioplugin.PluginUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pl.deathbot159.aioplugin.Main.getLoggerManager;

public class Console {
    private static final String pName = "AiOPlugin";
    public static void log(String functionName, Object text){
        System.out.println(Colors.GREEN+pName+ " | "+functionName+" | "+text+Colors.RESET);
        getLoggerManager().log(functionName, (String) text);
    }
    public static void error(String functionName, Object text){
        System.out.println(Colors.RED+pName+ " | " +functionName+" | ERROR | "+text+Colors.RESET);
        getLoggerManager().log(functionName, (String) text);
    }
    public static void warning(String functionName, Object text){
        System.out.println(Colors.YELLOW+pName+ " | " +functionName+" | WARNING | "+text+Colors.RESET);
        getLoggerManager().log(functionName, (String) text);
    }
    public static void debug(String functionName, Object text){
        System.out.println(Colors.GREEN+pName+ " | " +functionName+" | DEBUG | "+text+Colors.RESET);
        getLoggerManager().log(functionName, (String) text);
    }
}
