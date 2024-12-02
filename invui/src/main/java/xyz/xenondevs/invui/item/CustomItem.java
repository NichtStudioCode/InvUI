package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class CustomItem extends AbstractItem {
    
    private final BiConsumer<Item, Click> clickHandler;
    private Function<Player, ItemProvider> itemProvider;
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
    
    static final class Builder implements Item.Builder<Builder> {
        
        private BiConsumer<Item, Click> clickHandler = (item, click) -> {};
        private @Nullable Function<Player, ItemProvider> itemProviderFn;
        private @Nullable ItemProvider asyncPlaceholder;
        private Consumer<Item> modifier = item -> {};
        private boolean updateOnClick;
        private long updatePeriod = -1L;
        
        @Override
        public Builder itemProvider(ItemProvider itemProvider) {
            this.itemProviderFn = viewer -> itemProvider;
            return this;
        }
        
        @Override
        public Builder itemProvider(Supplier<ItemProvider> itemProvider) {
            this.itemProviderFn = viewer -> itemProvider.get();
            return this;
        }
        
        @Override
        public Builder itemProvider(Function<Player, ItemProvider> itemProvider) {
            this.itemProviderFn = itemProvider;
            return this;
        }
        
        @Override
        public Builder async(ItemProvider placeholder) {
            this.asyncPlaceholder = placeholder;
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
        public Builder click(BiConsumer<Item, Click> clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }
        
        @Override
        public Builder modify(Consumer<Item> modifier) {
            this.modifier = this.modifier.andThen(modifier);
            return this;
        }
        
        @Override
        public Item build() {
            if (updateOnClick) {
                clickHandler = clickHandler.andThen((item, click) -> item.notifyWindows());
            }
            
            CustomItem customItem;
            if (asyncPlaceholder != null && itemProviderFn != null) {
                customItem = new CustomItem(
                    clickHandler,
                    viewer -> asyncPlaceholder,
                    updatePeriod
                );
                
                Bukkit.getScheduler().runTaskAsynchronously(
                    InvUI.getInstance().getPlugin(),
                    () -> customItem.itemProvider = (viewer -> itemProviderFn.apply(viewer))
                );
                
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
