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
import xyz.xenondevs.invui.dsl.property.Dimensions
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.MutableProviderDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.internal.util.InventoryUtils
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.setCloseable
import xyz.xenondevs.invui.window.setTitle
import xyz.xenondevs.invui.window.setWindowState
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun window(viewer: Player, window: NormalSplitWindowDsl.() -> Unit): Window {
    contract { callsInPlace(window, InvocationKind.EXACTLY_ONCE) }
    return NormalSplitWindowDslImpl(viewer).apply(window).build()
}

@ExperimentalDslApi
inline fun mergedWindow(viewer: Player, window: NormalMergedWindowDsl.() -> Unit): Window {
    contract { callsInPlace(window, InvocationKind.EXACTLY_ONCE) }
    return NormalMergedWindowDslImpl(viewer).apply(window).build()
}

@WindowDslMarker
@ExperimentalDslApi
interface WindowOpenDsl

@WindowDslMarker
@ExperimentalDslApi
interface WindowCloseDsl {
    
    val reason: InventoryCloseEvent.Reason
    
}

@WindowDslMarker
@ExperimentalDslApi
interface WindowOutsideClickDsl {
    
    val player: Player
    val clickType: ClickType
    val hotbarButton: Int
    var isCancelled: Boolean
    
}

@ExperimentalDslApi
@WindowDslMarker
sealed interface WindowDsl {
    
    val viewer: Player
    val window: Provider<Window>
    
    val title: ProviderDslProperty<Component>
    val closeable: ProviderDslProperty<Boolean>
    val fallbackWindow: ProviderDslProperty<Window?>
    val serverWindowState: MutableProviderDslProperty<Int>
    val clientWindowState: Provider<Int>
    
    fun onOpen(handler: WindowOpenDsl.() -> Unit)
    
    fun onClose(handler: WindowCloseDsl.() -> Unit)
    
    fun onOutsideClick(handler: WindowOutsideClickDsl.() -> Unit)
    
}

@ExperimentalDslApi
sealed interface SplitWindowDsl : WindowDsl {
    
    val lowerGui: GuiDslProperty
    
}

@ExperimentalDslApi
sealed interface NormalSplitWindowDsl : SplitWindowDsl {
    
    val upperGui: GuiDslProperty
    
}

@ExperimentalDslApi
sealed interface NormalMergedWindowDsl : WindowDsl {
    
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
    
    override val lowerGui = GuiDslProperty(9, 4) { InventoryUtils.createPlayerReferencingInventoryGui(viewer) }
    
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