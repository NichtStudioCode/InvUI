package xyz.xenondevs.invui.window.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.CartographyWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.function.Consumer;

public interface WindowType<W extends Window, B extends WindowBuilder<W>> {
    
    WindowType<Window, NormalSingleWindowBuilder> NORMAL = NormalSingleWindowBuilder::new;
    WindowType<Window, NormalMergedWindowBuilder> NORMAL_MERGED = NormalMergedWindowBuilder::new;
    WindowType<Window, NormalSplitWindowBuilder> NORMAL_SPLIT = NormalSplitWindowBuilder::new;
    WindowType<AnvilWindow, AnvilSingleWindowBuilder> ANVIL = AnvilSingleWindowBuilder::new;
    WindowType<AnvilWindow, AnvilSplitWindowBuilder> ANVIL_SPLIT = AnvilSplitWindowBuilder::new;
    WindowType<CartographyWindow, CartographySingleWindowBuilder> CARTOGRAPHY = CartographySingleWindowBuilder::new;
    WindowType<CartographyWindow, CartographySplitWindowBuilder> CARTOGRAPHY_SPLIT = CartographySplitWindowBuilder::new;
    
    /**
     * Creates a new {@link WindowBuilder} for this {@link WindowType}.
     *
     * @return The new {@link WindowBuilder}.
     */
    @NotNull B builder();
    
    /**
     * Creates a new {@link Window} after modifying the {@link WindowBuilder} with the given {@link Consumer}.
     *
     * @param builderConsumer The {@link Consumer} which modifies the {@link WindowBuilder}.
     * @return The new {@link Window}.
     */
    default @NotNull W createWindow(Consumer<B> builderConsumer) {
        B builder = builder();
        builderConsumer.accept(builder);
        return builder.build();
    }
    
}
