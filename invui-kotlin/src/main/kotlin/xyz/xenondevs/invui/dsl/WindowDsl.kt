@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ClickEvent
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.internal.util.InventoryUtils2
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.setCloseable
import xyz.xenondevs.invui.window.setTitle
import xyz.xenondevs.invui.window.setWindowState
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a split [Window] using the DSL, with separate upper and lower GUI sections.
 *
 * The upper GUI is displayed in the top inventory, while the lower GUI is displayed in the
 * player's inventory area. The lower GUI defaults to a player inventory reference.
 *
 * ```
 * val myWindow = window(player) {
 *     title by "My Window"
 *
 *     upperGui by gui(
 *         "# # # # # # # # #",
 *         "# . . . x . . . #",
 *         "# # # # # # # # #",
 *     ) {
 *         '#' by borderItem
 *         'x' by someItem
 *     }
 * }
 * ```
 *
 * @see NormalSplitWindowDsl
 */
@ExperimentalDslApi
inline fun window(viewer: Player, window: NormalSplitWindowDsl.() -> Unit): Window {
    contract { callsInPlace(window, InvocationKind.EXACTLY_ONCE) }
    return NormalSplitWindowDslImpl(viewer).apply(window).build()
}

/**
 * Creates a merged [Window] using the DSL, where a single GUI spans both the top inventory
 * and the player's inventory area.
 *
 * ```
 * val myWindow = mergedWindow(player) {
 *     title by "Merged Window"
 *
 *     gui by gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# . . . x . . . #",
 *         "# . . . . . . . #",
 *         "# # # # # # # # #",
 *         ". . . . . . . . .",
 *         ". . . . . . . . .",
 *         ". . . . . . . . .",
 *         ". . . . . . . . .",
 *     ) {
 *         '#' by borderItem
 *         'x' by someItem
 *     }
 * }
 * ```
 *
 * @see NormalMergedWindowDsl
 */
@ExperimentalDslApi
inline fun mergedWindow(viewer: Player, window: NormalMergedWindowDsl.() -> Unit): Window {
    contract { callsInPlace(window, InvocationKind.EXACTLY_ONCE) }
    return NormalMergedWindowDslImpl(viewer).apply(window).build()
}

/**
 * DSL scope available inside [WindowDsl.onOpen] handlers.
 *
 * ```
 * onOpen {
 *     // window has been opened
 * }
 * ```
 */
@WindowDslMarker
@ExperimentalDslApi
interface WindowOpenDsl

/**
 * DSL scope available inside [WindowDsl.onClose] handlers, providing the close reason.
 *
 * ```
 * onClose {
 *     if (reason == InventoryCloseEvent.Reason.PLAYER) {
 *         // player closed the window manually
 *     }
 * }
 * ```
 */
@WindowDslMarker
@ExperimentalDslApi
interface WindowCloseDsl {
    
    /** The reason the window was closed. */
    val reason: InventoryCloseEvent.Reason
    
}

/**
 * DSL scope available inside [WindowDsl.onOutsideClick] handlers, providing information about
 * a click outside the inventory window. The event can be cancelled via [isCancelled].
 *
 * ```
 * onOutsideClick {
 *     player.sendMessage("You clicked outside!")
 *     isCancelled = true
 * }
 * ```
 */
@WindowDslMarker
@ExperimentalDslApi
interface WindowOutsideClickDsl {
    
    /** The player who clicked outside the window. */
    val player: Player
    
    /** The type of click performed. */
    val clickType: ClickType
    
    /** The hotbar button pressed, or `-1` if no hotbar button was involved. */
    val hotbarButton: Int
    
    /** Whether the click event is cancelled. Set to `true` to cancel. */
    var isCancelled: Boolean
    
}

