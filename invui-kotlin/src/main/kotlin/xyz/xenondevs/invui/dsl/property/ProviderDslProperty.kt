@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl.property

import io.papermc.paper.datacomponent.DataComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.dsl.ExperimentalDslApi
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import kotlin.reflect.KMutableProperty0

@JvmName("componentByString")
@ExperimentalDslApi
infix fun ProviderDslProperty<in Component>.by(miniMessage: String): Unit =
    by(MiniMessage.miniMessage().deserialize(miniMessage))

@JvmName("componentByStringProvider")
@ExperimentalDslApi
infix fun ProviderDslProperty<in Component>.by(provider: Provider<String>): Unit =
    by(provider.map(MiniMessage.miniMessage()::deserialize))

@JvmName("componentListByStringList")
@ExperimentalDslApi
infix fun ProviderDslProperty<in List<Component>>.by(miniMessageList: List<String>): Unit =
    by(miniMessageList.map { MiniMessage.miniMessage().deserialize(it) })

@JvmName("componentListByStringListProvider")
@ExperimentalDslApi
infix fun ProviderDslProperty<in List<Component>>.by(provider: Provider<List<String>>): Unit =
    by(provider.map { list -> list.map { MiniMessage.miniMessage().deserialize(it) } })

@JvmName("itemProviderByItemStack")
@ExperimentalDslApi
infix fun ProviderDslProperty<in ItemProvider>.by(itemStack: ItemStack): Unit =
    by(ItemWrapper(itemStack))

@JvmName("itemProviderByItemStackProvider")
@ExperimentalDslApi
infix fun ProviderDslProperty<in ItemProvider>.by(provider: Provider<ItemStack>): Unit =
    by(provider.map(::ItemWrapper))

@Suppress("UnstableApiUsage")
@JvmName("dataComponentValueByBuilder")
@ExperimentalDslApi
infix fun <T : Any> ProviderDslProperty<in T>.by(valueBuilder: DataComponentBuilder<T>): Unit =
    by(valueBuilder.build())

@Suppress("UnstableApiUsage")
@JvmName("dataComponentValueByBuilderProvider")
@ExperimentalDslApi
infix fun <T : Any> ProviderDslProperty<in T>.by(provider: Provider<DataComponentBuilder<T>>): Unit =
    by(provider.map(DataComponentBuilder<T>::build))

@ExperimentalDslApi
class ProviderDslProperty<T> internal constructor(
    override val delegate: Provider<T>,
    private val set: (Provider<T>) -> Unit
) : Provider<T> by delegate {
    
    internal constructor(
        field: KMutableProperty0<Provider<T>>
    ) : this(field.get(), field::set)
    
    internal constructor(
        initial: T,
        set: (Provider<T>) -> Unit
    ) : this(provider(initial), set)
    
    infix fun by(provider: Provider<T>) {
        set(provider.delegate)
    }
    
    infix fun by(value: T) {
        by(provider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.delegate === delegate
    override fun hashCode(): Int = System.identityHashCode(delegate)
    
}

@ExperimentalDslApi
class MutableProviderDslProperty<T> private constructor(
    override val delegate: MutableProvider<T>,
    private val set: (MutableProvider<T>) -> Unit
) : MutableProvider<T> by delegate {
    
    internal constructor(
        field: KMutableProperty0<MutableProvider<T>>
    ) : this(field.get(), field::set)
    
    infix fun by(provider: MutableProvider<T>) {
        set(provider.delegate)
    }
    
    infix fun by(value: T) {
        by(mutableProvider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.delegate === delegate
    override fun hashCode(): Int = System.identityHashCode(delegate)
    
}