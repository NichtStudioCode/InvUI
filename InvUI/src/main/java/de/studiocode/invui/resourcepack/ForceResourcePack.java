package de.studiocode.invui.resourcepack;

import de.studiocode.inventoryaccess.util.ReflectionRegistry;
import de.studiocode.inventoryaccess.util.VersionUtils;
import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.InvUI;
import de.studiocode.invui.util.DataUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.bukkit.event.player.PlayerResourcePackStatusEvent.Status.DECLINED;

/**
 * Forces {@link Player}s to use a custom ResourcePack and kicks them if they don't accept it.
 */
public class ForceResourcePack implements Listener {
    
    private static final String RP_VERSION = "v0.8";
    private static final ForceResourcePack INSTANCE = new ForceResourcePack();
    
    /**
     * A resource pack with all the {@link Icon}s
     */
    public static final String RESOURCE_PACK_URL =
        "https://github.com/NichtStudioCode/InvUIRP/releases/download/"
            + RP_VERSION + (ReflectionRegistry.VERSION > 14 ? "" : "-legacy") + "/InvUIRP.zip";
    
    private final HashMap<Player, BukkitTask> tasks = new HashMap<>();
    
    private String resourcePackUrl;
    private BaseComponent[] prompt;
    private byte[] hash;
    
    private ForceResourcePack() {
        Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
    }
    
    public static ForceResourcePack getInstance() {
        return INSTANCE;
    }
    
    public String getResourcePackUrl() {
        return resourcePackUrl;
    }
    
    /**
     * Sets the URL String for the custom ResourcePack every {@link Player} is required to download.
     * Can be set to null to stop forcing the Resource Pack.
     *
     * @param resourcePackUrl The ResourcePack URL String
     * @param prompt          The prompt to be displayed (since 1.17)
     */
    public void setResourcePack(@Nullable String resourcePackUrl, @Nullable BaseComponent[] prompt) {
        this.resourcePackUrl = resourcePackUrl;
        this.prompt = prompt;
        
        if (resourcePackUrl != null) {
            try {
                URL url = new URL(resourcePackUrl);
                hash = DataUtils.createSha1Hash(url.openStream());
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            
            Bukkit.getOnlinePlayers().forEach(this::sendResourcePack);
        }
    }
    
    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        if (resourcePackUrl != null) sendResourcePack(event.getPlayer());
    }
    
    private void sendResourcePack(Player player) {
        if (VersionUtils.isServerHigherOrEqual("1.17.0")) {
            InventoryAccess.getPlayerUtils().sendResourcePack(player, resourcePackUrl, hash, prompt, true);
        } else {
            player.setResourcePack(resourcePackUrl);
            tasks.put(player, Bukkit.getScheduler().runTaskLater(InvUI.getInstance().getPlugin(),
                () -> kickPlayer(player), 20 * 5));
        }
    }
    
    @EventHandler
    public void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (resourcePackUrl != null) {
            Player player = event.getPlayer();
            if (tasks.containsKey(player)) {
                if (event.getStatus() == DECLINED) kickPlayer(player);
                else tasks.get(player).cancel();
                tasks.remove(player);
            }
        }
    }
    
    private void kickPlayer(Player player) {
        player.kickPlayer("§cPlease accept the custom resource pack");
    }
    
}