/**
 * Base DSL scope for configuring a [Window].
 *
 * Provides common window properties like [title], [closeable], and event handlers
 * ([onOpen], [onClose], [onOutsideClick]).
 *
 * ```
 * window(player) {
 *     title by "My Window"
 *     closeable by true
 *
 *     onClose {
 *         // handle close
 *     }
 *
 *     upperGui by myGui
 * }
 * ```
 */
@ExperimentalDslApi
@WindowDslMarker
sealed interface WindowDsl {
    
    /** The player viewing this window. */
    val viewer: Player
    
    /**
     * A [Provider] that resolves to the built [Window] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    val window: Provider<Window>
    
    /**
     * The window title displayed at the top of the inventory.
     *
     * Defaults to an empty [Component]. Can be set to a static value or bound to a [Provider]:
     * ```
     * title by Component.text("My Window")
     * ```
     *
     * [MiniMessage][net.kyori.adventure.text.minimessage.MiniMessage] strings are also supported
     * via extension functions:
     * ```
     * title by "<red>My Window"
     * ```
     */
    val title: ProviderDslProperty<Component>
    
    /**
     * Whether the player can close this window by pressing escape or the inventory key.
     *
     * Defaults to `true`. Can be set to a static value or bound to a [Provider]:
     * ```
     * closeable by false
     * ```
     */
    val closeable: ProviderDslProperty<Boolean>
    
    /**
     * A fallback [Window] to open when this window is closed. Set to `null` for no fallback.
     *
     * Defaults to `null`. Can be set to a static value or bound to a [Provider]:
     * ```
     * fallbackWindow by anotherWindow
     * ```
     */
    val fallbackWindow: ProviderDslProperty<Window?>
    
    /**
     * The server-side window state. When changed, the new value is sent to the client via a
     * ping packet. Once the client acknowledges it with a pong, [clientWindowState] is updated
     * and any window state change handlers fire. This can be used to track what state the window
     * is in during interactions.
     *
     * Defaults to `0`.
     * 
     * ```
     * serverWindowState by 0
     * ```
     */
    val serverWindowState: MutableProviderDslProperty<Int>
    
    /**
     * The last window state acknowledged by the client. Updated automatically when the client
     * responds with a pong to the server's ping (triggered by [serverWindowState] changes).
     * This is a read-only [Provider].
     */
    val clientWindowState: Provider<Int>
    
    /**
     * Registers a handler that is called when the window is opened.
     * Multiple handlers can be registered and will all be called in order.
     *
     * ```
     * onOpen {
     *     // window opened
     * }
     * ```
     *
     * @see WindowOpenDsl
     */
    fun onOpen(handler: WindowOpenDsl.() -> Unit)
    
    /**
     * Registers a handler that is called when the window is closed.
     * Multiple handlers can be registered and will all be called in order.
     *
     * ```
     * onClose {
     *     if (reason == InventoryCloseEvent.Reason.PLAYER) {
     *         // player closed manually
     *     }
     * }
     * ```
     *
     * @see WindowCloseDsl
     */
    fun onClose(handler: WindowCloseDsl.() -> Unit)
    
    /**
     * Registers a handler that is called when the player clicks outside the window.
     * Multiple handlers can be registered and will all be called in order.
     *
     * ```
     * onOutsideClick {
     *     isCancelled = true
     * }
     * ```
     *
     * @see WindowOutsideClickDsl
     */
    fun onOutsideClick(handler: WindowOutsideClickDsl.() -> Unit)
    
}

/**
 * Base DSL scope for windows with separate upper and lower GUI sections.
 *
 * ```
 * window(player) {
 *     title by "Split Window"
 *
 *     upperGui by gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# # # # # # # # #",
 *     ) {
 *         '#' by borderItem
 *     }
 *
 *     // lowerGui defaults to the player's inventory;
 *     // override with a custom GUI if needed:
 *     // lowerGui by myCustomGui
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface SplitWindowDsl : WindowDsl {
    
    /**
     * The lower GUI displayed in the player's inventory area. Defaults to a player inventory
     * reference.
     *
     * ```
     * lowerGui by gui(
     *     "x x x x x x x x x",
     *     "x x x x x x x x x",
     *     "x x x x x x x x x",
     *     "x x x x x x x x x",
     * ) {
     *     'x' by Markers.CONTENT_LIST_SLOT_HORIZONTAL
     * }
     * ```
     */
    val lowerGui: GuiDslProperty
    
}

