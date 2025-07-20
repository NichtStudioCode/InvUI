package xyz.xenondevs.invui.window;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomCraftingTableMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class CraftingWindowImpl extends AbstractSplitWindow<CustomCraftingTableMenu> implements CraftingWindow {
    
    private final AbstractGui craftingGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
    
    public CraftingWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui craftingGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, lowerGui, 46, new CustomCraftingTableMenu(player), closeable);
        if (craftingGui.getWidth() != 3 || craftingGui.getHeight() != 3)
            throw new IllegalArgumentException("Crafting Gui must be of dimensions 3x3");
        if (resultGui.getWidth() != 1 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result Gui must be of dimensions 1x1");
        
        this.craftingGui = craftingGui;
        this.resultGui = resultGui;
        this.lowerGui = lowerGui;
        menu.setRecipeClickHandler(this::handleRecipeClick);
    }
    
    private void handleRecipeClick(Key recipeId) {
        for (var handler : recipeClickHandlers) {
            handler.accept(recipeId);
        }
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(resultGui, craftingGui, lowerGui);
    }
    
    @Override
    public void sendGhostRecipe(Key recipeId) {
        if (!isOpen())
            throw new IllegalStateException("Window is not open");
        menu.sendGhostRecipe(recipeId);
    }
    
    @Override
    public void addRecipeClickHandler(Consumer<? super Key> handler) {
        recipeClickHandlers.add(handler);
    }
    
    @Override
    public void removeRecipeClickHandler(Consumer<? super Key> handler) {
        recipeClickHandlers.remove(handler);
    }
    
    @Override
    public void setRecipeClickHandlers(List<? extends Consumer<? super Key>> handlers) {
        recipeClickHandlers.clear();
        recipeClickHandlers.addAll(handlers);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<CraftingWindow, CraftingWindow.Builder>
        implements CraftingWindow.Builder
    {
        
        private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
        private Supplier<? extends Gui> craftingGuiSupplier = () -> Gui.empty(3, 3);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        
        @Override
        public CraftingWindow.Builder setCraftingGui(Supplier<? extends Gui> guiSupplier) {
            this.craftingGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CraftingWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public CraftingWindow.Builder setRecipeClickHandlers(List<? extends Consumer<? super Key>> handlers) {
            this.recipeClickHandlers.clear();
            this.recipeClickHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public CraftingWindow.Builder addRecipeClickHandler(Consumer<? super Key> handler) {
            this.recipeClickHandlers.add(handler);
            return this;
        }
        
        @Override
        public CraftingWindow build(Player viewer) {
            var window = new CraftingWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) craftingGuiSupplier.get(),
                (AbstractGui) resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                closeable
            );
            
            window.setRecipeClickHandlers(recipeClickHandlers);
            applyModifiers(window);
            
            return window;
        }
    }
    
}
