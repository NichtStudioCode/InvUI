package xyz.xenondevs.invui.dsl.property

import xyz.xenondevs.invui.dsl.ExperimentalDslApi
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.inventory.Inventory
import kotlin.math.ceil

@ExperimentalDslApi
internal data class Dimensions(val width: Int, val height: Int) {
    val size: Int
        get() = width * height
}

@ExperimentalDslApi
class GuiDslProperty internal constructor(
    private val dimensions: List<Dimensions>,
    private val arbitraryHeight: Boolean = false,
    private val lazyDefaultGui: () -> Gui = { Gui.empty(dimensions[0].width, dimensions[0].height) }
) {
    
    internal var definedGui: Gui? = null
    internal val value: Gui
        get() = definedGui ?: lazyDefaultGui()
    
    internal constructor(
        width: Int, height: Int,
        arbitraryHeight: Boolean = false,
        lazyDefaultGui: () -> Gui = { Gui.empty(width, height) }
    ) : this(listOf(Dimensions(width, height)), arbitraryHeight, lazyDefaultGui)
    
    infix fun by(gui: Gui) {
        this.definedGui = gui
    }
    
    infix fun by(inventory: Inventory) {
        if (arbitraryHeight) {
            definedGui = Gui.of(
                dimensions[0].width,
                ceil(inventory.size / dimensions[0].width.toDouble()).toInt(),
                inventory
            )
        } else {
            val dim = dimensions
                .filter { it.size >= inventory.size }
                .minByOrNull { it.size - inventory.size }
                ?: dimensions.first()
            definedGui = Gui.of(dim.width, dim.height, inventory)
        }
    }
    
}