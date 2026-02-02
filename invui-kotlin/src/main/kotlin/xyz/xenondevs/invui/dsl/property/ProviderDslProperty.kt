@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl.property

import io.papermc.paper.datacomponent.DataComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.DeferredValue
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
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
class ProviderDslProperty<T> internal constructor(
    delegate: Provider<T>
) : Provider<T> {
    
    internal constructor(initial: T) : this(provider(initial))
    
    infix fun by(provider: Provider<T>) {
        delegate = provider
    }
    
    infix fun by(value: T) {
        delegate = provider(value)
    }
    
    //<editor-fold desc="delegate / equals / hashCode">
    override var delegate = delegate
        private set
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && delegate === delegate
    override fun hashCode(): Int = System.identityHashCode(delegate)
    //</editor-fold>
    
    //<editor-fold desc="delegation">
    override val parents: Set<Provider<*>>
        get() = delegate.parents
    override val children: Set<Provider<*>>
        get() = delegate.children
    override val value: DeferredValue<T>
        get() = delegate.value
    override val isStable: Boolean
        get() = false
    
    override fun <R> strongMap(transform: (T) -> R) =
        delegate.strongMap(transform)
    
    override fun <R> map(transform: (T) -> R): Provider<R> =
        delegate.map(transform)
    
    override fun <R> strongImmediateFlatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.strongImmediateFlatMap(transform)
    
    override fun <R> immediateFlatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.immediateFlatMap(transform)
    
    override fun <R> strongImmediateFlatMapMutable(transform: (T) -> MutableProvider<R>): MutableProvider<R> =
        delegate.strongImmediateFlatMapMutable(transform)
    
    override fun <R> immediateFlatMapMutable(transform: (T) -> MutableProvider<R>): MutableProvider<R> =
        delegate.immediateFlatMapMutable(transform)
    
    override fun <R> strongFlatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.strongFlatMap(transform)
    
    override fun <R> flatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.flatMap(transform)
    
    override fun subscribe(action: (value: T) -> Unit) =
        delegate.subscribe(action)
    
    override fun observe(action: () -> Unit) =
        delegate.observe(action)
    
    override fun <R : Any> subscribeWeak(owner: R, action: (owner: R, value: T) -> Unit) =
        delegate.subscribeWeak(owner, action)
    
    override fun <R : Any> observeWeak(owner: R, action: (owner: R) -> Unit) =
        delegate.observeWeak(owner, action)
    
    override fun unsubscribe(action: (T) -> Unit) =
        delegate.unsubscribe(action)
    
    override fun unobserve(action: () -> Unit) =
        delegate.unobserve(action)
    
    override fun <R : Any> unsubscribeWeak(owner: R, action: (R, T) -> Unit) =
        delegate.unsubscribeWeak(owner, action)
    
    override fun <R : Any> unobserveWeak(owner: R, action: (R) -> Unit) =
        delegate.unobserveWeak(owner, action)
    
    override fun <R : Any> unsubscribeWeak(owner: R) =
        delegate.unsubscribeWeak(owner)
    
    override fun <R : Any> unobserveWeak(owner: R) =
        delegate.unobserveWeak(owner)
    
    override fun addStrongChild(child: Provider<*>) =
        delegate.addStrongChild(child)
    
    override fun removeStrongChild(child: Provider<*>) =
        delegate.removeStrongChild(child)
    
    override fun addWeakChild(child: Provider<*>) =
        delegate.addWeakChild(child)
    
    override fun removeWeakChild(child: Provider<*>) =
        delegate.removeWeakChild(child)
    
    override fun handleParentUpdated(updatedParent: Provider<*>) =
        delegate.handleParentUpdated(updatedParent)
    //</editor-fold>
    
}

