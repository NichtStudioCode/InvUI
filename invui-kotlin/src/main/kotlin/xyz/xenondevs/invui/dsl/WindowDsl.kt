@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ClickEvent
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.ComponentProviderDslProperty
import xyz.xenondevs.invui.dsl.property.Dimensions
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.internal.util.InventoryUtils
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.setCloseable
import xyz.xenondevs.invui.window.setTitle
import xyz.xenondevs.invui.window.setWindowState

@ExperimentalDslApi
fun window(viewer: Player, window: NormalSplitWindowDsl.() -> Unit): Window =
    NormalSplitWindowDslImpl(viewer).apply(window).build()

@ExperimentalDslApi
fun mergedWindow(viewer: Player, window: NormalMergedWindowDsl.() -> Unit): Window =
    NormalMergedWindowDslImpl(viewer).apply(window).build()

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
    
    val title: ComponentProviderDslProperty
    val closeable: ProviderDslProperty<Boolean>
    val fallbackWindow: ProviderDslProperty<Window?>
    val serverWindowState: ProviderDslProperty<Int>
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

@ExperimentalDslApi
internal abstract class AbstractWindowDsl<W : Window, B : Window.Builder<W, B>>(
    private val viewer: Player
) : WindowDsl {
    
    override val title = ComponentProviderDslProperty()
    override val closeable = ProviderDslProperty(true)
    override val fallbackWindow = ProviderDslProperty<Window?>(null)
    override val serverWindowState = ProviderDslProperty(0)
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
    
    fun build(): W = createBuilder().apply(::applyToBuilder).build()
    
    open fun applyToBuilder(builder: B) {
        builder.apply { 
            setViewer(viewer)
            setTitle(title)
            setCloseable(closeable)
            setFallbackWindow(fallbackWindow)
            for (handler in openHandlers) {
                addOpenHandler { WindowOpenDslImpl().handler() }
            }
            for (handler in closeHandlers) {
                addCloseHandler { reason -> WindowCloseDslImpl(reason).handler() }
            }
            for (handler in outsideClickHandlers) {
                addOutsideClickHandler { event -> WindowOutsideClickDslImpl(event).handler() }
            }
            setWindowState(serverWindowState)
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