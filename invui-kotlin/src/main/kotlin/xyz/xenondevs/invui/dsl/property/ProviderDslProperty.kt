@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl.property

import io.papermc.paper.datacomponent.DataComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.immediateFlatten
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.dsl.ExperimentalDslApi
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper

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
open class ProviderDslProperty<T> internal constructor(
    private val source: MutableProvider<Provider<T>>,
    override val identifier: Provider<T> = source.immediateFlatten()
) : Provider<T> by identifier {
    
    internal constructor(initial: T) : this(mutableProvider(provider(initial)))
    
    internal constructor(initial: Provider<T>) : this(mutableProvider(initial))
    
    infix fun by(provider: Provider<T>) {
        source.set(provider)
    }
    
    infix fun by(value: T) {
        source.set(mutableProvider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.identifier === identifier
    override fun hashCode(): Int = System.identityHashCode(identifier)
    
}

@ExperimentalDslApi
class MutableProviderDslProperty<T> internal constructor(
    private val source: MutableProvider<MutableProvider<T>>,
    override val identifier: MutableProvider<T> = source.immediateFlatten()
) : MutableProvider<T> by identifier {
    
    internal constructor(initial: T) : this(mutableProvider(mutableProvider(initial)))
    
    infix fun by(provider: MutableProvider<T>) {
        source.set(provider)
    }
    
    infix fun by(value: T) {
        source.set(mutableProvider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.identifier === identifier
    override fun hashCode(): Int = System.identityHashCode(identifier)
    
}