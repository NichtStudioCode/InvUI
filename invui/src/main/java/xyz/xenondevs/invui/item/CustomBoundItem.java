package xyz.xenondevs.invui.item;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.util.QuadConsumer;
import xyz.xenondevs.invui.util.TriConsumer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

class CustomBoundItem<G extends Gui> extends AbstractBoundItem {
    
    private final Class<? extends Gui> guiClass;
    private final TriConsumer<? super Item, ? super G, ? super Click> clickHandler;
    private final QuadConsumer<? super Item, ? super G, ? super Player, ? super Integer> selectHandler;
    private volatile BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProvider;
    private final BiConsumer<? super Item, ? super G> bindHandler;
    private final BiConsumer<? super Item, ? super G> unbindHandler;
    private final int updatePeriod;
    private @Nullable ScheduledTask updateTask;
    
    public CustomBoundItem(
        Class<? extends Gui> guiClass,
        BiConsumer<? super Item, ? super G> bindHandler,
        BiConsumer<? super Item, ? super G> unbindHandler,
        TriConsumer<? super Item, ? super G, ? super Click> clickHandler,
        QuadConsumer<? super Item, ? super G, ? super Player, ? super Integer> selectHandler,
        BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProvider,
        int updatePeriod
    ) {
        this.guiClass = guiClass;
        this.bindHandler = bindHandler;
        this.unbindHandler = unbindHandler;
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
        if (!guiClass.isAssignableFrom(gui.getClass()))
            throw new IllegalArgumentException("This item can only be bound to a " + guiClass.getSimpleName());
        bindHandler.accept(this, (G) gui);
        super.bind(gui);
    }
    
    @Override
    public void unbind() {
        unbindHandler.accept(this, getGui());
        super.unbind();
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
    public void addObserver(Observer who, int what, int how) {
        super.addObserver(who, what, how);
        if (updatePeriod > 0 && updateTask == null) {
            updateTask = Bukkit.getAsyncScheduler().runAtFixedRate(
                InvUI.getInstance().getPlugin(),
                x -> notifyWindows(),
                0,
                updatePeriod * 50L,
                TimeUnit.MILLISECONDS
            );
        }
    }
    
    @Override
    public void removeObserver(Observer who, int what, int how) {
        super.removeObserver(who, what, how);
        if (updateTask != null && observers.isEmpty()) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    non-sealed static class Builder<G extends Gui> implements BoundItem.Builder<G> {
        
        private final Class<? extends Gui> guiClass;
        protected BiConsumer<Item, G> bindHandler = (item, gui) -> {};
        protected BiConsumer<Item, G> unbindHandler = (item, gui) -> {};
        private TriConsumer<Item, G, Click> clickHandler = (item, gui, click) -> {};
        private QuadConsumer<Item, G, Player, Integer> selectHandler = (item, gui, player, slot) -> {};
        private @Nullable BiFunction<? super Player, ? super G, ? extends ItemProvider> itemProviderFn;
        private @Nullable ItemProvider asyncPlaceholder;
        private @Nullable Supplier<? extends ItemProvider> asyncSupplier;
        private @Nullable CompletableFuture<? extends ItemProvider> asyncFuture;
        private Consumer<Item> modifier = item -> {};
        private boolean updateOnClick;
        private int updatePeriod = -1;
        
        public Builder(Class<? extends Gui> guiClass) {
            this.guiClass = guiClass;
        }
        
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
        public BoundItem.Builder<G> addUnbindHandler(BiConsumer<? super Item, ? super G> handler) {
            unbindHandler = unbindHandler.andThen(handler);
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
                    guiClass,
                    bindHandler,
                    unbindHandler,
                    clickHandler,
                    selectHandler,
                    (viewer, gui) -> asyncPlaceholder,
                    updatePeriod
                );
                
                if (asyncSupplier != null) {
                    Bukkit.getAsyncScheduler().runNow(
                        InvUI.getInstance().getPlugin(),
                        x -> {
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
                    guiClass,
                    bindHandler,
                    unbindHandler,
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
                super(PagedGui.class);
                
                var pageChangeHandler = new AtomicReference<BiConsumer<Integer, Integer>>();
                var pageCountChangeHandler = new AtomicReference<BiConsumer<Integer, Integer>>();
                
                bindHandler = bindHandler.andThen((item, gui) -> {
                    pageChangeHandler.set((oldPage, newPage) -> item.notifyWindows());
                    pageCountChangeHandler.set((oldCount, newCount) -> item.notifyWindows());
                    gui.addPageChangeHandler(pageChangeHandler.get());
                    gui.addPageCountChangeHandler(pageCountChangeHandler.get());
                });
                
                unbindHandler = unbindHandler.andThen((item, gui) -> {
                    gui.removePageChangeHandler(pageChangeHandler.get());
                    gui.removePageCountChangeHandler(pageCountChangeHandler.get());
                });
            }
            
        }
        
        static class Scroll extends Builder<ScrollGui<?>> {
            
            Scroll() {
                super(ScrollGui.class);
                
                var scrollHandler = new AtomicReference<BiConsumer<Integer, Integer>>();
                var lineCountChangeHandler = new AtomicReference<BiConsumer<Integer, Integer>>();
                
                bindHandler = bindHandler.andThen((item, gui) -> {
                    scrollHandler.set((oldScroll, newScroll) -> item.notifyWindows());
                    lineCountChangeHandler.set((oldCount, newCount) -> item.notifyWindows());
                    
                    gui.addScrollHandler(scrollHandler.get());
                    gui.addLineCountChangeHandler(lineCountChangeHandler.get());
                });
                
                unbindHandler = unbindHandler.andThen((item, gui) -> {
                    gui.removeScrollHandler(scrollHandler.get());
                    gui.removeLineCountChangeHandler(lineCountChangeHandler.get());
                });
            }
            
        }
        
        static class Tab extends Builder<TabGui> {
            
            Tab() {
                super(TabGui.class);
                
                var tabChangeHandler = new AtomicReference<BiConsumer<Integer, Integer>>();
                
                bindHandler = bindHandler.andThen((item, gui) -> {
                    tabChangeHandler.set((oldTab, newTab) -> item.notifyWindows());
                    gui.addTabChangeHandler(tabChangeHandler.get());
                });
                
                unbindHandler = unbindHandler.andThen((item, gui) -> {
                    gui.removeTabChangeHandler(tabChangeHandler.get());
                });
            }
            
        }
        
    }
    
}
