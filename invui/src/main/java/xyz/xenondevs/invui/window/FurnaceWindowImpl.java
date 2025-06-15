package xyz.xenondevs.invui.window;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomCraftingTableMenu;
import xyz.xenondevs.invui.internal.menu.CustomFurnaceMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class FurnaceWindowImpl extends AbstractSplitWindow<CustomFurnaceMenu> implements FurnaceWindow {
    
    private final AbstractGui inputGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
    
    public FurnaceWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui inputGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, lowerGui, 39, new CustomFurnaceMenu(player), closeable);
        if (inputGui.getWidth() != 1 || inputGui.getHeight() != 2)
            throw new IllegalArgumentException("Input Gui must be of dimensions 1x2");
        if (resultGui.getWidth() != 1 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result Gui must be of dimensions 1x1");
        
        this.inputGui = inputGui;
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
        return List.of(inputGui, resultGui, lowerGui);
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
        extends AbstractBuilder<FurnaceWindow, FurnaceWindow.Builder>
        implements FurnaceWindow.Builder
    {
        
        private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 2);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        
        @Override
        public FurnaceWindow.Builder setInputGui(Supplier<? extends Gui> guiSupplier) {
            this.inputGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public FurnaceWindow.Builder setResultGui(Supplier<? extends Gui> guiSupplier) {
            this.resultGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public FurnaceWindow.Builder setRecipeClickHandlers(List<? extends Consumer<? super Key>> handlers) {
            this.recipeClickHandlers.clear();
            this.recipeClickHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public FurnaceWindow.Builder addRecipeClickHandler(Consumer<? super Key> handler) {
            this.recipeClickHandlers.add(handler);
            return this;
        }
        
        @Override
        public FurnaceWindow build(Player viewer) {
            var window = new FurnaceWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) inputGuiSupplier.get(),
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
