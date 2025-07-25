package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomMerchantMenu;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class MerchantWindowImpl extends AbstractSplitWindow<CustomMerchantMenu> implements MerchantWindow {
    
    private static final int TRADE_MAGIC_SLOT = 100; // magic slot number that, when notified, updates trades
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    private final List<TradeImpl> lastKnownTrades = new ArrayList<>();
    
    private Property<? extends List<? extends Trade>> trades;
    private Property<? extends Integer> level;
    private Property<? extends Double> progress;
    private Property<? extends Boolean> restockMessage;
    
    MerchantWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        Property<? extends List<? extends Trade>> trades,
        Property<? extends Integer> level,
        Property<? extends Double> progress,
        Property<? extends Boolean> restockMessage,
        Property<? extends Boolean> closeable
    ) {
        super(player, title, lowerGui, 3 + 36, new CustomMerchantMenu(player), closeable);
        if (upperGui.getWidth() != 3 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper gui must be of dimensions 3x1");
        
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        this.trades = trades;
        this.level = level;
        this.progress = progress;
        this.restockMessage = restockMessage;
        
        trades.observeWeak(this, MerchantWindowImpl::updateTrades);
        this.level.observeWeak(this, MerchantWindowImpl::updateTrades);
        progress.observeWeak(this, MerchantWindowImpl::updateTrades);
        restockMessage.observeWeak(this, MerchantWindowImpl::updateTrades);
        
        menu.setTradeSelectHandler(this::handleTradeSelect);
        updateTrades();
    }
    
    private void handleTradeSelect(int tradeIndex) {
        if (tradeIndex < 0 || tradeIndex >= lastKnownTrades.size())
            return;
        lastKnownTrades.get(tradeIndex).handleClick(getViewer());
    }
    
    @Override
    public void setLevel(int level) {
        this.level.unobserveWeak(this);
        this.level = Property.of(level);
        updateTrades();
    }
    
    @Override
    public void setProgress(double progress) {
        this.progress.unobserveWeak(this);
        this.progress = Property.of(progress);
        updateTrades();
    }
    
    @Override
    public void setRestockMessageEnabled(boolean enabled) {
        this.restockMessage.unobserveWeak(this);
        this.restockMessage = Property.of(enabled);
        updateTrades();
    }
    
    @Override
    public void setTrades(List<? extends Trade> trades) {
        this.trades.unobserveWeak(this);
        this.trades = Property.of(trades);
        updateTrades();
    }
    
    @Override
    public @UnmodifiableView List<Trade> getTrades() {
        return Collections.unmodifiableList(lastKnownTrades);
    }
    
    @Override
    public int getLevel() {
        return level.get();
    }
    
    @Override
    public double getProgress() {
        return progress.get();
    }
    
    @Override
    public boolean isRestockMessageEnabled() {
        return restockMessage.get();
    }
    
    @SuppressWarnings("unchecked")
    private void updateTrades() {
        this.lastKnownTrades.forEach(this::removeTradeViewer);
        this.lastKnownTrades.clear();
        this.lastKnownTrades.addAll((List<TradeImpl>) trades.get());
        this.lastKnownTrades.forEach(this::addTradeViewer);
        
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    private void removeTradeViewer(TradeImpl trade) {
        if (trade.getFirstInput() != null)
            trade.getFirstInput().removeViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getSecondInput() != null)
            trade.getSecondInput().removeViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getOutput() != null)
            trade.getOutput().removeViewer(this, TRADE_MAGIC_SLOT);
        trade.getDiscountProperty().unobserveWeak(this);
        trade.getAvailableProperty().unobserveWeak(this);
    }
    
    private void addTradeViewer(TradeImpl trade) {
        if (trade.getFirstInput() != null)
            trade.getFirstInput().addViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getSecondInput() != null)
            trade.getSecondInput().addViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getOutput() != null)
            trade.getOutput().addViewer(this, TRADE_MAGIC_SLOT);
        trade.getDiscountProperty().observeWeak(this, MerchantWindowImpl::updateTrades);
        trade.getAvailableProperty().observeWeak(this, MerchantWindowImpl::updateTrades);
    }
    
    @Override
    protected void registerAsViewer() {
        super.registerAsViewer();
        this.lastKnownTrades.forEach(this::addTradeViewer);
        
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    @Override
    protected void unregisterAsViewer() {
        super.unregisterAsViewer();
        this.lastKnownTrades.forEach(this::removeTradeViewer);
    }
    
    @Override
    protected void update(int slot) {
        if (slot == TRADE_MAGIC_SLOT) {
            menu.sendTrades(lastKnownTrades, level.get(), progress.get(), restockMessage.get());
        } else {
            super.update(slot);
        }
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    final static class TradeImpl implements MerchantWindow.Trade {
        
        private final @Nullable AbstractItem firstInput;
        private final @Nullable AbstractItem secondInput;
        private final @Nullable AbstractItem output;
        private final Property<? extends Integer> discount;
        private final Property<? extends Boolean> available;
        
        public TradeImpl(
            @Nullable AbstractItem firstInput,
            @Nullable AbstractItem secondInput,
            @Nullable AbstractItem output,
            Property<? extends Integer> discount,
            Property<? extends Boolean> available
        ) {
            this.firstInput = firstInput;
            this.secondInput = secondInput;
            this.output = output;
            this.discount = discount;
            this.available = available;
        }
        
        @Override
        public @Nullable AbstractItem getFirstInput() {
            return firstInput;
        }
        
        @Override
        public @Nullable AbstractItem getSecondInput() {
            return secondInput;
        }
        
        @Override
        public @Nullable AbstractItem getOutput() {
            return output;
        }
        
        @Override
        public int getDiscount() {
            return discount.get();
        }
        
        @Override
        public boolean isAvailable() {
            return available.get();
        }
        
        public Property<? extends Integer> getDiscountProperty() {
            return discount;
        }
        
        public Property<? extends Boolean> getAvailableProperty() {
            return available;
        }
        
        public void handleClick(Player player) {
            var click = new Click(player, ClickType.LEFT);
            if (firstInput != null)
                firstInput.handleClick(ClickType.LEFT, player, click);
            if (secondInput != null)
                secondInput.handleClick(ClickType.LEFT, player, click);
            if (output != null)
                output.handleClick(ClickType.LEFT, player, click);
        }
        
        final static class BuilderImpl implements MerchantWindow.Trade.Builder {
            
            private @Nullable AbstractItem firstInput;
            private @Nullable AbstractItem secondInput;
            private @Nullable AbstractItem output;
            
            private Property<? extends Integer> discount = Property.of(0);
            private Property<? extends Boolean> available = Property.of(true);
            private Consumer<Trade> modifier = trade -> {};
            
            @Override
            public Trade.Builder setFirstInput(Item item) {
                this.firstInput = (AbstractItem) item;
                return this;
            }
            
            @Override
            public Trade.Builder setSecondInput(Item item) {
                this.secondInput = (AbstractItem) item;
                return this;
            }
            
            @Override
            public Trade.Builder setResult(Item item) {
                this.output = (AbstractItem) item;
                return this;
            }
            
            @Override
            public Builder setDiscount(Property<? extends Integer> discount) {
                this.discount = discount;
                return this;
            }
            
            @Override
            public Builder setAvailable(Property<? extends Boolean> available) {
                this.available = available;
                return this;
            }
            
            @Override
            public Builder addModifier(Consumer<? super Trade> modifier) {
                this.modifier = this.modifier.andThen(modifier);
                return this;
            }
            
            @Override
            public Trade build() {
                var trade = new TradeImpl(firstInput, secondInput, output, discount, available);
                modifier.accept(trade);
                return trade;
            }
            
        }
        
    }
    
    final static class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<MerchantWindow, MerchantWindow.Builder>
        implements MerchantWindow.Builder
    {
        
        private Supplier<? extends Gui> upperGuiSupplier = () -> Gui.empty(3, 1);
        private Property<? extends List<? extends Trade>> trades = Property.of(List.of());
        private Property<? extends Integer> level = Property.of(0);
        private Property<? extends Double> progress = Property.of(-1d);
        private Property<? extends Boolean> restockMessageEnabled = Property.of(false);
        
        @Override
        public MerchantWindow.Builder setLevel(Property<? extends Integer> level) {
            this.level = level;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setProgress(Property<? extends Double> progress) {
            this.progress = progress;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setRestockMessageEnabled(Property<? extends Boolean> enabled) {
            this.restockMessageEnabled = enabled;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setTrades(Property<? extends List<? extends Trade>> trades) {
            this.trades = trades;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setUpperGui(Supplier<? extends Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public MerchantWindow build(Player viewer) {
            var window = new MerchantWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                trades,
                level,
                progress,
                restockMessageEnabled,
                closeable
            );
            applyModifiers(window);
            return window;
        }
        
    }
    
}
