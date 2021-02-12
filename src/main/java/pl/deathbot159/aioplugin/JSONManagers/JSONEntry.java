package pl.deathbot159.aioplugin.JSONManagers;

public class JSONEntry {
    //================================================================================
    // Variables
    //================================================================================
    private final Object entry;

    //================================================================================
    // Constructor
    //================================================================================
    public JSONEntry(Object obj) {
        this.entry = obj;
    }

    //================================================================================
    // Methods
    //================================================================================
    public String toString(){
        return ((String)this.entry);
    }
    public Integer toInteger(){
        return Integer.parseInt(toString());
    }
    public Double toDouble(){
        return Double.parseDouble(toString());
    }
    public boolean toBoolean(){
        return Boolean.parseBoolean(toString());
    }
}
