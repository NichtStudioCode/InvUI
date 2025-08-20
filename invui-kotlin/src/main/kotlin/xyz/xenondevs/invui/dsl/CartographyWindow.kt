@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.window.CartographyWindow
import xyz.xenondevs.invui.window.setIcons
import xyz.xenondevs.invui.window.setView

// TODO: map image

@ExperimentalDslApi
fun cartographyWindow(viewer: Player, cartographyWindow: CartographyWindowDsl.() -> Unit): CartographyWindow =
    CartographyWindowDslImpl(viewer).apply(cartographyWindow).build()

@ExperimentalDslApi
sealed interface CartographyWindowDsl : SplitWindowDsl {
    
    val inputGui: GuiDslProperty
    val resultGui: GuiDslProperty
    val icons: ProviderDslProperty<Set<CartographyWindow.MapIcon>>
    val view: ProviderDslProperty<CartographyWindow.View>
    
}

@ExperimentalDslApi
internal class CartographyWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<CartographyWindow, CartographyWindow.Builder>(viewer), CartographyWindowDsl {
    
    override val inputGui = GuiDslProperty(1, 2)
    override val resultGui = GuiDslProperty(1, 1)
    override val icons = ProviderDslProperty(emptySet<CartographyWindow.MapIcon>())
    override val view = ProviderDslProperty(CartographyWindow.View.NORMAL)
    
    override fun createBuilder() = CartographyWindow.builder()
    
    override fun applyToBuilder(builder: CartographyWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply { 
            setInputGui(inputGui.value)
            setResultGui(resultGui.value)
            setIcons(icons.delegate)
            setView(view.delegate)
        }
    }
    
}