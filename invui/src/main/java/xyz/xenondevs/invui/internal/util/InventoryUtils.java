package xyz.xenondevs.invui.internal.util;

import net.minecraft.world.inventory.MenuType;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.OperationCategory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.util.ItemUtils;

public class InventoryUtils {
    
    /**
     * Gets the amount of slots that a menu type has.
     *
     * @param type The menu type
     * @return The amount of slots
     */
    public static int getSizeOf(MenuType<?> type) {
        if (type == MenuType.GENERIC_9x1)
            return 9;
        if (type == MenuType.GENERIC_9x2)
            return 18;
        if (type == MenuType.GENERIC_9x3)
            return 27;
        if (type == MenuType.GENERIC_9x4)
            return 36;
        if (type == MenuType.GENERIC_9x5)
            return 45;
        if (type == MenuType.GENERIC_9x6)
            return 54;
        if (type == MenuType.GENERIC_3x3)
            return 9;
        if (type == MenuType.CRAFTER_3x3)
            return 10;
        if (type == MenuType.ANVIL)
            return 3;
        if (type == MenuType.BEACON)
            return 1;
        if (type == MenuType.BLAST_FURNACE)
            return 3;
        if (type == MenuType.BREWING_STAND)
            return 5;
        if (type == MenuType.CRAFTING)
            return 10;
        if (type == MenuType.ENCHANTMENT)
            return 2;
        if (type == MenuType.FURNACE)
            return 3;
        if (type == MenuType.GRINDSTONE)
            return 3;
        if (type == MenuType.HOPPER)
            return 5;
        if (type == MenuType.LECTERN)
            return 1;
        if (type == MenuType.LOOM)
            return 4;
        if (type == MenuType.MERCHANT)
            return 3;
        if (type == MenuType.SHULKER_BOX)
            return 27;
        if (type == MenuType.SMITHING)
            return 4;
        if (type == MenuType.SMOKER)
            return 3;
        if (type == MenuType.CARTOGRAPHY_TABLE)
            return 3;
        if (type == MenuType.STONECUTTER)
            return 2;
        throw new UnsupportedOperationException("Unsupported menu type: " + type);
    }
    
    /**
     * Gets the amount of data slots that a menu type has.
     *
     * @param type The menu type
     * @return The amount of data slots
     */
    public static int getDataSlotCountOf(MenuType<?> type) {
        if (type == MenuType.CRAFTER_3x3)
            return 9;
        if (type == MenuType.ANVIL)
            return 1;
        if (type == MenuType.BEACON)
            return 3;
        if (type == MenuType.FURNACE || type == MenuType.BLAST_FURNACE || type == MenuType.SMOKER)
            return 4;
        if (type == MenuType.BREWING_STAND)
            return 2;
        if (type == MenuType.ENCHANTMENT)
            return 10;
        if (type == MenuType.LECTERN)
            return 1;
        if (type == MenuType.LOOM)
            return 1;
        if (type == MenuType.STONECUTTER)
            return 1;
        return 0;
    }
    
    /**
     * Finds a generic menu type that matches the specified width and height.
     *
     * @param width  The width of the menu
     * @param height The height of the menu
     * @return The matching generic menu type
     * @throws IllegalArgumentException if there is no matching generic menu typ e
     */
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public static MenuType<?> getMatchingGenericMenuType(int width, int height) {
        return switch (width) {
            case 3 -> switch (height) {
                case 3 -> MenuType.GENERIC_3x3;
                default -> throw new IllegalArgumentException("Illegal height: " + height + " for width 3");
            };
            case 5 -> switch (height) {
                case 1 -> MenuType.HOPPER;
                default -> throw new IllegalArgumentException("Illegal height: " + height + " for width 5");
            };
            case 9 -> switch (height) {
                case 1 -> MenuType.GENERIC_9x1;
                case 2 -> MenuType.GENERIC_9x2;
                case 3 -> MenuType.GENERIC_9x3;
                case 4 -> MenuType.GENERIC_9x4;
                case 5 -> MenuType.GENERIC_9x5;
                case 6 -> MenuType.GENERIC_9x6;
                default -> throw new IllegalArgumentException("Illegal height: " + height + " for width 9");
            };
            default -> throw new IllegalArgumentException("Illegal width: " + width);
        };
    }
    
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
     * @param player The player
     * @param itemStack The item stack
     */
    public static void addToInventoryOrDrop(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack.clone())
            .entrySet()
            .stream()
            .findFirst()
            .ifPresent(entry -> dropItemLikePlayer(player, entry.getValue()));
    }
    
    /**
     * Creates a new gui that references the player's inventory and handles interactions in the same way
     * that the player inventory does.
     *
     * @param player The player whose inventory to reference
     * @return The new gui that references the player's inventory
     */
    public static Gui createPlayerReferencingInventoryGui(Player player) {
        Inventory inv = ReferencingInventory.fromPlayerStorageContents(player.getInventory());
        inv.reverseIterationOrder(OperationCategory.ADD); // shift-clicking moves to bottom right
        inv.setGuiPriority(OperationCategory.ADD, Integer.MAX_VALUE); // shift-click always moves between upper and lower inv
        inv.setGuiPriority(OperationCategory.COLLECT, Integer.MIN_VALUE); // double-click collects from lower inv last
        return Gui.of(9, 4, inv);
    }
    
}
