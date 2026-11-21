package xyz.xenondevs.invui.internal.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.InventorySlot;
import xyz.xenondevs.invui.inventory.OperationCategory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.util.ItemUtils;

public final class InventoryUtils2 {
    
    private InventoryUtils2() {}
    
    /**
     * Returns whether the target inventory contains the backing slot of the source slot.
     *
     * @param target     The target inventory
     * @param source     The source inventory
     * @param sourceSlot The source slot
     * @return Whether the target inventory contains the source backing slot
     */
    public static boolean containsBackingSlot(Inventory target, Inventory source, int sourceSlot) {
        InventorySlot sourceBacking = source.getBackingSlot(sourceSlot);
        for (int i = 0; i < target.getSize(); i++) {
            if (isSameBackingSlot(sourceBacking, target.getBackingSlot(i)))
                return true;
        }
        return false;
    }
    
    private static boolean isSameBackingSlot(InventorySlot first, InventorySlot second) {
        if (first.inventory() == second.inventory() && first.slot() == second.slot())
            return true;
        
        if (first.inventory() instanceof ReferencingInventory refFirst
            && second.inventory() instanceof ReferencingInventory refSecond) {
            return refFirst.getReferencedInventory() == refSecond.getReferencedInventory()
                   && toReferencedSlot(refFirst, first.slot()) == toReferencedSlot(refSecond, second.slot());
        }
        
        return false;
    }
    
    /**
     * Converts a slot of the given {@link ReferencingInventory} to the corresponding slot of the referenced
     * Bukkit inventory.
     *
     * @param inventory The {@link ReferencingInventory}
     * @param slot      The slot in the {@link ReferencingInventory}
     * @return The slot in the referenced Bukkit inventory
     */
    private static int toReferencedSlot(ReferencingInventory inventory, int slot) {
        // mirrors ReferencingInventory.PlayerStorageContents#convertSlot, which moves the hotbar to the end
        return inventory instanceof ReferencingInventory.PlayerStorageContents ? (slot + 9) % 36 : slot;
    }
    
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
    
    /**
     * Simulates an item drag operation.
     *
     * @param clickType The click type of the drag
     * @param view      The inventory view
     * @param slots     The slots involved in the drag
     * @param cursor    The current cursor item stack. Will be modified to reflect the remaining items after the drag.
     * @return A map of slot indices to the resulting item stacks in those slots after the drag
     */
    public static Int2ObjectMap<ItemStack> simulateItemDrag(ClickType clickType, InventoryView view, IntList slots, ItemStack cursor) {
        if (ItemUtils.isEmpty(cursor) || slots.isEmpty())
            return new Int2ObjectOpenHashMap<>();
        
        var result = new Int2ObjectOpenHashMap<ItemStack>();
        if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
            int amountPerSlot = clickType == ClickType.LEFT ? cursor.getAmount() / slots.size() : 1;
            for (int slot : slots) {
                if (cursor.getAmount() <= 0)
                    break;
                
                var itemThere = view.getItem(slot);
                if (itemThere == null || (itemThere.isSimilar(cursor) && itemThere.getAmount() < itemThere.getMaxStackSize())) {
                    var currentAmount = ItemUtils.getAmount(itemThere);
                    var newAmount = Math.min(cursor.getMaxStackSize(), currentAmount + amountPerSlot);
                    var toPut = ItemUtils.cloneWithCount(cursor, newAmount);
                    assert toPut != null;
                    
                    result.put(slot, toPut);
                    cursor.setAmount(cursor.getAmount() - (newAmount - currentAmount));
                }
            }
        } else if (clickType == ClickType.MIDDLE) {
            for (int slot : slots) {
                var itemThere = view.getItem(slot);
                if (itemThere == null || (itemThere.isSimilar(cursor) && itemThere.getAmount() < itemThere.getMaxStackSize())) {
                    var toPut = ItemUtils.cloneWithCount(cursor, cursor.getMaxStackSize());
                    assert toPut != null;
                    
                    result.put(slot, toPut);
                }
            }
            cursor.setAmount(0);
        }
        
        return result;
    }
    
}
