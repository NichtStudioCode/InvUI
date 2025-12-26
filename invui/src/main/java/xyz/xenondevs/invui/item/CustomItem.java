package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.util.TriConsumer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class CustomItem extends AbstractItem {
    
    private final BiConsumer<? super Item, ? super Click> clickHandler;
    private final TriConsumer<? super Item, ? super Player, ? super Integer> selectHandler;
    private volatile Function<? super Player, ? extends ItemProvider> itemProvider;
    private final int updatePeriod;
    
    public CustomItem(
        BiConsumer<? super Item, ? super Click> clickHandler,
        TriConsumer<? super Item, ? super Player, ? super Integer> selectHandler,
        Function<? super Player, ? extends ItemProvider> itemProvider,
        int updatePeriod
    ) {
        this.clickHandler = clickHandler;
        this.selectHandler = selectHandler;
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
    public void handleBundleSelect(Player player, int bundleSlot) {
        selectHandler.accept(this, player, bundleSlot);
    }
    
    @Override
    public int getUpdatePeriod(int what) {
        return updatePeriod;
    }
    
    static final class Builder implements Item.Builder<Builder> {
        
        private BiConsumer<Item, Click> clickHandler = (item, click) -> {};
        private TriConsumer<Item, Player, Integer> selectHandler = (item, player, slot) -> {};
        private @Nullable Function<? super Player, ? extends ItemProvider> itemProviderFn;
        private @Nullable ItemProvider asyncPlaceholder;
        private @Nullable Supplier<? extends ItemProvider> asyncSupplier;
        private @Nullable CompletableFuture<? extends ItemProvider> asyncFuture;
        private Consumer<Item> modifier = item -> {};
        private boolean updateOnClick;
        private int updatePeriod = -1;
        
        @Override
        public Builder setItemProvider(ItemProvider itemProvider) {
            this.itemProviderFn = viewer -> itemProvider;
            return this;
        }
        
        @Override
        public Builder setItemProvider(Function<? super Player, ? extends ItemProvider> itemProvider) {
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
        public Builder async(ItemProvider placeholder, Supplier<? extends ItemProvider> itemProviderSupplier) {
            this.asyncPlaceholder = placeholder;
            this.asyncSupplier = itemProviderSupplier;
            return this;
        }
        
        @Override
        public Builder async(ItemProvider placeholder, CompletableFuture<? extends ItemProvider> itemProviderFuture) {
            this.asyncPlaceholder = placeholder;
            this.asyncFuture = itemProviderFuture;
            return this;
        }
        
        @Override
        public Builder updatePeriodically(int period) {
            this.updatePeriod = period;
            return this;
        }
        
        @Override
        public Builder updateOnClick() {
            this.updateOnClick = true;
            return this;
        }
        
        @Override
        public Builder addClickHandler(BiConsumer<? super Item, ? super Click> clickHandler) {
            this.clickHandler = this.clickHandler.andThen(clickHandler);
            return this;
        }
        
        @Override
        public Builder addBundleSelectHandler(TriConsumer<? super Item, ? super Player, ? super Integer> selectHandler) {
            this.selectHandler = this.selectHandler.andThen(selectHandler);
            return this;
        }
        
        @Override
        public Builder addModifier(Consumer<? super Item> modifier) {
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
                    selectHandler,
                    viewer -> asyncPlaceholder,
                    updatePeriod
                );
                
                if (asyncSupplier != null) {
                    Bukkit.getAsyncScheduler().runNow(
                        InvUI.getInstance().getPlugin(),
                        x -> {
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
                    selectHandler,
                    itemProviderFn != null ? itemProviderFn : viewer -> ItemProvider.EMPTY,
                    updatePeriod
                );
            }
            
            modifier.accept(customItem);
            return customItem;
        }
        
    }
    
}
