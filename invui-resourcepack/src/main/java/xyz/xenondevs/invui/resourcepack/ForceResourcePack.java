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
    
    private boolean forced = true;
    private String forceBypassPermission;
    private String promptBypassPermission;
    private ComponentWrapper prompt;
    
    private String resourcePackUrl;
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
     * @throws IOException If the connection was not successful
     */
    public void setResourcePack(@Nullable String resourcePackUrl) throws IOException {
        setResourcePack(resourcePackUrl, true);
    }
    
    /**
     * Sets the URL String for the custom ResourcePack every {@link Player} is required to download.
     * Can be set to null to stop forcing the Resource Pack.
     *
     * @param resourcePackUrl     The ResourcePack URL String
     * @param sendToOnlinePlayers If the resource pack should also be sent to all currently online players
     * @throws IOException If the connection was not successful
     */
    public void setResourcePack(@Nullable String resourcePackUrl, boolean sendToOnlinePlayers) throws IOException {
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
     * Gets the resource pack prompt message. (Since 1.17)
     *
     * @return The resource pack prompt message.
     */
    public ComponentWrapper getPrompt() {
        return prompt;
    }
    
    /**
     * Sets the resource pack prompt message. (Since 1.17)
     *
     * @param prompt The resource pack prompt message.
     */
    public void setPrompt(ComponentWrapper prompt) {
        this.prompt = prompt;
    }
    
    /**
     * Sets the resource pack prompt message. (Since 1.17)
     *
     * @param prompt The resource pack prompt message.
     */
    public void setPrompt(BaseComponent[] prompt) {
        this.prompt = new BungeeComponentWrapper(prompt);
    }
    
    /**
     * Gets whether {@link Player Players} are be forced to download the resource pack.
     *
     * @return Whether the resource pack is forced.
     */
    public boolean isForced() {
        return forced;
    }
    
    /**
     * Sets whether {@link Player Players} should be forced to download the resource pack.
     *
     * @param forced Whether the resource pack should be forced.
     */
    public void setForced(boolean forced) {
        this.forced = forced;
    }
    
    /**
     * Gets the bypass permission a {@link Player} needs to have to not be forced to download the ResourcePack.
     * <p>
     * {@link Player Players} with this permission will still get the resource pack prompt, but they can choose to not
     * download the resource pack.
     *
     * @return The bypass permission
     */
    public String getForceBypassPermission() {
        return forceBypassPermission;
    }
    
    /**
     * Sets the bypass permission a {@link Player} needs to have to not be forced to download the ResourcePack.
     * <p>
     * {@link Player Players} with this permission will still get the resource pack prompt, but they can choose to not
     * download the resource pack.
     *
     * @param forceBypassPermission The bypass permission
     */
    public void setForceBypassPermission(String forceBypassPermission) {
        this.forceBypassPermission = forceBypassPermission;
    }
    
    /**
     * Gets the bypass permission which exempts {@link Player Players} from receiving the resource pack prompt.
     *
     * @return The bypass permission.
     */
    public String getPromptBypassPermission() {
        return promptBypassPermission;
    }
    
    /**
     * Sets the bypass permission which exempts {@link Player Players} from receiving the resource pack prompt.
     *
     * @param promptBypassPermission The bypass permission.
     */
    public void setPromptBypassPermission(String promptBypassPermission) {
        this.promptBypassPermission = promptBypassPermission;
    }
    
    public void sendResourcePack(Player player) {
        // player is exempted from the resource pack prompt
        if (promptBypassPermission != null && player.hasPermission(promptBypassPermission))
            return;
        
        boolean forced = this.forced && (forceBypassPermission == null || !player.hasPermission(forceBypassPermission));
        
        if (VersionUtils.isServerHigherOrEqual("1.17.0")) {
            InventoryAccess.getPlayerUtils().sendResourcePack(player, resourcePackUrl, hash, prompt, forced);
        } else {
            player.setResourcePack(resourcePackUrl);
            if (forced) {
                tasks.put(player, Bukkit.getScheduler().runTaskLater(InvUI.getInstance().getPlugin(),
                    () -> kickPlayer(player), 20 * 5));
            }
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