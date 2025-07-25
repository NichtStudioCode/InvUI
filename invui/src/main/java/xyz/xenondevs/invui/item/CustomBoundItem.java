package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.util.QuadConsumer;
import xyz.xenondevs.invui.util.TriConsumer;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

class CustomBoundItem<G extends Gui> extends AbstractBoundItem {
    
    private final TriConsumer<? super Item, ? super G, ? super Click> clickHandler;
    private final QuadConsumer<? super Item, ? super G, ? super Player, ? super Integer> selectHandler;
    private volatile BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProvider;
    private final BiConsumer<? super Item, ? super G> bindHandler;
    private final int updatePeriod;
    private @Nullable BukkitTask updateTask;
    
    public CustomBoundItem(
        BiConsumer<? super Item, ? super G> bindHandler,
        TriConsumer<? super Item, ? super G, ? super Click> clickHandler,
        QuadConsumer<? super Item, ? super G, ? super Player, ? super Integer> selectHandler,
        BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProvider,
        int updatePeriod
    ) {
        this.bindHandler = bindHandler;
        this.clickHandler = clickHandler;
        this.selectHandler = selectHandler;
        this.itemProvider = itemProvider;
        this.updatePeriod = updatePeriod;
    }
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return itemProvider.apply(viewer, getGui());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public @NonNull G getGui() {
        return (G) super.getGui();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void bind(Gui gui) {
        bindHandler.accept(this, (G) gui);
        super.bind(gui);
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        clickHandler.accept(this, getGui(), click);
    }
    
    @Override
    public void handleBundleSelect(Player player, int bundleSlot) {
        selectHandler.accept(this, getGui(), player, bundleSlot);
    }
    
    @Override
    public void addViewer(AbstractWindow<?> who, int how) {
        super.addViewer(who, how);
        if (updatePeriod > 0 && updateTask == null) {
            updateTask = Bukkit.getScheduler().runTaskTimer(
                InvUI.getInstance().getPlugin(),
                this::notifyWindows,
                0,
                updatePeriod
            );
        }
    }
    
    @Override
    public void removeViewer(AbstractWindow<?> who, int how) {
        super.removeViewer(who, how);
        if (updateTask != null && viewers.isEmpty()) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    non-sealed static class Builder<G extends Gui> implements BoundItem.Builder<G> {
        
        protected BiConsumer<Item, G> bindHandler = (item, gui) -> {};
        private TriConsumer<Item, G, Click> clickHandler = (item, gui, click) -> {};
        private QuadConsumer<Item, G, Player, Integer> selectHandler = (item, gui, player, slot) -> {};
        private @Nullable BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProviderFn;
        private @Nullable ItemProvider asyncPlaceholder;
        private @Nullable Supplier<? extends ItemProvider> asyncSupplier;
        private @Nullable CompletableFuture<? extends ItemProvider> asyncFuture;
        private Consumer<Item> modifier = item -> {};
        private boolean updateOnClick;
        private int updatePeriod = -1;
        
        @Override
        public Builder<G> setItemProvider(ItemProvider itemProvider) {
            this.itemProviderFn = (viewer, gui) -> itemProvider;
            return this;
        }
        
        @Override
        public Builder<G> setItemProvider(Function<? super Player, ? extends ItemProvider> itemProvider) {
            this.itemProviderFn = (viewer, gui) -> itemProvider.apply(viewer);
            return this;
        }
        
        @Override
        public Builder<G> setItemProvider(BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProvider) {
            this.itemProviderFn = itemProvider;
            return this;
        }
        
        @Override
        public BoundItem.Builder<G> setCyclingItemProvider(int period, List<? extends ItemProvider> itemProviders) {
            if (itemProviders.isEmpty())
                throw new IllegalArgumentException("itemProviders must not be empty");
            
            if (itemProviders.size() > 1) {
                updatePeriodically(period);
                this.itemProviderFn = (viewer, gui) -> {
                    int i = (Bukkit.getCurrentTick() / period) % itemProviders.size();
                    return itemProviders.get(i);
                };
            } else {
                setItemProvider(itemProviders.getFirst());
            }
            return this;
        }
        
        @Override
        public BoundItem.Builder<G> async(ItemProvider placeholder, Supplier<? extends ItemProvider> itemProviderSupplier) {
            this.asyncPlaceholder = placeholder;
            this.asyncSupplier = itemProviderSupplier;
            return this;
        }
        
        @Override
        public BoundItem.Builder<G> async(ItemProvider placeholder, CompletableFuture<? extends ItemProvider> itemProviderFuture) {
            this.asyncPlaceholder = placeholder;
            this.asyncFuture = itemProviderFuture;
            return this;
        }
        
        @Override
        public Builder<G> updatePeriodically(int period) {
            this.updatePeriod = period;
            return this;
        }
        
        @Override
        public Builder<G> updateOnClick() {
            this.updateOnClick = true;
            return this;
        }
        
        @Override
        public Builder<G> addClickHandler(BiConsumer<? super Item, ? super Click> clickHandler) {
            this.clickHandler.andThen((item, gui, click) -> clickHandler.accept(item, click));
            return this;
        }
        
        @Override
        public Builder<G> addClickHandler(TriConsumer<? super Item, ? super G, ? super Click> handler) {
            clickHandler = clickHandler.andThen(handler);
            return this;
        }
        
        @Override
        public Builder<G> addBundleSelectHandler(QuadConsumer<? super Item, ? super G, ? super Player, ? super Integer> handler) {
            selectHandler = selectHandler.andThen(handler);
            return this;
        }
        
        @Override
        public Builder<G> addBundleSelectHandler(TriConsumer<? super Item, ? super Player, ? super Integer> selectHandler) {
            this.selectHandler = this.selectHandler.andThen((item, gui, player, slot) -> selectHandler.accept(item, player, slot));
            return this;
        }
        
        @Override
        public Builder<G> addBindHandler(BiConsumer<? super Item, ? super G> handler) {
            bindHandler = bindHandler.andThen(handler);
            return this;
        }
        
        @Override
        public Builder<G> addModifier(Consumer<? super Item> modifier) {
            this.modifier = this.modifier.andThen(modifier);
            return this;
        }
        
        @Override
        public BoundItem build() {
            if (updateOnClick) {
                clickHandler = clickHandler.andThen((item, gui, click) -> item.notifyWindows());
            }
            
            CustomBoundItem<G> customItem;
            if (asyncPlaceholder != null && itemProviderFn != null) {
                customItem = new CustomBoundItem<>(
                    bindHandler,
                    clickHandler,
                    selectHandler,
                    (viewer, gui) -> asyncPlaceholder,
                    updatePeriod
                );
                
                if (asyncSupplier != null) {
                    Bukkit.getScheduler().runTaskAsynchronously(
                        InvUI.getInstance().getPlugin(),
                        () -> {
                            var itemProvider = asyncSupplier.get();
                            customItem.itemProvider = (viewer, gui) -> itemProvider;
                            customItem.notifyWindows();
                        }
                    );
                } else if (asyncFuture != null) {
                    asyncFuture.thenAccept(itemProvider -> {
                        customItem.itemProvider = (viewer, gui) -> itemProvider;
                        customItem.notifyWindows();
                    });
                }
            } else {
                customItem = new CustomBoundItem<>(
                    bindHandler,
                    clickHandler,
                    selectHandler,
                    itemProviderFn != null ? itemProviderFn : (viewer, gui) -> ItemProvider.EMPTY,
                    updatePeriod
                );
            }
            
            modifier.accept(customItem);
            
            return customItem;
        }
        
        static class Paged extends Builder<PagedGui<?>> {
            
            Paged() {
                bindHandler = bindHandler.andThen((item, gui) -> {
                    gui.addPageChangeHandler((oldPage, newPage) -> item.notifyWindows());
                    gui.addPageCountChangeHandler((oldCount, newCount) -> item.notifyWindows());
                });
            }
            
        }
        
        static class Scroll extends Builder<ScrollGui<?>> {
            
            Scroll() {
                bindHandler = bindHandler.andThen((item, gui) -> {
                    gui.addScrollHandler((oldScroll, newScroll) -> item.notifyWindows());
                    gui.addLineCountChangeHandler((oldCount, newCount) -> item.notifyWindows());
                });
            }
            
        }
        
        static class Tab extends Builder<TabGui> {
            
            Tab() {
                bindHandler = bindHandler.andThen((item, gui) -> 
                    gui.addTabChangeHandler((oldTab, newTab) -> item.notifyWindows())
                );
            }
            
        }
        
    }
    
}
