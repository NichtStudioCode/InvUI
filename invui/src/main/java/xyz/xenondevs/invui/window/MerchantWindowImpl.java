package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomMerchantMenu;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.item.Item;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class MerchantWindowImpl extends AbstractSplitWindow<CustomMerchantMenu> implements MerchantWindow {
    
    private static final int TRADE_MAGIC_SLOT = 100;
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    private final List<TradeImpl> trades = new ArrayList<>();
    
    private Supplier<List<? extends Trade>> tradesSupplier;
    private Supplier<Integer> levelSupplier;
    private Supplier<Double> progressSupplier;
    private Supplier<Boolean> restockMessageSupplier;
    
    MerchantWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        Supplier<List<? extends Trade>> tradesSupplier,
        Supplier<Integer> levelSupplier,
        Supplier<Double> progressSupplier,
        Supplier<Boolean> restockMessageSupplier,
        boolean closeable
    ) {
        super(player, title, lowerGui, 3 + 36, new CustomMerchantMenu(player), closeable);
        if (upperGui.getWidth() != 3 || upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper gui must be of dimensions 3x1");
        
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        this.tradesSupplier = tradesSupplier;
        this.levelSupplier = levelSupplier;
        this.progressSupplier = progressSupplier;
        this.restockMessageSupplier = restockMessageSupplier;
        
        menu.setTradeSelectHandler(this::handleTradeSelect);
        initItems();
        updateTrades();
    }
    
    private void handleTradeSelect(int tradeIndex) {
        if (tradeIndex < 0 || tradeIndex >= trades.size())
            return;
        trades.get(tradeIndex).handleClick(getViewer());
    }
    
    @Override
    public void setLevelSupplier(Supplier<Integer> levelSupplier) {
        this.levelSupplier = levelSupplier;
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    @Override
    public void setProgressSupplier(Supplier<Double> progressSupplier) {
        this.progressSupplier = progressSupplier;
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    @Override
    public void setRestockMessageEnabledSupplier(Supplier<Boolean> restockMessageEnabledSupplier) {
        this.restockMessageSupplier = restockMessageEnabledSupplier;
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    @Override
    public void setTradesSupplier(Supplier<List<? extends Trade>> tradesSupplier) {
        this.tradesSupplier = tradesSupplier;
        updateTrades();
    }
    
    @Override
    public List<? extends Trade> getTrades() {
        return Collections.unmodifiableList(trades);
    }
    
    @Override
    public int getLevel() {
        return levelSupplier.get();
    }
    
    @Override
    public double getProgress() {
        return progressSupplier.get();
    }
    
    @Override
    public boolean isRestockMessageEnabled() {
        return restockMessageSupplier.get();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void updateTrades() {
        this.trades.forEach(this::removeTradeViewer);
        this.trades.clear();
        this.trades.addAll((List<TradeImpl>) tradesSupplier.get());
        this.trades.forEach(this::addTradeViewer);
        
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    private void removeTradeViewer(TradeImpl trade) {
        trade.removeViewer(this);
        if (trade.getFirstInput() != null)
            trade.getFirstInput().removeViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getSecondInput() != null)
            trade.getSecondInput().removeViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getOutput() != null)
            trade.getOutput().removeViewer(this, TRADE_MAGIC_SLOT);
    }
    
    private void addTradeViewer(TradeImpl trade) {
        trade.addViewer(this);
        if (trade.getFirstInput() != null)
            trade.getFirstInput().addViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getSecondInput() != null)
            trade.getSecondInput().addViewer(this, TRADE_MAGIC_SLOT);
        if (trade.getOutput() != null)
            trade.getOutput().addViewer(this, TRADE_MAGIC_SLOT);
    }
    
    @Override
    protected void registerAsViewer() {
        super.registerAsViewer();
        this.trades.forEach(this::addTradeViewer);
        
        notifyUpdate(TRADE_MAGIC_SLOT);
    }
    
    @Override
    protected void unregisterAsViewer() {
        super.unregisterAsViewer();
        this.trades.forEach(this::removeTradeViewer);
    }
    
    @Override
    protected void update(int slot) {
        if (slot == TRADE_MAGIC_SLOT) {
            menu.sendTrades(trades, levelSupplier.get(), progressSupplier.get(), restockMessageSupplier.get());
        } else {
            super.update(slot);
        }
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    final static class TradeImpl implements MerchantWindow.Trade {
        
        private final @Nullable AbstractItem firstInput;
        private final @Nullable AbstractItem secondInput;
        private final @Nullable AbstractItem output;
        private final Supplier<Integer> discountSupplier;
        private final Supplier<Boolean> availableSupplier;
        
        private final Set<MerchantWindowImpl> viewers = new HashSet<>();
        
        public TradeImpl(
            @Nullable AbstractItem firstInput,
            @Nullable AbstractItem secondInput,
            @Nullable AbstractItem output,
            Supplier<Integer> discountSupplier,
            Supplier<Boolean> availableSupplier
        ) {
            this.firstInput = firstInput;
            this.secondInput = secondInput;
            this.output = output;
            this.discountSupplier = discountSupplier;
            this.availableSupplier = availableSupplier;
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
            return discountSupplier.get();
        }
        
        @Override
        public boolean isAvailable() {
            return availableSupplier.get();
        }
        
        public void addViewer(MerchantWindowImpl viewer) {
            synchronized (viewers) {
                viewers.add(viewer);
            }
        }
        
        public void removeViewer(MerchantWindowImpl viewer) {
            synchronized (viewers) {
                viewers.remove(viewer);
            }
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
        
        @Override
        public void notifyWindows() {
            synchronized (viewers) {
                for (var viewer : viewers) {
                    viewer.notifyUpdate(TRADE_MAGIC_SLOT);
                }
            }
        }
        
        final static class BuilderImpl implements MerchantWindow.Trade.Builder {
            
            private @Nullable AbstractItem firstInput;
            private @Nullable AbstractItem secondInput;
            private @Nullable AbstractItem output;
            
            private Supplier<Integer> discountSupplier = () -> 0;
            private Supplier<Boolean> availableSupplier = () -> true;
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
            public Builder setDiscount(int discount) {
                this.discountSupplier = () -> discount;
                return this;
            }
            
            @Override
            public Builder setDiscountSupplier(Supplier<Integer> discountSupplier) {
                this.discountSupplier = discountSupplier;
                return this;
            }
            
            @Override
            public Trade.Builder setAvailable(boolean available) {
                this.availableSupplier = () -> available;
                return this;
            }
            
            @Override
            public Trade.Builder setAvailableSupplier(Supplier<Boolean> availableSupplier) {
                this.availableSupplier = availableSupplier;
                return this;
            }
            
            @Override
            public Builder addModifier(Consumer<Trade> modifier) {
                this.modifier = this.modifier.andThen(modifier);
                return this;
            }
            
            @Override
            public Trade build() {
                var trade = new TradeImpl(firstInput, secondInput, output, discountSupplier, availableSupplier);
                modifier.accept(trade);
                return trade;
            }
            
        }
        
    }
    
    final static class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<MerchantWindow, MerchantWindow.Builder>
        implements MerchantWindow.Builder
    {
        
        private Supplier<Gui> upperGuiSupplier = () -> Gui.empty(3, 1);
        private Supplier<List<? extends Trade>> tradesSupplier = List::of;
        private Supplier<Integer> levelSupplier = () -> 0;
        private Supplier<Double> progressSupplier = () -> -1d;
        private Supplier<Boolean> restockMessageSupplier = () -> false;
        
        @Override
        public MerchantWindow.Builder setLevelSupplier(Supplier<Integer> levelSupplier) {
            this.levelSupplier = levelSupplier;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setProgressSupplier(Supplier<Double> progressSupplier) {
            this.progressSupplier = progressSupplier;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setRestockMessageEnabledSupplier(Supplier<Boolean> restockMessageEnabledSupplier) {
            this.restockMessageSupplier = restockMessageEnabledSupplier;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setTradesSupplier(Supplier<List<? extends Trade>> tradesSupplier) {
            this.tradesSupplier = tradesSupplier;
            return this;
        }
        
        @Override
        public MerchantWindow.Builder setUpperGui(Supplier<Gui> guiSupplier) {
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
                tradesSupplier,
                levelSupplier,
                progressSupplier,
                restockMessageSupplier,
                closeable
            );
            applyModifiers(window);
            return window;
        }
        
    }
    
}
