package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.internal.util.PlayerUtils;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
sealed abstract class AbstractDoubleWindow 
    extends AbstractWindow
    permits AbstractMergedWindow, AbstractSplitWindow 
{
    
    protected Inventory upperInventory;
    private final Inventory playerInventory;
    private final @Nullable ItemStack[] playerItems = new ItemStack[36];
    
    AbstractDoubleWindow(Player player, Supplier<Component> title, int size, Inventory upperInventory, boolean closeable) {
        super(player, title, size, closeable);
        this.upperInventory = upperInventory;
        this.playerInventory = player.getInventory();
    }
    
    @Override
    protected void initItems() {
        // store and clear player inventory
        Inventory inventory = getViewer().getInventory();
        for (int i = 0; i < 36; i++) {
            playerItems[i] = inventory.getItem(i);
            inventory.setItem(i, null);
        }
        
        super.initItems();
    }
    
    @Override
    public @Nullable ItemStack @Nullable [] getPlayerItems() {
        if (isOpen())
            return playerItems;
        return null;
    }
    
    private void restorePlayerInventory() {
        Inventory inventory = getViewer().getInventory();
        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, playerItems[i]);
        }
    }
    
    @Override
    protected void setInvItem(int slot, @Nullable ItemStack itemStack) {
        if (slot >= upperInventory.getSize()) {
            if (isOpen()) {
                int invSlot = SlotUtils.translateGuiToPlayerInv(slot - upperInventory.getSize());
                setPlayerInvItem(invSlot, itemStack);
            }
        } else setUpperInvItem(slot, itemStack);
    }
    
    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        upperInventory.setItem(slot, itemStack);
    }
    
    protected void setPlayerInvItem(int slot, ItemStack itemStack) {
        playerInventory.setItem(slot, itemStack);
    }
    
    @Override
    public void handleViewerDeath(PlayerDeathEvent event) {
        if (isOpen()) {
            List<ItemStack> drops = event.getDrops();
            if (!event.getKeepInventory()) {
                drops.clear();
                Arrays.stream(playerItems)
                    .filter(Objects::nonNull)
                    .forEach(drops::add);
            }
        }
    }
    
    @Override
    protected void handleOpened() {
        // Prevent players from receiving advancements from UI items
        PlayerUtils.stopAdvancementListening(getViewer());
    }
    
    @Override
    protected void handleClosed() {
        restorePlayerInventory();
        
        // Start the advancement listeners again
        PlayerUtils.stopAdvancementListening(getViewer());
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        Pair<AbstractGui, Integer> clicked = getWhereClicked(event);
        clicked.first().handleClick(clicked.second(), (Player) event.getWhoClicked(), event);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        // empty, should not be called by the WindowManager
    }
    
    @Override
    protected List<xyz.xenondevs.invui.inventory.Inventory> getContentInventories() {
        List<xyz.xenondevs.invui.inventory.Inventory> inventories = new ArrayList<>();
        for (Gui gui : getGuis()) {
            inventories.addAll(gui.getInventories());
        }
        return inventories;
    }
    
    @Override
    public Inventory[] getInventories() {
        return isOpen() ? new Inventory[] {upperInventory, playerInventory} : new Inventory[] {upperInventory};
    }
    
    public Inventory getUpperInventory() {
        return upperInventory;
    }
    
    public Inventory getPlayerInventory() {
        return playerInventory;
    }
    
    @Override
    public boolean isDouble() {
        return true;
    }
    
    protected abstract Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event);
    
}
