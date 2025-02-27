package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class CustomItem extends AbstractItem {
    
    private final BiConsumer<Item, Click> clickHandler;
    private volatile Function<Player, ItemProvider> itemProvider;
    private final long updatePeriod;
    private @Nullable BukkitTask updateTask;
    
    public CustomItem(BiConsumer<Item, Click> clickHandler, Function<Player, ItemProvider> itemProvider, long updatePeriod) {
        this.clickHandler = clickHandler;
        this.itemProvider = itemProvider;
        this.updatePeriod = updatePeriod;
    }
    
    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return itemProvider.apply(viewer);
    }
    
    @Override
    public void handleClick(ClickType clickType, Player player, Click click) {
        clickHandler.accept(this, click);
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
    
    static final class Builder implements Item.Builder<Builder> {
        
        private BiConsumer<Item, Click> clickHandler = (item, click) -> {};
        private @Nullable Function<Player, ItemProvider> itemProviderFn;
        private @Nullable ItemProvider asyncPlaceholder;
        private @Nullable Supplier<ItemProvider> asyncSupplier;
        private @Nullable CompletableFuture<ItemProvider> asyncFuture;
        private Consumer<Item> modifier = item -> {};
        private boolean updateOnClick;
        private long updatePeriod = -1L;
        
        @Override
        public Builder setItemProvider(ItemProvider itemProvider) {
            this.itemProviderFn = viewer -> itemProvider;
            return this;
        }
        
        @Override
        public Builder setItemProvider(Function<Player, ItemProvider> itemProvider) {
            this.itemProviderFn = itemProvider;
            return this;
        }
        
        @Override
        public Builder setCyclingItemProvider(int period, List<? extends ItemProvider> itemProviders) {
            if (itemProviders.isEmpty())
                throw new IllegalArgumentException("itemProviders must not be empty");
            
            if (itemProviders.size() > 1) {
                updatePeriodically(period);
                this.itemProviderFn = viewer -> {
                    int i = (Bukkit.getCurrentTick() / period) % itemProviders.size();
                    return itemProviders.get(i);
                };
            } else {
                setItemProvider(itemProviders.getFirst());
            }
            return this;
        }
        
        @Override
        public Builder async(ItemProvider placeholder, Supplier<ItemProvider> itemProviderSupplier) {
            this.asyncPlaceholder = placeholder;
            this.asyncSupplier = itemProviderSupplier;
            return this;
        }
        
        @Override
        public Builder async(ItemProvider placeholder, CompletableFuture<ItemProvider> itemProviderFuture) {
            this.asyncPlaceholder = placeholder;
            this.asyncFuture = itemProviderFuture;
            return this;
        }
        
        @Override
        public Builder updatePeriodically(long period) {
            this.updatePeriod = period;
            return this;
        }
        
        @Override
        public Builder updateOnClick() {
            this.updateOnClick = true;
            return this;
        }
        
        @Override
        public Builder addClickHandler(BiConsumer<Item, Click> clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }
        
        @Override
        public Builder addModifier(Consumer<Item> modifier) {
            this.modifier = this.modifier.andThen(modifier);
            return this;
        }
        
        @Override
        public Item build() {
            if (updateOnClick) {
                clickHandler = clickHandler.andThen((item, click) -> item.notifyWindows());
            }
            
            CustomItem customItem;
            if (asyncPlaceholder != null) {
                customItem = new CustomItem(
                    clickHandler,
                    viewer -> asyncPlaceholder,
                    updatePeriod
                );
                
                if (asyncSupplier != null) {
                    Bukkit.getScheduler().runTaskAsynchronously(
                        InvUI.getInstance().getPlugin(),
                        () -> {
                            var itemProvider = asyncSupplier.get();
                            customItem.itemProvider = (viewer -> itemProvider);
                            customItem.notifyWindows();
                        }
                    );
                } else if (asyncFuture != null) {
                    asyncFuture.thenAccept(itemProvider -> {
                        customItem.itemProvider = (viewer -> itemProvider);
                        customItem.notifyWindows();
                    });
                }
            } else {
                customItem = new CustomItem(
                    clickHandler,
                    itemProviderFn != null ? itemProviderFn : viewer -> ItemProvider.EMPTY,
                    updatePeriod
                );
            }
            
            modifier.accept(customItem);
            return customItem;
        }
        
    }
    
}