/**
 * DSL scope for configuring a normal split [Window] with separate upper and lower GUIs.
 *
 * ```
 * val myWindow = window(player) {
 *     title by "Split Window"
 *
 *     upperGui by gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# # # # # # # # #",
 *     ) {
 *         '#' by borderItem
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface NormalSplitWindowDsl : SplitWindowDsl {
    
    /**
     * The upper GUI displayed in the top inventory section.
     *
     * ```
     * upperGui by gui(
     *     "# # # # # # # # #",
     *     "# . . . . . . . #",
     *     "# # # # # # # # #",
     * ) {
     *     '#' by borderItem
     * }
     * ```
     */
    val upperGui: GuiDslProperty
    
}

/**
 * DSL scope for configuring a merged [Window] where a single GUI spans both the top inventory
 * and the player's inventory area.
 *
 * ```
 * val myWindow = mergedWindow(player) {
 *     title by "Merged Window"
 *
 *     gui by gui(
 *         "# # # # # # # # #",
 *         "# . . . . . . . #",
 *         "# . . . . . . . #",
 *         "# . . . . . . . #",
 *         "# # # # # # # # #",
 *         ". . . . . . . . .",
 *         ". . . . . . . . .",
 *         ". . . . . . . . .",
 *         ". . . . . . . . .",
 *     ) {
 *         '#' by borderItem
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface NormalMergedWindowDsl : WindowDsl {
    
    /**
     * The GUI that spans both the upper inventory and the player's inventory area.
     *
     * ```
     * gui by gui(
     *     "# # # # # # # # #",
     *     "# . . . . . . . #",
     *     "# . . . . . . . #",
     *     "# . . . . . . . #",
     *     "# # # # # # # # #",
     *     ". . . . . . . . .",
     *     ". . . . . . . . .",
     *     ". . . . . . . . .",
     *     ". . . . . . . . .",
     * ) {
     *     '#' by borderItem
     * }
     * ```
     */
    val gui: GuiDslProperty
    
}

@PublishedApi
@ExperimentalDslApi
internal abstract class AbstractWindowDsl<W : Window, B : Window.Builder<W, B>>(
    override val viewer: Player
) : WindowDsl {
    
    private lateinit var _window: W
    override val window: Provider<W> = provider {
        check(::_window.isInitialized) { "Window cannot be accessed before it is built" }
        _window
    }
    
    private var _title = provider<Component>(Component.empty())
    private var _closeable = provider(true)
    private var _fallbackWindow = provider<Window?>(null)
    private var _serverWindowState = mutableProvider(0)
    
    override val title: ProviderDslProperty<Component>
        get() = ProviderDslProperty(::_title)
    override val closeable: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_closeable)
    override val fallbackWindow: ProviderDslProperty<Window?>
        get() = ProviderDslProperty(::_fallbackWindow)
    override val serverWindowState: MutableProviderDslProperty<Int>
        get() = MutableProviderDslProperty(::_serverWindowState)
    override val clientWindowState = mutableProvider(0)
    private val openHandlers = mutableListOf<WindowOpenDsl.() -> Unit>()
    private val closeHandlers = mutableListOf<WindowCloseDsl.() -> Unit>()
    private val outsideClickHandlers = mutableListOf<WindowOutsideClickDsl.() -> Unit>()
    
    override fun onOpen(handler: WindowOpenDsl.() -> Unit) {
        openHandlers += handler
    }
    
    override fun onClose(handler: WindowCloseDsl.() -> Unit) {
        closeHandlers += handler
    }
    
    override fun onOutsideClick(handler: WindowOutsideClickDsl.() -> Unit) {
        outsideClickHandlers += handler
    }
    
    fun build(): W {
        val window = createBuilder().apply(::applyToBuilder).build()
        _window = window
        return window
    }
    
    open fun applyToBuilder(builder: B) {
        builder.apply {
            setViewer(viewer)
            setTitle(_title)
            setCloseable(_closeable)
            setFallbackWindow(_fallbackWindow)
            for (handler in openHandlers) {
                addOpenHandler { WindowOpenDslImpl().handler() }
            }
            for (handler in closeHandlers) {
                addCloseHandler { reason -> WindowCloseDslImpl(reason).handler() }
            }
            for (handler in outsideClickHandlers) {
                addOutsideClickHandler { event -> WindowOutsideClickDslImpl(event).handler() }
            }
            setWindowState(_serverWindowState)
            addWindowStateChangeHandler(clientWindowState::set)
        }
    }
    
    abstract fun createBuilder(): B
    
}

