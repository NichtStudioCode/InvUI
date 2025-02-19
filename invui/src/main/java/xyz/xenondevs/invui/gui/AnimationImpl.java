package xyz.xenondevs.invui.gui;

import org.bukkit.Bukkit;
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
    private final BiPredicate<Gui, Slot> slotFilter;
    private final Function<Animation, Set<Slot>> slotSelector;
    private BiConsumer<Animation, Set<Slot>> showHandler;
    private Consumer<Animation> finishHandler;
    
    private final Set<Slot> remainingSlots = new HashSet<>();
    private @Nullable Gui gui;
    private @Nullable BukkitTask task;
    private int currentFrame;
    private boolean completed;
    
    public AnimationImpl(
        int tickDelay,
        BiPredicate<Gui, Slot> slotFilter,
        Function<Animation, Set<Slot>> slotSelector,
        BiConsumer<Animation, Set<Slot>> showHandler,
        Consumer<Animation> finishHandler
    ) {
        this.tickDelay = tickDelay;
        this.slotFilter = slotFilter;
        this.slotSelector = slotSelector;
        this.showHandler = showHandler;
        this.finishHandler = finishHandler;
    }
    
    @Override
    public Gui getGui() {
        if (this.gui == null)
            throw new IllegalStateException("Animation is not bound to a gui");
        
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
    
    public void addShowHandler(Consumer<Set<Slot>> showHandler) {
        this.showHandler = this.showHandler.andThen((animation, slot) -> showHandler.accept(slot));
    }
    
    public void addFinishHandler(Runnable finishHandler) {
        this.finishHandler = this.finishHandler.andThen(animation -> finishHandler.run());
    }
    
    /**
     * Binds the animation to a gui and populates the remaining slots.
     *
     * @param gui The gui to bind the animation to.
     */
    public void bind(Gui gui) {
        if (this.gui != null)
            throw new IllegalStateException("Animation is already bound to a gui");
        
        this.gui = gui;
        
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
        if (completed)
            throw new IllegalStateException("Animation is already completed");
        if (task != null)
            throw new IllegalStateException("Animation is already running");
        if (gui == null)
            throw new IllegalStateException("Animation is not bound to a gui");
        
        // no slots
        if (remainingSlots.isEmpty())
            return;
        
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::handleTick, 0, tickDelay);
    }
    
    private void handleTick() {
        var nextSlots = slotSelector.apply(this);
        remainingSlots.removeAll(nextSlots);
        showHandler.accept(this, nextSlots);
        
        currentFrame++;
        
        if (remainingSlots.isEmpty())
            cancel();
    }
    
    public void cancel() {
        if (task != null) {
            completed = true;
            task.cancel();
            finishHandler.accept(this);
        }
    }
    
    static final class BuilderImpl implements Animation.Builder {
        
        private int tickDelay = 1;
        private BiPredicate<Gui, Slot> slotFilter = (gui, slot) -> true;
        private BiConsumer<Animation, Set<Slot>> showHandler = (animation, slot) -> {};
        private Consumer<Animation> finishHandler = gui -> {};
        private @Nullable Function<Animation, Set<Slot>> slotSelector;
        
        @Override
        public Animation.Builder setTickDelay(int tickDelay) {
            this.tickDelay = tickDelay;
            return this;
        }
        
        @Override
        public Animation.Builder setSlotSelector(Function<Animation, Set<Slot>> selector) {
            this.slotSelector = selector;
            return this;
        }
        
        @Override
        public Animation.Builder addShowHandler(BiConsumer<Animation, Set<Slot>> showHandler) {
            this.showHandler = this.showHandler.andThen(showHandler);
            return this;
        }
        
        @Override
        public Animation.Builder addFinishHandler(Consumer<Animation> finishHandler) {
            this.finishHandler = this.finishHandler.andThen(finishHandler);
            return this;
        }
        
        @Override
        public Animation.Builder addSlotFilter(BiPredicate<Gui, Slot> filter) {
            this.slotFilter = this.slotFilter.and(filter);
            return this;
        }
        
        @Override
        public Animation build() {
            if (slotSelector == null)
                throw new IllegalStateException("SlotSelector needs to be set");
            
            return new AnimationImpl(tickDelay, slotFilter, slotSelector, showHandler, finishHandler);
        }
        
    }
    
    static final class ColumnSlotSelector implements Function<Animation, Set<Slot>> {
        
        private int column;
        
        @Override
        public Set<Slot> apply(Animation animation) {
            int width = animation.getGui().getWidth();
            int height = animation.getGui().getHeight();
            
            var slots = new HashSet<Slot>();
            
            while (slots.isEmpty() && column < width) {
                for (int y = 0; y < height; y++) {
                    var slot = new Slot(column, y);
                    if (animation.getRemainingSlots().contains(slot)) {
                        slots.add(slot);
                    }
                }
                
                column++;
            }
            
            return slots;
        }
        
    }
    
    static final class RowSlotSelector implements Function<Animation, Set<Slot>> {
        
        private int row;
        
        @Override
        public Set<Slot> apply(Animation animation) {
            int width = animation.getGui().getWidth();
            int height = animation.getGui().getHeight();
            
            var slots = new HashSet<Slot>();
            while (slots.isEmpty() && row < height) {
                for (int x = 0; x < width; x++) {
                    Slot slot = new Slot(x, row);
                    if (animation.getRemainingSlots().contains(slot)) {
                        slots.add(slot);
                    }
                }
                
                row++;
            }
            
            return slots;
        }
        
    }
    
    static final class HorizontalSnakeSlotSelector implements Function<Animation, Set<Slot>> {
        
        private int x;
        private int y;
        private boolean left;
        
        @Override
        public Set<Slot> apply(Animation animation) {
            int width = animation.getGui().getWidth();
            while (true) {
                Slot slot = new Slot(x, y);
                if (animation.getRemainingSlots().contains(slot))
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
    
    static final class VerticalSnakeSlotSelector implements Function<Animation, Set<Slot>> {
        
        private int x;
        private int y;
        private boolean up;
        
        @Override
        public Set<Slot> apply(Animation animation) {
            int height = animation.getGui().getWidth();
            while (true) {
                Slot slot = new Slot(x, y);
                if (animation.getRemainingSlots().contains(slot))
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
    
    static final class SequentialSlotSelector implements Function<Animation, Set<Slot>> {
        
        private int i;
        
        @Override
        public Set<Slot> apply(Animation animation) {
            int size = animation.getGui().getSize();
            int width = animation.getGui().getWidth();
            while (i < size) {
                int x = i % width;
                int y = i / width;
                
                Slot slot = new Slot(x, y);
                if (animation.getRemainingSlots().contains(slot))
                    return Set.of(slot);
                
                i++;
            }
            
            return Set.of();
        }
        
    }
    
    static final class SplitSequentialSlotSelector implements Function<Animation, Set<Slot>> {
        
        private int i1;
        private int i2;
        
        @Override
        public Set<Slot> apply(Animation animation) {
            int size = animation.getGui().getSize();
            int width = animation.getGui().getWidth();
            
            var slots = new HashSet<Slot>();
            
            while (i1 < size) {
                int x = i1 % width;
                int y = i1 / width;
                
                var slot = new Slot(x, y);
                if (animation.getRemainingSlots().contains(slot)) {
                    slots.add(slot);
                    break;
                }
                
                i1++;
            }
            
            while (i2 < size) {
                int x = (size - i2) % width;
                int y = (size - i2) / width;
                
                var slot = new Slot(x, y);
                if (animation.getRemainingSlots().contains(slot)) {
                    slots.add(slot);
                    break;
                }
                
                i2++;
            }
            
            return slots;
        }
        
    }
    
    static final Function<Animation, Set<Slot>> RANDOM_SLOT_SELECTOR = animation -> {
        var slots = animation.getRemainingSlots().toArray(Slot[]::new);
        return Set.of(slots[MathUtils.RANDOM.nextInt(slots.length)]);
    };
    
}
