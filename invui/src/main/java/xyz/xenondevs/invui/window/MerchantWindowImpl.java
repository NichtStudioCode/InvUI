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
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.state.MutableProperty;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class MerchantWindowImpl extends AbstractSplitWindow<CustomMerchantMenu> implements MerchantWindow {
    
    private static final int TRADES_REBUILD_MAGIC_SLOT = 100;
    private static final int TRADES_RESEND_MAGIC_SLOT = 101;
    private static final List<? extends Trade> DEFAULT_TRADES = List.of();
    private static final int DEFAULT_LEVEL = 0;
    private static final double DEFAULT_PROGRESS = -1.0;
    private static final boolean DEFAULT_RESTOCK_MESSAGE_ENABLED = false;
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    private final List<TradeImpl> activeTrades = new ArrayList<>();
    
    private final MutableProperty<? super List<? extends Trade>> trades;
    private final MutableProperty<Integer> level;
    private final MutableProperty<Double> progress;
    private final MutableProperty<Boolean> restockMessage;
    
    private final List<BiConsumer<? super Integer, ? super Integer>> tradeSelectHandlers = new ArrayList<>();
    private int previousSelectedTrade = -1;
    
    MerchantWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        MutableProperty<? super List<? extends Trade>> trades,
        MutableProperty<Integer> level,
        MutableProperty<Double> progress,
        MutableProperty<Boolean> restockMessage,
        MutableProperty<Boolean> closeable
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
        
        trades.observeWeak(this, thisRef -> thisRef.notifyUpdate(TRADES_REBUILD_MAGIC_SLOT));
        level.observeWeak(this, thisRef -> thisRef.notifyUpdate(TRADES_RESEND_MAGIC_SLOT));
        progress.observeWeak(this, thisRef -> thisRef.notifyUpdate(TRADES_RESEND_MAGIC_SLOT));
        restockMessage.observeWeak(this, thisRef -> thisRef.notifyUpdate(TRADES_RESEND_MAGIC_SLOT));
        
        menu.setTradeSelectHandler(this::handleTradeSelect);
        rebuildTrades();
    }
    
    private void handleTradeSelect(int tradeIndex) {
        if (tradeIndex < 0 || tradeIndex >= activeTrades.size())
            return;
        activeTrades.get(tradeIndex).handleClick(getViewer());
        
        if (tradeIndex != previousSelectedTrade) {
            CollectionUtils.forEachCatching(
                tradeSelectHandlers,
                handler -> handler.accept(previousSelectedTrade, tradeIndex),
                "Failed to handle trade selection change from " + previousSelectedTrade + " to " + tradeIndex
            );
            previousSelectedTrade = tradeIndex;
        }
    }
    
    @Override
    public void setLevel(int level) {
        this.level.set(level);
    }
    
    @Override
    public void setProgress(double progress) {
        this.progress.set(progress);
    }
    
    @Override
    public void setRestockMessageEnabled(boolean enabled) {
        this.restockMessage.set(enabled);
    }
    
    @Override
    public void setTrades(List<? extends Trade> trades) {
        this.trades.set(trades);
    }
    
    @Override
    public @UnmodifiableView List<Trade> getTrades() {
        return Collections.unmodifiableList(activeTrades);
    }
    
    @Override
    public int getLevel() {
        return FuncUtils.getSafely(level, DEFAULT_LEVEL);
    }
    
    @Override
    public double getProgress() {
        return FuncUtils.getSafely(progress, DEFAULT_PROGRESS);
    }
    
    @Override
    public boolean isRestockMessageEnabled() {
        return FuncUtils.getSafely(restockMessage, DEFAULT_RESTOCK_MESSAGE_ENABLED);
    }
    
    @Override
    protected void registerAsViewer() {
        super.registerAsViewer();
        this.activeTrades.forEach(this::addTradeViewer);
        
        notifyUpdate(TRADES_RESEND_MAGIC_SLOT);
    }
    
    @Override
    protected void unregisterAsViewer() {
        super.unregisterAsViewer();
        this.activeTrades.forEach(this::removeTradeViewer);
    }
    
    @Override
    protected void update(int slot) {
        switch (slot) {
            case TRADES_REBUILD_MAGIC_SLOT -> rebuildTrades();
            case TRADES_RESEND_MAGIC_SLOT -> 
                menu.sendTrades(activeTrades, getLevel(), getProgress(), isRestockMessageEnabled());
            default -> super.update(slot);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void rebuildTrades() {
        this.activeTrades.forEach(this::removeTradeViewer);
        this.activeTrades.clear();
        this.activeTrades.addAll((List<? extends TradeImpl>) FuncUtils.getSafely(trades, DEFAULT_TRADES));
        this.activeTrades.forEach(this::addTradeViewer);
    }
    
    private void removeTradeViewer(TradeImpl trade) {
        if (trade.getFirstInput() != null)
            trade.getFirstInput().removeViewer(this, TRADES_RESEND_MAGIC_SLOT);
        if (trade.getSecondInput() != null)
            trade.getSecondInput().removeViewer(this, TRADES_RESEND_MAGIC_SLOT);
        if (trade.getOutput() != null)
            trade.getOutput().removeViewer(this, TRADES_RESEND_MAGIC_SLOT);
        trade.getDiscountProperty().unobserveWeak(this);
        trade.getAvailableProperty().unobserveWeak(this);
    }
    
    private void addTradeViewer(TradeImpl trade) {
        if (trade.getFirstInput() != null)
            trade.getFirstInput().addViewer(this, TRADES_RESEND_MAGIC_SLOT);
        if (trade.getSecondInput() != null)
            trade.getSecondInput().addViewer(this, TRADES_RESEND_MAGIC_SLOT);
        if (trade.getOutput() != null)
            trade.getOutput().addViewer(this, TRADES_RESEND_MAGIC_SLOT);
        trade.getDiscountProperty().observeWeak(this, thisRef -> thisRef.notifyUpdate(TRADES_RESEND_MAGIC_SLOT));
        trade.getAvailableProperty().observeWeak(this, thisRef -> thisRef.notifyUpdate(TRADES_RESEND_MAGIC_SLOT));
    }
    
    @Override
    public void addTradeSelectHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        tradeSelectHandlers.add(handler);
    }
    
    @Override
    public void removeTradeSelectHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        tradeSelectHandlers.remove(handler);
    }
    
    @Override
    public void setTradeSelectHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
        tradeSelectHandlers.clear();
        tradeSelectHandlers.addAll(handlers);
    }
    
    @Override
    public @UnmodifiableView List<BiConsumer<Integer, Integer>> getTradeSelectHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(tradeSelectHandlers);
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    final static class TradeImpl implements MerchantWindow.Trade {
        
        private static final int DEFAULT_DISCOUNT = 0;
        private static final boolean DEFAULT_AVAILABLE = true;
        
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
            return FuncUtils.getSafely(discount, DEFAULT_DISCOUNT);
        }
        
        @Override
        public boolean isAvailable() {
            return FuncUtils.getSafely(available, DEFAULT_AVAILABLE);
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
            
            private Property<? extends Integer> discount = Property.of(DEFAULT_DISCOUNT);
            private Property<? extends Boolean> available = Property.of(DEFAULT_AVAILABLE);
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
        
        private final List<BiConsumer<? super Integer, ? super Integer>> tradeSelectHandlers = new ArrayList<>();
        private Supplier<? extends Gui> upperGuiSupplier = () -> Gui.empty(3, 1);
        private MutableProperty<List<? extends Trade>> trades = MutableProperty.of(DEFAULT_TRADES);
        private MutableProperty<Integer> level = MutableProperty.of(DEFAULT_LEVEL);
        private MutableProperty<Double> progress = MutableProperty.of(DEFAULT_PROGRESS);
        private MutableProperty<Boolean> restockMessageEnabled = MutableProperty.of(DEFAULT_RESTOCK_MESSAGE_ENABLED);
        
        @Override
        public MerchantWindow.Builder setLevel(MutableProperty<Integer> level) {
            this.level = level;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setProgress(MutableProperty<Double> progress) {
            this.progress = progress;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setRestockMessageEnabled(MutableProperty<Boolean> enabled) {
            this.restockMessageEnabled = enabled;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setTrades(MutableProperty<List<? extends Trade>> trades) {
            this.trades = trades;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setUpperGui(Supplier<? extends Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder addTradeSelectHandler(BiConsumer<? super Integer, ? super Integer> handler) {
            this.tradeSelectHandlers.add(handler);
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setTradeSelectHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
            this.tradeSelectHandlers.clear();
            this.tradeSelectHandlers.addAll(handlers);
            return this;
        }
        
        @SuppressWarnings({"unchecked", "rawtypes"})
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
            
            window.setTradeSelectHandlers((List) tradeSelectHandlers);
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
