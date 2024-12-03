package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.AnvilInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnvilSplitWindowImpl extends AbstractSplitWindow implements AnvilWindow {
    
    private final AnvilInventory anvilInventory;
    
    public AnvilSplitWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        List<Consumer<String>> renameHandlers,
        boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, null, closeable);
        
        anvilInventory = new AnvilInventory(player, renameHandlers);
        upperInventory = anvilInventory.getBukkitInventory();
    }
    
    @Override
    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        anvilInventory.setItem(slot, itemStack);
    }
    
    @Override
    protected void openInventory(Player viewer) {
        anvilInventory.open(getTitle());
    }
    
    @Override
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder.Split>
        implements AnvilWindow.Builder.Split
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
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");
            if (lowerGuiSupplier == null)
                throw new IllegalStateException("Lower Gui is not defined.");
            
            var window = new AnvilSplitWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                (AbstractGui) lowerGuiSupplier.get(),
                renameHandlers != null ? renameHandlers : List.of(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
