package de.studiocode.invgui.resourcepack;

import de.studiocode.invgui.InvGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

import static org.bukkit.event.player.PlayerResourcePackStatusEvent.Status.DECLINED;

public class ForceResourcePack implements Listener {
    
    private static final ForceResourcePack INSTANCE = new ForceResourcePack();
    
    private final HashMap<Player, BukkitTask> tasks = new HashMap<>();
    
    private String resourcePackUrl = "https://github.com/NichtStudioCode/InvGuiRP/releases/download/v0.2/InvGuiRP.zip";
    private boolean activated;
    
    private ForceResourcePack() {
        Bukkit.getPluginManager().registerEvents(this, InvGui.getInstance().getPlugin());
        Bukkit.getOnlinePlayers().forEach(this::sendResourcePack);
    }
    
    public static ForceResourcePack getInstance() {
        return INSTANCE;
    }
    
    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        if (activated) sendResourcePack(event.getPlayer());
    }
    
    @EventHandler
    public void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (activated) {
            Player player = event.getPlayer();
            if (tasks.containsKey(player)) {
                if (event.getStatus() == DECLINED) kickPlayer(player);
                else tasks.get(player).cancel();
                tasks.remove(player);
            }
        }
    }
    
    private void sendResourcePack(Player player) {
        player.setResourcePack(resourcePackUrl);
        tasks.put(player, Bukkit.getScheduler().runTaskLater(InvGui.getInstance().getPlugin(),
            () -> kickPlayer(player), 20 * 5));
    }
    
    private void kickPlayer(Player player) {
        player.kickPlayer("§cPlease accept the custom resource pack");
    }
    
    public String getResourcePackUrl() {
        return resourcePackUrl;
    }
    
    public void setResourcePackUrl(String resourcePackUrl) {
        this.resourcePackUrl = resourcePackUrl;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    
}
