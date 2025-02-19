package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.CustomStonecutterMenu;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.internal.util.ItemUtils2;
import xyz.xenondevs.invui.internal.util.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A stonecutter window that only uses the top inventory.
 * <p>
 * Slot layout:
 * <ul>
 *     <li>[0, 1] -> gui</li>
 *     <li>[2, 37] -> player inventory (not handled by this window, but slot numbers may not overlap, as this would interfere with drag handling)</li>
 *     <li>[38, ?] -> buttons gui</li>
 * </ul>
 */
final class StonecutterSingleWindowImpl extends AbstractSingleWindow implements StonecutterWindow {
    
    private final AbstractGui buttonsGui;
    private final CustomStonecutterMenu menu;
    private final @Nullable ItemStack[] buttons;
    private boolean buttonsDirty = false;
    private List<BiConsumer<Integer, Integer>> selectedSlotChangeHandlers;
    
    public StonecutterSingleWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui gui,
        AbstractGui buttonsGui,
        List<BiConsumer<Integer, Integer>> selectedSlotChangeHandlers,
        boolean closable
    ) {
        super(player, title, gui, gui.getSize() + 36 + buttonsGui.getSize(), null, closable);
        this.menu = new CustomStonecutterMenu(player, this::selectSlot);
        this.inventory = menu.getBukkitView().getTopInventory();
        this.buttonsGui = buttonsGui;
        this.buttons = new ItemStack[buttonsGui.getSize()];
        this.selectedSlotChangeHandlers = new ArrayList<>(selectedSlotChangeHandlers);
        
        if (gui.getWidth() != 2 || gui.getHeight() != 1)
            throw new IllegalArgumentException("Gui must of of dimensions 2x1.");
        if (buttonsGui.getWidth() != 4)
            throw new IllegalArgumentException("Buttons gui width must be 4.");
    }
    
    private void selectSlot(int prev, int slot) {
        for (var handler : selectedSlotChangeHandlers) {
            handler.accept(prev, slot);
        }
        
        if (slot >= 0 && slot < buttonsGui.getSize()) {
            var event = new InventoryClickEvent(
                menu.getBukkitView(),
                InventoryType.SlotType.CONTAINER,
                slot + 2,
                ClickType.LEFT,
                InventoryAction.UNKNOWN
            )
            {
                
                @Override
                public @Nullable ItemStack getCurrentItem() {
                    return buttons[slot];
                }
                
                @Override
                public void setCurrentItem(@Nullable ItemStack stack) {
                    throw new UnsupportedOperationException();
                }
            };
            
            buttonsGui.handleClick(slot, getViewer(), event);
        }
    }
    
    @Override
    protected void handleClosed() {
        super.handleClosed();
        menu.unregisterRecipes();
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
    public void handleItemShift(InventoryClickEvent event) {
        gui.handleItemShift(event);
        buttonsGui.handleItemShift(event);
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
    protected void openInventory(Player viewer) {
        InventoryUtils.openCustomInventory(viewer, menu, getTitle());
    }
    
    @Override
    protected void setInvItem(int slot, @Nullable ItemStack itemStack) {
        // refer to slot layout above
        if (slot == 0 || slot == 1) {
            if (slot == 0)
                itemStack = ItemUtils2.nonEmpty(itemStack);
            
            menu.setItem(slot, menu.incrementStateId(), CraftItemStack.asNMSCopy(itemStack));
        } else if (slot >= 38 && slot < buttonsGui.getSize() + 38) {
            buttons[slot - 38] = itemStack;
            buttonsDirty = true;
        }
    }
    
    @Override
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int i) {
        // refer to slot layout above
        if (i == 0 || i == 1)
            return new Pair<>(gui, i);
        
        i -= 38;
        
        if (i >= 0 && i < buttonsGui.getSize())
            return new Pair<>(buttonsGui, i);
        
        return null;
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(gui, buttonsGui);
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
        extends AbstractSingleWindow.AbstractBuilder<StonecutterWindow, StonecutterWindow.Builder.Single>
        implements StonecutterWindow.Builder.Single
    {
        
        private @Nullable Supplier<Gui> buttonsGuiSupplier;
        private List<BiConsumer<Integer, Integer>> selectedSlotChangeHandlers = new ArrayList<>();
        
        @Override
        public BuilderImpl setButtonsGui(Gui gui) {
            this.buttonsGuiSupplier = () -> gui;
            return this;
        }
        
        @Override
        public BuilderImpl setButtonsGui(Gui.Builder<?, ?> builder) {
            this.buttonsGuiSupplier = builder::build;
            return this;
        }
        
        @Override
        public BuilderImpl setButtonsGui(Supplier<Gui> guiSupplier) {
            this.buttonsGuiSupplier = guiSupplier;
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
        public StonecutterSingleWindowImpl build(Player viewer) {
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            if (buttonsGuiSupplier == null)
                throw new IllegalStateException("Buttons gui is not defined.");
            
            return new StonecutterSingleWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) guiSupplier.get(),
                (AbstractGui) buttonsGuiSupplier.get(),
                selectedSlotChangeHandlers,
                closeable
            );
        }
        
    }
    
}
