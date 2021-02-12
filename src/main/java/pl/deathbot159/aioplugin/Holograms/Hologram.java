package pl.deathbot159.aioplugin.Holograms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.deathbot159.aioplugin.PluginUtils.IntegerManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static pl.deathbot159.aioplugin.Main.getPredefinedMessageManager;

public class Hologram {
    //================================================================================
    // Variables
    //================================================================================
    private int id;
    private UUID uuid;
    private List<Integer> entityGroup = new ArrayList<>();
    private String content;
    private double x,y,z;
    private World world;
    private boolean noData = false;

    private List<String> replacements = Arrays.asList("\\{displayName\\}",
            "\\{onlinePlayers\\}",
            "\\{maxPlayers\\}",
            "\\{clock-HH:mm:ss\\}",
            "\\{clock-HH:mm\\}",
            "\\{date\\}" );

    //================================================================================
    // Constructor
    //================================================================================
    public Hologram(int id, UUID uuid, String content, double x, double y, double z, World world, boolean noData){
        this.noData = noData;
        setId(id); setUuid(uuid);
        setContent(content.replaceAll(StringEscapeUtils.escapeJava("\\n"), "\n"));
        setX(x); setY(y); setZ(z); setWorld(world);
        for (String s : getContent().split("\n", -1)) {
            this.entityGroup.add(IntegerManager.random());
        }
    }
    public Hologram(UUID uuid, String content, double x, double y, double z, World world, boolean noData){
        this.noData = noData;
        setUuid(uuid);
        setContent(content.replaceAll(StringEscapeUtils.escapeJava("\\n"), "\n"));
        setX(x); setY(y); setZ(z); setWorld(world);
        for (String s : getContent().split("\n", -1)) {
            this.entityGroup.add(IntegerManager.random());
        }
    }

    //================================================================================
    // Packets
    //================================================================================
    public List<PacketContainer> createPckt(){
        List<PacketContainer> packetList = new ArrayList<>();
        for (int i = 0; i < this.entityGroup.size(); i++) {
            int entityId = this.entityGroup.get(i);
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            packet.getIntegers().write(0, entityId);
            packet.getUUIDs().write(0, UUID.randomUUID());
            packet.getIntegers().write(1, 1);
            packet.getDoubles().write(0, this.getX());
            packet.getDoubles().write(1, this.getY() - (i * 0.26));
            packet.getDoubles().write(2, this.getZ());
            packetList.add(packet);
        }
        return packetList;
    }

    public List<PacketContainer> displayPckt(Player p){
        List<PacketContainer> packetList = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();
        String[] splName = getContent().split("\n", -1);
        String name = "";
        if(p!=null) name = p.getDisplayName();
        for (int i = 0; i < getEntityGroup().size(); i++) {
            int entityId = getEntityGroup().get(i);

            PacketContainer pckt = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
            pckt.getIntegers().write(0, entityId);
            WrappedDataWatcher dW = new WrappedDataWatcher();
            dW.setObject(0, WrappedDataWatcher.Registry
                    .get(Byte.class), (byte) (0x20));
            Optional<?> opt = Optional
                    .of(WrappedChatComponent
                            .fromChatMessage(replaceVariables(splName[i], name))[0].getHandle());
            dW.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt);

            dW.setObject(3, WrappedDataWatcher.Registry
                    .get(Boolean.class), (Object) true);
            pckt.getWatchableCollectionModifier().write(0, dW.getWatchableObjects());
            packetList.add(pckt);
        }
        return packetList;
    }

    public PacketContainer deletePckt(){
        PacketContainer pckt = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        int[] entities = new int[this.entityGroup.size()];
        for (int i = 0; i < this.entityGroup.size(); i++) {
            entities[i] = this.entityGroup.get(i);
        }
        pckt.getIntegerArrays().write(0, entities);
        return pckt;
    }

    //================================================================================
    // Utils
    //================================================================================
    private String replaceVariables(String s, Object... values){
        for (String key : getPredefinedMessageManager().getPredefinedMessages().keySet()) {
            s = s.replaceAll("\\{"+key+"\\}", getPredefinedMessageManager().getPredefinedMessages().get(key));
        }
        s = s.replaceAll("&", "§")
                .replaceAll(getReplacements().get(0), (String)values[0])
                .replaceAll(getReplacements().get(1), Bukkit.getServer().getOnlinePlayers().size()+"")
                .replaceAll(getReplacements().get(2), Bukkit.getMaxPlayers()+"")
                .replaceAll(getReplacements().get(3), DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()))
                .replaceAll(getReplacements().get(4), DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now()))
                .replaceAll(getReplacements().get(5), DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDateTime.now()));
        return s;
    }
    public Hologram deepCopy(){
        return new Hologram(getId(), getUuid(), getContent(), getX(), getY(), getZ(), getWorld(), isNoDataHologram());
    }

    //================================================================================
    // Access methods
    //================================================================================
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getEntityGroup() {
        return entityGroup;
    }

    public List<String> getReplacements() {
        return replacements;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public double getZ() {
        return z;
    }

    public double getX() {
        return x;
    }

    public String getContent() {
        return content;
    }

    public boolean isNoDataHologram(){
        return noData;
    }
}
