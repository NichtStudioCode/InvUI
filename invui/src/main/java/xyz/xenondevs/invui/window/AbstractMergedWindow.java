package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.inventory.ReferencingInventory;

import java.util.ArrayList;
import java.util.List;

sealed abstract class AbstractMergedWindow
    extends AbstractDoubleWindow
    permits NormalMergedWindowImpl 
{
    
    private final AbstractGui gui;
    
    public AbstractMergedWindow(Player player, Component title, AbstractGui gui, Inventory upperInventory, boolean closeable) {
        super(player, title, gui.getSize(), upperInventory, closeable);
        this.gui = gui;
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        int slot = event.getSlot();
        int clickedIndex = clicked == getUpperInventory() ? slot
            : getUpperInventory().getSize() + SlotUtils.translatePlayerInvToGui(slot);
        return new Pair<>(gui, clickedIndex);
    }
    
    @Override
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {gui};
    }
    
    @Override
    protected List<xyz.xenondevs.invui.inventory.Inventory> getContentInventories() {
        List<xyz.xenondevs.invui.inventory.Inventory> inventories = new ArrayList<>(gui.getAllInventories());
        inventories.add(ReferencingInventory.fromStorageContents(getViewer().getInventory()));
        return inventories;
    }
    
}
