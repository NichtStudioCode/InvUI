package xyz.xenondevs.invui.gui;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.internal.util.MathUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

final class AnimationImpl implements Animation {
    
    private final int tickDelay;
    private final BiPredicate<? super Gui, ? super Slot> slotFilter;
    private final Function<? super State, ? extends Set<? extends Slot>> slotSelector;
    private final Function<? super Slot, ? extends @Nullable SlotElement> intermediaryGenerator;
    private final BiConsumer<? super State, ? super Set<? extends Slot>> showHandler;
    private final Consumer<? super State> finishHandler;
    
    public AnimationImpl(
        int tickDelay,
        BiPredicate<? super Gui, ? super Slot> slotFilter,
        Function<? super State, ? extends Set<? extends Slot>> slotSelector,
        Function<? super Slot, ? extends @Nullable SlotElement> intermediaryGenerator,
        BiConsumer<? super State, ? super Set<? extends Slot>> showHandler,
        Consumer<? super State> finishHandler
    ) {
        this.tickDelay = tickDelay;
        this.slotFilter = slotFilter;
        this.slotSelector = slotSelector;
        this.intermediaryGenerator = intermediaryGenerator;
        this.showHandler = showHandler;
        this.finishHandler = finishHandler;
    }
    
    final class StateImpl implements State {
        
        private final Consumer<? super Set<? extends Slot>> extraShowHandler;
        private final Runnable extraFinishHandler;
        private final Set<Slot> remainingSlots = new HashSet<>();
        private final Gui gui;
        
        private @Nullable BukkitTask task;
        private int currentFrame;
        
        public StateImpl(
            Gui gui,
            Consumer<? super Set<? extends Slot>> extraShowHandler,
            Runnable extraFinishHandler
        ) {
            this.gui = gui;
            this.extraShowHandler = extraShowHandler;
            this.extraFinishHandler = extraFinishHandler;
            
            // populate remaining slots
            for (int x = 0; x < gui.getWidth(); x++) {
                for (int y = 0; y < gui.getHeight(); y++) {
                    if (!slotFilter.test(gui, new Slot(x, y)))
                        continue;
                    remainingSlots.add(new Slot(x, y));
                }
            }
        }
        
        /**
         * Starts the animation task timer.
         */
        public void start() {
            if (isFinished())
                throw new IllegalStateException("Animation is already completed");
            if (task != null)
                throw new IllegalStateException("Animation is already running");
            
            task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::handleTick, 0, tickDelay);
        }
        
        private void handleTick() {
            var nextSlots = slotSelector.apply(this);
            remainingSlots.removeAll(nextSlots);
            showHandler.accept(this, nextSlots);
            extraShowHandler.accept(nextSlots);
            
            currentFrame++;
            
            if (remainingSlots.isEmpty())
                cancel();
        }
        
        public void cancel() {
            if (task != null) {
                task.cancel();
                finishHandler.accept(this);
                extraFinishHandler.run();
            }
        }
        
        public @Nullable SlotElement getIntermediarySlotElement(Slot slot) {
            return intermediaryGenerator.apply(slot);
        }
        
        @Override
        public Gui getGui() {
            return gui;
        }
        
        @Override
        public int getFrame() {
            return currentFrame;
        }
        
        @Override
        public Set<Slot> getRemainingSlots() {
            return Collections.unmodifiableSet(remainingSlots);
        }
        
