@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.dsl.property.ProviderDslProperty
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.IngredientPreset
import xyz.xenondevs.invui.gui.setBackground
import xyz.xenondevs.invui.gui.setFrozen
import xyz.xenondevs.invui.gui.setIgnoreObscuredInventorySlots
import xyz.xenondevs.invui.item.ItemProvider
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalDslApi
inline fun gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return NormalGuiDslImpl(structure).apply(gui).build()
}

@ExperimentalDslApi
inline fun IngredientsDsl.gui(vararg structure: String, gui: GuiDsl.() -> Unit): Gui {
    contract { callsInPlace(gui, InvocationKind.EXACTLY_ONCE) }
    return NormalGuiDslImpl(structure, (this as IngredientsDslImpl).buildPresets()).apply(gui).build()
}

@ExperimentalDslApi
sealed interface GuiDsl : IngredientsDsl {
    
    val gui: Provider<Gui>
    
    val background: ProviderDslProperty<ItemProvider?>
    val frozen: ProviderDslProperty<Boolean>
    val ignoreObscuredInventorySlots: ProviderDslProperty<Boolean>
    
}

@PublishedApi
@ExperimentalDslApi
internal abstract class GuiDslImpl<G : Gui, B : Gui.Builder<G, B>>(
    private val structure: Array<out String>,
    presets: List<IngredientPreset>
) : IngredientsDslImpl(presets), GuiDsl {
    
    private lateinit var _gui: G
    override val gui: Provider<G> = provider {
        check(::_gui.isInitialized) { "Gui cannot be accessed before it is built"}
        _gui
    }
    
    private var _background = provider<ItemProvider?>(null)
    private var _frozen = provider(false)
    private var _ignoreObscuredInventorySlots = provider(false)
    
    override val background: ProviderDslProperty<ItemProvider?>
        get() = ProviderDslProperty(::_background)
    override val frozen: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_frozen)
    override val ignoreObscuredInventorySlots: ProviderDslProperty<Boolean>
        get() = ProviderDslProperty(::_ignoreObscuredInventorySlots)
    
    fun build(): G {
        val gui = createBuilder().apply(::applyToBuilder).build()
        _gui = gui
        return gui
    }
    
    open fun applyToBuilder(builder: B) {
        builder.apply {
            setStructure(*structure)
            for (preset in presets) {
                applyPreset(preset)
            }
            applyPreset(ingredients.build())
            
            setBackground(_background)
            setFrozen(_frozen)
            setIgnoreObscuredInventorySlots(_ignoreObscuredInventorySlots)
        }
    }
    
    abstract fun createBuilder(): B
    
}

@PublishedApi
@ExperimentalDslApi
internal class NormalGuiDslImpl<B : Gui.Builder<Gui, B>>(
    structure: Array<out String>,
    presets: List<IngredientPreset> = emptyList()
) : GuiDslImpl<Gui, B>(structure, presets) {
    
    @Suppress("UNCHECKED_CAST")
    override fun createBuilder(): B = Gui.builder() as B
    
}