@ExperimentalDslApi
class MutableProviderDslProperty<T> internal constructor(
    delegate: MutableProvider<T>
) : MutableProvider<T> {
    
    internal constructor(initial: T) : this(mutableProvider(initial))
    
    infix fun by(provider: MutableProvider<T>) {
        delegate = provider
    }
    
    infix fun by(value: T) {
        delegate = mutableProvider(value)
    }
    
    //<editor-fold desc="delegate / equals / hashCode">
    override var delegate: MutableProvider<T> = delegate
        private set
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.delegate === delegate
    override fun hashCode(): Int = System.identityHashCode(delegate)
    //</editor-fold>
    
    //<editor-fold desc="delegation">
    override val parents: Set<Provider<*>>
        get() = delegate.parents
    override val children: Set<Provider<*>>
        get() = delegate.children
    override val value: DeferredValue<T>
        get() = delegate.value
    override val isStable: Boolean
        get() = false
    
    override fun <R> strongMap(transform: (T) -> R) =
        delegate.strongMap(transform)
    
    override fun <R> map(transform: (T) -> R): Provider<R> =
        delegate.map(transform)
    
    override fun <R> strongImmediateFlatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.strongImmediateFlatMap(transform)
    
    override fun <R> immediateFlatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.immediateFlatMap(transform)
    
    override fun <R> strongImmediateFlatMapMutable(transform: (T) -> MutableProvider<R>): MutableProvider<R> =
        delegate.strongImmediateFlatMapMutable(transform)
    
    override fun <R> immediateFlatMapMutable(transform: (T) -> MutableProvider<R>): MutableProvider<R> =
        delegate.immediateFlatMapMutable(transform)
    
    override fun <R> strongFlatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.strongFlatMap(transform)
    
    override fun <R> flatMap(transform: (T) -> Provider<R>): Provider<R> =
        delegate.flatMap(transform)
    
    override fun subscribe(action: (value: T) -> Unit) =
        delegate.subscribe(action)
    
    override fun observe(action: () -> Unit) =
        delegate.observe(action)
    
    override fun <R : Any> subscribeWeak(owner: R, action: (owner: R, value: T) -> Unit) =
        delegate.subscribeWeak(owner, action)
    
    override fun <R : Any> observeWeak(owner: R, action: (owner: R) -> Unit) =
        delegate.observeWeak(owner, action)
    
    override fun unsubscribe(action: (T) -> Unit) =
        delegate.unsubscribe(action)
    
    override fun unobserve(action: () -> Unit) =
        delegate.unobserve(action)
    
    override fun <R : Any> unsubscribeWeak(owner: R, action: (R, T) -> Unit) =
        delegate.unsubscribeWeak(owner, action)
    
    override fun <R : Any> unobserveWeak(owner: R, action: (R) -> Unit) =
        delegate.unobserveWeak(owner, action)
    
    override fun <R : Any> unsubscribeWeak(owner: R) =
        delegate.unsubscribeWeak(owner)
    
    override fun <R : Any> unobserveWeak(owner: R) =
        delegate.unobserveWeak(owner)
    
    override fun addStrongChild(child: Provider<*>) =
        delegate.addStrongChild(child)
    
    override fun removeStrongChild(child: Provider<*>) =
        delegate.removeStrongChild(child)
    
    override fun addWeakChild(child: Provider<*>) =
        delegate.addWeakChild(child)
    
    override fun removeWeakChild(child: Provider<*>) =
        delegate.removeWeakChild(child)
    
    override fun handleParentUpdated(updatedParent: Provider<*>) =
        delegate.handleParentUpdated(updatedParent)
    
    override fun <R> strongMap(transform: (T) -> R, untransform: (R) -> T) =
        delegate.strongMap(transform, untransform)
    
    override fun <R> map(transform: (T) -> R, untransform: (R) -> T) =
        delegate.map(transform, untransform)
    
    override fun <R> mapObserved(createObservable: (value: T, updateHandler: () -> Unit) -> R) =
        delegate.mapObserved(createObservable)
    
    override fun <R> strongMapObserved(createObservable: (value: T, updateHandler: () -> Unit) -> R) =
        delegate.strongMapObserved(createObservable)
    
    override fun consume(source: Provider<T>) =
        delegate.consume(source)
    
    override fun update(value: DeferredValue<T>, ignore: Set<Provider<*>>) =
        delegate.update(value, ignore)
    //</editor-fold>
    
}