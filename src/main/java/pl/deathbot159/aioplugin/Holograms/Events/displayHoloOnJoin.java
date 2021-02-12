package pl.deathbot159.aioplugin.Holograms.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.deathbot159.aioplugin.Holograms.Types.HologramPacketType;

import static pl.deathbot159.aioplugin.Main.getHologramManager;

public class displayHoloOnJoin implements Listener {
    //================================================================================
    // Event
    //================================================================================
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent ev){
        getHologramManager().getHolograms().forEach(hologram -> {
            Player p = ev.getPlayer();
            if(p.getWorld().equals(hologram.getWorld()))
                getHologramManager().sendPacket(hologram.getId(), p, HologramPacketType.CREATE, HologramPacketType.DISPLAY);
        });
    }
}
