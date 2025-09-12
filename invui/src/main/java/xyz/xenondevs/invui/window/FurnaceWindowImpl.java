package xyz.xenondevs.invui.window;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomFurnaceMenu;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class FurnaceWindowImpl extends AbstractSplitWindow<CustomFurnaceMenu> implements FurnaceWindow {
    
    private static final int COOK_PROGRESS_MAGIC_SLOT = 100;
    private static final int BURN_PROGRESS_MAGIC_SLOT = 101;
    private static final double DEFAULT_COOK_PROGRESS = 0.0;
    private static final double DEFAULT_BURN_PROGRESS = 0.0;
    
    private final Gui inputGui;
    private final Gui resultGui;
    private final Gui lowerGui;
    private final List<Consumer<? super Key>> recipeClickHandlers = new ArrayList<>();
    private final MutableProperty<Double> cookProgress;
    private final MutableProperty<Double> burnProgress;
    
    public FurnaceWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        Gui inputGui,
        Gui resultGui,
        Gui lowerGui,
        MutableProperty<Double> cookProgress,
        MutableProperty<Double> burnProgress,
        MutableProperty<Boolean> closeable,
        MutableProperty<Integer> windowState
    ) {
        super(player, title, lowerGui, 39, new CustomFurnaceMenu(player), closeable, windowState);
        if (inputGui.getWidth() != 1 || inputGui.getHeight() != 2)
            throw new IllegalArgumentException("Input Gui must be of dimensions 1x2");
        if (resultGui.getWidth() != 1 || resultGui.getHeight() != 1)
            throw new IllegalArgumentException("Result Gui must be of dimensions 1x1");
        
        this.inputGui = inputGui;
        this.resultGui = resultGui;
        this.lowerGui = lowerGui;
        this.cookProgress = cookProgress;
        this.burnProgress = burnProgress;
        
        cookProgress.observeWeak(this, thisRef -> thisRef.notifyUpdate(COOK_PROGRESS_MAGIC_SLOT));
        burnProgress.observeWeak(this, thisRef -> thisRef.notifyUpdate(BURN_PROGRESS_MAGIC_SLOT));
        menu.setCookProgress(getCookProgress());
        menu.setBurnProgress(getBurnProgress());
        menu.setRecipeClickHandler(this::handleRecipeClick);
    }
    
    @Override
    protected void update(int slot) {
        switch (slot) {
            case COOK_PROGRESS_MAGIC_SLOT -> menu.setCookProgress(getCookProgress());
            case BURN_PROGRESS_MAGIC_SLOT -> menu.setBurnProgress(getBurnProgress());
            default -> super.update(slot);
        }
    }
    
    private void handleRecipeClick(Key recipeId) {
        CollectionUtils.forEachCatching(
            recipeClickHandlers,
            handler -> handler.accept(recipeId),
            "Failed to handle recipe click of '" + recipeId + "'"
        );
    }
    
    @Override
    public void setCookProgress(double progress) {
        this.cookProgress.set(progress);
    }
    
    @Override
    public double getCookProgress() {
        return FuncUtils.getSafely(cookProgress, DEFAULT_COOK_PROGRESS);
    }
    
    @Override
    public void setBurnProgress(double progress) {
        this.burnProgress.set(progress);
    }
    
    @Override
    public double getBurnProgress() {
        return FuncUtils.getSafely(burnProgress, DEFAULT_BURN_PROGRESS);
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
        private MutableProperty<Double> cookProgress = MutableProperty.of(DEFAULT_COOK_PROGRESS);
        private MutableProperty<Double> burnProgress = MutableProperty.of(DEFAULT_BURN_PROGRESS);
        
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
        public FurnaceWindow.Builder setCookProgress(MutableProperty<Double> progress) {
            this.cookProgress = progress;
            return this;
        }
        
        @Override
        public FurnaceWindow.Builder setBurnProgress(MutableProperty<Double> progress) {
            this.burnProgress = progress;
            return this;
        }
        
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public FurnaceWindow build(Player viewer) {
            var window = new FurnaceWindowImpl(
                viewer,
                titleSupplier,
                inputGuiSupplier.get(),
                resultGuiSupplier.get(),
                supplyLowerGui(viewer),
                cookProgress,
                burnProgress,
                closeable,
                windowState
            );
            
            window.setRecipeClickHandlers((List) recipeClickHandlers);
            applyModifiers(window);
            
            return window;
        }
    }
    
}
