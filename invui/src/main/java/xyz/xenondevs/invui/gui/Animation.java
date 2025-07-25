package xyz.xenondevs.invui.gui;

import org.bukkit.Sound;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.Set;
import java.util.function.*;

/**
 * An animation lets slots of a {@link Gui} pop in, in a specific order with a specific delay.
 * <p>
 * An animation consists of the following components:
 * <ul>
 *     <li>Slot filter: Defines which slots are part of the animation.</li>
 *     <li>Slot selector: Selects the slots that are shown in each frame.</li>
 *     <li>Intermediary generator: Creates intermediary {@link SlotElement SlotElements} that are displayed before
 *     the slots pop in.</li>
 *     <li>Show handler: Called when slot(s) are shown, for example to play a sound effect.</li>
 *     <li>Finish handler: Called when the animation is finished.</li>
 * </ul>
 *
 * @see Gui#playAnimation(Animation)
 */
public sealed interface Animation permits AnimationImpl {
    
    /**
     * Creates a new animation builder.
     *
     * @return The animation builder.
     */
    static Builder builder() {
        return new AnimationImpl.BuilderImpl();
    }
    
    /**
     * Creates a slot selector which shows the slots in a column-by-column order,
     * from left to right.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> columnSlotSelector() {
        return new AnimationImpl.ColumnSlotSelector();
    }
    
    /**
     * Creates a slot selector which shows the slots in a row-by-row order,
     * from top to bottom.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> rowSlotSelector() {
        return new AnimationImpl.RowSlotSelector();
    }
    
    /**
     * Creates a slot selector which moves like a snake in horizontal direction,
     * starting from (0,0) then moving all
     * the way right, one down, all the way left and repeats.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> horizontalSnakeSlotSelector() {
        return new AnimationImpl.HorizontalSnakeSlotSelector();
    }
    
    /**
     * Creates a slot selector which moves like a snake in vertical direction,
     * starting from (0,0) then moving all the way down, one right, all the way
     * up and repeats.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> verticalSnakeSlotSelector() {
        return new AnimationImpl.VerticalSnakeSlotSelector();
    }
    
    /**
     * Creates a slot selector which shows the slots individually,
     * from left to right, top to bottom.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> sequentialSlotSelector() {
        return new AnimationImpl.SequentialSlotSelector();
    }
    
    /**
     * Creates a slot selector which shows the slots individually like
     * {@link #sequentialSlotSelector()}, but from both the top-left
     * and the bottom-right corner.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> splitSequentialSlotSelector() {
        return new AnimationImpl.SplitSequentialSlotSelector();
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which selects
     * the slots randomly.
     *
     * @return The slot selector.
     */
    static Function<State, Set<Slot>> randomSlotSelector() {
        return AnimationImpl.RANDOM_SLOT_SELECTOR;
    }
    
    /**
     * Contains the state of a running animation.
     */
    sealed interface State permits AnimationImpl.StateImpl {
        
        /**
         * Gets the {@link Gui} that this animation is bound to, or throws an exception if the animation is not bound to a gui.
         *
         * @return The {@link Gui} that this animation is bound to.
         * @throws IllegalStateException If the animation is not bound to a gui.
         */
        Gui getGui();
        
        /**
         * Gets the current frame of the animation.
         *
         * @return The current frame of the animation.
         */
        int getFrame();
        
        /**
         * Gets an immutable view of the slots that are still remaining to be shown.
         *
         * @return An immutable view of the slots that are still remaining to be shown.
         */
        Set<Slot> getRemainingSlots();
        
        /**
         * Gets whether the animation is finished.
         *
         * @return Whether the animation is finished.
         */
        boolean isFinished();
        
    }
    
    /**
     * A builder for an {@link Animation}.
     */
    sealed interface Builder permits AnimationImpl.BuilderImpl {
        
        /**
         * Sets the delay between invocations of the slot selector.
         *
         * @param tickDelay The delay between invocations of the slot selector.
         * @return This {@link Builder}
         */
        Builder setTickDelay(int tickDelay);
        
        /**
         * Adds a filter defining which slots are part of the animation.
         *
         * @param filter The filter defining which slots are part of the animation.
         * @return This {@link Builder}
         */
        Builder addSlotFilter(BiPredicate<? super Gui, ? super Slot> filter);
        
        /**
         * Adds a {@link #addSlotFilter(BiPredicate) filter} that ignores all empty slots.
         *
         * @return This {@link Builder}
         */
        default Builder filterNonEmptySlots() {
            return addSlotFilter((gui, slot) -> gui.getSlotElement(slot.x(), slot.y()) != null);
        }
        
