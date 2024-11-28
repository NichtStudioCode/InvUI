package xyz.xenondevs.invui.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.function.BiConsumer;

/**
 * An {@link Item} that cycles through a predefined array of {@link ItemProvider}s when clicked.
 */
public class CycleItem extends AbstractItem {
    
    private final ItemProvider[] states;
    
    private int state;
    
    public CycleItem(ItemProvider... states) {
        this(0, states);
    }
    
    public CycleItem(int startState, ItemProvider... states) {
        this.states = states;
        this.state = startState;
    }
    
    public static CycleItem withStateChangeHandler(BiConsumer<Player, Integer> stateChangeHandler, ItemProvider... states) {
        return withStateChangeHandler(stateChangeHandler, 0, states);
    }
    
    public static CycleItem withStateChangeHandler(BiConsumer<Player, Integer> stateChangeHandler, int startState, ItemProvider... states) {
        return new CycleItem(startState, states) {
            @Override
            protected void handleStateChange(@Nullable Player player, int state) {
                stateChangeHandler.accept(player, state);
            }
        };
    }
    
    @Override
    public ItemProvider getItemProvider() {
        return states[state];
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (clickType.isLeftClick()) cycle(player, true);
        else if (clickType.isRightClick()) cycle(player, false);
    }
    
    public void cycle(boolean forward) {
        cycle(null, forward);
    }
    
    private void cycle(@Nullable Player player, boolean forward) {
        if (forward) {
            if (++state == states.length) state = 0;
        } else {
            if (--state < 0) state = states.length - 1;
        }
        
        handleStateChange(player, state);
        notifyWindows();
    }
    
    protected void handleStateChange(@Nullable Player player, int state) {
        // empty
    }
    
    public int getState() {
        return state;
    }
    
}
