package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link Window} where top and player {@link Inventory} are affected by different {@link Gui Guis}.
 * <p>
 * Only in very rare circumstances should this class be used directly.
 * Instead, use {@link Window#split()} to create such a {@link Window}.
 */
public abstract class AbstractSplitWindow extends AbstractDoubleWindow {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    
    /**
     * Creates a new {@link AbstractSplitWindow}.
     *
     * @param player         The {@link Player} that views the window.
     * @param title          The title of the window.
     * @param upperGui       The {@link Gui} of the upper part of the window.
     * @param lowerGui       The {@link Gui} of the lower part of the window.
     * @param upperInventory The {@link Inventory} of the upper part of the window.
     * @param closeable      Whether the window is closeable.
     */
    public AbstractSplitWindow(Player player, Component title, AbstractGui upperGui, AbstractGui lowerGui, Inventory upperInventory, boolean closeable) {
        super(player, title, upperGui.getSize() + lowerGui.getSize(), upperInventory, closeable);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(child == upperGui ? slotIndex : upperGui.getSize() + slotIndex,
            child.getSlotElement(slotIndex), true);
    }
    
    @Override
    public @Nullable SlotElement getSlotElement(int index) {
        if (index >= upperGui.getSize()) return lowerGui.getSlotElement(index - upperGui.getSize());
        else return upperGui.getSlotElement(index);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == getUpperInventory()) {
            return new Pair<>(upperGui, event.getSlot());
        } else {
            int index = SlotUtils.translatePlayerInvToGui(event.getSlot());
            return new Pair<>(lowerGui, index);
        }
    }
    
    @Override
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int index) {
        if (index < upperGui.getSize()) {
            return new Pair<>(upperGui, index);
        } else if (index < (upperGui.getSize() + lowerGui.getSize())) {
            return new Pair<>(lowerGui, index - upperGui.getSize());
        } else {
            return null;
        }
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {upperGui, lowerGui};
    }
    
    @Override
    protected List<xyz.xenondevs.invui.inventory.Inventory> getContentInventories() {
        List<xyz.xenondevs.invui.inventory.Inventory> inventories = new ArrayList<>();
        inventories.addAll(upperGui.getAllInventories());
        inventories.addAll(lowerGui.getAllInventories());
        return inventories;
    }
    
    /**
     * Builder for {@link AbstractSplitWindow}.
     * <p>
     * This class should only be used directly if you're creating a custom {@link AbstractBuilder} for a custom
     * {@link AbstractSingleWindow} implementation. Otherwise, use the static builder functions in the {@link Window}
     * interface, such as {@link Window#split()} to obtain a builder.
     *
     * @param <W> The type of the window.
     * @param <S> The type of the builder.
     */
    @SuppressWarnings("unchecked")
    public static abstract class AbstractBuilder<W extends Window, S extends Window.Builder.Double<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Window.Builder.Double<W, S>
    {
        
        /**
         * The {@link Supplier} to receive the upper {@link Gui} from.
         */
        protected @Nullable Supplier<Gui> upperGuiSupplier;
        /**
         * The {@link Supplier} to receive the lower {@link Gui} from.
         */
        protected @Nullable Supplier<Gui> lowerGuiSupplier;
        
        @Override
        public S setUpperGui(Supplier<Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public S setUpperGui(Gui gui) {
            this.upperGuiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public S setUpperGui(Gui.Builder<?, ?> builder) {
            this.upperGuiSupplier = builder::build;
            return (S) this;
        }
        
        @Override
        public S setLowerGui(Supplier<Gui> guiSupplier) {
            this.lowerGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public S setLowerGui(Gui gui) {
            this.lowerGuiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public S setLowerGui(Gui.Builder<?, ?> builder) {
            this.lowerGuiSupplier = builder::build;
            return (S) this;
        }
        
    }
    
}
