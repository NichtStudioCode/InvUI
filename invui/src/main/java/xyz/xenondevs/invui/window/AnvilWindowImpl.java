package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomAnvilMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnvilWindowImpl extends AbstractSplitWindow<CustomAnvilMenu> implements AnvilWindow {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    
    public AnvilWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        List<Consumer<String>> renameHandlers,
        boolean closeable
    ) {
        super(player, title, lowerGui, upperGui.getSize() + lowerGui.getSize(), new CustomAnvilMenu(player, renameHandlers), closeable);
        if (upperGui.getWidth() != 3 && upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper gui must be of dimensions 3x1");
        
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public String getRenameText() {
        return menu.getRenameText();
    }
    
    @Override
    public int getEnchantmentCost() {
        return menu.getEnchantmentCost();
    }
    
    @Override
    public void setEnchantmentCost(int enchantmentCost) {
        menu.setEnchantmentCost(enchantmentCost);
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder>
        implements AnvilWindow.Builder
    {
        
        private @Nullable List<Consumer<String>> renameHandlers;
        private Supplier<Gui> upperGuiSupplier = () -> Gui.empty(3, 1);
        
        @Override
        public BuilderImpl setUpperGui(Supplier<Gui> guiSupplier) {
            upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BuilderImpl setRenameHandlers(List<Consumer<String>> renameHandlers) {
            this.renameHandlers = renameHandlers;
            return this;
        }
        
        @Override
        public BuilderImpl addRenameHandler(Consumer<String> renameHandler) {
            if (renameHandlers == null)
                renameHandlers = new ArrayList<>();
            
            renameHandlers.add(renameHandler);
            return this;
        }
        
        @Override
        public AnvilWindow build(Player viewer) {
            var window = new AnvilWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                renameHandlers != null ? renameHandlers : List.of(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
