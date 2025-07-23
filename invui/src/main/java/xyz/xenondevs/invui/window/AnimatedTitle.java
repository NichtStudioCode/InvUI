package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import xyz.xenondevs.invui.internal.util.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link Component} supplier that enables auto-refreshing titles in {@link Window Windows}.
 * <p>
 * Usage:
 * <pre>{@code
 * AnimatedTitle title = AnimatedTitle.of(10, "A", "B");
 * window.setTitleSupplier(title);
 * }</pre>
 */
public interface AnimatedTitle extends Supplier<Component> {
    
    /**
     * Creates a new {@link AnimatedTitle} with the given frame time and frames.
     *
     * @param frameTime The time in ticks each frame should be displayed
     * @param frame     The first frame
     * @param frames    The following frames
     * @return A new {@link AnimatedTitle} that displays the given frames in a loop
     * @throws IllegalArgumentException If frameTime is less than 1
     */
    static AnimatedTitle of(int frameTime, Component frame, Component... frames) {
        return of(frameTime, ArrayUtils.concat(Component[]::new, frame, frames));
    }
    
    /**
     * Creates a new {@link AnimatedTitle} with the given frame time and frames.
     *
     * @param frameTime The time in ticks each frame should be displayed
     * @param frame     The first frame
     * @param frames    The following frames
     * @return A new {@link AnimatedTitle} that displays the given frames in a loop
     * @throws IllegalArgumentException If frameTime is less than 1
     */
    static AnimatedTitle of(int frameTime, String frame, String... frames) {
        return of(frameTime, ArrayUtils.concat(String[]::new, frame, frames));
    }
    
    /**
     * Creates a new {@link AnimatedTitle} with the given frame time and frames.
     *
     * @param frameTime The time in ticks each frame should be displayed
     * @param frames    The frames
     * @return A new {@link AnimatedTitle} that displays the given frames in a loop
     * @throws IllegalArgumentException If frameTime is less than 1
     * @throws IllegalArgumentException If frames is empty
     */
    static AnimatedTitle of(int frameTime, List<? extends Component> frames) {
        return of(frameTime, frames.toArray(Component[]::new));
    }
    
    /**
     * Creates a new {@link AnimatedTitle} with the given frame time and frames.
     *
     * @param frameTime The time in ticks each frame should be displayed
     * @param frames    The frames
     * @return A new {@link AnimatedTitle} that displays the given frames in a loop
     * @throws IllegalArgumentException If frameTime is less than 1
     * @throws IllegalArgumentException If frames is empty
     */
    static AnimatedTitle of(int frameTime, String[] frames) {
        var components = Arrays.stream(frames)
            .map(s -> MiniMessage.miniMessage().deserialize(s))
            .toArray(Component[]::new);
        return of(frameTime, components);
    }
    
    /**
     * Creates a new {@link AnimatedTitle} with the given frame time and frames.
     *
     * @param frameTime The time in ticks each frame should be displayed
     * @param frames    The frames
     * @return A new {@link AnimatedTitle} that displays the given frames in a loop
     * @throws IllegalArgumentException If frameTime is less than 1
     * @throws IllegalArgumentException If frames is empty
     */
    static AnimatedTitle of(int frameTime, Component[] frames) {
        if (frameTime < 1)
            throw new IllegalArgumentException("frameTime must be >= 1");
        if (frames.length == 0)
            throw new IllegalArgumentException("frames must not be empty");
        
        return () -> frames[(Bukkit.getCurrentTick() / frameTime) % frames.length];
    }
    
}
