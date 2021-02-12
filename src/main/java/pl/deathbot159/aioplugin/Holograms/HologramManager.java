package pl.deathbot159.aioplugin.Holograms;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONObject;
import pl.deathbot159.aioplugin.Holograms.Types.HologramPacketType;
import pl.deathbot159.aioplugin.Holograms.Types.HologramUpdateType;
import pl.deathbot159.aioplugin.JSONManagers.DataManager;
import pl.deathbot159.aioplugin.Main;
import pl.deathbot159.aioplugin.PluginUtils.Console;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HologramManager {
    //================================================================================
    // Variables
    //================================================================================
    private final ArrayList<Hologram> holograms = new ArrayList<>();
    private final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    private int lastId;
    private DataManager holoDataManager;

    //================================================================================
    // Constructor
    //================================================================================
    public HologramManager(){
        this.holoDataManager = new DataManager("holograms");
        Console.log("HologramManager:init", "Initialized.");
        this.loadHolograms();
    }

    //================================================================================
    // Hologram management
    //================================================================================
    public void createHologram(Hologram holo){
        holo.setId(getLastId()+1);
        setLastId(holo.getId());
        addHologram(holo);
        if(!holo.isNoDataHologram())
            getHoloDataManager().createDataFile(holo.getUuid(), generateData(holo.getId()));
        Bukkit.getOnlinePlayers().forEach(p->{
            if(p.getWorld().equals(holo.getWorld())){
                sendPacket(holo.getId(), p, HologramPacketType.CREATE, HologramPacketType.DISPLAY);
            }
        });
    }

    public boolean deleteHologram(int id){
        Hologram holo = getHologram(id);
        if(holo != null){
            Bukkit.getOnlinePlayers().forEach(p->{
                if(p.getWorld().equals(holo.getWorld())){
                    sendPacket(holo.getId(), p, HologramPacketType.DELETE);
                }
            });
            removeHologram(id);
            if(!holo.isNoDataHologram())
                getHoloDataManager().removeDataFile(holo.getUuid());
            return true;
        }
        return false;
    }

    public boolean updateHologram(int id, HologramUpdateType updateType, Object... val){
        Hologram holo = getHologram(id);
        if(holo != null){
            if(updateType == HologramUpdateType.UPDATE_LINE){
                String[] content = holo.getContent().split("\n", -1);
                int line = (int)val[0];
                if(line<=0 || line > content.length) return false;
                content[line-1] = (String)val[1];
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < content.length; i++) {
                    builder.append(content[i]);
                    if(i != content.length-1) builder.append("\n");
                }
                holo.setContent(builder.toString());
                Bukkit.getOnlinePlayers().forEach(p->{
                    if(p.getWorld().equals(holo.getWorld()))
                        sendPacket(holo.getId(), p, HologramPacketType.DISPLAY);
                });
                if(!holo.isNoDataHologram())
                    getHoloDataManager().recreateFile(holo.getUuid(), generateData(holo.getId()));
            }
            else if(updateType == HologramUpdateType.DELETE_LINE){
                String[] content = holo.getContent().split("\n", -1);
                int line = (int)val[0];
                if(line<=0 || line > content.length || content.length == 1) return false;
                content[line-1] = "";
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < content.length; i++) {
                    if(!content[i].equals("")) {
                        builder.append(content[i]);
                        if(line == content.length) {
                            if (i != content.length - 2) builder.append("\n");
                        } else{
                            if(i != content.length - 1)builder.append("\n");
                        }
                    }
                }
                holo.setContent(builder.toString());
                hideHologram(holo.getId());
                Hologram copy = holo.deepCopy();
                removeHologram(holo.getId());
                addHologram(copy);
                Bukkit.getOnlinePlayers().forEach(p-> {
                    if (p.getWorld().equals(holo.getWorld()))
                        sendPacket(copy.getId(), p, HologramPacketType.CREATE, HologramPacketType.DISPLAY);
                });
                if(!copy.isNoDataHologram())
                    getHoloDataManager().recreateFile(copy.getUuid(), generateData(copy.getId()));
            }
            else if(updateType == HologramUpdateType.INSERT_LINE){
                String[] content = holo.getContent().split("\n", -1);
                List<String> contentList = new ArrayList<String>(Arrays.asList(content));
                contentList.add((String)val[0]);
                holo.setContent(String.join("\n", contentList));
                hideHologram(holo.getId());
                Hologram copy = holo.deepCopy();
                removeHologram(holo.getId());
                addHologram(copy);
                Bukkit.getOnlinePlayers().forEach(p-> {
                    if (p.getWorld().equals(holo.getWorld()))
                        sendPacket(copy.getId(), p, HologramPacketType.CREATE, HologramPacketType.DISPLAY);
                });
                if(!copy.isNoDataHologram())
                    getHoloDataManager().recreateFile(copy.getUuid(), generateData(copy.getId()));
            }
            else if(updateType == HologramUpdateType.REPLACE_LINE){
                String[] content = holo.getContent().split("\n", -1);
                int lineFrom = (int)val[0];
                int lineTo = (int) val[1];
                if(lineFrom <= 0 || lineFrom > content.length || lineTo <= 0 || lineTo > content.length || content.length == 1) return false;
                List<String> contentList = new ArrayList<>(Arrays.asList(content));
                String valFrom = contentList.get(lineFrom-1);
                String valTo = contentList.get(lineTo-1);
                List<String> temp = new ArrayList<>();
                for (String s : contentList) {
                    if(s.equals(valFrom))
                        temp.add(valTo);
                    else if(s.equals(valTo))
                        temp.add(valFrom);
                    else
                        temp.add(s);
                }
                holo.setContent(String.join("\n", temp));
                Bukkit.getOnlinePlayers().forEach(p->{
                    if(p.getWorld().equals(holo.getWorld()))
                        sendPacket(holo.getId(), p, HologramPacketType.DISPLAY);
                });
                if(!holo.isNoDataHologram())
                    getHoloDataManager().recreateFile(holo.getUuid(), generateData(holo.getId()));
            }
            else if(updateType == HologramUpdateType.UPDATE_LOCATION){
                double x = (double)val[0];
                double y = (double)val[1];
                double z = (double)val[2];
                World world = (World)val[3];
                holo.setX(x); holo.setY(y); holo.setZ(z); holo.setWorld(world);
                hideHologram(holo.getId());
                Hologram copy = holo.deepCopy();
                removeHologram(holo.getId());
                addHologram(copy);
                Bukkit.getOnlinePlayers().forEach(p-> {
                    if (p.getWorld().equals(holo.getWorld()))
                        sendPacket(copy.getId(), p, HologramPacketType.CREATE, HologramPacketType.DISPLAY);
                });
                if(!copy.isNoDataHologram())
                    getHoloDataManager().recreateFile(copy.getUuid(), generateData(copy.getId()));
            }
        }
        return true;
    }

    public Hologram getHologram(int id){
        Hologram temp = null;
        for (Hologram holo : getHolograms()) {
            if(holo.getId() == id) temp = holo;
        }
        return temp;
    }
    public Hologram getHologram(UUID uuid){
        Hologram temp = null;
        for (Hologram hologram : getHolograms()) {
            if(hologram.getUuid().equals(uuid)) temp = hologram;
        }
        return temp;
    }

    //================================================================================
    // Hologram visibility tools
    //================================================================================
    public void showHolograms(Player p){
        getHolograms().forEach(holo->{
            if(p.getWorld().equals(holo.getWorld())){
                sendPacket(holo.getId(), p, HologramPacketType.CREATE, HologramPacketType.DISPLAY);
            }
        });
    }

    public void hideHolograms(Player p){
        getHolograms().forEach(holo->{
            if(p.getWorld().equals(holo.getWorld())){
                sendPacket(holo.getId(), p, HologramPacketType.DELETE);
            }
        });
    }

    public void hideHologram(int id){
        Hologram holo = getHologram(id);
        if(holo!=null){
            Bukkit.getOnlinePlayers().forEach(p->{
                if(p.getWorld().equals(holo.getWorld())){
                    sendPacket(holo.getId(), p, HologramPacketType.DELETE);
                }
            });
        }
    }

    //================================================================================
    // Packets
    //================================================================================
    public void sendPacket(int id, Player target, HologramPacketType... packetType){
        Hologram holo = getHologram(id);
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        List<PacketContainer> packetList = new ArrayList<>();
        if(holo!=null){
            for (HologramPacketType type : packetType) {
                if(type == HologramPacketType.CREATE)
                    packetList.addAll(holo.createPckt());
                if(type == HologramPacketType.DISPLAY)
                    packetList.addAll(holo.displayPckt(target));
                if(type == HologramPacketType.DELETE)
                    packetList.add(holo.deletePckt());
            }
            for (PacketContainer packetContainer : packetList) {
                try {
                    manager.sendServerPacket(target, packetContainer);
                } catch (InvocationTargetException e) {
                    Console.error("HologramManager:sendPacket()", e.getMessage());
                }
            }
        }
    }

    //================================================================================
    // Utils
    //================================================================================
    public void enableUpdateScheduler(Main main){
        getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                getHolograms().forEach(holo->{
                    Bukkit.getOnlinePlayers().forEach(p->{
                        if(p.getWorld().equals(holo.getWorld())){
                            sendPacket(holo.getId(), p, HologramPacketType.DISPLAY);
                        }
                    });
                });
            }
        }, 0L, 10L);
    }

    private void loadHolograms(){
        int t = 0;
        for (int i = 0; i < getHoloDataManager().getFiles().length; i++) {
            JSONObject data = getHoloDataManager().getFileContent(UUID.fromString(getHoloDataManager().getFiles()[i].getName().replaceAll(".json", "")));
            UUID uuid = UUID.fromString(getHoloDataManager().getFiles()[i].getName().replaceAll(".json", ""));
            int id = Integer.parseInt((String) getHoloDataManager().getFromJSONObject(data, "id"));
            double x = Double.parseDouble((String) getHoloDataManager().getFromJSONObject(data,"x"));
            double y = Double.parseDouble((String) getHoloDataManager().getFromJSONObject(data,"y"));
            double z = Double.parseDouble((String) getHoloDataManager().getFromJSONObject(data,"z"));
            World world = Bukkit.getWorld(UUID.fromString((String) getHoloDataManager().getFromJSONObject(data, "world")));
            String content = (String) getHoloDataManager().getFromJSONObject(data, "content");
            Hologram holo = new Hologram(id, uuid, content, x, y, z, world, true);
            setLastId(id);
            addHologram(holo);
            t++;
        }
        Console.log("HologramManager:loadHolograms()", "Loaded "+t+" holograms.");
    }

    private String generateData(int id){
        Hologram holo = getHologram(id);
        StringBuilder data = new StringBuilder();
        if(holo != null){
            data.append("{\n")
                    .append("\t\"id\":\"").append(holo.getId()).append("\",\n")
                    .append("\t\"content\":\"").append(StringEscapeUtils.escapeJava(holo.getContent())).append("\",\n")
                    .append("\t\"x\":\"").append(holo.getX()).append("\",\n")
                    .append("\t\"y\":\"").append(holo.getY()).append("\",\n")
                    .append("\t\"z\":\"").append(holo.getZ()).append("\",\n")
                    .append("\t\"world\":\"").append(holo.getWorld().getUID().toString()).append("\"\n")
                    .append("}");
        }
        return data.toString();
    }

    //================================================================================
    // Access Methods
    //================================================================================
    public ArrayList<Hologram> getHolograms() {
        return holograms;
    }

    private void addHologram(Hologram hologram){
        this.holograms.add(hologram);
    }

    private void removeHologram(int id){
        getHolograms().removeIf(holo->holo.getId()==id);
    }

    public BukkitScheduler getScheduler() {
        return scheduler;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public DataManager getHoloDataManager() {
        return holoDataManager;
    }
}