        /**
         * Adds a {@link #addSlotFilter(BiPredicate) filter} that only allows slots that are
         * {@link Gui#isTagged(Slot, char) tagged} with any of the given keys via a {@link Structure}.
         *
         * @param key  The key to filter for
         * @param keys Additional keys to filter for
         * @return This {@link Builder}
         * @see Gui#applyStructure(Structure)
         */
        default Builder filterTaggedSlots(char key, char... keys) {
            Set<Character> keySet = ArrayUtils.toSet(key, keys);
            return addSlotFilter((gui, slot) -> keySet.contains(gui.getKey(slot)));
        }
        
        /**
         * Shortcut for adding a sound effect {@link #addShowHandler(BiConsumer) show handler}.
         *
         * @param sound  The sound effect type
         * @param volume The volume of the sound
         * @param pitch  The pitch of the sound
         * @return This {@link Builder}
         */
        default Builder addSoundEffect(Sound sound, float volume, float pitch) {
            return addShowHandler((animation, slot) ->
                animation.getGui().getCurrentViewers().forEach(viewer ->
                    viewer.playSound(viewer, sound, volume, pitch)
                )
            );
        }
        
        /**
         * Sets supplier for the slot selector of the animation.
         * The slot selector determines which slots are shown in which order.
         * Slot selectors need to show all slots of {@link State#getRemainingSlots()} in order to complete the animation.
         *
         * @param selector The slot selector for the animation.
         * @return This {@link Builder}
         */
        Builder setSlotSelector(Supplier<? extends Function<? super State, ? extends Set<? extends Slot>>> selector);
        
        /**
         * Sets the intermediary {@link ItemProvider} that is displayed before the slots pop in.
         *
         * @param provider The intermediary {@link ItemProvider}.
         * @return This {@link Builder}
         */
        default Builder setIntermediary(ItemProvider provider) {
            return setIntermediary(Item.simple(provider));
        }
        
        /**
         * Sets the intermediary {@link Item} that is displayed before the slots pop in.
         *
         * @param item The intermediary {@link Item}.
         * @return This {@link Builder}
         */
        default Builder setIntermediary(Item item) {
            var element = new SlotElement.Item(item);
            return setIntermediaryElementGenerator(slot -> element);
        }
        
        /**
         * Sets the generator function that creates intermediary {@link ItemProvider ItemProviders} that are displayed
         * before the {@link Gui Gui's} slots pop in.
         *
         * @param intermediaryGenerator The generator function that creates intermediary {@link ItemProvider ItemProviders}.
         * @return This {@link Builder}
         */
        default Builder setIntermediaryGenerator(Function<? super Slot, ? extends @Nullable ItemProvider> intermediaryGenerator) {
            return setIntermediaryElementGenerator(slot -> {
                ItemProvider provider = intermediaryGenerator.apply(slot);
                return provider != null ? new SlotElement.Item(Item.simple(provider)) : null;
            });
        }
        
        /**
         * Sets the generator function that creates intermediary {@link SlotElement SlotElements} that are displayed
         * before the {@link Gui Gui's} slots pop in.
         *
         * @param intermediaryGenerator The generator function that creates intermediary {@link SlotElement SlotElements}.
         * @return This {@link Builder}
         */
        Builder setIntermediaryElementGenerator(Function<? super Slot, ? extends @Nullable SlotElement> intermediaryGenerator);
        
        /**
         * Adds a handler that is called when slot(s) are shown.
         *
         * @param showHandler The handler that is called when slot(s) are shown.
         * @return This {@link Builder}
         */
        Builder addShowHandler(BiConsumer<? super State, ? super Set<? extends Slot>> showHandler);
        
        /**
         * Adds a handler that is called when the animation is finished.
         *
         * @param finishHandler The handler that is called when the animation is finished.
         * @return This {@link Builder}
         */
        Builder addFinishHandler(Consumer<? super State> finishHandler);
        
        /**
         * Sets whether the {@link Gui} that plays the animation should be frozen (i.e. not allow any interactions)
         * during it. Defaults to true.
         *
         * @param freezing Whether the {@link Gui} should be frozen during the animation.
         * @return This {@link Builder}
         */
        Builder setFreezing(boolean freezing);
        
        /**
         * Builds the animation.
         *
         * @return The built animation.
         */
        Animation build();
        
    }
    
}
