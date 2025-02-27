package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.menu.CustomAnvilMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnvilWindowImpl extends AbstractSplitWindow<CustomAnvilMenu> implements AnvilWindow {
    
    public AnvilWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        List<Consumer<String>> renameHandlers,
        boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, new CustomAnvilMenu(player, renameHandlers), closeable);
        if (upperGui.getWidth() != 3 && upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper gui must be of dimensions 3x1");
        initItems();
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
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder>
        implements AnvilWindow.Builder
    {
        
        private @Nullable List<Consumer<String>> renameHandlers;
        
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
                supplyUpperGui(),
                supplyLowerGui(viewer),
                renameHandlers != null ? renameHandlers : List.of(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
