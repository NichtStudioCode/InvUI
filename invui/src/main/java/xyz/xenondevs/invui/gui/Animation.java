package xyz.xenondevs.invui.gui;

import org.bukkit.Sound;
import xyz.xenondevs.invui.internal.util.ArrayUtils;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An animation lets slots of a {@link Gui} pop in, in a specific order with a specific delay.
 * <p>
 * An animation consists of the following components:
 * <ul>
 *     <li>Slot filter: Defines which slots are part of the animation.</li>
 *     <li>Slot selector: Selects the slots that are shown in each frame.</li>
 *     <li>Show handler: Called when slot(s) are shown, for example to play a sound effect.</li>
 *     <li>Finish handler: Called when the animation is finished.</li>
 * </ul>
 *
 * @see Gui#playAnimation(Animation)
 */
public sealed interface Animation permits AnimationImpl {
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * shows the slots in a column-by-column order, from left to right.
     *
     * @return The animation builder.
     */
    static Builder column() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(new AnimationImpl.ColumnSlotSelector());
        return builder;
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * shows the slots in a row-by-row order, from top to bottom.
     *
     * @return The animation builder.
     */
    static Builder row() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(new AnimationImpl.RowSlotSelector());
        return builder;
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * moves like a snake in horizontal direction, starting from (0,0)
     * then moving all the way right, one down, all the way left and repeats.
     *
     * @return The animation builder.
     */
    static Builder horizontalSnake() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(new AnimationImpl.HorizontalSnakeSlotSelector());
        return builder;
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * moves like a snake in vertical direction, starting from (0,0)
     * then moving all the way down, one right, all the way up and repeats.
     *
     * @return The animation builder.
     */
    static Builder verticalSnake() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(new AnimationImpl.VerticalSnakeSlotSelector());
        return builder;
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * shows the slots individually, from left to right, top to bottom.
     *
     * @return The animation builder.
     */
    static Builder sequential() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(new AnimationImpl.SequentialSlotSelector());
        return builder;
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * shows the slots individually like {@link #sequential()}, but from
     * both the top-left and the bottom-right corner.
     *
     * @return The animation builder.
     */
    static Builder splitSequential() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(new AnimationImpl.SplitSequentialSlotSelector());
        return builder;
    }
    
    /**
     * Creates a new animation builder that uses a slot selector which
     * selects the slots randomly.
     *
     * @return The animation builder.
     */
    static Builder random() {
        var builder = new AnimationImpl.BuilderImpl();
        builder.setSlotSelector(AnimationImpl.RANDOM_SLOT_SELECTOR);
        return builder;
    }
    
    /**
     * Creates a new animation builder without any predefined slot selector.
     *
     * @return The animation builder.
     */
    static Builder custom() {
        return new AnimationImpl.BuilderImpl();
    }
    
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
    
    /**
     * A builder for an {@link Animation}.
     */
    sealed interface Builder permits AnimationImpl.BuilderImpl {
        
        /**
         * Sets the delay between invocations of the slot selector.
         *
         * @param tickDelay The delay between invocations of the slot selector.
         * @return This {@link Builder Animation builder}
         */
        Builder setTickDelay(int tickDelay);
        
        /**
         * Adds a filter defining which slots are part of the animation.
         *
         * @param filter The filter defining which slots are part of the animation.
         * @return This {@link Builder Animation builder}
         */
        Builder addSlotFilter(BiPredicate<Gui, Slot> filter);
        
        /**
         * Adds a {@link #addSlotFilter(BiPredicate) filter} that ignores all empty slots.
         *
         * @return This {@link Builder Animation builder}
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
         * @return This {@link Builder Animation builder}
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
         * @return This {@link Builder Animation builder}
         */
        default Builder addSoundEffect(Sound sound, float volume, float pitch) {
            return addShowHandler((animation, slot) ->
                animation.getGui().findAllCurrentViewers().forEach(viewer ->
                    viewer.playSound(viewer, sound, volume, pitch)
                )
            );
        }
        
        /**
         * Sets the slot selector for the animation.
         * The slot selector determines which slots are shown in which order.
         * Slot selectors need to show all slots of {@link Animation#getRemainingSlots()} in order to complete the animation.
         *
         * @param selector The slot selector for the animation.
         * @return This {@link Builder Animation builder}
         */
        Builder setSlotSelector(Function<Animation, Set<Slot>> selector);
        
        /**
         * Adds a handler that is called when slot(s) are shown.
         *
         * @param showHandler The handler that is called when slot(s) are shown.
         * @return This {@link Builder Animation builder}
         */
        Builder addShowHandler(BiConsumer<Animation, Set<Slot>> showHandler);
        
        /**
         * Adds a handler that is called when the animation is finished.
         *
         * @param finishHandler The handler that is called when the animation is finished.
         * @return This {@link Builder Animation builder}
         */
        Builder addFinishHandler(Consumer<Animation> finishHandler);
        
        /**
         * Builds the animation.
         *
         * @return The built animation.
         */
        Animation build();
        
    }
    
}
