package xyz.xenondevs.invui.window;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomFurnaceMenu;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class FurnaceWindowImpl extends AbstractSplitWindow<CustomFurnaceMenu> implements FurnaceWindow {
    
    private final AbstractGui inputGui;
    private final AbstractGui resultGui;
    private final AbstractGui lowerGui;
    private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
    private Property<? extends Double> cookProgress;
    private Property<? extends Double> burnProgress;
    
    public FurnaceWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui inputGui,
        AbstractGui resultGui,
        AbstractGui lowerGui,
        Property<? extends Double> cookProgress,
        Property<? extends Double> burnProgress,
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
        this.cookProgress = cookProgress;
        this.burnProgress = burnProgress;
        
        cookProgress.observeWeak(this, thisRef -> thisRef.menu.setCookProgress(cookProgress.get()));
        burnProgress.observeWeak(this, thisRef -> thisRef.menu.setBurnProgress(burnProgress.get()));
        menu.setCookProgress(cookProgress.get());
        menu.setBurnProgress(burnProgress.get());
        menu.setRecipeClickHandler(this::handleRecipeClick);
    }
    
    private void handleRecipeClick(Key recipeId) {
        for (var handler : recipeClickHandlers) {
            handler.accept(recipeId);
        }
    }
    
    @Override
    public void setCookProgress(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Progress must be between 0 and 1, but was " + progress);
        cookProgress.unobserveWeak(this);
        cookProgress = Property.of(progress);
        menu.setCookProgress(progress);
    }
    
    @Override
    public double getCookProgress() {
        return cookProgress.get();
    }
    
    @Override
    public void setBurnProgress(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Progress must be between 0 and 1, but was " + progress);
        burnProgress.unobserveWeak(this);
        burnProgress = Property.of(progress);
        menu.setBurnProgress(progress);
    }
    
    @Override
    public double getBurnProgress() {
        return burnProgress.get();
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
    public void setRecipeClickHandlers(List<? extends Consumer<Key>> handlers) {
        recipeClickHandlers.clear();
        recipeClickHandlers.addAll(handlers);
    }
    
    @Override
    public @UnmodifiableView List<Consumer<Key>> getRecipeClickHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(recipeClickHandlers);
    }
    
    public static final class BuilderImpl
        extends AbstractBuilder<FurnaceWindow, FurnaceWindow.Builder>
        implements FurnaceWindow.Builder
    {
        
        private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
        private Supplier<? extends Gui> inputGuiSupplier = () -> Gui.empty(1, 2);
        private Supplier<? extends Gui> resultGuiSupplier = () -> Gui.empty(1, 1);
        private Property<? extends Double> cookProgress = Property.of(0.0);
        private Property<? extends Double> burnProgress = Property.of(0.0);
        
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
        public FurnaceWindow.Builder setCookProgress(Property<? extends Double> progress) {
            this.cookProgress = progress;
            return this;
        }
        
        @Override
        public FurnaceWindow.Builder setBurnProgress(Property<? extends Double> progress) {
            this.burnProgress = progress;
            return this;
        }
        
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public FurnaceWindow build(Player viewer) {
            var window = new FurnaceWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) inputGuiSupplier.get(),
                (AbstractGui) resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                cookProgress,
                burnProgress,
                closeable
            );
            
            window.setRecipeClickHandlers((List) recipeClickHandlers);
            applyModifiers(window);
            
            return window;
        }
    }
    
}
