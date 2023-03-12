package xyz.xenondevs.invui.resourcepack;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.component.BungeeComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.util.ReflectionRegistry;
import xyz.xenondevs.inventoryaccess.util.VersionUtils;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.resourcepack.auth.AuthenticationServiceManager;
import xyz.xenondevs.invui.util.DataUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static org.bukkit.event.player.PlayerResourcePackStatusEvent.Status.DECLINED;

/**
 * Forces {@link Player}s to use a custom ResourcePack and kicks them if they don't accept it.
 */
public class ForceResourcePack implements Listener {
    
    private static final String RP_VERSION = "v0.8";
    private static final ForceResourcePack INSTANCE = new ForceResourcePack();
    
    public static final String RESOURCE_PACK_URL =
        "https://github.com/NichtStudioCode/InvUIRP/releases/download/"
            + RP_VERSION + (ReflectionRegistry.VERSION > 14 ? "" : "-legacy") + "/InvUIRP.zip";
    
    private final HashMap<Player, BukkitTask> tasks = new HashMap<>();
    
    private String resourcePackUrl;
    private ComponentWrapper prompt;
    private byte[] hash;
    
    private ForceResourcePack() {
        Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
        
        AuthenticationServiceManager.getInstance().setLoginHandler(player -> {
            if (resourcePackUrl != null) sendResourcePack(player);
        });
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
     * @throws IOException If the connection was not successful
     */
    public void setResourcePack(@Nullable String resourcePackUrl, @Nullable ComponentWrapper prompt) throws IOException {
        setResourcePack(resourcePackUrl, prompt, true);
    }
    
    /**
     * Sets the URL String for the custom ResourcePack every {@link Player} is required to download.
     * Can be set to null to stop forcing the Resource Pack.
     *
     * @param resourcePackUrl The ResourcePack URL String
     * @param prompt          The prompt to be displayed (since 1.17)
     * @throws IOException If the connection was not successful
     */
    public void setResourcePack(@Nullable String resourcePackUrl, @Nullable BaseComponent[] prompt) throws IOException {
        setResourcePack(resourcePackUrl, new BungeeComponentWrapper(prompt), true);
    }
    
    /**
     * Sets the URL String for the custom ResourcePack every {@link Player} is required to download.
     * Can be set to null to stop forcing the Resource Pack.
     *
     * @param resourcePackUrl     The ResourcePack URL String
     * @param prompt              The prompt to be displayed (since 1.17)
     * @param sendToOnlinePlayers If the resource pack should also be sent to all currently online players
     * @throws IOException If the connection was not successful
     */
    public void setResourcePack(@Nullable String resourcePackUrl, @Nullable ComponentWrapper prompt, boolean sendToOnlinePlayers) throws IOException {
        this.prompt = prompt;
        
        if (resourcePackUrl != null) {
            URL url = new URL(resourcePackUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response >= 200 && response < 300) {
                hash = DataUtils.createSha1Hash(url.openStream());
                this.resourcePackUrl = resourcePackUrl;
            } else throw new IOException("Service returned response code " + response);
            
            if (sendToOnlinePlayers) Bukkit.getOnlinePlayers().forEach(this::sendResourcePack);
        } else {
            this.resourcePackUrl = null;
        }
    }
    
    /**
     * Sets the URL String for the custom ResourcePack every {@link Player} is required to download.
     * Can be set to null to stop forcing the Resource Pack.
     *
     * @param resourcePackUrl     The ResourcePack URL String
     * @param prompt              The prompt to be displayed (since 1.17)
     * @param sendToOnlinePlayers If the resource pack should also be sent to all currently online players
     * @throws IOException If the connection was not successful
     */
    public void setResourcePack(@Nullable String resourcePackUrl, @Nullable BaseComponent[] prompt, boolean sendToOnlinePlayers) throws IOException {
        setResourcePack(resourcePackUrl, new BungeeComponentWrapper(prompt), sendToOnlinePlayers);
    }
    
    public void sendResourcePack(Player player) {
        if (VersionUtils.isServerHigherOrEqual("1.17.0")) {
            InventoryAccess.getPlayerUtils().sendResourcePack(player, resourcePackUrl, hash, prompt, true);
        } else {
            player.setResourcePack(resourcePackUrl);
            tasks.put(player, Bukkit.getScheduler().runTaskLater(InvUI.getInstance().getPlugin(),
                () -> kickPlayer(player), 20 * 5));
        }
    }
    
    @EventHandler
    private void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
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