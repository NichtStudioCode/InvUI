package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomCrafterMenu;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

final class CrafterWindowImpl extends AbstractSplitWindow<CustomCrafterMenu> implements CrafterWindow {
    
    private static final int CRAFTING_SLOTS = 9;
    
    private final AbstractGui craftingGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final List<? extends MutableProperty<Boolean>> slots;
    private final List<BiConsumer<? super Integer, ? super Boolean>> slotToggleHandlers;
    
    CrafterWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui craftingGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        List<? extends MutableProperty<Boolean>> slots,
        List<BiConsumer<? super Integer, ? super Boolean>> slotToggleHandlers,
        boolean closeable
    ) {
        super(player, title, lowerGui, 46, new CustomCrafterMenu(player), closeable);
        if (craftingGui.getWidth() != 3 || craftingGui.getHeight() != 3)
            throw new IllegalArgumentException("Crafting Gui must be of dimensions 3x3");
        if (resultGui.getWidth() != 1 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result Gui must be of dimensions 1x1");
        
        this.craftingGui = craftingGui;
        this.resultGui = resultGui;
        this.lowerGui = lowerGui;
        this.slots = slots;
        this.slotToggleHandlers = slotToggleHandlers;
        
        for (int i = 0; i < CRAFTING_SLOTS; i++) {
            int slot = i;
            MutableProperty<Boolean> property = slots.get(i);
            property.observeWeak(this, thisRef -> thisRef.menu.setSlotDisabled(slot, property.get()));
            menu.setSlotDisabled(i, property.get());
        }
        menu.setSlotStateChangeHandler(this::playerToggleSlot);
    }
    
    private void playerToggleSlot(int slot, boolean disabled) {
        slots.get(slot).set(disabled);
        for (var handler : slotToggleHandlers) {
            handler.accept(slot, disabled);
        }
    }
    
    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        menu.setSlotDisabled(slot, disabled);
        slots.get(slot).set(disabled);
    }
    
    @Override
    public boolean isSlotDisabled(int slot) {
        return menu.isSlotDisabled(slot);
    }
    
    @Override
    public @UnmodifiableView List<BiConsumer<Integer, Boolean>> getSlotToggleHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(slotToggleHandlers);
    }
    
    @Override
    public void setSlotToggleHandlers(List<? extends BiConsumer<Integer, Boolean>> handlers) {
        slotToggleHandlers.clear();
        slotToggleHandlers.addAll(handlers);
    }
    
    @Override
    public void addSlotToggleHandler(BiConsumer<? super Integer, ? super Boolean> handler) {
        slotToggleHandlers.add(handler);
    }
    
    @Override
    public void removeSlotToggleHandler(BiConsumer<? super Integer, ? super Boolean> handler) {
        slotToggleHandlers.remove(handler);
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(craftingGui, lowerGui, resultGui);
    }
    
    static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CrafterWindow, CrafterWindow.Builder>
        implements CrafterWindow.Builder
    {
        
        private Supplier<? extends Gui> craftingGuiSupplier = () -> Gui.empty(3, 3);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        private final List<BiConsumer<? super Integer, ? super Boolean>> slotToggleHandlers = new ArrayList<>();
        private final List<MutableProperty<Boolean>> slots = CollectionUtils.newList(9, i -> MutableProperty.of(false));
        
        @Override
        public CrafterWindow.Builder setCraftingGui(Supplier<? extends Gui> guiSupplier) {
            this.craftingGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CrafterWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CrafterWindow.Builder setSlotToggleHandlers(List<? extends BiConsumer<? super Integer, ? super Boolean>> handlers) {
            slotToggleHandlers.clear();
            slotToggleHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public CrafterWindow.Builder addSlotToggleHandler(BiConsumer<? super Integer, ? super Boolean> handler) {
            slotToggleHandlers.add(handler);
            return this;
        }
        
        @Override
        public CrafterWindow.Builder setSlot(int slot, MutableProperty<Boolean> state) {
            if (slot < 0 || slot >= CRAFTING_SLOTS)
                throw new IllegalArgumentException("Slot must be between 0 and 8");
            slots.set(slot, state);
            return this;
        }
        
        @Override
        public CrafterWindow.Builder setSlots(List<? extends MutableProperty<Boolean>> slots) {
            if (slots.size() != CRAFTING_SLOTS)
                throw new IllegalArgumentException("Slots must contain exactly " + CRAFTING_SLOTS + " properties");
            
            for (int i = 0; i < CRAFTING_SLOTS; i++) {
                this.slots.set(i, slots.get(i));
            }
            return this;
        }
        
        @Override
        public CrafterWindowImpl build(Player viewer) {
            var window = new CrafterWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) craftingGuiSupplier.get(),
                (AbstractGui) resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                slots,
                slotToggleHandlers,
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
