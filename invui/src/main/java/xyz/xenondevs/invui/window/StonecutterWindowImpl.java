package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomStonecutterMenu;
import xyz.xenondevs.invui.internal.util.ItemUtils2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A stonecutter window that uses both the top and bottom inventory.
 * <p>
 * Slot layout:
 * <ul>
 *     <li>[0, 1] -> upper gui</li>
 *     <li>[2, 37] -> lower gui</li>
 *     <li>[38, ?] -> buttons gui</li>
 * </ul>
 */
final class StonecutterWindowImpl extends AbstractSplitWindow<CustomStonecutterMenu> implements StonecutterWindow {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    private final AbstractGui buttonsGui;
    private final @Nullable ItemStack[] buttons;
    private boolean buttonsDirty = false;
    private List<BiConsumer<Integer, Integer>> selectedSlotChangeHandlers;
    
    public StonecutterWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        AbstractGui buttonsGui,
        List<BiConsumer<Integer, Integer>> selectedSlotChangeHandlers,
        boolean closable
    ) {
        super(player, title, lowerGui, upperGui.getSize() + lowerGui.getSize() + buttonsGui.getSize(), new CustomStonecutterMenu(player), closable);
        if (upperGui.getWidth() != 2 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Gui must of of dimensions 2x1.");
        if (lowerGui.getWidth() != 9 || lowerGui.getHeight() != 4)
            throw new IllegalArgumentException("Lower gui must of of dimensions 9x4.");
        if (buttonsGui.getWidth() != 4)
            throw new IllegalArgumentException("Buttons gui width must be 4.");
        
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        this.buttonsGui = buttonsGui;
        this.buttons = new ItemStack[buttonsGui.getSize()];
        this.selectedSlotChangeHandlers = new ArrayList<>(selectedSlotChangeHandlers);
        menu.setClickHandler(this::selectSlot);
    }
    
    private void selectSlot(int prev, int slot) {
        for (var handler : selectedSlotChangeHandlers) {
            handler.accept(prev, slot);
        }
        
        if (slot >= 0 && slot < buttonsGui.getSize()) {
            var click = new Click(getViewer(), ClickType.LEFT);
            buttonsGui.handleClick(slot, click);
        }
    }
    
    @Override
    public void handleTick() {
        super.handleTick();
        if (buttonsDirty) {
            menu.setButtons(ItemUtils2.withoutIntermediaryEmpties(Arrays.asList(buttons)));
            buttonsDirty = false;
        }
    }
    
    @Override
    public int getSelectedSlot() {
        return menu.getSelectedSlot();
    }
    
    @Override
    public void setSelectedSlot(int i) {
        menu.setSelectedSlot(i);
    }
    
    @Override
    protected void setMenuItem(int slot, @Nullable ItemStack itemStack) {
        if (slot == 0) {
            super.setMenuItem(0, ItemUtils2.nonEmpty(itemStack));
        } else if (slot >= 38 && slot < buttonsGui.getSize() + 38) {
            buttons[slot - 38] = itemStack;
            buttonsDirty = true;
        } else {
            super.setMenuItem(slot, itemStack);
        }
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(upperGui, lowerGui, buttonsGui);
    }
    
    @Override
    public List<? extends BiConsumer<Integer, Integer>> getSelectedSlotChangeHandlers() {
        return Collections.unmodifiableList(selectedSlotChangeHandlers);
    }
    
    @Override
    public void setSelectedSlotChangeHandlers(List<BiConsumer<Integer, Integer>> handlers) {
        selectedSlotChangeHandlers = new ArrayList<>(handlers);
    }
    
    @Override
    public void addSelectedSlotChangeHandler(BiConsumer<Integer, Integer> handler) {
        selectedSlotChangeHandlers.add(handler);
    }
    
    static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<StonecutterWindow, StonecutterWindow.Builder>
        implements StonecutterWindow.Builder
    {
        
        private Supplier<Gui> upperGuiSupplier = () -> Gui.empty(2, 1);
        private Supplier<Gui> butonsGuiSupplier = () -> Gui.empty(0, 0);
        private List<BiConsumer<Integer, Integer>> selectedSlotChangeHandlers = new ArrayList<>();
        
        @Override
        public StonecutterWindow.Builder setUpperGui(Supplier<Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BuilderImpl setButtonsGui(Supplier<Gui> guiSupplier) {
            this.butonsGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BuilderImpl setSelectedSlotChangeHandlers(List<BiConsumer<Integer, Integer>> handlers) {
            this.selectedSlotChangeHandlers = new ArrayList<>(handlers);
            return this;
        }
        
        @Override
        public BuilderImpl addSelectedSlotChangeHandler(BiConsumer<Integer, Integer> handler) {
            selectedSlotChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public StonecutterWindowImpl build(Player viewer) {
            return new StonecutterWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                (AbstractGui) butonsGuiSupplier.get(),
                selectedSlotChangeHandlers,
                closeable
            );
        }
        
    }
    
}