@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
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

@ExperimentalDslApi
fun window(viewer: Player, window: NormalSplitWindowDsl.() -> Unit): Window =
    NormalSplitWindowDslImpl(viewer).apply(window).build()

@ExperimentalDslApi
fun mergedWindow(viewer: Player, window: NormalMergedWindowDsl.() -> Unit): Window =
    NormalMergedWindowDslImpl(viewer).apply(window).build()

@ExperimentalDslApi
@WindowDslMarker
sealed interface WindowDsl {
    
    val title: ComponentProviderDslProperty
    val closeable: ProviderDslProperty<Boolean>
    val fallbackWindow: ProviderDslProperty<Window?>
    
    fun onOpen(handler: () -> Unit)
    
    fun onClose(handler: (reason: InventoryCloseEvent.Reason) -> Unit)
    
    fun onOutsideClick(handler: (ClickEvent) -> Unit)
    
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
    private val openHandlers = mutableListOf<() -> Unit>()
    private val closeHandlers = mutableListOf<(InventoryCloseEvent.Reason) -> Unit>()
    private val outsideClickHandlers = mutableListOf<(ClickEvent) -> Unit>()
    
    override fun onOpen(handler: () -> Unit) {
        openHandlers += handler
    }
    
    override fun onClose(handler: (InventoryCloseEvent.Reason) -> Unit) {
        closeHandlers += handler
    }
    
    override fun onOutsideClick(handler: (ClickEvent) -> Unit) {
        outsideClickHandlers += handler
    }
    
    fun build(): W = createBuilder().apply(::applyToBuilder).build()
    
    open fun applyToBuilder(builder: B) {
        builder.apply { 
            setViewer(viewer)
            setTitle(title.delegate)
            setCloseable(closeable.delegate)
            setFallbackWindow(fallbackWindow.delegate)
            openHandlers.forEach { addOpenHandler(it) }
            closeHandlers.forEach { addCloseHandler(it) }
            outsideClickHandlers.forEach { addOutsideClickHandler(it) }
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