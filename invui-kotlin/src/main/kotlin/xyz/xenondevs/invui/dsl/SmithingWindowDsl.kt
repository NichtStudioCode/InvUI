package xyz.xenondevs.invui.dsl

import org.bukkit.entity.Player
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.dsl.property.GuiDslProperty
import xyz.xenondevs.invui.window.SmithingWindow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [SmithingWindow] using the DSL.
 *
 * A smithing window displays a smithing table GUI with a 4x1 upper GUI representing the
 * template, base, addition, and result slots.
 *
 * ```
 * val myWindow = smithingWindow(player) {
 *     title by "Smithing Table"
 *     upperGui by gui("t b a r") {
 *         't' by templateItem
 *         'b' by baseItem
 *         'a' by additionItem
 *         'r' by resultItem
 *     }
 * }
 * ```
 *
 * @see SmithingWindowDsl
 */
@ExperimentalDslApi
inline fun smithingWindow(viewer: Player, smithingWindow: SmithingWindowDsl.() -> Unit): SmithingWindow {
    contract { callsInPlace(smithingWindow, InvocationKind.EXACTLY_ONCE) }
    return SmithingWindowDslImpl(viewer).apply(smithingWindow).build()
}

/**
 * DSL scope for configuring a [SmithingWindow].
 *
 * Extends [SplitWindowDsl] with a 4x1 [upperGui] for the smithing table's template, base,
 * addition, and result slots.
 *
 * ```
 * smithingWindow(player) {
 *     title by "Smithing Table"
 *     upperGui by gui("t b a r") {
 *         't' by templateItem
 *         'b' by baseItem
 *         'a' by additionItem
 *         'r' by resultItem
 *     }
 * }
 * ```
 */
@ExperimentalDslApi
sealed interface SmithingWindowDsl : SplitWindowDsl {
    
    /**
     * A [Provider] that resolves to the built [SmithingWindow] instance.
     *
     * Can be used to obtain a reference to the window after the DSL block finishes and
     * the window is built. Accessing it before the window is built throws an
     * [IllegalStateException].
     */
    override val window: Provider<SmithingWindow>
    
    /**
     * The upper GUI (4x1) for the template, base, addition, and result slots.
     *
     * ```
     * upperGui by gui("t b a r") {
     *     't' by templateItem
     *     'b' by baseItem
     *     'a' by additionItem
     *     'r' by resultItem
     * }
     * ```
     */
    val upperGui: GuiDslProperty
    
}

@PublishedApi
@ExperimentalDslApi
internal class SmithingWindowDslImpl(
    viewer: Player
) : AbstractSplitWindowDsl<SmithingWindow, SmithingWindow.Builder>(viewer), SmithingWindowDsl {
    
    override val upperGui = GuiDslProperty(4, 1)
    
    override fun createBuilder() = SmithingWindow.builder()
    
    override fun applyToBuilder(builder: SmithingWindow.Builder) {
        super.applyToBuilder(builder)
        builder.apply {
            setUpperGui(upperGui.value)
        }
    }
    
}