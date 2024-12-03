package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.util.TriConsumer;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.function.*;

class CustomBoundItem<G extends Gui> extends AbstractBoundItem {
    
    private final TriConsumer<Item, G, Click> clickHandler;
    private BiFunction<Player, G, ItemProvider> itemProvider;
    private final BiConsumer<Item, G> bindHandler;
    private final long updatePeriod;
    private @Nullable BukkitTask updateTask;
    
    public CustomBoundItem(BiConsumer<Item, G> bindHandler, TriConsumer<Item, G, Click> clickHandler, BiFunction<Player, G, ItemProvider> itemProvider, long updatePeriod) {
        this.bindHandler = bindHandler;
        this.clickHandler = clickHandler;
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
    public void addViewer(AbstractWindow who, int how) {
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
    public void removeViewer(AbstractWindow who, int how) {
        super.removeViewer(who, how);
        if (updateTask != null && viewers.isEmpty()) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    non-sealed static class Builder<G extends Gui> implements BoundItem.Builder<G> {
        
        protected BiConsumer<Item, G> bindHandler = (item, gui) -> {};
        private TriConsumer<Item, G, Click> clickHandler = (item, gui, click) -> {};
        private @Nullable BiFunction<Player, G, ItemProvider> itemProviderFn;
        private @Nullable ItemProvider asyncPlaceholder;
        private Consumer<Item> modifier = item -> {};
        private boolean updateOnClick;
        private long updatePeriod = -1L;
        
        @Override
        public Builder<G> setItemProvider(ItemProvider itemProvider) {
            this.itemProviderFn = (viewer, gui) -> itemProvider;
            return this;
        }
        
        @Override
        public Builder<G> setItemProvider(Function<Player, ItemProvider> itemProvider) {
            this.itemProviderFn = (viewer, gui) -> itemProvider.apply(viewer);
            return this;
        }
        
        @Override
        public Builder<G> setItemProvider(BiFunction<Player, G, ItemProvider> itemProvider) {
            this.itemProviderFn = itemProvider;
            return this;
        }
        
        @Override
        public Builder<G> async(ItemProvider placeholder) {
            this.asyncPlaceholder = placeholder;
            return this;
        }
        
        @Override
        public Builder<G> updatePeriodically(long period) {
            this.updatePeriod = period;
            return this;
        }
        
        @Override
        public Builder<G> updateOnClick() {
            this.updateOnClick = true;
            return this;
        }
        
        @Override
        public Builder<G> addClickHandler(BiConsumer<Item, Click> clickHandler) {
            this.clickHandler.andThen((item, gui, click) -> clickHandler.accept(item, click));
            return this;
        }
        
        @Override
        public Builder<G> addClickHandler(TriConsumer<Item, G, Click> handler) {
            clickHandler = clickHandler.andThen(handler);
            return this;
        }
        
        @Override
        public Builder<G> addBindHandler(BiConsumer<Item, G> handler) {
            bindHandler = bindHandler.andThen(handler);
            return this;
        }
        
        @Override
        public Builder<G> addModifier(Consumer<Item> modifier) {
            this.modifier = this.modifier.andThen(modifier);
            return this;
        }
        
        @Override
        public Item build() {
            if (updateOnClick) {
                clickHandler = clickHandler.andThen((item, gui, click) -> item.notifyWindows());
            }
            
            CustomBoundItem<G> customItem;
            if (asyncPlaceholder != null && itemProviderFn != null) {
                customItem = new CustomBoundItem<>(
                    bindHandler,
                    clickHandler,
                    itemProviderFn,
                    updatePeriod
                );
                
                Bukkit.getScheduler().runTaskAsynchronously(
                    InvUI.getInstance().getPlugin(),
                    () -> customItem.itemProvider = (viewer, gui) -> itemProviderFn.apply(viewer, gui)
                );
                
            } else {
                customItem = new CustomBoundItem<>(
                    bindHandler,
                    clickHandler,
                    itemProviderFn != null ? itemProviderFn : (viewer, gui) -> ItemProvider.EMPTY,
                    updatePeriod
                );
            }
            
            modifier.accept(customItem);
            
            return customItem;
        }
        
        static class Normal extends Builder<Gui> {
        }
        
        static class Paged extends Builder<PagedGui<?>> {
            
            
            Paged() {
                bindHandler = bindHandler.andThen((item, gui) -> {
                    //noinspection ConstantValue // the generic type is unchecked otherwise
                    if (!(gui instanceof PagedGui<?>))
                        throw new IllegalArgumentException("Item can only be bound to PagedGui");
                    
                    gui.addPageChangeHandler((oldPage, newPage) -> item.notifyWindows());
                    gui.addPageCountChangeHandler((oldCount, newCount) -> item.notifyWindows());
                });
            }
            
        }
        
        static class Scroll extends Builder<ScrollGui<?>> {
            
            Scroll() {
                bindHandler = bindHandler.andThen((item, gui) -> {
                    //noinspection ConstantValue // the generic type is unchecked otherwise
                    if (!(gui instanceof ScrollGui<?>))
                        throw new IllegalArgumentException("Item can only be bound to ScrollGui");
                    
                    gui.addScrollHandler((oldScroll, newScroll) -> item.notifyWindows());
                    gui.addLineCountChangeHandler((oldCount, newCount) -> item.notifyWindows());
                });
            }
            
        }
        
        static class Tab extends Builder<TabGui> {
            
            Tab() {
                bindHandler = bindHandler.andThen((item, gui) -> {
                    //noinspection ConstantValue // the generic type is unchecked otherwise
                    if (!(gui instanceof TabGui))
                        throw new IllegalArgumentException("Item can only be bound to TabGui");
                    
                    gui.addTabChangeHandler((oldTab, newTab) -> item.notifyWindows());
                });
            }
            
        }
        
    }
    
}