@ExperimentalDslApi
internal abstract class AbstractSplitWindowDsl<W : Window, B : Window.Builder.Split<W, B>>(
    viewer: Player
) : AbstractWindowDsl<W, B>(viewer), SplitWindowDsl {
    
    override val lowerGui = GuiDslProperty(9, 4) { InventoryUtils2.createPlayerReferencingInventoryGui(viewer) }
    
    override fun applyToBuilder(builder: B) {
        super.applyToBuilder(builder)
        builder.setLowerGui(lowerGui.value)
    }
    
}

@PublishedApi
@ExperimentalDslApi
internal class NormalSplitWindowDslImpl(viewer: Player) : AbstractSplitWindowDsl<Window, Window.Builder.Normal.Split>(viewer), NormalSplitWindowDsl {
    
    override val upperGui = GuiDslProperty(
        dimensions = listOf(
            Dimensions(9, 6),
            Dimensions(9, 5),
            Dimensions(9, 4),
            Dimensions(9, 3),
            Dimensions(9, 2),
            Dimensions(9, 1),
            Dimensions(5, 1),
            Dimensions(3, 3),
        )
    )
    
    override fun createBuilder() = Window.builder()
    
    override fun applyToBuilder(builder: Window.Builder.Normal.Split) {
        super.applyToBuilder(builder)
        builder.setUpperGui(upperGui.value)
    }
    
}

@PublishedApi
@ExperimentalDslApi
internal class NormalMergedWindowDslImpl(viewer: Player) : AbstractWindowDsl<Window, Window.Builder.Normal.Merged>(viewer), NormalMergedWindowDsl {
    
    override val gui = GuiDslProperty(
        dimensions = listOf(
            Dimensions(9, 10),
            Dimensions(9, 9),
            Dimensions(9, 8),
            Dimensions(9, 7),
            Dimensions(9, 6),
            Dimensions(9, 5),
        )
    )
    
    override fun createBuilder() = Window.mergedBuilder()
    
    override fun applyToBuilder(builder: Window.Builder.Normal.Merged) {
        super.applyToBuilder(builder)
        builder.setGui(gui.value)
    }
    
}

@ExperimentalDslApi
internal class WindowOpenDslImpl : WindowOpenDsl

@ExperimentalDslApi
internal class WindowCloseDslImpl(override val reason: InventoryCloseEvent.Reason) : WindowCloseDsl

@ExperimentalDslApi
internal class WindowOutsideClickDslImpl(private val event: ClickEvent) : WindowOutsideClickDsl {
    override val player: Player
        get() = event.player
    override val clickType: ClickType
        get() = event.clickType
    override val hotbarButton: Int
        get() = event.hotbarButton
    override var isCancelled: Boolean
        get() = event.isCancelled
        set(value) {
            event.isCancelled = value
        }
}