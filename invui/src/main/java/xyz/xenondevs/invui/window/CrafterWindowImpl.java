package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomCrafterMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

final class CrafterWindowImpl extends AbstractSplitWindow<CustomCrafterMenu> implements CrafterWindow {
    
    private final AbstractGui craftingGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final List<BiConsumer<Integer, Boolean>> slotToggleHandlers;
    
    CrafterWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui craftingGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        List<BiConsumer<Integer, Boolean>> slotToggleHandlers,
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
        this.slotToggleHandlers = slotToggleHandlers;
        
        menu.setSlotStateChangeHandler(this::handleSlotToggled);
        initItems();
    }
    
    private void handleSlotToggled(int slot, boolean disabled) {
        for (var handler : slotToggleHandlers) {
            handler.accept(slot, disabled);
        }
    }
    
    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        menu.setSlotDisabled(slot, disabled);
    }
    
    @Override
    public boolean isSlotDisabled(int slot) {
        return menu.isSlotDisabled(slot);
    }
    
    @Override
    public List<? extends BiConsumer<Integer, Boolean>> getSlotToggleHandlers() {
        return Collections.unmodifiableList(slotToggleHandlers);
    }
    
    @Override
    public void setSlotToggleHandlers(List<? extends BiConsumer<Integer, Boolean>> handlers) {
        slotToggleHandlers.clear();
        slotToggleHandlers.addAll(handlers);
    }
    
    @Override
    public void addSlotToggleHandler(BiConsumer<Integer, Boolean> handler) {
        slotToggleHandlers.add(handler);
    }
    
    @Override
    public void removeSlotToggleHandler(BiConsumer<Integer, Boolean> handler) {
        slotToggleHandlers.remove(handler);
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(craftingGui, lowerGui, resultGui);
    }
    
    static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CrafterWindow, CrafterWindow.Builder>
        implements CrafterWindow.Builder
    {
        
        private Supplier<Gui> craftingGuiSupplier = () -> Gui.empty(3, 3);
        private Supplier<Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        private final List<BiConsumer<Integer, Boolean>> slotToggleHandlers = new ArrayList<>();
        
        @Override
        public CrafterWindow.Builder setCraftingGui(Supplier<Gui> guiSupplier) {
            this.craftingGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CrafterWindow.Builder setResultGui(Supplier<Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CrafterWindow.Builder setSlotToggleHandlers(List<? extends BiConsumer<Integer, Boolean>> handlers) {
            slotToggleHandlers.clear();
            slotToggleHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public CrafterWindow.Builder addSlotToggleHandler(BiConsumer<Integer, Boolean> handler) {
            slotToggleHandlers.add(handler);
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
                slotToggleHandlers,
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
