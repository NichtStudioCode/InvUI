package de.studiocode.invui.item.impl;

import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An {@link Item} that cycles trough a predefined array of {@link ItemBuilder}s when clicked.
 */
public class CycleItem extends BaseItem {
    
    private final Consumer<Integer> stateChangeHandler;
    private final ItemBuilder[] states;
    
    private int state;
    
    public CycleItem(@Nullable Consumer<Integer> stateChangeHandler, @NotNull ItemBuilder... states) {
        this.stateChangeHandler = stateChangeHandler;
        this.states = states;
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return states[state];
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType.isLeftClick()) cycle(true);
        else if (clickType.isRightClick()) cycle(false);
    }
    
    public void cycle(boolean forward) {
        if (forward) {
            if (++state == states.length) state = 0;
        } else {
            if (--state < 0) state = states.length - 1;
        }
        handleStateChange();
    }
    
    private void handleStateChange() {
        if (stateChangeHandler != null) stateChangeHandler.accept(state);
        notifyWindows();
    }
    
    public int getState() {
        return state;
    }
    
}
