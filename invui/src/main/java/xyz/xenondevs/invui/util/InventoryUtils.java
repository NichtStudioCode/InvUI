package xyz.xenondevs.invui.util;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public final class InventoryUtils {

    private InventoryUtils() {}
    
    /**
     * Spawns an item entity as if the player dropped it, also firing {@link PlayerDropItemEvent}.
     *
     * @param player    The player
     * @param itemStack The item stack
     * @return Whether the item was dropped. False if the event was cancelled.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean dropItemLikePlayer(Player player, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return true;
        
        Location location = player.getLocation();
        location.add(0, 1.5, 0); // not the eye location
        
        Item item = location.getWorld().createEntity(location, Item.class);
        item.setItemStack(itemStack.clone());
        item.setPickupDelay(40);
        item.setVelocity(location.getDirection().multiply(0.35));
        
        if (new PlayerDropItemEvent(player, item).callEvent()) {
            location.getWorld().addEntity(item);
            return true;
        }
        
        return false;
    }
    
    /**
     * Adds an item stack to the player's inventory or drops it if it doesn't fit.
     * Also fires {@link PlayerDropItemEvent}, effectively deleting the item if the event is cancelled.
     *
     * @param player    The player
     * @param itemStack The item stack
     */
    public static void addToInventoryOrDrop(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack.clone())
            .entrySet()
            .stream()
            .findFirst()
            .ifPresent(entry -> dropItemLikePlayer(player, entry.getValue()));
    }
}