        @Override
        public boolean isFinished() {
            return remainingSlots.isEmpty();
        }
    
    }
    
    static final class BuilderImpl implements Animation.Builder {
        
        private int tickDelay = 1;
        private BiPredicate<Gui, Slot> slotFilter = (gui, slot) -> true;
        private @Nullable Function<? super State, ? extends Set<? extends Slot>> slotSelector;
        private Function<? super Slot, ? extends @Nullable SlotElement> intermediaryGenerator = slot -> null;
        private BiConsumer<State, Set<? extends Slot>> showHandler = (state, slot) -> {};
        private Consumer<State> finishHandler = gui -> {};
        
        @Override
        public Builder setTickDelay(int tickDelay) {
            this.tickDelay = tickDelay;
            return this;
        }
        
        @Override
        public Builder setSlotSelector(Function<? super State, ? extends Set<? extends Slot>> selector) {
            this.slotSelector = selector;
            return this;
        }
        
        @Override
        public Builder setIntermediaryElementGenerator(Function<? super Slot, ? extends @Nullable SlotElement> intermediaryGenerator) {
            this.intermediaryGenerator = intermediaryGenerator;
            return this;
        }
        
        @Override
        public Builder addShowHandler(BiConsumer<? super State, ? super Set<? extends Slot>> showHandler) {
            this.showHandler = this.showHandler.andThen(showHandler);
            return this;
        }
        
        @Override
        public Builder addFinishHandler(Consumer<? super State> finishHandler) {
            this.finishHandler = this.finishHandler.andThen(finishHandler);
            return this;
        }
        
        @Override
        public Builder addSlotFilter(BiPredicate<? super Gui, ? super Slot> filter) {
            this.slotFilter = this.slotFilter.and(filter);
            return this;
        }
        
        @Override
        public Animation build() {
            if (slotSelector == null)
                throw new IllegalStateException("SlotSelector needs to be set");
            
            return new AnimationImpl(tickDelay, slotFilter, slotSelector, intermediaryGenerator, showHandler, finishHandler);
        }
        
    }
    
    static final class ColumnSlotSelector implements Function<State, Set<Slot>> {
        
        private int column;
        
        @Override
        public Set<Slot> apply(State state) {
            int width = state.getGui().getWidth();
            int height = state.getGui().getHeight();
            
            var slots = new HashSet<Slot>();
            
            while (slots.isEmpty() && column < width) {
                for (int y = 0; y < height; y++) {
                    var slot = new Slot(column, y);
                    if (state.getRemainingSlots().contains(slot)) {
                        slots.add(slot);
                    }
                }
                
                column++;
            }
            
            return slots;
        }
        
    }
    
    static final class RowSlotSelector implements Function<State, Set<Slot>> {
        
        private int row;
        
        @Override
        public Set<Slot> apply(State state) {
            int width = state.getGui().getWidth();
            int height = state.getGui().getHeight();
            
            var slots = new HashSet<Slot>();
            while (slots.isEmpty() && row < height) {
                for (int x = 0; x < width; x++) {
                    Slot slot = new Slot(x, row);
                    if (state.getRemainingSlots().contains(slot)) {
                        slots.add(slot);
                    }
                }
                
                row++;
            }
            
            return slots;
        }
        
    }
    
    static final class HorizontalSnakeSlotSelector implements Function<State, Set<Slot>> {
        
        private int x;
        private int y;
        private boolean left;
        
        @Override
        public Set<Slot> apply(State state) {
            int width = state.getGui().getWidth();
            while (true) {
                Slot slot = new Slot(x, y);
                if (state.getRemainingSlots().contains(slot))
                    return Set.of(slot);
                
                if (left) {
                    if (x <= 0) {
                        y++;
                        left = false;
                    } else x--;
                } else {
                    if (x >= width - 1) {
                        y++;
                        left = true;
                    } else x++;
                }
            }
        }
        
    }
    
    static final class VerticalSnakeSlotSelector implements Function<State, Set<Slot>> {
        
        private int x;
        private int y;
        private boolean up;
        
        @Override
        public Set<Slot> apply(State state) {
            int height = state.getGui().getWidth();
            while (true) {
                Slot slot = new Slot(x, y);
                if (state.getRemainingSlots().contains(slot))
                    return Set.of(slot);
                
                if (up) {
                    if (y <= 0) {
                        x++;
                        up = false;
                    } else y--;
                } else {
                    if (y >= height - 1) {
                        x++;
                        up = true;
                    } else y++;
                }
            }
        }
        
    }
    
    static final class SequentialSlotSelector implements Function<State, Set<Slot>> {
        
        private int i;
        
        @Override
        public Set<Slot> apply(State state) {
            int size = state.getGui().getSize();
            int width = state.getGui().getWidth();
            while (i < size) {
                int x = i % width;
                int y = i / width;
                
                Slot slot = new Slot(x, y);
                if (state.getRemainingSlots().contains(slot))
                    return Set.of(slot);
                
                i++;
            }
            
            return Set.of();
        }
        
    }
    
    static final class SplitSequentialSlotSelector implements Function<State, Set<Slot>> {
        
        private int i1;
        private int i2;
        
        @Override
        public Set<Slot> apply(State state) {
            int size = state.getGui().getSize();
            int width = state.getGui().getWidth();
            
            var slots = new HashSet<Slot>();
            
            while (i1 < size) {
                int x = i1 % width;
                int y = i1 / width;
                
                var slot = new Slot(x, y);
                if (state.getRemainingSlots().contains(slot)) {
                    slots.add(slot);
                    break;
                }
                
                i1++;
            }
            
            while (i2 < size) {
                int x = (size - i2) % width;
                int y = (size - i2) / width;
                
                var slot = new Slot(x, y);
                if (state.getRemainingSlots().contains(slot)) {
                    slots.add(slot);
                    break;
                }
                
                i2++;
            }
            
            return slots;
        }
        
    }
    
    static final Function<State, Set<Slot>> RANDOM_SLOT_SELECTOR = state -> {
        var slots = state.getRemainingSlots().toArray(Slot[]::new);
        return Set.of(slots[MathUtils.RANDOM.nextInt(slots.length)]);
    };
    
}
