package xyz.xenondevs.invui.window;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.window.builder.WindowType;

import java.util.List;
import java.util.UUID;

/**
 * A Window is the way to show a player a Gui. Windows can only have one viewer.
 * The default Window implementations can be instantiated using {@link WindowType}.
 *
 * @see WindowType
 * @see AbstractWindow
 * @see AbstractSingleWindow
 * @see AbstractDoubleWindow
 * @see AbstractSplitWindow
 * @see AbstractMergedWindow
 */
public interface Window {
    
    /**
     * Shows the window to the player.
     */
    void show();
    
    /**
     * Gets if the player is able to close the {@link Inventory}.
     *
     * @return If the player is able to close the {@link Inventory}.
     */
    boolean isCloseable();
    
    /**
     * Sets if the player should be able to close the {@link Inventory}.
     *
     * @param closeable If the player should be able to close the {@link Inventory}.
     */
    void setCloseable(boolean closeable);
    
    /**
     * Closes the underlying {@link Inventory} for its viewer.
     */
    void close();
    
    /**
     * Gets if the {@link Window} is closed and can't be shown again.
     *
     * @return If the {@link Window} is closed.
     */
    boolean isRemoved();
    
    /**
     * Removes the {@link Window} from the {@link WindowManager} list.
     * If this method is called, the {@link Window} can't be shown again.
     */
    void remove();
    
    /**
     * Changes the title of the {@link Inventory}.
     *
     * @param title The new title
     */
    void changeTitle(@NotNull ComponentWrapper title);
    
    /**
     * Changes the title of the {@link Inventory}.
     *
     * @param title The new title
     */
    void changeTitle(@NotNull BaseComponent[] title);
    
    /**
     * Changes the title of the {@link Inventory}.
     *
     * @param title The new title
     */
    void changeTitle(@NotNull String title);
    
    /**
     * Gets the viewer of this {@link Window}
     *
     * @return The viewer of this window.
     */
    @Nullable Player getViewer();
    
    /**
     * Gets the current {@link Player} that is viewing this
     * {@link Window} or null of there isn't one.
     *
     * @return The current viewer of this {@link Window} (can be null)
     */
    @Nullable Player getCurrentViewer();
    
    /**
     * Gets the viewer's {@link UUID}
     *
     * @return The viewer's {@link UUID}
     */
    @NotNull UUID getViewerUUID();
    
    /**
     * Replaces the currently registered close handlers with the given list.
     *
     * @param closeHandlers The new close handlers
     */
    void setCloseHandlers(@NotNull List<@NotNull Runnable> closeHandlers);
    
    /**
     * Adds a close handler that will be called when this window gets closed.
     *
     * @param closeHandler The close handler to add
     */
    void addCloseHandler(@NotNull Runnable closeHandler);
    
    /**
     * Removes a close handler that has been added previously.
     *
     * @param closeHandler The close handler to remove
     */
    void removeCloseHandler(@NotNull Runnable closeHandler);
    
}
