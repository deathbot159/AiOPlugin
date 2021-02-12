package pl.deathbot159.aioplugin.PluginUtils;

public class StringManager {
    //================================================================================
    // Functions
    //================================================================================
    public static String removeChar(int index, String text){
        StringBuilder stringBuilder = new StringBuilder();
        String[] splited = text.split("", -1);
        for (int i = 0; i < splited.length; i++) {
            if(i == index) continue;
            stringBuilder.append(splited[i]);
        }
        return stringBuilder.toString();
    }
}
