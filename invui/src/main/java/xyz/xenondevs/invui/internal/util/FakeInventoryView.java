package xyz.xenondevs.invui.internal.util;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FakeInventoryView implements InventoryView {
    
    private final Player player;
    private final Inventory top;
    
    public FakeInventoryView(Player player, Inventory top) {
        this.player = player;
        this.top = top;
    }
    
    @Override
    public Inventory getTopInventory() {
        return top;
    }
    
    @Override
    public Inventory getBottomInventory() {
        return player.getInventory();
    }
    
    @Override
    public HumanEntity getPlayer() {
        return player;
    }
    
    @Override
    public InventoryType getType() {
        return InventoryType.CHEST;
    }
    
    @Override
    public void setItem(int slot, @Nullable ItemStack item) {
        var inv = getInventory(slot);
        if (inv != null) {
            int invSlot = convertSlot(slot);
            inv.setItem(invSlot, item);
        }
    }
    
    @Override
    public @Nullable ItemStack getItem(int slot) {
        var inv = getInventory(slot);
        if (inv != null) {
            int invSlot = convertSlot(slot);
            return inv.getItem(invSlot);
        }
        return null;
    }
    
    @Override
    public void setCursor(@Nullable ItemStack item) {
        player.setItemOnCursor(item);
    }
    
    @Override
    public ItemStack getCursor() {
        return player.getItemOnCursor();
    }
    
    @Override
    public @Nullable Inventory getInventory(int rawSlot) {
        if (rawSlot == InventoryView.OUTSIDE)
            return null;
        if (rawSlot < top.getSize())
            return top;
        rawSlot -= top.getSize();
        return (rawSlot < 9 * 4) ? player.getInventory() : null;
    }
    
    @Override
    public int convertSlot(int rawSlot) {
        if (rawSlot < top.getSize())
            return rawSlot;
        rawSlot -= top.getSize();
        return rawSlot >= 27 ? rawSlot - 27 : rawSlot + 9;
    }
    
    @Override
    public InventoryType.SlotType getSlotType(int slot) {
        if (slot - getTopInventory().getSize() > 26)
            return InventoryType.SlotType.QUICKBAR;
        return InventoryType.SlotType.CONTAINER;
    }
    
    @Override
    public void open() {
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public int countSlots() {
        return getTopInventory().getSize() + getBottomInventory().getSize();
    }
    
    @SuppressWarnings("removal")
    @Override
    public boolean setProperty(Property prop, int value) {
        return false;
    }
    
    @Override
    public String getTitle() {
        return "";
    }
    
    @Override
    public String getOriginalTitle() {
        return "";
    }
    
    @Override
    public void setTitle(String title) {
    }
    
    @Override
    public @Nullable MenuType getMenuType() {
        // if possible, uses a generic menu type that matches the inventory size
        return switch (top.getSize()) {
            case 9 -> MenuType.GENERIC_9X1;
            case 18 -> MenuType.GENERIC_9X2;
            case 27 -> MenuType.GENERIC_9X3;
            case 36 -> MenuType.GENERIC_9X4;
            case 45 -> MenuType.GENERIC_9X5;
            case 54 -> MenuType.GENERIC_9X6;
            default -> null;
        };
    }
    
}
